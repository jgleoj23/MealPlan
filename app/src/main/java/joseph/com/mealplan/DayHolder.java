package joseph.com.mealplan;

import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.woxthebox.draglistview.DragItemAdapter;

/**
 * @author Joseph Gardi
 */
class DayHolder extends DragItemAdapter.ViewHolder {
    TextView tvDay;
    DayHolder(final View itemView) {


        super(itemView, R.id.invisibleDummyHandle, false);
        tvDay = ((TextView) itemView.findViewById(R.id.tvDay));
        Typeface typeface = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Lobster-Regular.ttf");
        tvDay.setTypeface(typeface);

    }
}
