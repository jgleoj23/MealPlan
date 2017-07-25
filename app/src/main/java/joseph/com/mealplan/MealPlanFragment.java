package joseph.com.mealplan;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import joseph.com.mealplan.model.Day;
import joseph.com.mealplan.model.Recipe;

public class MealPlanFragment extends Fragment {

    private String TAG = getClass().getName();
    private List<Day> days = new ArrayList<>();
    private Realm realm = Realm.getDefaultInstance();
    private Recipe addingRecipe;

    @BindView(R.id.lvMealPlan)
    ListView lvMealPlan;


    public MealPlanFragment() {
        for (final String dayName : Arrays.asList("Sunday", "Monday", "Tuesday")) {
            final Day day = realm.where(Day.class).equalTo("name", dayName).findFirst();
            if (day == null) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Day newDay = realm.createObject(Day.class, dayName);
                        days.add(newDay);
                        Log.i(TAG, "finalDay is " + newDay);
                    }
                });
            } else {
                days.add(day);
            }
        }

        Log.i(TAG, "making meal plan: " + days.size());
    }


    public static MealPlanFragment newInstance() {
        return new MealPlanFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_plan, container, false);
        ButterKnife.bind(this, view);
        lvMealPlan.setAdapter(new MealAdapter());
        return view;
    }


    public void addRecipe(Recipe recipe) {
        addingRecipe = recipe;
    }


    public class MealAdapter extends BaseAdapter {

        private List flattenDays() {
            realm.beginTransaction();
            List result = Utils.flatten(days, new Function<Object, Collection>() {
                @Nullable
                @Override
                public Collection apply(@Nullable Object input) {
                    return ((Day) input).getMeals();
                }
            });
            realm.commitTransaction();

            return result;
        }


        @Override
        public int getCount() {
            return flattenDays().size();
        }


        @Override
        public Object getItem(int position) {
            return flattenDays().get(position);
        }


        @Override
        public long getItemId(int position) {
            Log.i(TAG, "get id: " + position);
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.i(TAG, "getView: " + position);
            Object item = getItem(position);
            if (item instanceof Day) {
                final Day day = (Day) item;
                View view = LayoutInflater.from(getContext()).inflate(R.layout.item_day, parent, false);
                TextView tvDay = (TextView) view.findViewById(R.id.tvDay);
                tvDay.setText(day.getName());

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (addingRecipe != null) {
                            Log.i(TAG, "adding it");
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    Number maxId = realm.where(Recipe.class).max("id");
                                    if (maxId != null) {
                                        addingRecipe.setId(maxId.longValue() + 1);
                                    } else {
                                        addingRecipe.setId(0);
                                    }

                                    day.getMeals().add(addingRecipe);
                                }
                            });

                            addingRecipe = null;

                            MealAdapter.this.notifyDataSetChanged();
                        }
                    }
                });

                return view;
            } else {
                final Recipe recipe = (Recipe) item;
                Log.i(TAG, "recipe: " + recipe.getTitle());
                RecipeView recipeView = new RecipeView(getContext());
                recipeView.bind(recipe);

                recipeView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle("Alert!!");
                        alert.setMessage("Are you sure to delete record");
                        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                realm.beginTransaction();
                                for (Day day : days) {
                                    if (day.getMeals().remove(recipe)) break;
                                }
                                realm.commitTransaction();
                                dialog.dismiss();
                                MealAdapter.this.notifyDataSetChanged();
                            }
                        });
                        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        alert.show();

                        return true;
                    }
                });

                return recipeView;
            }
        }
    }
}
