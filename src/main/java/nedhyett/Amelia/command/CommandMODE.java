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
import nedhyett.Amelia.enums.EnumChannelModes;
import nedhyett.Amelia.enums.Replies;
import nedhyett.Amelia.managers.ChannelManager;
import nedhyett.Amelia.managers.UserManager;

/**
 * Handles a MODE message from a user.
 *
 * @author Ned
 */
public class CommandMODE implements ICommand {

    /*
     *TODO:
     *O - creator
     *a - anonymous
     *i - invite only
     *m - moderated
     *n - no external
     *q - quiet
     *p - private
     *s - secret
     *r - reop
     *k - key
     *b - ban
     *e - exception
     *I - override INVITE-ONLY
     */
    
    @Override
    public void exec(User user, String[] args, String rawmsg) {
        if (args.length == 1) {
            user.sendRawS(Replies.RPL_CHANNELMODEIS + " " + user.nick + " " + args[0] + " " + ChannelManager.getChannel(args[0]).getFlags());
        } else {
            if (ChannelManager.validateChannelName(args[0])) {
                if (!ChannelManager.channelExists(args[0])) {
                    user.sendRawS(Replies.ERR_NOSUCHCHANNEL + " " + user.nick + " " + args[0] + " :No such channel");
                    return;
                }
                Channel c = ChannelManager.getChannel(args[0]);
                if(c.noModes){
                    //No modes allowed
                }
                User target;
                if (args.length == 2) {
                    target = user;
                } else {
                    target = UserManager.getUser(args[2]);
                }
                if (target != null) {
                    if (!ChannelManager.getAllChannelsWithUser(target).contains(c)) {
                        user.sendRawS(Replies.ERR_USERNOTINCHANNEL + " " + user.nick + " " + args[2] + " " + c.name + " :They aren't on that channel!");
                        return;
                    }
                }

                if (args[1].startsWith("+")) {
                    for (int i = 1; i < args[1].length(); i++) {
                        if (EnumChannelModes.get(Character.toString(args[1].charAt(i))) == null) {
                            user.sendRawS(Replies.ERR_UNKNOWNMODE + " " + user.nick + " " + args[1].charAt(i) + " :is unknown mode char to me for " + c.name);
                            continue;
                        }
                        switch (EnumChannelModes.get(Character.toString(args[1].charAt(i)))) {
                            case chanOp:
                                if (target == null) {
                                    user.sendRawS(Replies.ERR_NOSUCHNICK + " " + user.nick + " " + args[2] + " :No such nick");
                                    return;
                                }
                                if (c.canPerformOperatorFunction(user.nick)) {
                                    if (!c.isOp(target)) {
                                        c.op(target);
                                    }
                                    c.sendRaw(user.getID(), "MODE " + c.name + " +o " + target.nick);
                                } else {
                                    user.sendRawS(Replies.ERR_CHANOPRIVSNEEDED + " " + user.nick + " " + c.name + " :You're not channel operator!");
                                }
                                break;
                            case voice:
                                if (target == null) {
                                    user.sendRawS(Replies.ERR_NOSUCHNICK + " " + user.nick + " " + args[2] + " :No such nick");
                                    return;
                                }
                                if (c.canPerformVoiceFunction(user.nick)) {
                                    if (!c.isVoice(target)) {
                                        c.voice(target);
                                    }
                                    c.sendRaw(user.getID(), "MODE " + c.name + " +v " + target.nick);
                                } else {
                                    user.sendRawS(Replies.ERR_CHANOPRIVSNEEDED + " " + user.nick + " " + c.name + " :You're not channel operator!");
                                }
                                break;
                            case chanTopicProtected:
                                if (c.canPerformOperatorFunction(user.nick)) {
                                    c.topicProtection = true;
                                    c.sendRaw(user.getID(), "MODE " + c.name + " +t");
                                } else {
                                    user.sendRawS(Replies.ERR_CHANOPRIVSNEEDED + " " + user.nick + " " + c.name + " :You're not channel operator!");
                                }
                                break;
                            case chanUserLimit:
                                if (c.canPerformOperatorFunction(user.nick)) {
                                    try {
                                        c.userLimit = Integer.parseInt(args[3]);
                                        c.sendRaw(user.getID(), "MODE " + c.name + " +l " + c.userLimit);
                                    } catch (NumberFormatException ex) {
                                        user.sendRawS(Replies.ERR_NEEDMOREPARAMS + " " + " MODE :" + args[3] + " is not an integer!");
                                    }
                                } else {
                                    user.sendRawS(Replies.ERR_CHANOPRIVSNEEDED + " " + c.name + " :You're not channel operator!");
                                }
                                break;
                        }
                    }

                } else if (args[1].startsWith("-")) {

                    for (int i = 1; i < args[1].length(); i++) {
                        switch (EnumChannelModes.get(Character.toString(args[i].charAt(i)))) {
                            case chanOp:
                                if (target == null) {
                                    user.sendRawS(Replies.ERR_NOSUCHNICK + " " + user.nick + " " + args[2] + " :No such nick");
                                    return;
                                }
                                if (c.canPerformOperatorFunction(user.nick)) {
                                    if (c.isOp(target)) {
                                        c.deop(target);
                                    }
                                    c.sendRaw(user.getID(), "MODE " + c.name + " -o " + target.nick);
                                } else {
                                    user.sendRawS(Replies.ERR_CHANOPRIVSNEEDED + " " + user.nick + " " + c.name + " :You're not channel operator!");
                                }
                                break;
                            case voice:
                                if (target == null) {
                                    user.sendRawS(Replies.ERR_NOSUCHNICK + " " + user.nick + " " + args[2] + " :No such nick");
                                    return;
                                }
                                if (c.canPerformOperatorFunction(user.nick)) {
                                    if (c.isVoice(target)) {
                                        c.devoice(target);
                                    }
                                    c.sendRaw(user.getID(), "MODE " + c.name + " -v " + target.nick);
                                } else {
                                    user.sendRawS(Replies.ERR_CHANOPRIVSNEEDED + " " + user.nick + " " + c.name + " :You're not channel operator!");
                                }
                                break;
                            case chanTopicProtected:
                                if (c.canPerformOperatorFunction(user.nick)) {
                                    c.topicProtection = false;
                                    c.sendRaw(user.getID(), "MODE " + c.name + " -t");
                                } else {
                                    user.sendRawS(Replies.ERR_CHANOPRIVSNEEDED + " " + user.nick + " " + c.name + " :You're not channel operator!");
                                }
                                break;
                            case chanUserLimit:
                                if (c.canPerformOperatorFunction(user.nick)) {
                                    c.userLimit = -1;
                                    c.sendRaw(user.getID(), "MODE " + c.name + " -l");
                                } else {
                                    user.sendRawS(Replies.ERR_CHANOPRIVSNEEDED + " " + user.nick + " " + c.name + " :You're not channel operator!");
                                }
                                break;
                        }
                    }

                } else {
                    for (int i = 0; i < args[1].length(); i++) {
                        switch (EnumChannelModes.get(Character.toString(args[1].charAt(i)))) {
                            case chanTopicProtected:
                                user.sendRawS("PRIVMSG " + user.nick + " " + c.name + " t " + c.topicProtection);
                                break;
                            case chanUserLimit:
                                user.sendRawS("PRIVMSG " + user.nick + " " + c.name + " l " + c.userLimit);
                                break;
                        }
                    }
                }

            } else {

            }
        }
    }

}
