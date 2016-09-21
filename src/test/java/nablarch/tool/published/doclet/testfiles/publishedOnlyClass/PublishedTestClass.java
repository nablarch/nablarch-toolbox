package nablarch.tool.published.doclet.testfiles.publishedOnlyClass;

import nablarch.core.util.annotation.Published;

/**
 * Publishedなクラスの確認
 * @author Kenta.Morimoto
 *
 */
@Published
public class PublishedTestClass {

    /** 表示される*/
    public String testField;

    /** 表示される */
    public void testMethodPublished(String prop) {
    }
}
