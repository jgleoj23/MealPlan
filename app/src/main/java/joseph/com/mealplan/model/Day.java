package joseph.com.mealplan.model;

import java.util.ArrayList;

/**
 * Created by kcguo on 7/13/17.
 */

public class Day {
    private String name;
    private ArrayList<Recipe> meals;

    public Day(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addMeals(Recipe meal) {
        meals.add(meal);
    }

}
