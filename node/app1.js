/**
 * Created by suparngupta on 11/16/13.
 */


/**
 * Module dependencies.
 */

var express = require('express');
var routes = require('./routes');
var user = require('./routes/user');
var http = require('http');
var path = require('path');
var socket = require("socket.io");
var app = express();

// all environments
app.set('port', process.env.PORT || 3000);
app.set('views', __dirname + '/views');
app.set('view engine', 'jade');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.bodyParser());
app.use(express.methodOverride());
app.use(app.router);
app.use(express.static(path.join(__dirname, 'public')));

// development only
if ('development' == app.get('env')) {
    app.use(express.errorHandler());
}

app.get('/', function(req, res, next){
    res.redirect('/index2.html');
});
app.get('/users', user.list);

app.get('/contacts', function(req, res, next){
    var Contacts= {};
    Contacts.details=[
        {name: 'Suparn',    phn: '12345',     email:'some@getonsip.com' },
        {name: 'Rajendra',  phn: '34567',     email:''    },
        {name: 'Kim',       phn: '56789',     email:'KoreaKim@sip.com'    } ,
        {name: 'Aditya',    phn: '78901',     email:'adthakkar@sip.com'    }
        // and so on
    ];

    // TODO get the contacts from the phone and then send the JSON response to the browser.
    res.json(Contacts);
});

app.get('/messages', function(req, res, next){

    // TODO send request to phone to get all the messages.
});

app.post('/send/sms', function(req, res, next){

    // TODO send the request to phone to send the sms
    res.json(req.body);
});
var server = require('http').createServer(app);
server.listen(3000);
var io = socket.listen(server);


io.sockets.on('connection', function (socket) {
    console.log("connection");
    socket.on('message', function(message){
        console.log(message);
        socket.send(message);

        socket.emit("contact_req", {'data': 'yahoo'});
        socket.emit('sms_req', {data: 'yahoo'});
        socket.emit('send_sms_req', JSON.stringify({number: '15555215556', message: "I am also!"}));
    });

    //socket.emit('news', { hello: 'world' });
    socket.on('my other event', function (data) {
        console.log(data);
    });

    socket.on('contact_res', function(message){
        console.log(message);
        console.log("Received the contacts list");
        socket.send("Received the contacts");
    });
    socket.on('sms_res', function(message){
        console.log("Received the smses");
        console.log(message);
        socket.send('Received the smses');
    });

    socket.on('send_sms_res', function(message){
        console.log("Send SMS response received");
        console.log(message);
    });
});
