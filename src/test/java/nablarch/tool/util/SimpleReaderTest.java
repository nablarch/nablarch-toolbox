package nablarch.tool.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link SimpleReader}のテストクラス
 * 
 * @author Kenta.Morimoto
 *
 */
public class SimpleReaderTest {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    /**
     * ファイルを正常に読み込めることを確認する。
     * 
     */
    @Test
    public void testFileToString(){
        String actual = SimpleReader.fileToString("src/test/resources/nablarch/tool/util/SimpeReaderTestNomalData.txt", "UTF-8");
        
        String expected = "123456789\r\nabcdefg\r\n"
                        + "あいうえお\r\nカキクケコ\r\n"
                        + "一二三四五六七八九\r\n\r\n";
        
        assertThat(actual, is(expected));
    }
    
    /**
     * 指定したファイルが存在しない場合に発生する例外とエラーメッセージを確認する。
     */
    @Test
    public void testFileNotFound(){
        File file = new File("testFileNotFound.txt");
        
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("File is not found. file=[" + file.getAbsolutePath() + "]");        
        
        SimpleReader.fileToString("testFileNotFound.txt", "UTF-8");
    }
    
    /**
     * 指定したエンコードが存在しない場合に発生する例外とエラーメッセージを確認する。
     */
    @Test
    public void testUnsupportedEncoding(){
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("this encoding is not supported. encoding=[test-encoding]");
        
        SimpleReader.fileToString("src/test/resources/nablarch/tool/util/SimpeReaderTestNomalData.txt", "test-encoding");
    }
}
