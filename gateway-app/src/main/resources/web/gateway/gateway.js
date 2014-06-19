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
        }).
        when('/person/:id', {
          controller: 'PersonCtrl',
          templateUrl: templatePrefix + "person.html"
        })
        .
        when('/app/:id', {
          controller: 'AppInstCtrl',
          templateUrl: templatePrefix + "appInst.html"
        })
    ;
  });

  gateway.controller('FrontPageCtrl', function ($scope, GatewayData) {
    //$('#newPersonAlertSuccess').hide();

    $scope.showNewPersonAlert = false;

    // ----------------------- Person functions -----------
    GatewayData.PersonController.list().then(function (data) {
      $scope.persons = data;
    });

    $scope.savePerson = function () {
      GatewayData.PersonController.create($scope.person);
      console.log("Save person : ", $scope.person);
      $scope.showNewPersonAlert = true;
    };

    $scope.search = function () {
      console.log("Search : ", $scope.searchInput);
      GatewayData.PersonController.search($scope.searchInput).then(function (data) {
        $scope.searchRes = data;
      });
    };


    // ----------------------- Application Instance functions -----------
    GatewayData.ApplicationInstanceController.listAllAppInsts().then(function (data) {
      $scope.allApps = data;
    });

    $scope.saveAppInst = function () {

      console.log("Saving App Inst : ", $scope.appInst);
      $scope.newAppInstAlertSuccess =true;
      GatewayData.ApplicationInstanceController.create($scope.appInst);
    };

    $scope.appInstSearch = function () {
      console.log("Search appInst : ", $scope.searchAppInstInput);
      // GatewayData.PersonController.search($scope.searchInput).then(function (data) {
      // $scope.searchRes = data;
      //});
    };


  });




  gateway.controller('AppInstCtrl', function ($scope, $routeParams,GatewayData) {
    GatewayData.ApplicationInstanceController.findById($routeParams.id).then(function (data) {
      console.log("Data: ",data);
      $scope.appInst=data;
    });

    $scope.deleteAppInst = function(){
      console.log("Deleting id: ", $scope.app.id);
      GatewayData.ApplicationInstanceController.delete($scope.app.id);

    };


    $scope.updateAppInst = function(){
      console.log("Updating: ", $scope.appInst);
      //GatewayData.ApplicationInstanceController.delete($scope.app.id);
      GatewayData.ApplicationInstanceController.create($scope.appInst);

    };

  });


  gateway.controller('PersonCtrl', function ($scope, $routeParams) {
    $scope.personId = $routeParams.id;
  });

});
