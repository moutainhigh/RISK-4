const redis = require("redis") 
const port = "16950";
const host = "redis-16950.c99.us-east-1-4.ec2.cloud.redislabs.com";
const password = "M4QEE6JdvAOiRR8POtZpNISFoN1xIoJT"

let client = redis.createClient({port:port, host:host, password:password})

const { promisify } = require('util');
let repoRedis = {};
const timeExpires = '604800‬' // 604800 ~ 7 days

// Reference: https://github.com/NodeRedis/node_redis

// Connect to Redis
repoRedis.connectRedis = async () => {
    await client.on('connect', function() {
        console.log('Redis connected');
    });
}

// Browser fingerprint - SetEx => Shortname: BF
function keyBF(fingerprint) {
    return ("BF:" + fingerprint)
}

// Set BF to Redis
// Parameter: SRING fingerprint
// Result: True 
repoRedis.setBF = async (fingerprint) => {
    return (await client.setex(keyBF(fingerprint), parseInt(timeExpires), 1))
}

// Check BF existed in Redis or not
// Parameter: STRING fingerprint
// Result: True | False
repoRedis.isMemberBF = (fingerprint) => {
    getAsync = promisify(client.get).bind(client)
    return getAsync(keyBF(fingerprint)).then((res) => {
        return ((res==null) ? false : true)
    })
}

module.exports = repoRedis;