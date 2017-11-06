package nablarch.tool.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nablarch.core.util.FileUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link CsvUtil}のテストクラス。
 * 
 * @author Kenta.Morimoto
 */
public class CsvUtilTest {
    
    List<Map<String, String>> testDataList = null;
    String outputFilePath = "target/test-work/CsvUtilTest.csv";
    
    @Before
    public void setUp(){

        if (!FileUtil.deleteFile(new File(outputFilePath))) {
            throw new RuntimeException("This file can not be deleted. filePath = [" + outputFilePath + "]");
        }

        testDataList = new ArrayList<Map<String, String>>();
        for(int i = 0; i < 5; i++){
            Map<String, String> testDataElement = new HashMap<String, String>();
            testDataElement.put("testID", String.valueOf(i));
            testDataElement.put("testName", "Name" + i);
            testDataList.add(testDataElement);
        }
    }
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    /**
     * {@literal List<Map<String,String>>}型のデータを指定した<br />
     * フォーマット通りに変換できることを確認する。<br />
     * フォーマットは{@link CsvUtil#writeFile}の通り
     */
    @Test
    public void testWrite(){
        CsvUtil.writeFile(testDataList, outputFilePath, "UTF-8", "\r\n");
        
        String actual = SimpleReader.fileToString(outputFilePath, "UTF-8");
        String expected = SimpleReader.fileToString("src/test/resources/nablarch/tool/util/CsvUtilTest.csv", "UTF-8"); 
        
        assertThat(actual, is(expected));
    }
    
    /**
     * 存在しないディレクトリに出力しようとした際に、例外が発生することを確認する。<br/> 
     */
    @Test
    public void testNotExistsDirectory() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("FileNotFoundException occurred. FilePath=[work/CsvUtilTest/testNotExistsDirectory.csv]");
        
        CsvUtil.writeFile(testDataList, "work/CsvUtilTest/testNotExistsDirectory.csv", "UTF-8", "\r\n");
    }
    
    /**
     * charsetに存在しないエンコードが指定された場合、<br/>
     * 例外が発生することを確認する。
     */
    @Test
    public void testNotExistsEncoding() {
        
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("this encoding is not supported. encoding=[test-encoding]");
        
        CsvUtil.writeFile(testDataList, outputFilePath, "test-encoding", "\r\n");
    }
    
    /**
     * inputDataに空のリストが渡された際、空のCSVファイルが出力されることを確認する。
     * 
     */
    @Test
    public void testInputDataIsEmptyList(){
        CsvUtil.writeFile(new ArrayList<Map<String, String>>(), outputFilePath, "UTF-8", "\r\n");
        String actual = SimpleReader.fileToString(outputFilePath, "UTF-8");
        
        assertThat(actual, is(""));
    }
    
    @After
    public void after(){
        if (!FileUtil.deleteFile(new File(outputFilePath))) {
            throw new RuntimeException("This file can not be deleted. filePath = [" + outputFilePath + "]");
        }
    }
}
