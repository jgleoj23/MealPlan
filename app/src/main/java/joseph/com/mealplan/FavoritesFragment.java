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
import joseph.com.mealplan.model.Recipe;

public class FavoritesFragment extends Fragment {
    @BindView(R.id.rvFavorites)
    RecyclerView rvFavorites;
    ResultsAdapter resultsAdapter = new ResultsAdapter();

    MainActivity mainActivity;

    //private Realm realm = Realm.getDefaultInstance();

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
        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));

//        for (Recipe recipe : realm.where(Recipe.class).findAll()) {
//            favorited.add(recipe.getTitle().toString());
//            resultsAdapter.recipes.add(recipe);
//            resultsAdapter.notifyDataSetChanged();
//        }

        return view;
    }

    ArrayList<String> favorited = new ArrayList<String>();
    public void addFavorite(final Recipe recipe) {
        if(!favorited.contains(recipe.getTitle())) {
//            realm.executeTransactionAsync(new Realm.Transaction() {
//                @Override
//                public void execute(Realm realm1) {
//                    realm1.insert(recipe);
//                }
//            });
            resultsAdapter.recipes.add(recipe);
            resultsAdapter.notifyDataSetChanged();
        }
    }

    public void removeFavorite(Recipe recipe) {
        resultsAdapter.recipes.remove(recipe);
        resultsAdapter.notifyDataSetChanged();
        favorited.remove(recipe.getTitle());
    }

    public class ResultsAdapter extends RecyclerView.Adapter<RecipeViewHolder> {

        private List<Recipe> recipes = new ArrayList<>();

        @Override
        public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecipeViewHolder(new RecipeView(getContext()));
        }

        @Override
        public void onBindViewHolder(RecipeViewHolder holder, int position) {
            holder.getRecipeView().bind(recipes.get(position));
        }

        @Override
        public int getItemCount() {
            return recipes.size();
        }
    }
}
