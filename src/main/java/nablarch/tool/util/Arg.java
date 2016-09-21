package nablarch.tool.util;

/** 引数の簡易精査クラス */
public final class Arg {
    /**
     * 引数がnullでないことをチェックする。
     *
     * @param object 引数
     * @param <T>    引数の型
     * @return その引数自身
     */
    public static <T> T notNull(T object) {
        return notNull(object, "argument");
    }

    /**
     * 引数がnullでないことをチェックする。
     *
     * @param object 引数
     * @param name   その引数の名前
     * @param <T>    引数の型
     * @return その引数自身
     */
    public static <T> T notNull(T object, String name) {
        if (object == null) {
            throw new IllegalArgumentException(name + " must not be null.");
        }
        return object;
    }

    /**
     * 隠蔽コンストラクタ
     */
    private Arg() {
    }
}