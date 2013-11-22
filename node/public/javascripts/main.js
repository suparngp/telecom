var myApp = angular.module('myApp', ['ui.router']);
var session;
var audioElem = document.getElementById('call');
var globalScope;
var handlers = {
    'progress': function (e) {
        console.log(e);
        console.log("Call is in progress");
    },
    'failed': function (e) {
        console.log(e);
        console.log("Call has failed");
    },
    'started': function (e) {

        console.log(e);
        console.log("call has started");
        try{
            document.getElementById('call').src
                = window.URL.createObjectURL(session.getRemoteStreams()[0]);
            console.log("Stream has been added");
        }
        catch(e){
            console.log(e.toString());
            console.log(e);
        }

    }
};
var options = {
    eventHandlers: handlers,
    mediaConstraints: {audio: true, video: true}
};
var configuration = {
    ws_servers: ["wss://edge.sip.onsip.com"],
    register: true,
    'uri': 'sip:some@getonsip.com',
    'authorization_user': "getonsip_some",
    'password': 'zRFKwdKFvQsBqRVR'
};

var phone = new JsSIP.UA(configuration);
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

    $scope.call = function (emailId, modal, target) {

        $(modal).modal({
            backdrop: 'static',
            keyboard: false
        });

        var x = $scope;
        $scope.currentCall = {
            callee: target
        };

//        $http({url: 'call/phone', method: 'POST', data: {address: emailId}}).success(function(data) {
//            console.log(data);
//        });
        //window.phone.call('sip:eogus618@sip.linphone.org', window.options);
        //TODO replace the time out functions with calls to the phone.

        //send a requetx to the server and set the first check mark
        $scope.currentCall.req = true;
        console.log("Sent the request to make a SIP call");
        globalScope = $scope;
        $http({url: "/call/req", method: 'GET'})
            .success(function(data){
                //get the response from the server.
                // This means the call has been initialized. Set the second check mark
                //send the request to the server to check if the call has been connected
                console.log("Phone 1 says call is initialized");
                $scope.currentCall.sip = true;
                console.log("Check with phone 1 is the call is connected");
                $http({url: '/call/connected', method: 'GET'})
                    .success(function(data){
                        //this means that the sip call has been connected
                        //Send a request to disconnect the call.
                        console.log("Phone 1 says SIP call is connected");
                        $scope.currentCall.connected = true;
                        console.log("Send the request to Phone 1 to start the call relay process");
                        $http({url: '/call/end', method: 'GET'})
                            .success(function(data){
                                //this means the call has been disconnected
                                //start the sip call from the browser.
                                console.log("Phone 1 has started the call relay process");
                                phone.call('sip:' + emailId, options);
                                $scope.currentCall.started = true;
                                console.log("Phone 1 has successfully completed the call relay process.")
                            })
                            .error(function(data){
                                console.log("Error: unable to send the request to being call relay process");
                                $scope.currentCall.error = true;
                            })
                    })
                    .error(function(data){
                        console.log("Phone 1 was unable to connect the SIP call");
                        $scope.currentCall.error = true;
                    });

            })
            .error(function(data){
                console.log("Error: Phone 1 was unable to initialize the SIP call");
                $scope.currentCall.error = true;
            });
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




;
(function () {
    phone.on('connected', function (e) {
        console.log(e);
        console.log("I am connected to the network");
    });

    phone.on('disconnected', function (e) {
        console.log(e);
        console.log("I am disconnected from the network");
    });

    phone.on('newRTCSession', function (e) {
        session = e.data.session;
        console.log('session', session);

        console.log(e);
        console.log("I am on call");
        globalScope.currentCall.progress = true;
        globalScope.$apply();

    });

    phone.on('registered', function (e) {
        console.log(e);
        console.log("I am registered");
    });

    phone.on('unregistered', function (e) {
        console.log(e);
        console.log("I am unregistered");
    });

    phone.on('registrationFailed', function (e) {
        globalScope.currentCall.error = true;
        globalScope.$apply();
        console.log(e);
        console.log("Registration failed");

    });




    window.options = options;
    window.phone = phone;
    phone.start();
//    phone.call('sip:eogus618@sip.linphone.org', options);

}());