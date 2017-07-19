package joseph.com.mealplan;

import android.content.Intent;
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

/**
 * @author Joseph Gardi
 */
public class ResultView extends RecyclerView.ViewHolder {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivPic)
    ImageView ivPic;

    public ResultView(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false));

        ButterKnife.bind(this, itemView);
    }

    public void bind(final Recipe recipe) {
        tvTitle.setText(recipe.getTitle());

        Picasso.with(itemView.getContext())
               .load(recipe.getImageUrl())
               .into(ivPic);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), RecipeDetailsActivity.class);
                intent.putExtra("recipe", Parcels.wrap(recipe));
                itemView.getContext().startActivity(intent);
            }
        });
    }
}
