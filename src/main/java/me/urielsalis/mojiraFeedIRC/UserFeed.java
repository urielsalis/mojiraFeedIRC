package me.urielsalis.mojiraFeedIRC;

import nedhyett.Amelia.core.users.User;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * mojiraFeedIRC
 * <p>
 * Created by urielsalis on 09/08/17.
 */
public class UserFeed implements Serializable {
    String username;
    String password;
    transient User user;
    ArrayList<String> ignoreList = new ArrayList<>();

    public UserFeed(String username, String password, User user) {
        this.username = username;
        this.password = password;
        this.user = user;
    }

    public void addToIgnore(String str) {
        ignoreList.add(str);
    }
}
