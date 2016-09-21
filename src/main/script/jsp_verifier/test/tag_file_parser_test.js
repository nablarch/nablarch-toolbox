"use strict";

var assert = require("assert")
  , parser = require("../lib/parser/tag_file_parser.js");

describe("TagFileParser", function() {
  describe('#parse(body)', function() {
    it('attribute定義の内容を取得できること。', function() {
      var definitions = parser.parse('<%@attribute' +
                                     ' name="attribute name"' +
                                     ' description="attribute description"' +
                                     ' required="is attribute required"' +
                                     ' type="attribute type"' +
                                     ' rtexprvalue="is attribute rtexpvalue"' +
                                     ' fragment="is attribute fragment" %>');
      assert.ok(definitions[0]['name'] === 'attribute name');
      assert.ok(definitions[0]['description'] === 'attribute description');
      assert.ok(definitions[0]['required'] === 'is attribute required');
      assert.ok(definitions[0]['type'] === 'attribute type');
      assert.ok(definitions[0]['rtexprvalue'] === 'is attribute rtexpvalue');
      assert.ok(definitions[0]['fragment'] === 'is attribute fragment')
    });

    it('定義されていない属性はundefinedになること。', function() {
      var definitions = parser.parse('<%@attribute %>');
      assert.ok(definitions[0]['name'] === undefined);
      assert.ok(definitions[0]['description'] === undefined);
      assert.ok(definitions[0]['required'] === undefined);
      assert.ok(definitions[0]['type'] === undefined);
      assert.ok(definitions[0]['rtexprvalue'] === undefined);
      assert.ok(definitions[0]['fragment'] === undefined);
    });

    it('複数のattribute定義の内容を取得できること。', function() {
      var definitions = parser.parse('<%@tag%><%@taglib%>'+
                                     '<%@attribute' +
                                     ' name="attribute name"' +
                                     ' description="attribute description"' +
                                     ' required="is attribute required"' +
                                     ' type="attribute type"' +
                                     ' rtexprvalue="is attribute rtexpvalue"' +
                                     ' fragment="is attribute fragment" %>\r\n' +
                                     '<%@attribute' +
                                     ' name="attribute2 name"' +
                                     ' description="attribute2 description"' +
                                     ' required="is attribute2 required"' +
                                     ' type="attribute2 type"' +
                                     ' rtexprvalue="is attribute2 rtexpvalue"' +
                                     ' fragment="is attribute2 fragment" %>\r\n');
      assert.ok(definitions[0]['name'] === 'attribute name');
      assert.ok(definitions[0]['description'] === 'attribute description');
      assert.ok(definitions[0]['required'] === 'is attribute required');
      assert.ok(definitions[0]['type'] === 'attribute type');
      assert.ok(definitions[0]['rtexprvalue'] === 'is attribute rtexpvalue');
      assert.ok(definitions[0]['fragment'] === 'is attribute fragment')

      assert.ok(definitions[1]['name'] === 'attribute2 name');
      assert.ok(definitions[1]['description'] === 'attribute2 description');
      assert.ok(definitions[1]['required'] === 'is attribute2 required');
      assert.ok(definitions[1]['type'] === 'attribute2 type');
      assert.ok(definitions[1]['rtexprvalue'] === 'is attribute2 rtexpvalue');
      assert.ok(definitions[1]['fragment'] === 'is attribute2 fragment')
    });
  })
});
