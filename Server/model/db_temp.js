var mysql = require('mysql');
const { VARCHAR } = require('mysql/lib/protocol/constants/types');
var db = mysql.createConnection({
    host: "[]",
    user: '[]',
    password: '[]',
    database: '[]',
    dialectOptions: {
        options: {
            requestTimeout: 3000
        }
    }
});
db.connect();
module.exports = db;

