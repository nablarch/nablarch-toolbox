/**
 * 公開APIの一覧を出力するドックレットを提供する。
 * <p>
 * 本ドックレットを使うことで、公開APIを一覧化したファイルを出力できる。<br>
 * このファイルは使用不許可APIチェックツールの設定ファイル(ホワイトリスト)として利用できる。
 * </p>
 * 
 * <h1>出力判定ルール</h1>
 * <h2>基本的な出力対象判定ルール</h2>
 * <dl>
 * <dt>
 * クラスに{@code @Published}が付与された場合
 * </dt>
 * <dd>クラスと全てのメソッドが出力される。</dd>
 * <dt>
 * メンバー（メソッド、フィールド、コンストラクタ）にのみ{@code Published}が付与された場合
 * </dt>
 * <dd>クラスのコメントと、アノテーションが付与された要素が出力される。</dd>
 * <dt>
 * 自身はPublishedでないが、親クラスにPublishedが付与されている場合
 * </dt>
 * <dd>クラスのみ出力される。</dd>
 * <dt>
 * 自身はPublishedでないが、インターフェースにPublishedが付与されている場合
 * </dt>
 * <dd>出力されない。</dd>
 * </dl>
 *
 * <h2>本ドックレットの独自オプションを指定した場合の出力判定ルール</h2>
 * <p>
 * 本ドックレットの独自オプションとして{@code -tag}オプションが存在する。
 * 値にはタグ名を指定する。ここで指定されたタグ名を持つ公開APIが出力される。
 * 複数指定する場合は、値をカンマ区切りで指定する。
 * 以下に例を示す。
 * <pre>-tag=senior-developer,architect</pre>
 * </p>
 * <dl>
 * <dt>
 * {@code @Published}のtag属性に何も指定されていない場合（デフォルト）
 * </dt>
 * <dd>{@code -tag}オプションの指定に関わらず、出力される。</dd>
 * <dt>
 * {@code @Published}のtag属性が明示的に指定されている場合
 * </dt>
 * <dd>{@code @Published}のtag属性に、オプションで指定されたタグと合致するタグが指定されている場合、出力される。</dd>
 * </dl>
 * @see nablarch.core.util.annotation.Published
 */
package nablarch.tool.published.doclet;
