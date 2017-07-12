package joseph.com.mealplan.model;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import joseph.com.mealplan.R;

public class GroceryList extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);
        getSupportActionBar().setTitle("Grocery List");

        ListView resultsListView = (ListView) findViewById(R.id.lvGrocery);

        HashMap<String, String> nameAddresses = new HashMap<>();


        String[] array = {"Ham, chicken", "Milk, cheese, butter", "Bread", "Potatoes, lettuce", "Spaghetti, rice", "Banana, kiwi, pineapple", "Sugar"};
        nameAddresses.put("Aisle #1", array[new Random().nextInt(array.length)]);
        nameAddresses.put("Aisle #2", array[new Random().nextInt(array.length)]);
        nameAddresses.put("Aisle #4", array[new Random().nextInt(array.length)]);
        nameAddresses.put("Aisle #5", array[new Random().nextInt(array.length)]);
        nameAddresses.put("Aisle #6", array[new Random().nextInt(array.length)]);

        List<HashMap<String, String>> listItems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.grocery_item,
                new String[]{"First Line", "Second Line"},
                new int[]{R.id.txAisle, R.id.txGroc});


        Iterator it = nameAddresses.entrySet().iterator();
        while (it.hasNext())
        {
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry)it.next();
            resultsMap.put("First Line", pair.getKey().toString());
            resultsMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultsMap);
        }

        resultsListView.setAdapter(adapter);
    }
}
