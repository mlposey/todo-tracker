package com.marcusposey;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.junit.Assert;

import java.util.Map;

/** Tests for the Scanner class */
public class ScannerTest extends LightPlatformCodeInsightFixtureTestCase {
    @Override
    public String getTestDataPath() {
        return System.getProperty("user.dir") + "/testData";
    }

    /** Scanner.scan() should detect all project files. */
    public void testScan_seesFiles() throws Exception {
        final String testFile = "hello_world.go";
        myFixture.configureByFile(testFile);

        Scanner scanner = new Scanner(myFixture.getProject());
        scanner.scan();

        Map<String, VirtualFile> projectFiles = scanner.getFiles();
        Assert.assertEquals(1, projectFiles.keySet().stream()
                .filter(fileName -> fileName.contains(testFile))
                .count());
    }
}
