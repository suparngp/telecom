var myApp= angular.module('myApp',[]);

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
})

//in the scope the function name is provided
function ContactsController($scope, ContactsFunction){
    $scope.contacts = ContactsFunction;
}

function FirstController($scope){
    $scope.data = {message:"WORLD"};
}