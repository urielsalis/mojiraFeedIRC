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
package nedhyett.Amelia.enums;

/**
 * Pre-defined replies to commands. (some from RFC2812, others from 
 * https://www.alien.net.au/irc/irc2numerics.html)
 * @author Ned
 */
public enum Replies {

    /**
     * The first message sent after client registration.
     * <br><br>
     * Params: [hostmask]
     */
    RPL_WELCOME(1, ":Welcome to the Internet Relay Network %s", 1),

    /**
     * Part of the post-registration greeting.
     * <br><br>
     * Params: [hostname] [version string]
     */
    RPL_YOURHOST(2, ":Your host is %s, running version %s", 2),

    /**
     * Part of the post-registration greeting.
     * <br><br>
     * Params: [date created]
     */
    RPL_CREATED(3, ":This server was created %s", 1),

    /**
     * Part of the post-registration greeting.
     * <br><br>
     * Params: [server name] [version string] [supported user modes]
     * [supported channel modes]
     */
    RPL_MYINFO(4, "%s %s %s %s", 4),
    
    /**
     * Sent by the server to a user to suggest an alternative server, sometimes
     * used when the connection is refused because the server is already full.
     * <br><br>
     * Params: [new server name] [new server port]
     */
    RPL_BOUNCE(5, ":Try server %s, port %s", 2),
    
    /**
     * Reply with the links the server has made to clients/other servers.
     * <br><br>
     * Params: [linkname] [sendq] [sent_msgs] [sent_bytes] [recvd_msgs] [rcvd_bytes] [time_open]
     */
    RPL_STATSLINKINFO(211, "%s %s %s %s %s %s %s", 7),
    
    /**
     * End of RPL_STATS list.
     * <br><br>
     * Params: [stat letter]
     */
    RPL_ENDOFSTATS(219, "%s :End of STATS report", 1),
    
     /**
     * Information about a user's own modes.
     * <br><br>
     * Params: [user modes] [params]
     */
    RPL_UMODEIS(221, "%s %s", 2),
    
    /**
     * A service entry in the service list.
     * <br><br>
     * Params: [name] [server] [mask] [type] [hopcount] [info]
     */
    RPL_SERVLIST(234, "%s %s %s %s %s %s", 6),

    /**
     * Termination of an RPL_SERVLIST list.
     * <br><br>
     * Params: [mask] [type] [info]
     */
    RPL_SERVLISTEND(235, "%s %s :%s", 3),
    
    /**
     * Reports the server uptime.
     * <br><br>
     * Params: [days] [hours] [minutes] [seconds]
     */
    RPL_STATSUPTIME(242, ":Server up %s days %s:%s:%s", 4),
    
    /**
     * Reply to LUSERS command.
     * <br><br>
     * Params: [usercount] [invisible usercount] [servercount]
     */
    RPL_LUSERCLIENT(251, ":There are %s users and %s invisible on %s servers", 3),

    /**
     * Reply to LUSERS command - number of IRC operators (IRCOps) online.
     * <br><br>
     * Params: [number of IRCOps online]
     */
    RPL_LUSEROP(252, "%s :Operators online", 1),

    /**
     * Reply to LUSERS command - Number of unknown/unregistered connections.
     * <br><br>
     * Params: [number of unknown/unregistered connections]
     */
    RPL_LUSERUNKNOWN(253, "%s :Unknown connections", 1),

    /**
     * Reply to LUSERS command - Number of channels formed.
     * <br><br>
     * Params: [number of channels formed]
     */
    RPL_LUSERCHANNELS(254, "%s :Channels formed", 1),

    /**
     * Reply to LUSERS command - information about local connections.
     * <br><br>
     * Params: [clients] [servers]
     */
    RPL_LUSERME(255, ":I have %s clients and %s servers", 2),
    
    /**
     * Start of an ADMIN command reply.
     * <br><br>
     * Params: [server] [info]
     */
    RPL_ADMINME(256, "%s :%s", 2),

    /**
     * Reply to ADMIN command (location, first line).
     * <br><br>
     * Params: [admin location]
     */
    RPL_ADMINLOC1(257, ":%s", 1),

    /**
     * Reply to ADMIN command (location, second line).
     * <br><br>
     * Params: [admin location]
     */
    RPL_ADMINLOC2(258, ":%s", 1),

    /**
     * Reply to ADMIN command (Email address of admin).
     * <br><br>
     * Params: [email]
     */
    RPL_ADMINEMAIL(259, ":%s", 1),
    
    /**
     * When the server drops a command without processing it, this reply MUST
     * be given.
     * <br><br>
     * Params: [command] [reason]
     */
    RPL_TRYAGAIN(263, "%s :I did not process your command. Reason: %s", 2),
    
    /**
     * Dummy reply.
     * <br><br>
     * Params: none
     */
    RPL_NONE(300, "", 0),
    
    /**
     * Used in reply to a command directed at a user who is marked as AFK.
     * <br><br>
     * Params: [message]
     */
    RPL_AWAY(301, ":%s", 1),

    /**
     * Reply used by USERHOST (RFC 1459).
     * <br><br>
     * Params: DO NOT USE FORMATTER
     */
    RPL_USERHOST(302, "", Integer.MAX_VALUE),

    /**
     * Reply to the ISON command (RFC 1459).
     * <br><br>
     * Params: DO NOT USE FORMATTER
     */
    RPL_ISON(303, "", Integer.MAX_VALUE),
    
    /**
     * Reply from AWAY when no longer marked as away.
     * <br><br>
     * Params: [info]
     */
    RPL_UNAWAY(305, ":%s", 1),

    /**
     * Reply from AWAY when marked away.
     * <br><br>
     * Params: [info]
     */
    RPL_NOWAWAY(306, ":%s", 1),
    
    /**
     * Reply to WHOIS - Information about the user.
     * <br><br>
     * Params: [nick] [user] [host] [real name]
     */
    RPL_WHOISUSER(311, "%s %s %s * :%s", 4),

    /**
     * Reply to WHOIS - What server they're on.
     * <br><br>
     * Params: [nick] [server] [server info]
     */
    RPL_WHOISSERVER(312, "%s %s :%s", 3),

    /**
     * Reply to WHOIS - User has IRC Operator privileges.
     * <br><br>
     * Params: [nick] [privileges]
     */
    RPL_WHOISOPERATOR(313, "%s :%s", 2),
    
    /**
     * Reply to WHOWAS - Information about the user.
     * <br><br>
     * Params: [nick] [user] [host] [real name]
     */
    RPL_WHOWASUSER(314, "%s %s %s * :%s", 4),
    
    /**
     * Used to terminate a list of RPL_WHOREPLY replies.
     * <br><br>
     * Params: [name]
     */
    RPL_ENDOFWHO(315, "%s :End of /WHO list", 1),

    /**
     * Reply to WHOIS - Idle information.
     * <br><br>
     * Params: [nick] [seconds]
     */
    RPL_WHOISIDLE(317, "%s %s :seconds idle", 2),

    /**
     * Reply to WHOIS - End of list.
     * <br><br>
     * Params: [nick]
     */
    RPL_ENDOFWHOIS(318, "%s :End of WHOIS", 1),

    /**
     * Reply to WHOIS - Channel list for user.
     * <br><br>
     * Params: DO NOT USE FORMATTER
     */
    RPL_WHOISCHANNELS(319),
    
    /**
     * Channel list - A channel.
     * <br><br>
     * Params: [channel] [visible] [topic]
     */
    RPL_LIST(322, "%s %s :%s", 3),
    
    /**
     * Channel list - End of list.
     * <br><br>
     * Params: none
     */
    RPL_LISTEND(323, ":End of LIST", 0),    

    /**
     * Channel modes sent to client when they JOIN a channel.
     * <br><br>
     * Params: [channel] [mode] [mode params]
     */
    RPL_CHANNELMODEIS(324, "%s %s %s", 3),
    
    /**
     * ?
     * <br><br>
     * Params: [channel] [nickname]
     */
    RPL_UNIQOPIS(325, "%s %s", 2),
    
    /**
     * Response to TOPIC when no topic is set.
     * <br><br>
     * Params: [channel]
     */
    RPL_NOTOPIC(331, "%s :No topic set for this channel.", 2),

    /**
     * Response to TOPIC with the set topic.
     * <br><br>
     * Params: [channel] [topic]
     */
    RPL_TOPIC(332, "%s :%s", 2),
    
    /**
     * ?
     */
    RPL_TOPICWHOTIME(333),
    
    /**
     * Returned by the server to indicate that the attempted INVITE message was
     * successful and is being passed onto the end client.
     * <br><br>
     * Params: [nick] [channel]
     */
    RPL_INVITING(341, "%s %s", 2),

    /**
     * Not Implemented.
     */
    RPL_SUMMONING(342),
    
    /**
     * Sent to users on a channel when an INVITE command has been issued.
     * <br><br>
     * Params: [channel] [user being invited] [user issuing invite]
     * [user being invited] [user issuing invite]
     */
    RPL_INVITED(345, "%s %s %s :%s has been invited by %s", 5),
    
    /**
     * An invite mask for the invite mask list.
     * <br><br>
     * Params: [channel] [mask]
     */
    RPL_INVITELIST(346, "%s %s", 2),

    /**
     * Termination of an RPL_INVITELIST list.
     * <br><br>
     * Params: [channel]
     */
    RPL_ENDOFINVITELIST(347, "%s :End of INVITELIST", 1),
    
    /**
     * An exception mask for the exception mask list.
     * <br><br>
     * Params: [channel] [exception mask]
     */
    RPL_EXCEPTLIST(348, "%s %s", 2),

    /**
     * Termination of an RPL_EXCEPTLIST list.
     * <br><br>
     * Params: [channel]
     */
    RPL_ENDOFEXCEPTLIST(349, "%s :End of EXCEPTLIST", 1),
    
    /**
     * Reply by the server showing its version details.
     * <br><br>
     * Params: [version] [server] [comments]
     */
    RPL_VERSION(351, "%s %s :%s", 3),
    
    /**
     * Reply to vanilla WHO.
     * <br><br>
     * Params: [channel] [user] [host] [server] [nick] [H|G] [@|+] [hopcount]
     * [real name]
     */
    RPL_WHOREPLY(352, "%s %s %s %s %s %s%s :%s %s", 9),

    /**
     * Reply to NAMES.
     * <br><br>
     * Params: DO NOT USE FORMATTER
     */
    RPL_NAMEREPLY(353),
    
    /**
     * Reply to the LINKS command.
     * <br><br>
     * Params: [mask] [server] [hopcount] [server info]
     */
    RPL_LINKS(364, "%s %s :%s %s", 4),
    
    /**
     * Termination of an RPL_LINKS list.
     * <br><br>
     * Params: [mask]
     */
    RPL_ENDOFLINKS(365, "%s :End of LINKS", 1),

    /**
     * Termination of an RPL_NAMEREPLY list.
     * <br><br>
     * Params: [channel]
     */
    RPL_ENDOFNAMES(366, "%s :End of NAMES", 1),
    
    /**
     * A ban-list item.
     * <br><br>
     * Params: [channel] [banid] [time left] [reason]
     */
    RPL_BANLIST(367, "%s %s %s :%s", 4),

    /**
     * Termination of an RPL_BANLIST list.
     * <br><br>
     * Params: [channel]
     */
    RPL_ENDOFBANLIST(368, "%s :End of BANLIST", 1),
    
    /**
     * Reply to WHOWAS - End of list.
     * <br><br>
     * Params: [nick]
     */
    RPL_ENDOFWHOWAS(369, "%s :End of WHOWAS", 1),
    
    /**
     * Reply to INFO.
     * <br><br>
     * Params: [string]
     */
    RPL_INFO(371, ":%s", 1),
    
    /**
     * Reply to MOTD.
     * <br><br>
     * Params: [string]
     */
    RPL_MOTD(372, ":- %s", 1),
    
    /**
     * Termination of an RPL_INFO list.
     * <br><br>
     * Params: none
     */
    RPL_ENDOFINFO(374, ":End of INFO", 0),
    
    /**
     * Start of an RPL_MOTD list.
     * <br><br>
     * Params: [server]
     */
    RPL_MOTDSTART(375, ":- %s's Message of the day -", 1),

    /**
     * Termination of an RPL_MOTD list.
     * <br><br>
     * Params: none
     */
    RPL_ENDOFMOTD(376, ":- End of MOTD", 0),
    
    /**
     * Successful reply from OPER.
     * <br><br>
     * Params: none
     */
    RPL_YOUREOPER(381, ":Very few enter this zone... you are very privileged...", 0),

    /**
     * Successful reply from REHASH.
     * <br><br>
     * Params: [config file]
     */
    RPL_REHASHING(382, "%s :Reload successful.", 1),

    /**
     * Sent upon successful registration of a service.
     * <br><br>
     * Params: [service name]
     */
    RPL_YOURESERVICE(383, ":You are service %s", 1),
    
    /**
     * ?
     */
    RPL_NOTOPERANYMORE(385),
    
    /**
     * Response to the TIME command.
     * <br><br>
     * Params: [server] [time string]
     */
    RPL_TIME(391, "%s :%s", 2),
    
    /**
     * 
     */
    RPL_USERSSTART(392),

    /**
     *
     */
    RPL_USERS(393),

    /**
     *
     */
    RPL_ENDOFUSERS(394),

    /**
     *
     */
    RPL_NOUSERS(395),
    
    /**
     * Sent when an error occurred executing a command, but it is not known why
     * the command could not be executed.
     * <br><br>
     * Params: [command name]
     */
    ERR_UNKNOWNERROR(400, "%s :I could not complete your request; an unknown error occured.", 1),
    
    /**
     * Used to indicate that the nickname parameter supplied to a command is
     * currently unused.
     * <br><br>
     * Params: [nick]
     */
    ERR_NOSUCHNICK(401, "%s :I couldn't find this nick in my database!", 1),

    /**
     * Used to indicate the server name given currently doesn't exist.
     * <br><br>
     * Params: [server]
     */
    ERR_NOSUCHSERVER(402, "%s :I don't know about this server!", 1),

    /**
     * Used to indicate that the given channel name is invalid or does not exist.
     * <br><br>
     * Params: [channel]
     */
    ERR_NOSUCHCHANNEL(403, "%s :I can't find this channel in my database!", 2),

    /**
     * Sent to a user who does not have the rights to send a message to a channel.
     * <br><br>
     * Params: [channel]
     */
    ERR_CANNOTSENDTOCHAN(404, "%s :I cannot let you send to this channel!", 1),

    /**
     * Sent to a user when they have joined the maximum number of allowed
     * channels and they tried to join another channel.
     * <br><br>
     * Params: [channel]
     */
    ERR_TOOMANYCHANNELS(405, "%s :I cannot let you join this channel; you are already in the maximum permitted number of channels.", 1),

    /**
     * Returned by WHOWAS to indicate there was no history information for a given nickname.
     * <br><br>
     * Params: [nick]
     */
    ERR_WASNOSUCHNICK(406, "%s :I have no history information for this nickname.", 1),

    /**
     * The given target(s) for a command are ambiguous in that they relate to too many targets.
     * <br><br>
     * Params: [targets]
     */
    ERR_TOOMANYTARGETS(407, "%s :Targets too ambiguous.", 1),

    /**
     * Returned to a client which is attempting to send a SQUERY 
     * (or other message) to a service which does not exist.
     * <br><br>
     * Params: [service name]
     */
    ERR_NOSUCHSERVICE(408, "%s :I cannot forward your message to this service; it does not exist!", 1),

    /**
     * PING or PONG message missing the originator parameter which is required
     * since these commands must work without valid prefixes.
     * <br><br>
     */
    ERR_NOORIGIN(409, ":Invalid PING/PONG; no originator!", 0),

    /**
     *
     */
    ERR_NORECIPIENT(411),

    /**
     *
     */
    ERR_NOTEXTTOSEND(412),

    /**
     *
     */
    ERR_NOTOPLEVEL(413),

    /**
     *
     */
    ERR_WILDTOPLEVEL(414),

    /**
     *
     */
    ERR_BADMASK(415),

    /**
     *
     */
    ERR_UNKNOWNCOMMAND(421),

    /**
     * Sent when there is no MOTD to send to the client.
     * <br><br>
     * Params: none
     */
    ERR_NOMOTD(422, ":The MOTD file is missing.", 0),

    /**
     *
     */
    ERR_NOADMININFO(423),

    /**
     *
     */
    ERR_FILEERROR(424),

    /**
     * Returned when a nickname expected for a command isn't found.
     * <br><br>
     * Params: none
     */
    ERR_NONICKNAMEGIVEN(431, ":Command expected a nickname but one was not provided.", 0),

    /**
     * Returned after receiving a NICK message which contains a nickname which is considered invalid,
     * such as it's reserved or contains characters considered illegal for nicknames. This
     * numeric reply is misspelt but remains the same for historical reasons.
     * <br><br>
     * Params: [nick]
     */
    ERR_ERRONEUSNICKNAME(432, "%s :Illegal nickname!", 1),

    /**
     * Returned by the NICK command when the given nickname is already in use.
     * <br><br>
     * Params: [nick]
     */
    ERR_NICKNAMEINUSE(433, "%s :Nickname already in use!", 1),

    /**
     *
     */
    ERR_NICKCOLLISION(436),

    /**
     *
     */
    ERR_UNAVAILRESOURCE(437),

    /**
     *
     */
    ERR_USERNOTINCHANNEL(441),

    /**
     * Returned by the server whenever a client tries to perform a channel effecting
     * command for which the client is not a member.
     * <br><br>
     * Params: [channel]
     */
    ERR_NOTONCHANNEL(442, "%s :You aren't on this channel.", 1),

    /**
     * Returned when a client tries to invite a user to a channel they're already on.
     * <br><br>
     * Params: [nick] [channel]
     */
    ERR_USERONCHANNEL(443, "%s %s :User already in channel.", 2),

    /**
     *
     */
    ERR_NOLOGIN(444),

    /**
     *
     */
    ERR_SUMMONDISABLED(445),

    /**
     *
     */
    ERR_USERSDISABLED(446),

    /**
     *
     */
    ERR_NOTREGISTERED(451),

    /**
     * Returned by the server by any command which requires more parameters than
     * the number of parameters given.
     * <br><br>
     * Params: [command name] [syntax]
     */
    ERR_NEEDMOREPARAMS(461, "%s :Needs more params. Syntax: %s", 2),

    /**
     * Returned by the server to any link which attempts to register again.
     * <br><br>
     * Params: none
     */
    ERR_ALREADYREGISTERED(462, ":You are already registered!", 0),

    /**
     *
     */
    ERR_NOPERMFORHOST(463),

    /**
     * Returned by the PASS command to indicate the given password was required and was either
     * not given or was incorrect.
     * <br><br>
     * Params: none
     */
    ERR_PASSWDMISMATCH(464, ":Incorrect password", 0),

    /**
     *
     */
    ERR_YOUREBANNEDCREEP(465),

    /**
     *
     */
    ERR_YOUWILLBEBANNED(466),

    /**
     *
     */
    ERR_KEYSET(467),

    /**
     * Returned when attempting to join a channel which is set +l and is already full.
     * <br><br>
     * Params: [channel] [max users]
     */
    ERR_CHANNELISFULL(471, "%s :Cannot join channel; it is full (%s active users)", 2),

    /**
     * Returned when a given mode is unknown.
     * <br><br>
     * Params: [mode]
     */
    ERR_UNKNOWNMODE(472, "%s :I do not recognise this mode.", 1),

    /**
     * Returned when attempting to join a channel which is invite-only without an invitation.
     * <br><br>
     * Params: [channel]
     */
    ERR_INVITEONLYCHAN(473, "%s :Can't join channel; you are not invited!", 1),

    /**
     * Returned when attempting to join a channel a user is banned from.
     * <br><br>
     * Params: [channel]
     */
    ERR_BANNEDFROMCHAN(474, "%s :Cannot join channel; you are banned!", 1),

    /**
     * Returned when attempting to join a key-locked channel either without a key or with the wrong
     * key.
     * <br><br>
     * Params: [channel]
     */
    ERR_BADCHANNELKEY(475, "%s :Invalid channel key!", 1),

    /**
     *
     */
    ERR_BADCHANMASK(476),

    /**
     *
     */
    ERR_NOCHANMODES(477),

    /**
     *
     */
    ERR_BANLISTFULL(478),

    /**
     * Returned by any command requiring special privileges to indicate that the operation was
     * unsuccessful.
     * <br><br>
     * Params: none
     */
    ERR_NOPRIVILEGES(481, ":You have not got the required privileges.", 0),

    /**
     * Returned by any command requiring special channel privileges to indicate
     * that the operation was unsuccessful.
     * <br><br>
     * Params: [channel]
     */
    ERR_CHANOPRIVSNEEDED(482, "%s :You aren't channel operator!", 1),

    /**
     *
     */
    ERR_CANTKILLSERVER(483),

    /**
     *
     */
    ERR_RESTRICTED(484),

    /**
     *
     */
    ERR_UNIQOPPRIVSNEEDED(485),

    /**
     * Returned by OPER to a client who cannot become an IRC operator because the server has been
     * configured to disallow the client's host.
     * <br><br>
     * Params: none
     */
    ERR_NOOPERHOST(491, ":You cannot become OPER at this host.", 0),

    /**
     *
     */
    ERR_UMODEUNKNOWNFLAG(501),

    /**
     *
     */
    ERR_USERSDONTMATCH(502);

    /**
     *
     */
    public final int code;
    
    /**
     *
     */
    public final String defaultText;
    
    /**
     *
     */
    public final int requiredParams;
    
    private Replies(int code){
        this(code, null, Integer.MAX_VALUE);
    }

    private Replies(int code, String defaultText, int requiredParams) {
        this.code = code;
        this.defaultText = defaultText;
        this.requiredParams = requiredParams;
    }
    
    /**
     *
     * @param to
     * @param params
     * @return
     */
    public String format(String to, String...params){
        if(params.length < requiredParams) {
            return to + " :Wat?";
        }
        return this.toString() + " " + to + " " + String.format(this.defaultText, params);
    }

    @Override
    public String toString() {
        if (this.code < 10) {
            return "00" + this.code;
        }
        if (this.code < 100) {
            return "0" + this.code;
        }
        return this.code + "";
    }

}
