package joseph.com.mealplan;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import joseph.com.mealplan.model.Recipe;

/**
 * @author Joseph Gardi
 */
public class SearchFragment extends Fragment {

    public static List<Recipe> bad;

    private String TAG = getClass().getName();
    private RecipeClient client = new RecipeClient();

    private ResultsAdapter resultsAdapter;

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
        svQuery.clearFocus();

        resultsAdapter = new ResultsAdapter();
        rvResults.setAdapter(resultsAdapter);
        rvResults.setLayoutManager(new LinearLayoutManager(getContext()));

        int id = svQuery.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) svQuery.findViewById(id);
        textView.setTextColor(Color.WHITE);

        svQuery.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchRecipes(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                return true;
            }
        });

        return view;
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

    private void fetchRecipes(final String query) {
        Log.i(TAG, "fetch");
        client.cancelRequests();
        client.searchRecipes(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(TAG, "result for :" + query);
                resultsAdapter.recipes.clear();
                try {
                    JSONArray results = response.getJSONArray("recipes");
                    for (int i = 0; i < results.length(); i++) {
                        try {
                            Recipe recipe = Recipe.fromJson(results.getJSONObject(i));
                            resultsAdapter.recipes.add(recipe);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    resultsAdapter.notifyDataSetChanged();
                    bad = resultsAdapter.recipes;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
}