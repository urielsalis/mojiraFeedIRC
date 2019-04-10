package me.urielsalis.mojiraFeedIRC.infrastructure.mojira

import com.sun.syndication.feed.synd.SyndEntryImpl
import com.sun.syndication.io.SyndFeedInput
import com.sun.syndication.io.XmlReader
import me.urielsalis.mojiraFeedIRC.domain.Feed
import me.urielsalis.mojiraFeedIRC.domain.FeedListener
import org.apache.commons.collections4.queue.CircularFifoQueue
import java.net.URL

class FeedReader(private val url: String, private val feedListener: FeedListener) {
    val queue = CircularFifoQueue<Feed>(200)

    fun readFeed() {
        val feedSource = URL(url)
        val input = SyndFeedInput()
        val feed = input.build(XmlReader(feedSource))
        val entries = feed.entries
        entries
            .map {
                (it as SyndEntryImpl).toFeed()
            }
            .filterNot { queue.contains(it) }
            .forEach {
                queue.add(it)
                feedListener.listen(it)
            }
    }
}