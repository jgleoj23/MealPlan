package joseph.com.mealplan.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joseph Gardi
 */
public class Aisle implements Comparable<Aisle> {

    private int aisleNumber;

    private List<Grocery> groceries = new ArrayList<>();


    public int getAisleNumber() {
        return aisleNumber;
    }

    public void setAisleNumber(int aisleNumber) {
        this.aisleNumber = aisleNumber;
    }

    public List<Grocery> getGroceries() {
        return groceries;
    }

    public String getAisleName() {
        return "Aisle #" + getAisleNumber();
    }

    @Override
    public int compareTo(@NonNull Aisle other) {
        return aisleNumber - other.getAisleNumber();
    }
}
