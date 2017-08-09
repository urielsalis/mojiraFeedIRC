package me.urielsalis.mojiraFeedIRC;

import nedhyett.Amelia.Amelia;
import nedhyett.Amelia.CommandRegistry;
import nedhyett.Amelia.core.config.ConfigReader;
import nedhyett.Amelia.core.connection.ConnectionThread;
import nedhyett.Amelia.core.users.User;
import nedhyett.Amelia.managers.UserManager;
import nedhyett.Amelia.ping.PingPongThread;
import nedhyett.Amelia.services.NickServ;
import nedhyett.crimson.logging.CrimsonLog;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * mojiraFeedIRC
 * <p>
 * Created by urielsalis on 09/08/17.
 */
public class IRCServer extends Amelia {
    public static HashMap<String, UserFeed> userList = new HashMap<String, UserFeed>();

    public IRCServer(String[] args) {
        CrimsonLog.initialise("IRC");
        this.cmdargs = args;

        CrimsonLog.info("Loading configuration...");
        ConfigReader cr = new ConfigReader("config.txt");
        cr.read();
        config = cr.parse();
        CrimsonLog.info("Configuration loaded!");

        CrimsonLog.info("Loading MOTD...");
        File motdf = new File("motd.conf");
        if (motdf.exists()) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(motdf)));
                String line = br.readLine();
                while (line != null) {
                    MOTD.add(line);
                    line = br.readLine();
                }
                CrimsonLog.info("MOTD loaded!");
            } catch (IOException e) {
                MOTD.clear();
                CrimsonLog.warning("IOException while loading MOTD!");
            }
        } else {
            CrimsonLog.warning("motd.conf not found!");
        }

        CrimsonLog.info("Starting connection thread...");
        ConnectionThread ct = new ConnectionThread();
        ct.start();
        CrimsonLog.info("Connection thread started!");

        CrimsonLog.info("Starting PingPong thread...");
        PingPongThread ppt = new PingPongThread();
        ppt.start();
        CrimsonLog.info("PingPong thread started!");

        UserManager.addFakeUser("FeedServer", new FeedServer("FeedServer", "FeedServer", "Urielsalis", "FeedServer@Urielsalis"));

        Amelia.startupTime = new Date();

    }

    public void newFeed(Feed feedObj) {
        loop: for(Map.Entry<String, UserFeed> entry: userList.entrySet()) {
            UserFeed feed = entry.getValue();
            if(feed.user!=null && feed.user.respondedToLastPing) {
                if(feedObj.author.toLowerCase().contains(feed.username.toLowerCase())) continue;
                for(String str: feed.ignoreList) {
                    if(feedObj.title.contains(str)) {
                        continue loop;
                    }
                }
                feed.user.sendRaw(feedObj.author, "PRIVMSG #feed :\u000307 "+feedObj.link+"\u000f - "+feedObj.title);
            }
        }
    }
}
