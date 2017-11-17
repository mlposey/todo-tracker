package com.marcusposey;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Scanner searches project files for todo items */
public class Scanner {
    private final Project project;
    private final Map<String, VirtualFile> files = new HashMap<>();
    // The scanner only touches files with these extensions.
    private final Set<String> exts = new HashSet<>();

    public Scanner(Project project) {
        this.project = project;
        exts.add("go");
    }

    /** Scans all files in the project directory */
    public void scan() {
        ProjectFileIndex.SERVICE
                .getInstance(project)
                .iterateContent(file -> {
                    if (!file.isDirectory() && exts.contains(file.getExtension())) {
                        storeTodos(file);
                    }
                    return true;
                });
    }

    private void storeTodos(VirtualFile file) {
        files.put(file.getCanonicalPath(), file);
        // Todo: Turn the todos into Todo objects and store in this.todos.
    }

    public Map<String, VirtualFile> getFiles() {
        return files;
    }
}
