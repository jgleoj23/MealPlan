package joseph.com.mealplan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import joseph.com.mealplan.model.Day;
import joseph.com.mealplan.model.MealRow;
import joseph.com.mealplan.model.Recipe;

import static joseph.com.mealplan.Utils.DAYS_OF_WEEK;

public class MealPlanFragment extends Fragment {

    private String TAG = getClass().getName();

    /**
     * for {@link #lvMealPlan}
     */
    private ItemAdapter adapter = new ItemAdapter(new ArrayList<MealRow>());
    private Realm realm = Realm.getDefaultInstance();

    @BindView(R.id.lvMealPlan)
    DragListView lvMealPlan;

    public MealPlanFragment() {
        for (String dayName : DAYS_OF_WEEK) {
            Day day = realm.where(Day.class).equalTo("name", dayName).findFirst();
            if (day == null) {
                realm.beginTransaction();
                day = realm.createObject(Day.class, dayName);
                realm.commitTransaction();
            }
            adapter.getItemList().add(new MealRow(day));
            for (Recipe meal : day.getMeals()) {
                adapter.getItemList().add(new MealRow(meal));
            }
        }
    }


    public static MealPlanFragment newInstance() {
        return new MealPlanFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_plan, container, false);
        ButterKnife.bind(this, view);

        lvMealPlan.setLayoutManager(new LinearLayoutManager(getContext()));
        lvMealPlan.setCanDragHorizontally(false);
        lvMealPlan.setDragListListener(new DragListView.DragListListenerAdapter() {
            /**
             * update realm
             */
            @Override
            public void onItemDragEnded(final int fromPosition, final int toPosition) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Recipe meal = (Recipe) adapter.getItemList().get(toPosition).getData();
                        Day fromDay;
                        if (toPosition > fromPosition) {
                            fromDay = getDayForIndex(fromPosition - 1);
                        } else {
                            fromDay = getDayForIndex(fromPosition);
                        }
                        fromDay.getMeals().remove(meal);
                        getDayForIndex(toPosition).getMeals().add(meal);
                    }
                });
            }
        });

        lvMealPlan.getRecyclerView().setVerticalScrollBarEnabled(true);

        ArrayList<Pair<Long, String>> mItemArray = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            mItemArray.add(new Pair<>((long) i, "Item " + i));
        }

        lvMealPlan.setAdapter(adapter, false);

        return view;
    }

    Day getDayForIndex(int index) {
        for (int i = index; i >= 0; i--) {
            Object item = adapter.getItemList().get(i).getData();
            if (item instanceof Day) {
                return ((Day) item);

            }
        }

        throw new RuntimeException("no day found");
    }
    

    public void addRecipeWithDay(Recipe recipe, final String dayName) {
        int indexOfDay = Iterables.indexOf(adapter.getItemList(), new Predicate<MealRow>() {
            @Override
            public boolean apply(@Nullable MealRow row) {
                return row.getData() instanceof Day &&
                        ((Day) row.getData()).getName().equals(dayName);
            }
        });

        realm.beginTransaction();
        ((Day) adapter.getItemList().get(indexOfDay).getData()).getMeals().add(recipe);
        realm.commitTransaction();
        adapter.getItemList().add(indexOfDay + 1, new MealRow(recipe));
        adapter.notifyDataSetChanged();

    }
}
