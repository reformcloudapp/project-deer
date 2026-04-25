package app.reformcloud.projects.deer.executor;

import app.reformcloud.projects.deer.api.Database;
import app.reformcloud.projects.deer.api.filter.Filter;
import app.reformcloud.projects.deer.api.writer.FileWriter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

public class BasicDatabase<T extends FileWriter> implements Database<T> {

    public BasicDatabase(File folder, Function<File, T> applier, int values) {
        this.file = folder;
        this.applier = applier;
        this.expectedValues = values;

        if (values < 1) {
            throw new RuntimeException("We cannot handle a database which less than 1 values per key. Database config broken?");
        }

        Properties properties = new Properties();
        properties.setProperty("values", Integer.toString(expectedValues));
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(folder.getAbsolutePath(), "config.properties"), StandardOpenOption.CREATE_NEW)) {
            properties.store(outputStream, "Database configuration 0x001");
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    public BasicDatabase(File folder, Function<File, T> applier, Properties properties) {
        this.file = folder;
        this.applier = applier;

        try (InputStream stream = Files.newInputStream(Paths.get(folder.getAbsolutePath(), "config.properties"))) {
            properties.load(stream);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        String values = properties.getProperty("values", "-1");
        try {
            this.expectedValues = Integer.parseInt(values);
        } catch (final NumberFormatException ex) {
            throw new RuntimeException("Expected a number but got " + values + ". Database seems broken");
        }

        if (this.expectedValues < 1) {
            throw new RuntimeException("We cannot handle a database which less than 1 values per key. Database config broken?");
        }
    }

    private final File file;

    private final Function<File, T> applier;

    private final int expectedValues;

    @NotNull
    @Override
    public File getTargetFolder() {
        return this.file;
    }

    @NotNull
    @Override
    public Function<File, T> getApplier() {
        return this.applier;
    }

    @Override
    public int expectedValues() {
        return this.expectedValues;
    }

    @NotNull
    @Override
    public Optional<T> getEntry(@NotNull Filter filter) {
        File file = getFile(filter);
        return file == null ? Optional.empty() : Optional.ofNullable(getApplier().apply(file));
    }

    @Override
    public void insert(@NotNull String key, @NotNull String[] values, @NotNull T value) {
        String databaseFileName = key + "-" + String.join("-", values);
        File databaseFile = new File(getTargetFolder().getPath(), databaseFileName);
        if (databaseFile.exists()) {
            return;
        }

        if (values.length != this.expectedValues) {
            throw new RuntimeException("Cannot insert database object (expected != given)");
        }

        try {
            if (!databaseFile.createNewFile()) {
                throw new RuntimeException("Cannot create new database file");
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        try (java.io.FileWriter fileWriter = new java.io.FileWriter(databaseFile, false)) {
            fileWriter.write(value.toWriteableString());
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void updateKey(@NotNull Filter target, @NotNull T value) {
        File file = getFile(target);
        if (file == null) {
            return;
        }

        try (java.io.FileWriter fileWriter = new java.io.FileWriter(file, false)) {
            fileWriter.write(value.toWriteableString());
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void delete(@NotNull Filter filter) {
        File file = getFile(filter);
        if (file == null || file.isDirectory()) {
            return;
        }

        try {
            Files.deleteIfExists(file.toPath());
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    private File getFile(Filter filter) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(getTargetFolder().toPath())) {
            for (Path next : stream) {
                String fileName = next.getFileName().toString();
                if (filter.filter(fileName, this)) {
                    return next.toFile();
                }
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
