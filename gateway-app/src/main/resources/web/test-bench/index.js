'use strict';

define([
  'angular',
  './controllers/controllers',
  './directives/directives',
  './filters/filters',
  './services/services'
], function (angular) {
  var testBench = angular.module('test-bench', [
    'test-bench.services',
    'test-bench.controllers',
    'test-bench.filters',
    'test-bench.directives'
  ]);

  testBench.config(function ($httpProvider) {
    $httpProvider.interceptors.push('TestBenchHttpInterceptor');
  });

  require(['domReady!'], function (document) {
    try {
      angular.bootstrap(document, ['test-bench']);
    } catch (e) {
      if (console) {
        console.log('Unable to boot the application');
        if (e.stack) {
          console.log(e.stack);
        }
      }

      throw e;
    }
  });
});
