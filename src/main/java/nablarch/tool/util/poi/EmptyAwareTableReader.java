package nablarch.tool.util.poi;

import nablarch.core.util.StringUtil;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

/**
 * 数式の結果を判定しつつ表を読み込むクラス。
 * <p/>
 * {@link SimpleTableReader}との違いは以下の通り。<br/>
 * {@link SimpleTableReader}は、{@link org.apache.poi.hssf.usermodel.HSSFRow#getLastCellNum()}で最終列を
 * 求めるのに対し、本クラスは、空白セルが出現する直前のセルを最終列とみなす。
 *
 * 例えば、以下の様なデータがあった場合、"ccc"のセルが最終列とみなされる。
 * （{@link SimpleTableReader}の場合は、"eee"のセルが最終列）
 * <pre>
 * +-----+-----+-----+-----+-----+
 * |aaa  |bbb  |ccc  |     |eee  |
 * +-----+-----+-----+-----+-----+
 * </pre>
 *
 * @author T.Kawasaki
 *
 */
public class EmptyAwareTableReader extends SimpleTableReader {


    /** {@inheritDoc} */
    @Override
    protected int getLastCellNum(HSSFRow row) {
        return getLastCellNumHavingValue(row);
    }


    /**
     * 与えられた行のなかで、セル内の数式を評価した結果、
     * 値を保持している最後尾のセルの列番号を取得する。<br/>
     * 数式を評価した結果、空文字の場合は値を保持していないと見なす。
     *
     * @param row 検索対象となる行
     * @return 最後尾セルの列番号
     */
    private int getLastCellNumHavingValue(HSSFRow row) {
        int cellIdx = startColumn;
        for (; cellIdx < endColumn; cellIdx++) {
            HSSFCell cell = row.getCell(cellIdx);
            String cellValue = PoiUtil.toString(cell);
            if (StringUtil.isNullOrEmpty(cellValue)) {
                break;
            }
        }
        return cellIdx;

    }
}
