var myApp= angular.module('myApp',[]);

myApp.factory('ContactsFunction', function(){

    var Contacts= {};
    Contacts.details=[
        {name: 'Suparn',    phn: '12345',     sip_num:'s09iu8' },
        {name: 'Rajendra',  phn: '34567',     sip_num:'r@4jk'  },
        {name: 'Kim',       phn: '56789',     sip_num:'k!iu5'  } ,
        {name: 'Aditya',    phn: '78901',     sip_num:'@d!9i'  }
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