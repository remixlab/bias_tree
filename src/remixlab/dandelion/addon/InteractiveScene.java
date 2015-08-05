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

import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.Vec;
import remixlab.dandelion.addon.Constants.*;

import remixlab.bias.addon.*;
import remixlab.bias.event.KeyboardEvent;

/**
 * An interactive-scene is a {@link remixlab.dandelion.core.GrabberScene} that implements the 
 * {@link remixlab.bias.addon.InteractiveGrabber} interface. An interactive-scene implements the
 * {@link remixlab.dandelion.addon.Constants.SceneAction} on top of the grabber-scene api
 * (such as {@link #toggleAxesVisualHint()}, {@link #toggleCameraType()}, etc). The
 * {@link remixlab.dandelion.core.GrabberScene#keyboardAgent()} provide high-level methods to define
 * custom action bindings.
 * 
 * @see remixlab.dandelion.addon.KeyboardAgent
 */
public abstract class InteractiveScene extends GrabberScene implements InteractiveGrabber<GlobalAction>, Constants {	
	// grabber implementation
	
	protected MotionAgent<?>	defMotionAgent;
	protected KeyboardAgent		defKeyboardAgent;
	protected Action<GlobalAction>	action;
	
	@Override
	public KeyboardAgent keyboardAgent() {
		return defKeyboardAgent;
	}
	
	@Override
	public MotionAgent<?> motionAgent() {
		return defMotionAgent;
	}

	public GlobalAction referenceAction() {
		return action.referenceAction();
	}

	@Override
	public void setAction(Action<GlobalAction> a) {
		action = a;
	}

	@Override
	public Action<GlobalAction> action() {
		return action;
	}

	protected void performInteraction(KeyboardEvent event) {
		switch (referenceAction()) {
		case ADD_KEYFRAME_TO_PATH_1:
			eye().addKeyFrameToPath(1);
			break;
		case ADD_KEYFRAME_TO_PATH_2:
			eye().addKeyFrameToPath(2);
			break;
		case ADD_KEYFRAME_TO_PATH_3:
			eye().addKeyFrameToPath(3);
			break;
		case CUSTOM:
			performCustomAction(event);
			break;
		case DELETE_PATH_1:
			eye().deletePath(1);
			break;
		case DELETE_PATH_2:
			eye().deletePath(2);
			break;
		case DELETE_PATH_3:
			eye().deletePath(3);
			break;
		case DISPLAY_INFO:
			displayInfo();
			break;
		case INTERPOLATE_TO_FIT:
			eye().interpolateToFitScene();
			break;
		case PLAY_PATH_1:
			eye().playPath(1);
			break;
		case PLAY_PATH_2:
			eye().playPath(2);
			break;
		case PLAY_PATH_3:
			eye().playPath(3);
			break;
		case RESET_ANCHOR:
			eye().setAnchor(new Vec(0, 0, 0));
			// looks horrible, but works ;)
			eye().anchorFlag = true;
			eye().runResetAnchorHintTimer(1000);
			break;
		case SHOW_ALL:
			showAll();
			break;
		case TOGGLE_ANIMATION:
			toggleAnimation();
			break;
		case TOGGLE_AXES_VISUAL_HINT:
			toggleAxesVisualHint();
			break;
		case TOGGLE_CAMERA_TYPE:
			toggleCameraType();
			break;
		case TOGGLE_GRID_VISUAL_HINT:
			toggleGridVisualHint();
			break;
		case TOGGLE_PATHS_VISUAL_HINT:
			togglePathsVisualHint();
			break;
		case TOGGLE_PICKING_VISUAL_HINT:
			togglePickingVisualhint();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Callback method bound to {@link remixlab.dandelion.addon.Constants.GlobalAction#CUSTOM}.
	 */
	protected void performCustomAction(KeyboardEvent event) {
		GrabberScene.showMissingImplementationWarning("performCustomAction(KeyboardEvent event)", this.getClass()
				.getName());
	}
	
	@Override
	protected boolean checkIfGrabsInput(KeyboardEvent event) {
		return keyboardAgent().hasBinding(event.shortcut());
	}
	
	/**
	 * Display a warning that the specified Action is only available with 3D.
	 * 
	 * @param action
	 *          the action name (no parentheses)
	 */
	static public void showDepthWarning(MotionAction action) {
		showWarning(action.name() + " is not available in 2D.");
	}
	
	/**
	 * Display a warning that the specified Action lacks implementation.
	 */
	static public void showMissingImplementationWarning(MotionAction action, String theclass) {
		showWarning(action.name() + " should be implemented by your " + theclass + " derived class.");
	}
	
	/**
	 * Display a warning that the specified Action can only be implemented from a relative bogus event.
	 */
	static public void showEventVariationWarning(MotionAction action) {
		showWarning(action.name() + " can only be performed using a relative event.");
	}
	
	static public void showOnlyEyeWarning(MotionAction action) {
		showOnlyEyeWarning(action, true);
	}

	/**
	 * Display a warning that the specified Action is only available for the Eye frame.
	 */
	static public void showOnlyEyeWarning(MotionAction action, boolean eye) {
		if (eye)
			showWarning(action.name() + " can only be performed when frame is attached to an eye.");
		else
			showWarning(action.name() + " can only be performed when frame is detached from an eye.");
	}
	
	static public void showClickWarning(MotionAction action) {
		showWarning(action.name() + " cannot be performed from a ClickEvent.");
	}

	static public void showMotionWarning(MotionAction action) {
		showWarning(action.name() + " cannot be performed from a MotionEvent.");
	}

	static public void showKeyboardWarning(MotionAction action) {
		showWarning(action.name() + " cannot only be performed from a KeyboardEvent.");
	}
}