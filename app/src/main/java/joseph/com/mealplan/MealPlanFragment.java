package joseph.com.mealplan;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.google.common.collect.FluentIterable;

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
    private String addingDay;

    @BindView(R.id.lvMealPlan)
    ListView lvMealPlan;


    public MealPlanFragment() {
        for (final String dayName : Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")) {
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

    public void addRecipeWithDay(Recipe recipe, String day) {
        addingRecipe = recipe;
        addingDay = day;
    }


    public class MealAdapter extends BaseAdapter {

        /**
         * converts days into a format that corresponds to the rows of the ListView
         * e.g. [Day("Sunday", Recipe("Soup"), Day("Monday"), ...]
         */
        private List flattenDays() {
            realm.beginTransaction();
            List result = Utils.flatten(days, new Function<Day, Collection>() {
                @Nullable
                @Override
                public Collection apply(@Nullable Day input) {
                    return input.getMeals();
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

                if(addingDay != null && addingRecipe != null){
                    duplicateRecipe(addingDay, addingRecipe);
                    addingDay = null;
                    addingRecipe = null;
                }
                return view;
            } else {
                final Recipe recipe = (Recipe) item;
                Log.i(TAG, "recipe: " + recipe.getTitle());
                final RecipeView recipeView = new RecipeView(getContext());
                recipeView.bind(recipe);
                final Day[] deleted = new Day[1];
                final int i = position;
                recipeView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle("Alert!!");
                        alert.setMessage("What would you like to do?");
                        alert.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Object intendedDay = getItem(i);
                                int j = i;
                                while (intendedDay instanceof Recipe) {
                                    j = j - 1;
                                    intendedDay = getItem(j);
                                }
                                deleted[0] = (Day) intendedDay;
                                realm.beginTransaction();
                                ((Day) intendedDay).getMeals().remove(recipe);
                                realm.commitTransaction();
                                dialog.dismiss();
                                MealAdapter.this.notifyDataSetChanged();

                                View.OnClickListener undoDelete = new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) { //Re-adds the deleted entry
                                        realm.beginTransaction();
                                        deleted[0].getMeals().add(recipe);
                                        realm.commitTransaction();
                                        MealAdapter.this.notifyDataSetChanged();
                                    }
                                };
                                //Displays snackbar, which allows for undoing the delete
                                Snackbar.make(recipeView, "Removed recipe", Snackbar.LENGTH_LONG)
                                        .setAction("Undo", undoDelete)
                                        .show();
                            }
                        });
                        alert.setNeutralButton("BACK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });


                        alert.setNegativeButton("DUPLICATE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final Context context = getContext();
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Which day would you like to duplicate this recipe?");
                                builder.setItems(FluentIterable.from(days).transform(new Function<Day, String>() {
                                    @Nullable
                                    @Override
                                    public String apply(@Nullable Day day) {
                                        return day.getName();
                                    }
                                }).toArray(String.class), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        duplicateRecipe(days.get(which).getName(), recipe);
                                    }
                                });
                                builder.setItems(new CharSequence[]
                                                {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Back"},
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // The 'which' argument contains the index position
                                                // of the selected item
                                                switch (which) {
                                                    case 0:
                                                        duplicateRecipe("Sunday", recipe);
                                                        break;
                                                    case 1:
                                                        duplicateRecipe("Monday", recipe);
                                                        break;
                                                    case 2:
                                                        duplicateRecipe("Tuesday", recipe);
                                                        break;
                                                    case 3:
                                                        duplicateRecipe("Wednesday", recipe);
                                                        break;
                                                    case 4:
                                                        duplicateRecipe("Thursday", recipe);
                                                        break;
                                                    case 5:
                                                        duplicateRecipe("Friday", recipe);
                                                        break;
                                                    case 6:
                                                        duplicateRecipe("Saturday", recipe);
                                                        break;
                                                    case 7:
                                                        break;
                                                }
                                            }
                                        });
                                builder.create().show();
                            }
                        });

                        alert.show();
                    return true;}
                });

                return recipeView;
            }
        }

        public void duplicateRecipe(final String dayName, Recipe recipe){
            realm.beginTransaction();
            for (Day day : days) {
                if (day.getName().equals(dayName)) {
                        day.getMeals().add(recipe);
                        break;
                }
            }
            realm.commitTransaction();
            MealAdapter.this.notifyDataSetChanged();
        }
    }
}
