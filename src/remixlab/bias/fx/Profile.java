/**************************************************************************************
 * bias_tree
 * Copyright (c) 2014-2016 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.bias.fx;

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
 * A mapping defining {@link remixlab.bias.core.Shortcut} to {@link java.lang.reflect.Method}
 * bindings.
 * <p>
 * 
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
				append(map).
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
				.append(map, other.map)
				.append(initMap, other.initMap)
				.append(execMap, other.execMap)
				.append(flushMap, other.flushMap).
				isEquals();
	}
	
	protected static HashMap<Integer, Integer> idMap = new HashMap<Integer, Integer>();	
	protected HashMap<Shortcut, ObjectMethodTuple> map;
	protected Grabber grabber;
	
	// temporal vars
	String initAction;
	
	protected HashMap<Class<?>, ObjectMethodTuple> initMap, execMap, flushMap;
	
	/**
	 * Constructs the hash-map based profile.
	 */
	public Profile(Grabber g) {
		map = new HashMap<Shortcut, ObjectMethodTuple>();
		initMap = new HashMap<Class<?>, ObjectMethodTuple>();
		execMap = new HashMap<Class<?>, ObjectMethodTuple>();
		flushMap = new HashMap<Class<?>, ObjectMethodTuple>();
		grabber = g;
	}
	
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
	
	public static int registerMotionID(int dof) {
		if(dof != 1 && dof != 2 && dof != 3 && dof != 6)
			throw new RuntimeException("Warning: Nothing done! dofs in Profile.registerMotionID should be either 1, 2, 3 or 6.");
		int key = Collections.max(new ArrayList<Integer>(idMap.keySet())) + 1;
		idMap.put(key, dof);
		return key;
	}
	
	public static ArrayList<Integer> motionIDs() {
		return new ArrayList<Integer>(idMap.keySet());
	}
	
	public static void unregisterMotionID(int id) {
		idMap.remove(id);
	}
	
	public void from(Profile p) {	
		if( grabber.getClass() != p.grabber.getClass() ) {
			System.err.println("Profile grabbers should be of the same type");
			return;
		}
		map = new HashMap<Shortcut, ObjectMethodTuple>();
		for (Map.Entry<Shortcut, ObjectMethodTuple> entry : p.actionMap().entrySet()) {
			if( entry.getValue().object == p.grabber )
				map.put(entry.getKey(), new ObjectMethodTuple(grabber, entry.getValue().method));
			else
				map.put(entry.getKey(), new ObjectMethodTuple(entry.getValue().object, entry.getValue().method));
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
	
	public Grabber grabber() {
		return grabber;
	}

	/**
	 * Returns the {@code map} (which is simply an instance of {@code HashMap}) encapsulated by this object.
	 */
	protected HashMap<Shortcut, ObjectMethodTuple> actionMap() {
		return map;
	}
	
	protected HashMap<Class<?>, ObjectMethodTuple> initMap() {
		return initMap;
	}
	
	protected HashMap<Class<?>, ObjectMethodTuple> execMap() {
		return execMap;
	}
	
	protected HashMap<Class<?>, ObjectMethodTuple> flushMap() {
		return flushMap;
	}
	
	public Method method(HashMap<Class<?>, ObjectMethodTuple> stageMap, Class<?> event) {
		return stageMap.get(event) == null ? null : stageMap.get(event).method;
	}

	/**
	 * Returns the {@link java.lang.reflect.Method} binding for the given {@link remixlab.bias.core.Shortcut}
	 * key.
	 */
	public Method method(Shortcut key) {
		return map.get(key) == null ? null : map.get(key).method;
	}
	
	public String action(Shortcut key) {
		Method m = method(key); 
		if(m == null)
			return null;
		return m.getName();		
	}
	
	protected Object object(Shortcut key) {
		return map.get(key) == null ? null : map.get(key).object;
	}
	
	protected Object object(HashMap<Class<?>, ObjectMethodTuple> stageMap, Class<?> event) {
		return stageMap.get(event) == null ? null : stageMap.get(event).object;
	}
	
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
	
	public void handle(BogusEvent event) {
		if (!processStage(event))
			invokeAction(event);
	}
	
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
	 * Defines the shortcut that triggers the given method.
	 * 
	 * @param key
	 *          {@link remixlab.bias.core.Shortcut}
	 * @param action
	 *          {@link java.lang.reflect.Method}
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
		map.put(key, new ObjectMethodTuple(grabber, method));
	}
	
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
		map.put(key, new ObjectMethodTuple(object, method));
	}
	
	/**
	 * Removes the shortcut binding.
	 * 
	 * @param key
	 *          {@link remixlab.bias.core.Shortcut}
	 */
	public void removeBinding(Shortcut key) {
		map.remove(key);
	}

	/**
	 * Removes all the shortcuts from this object.
	 */
	public void removeBindings() {
		map.clear();
	}
	
	public void removeBindings(Class<?> cls) {
		Iterator<Entry<Shortcut, ObjectMethodTuple>> it = map.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Shortcut, ObjectMethodTuple> pair = it.next();
	        if(cls.isInstance(pair.getKey()))
	        	it.remove();
	    }
	}
	
	public String info(Class<?> cls) {
		String result = new String();
		for (Entry<Shortcut, ObjectMethodTuple> entry : map.entrySet())
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
		for (Entry<Shortcut, ObjectMethodTuple> entry : map.entrySet())
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
		return map.containsKey(key);
	}
	
	/**
	 * Returns true if this object maps one or more shortcuts to the specified action.
	 * 
	 * @param action
	 *          {@link java.lang.reflect.Method}
	 * @return true if this object maps one or more shortcuts to the specified action.
	 */
	public boolean isActionBound(String action) {
		for (ObjectMethodTuple tuple : map.values()) {
			if( grabber == tuple.object && tuple.method.getName().equals(action) )
				return true;
		}
		return false;
	}
	
	public boolean isMethodBound(Method method) {
		return isMethodBound(grabber, method);
	}
	
	public boolean isMethodBound(Object object, Method method) {
		return map.containsValue(new ObjectMethodTuple(object, method));
	}
	
	//
	
	/**
	 * Internal use. Algorithm to split an action flow into a 'three-tempi' {@link remixlab.bias.branch.Action} sequence.
	 * It's called like this (see {@link #performInteraction(BogusEvent)}):
	 * <pre>
     * {@code
	 * public void performInteraction(BogusEvent event) {
	 *	if (processEvent(event))
	 *		return;
	 *	if (event instanceof KeyboardEvent)
	 *		performInteraction((KeyboardEvent) event);
	 *	if (event instanceof ClickEvent)
	 *		performInteraction((ClickEvent) event);
	 *	if (event instanceof MotionEvent)
	 *		performInteraction((MotionEvent) event);
	 * }
     * }
     * </pre>
	 * <p>
	 * The algorithm parses the bogus-event in {@link #performInteraction(BogusEvent)} and then decide what to call:
	 * <ol>
     * <li>{@link #initStage(BogusEvent)} (1st tempi): sets the initAction, called when initAction == null.</li>
     * <li>{@link #execStage(BogusEvent)} (2nd tempi): continues action execution, called when initAction == action()
     * (current action)</li>
     * <li>{@link #flushStage(BogusEvent)} (3rd): ends action, called when {@link remixlab.bias.core.BogusEvent#flushed()}
     * is true or when initAction != action()</li>
     * </ol>
     * <p>
     * Useful to parse multiple-tempi actions, such as a mouse press/move/drag/release flow.
     * <p>
     * The following motion-actions have been implemented using the aforementioned technique:
	 * {@link remixlab.dandelion.branch.Constants.DOF2Action#SCREEN_ROTATE},
	 * {@link remixlab.dandelion.branch.Constants.DOF2Action#ZOOM_ON_REGION},
	 * {@link remixlab.dandelion.branch.Constants.DOF2Action#MOVE_BACKWARD}, and
	 * {@link remixlab.dandelion.branch.Constants.DOF2Action#MOVE_FORWARD}.
	 * <p>
     * Current implementation only supports {@link remixlab.bias.event.MotionEvent}s.
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
	
	protected boolean printWarning(HashMap<Class<?>, ObjectMethodTuple> stageMap, Class<?> event, String action) {
		Method a = method(stageMap, event);
		String str = event.getSimpleName() + " " + (stageMap == initMap() ? "init" : stageMap == execMap() ? "exec" : "flush" ) + " stage handler";
		if(action == null) {
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
	
	protected void addStageHandler(HashMap<Class<?>, ObjectMethodTuple> stageMap, Class<?> event, String action) {
		if (printWarning(stageMap, event, action))
			return;
		try {
			stageMap.put(event, new ObjectMethodTuple(grabber, grabber.getClass().getMethod(action, new Class<?>[] { event })));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + action + " method");
			e.printStackTrace();
		}
	}
	
	protected void addStageHandler(HashMap<Class<?>, ObjectMethodTuple> stageMap, Object object, Class<?> event, String action) {
		if (printWarning(stageMap, event, action))
			return;
		try {
			stageMap.put(event, new ObjectMethodTuple(object, object.getClass().getMethod(action, new Class<?>[] { grabber.getClass(), event })));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + action + " method");
			e.printStackTrace();
		}
	}
	
	public void addInitHandler(Class<?> event, String action) {
		addStageHandler(initMap, event, action);
	}
	
	public void addInitHandler(Object object, Class<?> event, String action) {
		addStageHandler(initMap, object, event, action);
	}
	
	public void addExecHandler(Class<?> event, String action) {
		addStageHandler(execMap, event, action);
	}
	
	public void addExecHandler(Object object, Class<?> event, String action) {
		addStageHandler(execMap, object, event, action);
	}
	
	public void addFlushHandler(Class<?> event, String action) {
		addStageHandler(flushMap, event, action);
	}
	
	public void addFlushHandler(Object object, Class<?> event, String action) {
		addStageHandler(flushMap, object, event, action);
	}
	
	protected boolean hasStageHandler(HashMap<Class<?>, ObjectMethodTuple> stageMap, Class<?> event) {
		return stageMap.containsKey(event);
	}
	
	public boolean hasInitHandler(Class<?> event) {
		return hasStageHandler(initMap, event);
	}
	
	public boolean hasExecHandler(Class<?> event) {
		return hasStageHandler(execMap, event);
	}
	
	public boolean hasFlushHandler(Class<?> event) {
		return hasStageHandler(flushMap, event);
	}
	
	protected void removeStageHandler(HashMap<Class<?>, ObjectMethodTuple> stageMap, Class<?> event) {
		stageMap.remove(event);
	}
	
	public void removeInitHandler(Class<?> event) {
		removeStageHandler(initMap, event);
	}
	
	public void removeExecHandler(Class<?> event) {
		removeStageHandler(execMap, event);
	}
	
	public void removeFlushHandler(Class<?> event) {
		removeStageHandler(flushMap, event);
	}
}