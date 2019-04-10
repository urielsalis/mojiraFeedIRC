package me.urielsalis.mojiraFeedIRC.infrastructure.irc

import nedhyett.Amelia.Amelia
import nedhyett.Amelia.core.config.ConfigReader
import nedhyett.Amelia.core.connection.ConnectionThread
import nedhyett.Amelia.managers.ChannelManager
import nedhyett.Amelia.managers.UserManager
import nedhyett.Amelia.ping.PingPongThread
import nedhyett.crimson.logging.CrimsonLog
import java.io.*
import java.util.*

class IRCServer: Amelia() {
    init {
        loadConfiguration()
        startThreads()
        UserManager.addFakeUser("FeedServer", FeedServer("FeedServer", "FeedServer", "Urielsalis", "FeedServer@Urielsalis", IRCUserStatusManager))
        setChannel("#feed")
        CrimsonLog.info("Configuration done!")
        Amelia.startupTime = Date()
    }

    private fun setChannel(channel: String) {
        CrimsonLog.info("Setting OP in #feed")
        val feedChannel = ChannelManager.createChannel(channel)
        val feedserver = UserManager.getUser("FeedServer")
        feedChannel.join(feedserver)
        feedChannel.op("FeedServer")
    }

    private fun startThreads() {
        CrimsonLog.info("Starting connection thread...")
        val ct = ConnectionThread()
        ct.start()
        CrimsonLog.info("Connection thread started!")

        CrimsonLog.info("Starting PingPong thread...")
        val ppt = PingPongThread()
        ppt.start()
        CrimsonLog.info("PingPong thread started!")
    }

    private fun loadConfiguration() {
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
    }
}