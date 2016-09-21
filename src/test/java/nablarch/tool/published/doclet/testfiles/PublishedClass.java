package nablarch.tool.published.doclet.testfiles;

import nablarch.core.util.annotation.Published;
import nablarch.tool.published.doclet.testfiles.publishedpkg.PubSub;
import nablarch.tool.published.doclet.testfiles.unpublshedpkg.UnPubSub;

/**
 * クラス自体がPublishedの場合のテスト（表示される）。
 *
 * @author T.Kawasaki
 */
@Published
public class PublishedClass {

    /** 表示される。 */
    public static Object StaticField;

    /** 表示される。 */
    public Object field;

    /** 表示される */
    public void method() {
    }

    /** ネストしたサブクラスのテスト（表示される）*/
    public static class NestedSubclassOfPublished extends PublishedClass {

        /** 表示される。*/
        @Override
        public void method() {
        }

        /** 表示されない。*/
        public void additionalMethod() {
        }
    }

    /** 内部クラスのテスト（Publishedメソッドがあるので表示される）*/
    public class InnerClassOfPublished {

        /** 表示される。*/
        @Published
        public void publishedMethod() {
        }

        /** 表示されない。*/
        public void additionalMethod() {
        }
    }

    /** 自身はPublishedでない内部クラスのテスト。表示されない。*/
    public class InnerClassOfPublishedHavingNoPublished {

        /** 表示されない。*/
        public void additionalMethod() {
        }
    }


    /** ネストしたサブクラスのテスト（表示される）。 */
    public static class StaticNestedPublished {
        /** 表示される。 */
        @Published
        public void publishedMethod() {
        }
        /** 表示されない。*/
        public void additionalMethod() {
        }
    }

    /** ネストしたクラスのテスト（表示されない） */
    public static class StaticNestedUnpublished {
    }

    /**
     * 引数と戻り値がリンク表示されるかどうかのテスト。<br />
     * 
     * @param val 引数リンク表示される。
     * 
     * @return 戻り値(リンク表示される。)
     */
    public PubSub publishedMethodParam(PubSub val) {
        return null;
    }


    /**
     * 引数と戻り値がリンク表示されるかどうかのテスト。<br />
     * 
     * @param val 引数リンク表示されない。
     * 
     * @return 戻り値(リンク表示されない。)
     */
    public UnPubSub unpublishedMethodParam(UnPubSub val) {
        return null;
    }
}
