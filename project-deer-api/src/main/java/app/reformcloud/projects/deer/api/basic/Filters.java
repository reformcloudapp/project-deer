package app.reformcloud.projects.deer.api.basic;

import app.reformcloud.projects.deer.api.Database;
import app.reformcloud.projects.deer.api.filter.Filter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Predicate;

public final class Filters {

    private Filters() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static Filter keyEq(@NotNull String keyToFind) {
        return new Filter() {
            @Override
            public boolean filter(@NotNull String keyFile, @NotNull Database<?> target) {
                String primaryKey = keyFile.split("-")[0];
                return primaryKey.equals(keyToFind);
            }
        };
    }

    @NotNull
    public static Filter anyMatch(@NotNull String toFind) {
        return new Filter() {
            @Override
            public boolean filter(@NotNull String keyFile, @NotNull Database<?> target) {
                String[] all = keyFile.split("-");
                return Arrays.asList(all).contains(toFind);
            }
        };
    }

    @NotNull
    public static Filter anyValueMatch(@NotNull String valueToFind) {
        return new Filter() {
            @Override
            public boolean filter(@NotNull String keyFile, @NotNull Database<?> target) {
                String[] all = keyFile.split("-");
                if (all.length == 1) {
                    return false;
                }

                all = Arrays.copyOfRange(all, 1, all.length);
                return Arrays.asList(all).contains(valueToFind);
            }
        };
    }

    @NotNull
    public static Filter findValue(@NotNull String valueToFind, int valueIndex) {
        return new Filter() {
            @Override
            public boolean filter(@NotNull String keyFile, @NotNull Database<?> target) {
                String[] all = keyFile.split("-");
                if (all.length <= valueIndex) {
                    return false;
                }

                return all[valueIndex].equals(valueToFind);
            }
        };
    }

    @NotNull
    public static Filter keyLt(long longToFind) {
        return new Filter() {
            @Override
            public boolean filter(@NotNull String keyFile, @NotNull Database<?> database) {
                return toLong(keyFile.split("-")[0], e -> e < longToFind);
            }
        };
    }

    @NotNull
    public static Filter keyHt(long longToFind) {
        return new Filter() {
            @Override
            public boolean filter(@NotNull String keyFile, @NotNull Database<?> database) {
                return toLong(keyFile.split("-")[0], e -> e > longToFind);
            }
        };
    }

    private static boolean toLong(String parse, Predicate<Long> predicate) {
        try {
            long l = Long.parseLong(parse);
            return predicate.test(l);
        } catch (final NumberFormatException ex) {
            return false;
        }
    }
}
