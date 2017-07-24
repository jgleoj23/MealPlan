package joseph.com.mealplan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import joseph.com.mealplan.model.Grocery;

public class GroceryListFragment extends Fragment {
    List<Map<String, String>> listItems;
    // Use Map as the type
    Map<String, String> resultsMap = new HashMap<>();
    SimpleAdapter adapter;
    // You don't need Hashtable unless you are using threads
    Hashtable<String, Integer> valid = new Hashtable<String, Integer>();
    MainActivity mainActivity;

    @BindView(R.id.lvGrocery)
    ListView resultsListView;
    @BindView(R.id.txAdd)
    EditText txAdd;
    @BindView(R.id.btAdd)
    Button btAdd;

    private Realm realm = Realm.getDefaultInstance();

    public static GroceryListFragment newInstance(MainActivity mainActivity) {
        GroceryListFragment fragment = new GroceryListFragment();
        fragment.mainActivity = mainActivity;
        return fragment;
    }

    String[] nameArray = {"Sour cream", "Olive oil", "Canola oil", "Black pepper", "Vanilla extract", "Cream cheese", "Sour cream", "Graham cracker", "Cocoa", "Salt", "Chocolate", "Ham", "Cheese", "Pineapple", "Milk", "Bread", "Kiwi", "Butter", "Rice", "Pasta", "Tomato", "Steak", "French fries", "Avocado", "Cookies", "Cake", "Water", "Onion", "Carrot", "Garlic", "Spinach", "Ramen", "Chicken", "Cheesecake", "Sugar", "Egg", "Lemon", "Flour", "Potato"};
    int[] number = {1, 4, 4, 3, 2, 1, 1, 2, 2, 2, 2, 5, 1, 3, 1, 2, 3, 1, 4, 4, 3, 5, 1, 3, 2, 2, 1, 3, 3, 3, 3, 4, 5, 2, 2, 1, 3, 2, 3};
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_grocery_list, container, false);
        ButterKnife.bind(this, view);

        //Creates a hash table of valid grocery items
        for(int i = 0; i != 39; i++){
            valid.put(nameArray[i], number[i]);
        }

        //Sets up adapter to allow for Aisle #: grocery layout
        listItems = new ArrayList<>(6);

        adapter = new SimpleAdapter(getContext(), listItems, R.layout.item_grocery,
                new String[]{"First Line", "Second Line"},
                new int[]{R.id.txAisle, R.id.txGroc});
        resultsListView.setAdapter(adapter);

        setupListViewListener();

        for (Grocery grocery : realm.where(Grocery.class).findAll()) {
            showItem(grocery.getName());
        }

        return view;
    }



    private void setupListViewListener(){
        resultsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            //If list entry is long clicked, delete entry
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                Log.i("MainActivity", "Item Removed:" + i);
                resultsMap = listItems.get(i);
                listItems.remove(i);
                final int x = i;
                adapter.notifyDataSetChanged();
                View.OnClickListener undoDelete = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //Re-adds the deleted entry
                        listItems.add(x, resultsMap);
                        adapter.notifyDataSetChanged();
                    }
                };
                //Displays snackbar, which allows for undoing the delete
                Snackbar.make(resultsListView, "Removed Aisle", Snackbar.LENGTH_LONG)
                        .setAction("Undo", undoDelete)
                        .show();
                return true;
            }
        });

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGrocery(txAdd.getText().toString());

            }
        });
    }

    public void addGrocery(String name) {
        String capitalized = GroceryListFragment.this.capitalize(name);
        if (valid.containsKey(capitalized)) {
            saveItemToDB(capitalized);
            showItem(capitalized);
        } else {
            Toast.makeText(getContext(), "Not a valid grocery item.", Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private void saveItemToDB(final String itemText) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insert(new Grocery(itemText));
            }
        });
    }


    public void showItem(final String itemText) {
         Map<String, String> resultsMap = Iterables.tryFind(listItems, new Predicate<Map<String, String>>() {
            @Override
            public boolean apply(@Nullable Map<String, String> resultsMap) {
                return resultsMap.get("First Line").equals("Aisle #" + valid.get(itemText));
            }
        }).orNull();

        if (resultsMap != null) {
            String old = resultsMap.get("Second Line");
            if(!old.contains(itemText)) {
                resultsMap.put("Second Line", old + "-" + itemText + "\n");
                adapter.notifyDataSetChanged();
                txAdd.setText("");
            } else {
                Toast.makeText(getContext(), "Grocery already in list.", Toast.LENGTH_LONG).show();
            }
        } else {
            resultsMap = new HashMap<>();

            resultsMap.put("First Line", "Aisle #" + valid.get(itemText));
            resultsMap.put("Second Line", "-" + itemText+ "\n");
            boolean changed = false;
            if(listItems.size() == 0) {
                listItems.add(resultsMap);
            } else {
                for (int i = 0; i != listItems.size(); i++) {
                    String number = listItems.get(i).get("First Line");
                    int x = Integer.valueOf(number.substring(number.indexOf("#") + 1));
                    if (x > valid.get(itemText)) {
                        listItems.add(i, resultsMap);
                        changed = true;
                        break;
                    }
                }

                if (!changed) {
                    listItems.add(resultsMap);
                }
            }
            adapter.notifyDataSetChanged();
            txAdd.setText("");
        }
    }


    public void addGroceries(List<Grocery> groceries) {
        for (Grocery grocery : groceries) {
            String name = grocery.getName().toLowerCase();
            for (String ingredientName : nameArray) {
                if (name.contains(ingredientName.toLowerCase())) {
                    addGrocery(ingredientName);
                    break;
                }
            }
        }
    }
}