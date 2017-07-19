package joseph.com.mealplan;

import android.support.v7.widget.RecyclerView;

/**
 * @author Joseph Gardi
 */
public class RecipeViewHolder extends RecyclerView.ViewHolder {

    private RecipeView recipeView;

    public RecipeViewHolder(RecipeView recipeView) {
        super(recipeView);
        this.recipeView = recipeView;
    }

    public RecipeView getRecipeView() {
        return recipeView;
    }
}