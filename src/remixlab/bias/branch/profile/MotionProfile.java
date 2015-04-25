/*********************************************************************************
 * bias_tree 
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.branch.profile;

import remixlab.bias.core.Action;
import remixlab.bias.event.MotionEvent;
import remixlab.bias.event.shortcut.*;

/**
 * A {@link remixlab.bias.branch.profile.Profile} defining a mapping between
 * {@link remixlab.bias.event.shortcut.MotionShortcut}s and user-defined {@link remixlab.bias.core.Action}s.
 * 
 * @param <A>
 *          {@link remixlab.bias.core.Action} : User-defined action.
 */
public class MotionProfile<A extends Action<?>> extends Profile<MotionShortcut, A> {
	public MotionProfile() {
		super();
	}
	
	protected MotionProfile(MotionProfile<A> other) {
		super(other);
	}
	
	/**
	 * Returns a deep-copy of this profile.
	 */
	@Override
	public MotionProfile<A> get() {
		return new MotionProfile<A>(this);
	}
	
	/**
	 * Returns true if the given binding binds an action.
	 */
	public boolean hasBinding() {
		return hasBinding(MotionEvent.NO_MODIFIER_MASK, MotionEvent.NO_ID);
	}

	/**
	 * Returns true if the given binding binds an action.
	 * 
	 * @param id
	 */
	public boolean hasBinding(Integer id) {
		return hasBinding(MotionEvent.NO_MODIFIER_MASK, id);
	}

	/**
	 * Returns true if the given binding binds an action.
	 * 
	 * @param mask
	 * @param id
	 */
	public boolean hasBinding(Integer mask, Integer id) {
		return hasBinding(new MotionShortcut(mask, id));
	}

	/**
	 * Convenience function that simply calls {@code setWheelShortcut(0, action)}.
	 */
	public void setBinding(A action) {
		setBinding(MotionEvent.NO_ID, action);
	}

	/**
	 * Binds the action to the given binding
	 * 
	 * @param button
	 * @param action
	 */
	public void setBinding(Integer button, A action) {
		setBinding(MotionEvent.NO_MODIFIER_MASK, button, action);
	}

	/**
	 * Binds the action to the given binding
	 * 
	 * @param mask
	 * @param id
	 * @param action
	 */
	public void setBinding(Integer mask, Integer id, A action) {
		if (hasBinding(mask, id)) {
			Action<?> a = action(mask, id);
			System.out.println("Warning: overwritting binding which was previously associated to " + a);
		}
		setBinding(new MotionShortcut(mask, id), action);
	}

	/**
	 * Convenience function that simply calls {@code removeWheelShortcut(0)}.
	 */
	public void removeBinding() {
		removeBinding(MotionEvent.NO_MODIFIER_MASK, MotionEvent.NO_ID);
	}

	/**
	 * Removes the action binding.
	 * 
	 * @param id
	 */
	public void removeBinding(Integer id) {
		removeBinding(MotionEvent.NO_MODIFIER_MASK, id);
	}

	/**
	 * Removes the action binding.
	 * 
	 * @param mask
	 * @param id
	 */
	public void removeBinding(Integer mask, Integer id) {
		removeBinding(new MotionShortcut(mask, id));
	}

	/**
	 * Returns the action associated to the given binding.
	 */
	public Action<?> action() {
		return action(MotionEvent.NO_MODIFIER_MASK, MotionEvent.NO_ID);
	}

	/**
	 * Returns the action associated to the given binding.
	 * 
	 * @param id
	 */
	public Action<?> action(Integer id) {
		return action(MotionEvent.NO_MODIFIER_MASK, id);
	}

	/**
	 * Returns the action associated to the given binding.
	 * 
	 * @param mask
	 * @param id
	 */
	public Action<?> action(Integer mask, Integer id) {
		return action(new MotionShortcut(mask, id));
	}
}
