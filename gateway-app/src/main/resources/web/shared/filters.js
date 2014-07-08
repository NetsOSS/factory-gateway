'use strict';

define([ 'angular' ], function (angular) {
  var filters = angular.module('shared.filters', []);


  filters.filter('secondstime', function () {
    return function (input) {
      console.log('filter!!!!!!!!!');
      var sec_num = parseInt(input, 10) || 0; // don't forget the second param
      if (sec_num == 0)
        return '';
      var hours = Math.floor(sec_num / 3600);
      var minutes = Math.floor((sec_num - (hours * 3600)) / 60);
      var seconds = sec_num - (hours * 3600) - (minutes * 60);

      if (hours < 10) {
        hours = "0" + hours;
      }
      if (minutes < 10) {
        minutes = "0" + minutes;
      }
      if (seconds < 10) {
        seconds = "0" + seconds;
      }
      var time = hours + 'h:' + minutes + 'm:' + seconds + 's';
      return time;
    };

  });
});
