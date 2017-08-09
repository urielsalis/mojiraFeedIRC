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
package nedhyett.Amelia.core.config;

import java.util.ArrayList;

/**
 * Stores an OPER config setting.
 *
 * @author Ned
 */
public class OperConfig {

    /**
     * The username of this IRCOp.
     */
    public final String name;

    /**
     * The regexes to allow this user to OPER from.
     */
    public final ArrayList<String> hostRegexes = new ArrayList<>();

    /**
     * The MD5 hash of the password.
     */
    public final String passwordMD5;

    /**
     * Allow this Oper to /KILL users.
     */
    public final boolean globalKill;

    /**
     * Allow remote usage of SQUIT and CONNECT
     */
    public final boolean remote;

    /**
     * Allows KILL, KLINE and DLINE
     */
    public final boolean kline;

    /**
     * Allows UNKLINE and UNDLINE
     */
    public final boolean unkline;

    /**
     * Allows GLINE
     */
    public final boolean gline;

    /**
     * Allows DIE and RESTART
     */
    public final boolean die;

    /**
     * Allows oper to REHASH config.
     */
    public final boolean rehash;

    /**
     * Allows oper to see nickchanges.
     */
    public final boolean nickchanges;

    /**
     * Gives admin privileges. Admins may (un)load modules and see the real IPs of servers.
     */
    public final boolean admin;

    /**
     *
     * @param name
     * @param hostRegexes
     * @param passwordMD5
     * @param globalKill
     * @param remote
     * @param kline
     * @param unkline
     * @param gline
     * @param nickchanges
     * @param rehash
     * @param die
     * @param admin
     */
    public OperConfig(String name, ArrayList<String> hostRegexes, String passwordMD5,
	    boolean globalKill, boolean remote, boolean kline, boolean unkline,
	    boolean gline, boolean nickchanges, boolean rehash, boolean die,
	    boolean admin) {

	this.name = name;
	this.hostRegexes.addAll(hostRegexes);
	this.passwordMD5 = passwordMD5;
	this.globalKill = globalKill;
	this.remote = remote;
	this.kline = kline;
	this.unkline = unkline;
	this.gline = gline;
	this.nickchanges = nickchanges;
	this.rehash = rehash;
	this.die = die;
	this.admin = admin;
    }

}
