'use strict';

define([
  'angular',
  'angular-route',
  '../directives/directives',
  '../filters/filters',
  '../services/services'
], function (angular) {
  console.log("test-bench.controllers");

  var controllers = angular.module('test-bench.controllers', [
    'ngRoute',
    'test-bench.services',
    'test-bench.filters',
    'test-bench.directives'
  ]);

  controllers.config(function ($routeProvider) {
    $routeProvider.when('/', {
      controller: 'FrontCtrl',
      templateUrl: 'web/test-bench/templates/front.html',
      resolve: {
        scenarios: function (ScenarioService) {
          return ScenarioService.getScenarios()
        }
      }
    });
    $routeProvider.when('/scenario/:scenarioType', {
      controller: 'ValidateAndSubmitCtrl',
      templateUrl: 'web/test-bench/templates/validate-and-submit.html'
    });
//    $routeProvider.when('/scenario/:scenarioType/validation', {
//      controller: 'ValidationCtrl',
//      templateUrl: 'web/test-bench/templates/valid-invoice-validation.html'
//    });
    $routeProvider.when('/scenario/:scenarioType/processing', {
      controller: 'ProcessingCtrl',
      templateUrl: 'web/test-bench/templates/processing.html',
      resolve: {
        scenarios: function (ScenarioService) {
          return ScenarioService.getScenarios()
        }
      }
    });
//    $routeProvider.otherwise({controller: function($routeParams) {
//      console.log('Unknown location: $routeParams', $routeParams);
//      // {redirectTo: '/'}
//    }});
  });

  controllers.controller('FrontCtrl', function ($scope, $location, scenarios, ScenarioService) {
    $scope.scenarios = scenarios;
  });

  controllers.controller('ValidateAndSubmitCtrl', function ($scope, $log, $http, $location, $routeParams, ScenarioService) {
    $scope.form = {};
    $scope.scenarioType = $routeParams.scenarioType;

    $scope.state = 'upload';

    $scope.validateAndSubmitFile = function () {
      ScenarioService.validateAndSubmitFile($scope.scenarioType, $scope.form.file).then(function (res) {
        $log.info('res', res);
        $scope.state = 'done';
        $scope.result = res.data;
      });
    };
  });

//  controllers.controller('ValidationCtrl', function ($scope, $log, $location, ScenarioService) {
//   // $scope.form = {};
//    $scope.done = false;
//    $scope.validation_errors = "11";
//    ScenarioService.runValidation('VALID_INVOICE').then(function(res) {
//      $log.info('res', res);
//      $scope.done = true;
//      $scope.validation_errors = "21";
//      $scope.result = res.data;
//    });
//  });

  controllers.controller('ProcessingCtrl', function ($scope, $log, $location, $timeout, $routeParams, ScenarioService) {
    $scope.scenarioType = $routeParams.scenarioType;
    $scope.state = 'processing';

    var checkResult = function (res) {
      $log.info('result', res.data);
      var data = res.data;

      if (data.done) {
        $scope.state = 'done';
        $scope.result = data;
      } else {
        $timeout(function() {
          ScenarioService.getProcessingResult($scope.scenarioType).then(checkResult);
        }, 1000);
      }
    };

    $timeout(function() {
      ScenarioService.getProcessingResult($scope.scenarioType).then(checkResult);
    }, 1000);
    
    $scope.resendReport = function(reportId) {
    	ScenarioService.resendReport(reportId).then(resendReportHandler);
	}
    
    var resendReportHandler = function(res) {
    	$log.info('result', res.data);
    	 $scope.updateStatus = res.data.updateStatus;
	}
  });

  return controllers;
});
