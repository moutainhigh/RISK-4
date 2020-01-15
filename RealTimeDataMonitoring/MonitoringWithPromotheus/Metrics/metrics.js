/*
    ===========
    DECLARATION
    ===========
*/

const client = require('prom-client');
const register = new client.Registry();
let metricsFunc = {}

const gauge_campaignAmount = new client.Gauge({
    name: "gauge_campaignAmount",
    help: "This is gauge of detecting discount amount burning down velocity of each campaign",
    labelNames: ['campaignID']
});

const gauge_numTransOfCampaign = new client.Gauge({
    name: "gauge_numTransOfCampaign",
    help: "This is gauge of detecting number of transactions used promotion campaign",
    labelNames: ['campaignID']
})

/*
    =========
    FUNCTIONS
    =========
*/

metricsFunc.getRegisterContentType = () => {
    return register.contentType
}

metricsFunc.getMetrics = () => {
    return register.metrics()
}

metricsFunc.registerMetrics = () => {
    register.registerMetric(gauge_campaignAmount);
    register.registerMetric(gauge_numTransOfCampaign);
}

function updateGaugeCampaignAmount(element) {
    campaignID = element['CampaignID']
    disAmount = Number(element['DiscountAmount'])

    gauge_campaignAmount.labels(campaignID).inc(disAmount)
}

function updateGaugeNumTransOfCampaign(element) {
    campaignID = element['CampaignID']
    
    if (campaignID.length != 0) {
        gauge_numTransOfCampaign.labels(campaignID).inc()
    }
}

metricsFunc.sendData = (jsonArray) => {
    let sizeArr = jsonArray.length
    let pos = 0
    let intervalID = setInterval(() => {
        if (pos < sizeArr) {
            // Detect campaignAmount
            updateGaugeCampaignAmount(jsonArray[pos])
            // Detect numTransOfCampaign
            updateGaugeNumTransOfCampaign(jsonArray[pos])

            pos += 1
        }
        else {
            clearInterval(intervalID)
        }
    }, 1000);
}

module.exports = metricsFunc;