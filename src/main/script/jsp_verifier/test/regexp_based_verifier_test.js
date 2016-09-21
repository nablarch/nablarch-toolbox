"use strict";

var assert = require("assert")
  , RegexpBasedVerifier = require("../lib/verifiers/regexp_based_verifier.js");

describe('RegexpBasedVerifier', function() {
  describe('#verify(jsp, path)', function() {
    beforeEach(function() {
      var config = {required: {"Script\\s+src": ''}, forbidden: {"META": '', "/>": "自己終了エレメント"}};
      this.sut = new RegexpBasedVerifier(config);
    });

    it('requiredに指定された正規表現にマッチするものが存在しない場合、検証エラーが発生すること。', function() {
      var result = this.sut.verify('<!doctype html>\n\n<html><body></body></html>', 'path');
      assert.ok(result.length === 1);
    });

    it('forbiddenに指定された正規表現にマッチするものが存在した場合、検証エラーが発生すること。', function() {
      var result = this.sut.verify('<!doctype html>\n' +
                                   '<!-- <%/* --> <script src=\"js/devtool.js\"></script><meta charset=\"utf-8\"><body> <!-- */%> -->\n' +
                                   '<n:set var="var" name="name" />' +
                                   '\n<html><body></body></html>', 'path');
      assert.ok(result.length === 2);
    });

    it('requiredとforbiddenの両方で検証エラーとなったものがマージされて返却されること。', function() {
      var result = this.sut.verify('<!doctype html>\n' +
                                   '<n:set var="var" name="name" />' +
                                   '\n<html><body></body></html>', 'path');
      assert.ok(result.length === 2);
    })
  });
});
