/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.branch;

import remixlab.bias.event.shortcut.*;
import remixlab.bias.core.*;

/**
 * This class is provided purely for symmetry and style reasons against the events and shortcuts API. Only needed if you
 * plan to implement your own KeyboardAgent.
 * 
 * @param <K>
 *          The {@link remixlab.bias.branch.profile.KeyboardProfile} to parameterize this Agent with.
 */
public class KeyboardBranch<E extends Enum<E>, A extends Action<E>> extends Branch<E, A, KeyboardShortcut> {
	public KeyboardBranch(Agent pnt, String n) {
		super(pnt, n);
	}

	protected KeyboardBranch(KeyboardBranch<E, A> other) {
		super(other);
	}

	@Override
	public KeyboardBranch<E, A> get() {
		return new KeyboardBranch<E, A>(this);
	}
	
	// high-level api (wrappers around the profile): from here nor really needed
	
	/**
	 * Removes all shortcut bindings.
	 */
	public void removeBindings() {
		profile().removeBindings();
	}

	/**
	 * Returns {@code true} if the keyboard action is bound.
	 */
	public boolean isActionBound(A action) {
		return profile().isActionBound(action);
	}
	
	/**
	 * Binds the vKey (virtual key) shortcut to the (Keyboard) action.
	 */
	public void setBinding(int vKey, A action) {
		profile().setBinding(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey), action);
	}

	/**
	 * Binds the mask-vKey (virtual key) shortcut to the (Keyboard) action.
	 */
	public void setBinding(int mask, int vKey, A action) {
		profile().setBinding(new KeyboardShortcut(mask, vKey), action);
	}

	/**
	 * Removes mask-vKey (virtual key) shortcut binding (if present).
	 */
	public void removeBinding(int vKey) {
		profile().removeBinding(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey));
	}

	/**
	 * Removes mask-vKey (virtual key) shortcut binding (if present).
	 */
	public void removeBinding(int mask, int vKey) {
		profile().removeBinding(new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Returns {@code true} if the vKey (virtual key) shortcut is bound to a (Keyboard) action.
	 */
	public boolean hasBinding(int vKey) {
		return profile().hasBinding(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey));
	}

	/**
	 * Returns {@code true} if the mask-vKey (virtual key) shortcut is bound to a (Keyboard) action.
	 */
	public boolean hasBinding(int mask, int vKey) {
		return profile().hasBinding(new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Returns the (Keyboard) action that is bound to the given vKey (virtual key) shortcut. Returns
	 * {@code null} if no action is bound to the given shortcut.
	 */
	public A action(int vKey) {
		return profile().action(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey));
	}

	/**
	 * Returns the (Keyboard) action that is bound to the given mask-vKey (virtual key) shortcut. Returns
	 * {@code null} if no action is bound to the given shortcut.
	 */
	public A action(int mask, int vKey) {
		return profile().action(new KeyboardShortcut(mask, vKey));
	}
	
	/**
	 * Binds the key shortcut to the (Keyboard) action.
	 */
	public void setBinding(char key, A action) {
		setBinding(agent.keyCode(key), action);
	}

	/**
	 * Binds the key shortcut to the (Keyboard) action.
	 */
	public void setBinding(int mask, char key, A action) {
		setBinding(mask, agent.keyCode(key), action);
	}

	/**
	 * Removes key shortcut binding (if present).
	 */
	public void removeBinding(char key) {
		removeBinding(agent.keyCode(key));
	}

	public void removeBinding(int mask, char key) {
		removeBinding(mask, agent.keyCode(key));
	}

	/**
	 * Returns {@code true} if the key shortcut is bound to a (Keyboard) action.
	 */
	public boolean hasBinding(char key) {
		return hasBinding(agent.keyCode(key));
	}

	public boolean hasBinding(int mask, char key) {
		return hasBinding(mask, agent.keyCode(key));
	}

	/**
	 * Returns the (Keyboard) action that is bound to the given key shortcut. Returns {@code null} if no action
	 * is bound to the given shortcut.
	 */
	public A action(char key) {
		return action(agent.keyCode(key));
	}

	public A action(int mask, char key) {
		return action(mask, agent.keyCode(key));
	}
}