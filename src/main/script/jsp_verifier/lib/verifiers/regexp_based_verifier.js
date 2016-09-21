"use strict";

require('sugar');

var VerificationError = require('../verification_error');

/**
 * 正規表現検証クラス
 * =====================================================================
 *
 * JSPファイル内に、指定された正規表現にマッチする文字列が存在(する|しない)かどうかを検証する（大文字・小文字は区別しない。）。
 *
 * 正規表現は以下のように指定する。
 *
 * > __設定フォーマット (JSON)__
 * >
 * >     "required": {
 * >       "ファイル内で必ず一箇所以上は一致しなくてはいけない正規表現": "コメント"
 * >     },
 * >     "forbidden": {
 * >       "ファイル内で一致する箇所が存在してはいけない正規表現": "コメント"
 * >     }
 * >
 * > __設定ファイル記載例 (verification_config.json)__
 * >
 * >     "required": {
 * >     },
 * >     "forbidden": {
 * >         "/>": "自己終了エレメントを利用すると、その要素以降の記述内容が描画されなくなるため禁止。"
 * >     }
 *
 * @class RegexpBasedVerifier
 */
RegexpBasedVerifier.prototype = {
  constructor: RegexpBasedVerifier,
  verify: RegexpBasedVerifier_verify,

  /**
   * マッチしなくてはいけない正規表現のリスト
   *
   * @property required
   * @type Array
   * @default []
   */
  required: [],

  /**
   * マッチしてはいけない正規表現のリスト
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
 * @method RegexpBasedVerifier
 * @constructor
 * @param {Object} config 設定情報を格納したオブジェクト。使用するキーは以下の通り。
 *
 *    * **required** 必ずマッチしなくてはいけない正規表現をキーに持ち、コメントを値としてもつオブジェクト。
 *    * **forbidden** マッチしてはいけない正規表現をキーに持ち、コメントを値としてもつオブジェクト。
 */
function RegexpBasedVerifier(config) {
  this.required = Object.keys(config['required']);
  this.forbidden = Object.keys(config['forbidden']);
}

/**
 * JSPファイル内に、 required に指定されている正規表現にマッチする文字列が存在すること、
 * および、forbiddenに指定されている正規表現にマッチする文字列が存在しないことを検証する。
 *
 * @method verify
 * @param {String} jsp 検証対象とするJSPファイルの内容
 * @param {String} path 検証対象ファイルのパス
 * @return {Array} 検証結果 ({{#crossLink "VerificationError"}}{{/crossLink}}) を格納した配列
 */
function RegexpBasedVerifier_verify(jsp, path) {
  var self = this
    , lines = jsp.split('\n')
    , result;
  result = self.required.reduce(function(res, r) {
    if (!jsp.match(new RegExp(r, "i"))) {
      res.push(new VerificationError('RegexpBasedVerifier', r + ' にマッチする文字列が検出されませんでした。', path, ''));
    }
    return res;
  }, []);
  result = lines.reduce(function(res, line, n) {
    self.forbidden.each(function(r) {
      if (line.match(new RegExp(r, "i"))) {
        res.push(new VerificationError('RegexpBasedVerifier', (n + 1) + '行目に ' + r + ' にマッチする文字列が検出されました。', path, line));
      }
    });
    return res;
  }, result);
  return result;
}

module.exports = RegexpBasedVerifier;
