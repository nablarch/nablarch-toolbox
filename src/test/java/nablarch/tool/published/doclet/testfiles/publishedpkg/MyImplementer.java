package nablarch.tool.published.doclet.testfiles.publishedpkg;


import nablarch.tool.published.doclet.testfiles.MyInterface;

/**
 * 別パッケージの実装クラスのテスト。インタフェースはPublishedだが、実装クラスは表示されない。
 *
 * @author T.Kawasaki
 */
public class MyImplementer implements MyInterface {

    /** 表示されない。*/
    public void publishedMethod() {
    }

    /** 表示されない。 */
    public void unpublished() {
    }
}
