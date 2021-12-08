package net.origind.destinybot.features

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import net.origind.destinybot.features.destiny.response.DestinyActivityDefinition
import net.origind.destinybot.features.destiny.response.lightgg.DisplayProperties
import net.origind.destinybot.features.destiny.response.lightgg.ItemDefinition
import org.litote.kmongo.KMongo
import org.litote.kmongo.find
import org.litote.kmongo.findOne

object Database {
    val mongoClient: MongoClient
    val db: MongoDatabase

    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    init {
        mongoClient = KMongo.createClient()
        db = mongoClient.getDatabase("destiny2")
    }

    fun getItemDefinition(itemId: String): ItemDefinition {
        return moshi.adapter(ItemDefinition::class.java).fromJson(db.getCollection("DestinyInventoryItemDefinition_chs").findOne("""{"hash": $itemId}""")?.toJson()!!)!!
    }

    fun getItemDefinitions(displayName: String): List<ItemDefinition> {
        val itemDefinitionCollection = db.getCollection("DestinyInventoryItemDefinition_chs")
        return itemDefinitionCollection.find("""{"displayProperties.name": "$displayName"}""").map { document ->
            moshi.adapter(ItemDefinition::class.java).fromJson(document.toJson())!!
        }.toList()
    }

    fun translate(name: String): DisplayProperties? {
        val itemDefinitionCollection = db.getCollection("DestinyInventoryItemDefinition_eng")
        return itemDefinitionCollection.find("""{"displayProperties.name": "$name"}""").map { document ->
            getItemDefinition(document["_id"].toString()).displayProperties
        }.firstOrNull()
    }

    fun getActivity(id: Long): DestinyActivityDefinition {
        return moshi.adapter(DestinyActivityDefinition::class.java).fromJson(db.getCollection("DestinyActivityDefinition_chs_chs").findOne("""{"hash": $id}""")?.toJson()!!)!!
    }
}
