'use strict';

define([
  'require',
  'angular'
], function (require, angular) {
  var templatePrefix = require.toUrl("./");
  var gateway = angular.module('gateway', ['ngRoute', 'shared.services']);

  gateway.config(function($routeProvider) {
    $routeProvider.
      when('/', {
        controller: 'FrontPageCtrl',
        templateUrl: templatePrefix + "gateway.html"
      });
  });

  gateway.controller('FrontPageCtrl', function($scope, GatewayData) {
    $scope.persons = GatewayData.PersonController.list();
  });
});
