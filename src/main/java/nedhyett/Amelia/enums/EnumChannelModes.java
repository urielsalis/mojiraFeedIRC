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
 * List of channel modes supported by Amelia.
 * @author Ned
 */
public enum EnumChannelModes {

    /**
     * Defines an anonymous channel. This means that when a message sent to the channel
     * is sent by the server to others and the origin is a user, then it <b>MUST</b> be masked.
     * <br><br>
     * To mask the message, the origin is changed to anonymous!anonymous@anonymous.".
     */
    anonymous("a"),
    
    /**
     * The channel flag '<b>q</b>' is for use by servers only. When set, it restricts the type
     * of data sent to users about the channel operations: other user joins, parts and nick changes
     * are not sent. From a user's point of view, the channel only contains one user.
     */
    quietChannel("q"),

    /**
     * When a user requests to join a channel, the server checks if the user's address matches any
     * of the ban masks set for the channel. If a match is found, the user request is denied unless
     * the address also matches an exception mask set for the channel. A user who is banned from a
     * channel and who carries an invitation sent by a channel operator is allowed to join the
     * channel.
     */
    ban("b"),

    /**
     * 
     */
    banExempt("e"),

    /**
     * When the channel flag '<b>i</b>' is set, new members are only accepted if their mask matches
     * the invite list or they have been invited by a channel operator. This flag also restricts the
     * usage of the <b>INVITE</b> command to channel operators.
     */
    inviteOnly("i"),

    /**
     * When a channel key is set, servers MUST reject their local users request to join the channel
     * unless this key is given. The channel key MUST only be made visible to the channel members in
     * the reply sent by the server to a <b>MODE</b> query.
     * <br><br>
     * Again, Amelia flouts protocol here. A configuration setting will allow IRCOps to join a channel
     * locked with a password if they have a particular userflag.
     */
    chanKey("k"),

    /**
     * A user limit may be set on channels by using the channel flag '<b>l</b>'. When the limit
     * is reached, servers MUST forbid their local users to join the channel.
     * <br><br>
     * Whilst this breaks the IRC protocol defined in the RFCs (as far as I know), Amelia has three
     * configurable modes for this flag. The first mode acts in accordance with the RFCs. The second
     * mode will kick a random non-opped and non-voiced user from the channel to make way for a
     * channel operator and the third mode exempts opped and voiced users from the user limit.
     */
    chanUserLimit("l"),

    /**
     * The channel flag '<b>m</b>' is used to control who may speak on a channel. When it is set
     * only channel operators, and members who have been given the voice privilege may send messages
     * to the channel.
     */
    chanModerated("m"),

    /**
     * 
     */
    chanUnregisteredNoSpeak("M"),

    /**
     * When the channel flag '<b>n</b>' is set, only channel members may send messages to the channel.
     */
    chanNoExternal("n"),

    /**
     * 
     */
    chanOp("o"),

    /**
     *
     */
    chanIRCOperOnly("O"),

    /**
     * 
     */
    chanRegistered("r"),

    /**
     *
     */
    chanOnlyRegisteredCanJoin("R"),

    /**
     * When a channel is "secret", in addition to not being displayed in WHOIS commands and the like,
     * the server will act as if the channel does not exist for queries like the <b>TOPIC</b>,
     * <b>LIST</b> and <b>NAMES</b> commands. Note that there is one exception to this rule:
     * servers will correctly reply to the MODE command. Secret channels are not accounted for
     * in the reply to the LUSERS command when the mask parameter is specified.
     */
    chanSecret("s"),

    /**
     * The channel flag '<b>t</b>' is used to restrict the usage of the TOPIC command to
     * channel operators.
     */
    chanTopicProtected("t"),

    /**
     *
     */
    voice("v");

    /**
     *
     */
    public final String text;

    private EnumChannelModes(String text) {
        this.text = text;
    }

    /**
     *
     * @param flag
     * @return
     */
    public static EnumChannelModes get(String flag) {
        for (EnumChannelModes mode : EnumChannelModes.values()) {
            if (mode.text.equals(flag)) {
                return mode;
            }
        }
        return null;
    }

}
