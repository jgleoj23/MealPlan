package joseph.com.mealplan;

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

    public static ImmutableList flatten(Iterable items, final Function<Object, Collection> getElements) {
        return  FluentIterable.from(items).transformAndConcat(new Function<Object, Iterable<?>>() {
            @Nullable
            @Override
            public Iterable<?> apply(@Nullable Object item) {
                ArrayList list = new ArrayList(Arrays.asList(item));
                list.addAll(getElements.apply(item));
                return list;
            }
        }).toList();
    }
}
