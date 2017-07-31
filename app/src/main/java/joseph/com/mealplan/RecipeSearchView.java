package joseph.com.mealplan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import joseph.com.mealplan.model.Recipe;

/**
 * Created by kcguo on 7/26/17.
 */

public class RecipeSearchView extends RelativeLayout{
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivPic)
    ImageView ivPic;

    public RecipeSearchView(Context context) {
        super(context);
        inflate(getContext(), R.layout.item_recipe_search, this);
        ButterKnife.bind(this);

    }

    public void bind(final Recipe recipe) {

        tvTitle.setText(recipe.getTitle());


        Picasso.with(getContext())
                .load(recipe.getImageUrl())
                .fit().centerCrop()
                .into(ivPic);

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RecipeDetailsActivity.class);
                intent.putExtra("recipe", Parcels.wrap(Recipe.class, recipe));
                getContext().startActivity(intent);
            }
        });

    }
}