/*
 * Copyright (c) 2014, Ned Hyett
 *  All rights reserved.
 *
 *  By using this program/package/library you agree to be completely and unconditionally
 *  bound by the agreement displayed below. Any deviation from this agreement will not
 *  be tolerated.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this
 *  list of conditions and the following disclaimer in the documentation and/or other
 *  materials provided with the distribution.
 *  3. The redistribution is not sold, unless permission is granted from the copyright holder.
 *  4. The redistribution must contain reference to the original author, and this page.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package nedhyett.Amelia;

import java.util.ArrayList;
import java.util.Random;
import nedhyett.Amelia.core.users.User;
import nedhyett.Amelia.enums.EnumChannelModes;
import nedhyett.Amelia.managers.ChannelManager;
import nedhyett.crimson.logging.CrimsonLog;

/**
 * Represents a channel on the network.
 *
 * @author Ned
 */
public class Channel {

    /**
     * The name of the channel (including prefix).
     */
    public String name;

    /**
     * The topic assigned to this channel.
     */
    public String topic = "";

    /**
     * The time that the topic was set at.
     */
    public long topicSetAt = 0L;

    /**
     * The hostmask of the user that set the topic.
     */
    public String topicSetBy = null;

    /**
     * The channel key (not implemented)
     */
    public String key = "";

    /**
     * The list of users currently in the channel.
     */
    private final ArrayList<User> activeUsers = new ArrayList<>();

    /**
     * The list of nicknames that have operator status in this channel.
     */
    private final ArrayList<String> ops = new ArrayList<>();

    /**
     * The list of nicknames that have voice status in this channel.
     */
    private final ArrayList<String> voices = new ArrayList<>();

    /**
     * List of hostmasks that are banned from this channel. (not implemented)
     */
    private final ArrayList<String> bannedHosts = new ArrayList<>();

    /**
     * List of hostmasks (or nicks?) that cannot be banned from the channel. (not implemented)
     */
    private final ArrayList<String> hostsExemptFromBans = new ArrayList<>();

    /**
     * List of users that are invited to join this channel. Only useful if the channel is
     * invite-only. (not implemented)
     */
    private final ArrayList<String> invitedUsers = new ArrayList<>();

    /**
     * Can people only be <b>INVITE</b>'d into this channel?
     */
    public boolean inviteOnly = false;

    /**
     * Can only those who have voice status or above talk in this channel?
     */
    public boolean moderated = false;

    /**
     * Prevent people who are not part of this channel sending messages to it's
     * users?
     */
    public boolean noexternal = true;

    /**
     * Is this channel registered with the built-in services?
     */
    public boolean registered = false;

    /**
     * Only allow ops to change the topic?
     */
    public boolean topicProtection = false;

    /**
     * The messages send to the channel are masked so nobody knows who sent it.
     */
    public boolean anonymous = false;

    /**
     * The maximum number of active users. Set to -1 to allow unlimited users.
     */
    public int userLimit = -1;

    /**
     * Disables modes on this channel.
     */
    public boolean noModes = false;

    /**
     * Create a new channel.
     *
     * @param name
     */
    public Channel(String name) {
	this.name = name;
    }

    /**
     * Allow a user to join the channel.
     *
     * @param user
     */
    public void join(User user) {
	this.activeUsers.add(user);
	if (this.activeUsers.size() == 1) {
	    this.ops.add(user.nick);
	}
	this.sendRaw(user.getID(), "JOIN :" + this.name);
	if (this.canPerformOperatorFunction(user.nick)) {
	    this.sendRaw(Amelia.config.serverHost, "MODE " + this.name + " +o " + user.nick);
	} else if (canPerformVoiceFunction(user.nick)) {
	    this.sendRaw(Amelia.config.serverHost, "MODE " + this.name + " +v " + user.nick);
	}
    }

    /**
     * Allow a user to leave the channel.
     *
     * @param user
     * @param reason
     */
    public void leave(User user, String reason) {
	this.sendRaw(user.getID(), "PART " + this.name + " :" + reason);
	this.activeUsers.remove(user);
	if (this.activeUsers.isEmpty()) {
	    CrimsonLog.info("Closing channel " + this.name);
	    ChannelManager.closeChannel(this.name);
	}
    }

    /**
     * Remove the user from the activeUsers list without announcing it.
     *
     * @param user
     */
    public void leaveNoAnnounce(User user) {
	this.activeUsers.remove(user);
	if (this.activeUsers.isEmpty()) {
	    CrimsonLog.info("Closing channel " + this.name);
	    ChannelManager.closeChannel(this.name);
	}
    }

    /**
     * Send raw protocol data to every active user.
     *
     * @param origin
     * @param message
     */
    public void sendRaw(String origin, String message) {
	this.activeUsers.stream().forEach((u) -> {
	    u.sendRaw(origin, message);
	});
    }

    /**
     * Send raw protocol data from the server host to every active user.
     *
     * @param message
     */
    public void sendRawS(String message) {
	this.sendRaw(Amelia.config.serverHost, message);
    }

    /**
     * Send raw protocol data to every active user except those specified.
     *
     * @param origin
     * @param message
     * @param except
     */
    public void sendRawExcept(String origin, String message, User... except) {
	for (User u : this.activeUsers) {
	    boolean exceptu = false;
	    for (User u1 : except) {
		if (u.equals(u1)) {
		    exceptu = true;
		    break;
		}
	    }
	    if (exceptu) {
		continue;
	    }
	    u.sendRaw(origin, message);
	}
    }

    /**
     * Send a <b>PRIVMSG</b> from the provided user.
     *
     * @param message
     * @param from
     */
    public void sendMsg(String message, User from) {
	this.sendRawExcept(from.getID(), "PRIVMSG " + this.name + " :" + message, from);
    }

    /**
     * Does this user have operator powers?<br>
     * Redirects to canPerformOperatorFunction(nick)
     *
     * @param u
     *
     * @return
     */
    public boolean canPerformOperatorFunction(User u) {
	return canPerformOperatorFunction(u.nick);
    }

    /**
     * Does this nick have operator powers?
     *
     * @param nick
     *
     * @return
     */
    public boolean canPerformOperatorFunction(String nick) {
	if (this.ops.isEmpty()) {
	    return true;
	}
	return this.ops.contains(nick);
    }

    /**
     * Does this user have voice powers?<br>
     * Redirects to canPerformVoiceFunction(nick)
     *
     * @param u
     *
     * @return
     */
    public boolean canPerformVocieFunction(User u) {
	return canPerformVoiceFunction(u.nick);
    }

    /**
     * Does this nick have voice powers?
     *
     * @param nick
     *
     * @return
     */
    public boolean canPerformVoiceFunction(String nick) {
	if (this.ops.isEmpty()) {
	    return true;
	}
	return this.ops.contains(nick);
    }

    /**
     * Get a list of flags set on this channel (used when new users are
     * connecting)
     *
     * @return
     */
    public String getFlags() {
	String str = "";
	if (this.inviteOnly) {
	    str += EnumChannelModes.inviteOnly.text;
	}
	if (this.moderated) {
	    str += EnumChannelModes.chanModerated.text;
	}
	if (this.noexternal) {
	    str += EnumChannelModes.chanNoExternal.text;
	}
	if (this.topicProtection) {
	    str += EnumChannelModes.chanTopicProtected.text;
	}
	return str;
    }

    /**
     * Checks if this user is banned. (not implemented)
     *
     * @param u
     *
     * @return
     */
    public boolean isBanned(User u) {
	return false;
    }

    /**
     * Checks if this user has been invited to the channel. (not implemented)
     *
     * @param u
     *
     * @return
     */
    public boolean isInvited(User u) {
	return false;
    }

    /**
     * Checks if this user has the power to speak in the channel.
     *
     * @param u
     *
     * @return
     */
    public boolean canSpeak(User u) {
	if (isBanned(u)) {
	    return false;
	}
	if (!moderated) {
	    return true;
	}
	return this.canPerformVoiceFunction(u.nick) || this.canPerformOperatorFunction(u.nick);
    }

    /**
     * Is this user an operator? (doesn't check if channel is empty)
     *
     * @param u
     *
     * @return
     */
    public boolean isOp(User u) {
	return this.ops.contains(u.nick);
    }

    /**
     * Is this user voiced?
     * (could be duplicate of canPerformVoiceFunction?)
     *
     * @param u
     *
     * @return
     */
    public boolean isVoice(User u) {
	return this.voices.contains(u.nick);
    }

    /**
     * Give this user operator powers.
     *
     * @param u
     */
    public void op(User u) {
	op(u.nick);
    }

    /**
     * Give this nick operator powers.
     *
     * @param nick
     */
    public void op(String nick) {
	this.ops.add(nick);
    }

    /**
     * Take operator powers from this user.
     *
     * @param u
     */
    public void deop(User u) {
	deop(u.nick);
    }

    /**
     * Take operator powers from this nick.
     *
     * @param nick
     */
    public void deop(String nick) {
	this.ops.remove(nick);
    }

    /**
     * Give voice to this user.
     *
     * @param u
     */
    public void voice(User u) {
	voice(u.nick);
    }

    /**
     * Give voice to this nick.
     *
     * @param nick
     */
    public void voice(String nick) {
	this.voices.add(nick);
    }

    /**
     * Take voice from this user.
     *
     * @param u
     */
    public void devoice(User u) {
	devoice(u.nick);
    }

    /**
     * Take voice from this nick.
     *
     * @param nick
     */
    public void devoice(String nick) {
	this.voices.remove(nick);
    }

    /**
     * Is the provided user in this channel?
     *
     * @param u
     *
     * @return
     */
    public boolean isInChannel(User u) {
	return this.activeUsers.contains(u);
    }

    /**
     * Is the provided nick in this channel?
     *
     * @param nick
     *
     * @return
     */
    public boolean isInChannel(String nick) {
	if (this.activeUsers.stream().anyMatch((u) -> (u.nick.equals(nick)))) {
	    return true;
	}
	return false;
    }

    /**
     * Invite a user to this channel.
     *
     * @param u
     */
    public void invite(User u) {
	invite(u.nick);
    }

    /**
     * Invite a nick to this channel.
     *
     * @param nick
     */
    public void invite(String nick) {
	this.invitedUsers.add(nick);
    }

    /**
     * Uninvite a user from this channel.
     *
     * @param u
     */
    public void uninvite(User u) {
	uninvite(u.nick);
    }

    /**
     * Uninvite a nick from this channel.
     *
     * @param nick
     */
    public void uninvite(String nick) {
	this.invitedUsers.remove(nick);
    }

    /**
     * Get the active users list.
     *
     * WARNING: DO NOT CHANGE THIS LIST DIRECTLY!
     *
     * @return
     */
    public ArrayList<User> getActiveUsers() {
	return this.activeUsers;
    }

    /**
     * Get the amount of users on this channel.
     *
     * @return
     */
    public int countActiveUsers() {
	return this.activeUsers.size();
    }

    /**
     * Checks if the user limit has been hit.
     *
     * @return
     */
    public boolean isAtUserLimit() {
	if (this.userLimit <= 0) {
	    return false;
	}
	return this.userLimit <= this.countActiveUsers();
    }

    /**
     * Pick a list of random users from this channel.<br><br>
     *
     * Notice: do not expect num amount of users back from this function. If
     * there are not enough users in this channel to meet this number, it will
     * return every user in the channel. If your query is too small (i.e. there
     * are not enough users who are not ops or voices in the channel), then
     * the function will try to match your number by ignoring your search query.
     *
     * @param num        The number of users to aim for
     * @param canBeVoice If this list can contain voices
     * @param canBeOp    If this list can contain operators.
     * @param seed       The seed for the RNG.
     *
     * @return The results of your query.
     */
    public ArrayList<User> pickRandomUsers(int num, boolean canBeVoice, boolean canBeOp, long seed) {
	if (num >= this.countActiveUsers()) {
	    return getActiveUsers();
	}
	int possibleChoices = 0;
	possibleChoices = getActiveUsers().stream().filter((u) -> !(!canBeVoice && this.isVoice(u))).filter((u) -> !(!canBeOp && this.isOp(u))).map((_item) -> 1).reduce(possibleChoices, (a, b) -> Integer.sum(a, b));
	ArrayList<User> picks = new ArrayList<>(this.getActiveUsers());
	ArrayList<User> returnedUsers = new ArrayList<>();
	Random r = new Random();
	if (seed != 0) {
	    r.setSeed(seed);
	} else {
	    r.setSeed(r.nextLong()); //I'm feeling random right now, so why not?
	}
	if (possibleChoices < num) {
	    int numGot = 0;
	    while (numGot < num) {
		returnedUsers.add(picks.remove(r.nextInt(picks.size() - 1)));
		numGot++;
	    }
	} else {
	    int numGot = 0;
	    while (numGot < num) {
		User u = picks.remove(r.nextInt(picks.size() - 1));
		if (!canBeVoice && this.isVoice(u)) {
		    continue;
		}
		if (!canBeOp && this.isOp(u)) {
		    continue;
		}
		returnedUsers.add(u);
		numGot++;
	    }
	}
	return returnedUsers;
    }

}
