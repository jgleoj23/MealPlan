package joseph.com.mealplan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import joseph.com.mealplan.model.Day;
import joseph.com.mealplan.model.Recipe;

import static joseph.com.mealplan.Utils.DAYS_OF_WEEK;

public class MealPlanFragment extends Fragment {

    private String TAG = getClass().getName();
    private List<Day> days = new ArrayList<>();
    private Realm realm = Realm.getDefaultInstance();
    private Recipe addingRecipe;
    private String addingDay;

    @BindView(R.id.lvMealPlan)
    ListView lvMealPlan;


    public MealPlanFragment() {
        for (final String dayName : DAYS_OF_WEEK) {
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

        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        Collections.rotate(days, -1 * (today - 1));

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
        public View getView(final int position, View convertView, ViewGroup parent) {
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

                recipeView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setMessage("Select an option");
                        alert.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Object intendedDay = getItem(position);
                                for (int j = position; intendedDay instanceof  Recipe; j--) {
                                    intendedDay = getItem(j);
                                }

                                final Day dayForRecipe = ((Day) intendedDay);
                                realm.beginTransaction();
                                dayForRecipe.getMeals().remove(recipe);
                                realm.commitTransaction();
                                dialog.dismiss();
                                MealAdapter.this.notifyDataSetChanged();


                                Snackbar.make(recipeView, "Removed recipe", Snackbar.LENGTH_LONG)
                                        .setAction("Undo", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v1) { //Re-adds the deleted entry
                                                realm.beginTransaction();
                                                dayForRecipe.getMeals().add(recipe);
                                                realm.commitTransaction();
                                                MealAdapter.this.notifyDataSetChanged();
                                            }
                                        })
                                        .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                            @Override
                                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                                if (event != DISMISS_EVENT_ACTION) {
                                                    // The snack bar was not dismissed by the undo button so we are
                                                    // going through with the delete
                                                    Intent intent = new Intent("remove-recipe");
                                                    intent.putExtra("recipe", Parcels.wrap(Recipe.class, recipe));
                                                    LocalBroadcastManager.getInstance(getContext())
                                                                         .sendBroadcast(intent);
                                                }
                                            }
                                        })
                                        .show();

                            }
                        });

                        alert.setNeutralButton("DUPLICATE", new DialogInterface.OnClickListener() {
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


                                builder.setItems(DAYS_OF_WEEK.toArray(new String[7]),
                                                 new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // The 'which' argument contains the index position
                                                // of the selected item
                                                duplicateRecipe(DAYS_OF_WEEK.get(which), recipe);
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
