package com.marcusposey;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.io.IOException;

/** SyncAction initiates a sync of a project's todo items */
public class SyncAction extends AnAction {

    /**
     * Takes each todo comment from all .go project files and adds them
     * to the GitHub repository as issues if they did not already
     * exist.
     */
    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        IssueService service = null;

        try {
            service = new GitHubIssueService(project);
        } catch (MissingRepoException exception) {
            Messages.showMessageDialog(project,
                    "Could not find a repository named " + project.getName(),
                    "Error", Messages.getErrorIcon());
            return;
        } catch (IOException e) {
            Messages.showMessageDialog(project,
                    "Could not authenticate GitHub user", "Error",
                    Messages.getErrorIcon());
            return;
        }

        Scanner scanner = new Scanner(project);

        try {
            service.push(scanner.scan());
        } catch (IOException e) {
            Messages.showMessageDialog(project,
                    "Could not connect to repository",
                    "Error", Messages.getErrorIcon());
        }
    }
}
