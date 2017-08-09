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

import nedhyett.crimson.networking.http.HttpResponse;
import nedhyett.crimson.networking.http.server.IProviderAcceptor;
import nedhyett.crimson.networking.http.HttpRequest;
import nedhyett.crimson.networking.http.HttpStatusCodes;
import nedhyett.crimson.networking.http.server.HttpQueryData;
import nedhyett.crimson.networking.http.server.IHttpServerProvider;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;

/**
 * Implements HTTP authentication for delegate providers using a set of assignable authentication realms.
 * <p>
 * This provider is intended to be used in a chain and does nothing on its own.
 *
 * @author Ned Hyett
 * @see IAuthenticationProviderComparator
 * <p>
 * (Created on 14/06/2015)
 */
public class AuthenticationProvider implements IHttpServerProvider, IProviderAcceptor {

	private final ArrayList<IHttpServerProvider> delegateProviders = new ArrayList<>();
	private final ArrayList<IAuthenticationProviderComparator> comparators = new ArrayList<>();

	@Override
	public void addProvider(IHttpServerProvider provider) {
		delegateProviders.add(provider);
	}

	@Override
	public void removeProvider(IHttpServerProvider provider) {
		delegateProviders.remove(provider);
	}

	public void addComparator(IAuthenticationProviderComparator comparator) {
		comparators.add(comparator);
	}

	public void removeComparator(IAuthenticationProviderComparator comparator) {
		comparators.remove(comparator);
	}

	private IAuthenticationProviderComparator getComparatorForRequest(HttpRequest request) {
		for(IAuthenticationProviderComparator comparator : comparators) {
			if(comparator.isValidForRequest(request)) return comparator;
		}
		return null;
	}

	private String[] extractAndDecryptHeaders(HttpRequest request) {
		if(!request.hasHeader("Authorization")) return null;
		return (new String(Base64.getDecoder().decode(request.getHeader("Authorization").split(" ")[1]))).split(":");
	}

	private boolean runComparators(HttpRequest request, String[] key) {
		for(IAuthenticationProviderComparator comparator : comparators) {
			if(comparator.isValidForRequest(request)) {
				if(comparator.compare(key[0], key[1])) return true;
			}
		}
		return false;
	}

	@Override
	public HttpResponse handle(HttpRequest request, HttpQueryData data, Socket s) throws Exception {
		String[] key = extractAndDecryptHeaders(request);
		IAuthenticationProviderComparator comparator = getComparatorForRequest(request);
		if(key == null || !runComparators(request, key)) {
			HttpResponse response = new HttpResponse(HttpStatusCodes._401);
			response.addResponseHeader("Connection", "close");
			response.addResponseHeader("WWW-Authenticate", "Basic Realm=\"" + comparator.getRealm(request) + "\"");
			response.addResponseBodyLine("Authenticate\n");
			return response;
		}
		for(IHttpServerProvider delegate : delegateProviders) {
			if(delegate.requestIsValid(request, s)) return delegate.handle(request, data, s);
		}
		return null;
	}

	@Override
	public boolean requestIsValid(HttpRequest request, Socket socket) {
		boolean valid = false;
		for(IAuthenticationProviderComparator comparator : comparators)
			if(comparator.isValidForRequest(request)) {
				valid = true;
				break;
			}
		if(!valid) return false;
		for(IHttpServerProvider provider : delegateProviders) if(provider.requestIsValid(request, socket)) return true;
		return false;
	}
}
