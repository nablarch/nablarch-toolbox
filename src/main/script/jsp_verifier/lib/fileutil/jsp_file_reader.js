'use strict';

var fs = require('fs');

/**
 * 以下の正規化処理を行って、JSPファイルを読み込む。
 *
 * * devtool.js読み込み用ののマジックコメントを除去
 * * JSPディレクティブ（<%@）の後のスペースを除去
 *
 * @param path 読み込むファイルのパス
 * @param encoding 読み込むファイルのエンコーディング
 * @returns {String} 正規化されて読み込まれたJSPファイルの内容
 */
function JspFileReader_readFormatted(path, encoding) {
  var DEVTOOL_MAGIC_COMMENT = '<!-- <%/* --> <script src="js/devtool.js"></script><meta charset="utf-8"><body> <!-- */%> -->'
    , jsp = fs.readFileSync(path, encoding);
  return jsp.replace(DEVTOOL_MAGIC_COMMENT, '').replace(/<%@\s+([a-zA-Z]+)\s/mg, '<%@$1 ');
}

module.exports = {
  readFormatted: JspFileReader_readFormatted
};
