package app.reformcloud.projects.deer.api;

import app.reformcloud.projects.deer.api.filter.Filter;
import app.reformcloud.projects.deer.api.writer.FileWriter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Optional;
import java.util.function.Function;

public interface Database<T extends FileWriter> {

    @NotNull
    File getTargetFolder();

    @NotNull
    Function<File, T> getApplier();

    int expectedValues();

    @NotNull
    Optional<T> getEntry(@NotNull Filter filter);

    void insert(@NotNull String key, @NotNull String[] values, @NotNull T value);

    void updateKey(@NotNull Filter target, @NotNull T value);

    void delete(@NotNull Filter filter);
}
