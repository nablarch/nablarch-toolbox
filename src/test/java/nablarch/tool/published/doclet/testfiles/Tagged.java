package nablarch.tool.published.doclet.testfiles;

import nablarch.core.util.annotation.Published;

/**
 * タグのテスト。表示される。
 *
 * @author T.Kawasaki
 */
public class Tagged {

    /** {@code -tag=architect}が指定された場合は、表示される */
    @Published(tag = "architect")
    public void publishedToArchitect() {
    }

    /** {@code -tag=architect}が指定された場合は、表示されない */
    @Published(tag = {"nobody"})
    public void publishedToDeveloper() {
    }

    /** 特にタグが指定されていない場合は、オプションに関わらず表示される */
    @Published
    public void publishedToEverybody() {
    }
}
