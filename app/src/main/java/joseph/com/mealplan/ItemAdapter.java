package joseph.com.mealplan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.woxthebox.draglistview.DragItemAdapter;

import java.util.List;

import joseph.com.mealplan.model.Day;
import joseph.com.mealplan.model.MealRow;
import joseph.com.mealplan.model.Recipe;

/**
 * @author Joseph Gardi
 */
public class ItemAdapter extends DragItemAdapter<MealRow, DragItemAdapter.ViewHolder> {

    private MealPlanLongClickListener listener;

    public ItemAdapter(List<MealRow> list, MealPlanLongClickListener listener) {
        this.listener = listener;
        setHasStableIds(true);
        setItemList(list);
    }


    public interface MealPlanLongClickListener {
        public void longClicked(MealRow row);
    }

    @Override
    public DragItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                return new DayHolder(inflater.inflate(R.layout.item_day, parent, false));
            default:
                return new RecipeHolder(new RecipeView(parent.getContext()));
        }
    }
    public int[] backgroundGenerate = {R.drawable.background_day2, R.drawable.background_day3};
    public int counter = 0;
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof DayHolder) {
            String text = ((Day) mItemList.get(position).getData()).getName();
            ((DayHolder) holder).tvDay.setText(text);
            ((DayHolder) holder).tvDay.setBackgroundResource(backgroundGenerate[counter]);
            counter += 1;
            if(counter == backgroundGenerate.length){
                counter = 0;
            }
            holder.itemView.setTag(mItemList.get(position));
        } else {
            ((RecipeHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mItemList.get(position).getData() instanceof Day) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).getId();
    }

    private class RecipeHolder extends DragItemAdapter.ViewHolder {

        private RecipeView recipeView;

        private RecipeHolder(RecipeView itemView) {
            super(itemView, R.id.ivPic, false);
            this.recipeView = itemView;
        }

        private void bind(final int position) {
            final Recipe recipe = ((Recipe) mItemList.get(position).getData());
            recipeView.bind(recipe);
            final MealRow row = getItemList().get(position);
            recipeView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.longClicked(row);
                    return true;
                }
            });
        }
    }
}
