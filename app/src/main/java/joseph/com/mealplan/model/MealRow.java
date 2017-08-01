package joseph.com.mealplan.model;

/**
 * This just wraps {@link #data} which is a Day or Recipe and adds the field {@link #id} which
 * is used to keep track of rows as they are moved around, duplicated, and deleted
 *
 * @author Joseph Gardi
 */
public class MealRow {
    /**
     * An instance of Day or Recipe
     */
    private final Object data;

    /**
     * keep track of the nextId to make {@link #id} autoincrement
     */
    private static long nextId = 0;

    /**
     * An id to identify it so that @link {@link joseph.com.mealplan.MealPlanFragment#lvMealPlan}
     * can keep track of which row is which as we move, delete, add and duplicate meals
     */
    private final long id = nextId;

    public MealRow(Object data) {
        this.data = data;
        nextId++;
    }

    public Object getData() {
        return data;
    }

    public long getId() {
        return id;
    }
}
