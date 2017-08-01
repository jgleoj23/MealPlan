package joseph.com.mealplan;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.woxthebox.draglistview.DragListView;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import joseph.com.mealplan.model.Day;
import joseph.com.mealplan.model.MealRow;
import joseph.com.mealplan.model.Recipe;

import static joseph.com.mealplan.Utils.DAYS_OF_WEEK;
import static joseph.com.mealplan.Utils.findDay;
import static joseph.com.mealplan.Utils.getDayForIndex;

public class MealPlanFragment extends Fragment {

    private String TAG = getClass().getName();

    private MyDragItem myDragItem;
    private ItemAdapter adapter = new ItemAdapter(new ArrayList<MealRow>(),
                                                  new ItemAdapter.MealPlanLongClickListener() {

        @Override
        public void longClicked(final MealRow row) {
            final int position = adapter.getItemList().indexOf(row);
            final Recipe recipe = ((Recipe) row.getData());
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setMessage("Select an option");
            alert.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialog, int which) {
                    adapter.getItemList().remove(position);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();

                    Snackbar.make(lvMealPlan, "Removed recipe", Snackbar.LENGTH_LONG)
                          .setAction("Undo", new View.OnClickListener() {
                              @Override
                              public void onClick(View v1) { //Re-adds the deleted entry
                                  adapter.getItemList().add(position, row);
                                  adapter.notifyDataSetChanged();
                              }
                          })
                          .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                              @Override
                              public void onDismissed(Snackbar transientBottomBar, int event) {
                                  if (event != DISMISS_EVENT_ACTION) {
                                      // The snack bar was not dismissed by the undo button so we are
                                      // going through with the delete
                                      realm.beginTransaction();
                                      getDayForIndex(adapter.getItemList(), position).getMeals().remove(recipe);
                                      realm.commitTransaction();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Which day would you like to duplicate this recipe?");
                    builder.setItems(DAYS_OF_WEEK.toArray(new String[7]),
                                   new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           duplicateRecipe(DAYS_OF_WEEK.get(which), recipe);
                                       }
                                   });
                    builder.create().show();
              }
            });

            alert.show();
          }
    });
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
        myDragItem = new MyDragItem(getContext());

        lvMealPlan.setLayoutManager(new LinearLayoutManager(getContext()));
        lvMealPlan.setCanDragHorizontally(false);
        lvMealPlan.getRecyclerView().setVerticalScrollBarEnabled(true);
        lvMealPlan.setAdapter(adapter, false);
        lvMealPlan.setCustomDragItem(myDragItem);
        lvMealPlan.setDragListListener(new DragListView.DragListListenerAdapter() {

            @Override
            public void onItemDragStarted(int position) {
                myDragItem.getRecipeView().bind(((Recipe) adapter.getItemList().get(position).getData()));
            }

            /**
             * update realm and make background color off the item white again
             */
            @Override
            public void onItemDragEnded(final int fromPosition, final int toPosition) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Recipe meal = (Recipe) adapter.getItemList().get(toPosition).getData();
                        Day fromDay;
                        if (toPosition > fromPosition) {
                            fromDay = getDayForIndex(adapter.getItemList(), fromPosition - 1);
                        } else {
                            fromDay = getDayForIndex(adapter.getItemList(), fromPosition);
                        }
                        fromDay.getMeals().remove(meal);
                        getDayForIndex(adapter.getItemList(), toPosition).getMeals().add(meal);
                    }
                });
            }
        });

        return view;
    }

    public void addRecipeWithDay(Recipe recipe, String dayName) {
        Pair<Integer, Day> listEntry = findDay(adapter.getItemList(), dayName);

        realm.beginTransaction();
        listEntry.second.getMeals().add(recipe);
        realm.commitTransaction();

        adapter.getItemList().add(listEntry.first + 1, new MealRow(recipe));
        adapter.notifyDataSetChanged();
    }

    private void duplicateRecipe(final String dayName, Recipe recipe) {
        Pair<Integer, Day> listEntry = findDay(adapter.getItemList(), dayName);

        adapter.getItemList().add(listEntry.first + 1, new MealRow(recipe));
        adapter.notifyDataSetChanged();

        realm.beginTransaction();
        listEntry.second.getMeals().add(recipe);
        realm.commitTransaction();
    }
}
