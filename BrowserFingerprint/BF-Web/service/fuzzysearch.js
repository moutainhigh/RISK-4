let fuzz = require('fuzzball');
let fuzzySearch = {}
const given_ratio_1 = 98

// Get ratio of fuzzy search between two version number (Self-Defined Algorithm)
// Params: Version number oldVersion, newVersion
// Result: Ratio
fuzzySearch.findRatioVersionNumber = (oldVersion, newVersion) => {
	let oldVersions = oldVersion.split(".");
	let newVersions = newVersion.split(".");
    let length = Math.max(oldVersions.length,newVersions.length);
    let subLength = Math.abs(oldVersions.length - newVersions.length);
    
    if (oldVersions.length < newVersions.length) {
        for (let i = 0; i < subLength; i++) {
            oldVersions.push('0');
        }
    }
    else {
        for (let i = 0; i < subLength; i++) {
            newVersions.push('0');
        }
    }

	let ratio = 100;
	let metric = [100, 30, 20, 10, 5, 2, 1];
	
    for (let i = 0; i < length; i++) {
        if (oldVersions[i] !== newVersions[i]) {
            if (i >= metric.length) return 99;
            ratio = 100 - metric[i];
			return ratio;
		}
	}

    return ratio;
    
    //console.log(findRatioVersionNumber("53.0.2357.1","53.0.2357.2")); => ratio == 90%
    //console.log(findRatioVersionNumber("53.0.2350.1","53.0.2357.1")); => ratio == 80%
}

// Get ratio of fuzzy search between two string
// Params: String a, b
// Result: Ratio
fuzzySearch.findRatio = (a, b) => {
    return fuzz.ratio(a,b)
}

// Compare information and satisfy given ratio
// Params: deviceInfo JSON List, deviceInfoArray JSON Array
// Result: nearestDeviceInfo JSON List | Null
fuzzySearch.compareInformation = (deviceInfo, deviceInfoArray) => {
    // Compare WebGLRenderer
    let SameRendererWebGL = []
    deviceInfoArray.forEach(element => {
        ratio = fuzzySearch.findRatio(deviceInfo.Hardware.WebGLRenderer, element.Hardware.WebGLRenderer)
        if (ratio >= given_ratio_1) SameRendererWebGL.push(element)
    });
    if (SameRendererWebGL.length == 0) return null

    // Compare DeviceModel
    let SameDeviceModel = []
    SameRendererWebGL.forEach(element => {
        
        ratio = fuzzySearch.findRatio(deviceInfo.HTTPHeaders.UserAgentDetail.DeviceModel, element.HTTPHeaders.UserAgentDetail.DeviceModel)
        if (ratio >= given_ratio_1) SameDeviceModel.push(element)
    });
    if (SameDeviceModel.length == 0) return null

    // Compare DeviceRTC
    let SameDeviceRTC = []
    SameDeviceModel.forEach(element => {
        ratio = fuzzySearch.findRatio(deviceInfo.Hardware.DeviceRTC, element.Hardware.DeviceRTC)
        if (ratio >= given_ratio_1) SameDeviceRTC.push(element)
    });
    if (SameDeviceRTC.length == 0) return null

    // Compare CPUVirtualCores
    let SameCPUVirtualCores = []
    SameDeviceRTC.forEach(element => {
        ratio = fuzzySearch.findRatio(deviceInfo.Hardware.CPUVirtualCores, element.Hardware.CPUVirtualCores)
        if (ratio >= given_ratio_1) SameCPUVirtualCores.push(element)
    });
    if (SameCPUVirtualCores.length == 0) return null

    // Compare CanvasFP
    let SameCanvasFP = []
    SameCPUVirtualCores.forEach(element => {
        ratio = fuzzySearch.findRatio(deviceInfo.Fingerprints.CanvasFP, element.Fingerprints.CanvasFP)
        if (ratio >= given_ratio_1) SameCanvasFP.push(element)
    });
    if (SameCanvasFP.length == 0) return null

    // Compare WebGLFP
    let SameWebGLFP = []
    SameCanvasFP.forEach(element => {
        ratio = fuzzySearch.findRatio(deviceInfo.Fingerprints.WebGLFP, element.Fingerprints.WebGLFP)
        if (ratio >= given_ratio_1) SameWebGLFP.push(element)
    });
    if (SameWebGLFP.length == 0) return null

    // Compare AudioFP
    let SameAudioFP = []
    SameWebGLFP.forEach(element => {
        ratio = fuzzySearch.findRatio(deviceInfo.Fingerprints.AudioFP, element.Fingerprints.AudioFP)
        if (ratio >= given_ratio_1) SameAudioFP.push(element)
    });
    if (SameAudioFP.length == 0) return null
    
    // Return nearest information
    // If array contains more than 1 element => Choose the first element in the array
    return SameAudioFP[0]
}

// Find neareast information
// Param: deviceInfo JSON List, deviceInfoArray JSON Array
// Result: Nearest Device Info | Null
fuzzySearch.findNearestInformation = (deviceInfo, deviceInfoArray) => {
    let result = null

    if (deviceInfoArray.length != 0) {
        result = fuzzySearch.compareInformation(deviceInfo, deviceInfoArray)
    }
    
    return result
}

module.exports = fuzzySearch;