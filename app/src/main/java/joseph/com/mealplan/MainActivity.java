package joseph.com.mealplan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import joseph.com.mealplan.model.Recipe;

public class MainActivity extends AppCompatActivity {

    private String TAG = getClass().getName();
    private ViewPager viewPager;

    private MealPlanFragment mealPlanFragment;
    private FavoritesFragment favoritesFragment;
    private GroceryListFragment groceryListFragment;

    {
        instance = this;
    }

    public MealPlanFragment getMealPlanFragment() {
        if (mealPlanFragment == null) {
            mealPlanFragment = MealPlanFragment.newInstance(MainActivity.this);
        }
        return mealPlanFragment;
    }

    public GroceryListFragment getGroceryListFragment() {
        if (groceryListFragment == null) {
            groceryListFragment = groceryListFragment.newInstance(MainActivity.this);
        }
        return groceryListFragment;
    }

    public FavoritesFragment getFavoritesFragment() {
        if (favoritesFragment == null) {
            favoritesFragment = favoritesFragment.newInstance(MainActivity.this);
        }
        return favoritesFragment;
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
    }

    public class MainPagerAdapter extends FragmentPagerAdapter {

        private List<String> tabs = Arrays.asList("Search", "Meal Plan", "Grocery", "Favorites");

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SearchFragment();
                case 1:
                    return getMealPlanFragment();
                case 2:
                    return getGroceryListFragment();
                case 3:
                    return getFavoritesFragment();
                default:
                    throw new RuntimeException("position " + position + " is out of bounds");
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs.get(position);
        }
    }

    public static MainActivity instance;

    public void plan(Recipe recipe) {
        Log.i(TAG, "start plan");
        getMealPlanFragment().addRecipe(recipe);
        viewPager.setCurrentItem(1);
    }

    public void favorite(Recipe recipe) {
        Log.i(TAG, "add to favorites");
        getFavoritesFragment().addFavorite(recipe);
        viewPager.setCurrentItem(3);
    }
}
