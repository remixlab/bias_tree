/*********************************************************************************
 * dandelion_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.dandelion.core;

import remixlab.bias.core.Action;

//TODO left out only a single CUSTOM action if dofs() are discarded
//TODO fix docs
public interface Constants {
	/**
	 * Which object is performing the motion action.
	 */
	public enum Target {
		EYE, FRAME
	}

	public enum MotionAction {
		/**
		 * Center frame
		 */
		CENTER_FRAME("Center frame", true, 0),
		/**
		 * Align frame with world
		 */
		ALIGN_FRAME("Align frame with world", true, 0),

		// Click actions require cursor pos:
		/**
		 * Interpolate the eye to zoom on pixel
		 */
		ZOOM_ON_PIXEL("Interpolate the eye to zoom on pixel", true, 0),
		/**
		 * Set the anchor from the pixel under the pointer
		 */
		ANCHOR_FROM_PIXEL("Set the anchor from the pixel under the pointer", true, 0),

		// Wheel

		/**
		 * custom Scale frame
		 */
		SCALE("Scale frame", true, 1),
		/**
		 * Zoom eye
		 */
		ZOOM("Zoom eye", false, 1),
		/**
		 * Zoom eye on anchor
		 */
		ZOOM_ON_ANCHOR("Zoom eye on anchor", false, 1),
		/**
		 * Translate along screen X axis
		 */
		TRANSLATE_X("Translate along screen X axis", true, 1),
		/**
		 * Translate along screen Y axis
		 */
		TRANSLATE_Y("Translate along screen Y axis", true, 1),
		/**
		 * Translate along screen Z axis
		 */
		TRANSLATE_Z("Translate along screen Z axis", false, 1),
		/**
		 * Rotate frame around screen x axis (eye or interactive frame)
		 */
		ROTATE_X("Rotate frame around screen x axis (eye or interactive frame)", false, 1),
		/**
		 * Rotate frame around screen y axis (eye or interactive frame)
		 */
		ROTATE_Y("Rotate frame around screen y axis (eye or interactive frame)", false, 1),
		/**
		 * Rotate frame around screen z axis (eye or interactive frame)
		 */
		ROTATE_Z("Rotate frame around screen z axis (eye or interactive frame)", true, 1),
		/**
		 * Drive (camera or interactive frame)
		 */
		DRIVE("Drive (camera or interactive frame)", false, 2),

		// 2 DOFs ACTIONs
		/**
		 * Frame (eye or interactive frame) arcball rotate
		 */
		ROTATE("Frame (eye or interactive frame) arcball rotate", true, 2),
		/**
		 * Rotate camera frame as in CAD applications
		 */
		ROTATE_CAD("Rotate camera frame as in CAD applications", false, 2),
		/**
		 * Translate frame (eye or interactive frame)
		 */
		TRANSLATE("Translate frame (eye or interactive frame)", true, 2),
		/**
		 * Move forward frame (camera or interactive frame)
		 */
		MOVE_FORWARD("Move forward frame (camera or interactive frame)", true, 2),
		/**
		 * Move backward frame (camera or interactive frame)
		 */
		MOVE_BACKWARD("Move backward frame (camera or interactive frame)", true, 2),
		/**
		 * Look around with frame (camera or interactive frame)
		 */
		LOOK_AROUND("Look around with frame (camera or interactive frame)", false, 2),
		/**
		 * Screen rotate (eye or interactive frame)
		 */
		SCREEN_ROTATE("Screen rotate (eye or interactive frame)", true, 2),
		/**
		 * Screen translate frame (eye or interactive frame)
		 */
		SCREEN_TRANSLATE("Screen translate frame (eye or interactive frame)", true, 2),
		/**
		 * Zoom on region (eye or interactive frame)
		 */
		ZOOM_ON_REGION("Zoom on region (eye or interactive frame)", true, 2),
		/**
		 * Translate frame (camera or interactive frame) from dx, dy, dz simultaneously
		 */
		TRANSLATE_XYZ("Translate frame (camera or interactive frame) from dx, dy, dz simultaneously", false, 3),
		/**
		 * Rotate frame (camera or interactive frame) from Euler angles
		 */
		ROTATE_XYZ("Rotate frame (camera or interactive frame) from Euler angles", false, 3),
		/**
		 * Translate frame (camera or interactive frame) from dx, dy, dz and rotate it from Euler angles simultaneously
		 */
		TRANSLATE_XYZ_ROTATE_XYZ(
				"Translate frame (camera or interactive frame) from dx, dy, dz and rotate it from Euler angles simultaneously",
				false, 6),
		/**
		 * Move camera on the surface of a sphere using 5-DOF's: 2 rotations around scene anchor, 1 rotation around scene-up
		 * vector and 1 translation along it, and 1 rotation around eye X-axis.
		 */
		HINGE("Move camera on the surface of a sphere using 5-DOF's", false, 6),

		// CUSTOM ACTIONs

		/**
		 * User defined action
		 */
		CUSTOM_CLICK_ACTION("User defined action", 0),

		/**
		 * User defined dof1-action
		 */
		CUSTOM_DOF1_ACTION("User defined dof1-action", 1),

		/**
		 * User defined dof2-action
		 */
		CUSTOM_DOF2_ACTION("User defined dof2-action", 2),

		/**
		 * User defined dof3-action
		 */
		CUSTOM_DOF3_ACTION("User defined dof3-action", 3),

		/**
		 * User defined dof6-action
		 */
		CUSTOM_DOF6_ACTION("User defined dof6-action", 6);

		String	description;
		boolean	twoD;
		int			dofs;

		MotionAction(String description, boolean td, int ds) {
			this.description = description;
			this.twoD = td;
			this.dofs = ds;
		}

		MotionAction(String description, int ds) {
			this.description = description;
			this.twoD = true;
			this.dofs = ds;
		}

		MotionAction(String description, boolean td) {
			this.description = description;
			this.twoD = td;
			this.dofs = 2;
		}

		MotionAction(String description) {
			this.description = description;
			this.twoD = true;
			this.dofs = 0;
		}

		/**
		 * Returns a description of the action item.
		 */
		public String description() {
			return description;
		}

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return twoD;
		}

		/**
		 * Returns the degrees-of-freedom needed to perform the action item.
		 */
		public int dofs() {
			return dofs;
		}
	}

	public enum SceneAction {
		// KEYfRAMES

		/**
		 * Add keyframe to path 1
		 */
		ADD_KEYFRAME_TO_PATH_1("Add keyframe to path 1", true, 0),
		/**
		 * Play path 1
		 */
		PLAY_PATH_1("Play path 1", true, 0),
		/**
		 * Delete path 1
		 */
		DELETE_PATH_1("Delete path 1", true, 0),
		/**
		 * Add keyframe to path 2
		 */
		ADD_KEYFRAME_TO_PATH_2("Add keyframe to path 2", true, 0),
		/**
		 * Play path 2
		 */
		PLAY_PATH_2("Play path 2", true, 0),
		/**
		 * Delete path 2
		 */
		DELETE_PATH_2("Delete path 2", true, 0),
		/**
		 * Add keyframe to path 3
		 */
		ADD_KEYFRAME_TO_PATH_3("Add keyframe to path 3", true, 0),
		/**
		 * Play path 3
		 */
		PLAY_PATH_3("Play path 3", true, 0),
		/**
		 * Delete path 3
		 */
		DELETE_PATH_3("Delete path 3", true, 0),

		/**
		 * Zoom to fit the scene
		 */
		INTERPOLATE_TO_FIT("Zoom to fit the scene", true, 0),

		// GENERAL KEYBOARD ACTIONs
		/**
		 * Toggles axes visual hint
		 */
		TOGGLE_AXES_VISUAL_HINT("Toggles axes visual hint", true, 0),
		/**
		 * Toggles grid visual hint
		 */
		TOGGLE_GRID_VISUAL_HINT("Toggles grid visual hint", true, 0),
		/**
		 * Toggles paths visual hint
		 */
		TOGGLE_PATHS_VISUAL_HINT("Toggles paths visual hint", true, 0),
		/**
		 * Toggles frame visual hint
		 */
		TOGGLE_PICKING_VISUAL_HINT("Toggles frame visual hint", true, 0),
		/**
		 * Toggles animation
		 */
		TOGGLE_ANIMATION("Toggles animation", true, 0),
		/**
		 * Toggles camera type
		 */
		TOGGLE_CAMERA_TYPE("Toggles camera type", false, 0),
		/**
		 * Displays the global help
		 */
		DISPLAY_INFO("Displays the global help", true, 0),
		/**
		 * Reset the anchor to the world origin
		 */
		RESET_ANCHOR("Reset the anchor to the world origin", true, 0),
		/**
		 * Show the whole scene
		 */
		SHOW_ALL("Show the whole scene", true, 0),

		// CAMERA KEYBOARD ACTIONs
		/**
		 * Move eye to the left
		 */
		MOVE_LEFT("Move eye to the left", true, 0),
		/**
		 * Move eye to the right
		 */
		MOVE_RIGHT("Move eye to the right", true, 0),
		/**
		 * Move eye up
		 */
		MOVE_UP("Move eye up", true, 0),
		/**
		 * Move eye down
		 */
		MOVE_DOWN("Move eye down", true, 0),
		/**
		 * Increase frame rotation sensitivity
		 */
		INCREASE_ROTATION_SENSITIVITY("Increase frame rotation sensitivity", true, 0),
		/**
		 * Decrease frame rotation sensitivity
		 */
		DECREASE_ROTATION_SENSITIVITY("Decrease frame rotation sensitivity", true, 0),
		/**
		 * Increase eye fly speed
		 */
		INCREASE_FLY_SPEED("Increase eye fly speed", true, 0),
		/**
		 * Decrease eye fly speed
		 */
		DECREASE_FLY_SPEED("Decrease eye fly speed", true, 0),

		// CUSTOM ACTIONs

		/**
		 * User defined keyboard-action
		 */
		CUSTOM_KEYBOARD_ACTION("User defined click-action", 0);

		String	description;
		boolean	twoD;
		int			dofs;

		SceneAction(String description, boolean td, int ds) {
			this.description = description;
			this.twoD = td;
			this.dofs = ds;
		}

		SceneAction(String description, int ds) {
			this.description = description;
			this.twoD = true;
			this.dofs = ds;
		}

		SceneAction(String description, boolean td) {
			this.description = description;
			this.twoD = td;
			this.dofs = 2;
		}

		SceneAction(String description) {
			this.description = description;
			this.twoD = true;
			this.dofs = 0;
		}

		/**
		 * Returns a description of the action item.
		 */
		public String description() {
			return description;
		}

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return twoD;
		}

		/**
		 * Returns the degrees-of-freedom needed to perform the action item.
		 */
		public int dofs() {
			return dofs;
		}
	}

	// Eye

	/**
	 * Click action sub-group.
	 */
	public enum ClickAction implements Action<MotionAction> {
		CENTER_FRAME(MotionAction.CENTER_FRAME),
		ALIGN_FRAME(MotionAction.ALIGN_FRAME),

		// Click actions require cursor pos:
		ZOOM_ON_PIXEL(MotionAction.ZOOM_ON_PIXEL),
		ANCHOR_FROM_PIXEL(MotionAction.ANCHOR_FROM_PIXEL),

		CUSTOM(MotionAction.CUSTOM_CLICK_ACTION);

		@Override
		public MotionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		@Override
		public int dofs() {
			return act.dofs();
		}

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		MotionAction	act;

		ClickAction(MotionAction a) {
			act = a;
		}
	}

	/**
	 * DOF1 action sub-group.
	 */
	public enum DOF1Action implements Action<MotionAction> {
		// DOF_1
		SCALE(MotionAction.SCALE),
		ZOOM(MotionAction.ZOOM),
		ZOOM_ON_ANCHOR(MotionAction.ZOOM_ON_ANCHOR),
		TRANSLATE_X(MotionAction.TRANSLATE_X),
		TRANSLATE_Y(MotionAction.TRANSLATE_Y),
		TRANSLATE_Z(MotionAction.TRANSLATE_Z),
		ROTATE_X(MotionAction.ROTATE_X),
		ROTATE_Y(MotionAction.ROTATE_Y),
		ROTATE_Z(MotionAction.ROTATE_Z),

		CUSTOM(MotionAction.CUSTOM_DOF1_ACTION);

		@Override
		public MotionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		@Override
		public int dofs() {
			return act.dofs();
		}

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		MotionAction	act;

		DOF1Action(MotionAction a) {
			act = a;
		}
	}

	/**
	 * DOF2 action sub-group.
	 */
	public enum DOF2Action implements Action<MotionAction> {
		// DOF_1
		SCALE(MotionAction.SCALE),
		ZOOM(MotionAction.ZOOM),
		ZOOM_ON_ANCHOR(MotionAction.ZOOM_ON_ANCHOR),
		TRANSLATE_X(MotionAction.TRANSLATE_X),
		TRANSLATE_Y(MotionAction.TRANSLATE_Y),
		TRANSLATE_Z(MotionAction.TRANSLATE_Z),
		ROTATE_X(MotionAction.ROTATE_X),
		ROTATE_Y(MotionAction.ROTATE_Y),
		ROTATE_Z(MotionAction.ROTATE_Z),

		// DOF_2
		DRIVE(MotionAction.DRIVE),
		ROTATE(MotionAction.ROTATE),
		ROTATE_CAD(MotionAction.ROTATE_CAD),
		TRANSLATE(MotionAction.TRANSLATE),
		MOVE_FORWARD(MotionAction.MOVE_FORWARD),
		MOVE_BACKWARD(MotionAction.MOVE_BACKWARD),
		LOOK_AROUND(MotionAction.LOOK_AROUND),
		SCREEN_ROTATE(MotionAction.SCREEN_ROTATE),
		SCREEN_TRANSLATE(MotionAction.SCREEN_TRANSLATE),
		ZOOM_ON_REGION(MotionAction.ZOOM_ON_REGION),

		CUSTOM(MotionAction.CUSTOM_DOF2_ACTION);

		@Override
		public MotionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		@Override
		public int dofs() {
			return act.dofs();
		}

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		MotionAction	act;

		DOF2Action(MotionAction a) {
			act = a;
		}
	}

	/**
	 * DOF3 action sub-group.
	 */
	public enum DOF3Action implements Action<MotionAction> {
		// DOF_1
		SCALE(MotionAction.SCALE),
		ZOOM(MotionAction.ZOOM),
		ZOOM_ON_ANCHOR(MotionAction.ZOOM_ON_ANCHOR),
		TRANSLATE_X(MotionAction.TRANSLATE_X),
		TRANSLATE_Y(MotionAction.TRANSLATE_Y),
		TRANSLATE_Z(MotionAction.TRANSLATE_Z),
		ROTATE_X(MotionAction.ROTATE_X),
		ROTATE_Y(MotionAction.ROTATE_Y),
		ROTATE_Z(MotionAction.ROTATE_Z),

		// DOF_2
		DRIVE(MotionAction.DRIVE),
		ROTATE(MotionAction.ROTATE),
		ROTATE_CAD(MotionAction.ROTATE_CAD),
		TRANSLATE(MotionAction.TRANSLATE),
		MOVE_FORWARD(MotionAction.MOVE_FORWARD),
		MOVE_BACKWARD(MotionAction.MOVE_BACKWARD),
		LOOK_AROUND(MotionAction.LOOK_AROUND),
		SCREEN_ROTATE(MotionAction.SCREEN_ROTATE),
		SCREEN_TRANSLATE(MotionAction.SCREEN_TRANSLATE),
		ZOOM_ON_REGION(MotionAction.ZOOM_ON_REGION),

		// DOF_3
		TRANSLATE_XYZ(MotionAction.TRANSLATE_XYZ),
		ROTATE_XYZ(MotionAction.ROTATE_XYZ),

		CUSTOM(MotionAction.CUSTOM_DOF3_ACTION);

		@Override
		public MotionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		@Override
		public int dofs() {
			return act.dofs();
		}

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		MotionAction	act;

		DOF3Action(MotionAction a) {
			act = a;
		}
	}

	/**
	 * DOF6 action sub-group.
	 */
	public enum DOF6Action implements Action<MotionAction> {
		// DOF_1
		SCALE(MotionAction.SCALE),
		ZOOM(MotionAction.ZOOM),
		ZOOM_ON_ANCHOR(MotionAction.ZOOM_ON_ANCHOR),
		TRANSLATE_X(MotionAction.TRANSLATE_X),
		TRANSLATE_Y(MotionAction.TRANSLATE_Y),
		TRANSLATE_Z(MotionAction.TRANSLATE_Z),
		ROTATE_X(MotionAction.ROTATE_X),
		ROTATE_Y(MotionAction.ROTATE_Y),
		ROTATE_Z(MotionAction.ROTATE_Z),

		// DOF_2
		ROTATE(MotionAction.ROTATE),
		DRIVE(MotionAction.DRIVE),
		ROTATE_CAD(MotionAction.ROTATE_CAD),
		TRANSLATE(MotionAction.TRANSLATE),
		MOVE_FORWARD(MotionAction.MOVE_FORWARD),
		MOVE_BACKWARD(MotionAction.MOVE_BACKWARD),
		LOOK_AROUND(MotionAction.LOOK_AROUND),
		SCREEN_ROTATE(MotionAction.SCREEN_ROTATE),
		SCREEN_TRANSLATE(MotionAction.SCREEN_TRANSLATE),
		ZOOM_ON_REGION(MotionAction.ZOOM_ON_REGION),

		// DOF_3
		TRANSLATE_XYZ(MotionAction.TRANSLATE_XYZ),
		ROTATE_XYZ(MotionAction.ROTATE_XYZ),

		// DOF_4
		HINGE(MotionAction.HINGE),

		// DOF_6
		TRANSLATE_XYZ_ROTATE_XYZ(MotionAction.TRANSLATE_XYZ_ROTATE_XYZ),

		CUSTOM(MotionAction.CUSTOM_DOF6_ACTION);

		@Override
		public MotionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		@Override
		public int dofs() {
			return act.dofs();
		}

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		MotionAction	act;

		DOF6Action(MotionAction a) {
			act = a;
		}
	}

	// Scene

	/**
	 * Keyboard action sub-group.
	 */
	public enum KeyboardAction implements Action<SceneAction> {
		// KEYfRAMES
		ADD_KEYFRAME_TO_PATH_1(SceneAction.ADD_KEYFRAME_TO_PATH_1),
		PLAY_PATH_1(SceneAction.PLAY_PATH_1),
		DELETE_PATH_1(SceneAction.DELETE_PATH_1),
		ADD_KEYFRAME_TO_PATH_2(SceneAction.ADD_KEYFRAME_TO_PATH_2),
		PLAY_PATH_2(SceneAction.PLAY_PATH_2),
		DELETE_PATH_2(SceneAction.DELETE_PATH_2),
		ADD_KEYFRAME_TO_PATH_3(SceneAction.ADD_KEYFRAME_TO_PATH_3),
		PLAY_PATH_3(SceneAction.PLAY_PATH_3),
		DELETE_PATH_3(SceneAction.DELETE_PATH_3),

		INTERPOLATE_TO_FIT(SceneAction.INTERPOLATE_TO_FIT),

		// GENERAL KEYBOARD ACTIONs
		TOGGLE_AXES_VISUAL_HINT(SceneAction.TOGGLE_AXES_VISUAL_HINT),
		TOGGLE_GRID_VISUAL_HINT(SceneAction.TOGGLE_GRID_VISUAL_HINT),
		TOGGLE_CAMERA_TYPE(SceneAction.TOGGLE_CAMERA_TYPE),
		TOGGLE_ANIMATION(SceneAction.TOGGLE_ANIMATION),
		RESET_ANCHOR(SceneAction.RESET_ANCHOR),
		DISPLAY_INFO(SceneAction.DISPLAY_INFO),
		TOGGLE_PATHS_VISUAL_HINT(SceneAction.TOGGLE_PATHS_VISUAL_HINT),
		TOGGLE_PICKING_VISUAL_HINT(SceneAction.TOGGLE_PICKING_VISUAL_HINT),
		SHOW_ALL(SceneAction.SHOW_ALL),

		// CAMERA KEYBOARD ACTIONs
		MOVE_LEFT(SceneAction.MOVE_LEFT),
		MOVE_RIGHT(SceneAction.MOVE_RIGHT),
		MOVE_UP(SceneAction.MOVE_UP),
		MOVE_DOWN(SceneAction.MOVE_DOWN),
		INCREASE_ROTATION_SENSITIVITY(SceneAction.INCREASE_ROTATION_SENSITIVITY),
		DECREASE_ROTATION_SENSITIVITY(SceneAction.DECREASE_ROTATION_SENSITIVITY),
		INCREASE_FLY_SPEED(SceneAction.INCREASE_FLY_SPEED),
		DECREASE_FLY_SPEED(SceneAction.DECREASE_FLY_SPEED),

		CUSTOM(SceneAction.CUSTOM_KEYBOARD_ACTION);

		@Override
		public SceneAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		@Override
		public int dofs() {
			return act.dofs();
		}

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		SceneAction	act;

		KeyboardAction(SceneAction a) {
			act = a;
		}
	}
}
