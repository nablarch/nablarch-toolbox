"use strict";

var VerificationError = require(__dirname + '/../../lib/verification_error');

function CustomVerifier(config) {

}

CustomVerifier.prototype = {
  verify: function(html, path) {
    return [new VerificationError('CustomVerifier', 'custom verification was failed.')]
  }
};

module.exports = CustomVerifier;
