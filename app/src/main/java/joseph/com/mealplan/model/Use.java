package joseph.com.mealplan.model;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * @author Joseph Gardi
 *
 * A RealmObject wrapping a String that describes the Use of some grocery in a recipe
 */
@RealmClass
public class Use extends RealmObject {

    /**
     * e.g. 1 cup of butter for Chicken noodle soup
     */
    private String use;

    public Use() {}

    public Use(String use) {
        this.use = use;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }
}
