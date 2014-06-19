'use strict'; // Generated by DataJsGenerator
define([], function () {
  function GatewayData($http, prefix) {
    this.$http = $http;
    prefix = typeof prefix === 'string' ? prefix : '';
    this.resolve = {};
    var getData = function (res) {
      return res.data;
    };
    this.ApplicationInstanceController = {};
    this.ApplicationInstanceController.create = function (appModel) {
      var req = {};
      req.method = 'POST';
      req.url = prefix + '/data/find';
      req.data = appModel;
      req.params = {};
      return $http(req).then(getData);
    };
    this.ApplicationInstanceController.findApp = function () {
      var req = {};
      req.method = 'GET';
      req.url = prefix + '/data/find';
      req.params = {};
      return $http(req).then(getData);
    };
    this.ApplicationInstanceController.listAllApps = function () {
      var req = {};
      req.method = 'GET';
      req.url = prefix + '/data/instances';
      req.params = {};
      return $http(req).then(getData);
    };
    var ApplicationInstanceController = this.ApplicationInstanceController;
    this.resolve.ApplicationInstanceController = {};
    this.resolve.ApplicationInstanceController.findApp = function (GatewayData, $route) {
      return ApplicationInstanceController.findApp();
    };
    this.resolve.ApplicationInstanceController.listAllApps = function (GatewayData, $route) {
      return ApplicationInstanceController.listAllApps();
    };
    this.PersonController = {};
    this.PersonController.create = function (personModel) {
      var req = {};
      req.method = 'POST';
      req.url = prefix + '/data/persons';
      req.data = personModel;
      req.params = {};
      return $http(req).then(getData);
    };
    this.PersonController.list = function () {
      var req = {};
      req.method = 'GET';
      req.url = prefix + '/data/persons';
      req.params = {};
      return $http(req).then(getData);
    };
    this.PersonController.search = function (name) {
      var req = {};
      req.method = 'GET';
      req.url = prefix + '/data/search';
      req.params = {};
      req.params.name = name;
      return $http(req).then(getData);
    };
    var PersonController = this.PersonController;
    this.resolve.PersonController = {};
    this.resolve.PersonController.list = function (GatewayData, $route) {
      return PersonController.list();
    };
    this.resolve.PersonController.search = function (GatewayData, $route) {
      return PersonController.search($route.current.params.name);
    };
  }
  
  return GatewayData;
});
