package me.urielsalis.mojiraFeedIRC

import me.urielsalis.mojiraFeedIRC.application.MojiraFeedListener
import me.urielsalis.mojiraFeedIRC.infrastructure.irc.IRCServer
import me.urielsalis.mojiraFeedIRC.infrastructure.irc.IRCUserStatusManager
import me.urielsalis.mojiraFeedIRC.infrastructure.mojira.FeedReader
import java.lang.Exception
import java.util.concurrent.TimeUnit


fun main(args: Array<String>) {
    IRCServer()
    val feedReader = FeedReader("https://bugs.mojang.com/activity", MojiraFeedListener(IRCUserStatusManager))
    while (true) {
        try {
            feedReader.readFeed()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        TimeUnit.SECONDS.sleep(20)
    }
}