'use strict';
define([
  'angular'
], function (angular) {
  console.log("test-bench.services");

    //creating the angular module
  var services = angular.module('test-bench.services', []);

  services.service('ScenarioService', function ($http) {
    return {
      getScenarios: function () {
        return $http.get('data/scenario').then(function (res) {
          return res.data;
        });
      },
      getScenarioByType: function (type) {
        return $http.get('data/scenario/type').then(function (res) {
          return res.data;
        });
      },
      validateAndSubmitFile: function (scenarioType, file) {
        var formData = new FormData();
        formData.append("file", file);

        return $http.post('data/scenario/' + scenarioType + '/validate-and-submit', formData, {
          transformRequest: angular.identity,
          headers: {'Content-Type': undefined}
        });
      },
      getProcessingResult: function (scenarioType) {
        return $http.get('data/scenario/' + scenarioType + '/processing-result');
      },
      resendReport: function (notifcationId) {
          return $http.post('data/scenario/update/'+notifcationId);
        }
    }
  });

  // This service and initialization has a race condition. It will work as the CSRF token is only required when doing
  // POST requests and by that time the token should(!) have been loaded by the client.

  // If this leads to failures we need to figure a way for the server to send the token earlier.
  var token = undefined;

  services.factory('TestBenchHttpInterceptor', function () {
    return {
      request: function (config) {
        if (token) {
          config.headers['x-csrf-token'] = token;
        }

        return config;
      }
    };
  });

  services.run(function($http, $log) {
    $log.info('getting csrf token');
    $http.get('/csrf').then(function (res) {
      $log.info('csrf: ', res.data);
      token = res.data;
    });
  });

  return services;
});
