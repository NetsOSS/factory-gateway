'use strict';

require([
  'angular',
  'angular-ui-sortable',
  'bootstrap',
  './shared',
  './gateway/gateway'
], function (angular) {
  require(['domReady!'], function (document) {
    angular.bootstrap(document, ['gateway']);
  });
});

