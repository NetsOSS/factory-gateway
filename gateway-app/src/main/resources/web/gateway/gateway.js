'use strict';

define([
  'require',
  'angular'
], function (require, angular) {
  var templatePrefix = require.toUrl("./");
  var gateway = angular.module('gateway', ['ngRoute', 'shared.services']);

  gateway.config(function ($routeProvider) {
    $routeProvider.
        when('/', {
          controller: 'FrontPageCtrl',
          templateUrl: templatePrefix + "gateway.html"
        });
  });

  gateway.controller('FrontPageCtrl', function ($scope, GatewayData) {
    GatewayData.PersonController.list().then(function (data) {
      $scope.persons =data;
    });
    $scope.savePerson = function(){
       GatewayData.PersonController.create($scope.person);
      console.log("",$scope.person);

    }
  });
});
