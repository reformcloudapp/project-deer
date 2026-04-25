package app.reformcloud.projects.deer.api.filter;

import app.reformcloud.projects.deer.api.Database;
import org.jetbrains.annotations.NotNull;

public interface Filter {

    boolean filter(@NotNull String keyFile, @NotNull Database<?> target);
}
