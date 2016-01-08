/**************************************************************************************
 * bias_tree
 * Copyright (c) 2014-2016 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.bias.ext;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.util.*;

/**
 * A grabber {@link remixlab.bias.core.Grabber} extension which allows to define
 * {@link remixlab.bias.core.Shortcut} to {@link java.lang.reflect.Method} bindings
 * (see {@link #setBinding(Shortcut, String)}  and {@link #setBinding(Object, Shortcut, String)})
 * and to split a grabber action into multiple stages (init, exec and flush)
 * ({@link #addStageHandler(HashMap, Class, String)} and
 * {@link #addStageHandler(HashMap, Object, Class, String)}).
 * <p>
 * To attach a profile to a grabber first override your
 * {@link remixlab.bias.core.Grabber#performInteraction(BogusEvent)} method like this:
 * {@code @Override} <br>
 * {@code public void performInteraction(BogusEvent event) {} <br>
 * {@code  	profile.handle(event);} <br>
 * {@code }} <br>
 * and then simply pass the grabber instance to the {@link #Profile(Grabber)} constructor.
 */
public class Profile {
	class ObjectMethodTuple {
		Object object;
		Method method;
		
		ObjectMethodTuple(Object o, Method m) {
			object = o;
			method = m;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				append(actionMap).
				append(initMap).
				append(execMap).
				append(flushMap).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;

		Profile other = (Profile) obj;
		return new EqualsBuilder()
				.append(actionMap, other.actionMap)
				.append(initMap, other.initMap)
				.append(execMap, other.execMap)
				.append(flushMap, other.flushMap).
				isEquals();
	}
	
	protected static HashMap<Integer, Integer> idMap = new HashMap<Integer, Integer>();	
	protected HashMap<Shortcut, ObjectMethodTuple> actionMap;
	protected Grabber grabber;
	
	// temporal vars
	protected String initAction;
	
	protected HashMap<Class<?>, ObjectMethodTuple> initMap, execMap, flushMap;
	
	/**
	 * Attaches a profile to the given grabber.
	 */
	public Profile(Grabber g) {
		actionMap = new HashMap<Shortcut, ObjectMethodTuple>();
		initMap = new HashMap<Class<?>, ObjectMethodTuple>();
		execMap = new HashMap<Class<?>, ObjectMethodTuple>();
		flushMap = new HashMap<Class<?>, ObjectMethodTuple>();
		grabber = g;
	}
	
	/**
	 * Registers a {@link remixlab.bias.event.MotionEvent#id()} to the Profile.
	 * 
	 * @see #registerMotionID(int)
	 * @see #motionIDs()
	 * @see #unregisterMotionID(int)
	 * 
	 * @param id the intended {@link remixlab.bias.event.MotionEvent#id()} to be registered
	 * @param dof Motion id degrees-of-freedom.. Either 1,2,3, or 6.
	 * @return the id or an exception if the id exists.
	 */
	public static int registerMotionID(int id, int dof) {
		if(idMap.containsKey(id))
			//System.out.println("Warning: nothing done! id already present in Profile. Call Profile.unregisterID first or use an id different than: " + Arrays.toString(idMap.keySet().toArray()));
			throw new RuntimeException("Nothing done! id already present in Profile. Call Profile.unregisterMotionID first or use an id different than: " + motionIDs().toString());
		if(dof == 1 || dof == 2 || dof == 3 || dof == 6) {
			idMap.put(id, dof);
			return id;
		}
		else
			throw new RuntimeException("Nothing done! dofs in Profile.registerMotionID should be either 1, 2, 3 or 6.");
	}
	
	/**
	 * Registers a {@link remixlab.bias.event.MotionEvent#id()} to the Profile.
	 * 
	 * @see #registerMotionID(int, int)
	 * @see #motionIDs()
	 * @see #unregisterMotionID(int)
	 * 
	 * @param dof Motion id degrees-of-freedom.. Either 1,2,3, or 6.
	 * @return the id.
	 */
	public static int registerMotionID(int dof) {
		if(dof != 1 && dof != 2 && dof != 3 && dof != 6)
			throw new RuntimeException("Warning: Nothing done! dofs in Profile.registerMotionID should be either 1, 2, 3 or 6.");
		int key = Collections.max(new ArrayList<Integer>(idMap.keySet())) + 1;
		idMap.put(key, dof);
		return key;
	}
	
	/**
	 * Returns a list of motion ids already registered at the profile.
	 * 
	 * @see #registerMotionID(int, int)
	 * @see #registerMotionID(int)
	 * @see #unregisterMotionID(int)
	 * 
	 * @return a list of motion ids already registered at the profile.
	 */
	public static ArrayList<Integer> motionIDs() {
		return new ArrayList<Integer>(idMap.keySet());
	}
	
	/**
	 * Unregisters the {@link remixlab.bias.event.MotionEvent#id()} from the Profile.
	 * 
	 * @see #registerMotionID(int, int)
	 * @see #registerMotionID(int)
	 * @see #motionIDs()
	 */
	public static void unregisterMotionID(int id) {
		idMap.remove(id);
	}
	
	/**
	 * Instantiates this profile from another profile. Both Profile {@link #grabber()} should be
	 * of the same type.
	 */
	public void from(Profile p) {	
		if( grabber.getClass() != p.grabber.getClass() ) {
			System.err.println("Profile grabbers should be of the same type");
			return;
		}
		actionMap = new HashMap<Shortcut, ObjectMethodTuple>();
		for (Map.Entry<Shortcut, ObjectMethodTuple> entry : p.actionMap().entrySet()) {
			if( entry.getValue().object == p.grabber )
				actionMap.put(entry.getKey(), new ObjectMethodTuple(grabber, entry.getValue().method));
			else
				actionMap.put(entry.getKey(), new ObjectMethodTuple(entry.getValue().object, entry.getValue().method));
		}
		initMap = new HashMap<Class<?>, ObjectMethodTuple>();
		for (Map.Entry<Class<?>, ObjectMethodTuple> entry : p.initMap.entrySet()) {
			if( entry.getValue().object == p.grabber )
				initMap.put(entry.getKey(), new ObjectMethodTuple(grabber, entry.getValue().method));
			else
				initMap.put(entry.getKey(), new ObjectMethodTuple(entry.getValue().object, entry.getValue().method));
		}
		execMap = new HashMap<Class<?>, ObjectMethodTuple>();
		for (Map.Entry<Class<?>, ObjectMethodTuple> entry : p.execMap.entrySet()) {
			if( entry.getValue().object == p.grabber )
				execMap.put(entry.getKey(), new ObjectMethodTuple(grabber, entry.getValue().method));
			else
				execMap.put(entry.getKey(), new ObjectMethodTuple(entry.getValue().object, entry.getValue().method));
		}
		flushMap = new HashMap<Class<?>, ObjectMethodTuple>();
		for (Map.Entry<Class<?>, ObjectMethodTuple> entry : p.flushMap.entrySet()) {
			if( entry.getValue().object == p.grabber )
				flushMap.put(entry.getKey(), new ObjectMethodTuple(grabber, entry.getValue().method));
			else
				flushMap.put(entry.getKey(), new ObjectMethodTuple(entry.getValue().object, entry.getValue().method));
		}
	}
	
	/**
	 * Returns the grabber to which this profile is attached.
	 */
	public Grabber grabber() {
		return grabber;
	}

	/**
	 * Internal use. Shortcut to action map.
	 */
	protected HashMap<Shortcut, ObjectMethodTuple> actionMap() {
		return actionMap;
	}
	
	/**
	 * Internal use. Event-class to init stage map.
	 */
	protected HashMap<Class<?>, ObjectMethodTuple> initMap() {
		return initMap;
	}
	
	/**
	 * Internal use. Event-class to exec stage map.
	 */
	protected HashMap<Class<?>, ObjectMethodTuple> execMap() {
		return execMap;
	}
	
	/**
	 * Internal use. Event-class to flush stage map.
	 */
	protected HashMap<Class<?>, ObjectMethodTuple> flushMap() {
		return flushMap;
	}
	
	/**
	 * Returns the the given stage {@link java.lang.reflect.Method} binding for the event key.
	 */
	public Method method(HashMap<Class<?>, ObjectMethodTuple> stageMap, Class<?> event) {
		return stageMap.get(event) == null ? null : stageMap.get(event).method;
	}

	/**
	 * Returns the {@link java.lang.reflect.Method} binding for the given {@link remixlab.bias.core.Shortcut}
	 * key.
	 * 
	 * @see #action(Shortcut)
	 */
	public Method method(Shortcut key) {
		return actionMap.get(key) == null ? null : actionMap.get(key).method;
	}
	
	/**
	 * Returns the {@link java.lang.reflect.Method} binding for the given {@link remixlab.bias.core.Shortcut}
	 * key.
	 * 
	 * @see #method(Shortcut)
	 */
	public String action(Shortcut key) {
		Method m = method(key); 
		if(m == null)
			return null;
		return m.getName();		
	}
	
	/**
	 * Internal macro. Returns the action performing object. Either the {@link #grabber()} or an external
	 * object.
	 */
	protected Object object(Shortcut key) {
		return actionMap.get(key) == null ? null : actionMap.get(key).object;
	}
	
	/**
	 * Internal macro. Returns the given stage performing object. Either the {@link #grabber()} or an external
	 * object.
	 */	
	protected Object object(HashMap<Class<?>, ObjectMethodTuple> stageMap, Class<?> event) {
		return stageMap.get(event) == null ? null : stageMap.get(event).object;
	}
	
	/**
	 * Internal macro. Sort of a shortcut to event reverse mapping. Override this method if you intend to
	 * implement your own event class.
	 */
	protected Class<?> cls(Shortcut key) {
		Class<?> eventClass = BogusEvent.class;
		if (key instanceof KeyboardShortcut)
			eventClass = KeyboardEvent.class;
		else if (key instanceof ClickShortcut)
			eventClass = ClickEvent.class;
		else if (key instanceof MotionShortcut) {
			switch (idMap.get(key.id())) {
			case 1:
				eventClass = DOF1Event.class;
				break;
			case 2:
				eventClass = DOF2Event.class;
				break;
			case 3:
				eventClass = DOF3Event.class;
				break;
			case 6:
				eventClass = DOF2Event.class;
				break;
			}
		}
		return eventClass;
	}
	
	/**
	 * Main class method to be called from {@link remixlab.bias.core.Grabber#performInteraction(BogusEvent)}.
	 * <p>
	 * Same as {@code if (!processStage(event)) invokeAction(event)}.
	 * 
	 * @see #processStage(BogusEvent)
	 * @see #invokeAction(BogusEvent)
	 */
	public void handle(BogusEvent event) {
		if (!processStage(event))
			invokeAction(event);
	}
	
	/**
	 * Calls an action handler if the {@link remixlab.bias.core.BogusEvent#shortcut()} is bound.
	 * 
	 * @see #setBinding(Shortcut, String)
	 * @see #setBinding(Object, Shortcut, String)
	 */
	protected boolean invokeAction(BogusEvent event) {
		Method iHandlerMethod = method(event.shortcut());
		if (iHandlerMethod != null) {
			try {
				if(object(event.shortcut()) == grabber)
					iHandlerMethod.invoke(object(event.shortcut()), new Object[] { event });
				else
					iHandlerMethod.invoke(object(event.shortcut()), new Object[] { grabber, event });
				return true;
			} catch (Exception e) {
				try {
					if(object(event.shortcut()) == grabber)
						iHandlerMethod.invoke(object(event.shortcut()), new Object[] {});
					else
						iHandlerMethod.invoke(object(event.shortcut()), new Object[] { grabber });
					return true;
				} catch (Exception empty) {
					System.out.println("Something went wrong when invoking your " + iHandlerMethod.getName() + " method");
					empty.printStackTrace();
				}
				System.out.println("Something went wrong when invoking your " + iHandlerMethod.getName() + " method");
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * Internal macro.
	 */
	protected boolean printWarning(Shortcut key, String action) {
		if(action == null) {
			this.removeBinding(key);
			System.out.println(key.description() + " removed");
			return true;
		}			
		if (hasBinding(key)) {
			Method a = method(key);
			if(a.getName().equals(action)) {
				System.out.println("Warning: shortcut already bound to " + a.getName());
				return true;
			}
			else {
				System.out.println("Warning: overwritting shortcut which was previously bound to " + a.getName());
				return false;
			}	
		}
		return false;
	}
	
	/**
	 * Defines the shortcut that triggers the given action.
	 * <p>
	 * The action is a method implemented by the {@link #grabber()} that returns void and may have
	 * a {@link remixlab.bias.core.BogusEvent} parameter, or no parameters at all.
	 * 
	 * @param key
	 *          {@link remixlab.bias.core.Shortcut}
	 * @param action
	 *          {@link java.lang.String}
	 *          
	 * @see #setBinding(Object, Shortcut, String)
	 */
	public void setBinding(Shortcut key, String action) {
		if (printWarning(key, action))
			return;
		Method method = null;
		try {
			method = grabber.getClass().getMethod(action, new Class<?>[] { cls(key) });
		} catch (Exception clazz) {
			boolean print = true;
			try {
				method = grabber.getClass().getMethod(action, new Class<?>[] {});
				print = false;
			} catch (Exception empty) {
				if (key instanceof MotionShortcut)
					try {
						method = grabber.getClass().getMethod(action, new Class<?>[] { MotionEvent.class });
						print = false;
					} catch (Exception motion) {
						System.out.println("Something went wrong when registering your " + action + " method");
						motion.printStackTrace();
					}
				else {
					System.out.println("Something went wrong when registering your " + action + " method");
					empty.printStackTrace();
				}
			}
			if(print) {
				System.out.println("Something went wrong when registering your " + action + " method");
				clazz.printStackTrace();
			}
		}
		actionMap.put(key, new ObjectMethodTuple(grabber, method));
	}
	
	/**
	 * Defines the shortcut that triggers the given action.
	 * <p>
	 * The action is a method implemented by the {@code object} that returns void and may have
	 * a {@link remixlab.bias.core.BogusEvent} parameter, or no parameters at all.
	 * 
	 * @param object
	 *          {@link java.lang.Object}
	 * @param key
	 *          {@link remixlab.bias.core.Shortcut}
	 * @param action
	 *          {@link java.lang.String}
	 *                   
	 * @see #setBinding(Object, Shortcut, String)
	 */
	public void setBinding(Object object, Shortcut key, String action) {
		if (printWarning(key, action))
			return;
		Method method = null;
		try {
			method = object.getClass().getMethod(action, new Class<?>[] { grabber.getClass(), cls(key) });
		} catch (Exception clazz) {
			boolean print = true;
			try {
				method = object.getClass().getMethod(action, new Class<?>[] { grabber.getClass() });
				print = false;
			} catch (Exception empty) {
				if (key instanceof MotionShortcut)
					try {
						method = object.getClass().getMethod(action, new Class<?>[] { grabber.getClass(), MotionEvent.class });
						print = false;
					} catch (Exception motion) {
						System.out.println("Something went wrong when registering your " + action + " method");
						motion.printStackTrace();
					}
				else {
					System.out.println("Something went wrong when registering your " + action + " method");
					empty.printStackTrace();
				}
			}
			if(print) {
				System.out.println("Something went wrong when registering your " + action + " method");
				clazz.printStackTrace();
			}
		}
		actionMap.put(key, new ObjectMethodTuple(object, method));
	}
	
	/**
	 * Removes the shortcut binding.
	 * 
	 * @param key
	 *          {@link remixlab.bias.core.Shortcut}
	 */
	public void removeBinding(Shortcut key) {
		actionMap.remove(key);
	}

	/**
	 * Removes all the shortcuts from this object.
	 */
	public void removeBindings() {
		actionMap.clear();
	}
	
	/**
	 * Removes all the shortcuts from the given event class.
	 */
	public void removeBindings(Class<?> cls) {
		Iterator<Entry<Shortcut, ObjectMethodTuple>> it = actionMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Shortcut, ObjectMethodTuple> pair = it.next();
	        if(cls.isInstance(pair.getKey()))
	        	it.remove();
	    }
	}
	
	/**
	 * Returns a description of all the bindings this profile holds from the given event class.
	 */
	public String info(Class<?> cls) {
		String result = new String();
		for (Entry<Shortcut, ObjectMethodTuple> entry : actionMap.entrySet())
			if (entry.getKey() != null && entry.getValue() != null)
				if(cls.isInstance(entry.getKey()))
					result += entry.getKey().description() + " -> " + entry.getValue().method.getName() + "\n";
		return result;
	}
	
	/**
	 * Returns a description of all the bindings this profile holds.
	 */
	public String info() {
		String result = new String();
		boolean title = false;
		for (Entry<Shortcut, ObjectMethodTuple> entry : actionMap.entrySet())
			if (entry.getKey() != null && entry.getValue() != null) {
				if(!title) {
				  result += entry.getKey().getClass().getSimpleName() + "s:\n";
				  title = true;
				}
				result += entry.getKey().description() + " -> " + entry.getValue().method.getName() + "\n";
			}
		return result;
	}

	/**
	 * Returns true if this object contains a binding for the specified shortcut.
	 * 
	 * @param key
	 *          {@link remixlab.bias.core.Shortcut}
	 * @return true if this object contains a binding for the specified shortcut.
	 */
	public boolean hasBinding(Shortcut key) {
		return actionMap.containsKey(key);
	}
	
	/**
	 * Returns true if this object maps one or more shortcuts to the action specified by the {@link #grabber()}.
	 * 
	 * @param action {@link java.lang.String}
	 * @return true if this object maps one or more shortcuts to the specified action.
	 */
	public boolean isActionBound(String action) {
		for (ObjectMethodTuple tuple : actionMap.values()) {
			if( grabber == tuple.object && tuple.method.getName().equals(action) )
				return true;
		}
		return false;
	}
	
	/**
	 * Returns true if this object maps one or more shortcuts to method specified by the {@link #grabber()}.
	 * 
	 * @param method {@link java.lang.reflect.Method}
	 * @return  true if this object maps one or more shortcuts to the specified action.
	 */
	public boolean isMethodBound(Method method) {
		return isMethodBound(grabber, method);
	}
	
	/**
	 * Returns true if this object maps one or more shortcuts to the {@code method} specified by the {@code object}.
	 * 
	 * @param object
	 *          {@link java.lang.Object}
	 * @param method {@link java.lang.reflect.Method}
	 * @return  true if this object maps one or more shortcuts to the specified action.
	 */
	public boolean isMethodBound(Object object, Method method) {
		return actionMap.containsValue(new ObjectMethodTuple(object, method));
	}
	
	/**
	 * Internal use. Algorithm to split an action flow into a 'three-tempi' stage sequence.
	 * <p>
	 * The algorithm parses the bogus-event in {@link remixlab.bias.core.Grabber#performInteraction(BogusEvent)}
	 * and then decide what stage to call (see {@link #invokeStageHandler(HashMap, BogusEvent)}):
	 * <ol>
     * <li>Init (1st tempi): sets the initAction, called when initAction == null.</li>
     * <li>Exec (2nd tempi): continues action execution, called when initAction == action()
     * (current action)</li>
     * <li>Flush (3rd): ends action, called when {@link remixlab.bias.core.BogusEvent#flushed()}
     * is true or when initAction != action()</li>
     * </ol>
     * <p>
     * Useful to parse multiple-tempi actions, such as a mouse press/move/drag/release flow.
	 */
	protected final boolean processStage(BogusEvent event) {
		if (initAction == null) {
			if (!event.flushed()) {
				initAction = action(event.shortcut());
				return (initAction == null) ? false : invokeStageHandler(initMap, event);// start action
			}
		}
		else { // initAction != null
			if (!event.flushed()) {
				if (initAction == action(event.shortcut()))
					return invokeStageHandler(execMap, event);// continue action
				else { // initAction != action() -> action changes abruptly
					invokeStageHandler(flushMap, event);
					initAction = action(event.shortcut());
					return (initAction == null) ? false : invokeStageHandler(initMap, event);// start action
				}
			}
			else {// action() == null
				invokeStageHandler(flushMap, event);// stopAction
				initAction = null;
				//setAction(null); // experimental, but sounds logical since: initAction != null && action() == null
				return true;
			}
		}
		return true;// i.e., if initAction == action() == null -> ignore :)
	}
	
	/**
	 * Calls an stage handler if the event class is bound.
	 * 
	 * @see #addStageHandler(HashMap, Class, String)
	 * @see #addStageHandler(HashMap, Object, Class, String)
	 */
	protected boolean invokeStageHandler(HashMap<Class<?>, ObjectMethodTuple> stageMap, BogusEvent event) {
		boolean result = false;
		ObjectMethodTuple tuple = stageMap.get(event.getClass());
		if (tuple == null)
			return result;
		Method iHandlerMethod = tuple.method;
		if (iHandlerMethod != null) {
			try {
				Object object = object(stageMap, event.getClass());
				if(stageMap != flushMap()) {
					if (object == grabber)
						result = (boolean) iHandlerMethod.invoke(object, new Object[] { event });
					else
						result = (boolean) iHandlerMethod.invoke(object, new Object[] { grabber, event });
				}
				else {
					if (object == grabber)
						iHandlerMethod.invoke(object, new Object[] { event });
					else
						iHandlerMethod.invoke(object, new Object[] { grabber, event });
					result = true;
				}
			} catch (Exception e) {
				System.out.println("Something went wrong when invoking your " + iHandlerMethod.getName() + " method");
				e.printStackTrace();
				result = false;
			}
		}
		return result;
	}
	
	/**
	 * Internal macro.
	 */
	protected boolean printWarning(HashMap<Class<?>, ObjectMethodTuple> stageMap, Class<?> event, String handler) {
		Method a = method(stageMap, event);
		String str = event.getSimpleName() + " " + (stageMap == initMap() ? "init" : stageMap == execMap() ? "exec" : "flush" ) + " stage handler";
		if(handler == null) {
			this.removeStageHandler(stageMap, event);
			System.out.println(str + " removed");
			return true;
		}			
		if (hasStageHandler(stageMap, event)) {
			System.out.println("Warning: " + a.getName() + " " + str + " overwritten");
			return false;	
		}
		return false;
	}
	
	/**
	 * Internal use.
	 */
	protected void addStageHandler(HashMap<Class<?>, ObjectMethodTuple> stageMap, Class<?> event, String handler) {
		if (printWarning(stageMap, event, handler))
			return;
		try {
			stageMap.put(event, new ObjectMethodTuple(grabber, grabber.getClass().getMethod(handler, new Class<?>[] { event })));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + handler + " method");
			e.printStackTrace();
		}
	}
	
	/**
	 * Internal use.
	 */
	protected void addStageHandler(HashMap<Class<?>, ObjectMethodTuple> stageMap, Object object, Class<?> event, String handler) {
		if (printWarning(stageMap, event, handler))
			return;
		try {
			stageMap.put(event, new ObjectMethodTuple(object, object.getClass().getMethod(handler, new Class<?>[] { grabber.getClass(), event })));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + handler + " method");
			e.printStackTrace();
		}
	}
	
	/**
	 * Defines an init stage handler for the given event class.
	 * <p>
	 * The handler is a method implemented by the {@link #grabber()} that returns boolean and has a
	 * {@link remixlab.bias.core.BogusEvent} parameter.
	 * 
	 * @see #addInitHandler(Object, Class, String)
	 */
	public void addInitHandler(Class<?> event, String handler) {
		addStageHandler(initMap, event, handler);
	}
	
	/**
	 * Defines an init stage handler for the given event class.
	 * <p>
	 * The handler is a method implemented by the {@code object} that returns boolean and has a
	 * {@link remixlab.bias.core.Grabber} parameter and a {@link remixlab.bias.core.BogusEvent} parameter.
	 * 
	 * @see #addInitHandler(Object, Class, String)
	 */
	public void addInitHandler(Object object, Class<?> event, String handler) {
		addStageHandler(initMap, object, event, handler);
	}
	
	/**
	 * Defines an exec stage handler for the given event class.
	 * <p>
	 * The handler is a method implemented by the {@link #grabber()} that returns boolean and has a
	 * {@link remixlab.bias.core.BogusEvent} parameter.
	 * 
	 * @see #addInitHandler(Object, Class, String)
	 */
	public void addExecHandler(Class<?> event, String action) {
		addStageHandler(execMap, event, action);
	}
	
	/**
	 * Defines an exec stage handler for the given event class.
	 * <p>
	 * The handler is a method implemented by the {@code object} that returns boolean and has a
	 * {@link remixlab.bias.core.Grabber} parameter and a {@link remixlab.bias.core.BogusEvent} parameter.
	 * 
	 * @see #addInitHandler(Object, Class, String)
	 */
	public void addExecHandler(Object object, Class<?> event, String handler) {
		addStageHandler(execMap, object, event, handler);
	}
	
	/**
	 * Defines a flush stage handler for the given event class.
	 * <p>
	 * The handler is a method implemented by the {@link #grabber()} that returns void and has a
	 * {@link remixlab.bias.core.BogusEvent} parameter.
	 * 
	 * @see #addInitHandler(Object, Class, String)
	 */
	public void addFlushHandler(Class<?> event, String action) {
		addStageHandler(flushMap, event, action);
	}
	
	/**
	 * Defines a flush stage handler for the given event class.
	 * <p>
	 * The handler is a method implemented by the {@code object} that returns void and has a
	 * {@link remixlab.bias.core.Grabber} parameter and a {@link remixlab.bias.core.BogusEvent} parameter.
	 * 
	 * @see #addInitHandler(Object, Class, String)
	 */
	public void addFlushHandler(Object object, Class<?> event, String handler) {
		addStageHandler(flushMap, object, event, handler);
	}
	
	/**
	 * Internal use.
	 */
	protected boolean hasStageHandler(HashMap<Class<?>, ObjectMethodTuple> stageMap, Class<?> event) {
		return stageMap.containsKey(event);
	}
	
	/**
	 * Returns true if there's an init handler for the given event class and false otherwise.
	 */
	public boolean hasInitHandler(Class<?> event) {
		return hasStageHandler(initMap, event);
	}
	
	/**
	 * Returns true if there's an exec handler for the given event class and false otherwise.
	 */
	public boolean hasExecHandler(Class<?> event) {
		return hasStageHandler(execMap, event);
	}
	
	/**
	 * Returns true if there's a flush handler for the given event class and false otherwise.
	 */
	public boolean hasFlushHandler(Class<?> event) {
		return hasStageHandler(flushMap, event);
	}
	
	/**
	 * Internal use.
	 */
	protected void removeStageHandler(HashMap<Class<?>, ObjectMethodTuple> stageMap, Class<?> event) {
		stageMap.remove(event);
	}
	
	/**
	 * Removes the init handler for the given event class.
	 */
	public void removeInitHandler(Class<?> event) {
		removeStageHandler(initMap, event);
	}
	
	/**
	 * Removes the exec handler for the given event class.
	 */
	public void removeExecHandler(Class<?> event) {
		removeStageHandler(execMap, event);
	}
	
	/**
	 * Removes the flush handler for the given event class.
	 */
	public void removeFlushHandler(Class<?> event) {
		removeStageHandler(flushMap, event);
	}
}