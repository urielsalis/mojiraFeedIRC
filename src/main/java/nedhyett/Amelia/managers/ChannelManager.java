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
package nedhyett.Amelia.managers;

import java.util.ArrayList;
import java.util.HashMap;
import nedhyett.Amelia.Channel;
import nedhyett.Amelia.core.users.User;
import nedhyett.crimson.logging.CrimsonLog;

/**
 * Manages Channels
 *
 * @author Ned
 */
public class ChannelManager {

    /*
     * Channel Types:
     * # = Standard Channel
     * ! = 'Safe Channel'
     * & = 'No flags channel'
     * 
     */
    /**
     * List of active channels.
     */
    private static final HashMap<String, Channel> channels = new HashMap<>();

    /**
     * List of channel prefixes. This should not be changed unless a new RFC
     * indicates that a new prefix is added.
     */
    public static final String[] allowedChanPrefixes = new String[]{"&", "#", "+", "!"};

    /**
     * Create a new channel with default settings.
     *
     * @param name The channel identifier (including prefix)
     *
     * @return The new channel instance
     */
    public static Channel createChannel(String name) {
	if (!validateChannelName(name)) {
	    return null;
	}
	channels.put(name, new Channel(name));
	if (name.startsWith("+")) {
	    channels.get(name).noModes = true;
	    channels.get(name).topicProtection = true;
	}
	return channels.get(name);
    }

    /**
     * Close the specified channel.
     *
     * @param name
     */
    public static void closeChannel(String name) {
	if (channelExists(name)) {
	    channels.remove(name);
	} else {
	    CrimsonLog.warning("Tried to close non-existant channel " + name);
	}
    }

    /**
     * Get a list of all active channels.
     *
     * @return
     */
    public static ArrayList<Channel> getAllChannels() {
	return new ArrayList<>(channels.values());
    }

    /**
     * Get a channel instance
     *
     * @param name
     *
     * @return
     */
    public static Channel getChannel(String name) {
	return channels.get(name);
    }

    /**
     * Check if the specified channel is open.
     *
     * @param name
     *
     * @return
     */
    public static boolean channelExists(String name) {
	return channels.containsKey(name);
    }

    /**
     * Search through all open channels and check the user list for the provided
     * user (expensive operation)
     *
     * @param u
     *
     * @return
     */
    public static ArrayList<Channel> getAllChannelsWithUser(User u) {
	ArrayList<Channel> ret = new ArrayList<>();
	for (Channel c : channels.values()) {
	    if (c.isInChannel(u)) {
		ret.add(c);
	    }
	}
	return ret;
    }

    /**
     * Checks if the provided channel name is OK to use.
     *
     * @param name
     *
     * @return
     */
    public static boolean validateChannelName(String name) {
	boolean valid = false;
	for (String prefix : allowedChanPrefixes) {
	    if (name.startsWith(prefix)) {
		valid = true;
	    }
	}
	if (name.contains(" ")) {
	    valid = false;
	}
	if (name.contains("^G")) {
	    valid = false;
	}
	if (name.contains(",")) {
	    valid = false;
	}
	return valid;
    }

    /**
     * Count the open channels.
     *
     * @return
     */
    public static int countChannels() {
	return channels.size();
    }

}
