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

package nedhyett.crimson.eventreactor;

import nedhyett.crimson.logging.CrimsonLog;
import nedhyett.crimson.logging.MiniLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * A system for sending events to places preventing the need for race-conditions.
 *
 * @author Ned Hyett
 */
public class EventReactor implements WildcardListener {

	/**
	 * The name of this EventReactor.
	 */
	public final String name;

	/**
	 * The logger for this EventReactor.
	 */
	private final MiniLogger logger;

	/**
	 * A list of events that can only be added to this reactor.
	 */
	private final ArrayList<Class<? extends IEvent>> lockedEvents = new ArrayList<>();

	/**
	 * A list of Event Listeners in their containers.
	 */
	private final HashMap<Class<? extends IEvent>, ArrayList<ListenerContainer>> listeners = new HashMap<>();

	/**
	 * These listeners will receive every event sent to the reactor.
	 */
	private final HashMap<String, WildcardListener> wildcardListeners = new HashMap<>();

	/**
	 * A set of classes that "echo" events to other places, for example, over
	 * the network. Network transmission brings in to question how one would
	 * wait for a response without blocking the thread.
	 */
	private final ArrayList<EchoReactor> echoReactors = new ArrayList<>();

	public EventReactor(String name) {
		this.name = name;
		logger = CrimsonLog.spawnLogger("EventReactor - " + name);
	}

	/**
	 * Create a new EventReactor with the specified name. This name will be used
	 * in logging.
	 *
	 * @param name         the name to assign to this reactor.
	 * @param lockedEvents a list of classes that the events must extend to be allowed into this reactor.
	 */
	public EventReactor(String name, Class<? extends IEvent>... lockedEvents) {
		this.name = name;
		logger = CrimsonLog.spawnLogger("EventReactor - " + name);
		Collections.addAll(this.lockedEvents, lockedEvents);
	}

	/**
	 * Register a listener to this reactor.
	 *
	 * @param listener the class to search for event handlers.
	 */
	public void register(Object listener) {
		Class clazz = listener.getClass();
		Method[] methods = clazz.getDeclaredMethods();
		for(Method m : methods) {
			if(m.isAnnotationPresent(EventSubscribe.class)) {
				if(!validateMethod(clazz, m)) continue;
				Class[] pars = m.getParameterTypes();
				if(!listeners.containsKey(pars[0])) listeners.put(pars[0], new ArrayList<>());
				listeners.get(pars[0]).add(new ListenerContainer(m, listener));
				logger.debug("Registered %s in class %s to event %s", m.getName(), listener.getClass(), pars[0].getName());
			}
		}
	}

	/**
	 * Remove a listener from this reactor
	 *
	 * @param listener the listener
	 */
	public void unregister(Object listener) {
		Class clazz = listener.getClass();
		Method[] methods = clazz.getDeclaredMethods();
		for(Method m : methods) {
			if(m.isAnnotationPresent(EventSubscribe.class)) {
				Class[] pars = m.getParameterTypes();
				if(!listeners.containsKey(pars[0])) continue;
				for(ListenerContainer container : listeners.get(pars[0])) {
					if(container.containerClass == listener) {
						listeners.get(pars[0]).remove(container);
						logger.debug("Removed %s from reactor", container.containerClass.getClass().getCanonicalName());
					}
				}
			}
		}
	}

	/**
	 * Register a wildcard listener to the reactor. This listener will receive all events published to the reactor.
	 *
	 * @param id       Unique ID for the listener.
	 * @param wildcard The listener class itself.
	 */
	public void registerWildcard(String id, WildcardListener wildcard) {
		if(wildcardListeners.containsKey(id)) {
			logger.warning("Cannot overwrite WildcardListener with id %s", id);
			return;
		}
		wildcardListeners.put(id, wildcard);
	}

	/**
	 * Unregister a wildcard listener from the reactor.
	 *
	 * @param id The unique ID of the wildcard listener
	 */
	public void unregisterWildcard(String id) {
		wildcardListeners.remove(id);
	}

	private boolean validateMethod(Class clazz, Method m) {
		Class[] pars = m.getParameterTypes();
		if(pars.length != 1) {
			logger.warning("Bad class (%s) registered to EventReactor!", clazz.getName());
			logger.warning("Method (%s) does not have exactly one parameter!", m.getName());
			return false;
		}
		if(!IEvent.class.isAssignableFrom(pars[0])) {
			logger.warning("Bad class (%s) registered to EventReactor!", clazz.getName());
			logger.warning("Method (%s) does not have an IEvent parameter!", m.getName());
			return false;
		}
		if(m.getExceptionTypes().length > 0) {
			logger.warning("Bad class (%s) registered to EventReactor!", clazz.getName());
			logger.warning("Method (%s) throws an exception!", m.getName());
			return false;
		}
		if(Modifier.isStatic(m.getModifiers())) {
			logger.warning("Bad class (%s) registered to EventReactor!", clazz.getName());
			logger.warning("Method (%s) is static!", m.getName());
			return false;
		}
		if(lockedEvents.isEmpty()) return true;
		for(Class<? extends IEvent> evtClazz : lockedEvents) {
			if(evtClazz.isAssignableFrom(pars[0])) return true;
		}
		logger.warning("Class %s is not allowed into this reactor!", clazz.getName());
		return false;
	}

	/**
	 * Publish an event to this reactor.
	 *
	 * @param e the event to publish
	 *
	 * @return true if not cancelled, false if cancelled.
	 */
	public boolean publish(IEvent e) {
		if(e == null) throw new IllegalStateException("Cannot publish null event!");
		if(!listeners.containsKey(e.getClass()) || listeners.get(e.getClass()).isEmpty()) return true;
		if(!e.setReactor(this)) return false;
		if(!lockedEvents.isEmpty()) {
			boolean canPublish = false;
			for(Class<? extends IEvent> evtTest : lockedEvents) {
				if(evtTest.isAssignableFrom(e.getClass())) {
					canPublish = true;
					break;
				}
			}
			if(!canPublish) return false;
		}
		for(WildcardListener listener : wildcardListeners.values()) {
			listener.handleWildcardEvent(e);
			if(e.isCancelled() && e.returnOnCancel()) return false;
		}
		for(ListenerContainer c : listeners.get(e.getClass())) {
			try {
				c.method.invoke(c.containerClass, e);
			} catch(IllegalAccessException | IllegalArgumentException ex) {
				logger.severe("Failed to invoke handler (%s in class %s)", c.method.getName(), c.containerClass.getClass().getName());
				logger.severe(ex);
			} catch(InvocationTargetException ex) {
				logger.severe("Failed to invoke handler (%s in class %s)", c.method.getName(), c.containerClass.getClass().getName());
				logger.severe(ex);
				logger.severe("Caused by:");
				logger.severe(ex.getCause());

			}
			if(e.isCancelled() && e.returnOnCancel()) return false;
		}
		return !e.isCancelled();
	}

	/**
	 * Check if any listeners are registered for the event to this reactor.
	 *
	 * @param event The event to check for
	 *
	 * @return presence of any listeners for the provided event
	 */
	public boolean hasListenersFor(Class<? extends IEvent> event) {
		return listeners.containsKey(event) && !listeners.get(event).isEmpty();
	}

	/**
	 * Count the number of listeners registered to this reactor for the specified event.
	 *
	 * @param event The event to check for
	 *
	 * @return the number of listeners registered for the provided event
	 */
	public int countListenersFor(Class<? extends IEvent> event) {
		return listeners.containsKey(event) ? listeners.get(event).size() : 0;
	}

	/**
	 * Delete all reactor listeners.
	 */
	public void flushAll() {
		logger.warning("Flushing all reactor listeners!");
		listeners.clear();
	}

	MiniLogger getLogger() {
		return logger;
	}

	@Override
	public void handleWildcardEvent(IEvent event) {
		publish(event);
	}
}
