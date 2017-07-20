package joseph.com.mealplan.model;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * @author Joseph Gardi
 */
@RealmClass
public class Grocery extends RealmObject {
    private String name;

    public Grocery() {}

    public Grocery(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
