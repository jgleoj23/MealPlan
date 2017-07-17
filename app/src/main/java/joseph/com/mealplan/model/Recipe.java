package joseph.com.mealplan.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * @author Joseph Gardi
 */
@Parcel
public class Recipe {
    private String title;
    private String imageUrl;
    private String sourceUrl;

    public Recipe() {}

    public Recipe(String title) {
        this.title = title;
    }

    public static Recipe fromJson(JSONObject jsonObject) {
        Recipe recipe = null;
        try {
            String recipeTitle = jsonObject.getString("title");

            recipe = new Recipe(recipeTitle);
            recipe.imageUrl = jsonObject.getString("image_url");
            recipe.sourceUrl = jsonObject.getString("source_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return recipe;
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

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
