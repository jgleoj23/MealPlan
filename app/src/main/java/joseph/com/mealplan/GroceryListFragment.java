package joseph.com.mealplan;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import joseph.com.mealplan.model.Aisle;
import joseph.com.mealplan.model.Grocery;
import joseph.com.mealplan.model.Recipe;
import joseph.com.mealplan.model.Use;

import static android.text.TextUtils.join;
import static com.google.common.collect.Iterables.tryFind;
import static joseph.com.mealplan.Utils.capitalize;

public class GroceryListFragment extends Fragment {

    private final String TAG = getClass().getName();
    private SortedSet<Aisle> aisles = new TreeSet<>();
    private GroceryAdapter adapter = new GroceryAdapter();
    /**
     * maps ingredient names to their aisle number
     */
    private Map<String, Integer> valid = new HashMap<>();
    private Realm realm = Realm.getDefaultInstance();

    @BindView(R.id.lvGrocery)
    ListView lvResults;
    @BindView(R.id.txAdd)
    EditText txAdd;
    @BindView(R.id.btAdd)
    Button btAdd;

    private String[] nameArray = {"Sour cream", "Olive oil", "Canola oil", "Black pepper", "Vanilla extract",
            "Cream cheese", "Sour cream", "Graham cracker", "Cocoa", "Salt", "Chocolate", "Ham", "Cheese", "Pineapple",
            "Milk", "Bread", "Kiwi", "Butter", "Rice", "Pasta", "Tomato", "Steak", "French fries", "Avocado", "Cookies",
            "Cake", "Water", "Onion", "Carrot", "Garlic", "Spinach", "Ramen", "Chicken", "Cheesecake", "Sugar", "Egg",
            "Lemon", "Flour", "Potato"};
    private int[] number = {1, 4, 4, 3, 2, 1, 1, 2, 2, 2, 2, 5, 1, 3, 1, 2, 3, 1, 4, 4, 3, 5, 1, 3, 2, 2, 1, 3, 3, 3, 3,
            4, 5, 2, 2, 1, 3, 2, 3};

    public GroceryListFragment() {
        for(int i = 0; i != nameArray.length; i++) {
            valid.put(nameArray[i], number[i]);
        }

        for (Grocery grocery : realm.where(Grocery.class).findAll()) {
            loadGroceryFromDisk(grocery);
        }
    }

    public static GroceryListFragment newInstance() {
        return new GroceryListFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_grocery_list, container, false);
        ButterKnife.bind(this, view);

        lvResults.setAdapter(adapter);

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String capitalized = capitalize(txAdd.getText().toString());
                if (valid.containsKey(capitalized)) {
                    final Grocery grocery = getOrAddGrocery(capitalized);
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            grocery.setWasAddedManullay(true);
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Not a valid grocery item.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    public void addGroceriesFor(Recipe recipe) {
        for (Grocery grocery : recipe.getIngredients()) {
            for (final String ingredientName : nameArray) {
                // Make it all lowercase so that it is case insensitive
                if (grocery.getName().toLowerCase().contains(ingredientName.toLowerCase())) {
                    final String useDescription = grocery.getName() + " for the " + recipe.getTitle();
                    final Grocery groceryForList = getOrAddGrocery(ingredientName);
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            groceryForList.getUses().add(new Use(useDescription));
                        }
                    });
                    // It should be break and not return to stop looking for grocery but still add the rest of the
                    // groceries
                    break;
                }
            }
        }
    }

    private void loadGroceryFromDisk(Grocery grocery) {
        if(valid.containsKey(grocery.getName())) {
            getOrAddAisle(grocery.getName()).getGroceries().add(grocery);
        }
    }

    private Grocery getOrAddGrocery(final String groceryName) {
        final Aisle aisle = getOrAddAisle(groceryName);

        return tryFind(aisle.getGroceries(), new Predicate<Grocery>() {
            @Override
            public boolean apply(@Nullable Grocery grocery) {
                return grocery.getName().contains(groceryName);
            }
        }).or(new Supplier<Grocery>() {
            @Override
            public Grocery get() {
                realm.beginTransaction();
                Grocery grocery = realm.createObject(Grocery.class);
                grocery.setName(groceryName);
                realm.commitTransaction();
                aisle.getGroceries().add(grocery);
                adapter.notifyDataSetChanged();
                return grocery;
            }
        });
    }

    private Aisle getOrAddAisle(final String groceryName) {
        final int aisleNumber = valid.get(groceryName);
        return tryFind(aisles, new Predicate<Aisle>() {
            @Override
            public boolean apply(@Nullable Aisle aisle) {
                return aisle.getAisleName().equals("Aisle #" + aisleNumber);
            }
        }).or(new Supplier<Aisle>() {
            @Override
            public Aisle get() {
                Aisle aisle = new Aisle();
                aisle.setAisleNumber(aisleNumber);
                aisles.add(aisle);
                return aisle;
            }
        });
    }

    public void removeIngredientsFor(final Recipe recipe) {
        Log.i(TAG, "removing ingredients");
        for (Iterator<Aisle> aisleIterator = aisles.iterator(); aisleIterator.hasNext();) {
            Aisle aisle = aisleIterator.next();
            for (final Iterator<Grocery> groceryIterator = aisle.getGroceries().iterator();
                 groceryIterator.hasNext();) {
                final Grocery grocery = groceryIterator.next();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        for (Iterator<Use> useIterator = grocery.getUses().iterator();
                             useIterator.hasNext();) {
                            if (useIterator.next().getUse().contains(recipe.getTitle())) {
                                Log.i(TAG, "removing a use");
                                useIterator.remove();
                            }
                        }

                        if (grocery.getUses().isEmpty() && !grocery.wasAddedManually()) {
                            groceryIterator.remove();
                            grocery.deleteFromRealm();
                        }
                    }
                });
            }

            if (aisle.getGroceries().isEmpty()) {
                aisleIterator.remove();
            }
        }

        adapter.notifyDataSetChanged();
    }


    private class GroceryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return flattenAisles().size();
        }

        @Override
        public Object getItem(int position) {
            return flattenAisles().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Object item = getItem(position);
            final Typeface typeface;
            if (item instanceof Aisle) {
                typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Lobster-Regular.ttf");
                View aisleView = LayoutInflater.from(getContext()).inflate(R.layout.item_aisle, parent, false);
                TextView tvAisle = (TextView) aisleView.findViewById(R.id.tvAisle);
                tvAisle.setTypeface(typeface);
                tvAisle.setText(((Aisle) item).getAisleName());
                return aisleView;

            } else {
                typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/DINAlternate-Bold.ttf");
                View groceryView = LayoutInflater.from(getContext()).inflate(R.layout.item_grocery, parent, false);
                TextView tvGrocery = (TextView) groceryView.findViewById(R.id.tvGrocery);
                final Grocery grocery = (Grocery) item;
                tvGrocery.setTypeface(typeface);
                tvGrocery.setText(grocery.getName());

                final ExpandableRelativeLayout expandableLayout =
                        ((ExpandableRelativeLayout) groceryView.findViewById(R.id.expandableLayout));
                expandableLayout.collapse();

                groceryView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "toggling");
                        expandableLayout.toggle();
                    }
                });

                TextView tvUses = ((TextView) groceryView.findViewById(R.id.tvUses));
                List<String> useDescriptions = FluentIterable.from(grocery.getUses())
                                                             .transform(new Function<Use, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable Use use) {
                        return use.getUse();
                    }
                }).toList();

                if(useDescriptions.size() == 0){
                    tvUses.setText("");
                }
                else {
                    tvUses.setTypeface(typeface);
                    tvUses.setText("• " + join("\n\n• ", useDescriptions));
                }

                groceryView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Aisle aisle = getOrAddAisle(grocery.getName());
                        aisle.getGroceries().remove(grocery);
                        if (aisle.getGroceries().isEmpty()) {
                            aisles.remove(aisle);
                        }

                        adapter.notifyDataSetChanged();

                        Snackbar.make(lvResults, "Removed Grocery", Snackbar.LENGTH_LONG)
                                .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v1) {
                                        // This way aisle will be added back if I removed it
                                        Aisle aisle = getOrAddAisle(grocery.getName());
                                        aisle.getGroceries().add(grocery);
                                        adapter.notifyDataSetChanged();
                                    }
                                })
                                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                    @Override
                                    public void onDismissed(Snackbar transientBottomBar, int event) {
                                        if (event != DISMISS_EVENT_ACTION) {
                                            // The snack bar was not dismissed by the undo button so we are
                                            // going through with the delete
                                            realm.executeTransaction(new Realm.Transaction() {
                                                @Override
                                                public void execute(Realm realm) {
                                                    Log.i(TAG, "delete from Realm");
                                                    grocery.deleteFromRealm();
                                                }
                                            });
                                        }
                                    }
                                })
                                .show();

                        return true;
                    }
                });

                return groceryView;
            }
        }

        /**
         * converts aisles into a format that corresponds to the rows of the ListView
         * e.g. [Aisle("Aisle #1", Grocery("Sour Cream"), Aisle("Aisle #2"), ...]
         */
        private List flattenAisles() {
            return Utils.flatten(aisles, new Function<Aisle, Collection>() {
                @Nullable
                @Override
                public Collection apply(@Nullable Aisle input) {
                    return input.getGroceries();
                }
            });
        }
    }
}