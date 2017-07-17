package joseph.com.mealplan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import joseph.com.mealplan.model.Recipe;

public class FavoritesFragments extends AppCompatActivity {

    @BindView(R.id.rvFavorites)
    RecyclerView rvFavorites;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_fragments);

        final ResultsAdapter resultsAdapter = new ResultsAdapter();
        rvFavorites.setAdapter(resultsAdapter);
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
}
