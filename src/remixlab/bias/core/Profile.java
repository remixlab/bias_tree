/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import remixlab.bias.event.*;
import remixlab.util.*;

/**
 * A mapping defining {@link remixlab.bias.core.Shortcut} to {@link java.lang.reflect.Method}
 * bindings.
 * <p>
 * 
 */
public class Profile implements Copyable {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(map).toHashCode();
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
		return new EqualsBuilder().append(map, other.map).isEquals();
	}
	
	protected HashMap<Shortcut, Method>	map;
	protected Grabber grabber;

	/**
	 * Constructs the hash-map based profile.
	 */
	public Profile(Grabber g) {
		map = new HashMap<Shortcut, Method>();
		grabber = g;
	}

	/**
	 * Copy constructor. Use {@link #get()} to copy this profile.
	 * 
	 * @param other
	 *          profile to be copied
	 */
	protected Profile(Profile other) {
		map = new HashMap<Shortcut, Method>();
		for (Map.Entry<Shortcut, Method> entry : other.map().entrySet()) {
			Shortcut key = entry.getKey();
			Method value = entry.getValue();
			map.put(key, value);
		}
		grabber = other.grabber;
	}

	/**
	 * Returns a deep-copy of this profile.
	 */
	@Override
	public Profile get() {
		return new Profile(this);
	}

	/**
	 * Returns the {@code map} (which is simply an instance of {@code HashMap}) encapsulated by this object.
	 */
	public HashMap<Shortcut, Method> map() {
		return map;
	}

	/**
	 * Returns the {@link java.lang.reflect.Method} binding for the given {@link remixlab.bias.core.Shortcut}
	 * key.
	 */
	public Method method(Shortcut key) {
		return map.get(key);
	}
	
		
	public boolean handle(BogusEvent event) {
		Method iHandlerMethod = method(event.shortcut());
		// 3. Draw external registered method
		if (iHandlerMethod != null) {
			try {
				iHandlerMethod.invoke(grabber, new Object[] { event });
				return true;
			} catch (Exception e) {
				System.out.println("Something went wrong when invoking your " + iHandlerMethod.getName() + " method");
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	public void setBinding(Shortcut key, String methodName) {
		if (hasBinding(key)) {
			Method a = method(key);
			if(a.getName().equals(methodName))
				System.out.println("Warning: shortcut already bound to " + a);
			else
				System.out.println("Warning: overwritting shortcut which was previously bound to " + a);
				
		}
		try {
			map.put(key, grabber.getClass().getMethod(methodName, new Class<?>[] { BogusEvent.class }));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}
	
	/**
	 * Defines the shortcut that triggers the given method.
	 * 
	 * @param key
	 *          {@link remixlab.bias.core.Shortcut}
	 * @param method
	 *          {@link java.lang.reflect.Method}
	 */
	public void setMotionBinding(Shortcut key, String methodName) {
		if (hasBinding(key)) {
			Method a = method(key);
			if(a.getName().equals(methodName))
				System.out.println("Warning: shortcut already bound to " + a);
			else
				System.out.println("Warning: overwritting shortcut which was previously bound to " + a);
				
		}
		try {
			map.put(key, grabber.getClass().getMethod(methodName, new Class<?>[] { MotionEvent.class }));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}
	
	public void removeMotionBindings() {
		Iterator<Entry<Shortcut, Method>> it = map.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Shortcut, Method> pair = it.next();
	        if( pair.getKey() instanceof MotionShortcut )
	        	it.remove();
	    }
	}
	
	public void setKeyboardBinding(Shortcut key, String methodName) {
		if (hasBinding(key)) {
			Method a = method(key);
			if(a.getName().equals(methodName))
				System.out.println("Warning: shortcut already bound to " + a);
			else
				System.out.println("Warning: overwritting shortcut which was previously bound to " + a);
				
		}
		try {
			map.put(key, grabber.getClass().getMethod(methodName, new Class<?>[] { KeyboardEvent.class }));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}
	
	public void removeKeyboardBindings() {
		Iterator<Entry<Shortcut, Method>> it = map.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Shortcut, Method> pair = it.next();
	        if( pair.getKey() instanceof KeyboardShortcut )
	        	it.remove();
	    }
	}
	
	public void setClickBinding(Shortcut key, String methodName) {
		if (hasBinding(key)) {
			Method a = method(key);
			if(a.getName().equals(methodName))
				System.out.println("Warning: shortcut already bound to " + a);
			else
				System.out.println("Warning: overwritting shortcut which was previously bound to " + a);
				
		}
		try {
			map.put(key, grabber.getClass().getMethod(methodName, new Class<?>[] { ClickEvent.class }));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}
	
	public void removeClickBindings() {
		Iterator<Entry<Shortcut, Method>> it = map.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Shortcut, Method> pair = it.next();
	        if( pair.getKey() instanceof ClickShortcut )
	        	it.remove();
	    }
	}
	
	public void setDOF1Binding(Shortcut key, String methodName) {
		if (hasBinding(key)) {
			Method a = method(key);
			if(a.getName().equals(methodName))
				System.out.println("Warning: shortcut already bound to " + a);
			else
				System.out.println("Warning: overwritting shortcut which was previously bound to " + a);
				
		}
		try {
			map.put(key, grabber.getClass().getMethod(methodName, new Class<?>[] { DOF1Event.class }));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}
	
	public void setDOF2Binding(Shortcut key, String methodName) {
		if (hasBinding(key)) {
			Method a = method(key);
			if(a.getName().equals(methodName))
				System.out.println("Warning: shortcut already bound to " + a);
			else
				System.out.println("Warning: overwritting shortcut which was previously bound to " + a);
				
		}
		try {
			map.put(key, grabber.getClass().getMethod(methodName, new Class<?>[] { DOF2Event.class }));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}
	
	public void setDOF3Binding(Shortcut key, String methodName) {
		if (hasBinding(key)) {
			Method a = method(key);
			if(a.getName().equals(methodName))
				System.out.println("Warning: shortcut already bound to " + a);
			else
				System.out.println("Warning: overwritting shortcut which was previously bound to " + a);
				
		}
		try {
			map.put(key, grabber.getClass().getMethod(methodName, new Class<?>[] { DOF3Event.class }));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}
	
	public void setDOF6Binding(Shortcut key, String methodName) {
		if (hasBinding(key)) {
			Method a = method(key);
			if(a.getName().equals(methodName))
				System.out.println("Warning: shortcut already bound to " + a);
			else
				System.out.println("Warning: overwritting shortcut which was previously bound to " + a);
				
		}
		try {
			map.put(key, grabber.getClass().getMethod(methodName, new Class<?>[] { DOF6Event.class }));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
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
	 * @param methot
	 *          {@link java.lang.reflect.Method}
	 * @return true if this object maps one or more shortcuts to the specified action.
	 */
	public boolean isMethodBound(Method method) {
		return map.containsValue(method);
	}

	/**
	 * Returns a description of all the bindings this profile holds.
	 */
	public String description() {
		String result = new String();
		boolean title = false;
		for (Entry<Shortcut, Method> entry : map.entrySet())
			if (entry.getKey() != null && entry.getValue() != null) {
				if(!title) {
				  result += entry.getKey().getClass().getSimpleName() + "s:\n";
				  title = true;
				}
				result += entry.getKey().description() + " -> " + entry.getValue().getName() + "\n";
			}
		return result;
	}
}