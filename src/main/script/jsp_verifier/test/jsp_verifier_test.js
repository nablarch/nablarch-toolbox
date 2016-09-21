"use strict";

var assert = require("assert")
  , JspVerifier = require("../lib/jsp_verifier.js");

describe("JspVerifier", function() {
  describe("Verifierの追加のディレクトリを指定せずに生成した JspVerifier#verify(html, path) を呼び出した場合", function() {
      beforeEach(function() {
        this.sut = new JspVerifier({"verifiers": {"TagUsageVerifier": {"allowed_tags": {"html": "", "body": ""} } } });
      });
      it('正常に検証が完了する。', function() {
        var result = this.sut.verify("<html><body></body></html>", "path");
        assert.ok(result.length === 0);
      });
    }
  );

  describe('Verifierの追加の格納ディレクトリを指定して生成した JspVerifier#verify(html, path) を呼び出した場合', function() {
    beforeEach(function() {
      var config = {
        "verifiers": {
          "TagUsageVerifier": {
            "allowed_tags": {"html": "", "body": ""}
          },
          "CustomVerifier":   {}
        }
      };
      this.sut = new JspVerifier(config, ["test/verifiers", "test/another_verifiers"]);
    });

    it('追加のVerifier格納ディレクトリからCustomVerifierを読み込んで検証が行われるが、' +
      '設定ファイルに登録されていないAnotherCustomVerifierでの検証は行われない。', function() {
      var result = this.sut.verify("<html><body></body></html>", "path");
      assert.ok(result.length === 1);
      assert.ok(result[0].message === "custom verification was failed.")
    });

    it('複数のVerifierでの検証結果がマージされて返却される。', function() {
      var results = this.sut.verify("<html><body><div><p></p></div></body></html>", "path");
      assert.ok(results.length === 3);
      assert.ok(results.any(function(result) {
        return result.message === "<div> タグは、JSPファイルでは利用しないでください。";
      }));
      assert.ok(results.any(function(result) {
        return result.message === "<p> タグは、JSPファイルでは利用しないでください。";
      }));
      assert.ok(results.any(function(result) {
        return result.message === "custom verification was failed.";
      }));
    });
  });
});
