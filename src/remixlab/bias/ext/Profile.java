/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.ext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import remixlab.bias.core.BogusEvent;
import remixlab.bias.core.Grabber;
import remixlab.bias.core.Shortcut;
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
	
	protected HashMap<Shortcut, ObjectMethodTuple>	map;
	protected Grabber grabber;

	/**
	 * Constructs the hash-map based profile.
	 */
	public Profile(Grabber g) {
		map = new HashMap<Shortcut, ObjectMethodTuple>();
		grabber = g;
	}
	
	public void from(Profile p) {		
		if( grabber.getClass() != p.grabber.getClass() ) {
			System.err.println("Profile grabbers should be of the same type");
			return;
		}
		map = new HashMap<Shortcut, ObjectMethodTuple>();
		for (Map.Entry<Shortcut, ObjectMethodTuple> entry : p.map().entrySet()) {
			if( entry.getValue().object == p.grabber )
				map.put(entry.getKey(), new ObjectMethodTuple(grabber, entry.getValue().method));
			else
				map.put(entry.getKey(), new ObjectMethodTuple(entry.getValue().object, entry.getValue().method));
		}
	}

	/**
	 * Returns the {@code map} (which is simply an instance of {@code HashMap}) encapsulated by this object.
	 */
	public HashMap<Shortcut, ObjectMethodTuple> map() {
		return map;
	}

	/**
	 * Returns the {@link java.lang.reflect.Method} binding for the given {@link remixlab.bias.core.Shortcut}
	 * key.
	 */
	public Method gesture(Shortcut key) {
		return map.get(key) == null ? null : map.get(key).method;
	}
	
	public String gestureName(Shortcut key) {
		Method m = gesture(key); 
		if(m == null)
			return null;
		return m.getName();		
	}
	
	public Object object(Shortcut key) {
		return map.get(key) == null ? null : map.get(key).object;
	}
		
	public boolean handle(BogusEvent event) {
		Method iHandlerMethod = gesture(event.shortcut());
		if (iHandlerMethod != null) {
			try {
				if(object(event.shortcut()) == grabber)
					iHandlerMethod.invoke(object(event.shortcut()), new Object[] { event });
				else
					iHandlerMethod.invoke(object(event.shortcut()), new Object[] { grabber, event });
				return true;
			} catch (Exception e) {
				System.out.println("Something went wrong when invoking your " + iHandlerMethod.getName() + " method");
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	protected void printWarning(Shortcut key, String methodName) {
		if (hasBinding(key)) {
			Method a = gesture(key);
			if(a.getName().equals(methodName))
				System.out.println("Warning: shortcut already bound to " + a.getName());
			else
				System.out.println("Warning: overwritting shortcut which was previously bound to " + a.getName());
				
		}
	}
	
	public void setBinding(Shortcut key, String methodName) {
		printWarning(key, methodName);
		try {
			map.put(key, new ObjectMethodTuple(grabber, grabber.getClass().getMethod(methodName, new Class<?>[] { BogusEvent.class })));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}
	
	public void setBinding(Object object, Shortcut key, String methodName) {
		printWarning(key, methodName);
		try {
			Method method = object.getClass().getMethod(methodName, new Class<?>[] { grabber.getClass(), BogusEvent.class });
			map.put(key, new ObjectMethodTuple(object, method));
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
		printWarning(key, methodName);
		try {
			Method method = grabber.getClass().getMethod(methodName, new Class<?>[] { MotionEvent.class });
			map.put(key, new ObjectMethodTuple(grabber, method));
			//System.out.println("grabber.getClass().getName() " + grabber.getClass().getName() + ", method.getDeclaringClass().getName(): " + method.getDeclaringClass().getName());
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}
	
	public void setMotionBinding(Object object, Shortcut key, String methodName) {
		printWarning(key, methodName);
		try {
			Method method = object.getClass().getMethod(methodName, new Class<?>[] { grabber.getClass(), MotionEvent.class });
			map.put(key, new ObjectMethodTuple(object, method));
			//System.out.println("grabber.getClass().getName() " + grabber.getClass().getName() + ", method.getDeclaringClass().getName(): " + method.getDeclaringClass().getName());
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}
	
	public void removeMotionBindings() {
		Iterator<Entry<Shortcut, ObjectMethodTuple>> it = map.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Shortcut, ObjectMethodTuple> pair = it.next();
	        if( pair.getKey() instanceof MotionShortcut )
	        	it.remove();
	    }
	}
	
	public void removeMotionBindings(int [] ids) {
		if(ids == null)
			return;
		Iterator<Entry<Shortcut, ObjectMethodTuple>> it = map.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Shortcut, ObjectMethodTuple> pair = it.next();
	        Shortcut shortcut = pair.getKey();
	        if(shortcut instanceof MotionShortcut) {
	        	int id = shortcut.id();
		        for(int i = 0; i < ids.length; i++ ) {
		        	if( id == ids[i] ) {
		        		it.remove();
		        		break;
		        	}
				}	        
	        }       	
	    }
	}
	
	public void setKeyboardBinding(Shortcut key, String methodName) {
		printWarning(key, methodName);
		try {
			Method method = grabber.getClass().getMethod(methodName, new Class<?>[] { KeyboardEvent.class });
			map.put(key, new ObjectMethodTuple(grabber, method));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}
	
	public void setKeyboardBinding(Object object, Shortcut key, String methodName) {
		printWarning(key, methodName);
		try {
			Method method = object.getClass().getMethod(methodName, new Class<?>[] { grabber.getClass(), KeyboardEvent.class });
			map.put(key, new ObjectMethodTuple(object, method));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}
	
	public void removeKeyboardBindings() {
		Iterator<Entry<Shortcut, ObjectMethodTuple>> it = map.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Shortcut, ObjectMethodTuple> pair = it.next();
	        if( pair.getKey() instanceof KeyboardShortcut )
	        	it.remove();
	    }
	}

	public void setClickBinding(Shortcut key, String methodName) {
		printWarning(key, methodName);
		try {
			Method method = grabber.getClass().getMethod(methodName, new Class<?>[] { ClickEvent.class });
			map.put(key, new ObjectMethodTuple(grabber, method));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}
	
	public void setClickBinding(Object object, Shortcut key, String methodName) {
		printWarning(key, methodName);
		try {
			Method method = object.getClass().getMethod(methodName, new Class<?>[] { grabber.getClass(), ClickEvent.class });
			map.put(key, new ObjectMethodTuple(object, method));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}
	
	public void removeClickBindings() {
		Iterator<Entry<Shortcut, ObjectMethodTuple>> it = map.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Shortcut, ObjectMethodTuple> pair = it.next();
	        if( pair.getKey() instanceof ClickShortcut )
	        	it.remove();
	    }
	}
	
	public void removeClickBindings(int [] ids) {
		if(ids == null)
			return;
		Iterator<Entry<Shortcut, ObjectMethodTuple>> it = map.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Shortcut, ObjectMethodTuple> pair = it.next();
	        Shortcut shortcut = pair.getKey();
	        if(shortcut instanceof ClickShortcut) {
	        	int id = shortcut.id();
		        for(int i = 0; i < ids.length; i++ ) {
		        	if( id == ids[i] ) {
		        		it.remove();
		        		break;
		        	}
				}	        
	        }       	
	    }
	}

	public void setDOF1Binding(Shortcut key, String methodName) {
		printWarning(key, methodName);
		try {
			Method method = grabber.getClass().getMethod(methodName, new Class<?>[] { DOF1Event.class });
			map.put(key, new ObjectMethodTuple(grabber, method));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}
	
	public void setDOF1Binding(Object object, Shortcut key, String methodName) {
		printWarning(key, methodName);
		try {
			Method method = object.getClass().getMethod(methodName, new Class<?>[] { grabber.getClass(), DOF1Event.class });
			map.put(key, new ObjectMethodTuple(object, method));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}
	
	public void setDOF2Binding(Shortcut key, String methodName) {
		printWarning(key, methodName);
		try {
			Method method = grabber.getClass().getMethod(methodName, new Class<?>[] { DOF2Event.class });
			map.put(key, new ObjectMethodTuple(grabber, method));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}
	
	public void setDOF2Binding(Object object, Shortcut key, String methodName) {
		printWarning(key, methodName);
		try {
			Method method = object.getClass().getMethod(methodName, new Class<?>[] { grabber.getClass(), DOF2Event.class });
			map.put(key, new ObjectMethodTuple(object, method));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}
	
	public void setDOF3Binding(Shortcut key, String methodName) {
		printWarning(key, methodName);
		try {
			Method method = grabber.getClass().getMethod(methodName, new Class<?>[] { DOF3Event.class });
			map.put(key, new ObjectMethodTuple(grabber, method));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}
	
	public void setDOF3Binding(Object object, Shortcut key, String methodName) {
		printWarning(key, methodName);
		try {
			Method method = object.getClass().getMethod(methodName, new Class<?>[] { grabber.getClass(), DOF3Event.class });
			map.put(key, new ObjectMethodTuple(object, method));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}
	
	public void setDOF6Binding(Shortcut key, String methodName) {
		printWarning(key, methodName);
		try {
			Method method = grabber.getClass().getMethod(methodName, new Class<?>[] { DOF6Event.class });
			map.put(key, new ObjectMethodTuple(grabber, method));
		} catch (Exception e) {
			System.out.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}
	
	public void setDOF6Binding(Object object, Shortcut key, String methodName) {
		printWarning(key, methodName);
		try {
			Method method = object.getClass().getMethod(methodName, new Class<?>[] { grabber.getClass(), DOF6Event.class });
			map.put(key, new ObjectMethodTuple(object, method));
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
	
	/*
	public void removeBindings(int [] ids) {
		if(ids == null)
			return;
		Iterator<Entry<Shortcut, ObjectMethodTuple>> it = map.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Shortcut, ObjectMethodTuple> pair = it.next();
	        int id = pair.getKey().id();
	        for(int i = 0; i < ids.length; i++ ) {
	        	if( id == ids[i] ) {
	        		it.remove();
	        		break;
	        	}
			}	        	
	    }
	}
	*/

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
	 * @param method
	 *          {@link java.lang.reflect.Method}
	 * @return true if this object maps one or more shortcuts to the specified action.
	 */
	public boolean isGestureBound(String method) {
		for (ObjectMethodTuple tuple : map.values()) {
			if( grabber == tuple.object && tuple.method.getName().equals(method) )
				return true;
		}
		return false;
	}
	
	public boolean isGestureBound(Method method) {
		return isGestureBound(grabber, method);
	}
	
	public boolean isGestureBound(Object object, Method method) {
		return map.containsValue(new ObjectMethodTuple(object, method));
	}

	/**
	 * Returns a description of all the bindings this profile holds.
	 */
	public String description() {
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
}