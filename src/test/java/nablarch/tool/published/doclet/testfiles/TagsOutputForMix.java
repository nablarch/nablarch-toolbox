package nablarch.tool.published.doclet.testfiles;

import nablarch.core.util.annotation.Published;

/**
 * タグの動作確認。<br />
 * テストで出力されたJavadocで、以下のリンクの状態（有効・無効）と、<br />
 * 有効ならばリンク先が存在していることを確認すること。<br />
 * <br />
 * Publishedアノテーションがついた要素とついていない要素が一つのクラスに混在している<br />
 * 場合のリンクの動作を確認する。<br/>
 * <br />
 * クラスがPublishedなものの確認<br />
 * package 表示される {@link nablarch.tool.published.doclet.testfiles.publishedOnlyClass}<br />
 * class 表示される {@link nablarch.tool.published.doclet.testfiles.publishedOnlyClass.PublishedTestClass}<br />
 * field 表示される {@link nablarch.tool.published.doclet.testfiles.publishedOnlyClass.PublishedTestClass#testField}<br />
 * method メソッド 表示される {@link nablarch.tool.published.doclet.testfiles.publishedOnlyClass.PublishedTestClass#testMethodPublished(String)}<br />
 * <br />
 * フィールドがPublishedなものの確認<br />
 * package 表示される {@link nablarch.tool.published.doclet.testfiles.publishedOnlyField}<br />
 * class 表示される {@link nablarch.tool.published.doclet.testfiles.publishedOnlyField.TestClassHavingPublishedField}<br />
 * field 表示される {@link nablarch.tool.published.doclet.testfiles.publishedOnlyField.TestClassHavingPublishedField#testFieldPublished}<br />
 * field 表示されない {@link nablarch.tool.published.doclet.testfiles.publishedOnlyField.TestClassHavingPublishedField#testFieldUnPublished}<br />
 * method 表示されない {@link nablarch.tool.published.doclet.testfiles.publishedOnlyField.TestClassHavingPublishedField#testMethodUnPublished()}<br />
 * <br />
 * メソッドがPublishedなものの確認<br />
 * package 表示される {@link nablarch.tool.published.doclet.testfiles.publishedOnlyMethod}<br />
 * class 表示される {@link nablarch.tool.published.doclet.testfiles.publishedOnlyMethod.TestClassHavingPublishedMethod}<br />
 * field 表示されない {@link nablarch.tool.published.doclet.testfiles.publishedOnlyMethod.TestClassHavingPublishedMethod#testFieldUnPublished}<br />
 * method 表示される {@link nablarch.tool.published.doclet.testfiles.publishedOnlyMethod.TestClassHavingPublishedMethod#testMethodPublished()}<br />
 * method 表示されない {@link nablarch.tool.published.doclet.testfiles.publishedOnlyMethod.TestClassHavingPublishedMethod#testMethodUnPublished()}<br />
 * <br />
 * 
 * @author Kenta Morimoto
 *
 *
 */
@Published
public class TagsOutputForMix {

    /**
    * タグの動作確認(メソッド内)。<br />
    * クラスのJavadocコメント部分と同じ動作をしていることを確認する。<br />
    * <br />
    * クラスがPublishedなものの確認<br />
    * package 表示される {@link nablarch.tool.published.doclet.testfiles.publishedOnlyClass}<br />
    * class 表示される {@link nablarch.tool.published.doclet.testfiles.publishedOnlyClass.PublishedTestClass}<br />
    * field 表示される {@link nablarch.tool.published.doclet.testfiles.publishedOnlyClass.PublishedTestClass#testField}<br />
    * method メソッド 表示されない {@link nablarch.tool.published.doclet.testfiles.publishedOnlyClass.PublishedTestClass#testMethodPublished(String)}<br />
    * <br />
    * フィールドがPublishedなものの確認<br />
    * package 表示される {@link nablarch.tool.published.doclet.testfiles.publishedOnlyField}<br />
    * class 表示される {@link nablarch.tool.published.doclet.testfiles.publishedOnlyField.TestClassHavingPublishedField}<br />
    * field 表示される {@link nablarch.tool.published.doclet.testfiles.publishedOnlyField.TestClassHavingPublishedField#testFieldPublished}<br />
    * field 表示されない {@link nablarch.tool.published.doclet.testfiles.publishedOnlyField.TestClassHavingPublishedField#testFieldUnPublished}<br />
    * method 表示されない {@link nablarch.tool.published.doclet.testfiles.publishedOnlyField.TestClassHavingPublishedField#testMethodUnPublished()}<br />
    * <br />
    * メソッドがPublishedなものの確認<br />
    * package 表示される {@link nablarch.tool.published.doclet.testfiles.publishedOnlyMethod}<br />
    * class 表示される {@link nablarch.tool.published.doclet.testfiles.publishedOnlyMethod.TestClassHavingPublishedMethod}<br />
    * field 表示されない {@link nablarch.tool.published.doclet.testfiles.publishedOnlyMethod.TestClassHavingPublishedMethod#testFieldUnPublished}<br />
    * method 表示される {@link nablarch.tool.published.doclet.testfiles.publishedOnlyMethod.TestClassHavingPublishedMethod#testMethodPublished()}<br />
    * method 表示されない {@link nablarch.tool.published.doclet.testfiles.publishedOnlyMethod.TestClassHavingPublishedMethod#testMethodUnPublished()}<br />
    * <br />
     */
    public void hoge() {
        
    }
}
