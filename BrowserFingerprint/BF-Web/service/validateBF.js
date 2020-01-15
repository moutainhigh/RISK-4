let service = require('./service')
let validateBF = {}

// Validate BF existed in Redis or not, then in Mongo or not
// Parameter: STRING fingerprint
// Result: True | False
validateBF.validate = async (fingerprint)=>{
    console.log("validate...."+fingerprint);

    let isInRedis = await service.ExistedBFInRedis(fingerprint);
    
    if (isInRedis) {
        //saved in redis
        console.log("Saved in redis")
        return true;
    }
    else {
		//not saved in redis
        let bfzp = await service.isExistedFingerprint(fingerprint);
        console.log("Not saved in redis")
        
        if (bfzp) {
            //saved in mongo
            console.log("Saved in mongo")
            await service.addBFToRedis(bfzp);
            return true;
		}
		else {
            console.log("Not saved in mongo")
            //not saved in mongo
            return false;
        }
    }
}

// From Info then find nearest device in DB with information, then return BF or create new BF
// Parameter: req, res, idElement, browserFingerprint created by uuid
// Return: void
validateBF.processInfoThenReturnBF = async (req, res, idElement, browserFingerprint)=>{
    let hh = req.body.jsonHH;
	jsonUserAgentDetail = service.jsonParseUserAgentDetail(hh['Device brand'], hh['Device model'], hh['Device type'], hh['OS name'], hh['OS version'], hh['Client name'], hh['Client version'], hh['Zalo version']);
	jsonHTTPHeader = service.jsonParseHTTPHeaders(idElement, hh['User-Agent'], jsonUserAgentDetail, hh['Accept'], hh['Accept encoding'], hh['Accept language']);

	let hi = req.body.jsonHI;
	jsonHardware = service.jsonParseHardware(idElement, hi['CPU virtual cores'], hi['Touch compatibility'], hi['Vendor WebGL'], hi['Renderer WebGL'], hi['DeviceRTC info']);

	let pf = req.body.jsonPF;
	jsonPersonal = service.jsonParsePersonal(idElement, pf['Language'], pf['Screen resolution'], pf['Color depth'], pf['Time zone'], pf['Font list']);

	let ei = req.body.jsonEI;
	jsonEnvironment = service.jsonParseEnvironment(idElement, ei['IP address'], ei['Country code'], ei['Continent code'], ei['Latitude'], ei['Longitude']);

	let fi = req.body.jsonFI;
	jsonFingerprint = service.jsonParseFingerprints(idElement, fi['Canvas'], fi['WebGL'], fi['Audio'], browserFingerprint);

	jsonDeviceInfo = service.jsonParseDeviceInfo(jsonHTTPHeader, jsonHardware, jsonPersonal, jsonEnvironment, jsonFingerprint);

	//find nearest information
	nearestInformation = await service.findNearestInformation(jsonDeviceInfo);
	if (nearestInformation == null) {
		jsonList = service.addNewDeviceInfo(jsonHTTPHeader, jsonHardware, jsonPersonal, jsonEnvironment, jsonFingerprint);
        
        //save to cache
        service.addBFToRedis(browserFingerprint)

		res.json({
			fingerprint: browserFingerprint,
			message: "This device fingerprint haven't existed on the server yet. Therefore, it has been created and stored in the server."
		});
	} else {
		//save to cache
        service.addBFToRedis(nearestInformation.Fingerprints.BrowserFP)

		res.json({
			fingerprint: nearestInformation.Fingerprints.BrowserFP,
			message: "These information have stored in server before."
		});
	}
	// --------------------------------------------------
}

module.exports = validateBF;