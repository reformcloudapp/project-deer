package app.reformcloud.projects.deer.api.writer;

import org.jetbrains.annotations.NotNull;

public interface FileWriter {

    @NotNull
    String toWriteableString();
}
