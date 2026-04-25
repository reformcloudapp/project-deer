package app.reformcloud.projects.deer.api.provider;

import app.reformcloud.projects.deer.api.DatabaseDriver;
import org.jetbrains.annotations.NotNull;

public final class DatabaseProvider {

    private DatabaseProvider() {
        throw new UnsupportedOperationException();
    }

    private static DatabaseDriver databaseDriver;

    public static void setDatabaseDriver(DatabaseDriver databaseDriver) {
        if (databaseDriver == null) {
            throw new UnsupportedOperationException("Cannot redefine database driver");
        }

        DatabaseProvider.databaseDriver = databaseDriver;
    }

    @NotNull
    public static DatabaseDriver getDatabaseDriver() {
        return databaseDriver;
    }
}
