package nablarch.tool.util.poi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import nablarch.core.util.StringUtil;
import nablarch.tool.util.Arg;

/**
 * 行検索クラス。
 *
 * @author T.Kawasaki
 */
public class RowFinder {

    /** 検索対象シート */
    private final HSSFSheet sheet;

    /** カラム定義 */
    private final XlsColumnDefs defs;

    /** 処理対象行のインデックス */
    private int rowIndex = 0;

    /** デフォルトの処理終了条件 */
    private static final Condition DEFAULT_END_CONDITION = Condition.EMPTY_ROW;

    /**
     * コンストラクタ
     *
     * @param sheet         処理対象シート
     * @param startRowIndex 検索開始行
     * @param defs          カラム定義
     */
    public RowFinder(HSSFSheet sheet, int startRowIndex, XlsColumnDefs defs) {
        this.sheet = Arg.notNull(sheet, "sheet");
        this.rowIndex = startRowIndex;
        this.defs = Arg.notNull(defs);
    }

    /**
     * 検索条件に合致する行を検索する。 2回目以降は前回のヒットした行から
     * 合致する行が見つかる前に空行が現れた場合、検索を終了する。
     *
     * @param searchCondition 検索条件
     * @return 検索条件に合致した行のデータ（ヒットしない場合はnull）
     */
    public Map<String, String> findNext(Condition searchCondition) {
        return findNext(searchCondition, DEFAULT_END_CONDITION);
    }

    /**
     * 検索条件に合致する行を検索する。
     *
     * @param searchCondition 検索条件
     * @param endCondition    検索終了条件
     * @return 検索条件に合致した行のデータ（ヒットしない場合はnull）
     */
    public Map<String, String> findNext(Condition searchCondition, Condition endCondition) {
        for (; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Map<String, String> map = getRowDataAt(rowIndex);
            if (endCondition.matches(map)) {
                break;  // not found
            }
            if (searchCondition.matches(map)) {
                rowIndex++;  // proceed for next search
                return map;  // found
            }
        }
        return null;  // not found
    }

    /**
     * 全件検索（条件指定なし）
     * @return 全行のデータ
     */
    public List<Map<String, String>> findAll() {
        return findAll(Condition.ALWAYS_MATCH);
    }

    /**
     * 検索条件に合致する行を全件取得する。
     *
     * @param searchCondition 検索条件
     * @return 条件に合致した行全て
     */
    public List<Map<String, String>> findAll(Condition searchCondition) {
        return findAll(searchCondition, DEFAULT_END_CONDITION);
    }

    /**
     * 検索条件に合致する行を全件取得する。
     *
     * @param searchCondition 検索条件
     * @param endCondition 終了条件
     * @return 条件に合致した行全て
     */
    public List<Map<String, String>> findAll(Condition searchCondition, Condition endCondition) {
        List<Map<String, String>> ret = new ArrayList<Map<String, String>>();
        while (true) {
            Map<String, String> next = findNext(searchCondition, endCondition);
            if (next == null) {
                break; // ヒットしない時点で終了
            }
            ret.add(next);
        }
        return ret;
    }

    /**
     * 現在処理中の行インデックスを取得する。
     *
     * @return 処理中の行を指すインデックス（0オリジン)
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * 指定した行をカラム定義に沿って取得する。
     *
     * @param idx 行インデックス
     * @return 行データ
     */
    private Map<String, String> getRowDataAt(int idx) {
        try {
            HSSFRow row = sheet.getRow(idx);
            return defs.collect(row);
        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "unexpected exception occurred in processing row at [" + idx + "]", e);
        }
    }

    /**
     * 検索時の条件を表すクラス。
     */
    public interface Condition {

        /**
         * 指定した行データが条件に合致するか判定する。
         *
         * @param row 行データ
         * @return 合致した場合、真
         */
        boolean matches(Map<String, String> row);

        /**
         * 常にマッチしない検索条件。
         * シート終端まで読み取る場合の終了条件に使用する。
         */
        Condition NEVER_MATCH = new Condition() {
            public boolean matches(Map<String, String> row) {
                return false;
            }
        };

        /**
         * 常にマッチする検索条件。
         * 全件取得する際の検索条件に使用する。
         */
        Condition ALWAYS_MATCH = new Condition() {
            public boolean matches(Map<String, String> row) {
                return true;
            }
        };

        /**
         * 空行にマッチする検索条件
         */
        Condition EMPTY_ROW = new Condition() {
            public boolean matches(Map<String, String> row) {
                // 全要素が空であれば空行とみなす。
                return StringUtil.isNullOrEmpty(row.values());
            }
        };
    }
}
