"use strict";

require('sugar');

var $ = require('cheerio')
  , VerificationError = require('../verification_error');

/**
 * DOM検証クラス
 * =====================================================================
 *
 * JSPファイル内に、指定されたセレクタにマッチするノードが存在(する|しない)かどうかを検証する。
 *
 * セレクタは以下のように指定する。なお、jQueryと似たセレクタを使用できるが、完全に互換性があるわけではない。
 * 使用できるセレクタの詳細が必要な場合には、 http://matthewmueller.github.io/cheerio/ を参照すること。
 *
 * また、field:textなどのウィジェットをセレクタで指定する場合には、"field\\\\:text"と、":"をエスケープして記載する必要があることに注意。
 *
 * > __設定フォーマット (JSON)__
 * >
 * >     "required": {
 * >       "ファイル内に必ず一箇所以上はマッチするノードが存在しなくてはいけないセレクタ": "コメント"
 * >     },
 * >     "forbidden": {
 * >       "ファイル内で一致する箇所が存在してはいけないセレクタ": "コメント"
 * >     }
 * >
 * > __設定ファイル記載例 (verification_config.json)__
 * >
 * >     "required": {
 * >     },
 * >     "forbidden": {
 * >       "table:not([listSearchInfoName])": "テーブルにはlistSearchInfoNameがないと結果件数が表示されないため、listSearchInfoNameを強制。",
 * >       "table:not([id])" : "テーブルを複数表示する場合にIDが必須となるため、テーブルにはIDを強制。"
 * >     }
 *
 * @class SelectorBasedVerifier
 */
SelectorBasedVerifier.prototype = {
  constructor: SelectorBasedVerifier,
  verify: SelectorBasedVerifier_verify,

  /**
   * マッチしなくてはいけないセレクタのリスト
   *
   * @param required
   * @type Array
   * @default []
   */
  required: [],

  /**
   * マッチしてはいけないセレクタのリスト
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
 * @method SelectorBasedVerifier
 * @constructor
 * @param {Object} config 設定情報を格納したオブジェクト。使用するキーは以下の通り。
 *
 *    * **required** 必ずマッチしなくてはいけないセレクタをキーに持ち、コメントを値としてもつオブジェクト。
 *    * **forbidden** マッチしてはいけないセレクタをキーに持ち、コメントを値としてもつオブジェクト。
 */
function SelectorBasedVerifier(config) {
  this.required = Object.keys(config['required']);
  this.forbidden = Object.keys(config['forbidden']);
}

/**
 * JSPファイル内に、 required に指定されているセレクタにマッチするノードが存在すること、
 * および、forbiddenに指定されているセレクタにマッチするノードが存在しないことを検証する。
 *
 * @method verify
 * @param {String} jsp 検証対象とするJSPファイルの内容
 * @param {String} path 検証対象ファイルのパス
 * @return {Array} 検証結果 ({{#crossLink "VerificationError"}}{{/crossLink}}) を格納した配列
 */
function SelectorBasedVerifier_verify(jsp, path) {
  var $jsp = $('<div>').append($(jsp))
    , result;
  result = this.required.reduce(function(res, r) {
    if ($jsp.find(r).length === 0) {
      res.push(new VerificationError('SelectorBasedVerifier', r + ' にマッチするタグが検出されませんでした。', path, ''));
    }
    return res;
  }, []);
  result = this.forbidden.reduce(function(res, r) {
    var found = $jsp.find(r);
    if (found.length !== 0) {
      res.push(new VerificationError('SelectorBasedVerifier', r + ' にマッチするタグが検出されました。', path, $('<div>').append(found).html()));
    }
    return res;
  }, result);
  return result;
}

module.exports = SelectorBasedVerifier;
