'use strict';

require('sugar');

var $ = require('cheerio')
  , path = require('path');

/**
 * tagファイルのattribute定義情報のリストを取得し、返却する。
 *
 * attribute定義情報は、以下の属性値を格納したオブジェクトとなっており、すべてStringとして格納されている。
 * なお、属性値が定義されていない場合には、undefinedとして格納される。
 *
 * * name
 * * description
 * * required
 * * type
 * * rtexprvalue
 * * fragment
 *
 * @param body tagファイルの内容
 * @returns {Array} tagファイルに定義されている属性のリスト
 */
function TagFileParser_parse(body) {
  var parsed = [];
  $('<div>').append(body).find('\\%\\@attribute').each(function(idx, node) {
    var $node = $(node);
    parsed.push({
      name: $node.attr('name'),
      description: $node.attr('description'),
      required: $node.attr('required'),
      type: $node.attr('type'),
      rtexprvalue: $node.attr('rtexprvalue'),
      fragment: $node.attr('fragment')
    });
  });
  return parsed;
}

module.exports = {
  parse: TagFileParser_parse
};
