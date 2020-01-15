/*
    =======================    
    SERVER
    =======================

    Installation:
    + Module/Lib "express"
    + Module/Lib "ua-parser-js"
    + Module/Lib "body-parser"
    + Module/Lib "express-handlebars"
*/

// Run server
var express = require('express')
var app = express();
app.set('port', (process.env.PORT || 5000));
app.listen(app.get('port'), function () {
	console.log('Server is listening at port ' + app.get('port'));
});

// Using body parser and directing static folder (/public)
var bodyParser = require("body-parser");
app.use(bodyParser.urlencoded({
	extended: true
}))
app.use(bodyParser.json());
app.use(express.static(__dirname + '/public'));


// Using handle bars
var expressHbs = require('express-handlebars');
var hbs = expressHbs.create({
	extname: 'hbs',
	defaultLayout: 'layout',
	layoutsDir: __dirname + '/views/layouts/',
	partialsDir: __dirname + '/views/partials/'
});
app.engine('hbs', hbs.engine);
app.set('view engine', 'hbs');

// Declare DeviceDetector & BrowserInfo variable
const parser = require('ua-parser-js');

// Get browser info
function getBrowserInfo(req) {
    userAgent = req.get('User-Agent');
    detectInformation = parser(userAgent);
	
	// Get ZaloVersion information
	let zaloVersion = userAgent
	posZalo = userAgent.indexOf('Zalo');
	if (posZalo === -1) zaloVersion = "Not zalo"
	else zaloVersion = zaloVersion.slice(posZalo + 5);

	let hardwareInfo = req.body.jsonHardware;
    let personalInfo = req.body.jsonPersonal;
    let fingerprintInfo = req.body.jsonFingerprint;

    return [
        {
			id: "1",
			field: "User-Agent",
			value: userAgent
		},
		{
			id: "2",
			field: "Device brand",
			value: (detectInformation.device.vendor === undefined) ? 'undefined' :
			detectInformation.device.vendor
		},
		{
			id: "3",
			field: "Device model",
			value: (detectInformation.device.model === undefined) ? 'undefined' : detectInformation.device.model
		},
		{
			id: "4",
			field: "Device type",
			value: (detectInformation.device.type == undefined) ? 'undefined' : detectInformation.device.type
		},
		{
			id: "5",
			field: "Client name",
			value: detectInformation.browser.name
		},
		{
			id: "6",
			field: "Client version",
			value: detectInformation.browser.version
		}
		, 
        {
			id: "7",
			field: "Zalo version",
			value: zaloVersion
        },
        {
			id: "8",
			field: "Accept",
			value: (req.headers["accept"]).toString().replace(/\,+/g, " ")
        }, 
        {
			id: "9",
			field: "Accept encoding",
			value: req.headers["accept-encoding"]
        }, 
        {
			id: "10",
			field: "Accept language",
			value: req.headers["accept-language"]
        },
        {
			id: "11",
			field: "CPU virtual cores",
			value: hardwareInfo['CPU virtual cores']
        },
        {
			id: "12",
			field: "Renderer WebGL",
			value: hardwareInfo['Renderer WebGL']
        },
        {
			id: "13",
			field: "DeviceRTC info",
			value: hardwareInfo['DeviceRTC info']
		},
		{
			id: "14",
			field: "Time zone",
			value: personalInfo['Time zone']
		},
		{
			id: "15",
			field: "City",
			value: personalInfo['City']
		},
		{
			id: "16",
			field: "Continent",
			value: personalInfo['Continent']
        },
		{
			id: "17",
			field: "Screen resolution",
			value: personalInfo['Screen resolution']
        },
        {
			id: "18",
			field: "Font list",
			value: personalInfo['Font list']
        },
        {
			id: "19",
			field: "Canvas FP",
			value: fingerprintInfo['Canvas FP']
        },
        {
			id: "20",
			field: "WebGL FP",
			value: fingerprintInfo['WebGL FP']
        },
        {
			id: "21",
			field: "Audio FP",
			value: fingerprintInfo['Audio FP']
		},{
			id: "22",
			field: "Sensor orientation",
			value: personalInfo['Sensor orientation']
		}
    ]
}

// URL: '/'
app.get('/', (req, res) => {
    res.render('homepage')
});

// URL: '/user/info'
// Result: "browserInfo" will contains all info of client
app.post('/user/info', (req, res) => {
	browserInfo = getBrowserInfo(req)
	console.log(browserInfo)
    res.send('[SUCCESS] Server received information from client!')
});