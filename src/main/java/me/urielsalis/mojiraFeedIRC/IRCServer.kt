package me.urielsalis.mojiraFeedIRC

import nedhyett.Amelia.Amelia
import nedhyett.Amelia.core.config.ConfigReader
import nedhyett.Amelia.core.connection.ConnectionThread
import nedhyett.Amelia.managers.ChannelManager
import nedhyett.Amelia.managers.UserManager
import nedhyett.Amelia.ping.PingPongThread
import nedhyett.crimson.logging.CrimsonLog
import nedhyett.crimson.logging.CrimsonLog.info
import nedhyett.crimson.logging.CrimsonLog.warning
import org.apache.commons.collections4.CollectionUtils
import java.io.*
import java.util.*

object IRCServer : Amelia() {
    var args: Array<String> = emptyArray()
    var userList: MutableMap<String, UserFeed> = mutableMapOf()

    fun start() {
        CrimsonLog.initialise("IRC")
        cmdargs = args

        info("Loading configuration...")
        val cr = ConfigReader("config.txt")
        cr.read()
        Amelia.config = cr.parse()
        info("Configuration loaded!")

        info("Loading MOTD...")
        val motdf = File("motd.conf")
        if (motdf.exists()) {
            try {
                val br = BufferedReader(InputStreamReader(FileInputStream(motdf)))
                var line: String? = br.readLine()
                while (line != null) {
                    Amelia.MOTD.add(line)
                    line = br.readLine()
                }
                info("MOTD loaded!")
            } catch (e: IOException) {
                Amelia.MOTD.clear()
                warning("IOException while loading MOTD!")
            }

        } else {
            warning("motd.conf not found!")
        }

        info("Starting connection thread...")
        val ct = ConnectionThread()
        ct.start()
        info("Connection thread started!")

        info("Starting PingPong thread...")
        val ppt = PingPongThread()
        ppt.start()
        info("PingPong thread started!")

        UserManager.addFakeUser("FeedServer", FeedServer("FeedServer", "FeedServer", "Urielsalis", "FeedServer@Urielsalis"))

        info("Setting OP in #feed")
        ChannelManager.createChannel("#feed")
        val feedChannel = ChannelManager.getChannel("#feed")
        val feedserver = UserManager.getUser("FeedServer")
        feedChannel.join(feedserver)
        feedChannel.op("FeedServer")

        info("Configuration done!")
        Amelia.startupTime = Date()
    }

    fun newFeed(feedObj: Feed) {
        val nulls = userList
                .mapValues {  UserManager.getUser(it.key) }
                .filterValues { it == null }
        nulls.forEach { userList.remove(it.key) }
        val inactive = userList
                .mapValues {  UserManager.getUser(it.key) }
                .filterValues { !it.respondedToLastPing }
        inactive.forEach { userList.remove(it.key)}
        userList
                .mapKeys {  UserManager.getUser(it.key) }
                .filterKeys { it != null }
                .filterKeys { it.respondedToLastPing }
                .filterKeys { !feedObj.author.contains(it.username, true) }
                .filterValues { feed -> !feed.ignoreList.any { feedObj.title.matches(it.toRegex()) } }
                .forEach { user, _ -> user.sendRaw(feedObj.author, "PRIVMSG #feed :\u000307${feedObj.link} \u000f- ${feedObj.title}") }

        refreshPerms()
    }

    private fun refreshPerms() {
        val feedChannel = ChannelManager.getChannel("#feed")
        userList
                .mapKeys {  UserManager.getUser(it.key) }
                .filterKeys { it == null || !it.respondedToLastPing }.forEach { username, _ -> feedChannel.devoice(username) }
    }
}
