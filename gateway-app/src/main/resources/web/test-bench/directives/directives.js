'use strict';

define([
  'angular'
], function (angular) {
  console.log("test-bench.directives");

  var directives = angular.module('test-bench.directives', []);

  directives.directive('fileModel', ['$parse', function ($parse) {
    return {
      restrict: 'A',
      link: function(scope, element, attrs) {
        var model = $parse(attrs.fileModel);
        var modelSetter = model.assign;

        element.bind('change', function(){
          scope.$apply(function(){
            modelSetter(scope, element[0].files[0]);
          });
        });
      }
    };
  }]);

  return directives;
});
