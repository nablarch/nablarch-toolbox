"use strict";

var assert = require('assert')
  , TagAttributeVerifier = require('../lib/verifiers/tag_attribute_verifier.js');

describe('TagAttributeVerifier', function() {
  describe('#verify(jsp, path)', function() {
    beforeEach(function() {
      var config = {directory: 'test/tags', encoding: 'utf-8'};
      this.sut = new TagAttributeVerifier(config);
    });

    it('存在しない属性を使用している場合には検知されること。', function() {
      var result = this.sut.verify('<testWidget:noAttribute attribute>' +
                                   '  <testWidget:hasAttribute noSuch="attribute"></testWidget:hasAttribute>' +
                                   '</testWidget:noAttribute>', 'path');
      assert.ok(result.length === 2)
    });

    it('存在する属性のみ指定されている場合には検知されないこと。', function() {
      var result = this.sut.verify('<testWidget:noAttribute>' +
                                   '  <testWidget:hasAttribute attribute></testWidget:hasAttribute>' +
                                   '</testWidget:noAttribute>', 'path');
      assert.ok(result.length === 0)
    });

    it('チェック対象外のタグ（tagファイルの格納ディレクトリに格納されているもの以外）についてはチェックされないこと', function() {
      var result = this.sut.verify('<testWidget:notExist noSuchAttribute></testWidget:notExist>', 'path');
      assert.ok(result.length === 0)
    });
  });
});
