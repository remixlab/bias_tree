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
		CENTER_FRAME("Center frame", true),
		/**
		 * Align frame with world
		 */
		ALIGN_FRAME("Align frame with world", true),

		// Click actions require cursor pos:
		/**
		 * Interpolate the eye to zoom on pixel
		 */
		ZOOM_ON_PIXEL("Interpolate the eye to zoom on pixel", true),
		/**
		 * Set the anchor from the pixel under the pointer
		 */
		ANCHOR_FROM_PIXEL("Set the anchor from the pixel under the pointer", true),

		// Wheel

		/**
		 * custom Scale frame
		 */
		SCALE("Scale frame", true),
		/**
		 * Zoom eye
		 */
		ZOOM("Zoom eye", false),
		/**
		 * Zoom eye on anchor
		 */
		ZOOM_ON_ANCHOR("Zoom eye on anchor", false),
		/**
		 * Translate along screen X axis
		 */
		TRANSLATE_X("Translate along screen X axis", true),
		/**
		 * Translate along screen Y axis
		 */
		TRANSLATE_Y("Translate along screen Y axis", true),
		/**
		 * Translate along screen Z axis
		 */
		TRANSLATE_Z("Translate along screen Z axis", false),
		/**
		 * Rotate frame around screen x axis (eye or interactive frame)
		 */
		ROTATE_X("Rotate frame around screen x axis (eye or interactive frame)", false),
		/**
		 * Rotate frame around screen y axis (eye or interactive frame)
		 */
		ROTATE_Y("Rotate frame around screen y axis (eye or interactive frame)", false),
		/**
		 * Rotate frame around screen z axis (eye or interactive frame)
		 */
		ROTATE_Z("Rotate frame around screen z axis (eye or interactive frame)", true),
		/**
		 * Drive (camera or interactive frame)
		 */
		DRIVE("Drive (camera or interactive frame)", false),

		// 2 DOFs ACTIONs
		/**
		 * Frame (eye or interactive frame) arcball rotate
		 */
		ROTATE("Frame (eye or interactive frame) arcball rotate", true),
		/**
		 * Rotate camera frame as in CAD applications
		 */
		ROTATE_CAD("Rotate camera frame as in CAD applications", false),
		/**
		 * Translate frame (eye or interactive frame)
		 */
		TRANSLATE("Translate frame (eye or interactive frame)", true),
		/**
		 * Move forward frame (camera or interactive frame)
		 */
		MOVE_FORWARD("Move forward frame (camera or interactive frame)", true),
		/**
		 * Move backward frame (camera or interactive frame)
		 */
		MOVE_BACKWARD("Move backward frame (camera or interactive frame)", true),
		/**
		 * Look around with frame (camera or interactive frame)
		 */
		LOOK_AROUND("Look around with frame (camera or interactive frame)", false),
		/**
		 * Screen rotate (eye or interactive frame)
		 */
		SCREEN_ROTATE("Screen rotate (eye or interactive frame)", true),
		/**
		 * Screen translate frame (eye or interactive frame)
		 */
		SCREEN_TRANSLATE("Screen translate frame (eye or interactive frame)", true),
		/**
		 * Zoom on region (eye or interactive frame)
		 */
		ZOOM_ON_REGION("Zoom on region (eye or interactive frame)", true),
		/**
		 * Translate frame (camera or interactive frame) from dx, dy, dz simultaneously
		 */
		TRANSLATE_XYZ("Translate frame (camera or interactive frame) from dx, dy, dz simultaneously", false),
		/**
		 * Rotate frame (camera or interactive frame) from Euler angles
		 */
		ROTATE_XYZ("Rotate frame (camera or interactive frame) from Euler angles", false),
		/**
		 * Translate frame (camera or interactive frame) from dx, dy, dz and rotate it from Euler angles simultaneously
		 */
		TRANSLATE_XYZ_ROTATE_XYZ(
				"Translate frame (camera or interactive frame) from dx, dy, dz and rotate it from Euler angles simultaneously",
				false),
		/**
		 * Move camera on the surface of a sphere using 5-DOF's: 2 rotations around scene anchor, 1 rotation around scene-up
		 * vector and 1 translation along it, and 1 rotation around eye X-axis.
		 */
		HINGE("Move camera on the surface of a sphere using 5-DOF's", false),

		// CUSTOM ACTIONs

		/**
		 * User defined motion-action
		 */
		CUSTOM("User defined motion-action");

		String	description;
		boolean	twoD;

		MotionAction(String description, boolean td) {
			this.description = description;
			this.twoD = td;
		}

		MotionAction(String description) {
			this.description = description;
			this.twoD = true;
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
	}

	public enum SceneAction {
		// KEYfRAMES

		/**
		 * Add keyframe to path 1
		 */
		ADD_KEYFRAME_TO_PATH_1("Add keyframe to path 1", true),
		/**
		 * Play path 1
		 */
		PLAY_PATH_1("Play path 1", true),
		/**
		 * Delete path 1
		 */
		DELETE_PATH_1("Delete path 1", true),
		/**
		 * Add keyframe to path 2
		 */
		ADD_KEYFRAME_TO_PATH_2("Add keyframe to path 2", true),
		/**
		 * Play path 2
		 */
		PLAY_PATH_2("Play path 2", true),
		/**
		 * Delete path 2
		 */
		DELETE_PATH_2("Delete path 2", true),
		/**
		 * Add keyframe to path 3
		 */
		ADD_KEYFRAME_TO_PATH_3("Add keyframe to path 3", true),
		/**
		 * Play path 3
		 */
		PLAY_PATH_3("Play path 3", true),
		/**
		 * Delete path 3
		 */
		DELETE_PATH_3("Delete path 3", true),

		/**
		 * Zoom to fit the scene
		 */
		INTERPOLATE_TO_FIT("Zoom to fit the scene", true),

		// GENERAL KEYBOARD ACTIONs
		/**
		 * Toggles axes visual hint
		 */
		TOGGLE_AXES_VISUAL_HINT("Toggles axes visual hint", true),
		/**
		 * Toggles grid visual hint
		 */
		TOGGLE_GRID_VISUAL_HINT("Toggles grid visual hint", true),
		/**
		 * Toggles paths visual hint
		 */
		TOGGLE_PATHS_VISUAL_HINT("Toggles paths visual hint", true),
		/**
		 * Toggles frame visual hint
		 */
		TOGGLE_PICKING_VISUAL_HINT("Toggles frame visual hint", true),
		/**
		 * Toggles animation
		 */
		TOGGLE_ANIMATION("Toggles animation", true),
		/**
		 * Toggles camera type
		 */
		TOGGLE_CAMERA_TYPE("Toggles camera type", false),
		/**
		 * Displays the global help
		 */
		DISPLAY_INFO("Displays the global help", true),
		/**
		 * Reset the anchor to the world origin
		 */
		RESET_ANCHOR("Reset the anchor to the world origin", true),
		/**
		 * Show the whole scene
		 */
		SHOW_ALL("Show the whole scene", true),

		// CAMERA KEYBOARD ACTIONs
		/**
		 * Move eye to the left
		 */
		MOVE_LEFT("Move eye to the left", true),
		/**
		 * Move eye to the right
		 */
		MOVE_RIGHT("Move eye to the right", true),
		/**
		 * Move eye up
		 */
		MOVE_UP("Move eye up", true),
		/**
		 * Move eye down
		 */
		MOVE_DOWN("Move eye down", true),
		/**
		 * Increase frame rotation sensitivity
		 */
		INCREASE_ROTATION_SENSITIVITY("Increase frame rotation sensitivity", true),
		/**
		 * Decrease frame rotation sensitivity
		 */
		DECREASE_ROTATION_SENSITIVITY("Decrease frame rotation sensitivity", true),
		/**
		 * Increase eye fly speed
		 */
		INCREASE_FLY_SPEED("Increase eye fly speed", true),
		/**
		 * Decrease eye fly speed
		 */
		DECREASE_FLY_SPEED("Decrease eye fly speed", true),

		// CUSTOM ACTIONs

		/**
		 * User defined keyboard-action
		 */
		CUSTOM("User defined keyboard-action");

		String	description;
		boolean	twoD;

		SceneAction(String description, boolean td) {
			this.description = description;
			this.twoD = td;
		}

		SceneAction(String description) {
			this.description = description;
			this.twoD = true;
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

		CUSTOM(MotionAction.CUSTOM);

		@Override
		public MotionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
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

		CUSTOM(MotionAction.CUSTOM);

		@Override
		public MotionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		public DOF2Action dof2Action() {
			return DOF2Action.valueOf(this.toString());
		}

		public DOF3Action dof3Action() {
			return DOF3Action.valueOf(this.toString());
		}

		public DOF6Action dof6Action() {
			return DOF6Action.valueOf(this.toString());
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

		CUSTOM(MotionAction.CUSTOM);

		@Override
		public MotionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		public DOF3Action dof3Action() {
			return DOF3Action.valueOf(this.toString());
		}

		public DOF6Action dof6Action() {
			return DOF6Action.valueOf(this.toString());
		}

		public DOF1Action dof1Action() {
			DOF1Action dof1Action = null;
			try {
				dof1Action = DOF1Action.valueOf(this.toString());
			} catch (IllegalArgumentException e) {
				System.out.println("non-existant DOF1Action");
			}
			return dof1Action;
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

		CUSTOM(MotionAction.CUSTOM);

		@Override
		public MotionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		public DOF6Action dof6Action() {
			return DOF6Action.valueOf(this.toString());
		}

		public DOF1Action dof1Action() {
			DOF1Action dof1Action = null;
			try {
				dof1Action = DOF1Action.valueOf(this.toString());
			} catch (IllegalArgumentException e) {
				System.out.println("non-existant DOF1Action");
			}
			return dof1Action;
		}

		public DOF2Action dof2Action() {
			DOF2Action dof2Action = null;
			try {
				dof2Action = DOF2Action.valueOf(this.toString());
			} catch (IllegalArgumentException e) {
				System.out.println("non-existant DOF2Action");
			}
			return dof2Action;
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

		CUSTOM(MotionAction.CUSTOM);

		@Override
		public MotionAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
		}

		/**
		 * Whether or not this action item is available in 2D. All actions are available in 3D.
		 */
		public boolean is2D() {
			return act.is2D();
		}

		public DOF1Action dof1Action() {
			DOF1Action dof1Action = null;
			try {
				dof1Action = DOF1Action.valueOf(this.toString());
			} catch (IllegalArgumentException e) {
				System.out.println("non-existant DOF1Action");
			}
			return dof1Action;
		}

		public DOF2Action dof2Action() {
			DOF2Action dof2Action = null;
			try {
				dof2Action = DOF2Action.valueOf(this.toString());
			} catch (IllegalArgumentException e) {
				System.out.println("non-existant DOF2Action");
			}
			return dof2Action;
		}

		public DOF3Action dof3Action() {
			DOF3Action dof3Action = null;
			try {
				dof3Action = DOF3Action.valueOf(this.toString());
			} catch (IllegalArgumentException e) {
				System.out.println("non-existant DOF3Action");
			}
			return dof3Action;
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

		CUSTOM(SceneAction.CUSTOM);

		@Override
		public SceneAction referenceAction() {
			return act;
		}

		@Override
		public String description() {
			return this.referenceAction().description();
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
