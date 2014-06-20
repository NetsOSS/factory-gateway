'use strict';

define([
  'require',
  'angular'
], function (require, angular) {
  var templatePrefix = require.toUrl("./");
  var gateway = angular.module('gateway', ['ngRoute', 'shared.services', 'shared.directives']);

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
          controller: 'AppCtrl',
          templateUrl: templatePrefix + "app.html"
        })
        .when('/appInst/:id', {
          controller: 'AppInstCtrl',
          templateUrl: templatePrefix + "appInst.html"
        })
        .when('/lb/:id', {
          controller: 'LoadBalancerCtrl',
          templateUrl: templatePrefix + "loadBalancer.html"
        })
    ;
  });

  gateway.controller('FrontPageCtrl', function ($scope, GatewayData) {


    $scope.showNewPersonAlert = false;

    // ----------------------- Person functions ------------------------------------
    function loadPersonList(){
      console.log("Loading persons");
      GatewayData.PersonController.list().then(function (data) {
        $scope.persons = data;
      });
    }
    loadPersonList();


    $scope.savePerson = function () {
       GatewayData.PersonController.create($scope.person);
      console.log("Save person : ", $scope.person);
      $scope.persons.push($scope.person);
      //console.log("Response : ", newPersonRsp);
      $scope.showNewPersonAlert = true;
      loadPersonList();
    };

    $scope.search = function () {
      console.log("Search : ", $scope.searchInput);
      GatewayData.PersonController.search($scope.searchInput).then(function (data) {
        $scope.searchRes = data;
      });
    };


    // ----------------------- Application functions ------------------------------------
    GatewayData.ApplicationController.listAllApps().then(function (data) {
      $scope.allApps = data;
    });


    $scope.createApplication = function () {
      console.log("New application : ", $scope.app);
      GatewayData.ApplicationController.create($scope.app);
      $scope.allApps.push($scope.app);

    };

    // ----------------------- Load balancer functions ------------------------------------


    function loadLoadBalancerList(){
      GatewayData.LoadBalancerController.listAllLoadBalancers().then(function (data) {
        console.log("LB list:",data);
        $scope.allLBs = data;
      });
    }
    loadLoadBalancerList();
    $scope.createLoadBalancer = function () {
      console.log("Creating new LB : ", $scope.lb);
      GatewayData.LoadBalancerController.create($scope.lb);
      loadLoadBalancerList();
      //listAll
      //$route.reload();
      //GatewayData.ApplicationController.create($scope.app);

    };




  });

  //Application controller
  gateway.controller('AppCtrl', function ($scope, $routeParams, GatewayData) {
    //Find the Application object from id.
    GatewayData.ApplicationController.findById($routeParams.id).then(function (data) {
      console.log("Data: ", data);
      $scope.app = data;
    });

    // ----------------------- Application Instance functions ------------------------------------
    GatewayData.ApplicationInstanceController.listAllAppInsts().then(function (data) {
      $scope.allInstApps = data;
    });

    $scope.saveAppInst = function () {

      console.log("Saving App Inst : ", $scope.appInst);
      $scope.newAppInstAlertSuccess = true;
      GatewayData.ApplicationInstanceController.create($scope.appInst);
    };

    $scope.removeApp = function () {
      console.log("Deleting id: ", $scope.app.id);
      //GatewayData.ApplicationInstanceController.remove($scope.appInst.id);

    };

    $scope.appInstSearch = function () {
      console.log("Search appInst : ", $scope.searchAppInstInput);
      // GatewayData.PersonController.search($scope.searchInput).then(function (data) {
      // $scope.searchRes = data;
      //});
    };

  });


  gateway.controller('AppInstCtrl', function ($scope, $routeParams, GatewayData) {
    GatewayData.ApplicationInstanceController.findById($routeParams.id).then(function (data) {
      console.log("Data: ", data);
      $scope.appInst = data;
    });

    $scope.deleteAppInst = function () {
      console.log("Deleting id: ", $scope.appInst.id);
      GatewayData.ApplicationInstanceController.remove($scope.appInst.id);

    };


    $scope.updateAppInst = function () {
      console.log("Updating: ", $scope.appInst);
      //GatewayData.ApplicationInstanceController.delete($scope.app.id);
      GatewayData.ApplicationInstanceController.update($scope.appInst.id, $scope.appInst);

    };

  });

 //    ----------------------- Load balancer Controller ------------------------------------
  gateway.controller('LoadBalancerCtrl', function ($scope, $routeParams, GatewayData) {

    GatewayData.LoadBalancerController.findById($routeParams.id).then(function (data) {
      console.log("Data: ", data);
      $scope.lb = data;
    });

  });


  gateway.controller('PersonCtrl', function ($scope, $routeParams) {
    $scope.personId = $routeParams.id;
  });

});
