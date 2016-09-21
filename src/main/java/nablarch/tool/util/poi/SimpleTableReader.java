package nablarch.tool.util.poi;

import nablarch.core.util.StringUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 簡易的に表を読み込むクラス。
 * <p/>
 * 例えば、以下の表は、
 * <pre>
 * +-----+-----+
 * |key1 |key2 |
 * +-----+-----+
 * |11   |12   |
 * +-----+-----+
 * |21   |22   |
 * +-----+-----+
 * </pre>
 * 次のようなList-Mapとして読み込まれる。
 * <pre>
 * [{key2=12, key1=11}, {key2=22, key1=21}]
 * </pre>
 *
 * @author T.Kawasaki
 */
public class SimpleTableReader {

    /** ヘッダ行の行番号（０オリジン） */
    private int headerRowNum = 0;

    /** カラムのオフセット（０オリジン） */
    int startColumn = 0;     // SUPPRESS CHECKSTYLE サブクラスに対してカプセル化不要なため

    /** カラムの読み取り上限 */
    int endColumn = 255;     // SUPPRESS CHECKSTYLE サブクラスに対してカプセル化不要なため


    /**
     * ヘッダ行の行番号を設定する。
     *
     * @param headerRowNum ヘッダ行の行番号（０オリジン）
     * @return 本インスタンス自身
     */
    public SimpleTableReader setHeaderRowNum(int headerRowNum) {
        this.headerRowNum = headerRowNum;
        return this;
    }

    /**
     * カラム読み取り終了位置を取得する。
     *
     * @param endColumn カラム読み取り終了位置（０オリジン）
     * @return 本インスタンス自身
     */
    public SimpleTableReader setEndColumn(int endColumn) {
        this.endColumn = endColumn;
        return this;
    }

    /**
     * カラム読み取り開始位置を取得する。
     * （デフォルトは0）
     *
     * @param startColumn カラム読み取り開始位置（０オリジン）
     * @return 本インスタンス自身
     */
    public SimpleTableReader setStartColumn(int startColumn) {
        this.startColumn = startColumn;
        return this;
    }

    /** 読み取り時の事前条件をチェックする。 */
    private void checkPreConditions() {
        if (startColumn > endColumn) {
            throw new IllegalStateException(
                    "endColumn must be bigger than startColumn."
                            + " startColumn=[" + startColumn + "]"
                            + " endColumn=[" + endColumn + "]");
        }
    }

    /**
     * テーブルを読み込む。
     *
     * @param sheet 読み込み対象のシート
     * @return 読み込み結果
     */
    public List<Map<String, String>> read(HSSFSheet sheet) {
        checkPreConditions();
        List<String> head = readHeader(sheet);
        List<Map<String, String>> ret = new ArrayList<Map<String, String>>();
        for (int rowIndex = headerRowNum + 1;; rowIndex++) {
            HSSFRow row = sheet.getRow(rowIndex);
            Map<String, String> map = toMap(row, head);
            if (map == null) {
                break;
            }
            ret.add(map);
        }
        return ret;
    }

    /**
     * 行データをMapに変換する。
     *
     * @param row    行データ
     * @param header ヘッダー
     * @return 変換後のMap(行データがnullまたは空の場合はnullを返却)
     */
    private LinkedHashMap<String, String> toMap(HSSFRow row, List<String> header) {
        if (row == null) {
            return null;
        }
        LinkedHashMap<String, String> ret = new LinkedHashMap<String, String>();
        boolean hasValue = false;
        for (int i = 0; i < header.size(); i++) {
            String key = header.get(i);
            String value = PoiUtil.toString(row.getCell(i + startColumn));
            hasValue = hasValue || StringUtil.hasValue(value);
            ret.put(key, value);
        }
        return (hasValue) ? ret : null;
    }

    /**
     * ヘッダ行を読み取る。
     *
     * @param sheet 読み取り対象のシート
     * @return ヘッダ行
     */
    private List<String> readHeader(HSSFSheet sheet) {
        HSSFRow row = sheet.getRow(headerRowNum);
        int lastCellNum = getLastCellNum(row);
        List<String> headerColumns = new ArrayList<String>(lastCellNum);
        for (int i = startColumn; i < lastCellNum; i++) {
            String cellValue = PoiUtil.toString(row.getCell(i));
            if (StringUtil.hasValue(cellValue)) {
                headerColumns.add(cellValue);
            }
        }
        return headerColumns;
    }

    /**
     * 最終セルの位置を取得する。
     * 以下の値のうち、小さいほうが返却される。
     * <ul>
     * <li>{@link HSSFRow#getLastCellNum()}の値</li>
     * <li>{@link #setEndColumn(int)}で設定した値</li>
     * </ul>
     *
     * @param row 行データ
     * @return 最終セルの位置
     */
    protected int getLastCellNum(HSSFRow row) {
        int last = row.getLastCellNum();
        return (last < endColumn) ? last : endColumn;
    }

}
