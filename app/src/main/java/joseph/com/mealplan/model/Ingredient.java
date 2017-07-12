package joseph.com.mealplan.model;

import javax.annotation.Nonnull;

/**
 * @author Joseph Gardi
 */
public class Ingredient {
    private String name;

    public @Nonnull String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
