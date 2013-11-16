/**
 * Created by suparngupta on 11/15/13.
 */


;
(function () {

    var session;
    var audioElem = document.getElementById('call');
    var configuration = {
        ws_servers: ["wss://edge.sip.onsip.com"],
        register: true,
        'uri': 'sip:some@getonsip.com',
        'authorization_user': "getonsip_some",
        'password': 'zRFKwdKFvQsBqRVR'
    };

    var phone = new JsSIP.UA(configuration);

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
        console.log(e);
        console.log("Registration failed");
    });


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
                document.getElementById('call').src = window.URL.createObjectURL(session.getRemoteStreams()[0]);
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
    window.options = options;
    window.phone = phone;
//        phone.start();
//        phone.call('sip:eogus618@sip.linphone.org', options);

}());