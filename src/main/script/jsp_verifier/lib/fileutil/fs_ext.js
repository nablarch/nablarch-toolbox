'use strict';

var fs = require('fs')
  , path = require('path');

/**
 * 指定されたディレクトリから、filterにマッチするファイルを再帰的に抽出し、
 * それらのファイルへの絶対パスのリストを返却する。
 *
 * @param dir ファイルを抽出する対象のディレクトリ
 * @param filter 抽出対象とするファイルの場合にtrueを返却するフィルター
 * @returns {Array} 抽出されたファイルへの絶対パスのリスト
 */
function FsExt_listFilesRecursively(dir, filter) {
  var results = [];
  var list = fs.readdirSync(dir);
  list.forEach(function(file) {
    file = dir + path.sep + file;
    var stat = fs.statSync(file)
      , normalized;
    if(stat && stat.isDirectory()) {
      results = results.concat(FsExt_listFilesRecursively(file, filter))
    } else {
      normalized = path.resolve(path.normalize(file));
      if(filter(normalized)) {
        results.push(normalized);
      }
    }
  });
  return results
}

module.exports = {
  listFilesRecursively: FsExt_listFilesRecursively
};
