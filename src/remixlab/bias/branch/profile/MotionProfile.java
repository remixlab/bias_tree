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
	/**
	 * Returns true if the given binding binds an action.
	 */
	public boolean hasBinding() {
		return hasBinding(MotionEvent.NO_MODIFIER_MASK, MotionEvent.NO_ID);
	}

	/**
	 * Returns true if the given binding binds an action.
	 * 
	 * @param button
	 */
	public boolean hasBinding(Integer button) {
		return hasBinding(MotionEvent.NO_MODIFIER_MASK, button);
	}

	/**
	 * Returns true if the given binding binds an action.
	 * 
	 * @param mask
	 * @param button
	 */
	public boolean hasBinding(Integer mask, Integer button) {
		return hasBinding(new MotionShortcut(mask, button));
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
	 * @param button
	 * @param action
	 */
	public void setBinding(Integer mask, Integer button, A action) {
		if (hasBinding(mask, button)) {
			Action<?> a = action(mask, button);
			System.out.println("Warning: overwritting binding which was previously associated to " + a);
		}
		setBinding(new MotionShortcut(mask, button), action);
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
	 * @param button
	 */
	public void removeBinding(Integer button) {
		removeBinding(MotionEvent.NO_MODIFIER_MASK, button);
	}

	/**
	 * Removes the action binding.
	 * 
	 * @param mask
	 * @param button
	 */
	public void removeBinding(Integer mask, Integer button) {
		removeBinding(new MotionShortcut(mask, button));
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
	 * @param button
	 */
	public Action<?> action(Integer button) {
		return action(MotionEvent.NO_MODIFIER_MASK, button);
	}

	/**
	 * Returns the action associated to the given binding.
	 * 
	 * @param mask
	 * @param button
	 */
	public Action<?> action(Integer mask, Integer button) {
		return action(new MotionShortcut(mask, button));
	}
}
