package joseph.com.mealplan;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

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

    private String TAG = getClass().getName();
    private RecipeClient client;

    private ResultsAdapter resultsAdapter;

    @BindView(R.id.svQuery)
    SearchView svQuery;
    @BindView(R.id.rvResults)
    RecyclerView rvResults;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("tag", "hello");
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);

        svQuery.setSuggestionsAdapter(null);
        svQuery.setIconified(false);

        resultsAdapter = new ResultsAdapter();
        rvResults.setAdapter(resultsAdapter);
        rvResults.setLayoutManager(new LinearLayoutManager(getContext()));

        svQuery.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if (newText.length() >= 3) {
                    fetchRecipes(newText);
                }

                return true;
            }
        });

        return view;
    }


    public class ResultsAdapter extends RecyclerView.Adapter<ResultView> {

        private List<Recipe> recipes = new ArrayList<>();

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
        @BindView(R.id.ivPic)
        ImageView ivPic;

        public ResultView(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }

        public void bind(final Recipe recipe) {
            tvTitle.setText(recipe.getTitle());

            Picasso.with(SearchFragment.this.getContext())
                   .load(recipe.getImageUrl())
                   .into(ivPic);
            
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), RecipeDetailsActivity.class);
                    intent.putExtra("recipe", Parcels.wrap(recipe));
                    startActivity(intent);
                }
            });
        }
    }

    private void fetchRecipes(String query) {
        client = new RecipeClient();
        client.getRecipes(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(TAG, "result:");
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
