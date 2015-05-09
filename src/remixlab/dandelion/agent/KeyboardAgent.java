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
	protected AbstractScene																											scene;
	protected KeyboardBranch<SceneAction, KeyboardProfile<GlobalAction>>	keySceneBranch;
	protected KeyboardBranch<MotionAction, KeyboardProfile<KeyboardAction>>	keyFrameBranch, keyEyeBranch;

	public KeyboardAgent(AbstractScene scn, String n) {
		super(scn.inputHandler(), n);
		scene = scn;
		keySceneBranch = new KeyboardBranch<SceneAction, KeyboardProfile<GlobalAction>>(
				new KeyboardProfile<GlobalAction>(),
				this,
				"scene_keyboard_branch");
		keyFrameBranch = new KeyboardBranch<MotionAction, KeyboardProfile<KeyboardAction>>(
				new KeyboardProfile<KeyboardAction>(),
				this,
				"frame_keyboard_branch");
		keyEyeBranch = new KeyboardBranch<MotionAction, KeyboardProfile<KeyboardAction>>(
				new KeyboardProfile<KeyboardAction>(),
				this,
				"eye_keyboard_branch");
		// new, mimics eye -> motionAgent -> scene -> keyAgent
		// addGrabber(scene);
		// addGrabber(scene.eye().frame());
		resetDefaultGrabber();
		setDefaultGrabber(scene);
		setDefaultBindings();
	}

  @Override
  public boolean addGrabber(Grabber frame) {
  	if(frame instanceof AbstractScene)
  		return addGrabber(scene, keySceneBranch);
  	if (frame instanceof InteractiveFrame)
  		return addGrabber((InteractiveFrame) frame,	((InteractiveFrame) frame).isEyeFrame() ? keyEyeBranch : keyFrameBranch);
  	if (!(frame instanceof InteractiveGrabber))
  		return super.addGrabber(frame);
  	return false;
  }

	/*
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

	public KeyboardBranch<SceneAction, KeyboardProfile<GlobalAction>> sceneBranch() {
		return keySceneBranch;
	}

	public KeyboardBranch<MotionAction, KeyboardProfile<KeyboardAction>> eyeBranch() {
		return keyEyeBranch;
	}

	public KeyboardBranch<MotionAction, KeyboardProfile<KeyboardAction>> frameBranch() {
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

	protected KeyboardProfile<GlobalAction> sceneProfile() {
		return sceneBranch().keyboardProfile();
	}

	protected KeyboardProfile<KeyboardAction> eyeProfile() {
		return eyeBranch().keyboardProfile();
	}

	protected KeyboardProfile<KeyboardAction> frameProfile() {
		return frameBranch().keyboardProfile();
	}

	protected KeyboardProfile<KeyboardAction> motionProfile(Target target) {
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
	public void setDefaultBindings() {
		sceneProfile().removeBindings();
		sceneProfile().setBinding('a', GlobalAction.TOGGLE_AXES_VISUAL_HINT);
		sceneProfile().setBinding('f', GlobalAction.TOGGLE_PICKING_VISUAL_HINT);
		sceneProfile().setBinding('g', GlobalAction.TOGGLE_GRID_VISUAL_HINT);
		sceneProfile().setBinding('m', GlobalAction.TOGGLE_ANIMATION);

		sceneProfile().setBinding('e', GlobalAction.TOGGLE_CAMERA_TYPE);
		sceneProfile().setBinding('h', GlobalAction.DISPLAY_INFO);
		sceneProfile().setBinding('r', GlobalAction.TOGGLE_PATHS_VISUAL_HINT);

		sceneProfile().setBinding('s', GlobalAction.INTERPOLATE_TO_FIT);
		sceneProfile().setBinding('S', GlobalAction.SHOW_ALL);

		// TODO add some eye and frame defs
		removeBindings(Target.EYE);
		removeBindings(Target.FRAME);
		setBinding(Target.EYE, 'a', KeyboardAction.ALIGN_FRAME);
		setBinding(Target.EYE, 'c', KeyboardAction.CENTER_FRAME);
		setBinding(Target.FRAME, 'a', KeyboardAction.ALIGN_FRAME);
		setBinding(Target.FRAME, 'c', KeyboardAction.CENTER_FRAME);
		setBinding(Target.FRAME, 'x', KeyboardAction.TRANSLATE_DOWN_X);
		setBinding(Target.FRAME, 'X', KeyboardAction.TRANSLATE_UP_X);
		setBinding(Target.FRAME, 'y', KeyboardAction.TRANSLATE_DOWN_Y);
		setBinding(Target.FRAME, 'Y', KeyboardAction.TRANSLATE_UP_Y);
		setBinding(Target.FRAME, 'z', KeyboardAction.ROTATE_DOWN_Z);
		setBinding(Target.FRAME, 'Z', KeyboardAction.ROTATE_UP_Z);
		setBinding(Target.EYE, 'x', KeyboardAction.TRANSLATE_DOWN_X);
		setBinding(Target.EYE, 'X', KeyboardAction.TRANSLATE_UP_X);
		setBinding(Target.EYE, 'y', KeyboardAction.TRANSLATE_DOWN_Y);
		setBinding(Target.EYE, 'Y', KeyboardAction.TRANSLATE_UP_Y);
		setBinding(Target.EYE, 'z', KeyboardAction.ROTATE_DOWN_Z);
		setBinding(Target.EYE, 'Z', KeyboardAction.ROTATE_UP_Z);
	}

	/**
	 * Sets the default (virtual) key to play eye paths.
	 */
	public void setKeyCodeToPlayPath(int vkey, int path) {
		switch (path) {
		case 1:
			sceneProfile().setBinding(BogusEvent.NO_MODIFIER_MASK, vkey, GlobalAction.PLAY_PATH_1);
			break;
		case 2:
			sceneProfile().setBinding(BogusEvent.NO_MODIFIER_MASK, vkey, GlobalAction.PLAY_PATH_2);
			break;
		case 3:
			sceneProfile().setBinding(BogusEvent.NO_MODIFIER_MASK, vkey, GlobalAction.PLAY_PATH_3);
			break;
		default:
			break;
		}
	}
	
  //high level
	//i. scene
	
	public void setBinding(KeyboardShortcut shortcut, GlobalAction action) {
		sceneProfile().setBinding(shortcut, action);
	}
	
	public void removeBinding(KeyboardShortcut shortcut) {
		sceneProfile().removeBinding(shortcut);
	}
	
  public boolean hasBinding(KeyboardShortcut shortcut) {
  	return sceneProfile().hasBinding(shortcut);
	}
  
  public GlobalAction action(KeyboardShortcut shortcut) {
		return sceneProfile().action(shortcut);
	}
  
  //don't override from here
  
  /**
	 * Removes all shortcut bindings.
	 */
	public void removeBindings() {
		sceneProfile().removeBindings();
	}
  
  /**
	 * Returns {@code true} if the keyboard action is bound.
	 */
	public boolean isActionBound(GlobalAction action) {
		return sceneProfile().isActionBound(action);
	}
	
	//ii. Frame
	
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
  
  //don't override from here
  
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
	
	//end

	/**
	 * Binds the key shortcut to the (Keyboard) dandelion action.
	 */
	public void setBinding(Character key, GlobalAction action) {
		sceneProfile().setBinding(key, action);
	}

	/**
	 * Binds the mask-vKey (virtual key) shortcut to the (Keyboard) dandelion action.
	 */
	public void setBinding(int mask, int vKey, GlobalAction action) {
		sceneProfile().setBinding(mask, vKey, action);
	}

	/**
	 * Removes key shortcut binding (if present).
	 */
	public void removeBinding(Character key) {
		sceneProfile().removeBinding(key);
	}

	/**
	 * Removes mask-vKey (virtual key) shortcut binding (if present).
	 */
	public void removeBinding(int mask, int vKey) {
		sceneProfile().removeBinding(mask, vKey);
	}

	/**
	 * Returns {@code true} if the key shortcut is bound to a (Keyboard) dandelion action.
	 */
	public boolean hasBinding(Character key) {
		return sceneProfile().hasBinding(key);
	}

	/**
	 * Returns {@code true} if the mask-vKey (virtual key) shortcut is bound to a (Keyboard) dandelion action.
	 */
	public boolean hasBinding(int mask, int vKey) {
		return sceneProfile().hasBinding(mask, vKey);
	}

	/**
	 * Returns the (Keyboard) dandelion action that is bound to the given key shortcut. Returns {@code null} if no action
	 * is bound to the given shortcut.
	 */
	public GlobalAction action(Character key) {
		return action(new KeyboardShortcut(key));
	}

	/**
	 * Returns the (Keyboard) dandelion action that is bound to the given mask-vKey (virtual key) shortcut. Returns
	 * {@code null} if no action is bound to the given shortcut.
	 */
	public GlobalAction action(int mask, int vKey) {
		return action(new KeyboardShortcut(mask, vKey));
	}

	// FRAMEs
	// TODO DOCs are broken (since they were copied/pasted from above)

	/**
	 * Binds the key shortcut to the (Keyboard) dandelion action.
	 */
	public void setBinding(Target target, Character key, KeyboardAction action) {
		motionProfile(target).setBinding(key, action);
	}

	/**
	 * Binds the mask-vKey (virtual key) shortcut to the (Keyboard) dandelion action.
	 */
	public void setBinding(Target target, int mask, int vKey, KeyboardAction action) {
		motionProfile(target).setBinding(mask, vKey, action);
	}

	/**
	 * Removes key shortcut binding (if present).
	 */
	public void removeBinding(Target target, Character key) {
		motionProfile(target).removeBinding(key);
	}

	/**
	 * Removes mask-vKey (virtual key) shortcut binding (if present).
	 */
	public void removeBinding(Target target, int mask, int vKey) {
		motionProfile(target).removeBinding(mask, vKey);
	}

	/**
	 * Returns {@code true} if the key shortcut is bound to a (Keyboard) dandelion action.
	 */
	public boolean hasBinding(Target target, Character key) {
		return motionProfile(target).hasBinding(key);
	}

	/**
	 * Returns {@code true} if the mask-vKey (virtual key) shortcut is bound to a (Keyboard) dandelion action.
	 */
	public boolean hasBinding(Target target, int mask, int vKey) {
		return motionProfile(target).hasBinding(mask, vKey);
	}

	/**
	 * Returns the (Keyboard) dandelion action that is bound to the given key shortcut. Returns {@code null} if no action
	 * is bound to the given shortcut.
	 */
	public KeyboardAction action(Target target, Character key) {
		return action(target, new KeyboardShortcut(key));
	}

	/**
	 * Returns the (Keyboard) dandelion action that is bound to the given mask-vKey (virtual key) shortcut. Returns
	 * {@code null} if no action is bound to the given shortcut.
	 */
	public KeyboardAction action(Target target, int mask, int vKey) {
		return action(target, new KeyboardShortcut(mask, vKey));
	}
}