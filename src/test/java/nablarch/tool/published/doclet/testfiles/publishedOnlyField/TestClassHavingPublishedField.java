package nablarch.tool.published.doclet.testfiles.publishedOnlyField;

import nablarch.core.util.annotation.Published;

/**
 * フィールドのみPublishedの確認
 * @author Kenta.Morimoto
 *
 */
public class TestClassHavingPublishedField {

    /**
     * 表示される
     */
    @Published
    public String testFieldPublished;

    /** 表示されない */
    public String testFieldUnPublished;

    /**
     * 表示されない
     */
    public void testMethodUnPublished() {

    }
}
