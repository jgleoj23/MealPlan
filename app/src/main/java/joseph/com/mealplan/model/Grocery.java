package joseph.com.mealplan.model;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * @author Joseph Gardi
 *
 * Represents a Grocery listed in the GroceryListFragment
 */
@RealmClass
public class Grocery extends RealmObject {
    private String name;

    private RealmList<Use> uses = new RealmList<>();

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

    public List<Use> getUses() {
        return uses;
    }
}
