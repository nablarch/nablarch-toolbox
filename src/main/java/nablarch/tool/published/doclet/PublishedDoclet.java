package nablarch.tool.published.doclet;

import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.doclet.StandardDoclet;

import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Publishedアノテーションが付与された要素のみを抽出してファイルに出力するドックレット実装。<br/>
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
 * 以下に例を示す。
 * <pre>-output=openApi.config</pre>
 *
 * @author T.Kawasaki
 */
public final class PublishedDoclet extends StandardDoclet {
    private Reporter reporter;
    private final PublishedOption options = new PublishedOption();

    @Override
    public void init(Locale locale, Reporter reporter) {
        super.init(locale, reporter);
        this.reporter = reporter;
    }

    @Override
    public String getName() {
        return "PublishedDoclet";
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        HashSet<Option> supported = new HashSet<>(super.getSupportedOptions());
        supported.addAll(this.options.getSupportedOptions());
        return supported;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        printOption();
        
        if (options.getOutput() == null) {
            throw new IllegalArgumentException("Output file name must be specified with -output option. e.g. -output=openApi.config");
        }
        
        PublishedCollector publishedSelector = new PublishedCollector(options.getTags());
        new PublishedConfigGenerator(publishedSelector).generate(environment, options.getOutput());
        return true;
    }

    /**
     * 起動オプションを表示する。
     */
    private void printOption() {
        reporter.print(Diagnostic.Kind.NOTE, "Options:");
        
        if (!options.getTags().isEmpty()) {
            reporter.print(Diagnostic.Kind.NOTE, "\t-tag=" + options.getTags());
        }
        if (options.getOutput() != null) {
            reporter.print(Diagnostic.Kind.NOTE, "\t-output=" + options.getOutput());
        }
    }
}
