package com.marcusposey;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

/** SyncAction initiates a sync of a project's todo items */
public class SyncAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        TodoService service = null;
        try {
            service = new TodoService(project);
        } catch (MissingRepoException exception) {
            Messages.showMessageDialog(event.getProject(),
                    "Could not find a repository named " + project.getName(),
                    "Error", Messages.getErrorIcon());
            return;
        }

        // Scanner scanner = new Scanner(project);
        // service.push(scanner.scan());
    }
}
