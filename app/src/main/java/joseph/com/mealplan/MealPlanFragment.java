package joseph.com.mealplan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import joseph.com.mealplan.model.Day;
import joseph.com.mealplan.model.Recipe;

public class MealPlanFragment extends Fragment {

    private List<Day> days = Arrays.asList(new Day("Sunday"), new Day("Monday"));
    private LayoutInflater inflater;

    @BindView(R.id.lvMealPlan)
    ListView lvMealPlan;

    MainActivity mainActivity;


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

        days.get(0).getMeals().add(new Recipe("Hot dog"));

        lvMealPlan.setAdapter(new MealAdapter());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (addingRecipe != null) {
            // TODO add it and addingRecipe = null
        }
    }

    Recipe addingRecipe;

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
                        }
                    }
                });

                return view;
            } else {
                Recipe recipe = (Recipe) item;
                ResultView resultView = new ResultView(parent);
                resultView.bind(recipe);
                return resultView.itemView;
            }
        }
    }
}
