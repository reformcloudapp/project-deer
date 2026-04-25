package app.reformcloud.projects.deer.executor;

import app.reformcloud.projects.deer.api.Database;
import app.reformcloud.projects.deer.api.DatabaseDriver;
import app.reformcloud.projects.deer.api.provider.DatabaseProvider;
import app.reformcloud.projects.deer.api.writer.FileWriter;
import app.reformcloud.projects.deer.executor.utils.DatabaseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Function;

public class BasicDatabaseDriver implements DatabaseDriver {

    static {
        DatabaseProvider.setDatabaseDriver(new BasicDatabaseDriver());
    }

    private BasicDatabaseDriver() {
    }

    @NotNull
    @Override
    public <T extends FileWriter> Database<T> getDatabase(@NotNull File folder, @NotNull Function<File, T> applier, int valueLength) {
        return DatabaseUtil.loadOrCreateDatabase(folder, applier, valueLength);
    }

    @Nullable
    @Override
    public <T extends FileWriter> Database<T> getDatabaseIfExists(@NotNull File folder, @NotNull Function<File, T> applier) {
        return DatabaseUtil.loadDatabase(folder, applier);
    }

    @Override
    public void clearDatabase(@NotNull File database) {
        if (!database.exists() || !database.isDirectory()) {
            return;
        }

        this.clear(database, true);
    }

    @Override
    public void deleteDatabase(@NotNull File database) {
        if (!database.exists() || !database.isDirectory()) {
            return;
        }

        this.clear(database, false);
        this.clearDatabase(database);

        try {
            Files.delete(database.toPath());
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    private void clear(File file, boolean ignoreConfig) {
        try {
            Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public @NotNull FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs) {
                    if (file.toFile().getName().equals("config.properties") && ignoreConfig) {
                        return FileVisitResult.CONTINUE;
                    }

                    try {
                        Files.deleteIfExists(file);
                    } catch (final IOException ex) {
                        ex.printStackTrace();
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}
