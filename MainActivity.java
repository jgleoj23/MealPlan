package joseph.com.mealplan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import joseph.com.mealplan.model.GroceryList;

public class MainActivity extends AppCompatActivity {

//    ArrayList<String> items;
//    ArrayAdapter<String> itemAdapter;
//    ListView lvItems;
//    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = new Intent(this, GroceryList.class);
        startActivity(i);
    }
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        items = new ArrayList<>();
//        ReadItems();
//        itemAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
//        lvItems = (ListView) findViewById(R.id.lvItems);
//        lvItems.setAdapter(itemAdapter);
//        setupListViewListener();
//    }
//
//    public void onAddItem(View v){
//        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
//        String ItemText =  etNewItem.getText().toString();
//        itemAdapter.add(ItemText);
//        etNewItem.setText("");
//        //Toast.makeText(getApplicationContext(),"Item Added", Toast.LENGTH_LONG).show();
//        WriteItems();
//    }
//
//
//    private void setupListViewListener(){
//        Log.i("MainActivity", "Setting Up List View");
//        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.i("MainActivity", "Item Removed:" + i);
//                items.remove(i);
//                itemAdapter.notifyDataSetChanged();
//                WriteItems();
//                return true;
//            }
//        });
//    }
//
//    private File getDataFile(){
//        return new File(getFilesDir(), "todo.txt");
//    }
//
//    private void ReadItems(){
//        try{
//            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
//        }
//        catch(IOException e){
//            Log.e("MainActivity", "Error Reading file", e);
//            items = new ArrayList<>();
//        }
//    }
//
//
//    private void WriteItems(){
//        try{
//            FileUtils.readLines(getDataFile(), Charset.defaultCharset());
//        }
//        catch(IOException e){
//            Log.e("MainActivity", "Error Reading file", e);
//        }
//    }
}