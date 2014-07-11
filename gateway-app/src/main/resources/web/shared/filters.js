'use strict';

define([ 'angular' ], function (angular) {
  var filters = angular.module('shared.filters', []);


  filters.filter('secondstime', function () {
    return function (input) {

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
  filters.filter('bytes', function(){
    return function(bytes){
      if(isNaN(bytes))
        return "";
      //http://stackoverflow.com/questions/15900485/correct-way-to-convert-size-in-bytes-to-kb-mb-gb-in-javascript
      if(bytes == 0) return '0 Byte';
      var k = 1000;
      var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
      var i = Math.floor(Math.log(bytes) / Math.log(k));
      return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
    }
  })
});
