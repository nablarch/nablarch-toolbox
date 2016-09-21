package nablarch.tool.published.doclet.testfiles;

import nablarch.core.util.annotation.Published;

/**
 * Publshedなメソッドがあるのでクラスのコメントも表示される。
 *
 * @author T.Kawasaki
 */
public class MySubClass extends MySuperClass {

    /** 表示される。 */
    @Published
    public void additionalMethod() {
    }

    /** 表示されない */
    public void unpublishedMethod() {
    }
}
