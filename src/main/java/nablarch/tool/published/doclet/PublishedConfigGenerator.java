package nablarch.tool.published.doclet;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;

import nablarch.core.util.Builder;
import nablarch.core.util.FileUtil;

/**
 * Nablarchの提供する「使用不許可APIチェックツール」で利用する設定ファイル（Nablarchで利用可能としているAPIのホワイトリスト）を作成する。
 *
 * @author Ryo TANAKA
 */
public class PublishedConfigGenerator {

    /**
     * Publishedアノテーションが付与されたクラス・メソッドかどうかの判定に利用するクラス。
     */
    private PublishedCollector collector;

    /**
     * 出力対象とするPublishedCollectorを指定してインスタンスを生成する。
     *
     * @param collector 出力対象とするPublishedアノテーションのタグ。
     */
    public PublishedConfigGenerator(PublishedCollector collector) {
        this.collector = collector;
    }

    /**
     * 「使用不許可APIチェックツール」で利用する設定ファイルを生成する。設定は{@code path} に指定されたファイルに出力される。
     * <p/>
     *
     * @param rootDoc プログラム構造情報のルート
     * @param path 「使用不許可APIチェックツール」用設定ファイルの出力先パス
     */
    public void generate(RootDoc rootDoc, String path) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path));
            for (ClassDoc classDoc : rootDoc.classes()) {
                List<String> apis = generateForClassElements(classDoc);
                if (!apis.isEmpty()) {
                    writer.write(Builder.join(apis, Builder.LS));
                    writer.write(Builder.LS);
                    writer.flush();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file. file=[" + path + "]");
        } finally {
            FileUtil.closeQuietly(writer);
        }
    }

    /**
     * 指定されたクラスについて、指定されたタグを持つか、あるいはタグを持たないPublishedアノテーションを付与された
     * クラス・コンストラクタ・メソッド・フィールド（内部クラスを含む）の一覧を返却する。
     *
     * @param classDoc クラスの構造情報
     * @return クラス内で、指定されたタグを持つかあるいはタグを持たないPublishedアノテーションを付与された一覧
     */
    List<String> generateForClassElements(ClassDoc classDoc) {
        List<String> result = new ArrayList<String>();
        if (collector.hasPublishedAnnotation(classDoc)) {
            result.add(classDoc.qualifiedTypeName());
        } else {
            for (ConstructorDoc constructor : classDoc.constructors()) {
                if (collector.hasPublishedAnnotation(constructor)) {
                    result.add(constructor.qualifiedName() + "." + constructor.name() + deleteGenerics(constructor.signature()));
                }
            }

            for (MethodDoc method : classDoc.methods()) {
                if (collector.hasPublishedAnnotation(method)) {
                    result.add(method.qualifiedName() + deleteGenerics(method.signature()));
                }
            }

            for (FieldDoc field : classDoc.fields()) {
                if (collector.hasPublishedAnnotation(field)) {
                    result.add(field.qualifiedName());
                }
            }

            for (ClassDoc innerClazz : classDoc.innerClasses()) {
                result.addAll(generateForClassElements(innerClazz));
            }
        }
        return result;
    }

    /**
     * 引数に渡されたシグネチャにGenericsが使用されている場合、Genericsを削除して返却する。</br>
     * Genericsが使用されていない場合は、修正せずに返却する。</br>
     * Genericsを使用している設定ファイルを使用した場合、使用禁止APIチェックツールが誤検出を行うための対処である。
     *
     * @param signature シグネチャ
     * @return 引数に渡されたシグネチャからGenericsを削除した文字列
     */
    private static String deleteGenerics(String signature) {

        if ("".equals(signature) || "()".equals(signature)) {
            return signature;
        }

        int bracketCount = 0;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < signature.length(); i++) {
            char ch = signature.charAt(i);
            switch (ch) {
                case '<':
                    bracketCount++;
                    break;
                case '>':
                    bracketCount--;
                    break;
                default:
                    if (bracketCount == 0) {
                        result.append(ch);
                    }
            }
        }
        return genericToObject(result.toString());
    }

    /**
     * 型パラメータを抜き出す正規表現
     */
    private static final Pattern PARAM_TYPE_PATTERN = Pattern.compile(
        "([\\(, ])[A-Z][A-Za-z0-9]*");

    /**
     * 指定されたシグネチャに総称型が使われていた場合、「java.lang.Object」に変換する。
     *
     * @param signature シグネチャ
     * @return 変換後のシグネチャ
     */
    private static String genericToObject(String signature) {
        if ("".equals(signature) || "()".equals(signature)) {
            return signature;
        }
        Matcher matcher = PARAM_TYPE_PATTERN.matcher(signature);
        while (matcher.find()) {
            signature = matcher.replaceFirst(matcher.group(1) + "java.lang.Object");
            matcher = PARAM_TYPE_PATTERN.matcher(signature);
        }
        return signature;
    }
}
