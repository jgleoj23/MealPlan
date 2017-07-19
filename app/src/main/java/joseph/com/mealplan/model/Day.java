package joseph.com.mealplan.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kcguo on 7/13/17.
 */

public class Day {
    private String name;
    private List<Recipe> meals = new ArrayList<>();

    public Day(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Recipe> getMeals() {
        return meals;
    }
}
