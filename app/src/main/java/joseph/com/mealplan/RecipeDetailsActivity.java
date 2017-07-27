package joseph.com.mealplan;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import joseph.com.mealplan.model.Recipe;

import static joseph.com.mealplan.MainActivity.instance;

public class RecipeDetailsActivity extends AppCompatActivity {

    private String TAG = getClass().getName();

    private RecipeClient client;
    Recipe recipe;

    TextView tvRecipeName;
    ScaleImageView ivRecipeImage;
    TextView tvRecipeDirections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        tvRecipeName = (TextView) findViewById(R.id.tvRecipeName);
        ivRecipeImage = (ScaleImageView) findViewById(R.id.ivRecipeImage);
        tvRecipeDirections = (TextView) findViewById(R.id.tvRecipeDirections);
        tvRecipeDirections.setMovementMethod(new ScrollingMovementMethod());

        // unwrap recipe passed in via intent

        recipe = Parcels.unwrap(getIntent().getParcelableExtra("recipe"));

        tvRecipeName.setText(recipe.getTitle());
        final Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/DINAlternate-Bold.ttf");
        tvRecipeName.setTypeface(typeface);

        Picasso.with(this)
               .load(recipe.getImageUrl())
               .fit().centerCrop()
               .into(ivRecipeImage);

        ivRecipeImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(recipe.getSourceUrl()));
                startActivity(intent);
            }
        });

        client = new RecipeClient();

        client.getRecipe(recipe.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(TAG, response.toString());
                recipe.addIngredients(response);
                String ingredientsList = "";
                for (int i = 0; i < recipe.getIngredients().size(); i++) {
                    ingredientsList += recipe.getIngredients().get(i).getName();
                    ingredientsList += "\n";
                }

                tvRecipeDirections.setText(ingredientsList);
                tvRecipeDirections.setTypeface(typeface);
            }

            //
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public void goToUrl(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(recipe.getSourceUrl()));
        startActivity(intent);
    }

    public void addFavorites(View view) {
        instance.favorite(recipe);
        Toast.makeText(getApplicationContext(), "Recipe added to favorites", Toast.LENGTH_SHORT).show();
    }

    public void addMealPlan(View view) {
        instance.plan(recipe);
        finish();
    }
}
