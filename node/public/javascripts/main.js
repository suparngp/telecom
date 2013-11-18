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


myApp.directive('contacts', function ($http) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
//           var Contacts= {};
            console.log(attrs);
//           Contacts.details=[
//               {contactName: 'Suparn',    contactNum: '12345',     contactEmail:'suparn@sip.com' },
//               {contactName: 'Rajendra',  contactNum: '34567',     contactEmail:''    },
//               {contactName: 'Kim',       contactNum: '56789',     contactEmail:'kim@sip.com'    } ,
//               {contactName: 'Aditya',    contactNum: '78901',     contactEmail:'adi@sip.com'    }
//               // and so on
//           ];
//           scope.contacts = Contacts;
            console.log("Contacts set");

            $http({method: 'GET', url: '/contacts'})
                .success(function (data, status, headers, config) {
                    var det = JSON.parse(data);
                    var m = JSON.parse(det);
                    console.log(m);
                    scope.contacts = m;
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
myApp.controller('ContactsController', function ($scope, $timeout, $http) {


    console.log("contacts requested");

    $scope.call = function (modal, target) {

        $(modal).modal({
            backdrop: 'static',
            keyboard: false
        });

        var x = $scope;
        $scope.currentCall = {
            callee: target
        };

        $http({url: 'call/phone', method: 'POST', data: {address: 'eogus618@sip.linphone.org'}}).success(function(data) {
            console.log(data);
        });
        //window.phone.call('sip:eogus618@sip.linphone.org', window.options);
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

myApp.filter('date', function(){
    return function(timestamp){
        console.log(timestamp);
        try{
            var d = moment.unix(Number(timestamp)/1000).format("ddd, MMM DD YYYY, hh:mm:ss a");
            return d.toString();
        }
        catch(error){
            console.log(error.toString());
            return "";
        }

    }
});
myApp.directive('messages', function ($http) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
//           var Contacts= {};
            console.log(attrs);
//           Contacts.details=[
//               {contactName: 'Suparn',    contactNum: '12345',     contactEmail:'suparn@sip.com' },
//               {contactName: 'Rajendra',  contactNum: '34567',     contactEmail:''    },
//               {contactName: 'Kim',       contactNum: '56789',     contactEmail:'kim@sip.com'    } ,
//               {contactName: 'Aditya',    contactNum: '78901',     contactEmail:'adi@sip.com'    }
//               // and so on
//           ];
//           scope.contacts = Contacts;
            console.log("Contacts set");

            $http({method: 'GET', url: '/messages'})
                .success(function (data, status, headers, config) {
                    var det = JSON.parse(data);
                    var m = JSON.parse(det);
                    console.log(m);
                    console.log(m);
                    var converted = convertMessages(m['groupedList'])
                    console.log(converted);
                    scope.messages = converted;
                });
        }
    }
});
/**
 * Controller for the messages
 * */
myApp.controller('MessagesController', function ($scope, $http) {


    //$scope.messages = [{"contactName":"","contactNum":"15555215554","date":"1384490812673","message":"Yes Android is great","msgRead":"1","msgType":"2"},{"contactName":"","contactNum":"15555215554","date":"1384490801065","message":"Hello","msgRead":"1","msgType":"2"},{"contactName":"1","contactNum":"15555215554","date":"1384490172918","message":"Android is awesome","msgRead":"1","msgType":"1"},{"contactName":"1","contactNum":"15555215554","date":"1384490158187","message":"Hello","msgRead":"1","msgType":"1"},{"contactName":"1","contactNum":"15555215554","date":"1384490142620","message":"Hi","msgRead":"1","msgType":"1"}];
});

function convertMessages(data){
    console.log(data);
    var list = [];
    for(var x in data){
        console.log(x);
        console.log(data[x]);
        var datum = {
            groupId: x,
            messages: data[x]
        };
        list.push(datum);
    }
    return list;
}