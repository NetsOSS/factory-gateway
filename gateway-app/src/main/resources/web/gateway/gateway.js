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
        .when('/group/:id', {
          controller: 'AppGroupCtrl',
          templateUrl: templatePrefix + "appGroup.html"
        })
    ;
  });

  gateway.controller('FrontPageCtrl', function ($location, $scope, GatewayData) {
    $scope.allApps = [];

    $scope.showNewPersonAlert = false;

    // ----------------------- Person functions ------------------------------------


    GatewayData.PersonController.list().then(function (data) {
      $scope.persons = data;
    });
    GatewayData.ApplicationGroupController.listAllAppGroups().then(function (data) {
      $scope.allAppGroups = data;
    });


    $scope.savePerson = function () {
      GatewayData.PersonController.create($scope.person).then(function (data) {
        console.log("Recived the new person");
        $scope.persons.push(data);

        $scope.showNewPersonAlert = true;
      });
      console.log("Save person : ", $scope.person);


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
      GatewayData.ApplicationController.create($scope.app).then(function (data) {

        $scope.allApps.push(data);
      });


    };

    // ----------------------- Load balancer functions ------------------------------------


    GatewayData.LoadBalancerController.listAllLoadBalancers().then(function (data) {
      console.log("LB list:", data);
      $scope.allLBs = data;
    });


    $scope.createLoadBalancer = function () {
      console.log("Creating new LB : ", $scope.lb);
      GatewayData.LoadBalancerController.create($scope.lb).then(function (data) {
        $scope.allLBs.push(data);
      });
      //loadLoadBalancerList();
      //listAll
      //$route.reload();
      //GatewayData.ApplicationController.create($scope.app);

    };

    // ----------------------- Application Group functions ------------------------------------

    $scope.createApplicationGroup = function () {
      console.log("New application Group : ", $scope.appGroup);
      GatewayData.ApplicationGroupController.create($scope.appGroup).then(function (data) {
        $scope.allAppGroups.push(data);
      });

    };
    GatewayData.ApplicationGroupController.listAllAppGroups().then(function (data) {
      console.log(data);
    });


  });

  //Application controller
  gateway.controller('AppCtrl', function ($scope, $routeParams, GatewayData) {
    //Find the Application object from id.
    GatewayData.ApplicationController.findById($routeParams.id).then(function (data) {
      console.log("Application data : ", data);
      $scope.app = data;

      //Find more info  (name) about the group it belongs too.
      GatewayData.ApplicationGroupController.findById($scope.app.applicationGroupId).then(function (data) {
        $scope.appGroup = data;
      });
    });


    // ----------------------- Application Instance functions ------------------------------------
    /*GatewayData.ApplicationInstanceController.listAllAppInsts().then(function (data) {
     $scope.allInstApps = data;
     });
     */
    $scope.saveAppInst = function () {

      console.log("Saving App Inst : ", $scope.appInst);
      $scope.newAppInstAlertSuccess = true;

      GatewayData.ApplicationInstanceController.create($scope.app.id, $scope.appInst).then(function (data) {
        $scope.app.applicationInstances.push(data);

      });
    };

    $scope.removeApp = function () {
      console.log("Deleting id: ", $scope.app.id);
      GatewayData.ApplicationController.remove($scope.app.id);
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

  gateway.controller('LoadBalancerFormCtrl', function ($scope) {
    console.log("LB Form Controller");
  });

  //    ----------------------- Load balancer Controller ------------------------------------
  gateway.controller('LoadBalancerCtrl', function ($scope, $routeParams, GatewayData) {


    GatewayData.LoadBalancerController.findById($routeParams.id).then(function (data) {
      console.log("Data: ", data);
      $scope.lb = data;
    });

    $scope.addAppToLB = function (appId) {
      console.log('Adding app to LB ',appId);
      GatewayData.LoadBalancerController.addApplication( $scope.lb.id,appId).then(function (data) {
        console.log('Added!!');
      });
    };

    $scope.removeAppFromLB = function (appId) {
      console.log('Remove app from LB ',appId);

    };


    GatewayData.ApplicationController.listAllApps().then(function (data) {
      $scope.allApps = data;
    });

    GatewayData.LoadBalancerController.getApplications($routeParams.id).then(function (data) {
      console.log("Apps for this LB : ", data);
      $scope.allAppsInLb = data;
    });

  });

  //    ----------------------- Load balancer Controller ------------------------------------
  gateway.controller('AppGroupCtrl', function ($scope, $routeParams, $location, GatewayData) {
    GatewayData.ApplicationGroupController.findById($routeParams.id).then(function (data) {
      console.log("AppGroup : ", data);
      $scope.group = data;
    });

    $scope.removeGroup = function () {
      GatewayData.ApplicationGroupController.remove($routeParams.id);
      $location.path("/");
    };


  });


  gateway.controller('PersonCtrl', function ($scope, $routeParams) {
    $scope.personId = $routeParams.id;
  });

});
