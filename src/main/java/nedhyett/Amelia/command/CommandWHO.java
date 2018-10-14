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

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the WHO command.
 *
 * @author Ned
 */
public class CommandWHO implements ICommand {

    @Override
    public void exec(User user, String[] args, String rawmsg) {
        if (ChannelManager.channelExists(args[0])) {
            Channel c = ChannelManager.getChannel(args[0]);
            final List<User> usersToRemove = new ArrayList<>();
            c.getActiveUsers().forEach((u) -> {
                if(u==null || u.socket==null || u.socket.isClosed() || !u.socket.isConnected()) {
                    usersToRemove.add(u);
                } else {
                    user.sendRawS(Replies.RPL_WHOREPLY + " " + user.nick + " " + c.name + " " + u.username + " " + u.socket.getInetAddress().getHostAddress() + " " + Amelia.config.serverHost + " " + u.nick + " H" + ((c.isOp(u)) ? "@" : "") + " :0 " + u.GECOS);
                }
            });
            usersToRemove.forEach(c::leaveNoAnnounce);
            user.sendRawS(Replies.RPL_ENDOFWHO + " " + user.nick + " " + c.name + " :End of /WHO list");
        }
    }

}
