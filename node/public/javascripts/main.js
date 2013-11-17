var myApp= angular.module('myApp',['ui.router']);

myApp.config(function($stateProvider, $urlRouterProvider){

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

myApp.factory('ContactsFunction', function(){

    var Contacts= {};
    Contacts.details=[
        {name: 'Suparn',    phn: '12345',     email:'suparn@sip.com' },
        {name: 'Rajendra',  phn: '34567',     email:''    },
        {name: 'Kim',       phn: '56789',     email:'kim@sip.com'    } ,
        {name: 'Aditya',    phn: '78901',     email:'adi@sip.com'    }
        // and so on
    ];
    return Contacts;
});

myApp.directive('contacts', function($http){
   return {
       restrict: 'A',
       link: function(scope, element, attrs){
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
               .success(function(data, status, headers, config){
                   console.log(data);
                   scope.contacts = data;
               });
       }
   }
});
myApp.controller('MainController', function($scope){

});


//in the scope the function name is provided
function ContactsController($scope, ContactsFunction){
    //$scope.contacts = ContactsFunction;

    $scope.call = function(target){
        console.log("I am calling " + target);
    }
}

function FirstController($scope){
    $scope.data = {message:"WORLD"};
}


/*<script>
 function MakeCall()
 {
 document.getElementById("demo").innerHTML="Hello World";
 }
 </script>
 */
