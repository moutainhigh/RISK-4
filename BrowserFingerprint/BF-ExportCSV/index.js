/*
    --------------------------------------------------------------------
    DATABASE
    --------------------------------------------------------------------
*/

const mongo = require('mongodb').MongoClient
const url = 'mongodb+srv://cong:cong@cluster0-nvvfw.mongodb.net/test?retryWrites=true&w=majority'
// const url = 'mongodb://localhost:27017'
let collectionHeaders
let collectionHardware
let collectionPersonal
let collectionEnvironment
let collectionFingerprints

async function connectMongoDB() {
    await mongo.connect(url, { useNewUrlParser: true, useUnifiedTopology: true })
    .then((client) => {
        let myDB = client.db('BrowserFingerprintDB')
        collectionHeaders = myDB.collection('HTTPHeaders')
        collectionHardware = myDB.collection('Hardware')
        collectionPersonal = myDB.collection('Personal')
        collectionEnvironment = myDB.collection('Environment')
        collectionFingerprints = myDB.collection('Fingerprints')
        console.log("MongoDB connected")
    })
    .catch((err) => {
        console.log('[ERROR] Connect to database BrowserFingerprintDB fail')
        console.log(err)
    })
}

// Get collection "HTTPHeaders" information
// Result: "HTTPHeaders" collection | Null
async function getHTTPHeaders() {
    let result = await collectionHeaders.find().toArray()
    return (result != null) ? result : null
}

// Get one element from collection "Hardware" by _id
// Result: One element from "Hardware" collection | Null
async function getElementHardware(_id) {
    let result = await collectionHardware.findOne({_id: _id})
    return (result != null) ? result : null
}

// Get one element from collection "Personal" by _id
// Result: One element from "Personal" collection | Null
async function getElementPersonal(_id) {
    let result = await collectionPersonal.findOne({_id: _id})
    return (result != null) ? result : null
}

// Get one element from collection "Environment" by _id
// Result: One element from "Environment" collection | Null
async function getElementEnvironment(_id) {
    let result = await collectionEnvironment.findOne({_id: _id})
    return (result != null) ? result : null
}

// Get one element from collection "Fingerprints" by _id
// Result: One element from "Fingerprints" collection | Null
async function getElementFingerprints(_id) {
    let result = await collectionFingerprints.findOne({_id: _id})
    return (result != null) ? result : null
}

/*
    --------------------------------------------------------------------
    FUNCTIONS
    --------------------------------------------------------------------
*/

// JSON.parse for DeviceInfo
/*
    Params:
    -------
    + HTTPHeaders
    + Hardware
    + Personal
    + Environment
    + Fingerprints
*/
// Result: JSON List
function jsonParseDeviceInfo (
    HTTPHeaders,
    Hardware,
    Personal,
    Environment,
    Fingerprints
    ) {
    return JSON.parse('{"HTTPHeaders" : ' + JSON.stringify(HTTPHeaders) + ', "Hardware" : ' + JSON.stringify(Hardware) + ', "Personal" : ' + JSON.stringify(Personal) + ', "Environment" : ' + JSON.stringify(Environment) + ', "Fingerprints" : ' + JSON.stringify(Fingerprints) + '}')
}

async function getAllDeviceInfo() {
    let allDeviceInfo = []

    resHTTPHeadersArray = await getHTTPHeaders()
    const forLoop = async _ => {
        for (let i = 0; i < resHTTPHeadersArray.length; i++) {
            const element = resHTTPHeadersArray[i]

            resElementHardware = await getElementHardware(element._id)
            resELementPersonal = await getElementPersonal(element._id)
            resElementEnvironment = await getElementEnvironment(element._id)
            resElementFingerprints = await getElementFingerprints(element._id)

            allDeviceInfo.push(jsonParseDeviceInfo(element, resElementHardware, resELementPersonal, resElementEnvironment, resElementFingerprints))
        }
    }
    await forLoop()

    return allDeviceInfo
}

/*
    --------------------------------------------------------------------
    SERVER
    --------------------------------------------------------------------
*/

var express = require('express')
var app = express();
app.set('port', (process.env.PORT || 5000));
var { Parser } = require('json2csv')

function deleteCurlyMark(str) {
    newstr = str.replace('{','').replace('}','')
    return newstr
}

app.get('/exportcsv', async function (req, res) {
    data = await getAllDeviceInfo()

    for (let i = 0; i < data.length; i++) {
        let element = data[i]

        HTTPHeaders = element['HTTPHeaders']
        Hardware = element['Hardware']
        Personal = element['Personal']
        Environment = element['Environment']
        Fingerprints = element['Fingerprints']

        UserAgentDetail = HTTPHeaders['UserAgentDetail']
        delete HTTPHeaders['UserAgentDetail']

        result = JSON.parse('{' + deleteCurlyMark(JSON.stringify(HTTPHeaders)) + ',' 
                                + deleteCurlyMark(JSON.stringify(UserAgentDetail)) + ',' 
                                + deleteCurlyMark(JSON.stringify(Hardware)) + ','
                                + deleteCurlyMark(JSON.stringify(Personal)) + ','
                                + deleteCurlyMark(JSON.stringify(Environment)) + ','
                                + deleteCurlyMark(JSON.stringify(Fingerprints))
                            + '}')
        data[i] = result
    }

    const fields = ['_id', 'UserAgent', 'Accept', 'AcceptEncoding', 'AcceptLanguage', 'DeviceBrand', 'DeviceModel', 'DeviceType', 'OSName', 'OSVersion', 'ClientName', 'ClientVersion', 'ZaloVersion', 'CPUVirtualCores', 'TouchCompability', 'WebGLVendor', 'WebGLRenderer', 'DeviceRTC', 'Language', 'ScreenResolution', 'ColorDepth', 'TimeZone', 'FontList', 'IPAddress', 'CountryCode', 'ContinentCode', 'Latitude', 'Longitude', 'CanvasFP', 'WebGLFP', 'AudioFP', 'BrowserFP'];

    const json2csv = new Parser({ fields: fields })
    csv = json2csv.parse(data)
    res.attachment('BF-Data.csv')
    res.status(200).send(csv)
})

// App listens...
app.listen(app.get('port'), function () {
	console.log('Server is listening at port ' + app.get('port'));
	connectMongoDB();
});