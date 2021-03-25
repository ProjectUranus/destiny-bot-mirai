//  & 'C:\Program Files\MongoDB\Server\4.1\bin\mongodump.exe' /gzip /archive:destiny2.gz /db:destiny2
// mongorestore --gzip --archive=destiny2.gz
const fs = require('fs');

const MongoClient = require('mongodb').MongoClient;
const url = 'mongodb://localhost:27017/';

MongoClient.connect(
    url,
    { useNewUrlParser: true, useUnifiedTopology: true },
    function (err, db) {
        if (err) throw err;
        db.db('destiny2').dropDatabase();
        const dbo = db.db('destiny2');

        let d2obj = JSON.parse(
            fs
                .readFileSync('d2-chs.json')
                .toString()
                .replace(
                    /BungieNet\.Engine\.Contract\.Destiny\.World\.Definitions\.IDestinyDisplayDefinition\.displayProperties/g,
                    ''
                )
        );

        for (const definition in d2obj) {
            for (const id in d2obj[definition]) {
                d2obj[definition][id]['_id'] = id;
                dbo.collection(definition + '_chs').insertOne(
                    d2obj[definition][id],
                    function (err, res) {
                        if (err) throw err;
                    }
                );
            }
        }

        d2obj = JSON.parse(
            fs
                .readFileSync('d2-eng.json')
                .toString()
                .replace(
                    /BungieNet\.Engine\.Contract\.Destiny\.World\.Definitions\.IDestinyDisplayDefinition\.displayProperties/g,
                    ''
                )
        );

        for (const definition in d2obj) {
            for (const id in d2obj[definition]) {
                d2obj[definition][id]['_id'] = id;
                dbo.collection(definition + '_eng').insertOne(
                    d2obj[definition][id],
                    function (err, res) {
                        if (err) throw err;
                    }
                );
            }
        }
    }
);
