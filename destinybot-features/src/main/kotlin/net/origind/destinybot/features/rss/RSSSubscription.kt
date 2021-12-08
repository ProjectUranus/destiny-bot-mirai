package net.origind.destinybot.features.rss

import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.net.URL
import java.time.Instant

class RSSSubscription(val url: String) {
    val lastUpdated: Instant = Instant.MIN
    val feed = RSSFeed()
    val entries = arrayListOf<RSSEntry>()

    suspend fun update() =
        coroutineScope {
            launch {
                val fetchedFeed: SyndFeed = SyndFeedInput().build(XmlReader(URL(url)))
                feed.title = fetchedFeed.title
                entries.clear()
                entries.addAll(fetchedFeed.entries.map { RSSEntry(it.title, it.updatedDate) })
            }
        }

}
