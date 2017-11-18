package com.marcusposey;

import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scanner searches project files for todo items
 *
 * It supports the following extensions:
 *      go, java, cpp, cc, h, hpp
 */
public class Scanner {
    private final Project project;
    // The scanner only touches files with these extensions.
    private final Set<String> exts = new HashSet<>();

    private List<Todo> todos;

    // Matches a todo comment header
    // The match contains between 1 and 2 groups.
    // Group 1 [optional] = name
    // Group 2 [required] = title
    private final Pattern header = Pattern.compile("// TODO(.*)?: (.*)");

    /** Creates a new Scanner attached to a project's files */
    public Scanner(@NotNull Project project) {
        this.project = project;
        exts.addAll(Arrays.asList("go", "java", "cpp", "h", "hpp", "cc"));
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
     *      // You can do it! :D
     *
     *      // TODO(name): Do the thing.
     *      // You can do it! :D
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

    /** Finds and stores each todo block contained in file */
    private void storeTodos(@NotNull VirtualFile file) {
        final String[] lines = LoadTextUtil.loadText(file).toString().split("\n");

        String author = null, title = null;
        StringBuilder body = new StringBuilder();

        for (String line : lines) {
            line = removeIndentation(line);

            if (!line.startsWith("//")) {
                if (title != null) { // The todo definition is finished.
                    todos.add(new Todo(
                            trimEdges(author), title, trimNewline(body))
                    );
                    author = title = null;
                    body = new StringBuilder();
                }
                continue;
            }
            // Found a comment line.
            Matcher matcher = header.matcher(line);
            if (matcher.find()) {
                author = matcher.group(1);
                title = matcher.group(2);
            } else {
                if (title != null) { // Still consuming the body.
                    body.append(line.replace("// ", "").replace("//", ""));
                    body.append('\n');
                }
            }
        }
        if (author != null) {
            // There could be a todo on the final line that would not be
            // caught by the loop above.
            todos.add(new Todo(trimEdges(author), title, trimNewline(body)));
        }
    }

    /**
     * Returns a copy of val without the first and last character
     * If val is null or empty, it will simply be returned.
     */
    @Nullable
    private String trimEdges(@Nullable String val) {
        if (val == null || val.isEmpty()) return val;
        return val.substring(1, val.length() - 1);
    }

    /**
     * Returns a copy of val without the final newline
     * If val is empty, null is returned.
     */
    @Nullable
    private String trimNewline(@NotNull StringBuilder val) {
        if (val.length() == 0) return null;
        int len = val.length();
        if (val.charAt(len - 1) == '\n') return val.substring(0, len - 1);
        return val.toString();
    }

    /**
     * Removes the preceding indentation of a string
     * @param val text which may be preceded by spaces or tabs
     */
    private String removeIndentation(@NotNull String val) {
        if (val.isEmpty()) return val;

        int start = 0;
        while (val.charAt(start) == ' ' || val.charAt(start) == '\t') start++;
        return val.substring(start);
    }
}
