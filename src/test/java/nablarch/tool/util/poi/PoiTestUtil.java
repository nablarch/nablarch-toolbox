package nablarch.tool.util.poi;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * @author T.Kawasaki
 */
public class PoiTestUtil {

    public static HSSFWorkbook createBook(String sheetName) {
        HSSFWorkbook book = new HSSFWorkbook();
        HSSFSheet sheet = book.createSheet(sheetName);
        initSheet(sheet);
        return book;
    }

    public static void initSheet(HSSFSheet sheet) {
        for (int i = 0; i < 100;i++) {
            HSSFRow row = sheet.createRow(i);
            for (int j = 0; j < 100; j++) {
                row.createCell(j);
            }
        }
    }
}
