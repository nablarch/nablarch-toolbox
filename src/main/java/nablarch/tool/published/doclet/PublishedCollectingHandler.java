package nablarch.tool.published.doclet;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;

/**
 * 公開APIのみを抽出してAPIドキュメントを抽出する
 * {@link InvocationHandler}実装クラス。
 *
 * @author T.Kawasaki
 */
class PublishedCollectingHandler implements InvocationHandler {

    /** ラップしたオブジェクト */
    private final Object wrapped;

    /** 公開APIを抽出するクラス */
    private final PublishedCollector publishedCollector;
    
    /** 文書に含まれるか否か */
    private boolean forceNotIncluded;

    /**
     * 指定されたオブジェクトを本クラスでラップする。<br/>
     * {@link com.sun.javadoc.Doclet#start(com.sun.javadoc.RootDoc)}メソッドで処理される
     * RoodDoc及びその要素を動的プロキシでラップすることで、
     * 出力対象の制限を実現する。
     *
     * @param obj ラップされるオブジェクト
     * @param publishedDetector 公開API抽出クラス
     * @param included 要素が出力される場合、true
     * @return ラップ後のオブジェクト
     */
    static Object wrap(Object obj, PublishedCollector publishedDetector, boolean included) {
        Class<?> clazz = obj.getClass();
        return Proxy.newProxyInstance(clazz.getClassLoader(),
                                      clazz.getInterfaces(),
                                      new PublishedCollectingHandler(publishedDetector, obj, included));
    }

    /**
     * コンストラクタ。
     * 
     * @param publishedCollector 公開API抽出クラス
     * @param wrapped ラップされるオブジェクト
     * @param forceNotIncluded included メソッドの結果を常に false にする場合 true 。
     */
    public PublishedCollectingHandler(PublishedCollector publishedCollector,
            Object wrapped, boolean forceNotIncluded) {
        super();
        this.wrapped = wrapped;
        this.publishedCollector = publishedCollector;
        this.forceNotIncluded = forceNotIncluded;
    }


    /**
     * ラップしたオブジェクトを取り出すメソッド一覧。
     * これらのメソッドがinvokeされた場合、{@link #unwrap(Object)}を呼び出す。
     */
    private static final Set<String> UNWRAP_METHODS = new HashSet<String>() {
        {
            add("compareTo");
            add("equals");
            add("overrides");
            add("subclassOf");
        }
    };

    /** {@inheritDoc} */
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {

        if (method.getName().equals("isIncluded")) {
            if (forceNotIncluded) {
                // 強制的に false を返す場合はそのまま返し、
                // そうでなければ、特に何もせず元のメソッドの結果を返す。
                return false;
            }
        }

        if (UNWRAP_METHODS.contains(method.getName())) {
            args[0] = unwrap(args[0]);
        }
        Object ret = method.invoke(wrapped, args);
        Object collected = publishedCollector.collect(ret, method.getReturnType());
        
        return convertValidType(method.getName(), ret, collected);
    }

    /**
     * 戻り値が array の場合、メソッド名から戻り値を予測して配列に変換する。<br />
     * (引数の型がList以外であれば、値をそのまま返す。)
     * 
     * @param name メソッド名
     * @param original 元のオブジェクト
     * @param collected 戻り値の値
     * @return 変換後の値
     */
    private Object convertValidType(String name, Object original, Object collected) {
        if (original == null) {
            return null;
        }
        if (original.getClass().isArray() && (collected instanceof List)) {
            
            String lower = name.toLowerCase();

            if (lower.endsWith("annotationtypes")) {
                return toArray((List<?>) collected, AnnotationTypeDoc.class);
            } else if (lower.endsWith("classes")) {
                return toArray((List<?>) collected, ClassDoc.class);
            } else if (lower.endsWith("interfaces")) {
                return toArray((List<?>) collected, ClassDoc.class);
            } else if (lower.endsWith("enums")) {
                return toArray((List<?>) collected, ClassDoc.class);
            } else if (lower.endsWith("exceptions")) {
                return toArray((List<?>) collected, ClassDoc.class);
            } else if (lower.endsWith("errors")) {
                return toArray((List<?>) collected, ClassDoc.class);
            } else if (lower.endsWith("packages")) {
                return toArray((List<?>) collected, PackageDoc.class);
            } else if (lower.endsWith("enumconstants")) {
                return toArray((List<?>) collected, FieldDoc.class);
            } else if (lower.endsWith("fields")) {
                return toArray((List<?>) collected, FieldDoc.class);
            } else if (lower.endsWith("constructors")) {
                return toArray((List<?>) collected, ConstructorDoc.class);
            } else if (lower.endsWith("methods")) {
                return toArray((List<?>) collected, MethodDoc.class);
            } else if (original.getClass().isArray()
                    && original.getClass().getComponentType().getName().startsWith("com.sun.javadoc")) {
                // 上記以外は original.getClass().getComponentType() で判別可能なはず（か？）
                return toArray((List<?>) collected , original.getClass().getComponentType());
            } else {
                return original;
            }
        } else {
            return collected;
        }
    }


    /**
     * リストを配列に変換する。
     *
     * @param list リスト
     * @param type 配列の型
     * @return 変換後の配列
     */
    private static Object[] toArray(List<?> list, Class<?> type) {
        return list.toArray((Object[]) Array.newInstance(type, list.size()));
    }

    /**
     * ラップしたオブジェクトを取り出す。<br/>
     * 引数がプロキシでない場合、引数をそのまま返却する。
     *
     * @param obj プロキシ
     * @return 元のオブジェクト
     */
    private Object unwrap(Object obj) {
        if (obj instanceof Proxy) {
            PublishedCollectingHandler handler = (PublishedCollectingHandler) Proxy.getInvocationHandler(obj);
            return handler.wrapped;
        }
        return obj;
    }
}
