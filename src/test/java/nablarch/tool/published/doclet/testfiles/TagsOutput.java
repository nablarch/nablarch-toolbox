package nablarch.tool.published.doclet.testfiles;

import nablarch.core.util.annotation.Published;
import nablarch.tool.published.doclet.testfiles.unpublshedpkg.UnPubSub;

/**
 * タグの動作確認。<br />
 * テストで出力されたJavadocで、以下のリンクの状態（有効・無効）と、<br />
 * 有効ならばリンク先が存在していることを確認すること。<br />
 * <br />
 * プロジェクトで作成したクラスの確認をする。<br />
 * 含まれるものはリンクが張ってあること、指定したクラスに正しく飛べることを確認する。<br />
 * 含まれないものはリンクが張られていない事を確認する。<br />
 * <br />
 * 含まれる link テスト {@link nablarch.tool.published.doclet.testfiles.publishedpkg}。<br />
 * 含まれる linkplain テスト {@linkplain nablarch.tool.published.doclet.testfiles.publishedpkg} <br />
 * 含まれる link テスト {@link MySuperClass}。<br />
 * 含まれる linkplain テスト {@linkplain MySuperClass} <br />
 * 含まれる link テスト {@link MySuperClass#publishedMethod()}。<br />
 * 含まれる linkplain テスト {@linkplain MySuperClass#publishedMethod()} <br />
 * <br />
 * 含まれない link テスト {@link nablarch.tool.published.doclet.testfiles.unpublshedpkg}。<br />
 * 含まれない linkplain テスト {@linkplain nablarch.tool.published.doclet.testfiles.unpublshedpkg} <br />
 * 含まれない link テスト {@link UnPubSub}。<br />
 * 含まれない linkplain テスト {@linkplain UnPubSub} <br />
 * 含まれない link テスト {@link MySuperClass#unpublishedField}。<br />
 * 含まれない linkplain テスト {@linkplain MySuperClass#unpublishedField} <br />
 * <br />
 * 以下、公開クラス、公開メソッド、公開フィールドがパッケージ内にある場合、パッケージが表示されることを確認する。<br />
 * 公開クラスを含むパッケージが表示されることの確認 {@link nablarch.tool.published.doclet.testfiles.publishedOnlyClass} <br />
 * 公開メソッドを持つクラスのみを持つパッケージが表示されることの確認 {@link nablarch.tool.published.doclet.testfiles.publishedOnlyMethod} <br />
 * 公開フィールドを持つクラスのみを持つパッケージの表示されることの確認 {@link nablarch.tool.published.doclet.testfiles.publishedOnlyField} <br />
 * <br />
 * 外部ドキュメントへのリンクのテスト <br />
 * 外部ドキュメントへのリンクの動作を確認する。リンクの状態、リンク先が正しいことを確認する。<br />
 * <br />
 * プロジェクト中でImportしているものはリンクが張られる。<br />
 * リンク有 {@link java.lang}  <br />
 * リンク有 {@link java.lang.String}  <br />
 * リンク有 {@link java.lang.String#length()} <br />
 * <br />
 * プロジェクト中にImportしていないものはリンクが張られない。<br />
 * リンク無 {@link java.awt} <br />
 * リンク無 {@link java.awt.Checkbox} <br />
 * リンク無 {@link java.awt.Checkbox#action(java.awt.Event, Object)} <br />
 * <br />
 * <br />
 * 以下seeタグのテスト。それぞれのパッケージ、クラス、メソッド、フィールドに対して、<br />
 * linkタグと同じ動作になっている事を確認する。
 * 
 * @see nablarch.tool.published.doclet.testfiles.publishedpkg
 * @see MySuperClass
 * @see MySuperClass#publishedMethod()
 * @see nablarch.tool.published.doclet.testfiles.publishedOnlyClass
 * @see nablarch.tool.published.doclet.testfiles.publishedOnlyMethod
 * @see nablarch.tool.published.doclet.testfiles.publishedOnlyField
 * @see java.lang
 * @see java.lang.String
 * @see java.lang.String#length()
 *
 * @see nablarch.tool.published.doclet.testfiles.unpublshedpkg
 * @see UnPubSub
 * @see MySuperClass#unpublishedField
 * @see java.awt
 * @see java.awt.Checkbox
 * @see java.awt.Checkbox#action(java.awt.Event, Object)
 * 
 * @author Koichi Asano
 *
 *
 */
@Published
public class TagsOutput {

    /**
     * 以下、メソッド内のリンクテスト。<br />
     * クラスのJavadocコメント部分と同じ動作をしていることを確認する。<br />
     * <br />
     * 含まれる link テスト {@link nablarch.tool.published.doclet.testfiles.publishedpkg}。<br />
     * 含まれる linkplain テスト {@linkplain nablarch.tool.published.doclet.testfiles.publishedpkg} <br />
     * 含まれる link テスト {@link MySuperClass}。<br />
     * 含まれる linkplain テスト {@linkplain MySuperClass} <br />
     * 含まれる link テスト {@link MySuperClass#publishedMethod()}。<br />
     * 含まれる linkplain テスト {@linkplain MySuperClass#publishedMethod()} <br />
     * <br />
     * 含まれない link テスト {@link nablarch.tool.published.doclet.testfiles.unpublshedpkg}。<br />
     * 含まれない linkplain テスト {@linkplain nablarch.tool.published.doclet.testfiles.unpublshedpkg} <br />
     * 含まれない link テスト {@link UnPubSub}。<br />
     * 含まれない linkplain テスト {@linkplain UnPubSub} <br />
     * 含まれない link テスト {@link MySuperClass#unpublishedField}。<br />
     * 含まれない linkplain テスト {@linkplain MySuperClass#unpublishedField} <br />
     * <br />
     * 公開クラスを含むパッケージが表示されることの確認 {@link nablarch.tool.published.doclet.testfiles.publishedOnlyClass} <br />
     * 公開メソッドを持つクラスのみを持つパッケージが表示されることの確認 {@link nablarch.tool.published.doclet.testfiles.publishedOnlyMethod} <br />
     * 公開フィールドを持つクラスのみを持つパッケージの表示されることの確認 {@link nablarch.tool.published.doclet.testfiles.publishedOnlyField} <br />
     * <br />
     * 外部参照のリンクのテスト <br /> 
     * プロジェクト中でImportしているものはリンクが張られる。<br />
     * リンク有 {@link java.lang}  <br />
     * リンク有 {@link java.lang.String}  <br />
     * リンク有 {@link java.lang.String#length()} <br />
     * <br />
     * プロジェクト中にImportしていないものはリンクが張られない。<br />
     * リンク無 {@link java.awt} <br />
     * リンク無 {@link java.awt.Checkbox} <br />
     * リンク無 {@link java.awt.Checkbox#action(java.awt.Event, Object)} <br />
     * 
     * @see nablarch.tool.published.doclet.testfiles.publishedpkg
     * @see MySuperClass
     * @see MySuperClass#publishedMethod()
     * @see nablarch.tool.published.doclet.testfiles.publishedOnlyClass
     * @see nablarch.tool.published.doclet.testfiles.publishedOnlyMethod
     * @see nablarch.tool.published.doclet.testfiles.publishedOnlyField
     * @see java.lang
     * @see java.lang.String
     * @see java.lang.String#length()
     *
     * @see nablarch.tool.published.doclet.testfiles.unpublshedpkg
     * @see UnPubSub
     * @see MySuperClass#unpublishedField
     * @see java.awt
     * @see java.awt.Checkbox
     * @see java.awt.Checkbox#action(java.awt.Event, Object)
     */
    public void hoge() {
        
    }
}
