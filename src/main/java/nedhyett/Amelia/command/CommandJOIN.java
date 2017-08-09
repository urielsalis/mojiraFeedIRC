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
import nedhyett.Amelia.*;
import nedhyett.Amelia.core.users.User;
import nedhyett.Amelia.enums.Replies;
import nedhyett.Amelia.managers.ChannelManager;

/**
 * Handles a JOIN message from a user.
 *
 * @author Ned
 */
public class CommandJOIN implements ICommand {

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
	String[] chans;
	String[] passwrds;
	if (args.length < 2) {
	    passwrds = new String[0];
	} else {
	    if (!args[1].contains(",")) {
		passwrds = new String[]{args[1]};
	    } else {
		passwrds = args[1].split(",");
	    }
	}
	if (!args[0].contains(",")) {
	    chans = new String[]{args[0]};
	} else {
	    chans = args[0].split(",");
	}
	List<String> chanlist = stripNull(chans);
	List<String> passwrdlist = stripNull(passwrds);
	for (int i = 0; i < chanlist.size(); i++) {
	    String channame = chanlist.get(i);
	    String key = null;
	    if (i < passwrdlist.size()) {
		key = passwrdlist.get(i);
	    }
	    if (!ChannelManager.validateChannelName(channame)) {
		//user.sendRawS(Replies.ERR_BADCHANMASK + " " + user.nick + " " + arg + " :Bad channel mask!");
		//FIXME: what response code to send here?
		continue;
	    }
	    Channel c = ChannelManager.getChannel(channame);
	    if (!ChannelManager.channelExists(channame)) {
		c = ChannelManager.createChannel(channame);
	    }
	    if (c.isBanned(user)) { //Check if the user is banned
		user.sendRawS(Replies.ERR_BANNEDFROMCHAN.format(user.nick, c.name));
		continue;
	    }
	    if (c.inviteOnly && !c.isInvited(user)) { //Check if the user has been invited to the channel.
		user.sendRawS(Replies.ERR_INVITEONLYCHAN.format(user.nick, c.name));
		continue;
	    }
	    if (!c.key.isEmpty() && !c.key.equals(key)) {
		user.sendRawS(Replies.ERR_BADCHANNELKEY.format(user.nick, c.name));
		continue;
	    }
	    if (c.userLimit > -1 && c.isAtUserLimit()) { //Check if adding the user to the activeUsers list doesn't overflow +l
		user.sendRawS(Replies.ERR_CHANNELISFULL.format(user.nick, c.name, c.userLimit + ""));
		continue;
	    }
	    c.join(user);
	    String nicks1 = "";
	    for (User u : c.getActiveUsers()) {
		nicks1 += ((c.canPerformOperatorFunction(u.nick)) ? "@" : "") + ((c.canPerformVoiceFunction(u.nick)) ? "+" : "") + u.nick + " "; //Compile the NAMES list.
	    }
	    nicks1 = nicks1.trim();
	    user.sendRawS(Replies.RPL_NAMEREPLY + " " + user.nick + " = " + channame + " :" + nicks1);

	}
    }

}
