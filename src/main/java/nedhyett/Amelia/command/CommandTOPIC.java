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

import nedhyett.Amelia.*;
import nedhyett.Amelia.core.users.User;
import nedhyett.Amelia.enums.Replies;
import nedhyett.Amelia.managers.ChannelManager;

/**
 * Handles the TOPIC command.
 *
 * @author Ned
 */
public class CommandTOPIC implements ICommand {

    @Override
    public void exec(User user, String[] args, String rawmsg) {
	Channel c = ChannelManager.getChannel(args[0].replace(":", ""));
	if (args.length == 1) {
	    if (c.topic.isEmpty()) {
		user.sendRawS(Replies.RPL_NOTOPIC.format(user.nick, c.name));
		return;
	    } else {
		user.sendRawS(Replies.RPL_TOPIC.format(user.nick, c.name, c.topic));

		//FIXME time sent is sometime in 2038. Why?
		user.sendRawS(Replies.RPL_TOPICWHOTIME + " " + user.nick + " " + c.name + " " + c.topicSetBy + " " + c.topicSetAt);
		return;
	    }
	}
	if (!c.isInChannel(user)) {
	    user.sendRawS(Replies.ERR_NOTONCHANNEL.format(user.nick, c.name));
	    return;
	}
	if (c.topicProtection && !c.canPerformOperatorFunction(user.nick)) {
	    user.sendRawS(Replies.ERR_CHANOPRIVSNEEDED.format(user.nick, c.name));
	} else {
	    c.topic = rawmsg.substring(rawmsg.indexOf(':') + 1);
	    c.topicSetBy = user.getID();
	    c.topicSetAt = Util.getMicroTime();
	    c.sendRaw(user.getID(), "TOPIC " + c.name + " :" + c.topic);
	}
    }

}
