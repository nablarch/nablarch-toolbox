"use strict";

var VerificationError = require(__dirname + '/../../lib/verification_error');

function AnotherCustomVerifier(config) {

}

AnotherCustomVerifier.prototype = {
  verify: function(html, path) {
    return [new VerificationError('AnotherCustomVerifier', 'another custom verification was failed.')]
  }
};

module.exports = AnotherCustomVerifier;
