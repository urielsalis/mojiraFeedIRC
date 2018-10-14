package me.urielsalis.mojiraFeedIRC

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.sun.syndication.feed.synd.SyndEntryImpl
import com.sun.syndication.io.FeedException
import com.sun.syndication.io.SyndFeedInput
import com.sun.syndication.io.XmlReader
import org.apache.commons.collections4.queue.CircularFifoQueue
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.net.URL
import java.util.concurrent.TimeUnit


object Main {
    private const val url = "https://bugs.mojang.com/activity"
    private const val secondsToSleep: Long = 20
    private val feeds = CircularFifoQueue<Feed>(200)
    var running = true

    fun run() {
        load()
        IRCServer.start()
        while (running) {
            readFeed(url)
            TimeUnit.SECONDS.sleep(secondsToSleep)
        }
    }

    @Throws(IOException::class, FeedException::class)
    private fun readFeed(url: String) {
        val feedSource = URL(url)
        val input = SyndFeedInput()
        val feed = input.build(XmlReader(feedSource))
        val entries = feed.entries
        entries
                .map {
                    Feed(it as SyndEntryImpl)
                }
                .filterNot { feeds.contains(it) }
                .forEach {
                    feeds.add(it)
                    IRCServer.newFeed(it)
                }
    }

    fun load() {
        val file = File("userlist.json")
        if (file.exists()) {
            IRCServer.userList = Gson().fromJson(file.reader(), object : TypeToken<Map<String, UserFeed>>() {}.type)
        }
    }

    fun save() {
        FileWriter("userlist.json").use { writer ->
            val gson = GsonBuilder().create()
            gson.toJson(IRCServer.userList, writer)
        }
    }
}

fun main(args: Array<String>) {
    IRCServer.args = args
    Main.run()
}