'use strict';

define([
    'require',
    'angular'
], function (require, angular) {
    var templatePrefix = require.toUrl("./");
    var gateway = angular.module('gateway', ['ngRoute', 'shared.services', 'shared.directives', 'shared.filters','ui.sortable']);


    gateway.config(function ($routeProvider, $httpProvider,$locationProvider) {
        $routeProvider.
            when('/application-groups/:applicationGroupId?', {
                controller: 'FrontPageCtrl',
                templateUrl: templatePrefix + "gateway.html",
                resolve: {
                    allLBs: function (GatewayData) {
                        return GatewayData.LoadBalancerController.listAllLoadBalancers();
                    },
                    allAppGroups: function (GatewayData) {
                        return GatewayData.ApplicationGroupController.listAllAppGroups();
                    }
                }

            }).
            when('/person/:id', {
                controller: 'PersonCtrl',
                templateUrl: templatePrefix + "person.html"
            })
            .
            when('/app/:id', {
                controller: 'AppCtrl',
                templateUrl: templatePrefix + "app.html"
            })
            .when('/appInst/:id', {
                controller: 'AppInstCtrl',
                templateUrl: templatePrefix + "appInst.html"
            })
            .when('/lb/:id', {
                controller: 'LoadBalancerCtrl',
                templateUrl: templatePrefix + "loadBalancer.html"
            })
            .when('/group/:id', {
                controller: 'AppGroupCtrl',
                templateUrl: templatePrefix + "appGroup.html"
            })
            .when('/allLb', {
                controller: 'LoadBalancersCtrl',
                templateUrl: templatePrefix + "allLoadBalancers.html"
            })
            .otherwise({redirectTo: '/application-groups'});
      //$locationProvider.html5Mode(true);

        $httpProvider.interceptors.push(['$q', function ($q) {
            return {

                /* All the following methods are optional */
                response: function (response) {
                    if (response.config.method == "PUT") {
                        $("#MessageDisplaySuccessText").text("Updated " + response.data.name + " successfully.");
                        $('#messageDisplaySuccess').show().delay(2000).fadeOut('slow');
                    }
                    if (response.config.method == "POST") {
                        $("#MessageDisplaySuccessText").text("Created " + response.data.name + " successfully.");
                        $('#messageDisplaySuccess').show().delay(2000).fadeOut('slow');
                    }

                    return response || $q.when(response);
                },

                responseError: function (rejection) {
                    /* Called when another XHR request returns with an error status code. */
                    if (rejection.status == 400) {
                        $("#MessageDisplayText").text(rejection.data);
                        $("#messageDisplay").show().delay(5000).fadeOut('slow');

                    } else if (rejection.status == 404) {
                        window.location = "./";
                        return;
                    } else {
                        console.log("New unknown error : ", rejection);
                    }
                    return $q.reject(rejection);
                }

            }

        }]);

    });
    gateway.controller('IndexController', function ($location, $scope, $filter, GatewayData) {

        $scope.applicationList =[];
        $scope.applicationInstanceList =[];
        $scope.loadBalancerList =[];

        $scope.getData = function() {

            var search = document.getElementById("searchInput").value;
            $scope.applicationList =[];
            $scope.applicationInstanceList =[];
            $scope.loadBalancerList =[];


            if(search.length > 0) {
                GatewayData.ApplicationController.search(search).then(function (data) {
                    $scope.applicationList = data;

                });

                GatewayData.ApplicationInstanceController.search(search).then(function (data) {
                    $scope.applicationInstanceList = data;
                });

                GatewayData.LoadBalancerController.search(search).then(function (data) {
                    $scope.loadBalancerList = data;
                });
            } else {
                GatewayData.ApplicationController.listAllApps().then(function(data){
                    $scope.applicationList = data;
                });
                GatewayData.ApplicationInstanceController.listAllAppInsts().then(function (data) {
                    $scope.applicationInstanceList = data;
                });
                GatewayData.LoadBalancerController.listAllLoadBalancers().then(function (data) {
                    $scope.loadBalancerList = data;
                });
            }
        };
    });

    gateway.controller('FrontPageCtrl', function ($location, $scope, $filter, $routeParams, GatewayData, allAppGroups, allLBs) {
        $scope.currentAppGroupId = $routeParams.applicationGroupId;
        $scope.newApp = {}; //Model for new ApplicationForm
     // $scope.newAppInstForm = {};
        $scope.newAppGroup={};
        $scope.emailFields = [{'id':"mail1"}];
         $scope.showAppInstForm=false;
        $scope.newGroupURL = "newGroup";


        $scope.showNewMailField = function(mail) {
            return mail.id == $scope.emailFields[$scope.emailFields.length-1].id;
        };

        $scope.addMailField = function () {
            var newField = $scope.emailFields.length+1;
            $scope.emailFields.push({'id':'mail'+newField});


        };

        $scope.removeMailField = function() {
            var removeField = $scope.emailFields.length-1;
            if(removeField > 0) {
                $scope.emailFields.pop();
            }
        };

        $scope.addAppInst = function (app) {
            console.log("New app inst : ", app.newAppInstForm, " to ", app.id);
            var testObj = {};
            testObj.name = app.newAppInstForm.name;
            testObj.server = app.newAppInstForm.host;
            testObj.applicationId = app.id;

            GatewayData.ApplicationInstanceController.create(app.id, testObj).then(function (data) {
                app.applicationInstances.push(data);
            });
        };

        $scope.updateAppInst = function (appInst) {
            console.log("Update App Inst : ", appInst);
            GatewayData.ApplicationInstanceController.update(appInst.id, appInst);
        };

        // ----------------------- Load balancer functions ------------------------------------
       /* GatewayData.LoadBalancerController.listAllLoadBalancers().then(function (data) {
            $scope.allLBs = data;
        });*/

        $scope.allLBs = allLBs;

        $scope.createLoadBalancer = function () {
            GatewayData.LoadBalancerController.create($scope.lb).then(function (data) {
                $scope.allLBs.push(data);
            });
        };

        // ----------------------- Application Group functions ------------------------------------
        $scope.createApplicationGroup = function () {
            GatewayData.ApplicationGroupController.create($scope.newAppGroup).then(function (data) {

                $scope.allAppGroups.push(data);
                $scope.newAppGroup = {};
            });
        };

        /*GatewayData.ApplicationGroupController.listAllAppGroups().then(function (data) {
            $scope.allAppGroups = data;
        });*/

        $scope.allAppGroups = allAppGroups;


        $scope.showUpdateApplicationGroup = function (appGroup) {
            $scope.updateAppGroup = {};

            $scope.updateAppGroup = angular.copy(appGroup);
            $('#modalUpdateAppGroup').modal('show');
        };

        $scope.updateApplicationGroup = function () {
            GatewayData.ApplicationGroupController.update($scope.updateAppGroup.id, $scope.updateAppGroup).then(function (data) {
                var foundGroup = $filter('getById')($scope.allAppGroups, data.id);
                foundGroup.name = data.name;
                foundGroup.port = data.port;
                $('#modalUpdateAppGroup').modal('hide');
            });
        };

        $scope.removeGroup = function () {
            GatewayData.ApplicationGroupController.remove($scope.appGroupToBeDeleted.id).then(function (data) {
                $scope.allAppGroups.splice($scope.allAppGroups.indexOf($scope.appGroupToBeDeleted), 1);
            });
        };

        $scope.removeApp = function (appGroup, app) {
            console.log("Remove ", app, " from appgroup  ", appGroup, " index: ", appGroup.applications.indexOf(app));
            GatewayData.ApplicationController.remove(app.id).then(function (data) {
                appGroup.applications.splice(appGroup.applications.indexOf(app), 1);
            });
        };

        $scope.removeAppInst = function () {
            console.log("Remove ", $scope.appInstToBeDeleted, " from appg  ",  $scope.appToBeSpliced, " index: ",  $scope.appToBeSpliced.applicationInstances.indexOf($scope.appInstToBeDeleted));
            GatewayData.ApplicationInstanceController.remove($scope.appInstToBeDeleted.id).then(function (data) {
                $scope.appToBeSpliced.applicationInstances.splice($scope.appToBeSpliced.applicationInstances.indexOf($scope.appInstToBeDeleted), 1);
            });
        };

        /* Update Application start */
        $scope.showNewMailFieldForUpdate = function(mail) {
            return mail.id == $scope.updateEmails[$scope.updateEmails.length-1].id;
        };

        $scope.addMailFieldForUpdate = function () {
            var newField = $scope.updateEmails.length+1;
            $scope.updateEmails.push({'id':'mail'+newField});

        };

        $scope.removeMailFieldForUpdate = function() {
            var removeField = $scope.updateEmails.length-1;
            if(removeField > 0) {
                $scope.updateEmails.pop();
            }
        };

        $scope.showUpdateApplication = function (app) {
            $scope.updateApp = {};

            $scope.updateApp = angular.copy(app);
            $scope.updateApp.applicationInstances = [];
            $scope.updateApp.loadBalancers = [];
            $scope.updateEmails = [];

            var mails = app.emails.split(",");
            for(var i = 0; i < mails.length; i++) {
                $scope.updateEmails.push({'id':i, 'name':mails[i]});
            }
            $('#modalUpdateApp').modal('show');
        };

        /* Update Application end */
        $scope.showDeleteAppWarning = function (app, appGroup) {
            $scope.appToBeDeleted = app;
            $scope.appGroupToBeSpliced = appGroup;
            $('#deleteAppWarningModal').modal('show');
        };

        $scope.showDeleteAppGroupWarning = function (appGroup) {
            $scope.appGroupToBeDeleted = appGroup;
            $('#deleteAppGroupWarningModal').modal('show');
        };

        $scope.showDeleteAppInstWarning = function(app, appInst) {
            $scope.appInstToBeDeleted = appInst;
            $scope.appToBeSpliced = app;
            $('#deleteAppInstWarningModal').modal('show');
        };

        $scope.removeApp = function() {
            GatewayData.ApplicationController.remove($scope.appToBeDeleted.id).then(function (data) {
                $scope.appGroupToBeSpliced.applications.splice($scope.appGroupToBeSpliced.applications.indexOf($scope.appToBeDeleted), 1);
            });

        };


      // ------------------------ Header Rules For Application --------------------------------------
      $scope.showUpdateApplicationRules = function (app) {
        $scope.updateApp = app;
        $('#modalAppRules').modal('show');
      };

      $scope.removeRuleFromApp = function(app, headerRule) {
        console.log("remove header id ",headerRule," from app ",app);
        GatewayData.ApplicationController.removeHeaderRule(app.id,headerRule.id).then(function (data) {
          app.headerRules.splice( app.headerRules.indexOf(headerRule), 1);
        });

      };

      $scope.updateApplicationRules = function (app) {
        GatewayData.ApplicationController.addHeaderRule($scope.updateApp.id, $scope.inRule).then(function (data) {
          $scope.updateApp.headerRules.push(data);
        });
      };
      // ------------------------------ Update Application ------------------------

        $scope.updateApplication = function () {
            var foundGroup = $filter('getById')($scope.allAppGroups, $scope.updateApp.applicationGroupId);
            var foundAppIndex = $filter('getIndexById')(foundGroup.applications, $scope.updateApp.id);

            var mails ="";
            var comma = false;
            for(var i = 0; i < $scope.updateEmails.length; i++) {
                if ($scope.updateEmails[i].name != null && $scope.updateEmails[i].name.length > 0) {
                    if (comma) {
                        mails = mails + "," + $scope.updateEmails[i].name;
                    } else {
                        mails = mails + $scope.updateEmails[i].name;
                        comma = true;
                    }
                }
            }
            $scope.updateApp.emails = mails;


            GatewayData.ApplicationController.update($scope.updateApp.id, $scope.updateApp).then(function (data) {
                foundGroup.applications[foundAppIndex] = data;
                $scope.updateApp = {};
                $scope.updateEmails =[];
                $('#modalUpdateApp').modal('hide');
            });
        };

        $scope.currAppGroupSelected = {};
        $scope.showCreateApplicationForGroup = function (appGroup) {
            console.log("show modal ", appGroup);
            $scope.currAppGroupSelected = appGroup;
            $('#modalCreateApp').modal('show');

        };

        $scope.createApplication = function (appGroup) {
            var copyNewApp = angular.copy($scope.newApp);
            copyNewApp.applicationGroupId = appGroup.id;
            var mails ="";
            var comma = false;
            for(var i = 0; i < $scope.emailFields.length; i++) {
                if($scope.emailFields[i].name != null && $scope.emailFields[i].name.length >0) {
                    if (comma) {
                        mails = mails + ","+ $scope.emailFields[i].name;
                    } else {
                        mails = mails + $scope.emailFields[i].name;
                        comma = true;
                    }
                }
            }
            copyNewApp.emails = mails;
            GatewayData.ApplicationController.create(copyNewApp).then(function (data) {
                appGroup.applications.push(data);
                $scope.emailFields = [{'id':"mail1"}];


            });
        };

        $scope.setSticky = function (app, sticky) {
            console.log("SetSticky : id=", app.id, " sticky=", sticky);

            GatewayData.ApplicationController.setSticky(app.id, sticky).then(function (data) {
                app.stickySession = sticky;
                /* Should check if it was a success */
            });
        };

      $scope.setToBackup = function (appInst) {
        var objNode = {"backup" : !appInst.backup};
        GatewayData.ApplicationInstanceController.setToBackup(appInst.id,objNode).then(function(data){
          appInst.backup= !appInst.backup;
        });
      };

      $scope.sortableApps = {
        handle: ".drag-handle",
        stop : function(e, ui) {
          var movedApp = ui.item.scope().app;

          var moveObj= {
            "from": ui.item.sortable.index,
            "to": ui.item.sortable.dropindex
          };
          console.log('moved', movedApp, moveObj.from, moveObj.to);

          /* If to is undefined, the item did not move. (Dragged back to the same position) -> Do nothing */
          if(typeof moveObj.to === 'undefined')
            return;

          console.log(moveObj);
          GatewayData.ApplicationGroupController.changeIndexOrderOfApplications(movedApp.applicationGroupId,moveObj).then(function (data) {
          });
        }
      };
    });

    /* Application controller */
    gateway.controller('AppCtrl', function ($scope, $routeParams, GatewayData) {

        $scope.onAppLoadDone = false;

        $scope.onUpdatedApp = function (data) {
            GatewayData.ApplicationController.findById($routeParams.id).then(function (data) {
                console.log("Application data : ", data);
                $scope.app = data;
                $scope.localApp = angular.copy($scope.app);
                $scope.onAppLoadDone = true;
                $scope.newApp = angular.copy($scope.app);
                $scope.updateEmails = [];

                var emails = data.emails.split(",");
                for(var i = 0; i < emails.length; i++) {
                    $scope.updateEmails.push({"id":"mail"+i, "name":emails[i]});
                }

                /* Find more info  (name) about the group it belongs too. */
                GatewayData.ApplicationGroupController.findById($scope.app.applicationGroupId).then(function (data) {
                    $scope.appGroup = data;
                });
                $scope.getStatusOfApplication();
            });
        };

        $scope.setSticky = function (id, sticky) {
            GatewayData.ApplicationController.setSticky(id, sticky).then(function (data) {
            });
        };

        $scope.onUpdatedApp();

        $scope.removeApp = function () {
            console.log("Deleting id: ", $scope.app.id);
            GatewayData.ApplicationController.remove($scope.app.id).then(function (data) {
                $scope.$apply();
                history.back();
            });
        };

        $scope.showNewMailFieldForUpdate = function(mail) {
            return mail.id == $scope.updateEmails[$scope.updateEmails.length-1].id;
        };

        $scope.addMailFieldForUpdate = function () {
            var newField = $scope.updateEmails.length+1;
            $scope.updateEmails.push({'id':'mail'+newField});

        };

        $scope.removeMailFieldForUpdate = function() {
            var removeField = $scope.updateEmails.length-1;
            if(removeField > 0) {
                $scope.updateEmails.pop();
            }
        };


        $scope.updateApplication = function () {

            var mails ="";
            var comma = false;
            for(var i = 0; i < $scope.updateEmails.length; i++) {
                if ($scope.updateEmails[i].name != null && $scope.updateEmails[i].name.length > 0) {
                    if (comma) {
                        mails = mails + "," + $scope.updateEmails[i].name;
                    } else {
                        mails = mails + $scope.updateEmails[i].name;
                        comma = true;
                    }
                }
            }
            $scope.newApp.emails = mails;
            GatewayData.ApplicationController.update($scope.newApp.id, $scope.newApp).then(function (data) {
                $scope.app = data;
            });
        };

        $scope.saveAppInst = function () {
            $scope.newAppInstAlertSuccess = true;

            $scope.appInst.applicationId = $scope.app.id;

            GatewayData.ApplicationInstanceController.create($scope.app.id, $scope.appInst).then(function (data) {
                $scope.app.applicationInstances.push(data);
            });
        };

        /* --- Status --- */
        $scope.getStatusOfApplication = function () {
            GatewayData.StatusController.getServerStatusForApplication($routeParams.id).then(function (data) {
                $scope.statusAppServers = data;
            });
        };

        $scope.currModalStatus = {};
        $scope.showModalDetail = function (statusObj) {
            $scope.currModalStatus = statusObj;
            $('#modalAppInstDetails').modal('show');

        };
    });

    //  ------------------------ApplicationInstanceControler------------------------------
    gateway.controller('AppInstCtrl', function ($scope, $routeParams, GatewayData) {
        GatewayData.ApplicationInstanceController.findById($routeParams.id).then(function (data) {
            $scope.appInst = data;
        });

        $scope.deleteAppInst = function () {
            console.log("Removing: ", $scope.appInst);
            GatewayData.ApplicationInstanceController.remove($scope.appInst.id).then(function (data) {
                history.back();
            });
        };


        $scope.updateAppInst = function () {
            console.log("Updating: ", $scope.appInst);
            GatewayData.ApplicationInstanceController.update($scope.appInst.id, $scope.appInst).then(function (data) {
            });
        };

        $scope.updateAppInst = function () {
            console.log("Updating: ", $scope.appInst);
            GatewayData.ApplicationInstanceController.update($scope.appInst.id, $scope.appInst).then(function (data) {
            });
        };

        GatewayData.StatusController.getStatusForOneServer($routeParams.id).then(function (data) {
            $scope.rawStatusForOneInst = data;
        });

    });


    //    ----------------------- Load balancer Controller ------------------------------------
    gateway.controller('LoadBalancerCtrl', function ($scope, $routeParams, $timeout, GatewayData) {
        $scope.inLBList = [];
        $scope.allLBList = [];

        $scope.autoRefresh = true;
        $scope.lbLoadingDone = false;
        var LBid = $routeParams.id;


        GatewayData.LoadBalancerController.findById($routeParams.id).then(function (data) {
            $scope.lb = data;
            $scope.lbLoadingDone = true;
            reloadAppLists();
        });

        $scope.addAppToLB = function (appId) {
            GatewayData.LoadBalancerController.addApplication($scope.lb.id, appId).then(function (data) {
                $scope.lb = data;
                reloadAppLists();
            });
        };
        $scope.removeAppFromLB = function (appId) {
            console.log('Remove app ', appId, ' from LB ', $scope.lb.id);
            GatewayData.LoadBalancerController.removeApplicationFromLoadbalancer($scope.lb.id, appId).then(function (data) {
                $scope.lb = data;
                reloadAppLists();
            });

        };

        $scope.setProxyStateWithAPI = function (statusObj,state) {
            console.log(statusObj);

            var statusChangeObj = {
                "s": statusObj.svname,
                "action": state,
                "b": statusObj.iid
            };

            console.log(statusChangeObj);
            GatewayData.StatusController.changeStatusAPI($scope.lb.id, statusChangeObj);
        };


        $scope.setProxyState = function (appInstName) {


            var sel = document.getElementById(appInstName + "-state");
            var state = sel.options[sel.selectedIndex].value;

            GatewayData.ApplicationInstanceController.setProxyStateForInstanceAndStartLoadbalancer(appInstName, state).then(function (data) {

            });

        };

        $scope.removeLoadBalancer = function () {
            GatewayData.LoadBalancerController.remove($scope.lb.id).then(function (data) {
                history.back();
                $scope.$$phase || $scope.$apply(); /* Safe apply */
            });
        };

        $scope.startLoadBalancer = function () {
            console.log("start LoadBalancer function");
            GatewayData.LoadBalancerController.startLoadBalancer($scope.lb.id);
        };

      $scope.stopLoadBalancer = function () {
        console.log("stop LoadBalancer function");
        GatewayData.LoadBalancerController.stopLoadBalancer($scope.lb.id);
      };


        var reloadAppLists = function () {
            $scope.inLBList = [];
            $scope.allLBList = [];
            $scope.inLBList = $scope.lb.applications;

            GatewayData.ApplicationController.listAllApps().then(function (data) {
                /* Search: Loop through All Apps. Add an app if it does not exist in the Load Balancer. */
                for (var i = 0; i < data.length; i++) {
                    var contains = false;
                    for (var j = 0; j < $scope.inLBList.length; j++) {

                        if (data[i].id == $scope.inLBList[j].id) {
                            contains = true;
                            break;
                        }
                    }
                    if (!contains)
                        $scope.allLBList.push(data[i]);
                }

            });

            GatewayData.LoadBalancerController.generateConfiguration($routeParams.id).then(function(data) {
                var fixed = data;
                fixed = fixed.replace(/\\r\\n/g, "\n");
                fixed = fixed.replace(/\\n/g, "\n");
                var re = new RegExp('\\\\\\\\', 'g');
                fixed = fixed.replace(re, "\\");
                $scope.configFile = fixed;
            });
        };

        //----- Status proxy -----------------------
        var poller = null;
        $scope.startStopAutoRefresh = function () {
            $scope.autoRefresh = !$scope.autoRefresh;

            if ($scope.autoRefresh) {
                console.log('Starting poller!');
                loadStatus();

            } else {
                console.log('Stopped poller!');
                $timeout.cancel(poller);
            }
        };
        $scope.isLBonline = false;
        var updateLBisOlineStatus = function () {
            GatewayData.StatusController.isLoadBalancerOnline($routeParams.id).then(function (data) {
                $scope.isLBonline = (data==='true');
            });
        };


        var loadStatus = function () {
            updateLBisOlineStatus();
            GatewayData.StatusController.getStatusForLoadbalancer($routeParams.id).then(function (data) {

              $scope.rawStatus = data;
            });

            if ($scope.autoRefresh) {
                poller = $timeout(loadStatus, 1000);
            }

        };
        loadStatus();

        /* Clean up. Stop poling if leaving page */
        $scope.$on('$locationChangeStart', function () {
            $timeout.cancel(poller);
        });
        $scope.$on('$destroy', function () {
            $timeout.cancel(poller);
        });


    });

    //    ----------------------- App Group Controller ------------------------------------
    gateway.controller('AppGroupCtrl', function ($scope, $routeParams, $location, GatewayData) {
        $scope.newApp = {};
        $scope.newApp.applicationGroupId = $routeParams.id;

        GatewayData.ApplicationGroupController.findById($routeParams.id).then(function (data) {
            $scope.group = data;

            GatewayData.ApplicationGroupController.getApplications($scope.group.id).then(function (data) {
                $scope.allAppsInGroup = data;
            });
        });

        $scope.removeGroup = function () {
            GatewayData.ApplicationGroupController.remove($routeParams.id);
            $location.path("/");
        };

        $scope.createApplication = function () {
            GatewayData.ApplicationController.create($scope.newApp).then(function (data) {
                $scope.allAppsInGroup.push(data);
                $scope.newApp = {};
                $scope.newApp.applicationGroupId = $routeParams.id;
            });
        };

    });

    gateway.controller('PersonCtrl', function ($scope, $routeParams) {
        $scope.personId = $routeParams.id;
    });

    gateway.controller('LoadBalancersCtrl', function ($scope, $routeParams, $location, GatewayData) {
        GatewayData.StatusController.getStatusForAllLoadbalancers().then(function (data) {
            $scope.allStatus = data;
        });
    });
});
