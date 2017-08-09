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
package nedhyett.Amelia.managers;

import java.util.ArrayList;
import nedhyett.Amelia.AmeliaThread;

/**
 * Manages threads and provides an easy way to interrupt all open threads.
 *
 * @author Ned
 */
public class ThreadManager {

    private static final ArrayList<AmeliaThread> threads = new ArrayList<>();

    /**
     * Register a new thread with this manager.
     *
     * @param thread
     */
    public static void register(AmeliaThread thread) {
	threads.add(thread);
    }

    /**
     * Get a list of all registered threads.
     *
     * @return
     */
    public static ArrayList<AmeliaThread> getAllThreads() {
	return threads;
    }

    /**
     * Count the number of threads.
     *
     * @return
     */
    public static int countThreads() {
	return threads.size();
    }

    /**
     * Interrupts all the registered threads.
     */
    public static void interruptAllThreads() {
	threads.stream().forEach((thread) -> {
	    thread.interrupt();
	});
	cleanUp();
    }

    /**
     * Interrupts all the registered threads of a specific type.
     *
     * @param type
     */
    public static void interruptAllThreads(Class<? extends AmeliaThread> type) {
	threads.stream().filter((thread) -> (type.isAssignableFrom(thread.getClass()))).forEach((thread) -> {
	    thread.interrupt();
	});
	cleanUp();
    }

    private static void cleanUp() {
	threads.stream().filter((thread) -> (!thread.isAlive())).forEach((thread) -> {
	    threads.remove(thread);
	});
    }

}
