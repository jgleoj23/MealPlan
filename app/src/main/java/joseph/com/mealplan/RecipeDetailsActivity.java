package joseph.com.mealplan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import joseph.com.mealplan.model.Recipe;

public class RecipeDetailsActivity extends AppCompatActivity {

    private RecipeClient client;
    Recipe recipe;
    FavoritesFragment favoritesFragment;

    TextView tvRecipeName;
    ImageView ivRecipeImage;
    TextView tvRecipeDirections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        tvRecipeName = (TextView) findViewById(R.id.tvRecipeName);
        ivRecipeImage = (ImageView) findViewById(R.id.ivRecipeImage);
        tvRecipeDirections = (TextView) findViewById(R.id.tvRecipeDirections);
        tvRecipeDirections.setMovementMethod(new ScrollingMovementMethod());

        // unwrap recipe passed in via intent

        recipe = Parcels.unwrap(getIntent().getParcelableExtra("recipe"));

        tvRecipeName.setText(recipe.getTitle());

        Picasso.with(this)
               .load(recipe.getImageUrl())
               .fit()
               .into(ivRecipeImage);

        client = new RecipeClient();
//        Log.i("tag", recipe.getRecipeId());
        client.getRecipe(recipe.getRecipeId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                recipe.addIngredients(response);
                String ingredientsList = "";
//                for (int i = 0; i < recipe.getIngredients().size(); i++) {
//                    ingredientsList += recipe.getIngredients().get(i);
//                    ingredientsList += "\n";
//                }
//
//                tvRecipeDirections.setText(ingredientsList);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public void addFavorites(View view) {
        favoritesFragment.resultsAdapter.recipes.add(recipe);
        
        favoritesFragment.resultsAdapter.notifyDataSetChanged();
    }
}
