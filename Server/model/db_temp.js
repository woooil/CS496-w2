var mysql = require('mysql');
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