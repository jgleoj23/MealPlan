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
        return view;
    }

    public void addFavorite(Recipe recipe) {
        resultsAdapter.recipes.add(recipe);
        resultsAdapter.notifyDataSetChanged();
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
