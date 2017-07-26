package joseph.com.mealplan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import joseph.com.mealplan.model.Recipe;

public class FavoritesFragment extends Fragment {
    @BindView(R.id.rvFavorites)
    RecyclerView rvFavorites;
    ResultsAdapter resultsAdapter = new ResultsAdapter();

    MainActivity mainActivity;
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
        rvFavorites.setAdapter(resultsAdapter);
        for (Recipe recipe : realm.where(Recipe.class).findAll()) {
            if(recipe.getTitle().substring(0, 1).equals("*") && !favorited.contains(recipe.getTitle())) {
                showFavorite(recipe);
                favorited.add(recipe.getTitle());
            }
        }
        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));

//        for (Recipe recipe : realm.where(Recipe.class).findAll()) {
//            favorited.add(recipe.getTitle().toString());
//            resultsAdapter.recipes.add(recipe);
//            resultsAdapter.notifyDataSetChanged();
//        }

        return view;
    }

    ArrayList<String> favorited = new ArrayList<String>();

    public void showFavorite(final Recipe recipe){
        if(!favorited.contains(recipe.getTitle())) {
            resultsAdapter.recipes.add(recipe);
            resultsAdapter.notifyDataSetChanged();
        }
    }
    public void addFavorite(final Recipe recipe) {
        if(!favorited.contains(recipe.getTitle())) {
            realm.beginTransaction();
            recipe.setTitle("*"+recipe.getTitle());
            realm.insertOrUpdate(recipe);
            realm.commitTransaction();
        }
    }


    public class ResultsAdapter extends RecyclerView.Adapter<RecipeSearchViewHolder> {

        private List<Recipe> recipes = new ArrayList<>();

        @Override
        public RecipeSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecipeSearchViewHolder(new RecipeSearchView(getContext()));
        }

        @Override
        public void onBindViewHolder(RecipeSearchViewHolder holder, int position) {
            holder.getRecipeView().bind(recipes.get(position));
        }

        @Override
        public int getItemCount() {
            return recipes.size();
        }
    }
}