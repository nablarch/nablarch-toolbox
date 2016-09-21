"use strict";

var assert = require("assert")
  , WrappingTagVerifier = require("../lib/verifiers/wrapping_tag_verifier.js");

describe('WrappingTagVerifier', function() {
  describe('#verify(jsp, path)', function() {
    beforeEach(function() {
      var config = {
        "required": {
          "table:plain": {
            "child":"table:plain",
            "parent": "n\\:form"
          },
          "button:block": {
            "child": "button:block",
            "parent": "n\\:form"
          },
          "button": {
            "child": "button:.*",
            "parent": "button\\:block"
          }
        },
        "forbidden": {
          "body": {
            "child": "body",
            "parent": "html"
          }
        }
      };
      this.sut = new WrappingTagVerifier(config);
    });

    it('requiredに指定されたセレクタにマッチするタグが存在しない場合、検証エラーが発生すること。', function() {
      var result = this.sut.verify('<table:plain></table:plain>', 'path');
      assert.ok(result.length === 1);
    });

    it('forbiddenに指定されたセレクタにマッチするタグが存在した場合、検証エラーが発生すること。', function() {
      var result = this.sut.verify('<html><body></body></html>', 'path');
      assert.ok(result.length === 1);
    });

    it('requiredとforbiddenの両方で検証エラーとなったものがマージされて返却されること。', function() {
      var result = this.sut.verify('<html><body><table:plain></table:plain></body></html>', 'path');
      assert.ok(result.length === 2);
    })
  });
});
