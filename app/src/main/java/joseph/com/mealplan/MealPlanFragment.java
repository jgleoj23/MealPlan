package joseph.com.mealplan;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import joseph.com.mealplan.model.Day;
import joseph.com.mealplan.model.Recipe;

public class MealPlanFragment extends Fragment {

    private List<Day> days = new ArrayList<>();
    private LayoutInflater inflater;
    private Realm realm = Realm.getDefaultInstance();

    @BindView(R.id.lvMealPlan)
    ListView lvMealPlan;

    MainActivity mainActivity;

    public MealPlanFragment() {
        for (String dayName : Arrays.asList("Sunday", "Monday", "Tuesday")) {
            Day day = realm.where(Day.class).equalTo("name", dayName).findFirst();
            if (day == null) {
                day = new Day(dayName);
                final Day finalDay = day;
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.insert(finalDay);
                    }
                });
            }

            days.add(day);
        }
    }


    public static MealPlanFragment newInstance(MainActivity mainActivity) {
        MealPlanFragment fragment = new MealPlanFragment();
        fragment.mainActivity = mainActivity;


        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflater = inflater;
        View view = inflater.inflate(R.layout.fragment_meal_plan, container, false);
        ButterKnife.bind(this, view);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                days.get(0).getMeals().add(new Recipe("Hot dog"));
            }
        });

        lvMealPlan.setAdapter(new MealAdapter());

        return view;
    }

    private Recipe addingRecipe;

    public void addRecipe(Recipe recipe) {
        addingRecipe = recipe;
    }


    public class MealAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            int i = 0;
            for (Day day : days) {
                i += 1 + day.getMeals().size();
            }

            return i;
        }

        @Override
        public Object getItem(int position) {
            int i = 0;
            for (Day day : days) {
                if (i == position) {
                    return day;
                }

                i += 1;

                for (Recipe recipe : day.getMeals()) {
                    if (i == position) {
                        return recipe;
                    }

                    i += 1;
                }
            }

            throw new IndexOutOfBoundsException("position " + position + "is out of bounds");
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Object item = getItem(position);
            if (item instanceof Day) {
                final Day day = (Day) item;
                View view = inflater.inflate(R.layout.item_day, parent, false);
                TextView tvDay = (TextView) view.findViewById(R.id.tvDay);
                tvDay.setText(day.getName());

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (addingRecipe != null) {
                            day.getMeals().add(addingRecipe);
                            addingRecipe = null;
                            Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.insertOrUpdate(day);
                                }
                            });
                        }
                    }
                });

                return view;
            } else {
                final Recipe recipe = (Recipe) item;
                RecipeView recipeView = new RecipeView(getContext());

                recipeView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle("Alert!!");
                        alert.setMessage("Are you sure to delete record");
                        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (Day day : days) {
                                    if (day.getMeals().remove(recipe)) {
                                        break;
                                    }
                                }
                                MealAdapter.this.notifyDataSetChanged();
                                dialog.dismiss();
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

                recipeView.bind(recipe);
                return recipeView;
            }
        }
    }
}
