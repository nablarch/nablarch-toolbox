package nablarch.tool.util;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ToolFileUtilTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void testDeleteAndCheck() throws IOException {
        File file = tempDir.newFile("a.txt");
        ToolFileUtil.deleteAndCheck(file);
    }

}