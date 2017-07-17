package joseph.com.mealplan;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import joseph.com.mealplan.model.Recipe;

/**
 * @author Joseph Gardi
 */
public class SearchFragment extends Fragment {

    private String TAG = getClass().getName();
    private RecipeClient client;
    ArrayList<Recipe> recipes;

    @BindView(R.id.svQuery)
    SearchView svQuery;
    @BindView(R.id.rvResults)
    RecyclerView rvResults;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);

        svQuery.setSuggestionsAdapter(null);
        svQuery.setIconified(false);

        final ResultsAdapter resultsAdapter = new ResultsAdapter();
        rvResults.setAdapter(resultsAdapter);
        rvResults.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchRecipes("chicken");
        svQuery.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if (newText.length() == 3) {
                    resultsAdapter.recipes = new ArrayList<>(Arrays.asList(new Recipe("Hotdog"), new Recipe("Pasta"), new Recipe("Ramen"),
                                                                           new Recipe("Pizza"), new Recipe("Sushi"), new Recipe("Chili")));
                    resultsAdapter.notifyDataSetChanged();
                } else if (newText.length() > 3) {
                    Iterables.removeIf(resultsAdapter.recipes, new Predicate<Recipe>() {
                        @Override
                        public boolean apply(@Nullable Recipe recipe) {
                            return !recipe.getTitle().contains(newText);
                        }
                    });

                    resultsAdapter.notifyDataSetChanged();
                }

                return true;
            }
        });

        return view;
    }


    public class ResultsAdapter extends RecyclerView.Adapter<ResultView> {

        private List<Recipe> recipes = Arrays.asList(new Recipe("Hotdog"), new Recipe("Pasta"), new Recipe("Ramen"),
                                                     new Recipe("Pizza"), new Recipe("Sushi"), new Recipe("Chili"));

        @Override
        public ResultView onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
            return new ResultView(view);
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


    public class ResultView extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTitle)
        TextView tvTitle;

        public ResultView(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }

        public void bind(Recipe recipe) {
            tvTitle.setText(recipe.getTitle());
            
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), RecipeDetailsActivity.class);
                    // TODO intent.putExtra("recipe", recipe)
                    startActivity(intent);
                }
            });
        }
    }

    private void fetchRecipes(String query) {
        client = new RecipeClient();
        client.getRecipes(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        Recipe recipe = Recipe.fromJson(response.getJSONObject(i));
                        recipes.add(recipe);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
}
