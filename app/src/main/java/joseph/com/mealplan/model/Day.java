package joseph.com.mealplan.model;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by kcguo on 7/13/17.
 *
 * Represents a Day in the MealPlanFragment
 */
@RealmClass
public class Day extends RealmObject {

    @PrimaryKey
    private String name;

    public Day() {}

    private RealmList<Recipe> meals = new RealmList<>();

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
