package nablarch.tool.util;

/** 改行コードを表す列挙型。 */
public enum LineSeparator {
    /** CR */
    CR("\r", "\\r"),
    /** LF */
    LF("\n", "\\n"),
    /** CRLF */
    CRLF("\r\n", "\\r\\n");

    /** 実際の改行コード*/
    public final String code;
    /** 改行コードにマッチする正規表現 */
    public final String regExp;

    /**
     * コンストラクタ
     * @param code 改行コード
     * @param regExp 改行コードにマッチする正規表現
     */
    LineSeparator(String code, String regExp) {
        this.code = code;
        this.regExp = regExp;
    }

    /**
     * 与えられた文字列が改行コードに合致するか判定する。
     *
     * @param code 任意の文字列
     * @return そのインスタンスが表す改行コードに合致する場合、真
     */
    boolean matches(String code) {
        return this.code.equals(code);
    }

    /**
     * 指定した値に合致するインスタンスを取得する。
     *
     * @param code 改行コード
     * @return 対応するインスタンス
     */
    static LineSeparator get(String code) {
        LineSeparator[] values = LineSeparator.values();
        for (LineSeparator e : values) {
            if (e.matches(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException(
                "invalid line separator. [" + code + "]");
    }

}
