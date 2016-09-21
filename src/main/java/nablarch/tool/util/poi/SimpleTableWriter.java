package nablarch.tool.util.poi;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 単純な書き込みを行うクラス。
 * List-Mapの先頭行のkeySetを見出し行として書き込む。
 * ２行目以降には見出しに対応するvalueが書き込まれる。
 *
 * 本クラスではメモリ上の書き込み操作のみを行う。
 * ファイルへの永続化が必要な場合は、別途実施すること。
 *
 * @author T.Kawasaki
 */
public class SimpleTableWriter {

    /** 書き込み対象シート */
    private final HSSFSheet targetSheet;

    /** ヘッダ行の位置 */
    private final int headerIndex;

    /**
     * コンストラクタ。
     *
     * @param targetSheet 書き込み対象シート
     * @param headerIndex ヘッダ行の位置
     */
    public SimpleTableWriter(HSSFSheet targetSheet, int headerIndex) {
        this.targetSheet = targetSheet;
        this.headerIndex = headerIndex;
    }

    /**
     * 書き込みを行う。
     *
     * @param lines 書き込むデータ（全行）
     */
    public void write(List<Map<String, String>> lines) {
        if (lines.isEmpty()) {
            return;
        }
        Set<String> headerColumns = lines.get(0).keySet();
        writeHeader(headerColumns);
        int rowIndex = headerIndex + 1;

        for (Map<String, String> e : lines) { // 行ループ
            HSSFRow rowToWrite = targetSheet.getRow(rowIndex++);
            Iterator<String> itr = headerColumns.iterator();
            for (int j = 0; j < headerColumns.size(); j++) { // 列ループ
                HSSFCell cellToWrite = rowToWrite.getCell(j);
                String headCol = itr.next();
                cellToWrite.setCellValue(e.get(headCol));
            }
        }
    }

    /**
     * ヘッダを書き込む。
     *
     * @param headerColumns ヘッダカラム一覧
     */
    private void writeHeader(Set<String> headerColumns) {
        HSSFRow headerRow = targetSheet.getRow(headerIndex);
        int colIdx = 0;
        for (String e : headerColumns) {
            HSSFCell cell = headerRow.getCell(colIdx++);
            cell.setCellValue(e);
        }
    }
}
