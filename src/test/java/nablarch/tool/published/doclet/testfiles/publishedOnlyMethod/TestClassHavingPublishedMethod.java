package nablarch.tool.published.doclet.testfiles.publishedOnlyMethod;

import nablarch.core.util.annotation.Published;

/**
 * メソッドのみPublishedなクラスの確認
 * @author Kenta.Morimoto
 *
 */
public class TestClassHavingPublishedMethod {

    /** 表示されない */
    public String testFieldUnPublished;

    /** 表示される */
    @Published
    public void testMethodPublished() {

    }

    /** 表示されない */
    public void testMethodUnPublished() {

    }
}
