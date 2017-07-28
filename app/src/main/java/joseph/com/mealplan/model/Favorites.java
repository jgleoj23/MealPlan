package joseph.com.mealplan.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * @author Joseph Gardi
 */
@RealmClass
public class Favorites extends RealmObject {

    private RealmList<Recipe> favorites = new RealmList<>();

    public RealmList<Recipe> getFavorites() {
        return favorites;
    }
}
