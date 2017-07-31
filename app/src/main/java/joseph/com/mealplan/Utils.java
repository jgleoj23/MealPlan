package joseph.com.mealplan;

import android.support.annotation.NonNull;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

/**
 * @author Joseph Gardi
 */
public class Utils {

    public static final List<String> DAYS_OF_WEEK = Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday",
                                                                   "Thursday", "Friday", "Saturday");

    public static <T> ImmutableList flatten(Iterable<T> items, final Function<T, Collection> getElements) {
        return  FluentIterable.from(items).transformAndConcat(new Function<T, Iterable<?>>() {
            @Nullable
            @Override
            public Iterable<?> apply(@Nullable T item) {
                List list = new ArrayList<>(Arrays.asList(item));
                list.addAll(getElements.apply(item));
                return list;
            }
        }).toList();
    }

    @NonNull
    public static String capitalize(String str) {
        Matcher letterMatcher = Pattern.compile("[A-z]").matcher(str);
        if (letterMatcher.find()) {
            int index = letterMatcher.start();
            return str.substring(0, index) + str.substring(index, index + 1).toUpperCase() + str.substring(index + 1);
        } else {
            return str;
        }
    }
}
