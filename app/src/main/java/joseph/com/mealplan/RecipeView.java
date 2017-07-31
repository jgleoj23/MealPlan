package joseph.com.mealplan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.parceler.Parcels;

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

    public RecipeView(Context context) {
        super(context);

        inflate(getContext(), R.layout.item_recipe, this);
        ButterKnife.bind(this);
    }


    public void bind(final Recipe recipe) {
        tvTitle.setText(recipe.getTitle());

        Picasso.with(getContext())
               .load(recipe.getImageUrl())
               .fit().centerCrop()
               .transform(new CircleTransform())
               .into(ivPic);

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(getContext(), RecipeDetailsActivity.class);
               intent.putExtra("recipe", Parcels.wrap(Recipe.class, recipe));
               getContext().startActivity(intent);
            }
        });

            final Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/DINAlternate-Bold.ttf");
            tvTitle.setTypeface(typeface);
            tvTitle.setText(recipe.getTitle());

            Picasso.with(getContext())
                    .load(recipe.getImageUrl())
                    .fit().centerCrop()
                    .transform(new CircleTransform())
                    .into(ivPic);

            favorites = realm.where(Favorites.class).findFirst();
            if (favorites == null) {
                realm.beginTransaction();
                favorites = realm.createObject(Favorites.class);
                realm.commitTransaction();
            }


            if (favorites.getFavorites().contains(recipe)) {
                ivFave.setVisibility(VISIBLE);
            }
            else{
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

    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size/2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}
