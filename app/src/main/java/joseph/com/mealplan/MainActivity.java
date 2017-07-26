package joseph.com.mealplan;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

import io.realm.Realm;
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
            mealPlanFragment = MealPlanFragment.newInstance();
        }
        return mealPlanFragment;
    }

    public GroceryListFragment getGroceryListFragment() {
        if (groceryListFragment == null) {
            groceryListFragment = GroceryListFragment.newInstance(MainActivity.this);
        }
        return groceryListFragment;
    }

    public FavoritesFragment getFavoritesFragment() {
        if (favoritesFragment == null) {
            favoritesFragment = FavoritesFragment.newInstance(MainActivity.this);
        }
        return favoritesFragment;
    }

    MainPagerAdapter adapterViewPager;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapterViewPager = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_search_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_local_dining_black_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_shopping_basket_black_24dp);
        tabLayout.getTabAt(3).setIcon(R.drawable.like);
    }

    public class MainPagerAdapter extends FragmentPagerAdapter {
        private  String tabs[] = new String[] {"Search", "Meals", "Grocery", "Favorite"};

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
            return tabs[position];
        }
    }

    public static MainActivity instance;

    public void plan(Recipe recipe) {
        Log.i(TAG, "start plan");
        getMealPlanFragment().addRecipe(recipe);
        viewPager.setCurrentItem(1);
        getGroceryListFragment().addGroceries(recipe.getIngredients());
    }

    ArrayList<String> favorited = new ArrayList<String>();

    public void favorite(Recipe recipe) {
        Log.i(TAG, "add to favorites");
        if(!favorited.contains(recipe.getTitle())) {
            getFavoritesFragment().addFavorite(recipe);
            favorited.add(recipe.getTitle());
        }
    }
}
