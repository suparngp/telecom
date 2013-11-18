var myApp = angular.module('myApp', ['ui.router']);

myApp.config(function ($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.when('', '/home');
    var home = {
            url: "/home",
            templateUrl: "partials/home.html"
        },
        contacts = {
            url: "/contacts",
            templateUrl: "partials/contacts.html"
        },
        messages = {
            url: "/messages",
            templateUrl: "partials/messages.html"
        };


    $stateProvider
        .state('home', home)
        .state('contacts', contacts)
        .state('messages', messages);
});

myApp.factory('ContactsFunction', function () {

    var Contacts = {};
    Contacts.details = [
        {name: 'Suparn', phn: '12345', email: 'suparn@sip.com' },
        {name: 'Rajendra', phn: '34567', email: ''    },
        {name: 'Kim', phn: '56789', email: 'kim@sip.com'    } ,
        {name: 'Aditya', phn: '78901', email: 'adi@sip.com'    }
        // and so on
    ];
    return Contacts;
});

myApp.directive('contacts', function ($http) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
//           var Contacts= {};
//           Contacts.details=[
//               {name: 'Suparn',    phn: '12345',     email:'suparn@sip.com' },
//               {name: 'Rajendra',  phn: '34567',     email:''    },
//               {name: 'Kim',       phn: '56789',     email:'kim@sip.com'    } ,
//               {name: 'Aditya',    phn: '78901',     email:'adi@sip.com'    }
//               // and so on
//           ];
//           scope.contacts = Contacts;
            console.log("Contacts set");

            $http({method: 'GET', url: '/contacts'})
                .success(function (data, status, headers, config) {
                    console.log(data);
                    scope.contacts = data;
                });
        }
    }
});

/**
 * Application's root controller
 * */
myApp.controller('MainController', function ($scope, $http) {
    $scope.sms = {};

    $scope.sms.response = '';

    $scope.openSms = function (modal, recipientName, recipientNumber) {
        $scope.sms = {};
        $scope.sms.recipient = {};
        $scope.sms.recipient.name = recipientName;
        $scope.sms.recipient.phn = recipientNumber;
        $(modal).modal('show');
        $scope.modal = modal;
    };

    $scope.sendSms = function () {
        console.log($scope.sms);

        var payload = {
            content: $scope.sms.content,
            number: $scope.sms.recipient.phn
        };

        console.log(payload);
        $http({url: 'send/sms', data: payload, method: 'POST'}).success(function (data) {
            console.log(data);
            $scope.sms.success = true;
            $scope.sms.error = false;
            //$($scope.modal).modal('toggle');
        }).error(function () {
                $scope.sms.success = false;
                $scope.sms.error = true;
            });

    }
});

/**
 * Controller for the side nav menu
 * */
myApp.controller('SideNavController', function ($scope, $state, $location) {
    //$state.go($location.path().replace("/", ""));

    $scope.selected = $location.path().replace("/", "");
});

/**
 * Controller for the contacts
 * */
myApp.controller('ContactsController', function ($scope, $timeout) {

    $scope.call = function (modal, target) {

        $(modal).modal({
            backdrop: 'static',
            keyboard: false
        });

        var x = $scope;
        $scope.currentCall = {
            callee: target
        };

        //TODO replace the time out functions with calls to the phone.
        $timeout(function(){
            $scope.currentCall.req = true;
            $timeout(function(){
                $scope.currentCall.sip = true;
                $timeout(function(){
                    $scope.currentCall.connected = true;
                    $timeout(function(){
                        $scope.currentCall.started = true;
                        $timeout(function(){
                            $scope.currentCall.progress = true;
                        }, 4000);
                        $timeout(function(){
                            $scope.currentCall.error = true;
                        }, 6000);
                    }, 4000);
                }, 4000);
            }, 4000);
        }, 4000);
        console.log("I am calling " + target);
        $(modal).modal('show');
    };

    $scope.endCall = function(modal){

        //TODO add the end call support
        $scope.currentCall = {};
        $(modal).modal('hide');
    }
});

/**
 * Controller for the messages
 * */
myApp.controller('MessagesController', function ($scope) {

});




