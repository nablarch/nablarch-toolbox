"use strict";

var assert = require("assert")
  , SelectorBasedVerifier = require("../lib/verifiers/selector_based_verifier.js");

describe('SelectorBasedVerifier', function() {
  describe('#verify(jsp, path)', function() {
    beforeEach(function() {
      var config = {
        required: {"t\\:page_template:has(n\\:form)": ''},
        forbidden: {"jsp\\:attribute c\\:choose": "", "table\\:plain:not([resultSetName] [resultNumName])": ""}};
      this.sut = new SelectorBasedVerifier(config);
    });

    it('requiredに指定されたセレクタにマッチするタグが存在しない場合、検証エラーが発生すること。', function() {
      var result = this.sut.verify('<t:page_template><button:block></button:block></t:page_template>>', 'path');
      assert.ok(result.length === 1);
    });

    it('forbiddenに指定されたセレクタにマッチするタグが存在した場合、検証エラーが発生すること。', function() {
      var result = this.sut.verify('<t:page_template><jsp:attribute name="contentHtml"><n:form><button:block><c:choose></c:choose></button:block></n:form></jsp:attribute></t:page_template>>', 'path');
      assert.ok(result.length === 1);
    });

    it('requiredとforbiddenの両方で検証エラーとなったものがマージされて返却されること。', function() {
      var result = this.sut.verify('<t:page_template><jsp:attribute name="contentHtml"><button:block><c:choose></c:choose></button:block></jsp:attribute></t:page_template>>',
                                   'path');
      assert.ok(result.length === 2);
    });

    it('forbiddenに指定されたセレクタにマッチするタグが存在した場合、検証エラーが発生すること。', function() {
      var result = this.sut.verify('<t:page_template><n:form>'+
                                   '<table:plain></table:plain>'+
                                   '<table:plain resultSetName=""></table:plain>'+
                                   '<table:plain resultNumName=""></table:plain>'+
                                   '<table:plain resultSetName="" resultNumName=""></table:plain>'+
                                   '</n:form></t:page_template>>', 'path');
      assert.ok(result.length === 1);
    });
  });
});

