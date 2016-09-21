"use strict";

require('sugar');

var $ = require('cheerio')
  , VerificationError = require('../verification_error');

/**
 * 親タグ検証クラス
 * =====================================================================
 *
 * 正規表現で指定されたタグ名にマッチするタグの親タグに、指定されているタグが存在(する|しない)かどうかを検証する。
 *
 * 検証対象とするタグおよびその親タグは以下のように設定する。
 *
 * childは __正規表現__ で指定するが、parentは __セレクタ__ で指定することに注意。
 * このため、parentにn:formなどの":"を含むタグを指定したい場合には、 "n\\\\:form" のようにエスケープする必要がある。
 *
 * なお、jQueryと似たセレクタを使用できるが、完全に互換性があるわけではない。
 * 使用できるセレクタの詳細が必要な場合には、 http://matthewmueller.github.io/cheerio/ を参照すること。
 *
 * > __設定フォーマット (JSON)__
 * >
 * >     "required": {
 * >       "コメント": {
 * >         "child": "検証対象とするタグにマッチする正規表現",
 * >         "parent": "childに指定したタグの親に存在しなくてはいけないタグにマッチするセレクタ"
 * >       }
 * >     },
 * >     "forbidden": {
 * >       "コメント": {
 * >         "child": "検証対象とするタグにマッチする正規表現",
 * >         "parent": "childに指定したタグの親に存在してはいけないタグにマッチするセレクタ"
 * >       }
 * >     }
 * >
 * > __設定ファイル記載例 (verification_config.json)__
 * >
 * >     "required": {
 * >       "tableウィジェットは、n:formで囲む必要がある。": {
 * >         "child": "table:.*",
 * >         "parent": "n\\:form"
 * >       },
 * >       "buttonウィジェットは、n:formで囲む必要がある。": {
 * >         "child": "button:.*",
 * >         "parent": "n\\:form"
 * >       },
 * >      "設計書ビューで画面項目定義に表示されるウィジェットは、spec:layoutで囲む必要がある。": {
 * >        "child": "(table|field|column):.+",
 * >        "parent": "spec\\:layout"
 * >      }
 * >    },
 * >    "forbidden": {
 * >    }
 *
 * @class WrappingTagVerifier
 */
WrappingTagVerifier.prototype = {
  constructor: WrappingTagVerifier,
  verify: WrappingTagVerifier_verify,

  /**
   * 満たさなくてはいけない親子関係のリスト
   *
   * @param required
   * @type Array
   * @default []
   */
  required: [],

  /**
   * 満たしてはいけない親子関係のリスト
   *
   * @property forbidden
   * @type Array
   * @default []
   */
  forbidden: []
};

/**
 * コンストラクタ関数
 *
 * @method WrappingTagVerifier
 * @constructor
 * @param {Object} config 設定情報を格納したオブジェクト。使用するキーは以下の通り。
 *
 *    * **directory** タグファイルが格納されているディレクトリのパス
 *    * **encoding** タグファイルのエンコーディング
 */
function WrappingTagVerifier(config) {
  this.required = Object.values(config['required']);
  this.forbidden = Object.values(config['forbidden']);
}

/**
 * タグの親子関係を検証する。
 *
 * @method verify
 * @param {String} jsp 検証対象とするJSPファイルの内容
 * @param {String} path 検証対象ファイルのパス
 * @return {Array} 検証結果 ({{#crossLink "VerificationError"}}{{/crossLink}}) を格納した配列
 */
function WrappingTagVerifier_verify(jsp, path) {
  var $tags = $('<div>').append($(jsp)).find('*')
    , self = this
    , result = [];
  $tags.each(function(idx, t) {
    var $t = $(t);
    result = self.required.reduce(function(res, r) {
      var c = r["child"]
        , p = r["parent"];
      if(t.name.match(new RegExp('^' + c + '$', 'i')) && $t.parents(p).length === 0) {
        res.push(new VerificationError('WrappingTagVerifier', '<' + t.name + '> の親タグに <' + p + '> にマッチするタグが検出されませんでした。', path, $('<div>').append($t).html()));
      }
      return res
    }, result);
    result = self.forbidden.reduce(function(res, r) {
      var c = r["child"]
        , p = r["parent"];
      if(t.name.match(new RegExp('^' + c + '$', 'i')) && $t.parents(r["parent"]).length !== 0) {
        res.push(new VerificationError('WrappingTagVerifier', '<' + t.name + '> の親タグに <' + p + '> にマッチするタグが検出されました。', path, $('<div>').append($t).html()));
      }
      return res
    }, result)
  });
  return result;
}

module.exports = WrappingTagVerifier;
