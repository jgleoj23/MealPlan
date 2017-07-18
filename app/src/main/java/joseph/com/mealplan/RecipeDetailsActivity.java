package joseph.com.mealplan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import joseph.com.mealplan.model.Recipe;

public class RecipeDetailsActivity extends AppCompatActivity {

    private RecipeClient client;
    Recipe recipe;

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

        Glide.with(this)
                .load(recipe.getImageUrl())
                .centerCrop()
                .into(ivRecipeImage);

        client = new RecipeClient();
        Log.i("tag", recipe.getRecipeId());
        client.getRecipe(recipe.getRecipeId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                recipe.addIngredients(response);
                String ingredientsList = "";
                int count = recipe.getIngredients().size();
                for (int i = 0; i < recipe.getIngredients().size(); i++) {
                    ingredientsList += recipe.getIngredients().get(i);
                    ingredientsList += "\n";
                }


                tvRecipeDirections.setText(ingredientsList);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
}
