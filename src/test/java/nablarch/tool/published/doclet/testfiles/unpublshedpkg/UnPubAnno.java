package nablarch.tool.published.doclet.testfiles.unpublshedpkg;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Published なし。
 * 
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target({ ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR,
        ElementType.FIELD, ElementType.METHOD, ElementType.TYPE })
public @interface UnPubAnno {

}
