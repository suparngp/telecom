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
myApp.controller('SideNavController', function ($scope) {
    $scope.selected = 'home';
});

/**
 * Controller for the contacts
 * */
myApp.controller('ContactsController', function ($scope) {
    $scope.call = function (target) {
        console.log("I am calling " + target);
    };

});

/**
 * Controller for the messages
 * */
myApp.controller('MessagesController', function ($scope) {

});




