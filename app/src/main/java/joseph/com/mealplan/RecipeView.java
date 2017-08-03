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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import joseph.com.mealplan.model.Favorites;
import joseph.com.mealplan.model.Recipe;

/**
 * @author Joseph Gardi
 */
public class RecipeView extends RelativeLayout {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivPic)
    ImageView ivPic;
    @BindView(R.id.ivFave)
    ImageView ivFave;

    Realm realm = Realm.getDefaultInstance();
    Favorites favorites;
    ArrayList<String> favorite_name = new ArrayList<String>();

    public RecipeView(Context context) {
        super(context);
        inflate(getContext(), R.layout.item_recipe, this);
        ButterKnife.bind(this);
    }

    public void bind(final Recipe recipe) {
        final Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/DINAlternate-Bold.ttf");
        tvTitle.setTypeface(typeface);
        tvTitle.setText(recipe.getTitle());

        Picasso.with(getContext())
                .load(recipe.getImageUrl())
                .fit().centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(ivPic);

        favorites = realm.where(Favorites.class).findFirst();
        if (favorites == null) {
            realm.beginTransaction();
            favorites = realm.createObject(Favorites.class);
            realm.commitTransaction();
        }
        for(Recipe recipe1 : favorites.getFavorites()){
            if(recipe1.getTitle() != null) {
                favorite_name.add(recipe1.getTitle());
            }
        }

        if (favorite_name.contains(recipe.getTitle())) {
            ivFave.setVisibility(VISIBLE);
        }
        else {
            ivFave.setVisibility(INVISIBLE);
        }

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
