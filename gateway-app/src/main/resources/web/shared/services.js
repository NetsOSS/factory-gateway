'use strict';

define([
  'angular',
  './GatewayData'
], function (angular, GatewayData) {
  var services = angular.module('shared.services', []);

  services.service('GatewayData', function ($http) {
    return new GatewayData($http,window.location.pathname.replace(/\/$/,'') );
  });
});
