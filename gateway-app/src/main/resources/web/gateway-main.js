'use strict';

require([
  'angular',
  './shared',
  './gateway/gateway'
], function (angular) {
  require(['domReady!'], function (document) {
    angular.bootstrap(document, ['gateway']);
  });
});
