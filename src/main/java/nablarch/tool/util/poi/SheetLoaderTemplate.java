package nablarch.tool.util.poi;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Excelシートから表データを読み込むクラスのテンプレートクラス。
 *
 * @author T.Kawasaki
 */
public abstract class SheetLoaderTemplate {

    /** ブックへのパス */
    private String pathToBook;

    /** 読み込んだブック */
    private HSSFWorkbook book;

    /** 明示的に指定しない場合に読み込み対象となるシート名 */
    private String defaultSheetName;

    /** データ開始行 */
    private int startRowIndex;

    /** カラム定義 */
    private XlsColumnDefs columnDefs;

    /**
     * 読み込み対象ブックのパスを取得する。
     *
     * @return パス
     */
    protected String getPathToBook() {
        return pathToBook;
    }

    /**
     * 読み込み対象ブックのパスを設定する。
     *
     * @param pathToBook ブックのパス
     */
    public void setPathToBook(String pathToBook) {
        this.pathToBook = pathToBook;
        this.book = PoiUtil.getHssfWorkbook(pathToBook);
    }

    /**
     * データ開始行を取得する。
     *
     * @return データの開始行
     */
    protected int getStartRowIndex() {
        return startRowIndex;
    }

    /**
     * データ開始行を設定する。
     *
     * @param startRowIndex 開始行
     */
    public void setStartRowIndex(int startRowIndex) {
        this.startRowIndex = startRowIndex;
    }

    /**
     * カラム定義を取得する。
     *
     * @return カラム定義
     */
    protected XlsColumnDefs getColumnDefs() {
        return columnDefs;
    }

    /**
     * カラム定義を設定する。
     *
     * @param columnDefs カラム定義
     */
    public void setColumnDefs(XlsColumnDefs columnDefs) {
        this.columnDefs = columnDefs;
    }

    /**
     * 処理対象となるシート名を取得する。
     *
     * @return シート名
     */
    public String getSheetName() {
        return defaultSheetName;
    }

    /**
     * 処理対象となるシート名を設定する。
     *
     * @param defaultSheetName シート名
     */
    public void setSheetName(String defaultSheetName) {
        this.defaultSheetName = defaultSheetName;
    }

    /**
     * 行検索クラスを取得する。
     * 検索対象となるシートは{@link #setSheetName} で設定したシートとなる。
     *
     * @return 行検索クラス
     */
    protected RowFinder getRowFinder() {
        HSSFSheet targetSheet = getTargetSheet();
        return getRowFinder(targetSheet);
    }

    /**
     * 行検索クラスを取得する。
     *
     * @param sheetName シート名
     * @return 行検索クラス
     */
    protected RowFinder getRowFinder(String sheetName) {
        HSSFSheet targetSheet = getSheet(sheetName);
        return getRowFinder(targetSheet);
    }

    /**
     * 行検索クラスを取得する。
     *
     * @param sheet 検索対象シート
     * @return 行検索クラス
     */
    private RowFinder getRowFinder(HSSFSheet sheet) {
        return new RowFinder(sheet, startRowIndex, columnDefs);
    }

    /**
     * 指定されたブックから、処理対象のシート（{@link #setSheetName} で設定）
     * を取得する。
     *
     * @return シート
     */
    protected HSSFSheet getTargetSheet() {
        return getSheet(defaultSheetName);
    }

    /**
     * ブックに格納された全シート名を取得する。
     *
     * @return 全シート名
     */
    protected List<String> getSheetNames() {
        int numberOfSheets = book.getNumberOfSheets();
        List<String> sheetNames = new ArrayList<String>(numberOfSheets);
        for (int i = 0; i < numberOfSheets; i++) {
            String sheetName = book.getSheetName(i);
            sheetNames.add(sheetName);
        }
        return sheetNames;
    }

    /**
     * {@link HSSFSheet}を取得する。
     *
     * @param name シート名
     * @return シート
     */
    protected HSSFSheet getSheet(String name) {
        HSSFSheet sheet = book.getSheet(name);
        if (sheet == null) {
            throw new IllegalArgumentException("could not find sheet [" + name + "] in the book.");
        }
        return sheet;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "book=[" + pathToBook + "] "
                + "sheetName=[" + defaultSheetName + "] "
                + "startRowIndex=" + startRowIndex + "] "
                + "xlsColumnDef=" + columnDefs;
    }
}
