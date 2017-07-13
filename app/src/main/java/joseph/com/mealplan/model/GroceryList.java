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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import joseph.com.mealplan.R;

public class GroceryList extends AppCompatActivity {
    ListView resultsListView;
    List<HashMap<String, String>> listItems;
    HashMap<String, String> resultsMap = new HashMap<>();
    SimpleAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);
        getSupportActionBar().setTitle("Grocery List");

        resultsListView = (ListView) findViewById(R.id.lvGrocery);

        HashMap<String, String> nameAddresses = new HashMap<>();


        String[] array = {"-Ham\n-Chicken", "-Milk \n-Cheese \n-Butter", "-Bread", "-Potatoes \n-Lettuce", "-Spaghetti \n-Rice", "-Banana\n-Kiwi \n-Pineapple", "-Sugar"};
        Set<String> dups    = new HashSet<>();
        int i = 1;

        while(nameAddresses.size() != 5){
            String values = array[new Random().nextInt(array.length)];
            if(dups.add(values)) {
                nameAddresses.put("Aisle #"+i, values);
                i+=1;
            }
        }

        listItems = new ArrayList<>();
        adapter = new SimpleAdapter(this, listItems, R.layout.grocery_item,
                new String[]{"First Line", "Second Line"},
                new int[]{R.id.txAisle, R.id.txGroc});


        Iterator it = nameAddresses.entrySet().iterator();
        while (it.hasNext())
        {
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry)it.next();
            resultsMap.put("First Line", pair.getKey().toString());
            resultsMap.put("Second Line", pair.getValue().toString()+"\n");
            listItems.add(resultsMap);
        }
        resultsListView.setAdapter(adapter);

        setupListViewListener();
    }



    private void setupListViewListener(){
        Log.i("MainActivity", "Setting Up List View");
        resultsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("MainActivity", "Item Removed:" + i);
                resultsMap = listItems.get(i);
                listItems.remove(i);
                adapter.notifyDataSetChanged();

                View.OnClickListener undoDelete = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listItems.add(0, resultsMap);
                        adapter.notifyDataSetChanged();
                    }
                };

                Snackbar.make(resultsListView, "Successfully removed", Snackbar.LENGTH_LONG)
                        .setAction("Undo", undoDelete)
                        .show();
                return true;
            }
        });
    }


    public void onAddItem(View v){
        EditText txAdd = (EditText) findViewById(R.id.txAdd);
        String ItemText =  txAdd.getText().toString();
        HashMap<String, String> resultsMap = new HashMap<>();
        Random r = new Random();
        if(listItems.size() > 1) {
            resultsMap = listItems.get(r.nextInt(listItems.size() - 1));
            String dummy = resultsMap.get("Second Line");
            resultsMap.put("Second Line", dummy + "-"+ItemText+"\n");
            //listItems.add(resultsMap);
            txAdd.setText("");
            //Toast.makeText(getApplicationContext(),"Item Added", Toast.LENGTH_LONG).show();
        }
        else if(listItems.size() == 1){
            resultsMap = listItems.get(0);
            String dummy = resultsMap.get("Second Line");
            resultsMap.put("Second Line", dummy + "-"+ItemText+"\n");
            //listItems.add(resultsMap);
            txAdd.setText("");
            //Toast.makeText(getApplicationContext(),"Item Added", Toast.LENGTH_LONG).show();
        }
        else if(listItems.size() == 0){
            resultsMap.put("First Line", "Aisle #2");
            resultsMap.put("Second Line", "-"+ItemText+ "\n");
            listItems.add(resultsMap);
            adapter.notifyDataSetChanged();
            txAdd.setText("");
        }
    }
}