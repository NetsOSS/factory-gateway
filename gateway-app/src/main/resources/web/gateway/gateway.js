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

    $scope.hello ="yolo";

    GatewayData.PersonController.list().then(function (data) {
      $scope.persons =data;
    });
    $scope.savePerson = function(){
       GatewayData.PersonController.create($scope.person);
      console.log("",$scope.person);

    };

    $scope.search = function(){
      console.log("Search : ",$scope.searchInput);
      GatewayData.PersonController.search($scope.searchInput).then(function (data) {
        $scope.searchRes =data;
      });

    };


    // ---------------------- App instance
    $scope.saveAppInst = function(){
      //GatewayData.PersonController.create($scope.person);
      console.log("Saving App Inst : ",$scope.appInst);

    }

  });
});
