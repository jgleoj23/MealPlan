package joseph.com.mealplan.model;

/**
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
