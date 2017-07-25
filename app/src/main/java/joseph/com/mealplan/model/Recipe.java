package joseph.com.mealplan.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * @author Joseph Gardi
 */
@RealmClass
@Parcel(value = Parcel.Serialization.BEAN, analyze = { Recipe.class })
public class Recipe extends RealmObject {
    private static String TAG = Recipe.class.getName();

    @PrimaryKey
    private long id;

    private String title;
    private String imageUrl;
    private String sourceUrl;

    private RealmList<Grocery> ingredients = new RealmList<>();

    public Recipe() {}

    public Recipe(String title) {
        this.title = title;
    }

    public static Recipe fromJson(JSONObject jsonObject) {
        Recipe recipe = null;
        try {
            Log.i("tag", jsonObject.toString());
            String recipeTitle = jsonObject.getString("title");

            recipe = new Recipe(recipeTitle);
            recipe.imageUrl = jsonObject.getString("image_url");
            recipe.sourceUrl = jsonObject.getString("source_url");
            recipe.id = jsonObject.getLong("recipe_id");

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
                this.ingredients.add(new Grocery(ingredientArray.getString(i)));
                Log.i("tag", "adding to ingredients");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public List<Grocery> getIngredients() {
        return ingredients;
    }
    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
