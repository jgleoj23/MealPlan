package joseph.com.mealplan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import joseph.com.mealplan.model.Recipe;

public class FavoritesFragment extends Fragment {
    @BindView(R.id.lvFavorites)
    ListView lvFavorites;
    MainActivity mainActivity;
    FavoritesAdapter favoritesAdapter;
    ArrayList<Recipe> recipes = new ArrayList<>();
    private Realm realm = Realm.getDefaultInstance();

    public static FavoritesFragment newInstance(MainActivity mainActivity) {
        FavoritesFragment fragment = new FavoritesFragment();
        fragment.mainActivity = mainActivity;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        ButterKnife.bind(this, view);
        super.onCreate(savedInstanceState);
        favoritesAdapter = new FavoritesAdapter(getContext(), recipes);
        lvFavorites.setAdapter(favoritesAdapter);
        for (Recipe recipe : realm.where(Recipe.class).findAll()) {
            if (recipe.getTitle().substring(0, 1).equals("*") && !favorited.contains(recipe.getTitle())) {
                showFavorite(recipe);
                favorited.add(recipe.getTitle());
            }
        }
        setupListViewListener();

        return view;
    }


    private void setupListViewListener() {
        lvFavorites.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            //If list entry is long clicked, delete entry
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Alert!!");
                alert.setMessage("Are you sure you want to unfavorite this recipe?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Recipe recipe = recipes.get(i);
                        realm.beginTransaction();
                        recipe.deleteFromRealm();
                        realm.commitTransaction();
                        dialog.dismiss();
                        recipes.remove(i);
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
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l){
            Intent intent = new Intent(getContext(), RecipeDetailsActivity.class);
                intent.putExtra("recipe", Parcels.wrap(Recipe.class, recipes.get(i)));
                getContext().startActivity(intent);
            }
        });
    }

    ArrayList<String> favorited = new ArrayList<String>();

    public void showFavorite(final Recipe recipe) {
        if (!favorited.contains(recipe.getTitle())) {
            favoritesAdapter.add(recipe);
            favoritesAdapter.notifyDataSetChanged();
        }
    }


    public void addFavorite(final Recipe recipe) {
        if (!favorited.contains(recipe.getTitle())) {
            realm.beginTransaction();
            recipe.setTitle("*" + recipe.getTitle());
            realm.insertOrUpdate(recipe);
            realm.commitTransaction();
        }
    }


    public class FavoritesAdapter extends ArrayAdapter<Recipe> {

        public FavoritesAdapter(Context context, ArrayList<Recipe> recipes) {
            super(context, 0, recipes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            final Recipe recipe = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_recipe, parent, false);
            }
            TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            ImageView ivPic = (ImageView) convertView.findViewById(R.id.ivPic);
                // Lookup view for data population
            tvTitle.setText(recipe.getTitle());
            Picasso.with(getContext())
                        .load(recipe.getImageUrl())
                        .fit().centerCrop()
                        .transform(new CircleTransform())
                        .into(ivPic);


            return convertView;
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


}