/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.agent.profile;

import remixlab.bias.core.Action;
import remixlab.bias.event.shortcut.KeyboardShortcut;

/**
 * A {@link remixlab.bias.agent.profile.Profile} defining a mapping between
 * {@link remixlab.bias.event.shortcut.KeyboardShortcut}s and user-defined {@link remixlab.bias.core.Action} s.
 * 
 * @param <A>
 *          {@link remixlab.bias.core.Action} : User-defined action.
 */
public class KeyboardProfile<A extends Action<?>> extends Profile<KeyboardShortcut, A> {
	/**
	 * Defines a keyboard shortcut to bind the given action.
	 * 
	 * @param key
	 *          shortcut
	 * @param action
	 *          action to be bound
	 */
	public void setBinding(Character key, A action) {
		if (hasBinding(key)) {
			Action<?> a = action(key);
			System.out.println("Warning: overwritting shortcut which was previously bound to " + a);
		}
		setBinding(new KeyboardShortcut(key), action);
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
	 * @param key
	 *          shortcut
	 */
	public void removeBinding(Character key) {
		removeBinding(new KeyboardShortcut(key));
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
	 * @param key
	 *          shortcut
	 * @return action
	 */
	public Action<?> action(Character key) {
		return action(new KeyboardShortcut(key));
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
	 * @param key
	 *          shortcut
	 */
	public boolean hasBinding(Character key) {
		return hasBinding(new KeyboardShortcut(key));
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
