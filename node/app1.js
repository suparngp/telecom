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
var phone1;

var response;
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
    response = res;
    var Contacts= {};
    Contacts.details=[
        {name: 'Suparn',    phn: '12345',     email:'some@getonsip.com' },
        {name: 'Rajendra',  phn: '34567',     email:''    },
        {name: 'Kim',       phn: '56789',     email:'KoreaKim@sip.com'    } ,
        {name: 'Aditya',    phn: '78901',     email:'adthakkar@sip.com'    }
        // and so on
    ];

    // TODO get the contacts from the phone and then send the JSON response to the browser.

    phone1.emit('contact_req');
    //res.json(Contacts);
});

app.get('/messages', function(req, res, next){
    console.log("received the message req");
    response = res;
//    var data = [{"contactName":"","contactNum":"15555215554","date":"1384490812673","message":"Yes Android is great","msgRead":"1","msgType":"2"},{"contactName":"","contactNum":"15555215554","date":"1384490801065","message":"Hello","msgRead":"1","msgType":"2"},{"contactName":"1","contactNum":"15555215554","date":"1384490172918","message":"Android is awesome","msgRead":"1","msgType":"1"},{"contactName":"1","contactNum":"15555215554","date":"1384490158187","message":"Hello","msgRead":"1","msgType":"1"},{"contactName":"1","contactNum":"15555215554","date":"1384490142620","message":"Hi","msgRead":"1","msgType":"1"}];
//    res.json(data);
    phone1.emit('sms_req');
    // TODO send request to phone to get all the messages.
});

app.post('/send/sms', function(req, res, next){

    response = res;
    // TODO send the request to phone to send the sms
    phone1.emit('send_sms_req', JSON.stringify({number: req.body.number, message: req.body.content}));

});

app.post('/call/phone', function(req, res, next){
    response = res;
    phone1.emit('send_sip_req', JSON.stringify(req.body));
});

var server = require('http').createServer(app);
server.listen(3000);
var io = socket.listen(server);


io.sockets.on('connection', function (socket) {
    phone1 = socket;
    console.log("connection");
    socket.on('message', function(message){
        console.log(message);
        socket.send(message);

//        socket.emit("contact_req", {'data': 'yahoo'});
//        socket.emit('sms_req', {data: 'yahoo'});
//        socket.emit('send_sms_req', JSON.stringify({number: '15555215556', message: "I am also!"}));
    });

    //socket.emit('news', { hello: 'world' });
    socket.on('my other event', function (data) {
        console.log(data);
    });

    socket.on('contact_res', function(message){
        console.log(message);
        console.log("Received the contacts list");
        socket.send("Received the contacts");
        response.json(message);
    });
    socket.on('sms_res', function(message){
        console.log("Received the smses");
        console.log(message);
        socket.send('Received the smses');
        response.json(message);
    });

    socket.on('send_sms_res', function(message){
        console.log("Send SMS response received");
        console.log(message);
        response.json("Sms sent");
    });

    socket.on('send_sip_res', function(message){
        console.log("Send Sip res received");
        console.log(message);
        response.json(200, "Done");
    });

});
