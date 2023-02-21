package nablarch.tool.published.doclet;

import nablarch.tool.published.doclet.testfiles.PublishedClass;
import org.junit.Test;

import javax.tools.DocumentationTool;
import javax.tools.ToolProvider;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static nablarch.tool.published.doclet.Util.prepare;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * テスト用の公開API出力クラス。
 * <p>
 * このテストクラスは、以下2つのことをチェックしている。
 * </p>
 * <ul>
 *   <li>終了ステータスが意図したものになっていること</li>
 *   <li>出力された使用不許可APIチェックツールの設定ファイルが意図通りであること</li>
 * </ul>
 * <p>
 * 5uXXの頃はPublishedアノテーションの有無でJavadocの出力内容を制御していたため
 * Javadocの出力結果をチェックする必要があった。
 * しかし、6uXXで全ての要素をそのまま出力することになったため、PublishedDocletからは
 * Javadocの出力機能を削除した。
 * </p>
 * 
 * @author T.Kawasaki
 */
public class PublisherTest {

    /** 設定ファイル出力先ディレクトリ */
    private static final File CONFIG_DEST_DIR = prepare("target/published");

    /** パッケージ */
    private static final String PKG = PublishedClass.class.getPackage().getName();

    /** ドックレット引数。 */
    private final List<String> docletArgs = List.of(
        "-sourcepath", "src/test/java",
        "-encoding", "UTF-8",
        "-charset", "UTF-8",
        "-protected",
        "-subpackages", PKG,
        "-doclet", PublishedDoclet.class.getName()
    );

    /**
     * 公開APIを作成する。
     */
    @Test
    public void testPublish() throws Exception {
        File output = new File(CONFIG_DEST_DIR, "/publishedForAll.config");
        int status = publish(output);
        assertThat(status, is(0));

        final List<String> actual = readAllLinesWithSort(output.toURI().toURL());
        final List<String> expected = readAllLinesWithSort(
                PublisherTest.class.getResource("/nablarch/tool/published/doclet/publishedForAll.config"));
        
        assertThat(actual, equalTo(expected));
    }

    /**
     * タグ付きの公開APIを作成する。
     */
    @Test
    public void testPublishTagged() throws Exception {
        File output = new File(CONFIG_DEST_DIR, "publishedForArchitect.config");
        int status = publishForArchitect(output);
        assertThat(status, is(0));
        
        final List<String> actual = readAllLinesWithSort(output.toURI().toURL());
        final List<String> expected = readAllLinesWithSort(
                PublisherTest.class.getResource("/nablarch/tool/published/doclet/publishedForArchitect.config"));

        assertThat(actual, equalTo(expected));
    }

    /**
     * outputオプションを指定せずに公開APIを作成する。
     */
    @Test
    public void testPublishWithoutOutput() {
        int status = publishWithoutOutput();
        assertThat(status, is(not(0)));
    }

    /**
     * デフォルトの公開範囲で公開する。
     *
     * @param output 設定ファイルの出力先
     * @return ステータスコード
     */
    public int publish(File output) {
        List<String> args = new ArrayList<>(docletArgs);
        args.add("-d");
        args.add("./target/javadoc/publish");
        args.add("-classpath");
        args.add(System.getProperty("java.class.path"));
        args.add("-output");
        args.add(output.getPath());

        return publish(args);
    }

    /**
     * アーキテクト向けの公開範囲で公開する。
     *
     * @param output 設定ファイルの出力先
     * @return ステータスコード
     */
    public int publishForArchitect(File output) {
        List<String> args = new ArrayList<>(docletArgs);
        args.add("-d");
        args.add("./target/javadoc/publishArchetect");
        args.add("-tag");
        args.add("architect");
        args.add("-classpath");
        args.add(System.getProperty("java.class.path"));
        args.add("-output");
        args.add(output.getPath());

        return publish(args);
    }

    /**
     * outputオプションを指定せずに実行する。
     *
     * @return ステータスコード
     */
    public int publishWithoutOutput() {
        List<String> args = new ArrayList<>(docletArgs);
        args.add("-classpath");
        args.add(System.getProperty("java.class.path"));

        return publish(args);
    }

    /**
     * Publish を実行する。
     * 
     * @param args javadoc の引数
     * @return javadoc 実行結果の戻り値。
     */
    private int publish(List<String> args) {
        System.out.println("generating javadoc in [" + new File(System.getProperty("user.dir")).getAbsolutePath() + "]");

        String[] docletArgs = args.toArray(new String[0]);

        final DocumentationTool documentationTool = ToolProvider.getSystemDocumentationTool();
        return documentationTool.run(null, null, null, docletArgs);
    }

    /**
     * 指定されたパスのファイルの内容を読み込む。
     * <p>
     * 行ごとにリストの要素に分割され、ソートされた結果が返される。
     * </p>
     * @param url 読み込むファイルのURL
     * @return 読み込み結果
     */
    private List<String> readAllLinesWithSort(URL url) {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            List<String> result = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
            result.sort(String::compareTo);
            return result;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * メインメソッド。
     *
     * @param args 使用しない
     */
    public static void main(String[] args) {
        new PublisherTest().publish(new File("target/test.config"));
    }
}
