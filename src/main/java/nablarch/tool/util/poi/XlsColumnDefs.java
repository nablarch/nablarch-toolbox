package nablarch.tool.util.poi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFRow;

/**
 * Excelのカラム定義を表すクラス。 何列目が何のカラムであるかという定義情報を保持する。
 * この定義情報に実際の行データ（{@link HSSFRow}）を入力することで、
 * 定義に沿ったデータが出力される。
 *
 * @author T.Kawasaki
 */
public class XlsColumnDefs {

    /** カラムインデックス(0オリジン)とカラム名のペア */
    private Map<Integer, String> indexNamePairs;

    /**
     * 本インスタンスの定義情報に沿ってデータ行からデータを収集する。
     *
     * @param row 行
     * @return 結果（カラムのインデックス順に順序付けされている）
     */
    LinkedHashMap<String, String> collect(HSSFRow row) {
        LinkedHashMap<String, String> ret = new LinkedHashMap<String, String>(indexNamePairs.size());
        for (Map.Entry<Integer, String> entry : indexNamePairs.entrySet()) {
            Integer columnIndex = entry.getKey();
            String columnValue = PoiUtil.toString(row, columnIndex);
            String columnName = entry.getValue();
            ret.put(columnName, columnValue);
        }
        return ret;
    }

    /**
     *
     * @param indexNamePairs カラムインデックス(0オリジン)とカラム名のペア
     */
    public void setIndexNamePairs(Map<String, String> indexNamePairs) {
        this.indexNamePairs = convertKeyType(indexNamePairs);
    }

    /**
     * キーの型をStringからIntegerに変換する。
     * (DiContainerから取得したMapは{@code Map<String,String>}であるため）
     *
     * @param orig 元データ
     * @return 変換後データ
     */
    private static Map<Integer, String> convertKeyType(Map<String, String> orig) {
        Map<Integer, String> converted = new TreeMap<Integer, String>();    // index昇順でソートするため
        for (Entry<String, String> entry : orig.entrySet()) {
            converted.put(Integer.parseInt(entry.getKey()),
                          entry.getValue());
        }
        return converted;
    }

    @Override
    public String toString() {
        return String.valueOf(indexNamePairs);
    }
}
