/*
    =======================    
    CLIENT
    =======================

    Installation:
    + Module/Lib "Fingerprintjs2"
*/

// Get browser info from client
function getBrowserInfo(components) {
    const gl = document.createElement("canvas").getContext("webgl");
    const ext = gl.getExtension("WEBGL_debug_renderer_info");

    // Get CPU virtual cores
    CPUVirtualCores = window.navigator.hardwareConcurrency

    // Get Renderer WebGL
    RendererWebGL = gl.getParameter(ext.UNMASKED_RENDERER_WEBGL)

    // Get Canvas FP, WebGL FP and Font List
    var newComp = {};
    for (let i = 0; i < components.length; ++i) {
        newComp[components[i].key] = components[i].value;
    }

    CanvasFP = Fingerprint2.x64hash128(newComp['canvas'].join(''), 31);
    WebGLFP = Fingerprint2.x64hash128(newComp['webgl'].join(''), 31);
    FontList = newComp['fonts'];

    // 1. Get DeviceRTC info and Audio FP.
    // 2. Collect & combine all info.
    // 3. Send all info from client to server.
    run_pxi_fp("AudioFP-Info", function () {
        DetectRTC.load(function () {
            let DeviceRTCInfo
            if (DetectRTC.MediaDevices.length == 0) 
            {
                DeviceRTCInfo = "Can not detect"
            } 
            else 
            {
                DeviceRTCInfo = `${DetectRTC.audioInputDevices.length} microphone - ` + `${DetectRTC.audioOutputDevices.length} speaker - ` + `${DetectRTC.videoInputDevices.length} camera`;
            }

            var jsonHardware = {};
            jsonHardware['CPU virtual cores'] = CPUVirtualCores
            jsonHardware['Renderer WebGL'] = RendererWebGL
            jsonHardware['DeviceRTC info'] = DeviceRTCInfo

            var jsonPersonal = {};
            jsonPersonal['Font list'] = FontList
            jsonPersonal['Time zone'] = new Date().toTimeString().slice(9);
            var timezone =Intl.DateTimeFormat().resolvedOptions().timeZone;
            jsonPersonal['City'] = timezone.split("/")[1];
            jsonPersonal['Continent'] = timezone.split("/")[0];
            jsonPersonal['Screen resolution'] = screen.width+"x"+screen.height+"x"+screen.colorDepth;

            jsonPersonal['Sensor orientation'] = document.getElementById('Sensor-orientation').innerText;

            var jsonFingerprint = {};
            jsonFingerprint['Canvas FP'] = CanvasFP
            jsonFingerprint['WebGL FP'] = WebGLFP
            jsonFingerprint['Audio FP'] = document.getElementById(`AudioFP-Info`).innerText;

            sendInfo(jsonHardware, jsonPersonal, jsonFingerprint)
        });
    });
}

// Send info from client to server
function sendInfo(jsonHardware, jsonPersonal, jsonFingerprint) {
    axios.post('/user/info', {
        jsonHardware: jsonHardware,
        jsonPersonal: jsonPersonal,
        jsonFingerprint: jsonFingerprint
    }).then((res) => {
        console.log(res)
        document.getElementById("Notification").innerText="[SUCCESS] Send data to server successfully"
    }).catch((err) => {
        document.getElementById("Notification").innerText="[ERROR] Send data to server fail"
        console.log('[ERROR] Send data to server fail')
        console.log(err);
    })
}

$(document).ready(function () {
    var options = {fonts: {extendedJsFonts: true}}
    getInfoSensor('Sensor-orientation')

    if (window.requestIdleCallback) {
        requestIdleCallback(function () {
            Fingerprint2.get(options, function (res) {
                getBrowserInfo(res);
            })
        })
    } else {
        setTimeout(function () {
            Fingerprint2.get(options, function (res) {
                getBrowserInfo(res);
            })
        }, 2000)
    }
});