package nablarch.tool.published.doclet;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.standard.Standard;

import nablarch.core.util.StringUtil;

/**
 * Publishedアノテーションが付与された要素のみを抽出するドックレット実装。<br/>
 * 標準のオプションに加えて、以下のオプションを付与できる。
 * <ul>
 * <li>-tag</li>
 * </ul>
 * 値にはタグ名を指定する。ここで指定されたタグ名を持つ公開APIが出力される。
 * 複数指定する場合は、値をカンマ区切りで指定する。
 * 以下に例を示す。
 * <pre>-tag=senior-developer,architect</pre>
 *
 * <ul>
 * <li>-output</li>
 * </ul>
 * 値には「使用不許可APIチェックツール」用設定ファイルの出力先を指定する。
 * 指定されない場合は、{@code openApi.config} がデフォルト値として利用される。
 * 以下に例を示す。
 * <pre>-output=openApi.config</pre>
 *
 * @author T.Kawasaki
 * @see com.sun.javadoc.Doclet
 */
public final class PublishedDoclet {

    /**
     * ドキュメントを生成する。<br/>
     * ルート要素に動的Proxyを設定することで、出力要素の取捨選択を行う。
     *
     * @param root プログラム構造情報のルート
     * @return 成功した場合、真
     * @see com.sun.javadoc.Doclet#start(com.sun.javadoc.RootDoc)
     */
    public static boolean start(RootDoc root) {
        printOption(root);
        List<String> tags = readTagOption(root.options());
        String path = readOutputFileOption(root.options());
        PublishedCollector publishedSelector = new PublishedCollector(tags);
        RootDoc rootDoc = (RootDoc) PublishedCollectingHandler.wrap(root, publishedSelector, false);
        new PublishedConfigGenerator(publishedSelector).generate(rootDoc, path);
        return Standard.start(rootDoc);
    }

    /**
     * オプションの引数が正しいかどうかをチェックする。
     *
     * @param options オプション
     * @param reporter エラー、警告、および通知の表示機能
     * @return オプションが有効な場合、真
     * @see com.sun.javadoc.Doclet#validOptions(String[][], com.sun.javadoc.DocErrorReporter)
     */
    public static boolean validOptions(String[][] options, DocErrorReporter reporter) {
        for (String[] opt : options) {
            if (opt[0].equals("-tag") || opt[0].equals("-output")) {
                return true;   // Standardのチェックに引っかからないようtrueでチェック処理を終了する。
            }
        }
        return Standard.validOptions(options, reporter);
    }

    /**
     * 指定されたオプションのコマンド行で指定しなければならない引数の数を返却する。
     *
     * @param option オプション
     * @return 引数の数
     * @see com.sun.javadoc.Doclet#optionLength(String)
     */
    public static int optionLength(String option) {
        if (option.equals("-tag") || option.equals("-output")) {
            return 2;
        }
        return Standard.optionLength(option);
    }

    /**
     * オプション"-tag"を読み取る。
     *
     * @param options オプション
     * @return タグ一覧
     */
    private static List<String> readTagOption(String[][] options) {
        String value = null;
        for (String[] opt : options) {
            if (opt[0].equals("-tag")) {
                value = opt[1];
                break;
            }
        }
        if (value == null) {
            return Collections.emptyList();
        }
        String[] tags = value.split(",");
        return Arrays.asList(tags);
    }

    /**
     * オプション"-output"を読み取る。
     *
     * @param options オプション
     * @return 「使用不許可APIチェックツール」用設定ファイルの出力先
     */
    private static String readOutputFileOption(String[][] options) {
        String file = null;
        for (String[] opt : options) {
            if (opt[0].equals("-output")) {
                file = opt[1];
                break;
            }
        }
        if (!StringUtil.hasValue(file)) {
            throw new IllegalArgumentException("Output file name must be specified with -output option. e.g. -output=openApi.config");
        }
        return file;
    }

    /**
     * このドックレットがサポートしている Java プログラミング言語のバージョンを取得する。
     *
     * @return バージョン
     * @see com.sun.javadoc.Doclet#languageVersion
     */
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }


    /**
     * 起動オプションを表示する。
     *
     * @param rootDoc {@link RootDoc}
     */
    private static void printOption(RootDoc rootDoc) {
        System.out.println("Options:");
        for (String[] param : rootDoc.options()) {
            System.out.println('\t' + Arrays.toString(param) + '\n');
        }
    }

    /** プライベートコンストラクタ。 */
    private PublishedDoclet() {
    }
}
