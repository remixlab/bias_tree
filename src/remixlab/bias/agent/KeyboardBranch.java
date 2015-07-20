/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.agent;

import remixlab.bias.event.*;
import remixlab.bias.core.*;

/**
 * A {@link remixlab.bias.core.Branch} with a single {@link #keyboardProfile()}, defining
 * some {@link remixlab.bias.event.KeyboardShortcut} / (keyboard) action mappings. Keyboard
 * branches may be appended only to an {@link remixlab.bias.agent.AbstractKeyboardAgent}. See
 * {@link remixlab.bias.agent.AbstractKeyboardAgent#appendBranch(String)}.
 * <p>
 * <b>Note</b> that all the methods provided here are simply wrappers to the {@link #keyboardProfile()}.
 * See {@link remixlab.bias.core.Profile}.
 *
 * @param <E> Reference action enum.
 * 
 * @param <A> Action enum sub-group.
 */
public class KeyboardBranch<E extends Enum<E>, A extends Action<E>> extends Branch<E> {
	AbstractKeyboardAgent keyAgent;
	protected Profile<E, KeyboardShortcut, A> keyProfile;
	protected KeyboardBranch(AbstractKeyboardAgent a, String n) {
		super(a, n);
		keyAgent = a;
		keyProfile = new Profile<E, KeyboardShortcut, A>();
		profiles().add(keyProfile);
	}

	protected KeyboardBranch(KeyboardBranch<E, A> other) {
		super(other);
		profiles.clear();
		keyProfile = other.keyboardProfile().get();
		profiles.add(keyProfile);
	}

	@Override
	public KeyboardBranch<E, A> get() {
		return new KeyboardBranch<E, A>(this);
	}
	
	/**
	 * @return Keyboard {@link remixlab.bias.core.Profile}.
	 */
	public Profile<E, KeyboardShortcut, A> keyboardProfile() {
		return keyProfile;
	}
	
	// high-level api (wrappers around the profile): from here nor really needed
	
	/**
	 * Removes all bindings from the {@link #keyboardProfile()}.
	 */
	public void removeBindings() {
		keyboardProfile().removeBindings();
	}

	/**
	 * Returns {@code true} if the keyboard action is bound.
	 */
	public boolean isActionBound(A action) {
		return keyboardProfile().isActionBound(action);
	}
	
	/**
	 * Binds the vKey (virtual key) shortcut to the (Keyboard) action.
	 */
	public void setBinding(int vKey, A action) {
		keyboardProfile().setBinding(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey), action);
	}

	/**
	 * Binds the mask-vKey (virtual key) shortcut to the (Keyboard) action.
	 */
	public void setBinding(int mask, int vKey, A action) {
		keyboardProfile().setBinding(new KeyboardShortcut(mask, vKey), action);
	}

	/**
	 * Removes mask-vKey (virtual key) shortcut binding (if present).
	 */
	public void removeBinding(int vKey) {
		keyboardProfile().removeBinding(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey));
	}

	/**
	 * Removes mask-vKey (virtual key) binding (if present).
	 */
	public void removeBinding(int mask, int vKey) {
		keyboardProfile().removeBinding(new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Returns {@code true} if the vKey (virtual key) binds a (Keyboard) action.
	 */
	public boolean hasBinding(int vKey) {
		return keyboardProfile().hasBinding(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey));
	}

	/**
	 * Returns {@code true} if the mask-vKey (virtual key) is bound to a (Keyboard) action.
	 */
	public boolean hasBinding(int mask, int vKey) {
		return keyboardProfile().hasBinding(new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Returns the (Keyboard) action that is bound to the given vKey (virtual key). Returns
	 * {@code null} if no action is bound to the given shortcut virtual key.
	 */
	public A action(int vKey) {
		return keyboardProfile().action(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey));
	}

	/**
	 * Returns the (Keyboard) action that is bound to the given mask-vKey (virtual key). Returns
	 * {@code null} if no action is bound to the given virtual key.
	 */
	public A action(int mask, int vKey) {
		return keyboardProfile().action(new KeyboardShortcut(mask, vKey));
	}
	
	/**
	 * Binds the key to the (Keyboard) action.
	 */
	public void setBinding(char key, A action) {
		for(Branch<?> branch : keyAgent.branches())
			if(branch instanceof KeyboardBranch)
				if(branch != this)
					if(((KeyboardBranch<?, ?>)branch).hasBinding(key))
						System.out.println("Warning: KeyboardShortcut already bound to " + ((KeyboardBranch<?, ?>)branch).action(key) + " in " + branch.name());
		setBinding(keyAgent.keyCode(key), action);
	}

	/**
	 * Binds the key to the (Keyboard) action.
	 */
	public void setBinding(int mask, char key, A action) {
		for(Branch<?> branch : keyAgent.branches())
			if(branch instanceof KeyboardBranch)
				if(branch != this)
					if(((KeyboardBranch<?, ?>)branch).hasBinding(mask, key))
						System.out.println("Warning: KeyboardShortcut already bound to " + ((KeyboardBranch<?, ?>)branch).action(mask, key) + " in " + branch.name());
		setBinding(mask, keyAgent.keyCode(key), action);
	}

	/**
	 * Removes the key binding (if present).
	 */
	public void removeBinding(char key) {
		removeBinding(keyAgent.keyCode(key));
	}

	/**
	 * Removes the mask-key binding (if present).
	 */
	public void removeBinding(int mask, char key) {
		removeBinding(mask, keyAgent.keyCode(key));
	}

	/**
	 * Returns {@code true} if the key binds a (Keyboard) action.
	 */
	public boolean hasBinding(char key) {
		return hasBinding(keyAgent.keyCode(key));
	}

	/**
	 * Returns {@code true} if the mask-key binds a (Keyboard) action.
	 */
	public boolean hasBinding(int mask, char key) {
		return hasBinding(mask, keyAgent.keyCode(key));
	}

	/**
	 * Returns the (Keyboard) action that is bound to the given key. Returns {@code null} if no action
	 * is bound.
	 */
	public A action(char key) {
		return action(keyAgent.keyCode(key));
	}

	/**
	 * Returns the (Keyboard) action that is bound to the given mask-key. Returns {@code null} if no action
	 * is bound.
	 */
	public A action(int mask, char key) {
		return action(mask, keyAgent.keyCode(key));
	}
}