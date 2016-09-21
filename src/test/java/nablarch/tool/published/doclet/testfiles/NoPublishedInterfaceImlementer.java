package nablarch.tool.published.doclet.testfiles;

import nablarch.core.util.annotation.Published;

/**
 * Published ではないインタフェースはリンクされない。
 * 
 */
@Published(tag = "architect")
public class NoPublishedInterfaceImlementer implements NoPublishedInterface {

    /**
     * {@inheritDoc}
     */
    public void hoge() {

    }
}
