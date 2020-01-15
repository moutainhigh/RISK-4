var express = require('express')
var app = express();
app.set('port', (process.env.PORT || 8080));
let dataFunc = require('./dataProcessing');
let metricsFunc = require('./metrics')

app.get('/', (req, res) => {
    res.send("ERROR - NOTHING TO SHOW")
});

app.get('/metrics',  (req, res) => {
    res.set('Content-Type', metricsFunc.getRegisterContentType());
    res.end(metricsFunc.getMetrics());
});

// App listens...
app.listen(app.get('port'), async function () {
    console.log('Server is listening at port ' + app.get('port'));
    metricsFunc.registerMetrics()
    dataArray = await dataFunc.dataProcessing()
    metricsFunc.sendData(dataArray)
});