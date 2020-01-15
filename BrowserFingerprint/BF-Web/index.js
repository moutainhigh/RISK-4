var express = require('express')
var app = express();
let service = require('./service/service');
let validateBF = require('./service/validateBF');
var sslRedirect = require('heroku-ssl-redirect');
const uuidv4 = require('uuid/v4');

// Using body parser and directing static folder (/public)
var bodyParser = require("body-parser");
app.use(bodyParser.urlencoded({
	extended: true
}))
app.use(bodyParser.json());
app.use(express.static(__dirname + '/public'));

// Using SSL for URl
app.use(sslRedirect());

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
app.set('port', (process.env.PORT || 5000));

// Functions
function parseUserAgentInfo(req) {
	// Declare variables
	const DeviceDetector = require('node-device-detector');
	const detector = new DeviceDetector;

	var userAgent = req.get('User-Agent');
	detectInformation = detector.detect(userAgent);
	
	
	// Get ZaloVersion information
	var zaloVersion = userAgent;
	var posZalo = userAgent.indexOf('Zalo');

	if (posZalo === -1) 
		zaloVersion = "Not zalo"
	else 
	{
		zaloVersion = zaloVersion.slice(posZalo + 5);
	}

	return [
		{
			id: "HH-0",
			field: "User-Agent",
			value: userAgent
		},
		{
			id: "HH-1",
			field: "Device brand",
			value: (detectInformation.device.brand.length == 0) ? 'undefined' : detectInformation.device.brand
		},
		{
			id: "HH-2",
			field: "Device model",
			value: (detectInformation.device.model.length == 0) ? 'undefined' : detectInformation.device.model
		},
		{
			id: "HH-3",
			field: "Device type",
			value: detectInformation.device.type
		},
		{
			id: "HH-4",
			field: "OS name",
			value: detectInformation.os.name
		},
		{
			id: "HH-5",
			field: "OS version",
			value: (detectInformation.os.version.length == 0) ? 'undefined' : detectInformation.os.version
		},
		{
			id: "HH-6",
			field: "Client name",
			value: detectInformation.client.name
		},
		{
			id: "HH-7",
			field: "Client version",
			value: detectInformation.client.version
		},
		{
			id: "HH-8",
			field: "Zalo version",
			value: zaloVersion
		}, 
		{
			id: "HH-9",
			field: "Accept",
			value: (req.headers["accept"]).toString().replace(/\,+/g, " ")
		}, 
		{
			id: "HH-10",
			field: "Accept encoding",
			value: req.headers["accept-encoding"]
		}, 
		{
			id: "HH-11",
			field: "Accept language",
			value: req.headers["accept-language"]
		}
	]
}

app.get('/', (req, res) => {
	res.locals.HHContent = parseUserAgentInfo(req);
	res.render('homepage');
});

app.get('/d3m-bf-doc', (req, res) => {
	res.render('document');
});

app.post('/user/info', async (req, res) => {
	let idElement = uuidv4();
	let browserFingerprint = uuidv4();
	await validateBF.processInfoThenReturnBF(req, res, idElement, browserFingerprint);
});

app.post('/user/validate', async (req, res) => {
	isExist = await validateBF.validate(req.body.bfzp);
	if (isExist){
		res.json({
			isValidated: true,
			message: "Fingerprint got from local storage is validated!"
		});
	}else{
		res.json({
			isValidated: false,
			message: "Fingerprint got from local storage is not validated"
		});
	}
});

app.get('/d3m-bf-doc/*', function (req, res) {
	res.redirect('/');
});

app.get('*', function (req, res) {
	res.redirect('/');
});

// App listens...
app.listen(app.get('port'), function () {
	console.log('Server is listening at port ' + app.get('port'));
	service.connectMongoDB();
	service.connectRedis();
});