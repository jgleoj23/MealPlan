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

//    class DayHolder extends DragItemAdapter.ViewHolder {
//
//        @BindView(R.id.tvDay)
//        TextView tvDay;
//
//        private DayHolder(View itemView) {
//            // I don't want them to drag days
//            super(itemView, R.id.tvDay, false);
//            ButterKnife.bind(this, itemView);
//        }
//
//        private void bind(Day day) {
//            tvDay.setText(day.getName());
//        }
//    }
//
//    private class RecipeHolder extends DragItemAdapter.ViewHolder {
//
//        private RecipeView recipeView;
//
//        private RecipeHolder(RecipeView itemView) {
//            super(itemView, R.id.ivPic, false);
//            this.recipeView = itemView;
//        }
//    }

//    private class DragAdapter extends DragItemAdapter<MealRow, DayHolder> {
//
//        DragAdapter() {
//            setHasStableIds(true);
//            setItemList(rows);
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            if (rows.get(position).getData() instanceof Day) {
//                return 0;
//            } else {
//                return 1;
//            }
//        }
//
//        @Override
//        public DayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            LayoutInflater inflater = LayoutInflater.from(getContext());
////            switch (viewType) {
////                case 0:
//                    return new DayHolder(inflater.inflate(R.layout.item_day, parent, false));
////                default:
////                    return new RecipeHolder(new RecipeView(getContext()));
////            }
//        }
//
//        @Override
//        public void onBindViewHolder(DayHolder holder, int position) {
//            holder.bind((Day) rows.get(position).getData());
////            if (holder instanceof DayHolder) {
////                ((DayHolder) holder).bind(((Day) rows.get(position).getData()));
////            } else {
////                ((RecipeHolder) holder).recipeView.bind(((Recipe) rows.get(position).getData()));
////            }
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return rows.get(position).getId();
//        }
//    }

//    class MealAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return flattenDays().size();
//        }
//
//
//        @Override
//        public Object getItem(int position) {
//            return flattenDays().get(position);
//        }
//
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            Log.i(TAG, "getView: " + position);
//            Object item = getItem(position);
//            if (item instanceof Day) {
//                final Day day = (Day) item;
//                View view = LayoutInflater.from(getContext()).inflate(R.layout.item_day, parent, false);
//                TextView tvDay = (TextView) view.findViewById(R.id.tvDay);
//                tvDay.setText(day.getName());
//
//                return view;
//            } else {
//                final Recipe recipe = (Recipe) item;
//                Log.i(TAG, "recipe: " + recipe.getTitle());
//                final RecipeView recipeView = new RecipeView(getContext());
//                recipeView.bind(recipe);
//
//                recipeView.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
//                        alert.setMessage("Select an option");
//                        alert.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Object intendedDay = getItem(position);
//                                for (int j = position; intendedDay instanceof  Recipe; j--) {
//                                    intendedDay = getItem(j);
//                                }
//
//                                final Day dayForRecipe = ((Day) intendedDay);
//                                realm.beginTransaction();
//                                dayForRecipe.getMeals().remove(recipe);
//                                realm.commitTransaction();
//                                dialog.dismiss();
//                                MealAdapter.this.notifyDataSetChanged();
//
//
//                                Snackbar.make(recipeView, "Removed recipe", Snackbar.LENGTH_LONG)
//                                        .setAction("Undo", new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v1) { //Re-adds the deleted entry
//                                                realm.beginTransaction();
//                                                dayForRecipe.getMeals().add(recipe);
//                                                realm.commitTransaction();
//                                                MealAdapter.this.notifyDataSetChanged();
//                                            }
//                                        })
//                                        .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
//                                            @Override
//                                            public void onDismissed(Snackbar transientBottomBar, int event) {
//                                                if (event != DISMISS_EVENT_ACTION) {
//                                                    // The snack bar was not dismissed by the undo button so we are
//                                                    // going through with the delete
//                                                    Intent intent = new Intent("remove-recipe");
//                                                    intent.putExtra("recipe", Parcels.wrap(Recipe.class, recipe));
//                                                    LocalBroadcastManager.getInstance(getContext())
//                                                                         .sendBroadcast(intent);
//                                                }
//                                            }
//                                        })
//                                        .show();
//
//                            }
//                        });
//
//                        alert.setNeutralButton("DUPLICATE", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                final Context context = getContext();
//                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                                builder.setTitle("Which day would you like to duplicate this recipe?");
//                                builder.setItems(FluentIterable.from(days).transform(new Function<Day, String>() {
//                                    @Nullable
//                                    @Override
//                                    public String apply(@Nullable Day day) {
//                                        return day.getName();
//                                    }
//                                }).toArray(String.class), new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        duplicateRecipe(days.get(which).getName(), recipe);
//                                    }
//                                });
//
//
//                                builder.setItems(DAYS_OF_WEEK.toArray(new String[7]),
//                                                 new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                // The 'which' argument contains the index position
//                                                // of the selected item
//                                                duplicateRecipe(DAYS_OF_WEEK.get(which), recipe);
//                                            }
//                                        });
//                                builder.create().show();
//                            }
//                        });
//
//                        alert.show();
//                    return true;}
//                });
//
//                return recipeView;
//            }
//        }
//
//        public void duplicateRecipe(final String dayName, Recipe recipe){
//            realm.beginTransaction();
//            for (Day day : days) {
//                if (day.getName().equals(dayName)) {
//                        day.getMeals().add(recipe);
//                        break;
//                }
//            }
//            realm.commitTransaction();
//            MealAdapter.this.notifyDataSetChanged();
//        }
//    }
}
