const mongo = require('mongodb').MongoClient
const url = 'mongodb+srv://cong:cong@cluster0-nvvfw.mongodb.net/test?retryWrites=true&w=majority'
// const url = 'mongodb://localhost:27017'
const { promisify } = require('util');
let repoMongo = {}
let collectionHeaders
let collectionHardware
let collectionPersonal
let collectionEnvironment
let collectionFingerprints

/* 
    ---------------------------------------------
    Database name: "BrowserFingerprintDB"
    Name of collections in database: 
        + HTTPHeaders
        + Hardware
        + Personal
        + Environment
        + Fingerprints
    Name of collections in source code:
        + collectionHeaders
        + collectionHardware
        + collectionPersonal
        + collectionEnvironment
        + collectionFingerprints
    ---------------------------------------------
*/

/*
    ---------------------------------------------    
    Connect to MongoDB
    ---------------------------------------------
*/
repoMongo.connectMongoDB = async () => {
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

/*
    ---------------------------------------------    
    Get information of collection from MongoDB
    ---------------------------------------------
*/
// Get collection "HTTPHeaders" information
// Result: "HTTPHeaders" collection | Null
repoMongo.getHTTPHeaders = async () => {
    let result = await collectionHeaders.find().toArray()
    return (result != null) ? result : null
}

// Get one element from collection "HTTPHeaders" by _id
// Result: One element from "HTTPHeaders" collection | Null
repoMongo.getElementHTTPHeaders = async (_id) => {
    let result = await collectionHeaders.findOne({_id: _id})
    return (result != null) ? result : null
}

// Get collection "Hardware" information
// Result: "Hardware" collection | Null
repoMongo.getHardware = async () => {
    let result = await collectionHardware.find().toArray()
    return (result != null) ? result : null
}

// Get one element from collection "Hardware" by _id
// Result: One element from "Hardware" collection | Null
repoMongo.getElementHardware = async (_id) => {
    let result = await collectionHardware.findOne({_id: _id})
    return (result != null) ? result : null
}

// Get collection "Personal" information
// Result: "Personal" collection | Null
repoMongo.getPersonal = async () => {
    let result = await collectionPersonal.find().toArray()
    return (result != null) ? result : null
}

// Get one element from collection "Personal" by _id
// Result: One element from "Personal" collection | Null
repoMongo.getElementPersonal = async (_id) => {
    let result = await collectionPersonal.findOne({_id: _id})
    return (result != null) ? result : null
}

// Get collection "Environment" information
// Result: "Environment" collection | Null
repoMongo.getEnvironment = async () => {
    let result = await collectionEnvironment.find().toArray()
    return (result != null) ? result : null
}

// Get one element from collection "Environment" by _id
// Result: One element from "Environment" collection | Null
repoMongo.getElementEnvironment = async (_id) => {
    let result = await collectionEnvironment.findOne({_id: _id})
    return (result != null) ? result : null
}

// Get collection "Fingerprints" information
// Result: "Fingerprints" collection | Null
repoMongo.getFingerprints = async () => {
    let result = await collectionFingerprints.find().toArray()
    return (result != null) ? result : null
}

// Get one element from collection "Fingerprints" by _id
// Result: One element from "Fingerprints" collection | Null
repoMongo.getElementFingerprints = async (_id) => {
    let result = await collectionFingerprints.findOne({_id: _id})
    return (result != null) ? result : null
}

/*
    ---------------------------------------------    
    Insert element to collections of MongoDB
    ---------------------------------------------
*/
// Insert one element to collection "HTTPHeaders"
/* 
    Params:
    -------
    > HTTPHeaders : JSON List
        + _id : uuid
        + UserAgent : String
        + UserAgentDetail : JSON List
            - DeviceBrand
            - DeviceModel
            - DeviceType
            - OSName
            - OSVersion
            - ClientName
            - ClientVersion
            - ZaloVersion
        + Accept : String
        + AcceptEncoding : String
        + AcceptLanguage : String      
*/
// Result: Element data | Null
repoMongo.insertElementHTTPHeaders = (HTTPHeaders) => {
    getAsync = promisify(collectionHeaders.insertOne).bind(collectionHeaders)
    return getAsync({
        _id: HTTPHeaders._id, 
        UserAgent: HTTPHeaders.UserAgent,
        UserAgentDetail: HTTPHeaders.UserAgentDetail, 
        Accept: HTTPHeaders.Accept, 
        AcceptEncoding: HTTPHeaders.AcceptEncoding, 
        AcceptLanguage: HTTPHeaders.AcceptLanguage
    }).then((res) => {
        return res
    }).catch((err) => {
        console.log('[ERROR] Database inserts element to collection "HTTPHeaders" fail!')
        console.log(err)
        return null
    })
}

// Insert one element to collection "Hardware"
/* 
    Params:
    -------
    > Hardware : JSON List
        + _id : uuid
        + CPUVirtualCores : String
        + TouchCompability : String
        + WebGLVendor : String
        + WebGLRenderer : String
        + DeviceRTC : String
*/
// Result: Element data | Null
repoMongo.insertElementHardware = (Hardware) => {
    getAsync = promisify(collectionHardware.insertOne).bind(collectionHardware)
    return getAsync({
        _id: Hardware._id, 
        CPUVirtualCores: Hardware.CPUVirtualCores, 
        TouchCompability: Hardware.TouchCompability, 
        WebGLVendor: Hardware.WebGLVendor, 
        WebGLRenderer: Hardware.WebGLRenderer, 
        DeviceRTC: Hardware.DeviceRTC
    }).then((res) => {
        return res
    }).catch((err) => {
        console.log('[ERROR] Database inserts element to collection "Hardware" fail!')
        console.log(err)
        return null
    })
} 

// Insert one element to collection "Personal"
/* 
    Params:
    -------
    > Personal : JSON List
        + _id : uuid
        + Language : String
        + ScreenResolution : String
        + ColorDepth : String
        + TimeZone : String
        + FontList : String
*/
// Result: Element data | Null
repoMongo.insertElementPersonal = (Personal) => {
    getAsync = promisify(collectionPersonal.insertOne).bind(collectionPersonal)
    return getAsync({
        _id: Personal._id, 
        Language: Personal.Language, 
        ScreenResolution: Personal.ScreenResolution, 
        ColorDepth: Personal.ColorDepth, 
        TimeZone: Personal.TimeZone, 
        FontList: Personal.FontList
    }).then((res) => {
        return res
    }).catch((err) => {
        console.log('[ERROR] Database inserts element to collection "Personal" fail!')
        console.log(err)
        return null
    })
} 

// Insert one element to collection "Environment"
/* 
    Params:
    -------
    > Environment : JSON List
        + _id : uuid
        + IPAddress : String
        + CountryCode : String
        + ContinentCode : String
        + Latitude : String
        + Longitude : String
*/
// Result: Element data | Null
repoMongo.insertElementEnvironment = (Environment) => {
    getAsync = promisify(collectionEnvironment.insertOne).bind(collectionEnvironment)
    return getAsync({
        _id: Environment._id, 
        IPAddress: Environment.IPAddress, 
        CountryCode: Environment.CountryCode, 
        ContinentCode: Environment.ContinentCode, 
        Latitude: Environment.Latitude, 
        Longitude: Environment.Longitude
    }).then((res) => {
        return res
    }).catch((err) => {
        console.log('[ERROR] Database inserts element to collection "Environment" fail!')
        console.log(err)
        return null
    })
}

// Insert one element to collection "Fingerprints"
/* 
    Params:
    -------
    > Fingerprints : JSON List
        + _id : uuid
        + CanvasFP : String
        + WebGLFP : String
        + AudioFP : String
        + BrowserFP : uuid
*/
// Result: Element data | Null
repoMongo.insertElementFingerprints = (Fingerprints) => {
    getAsync = promisify(collectionFingerprints.insertOne).bind(collectionFingerprints)
    return getAsync({
        _id: Fingerprints._id, 
        CanvasFP: Fingerprints.CanvasFP, 
        WebGLFP: Fingerprints.WebGLFP,
        AudioFP: Fingerprints.AudioFP, 
        BrowserFP: Fingerprints.BrowserFP
    }).then((res) => {
        return res
    }).catch((err) => {
        console.log('[ERROR] Database inserts element to collection "Fingerprints" fail!')
        console.log(err)
        return null
    })
}

/*
    ---------------------------------------------    
    Update element of collection in MongoDB
    Ref: 
        + https://dba.stackexchange.com/questions/233769/how-to-update-a-field-in-a-nested-json-document-in-mongo
        + https://stackoverflow.com/questions/17362401/how-to-set-mongo-field-from-variable
    ---------------------------------------------
*/

// Update one element in collection "HTTPHeaders"
/* 
    Params:
    -------
    + _id : uuid
    + Key : String
    + Value: String

    Example 1:
    ----------
    _id: "asd-123"
    Key: "UserAgent"
    Value: "xyziasdjhias/Chrome/..."

    Example 2:
    ----------
    _id: "987123"
    Key: "UserAgentDetail.OSName"
    Value: "Win32"
*/
// Result: Element data | Null
repoMongo.updateElementHTTPHeaders = (_id, Key, Value) => {
    let setField = {}
    setField[Key] = Value

    getAsync = promisify(collectionHeaders.updateOne).bind(collectionHeaders)
    return getAsync(
        {_id: _id},
        {$set: setField}
    ).then((res) => {
        return res
    }).catch((err) => {
        console.log('[ERROR] Database updates element of collection "HTTPHeaders" fail!')
        console.log(err)
        return null
    })
}

// Update one element in collection "Hardware"
/* 
    Params:
    -------
    + _id : uuid
    + Key : String
    + Value: String

    Example 1:
    ----------
    _id: "asd-123"
    Key: "UserAgent"
    Value: "xyziasdjhias/Chrome/..."

    Example 2:
    ----------
    _id: "987123"
    Key: "UserAgentDetail.OSName"
    Value: "Win32"
*/
// Result: Element data | Null
repoMongo.updateElementHardware = (_id, Key, Value) => {
    let setField = {}
    setField[Key] = Value

    getAsync = promisify(collectionHardware.updateOne).bind(collectionHardware)
    return getAsync(
        {_id: _id},
        {$set: setField}
    ).then((res) => {
        return res
    }).catch((err) => {
        console.log('[ERROR] Database updates element of collection "Hardware" fail!')
        console.log(err)
        return null
    })
}

// Update one element in collection "Personal"
/* 
    Params:
    -------
    + _id : uuid
    + Key : String
    + Value: String

    Example 1:
    ----------
    _id: "asd-123"
    Key: "UserAgent"
    Value: "xyziasdjhias/Chrome/..."

    Example 2:
    ----------
    _id: "987123"
    Key: "UserAgentDetail.OSName"
    Value: "Win32"
*/
// Result: Element data | Null
repoMongo.updateElementPersonal = (_id, Key, Value) => {
    let setField = {}
    setField[Key] = Value

    getAsync = promisify(collectionPersonal.updateOne).bind(collectionPersonal)
    return getAsync(
        {_id: _id},
        {$set: setField}
    ).then((res) => {
        return res
    }).catch((err) => {
        console.log('[ERROR] Database updates element of collection "Personal" fail!')
        console.log(err)
        return null
    })
}

// Update one element in collection "Environment"
/* 
    Params:
    -------
    + _id : uuid
    + Key : String
    + Value: String

    Example 1:
    ----------
    _id: "asd-123"
    Key: "UserAgent"
    Value: "xyziasdjhias/Chrome/..."

    Example 2:
    ----------
    _id: "987123"
    Key: "UserAgentDetail.OSName"
    Value: "Win32"
*/
// Result: Element data | Null
repoMongo.updateElementEnvironment = (_id, Key, Value) => {
    let setField = {}
    setField[Key] = Value

    getAsync = promisify(collectionEnvironment.updateOne).bind(collectionEnvironment)
    return getAsync(
        {_id: _id},
        {$set: setField}
    ).then((res) => {
        return res
    }).catch((err) => {
        console.log('[ERROR] Database updates element of collection "Environment" fail!')
        console.log(err)
        return null
    })
}

// Update one element in collection "Fingerprints"
/* 
    Params:
    -------
    + _id : uuid
    + Key : String
    + Value: String

    Example 1:
    ----------
    _id: "asd-123"
    Key: "UserAgent"
    Value: "xyziasdjhias/Chrome/..."

    Example 2:
    ----------
    _id: "987123"
    Key: "UserAgentDetail.OSName"
    Value: "Win32"
*/
// Result: Element data | Null
repoMongo.updateElementFingerprints = (_id, Key, Value) => {
    let setField = {}
    setField[Key] = Value

    getAsync = promisify(collectionFingerprints.updateOne).bind(collectionFingerprints)
    return getAsync(
        {_id: _id},
        {$set: setField}
    ).then((res) => {
        return res
    }).catch((err) => {
        console.log('[ERROR] Database updates element of collection "Fingerprints" fail!')
        console.log(err)
        return null
    })
}

module.exports = repoMongo;