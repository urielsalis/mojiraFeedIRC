package me.urielsalis.mojiraFeedIRC;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import nedhyett.Amelia.Amelia;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * mojiraFeedIRC
 * <p>
 * Created by urielsalis on 08/08/17.
 */
public class Main {
    public static Main instance;
    private ArrayList<Feed> feeds = new ArrayList<Feed>();
    private boolean running = true;
    private String url = "https://bugs.mojang.com/activity";
    private long secondsToSleep = 20;
    private IRCServer irc;
    private static String[] args;

    public static void main(String[] args) { instance = new Main(); Main.args = args;}

    public Main() {
        try {
            load();
            initIRC();

            while(running) {
                    readFeed(url);
                    Thread.sleep(secondsToSleep);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void initIRC() throws IOException {
        irc = new IRCServer(args);
    }

    private void readFeed(String url) throws IOException, FeedException {
        URL feedSource = new URL(url);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedSource));
        List<SyndEntryImpl> entries = feed.getEntries();
        for(SyndEntryImpl entry: entries) {
            Feed feedObj = new Feed(entry);
            if(!feeds.contains(feedObj)) {
                feeds.add(feedObj);
                newFeed(feedObj);
            }
        }
    }

    private void newFeed(Feed feedObj) {
        irc.newFeed(feedObj);
    }

    public static void save() {
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream("save.obj");
            out = new ObjectOutputStream(fos);
            out.writeObject(IRCServer.userList);

            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void load() {
        FileInputStream fis = null;
        ObjectInputStream in = null;
        try {
            fis = new FileInputStream("save.obj");
            in = new ObjectInputStream(fis);
            IRCServer.userList = (HashMap<String, UserFeed>) in.readObject();
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
