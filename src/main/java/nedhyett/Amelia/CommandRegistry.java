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
package nedhyett.Amelia;

import java.util.HashMap;
import nedhyett.Amelia.command.*;

/**
 * Registry for looking up handlers for IRC message parsing.
 *
 * @author Ned
 */
public class CommandRegistry {

    private static final HashMap<String, ICommand> commands = new HashMap<>();

    /*
     Todo:
     SQUIT
     NAMES
     LIST
     KICK
     NOTICE
     LUSERS
     VERSION
     STATS - IN PROGRESS
     LINKS
     TIME
     CONNECT
     TRACE
     INFO
     SERVLIST
     SQUERY
     WHOIS - IDLE TIME
     WHOWAS
     ERROR
     AWAY
     RESTART
     USERS
     OPERWALL
     HELP
     KNOCK
     PROTOCTL
     REHASH
     RULES
     SETNAME
     SILENCE
     WATCH
     SERVICE
     */
    static {
	addCommand("ADMIN", new CommandADMIN());
	addCommand("CAP", new CommandCAP());
	addCommand("DIE", new CommandDIE());
	addCommand("INVITE", new CommandINVITE());
	addCommand("JOIN", new CommandJOIN());
	addCommand("KILL", new CommandKILL());
	addCommand("MODE", new CommandMODE());
	addCommand("MOTD", new CommandMOTD());
	addCommand("NICK", new CommandNICK());
	addCommand("OPER", new CommandOPER());
	addCommand("PART", new CommandPART());
	addCommand("PING", new CommandPING());
	addCommand("PONG", new CommandPONG());
	addCommand("PRIVMSG", new CommandPRIVMSG());
	addCommand("QUIT", new CommandQUIT());
	addCommand("STATS", new CommandSTATS());
	addCommand("TOPIC", new CommandTOPIC());
	addCommand("USER", new CommandUSER());
	addCommand("USERHOST", new CommandUSERHOST());
	addCommand("WHO", new CommandWHO());
	addCommand("WHOIS", new CommandWHOIS());
    }

    /**
     * Register a new handler to this registry.
     *
     * @param identifier the IRC command to handle
     * @param command    the handler instance
     */
    public static void addCommand(String identifier, ICommand command) {
	commands.put(identifier, command);
    }

    /**
     * Checks if the provided IRC command is registered.
     *
     * @param identifer
     *
     * @return
     */
    public static boolean hasCommand(String identifer) {
	return commands.containsKey(identifer);
    }

    /**
     * Get a handler instance for the provided IRC command.
     *
     * @param identifier
     *
     * @return
     */
    public static ICommand getCommand(String identifier) {
	return commands.get(identifier);
    }

}
