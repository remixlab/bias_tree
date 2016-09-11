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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import remixlab.bias.core.*;
import remixlab.bias.event.*;

/**
 * A {@link remixlab.bias.core.Grabber} extension which allows to define
 * {@link remixlab.bias.core.Shortcut} to {@link java.lang.reflect.Method} bindings. See
 * {@link #setBinding(Shortcut, String)} and {@link #setBinding(Object, Shortcut, String)}
 * .
 * <p>
 * To attach a profile to a grabber first override your
 * {@link remixlab.bias.core.Grabber#performInteraction(BogusEvent)} method like this:
 * 
 * <pre>
 * {@code
 *   public void performInteraction(BogusEvent event) {
 *     profile.handle(event);
 *   }
 * }
 * </pre>
 * 
 * (see {@link #handle(BogusEvent)}) and then simply pass the grabber instance to the
 * {@link #Profile(Grabber)} constructor.
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

  static class AgentDOFTuple {
    Class<?> agent;
    int dofs;

    AgentDOFTuple(Class<?> a, int d) {
      agent = a;
      dofs = d;
    }
  }

  protected HashMap<Shortcut, ObjectMethodTuple> map;
  protected Grabber grabber;
  public static Object context = null;

  /**
   * Attaches a profile to the given grabber.
   */
  public Profile(Grabber g) {
    map = new HashMap<Shortcut, ObjectMethodTuple>();
    grabber = g;
  }

  /**
   * Instantiates this profile from another profile. Both Profile {@link #grabber()}
   * should be of the same type.
   */
  public void from(Profile p) {
    if (grabber.getClass() != p.grabber.getClass()) {
      System.err.println("Profile grabbers should be of the same type");
      return;
    }
    map = new HashMap<Shortcut, ObjectMethodTuple>();
    for (Map.Entry<Shortcut, ObjectMethodTuple> entry : p.actionMap().entrySet()) {
      if (entry.getValue().object == p.grabber)
        map.put(entry.getKey(), new ObjectMethodTuple(grabber, entry.getValue().method));
      else
        map.put(entry.getKey(), new ObjectMethodTuple(entry.getValue().object, entry.getValue().method));
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
    return map;
  }

  /**
   * Returns the {@link java.lang.reflect.Method} binding for the given
   * {@link remixlab.bias.core.Shortcut} key.
   * 
   * @see #action(Shortcut)
   */
  public Method method(Shortcut key) {
    return map.get(key) == null ? null : map.get(key).method;
  }

  /**
   * Returns the {@link java.lang.reflect.Method} binding for the given
   * {@link remixlab.bias.core.Shortcut} key.
   * 
   * @see #method(Shortcut)
   */
  public String action(Shortcut key) {
    Method m = method(key);
    if (m == null)
      return null;
    return m.getName();
  }

  /**
   * Internal macro. Returns the action performing object. Either the {@link #grabber()}
   * or an external object.
   */
  protected Object object(Shortcut key) {
    return map.get(key) == null ? null : map.get(key).object;
  }

  /**
   * Main class method to be called from
   * {@link remixlab.bias.core.Grabber#performInteraction(BogusEvent)}. Calls an action
   * handler if the {@link remixlab.bias.core.BogusEvent#shortcut()} is bound.
   * 
   * @see #setBinding(Shortcut, String)
   * @see #setBinding(Object, Shortcut, String)
   */
  public boolean handle(BogusEvent event) {
    Method iHandlerMethod = method(event.shortcut());
    if (iHandlerMethod != null) {
      try {
        if (object(event.shortcut()) == grabber)
          iHandlerMethod.invoke(object(event.shortcut()), new Object[] { event });
        else
          iHandlerMethod.invoke(object(event.shortcut()), new Object[] { grabber, event });
        return true;
      } catch (Exception e) {
        try {
          if (object(event.shortcut()) == grabber)
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
    if (action == null) {
      this.removeBinding(key);
      System.out.println(key.description() + " removed");
      return true;
    }
    if (hasBinding(key)) {
      Method a = method(key);
      if (a.getName().equals(action)) {
        System.out.println("Warning: shortcut already bound to " + a.getName());
        return true;
      } else {
        System.out.println("Warning: overwritting shortcut which was previously bound to " + a.getName());
        return false;
      }
    }
    return false;
  }

  /**
   * Defines the shortcut that triggers the given action.
   * <p>
   * The action is a method implemented by the {@link #grabber()} that returns void and
   * may have a {@link remixlab.bias.core.BogusEvent} parameter, or no parameters at all.
   * A {@link remixlab.bias.event.MotionEvent} or a <b>DOFnEvent()</b> that matches the
   * {@link remixlab.bias.core.Shortcut#eventClass()} may be passed to the action when
   * binding a {@link remixlab.bias.event.MotionShortcut}.
   * 
   * @param key
   *          {@link remixlab.bias.core.Shortcut}
   * @param action
   *          {@link java.lang.String}
   * 
   * @see #setBinding(Object, Shortcut, String)
   */
  public boolean setBinding(Shortcut key, String action) {
    if (printWarning(key, action))
      return false;
    boolean success = false;
    Method method = null;
    String message = "Check that the " + context != null
        ? context.getClass().getSimpleName() + "." + action + " or the "
        : "" + grabber().getClass().getSimpleName() + "." + action
            + " method exists, is public and returns void, and that it takes no parameters or a "
            + ((key instanceof MotionShortcut) ? key.eventClass().getSimpleName() + " or MotionEvent"
                : key.eventClass().getSimpleName())
            + " parameter";
    try {
      method = grabber.getClass().getMethod(action, new Class<?>[] { key.eventClass() });
      success = true;
    } catch (Exception clazz) {
      try {
        method = grabber.getClass().getMethod(action, new Class<?>[] {});
        success = true;
      } catch (Exception empty) {
        if (key instanceof MotionShortcut)
          try {
            method = grabber.getClass().getMethod(action, new Class<?>[] { MotionEvent.class });
            success = true;
          } catch (Exception motion) {
            if (context == null) {
              System.out.println(message);
              clazz.printStackTrace();
              motion.printStackTrace();
            }
          }
        if (!success && context != null && context != grabber) {
          success = setBinding(context, key, action);
          if (!success) {
            System.out.println(message);
            clazz.printStackTrace();
            empty.printStackTrace();
          }
          return success;
        }
      }
    }
    if (success)
      map.put(key, new ObjectMethodTuple(grabber, method));
    return success;
  }

  /**
   * Defines the shortcut that triggers the given action.
   * <p>
   * The action is a method implemented by the {@code object} that returns void and may
   * have a {@link remixlab.bias.core.BogusEvent} parameter, or no parameters at all. A
   * {@link remixlab.bias.event.MotionEvent} or a <b>DOFnEvent()</b> that matches the
   * {@link remixlab.bias.core.Shortcut#eventClass()} may be passed to the action when
   * binding a {@link remixlab.bias.event.MotionShortcut}.
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
  public boolean setBinding(Object object, Shortcut key, String action) {
    if (printWarning(key, action))
      return false;
    boolean success = false;
    Method method = null;
    String message = "Check that the " + object.getClass().getSimpleName() + "." + action
        + " method exists, is public and returns void, and that it takes a " + grabber().getClass().getSimpleName()
        + " parameter and, optionally, a " + ((key instanceof MotionShortcut)
            ? key.eventClass().getSimpleName() + " or MotionEvent" : key.eventClass().getSimpleName())
        + " parameter";
    try {
      method = object.getClass().getMethod(action, new Class<?>[] { grabber.getClass(), key.eventClass() });
      success = true;
    } catch (Exception clazz) {
      try {
        method = object.getClass().getMethod(action, new Class<?>[] { grabber.getClass() });
        success = true;
      } catch (Exception empty) {
        if (key instanceof MotionShortcut)
          try {
            method = object.getClass().getMethod(action, new Class<?>[] { grabber.getClass(), MotionEvent.class });
            success = true;
          } catch (Exception motion) {
            System.out.println(message);
            clazz.printStackTrace();
            motion.printStackTrace();
          }
        else {
          System.out.println(message);
          clazz.printStackTrace();
          empty.printStackTrace();
        }
      }
    }
    if (success)
      map.put(key, new ObjectMethodTuple(object, method));
    return success;
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
   * Removes all the shortcuts from the given shortcut class.
   */
  public void removeBindings(Class<?> cls) {
    Iterator<Entry<Shortcut, ObjectMethodTuple>> it = map.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<Shortcut, ObjectMethodTuple> pair = it.next();
      if (cls.isInstance(pair.getKey()))
        it.remove();
    }
  }

  /**
   * Returns a description of all the bindings this profile holds from the given shortcut
   * class.
   */
  public String info(Class<?> cls) {
    String result = new String();
    for (Entry<Shortcut, ObjectMethodTuple> entry : map.entrySet())
      if (entry.getKey() != null && entry.getValue() != null)
        if (cls.isInstance(entry.getKey()))
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
        if (!title) {
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
   * Returns true if this object maps one or more shortcuts to the action specified by the
   * {@link #grabber()}.
   * 
   * @param action
   *          {@link java.lang.String}
   * @return true if this object maps one or more shortcuts to the specified action.
   */
  public boolean isActionBound(String action) {
    for (ObjectMethodTuple tuple : map.values()) {
      if (grabber == tuple.object && tuple.method.getName().equals(action))
        return true;
    }
    return false;
  }

  /**
   * Returns true if this object maps one or more shortcuts to method specified by the
   * {@link #grabber()}.
   * 
   * @param method
   *          {@link java.lang.reflect.Method}
   * @return true if this object maps one or more shortcuts to the specified action.
   */
  public boolean isMethodBound(Method method) {
    return isMethodBound(grabber, method);
  }

  /**
   * Returns true if this object maps one or more shortcuts to the {@code method}
   * specified by the {@code object}.
   * 
   * @param object
   *          {@link java.lang.Object}
   * @param method
   *          {@link java.lang.reflect.Method}
   * @return true if this object maps one or more shortcuts to the specified action.
   */
  public boolean isMethodBound(Object object, Method method) {
    return map.containsValue(new ObjectMethodTuple(object, method));
  }
}