package com.marcusposey;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

/** An IssueService submits todos as issues to some remote service. */
public interface IssueService {
    /**
     * @throws IOException when a network error is encountered
     */
    void push(@NotNull List<Todo> todos) throws IOException;
}
