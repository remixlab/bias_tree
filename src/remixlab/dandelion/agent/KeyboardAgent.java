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
	protected AbstractScene																											scene;
	protected KeyboardBranch<SceneAction, KeyboardProfile<KeyboardSceneAction>>	keySceneBranch;
	protected KeyboardBranch<MotionAction, KeyboardProfile<KeyboardMotionAction>>	keyFrameBranch, keyEyeBranch;

	public KeyboardAgent(AbstractScene scn, String n) {
		super(scn.inputHandler(), n);
		scene = scn;
		keySceneBranch = new KeyboardBranch<SceneAction, KeyboardProfile<KeyboardSceneAction>>(
				new KeyboardProfile<KeyboardSceneAction>(),
				this,
				"scene_keyboard_branch");
		keyFrameBranch = new KeyboardBranch<MotionAction, KeyboardProfile<KeyboardMotionAction>>(
				new KeyboardProfile<KeyboardMotionAction>(),
				this,
				"frame_keyboard_branch");
		keyEyeBranch = new KeyboardBranch<MotionAction, KeyboardProfile<KeyboardMotionAction>>(
				new KeyboardProfile<KeyboardMotionAction>(),
				this,
				"eye_keyboard_branch");
		// new, mimics eye -> motionAgent -> scene -> keyAgent
		// addGrabber(scene);
		// addGrabber(scene.eye().frame());
		resetDefaultGrabber();
		setDefaultGrabber(scene);
		setDefaultShortcuts();
	}

	/*
	 * @Override public boolean addGrabber(Grabber frame) { if(frame instanceof AbstractScene) return addGrabber(scene,
	 * keySceneBranch); if (frame instanceof InteractiveFrame) return addGrabber((InteractiveFrame) frame,
	 * ((InteractiveFrame) frame).isEyeFrame() ? keyEyeBranch : keyFrameBranch); if (!(frame instanceof
	 * InteractiveGrabber)) return super.addGrabber(frame); return false; }
	 */

	// TODO debug
	@Override
	public boolean addGrabber(Grabber frame) {
		if (frame instanceof AbstractScene)
			return addGrabber(scene, keySceneBranch);
		if (frame instanceof InteractiveFrame) {
			if (((InteractiveFrame) frame).isEyeFrame())
				System.out.println("adding EYE frame in keyboard");
			else
				System.out.println("adding FRAME frame in keyboard");
			return addGrabber((InteractiveFrame) frame, ((InteractiveFrame) frame).isEyeFrame() ? keyEyeBranch
					: keyFrameBranch);
		}
		if (!(frame instanceof InteractiveGrabber))
			return super.addGrabber(frame);
		return false;
	}

	/**
	 * Returns the scene this object belongs to
	 */
	public AbstractScene scene() {
		return scene;
	}

	@Override
	public void resetDefaultGrabber() {
		addGrabber(scene);
		setDefaultGrabber(scene);
	}

	public KeyboardBranch<SceneAction, KeyboardProfile<KeyboardSceneAction>> sceneBranch() {
		return keySceneBranch;
	}

	public KeyboardBranch<MotionAction, KeyboardProfile<KeyboardMotionAction>> eyeBranch() {
		return keyEyeBranch;
	}

	public KeyboardBranch<MotionAction, KeyboardProfile<KeyboardMotionAction>> frameBranch() {
		return keyFrameBranch;
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

	protected KeyboardProfile<KeyboardSceneAction> sceneProfile() {
		return sceneBranch().keyboardProfile();
	}

	protected KeyboardProfile<KeyboardMotionAction> eyeProfile() {
		return eyeBranch().keyboardProfile();
	}

	protected KeyboardProfile<KeyboardMotionAction> frameProfile() {
		return frameBranch().keyboardProfile();
	}

	protected KeyboardProfile<KeyboardMotionAction> motionProfile(Target target) {
		return target == Target.EYE ? eyeProfile() : frameProfile();
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
		sceneProfile().removeBindings();
		sceneProfile().setBinding('a', KeyboardSceneAction.TOGGLE_AXES_VISUAL_HINT);
		sceneProfile().setBinding('f', KeyboardSceneAction.TOGGLE_PICKING_VISUAL_HINT);
		sceneProfile().setBinding('g', KeyboardSceneAction.TOGGLE_GRID_VISUAL_HINT);
		sceneProfile().setBinding('m', KeyboardSceneAction.TOGGLE_ANIMATION);

		sceneProfile().setBinding('e', KeyboardSceneAction.TOGGLE_CAMERA_TYPE);
		sceneProfile().setBinding('h', KeyboardSceneAction.DISPLAY_INFO);
		sceneProfile().setBinding('r', KeyboardSceneAction.TOGGLE_PATHS_VISUAL_HINT);

		sceneProfile().setBinding('s', KeyboardSceneAction.INTERPOLATE_TO_FIT);
		sceneProfile().setBinding('S', KeyboardSceneAction.SHOW_ALL);

		// TODO add some eye and frame defs
		removeShortcuts(Target.EYE);
		removeShortcuts(Target.FRAME);
		setShortcut(Target.EYE, 'c', KeyboardMotionAction.ALIGN_FRAME);
		setShortcut(Target.EYE, 'a', KeyboardMotionAction.CENTER_FRAME);
		setShortcut(Target.FRAME, 'a', KeyboardMotionAction.ALIGN_FRAME);
		setShortcut(Target.FRAME, 'c', KeyboardMotionAction.CENTER_FRAME);
	}

	/**
	 * Sets the default (virtual) key to play eye paths.
	 */
	public void setKeyCodeToPlayPath(int vkey, int path) {
		switch (path) {
		case 1:
			sceneProfile().setBinding(BogusEvent.NO_MODIFIER_MASK, vkey, KeyboardSceneAction.PLAY_PATH_1);
			break;
		case 2:
			sceneProfile().setBinding(BogusEvent.NO_MODIFIER_MASK, vkey, KeyboardSceneAction.PLAY_PATH_2);
			break;
		case 3:
			sceneProfile().setBinding(BogusEvent.NO_MODIFIER_MASK, vkey, KeyboardSceneAction.PLAY_PATH_3);
			break;
		default:
			break;
		}
	}

	/**
	 * Binds the key shortcut to the (Keyboard) dandelion action.
	 */
	public void setShortcut(Character key, KeyboardSceneAction action) {
		sceneProfile().setBinding(key, action);
	}

	/**
	 * Binds the mask-vKey (virtual key) shortcut to the (Keyboard) dandelion action.
	 */
	public void setShortcut(int mask, int vKey, KeyboardSceneAction action) {
		sceneProfile().setBinding(mask, vKey, action);
	}

	/**
	 * Removes key shortcut binding (if present).
	 */
	public void removeShortcut(Character key) {
		sceneProfile().removeBinding(key);
	}

	/**
	 * Removes mask-vKey (virtual key) shortcut binding (if present).
	 */
	public void removeShortcut(int mask, int vKey) {
		sceneProfile().removeBinding(mask, vKey);
	}

	/**
	 * Removes all shortcut bindings.
	 */
	public void removeShortcuts() {
		sceneProfile().removeBindings();
	}

	/**
	 * Returns {@code true} if the key shortcut is bound to a (Keyboard) dandelion action.
	 */
	public boolean hasShortcut(Character key) {
		return sceneProfile().hasBinding(key);
	}

	/**
	 * Returns {@code true} if the mask-vKey (virtual key) shortcut is bound to a (Keyboard) dandelion action.
	 */
	public boolean hasShortcut(int mask, int vKey) {
		return sceneProfile().hasBinding(mask, vKey);
	}

	/**
	 * Returns {@code true} if the keyboard action is bound.
	 */
	public boolean isActionBound(KeyboardSceneAction action) {
		return sceneProfile().isActionBound(action);
	}

	/**
	 * Returns the (Keyboard) dandelion action that is bound to the given key shortcut. Returns {@code null} if no action
	 * is bound to the given shortcut.
	 */
	public KeyboardSceneAction action(Character key) {
		return (KeyboardSceneAction) sceneProfile().action(key);
	}

	/**
	 * Returns the (Keyboard) dandelion action that is bound to the given mask-vKey (virtual key) shortcut. Returns
	 * {@code null} if no action is bound to the given shortcut.
	 */
	public KeyboardSceneAction action(int mask, int vKey) {
		return (KeyboardSceneAction) sceneProfile().action(mask, vKey);
	}

	// FRAMEs
	// TODO DOCs are broken (since they were copied/pasted from above)

	/**
	 * Binds the key shortcut to the (Keyboard) dandelion action.
	 */
	public void setShortcut(Target target, Character key, KeyboardMotionAction action) {
		motionProfile(target).setBinding(key, action);
	}

	/**
	 * Binds the mask-vKey (virtual key) shortcut to the (Keyboard) dandelion action.
	 */
	public void setShortcut(Target target, int mask, int vKey, KeyboardMotionAction action) {
		motionProfile(target).setBinding(mask, vKey, action);
	}

	/**
	 * Removes key shortcut binding (if present).
	 */
	public void removeShortcut(Target target, Character key) {
		motionProfile(target).removeBinding(key);
	}

	/**
	 * Removes mask-vKey (virtual key) shortcut binding (if present).
	 */
	public void removeShortcut(Target target, int mask, int vKey) {
		motionProfile(target).removeBinding(mask, vKey);
	}

	/**
	 * Removes all shortcut bindings.
	 */
	public void removeShortcuts(Target target) {
		motionProfile(target).removeBindings();
	}

	/**
	 * Returns {@code true} if the key shortcut is bound to a (Keyboard) dandelion action.
	 */
	public boolean hasShortcut(Target target, Character key) {
		return motionProfile(target).hasBinding(key);
	}

	/**
	 * Returns {@code true} if the mask-vKey (virtual key) shortcut is bound to a (Keyboard) dandelion action.
	 */
	public boolean hasShortcut(Target target, int mask, int vKey) {
		return motionProfile(target).hasBinding(mask, vKey);
	}

	/**
	 * Returns {@code true} if the keyboard action is bound.
	 */
	public boolean isActionBound(Target target, KeyboardMotionAction action) {
		return motionProfile(target).isActionBound(action);
	}

	/**
	 * Returns the (Keyboard) dandelion action that is bound to the given key shortcut. Returns {@code null} if no action
	 * is bound to the given shortcut.
	 */
	public KeyboardMotionAction action(Target target, Character key) {
		return (KeyboardMotionAction) motionProfile(target).action(key);
	}

	/**
	 * Returns the (Keyboard) dandelion action that is bound to the given mask-vKey (virtual key) shortcut. Returns
	 * {@code null} if no action is bound to the given shortcut.
	 */
	public KeyboardMotionAction action(Target target, int mask, int vKey) {
		return (KeyboardMotionAction) motionProfile(target).action(mask, vKey);
	}
}
