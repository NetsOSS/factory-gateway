'use strict';

require([
  'angular',
  'bootstrap',
  './shared',
  './gateway/gateway'
], function (angular) {
  require(['domReady!'], function (document) {
    angular.bootstrap(document, ['gateway']);
  });
});

