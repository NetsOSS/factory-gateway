'use strict';

define([
    'require',
    'angular'
], function (require, angular) {
    var templatePrefix = require.toUrl("./");
    var gateway = angular.module('gateway', ['ngRoute', 'shared.services', 'shared.directives', 'shared.filters']);


    gateway.config(function ($routeProvider, $httpProvider) {
        $routeProvider.
            when('/', {
                controller: 'FrontPageCtrl',
                templateUrl: templatePrefix + "gateway.html"
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
        ;

        $httpProvider.interceptors.push(['$q', function ($q) {
            return {
                //http://blog.brunoscopelliti.com/xhr-interceptor-in-an-angularjs-web-app

                /* All the following methods are optional */
                response: function (response) {
                    // response.status === 200
                    if (response.config.method == "PUT") {
                        //console.log("Update successfully" ,response);
                        $("#MessageDisplaySuccessText").text("Updated " + response.data.name + " successfully.");
                        $('#messageDisplaySuccess').show().delay(2000).fadeOut('slow');
                    }
                    if (response.config.method == "POST") {
                        //console.log("Update successfully" ,response);
                        $("#MessageDisplaySuccessText").text("Created " + response.data.name + " successfully.");
                        $('#messageDisplaySuccess').show().delay(2000).fadeOut('slow');
                    }
                    //console.log("Response: ",response)
                    return response || $q.when(response);
                },

                responseError: function (rejection) {
                    // Called when another XHR request returns with an error status code.

                    if (rejection.status == 400) {
                        //alert(rejection.data);
                        // $scope.messageError=rejection.data;

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

    gateway.controller('FrontPageCtrl', function ($location, $scope, GatewayData) {
        $scope.allApps = [];

        // ----------------------- Application functions ------------------------------------
        GatewayData.ApplicationController.listAllApps().then(function (data) {
            $scope.allApps = data;
        });

        //Callback to be called from AppForm. To update the list with the new application.
        /*$scope.onAppCreated = function (data) {
         $scope.allApps.push(data);
         };

         $scope.createApplication = function () {
         console.log("New application : ", $scope.app);
         GatewayData.ApplicationController.create($scope.app).then(function (data) {
         $scope.allApps.push(data);
         });
         };*/

        // ----------------------- Load balancer functions ------------------------------------
        GatewayData.LoadBalancerController.listAllLoadBalancers().then(function (data) {
            $scope.allLBs = data;
        });

        $scope.createLoadBalancer = function () {
            GatewayData.LoadBalancerController.create($scope.lb).then(function (data) {
                $scope.allLBs.push(data);
            });
        };

        // ----------------------- Application Group functions ------------------------------------
        $scope.createApplicationGroup = function () {
            GatewayData.ApplicationGroupController.create($scope.appGroup).then(function (data) {
                $scope.allAppGroups.push(data);
                $scope.appGroup = {};
            });
        };

        GatewayData.ApplicationGroupController.listAllAppGroups().then(function (data) {
            $scope.allAppGroups = data;
        });


        $scope.currAppGroupSelected = {};
        $scope.showCreateApplicationForGroup = function (appGroup) {
            console.log("show modal ", appGroup);
            $scope.currAppGroupSelected = appGroup;
            $('#modalCreateApp').modal('show');

        };

        $scope.createApplication = function () {
            $scope.newApp.applicationGroupId = $scope.currAppGroupSelected.id;
            GatewayData.ApplicationController.create($scope.newApp).then(function (data) {
                $scope.currAppGroupSelected.applications.push(data);

            });
        };


    });

    //Application controller
    gateway.controller('AppCtrl', function ($scope, $routeParams, GatewayData) {

        $scope.onAppLoadDone = false;

        $scope.onUpdatedApp = function (data) {
            GatewayData.ApplicationController.findById($routeParams.id).then(function (data) {
                console.log("Application data : ", data);
                $scope.app = data;
                $scope.localApp = angular.copy($scope.app);
                $scope.onAppLoadDone = true;
                $scope.newApp = angular.copy($scope.app);

                //Find more info  (name) about the group it belongs too.
                GatewayData.ApplicationGroupController.findById($scope.app.applicationGroupId).then(function (data) {
                    $scope.appGroup = data;
                });
                $scope.getStatusOfApplication();
            });
        };

        $scope.changeSetup = function(id, setup) {
            alert(id + " " + setup);

            GatewayData.ApplicationController.configureHaproxySetup(id, setup).then(function(data) {

            });
        };

        $scope.onUpdatedApp();

        $scope.removeApp = function () {
            console.log("Deleting id: ", $scope.app.id);
            GatewayData.ApplicationController.remove($scope.app.id).then(function (data) {
                history.back();
                $scope.$apply();
            });
        };


        $scope.updateApplication = function () {
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

        //--- Status --
        $scope.getStatusOfApplication = function () {
            /*GatewayData.StatusController.getBackendStatusForApplication($routeParams.id).then(function (data) {
             //$scope.statusApp = data;
             });*/
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


    gateway.controller('AppInstCtrl', function ($scope, $routeParams, GatewayData) {
        GatewayData.ApplicationInstanceController.findById($routeParams.id).then(function (data) {
            $scope.appInst = data;
        });

        $scope.deleteAppInst = function () {
            GatewayData.ApplicationInstanceController.remove($scope.appInst.id).then(function (data) {
                history.back();
                $scope.$apply();
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

        $scope.autoRefresh = false;
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

        $scope.setProxyState = function (appInstName) {

            var sel = document.getElementById(appInstName + "-state");
            var state = sel.options[sel.selectedIndex].value;

            GatewayData.ApplicationInstanceController.setProxyStateByInstanceName(appInstName, state).then(function(data) {

            });

        };

        $scope.removeLoadBalancer = function () {
            GatewayData.LoadBalancerController.remove($scope.lb.id).then(function (data) {
                history.back();
                $scope.$$phase || $scope.$apply(); // Safe apply
            });
        };

        $scope.startLoadBalancer = function () {
            console.log("start LoadBalancer function");
            GatewayData.LoadBalancerController.startLoadBalancer($scope.lb.id);
        };


        var reloadAppLists = function () {
            $scope.inLBList = [];
            $scope.allLBList = [];
            $scope.inLBList = $scope.lb.applications;

            GatewayData.ApplicationController.listAllApps().then(function (data) {
                //Search: Loop through All Apps. Add an app if it does not exist in the Load Balancer.
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

            //Reload config file.. should maybe be saved as a string in LB model.
            GatewayData.LoadBalancerController.pushConfiguration($routeParams.id).then(function (data) {
                var fixed = data;
                fixed = fixed.replace(/\\r\\n/g, "\n");
                fixed = fixed.replace(/\\n/g, "\n");
                var re = new RegExp('\\\\\\\\', 'g');
                fixed = fixed.replace(re, "\\");
                $scope.configFile = fixed;
            });

            //var s = '"    global\n        daemon\n        maxconn 256\n\n    defaults\n        mode http\n        timeout connect 5000ms\n        timeout client 50000ms\n        timeout server 50000ms\n\n    frontend http-in\n        bind *:5000\n        acl Kaminorule path -m beg /kamino\n        acl Sekotrule path -m beg /sekot\n        acl Finchrule path -m beg /finch\n        use_backend Kamino if Kaminorule\n        use_backend Sekot if Sekotrule\n        use_backend Finch if Finchrule\n        \n    backend Kamino\n        option httpchk GET /kamino/v1/ping\n        server kamino1 vm-stapp-145:8100/kamino maxconn 32 check\n        server kamino2 vm-stapp-146:8100/kamino maxconn 32 check\n\n        \n    backend Sekot\n        option httpchk GET /sekot/mcp.html\n        server sekot2 vm-stapp-146:9494/sekot maxconn 32 check\n        server sekot3 vm-stapp-145:9595/sekot maxconn 32 check\n        server sekot1 vm-stapp-145:9494/sekot maxconn 32 check\n\n        \n    backend Finch\n        option httpchk GET /finch/index.html\n        server finch2 vm-stapp-146:7272/finch maxconn 32 check\n        server finch1 vm-stapp-145:7272/finch maxconn 32 check\n        server awd asd:654/sad maxconn 32 check\n\n\n    listen stats *:5001\n        mode http\n        stats enable\n        stats uri /proxy-stats\n        stats admin if TRUE\n"';


            // $scope.configFile2=s;
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
                $scope.isLBonline = data;
                console.log("LB is online ? ", data);
            });
        };


        var loadStatus = function () {
            console.log('Loading status');
            updateLBisOlineStatus();
            GatewayData.StatusController.getStatusForLoadbalancer($routeParams.id).then(function (data) {
                $scope.rawStatus = data;
            });

            if ($scope.autoRefresh) {
                poller = $timeout(loadStatus, 5000);
            }

        };
        loadStatus();

        //Clean up. Stop poling if leaving page
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
