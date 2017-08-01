package joseph.com.mealplan;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import joseph.com.mealplan.model.Favorites;
import joseph.com.mealplan.model.Recipe;

public class FavoritesFragment extends Fragment {

    private FavoritesAdapter favoritesAdapter;
    private Favorites favorites;
    private Realm realm = Realm.getDefaultInstance();

    @BindView(R.id.lvFavorites)
    ListView lvFavorites;

    public FavoritesFragment() {
        favorites = realm.where(Favorites.class).findFirst();
        if (favorites == null) {
            realm.beginTransaction();
            favorites = realm.createObject(Favorites.class);
            realm.commitTransaction();
        }
    }

    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        ButterKnife.bind(this, view);
        super.onCreate(savedInstanceState);

        favoritesAdapter = new FavoritesAdapter();
        lvFavorites.setAdapter(favoritesAdapter);

        setupListViewListener();

        return view;
    }


    private void setupListViewListener() {
        lvFavorites.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            //If list entry is long clicked, delete entry
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure you want to unfavorite this recipe?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Recipe recipe = favorites.getFavorites().get(i);
                        realm.beginTransaction();
                        favorites.getFavorites().remove(recipe);
                        realm.commitTransaction();
                        dialog.dismiss();
                        favoritesAdapter.notifyDataSetChanged();
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();

                return true;
            }
        });

        lvFavorites.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            //If list entry is long clicked, delete entry
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
            Intent intent = new Intent(getContext(), RecipeDetailsActivity.class);
                intent.putExtra("recipe", Parcels.wrap(Recipe.class, favorites.getFavorites().get(i)));
                getContext().startActivity(intent);
            }
        });
    }

    public void addFavorite(final Recipe recipe) {
        // I have to make sure I'm not adding the same recipe twice
        if (favorites.getFavorites().where().equalTo("id", recipe.getId()).findFirst() == null) {
            realm.beginTransaction();
            favorites.getFavorites().add(recipe);
            realm.insertOrUpdate(recipe);
            realm.commitTransaction();

            if (favoritesAdapter != null) {
                favoritesAdapter.notifyDataSetChanged();
            }
        }
    }



    private class FavoritesAdapter extends ArrayAdapter<Recipe> {

        private FavoritesAdapter() {
            super(FavoritesFragment.this.getContext(), 0, favorites.getFavorites());
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            // Get the data item for this position
            final Recipe recipe = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_recipe, parent, false);
            }

            TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            ImageView ivPic = (ImageView) convertView.findViewById(R.id.ivPic);
            final Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/DINAlternate-Bold.ttf");
            tvTitle.setTypeface(typeface);
            tvTitle.setText(recipe.getTitle());
            Picasso.with(getContext())
                        .load(recipe.getImageUrl())
                        .fit().centerCrop()
                        //.transform(new CircleTransform())
                        .into(ivPic);


            return convertView;
        }

        private class CircleTransform implements Transformation {
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


}