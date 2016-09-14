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

import java.util.ArrayList;
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

  // : static stuff
  public static Object context = null;

  public static String parseKeyInfo(String info) {
    // parse...
    // the left-right-up-down keys:
    String vk_l = "VKEY_" + String.valueOf(37);
    String vk_u = "VKEY_" + String.valueOf(38);
    String vk_r = "VKEY_" + String.valueOf(39);
    String vk_d = "VKEY_" + String.valueOf(40);
    // the function keys
    String vk_f1 = "VKEY_" + String.valueOf(112);
    String vk_f2 = "VKEY_" + String.valueOf(113);
    String vk_f3 = "VKEY_" + String.valueOf(114);
    String vk_f4 = "VKEY_" + String.valueOf(115);
    String vk_f5 = "VKEY_" + String.valueOf(116);
    String vk_f6 = "VKEY_" + String.valueOf(117);
    String vk_f7 = "VKEY_" + String.valueOf(118);
    String vk_f8 = "VKEY_" + String.valueOf(119);
    String vk_f9 = "VKEY_" + String.valueOf(120);
    String vk_f10 = "VKEY_" + String.valueOf(121);
    String vk_f11 = "VKEY_" + String.valueOf(122);
    String vk_f12 = "VKEY_" + String.valueOf(123);
    // other common keys
    String vk_cancel = "VKEY_" + String.valueOf(3);
    String vk_insert = "VKEY_" + String.valueOf(155);
    String vk_delete = "VKEY_" + String.valueOf(127);
    String vk_scape = "VKEY_" + String.valueOf(27);
    String vk_enter = "VKEY_" + String.valueOf(10);
    String vk_pageup = "VKEY_" + String.valueOf(33);
    String vk_pagedown = "VKEY_" + String.valueOf(34);
    String vk_end = "VKEY_" + String.valueOf(35);
    String vk_home = "VKEY_" + String.valueOf(36);
    String vk_begin = "VKEY_" + String.valueOf(65368);

    // ... and replace it with proper descriptions:

    info = info.replace(vk_l, "LEFT_vkey").replace(vk_u, "UP_vkey").replace(vk_r, "RIGHT_vkey")
        .replace(vk_d, "DOWN_vkey").replace(vk_f1, "F1_vkey").replace(vk_f2, "F2_vkey").replace(vk_f3, "F3_vkey")
        .replace(vk_f4, "F4_vkey").replace(vk_f5, "F5_vkey").replace(vk_f6, "F6_vkey").replace(vk_f7, "F7_vkey")
        .replace(vk_f8, "F8_vkey").replace(vk_f9, "F9_vkey").replace(vk_f10, "F10_vkey").replace(vk_f11, "F11_vkey")
        .replace(vk_f12, "F12_vkey").replace(vk_cancel, "CANCEL_vkey").replace(vk_insert, "INSERT_vkey")
        .replace(vk_delete, "DELETE_vkey").replace(vk_scape, "SCAPE_vkey").replace(vk_enter, "ENTER_vkey")
        .replace(vk_pageup, "PAGEUP_vkey").replace(vk_pagedown, "PAGEDOWN_vkey").replace(vk_end, "END_vkey")
        .replace(vk_home, "HOME_vkey").replace(vk_begin, "BEGIN_vkey");
    // */

    return info;
  }

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
   * The action may be:
   * <ol>
   * <li>A method implemented in the {@link #context} that returns void and has a
   * {@link #grabber()} parameter and, optionally, a {@link remixlab.bias.core.BogusEvent}
   * parameter, or no parameters at all. A {@link remixlab.bias.event.MotionEvent} or a
   * <b>DOFnEvent()</b> that matches the {@link remixlab.bias.core.Shortcut#eventClass()}
   * may be passed to the action when binding a
   * {@link remixlab.bias.event.MotionShortcut}.</li>
   * <li>A method implemented by the {@link #grabber()} that returns void and may have a
   * {@link remixlab.bias.core.BogusEvent} parameter, or no parameters at all. A
   * {@link remixlab.bias.event.MotionEvent} or a <b>DOFnEvent()</b> that matches the
   * {@link remixlab.bias.core.Shortcut#eventClass()} may be passed to the action when
   * binding a {@link remixlab.bias.event.MotionShortcut}.</li>
   * </ol>
   * The algorithm searches the action in the above order. It will only search the action
   * in the {@link #grabber()} if nothing is found at the {@link #context}, i.e., the
   * {@link #context} takes higher precedence over the {@link #grabber()}.
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
    String message1 = null;
    Method method = null;
    if (context != null && context != grabber) {
      try {
        method = method(context, key, action);
      } catch (Exception e) {
        message1 = message(context, key, action);
      }
      if (method != null) {
        map.put(key, new ObjectMethodTuple(context, method));
        return true;
      }
    }
    String message2 = grabber().getClass().getSimpleName() + "." + action
        + " exists, is public and returns void, and that it takes no parameters or a "
        + ((key instanceof MotionShortcut) ? key.eventClass().getSimpleName() + " or MotionEvent"
            : key.eventClass().getSimpleName())
        + " parameter";
    try {
      method = grabber.getClass().getMethod(action, new Class<?>[] { key.eventClass() });
    } catch (Exception clazz) {
      try {
        method = grabber.getClass().getMethod(action, new Class<?>[] {});
      } catch (Exception empty) {
        if (key instanceof MotionShortcut)
          try {
            method = grabber.getClass().getMethod(action, new Class<?>[] { MotionEvent.class });
          } catch (Exception motion) {
            System.out.println("Warning: not binding set! Check that the "
                + (message1 != null ? message1 + ". Also check that the " + message2 : message2));
            clazz.printStackTrace();
            motion.printStackTrace();
          }
      }
    }
    if (method != null) {
      map.put(key, new ObjectMethodTuple(grabber, method));
      return true;
    }
    return false;
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
    Method method = null;
    try {
      method = method(object, key, action);
    } catch (Exception e) {
      System.out.println("Warning: not binding set! Check that the " + message(object, key, action));
      e.printStackTrace();
    }
    if (method != null) {
      map.put(key, new ObjectMethodTuple(object, method));
      return true;
    }
    return false;
  }

  /**
   * Internal use.
   * 
   * @see #setBinding(Shortcut, String)
   * @see #setBinding(Object, Shortcut, String)
   */
  protected String message(Object object, Shortcut key, String action) {
    String message = null;
    if (object != null)
      message = object.getClass().getSimpleName() + "." + action
          + " method exists, is public and returns void, and that it takes a " + grabber().getClass().getSimpleName()
          + " parameter and, optionally, a " + ((key instanceof MotionShortcut)
              ? key.eventClass().getSimpleName() + " or MotionEvent" : key.eventClass().getSimpleName())
          + " parameter.";
    return message;
  }

  /**
   * Internal use.
   * 
   * @see #setBinding(Shortcut, String)
   * @see #setBinding(Object, Shortcut, String)
   */
  protected Method method(Object object, Shortcut key, String action) throws NoSuchMethodException, SecurityException {
    Method method = null;
    try {
      method = object.getClass().getMethod(action, new Class<?>[] { grabber.getClass(), key.eventClass() });
    } catch (Exception clazz) {
      try {
        method = object.getClass().getMethod(action, new Class<?>[] { grabber.getClass() });
      } catch (Exception empty) {
        // Take into account that at the end this is the only exception to be thrown!
        if (key instanceof MotionShortcut)
          method = object.getClass().getMethod(action, new Class<?>[] { grabber.getClass(), MotionEvent.class });
      }
    }
    return method;
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
    HashMap<Shortcut, ObjectMethodTuple> clsMap = map(cls);
    for (Entry<Shortcut, ObjectMethodTuple> entry : clsMap.entrySet())
      result += entry.getKey().description() + " -> " + entry.getValue().method.getName() + "\n";
    return result;
  }

  /**
   * (Internal) Used by {@link #info(Class)}.
   */
  protected HashMap<Shortcut, ObjectMethodTuple> map(Class<?> cls) {
    HashMap<Shortcut, ObjectMethodTuple> result = new HashMap<Shortcut, ObjectMethodTuple>();
    for (Entry<Shortcut, ObjectMethodTuple> entry : map.entrySet())
      if (entry.getKey() != null && entry.getValue() != null)
        if (cls.isInstance(entry.getKey()))
          result.put(entry.getKey(), entry.getValue());
    return result;
  }

  /**
   * Returns a description of all the bindings this profile holds.
   */
  public String info() {
    // 1. Shortcut class list
    ArrayList<Class<?>> list = new ArrayList<Class<?>>();
    for (Shortcut s : map.keySet())
      if (!list.contains(s.getClass()))
        list.add(s.getClass());
    System.out.println(list.size());
    // 2. Print info per Shortcut class
    String result = new String();
    for (Class<?> clazz : list) {
      String info = info(clazz);
      if (!info.isEmpty()) {
        result += clazz.getSimpleName() + " bindings:\n";
        result += parseKeyInfo(info);
      }
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