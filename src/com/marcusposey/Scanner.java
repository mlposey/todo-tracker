package com.marcusposey;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.HashMap;
import java.util.Map;

/** Scanner searches project files for todo items */
public class Scanner {
    private final Project project;
    private final Map<String, VirtualFile> files = new HashMap<>();

    public Scanner(Project project) {
        this.project = project;
    }

    /** Scans all files in the project directory */
    public void scan() {
        ProjectFileIndex.SERVICE
                .getInstance(project)
                //.iterateContent(new ProjectFileIterator(this));
                .iterateContent(file -> {
                    if (!file.isDirectory()) {
                        // Todo: Match against extensions.
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
