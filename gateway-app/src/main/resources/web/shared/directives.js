'use strict';

define([ 'angular' ], function (angular) {
  var directives = angular.module('shared.directives', []);


  var INTEGER_REGEXP = /^\-?\d+$/;
  directives.directive('integer', function () {

    return {
      require: 'ngModel',
      link: function (scope, elm, attrs, ctrl) {
        ctrl.$parsers.unshift(function (viewValue) {
          if (INTEGER_REGEXP.test(viewValue)) {
            // it is valid
            ctrl.$setValidity('integer', true);
            return viewValue;
          } else {
            // it is invalid, return undefined (no model update)
            ctrl.$setValidity('integer', false);
            return undefined;
          }
        });
      }
    };
  });

  directives.directive('lbForm', function () {
    return {
      restrict: 'E',
      templateUrl: 'web/gateway/lbform.html',
      //template: "<div><span>{{mandateperson.id}}<span><input type='text' ng-model='mandateperson.firstname' /></div>",
      //scope: { mandateperson: '=' },
      scope: false,
      controller: function ($scope, $routeParams, GatewayData) {
        $scope.isNewLb = $scope.lb == null;
        console.log('LB in form: ', $scope.lb);
        if ($scope.lb != null) {
          $scope.isNewLb = $scope.lb.id == null;
          console.log('cecking id');
        }

        $scope.updateOrCreateLB = function () {
          console.log('LB in form: ', $scope.lb);
          if ($scope.lb.id != null) {
            console.log('Updateing LB');
            $scope.lb.applications = [];
            GatewayData.LoadBalancerController.update($scope.lb.id, $scope.lb);
          } else {
            console.log('Createing LB');
            GatewayData.LoadBalancerController.create($scope.lb).then(function (data) {
              $scope.allLBs.push(data);
            });
          }
        };
      }
      //controller: 'LoadBalancerFormCtrl'
    };
  });

  directives.directive('appForm', function () {
    return {
      restrict: 'E',
      templateUrl: 'web/gateway/appform.html',
      scope: {
        appObj: '=',
        onCreated: '='
      },
      controller: function ($scope, $routeParams, GatewayData) {
       // console.log('$scope.onCreated', $scope.onCreated);
       // console.log('$scope.$parent[$scope.onCreated]', $scope.$parent[$scope.onCreated]);

        $scope.localApp = angular.copy($scope.appObj);
        var isAppNew = $scope.localApp === undefined;
        //console.log('IsNew : ', isAppNew);
        if (isAppNew)
          $scope.localApp = {};
        else {
          //No need to pass this information. also breaks the server if sent
          delete $scope.localApp.loadBalancers;
          delete $scope.localApp.applicationInstances;
        }

        $scope.createOrUpdateApplication = function () {
          if (isAppNew) {
            console.log('Creating NEW');
            GatewayData.ApplicationController.create($scope.localApp).then(function (data) {
              $scope.onCreated(data);
            });
          } else {
            GatewayData.ApplicationController.update($scope.localApp.id, $scope.localApp).then(function(data){
              console.log('Updating exisitng');
              $scope.onCreated(data);
            });

          }
        };

        GatewayData.ApplicationGroupController.listAllAppGroups().then(function (data) {
          $scope.allAppGroups = data;
        });

      }
    };
  });

  directives.directive('backButton', function () {
    return {
      restrict: 'A',

      link: function (scope, element, attrs) {
        element.bind('click', goBack);

        function goBack() {
          history.back();
          scope.$apply();
        }
      }
    }
  });


  directives.directive("clickToEdit", function() {
    var editorTemplate = '<div class="click-to-edit">' +
        '<div ng-hide="view.editorEnabled">' +
        '{{value}} ' +
        ' <a><span ng-click="enableEditor()" class="glyphicon glyphicon-wrench pull-right"></span></a>'+
        '' +
        '</div>' +
        '<div ng-show="view.editorEnabled">' +
        '<input class="form-control" ng-model="view.editableValue">' +
            '<span class="pull-right">'+
        '<a ng-click="save()">Save</a>' +
        ' or ' +
        '<a ng-click="disableEditor()">cancel</a>.</span>' +
        '</div>' +
        '</div>';

    return {
      restrict: "A",
      replace: true,
      template: editorTemplate,
      scope: {
        value: "=clickToEdit",
        saveCallback : "&saveFunction"
        //callback:
      },
      controller: function($scope,$timeout) {
        $scope.view = {
          editableValue: $scope.value,
          editorEnabled: false
        };

        $scope.enableEditor = function() {
          $scope.view.editorEnabled = true;
          $scope.view.editableValue = $scope.value;
        };

        $scope.disableEditor = function() {
          $scope.view.editorEnabled = false;
        };

        $scope.save = function() {
          $scope.value = $scope.view.editableValue;
          $scope.disableEditor();
          $timeout(function() {
            $scope.saveCallback();
          });
        };
      }
    };
  });

  directives.directive( 'goClick', function ( $location ) {
    return function ( scope, element, attrs ) {
      var path;

      attrs.$observe( 'goClick', function (val) {
        path = val;
      });

      element.bind( 'click', function () {
        scope.$apply( function () {
          $location.path( path );
        });
      });
    };
  });

});


