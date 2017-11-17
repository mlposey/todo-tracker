package com.marcusposey;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Todo describes a task that has yet to be done. */
public class Todo {
    @Nullable
    public final String creator;

    @NotNull
    public final String title;

    @Nullable
    public final String body;

    public Todo(@Nullable String creator, String title, @Nullable String body) {
        this.creator = creator;
        this.title = title;
        this.body = body;
    }
}
