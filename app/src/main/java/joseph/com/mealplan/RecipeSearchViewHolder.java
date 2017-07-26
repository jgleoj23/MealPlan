package joseph.com.mealplan;

import android.support.v7.widget.RecyclerView;

/**
 * Created by kcguo on 7/26/17.
 */

public class RecipeSearchViewHolder extends RecyclerView.ViewHolder {

    private RecipeSearchView recipeView;

    public RecipeSearchViewHolder(RecipeSearchView recipeView) {
        super(recipeView);
        this.recipeView = recipeView;
    }

    public RecipeSearchView getRecipeView() {
        return recipeView;
    }
}
