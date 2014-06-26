'use strict';

define([ 'angular' ], function (angular) {
  var directives = angular.module('shared.directives', []);


  var INTEGER_REGEXP = /^\-?\d+$/;
  directives.directive('integer', function () {
    console.log('hei ');
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
      controller: 'LoadBalancerFormCtrl'
    };
  });

  directives.directive('backButton', function(){
    return {
      restrict: 'A',

      link: function(scope, element, attrs) {
        element.bind('click', goBack);

        function goBack() {
          history.back();
          scope.$apply();
        }
      }
    }
  });

});


