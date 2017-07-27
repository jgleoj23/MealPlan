package joseph.com.mealplan;

import android.support.annotation.NonNull;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nullable;

/**
 * @author Joseph Gardi
 */
public class Utils {

    public static <T> ImmutableList flatten(Iterable<T> items, final Function<T, Collection> getElements) {
        return  FluentIterable.from(items).transformAndConcat(new Function<T, Iterable<?>>() {
            @Nullable
            @Override
            public Iterable<?> apply(@Nullable T item) {
                ArrayList list = new ArrayList(Arrays.asList(item));
                list.addAll(getElements.apply(item));
                return list;
            }
        }).toList();
    }

    @NonNull
    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase(); //This is fine
    }
}
