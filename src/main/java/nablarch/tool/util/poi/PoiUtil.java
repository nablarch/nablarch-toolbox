package nablarch.tool.util.poi;

import nablarch.core.util.FileUtil;
import nablarch.test.NablarchTestUtils;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * POIを扱う際のユーティリティクラス。
 *
 * @author T.Kawasaki
 */
public final class PoiUtil {

    /** 一度読み込んだブック（{@link HSSFWorkbook}）のキャッシュ */
    private static Map<String, HSSFWorkbook> bookCache = NablarchTestUtils.createLRUMap(5);

    /** プライベートコンストラクタ */
    private PoiUtil() {
    }

    /**
     * ブックを取得する。
     *
     * @param filePath ファイルパス
     * @return ブック
     */
    public static HSSFWorkbook getHssfWorkbook(String filePath) {
        return getHssfWorkbook(new File(filePath));
    }

    /**
     * ブックを取得する。
     * 後でドキュメントのファイルパスを取得できるように、タイトル領域にパスを設定する。
     *
     * @param file ファイル
     * @return ブック
     */
    public static HSSFWorkbook getHssfWorkbook(File file) {
        String uri = file.toURI().toString();
        HSSFWorkbook book = bookCache.get(uri);
        if (book != null) {
            return book;  // cache hit.
        }
        InputStream in = FileUtil.getResource(uri);
        try {
            book = new HSSFWorkbook(new POIFSFileSystem(in));
            // 後でドキュメントのファイルパスを取得できるように、タイトル領域にパスを設定する。
            book.getSummaryInformation().setTitle(file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("test data file open failed.", e);
        } finally {
            FileUtil.closeQuietly(in);
        }
        bookCache.put(uri, book);
        return book;
    }

    /**
     * ブックのパスを取得する。
     *
     * @param book ブック
     * @return ブックのパス
     */
    public static String getBookPath(HSSFWorkbook book) {
        SummaryInformation info = book.getSummaryInformation();
        if (info == null) {
            return "";
        }
        return info.getTitle();
    }
    /**
     * セルの文字列表現を取得する。
     *
     * @param sheet     シート
     * @param rowIndex  行番号
     * @param cellIndex 列番号
     * @return セルの文字列表現
     */
    public static String toString(HSSFSheet sheet, int rowIndex, int cellIndex) {
        return toString(sheet.getRow(rowIndex), cellIndex);
    }

    /**
     * 行{@link HSSFRow}中の指定した場所のセル内容を文字列として取得する。
     *
     * @param row       行
     * @param cellIndex セルの位置（0オリジン）
     * @return セル内容の文字列表現
     */
    public static String toString(HSSFRow row, int cellIndex) {
        if (row == null) {
            return "";
        }
        return toString(row.getCell(cellIndex));
    }

    /**
     * セルを文字列に変換する。
     *
     * @param cell 変換対象のセル
     * @return 変換後の文字列
     */
    public static String toString(HSSFCell cell) {
        if (cell == null) {
            return "";
        }
        try {
            return readCellValue(cell);
        } catch (RuntimeException e) {
            HSSFSheet sheet = cell.getSheet();
            HSSFWorkbook workbook = sheet.getWorkbook();
            throw new RuntimeException(
                    "unexpected exception occurred in processing cell."
                            + " fileName = [" + workbook.getSummaryInformation().getTitle() + ']'
                            + " sheetName = [" + sheet.getSheetName() + ']'
                            + " rowNumber = [" + (cell.getRowIndex() + 1) + ']'
                            + " columnNumber = [" + (cell.getColumnIndex() + 1) + ']',
                    e);
        }
    }

    /**
     * {@link HSSFCell}の値を文字列として読み込む。
     *
     * @param cell {@link HSSFCell}
     * @return 読み込んだ文字列値
     */
    private static String readCellValue(HSSFCell cell) {
        Object content = doReadCell(cell);
        return String.valueOf(content);
    }

    /**
     * {@link HSSFCell}の値を読み込む。<br/>
     * セルの値が数式の場合、その数式を評価した結果を返却する。
     * 例えばセルの値が<code>=1+1</code>の場合は、2を返却する。
     *
     * @param cell {@link HSSFCell}
     * @return 読み込んだ文字列値
     * @throws IllegalArgumentException 数式の評価結果がエラーの場合
     */
    static Object doReadCell(HSSFCell cell) throws IllegalArgumentException {
        int type = cell.getCellType();
        switch (type) {
            case Cell.CELL_TYPE_BLANK:
                return "";
            case Cell.CELL_TYPE_NUMERIC:
                return cell.getNumericCellValue();
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_FORMULA:
                // 数式の場合、式評価の結果により分岐する。
                CellValue evaluated = evaluateFormula(cell);
                int evaluatedCellType = evaluated.getCellType();
                switch (evaluatedCellType) {
                    case Cell.CELL_TYPE_STRING:
                        return evaluated.getStringValue();
                    case Cell.CELL_TYPE_NUMERIC:
                        return evaluated.getNumberValue();
                    case Cell.CELL_TYPE_BOOLEAN:
                        return evaluated.getBooleanValue();
                    default:
                        throw new IllegalArgumentException(
                                "could not handle cell formula. cell type=[" + evaluatedCellType + "]");
                }
            default:
                //その他の場合(通常ありえない)
                throw new IllegalArgumentException(
                        "could not handle cell formula. cell type=[" + type + "]");

        }
    }

    /**
     * 数式を評価した値を取得する。
     *
     * @param cell 評価対象となるセル
     * @return 数式を評価した結果の値
     */
    private static CellValue evaluateFormula(HSSFCell cell) {
        return cell.getSheet()
                   .getWorkbook()
                   .getCreationHelper()
                   .createFormulaEvaluator()
                   .evaluate(cell);
    }

    /**
     * 指定されたファイルが保持するシート名の一覧を取得する。
     *
     * @param file ファイル
     * @return シート名一覧
     */
    public static List<String> getSheetNamesOf(File file) {
        HSSFWorkbook book = getHssfWorkbook(file);
        return getSheetNamesOf(book);
    }

    /**
     * 指定されたファイルが保持するシート名の一覧を取得する。
     *
     * @param book ブック
     * @return シート名一覧
     */
    public static List<String> getSheetNamesOf(HSSFWorkbook book) {
        int numberOfSheets = book.getNumberOfSheets();
        List<String> names = new ArrayList<String>(numberOfSheets);
        for (int i = 0; i < numberOfSheets; i++) {
            String name = book.getSheetName(i);
            names.add(name);
        }
        return names;
    }

    /**
     * ファイルを保存する。
     *
     * @param bookToSave 保存対象のブック
     * @param path       保存先パス
     */
    public static void save(HSSFWorkbook bookToSave, String path) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            bookToSave.write(out);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            FileUtil.closeQuietly(out);
        }
    }
}
