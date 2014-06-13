'use strict';

require.config({
  paths: {
    'domReady': '../bower/requirejs-domready/domReady',
    'angular': '../bower/angular/angular',
    'angular-route': '../bower/angular-route/angular-route',
    'jquery': '../bower/jquery/dist/jquery',
    'bootstrap': '../bower/bootstrap/dist/bootstrap'
  },

  shim: {
    'angular': {
      exports: 'angular'
    },
    'angular-route': {
      deps: ['angular']
    }
  },

  deps: ['./index']
});
