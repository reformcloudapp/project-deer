package app.reformcloud.projects.deer.executor.utils;

import app.reformcloud.projects.deer.api.Database;
import app.reformcloud.projects.deer.api.writer.FileWriter;
import app.reformcloud.projects.deer.executor.BasicDatabase;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.function.Function;

public final class DatabaseUtil {

    private DatabaseUtil() {
        throw new UnsupportedOperationException();
    }

    public static <T extends FileWriter> Database<T> loadOrCreateDatabase(@NotNull File file, @NotNull Function<File, T> applier, int valueLength) {
        if (!file.exists()) {
            try {
                Files.createDirectories(file.toPath());
            } catch (final IOException ex) {
                throw new RuntimeException(ex);
            }

            return new BasicDatabase<>(file, applier, valueLength);
        }

        if (!file.isDirectory()) {
            throw new RuntimeException("Can only load databases which are folders!");
        }

        return new BasicDatabase<>(file, applier, new Properties());
    }

    public static <T extends FileWriter> Database<T> loadDatabase(@NotNull File file, @NotNull Function<File, T> applier) {
        if (!file.isDirectory()) {
            throw new RuntimeException("Can only load databases which are folders!");
        }

        return new BasicDatabase<>(file, applier, new Properties());
    }
}
