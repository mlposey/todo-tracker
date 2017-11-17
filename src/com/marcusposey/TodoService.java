package com.marcusposey;

import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.github.api.GithubApiUtil;
import org.jetbrains.plugins.github.api.GithubConnection;
import org.jetbrains.plugins.github.api.GithubFullPath;
import org.jetbrains.plugins.github.api.data.GithubIssue;
import org.jetbrains.plugins.github.util.*;

import java.io.IOException;
import java.util.*;


/** TodoServer pushes todo comments to GitHub as issues. */
public class TodoService {
    private final Project project;

    // A URI to the issues POST endpoint
    private String issuesPostUri;

    private GithubFullPath userAndRepo;
    private final GithubConnection conn;

    private Gson gson = new Gson();

    /**
     * Creates a new service that is attached to a GitHub repository
     * @param project The local project
     * @throws MissingRepoException if a repository cannot be found that matches
     *                              the current project
     * @throws IOException if GitHub authorization cannot be granted
     */
    public TodoService(@NotNull Project project) throws MissingRepoException,
            IOException {
        GithubAuthDataHolder authDataHolder = GithubUtil
                .computeValueInModalIO(project, "Access to GitHub", indicator ->
                    GithubUtil.getValidAuthDataHolderFromConfig(project,
                            AuthLevel.LOGGED, indicator)
        );
        conn = new GithubConnection(authDataHolder.getAuthData(), true);

        GitRepository repo = GitUtil.getRepositoryManager(project)
                .getRepositoryForFile(project.getProjectFile());
        if (repo == null) throw new MissingRepoException();

        this.project = project;
        extractMeta(repo);
    }

    /**
     * Pushes todos to the project repository as issues
     *
     * If a todo has the same title as an existing issue's title, it will
     * not be uploaded to the repository.
     * @throws IOException if authentication fails
     */
    public void push(@NotNull List<Todo> todos) throws IOException {
        List<GithubIssue> issues = GithubApiUtil.getIssuesAssigned(conn,
                userAndRepo.getUser(), userAndRepo.getRepository(), "",
                Integer.MAX_VALUE, false);

        Set<String> issueTitles = new HashSet<>();
        issues.forEach(iss -> issueTitles.add(iss.getTitle()));

        // Submit only new issues.
        todos.stream()
                .filter(todo -> !issueTitles.contains(todo.title))
                .forEach(this::submitNewIssue);
    }

    /** Submits the todo as a new issue to the project repository */
    private void submitNewIssue(@NotNull Todo todo) {
        try {
            conn.postRequest(issuesPostUri, gson.toJson(todo));
        } catch (IOException e) {
            Messages.showMessageDialog(project, "Could not post issue:\n" +
                gson.toJson(todo), "Error", Messages.getErrorIcon());
        }
    }

    /** Extracts information about the repository */
    private void extractMeta(GitRepository repo) throws IOException {
        String url = GithubUtil.findGithubRemoteUrl(repo);
        if (url == null) throw new IOException("Cannot find remote url");

        userAndRepo = GithubUrlUtil.getUserAndRepositoryFromRemoteUrl(url);
        if (userAndRepo == null) {
            throw new IOException("Missing user or repository at remote URL");
        }

        issuesPostUri = "/repos/" + userAndRepo.getUser() + '/' +
                userAndRepo.getRepository() + "/issues";
    }
}
