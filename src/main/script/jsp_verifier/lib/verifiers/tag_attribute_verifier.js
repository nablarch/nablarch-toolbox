"use strict";

require('sugar');

var $ = require('cheerio')
  , reader = require('../fileutil/tag_file_reader')
  , VerificationError = require('../verification_error');

/**
 * タグ属性検証クラス
 * =====================================================================
 *
 * タグに設定されている属性が、tagファイルで実際に定義されている属性かどうかを検証する。
 *
 * 下記の設定で、指定されているディレクトリに格納されているタグファイルに対してのみ、属性値の検証が行われる。
 *
 * > __設定フォーマット (JSON)__
 * >
 * >     "directory": "タグファイルが格納されているディレクトリのパス",
 * >     "encoding": "タグファイルのエンコーディング"
 * >
 * > __設定ファイル記載例 (verification_config.json)__
 * >
 * >     "directory": "C:\\nablarch\\tutorial\\main\\web\\WEB-INF\\tags\\widget",
 * >     "encoding": "utf-8"
 *
 * @class TagAttributeVerifier
 */
TagAttributeVerifier.prototype = {
  constructor: TagAttributeVerifier,
  verify: TagAttributeVerifier_verify,

  /**
   * 指定されたディレクトリから読み込んだタグファイルの定義情報
   *
   * @property definitions
   * @type Array
   * @default []
   */
  definitions: null
};

/**
 * コンストラクタ関数
 *
 * @method TagAttributeVerifier
 * @constructor
 * @param {Object} config 設定情報を格納したオブジェクト。使用するキーは以下の通り。
 *
 *    * **directory** タグファイルが格納されているディレクトリのパス
 *    * **encoding** タグファイルのエンコーディング
 */
function TagAttributeVerifier(config) {
  this.definitions = reader.loadAllTags(config['directory'], config['encoding']);
}

/**
 * JSPファイルで使用されているタグの属性が、実際に定義されているものであることを検証する。
 *
 * @method verify
 * @param {String} jsp 検証対象とするJSPファイルの内容
 * @param {String} path 検証対象ファイルのパス
 * @return {Array} 検証結果 ({{#crossLink "VerificationError"}}{{/crossLink}}) を格納した配列
 */
function TagAttributeVerifier_verify(jsp, path) {
  var $nodes = $('<div>').append(jsp).find('*')
    , self = this
    , results = [];

  $nodes.each(function(res, node) {
    var def = self.definitions.find(function(def) {
      return def.name.toLowerCase() === node.name.toLowerCase();
    });
    if (def) {
      var $node = $(node);
      Object.each($node.attr(), (function(attr) {
        var found = def.attributes.find(function(d) {
          return d.name.toLowerCase() === attr.toLowerCase();
        });
        if (!found) {
          results.push(new VerificationError('TagAttributeVerifier',
                                             '<' + node.name + '> タグには、' + attr + '属性は定義されていません。' +
                                             '定義されているのは、' + def.attributes.map('name').join(', ') + 'です。',  path, $('<div>').append($node).html()))
        }
      }))
    }
  });

  return results;
}

module.exports = TagAttributeVerifier;
