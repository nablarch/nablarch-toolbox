package nablarch.tool.published.doclet.testfiles;

import nablarch.core.util.annotation.Published;
import nablarch.tool.published.doclet.testfiles.publishedpkg.PubAnno;
import nablarch.tool.published.doclet.testfiles.unpublshedpkg.UnPubAnno;

/**
 * アノテーションテスト用クラス。
 *
 */
@Published
@PubAnno
@UnPubAnno
public class AnnotationOutput {

    /**
     * フィールド。
     */
    @PubAnno
    @UnPubAnno
    public String fuga;
    
    /**
     * メソッド。
     */
    @PubAnno
    @UnPubAnno
    public void hoge() {
        
    }

    /**
     *
     * インタフェース。
     */
    @PubAnno
    @UnPubAnno
    public static interface Fuga {
        
    }


    /**
     *
     * アノテーション。
     */
    @PubAnno
    @UnPubAnno
    public @interface ExAnnnotation {
        
    }
}
