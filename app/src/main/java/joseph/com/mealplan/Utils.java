package joseph.com.mealplan;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;

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

import joseph.com.mealplan.model.Day;
import joseph.com.mealplan.model.MealRow;

/**
 * @author Joseph Gardi
 */
public class Utils {

    public static final List<String> DAYS_OF_WEEK = Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday",
                                                                   "Thursday", "Friday", "Saturday");

    public static <T> ImmutableList<Object> flatten(Iterable<T> items, final Function<T, Collection> getElements) {
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

    public static AlertDialog.Builder createAlert(String title, String msg, Context context) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    public static Day getDayForIndex(List<MealRow> list, int index) {
        for (int i = index - 1; i >= 0; i--) {
            Object item = list.get(i).getData();
            if (item instanceof Day) {
                return ((Day) item);
            }
        }

        throw new RuntimeException("no day found");
    }

    public static Pair<Integer, Day> findDay(List<MealRow> list, String dayName) {
        for (Integer i = 0; i < list.size(); i++) {
            Object item = list.get(i).getData();
            if (item instanceof Day) {
                Day day = ((Day) item);
                if (day.getName().equals(dayName)) {
                    return new Pair<>(i, day);
                }
            }
        }

        throw new RuntimeException("no day found");
    }
}
