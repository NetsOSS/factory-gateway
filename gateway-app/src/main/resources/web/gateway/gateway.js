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
    ;
  });

  gateway.controller('FrontPageCtrl', function ($scope, GatewayData) {
    $('#newPersonAlertSuccess').hide();
    $scope.hello = "yolo";

    GatewayData.PersonController.list().then(function (data) {
      $scope.persons = data;
    });
    $scope.savePerson = function () {
      GatewayData.PersonController.create($scope.person);
      console.log("Save person : ", $scope.person);
      $('#newPersonAlertSuccess').show();

    };

    $scope.search = function () {
      console.log("Search : ", $scope.searchInput);
      GatewayData.PersonController.search($scope.searchInput).then(function (data) {
        $scope.searchRes = data;
      });

    };

    //GatewayData.AppInstController.list();
    // ---------------------- App instance
    /*GatewayData.AppInstController.list().then(function (data) {
      $scope.appInstList = data;
    });
  */
    $scope.saveAppInst = function () {
      //GatewayData.PersonController.create($scope.person);
      console.log("Saving App Inst : ", $scope.appInst);
      GatewayData.ApplicationInstanceController.create($scope.appInst);

    }

  });


  gateway.controller('PersonCtrl',function($scope,$routeParams){
    $scope.personId = $routeParams.id;
  });


 /* phonecatControllers.controller('PhoneDetailCtrl', ['$scope', '$routeParams',
    function($scope, $routeParams) {
      $scope.phoneId = $routeParams.phoneId;
    }]);*/
});
