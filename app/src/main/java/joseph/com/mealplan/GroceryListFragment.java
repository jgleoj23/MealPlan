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

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroceryListFragment extends Fragment {
    List<HashMap<String, String>> listItems;
    HashMap<String, String> resultsMap = new HashMap<>();
    SimpleAdapter adapter;
    Hashtable<String, Integer> valid = new Hashtable<String, Integer>();
    MainActivity mainActivity;

    @BindView(R.id.lvGrocery)
    ListView resultsListView;
    @BindView(R.id.txAdd)
    EditText txAdd;
    @BindView(R.id.btAdd)
    Button btAdd;

    public static GroceryListFragment newInstance(MainActivity mainActivity) {
        GroceryListFragment fragment = new GroceryListFragment();
        fragment.mainActivity = mainActivity;
        return fragment;
    }

    String[] nameArray = {"ham", "cheese", "pineapple", "milk", "bread", "kiwi", "butter", "rice", "pasta", "tomato", "steak", "french fries", "avocado", "cookies", "cake", "water", "onions", "carrots", "garlic", "spinach", "ramen", "chicken", "cheesecake", "sugar"};
    int[] number = {5, 1, 3, 1, 2, 3, 1, 4, 4, 3, 5, 1, 3, 2, 2, 1, 3, 3, 3, 3, 4, 5, 2, 2};
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grocery_list, container, false);
        ButterKnife.bind(this, view);
        super.onCreate(savedInstanceState);

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

//        for(int i = 0; i != 5; i++){
//            resultsMap = new HashMap<>();
//            resultsMap.put("First Line", "");
//            resultsMap.put("Second Line", "");
//            listItems.add(resultsMap);
//        }
        setupListViewListener();
        return view;
    }



    private void setupListViewListener(){
        Log.i("MainActivity", "Setting Up List View");
        resultsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            //If list entry is long clicked, delete entry
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("MainActivity", "Item Removed:" + i);
                resultsMap = listItems.get(i);
                final String old = resultsMap.get("Second Line");
                resultsMap.put("Second Line", "");
                adapter.notifyDataSetChanged();
                View.OnClickListener undoDelete = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //Re-adds the deleted entry
                        resultsMap.put("Second Line", old);
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
                onAddItem(v);
            }
        });
    }


    public void onAddItem(View v) {
        String ItemText = txAdd.getText().toString().toLowerCase(); //Lowercase allows for less stringent grocery inputs
        HashMap<String, String> resultsMap = new HashMap<>();

        if (valid.containsKey(ItemText)) { //Checks if the inputted text is a valid grocery item
            String capitalized =ItemText.substring(0, 1).toUpperCase() + ItemText.substring(1);
            try{ //Case #1: The aisle # for the provided input has already been created, so update the values under
                for(int i = 0; i != listItems.size() + 1; i++){
                    resultsMap = listItems.get(i);
                    String aisle = resultsMap.get("First Line");
                    if(aisle.equals("Aisle #" + valid.get(ItemText))){
                        String old = resultsMap.get("Second Line");
                        if(!old.contains(capitalized)){
                            resultsMap.put("Second Line", old + "-" + capitalized + "\n");
                            adapter.notifyDataSetChanged();
                            txAdd.setText("");
                            break;
                        }
                        else{
                            Toast.makeText(getContext(), "Grocery already in list.", Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                }
            }

            catch(IndexOutOfBoundsException e){ //Case #2: The aisle # for the input provided doesn't exist yet, so create it
                resultsMap = new HashMap<>();
                resultsMap.put("First Line", "Aisle #" + valid.get(ItemText));
                resultsMap.put("Second Line", "-" + capitalized + "\n");
                boolean changed = false;
                if(listItems.size() == 0){
                    listItems.add(resultsMap);
                }
                else {
                    for (int i = 0; i != listItems.size(); i++) {
                        String number = listItems.get(i).get("First Line");
                        int x = Integer.valueOf(number.substring(number.indexOf("#") + 1));
                        if (x > valid.get(ItemText)) {
                            listItems.add(i, resultsMap);
                            changed = true;
                            break;
                        }
                    }
                    if(changed == false){
                        listItems.add(resultsMap);
                    }
                }
                adapter.notifyDataSetChanged();
                txAdd.setText("");
            }
        }

        else{
            Toast.makeText(getContext(), "Not a valid grocery item.", Toast.LENGTH_LONG).show();
        }
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
                if(changed == false){
                    listItems.add(resultsMap);
                }
            }
            adapter.notifyDataSetChanged();
            txAdd.setText("");
                txAdd.setText("");
            }
    }

    public void addGroceries(ArrayList<String> ingredients) {
        for(int i = 0; i != ingredients.size(); i++){
            String ingredient = ingredients.get(i).toLowerCase();
            for(int j = 0; j != 24; j++){
                if(ingredient.contains(nameArray[j])){
                    onImportGrocery(nameArray[j]);
                    break;
                }
            }
        }
    }
}