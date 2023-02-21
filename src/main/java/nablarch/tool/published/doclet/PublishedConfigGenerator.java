package nablarch.tool.published.doclet;

import jdk.javadoc.doclet.DocletEnvironment;
import nablarch.core.util.Builder;
import nablarch.core.util.FileUtil;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Nablarchの提供する「使用不許可APIチェックツール」で利用する設定ファイル（Nablarchで利用可能としているAPIのホワイトリスト）を作成する。
 *
 * @author Ryo TANAKA
 */
public class PublishedConfigGenerator {

    /**
     * Publishedアノテーションが付与されたクラス・メソッドかどうかの判定に利用するクラス。
     */
    private final PublishedCollector collector;

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
    public void generate(DocletEnvironment rootDoc, String path) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path));

            Set<TypeElement> typeElements = ElementFilter.typesIn(rootDoc.getIncludedElements());

            for (TypeElement typeElement : typeElements) {
                List<String> apis = generateForClassElements(typeElement);
                if (!apis.isEmpty()) {
                    writer.write(Builder.join(apis, Builder.LS));
                    writer.write(Builder.LS);
                    writer.flush();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file. file=[" + path + "]", e);
        } finally {
            FileUtil.closeQuietly(writer);
        }
    }

    /**
     * 指定されたクラスについて、指定されたタグを持つか、あるいはタグを持たないPublishedアノテーションを付与された
     * クラス・コンストラクタ・メソッド・フィールド（内部クラスを含む）の一覧を返却する。
     *
     * @param typeElement クラスの構造情報
     * @return クラス内で、指定されたタグを持つかあるいはタグを持たないPublishedアノテーションを付与された一覧
     */
    List<String> generateForClassElements(TypeElement typeElement) {
        List<String> result = new ArrayList<>();
        if (collector.hasPublishedAnnotation(typeElement)) {
            result.add(typeElement.getQualifiedName().toString());
        } else {
            Name simpleClassName = typeElement.getSimpleName();
            Name qualifiedClassName = typeElement.getQualifiedName();
            
            for (ExecutableElement constructor : obtainConstructorElement(typeElement)) {
                if (collector.hasPublishedAnnotation(constructor)) {
                    result.add(qualifiedClassName + "." + simpleClassName + deleteGenerics(buildMethodArgsSignature(constructor)));
                }
            }

            for (ExecutableElement method : obtainMethodElement(typeElement)) {
                if (collector.hasPublishedAnnotation(method)) {
                    Name methodName = method.getSimpleName();
                    result.add(qualifiedClassName + "." + methodName + deleteGenerics(buildMethodArgsSignature(method)));
                }
            }

            for (VariableElement field : obtainFieldElement(typeElement)) {
                if (collector.hasPublishedAnnotation(field)) {
                    result.add(qualifiedClassName + "." + field.getSimpleName());
                }
            }

            for (TypeElement innerClazz : obtainInnerClassElement(typeElement)) {
                result.addAll(generateForClassElements(innerClazz));
            }
        }
        return result;
    }

    /**
     * 指定された型要素に含まれるコンストラクタ要素を取得する。
     * @param typeElement 型要素
     * @return コンストラクタ要素
     */
    private List<ExecutableElement> obtainConstructorElement(TypeElement typeElement) {
        return ElementFilter.constructorsIn(typeElement.getEnclosedElements());
    }

    /**
     * 指定された型要素に含まれるメソッド要素を取得する。
     * @param typeElement 型要素
     * @return メソッド要素
     */
    private List<ExecutableElement> obtainMethodElement(TypeElement typeElement) {
        return ElementFilter.methodsIn(typeElement.getEnclosedElements());
    }

    /**
     * 指定された型要素に含まれるフィールド要素を取得する。
     * @param typeElement 型要素
     * @return フィールド要素
     */
    private List<VariableElement> obtainFieldElement(TypeElement typeElement) {
        return ElementFilter.fieldsIn(typeElement.getEnclosedElements());
    }

    /**
     * 指定された型要素に含まれる内部クラスの型要素を取得する。
     * @param typeElement 型要素
     * @return 内部クラスの型要素
     */
    private List<TypeElement> obtainInnerClassElement(TypeElement typeElement) {
        return ElementFilter.typesIn(typeElement.getEnclosedElements());
    }

    /**
     * メソッドやコンストラクタの引数のシグネチャ文字列を生成する。
     * @param element メソッドまたはコンストラクタ要素
     * @return 引数部分のシグネチャ
     */
    private String buildMethodArgsSignature(ExecutableElement element) {
        String parameters = element.getParameters().stream()
                .map(VariableElement::asType)
                .map(TypeMirror::toString)
                .collect(Collectors.joining(", "));
        return "(" + parameters + ")";
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
