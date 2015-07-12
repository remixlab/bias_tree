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
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.bias.event.shortcut.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

public class KeyboardAgent extends Agent {
	public static int LEFT_KEY	= 1, RIGHT_KEY = 2, UP_KEY = 3, DOWN_KEY = 4;
	
	protected AbstractScene															scene;
	protected KeyboardBranch<GlobalAction, SceneAction>	keySceneBranch;
	protected KeyboardBranch<MotionAction, KeyboardAction>	keyFrameBranch, keyEyeBranch;

	public KeyboardAgent(AbstractScene scn, String n) {
		super(scn.inputHandler(), n);
		scene = scn;
		keySceneBranch = new KeyboardBranch<GlobalAction, SceneAction>(this, "scene_keyboard_branch");
		keyFrameBranch = new KeyboardBranch<MotionAction, KeyboardAction>(this, "frame_keyboard_branch");
		keyEyeBranch = new KeyboardBranch<MotionAction, KeyboardAction>(this, "eye_keyboard_branch");
		//resetDefaultGrabber();
		addGrabber(scene);
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
		System.err.println("use addGrabber(G grabber, K KeyboardBranch) instead");
		return false;
	}
	
	@Override
	public boolean setDefaultGrabber(Grabber g) {
		if( g instanceof AbstractScene ) {
			System.err.println("No default keyboard agent grabber set. A scene cannot be set as a default keyboard agent input grabber.");
			return false;
		}
		return super.setDefaultGrabber(g);
	}

	/**
	 * Returns the scene this object belongs to
	 */
	public AbstractScene scene() {
		return scene;
	}
	
	@Override
	public boolean resetDefaultGrabber() {
		addGrabber(scene.eye().frame());
		return setDefaultGrabber(scene.eye().frame());
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
	
	//TODO discard me, use only next method
	@Override
	protected boolean appendBranch(Branch<?, ?, ?> branch) {
		if (branch instanceof KeyboardBranch)
			return super.appendBranch(branch);
		else {
			System.out.println("Branch should be instanceof KeyboardBranch to be appended");
			return false;
		}
	}
	
	//TODO what would be the method signature in Agent class this method overrides? (might allow to make all branch constructors protected)
	public <E extends Enum<E>, A extends Action<E>> KeyboardBranch<E, A> appendBranch() {
		return new KeyboardBranch<E, A>(this, "my_key_branch");
	}

	protected Profile<GlobalAction, KeyboardShortcut, SceneAction> sceneProfile() {
		return sceneBranch().profile();
	}

	protected Profile<MotionAction, KeyboardShortcut, KeyboardAction> eyeProfile() {
		return eyeBranch().profile();
	}

	protected Profile<MotionAction, KeyboardShortcut, KeyboardAction> frameProfile() {
		return frameBranch().profile();
	}

	protected Profile<MotionAction, KeyboardShortcut, KeyboardAction> motionProfile(Target target) {
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
		setBinding('a', SceneAction.TOGGLE_AXES_VISUAL_HINT);
		// VK_E : 69
		setBinding('e', SceneAction.TOGGLE_CAMERA_TYPE);
		// VK_F : 70
		setBinding('f', SceneAction.TOGGLE_PICKING_VISUAL_HINT);
		// VK_G : 71
		setBinding('g', SceneAction.TOGGLE_GRID_VISUAL_HINT);
		// VK_H : 72
		setBinding('h', SceneAction.DISPLAY_INFO);
		// VK_M : 77
		setBinding('m', SceneAction.TOGGLE_ANIMATION);
		// VK_R : 82
		setBinding('r', SceneAction.TOGGLE_PATHS_VISUAL_HINT);
		// VK_S : 83
		setBinding('s', SceneAction.INTERPOLATE_TO_FIT);
		setBinding(BogusEvent.SHIFT, 's', SceneAction.SHOW_ALL);

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
		setBinding(Target.EYE, 'n', KeyboardAction.ALIGN_FRAME);
		// VK_C
		setBinding(Target.EYE, 'c', KeyboardAction.CENTER_FRAME);
		// VK_LEFT
		setBinding(Target.EYE, LEFT_KEY, KeyboardAction.TRANSLATE_X_NEG);
		// VK_RIGHT
		setBinding(Target.EYE, RIGHT_KEY, KeyboardAction.TRANSLATE_X_POS);
		// VK_DOWN
		setBinding(Target.EYE, DOWN_KEY, KeyboardAction.TRANSLATE_Y_NEG);
		// VK_UP
		setBinding(Target.EYE, UP_KEY, KeyboardAction.TRANSLATE_Y_POS);
		// VK_Z
		setBinding(Target.EYE, 'z', KeyboardAction.ROTATE_Z_NEG);
		// VK_Z
		setBinding(Target.EYE, BogusEvent.SHIFT, 'z', KeyboardAction.ROTATE_Z_POS);

		// 3. Frame bindings
		// VK_A : 65
		setBinding(Target.FRAME, 'n', KeyboardAction.ALIGN_FRAME);
		// VK_C
		setBinding(Target.FRAME, 'c', KeyboardAction.CENTER_FRAME);
		// VK_LEFT
		setBinding(Target.FRAME, LEFT_KEY, KeyboardAction.TRANSLATE_X_NEG);
		// VK_RIGHT
		setBinding(Target.FRAME, RIGHT_KEY, KeyboardAction.TRANSLATE_X_POS);
		// VK_UP
		setBinding(Target.FRAME, DOWN_KEY, KeyboardAction.TRANSLATE_Y_NEG);
		// VK_DOWN
		setBinding(Target.FRAME, UP_KEY, KeyboardAction.TRANSLATE_Y_POS);
		// VK_Z
		setBinding(Target.FRAME, 'z', KeyboardAction.ROTATE_Z_NEG);
		// VK_Z
		setBinding(Target.FRAME, BogusEvent.SHIFT, 'z', KeyboardAction.ROTATE_Z_POS);
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

	//TODO make me more general, i.e., take into account all branches
	public void setBinding(KeyboardShortcut shortcut, SceneAction action) {
		if( hasBinding(Target.EYE, shortcut) )
			System.out.println("Warning: scene " + shortcut.description() + " will shadow " + action(Target.EYE, shortcut) + " Eye binding");
		if( hasBinding(Target.FRAME, shortcut) )
			System.out.println("Warning: scene " + shortcut.description() + " will shadow " + action(Target.FRAME, shortcut) + " Frame binding");
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
		if( hasBinding(shortcut) ) {
			System.err.println("No keyboard binding set for target " + target + ". KeyboardShortcut: " + shortcut.description() + " already exists for the scene. Remove it first with removeBinding(KeyboardShortcut shortcut) if you still wanna use it");
			return;
		}			
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
	
	//SCENE

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
	
	// char hack
	
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
	
	/*
	public List<KeyboardShortcut> shortcuts() {
		return this.sceneProfile().shorcuts();
	}
	*/
}