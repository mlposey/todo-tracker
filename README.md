# Todo Tracker
<a href="http://teamcity.marcusposey.com/viewType.html?buildTypeId=TodoTracker_Build&guest=1">
<img src="http://teamcity.marcusposey.com/app/rest/builds/buildType:(id:TodoTracker_Build)/statusIcon"/>
</a>  

This plugin tracks todo comments in an IntelliJ IDEA project. It currently
supports Go, Java, and C++ file extensions, as well as the ability to
submit todos as GitHub issues.

## GitHub Issues
In order to submit todo blocks as GitHub issues, the following must hold:  
1. The project contains a Git repository with a GitHub remote.
2. The GitHub account has pull access to the remote.
3. The todo block contains an author tag that matches the GitHub account in use.

Assuming 1, 2, and an account `user`, these two styles will result in successful
issues:

// TODO(user): The issue title.

// TODO(user): The issue title.  
// The body of the  
// issue goes here.

