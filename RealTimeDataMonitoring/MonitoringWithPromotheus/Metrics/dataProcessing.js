const csvFilePath = './data/translog_example.csv'
const csv = require('csvtojson')
let dataFunc = {}

function retrieveElement(element) {
    let newElement = JSON.parse('{"TransID" : "' + element['transID'] + '", "Amount": "' + element['amount'] + '", "UserID": "' + element['userID'] + '", "BankCode": "' + element['bankCode'] + '", "First6CharCardNumber": "' + element['first6CharCardNumber'] + '", "OSVer": "' + element['osVer'] + '", "DeviceModel": "' + element['deviceModel'] + '", "MNO": "' + element['mno'] + '", "DeviceID": "' + element['deviceID'] + '", "AppVer": "' + element['appVer'] + '", "UserIP": "' + element['userIP'] + '", "Latitude": "' + element['latitude'] + '", "Longitude": "' + element['longitude'] + '", "ReqDate" : "' + element['reqDate'] + '", "CampaignID" : "' + element['campaignID'] + '", "DiscountAmount" : "' + element['discountAmount'] +'"}')

    return newElement
}

async function processData() {
    const jsonArray = await csv().fromFile(csvFilePath)

    let newJsonArray = []
    //jsonArray.length
    for (let i = 0; i < 5; ++i) {
        // Only convert elements which have SUCCESSFUL transStatus
        if (jsonArray[i]['transStatus'] == 'SUCCESSFUL') {
            newJsonArray.push(retrieveElement(jsonArray[i]))
        }
    }
    
    return newJsonArray
}

dataFunc.dataProcessing = async () => {
    result = await processData()
    return result
}

module.exports = dataFunc;