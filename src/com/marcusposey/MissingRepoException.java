package com.marcusposey;

/** Indicates that a remote Git repository cannot be found */
public class MissingRepoException extends Exception {
    @Override
    public String getMessage() {
        return "The project cannot be matched to a repository.";
    }
}
