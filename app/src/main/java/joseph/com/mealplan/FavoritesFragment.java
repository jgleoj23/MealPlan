package joseph.com.mealplan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    ResultsAdapter resultsAdapter;

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
        resultsAdapter = new ResultsAdapter();
        rvFavorites.setAdapter(resultsAdapter);
        return view;
    }

    public class ResultsAdapter extends RecyclerView.Adapter<ResultView> {

        public List<Recipe> recipes = new ArrayList<>();

        @Override
        public ResultView onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ResultView(parent);
        }

        @Override
        public void onBindViewHolder(ResultView holder, int position) {
            holder.bind(recipes.get(position));
        }

        @Override
        public int getItemCount() {
            return recipes.size();
        }
    }
}
