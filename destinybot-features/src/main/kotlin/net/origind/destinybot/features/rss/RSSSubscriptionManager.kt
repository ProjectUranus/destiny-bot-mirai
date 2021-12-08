package net.origind.destinybot.features.rss

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant

class RSSSubscriptionManager {
    var lastUpdatedTime = Instant.MIN
    var subscriptions = arrayListOf<RSSSubscription>()

    suspend fun update() {
        val jobs = ArrayList<Job>(subscriptions.size)
        for (subscription in subscriptions) {
            jobs += subscription.update()
        }
        jobs.joinAll()
    }

    suspend fun subscribe(url: String) {
        subscriptions += RSSSubscription(url)
        update()
    }
}

fun main(args: Array<String>) {
    val path = Paths.get("subscriptions.json")
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val manager = moshi.adapter(RSSSubscriptionManager::class.java).fromJson(Files.readString(path)) ?: RSSSubscriptionManager()
    runBlocking {
        manager.subscribe("https://github.com/LasmGratel.private.atom?token=ABS4INLGZZG4PGIBVLL4DM57WCP42")
    }
    Files.writeString(path, moshi.adapter(RSSSubscriptionManager::class.java).toJson(manager))
}
