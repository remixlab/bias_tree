/**************************************************************************************
 * bias_tree
 * Copyright (c) 2014-2016 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.bias.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import remixlab.bias.core.Shortcut;
import remixlab.util.Copyable;

/**
 * This class represents {@link remixlab.bias.event.MotionEvent} shortcuts.
 * <p>
 * Motion shortcuts can be of one of two forms: 1. A gesture-id (e.g., 'LEFT_ID' , or even
 * 'NO_ID') or, ; 2. A gesture-id + modifier key combinations (e.g., 'RIGHT_ID' + 'CTRL').
 * <p>
 * Note that the shortcut may be empty: the no-id (NO_ID) and no-modifier-mask
 * (NO_MODIFIER_MASK) combo may also defined a shortcut. Empty shortcuts may bind
 * gesture-less motion interactions (e.g., mouse move without any button pressed).
 * <p>
 * <b>Note</b> that the motion-event {@link #id()} DOFs should be registered first (see
 * {@link #registerID(int)}) before using the shortcut.
 */
public final class MotionShortcut extends Shortcut implements Copyable {
protected static HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
  
  /**
   * Returns the {@link #id()} DOF's.
   * <p>
   * Returns {@code null} if nthe id is not registered.
   * 
   * @see #registerID(int)
   * @see #registerID(int, int)
   */
  public static int dofs(int id) {
    if(!map.containsKey(id))
        System.out.println("MotionEvent id: " + id + " not registered. Call MotionShortcut.registerID(id) first" );
    return map.get(id);
  }
  
  /**
   * Registers a MotionEvent {@link #id()} with the given dofs.
   * 
   * @see #registerID(int, int)
   * @see #dofs(int)
   * 
   * @param id
   *          the intended {@link #id()} to be registered.
   * @param dof
   *          Motion id degrees-of-freedom. Either 1,2,3, or 6.
   * @return the id or an exception if the id exists.
   */
  public static int registerID(int id, int dof) {
    if (map.containsKey(id)) {
        System.out.println("Nothing done! id already present in MotionShortcut. Use an id different than: "
            + (new ArrayList<Integer>(map.keySet())).toString());
    } else if (dof == 1 || dof == 2 || dof == 3 || dof == 6)
      map.put(id, dof);
    else
      System.out.println("Nothing done! dofs in MotionShortcut.registerMotionID should be either 1, 2, 3 or 6.");
    return id;
  }
  
  /**
   * Registers a MotionEvent {@link #id()} with the given dofs.
   * 
   * @see #registerID(int, int)
   * @see #dofs(int)
   * 
   * @param dof
   *          Motion id degrees-of-freedom. Either 1,2,3, or 6.
   * @return the id.
   */
  public static int registerID(int dof) {
    int key = 0;
    if (dof != 1 && dof != 2 && dof != 3 && dof != 6)
      System.out.println("Warning: Nothing done! dofs in Profile.registerMotionID should be either 1, 2, 3 or 6.");
    else {
      ArrayList<Integer> ids = new ArrayList<Integer>(map.keySet());
      if (ids.size() > 0)
        key = Collections.max(ids) + 1;
      map.put(key, dof);
    }
    return key;
  }
  
  /**
   * Constructs an "empty" shortcut by conveniently calling
   * {@code this(NO_MODIFIER_MASK, NO_ID);}
   */
  public MotionShortcut() {
    super();
  }

  /**
   * Defines a shortcut from the given gesture-id.
   * 
   * @param id
   *          gesture-id
   */
  public MotionShortcut(int id) {
    super(id);
  }

  /**
   * Defines a shortcut from the given modifier mask and gesture-id combination.
   * 
   * @param m
   *          the mask
   * @param id
   *          gesture-id
   */
  public MotionShortcut(int m, int id) {
    super(m, id);
  }

  protected MotionShortcut(MotionShortcut other) {
    super(other);
  }

  @Override
  public MotionShortcut get() {
    return new MotionShortcut(this);
  }

  @Override
  public Class<?> eventClass() {
    Class<?> clazz = MotionEvent.class;
    if((Integer)dofs(id()) != null)
      switch (dofs(id())) {
      case 1:
        clazz = DOF1Event.class;
        break;
      case 2:
        clazz = DOF2Event.class;
        break;
      case 3:
        clazz = DOF3Event.class;
        break;
      case 6:
        clazz = DOF6Event.class;
        break;
      }
    return clazz;
  }
}