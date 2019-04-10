package me.urielsalis.mojiraFeedIRC.application

import me.urielsalis.mojiraFeedIRC.domain.Feed
import me.urielsalis.mojiraFeedIRC.domain.FeedListener
import me.urielsalis.mojiraFeedIRC.domain.UserStatusManager

class MojiraFeedListener(private val userStatusManager: UserStatusManager): FeedListener {

    override fun listen(feed: Feed) {
        val userList = userStatusManager.getLoggedInUsers()
        userList
                .filter { !it.nick.contains(feed.author, true) }
                .filter { !userStatusManager.isMessageIgnored(it, feed.title)}
                .forEach { userStatusManager.sendMessage(it, feed.author, "#feed", "\u000307${feed.link} \u000F- ${feed.title}") }
    }
}