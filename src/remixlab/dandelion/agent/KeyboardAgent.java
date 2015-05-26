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
import remixlab.bias.event.shortcut.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

public class KeyboardAgent extends Agent {
	protected AbstractScene															scene;
	protected KeyboardBranch<GlobalAction, SceneAction>	keySceneBranch;
	protected KeyboardBranch<MotionAction, KeyboardAction>	keyFrameBranch, keyEyeBranch;

	public KeyboardAgent(AbstractScene scn, String n) {
		super(scn.inputHandler(), n);
		scene = scn;
		keySceneBranch = new KeyboardBranch<GlobalAction, SceneAction>(this, "scene_keyboard_branch");
		keyFrameBranch = new KeyboardBranch<MotionAction, KeyboardAction>(this, "frame_keyboard_branch");
		keyEyeBranch = new KeyboardBranch<MotionAction, KeyboardAction>(this, "eye_keyboard_branch");
		// new, mimics eye -> motionAgent -> scene -> keyAgent
		// addGrabber(scene);
		// addGrabber(scene.eye().frame());
		resetDefaultGrabber();
		setDefaultGrabber(scene);
		setDefaultBindings();
	}

	@Override
	public boolean addGrabber(Grabber frame) {
		if (frame instanceof AbstractScene)
			return addGrabber(scene, keySceneBranch);
		if (frame instanceof InteractiveFrame)
			return addGrabber((InteractiveFrame) frame, ((InteractiveFrame) frame).isEyeFrame() ? keyEyeBranch
					: keyFrameBranch);
		if (!(frame instanceof InteractiveGrabber))
			return super.addGrabber(frame);
		return false;
	}

	/*
	 * // TODO debug
	 * 
	 * @Override public boolean addGrabber(Grabber frame) { if (frame instanceof AbstractScene) return addGrabber(scene,
	 * keySceneBranch); if (frame instanceof InteractiveFrame) { if (((InteractiveFrame) frame).isEyeFrame())
	 * System.out.println("adding EYE frame in keyboard"); else System.out.println("adding FRAME frame in keyboard");
	 * return addGrabber((InteractiveFrame) frame, ((InteractiveFrame) frame).isEyeFrame() ? keyEyeBranch :
	 * keyFrameBranch); } if (!(frame instanceof InteractiveGrabber)) return super.addGrabber(frame); return false; }
	 */

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

	public KeyboardBranch<GlobalAction, SceneAction> sceneBranch() {
		return keySceneBranch;
	}

	public KeyboardBranch<MotionAction, KeyboardAction> eyeBranch() {
		return keyEyeBranch;
	}

	public KeyboardBranch<MotionAction, KeyboardAction> frameBranch() {
		return keyFrameBranch;
	}

	@Override
	public KeyboardEvent feed() {
		return null;
	}

	@Override
	public boolean appendBranch(Branch<?, ?, ?> branch) {
		if (branch instanceof KeyboardBranch)
			return super.appendBranch(branch);
		else {
			System.out.println("Branch should be instanceof KeyboardBranch to be appended");
			return false;
		}
	}

	protected Profile<KeyboardShortcut, SceneAction> sceneProfile() {
		return sceneBranch().keyboardProfile();
	}

	protected Profile<KeyboardShortcut, KeyboardAction> eyeProfile() {
		return eyeBranch().keyboardProfile();
	}

	protected Profile<KeyboardShortcut, KeyboardAction> frameProfile() {
		return frameBranch().keyboardProfile();
	}

	protected Profile<KeyboardShortcut, KeyboardAction> motionProfile(Target target) {
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
	 * <p>
	 * {@code left_arrow -> KeyboardAction.MOVE_LEFT}<br>
	 * {@code right_arrow -> KeyboardAction.MOVE_RIGHT}<br>
	 * {@code up_arrow -> KeyboardAction.MOVE_UP}<br>
	 * {@code down_arrow -> KeyboardAction.MOVE_DOWN	}<br>
	 * {@code CTRL + java.awt.event.KeyEvent.VK_1 -> KeyboardAction.ADD_KEYFRAME_TO_PATH_1}<br>
	 * {@code ALT + java.awt.event.KeyEvent.VK_1 -> KeyboardAction.DELETE_PATH_1}<br>
	 * {@code CTRL + java.awt.event.KeyEvent.VK_2 -> KeyboardAction.ADD_KEYFRAME_TO_PATH_2}<br>
	 * {@code ALT + java.awt.event.KeyEvent.VK_2 -> KeyboardAction.DELETE_PATH_2}<br>
	 * {@code CTRL + java.awt.event.KeyEvent.VK_3 -> KeyboardAction.ADD_KEYFRAME_TO_PATH_3}<br>
	 * {@code ALT + java.awt.event.KeyEvent.VK_3 -> KeyboardAction.DELETE_PATH_3}<br>
	 * <p>
	 * Finally, it calls: {@code setKeyCodeToPlayPath(java.awt.event.KeyEvent.VK_1, 1)},
	 * {@code setKeyCodeToPlayPath(java.awt.event.KeyEvent.VK_2, 2)} and
	 * {@code setKeyCodeToPlayPath(java.awt.event.KeyEvent.VK_3, 3)} to play the paths.
	 * 
	 * @see remixlab.dandelion.agent.KeyboardAgent#setDefaultBindings()
	 * @see remixlab.dandelion.agent.KeyboardAgent#setKeyCodeToPlayPath(int, int)
	 */
	public void setDefaultBindings() {
		// TODO docs pending
		removeBindings();
		removeBindings(Target.EYE);
		removeBindings(Target.FRAME);
		// 1. Scene bindings
		// VK_A : 65
		setBinding(65, SceneAction.TOGGLE_AXES_VISUAL_HINT);
		// VK_E : 69
		setBinding(69, SceneAction.TOGGLE_CAMERA_TYPE);
		// VK_F : 70
		setBinding(70, SceneAction.TOGGLE_PICKING_VISUAL_HINT);
		// VK_G : 71
		setBinding(71, SceneAction.TOGGLE_GRID_VISUAL_HINT);
		// VK_H : 72
		setBinding(72, SceneAction.DISPLAY_INFO);
		// VK_M : 77
		setBinding(77, SceneAction.TOGGLE_ANIMATION);
		// VK_R : 82
		setBinding(82, SceneAction.TOGGLE_PATHS_VISUAL_HINT);
		// VK_S : 83
		setBinding(83, SceneAction.INTERPOLATE_TO_FIT);
		// setBinding('S', SceneAction.SHOW_ALL);
		setBinding('p', SceneAction.INTERPOLATE_TO_FIT);

		// VK_LEFT : 37
		setBinding(37, SceneAction.MOVE_LEFT);
		// setBinding(BogusEvent.NO_MODIFIER_MASK, 37, SceneAction.MOVE_LEFT);
		// VK_UP : 38
		setBinding(38, SceneAction.MOVE_UP);
		// setBinding(BogusEvent.NO_MODIFIER_MASK, 38, SceneAction.MOVE_UP);
		// VK_RIGHT : 39
		setBinding(39, SceneAction.MOVE_RIGHT);
		// setBinding(BogusEvent.NO_MODIFIER_MASK, 39, SceneAction.MOVE_RIGHT);
		// VK_DOWN : 40
		setBinding(40, SceneAction.MOVE_DOWN);
		// setBinding(BogusEvent.NO_MODIFIER_MASK, 40, SceneAction.MOVE_DOWN);

		// VK_1 : 49
		setBinding(BogusEvent.CTRL, 49, SceneAction.ADD_KEYFRAME_TO_PATH_1);
		setBinding(BogusEvent.ALT, 49, SceneAction.DELETE_PATH_1);
		setKeyCodeToPlayPath(49, 1);
		// VK_2 : 50
		setBinding(BogusEvent.CTRL, 50, SceneAction.ADD_KEYFRAME_TO_PATH_2);
		setBinding(BogusEvent.ALT, 50, SceneAction.DELETE_PATH_2);
		setKeyCodeToPlayPath(50, 2);
		// VK_3 : 51
		setBinding(BogusEvent.CTRL, 51, SceneAction.ADD_KEYFRAME_TO_PATH_3);
		setBinding(BogusEvent.ALT, 51, SceneAction.DELETE_PATH_3);
		setKeyCodeToPlayPath(51, 3);

		// 2. Eye bindings
		// VK_A : 65
		setBinding(Target.EYE, 65, KeyboardAction.ALIGN_FRAME);
		// VK_C
		setBinding(Target.EYE, 67, KeyboardAction.CENTER_FRAME);
		// VK_X
		setBinding(Target.EYE, 88, KeyboardAction.TRANSLATE_DOWN_X);
		// VK_X
		setBinding(Target.EYE, BogusEvent.SHIFT, 88, KeyboardAction.TRANSLATE_UP_X);
		// VK_Y
		setBinding(Target.EYE, 89, KeyboardAction.TRANSLATE_DOWN_Y);
		// VK_Y
		setBinding(Target.EYE, BogusEvent.SHIFT, 89, KeyboardAction.TRANSLATE_UP_Y);
		// VK_Z
		setBinding(Target.EYE, 90, KeyboardAction.ROTATE_DOWN_Z);
		// VK_Z
		setBinding(Target.EYE, BogusEvent.SHIFT, 90, KeyboardAction.ROTATE_UP_Z);

		// 3. Frame bindings
		// VK_A : 65
		setBinding(Target.FRAME, 65, KeyboardAction.ALIGN_FRAME);
		// VK_C
		setBinding(Target.FRAME, 67, KeyboardAction.CENTER_FRAME);
		// VK_X
		setBinding(Target.FRAME, 88, KeyboardAction.TRANSLATE_DOWN_X);
		// VK_X
		setBinding(Target.FRAME, BogusEvent.SHIFT, 88, KeyboardAction.TRANSLATE_UP_X);
		// VK_Y
		setBinding(Target.FRAME, 89, KeyboardAction.TRANSLATE_DOWN_Y);
		// VK_Y
		setBinding(Target.FRAME, BogusEvent.SHIFT, 89, KeyboardAction.TRANSLATE_UP_Y);
		// VK_Z
		setBinding(Target.FRAME, 90, KeyboardAction.ROTATE_DOWN_Z);
		// VK_Z
		setBinding(Target.FRAME, BogusEvent.SHIFT, 90, KeyboardAction.ROTATE_UP_Z);
	}

	/**
	 * Sets the default (virtual) key to play eye paths.
	 */
	public void setKeyCodeToPlayPath(int vkey, int path) {
		switch (path) {
		case 1:
			setBinding(vkey, SceneAction.PLAY_PATH_1);
			// setBinding(BogusEvent.NO_MODIFIER_MASK, vkey, SceneAction.PLAY_PATH_1);
			break;
		case 2:
			setBinding(vkey, SceneAction.PLAY_PATH_2);
			// setBinding(BogusEvent.NO_MODIFIER_MASK, vkey, SceneAction.PLAY_PATH_2);
			break;
		case 3:
			setBinding(vkey, SceneAction.PLAY_PATH_3);
			// setBinding(BogusEvent.NO_MODIFIER_MASK, vkey, SceneAction.PLAY_PATH_3);
			break;
		default:
			break;
		}
	}

	// high level
	// i. scene

	public void setBinding(KeyboardShortcut shortcut, SceneAction action) {
		sceneProfile().setBinding(shortcut, action);
	}

	public void removeBinding(KeyboardShortcut shortcut) {
		sceneProfile().removeBinding(shortcut);
	}

	public boolean hasBinding(KeyboardShortcut shortcut) {
		return sceneProfile().hasBinding(shortcut);
	}

	public SceneAction action(KeyboardShortcut shortcut) {
		return sceneProfile().action(shortcut);
	}

	// don't override from here

	/**
	 * Removes all shortcut bindings.
	 */
	public void removeBindings() {
		sceneProfile().removeBindings();
	}

	/**
	 * Returns {@code true} if the keyboard action is bound.
	 */
	public boolean isActionBound(SceneAction action) {
		return sceneProfile().isActionBound(action);
	}

	// ii. Frame

	public void setBinding(Target target, KeyboardShortcut shortcut, KeyboardAction action) {
		motionProfile(target).setBinding(shortcut, action);
	}

	public void removeBinding(Target target, KeyboardShortcut shortcut) {
		motionProfile(target).removeBinding(shortcut);
	}

	public boolean hasBinding(Target target, KeyboardShortcut shortcut) {
		return motionProfile(target).hasBinding(shortcut);
	}

	public KeyboardAction action(Target target, KeyboardShortcut shortcut) {
		return motionProfile(target).action(shortcut);
	}

	// don't override from here

	/**
	 * Removes all shortcut bindings.
	 */
	public void removeBindings(Target target) {
		motionProfile(target).removeBindings();
	}

	/**
	 * Returns {@code true} if the keyboard action is bound.
	 */
	public boolean isActionBound(Target target, KeyboardAction action) {
		return motionProfile(target).isActionBound(action);
	}

	/**
	 * Binds the vKey (virtual key) shortcut to the (Keyboard) dandelion action.
	 */
	public void setBinding(int vKey, SceneAction action) {
		setBinding(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey), action);
	}

	/**
	 * Binds the mask-vKey (virtual key) shortcut to the (Keyboard) dandelion action.
	 */
	public void setBinding(int mask, int vKey, SceneAction action) {
		setBinding(new KeyboardShortcut(mask, vKey), action);
	}

	/**
	 * Removes mask-vKey (virtual key) shortcut binding (if present).
	 */
	public void removeBinding(int vKey) {
		removeBinding(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey));
	}

	/**
	 * Removes mask-vKey (virtual key) shortcut binding (if present).
	 */
	public void removeBinding(int mask, int vKey) {
		removeBinding(new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Returns {@code true} if the vKey (virtual key) shortcut is bound to a (Keyboard) dandelion action.
	 */
	public boolean hasBinding(int vKey) {
		return hasBinding(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey));
	}

	/**
	 * Returns {@code true} if the mask-vKey (virtual key) shortcut is bound to a (Keyboard) dandelion action.
	 */
	public boolean hasBinding(int mask, int vKey) {
		return hasBinding(new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Returns the (Keyboard) dandelion action that is bound to the given vKey (virtual key) shortcut. Returns
	 * {@code null} if no action is bound to the given shortcut.
	 */
	public SceneAction action(int vKey) {
		return action(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey));
	}

	/**
	 * Returns the (Keyboard) dandelion action that is bound to the given mask-vKey (virtual key) shortcut. Returns
	 * {@code null} if no action is bound to the given shortcut.
	 */
	public SceneAction action(int mask, int vKey) {
		return action(new KeyboardShortcut(mask, vKey));
	}

	// FRAMEs
	// TODO DOCs are broken (since they were copied/pasted from above)

	/**
	 * Binds the vKey (virtual key) shortcut to the (Keyboard) dandelion action.
	 */
	public void setBinding(Target target, int vKey, KeyboardAction action) {
		setBinding(target, new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey), action);
	}

	/**
	 * Binds the mask-vKey (virtual key) shortcut to the (Keyboard) dandelion action.
	 */
	public void setBinding(Target target, int mask, int vKey, KeyboardAction action) {
		setBinding(target, new KeyboardShortcut(mask, vKey), action);
	}

	/**
	 * Removes vKey (virtual key) shortcut binding (if present).
	 */
	public void removeBinding(Target target, int vKey) {
		removeBinding(target, new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey));
	}

	/**
	 * Removes mask-vKey (virtual key) shortcut binding (if present).
	 */
	public void removeBinding(Target target, int mask, int vKey) {
		removeBinding(target, new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Returns {@code true} if the vKey (virtual key) shortcut is bound to a (Keyboard) dandelion action.
	 */
	public boolean hasBinding(Target target, int vKey) {
		return hasBinding(target, new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey));
	}

	/**
	 * Returns {@code true} if the mask-vKey (virtual key) shortcut is bound to a (Keyboard) dandelion action.
	 */
	public boolean hasBinding(Target target, int mask, int vKey) {
		return hasBinding(target, new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Returns the (Keyboard) dandelion action that is bound to the given vKey (virtual key) shortcut. Returns
	 * {@code null} if no action is bound to the given shortcut.
	 */
	public KeyboardAction action(Target target, int vKey) {
		return action(target, new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey));
	}

	/**
	 * Returns the (Keyboard) dandelion action that is bound to the given mask-vKey (virtual key) shortcut. Returns
	 * {@code null} if no action is bound to the given shortcut.
	 */
	public KeyboardAction action(Target target, int mask, int vKey) {
		return action(target, new KeyboardShortcut(mask, vKey));
	}

	// Char hack from here

	public int keyCode(char key) {
		AbstractScene.showMissingImplementationWarning("getKeyCodeForChar", "KeyboardAgent");
		return BogusEvent.NO_ID;
	}

	/**
	 * Binds the key shortcut to the (Keyboard) dandelion action.
	 */
	public void setBinding(char key, SceneAction action) {
		setBinding(keyCode(key), action);
	}

	/**
	 * Binds the key shortcut to the (Keyboard) dandelion action.
	 */
	public void setBinding(int mask, char key, SceneAction action) {
		setBinding(mask, keyCode(key), action);
	}

	/**
	 * Removes key shortcut binding (if present).
	 */
	public void removeBinding(char key) {
		removeBinding(keyCode(key));
	}

	public void removeBinding(int mask, char key) {
		removeBinding(mask, keyCode(key));
	}

	/**
	 * Returns {@code true} if the key shortcut is bound to a (Keyboard) dandelion action.
	 */
	public boolean hasBinding(char key) {
		return hasBinding(keyCode(key));
	}

	public boolean hasBinding(int mask, char key) {
		return hasBinding(mask, keyCode(key));
	}

	/**
	 * Returns the (Keyboard) dandelion action that is bound to the given key shortcut. Returns {@code null} if no action
	 * is bound to the given shortcut.
	 */
	public SceneAction action(char key) {
		return action(keyCode(key));
	}

	public SceneAction action(int mask, char key) {
		return action(mask, keyCode(key));
	}

	/**
	 * Binds the key shortcut to the (Keyboard) dandelion action.
	 */
	public void setBinding(Target target, char key, KeyboardAction action) {
		setBinding(target, keyCode(key), action);
	}

	public void setBinding(Target target, int mask, char key, KeyboardAction action) {
		setBinding(target, mask, keyCode(key), action);
	}

	/**
	 * Removes key shortcut binding (if present).
	 */
	public void removeBinding(Target target, char key) {
		removeBinding(target, keyCode(key));
	}

	public void removeBinding(Target target, int mask, char key) {
		removeBinding(target, mask, keyCode(key));
	}

	/**
	 * Returns {@code true} if the key shortcut is bound to a (Keyboard) dandelion action.
	 */
	public boolean hasBinding(Target target, char key) {
		return hasBinding(target, keyCode(key));
	}

	public boolean hasBinding(Target target, int mask, char key) {
		return hasBinding(target, mask, keyCode(key));
	}

	/**
	 * Returns the (Keyboard) dandelion action that is bound to the given key shortcut. Returns {@code null} if no action
	 * is bound to the given shortcut.
	 */
	public KeyboardAction action(Target target, char key) {
		return action(target, keyCode(key));
	}

	public KeyboardAction action(Target target, int mask, char key) {
		return action(target, mask, keyCode(key));
	}
}