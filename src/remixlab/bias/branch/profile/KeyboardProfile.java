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
import remixlab.bias.core.BogusEvent;
import remixlab.bias.event.shortcut.KeyboardShortcut;

/**
 * A {@link remixlab.bias.branch.profile.Profile} defining a mapping between
 * {@link remixlab.bias.event.shortcut.KeyboardShortcut}s and user-defined {@link remixlab.bias.core.Action} s.
 * 
 * @param <A>
 *          {@link remixlab.bias.core.Action} : User-defined action.
 */
public class KeyboardProfile<A extends Action<?>> extends Profile<KeyboardShortcut, A> {
	public KeyboardProfile() {
		super();
	}

	protected KeyboardProfile(KeyboardProfile<A> other) {
		super(other);
	}

	/**
	 * Returns a deep-copy of this profile.
	 */
	@Override
	public KeyboardProfile<A> get() {
		return new KeyboardProfile<A>(this);
	}

	/**
	 * Defines a keyboard shortcut to bind the given action.
	 * 
	 * @param key
	 *          shortcut
	 * @param action
	 *          action to be bound
	 */
	public void setBinding(Integer vKey, A action) {
		if (hasBinding(vKey)) {
			Action<?> a = action(vKey);
			System.out.println("Warning: overwritting shortcut which was previously bound to " + a);
		}
		setBinding(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey), action);
	}

	/**
	 * Defines a keyboard shortcut to bind the given action.
	 * 
	 * @param mask
	 *          modifier mask defining the shortcut
	 * @param vKey
	 *          coded key defining the shortcut
	 * @param action
	 *          action to be bound
	 */
	public void setBinding(Integer mask, Integer vKey, A action) {
		if (hasBinding(mask, vKey)) {
			Action<?> a = action(mask, vKey);
			System.out.println("Warning: overwritting shortcut which was previously bound to " + a);
		}
		setBinding(new KeyboardShortcut(mask, vKey), action);
	}

	/**
	 * Removes the keyboard shortcut.
	 * 
	 * @param vKey
	 *          shortcut
	 */
	public void removeBinding(Integer vKey) {
		removeBinding(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey));
	}

	/**
	 * Removes the keyboard shortcut.
	 * 
	 * @param mask
	 *          modifier mask that defining the shortcut
	 * @param vKey
	 *          coded key defining the shortcut
	 */
	public void removeBinding(Integer mask, Integer vKey) {
		removeBinding(new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Returns the action that is bound to the given keyboard shortcut.
	 * 
	 * @param vKey
	 *          shortcut
	 * @return action
	 */
	public Action<?> action(Integer vKey) {
		return action(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey));
	}

	/**
	 * Returns the action that is bound to the given keyboard shortcut.
	 * 
	 * @param mask
	 *          modifier mask defining the shortcut
	 * @param vKey
	 *          coded key defining the shortcut
	 * @return action
	 */
	public Action<?> action(Integer mask, Integer vKey) {
		return action(new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Returns true if the given keyboard shortcut binds an action.
	 * 
	 * @param vKey
	 *          shortcut
	 */
	public boolean hasBinding(Integer vKey) {
		return hasBinding(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey));
	}

	/**
	 * Returns true if the given keyboard shortcut binds an action.
	 * 
	 * @param mask
	 *          modifier mask defining the shortcut
	 * @param vKey
	 *          coded key defining the shortcut
	 */
	public boolean hasBinding(Integer mask, Integer vKey) {
		return hasBinding(new KeyboardShortcut(mask, vKey));
	}
}
