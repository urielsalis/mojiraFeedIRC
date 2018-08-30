package me.urielsalis.mojiraFeedIRC

import nedhyett.Amelia.Amelia
import nedhyett.Amelia.core.config.ConfigReader
import nedhyett.Amelia.core.connection.ConnectionThread
import nedhyett.Amelia.managers.UserManager
import nedhyett.Amelia.ping.PingPongThread
import nedhyett.crimson.logging.CrimsonLog
import java.io.*
import java.util.*

object IRCServer : Amelia() {
    var args: Array<String> = emptyArray()
    var userList: MutableMap<String, UserFeed> = mutableMapOf()

    fun start() {
        CrimsonLog.initialise("IRC")
        cmdargs = args

        CrimsonLog.info("Loading configuration...")
        val cr = ConfigReader("config.txt")
        cr.read()
        Amelia.config = cr.parse()
        CrimsonLog.info("Configuration loaded!")

        CrimsonLog.info("Loading MOTD...")
        val motdf = File("motd.conf")
        if (motdf.exists()) {
            try {
                val br = BufferedReader(InputStreamReader(FileInputStream(motdf)))
                var line: String? = br.readLine()
                while (line != null) {
                    Amelia.MOTD.add(line)
                    line = br.readLine()
                }
                CrimsonLog.info("MOTD loaded!")
            } catch (e: IOException) {
                Amelia.MOTD.clear()
                CrimsonLog.warning("IOException while loading MOTD!")
            }

        } else {
            CrimsonLog.warning("motd.conf not found!")
        }

        CrimsonLog.info("Starting connection thread...")
        val ct = ConnectionThread()
        ct.start()
        CrimsonLog.info("Connection thread started!")

        CrimsonLog.info("Starting PingPong thread...")
        val ppt = PingPongThread()
        ppt.start()
        CrimsonLog.info("PingPong thread started!")

        UserManager.addFakeUser("FeedServer", FeedServer("FeedServer", "FeedServer", "Urielsalis", "FeedServer@Urielsalis"))
        Amelia.startupTime = Date()
    }

    fun newFeed(feedObj: Feed) {
        userList
                .filterValues { it.user!=null }
                .filterValues { it.user!!.respondedToLastPing }
                .filterValues { !feedObj.author.contains(it.username, true) }
                .filterValues { feed -> !feed.ignoreList.any { feedObj.title.matches(it.toRegex()) } }
                .forEach { _, feed -> feed.user!!.sendRaw(feedObj.author, "PRIVMSG #feed :\u000307" + feedObj.link + "\u000f - " + feedObj.title) }
    }
}
