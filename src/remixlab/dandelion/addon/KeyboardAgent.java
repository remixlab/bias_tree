/*********************************************************************************
 * dandelion_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.dandelion.addon;

import remixlab.bias.addon.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

/**
 * The root of all dandelion keyboard agents. This agent specializes in handling
 * {@link remixlab.dandelion.addon.InteractiveFrame} and
 * {@link remixlab.dandelion.core.AbstractScene} objects, but it can also handle third-party
 * {@link remixlab.bias.core.Grabber} or {@link remixlab.bias.addon.InteractiveGrabber} object
 * instances. In the latter case, third-parties should implement their own
 * {@link remixlab.bias.addon.KeyboardBranch}es.
 * <p>
 * The agent has a {@code KeyboardBranch<GlobalAction, SceneAction>} branch to handle scene instances: the
 * {@link #sceneBranch()}; and two branches of the type {@code MotionBranch<MotionAction, A, ClickAction>}
 * to handle {@link remixlab.dandelion.addon.InteractiveFrame} object instances: {@link #eyeBranch()}, for
 * {@link remixlab.dandelion.core.Constants.Target#EYE} (which typically has one one single instance, that
 * of the {@link remixlab.dandelion.core.AbstractScene#eyeFrame()}); and {@link #frameBranch()}, for
 * {@link remixlab.dandelion.core.Constants.Target#FRAME} (which may have several instances, see
 * {@link #addGrabber(Grabber)}). Note that through the aforementioned branches the following
 * {@link remixlab.bias.addon.Profile}s are available: {@link #sceneProfile()},
 * {@link #eyeProfile()} and {@link #frameProfile()}. Refer to the {@link remixlab.bias.addon.Branch} and
 * {@link remixlab.bias.addon.Profile} for details.
 * <p>
 * 'Interaction customization', i.e., binding keyboard shortcuts (see
 * {@link remixlab.bias.event.KeyboardShortcut}) to dandelion global and motion actions, may
 * be achieved through those profiles or through various of the high-level methods conveniently
 * provided by this agent, such as {@link #setBinding(char, SceneAction)} or
 * {@link #setBinding(Target, int, KeyboardAction)} and so on. Note that to discriminate between the
 * {@link remixlab.dandelion.core.AbstractScene#eyeFrame()} from other 
 * {@link remixlab.dandelion.addon.InteractiveFrame} instances, some methods take an extra
 * {@link remixlab.dandelion.core.Constants.Target} parameter.
 * <p>
 * The agent's {@link #defaultGrabber()} is the {@link remixlab.dandelion.core.AbstractScene#eye()}
 * frame (see {@link remixlab.dandelion.core.Eye#frame()}) (note that {@link #resetDefaultGrabber()}
 * will thus defaults to the eye frame too).
 * 
 * @see remixlab.dandelion.core.Constants.SceneAction
 * @see remixlab.dandelion.core.Constants.KeyboardAction
 */

public abstract class KeyboardAgent extends KeyboardBranchAgent {	
	protected AbstractScene															scene;
	protected KeyboardBranch<GlobalAction, SceneAction>	keySceneBranch;
	protected KeyboardBranch<MotionAction, KeyboardAction>	keyFrameBranch, keyEyeBranch;

	/**
	 * Creates a keyboard agent and appends the {@link #sceneBranch()}, {@link #eyeBranch()} and
	 * {@link #frameBranch()} to it. The keyboard agent is added to the
	 * {@link remixlab.dandelion.core.AbstractScene#inputHandler()}.
	 */
	public KeyboardAgent(AbstractScene scn, String n) {
		super(scn.inputHandler(), n);
		scene = scn;		
		keySceneBranch = appendBranch("scene_keyboard_branch");
		keyFrameBranch = appendBranch("frame_keyboard_branch");
		keyEyeBranch = appendBranch("eye_keyboard_branch");
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
		if (!(frame instanceof remixlab.bias.addon.InteractiveGrabber))
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

	/**
	 * Returns the scene {@code KeyboardBranch<GlobalAction, SceneAction>}.
	 */
	public KeyboardBranch<GlobalAction, SceneAction> sceneBranch() {
		return keySceneBranch;
	}

	/**
	 * Returns the eye frame {@code KeyboardBranch<MotionAction, KeyboardAction>}.
	 */
	public KeyboardBranch<MotionAction, KeyboardAction> eyeBranch() {
		return keyEyeBranch;
	}

	/**
	 * Returns the frame {@code KeyboardBranch<MotionAction, KeyboardAction>}.
	 */
	public KeyboardBranch<MotionAction, KeyboardAction> frameBranch() {
		return keyFrameBranch;
	}

	/**
	 * Same as {@code return sceneBranch().keyboardProfile()}.
	 * <p>
	 * The profile defines customizable {@link remixlab.bias.core.KeyboardShortcut} to
	 * {@link remixlab.dandelion.core.Constants.SceneAction} mappings (bindings).
	 * 
	 * @see #sceneBranch()
	 * @see #remixlab.bias.core.Profile
	 */
	protected remixlab.bias.addon.Profile<GlobalAction, KeyboardShortcut, SceneAction> sceneProfile() {
		return sceneBranch().keyboardProfile();
	}

	/**
	 * Same as {@code eyeBranch().keyboardProfile()}.
	 * <p>
	 * The profile defines customizable {@link remixlab.bias.core.KeyboardShortcut} to
	 * {@link remixlab.dandelion.core.Constants.KeyboardAction} mappings (bindings) for
	 * the {@link remixlab.dandelion.core.AbstractScene#eyeFrame()} instance.
	 * 
	 * @see #eyeBranch()
	 */
	protected remixlab.bias.addon.Profile<MotionAction, KeyboardShortcut, KeyboardAction> eyeProfile() {
		return eyeBranch().keyboardProfile();
	}

	/**
	 * Same as {@code return frameBranch().keyboardProfile()}.
	 * <p>
	 * The profile defines customizable {@link remixlab.bias.core.KeyboardShortcut} to
	 * {@link remixlab.dandelion.core.Constants.KeyboardAction} mappings (bindings) for interactive-frame instances
	 * different than the {@link remixlab.dandelion.core.AbstractScene#eyeFrame()}.
	 * 
	 * @see #frameBranch()
	 */
	protected remixlab.bias.addon.Profile<MotionAction, KeyboardShortcut, KeyboardAction> frameProfile() {
		return frameBranch().keyboardProfile();
	}

	/**
	 * Same as {@code return target == Target.EYE ? eyeProfile() : frameProfile()}.
	 * <p>
	 * The profile defines customizable {@link remixlab.bias.core.KeyboardShortcut} to
	 * {@link remixlab.dandelion.core.Constants.KeyboardAction} mappings (bindings) either the
	 * {@link remixlab.dandelion.core.AbstractScene#eyeFrame()} (for {@link remixlab.dandelion.core.Constants.Target#EYE});
	 * or, for other interactive-frame instances (for {@link remixlab.dandelion.core.Constants.Target#FRAME}).
	 * 
	 * @see #eyeProfile()
	 * @see #frameProfile()
	 */
	protected remixlab.bias.addon.Profile<MotionAction, KeyboardShortcut, KeyboardAction> motionProfile(Target target) {
		return target == Target.EYE ? eyeProfile() : frameProfile();
	}

	/**
	 * Set the default keyboard shortcuts as follows:
	 * <p>
	 * 1. Scene bindings:
	 * {@code 'a' -> SceneAction.TOGGLE_AXIS_VISUAL_HINT}<br>
	 * {@code 'f' -> SceneAction.TOGGLE_FRAME_VISUAL_HINT}<br>
	 * {@code 'g' -> SceneAction.TOGGLE_GRID_VISUAL_HINT}<br>
	 * {@code 'm' -> SceneAction.TOGGLE_ANIMATION}<br>
	 * {@code 'e' -> SceneAction.TOGGLE_CAMERA_TYPE}<br>
	 * {@code 'h' -> SceneAction.DISPLAY_INFO}<br>
	 * {@code 'r' -> KeyboardAction.TOGGLE_PATHS_VISUAL_HINT}<br>
	 * {@code 's' -> SceneAction.INTERPOLATE_TO_FIT}<br>
	 * {@code 'S' -> SceneAction.SHOW_ALL}<br>
	 * {@code CTRL + '1' -> SceneAction.ADD_KEYFRAME_TO_PATH_1}<br>
	 * {@code ALT + '1' -> SceneAction.DELETE_PATH_1}<br>
	 * {@code CTRL + '2' -> SceneAction.ADD_KEYFRAME_TO_PATH_2}<br>
	 * {@code ALT + '2' -> SceneAction.DELETE_PATH_2}<br>
	 * {@code CTRL + '3' -> SceneAction.ADD_KEYFRAME_TO_PATH_3}<br>
	 * {@code ALT + '3' -> SceneAction.DELETE_PATH_3}<br>
	 * <p>
	 * 2. Bindings for the {@link remixlab.dandelion.core.AbstractScene#eyeFrame()} and other frame instances:
	 * {@code left_arrow -> KeyboardAction.TRANSLATE_X_NEG}<br>
	 * {@code right_arrow -> KeyboardAction.TRANSLATE_X_POS}<br>
	 * {@code up_arrow -> KeyboardAction.TRANSLATE_Y_POS}<br>
	 * {@code down_arrow -> KeyboardAction.TRANSLATE_Y_NEG}<br>
	 * {@code down_arrow -> KeyboardAction.ROTATE_Z_NEG}<br>
	 * <p>
	 * Finally, it calls: {@code setKeyCodeToPlayPath('1', 1)},
	 * {@code setKeyCodeToPlayPath('2', 2)} and
	 * {@code setKeyCodeToPlayPath('3', 3)} to play the paths.
	 * 
	 * @see remixlab.dandelion.addon.KeyboardAgent#setDefaultBindings()
	 * @see remixlab.dandelion.addon.KeyboardAgent#setKeyCodeToPlayPath(int, int)
	 */
	public void setDefaultBindings() {
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

	/**
	 * Defines a scene keyboard-shortcut binding. Same as {@code sceneProfile().setBinding(shortcut, action)}.
	 * 
	 * @see #sceneProfile()
	 */
	public void setBinding(KeyboardShortcut shortcut, SceneAction action) {
		if( hasBinding(Target.EYE, shortcut) )
			System.out.println("Warning: scene " + shortcut.description() + " will shadow " + action(Target.EYE, shortcut) + " Eye binding");
		if( hasBinding(Target.FRAME, shortcut) )
			System.out.println("Warning: scene " + shortcut.description() + " will shadow " + action(Target.FRAME, shortcut) + " Frame binding");
		sceneProfile().setBinding(shortcut, action);
	}

	/**
	 * Removes the scene keyboard-shortcut binding. Same as {@code sceneProfile().removeBinding(shortcut)}.
	 * 
	 * @see #sceneProfile()
	 */
	public void removeBinding(KeyboardShortcut shortcut) {
		sceneProfile().removeBinding(shortcut);
	}

	/**
	 * Checks if the scene keyboard-shortcut binding exists (true/false).
	 * Same as {@code return sceneProfile().hasBinding(shortcut)}.
	 * 
	 * @see #sceneProfile()
	 */
	public boolean hasBinding(KeyboardShortcut shortcut) {
		return sceneProfile().hasBinding(shortcut);
	}

	/**
	 * Returns the scene action that is bound to the keyboard shortcut (may be null).
	 * Same as {@code return sceneProfile().action(shortcut)}.
	 * 
	 * @see #sceneProfile()
	 */
	public SceneAction action(KeyboardShortcut shortcut) {
		return sceneProfile().action(shortcut);
	}

	// don't override from here

	/**
	 * Removes all the scene keyboard-shortcut bindings. Same as {@code sceneProfile().removeBindings()}.
	 * 
	 * @see #sceneProfile()
	 */
	public void removeBindings() {
		sceneProfile().removeBindings();
	}

	/**
	 * Checks if the scene action is bound to a keyboard-shortcut (true/false).
	 * Same as {@code return sceneProfile().isActionBound(action)}.
	 * 
	 * @see #sceneProfile()
	 */
	public boolean isActionBound(SceneAction action) {
		return sceneProfile().isActionBound(action);
	}

	// ii. Frame

	/**
	 * Defines a keyboard-shortcut binding for the target interactive-frame. Same as {@code motionProfile(target).setBinding(shortcut, action)}.
	 * <p>
	 * The {@code target} may be either ({@link remixlab.dandelion.core.Constants.Target#EYE} to point out the
	 * {@link remixlab.dandelion.core.AbstractScene#eyeFrame()}, or {@link remixlab.dandelion.core.Constants.Target#FRAME} to specify
	 * the remaining interactive-frames.
	 * 
	 * @see #motionProfile(Target)
	 */
	public void setBinding(Target target, KeyboardShortcut shortcut, KeyboardAction action) {
		if( hasBinding(shortcut) ) {
			System.err.println("No keyboard binding set for target " + target + ". KeyboardShortcut: " + shortcut.description() + " already exists for the scene. Remove it first with removeBinding(KeyboardShortcut shortcut) if you still wanna use it");
			return;
		}			
		motionProfile(target).setBinding(shortcut, action);
	}

	/**
	 * Removes the target interactive-frame keyboard-shortcut binding. Same as {@code motionProfile(target).removeBinding(shortcut)}.
	 * <p>
	 * The {@code target} may be either ({@link remixlab.dandelion.core.Constants.Target#EYE} to point out the
	 * {@link remixlab.dandelion.core.AbstractScene#eyeFrame()}, or {@link remixlab.dandelion.core.Constants.Target#FRAME} to specify
	 * the remaining interactive-frames.
	 * 
	 * @see #motionProfile(Target)
	 */
	public void removeBinding(Target target, KeyboardShortcut shortcut) {
		motionProfile(target).removeBinding(shortcut);
	}

	/**
	 * Checks if the target interactive-frame or other frames keyboard-shortcut binding exists (true/false). Same as
	 * {@code return motionProfile(target).hasBinding(shortcut)}.
	 * <p>
	 * The {@code target} may be either ({@link remixlab.dandelion.core.Constants.Target#EYE} to point out the
	 * {@link remixlab.dandelion.core.AbstractScene#eyeFrame()}, or {@link remixlab.dandelion.core.Constants.Target#FRAME} to specify
	 * the remaining interactive-frames.
	 * 
	 * @see #motionProfile(Target)
	 */
	public boolean hasBinding(Target target, KeyboardShortcut shortcut) {
		return motionProfile(target).hasBinding(shortcut);
	}

	/**
	 * Returns the target interactive-frame action that is bound to the keyboard shortcut (may be null). Same as
	 * {@code return motionProfile(target).action(shortcut)}.
	 * <p>
	 * The {@code target} may be either ({@link remixlab.dandelion.core.Constants.Target#EYE} to point out the
	 * {@link remixlab.dandelion.core.AbstractScene#eyeFrame()}, or {@link remixlab.dandelion.core.Constants.Target#FRAME} to specify
	 * the remaining interactive-frames.
	 * 
	 * @see #motionProfile(Target)
	 */
	public KeyboardAction action(Target target, KeyboardShortcut shortcut) {
		return motionProfile(target).action(shortcut);
	}

	// don't override from here

	/**
	 * Removes all the target interactive-frame keyboard-shortcut bindings. Same as {@code motionProfile(target).removeBindings()}.
	 * <p>
	 * The {@code target} may be either ({@link remixlab.dandelion.core.Constants.Target#EYE} to point out the
	 * {@link remixlab.dandelion.core.AbstractScene#eyeFrame()}, or {@link remixlab.dandelion.core.Constants.Target#FRAME} to specify
	 * the remaining interactive-frames.
	 * 
	 * @see #motionProfile(Target)
	 */
	public void removeBindings(Target target) {
		motionProfile(target).removeBindings();
	}

	/**
	 * Checks if the target interactive-frame action is bound to a keyboard-shortcut (true/false). Same as
	 * {@code return motionProfile(target).isActionBound(action)}.
	 * <p>
	 * The {@code target} may be either ({@link remixlab.dandelion.core.Constants.Target#EYE} to point out the
	 * {@link remixlab.dandelion.core.AbstractScene#eyeFrame()}, or {@link remixlab.dandelion.core.Constants.Target#FRAME} to specify
	 * the remaining interactive-frames.
	 * 
	 * @see #motionProfile(Target)
	 */
	public boolean isActionBound(Target target, KeyboardAction action) {
		return motionProfile(target).isActionBound(action);
	}
	
	//SCENE

	/**
	 * Same as {@code setBinding(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey), action)}.
	 * 
	 * @see #setBinding(KeyboardShortcut, SceneAction)
	 */
	public void setBinding(int vKey, SceneAction action) {
		setBinding(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey), action);
	}

	/**
	 * Same as {@code setBinding(new KeyboardShortcut(mask, vKey), action)}.
	 * 
	 * @see #setBinding(KeyboardShortcut, SceneAction)
	 */
	public void setBinding(int mask, int vKey, SceneAction action) {
		setBinding(new KeyboardShortcut(mask, vKey), action);
	}

	/**
	 * Same as {@code removeBinding(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey))}.
	 * 
	 * @see #removeBinding(KeyboardShortcut)
	 */
	public void removeBinding(int vKey) {
		removeBinding(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey));
	}

	/**
	 * Same as {@code removeBinding(new KeyboardShortcut(mask, vKey))}.
	 * 
	 * @see #removeBinding(KeyboardShortcut)
	 */
	public void removeBinding(int mask, int vKey) {
		removeBinding(new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Same as {@code return hasBinding(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey))}.
	 * 
	 * @see #hasBinding(KeyboardShortcut)
	 */
	public boolean hasBinding(int vKey) {
		return hasBinding(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey));
	}

	/**
	 * Same as {@code return hasBinding(new KeyboardShortcut(mask, vKey))}.
	 * 
	 * @see #hasBinding(KeyboardShortcut)
	 */
	public boolean hasBinding(int mask, int vKey) {
		return hasBinding(new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Same as {@code return action(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey))}.
	 * 
	 * @see #action(KeyboardShortcut)
	 */
	public SceneAction action(int vKey) {
		return action(new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey));
	}

	/**
	 * Same as {@code return action(new KeyboardShortcut(mask, vKey))}.
	 * 
	 * @see #action(KeyboardShortcut)
	 */
	public SceneAction action(int mask, int vKey) {
		return action(new KeyboardShortcut(mask, vKey));
	}
	
	// char
	
	/**
	 * Same as {@code setBinding(keyCode(key), action)}.
	 * 
	 * @see #setBinding(int, SceneAction)
	 */
	public void setBinding(char key, SceneAction action) {
		setBinding(keyCode(key), action);
	}

	/**
	 * Same as {@code setBinding(mask, keyCode(key), action)}.
	 * 
	 * @see #setBinding(int, int, SceneAction)
	 */
	public void setBinding(int mask, char key, SceneAction action) {
		setBinding(mask, keyCode(key), action);
	}

	/**
	 * Same as {@code removeBinding(keyCode(key))}.
	 * 
	 * @see #removeBinding(int)
	 */
	public void removeBinding(char key) {
		removeBinding(keyCode(key));
	}

	/**
	 * Same as {@code removeBinding(mask, keyCode(key))}.
	 * 
	 * @see #removeBinding(int, int)
	 * @see #removeBinding(int)
	 */
	public void removeBinding(int mask, char key) {
		removeBinding(mask, keyCode(key));
	}

	/**
	 * Same as {@code return hasBinding(keyCode(key))}.
	 * 
	 * {@link #hasBinding(int, char)}
	 * {@link #hasBinding(int)}
	 */
	public boolean hasBinding(char key) {
		return hasBinding(keyCode(key));
	}

	/**
	 * Same as {@code return hasBinding(mask, keyCode(key))}.
	 * 
	 * {@link #hasBinding(int, char)}
	 * {@link #hasBinding(int, int)}
	 */
	public boolean hasBinding(int mask, char key) {
		return hasBinding(mask, keyCode(key));
	}

	/**
	 * Same as {@code return action(keyCode(key))}.
	 * 
	 * @see #action(int, char)
	 * @see #action(int, int)
	 */
	public SceneAction action(char key) {
		return action(keyCode(key));
	}

	/**
	 * Same as {@code return action(mask, keyCode(key))}.
	 * 
	 * @see #action(int, char)
	 * @see #action(int, int)
	 */
	public SceneAction action(int mask, char key) {
		return action(mask, keyCode(key));
	}

	// FRAMEs

	/**
	 * Same as {@code setBinding(target, new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey), action)}.
	 * 
	 * @see #setBinding(Target, KeyboardShortcut, KeyboardAction)
	 */
	public void setBinding(Target target, int vKey, KeyboardAction action) {
		setBinding(target, new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey), action);
	}

	/**
	 * Same as {@code setBinding(target, new KeyboardShortcut(mask, vKey), action)}.
	 * 
	 * @see #setBinding(Target, KeyboardShortcut, KeyboardAction)
	 */
	public void setBinding(Target target, int mask, int vKey, KeyboardAction action) {
		setBinding(target, new KeyboardShortcut(mask, vKey), action);
	}

	/**
	 * Same as {@code removeBinding(target, new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey))}.
	 * 
	 * @see #removeBinding(Target, KeyboardShortcut)
	 */
	public void removeBinding(Target target, int vKey) {
		removeBinding(target, new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey));
	}

	/**
	 * Same as {@code removeBinding(target, new KeyboardShortcut(mask, vKey))}.
	 * 
	 * @see #removeBinding(Target, KeyboardShortcut)
	 */
	public void removeBinding(Target target, int mask, int vKey) {
		removeBinding(target, new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Same as {@code return hasBinding(target, new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey))}.
	 * 
	 * @see #hasBinding(Target, KeyboardShortcut)
	 */
	public boolean hasBinding(Target target, int vKey) {
		return hasBinding(target, new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey));
	}

	/**
	 * Same as {@code return hasBinding(target, new KeyboardShortcut(mask, vKey))}.
	 * 
	 * @see #hasBinding(Target, KeyboardShortcut)
	 */
	public boolean hasBinding(Target target, int mask, int vKey) {
		return hasBinding(target, new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Same as {@code return action(target, new KeyboardShortcut(mask, vKey))}.
	 * 
	 * @see #action(Target, KeyboardShortcut)
	 */
	public KeyboardAction action(Target target, int vKey) {
		return action(target, new KeyboardShortcut(BogusEvent.NO_MODIFIER_MASK, vKey));
	}

	/**
	 * Same as {@code return action(target, new KeyboardShortcut(mask, vKey))}.
	 * 
	 * @see #action(Target, KeyboardShortcut)
	 */
	public KeyboardAction action(Target target, int mask, int vKey) {
		return action(target, new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Same as {@code setBinding(target, keyCode(key), action)}.
	 * 
	 * @see #setBinding(Target, char, KeyboardAction)
	 * @see #setBinding(Target, int, KeyboardAction)
	 */
	public void setBinding(Target target, char key, KeyboardAction action) {
		setBinding(target, keyCode(key), action);
	}

	/**
	 * Same as {@code setBinding(target, mask, keyCode(key), action)}.
	 * 
	 * @see #setBinding(Target, char, KeyboardAction)
	 * @see #setBinding(Target, int, KeyboardAction)
	 */
	public void setBinding(Target target, int mask, char key, KeyboardAction action) {
		setBinding(target, mask, keyCode(key), action);
	}

	/**
	 * Same as {@code removeBinding(target, keyCode(key))}.
	 * 
	 * @see #removeBinding(Target, char)
	 * @see #removeBinding(Target, int)
	 */
	public void removeBinding(Target target, char key) {
		removeBinding(target, keyCode(key));
	}

	/**
	 * Same as {@code removeBinding(target, mask, keyCode(key))}.
	 * 
	 * @see #removeBinding(Target, char)
	 * @see #removeBinding(Target, int)
	 */
	public void removeBinding(Target target, int mask, char key) {
		removeBinding(target, mask, keyCode(key));
	}

	/**
	 * Same as {@code return hasBinding(target, keyCode(key))}.
	 * 
	 * @see #hasBinding(Target, char)
	 * @see #hasBinding(Target, int)
	 */
	public boolean hasBinding(Target target, char key) {
		return hasBinding(target, keyCode(key));
	}

	/**
	 * Same as {@code return hasBinding(target, mask, keyCode(key))}.
	 * 
	 * @see #hasBinding(Target, char)
	 * @see #hasBinding(Target, int)
	 */
	public boolean hasBinding(Target target, int mask, char key) {
		return hasBinding(target, mask, keyCode(key));
	}

	/**
	 * Same as {@code return action(target, keyCode(key))}.
	 * 
	 * @see #action(Target, char)
	 * @see #action(Target, int)
	 */
	public KeyboardAction action(Target target, char key) {
		return action(target, keyCode(key));
	}

	/**
	 * Same as {@code return action(target, mask, keyCode(key))}.
	 * 
	 * @see #action(Target, char)
	 * @see #action(Target, int)
	 */
	public KeyboardAction action(Target target, int mask, char key) {
		return action(target, mask, keyCode(key));
	}
}