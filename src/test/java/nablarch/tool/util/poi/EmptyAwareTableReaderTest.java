package nablarch.tool.util.poi;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

/**
 * @author T.Kawasaki
 */
public class EmptyAwareTableReaderTest {

    private static final String BOOK = "src/test/resources/nablarch/tool/util/poi/EmptyAwareTableReaderTest.xls";

    /**
     * 数式を利用したシートであっても、表を認識して読み取れること。
     */
    @Test
    public void testWithFormula() {

        final String sheetName = "SYSTEM_ACCOUNT_AUTHORITY";

        HSSFWorkbook workbook = PoiUtil.getHssfWorkbook(BOOK);
        HSSFSheet sheet = workbook.getSheet(sheetName);
        EmptyAwareTableReader target = new EmptyAwareTableReader();
        target.setHeaderRowNum(2); // 3行目をヘッダ行とする。

        // 読み取り
        List<Map<String, String>> result = target.read(sheet);

        // 結果確認
        assertNotNull(result);
        // 1件目
        {
            Map<String, String> first = result.get(0);
            assertThat(first.get("USER_ID"), is("0000000001"));
            assertThat(first.get("KANJI_NAME"), is("名部良太郎"));
            assertThat(first.get("0000000000"), is("a"));
            assertThat(first.get("0000000001"), is("b"));
            assertThat(first.size(), is(4));
        }
        // 2件目
        {
            Map<String, String> second = result.get(1);
            assertThat(second.get("USER_ID"), is("0000000002"));
            assertThat(second.get("KANJI_NAME"), is("お客様１（携帯電話番号および認可単位無し）"));
            assertThat(second.get("0000000000"), is("c"));
            assertThat(second.get("0000000001"), is("d"));
            assertThat(second.size(), is(4));
        }
        assertThat(result.size(), is(2));
    }

    /**
     * 数式でない、通常の値でも、表を認識して読み取れること。
     */
    @Test
    public void testSimpleValue() {
        final String sheetName = "PERMISSION_UNIT";
        HSSFWorkbook workbook = PoiUtil.getHssfWorkbook(BOOK);
        HSSFSheet sheet = workbook.getSheet(sheetName);
        EmptyAwareTableReader target = new EmptyAwareTableReader();
        target.setHeaderRowNum(1); // 2行目をヘッダ行とする。

        // 読み取り
        List<Map<String, String>> result = target.read(sheet);

        // 結果確認
        assertNotNull(result);
        // 1件目
        {
            Map<String, String> first = result.get(0);
            assertThat(first.get("PERMISSION_UNIT_ID"), is("0000000000"));
            assertThat(first.get("PERMISSION_UNIT_NAME"), is("ログイン"));
            assertThat(first.size(), is(2));
        }
        // 2件目
        {
            Map<String, String> second = result.get(1);
            assertThat(second.get("PERMISSION_UNIT_ID"), is("0000000001"));
            assertThat(second.get("PERMISSION_UNIT_NAME"), is("ユーザ一覧照会"));
            assertThat(second.size(), is(2));
        }
        assertThat(result.size(), is(2));
    }

    /** */
    @Test
    public void testEnd() {
        final String sheetName = "just";
        HSSFWorkbook workbook = PoiUtil.getHssfWorkbook(BOOK);
        HSSFSheet sheet = workbook.getSheet(sheetName);
        EmptyAwareTableReader target = new EmptyAwareTableReader();
        target.setHeaderRowNum(1); // 2行目をヘッダ行とする。
        target.setEndColumn(2);

        // 読み取り
        int lastCellNum = target.getLastCellNum(sheet.getRow(0));
        assertThat(lastCellNum, is(2));
    }
}
