/*
 * The MIT License
 *
 * Copyright 2017 Ned Hyett.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package nedhyett.crimson.networking.http.server.provider.auth;

import nedhyett.crimson.networking.http.HttpRequest;

/**
 * Implements an authentication realm for the AuthenticationProvider.
 *
 * @author Ned Hyett
 * @see AuthenticationProvider
 * <p>
 * (Created on 14/06/2015)
 */
public interface IAuthenticationProviderComparator {

	/**
	 * Compares the username and password sent by the client to the realm security system.
	 *
	 * @param username username provided by the client. (plaintext)
	 * @param password password provided by the client. (plaintext)
	 *
	 * @return true to allow the user to be allowed through.
	 */
	boolean compare(String username, String password);

	/**
	 * Determines if this realm is valid for this request (i.e. page).
	 *
	 * @param request the request to evaluate.
	 *
	 * @return true to allow this realm to be used for further handling.
	 */
	boolean isValidForRequest(HttpRequest request);

	String getRealm(HttpRequest request);

}
