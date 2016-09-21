package nablarch.tool.util.poi;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * {@link PoiUtil}のテスト。
 *
 * @author hisaaki sioiri
 */
public class PoiUtilTest {

    /** テストで使用するEXCELファイル */
    private static File excelFile = new File("./java/nablarch/tool/util/poi/PoiUtilTest.xls");

    /**
     * ファイルが正しく開けること。
     */
    @Test
    public void testOpenWorkBook() {
        HSSFWorkbook workbook = PoiUtil.getHssfWorkbook(excelFile);
        assertThat(workbook.getSummaryInformation().getTitle(), is(excelFile.getAbsolutePath()));

        workbook = PoiUtil.getHssfWorkbook("./java/nablarch/tool/util/poi/PoiUtilTest1.xls");
        assertThat(workbook.getSummaryInformation().getTitle(), is(new File("./java/nablarch/tool/util/poi/PoiUtilTest1.xls").getAbsolutePath()));
    }

    /**
     * シート名の一覧が取得できること。
     */
    @Test
    public void testSheetNames() {
        HSSFWorkbook workbook = PoiUtil.getHssfWorkbook(excelFile.getAbsolutePath());
        assertThat(PoiUtil.getSheetNamesOf(workbook), is(containsInAnyOrder("sheet1", "sheet2", "sheet3", "type")));
        assertThat(PoiUtil.getSheetNamesOf(excelFile), is(containsInAnyOrder("sheet1", "sheet2", "sheet3", "type")));
    }

    /**
     * セルの値が読み取れること。
     */
    @Test
    public void testToString() {
        HSSFWorkbook workbook = PoiUtil.getHssfWorkbook(excelFile);
        HSSFSheet sheet1 = workbook.getSheet("sheet1");

        // A1セルの確認
        assertThat(PoiUtil.toString(sheet1, 0, 0), is("A1"));
        assertThat(PoiUtil.toString(sheet1.getRow(0), 0), is("A1"));
        assertThat(PoiUtil.toString(sheet1.getRow(0).getCell(0)), is("A1"));

        // A2セルの確認
        assertThat(PoiUtil.toString(sheet1, 1, 0), is("100.01"));
        assertThat(PoiUtil.toString(sheet1.getRow(1), 0), is("100.01"));
        assertThat(PoiUtil.toString(sheet1.getRow(1).getCell(0)), is("100.01"));

        // B1,B2
        assertThat(PoiUtil.toString(sheet1, 0, 1), is("B1"));
        assertThat(PoiUtil.toString(sheet1, 1, 1), is("100.0"));

        // A3,B3
        assertThat(PoiUtil.toString(sheet1, 2, 0), is("true"));
        assertThat(PoiUtil.toString(sheet1, 2, 1), is("false"));

        // A4,B4
        assertThat(PoiUtil.toString(sheet1, 3, 0), is(""));
        assertThat(PoiUtil.toString(sheet1, 3, 1), is("い"));

        // 別シート
        HSSFSheet sheet2 = workbook.getSheetAt(1);
        assertThat(PoiUtil.toString(sheet2, 0, 0), is("シート2:A1"));
        assertThat(PoiUtil.toString(sheet2.getRow(0), 1), is("シート2:B1"));

        // Rowがnullの場合は、空文字
        assertThat(PoiUtil.toString(null, 0), is(""));
    }


    /** */
    @Test
    public void testToStringReference() {
        HSSFWorkbook workbook = PoiUtil.getHssfWorkbook(excelFile);
        HSSFSheet sheet3 = workbook.getSheet("sheet3");
        assertNotNull(sheet3);
        assertThat(PoiUtil.toString(sheet3, 0, 1), is("AAA"));
        assertThat(PoiUtil.toString(sheet3, 1, 1), is("100.0"));
        assertThat(PoiUtil.toString(sheet3, 2, 1), is("true"));
    }

    /**
     * セルに数式が使用されていて、セル読み込みに失敗する場合
     */
    @Test
    public void testErrorInToString() {
        HSSFWorkbook workbook = PoiUtil.getHssfWorkbook(excelFile);
        HSSFSheet sheet2 = workbook.getSheetAt(1);

        try {
            String s = PoiUtil.toString(sheet2, 1, 0);
            assertThat(s, is("10000.0"));
            //fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is(allOf(
                    containsString("unexpected exception occurred in processing cell."),
                    containsString("fileName = [" + excelFile.getAbsolutePath() + "]"),
                    containsString("sheetName = [sheet2]"),
                    containsString("rowNumber = [2]"),
                    containsString("columnNumber = [1]")
            )));
        }

        try {
            String s = PoiUtil.toString(sheet2.getRow(2), 0);
            assertThat(s, is("6.0"));
            //fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is(allOf(
                    containsString("unexpected exception occurred in processing cell."),
                    containsString("fileName = [" + excelFile.getAbsolutePath() + "]"),
                    containsString("sheetName = [sheet2]"),
                    containsString("rowNumber = [3]"),
                    containsString("columnNumber = [1]")
            )));
        }

        try {
            String s = PoiUtil.toString(sheet2.getRow(2).getCell(1));
            assertThat(s, is("0.0"));
            //fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is(allOf(
                    containsString("unexpected exception occurred in processing cell."),
                    containsString("fileName = [" + excelFile.getAbsolutePath() + "]"),
                    containsString("sheetName = [sheet2]"),
                    containsString("rowNumber = [3]"),
                    containsString("columnNumber = [2]")
            )));
        }
    }

    /** セルの形式によって正しく値が読み出せること。*/
    @Test
    public void testCellTypes() {
        HSSFWorkbook workbook = PoiUtil.getHssfWorkbook(excelFile);
        HSSFSheet sheet = workbook.getSheet("type");
        HSSFRow row = sheet.getRow(1);
        Object[] expected = {
                100.0,   // CELL_TYPE_NUMERIC
                "moji",  // CELL_TYPE_STRING
                true,    // CELL_TYPE_BOOLEAN
                "moji",  // CELL_TYPE_STRING(式)
                2.0,     // CELL_TYPE_NUMERIC(式)
                false    // CELL_TYPE_BOOLEAN(式)
        };

        for (int i = 0; i < expected.length; i++) {
            assertThat(PoiUtil.doReadCell(row.getCell(i)),
                       is(expected[i]));
        }

        // CELL_TYPE_ERROR
        try {
            PoiUtil.doReadCell(row.getCell(6));
            fail();
        } catch (IllegalArgumentException e) {
            // OK
        }

        // CELL_TYPE_BLANK
        HSSFCell cell = row.getCell(0);
        cell.setCellValue("");
        cell.setCellType(Cell.CELL_TYPE_BLANK);
        assertThat((String) PoiUtil.doReadCell(cell), is(""));

        // CELL_TYPE_ERROR
        cell.setCellType(Cell.CELL_TYPE_ERROR);
        try {
            PoiUtil.doReadCell(cell);
            fail();
        } catch (IllegalArgumentException e) {
            // OK
        }
    }

    /** 指定したファイルのファイルシステム上のパスを取得できること。 */
    @Test
    public void testGetBookPath() {
        HSSFWorkbook workbook = PoiUtil.getHssfWorkbook(excelFile);
        assertThat(PoiUtil.getBookPath(workbook),
                   is(excelFile.getAbsolutePath()));
    }

    /** ブックのパスが設定されていない場合、空文字が取得できること。 */
    @Test
    public void testGetBookPathNull() {
        HSSFWorkbook workbook = new HSSFWorkbook();
        assertThat(PoiUtil.getBookPath(workbook), is(""));
    }


}

