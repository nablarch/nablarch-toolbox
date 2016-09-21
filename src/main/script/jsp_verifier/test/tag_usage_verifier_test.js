"use strict";

var assert = require('assert')
  , TagUsageVerifier = require('../lib/verifiers/tag_usage_verifier.js');

describe('TagUsageVerifier', function() {
  describe('#verify(html, path)', function() {
    beforeEach(function() {
      this.sut = new TagUsageVerifier({"allowed_tags": {"html": "", "body": ""}});
    });

    it('使用可能タグのみの場合は、検証エラーが0件となる。', function() {
      assert.ok(this.sut.verify("<html><body></body></html>", "path").length === 0);
    });

    it('使用不可のタグが存在する場合は、使用不可のタグの件数だけ、検証エラーが返却される。', function() {
      var results = this.sut.verify("<html><body><div><p></p></div></body></html>", "path");
      assert.ok(results.length === 2);
    });

    it('使用可能タグの設定値の大文字・小文字は無視して検証されること。', function() {
      var sut = new TagUsageVerifier({"allowed_tags": {"HTML": "", "body": ""}});
      assert.ok(sut.verify("<html><body></body></html>", "path").length === 0)
    });
  })
});
