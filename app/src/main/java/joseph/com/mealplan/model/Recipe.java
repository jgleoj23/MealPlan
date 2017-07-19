package joseph.com.mealplan.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * @author Joseph Gardi
 */
@Parcel
public class Recipe {
    private String title;
    private String imageUrl;
    private String sourceUrl;
    private String recipeId;
    private ArrayList<String> ingredients;

    public Recipe() {}

    public Recipe(String title) {
        this.title = title;
        this.ingredients = new ArrayList<>();
    }

    public static Recipe fromJson(JSONObject jsonObject) {
        Recipe recipe = null;
        try {
            Log.i("tag", jsonObject.toString());
            String recipeTitle = jsonObject.getString("title");

            recipe = new Recipe(recipeTitle);
            recipe.imageUrl = jsonObject.getString("image_url");
            recipe.sourceUrl = jsonObject.getString("source_url");
            recipe.recipeId = jsonObject.getString("recipe_id");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return recipe;
    }

    public void addIngredients(JSONObject jsonObject) {
        try {
            JSONArray ingredientArray = jsonObject.getJSONObject("recipe").getJSONArray("ingredients");
            int count = ingredientArray.length();
            for (int i = 0; i < ingredientArray.length(); i++) {
                this.ingredients.add(ingredientArray.getString(i));
                Log.i("tag", "adding to ingredients");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<String> getIngredients() {
        return ingredients;
    }
    public String getSourceUrl() {
        return sourceUrl;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getRecipeId() { return recipeId; }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
