/*********************************************************************************
 * dandelion_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.dandelion.agent;

import remixlab.bias.branch.*;
import remixlab.bias.branch.profile.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

public class KeyboardAgent extends Agent {
	protected AbstractScene																									scene;
	protected KeyboardBranch<SceneAction, KeyboardProfile<KeyboardAction>>	keyBranch;

	public KeyboardAgent(AbstractScene scn, String n) {
		super(scn.inputHandler(), n);
		scene = scn;
		keyBranch = new KeyboardBranch<SceneAction, KeyboardProfile<KeyboardAction>>(new KeyboardProfile<KeyboardAction>(),
				this,
				"scene_keyboard_agent");
		// new, mimics eye -> motionAgent -> scene -> keyAgent
		addGrabber(scene, keyBranch);
		setDefaultGrabber(scene);
		setDefaultShortcuts();
	}

	@Override
	public void resetDefaultGrabber() {
		addGrabber(scene, keyBranch);
		setDefaultGrabber(scene);
	}

	/*
	 * public <K extends KeyboardProfile<?>> void addBranch(K k, String n) { ActionKeyboardAgent<K> branch = new
	 * ActionKeyboardAgent<K>(k, this, n); //System.out.println("ActionInputMotionAgent add branch: " +
	 * actionAgent.name()); if (!brnchs.contains(branch)) { this.brnchs.add(0, branch); } }
	 */

	public KeyboardBranch<SceneAction, KeyboardProfile<KeyboardAction>> sceneBranch() {
		return keyBranch;
	}

	@Override
	public KeyboardEvent feed() {
		return null;
	}

	@Override
	public boolean appendBranch(Branch<?, ?> branch) {
		if (branch instanceof KeyboardBranch)
			return super.appendBranch(branch);
		else {
			System.out.println("Branch should be instanceof KeyboardBranch to be appended");
			return false;
		}
	}

	protected KeyboardProfile<KeyboardAction> keyboardProfile() {
		return sceneBranch().keyboardProfile();
	}

	/**
	 * Set the default keyboard shortcuts as follows:
	 * <p>
	 * {@code 'a' -> KeyboardAction.TOGGLE_AXIS_VISUAL_HINT}<br>
	 * {@code 'f' -> KeyboardAction.TOGGLE_FRAME_VISUAL_HINT}<br>
	 * {@code 'g' -> KeyboardAction.TOGGLE_GRID_VISUAL_HINT}<br>
	 * {@code 'm' -> KeyboardAction.TOGGLE_ANIMATION}<br>
	 * {@code 'e' -> KeyboardAction.TOGGLE_CAMERA_TYPE}<br>
	 * {@code 'h' -> KeyboardAction.DISPLAY_INFO}<br>
	 * {@code 'r' -> KeyboardAction.TOGGLE_PATHS_VISUAL_HINT}<br>
	 * {@code 's' -> KeyboardAction.INTERPOLATE_TO_FIT}<br>
	 * {@code 'S' -> KeyboardAction.SHOW_ALL}<br>
	 */
	public void setDefaultShortcuts() {
		keyboardProfile().removeBindings();
		keyboardProfile().setBinding('a', KeyboardAction.TOGGLE_AXES_VISUAL_HINT);
		keyboardProfile().setBinding('f', KeyboardAction.TOGGLE_PICKING_VISUAL_HINT);
		keyboardProfile().setBinding('g', KeyboardAction.TOGGLE_GRID_VISUAL_HINT);
		keyboardProfile().setBinding('m', KeyboardAction.TOGGLE_ANIMATION);

		keyboardProfile().setBinding('e', KeyboardAction.TOGGLE_CAMERA_TYPE);
		keyboardProfile().setBinding('h', KeyboardAction.DISPLAY_INFO);
		keyboardProfile().setBinding('r', KeyboardAction.TOGGLE_PATHS_VISUAL_HINT);

		keyboardProfile().setBinding('s', KeyboardAction.INTERPOLATE_TO_FIT);
		keyboardProfile().setBinding('S', KeyboardAction.SHOW_ALL);
	}

	/**
	 * Sets the default (virtual) key to play eye paths.
	 */
	public void setKeyCodeToPlayPath(int vkey, int path) {
		switch (path) {
		case 1:
			keyboardProfile().setBinding(BogusEvent.NO_MODIFIER_MASK, vkey, KeyboardAction.PLAY_PATH_1);
			break;
		case 2:
			keyboardProfile().setBinding(BogusEvent.NO_MODIFIER_MASK, vkey, KeyboardAction.PLAY_PATH_2);
			break;
		case 3:
			keyboardProfile().setBinding(BogusEvent.NO_MODIFIER_MASK, vkey, KeyboardAction.PLAY_PATH_3);
			break;
		default:
			break;
		}
	}

	/**
	 * Binds the key shortcut to the (Keyboard) dandelion action.
	 */
	public void setShortcut(Character key, KeyboardAction action) {
		keyboardProfile().setBinding(key, action);
	}

	/**
	 * Binds the mask-vKey (virtual key) shortcut to the (Keyboard) dandelion action.
	 */
	public void setShortcut(int mask, int vKey, KeyboardAction action) {
		keyboardProfile().setBinding(mask, vKey, action);
	}

	/**
	 * Removes key shortcut binding (if present).
	 */
	public void removeShortcut(Character key) {
		keyboardProfile().removeBinding(key);
	}

	/**
	 * Removes mask-vKey (virtual key) shortcut binding (if present).
	 */
	public void removeShortcut(int mask, int vKey) {
		keyboardProfile().removeBinding(mask, vKey);
	}

	/**
	 * Removes all shortcut bindings.
	 */
	public void removeShortcuts() {
		keyboardProfile().removeBindings();
	}

	/**
	 * Returns {@code true} if the key shortcut is bound to a (Keyboard) dandelion action.
	 */
	public boolean hasShortcut(Character key) {
		return keyboardProfile().hasBinding(key);
	}

	/**
	 * Returns {@code true} if the mask-vKey (virtual key) shortcut is bound to a (Keyboard) dandelion action.
	 */
	public boolean hasShortcut(int mask, int vKey) {
		return keyboardProfile().hasBinding(mask, vKey);
	}

	/**
	 * Returns {@code true} if the keyboard action is bound.
	 */
	public boolean isActionBound(KeyboardAction action) {
		return keyboardProfile().isActionBound(action);
	}

	/**
	 * Returns the (Keyboard) dandelion action that is bound to the given key shortcut. Returns {@code null} if no action
	 * is bound to the given shortcut.
	 */
	public KeyboardAction action(Character key) {
		return (KeyboardAction) keyboardProfile().action(key);
	}

	/**
	 * Returns the (Keyboard) dandelion action that is bound to the given mask-vKey (virtual key) shortcut. Returns
	 * {@code null} if no action is bound to the given shortcut.
	 */
	public KeyboardAction action(int mask, int vKey) {
		return (KeyboardAction) keyboardProfile().action(mask, vKey);
	}
}
