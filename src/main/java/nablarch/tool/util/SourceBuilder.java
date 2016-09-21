package nablarch.tool.util;

import java.util.ArrayList;
import java.util.List;

import nablarch.core.util.Builder;

/**
 * ソースコードを組み立てる為のクラス。
 *
 * @author T.Kawasaki
 */
public class SourceBuilder {

    /**
     * デフォルトのインデント幅（半角スペース数）
     */
    private static final int DEFAULT_SIZE_OF_TAB_SPACE = 2;

    /** インデント文字列 */
    private final String indent;

    /** インデントレベル（初期値は1） */
    private int indentLevel = 1;

    /** 作成したコードのバッファ(1行分） */
    private final StringBuilder buf = new StringBuilder(256);

    /** 改行コード */
    private final String ls;

    /** 作成したコードのバッファ（全行分） */
    private final List<String> lines = new ArrayList<String>();

    /** 現在、行頭を処理中か */
    private boolean head = true;

    /** デフォルトコンストラクタ */
    public SourceBuilder() {
        this(LineSeparator.CRLF.code);
    }

    /**
     * コンストラクタ。
     * <p/>
     * インデント幅はデフォルト値を利用する。
     *
     * @param ls 改行コード
     */
    public SourceBuilder(String ls) {
        this(DEFAULT_SIZE_OF_TAB_SPACE, ls);
    }

    /**
     * インデント幅・改行コードを指定するコンストラクタ。
     *
     * @param sizeOfTabSpace インデント幅
     * @param ls 改行コード
     */
    public SourceBuilder(int sizeOfTabSpace, String ls) {
        this(getTabSpace(sizeOfTabSpace), ls);
    }

    /**
     * インデント幅に対応するインデント文字列（インデント幅分の半角スペース）を返却する。
     *
     * @param size インデント幅
     * @return インデント幅に対応するインデント文字列
     */
    private static String getTabSpace(int size) {
        StringBuilder tabSpace = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            tabSpace.append(' ');
        }
        return tabSpace.toString();
    }

    /**
     * コンストラクタ。
     *
     * @param indent インデント文字列
     * @param ls     改行コード
     */
    public SourceBuilder(String indent, String ls) {
        this.indent = indent;
        this.ls = ls;
    }

    /**
     * 1行分のソースを追加する。
     *
     * @param elements ソース1行を構成する要素群
     * @return 本インスタンス
     */
    public SourceBuilder println(Object... elements) {
        print(elements);
        return println();
    }

    /**
     * ソースを追加する。
     *
     * @param elements ソースを構成する要素群
     * @return 本インスタンス
     */
    public SourceBuilder print(Object... elements) {
        for (Object e : elements) {
            doPrint(e);
        }
        return this;
    }

    /**
     * バッファに要素を追加する。
     *
     * @param e 要素
     */
    private void doPrint(Object e) {
        if (head) {
            appendIndent();
            head = false;
        }
        buf.append(String.valueOf(e));
    }

    /**
     * 改行を追加する。
     *
     * @return 本インスタンス
     */
    public SourceBuilder println() {
        lines.add(buf.toString());
        buf.setLength(0);
        head = true;
        return this;
    }

    /**
     * 1行分のソースを追加し、インデントレベルを1レベル下げる。
     *
     * @param elements ソース1行を構成する要素群
     * @return 本インスタンス
     */
    public SourceBuilder printlnWithDeIndent(Object... elements) {
        print(elements);
        return deIndent();
    }

    /**
     * インデントを1レベル上げる。
     *
     * @return 本インスタンス。
     */
    public SourceBuilder indent() {
        if (!head) {
            throw new IllegalStateException(
                    "you can add indent only when in line head."
            );
        }
        indentLevel++;
        return this;
    }

    /**
     * インデントを１レベル下げる。
     *
     * @return 本インスタンス
     */
    private SourceBuilder deIndent() {
        indentLevel--;
        return println();
    }

    /**
     * 文字列表現を得る。<br/>
     * 今まで追加したソースコードが返却される。
     *
     * @return ソースコード
     */
    public String toString() {
        return Builder.join(lines, ls);
    }

    /**
     * インデントを左端にリセットする。
     *
     * @return 本インスタンス
     */
    public SourceBuilder resetIndent() {
        indentLevel = 0;
        return this;
    }

    /**
     * インデント文字列を追加する。
     *
     * @return 本インスタンス
     */
    private SourceBuilder appendIndent() {
        for (int i = 0; i < indentLevel; i++) {
            buf.append(indent);
        }
        return this;
    }
}
