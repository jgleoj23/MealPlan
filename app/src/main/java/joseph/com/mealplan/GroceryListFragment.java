package joseph.com.mealplan;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import joseph.com.mealplan.model.Grocery;

public class GroceryListFragment extends Fragment {
    List<HashMap<String, String>> listItems;
    // Use Map as the type
    HashMap<String, String> resultsMap = new HashMap<>();
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

    String[] nameArray = {"ham", "cheese", "pineapple", "milk", "bread", "kiwi", "butter", "rice", "pasta", "tomato", "steak", "french fries", "avocado", "cookies", "cake", "water", "onions", "carrots", "garlic", "spinach", "ramen", "chicken", "cheesecake", "sugar"};
    int[] number = {5, 1, 3, 1, 2, 3, 1, 4, 4, 3, 5, 1, 3, 2, 2, 1, 3, 3, 3, 3, 4, 5, 2, 2};
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_grocery_list, container, false);
        ButterKnife.bind(this, view);

        //Creates a hash table of valid grocery items
        for(int i = 0; i != 24; i++){
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
        // Use TAG. This is not MainActivity
        Log.i("MainActivity", "Setting Up List View");
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
                String itemText = txAdd.getText().toString();
                String capitalized = itemText.substring(0, 1).toUpperCase() + itemText.substring(1).toLowerCase();
                if (valid.containsKey(capitalized)) {
                    saveItem(capitalized);
                    showItem(capitalized);
                } else {
                    Toast.makeText(getContext(), "Not a valid grocery item.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveItem(final String itemText) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insert(new Grocery(itemText));
            }
        });
    }


    public void showItem(String itemText) {
        // Never use try catch for normal control flow. That is like using GOTO
        for (Map<String, String> resultsMap : listItems) {
            String aisle = resultsMap.get("First Line");
            if(aisle.equals("Aisle #" + valid.get(itemText))) {
                String old = resultsMap.get("Second Line");
                if(!old.contains(itemText)){
                    resultsMap.put("Second Line", old + "-" + itemText + "\n");
                    adapter.notifyDataSetChanged();
                    txAdd.setText("");
                }  else {
                    Toast.makeText(getContext(), "Grocery already in list.", Toast.LENGTH_LONG).show();
                }

                // Aisle has been found. Were done!
                return;
            }
        }
        // create it
        resultsMap = new HashMap<String, String>();

        resultsMap.put("First Line", "Aisle #" + valid.get(itemText));
        resultsMap.put("Second Line", "-" + itemText+ "\n");
        boolean changed = false;
        if(listItems.size() == 0){
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


    public void onImportGrocery(String ingredient) {
        HashMap<String, String> resultsMap = new HashMap<>();
        String capitalized =ingredient.substring(0, 1).toUpperCase() + ingredient.substring(1);
        try{ //Case #1: The aisle # for the provided input has already been created, so update the values under
                for(int i = 0; i != listItems.size() + 1; i++){
                    resultsMap = listItems.get(i);
                    String aisle = resultsMap.get("First Line");
                    if(aisle.equals("Aisle #" + valid.get(ingredient))){
                        String old = resultsMap.get("Second Line");
                        if(!old.contains(capitalized)){
                            resultsMap.put("Second Line", old + "-" + capitalized + "\n");
                            adapter.notifyDataSetChanged();
                            txAdd.setText("");
                            break;
                        }
                        else{
                            break;
                        }
                    }
                }
        }
        catch(IndexOutOfBoundsException e){ //Case #2: The aisle # for the input provided doesn't exist yet, so create it
            resultsMap = new HashMap<>();
            resultsMap.put("First Line", "Aisle #" + valid.get(ingredient));
            resultsMap.put("Second Line", "-" + capitalized + "\n");
            boolean changed = false;
            if(listItems.size() == 0){
                listItems.add(resultsMap);
            }
            else {
                for (int i = 0; i != listItems.size(); i++) {
                    String number = listItems.get(i).get("First Line");
                    int x = Integer.valueOf(number.substring(number.indexOf("#") + 1));
                    if (x > valid.get(ingredient)) {
                        listItems.add(i, resultsMap);
                        changed = true;
                        break;
                    }
                }
                if (!changed){
                    listItems.add(resultsMap);
                }
            }
            adapter.notifyDataSetChanged();
            txAdd.setText("");
                txAdd.setText("");
            }
    }

    public void addGroceries(List<Grocery> ingredients) {
        for(int i = 0; i != ingredients.size(); i++){
            String ingredient = ingredients.get(i).getName().toLowerCase();
            for(int j = 0; j != 24; j++){
                if(ingredient.contains(nameArray[j])){
                    onImportGrocery(nameArray[j]);
                    break;
                }
            }
        }
    }
}