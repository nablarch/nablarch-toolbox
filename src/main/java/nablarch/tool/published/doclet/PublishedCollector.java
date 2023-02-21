package nablarch.tool.published.doclet;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 公開APIを抽出するクラス。<br/>
 *
 * @author T.Kawasaki
 */
class PublishedCollector {

    /** 抽出対象とするタグ */
    private final Set<String> tagsToPublish;

    /**
     * コンストラクタ。
     *
     * @param tagsToPublish 抽出対象とするタグ
     */
    PublishedCollector(List<String> tagsToPublish) {
        this.tagsToPublish = new HashSet<>(tagsToPublish);
    }

    /**
     * ある文書要素にPublishedアノテーションが付与されているかどうか判定する。
     *
     * @param element 文書要素
     * @return Publishedアノテーションが付与されている場合、真
     */
    boolean hasPublishedAnnotation(Element element) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (isPublishedAnnotation(annotationMirror)) {
                return true;
            }
        }
        return false;
    }

    /**
     * アノテーションがPublishedアノテーションかどうか判定する。<br/>
     *
     * @param annotationMirror アノテーション
     * @return Publishedアノテーションの場合、真
     */
    boolean isPublishedAnnotation(AnnotationMirror annotationMirror) {
        final DeclaredType annotationType = annotationMirror.getAnnotationType();
        if (!annotationType.asElement().getSimpleName().contentEquals("Published")) {
            return false;
        }

        final Map<? extends ExecutableElement, ? extends AnnotationValue> values = annotationMirror.getElementValues();

        boolean hasNoTag = values.size() == 0;
        // 何のタグも指定されていない（デフォルト）か、もしくは、
        // 指定されたタグを保持しているか
        return hasNoTag || hasQualifiedTag(values);
    }

    /**
     * ドックレットのパラメータで指定されたタグを保持しているか
     *
     * @param annotationValues アノテーションとその値のペア
     * @return 指定されたタグを保持している場合、真
     */
    boolean hasQualifiedTag(Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues) {
        // アノテーション内の全を
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationValues.entrySet()) {
            final ExecutableElement key = entry.getKey();
            if (key.getSimpleName().contentEquals("tag")) {
                AnnotationValue annotationValue = entry.getValue();
                @SuppressWarnings("unchecked")
                List<AnnotationValue> values = (List<AnnotationValue>) annotationValue.getValue();
                for (AnnotationValue value : values) {
                    String tag = value.getValue().toString();
                    if (tagsToPublish.contains(tag)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
