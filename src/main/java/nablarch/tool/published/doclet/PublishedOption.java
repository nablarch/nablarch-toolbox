package nablarch.tool.published.doclet;

import jdk.javadoc.doclet.Doclet;

import java.util.List;
import java.util.Set;

/**
 * {@link jdk.javadoc.doclet.Doclet.Option}の実装クラス。
 * <p>
 * {@code jdk.javadoc.internal.doclets.formats.html.HtmlOptions}の実装を参考にしています。
 * </p>
 * @author Tanaka Tomoyuki
 */
public class PublishedOption {
    
    private List<String> tags = List.of();
    private String output;

    /**
     * オプションで指定されたタグの一覧を取得する。
     * @return タグの一覧
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * オプションで指定された出力先を取得する。
     * @return 出力先
     */
    public String getOutput() {
        return output;
    }

    /**
     * サポートしているオプションのセットを取得する。
     * @return サポートしているオプションのセット
     */
    public Set<? extends Doclet.Option> getSupportedOptions() {
        return Set.of(
            new AbstractOption(1, "出力対象のPublishedのタグ", List.of("-tag"), "<tag>[,<tag>...]") {
                @Override
                public boolean process(String option, List<String> arguments) {
                    String arg = arguments.get(0);
                    tags = List.of(arg.split(","));
                    return true;
                }
            },
            new AbstractOption(1, "ホワイトリストファイルの出力先", List.of("-output"), "<path/to/file>") {
                @Override
                public boolean process(String option, List<String> arguments) {
                    output = arguments.get(0);
                    return true;
                }
            }
        );
    }

    /**
     * {@link Doclet.Option}の共通部分を実装した抽象クラス。
     */
    private static abstract class AbstractOption implements Doclet.Option {
        private final int argumentCount;
        private final String description;
        private final List<String>names;
        private final String parameters;

        /**
         * コンストラクタ。
         * @param argumentCount 引数の数
         * @param description このオプションの説明
         * @param names このオプションの名前
         * @param parameters パラメータの説明
         */
        private AbstractOption(int argumentCount, String description, List<String> names, String parameters) {
            this.argumentCount = argumentCount;
            this.description = description;
            this.names = names;
            this.parameters = parameters;
        }

        @Override
        public int getArgumentCount() {
            return argumentCount;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public Kind getKind() {
            return Kind.STANDARD;
        }

        @Override
        public List<String> getNames() {
            return names;
        }

        @Override
        public String getParameters() {
            return parameters;
        }
    }
}
