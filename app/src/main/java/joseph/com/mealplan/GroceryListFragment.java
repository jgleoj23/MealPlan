package joseph.com.mealplan;

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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

import java.util.Collection;
import java.util.HashMap;
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

import static com.google.common.collect.Iterables.tryFind;
import static joseph.com.mealplan.Utils.capitalize;

public class GroceryListFragment extends Fragment {

    private final String TAG = getClass().getName();
    private SortedSet<Aisle> aisles = new TreeSet<>();
    private GroceryAdapter adapter = new GroceryAdapter();
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

    public static GroceryListFragment newInstance() {
        return new GroceryListFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_grocery_list, container, false);
        ButterKnife.bind(this, view);

        for(int i = 0; i != nameArray.length; i++) {
            valid.put(nameArray[i], number[i]);
        }

        lvResults.setAdapter(adapter);

        for (Grocery grocery : realm.where(Grocery.class).findAll()) {
            loadGroceryFromDisk(grocery);
        }

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGrocery(txAdd.getText().toString());
            }
        });

        return view;
    }

    public void addGrocery(String name) {
        final String capitalized = capitalize(name);
        if (valid.containsKey(capitalized)) {
            showItem(capitalized);
        }
        else {
            Toast.makeText(getContext(), "Not a valid grocery item.", Toast.LENGTH_LONG).show();
        }
    }

    public void addGroceries(List<Grocery> groceries) {
        for (Grocery grocery : groceries) {
            for (String ingredientName : nameArray) {
                if (grocery.getName().toLowerCase().contains(ingredientName.toLowerCase())) { //Making the entire grocery lowercase makes it so we don't have to find the correct word to capitalize
                    addGrocery(ingredientName);
                    break; //It should be break and not return because return terminates the function after one grocery has been added, while break just escapes the inner for loop and moves to the next grocery item
                }
            }
        }
    }

    private void loadGroceryFromDisk(Grocery grocery) {
        getOrAddAisle(grocery.getName()).getGroceries().add(grocery);
        adapter.notifyDataSetChanged();
    }

    private void showItem(final String itemText) {
        final Aisle aisle = getOrAddAisle(itemText);

        Grocery grocery = tryFind(aisle.getGroceries(), new Predicate<Grocery>() {
            @Override
            public boolean apply(@Nullable Grocery grocery) {
                return grocery.getName().contains(itemText);
            }
        }).or(new Supplier<Grocery>() {
            @Override
            public Grocery get() {
                realm.beginTransaction();
                Grocery grocery = realm.createObject(Grocery.class);
                grocery.setName(itemText);
                realm.commitTransaction();
                aisle.getGroceries().add(grocery);
                adapter.notifyDataSetChanged();
                return grocery;
            }
        });

        // TODO add the description of the use to the grocery
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

    private List flattenAisles() {
        return Utils.flatten(aisles, new Function<Aisle, Collection>() {
            @Nullable
            @Override
            public Collection apply(@Nullable Aisle input) {
                return input.getGroceries();
            }
        });
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

            if (item instanceof Aisle) {
                TextView textView = new TextView(getContext());
                textView.setText(((Aisle) item).getAisleName());
                return textView;
            } else {
                View groceryView = LayoutInflater.from(getContext()).inflate(R.layout.item_grocery, parent, false);
                TextView tvGrocery = (TextView) groceryView.findViewById(R.id.tvGrocery);
                final Grocery grocery = (Grocery) item;
                tvGrocery.setText(grocery.getName());

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
    }
}