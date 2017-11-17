package com.marcusposey;

import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Scanner searches project files for todo items */
public class Scanner {
    private final Project project;
    // The scanner only touches files with these extensions.
    private final Set<String> exts = new HashSet<>();

    private List<Todo> todos;

    // Matches a todo comment on three capture groups:
    // 1. The creator; e.g., // TODO(marcus)
    // 2. The title;   e.g., // TODO: Buy milk.
    // 3. The body:    e.g., // Todo: Buy milk.
    //       this  -->       //       Milk is the key to baking.
    private final Pattern todoExpr =
            Pattern.compile("\\/\\/ TODO(.*)?: (.*)\\s(\\s\\/\\/.*\\s)*",
                            Pattern.MULTILINE);

    /** Creates a new Scanner that scans Go files */
    public Scanner(@NotNull Project project) {
        this.project = project;
        exts.add("go");
    }

    /**
     * Scans all project files for todo items
     *
     * A todo item assumes the following forms:
     *
     *      // TODO: Do the thing.
     *
     *      // TODO(name): Do the thing.
     *
     *      // TODO: Do the thing..
     *      //       You can do it! :D
     *
     *      // TODO(name): Do the thing.
     *      //             You can do it! :D
     *
     * Because of the format, the end of a todo block should be followed
     * by a newline if the next line would be a normal comment. E.g.,
     *      // TODO: Do the thing.
     *      -->> The important blank line <<--
     *      // Hey! I'm not a todo.
     */
    @NotNull
    public List<Todo> scan() {
        todos = new ArrayList<>();
        ProjectFileIndex.SERVICE
                .getInstance(project)
                .iterateContent(file -> {
                    if (!file.isDirectory() && exts.contains(file.getExtension())) {
                        storeTodos(file);
                    }
                    return true;
                });
        return todos;
    }

    private void storeTodos(VirtualFile file) {
        final CharSequence contents = LoadTextUtil.loadText(file);
        if (contents.length() == 0) return;
        Matcher matcher = todoExpr.matcher(contents);

        while (matcher.find()) {
            Todo todo = new Todo(
                    matcher.group(1),
                    matcher.group(2),
                    matcher.group(3)
            );
            todos.add(todo);
        }
    }
}
