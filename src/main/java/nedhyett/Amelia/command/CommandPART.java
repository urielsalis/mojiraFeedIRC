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
package nedhyett.Amelia.command;

import java.util.ArrayList;
import java.util.List;
import nedhyett.Amelia.ICommand;
import nedhyett.Amelia.core.users.User;
import nedhyett.Amelia.enums.Replies;
import nedhyett.Amelia.managers.ChannelManager;

/**
 * Processes a request from a user to leave one or more channels.
 *
 * @author Ned
 */
public class CommandPART implements ICommand {

    /**
     * Removes all null and empty lines from the array.
     *
     * @param in
     *
     * @return
     */
    private static List<String> stripNull(String[] in) {
	List<String> ret = new ArrayList<>();
	for (String s : in) {
	    if (s != null && !s.isEmpty()) {
		ret.add(s);
	    }
	}
	return ret;
    }

    @Override
    public void exec(User user, String[] args, String rawmsg) {
	if (args.length == 0) {
	    user.sendRawS(Replies.ERR_NEEDMOREPARAMS.format(user.nick, "PART", "<channel>,*<channel> [reason]"));
	    return;
	}
	ArrayList<String> chans = new ArrayList<>();
	if (args[0].contains(",")) {
	    chans.addAll(stripNull(args[0].split(",")));
	} else {
	    chans.add(args[0]);
	}
	String partMessage = user.nick;
	if (rawmsg.contains(":")) {
	    partMessage = rawmsg.substring(rawmsg.indexOf(':') + 1);
	}
	for (String chan : chans) {
	    if (!ChannelManager.channelExists(chan)) {
		user.sendRawS(Replies.ERR_NOSUCHCHANNEL.format(user.nick, chan));
		continue;
	    }
	    if (!ChannelManager.getChannel(chan).isInChannel(user)) {
		user.sendRawS(Replies.ERR_NOTONCHANNEL.format(user.nick, chan));
		continue;
	    }
	    ChannelManager.getChannel(chan).leave(user, partMessage);
	}
    }

}
