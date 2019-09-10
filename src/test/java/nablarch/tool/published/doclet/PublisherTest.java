package nablarch.tool.published.doclet;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assume;
import org.junit.Test;

import com.sun.tools.javadoc.Main;

import nablarch.tool.published.doclet.testfiles.PublishedClass;

import static nablarch.tool.published.doclet.Util.prepare;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * テスト用の公開API出力クラス。<br />
 * テスト結果を確認する際は、テストで出力されたJavadoc(target/javadoc/)<br />
 * を確認すること。<br />
 * ドキュメントのリンクに関しては、以下のクラスのJavadocに記述している。<br />
 * {@link nablarch.tool.published.doclet.testfiles.TagsOutput}<br />
 * {@link nablarch.tool.published.doclet.testfiles.TagsOutputForMix}<br />
 * <br />
 * テスト用に以下のパッケージを読み込んでいる。<br />
 * nablarch.tool.published.doclet.testfiles<br />
 * nablarch.tool.published.doclet.testfiles.publishedpkg<br />
 * nablarch.tool.published.doclet.testfiles.unpublshedpkg<br />
 * nablarch.tool.published.doclet.testfiles.publishedOnlyClass<br />
 * nablarch.tool.published.doclet.testfiles.publishedOnlyField<br/>
 * nablarch.tool.published.doclet.testfiles.publishedOnlyMethod<br/>
 * <br />
 * また、外部ドキュメントへのリンクを張るのに必要なため、次のファイルを使用している。<br />
 * src/test/resources/nablarch/tool/published/package-list<br />
 * 
 * @author T.Kawasaki
 */
public class PublisherTest {

    /** ドックレットクラス名 */
    private static final String DOCLET_NAME = PublishedDoclet.class.getName();

    /** 出力先ディレクトリ */
    private static final File DEST_DIR = prepare("target/javadoc");

    /** パッケージ */
    private static final String PKG = PublishedClass.class.getPackage().getName();

    /** ドックレット引数。 */
    private final List<String> docletArgs = new ArrayList<String>(Arrays.asList(
            "-sourcepath", "src/test/java",
            "-d", DEST_DIR.getPath(),
            "-encoding", "UTF-8",
            "-charset", "UTF-8",
            "-protected",
            "-subpackages", PKG,
            "-linkoffline", "http://docs.oracle.com/javase/jp/7/api", "resources/nablarch/tool/published"
            //"-tag", "architect",
    ));

    /**
     * 公開APIを作成する。
     *
     *  補足：<br />
     *  本テストを実行する際は、Javaのバージョンと同じバージョンのtool.jarを使用しないと、実行できない。<br />
     *  しかしながら、mvnのコンパイルのフェーズと、mvnのテストフェーズで別々のtool.jarを指定する方法が不明なので、
     *  コンパイル時と同じJavaでユニットテストを(Java6でビルドする想定)のみ、このテストは実行する。<br />
     */
    @Test
    public void testPublish() {
        Assume.assumeTrue(new BigDecimal(System.getProperty("java.specification.version")).compareTo(new BigDecimal("1.6")) <= 0);
        int status = publish();
        assertThat(status, is(0));
    }

    /**
     * タグ付きの公開APIを作成する。
     *
     *  補足：<br />
     *  本テストを実行する際は、Javaのバージョンと同じバージョンのtool.jarを使用しないと、実行できない。<br />
     *  しかしながら、mvnのコンパイルのフェーズと、mvnのテストフェーズで別々のtool.jarを指定する方法が不明なので、
     *  コンパイル時と同じJavaでユニットテストを(Java6でビルドする想定)のみ、このテストは実行する。<br />
     */
    @Test
    public void testPublishTagged() {
        Assume.assumeTrue(new BigDecimal(System.getProperty("java.specification.version")).compareTo(new BigDecimal("1.6")) <= 0);
        int status = publishForArchitect();
        assertThat(status, is(0));
    }

    /**
     * outputオプションを指定せずに公開APIを作成する。
     *
     *  補足：<br />
     *  本テストを実行する際は、Javaのバージョンと同じバージョンのtool.jarを使用しないと、実行できない。<br />
     *  しかしながら、mvnのコンパイルのフェーズと、mvnのテストフェーズで別々のtool.jarを指定する方法が不明なので、
     *  コンパイル時と同じJavaでユニットテストを(Java6でビルドする想定)のみ、このテストは実行する。<br />
     */
    @Test
    public void testPublishWithoutOutput() {
        Assume.assumeTrue(new BigDecimal(System.getProperty("java.specification.version")).compareTo(new BigDecimal("1.6")) <= 0);
        int status = publishWithoutOutput();
        assertThat(status, is(1));
    }

    /**
     * メインメソッド。
     *
     * @param args 使用しない
     */
    public static void main(String[] args) {
        new PublisherTest().publish();
    }

    /**
     * デフォルトの公開範囲で公開する。
     *
     * @return ステータスコード
     */
    public int publish() {
        List<String> args = new ArrayList<String>();
        args.addAll(docletArgs);
        args.add("-d");
        args.add("./target/javadoc/publish");
        args.add("-classpath");
        args.add(System.getProperty("java.class.path"));
        args.add("-output");
        args.add(this.getClass().getSimpleName() + ".config");

        return publish(args);
    }

    /**
     * アーキテクト向けの公開範囲で公開する。
     *
     * @return ステータスコード
     */
    public int publishForArchitect() {
        List<String> args = new ArrayList<String>();
        args.addAll(docletArgs);
        args.add("-d");
        args.add("./target/javadoc/publishArchetect");
        args.add("-tag");
        args.add("architect");
        args.add("-classpath");
        args.add(System.getProperty("java.class.path"));
        args.add("-output");
        args.add(this.getClass().getSimpleName() + ".config");

        return publish(args);
    }

    /**
     * outputオプションを指定せずに実行する。
     *
     * @return ステータスコード
     */
    public int publishWithoutOutput() {
        List<String> args = new ArrayList<String>();
        args.addAll(docletArgs);
        args.add("-classpath");
        args.add(System.getProperty("java.class.path"));

        return publish(docletArgs);
    }

    /**
     * Publish を実行する。
     * 
     * @param args javadoc の引数
     * @return javadoc 実行結果の戻り値。
     */
    private int publish(List<String> args) {
        System.out.println("generating javadoc in [" + new File(System.getProperty("user.dir")).getAbsolutePath());

        String[] docletArgs = args.toArray(new String[args.size()]);
        return Main.execute(DOCLET_NAME, DOCLET_NAME, docletArgs);
    }

}
