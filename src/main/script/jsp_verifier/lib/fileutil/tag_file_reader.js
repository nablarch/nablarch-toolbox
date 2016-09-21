'use strict';

require('sugar');

var reader = require('./jsp_file_reader')
  , path = require('path')
  , fsExt = require('./fs_ext')
  , parser = require('../parser/tag_file_parser');

/**
 * 指定されたディレクトリに格納されているtagファイルを読み込み、
 * それぞれのtagの定義情報のリストを返却する。
 *
 * tagの定義情報は以下の形式で格納されている。
 *
 * name
 * : tag名。指定されたディレクトリをベースとして、配下のディレクトリ名とファイル名を、dirname:filename形式で結合した値。
 *
 * attributes
 * : tagファイルの内容をTagFileParserでパースした内容
 *
 * @param directory tagファイルの格納されているディレクトリ
 * @param encoding tagファイルのエンコーディング
 * @returns {Array} 読み込まれたtagファイルのパース結果リスト（キー：tag名、値：tagファイルの定義情報）
 */
function TagFileReader_loadAllTags(directory, encoding) {
  directory = path.resolve(path.normalize(directory));
  return fsExt.listFilesRecursively(directory, function(name) {
    return path.extname(name) === '.tag'
  }).reduce(function(loaded, file) {
    var parsed = {};
    // 指定されたディレクトリ名の末尾に\や/がついているかどうか分からないので、
    // ディレクトリ名除去後に、先頭の\と/を除去する。
    parsed['name'] = file.replace(directory, '').replace(/^[\/\\]/, '').replace(/[\/\\]/g, ':').replace(/\.tag$/, '');
    parsed['attributes'] = parser.parse(reader.readFormatted(file, encoding));
    loaded.push(parsed);
    return loaded
  }, []);
}

module.exports = {
  loadAllTags: TagFileReader_loadAllTags
};
