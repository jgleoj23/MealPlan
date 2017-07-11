package joseph.com.mealplan;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Joseph Gardi
 */
public class SearchFragment extends Fragment {

    private String TAG = getClass().getName();

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

        rvResults.setAdapter(new ResultsAdapter());
        rvResults.setLayoutManager(new LinearLayoutManager(getContext()));

        svQuery.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, "submitted");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(TAG, "text changed to: " + newText);
                return true;
            }
        });

        return view;
    }


    public class ResultsAdapter extends RecyclerView.Adapter<ResultView> {

        @Override
        public ResultView onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
            return new ResultView(view);
        }

        @Override
        public void onBindViewHolder(ResultView holder, int position) {}

        @Override
        public int getItemCount() {
            return 20;
        }
    }


    public class ResultView extends RecyclerView.ViewHolder {

        public ResultView(View view) {
            super(view);
        }
    }
}
