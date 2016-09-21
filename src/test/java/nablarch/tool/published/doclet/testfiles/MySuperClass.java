package nablarch.tool.published.doclet.testfiles;

import java.util.List;
import java.util.Map;

import nablarch.core.util.annotation.Published;

/**
 * スーパークラス（このドキュメントは表示される）。
 *
 * @author T.Kawasaki
 */
public class MySuperClass<T> {

    /** 表示される。*/
    @Published(tag = {"architect"})
    public MySuperClass() {
    }

    /** 表示されない。*/
    public MySuperClass(String arg) {
    }

    @Published(tag = {"architect"})
    public void genericMethod(List<Map<String, T>> arg) {
    }

    @Published(tag = {"architect"})
    public void typeParam(T arg) {
    }

    /** 表示される。 */
    @Published
    public static Object publishedStaticField;

    /** 表示されない */
    public Object unpublishedStaticField;

    /** 表示される。 */
    @Published
    public Object publishedField;

    /** 表示されない */
    public Object unpublishedField;

    /** 表示される。 */
    @Published
    public void publishedMethod() {
    }

    /** 表示されない */
    public void unpublishedMethod() {
    }

    /** 内部クラスのテスト。表示される。 */
    public class MyInnerSubClass {

        /** 表示される。 */
        @Published
        public void additionalMethod() {
        }

        /** 表示されない */
        public void unpublishedMethod() {
        }
    }

}
