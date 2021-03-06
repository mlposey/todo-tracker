package com.marcusposey;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.junit.Assert;

import java.util.List;

/** Tests for the Scanner class */
public class ScannerTest extends LightPlatformCodeInsightFixtureTestCase {
    @Override
    public String getTestDataPath() {
        return System.getProperty("user.dir") + "/testData";
    }

    /** Scanner.scan() should detect all todo items. */
    public void testScan_seesTodos() throws Exception {
        myFixture.configureByFile("todo_sample.go");
        final int todoCount = 5;

        Scanner scanner = new Scanner(myFixture.getProject());
        List<Todo> todos = scanner.scan();

        Assert.assertEquals(todoCount, todos.size());
    }

    /** Scanner.scan should detect all go files and nothing else. */
    public void testScan_seesAllFiles() {
        myFixture.configureByFiles("todo_sample.go", "todo_sample2.go",
                "skippable.sh");
        final int todoCount = 6;

        Scanner scanner = new Scanner(myFixture.getProject());
        List<Todo> todos = scanner.scan();

        Assert.assertEquals(todoCount, todos.size());
    }

    /** Scanner.scan should parse the body of a todo if it exists */
    public void testScan_retrievesBody() {
        myFixture.configureByFile("body.go");
        final String expectedBody = "You can do it! :D\n" +
                "Believe in the belief to believe!";

        Scanner scanner = new Scanner(myFixture.getProject());
        List<Todo> todos = scanner.scan();

        Assert.assertEquals(1, todos.size());
        Assert.assertEquals(expectedBody, todos.get(0).body);
    }
}
