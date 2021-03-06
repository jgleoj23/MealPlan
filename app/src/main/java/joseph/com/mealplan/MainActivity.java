package joseph.com.mealplan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;

import org.parceler.Parcels;

import io.realm.Realm;
import joseph.com.mealplan.model.Recipe;

public class MainActivity extends AppCompatActivity {


    private ViewPager viewPager;
    private MealPlanFragment mealPlanFragment;
    private FavoritesFragment favoritesFragment;
    private GroceryListFragment groceryListFragment;


    public MealPlanFragment getMealPlanFragment() {
        if (mealPlanFragment == null) {
            mealPlanFragment = MealPlanFragment.newInstance();
        }
        return mealPlanFragment;
    }

    public GroceryListFragment getGroceryListFragment() {
        if (groceryListFragment == null) {
            groceryListFragment = GroceryListFragment.newInstance();
        }
        return groceryListFragment;
    }

    public FavoritesFragment getFavoritesFragment() {
        if (favoritesFragment == null) {
            favoritesFragment = FavoritesFragment.newInstance();
        }
        return favoritesFragment;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(this);


        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Recipe recipe = Parcels.unwrap(intent.getParcelableExtra("recipe"));
                getFavoritesFragment().addFavorite(recipe);
            }
        }, new IntentFilter("favorite"));

        broadcastManager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Recipe recipe = Parcels.unwrap(intent.getParcelableExtra("recipe"));
                getFavoritesFragment().removeFavorite(recipe);
            }
        }, new IntentFilter("remove-favorite"));

        broadcastManager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Recipe recipe = Parcels.unwrap(intent.getParcelableExtra("recipe"));
                String day = intent.getStringExtra("day");
                getMealPlanFragment().addRecipeWithDay(recipe, day);
                viewPager.setCurrentItem(1);
                getGroceryListFragment().addGroceriesFor(recipe);
            }
        }, new IntentFilter("plan"));

        broadcastManager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Recipe recipe = Parcels.unwrap(intent.getParcelableExtra("recipe"));
                getGroceryListFragment().removeIngredientsFor(recipe);
            }
        }, new IntentFilter("remove-recipe"));



        viewPager = (ViewPager) findViewById(R.id.viewpager);
        MainPagerAdapter adapterViewPager = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        // Close keyboard when switching tabs
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(tabLayout.getWindowToken(), 0);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
            @Override
            public void onTabSelected(TabLayout.Tab tab) {}
        });
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_search_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_local_dining_black_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_shopping_basket_black_24dp);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_vector_heart_stroke);
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
}
