package nablarch.tool.published.doclet.testfiles;

import nablarch.core.util.annotation.Published;

/**
 * インタフェースのテスト。表示される。
 *
 * @author T.Kawasaki
 */
public interface MyInterface {

    /** 表示される。 */
    @Published
    void publishedMethod();

    /** 表示されない。 */
    void unpublished();

    /** ネストクラスのメソッドがPublished(表示される)。 */
    public static class MyNestedImplementer implements MyInterface {

        /** ネストクラスのメソッドがPublished(表示される)。 */
        @Published
        public void publishedMethod() {
        }

        /** 表示されない。*/
        public void unpublished() {

        }
    }
}
