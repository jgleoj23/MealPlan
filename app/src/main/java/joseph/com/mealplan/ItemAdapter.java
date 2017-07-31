package joseph.com.mealplan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.woxthebox.draglistview.DragItemAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import joseph.com.mealplan.model.Day;
import joseph.com.mealplan.model.MealRow;
import joseph.com.mealplan.model.Recipe;

/**
 * @author Joseph Gardi
 */
public class ItemAdapter extends DragItemAdapter<MealRow, DragItemAdapter.ViewHolder> {

    ItemAdapter(List<MealRow> list) {
        setHasStableIds(true);
        setItemList(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                return new DayHolder(inflater.inflate(R.layout.item_day, parent, false));
            default:
                return new RecipeHolder(new RecipeView(parent.getContext()));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof DayHolder) {
            String text = ((Day) mItemList.get(position).getData()).getName();
            ((DayHolder) holder).tvDay.setText(text);
            holder.itemView.setTag(mItemList.get(position));
        } else {
            ((RecipeHolder) holder).recipeView.bind(((Recipe) mItemList.get(position).getData()));
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

    class DayHolder extends DragItemAdapter.ViewHolder {
        @BindView(R.id.tvDay)
        TextView tvDay;

        DayHolder(final View itemView) {
            super(itemView, R.id.invisibleDummyHandle, false);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onItemClicked(View view) {
            Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClicked(View view) {
            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private class RecipeHolder extends DragItemAdapter.ViewHolder {

        private RecipeView recipeView;

        private RecipeHolder(RecipeView itemView) {
            super(itemView, R.id.ivPic, false);
            this.recipeView = itemView;
        }
    }
}
