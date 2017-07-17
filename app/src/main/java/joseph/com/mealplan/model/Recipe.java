package joseph.com.mealplan.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Joseph Gardi
 */
public class Recipe {
    private String title;

    public Recipe(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    /* public void setTitle(String title) {
        this.title = title;
    }*/

    public static Recipe fromJson(JSONObject jsonObject) {
        Recipe recipe = null;
        try {
            String recipeTitle = jsonObject.getString("title");
             recipe = new Recipe(recipeTitle);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return recipe;
    }
}
