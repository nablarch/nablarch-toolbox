package nablarch.tool.published.doclet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.AnnotationTypeElementDoc;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;

/**
 * 公開APIを抽出するクラス。<br/>
 *
 * @author T.Kawasaki
 */
class PublishedCollector {

    /** 抽出対象とするタグ */
    private final Set<String> tagsToPublish;

    /**
     * コンストラクタ。
     *
     * @param tagsToPublish 抽出対象とするタグ
     */
    PublishedCollector(List<String> tagsToPublish) {
        this.tagsToPublish = new HashSet<String>(tagsToPublish);
    }

    /**
     * 出力対象のオブジェクトを抽出する。
     *
     * @param obj  オブジェクト
     * @param type 型
     * @return 抽出結果
     */
    Object collect(Object obj, Class<?> type) {
        if (obj == null) {
            return null;
        }
        Class<?> cls = obj.getClass();
        if (implementsJavadocInterface(cls)) {
            // プロキシを噛ませる
            return PublishedCollectingHandler.wrap(obj, this, !isIncluded(obj));
        }
        if (obj instanceof Object[]) {
            Object[] array = (Object[]) obj;
            Class<?> componentType = type.getComponentType();
            if (componentType == null) {
                componentType = array.getClass().getComponentType();
            }
            List<Object> list = new ArrayList<Object>(array.length);
            for (Object e : array) {
                Object collected;

                boolean included = isIncluded(e);

                // そもそも e 自体がメソッドやクラス、パッケージ(つまり javadoc に出す対象か否かを判定する場合)
                // そもそも戻り値には追加しない。
                if ((!included) && ((e instanceof ClassDoc) || (e instanceof MemberDoc) || (e instanceof PackageDoc))) {
                    continue;
                }
                
                if (included) {
                    collected = collect(e, componentType);   // recursive call.
                } else if (e instanceof Doc) {
                    // 含まれない要素は isIncluded で false 返すようにする。
                    collected = PublishedCollectingHandler.wrap(e, this, true);
                } else if (e instanceof SeeTag) {
                    SeeTag seeTag = (SeeTag) e;
                    Doc linkedDoc = null;

                    if (seeTag.referencedClass() != null) {
                        linkedDoc = seeTag.referencedClass();
                    } else {
                        linkedDoc = seeTag.referencedPackage();
                    }

                    //seeTagのリンク先が存在しない場合も、文字列にして表示する。
                    if (linkedDoc == null || linkedDoc.isIncluded()) {
                        collected = new NoLinkSeeTag(seeTag);
                    } else {
                        collected = seeTag;
                    }
                } else {
                    // ここは到達不能だが、JavaDocのバージョンアップ時に問題に気付けるよう残しておく。
                    // そのまま収集されたら、ClassCastExceptionが発生する可能性がある。
                    collected = e;
                }
                
                list.add(collected);
            }
            return list;
        }
        return obj;
    }

    /**
     * 標準Javadoc API のインタフェースを実装しているかチェックする。
     * 
     * @param cls チェック対象のクラス
     * @return Doclet API のインタフェースを実装している場合 true
     */
    public boolean implementsJavadocInterface(Class<?> cls) {
        for (Class<?> i : cls.getInterfaces()) {
            if (i.getName().startsWith("com.sun.javadoc")) {
                return true;
            }
        }
        return false;
    }

    /**
     * あるオブジェクトが出力対象であるかどうか判定する。<br/>
     * そのオブジェクトが{@link Doc}である場合は、
     * その要素を走査して出力対象かどうか判定する。
     * そうでない場合は、一律出力対象とする。
     *
     * @param o オブジェクト
     * @return 出力対象である場合、真
     */
    private boolean isIncluded(Object o) {
        // パッケージ
        if (o instanceof PackageDoc) {
            PackageDoc packageDoc = (PackageDoc) o;
            return isIncludedPackage(packageDoc);
        }
        // クラス
        if (o instanceof ClassDoc) {
            ClassDoc classDoc = (ClassDoc) o;
            return isIncludedClass(classDoc);
        }
        // メンバー（メソッド、フィールド、コンストラクタ）
        if (o instanceof MemberDoc) {
            MemberDoc memberDoc = (MemberDoc) o;
            return isIncludedMember(memberDoc);
        }
        // @link や @see タグでのリンク
        if (o instanceof SeeTag) {
            SeeTag seeTag = (SeeTag) o;
            return isIncludedSeeTag(seeTag);
        }
        return true;  // そもそも除外対象でないので真
    }

    /**
     * パッケージが出力対象であるかどうか判定する。<br/>
     * そのパッケージ配下にPublishedな要素が含まれる場合、
     * そのパッケージは出力対象とみなされる。
     *
     * @param packageDoc パッケージ
     * @return 出力対象である場合、真
     */
    private boolean isIncludedPackage(PackageDoc packageDoc) {
        for (ClassDoc e : packageDoc.allClasses()) {
            if (isIncludedClass(e)) {
                return true;
            }
        }
        return false;
    }


    /**
     * クラスが出力対象かどうか判定する。
     * 以下の場合、出力対象とみなす。
     * <ul>
     * <li>そのクラスにPublishedアノテーションが付与されている場合</li>
     * <li>そのクラスのメンバーにPublishedアノテーションが付与されている場合</li>
     * </ul>
     *
     * @param classDoc クラス
     * @return 出力対象である場合、真
     */
    private boolean isIncludedClass(ClassDoc classDoc) {
        return hasPublishedAnnotation(classDoc)
                || containsPublishedElement(classDoc);
    }

    /**
     * メンバーが出力対象かどうか判定する。
     * 以下の場合、出力対象とみなす。
     * <ul>
     * <li>その要素にPublishedが付与されている場合</li>
     * <li>クラス自体がPublishedでありかつ暗黙のデフォルトコンストラクタでない場合</li>
     * </ul>
     *
     * @param memberDoc メンバー
     * @return 出力対象である場合、真
     */
    private boolean isIncludedMember(MemberDoc memberDoc) {
        // クラス自体がPublishedならメソッドもPublished
        if (isMemberOfPublishedClass(memberDoc)) {
            // ただし暗黙のコンストラクタは除外
            return !isImplicitDefaultConstructor(memberDoc);
        }
        // Publishedな要素なら出力する
        return hasPublishedAnnotation(memberDoc);

    }

    /**
     * リンク先が出力対象かどうか判定する。
     * 以下の場合、出力対象とみなす。
     * <ul>
     * <li>その要素のリンク先にPublishedが付与されている場合</li>
     * <li>クラス自体がPublishedでありかつ暗黙のデフォルトコンストラクタでない場合</li>
     * </ul>
     *
     * @param seeTag メンバー
     * @return 出力対象である場合、真
     */
    private boolean isIncludedSeeTag(SeeTag seeTag) {

        MemberDoc referencedMember = seeTag.referencedMember();
        if (referencedMember != null) {
            return isIncludedMember(referencedMember);
        }

        ClassDoc referencedClass = seeTag.referencedClass();
        if (referencedClass != null) {
            return isIncludedClass(referencedClass);
        }
        
        PackageDoc referencedPackage = seeTag.referencedPackage();
        if (referencedPackage != null) {
            for (ClassDoc innerClassDoc : referencedPackage.allClasses()) {
                if (isIncludedClass(innerClassDoc)) {
                    return true;
                }
            }
            return false;
        }
        //Member,Class,Packageどれでもない場合はfalse
        return false;

    }

    /**
     * クラスにPublishedな要素が含まれるかどうか判定する。
     *
     * @param classDoc クラス
     * @return Publishedな要素が含まれる場合、真
     */
    private boolean containsPublishedElement(ClassDoc classDoc) {
        return hasPublishedAnnotation(classDoc.methods())
                || hasPublishedAnnotation(classDoc.fields())
                || hasPublishedAnnotation(classDoc.constructors());
    }

    /**
     * ある文書要素群にPublishedが含まれるかどうか判定する。
     *
     * @param elementDocs 要素群
     * @return Publishedが含まれる場合、真
     */
    private boolean hasPublishedAnnotation(ProgramElementDoc[] elementDocs) {
        for (ProgramElementDoc e : elementDocs) {
            if (hasPublishedAnnotation(e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * javadocコメントを保持しているかどうか判定する。<br/>
     * デフォルトコンストラクタ判定に使用する。
     *
     * @param doc 文書
     * @return コメントが明示的に設定されている場合、真
     */
    private boolean hasComment(Doc doc) {
        String comment = doc.commentText();
        return comment != null && comment.trim().length() > 0;
    }


    /**
     * メンバーが、Publishedクラスに属しているかどうか判定する。
     *
     * @param memberDoc メンバー要素（メソッド、フィールド、コンストラクタ）
     * @return Publishedが付与されたクラスに属している場合、真
     */
    private boolean isMemberOfPublishedClass(MemberDoc memberDoc) {
        ClassDoc classDoc = memberDoc.containingClass();
        return hasPublishedAnnotation(classDoc);
    }

    /**
     * 暗黙のデフォルトコンストラクタで有るかどうか判定する。
     *
     * @param memberDoc メンバー
     * @return 暗黙のデフォルトコンストラクタである場合、真
     */
    private boolean isImplicitDefaultConstructor(MemberDoc memberDoc) {
        return memberDoc.isConstructor() && !hasComment(memberDoc);
    }

    /**
     * ある文書要素にPublishedアノテーションが付与されているかどうか判定する。
     *
     * @param programElementDoc 文書要素
     * @return Publishedアノテーションが付与されている場合、真
     */
    boolean hasPublishedAnnotation(ProgramElementDoc programElementDoc) {
        for (AnnotationDesc annotationDesc : programElementDoc.annotations()) {
            if (isPublishedAnnotation(annotationDesc)) {
                return true;
            }
        }
        return false;
    }

    /**
     * アノテーションがPublishedアノテーションかどうか判定する。<br/>
     *
     * @param annotationDesc アノテーション
     * @return Publishedアノテーションの場合、真
     */
    boolean isPublishedAnnotation(AnnotationDesc annotationDesc) {
        AnnotationTypeDoc annotationTypeDoc = annotationDesc.annotationType();
        if (!annotationTypeDoc.name().equals("Published")) {
            return false;
        }
        ElementValuePair[] elementValuePairs = annotationDesc.elementValues();
        boolean hasNoTag = elementValuePairs.length == 0;
        // 何のタグも指定されていない（デフォルト）か、もしくは、
        // 指定されたタグを保持しているか
        return hasNoTag || hasQualifiedTag(elementValuePairs);
    }

    /**
     * ドックレットのパラメータで指定されたタグを保持しているか
     *
     * @param elementValuePairs アノテーションとその値のペア
     * @return 指定されたタグを保持している場合、真
     */
    boolean hasQualifiedTag(ElementValuePair[] elementValuePairs) {
        // アノテーション内の全を
        for (ElementValuePair elementValuePair : elementValuePairs) {
            AnnotationTypeElementDoc annotationTypeElementDoc = elementValuePair.element();
            if (annotationTypeElementDoc.name().equals("tag")) {
                AnnotationValue annotationValue = elementValuePair.value();
                AnnotationValue[] values = (AnnotationValue[]) annotationValue.value();
                for (AnnotationValue value : values) {
                    String tag = unquote(value.toString());
                    if (tagsToPublish.contains(tag)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 引用符を外す。
     *
     * @param src 元の文字列
     * @return 引用符を外した文字列
     */
    private static String unquote(String src) {
        boolean isQuoted = src.length() > 2
                && src.startsWith("\"")
                && src.endsWith("\"");
        if (isQuoted) {
            return src.substring(1, src.length() - 1);
        }
        return src;
    }

    /**
     * SeeTagをラップして単純なテキストを出力するタグ。
     */
    private static final class NoLinkSeeTag implements SeeTag {

        /**
         * 元にした SeeTag オブジェクト。
         */
        private SeeTag seeTag;
        
        /**
         * コンストラクタ。
         * 
         * @param seeTag 元にした SeeTag オブジェクト
         */
        public NoLinkSeeTag(SeeTag seeTag) {
            super();
            this.seeTag = seeTag;
        }

        /**
         * {@inheritDoc}
         */
        public String name() {
            return seeTag.name();
        }

        /**
         * {@inheritDoc}
         */
        public Doc holder() {
            return seeTag.holder();
        }

        /**
         * {@inheritDoc}
         */
        public String kind() {
            return seeTag.kind();
        }

        /**
         * {@inheritDoc}
         */
        public String text() {
            return seeTag.text();
        }

        /**
         * {@inheritDoc}
         */
        public Tag[] inlineTags() {
            return new Tag[] {};
        }

        /**
         * {@inheritDoc}
         */
        public Tag[] firstSentenceTags() {
            return new Tag[] {};
        }

        /**
         * {@inheritDoc}
         */
        public SourcePosition position() {
            return seeTag.holder().position();
        }

        /**
         * {@inheritDoc}
         */
        public String label() {
            return seeTag.label();
        }

        /**
         * {@inheritDoc}
         */
        public PackageDoc referencedPackage() {
            // リンクを出さないために、nullを返す
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public String referencedClassName() {
            // リンクを出さないために、nullを返す
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public ClassDoc referencedClass() {
            // リンクを出さないために、nullを返す
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public String referencedMemberName() {
            // リンクを出さないために、nullを返す
            return null;
        }

        /**
         * {@inheritDoc}
         */        
        public MemberDoc referencedMember() {
            // リンクを出さないために、nullを返す
            return null;
        }
    }
}
