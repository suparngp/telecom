
/**
 * Module dependencies.
 */

var express = require('express');
var routes = require('./routes');
var user = require('./routes/user');
var http = require('http');
var path = require('path');
var socket = require("websocket.io");
var app = express();

// all environments
app.set('port', process.env.PORT || '3000');
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

app.get('/', routes.index);
app.get('/users', user.list);


var server = require('http').createServer(app);

io = socket.attach(server);
server.listen(3000);
io.on('connection', function (socket) {
    console.log("connection");
    socket.on('message', function(message){
        console.log(message);
        socket.send(message);
        switch(message.msgType){

        }
    });

    socket.emit('news', { hello: 'world' });
    socket.on('my other event', function (data) {
        console.log(data);
    });
});