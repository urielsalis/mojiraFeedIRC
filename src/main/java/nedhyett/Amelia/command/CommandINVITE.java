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

import nedhyett.Amelia.Channel;
import nedhyett.Amelia.ICommand;
import nedhyett.Amelia.core.users.User;
import nedhyett.Amelia.enums.Replies;
import nedhyett.Amelia.managers.ChannelManager;
import nedhyett.Amelia.managers.UserManager;

/**
 * Invites a user to a channel.
 * 
 * @author Ned
 */
public class CommandINVITE implements ICommand {

    @Override
    public void exec(User user, String[] args, String rawmsg) {
        if(args.length < 2){
            user.sendRawS(Replies.ERR_NEEDMOREPARAMS.format(user.nick, "INVITE", "<nick> <channel>"));
            return;
        }
        if(!ChannelManager.channelExists(args[1])){
            user.sendRawS(Replies.ERR_NOSUCHCHANNEL.format(user.nick, args[1], "Can't invite to channel that doesn't exist!"));
            return;
        }
        Channel c = ChannelManager.getChannel(args[1]);
        if(!c.isInChannel(user)){
            user.sendRawS(Replies.ERR_NOTONCHANNEL.format(user.nick, args[1]));
            return;
        }
        if(ChannelManager.getAllChannelsWithUser(UserManager.getUser(args[0])).contains(c)){
           user.sendRawS(Replies.ERR_USERONCHANNEL.format(user.nick, args[0], args[1]));
           return;
        }
        if(c.inviteOnly && !c.canPerformOperatorFunction(user.nick)){
            user.sendRawS(Replies.ERR_CHANOPRIVSNEEDED.format(user.nick, args[1]));
        } else {
            if(!UserManager.userExists(args[0])){
                user.sendRawS(Replies.ERR_NOSUCHNICK.format(user.nick, args[0]));
                return;
            }
            if(c.inviteOnly) {
		c.invite(args[0]);
	    }
            User u = UserManager.getUser(args[0]);
            user.sendRawS(Replies.RPL_INVITING.format(args[0], u.nick, c.name));
            c.sendRawS(Replies.RPL_INVITED.format(c.name, c.name, u.nick, user.nick, u.nick, user.nick));
            u.sendRaw(user.getID(), "INVITE " + user.nick + " " + c.name);
        }
    }
    
}
