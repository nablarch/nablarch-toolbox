package nablarch.tool.published.doclet;

import com.sun.tools.javadoc.Main;
import nablarch.tool.published.doclet.testfiles.PublishedClass;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static nablarch.tool.published.doclet.Util.join;
import static nablarch.tool.published.doclet.Util.prepare;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * テスト用の公開API出力クラス。
 * tool/testをカレントディレクトリとして実行する。
 *
 * @author T.Kawasaki
 */
public class NabTestPublisher {

    /** ドックレットクラス名 */
    private static final String DOCLET_NAME = PublishedDoclet.class.getName();

    /** 出力先ディレクトリ */
    private static final File DEST_DIR = prepare("target/javadoc");

    /** パッケージ */
//    private static final String PKG = PublishedClass.class.getPackage().getName();
    private static final String PKG = "nablarch";

    /** ドックレット実行時のクラスパス */
    private static final String CLASSPATH = "C:\\work\\workspace-helios\\Nablarch\\lib\\activation.jar;C:\\work\\workspace-helios\\Nablarch\\lib\\jms-api-1.1-rev-1.jar;C:\\work\\workspace-helios\\Nablarch\\lib\\jsp-api-2.1.jar;C:\\work\\workspace-helios\\Nablarch\\lib\\mailapi.jar;C:\\work\\workspace-helios\\Nablarch\\lib\\nablarch-tfw.jar;C:\\work\\workspace-helios\\Nablarch\\lib\\nablarch-toolbox.jar;C:\\work\\workspace-helios\\Nablarch\\lib\\servlet-api.jar;C:\\work\\workspace-helios\\Nablarch\\lib\\standard-1.1.2.jar;C:\\work\\workspace-helios\\Nablarch\\lib\\tools.jar";

    /** ドックレット引数。 */
    private final List<String> docletArgs = new ArrayList<String>(Arrays.asList(
            "-classpath", CLASSPATH,
            "-sourcepath", "C:\\work\\workspace-helios\\Nablarch\\java",
            "-d", DEST_DIR.getPath(),
            "-encoding", "UTF-8",
            "-charset", "UTF-8",
            "-protected",
            "-subpackages", PKG
            // タグをつけると範囲が広がります。
            //"-tag", "architect",
    ));

    /** 公開APIを作成する。 */
    @Test
    public void testPublish() {
        int status = publish();
        assertThat(status, is(0));
    }

    /** タグ付きの公開APIを作成する。 */
    @Test
    public void testPublishTagged() {
        int status = publishForArchitect();
        assertThat(status, is(0));
    }

    /**
     * メインメソッド。
     *
     * @param args 使用しない
     */
    public static void main(String[] args) {
        new NabTestPublisher().publish();
    }

    /**
     * デフォルトの公開範囲で公開する。
     *
     * @return ステータスコード
     */
    public int publish() {
        return publish(docletArgs);
    }

    /**
     * アーキテクト向けの公開範囲で公開する。
     *
     * @return ステータスコード
     */
    public int publishForArchitect() {
        docletArgs.add("-tag");
        docletArgs.add("architect");
        return publish(docletArgs);
    }

    private int publish(List<String> args) {
        System.out.println("generating javadoc in [" + new File(System.getProperty("user.dir")).getAbsolutePath());

        String[] docletArgs = args.toArray(new String[args.size()]);
        return Main.execute(DOCLET_NAME, DOCLET_NAME, docletArgs);
    }

}
