"use strict";

/**
 * 検証結果を表すクラス。
 *
 * @class VerificationError
 */
VerificationError.prototype = {
  constructor: VerificationError,

  /**
   * 検証を実施したクラス名。
   *
   * @property verifier
   * @type String
   */
  verifier: null,

  /**
   * エラー内容を表すメッセージ。
   *
   * @property message
   * @type String
   */
  message: null,

  /**
   * このエラーが発生したファイルのパス
   *
   * @property path
   * @type String
   */
  path: null,

  /**
   * 検証エラーとなったノードの内容
   *
   * @property path
   * @type String
   */
  contents: null
};

/**
 * 検証結果を生成する。
 *
 * @constructor
 * @method VerificationError
 * @param {String} verifier 検証を実施したクラス名。
 * @param {String} message エラー内容を表すメッセージ
 * @param {String} path 検証エラーとなったファイルのパス
 * @param {String} contents 検証エラーとなったノードの内容
 */
function VerificationError(verifier, message, path, contents) {
  this.verifier = verifier;
  this.message = message;
  this.path = path;
  this.contents = contents;
}

module.exports = VerificationError;
