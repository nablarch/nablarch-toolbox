package nablarch.tool.published.doclet.testfiles.publishedpkg;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import nablarch.core.util.annotation.Published;

/**
 * @Published 付。
 *
 */
@Published(tag = "architect")
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
public @interface PubAnno {

}
