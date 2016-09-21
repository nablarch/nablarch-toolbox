"use strict";

require('sugar');

var fs = require('fs')
  , path = require('path');

/**
 * JSPファイル検証クラス
 * =====================================================================
 *
 * 設定情報をもとに、実際に検証を実施するクラス (`Verifier`) を生成し、それらのクラスでの検証結果を集約して返却する。
 *
 * `Verifier` についての設定は、設定ファイルに以下のように記載する。
 *
 * > __設定フォーマット (JSON)__
 * >
 * >     "verifiers": {
 * >       "Verifierのコンストラクタ名": {
 * >         // Verifier毎の設定情報
 * >       }
 * >     }
 * >
 * > __設定ファイル記載例 (verification_config.json)__
 * >
 * > 例えば、 `TagUsageVerifier` と `SelectorBasedVerifier` を利用する場合、以下のように記載する。
 * >
 * >     {
 * >       "verifiers": {
 * >         "TagUsageVerifier": {
 * >           "allowed_tags": {
 * >           }
 * >         }
 * >         "SelectorBasedVerifier": {
 * >           "required": {
 * >           }
 * >           "forbidden": {
 * >           }
 * >         }
 * >       }
 * >     }
 * >
 *
 * @class JspVerifier
 */
JspVerifier.prototype = {
  constructor: JspVerifier,

  /**
   * 登録されたVerifierを利用して検証を実施する。
   *
   * @method verify
   * @param jsp 検証対象となるJSPファイルの内容
   * @param path 検証対象JSPファイルのパス
   * @return {Array} 検証結果リスト
   */
  verify: function(jsp, path) {
    return this.verifiers.map(function(verifier) {
      return verifier.verify(jsp, path);
    }).flatten();
  }
};

/**
 * JSPの検証を行うインスタンスを生成する。
 *
 * @constructor
 * @method JspVerifier
 * @param configurations {Object} JSPの検証を行うクラスの設定情報を格納したオブジェクト。使用するキーは以下の通り。
 *
 *    * **verifiers** `Verifier` のコンストラクタ名をキーに持ち、各 `Verifier` の設定情報を値として持つオブジェクト。
 *
 * @param additionalVerifierDirs {Array | String} 独自に作成した `Verifier` を配置したディレクトリ。
 */
function JspVerifier(configurations, additionalVerifierDirs) {
  var self = this
    , verifierDirs = [__dirname + "/verifiers", additionalVerifierDirs].flatten()
    , constructors = loadVerifiers(verifierDirs);

  self.verifiers = [];
  Object.each(configurations["verifiers"], function(name, config) {
      var constructor = constructors[name];
      if(!constructor) {
        throw {message: ("設定ファイルに記載されている {name} を生成できませんでした。\n" +
                         "Verifierの配置ディレクトリは\n" +
                         "{verifierDirs}\n" +
                         "と設定されています。\n" +
                         "設定ファイルが正しいか、もしくは配置ディレクトリを正しく設定しているかを確認してください。").assign({
                                                                                   "name": name,
                                                                                   "verifierDirs": "\t" + verifierDirs.join("\n\t")
                                                                                 })};
      }
      self.verifiers.push(new constructor(config))
    }
  );
}

/**
 * verifierDirs内のすべてのファイルを読み込み、Verifierのコンストラクタを取得する。
 *
 * @param verifierDirs Verifierが格納されているディレクトリ
 * @returns {Object} Verifierの名前がキー、コンストラクタメソッドが値となっているオブジェクト
 */
function loadVerifiers(verifierDirs) {
  return verifierDirs.reduce(function(constructors, verifierDir) {
    if(verifierDir) {
      fs.readdirSync(verifierDir).each(function(file) {
        var v = require(path.resolve('.', verifierDir + "/" + file));
        constructors[v.name] = v;
      });
    }
    return constructors;
  }, {});
}

module.exports = JspVerifier;
