/*
 * Copyright (c) 2014, Ned Hyett
 * All rights reserved.
 *
 * By using this program/package/library you agree to be completely and unconditionally
 * bound by the agreement displayed below. Any deviation from this agreement will not
 * be tolerated.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. No part of this text may be modified
 *    by anyone other than the original copyright holder.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 * 3. The redistribution is not sold, unless permission is granted from the copyright holder.
 * 4. The redistribution must contain reference to the original author and provide a
 *    link (or other means) to aquire the original source code from the original copyright holder.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package nedhyett.Amelia.core.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import nedhyett.Amelia.Amelia;
import nedhyett.Amelia.AmeliaThread;
import nedhyett.Amelia.CommandRegistry;
import nedhyett.Amelia.ICommand;
import nedhyett.Amelia.core.users.User;
import nedhyett.crimson.logging.CrimsonLog;

/**
 * An input reader for a connection.
 *
 * @author Ned
 */
public class InputThread extends AmeliaThread {

    private final BufferedReader in;

    private final User parent;

    private int fails = 0;

    /**
     * Create a new input thread that wraps around the provided InputStream
     *
     * @param parent
     * @param in
     */
    public InputThread(User parent, InputStream in) {
        super();
        this.setDaemon(true);
        this.setName("Amelia:InputThread");
        this.in = new BufferedReader(new InputStreamReader(in));
        this.parent = parent;
    }

    /**
     * Get the User instance that created this InputThread.
     *
     * @return
     */
    public User getParent() {
        return this.parent;
    }

    @Override
    public void run() {
        while (!this.isInterrupted() && !this.getParent().socket.isInputShutdown()) {
            try {
                String line = this.in.readLine();
                this.processLine(line);
            } catch (IOException e) {
                CrimsonLog.severe("Exception in input thread!");
                CrimsonLog.severe(e);
                this.getParent().quit("Server error: " + e.getMessage());
                break;
            }
        }
        try {
            getParent().socket.close();
        } catch (IOException e) {
            //Ignore the error.
        }
    }

    @Override
    public void interrupt() {
        try {
            this.getParent().socket.shutdownInput();
        } catch (IOException ex) {

        } finally {
            super.interrupt();
        }
    }

    /**
     * Handle the line read from the input stream.
     *
     * @param command
     */
    public void processLine(String command) {
        CrimsonLog.debug(command + " (from " + this.getParent().getID() + ")");
        if (command == null) {
            if (this.fails > 100) {
                //send("You have been kicked due to receiving too many null lines!");
                //getParent().die(new IllegalStateException("Too many null lines!"));
                this.interrupt();
            }
            this.fails++;
            return;
        }

        if (command.indexOf(' ') <= 0) {
            command += " ";
        }
        if (this.fails > 0) {
            this.fails--; //Give the user credit for actually sending a line this time :)
        }
        if (!CommandRegistry.hasCommand(command.split(" ")[0].toUpperCase())) {
            CrimsonLog.warning(getParent().getID() + " is sending invalid command " + command);
            send(Amelia.config.serverHost, "421 " + getParent().getID() + " " + command.split(" ")[0].toUpperCase() + " :Unknown command");
        } else {
            ICommand cmd = CommandRegistry.getCommand(command.split(" ")[0].toUpperCase());
            String[] args = new String[command.split(" ").length - 1];
            for (int i = 1; i < command.split(" ").length; i++) {
                args[i - 1] = command.split(" ")[i];
            }
            try {
                cmd.exec(this.getParent(), args, command);
            } catch (Exception e) {
                CrimsonLog.warning(e);
            }
        }
    }

    /**
     * Send a line to this client.
     *
     * @param origin
     * @param line
     */
    public void send(String origin, String line) {
        this.getParent().sendRaw(origin, line);
    }

}
