package joseph.com.mealplan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import joseph.com.mealplan.model.Recipe;

public class RecipeDetailsActivity extends AppCompatActivity {

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
        // set title

    }
}
