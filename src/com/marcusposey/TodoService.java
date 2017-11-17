package com.marcusposey;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.github.api.GithubConnection;
import org.jetbrains.plugins.github.util.GithubAuthData;


/** TodoServer pushes todo comments to GitHub as issues. */
public class TodoService {
    private final GithubConnection conn;
    private final GitRepository repo;

    /**
     * Creates a new service that is attached to a GitHub repository
     * @param project The local project
     * @throws MissingRepoException if a repository cannot be found that matches
     *                              the current project
     */
    public TodoService(@NotNull Project project) throws MissingRepoException {
        GithubAuthData authData = GithubAuthData.createFromSettings();
        conn = new GithubConnection(authData, true);

        VirtualFile file = project.getProjectFile();
        repo = GitUtil.getRepositoryManager(project).getRepositoryForFile(file);
        if (repo == null) throw new MissingRepoException();
    }
}
