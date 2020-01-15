let repoMongo = require('../repository/repoMongo')
let repoRedis = require('../repository/repoRedis');
let fuzzySearch = require('./fuzzysearch')
let service = {}

/*
    ===============================================================================================
                                                MONGODB
    ===============================================================================================
*/

/*
    ---------------------------------------------    
    Connect to MongoDB
    ---------------------------------------------
*/

service.connectMongoDB = async () => {
    await repoMongo.connectMongoDB()
}

/*
    ---------------------------------------------    
    Functions: JSON parse
    ---------------------------------------------
*/

// JSON.parse for UserAgentDetail
/* 
    Params:
    -------
    + DeviceBrand
    + DeviceModel
    + DeviceType
    + OSName
    + OSVersion
    + ClientName
    + ClientVersion
    + ZaloVersion
*/
// Result: JSON List
service.jsonParseUserAgentDetail = (
    DeviceBrand, 
    DeviceModel,
    DeviceType,
    OSName,
    OSVersion,
    ClientName,
    ClientVersion,
    ZaloVersion
    ) => {
    return JSON.parse('{"DeviceBrand" : "' + DeviceBrand + '", "DeviceModel" : "' + DeviceModel + '", "DeviceType" : "' + DeviceType + '", "OSName" : "' + OSName + '", "OSVersion" : "' + OSVersion + '", "ClientName" : "' + ClientName + '", "ClientVersion" : "' + ClientVersion + '", "ZaloVersion" : "' + ZaloVersion + '"}')
}

// JSON.parse for HTTPHeaders
/* 
    Params:
    -------
    + _id
    + UserAgent
    + UserAgentDetail
    + Accept
    + AcceptEncoding
    + AcceptLanguage
*/
// Result: JSON List
service.jsonParseHTTPHeaders = (
    _id,
    UserAgent,
    UserAgentDetail,
    Accept,
    AcceptEncoding,
    AcceptLanguage
    ) => {
    return JSON.parse('{"_id" : "' + _id + '", "UserAgent" : "' + UserAgent + '", "UserAgentDetail" : ' + JSON.stringify(UserAgentDetail) + ', "Accept" : "' + Accept + '", "AcceptEncoding" : "' + AcceptEncoding + '", "AcceptLanguage" : "' + AcceptLanguage + '"}')
}

// JSON.parse for Hardware
/* 
    Params:
    -------
    + _id
    + CPUVirtualCores
    + TouchCompability
    + WebGLVendor
    + WebGLRenderer
    + DeviceRTC
*/
// Result: JSON List
service.jsonParseHardware = (
    _id,
    CPUVirtualCores,
    TouchCompability,
    WebGLVendor,
    WebGLRenderer,
    DeviceRTC
    ) => {
    return JSON.parse('{"_id" : "' + _id + '", "CPUVirtualCores" : "' + CPUVirtualCores + '", "TouchCompability" : "' + TouchCompability + '", "WebGLVendor" : "' + WebGLVendor + '", "WebGLRenderer" : "' + WebGLRenderer + '", "DeviceRTC" : "' + DeviceRTC + '"}')
}

// JSON.parse for Personal
/* 
    Params:
    -------
    + _id
    + Language
    + ScreenResolution
    + ColorDepth
    + TimeZone
    + FontList
*/
// Result: JSON List
service.jsonParsePersonal = (
    _id,
    Language,
    ScreenResolution,
    ColorDepth,
    TimeZone,
    FontList
    ) => {
    return JSON.parse('{"_id" : "' + _id + '", "Language" : "' + Language + '", "ScreenResolution" : "' + ScreenResolution + '", "ColorDepth" : "' + ColorDepth + '", "TimeZone" : "' + TimeZone + '", "FontList" : "' + FontList + '"}')
}

// JSON.parse for Environment
/* 
    Params:
    -------
    + _id
    + IPAddress
    + CountryCode
    + ContinentCode
    + Latitude
    + Longitude
*/
// Result: JSON List
service.jsonParseEnvironment = (
    _id,
    IPAddress,
    CountryCode,
    ContinentCode,
    Latitude,
    Longitude
    ) => {
    return JSON.parse('{"_id" : "' + _id + '", "IPAddress" : "' + IPAddress + '", "CountryCode" : "' + CountryCode + '", "ContinentCode" : "' + ContinentCode + '", "Latitude" : "' + Latitude + '", "Longitude" : "' + Longitude + '"}')
}

// JSON.parse for Fingerprints
/* 
    Params:
    -------
    + _id
    + CanvasFP
    + WebGLFP
    + AudioFP
    + BrowserFP
*/
// Result: JSON List
service.jsonParseFingerprints = (
    _id,
    CanvasFP,
    WebGLFP,
    AudioFP,
    BrowserFP
    ) => {
    return JSON.parse('{"_id" : "' + _id + '", "CanvasFP" : "' + CanvasFP + '", "WebGLFP" : "' + WebGLFP + '", "AudioFP" : "' + AudioFP + '", "BrowserFP" : "' + BrowserFP + '"}')
}

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
service.jsonParseDeviceInfo = (
    HTTPHeaders,
    Hardware,
    Personal,
    Environment,
    Fingerprints
    ) => {
    return JSON.parse('{"HTTPHeaders" : ' + JSON.stringify(HTTPHeaders) + ', "Hardware" : ' + JSON.stringify(Hardware) + ', "Personal" : ' + JSON.stringify(Personal) + ', "Environment" : ' + JSON.stringify(Environment) + ', "Fingerprints" : ' + JSON.stringify(Fingerprints) + '}')
}

/*
    ---------------------------------------------    
    Functions: Get/Insert/Update info from DB
    ---------------------------------------------
*/

// Add new device information into database
// Params: JSON List of HTTPHeaders, Hardware, Personal, Environment, Fingerprints 
// Result: Device Info JSON List
service.addNewDeviceInfo = async (
    HTTPHeaders, 
    Hardware, 
    Personal, 
    Environment, 
    Fingerprints
    ) => {
    resHTTPHeaders = await repoMongo.insertElementHTTPHeaders(HTTPHeaders)
    resHardware = await repoMongo.insertElementHardware(Hardware)
    resPersonal = await repoMongo.insertElementPersonal(Personal)
    resEnvironment = await repoMongo.insertElementEnvironment(Environment)
    resFingerprints = await repoMongo.insertElementFingerprints(Fingerprints)
    
    return service.jsonParseDeviceInfo(resHTTPHeaders, resHardware, resPersonal, resEnvironment, resFingerprints)
}

// Get device info by _id
// Param: String uuid
// Result: Device info JSON List
service.getDeviceInfo = async (_id) => {
    resHTTPHeaders = await repoMongo.getElementHTTPHeaders(_id)
    resHardware = await repoMongo.getElementHardware(_id)
    resPersonal = await repoMongo.getElementPersonal(_id)
    resEnvironment = await repoMongo.getElementEnvironment(_id)
    resFingerprints = await repoMongo.getElementFingerprints(_id)

    return service.jsonParseDeviceInfo(resHTTPHeaders, resHardware, resPersonal, resEnvironment, resFingerprints)
}

// Get all device info
// Result: Device Info JSON Array
service.getAllDeviceInfo = async () => {
    let allDeviceInfo = []

    resHTTPHeadersArray = await repoMongo.getHTTPHeaders()
    const forLoop = async _ => {
        for (let i = 0; i < resHTTPHeadersArray.length; i++) {
            const element = resHTTPHeadersArray[i]

            resElementHardware = await repoMongo.getElementHardware(element._id)
            resELementPersonal = await repoMongo.getElementPersonal(element._id)
            resElementEnvironment = await repoMongo.getElementEnvironment(element._id)
            resElementFingerprints = await repoMongo.getElementFingerprints(element._id)

            allDeviceInfo.push(service.jsonParseDeviceInfo(element, resElementHardware, resELementPersonal, resElementEnvironment, resElementFingerprints))
        }
    }
    await forLoop()

    return allDeviceInfo
}

// Find nearest information of device
// Param: deviceInfo JSON List
// Result: Nearest Device Info | Null
service.findNearestInformation = async (deviceInfo) => {
    // Get all device info in DB
    allDeviceInfo = await service.getAllDeviceInfo()
    if (allDeviceInfo.length == 0) return null

    // Fuzzy Search
    return fuzzySearch.findNearestInformation(deviceInfo, allDeviceInfo)
}

// Check fingerprint existed in DB or not
// Param: bfzp
// Result: Browser fingerprint or Null
service.isExistedFingerprint = async (bfzp) => {
    resFingerprints = await repoMongo.getFingerprints()
    let fingerprint = null;
    resFingerprints.forEach(element => {
        if (element.BrowserFP === bfzp) {
            fingerprint = element.BrowserFP
        }
    });
    return fingerprint
}

/*
    ===============================================================================================
                                                REDIS
    ===============================================================================================
*/

/*
    ---------------------------------------------    
    Connect to Redis
    ---------------------------------------------
*/

// Connect to repository of MongoDB
service.connectRedis = () => {
    repoRedis.connectRedis()
}

// Set value of fingerprint to Redis
service.addBFToRedis = async (fingerprint) => {
    return await repoRedis.setBF(fingerprint)
}

// Check BF existed in Redis or not
// Parameter: STRING fingerprint
// Result: True | False
service.ExistedBFInRedis = async (fingerprint) => {
    return await repoRedis.isMemberBF(fingerprint)
}

module.exports = service;