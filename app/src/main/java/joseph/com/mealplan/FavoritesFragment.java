package joseph.com.mealplan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import joseph.com.mealplan.model.Recipe;

public class FavoritesFragment extends Fragment {
    @BindView(R.id.rvResults)
    RecyclerView rvResults;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivPic)
    ImageView ivPic;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this, view);
        return view;
    }

    public class ResultView extends RecyclerView.ViewHolder {

        public ResultView(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }

        public void bind(final Recipe recipe) {
            tvTitle.setText(recipe.getTitle());

            Picasso.with(FavoritesFragment.this.getContext())
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

}
