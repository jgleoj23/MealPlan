package joseph.com.mealplan.model;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import joseph.com.mealplan.R;

public class GroceryList extends AppCompatActivity {
    ListView resultsListView;
    List<HashMap<String, String>> listItems;
    HashMap<String, String> resultsMap = new HashMap<>();
    SimpleAdapter adapter;
    Hashtable<String, Integer> valid = new Hashtable<String, Integer>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);
        getSupportActionBar().setTitle("Grocery List");

        resultsListView = (ListView) findViewById(R.id.lvGrocery);

        //Creates a hash table of valid grocery items
        String[] nameArray = {"ham", "cheese", "pineapple", "milk", "bread", "kiwi", "butter", "rice", "pasta", "tomato", "steak", "french fries", "avocado", "cookies", "cake", "water", "onions", "carrots", "garlic", "spinach"};
        int[] number = {5, 1, 3, 1, 2, 3, 1, 4, 4, 3, 5, 1, 3, 6, 6, 1, 3, 3, 3, 3};
        for(int i = 0; i != 20; i++){
            valid.put(nameArray[i], number[i]);
        }

        //Sets up adapter to allow for Aisle #: grocery layout
        listItems = new ArrayList<>();
        adapter = new SimpleAdapter(this, listItems, R.layout.grocery_item,
                new String[]{"First Line", "Second Line"},
                new int[]{R.id.txAisle, R.id.txGroc});
        resultsListView.setAdapter(adapter);
        setupListViewListener();
    }



    private void setupListViewListener(){
        Log.i("MainActivity", "Setting Up List View");
        resultsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            //If list entry is long clicked, delete entry
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("MainActivity", "Item Removed:" + i);
                resultsMap = listItems.get(i);
                listItems.remove(i);
                adapter.notifyDataSetChanged();
                View.OnClickListener undoDelete = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //Re-adds the deleted entry
                        listItems.add(0, resultsMap);
                        adapter.notifyDataSetChanged();
                    }
                };
                //Displays snackbar, which allows for undoing the delete
                Snackbar.make(resultsListView, "Removed Aisle #" + i, Snackbar.LENGTH_LONG)
                        .setAction("Undo", undoDelete)
                        .show();
                return true;
            }
        });
    }


    public void onAddItem(View v) {
        EditText txAdd = (EditText) findViewById(R.id.txAdd);
        String ItemText = txAdd.getText().toString().toLowerCase(); //Lowercase allows for less stringent grocery inputs
        HashMap<String, String> resultsMap = new HashMap<>();


        if (valid.containsKey(ItemText)) { //Checks if the inputted text is a valid grocery item

            try{ //Case #1: The aisle # for the provided input has already been created, so update the values under
                for(int i = 0; i != listItems.size() + 1; i++){
                    resultsMap = listItems.get(i);
                    String aisle = resultsMap.get("First Line");
                    if(aisle.equals("Aisle #" + valid.get(ItemText))){
                        String old = resultsMap.get("Second Line");
                        resultsMap.put("Second Line", old + "-" + ItemText + "\n");
                        adapter.notifyDataSetChanged();
                        txAdd.setText("");
                        break;
                    }
                }
            }

            catch(IndexOutOfBoundsException e){ //Case #2: The aisle # for the input provided doesn't exist yet, so create it
                resultsMap = new HashMap<>();
                resultsMap.put("First Line", "Aisle #" + valid.get(ItemText));
                resultsMap.put("Second Line", "-" + ItemText + "\n");
                listItems.add(resultsMap);
                adapter.notifyDataSetChanged();
                txAdd.setText("");
            }
        }

        else{
            Toast.makeText(getApplicationContext(), "Not a valid grocery item.", Toast.LENGTH_LONG).show();
        }
    }
}