package nablarch.tool.util.poi;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

/**
 * @author T.Kawasaki
 */
public class SimpleTableWriterTest {


    private static String BOOK = "src/test/resources/nablarch/tool/util/poi/SimpleTableWriterTest.xls";


    /** */
    @Test
    public void testWrite() {
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        {
            Map<String, String> row = new LinkedHashMap<String, String>();
            row.put("NAME", "奈部楽太郎");
            row.put("ADDRESS", "東京都");
            row.put("AGE", "30");
            data.add(row);

        }
        {
            // Excelシートの見出し行の順に沿うことを確認するため
            // 敢えて順番を1行目とバラバラにして設定する。
            Map<String, String> row = new LinkedHashMap<String, String>();
            row.put("AGE", "28");
            row.put("NAME", "奈部楽次郎");
            row.put("ADDRESS", "神奈川県");
            data.add(row);
        }
        HSSFWorkbook workbook = PoiUtil.getHssfWorkbook(BOOK);
        HSSFSheet sheet = workbook.getSheet("testWrite");
        SimpleTableWriter target = new SimpleTableWriter(sheet, 1);
        target.write(data);
        {
            HSSFRow row = sheet.getRow(1);
            assertThat(PoiUtil.toString(row.getCell(0)), is("NAME"));
            assertThat(PoiUtil.toString(row.getCell(1)), is("ADDRESS"));
            assertThat(PoiUtil.toString(row.getCell(2)), is("AGE"));
        }
        {
            HSSFRow row = sheet.getRow(2);
            assertThat(PoiUtil.toString(row.getCell(0)), is("奈部楽太郎"));
            assertThat(PoiUtil.toString(row.getCell(1)), is("東京都"));
            assertThat(PoiUtil.toString(row.getCell(2)), is("30"));
        }

        {
            HSSFRow row = sheet.getRow(3);
            assertThat(PoiUtil.toString(row.getCell(0)), is("奈部楽次郎"));
            assertThat(PoiUtil.toString(row.getCell(1)), is("神奈川県"));
            assertThat(PoiUtil.toString(row.getCell(2)), is("28"));
        }

    }
}
