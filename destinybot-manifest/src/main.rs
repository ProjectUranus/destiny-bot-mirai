use std::path::Path;

use mongodb::{Client, options::ClientOptions, Database};
use bson::{Bson, Document};

#[tokio::main]
async fn main() -> mongodb::error::Result<()> {
    let mut client_options = ClientOptions::parse("mongodb://localhost:27017").await?;
    client_options.app_name = Some("Destiny 2 Bot".to_string());
    let client = Client::with_options(client_options)?;
    let db = client.database("destiny2");
    db.drop(None).await?;
    read_and_add("d2-chs.json", "chs", &db).await.expect("IO error");
    read_and_add("d2-eng.json", "eng", &db).await.expect("IO error");
    Ok(())
}

async fn read_and_add<P: AsRef<Path>>(path: P, language: &'static str, database: &Database) -> std::io::Result<()> {
    let file = tokio::fs::read_to_string(path).await?;
    let file_replaced = file.replace(r#"BungieNet.Engine.Contract.Destiny.World.Definitions.IDestinyDisplayDefinition.displayProperties"#, "bungieDisplayProperties");

    let bson = serde_json::from_str::<Document>(&file_replaced).expect("Cannot parse JSON to BSON");
    let tasks = bson.into_iter().map(|(definition, bson)| {
        let database = database.clone();
        tokio::spawn(async move {
            let collection = format!("{}_{}", &definition, language);
            let collection = database.collection::<Document>(&collection);
    
            if let Bson::Document(doc) = bson {
                let docs = doc.into_iter().map(|(id, entry)| {
                    if let Bson::Document(d) = entry {
                        let mut d = d;
                        d.insert("_id", id.clone());
                        Some(d)
                    } else {
                        None
                    }
                }).map(|x| x.unwrap()).collect::<Vec<Document>>();
                if !docs.is_empty() {
                    collection.insert_many(docs, None).await.unwrap();
                }
            }
        })
    });
    futures::future::join_all(tasks).await;
    Ok(())
}