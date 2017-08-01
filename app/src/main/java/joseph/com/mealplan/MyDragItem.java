package joseph.com.mealplan;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.woxthebox.draglistview.DragItem;

/**
 * Makes a custom style for recipe when it is being dragged
 *
 * @author Joseph Gardi
 */
public class MyDragItem extends DragItem {
    private RecipeView recipeView;

    public MyDragItem(Context context) {
        super(context, R.layout.drag);
        recipeView = new RecipeView(context);
        int color = context.getResources().getColor(R.color.list_item_background, null);
        recipeView.findViewById(R.id.cv).setBackgroundColor(color);
    }

    @Override
    public void onBindDragView(View clickedView, View dragView) {
        // I won't be able to add the recipe view wto the dragView if it already has a parent
        ViewGroup parent = (ViewGroup) recipeView.getParent();
        if (parent != null) {
            parent.removeView(recipeView);
        }
        ((RelativeLayout) dragView.findViewById(R.id.drag)).addView(recipeView);
    }

    public RecipeView getRecipeView() {
        return recipeView;
    }
}
