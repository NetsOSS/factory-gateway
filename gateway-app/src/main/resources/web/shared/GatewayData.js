'use strict'; // Generated by DataJsGenerator
define([], function () {
  function GatewayData($http, prefix) {
    this.$http = $http;
    prefix = typeof prefix === 'string' ? prefix : '';
    this.resolve = {};
    var getData = function (res) {
      return res.data;
    };
    this.ApplicationController = {};
    this.ApplicationController.create = function (appModel) {
      var req = {};
      req.method = 'POST';
      req.url = prefix + '/data/applications';
      req.data = appModel;
      req.params = {};
      return $http(req).then(getData);
    };
    this.ApplicationController.findById = function (id) {
      var req = {};
      req.method = 'GET';
      req.url = prefix + '/data/applications/{id}';
      req.url = req.url.replace(/{id}/, id);
      req.params = {};
      return $http(req).then(getData);
    };
    this.ApplicationController.listAllApps = function () {
      var req = {};
      req.method = 'GET';
      req.url = prefix + '/data/applications';
      req.params = {};
      return $http(req).then(getData);
    };
    this.ApplicationController.search = function (name) {
      var req = {};
      req.method = 'GET';
      req.url = prefix + '/data/applications/find';
      req.params = {};
      req.params.name = name;
      return $http(req).then(getData);
    };
    var ApplicationController = this.ApplicationController;
    this.resolve.ApplicationController = {};
    this.resolve.ApplicationController.findById = function (GatewayData, $route) {
      return ApplicationController.findById($route.current.params.id);
    };
    this.resolve.ApplicationController.listAllApps = function (GatewayData, $route) {
      return ApplicationController.listAllApps();
    };
    this.resolve.ApplicationController.search = function (GatewayData, $route) {
      return ApplicationController.search($route.current.params.name);
    };
    this.ApplicationGroupController = {};
    this.ApplicationGroupController.create = function (appGroupModel) {
      var req = {};
      req.method = 'POST';
      req.url = prefix + '/data/application-group';
      req.data = appGroupModel;
      req.params = {};
      return $http(req).then(getData);
    };
    this.ApplicationGroupController.findById = function (id) {
      var req = {};
      req.method = 'GET';
      req.url = prefix + '/data/application-group/{id}';
      req.url = req.url.replace(/{id}/, id);
      req.params = {};
      return $http(req).then(getData);
    };
    this.ApplicationGroupController.listAllAppGroups = function () {
      var req = {};
      req.method = 'GET';
      req.url = prefix + '/data/application-groups';
      req.params = {};
      return $http(req).then(getData);
    };
    this.ApplicationGroupController.search = function (name) {
      var req = {};
      req.method = 'GET';
      req.url = prefix + '/data/application-group/find';
      req.params = {};
      req.params.name = name;
      return $http(req).then(getData);
    };
    var ApplicationGroupController = this.ApplicationGroupController;
    this.resolve.ApplicationGroupController = {};
    this.resolve.ApplicationGroupController.findById = function (GatewayData, $route) {
      return ApplicationGroupController.findById($route.current.params.id);
    };
    this.resolve.ApplicationGroupController.listAllAppGroups = function (GatewayData, $route) {
      return ApplicationGroupController.listAllAppGroups();
    };
    this.resolve.ApplicationGroupController.search = function (GatewayData, $route) {
      return ApplicationGroupController.search($route.current.params.name);
    };
    this.ApplicationInstanceController = {};
    this.ApplicationInstanceController.create = function (appInstModel) {
      var req = {};
      req.method = 'POST';
      req.url = prefix + '/data/instances';
      req.data = appInstModel;
      req.params = {};
      return $http(req).then(getData);
    };
    this.ApplicationInstanceController.findById = function (id) {
      var req = {};
      req.method = 'GET';
      req.url = prefix + '/data/instances/{id}';
      req.url = req.url.replace(/{id}/, id);
      req.params = {};
      return $http(req).then(getData);
    };
    this.ApplicationInstanceController.listAllAppInsts = function () {
      var req = {};
      req.method = 'GET';
      req.url = prefix + '/data/instances';
      req.params = {};
      return $http(req).then(getData);
    };
    this.ApplicationInstanceController.remove = function (id) {
      var req = {};
      req.method = 'DELETE';
      req.url = prefix + '/data/instances/{id}';
      req.url = req.url.replace(/{id}/, id);
      req.params = {};
      return $http(req).then(getData);
    };
    this.ApplicationInstanceController.search = function (name) {
      var req = {};
      req.method = 'GET';
      req.url = prefix + '/data/find';
      req.params = {};
      req.params.name = name;
      return $http(req).then(getData);
    };
    this.ApplicationInstanceController.update = function (id, appInstModel) {
      var req = {};
      req.method = 'PUT';
      req.url = prefix + '/data/instances/{id}';
      req.url = req.url.replace(/{id}/, id);
      req.data = appInstModel;
      req.params = {};
      return $http(req).then(getData);
    };
    var ApplicationInstanceController = this.ApplicationInstanceController;
    this.resolve.ApplicationInstanceController = {};
    this.resolve.ApplicationInstanceController.findById = function (GatewayData, $route) {
      return ApplicationInstanceController.findById($route.current.params.id);
    };
    this.resolve.ApplicationInstanceController.listAllAppInsts = function (GatewayData, $route) {
      return ApplicationInstanceController.listAllAppInsts();
    };
    this.resolve.ApplicationInstanceController.search = function (GatewayData, $route) {
      return ApplicationInstanceController.search($route.current.params.name);
    };
    this.LoadBalancerController = {};
    this.LoadBalancerController.create = function (loadBalancerModel) {
      var req = {};
      req.method = 'POST';
      req.url = prefix + '/data/loadbalancers';
      req.data = loadBalancerModel;
      req.params = {};
      return $http(req).then(getData);
    };
    this.LoadBalancerController.findLoadBalancerById = function (id) {
      var req = {};
      req.method = 'GET';
      req.url = prefix + '/data/loadbalancers/{id}';
      req.url = req.url.replace(/{id}/, id);
      req.params = {};
      return $http(req).then(getData);
    };
    this.LoadBalancerController.findLoadBalancerBySshKey = function (sshKey) {
      var req = {};
      req.method = 'GET';
      req.url = prefix + '/data/loadbalancers/{sshKey}';
      req.url = req.url.replace(/{sshKey}/, sshKey);
      req.params = {};
      return $http(req).then(getData);
    };
    this.LoadBalancerController.listAllLoadBalancers = function () {
      var req = {};
      req.method = 'GET';
      req.url = prefix + '/data/loadbalancers';
      req.params = {};
      return $http(req).then(getData);
    };
    var LoadBalancerController = this.LoadBalancerController;
    this.resolve.LoadBalancerController = {};
    this.resolve.LoadBalancerController.findLoadBalancerById = function (GatewayData, $route) {
      return LoadBalancerController.findLoadBalancerById($route.current.params.id);
    };
    this.resolve.LoadBalancerController.findLoadBalancerBySshKey = function (GatewayData, $route) {
      return LoadBalancerController.findLoadBalancerBySshKey($route.current.params.sshKey);
    };
    this.resolve.LoadBalancerController.listAllLoadBalancers = function (GatewayData, $route) {
      return LoadBalancerController.listAllLoadBalancers();
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
