"use strict";

require('sugar');

var $ = require('cheerio')
  , VerificationError = require('../verification_error');

/**
 * 使用可能タグ検証クラス
 * =====================================================================
 *
 * JSPファイル内で使用されているタグが、使用可能なタグであるかどうかを検証する。
 *
 * 使用可能なタグは、タグ名に完全一致する正規表現として設定ファイルに以下のように記載する。
 *
 * > __設定フォーマット (JSON)__
 * >
 * >     "allowed_tags": {
 * >       "許可するタグ名に完全一致する正規表現": "コメント"
 * >     }
 * >
 * > __設定ファイル記載例 (verification_config.json)__
 * >
 * >     {
 * >       "verifiers": {
 * >         "TagUsageVerifier": {
 * >           "allowed_tags": {
 * >             "n:form": ""
 * >             , "n:set": ""
 * >             , "n:write": ""
 * >             , "n:ConfirmationPage": ""
 * >             , "n:forConfirmationPage": ""
 * >             , "n:forInputPage": ""
 * >             , "n:param": ""
 * >             , "t:page_template": "業務画面のテンプレート"
 * >             , "t:errorpage_template": "エラー画面のテンプレート"
 * >             , "box:.*": ""
 * >             , "button:.*": "ボタンウィジェット"
 * >             , "field:.*": "入力フォーム部品"
 * >             , "link:.*": "リンクウィジェット"
 * >             , "tab:.*": "タブウィジェット"
 * >             , "table:.*": "テーブルウィジェット"
 * >             , "c:if": ""
 * >             , "jsp:attribute": ""
 * >             , "%@": "JSPディレクティブ"
 * >             , "%--": "JSPコメント"
 * >           }
 * >         }
 * >       }
 * >     }
 *
 * @class TagUsageVerifier
 */
TagUsageVerifier.prototype = {
  constructor: TagUsageVerifier,
  verify: TagUsageVerifier_verify,

  /**
   * 使用可能とするタグ名にマッチする正規表現
   *
   * @property allowed_tags
   * @type Array
   * @default []
   */
  allowed_tags: []
};

/**
 * コンストラクタ関数
 *
 * @method TagUsageVerifier
 * @constructor
 * @param {Object} config 設定情報を格納したオブジェクト。使用するキーは以下の通り。
 *
 *    * **allowed_tags** 使用可能とするタグ名にマッチする正規表現をキーに持ち、コメントを値としてもつオブジェクト。
 */
function TagUsageVerifier(config) {
  this.allowed_tags = this.allowed_tags.concat(Object.keys(config['allowed_tags']));
}

/**
 * JSPファイルで使用されている全てのタグが、 `allowed_tags` に指定されているかどうかを検証する。
 *
 * なお、以下のUI開発基盤を利用したローカル表示用のマジックコメントは解析対象にならない。
 *
 *     <!-- <%/* --> <script src="js/devtool.js"></script><meta charset="utf-8"><body> <!-- * /%> -->
 *
 * また、JSPディレクティブについては、下記の置換を行ってディレクティブ名を正規化している。
 *
 *     replace(/<%@\s+([a-zA-Z]+)\s/mg, '<%@$1 ')
 *
 * @method verify
 * @param {String} jsp 検証対象とするJSPファイルの内容
 * @param {String} path 検証対象ファイルのパス
 * @return {Array} 検証結果 ({{#crossLink "VerificationError"}}{{/crossLink}}) を格納した配列
 */
function TagUsageVerifier_verify(jsp, path) {
  var $tags = $('<div>').append($(jsp)).find('*')
    , self = this
    , result = []
    , message;

  $tags.each(function(idx, node) {
    var tag = node.name
      , isNotAllowed = self.allowed_tags.none(function(allowed) {
        return tag.match(new RegExp('^' + allowed + '$', "i"))
      });

    if (isNotAllowed) {
      message = '<{tag}> タグは、JSPファイルでは利用しないでください。'.assign({"tag": tag});
      result.push(new VerificationError('TagUsageVerifier', message, path, $('<div>').append(node).html()))
    }
  });

  return result;
}

module.exports = TagUsageVerifier;
