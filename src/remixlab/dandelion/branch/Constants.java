/*********************************************************************************
 * dandelion_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.dandelion.branch;

import remixlab.bias.branch.Action;

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

		// only keyboard

		/**
		 * Scale up frame
		 */
		SCALE_UP("Scale up frame", true),
		/**
		 * Scale down frame
		 */
		SCALE_DOWN("Scale down frame", true),
		/**
		 * Zoom in eye on anchor
		 */
		ZOOM_IN_ON_ANCHOR("Zoom in eye on anchor", false),
		/**
		 * Zoom out eye on anchor
		 */
		ZOOM_OUT_ON_ANCHOR("Zoom out eye on anchor", false),
		/**
		 * Translate frame up along X axis
		 */
		TRANSLATE_X_POS("Translate frame up along X axis", true),
		/**
		 * Translate frame down along X axis
		 */
		TRANSLATE_X_NEG("Translate frame down along X axis", true),
		/**
		 * Translate frame up along Y axis
		 */
		TRANSLATE_Y_POS("Translate frame up along Y axis", true),
		/**
		 * Translate frame down along Y axis
		 */
		TRANSLATE_Y_NEG("Translate frame down along Y axis", true),
		/**
		 * Translate frame up along Z axis
		 */
		TRANSLATE_Z_POS("Translate frame up along Z axis", false),
		/**
		 * Translate frame down along Z axis
		 */
		TRANSLATE_Z_NEG("Translate frame down along Z axis", false),
		/**
		 * Rotate frame up X axis
		 */
		ROTATE_X_POS("Rotate frame up X axis", false),
		/**
		 * Rotate frame down X axis
		 */
		ROTATE_X_NEG("Rotate frame down X axis", false),
		/**
		 * Rotate frame up Y axis
		 */
		ROTATE_Y_POS("Rotate frame up Y axis", false),
		/**
		 * Rotate frame down Y axis
		 */
		ROTATE_Y_NEG("Rotate frame down Y axis", false),
		/**
		 * Rotate frame up Z axis
		 */
		ROTATE_Z_POS("Rotate frame up Z axis", true),
		/**
		 * Rotate frame down Z axis
		 */
		ROTATE_Z_NEG("Rotate frame down Z axis", true),

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

	public enum GlobalAction {
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

		// CUSTOM ACTIONs

		/**
		 * User defined keyboard-action
		 */
		CUSTOM("User defined keyboard-action");

		String	description;
		boolean	twoD;

		GlobalAction(String description, boolean td) {
			this.description = description;
			this.twoD = td;
		}

		GlobalAction(String description) {
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

	public enum KeyboardAction implements Action<MotionAction> {
		CENTER_FRAME(MotionAction.CENTER_FRAME),
		ALIGN_FRAME(MotionAction.ALIGN_FRAME),
		// DOF_1
		SCALE_UP(MotionAction.SCALE_UP),
		SCALE_DOWN(MotionAction.SCALE_DOWN),
		ZOOM_IN_ON_ANCHOR(MotionAction.ZOOM_IN_ON_ANCHOR),
		ZOOM_OUT_ON_ANCHOR(MotionAction.ZOOM_OUT_ON_ANCHOR),
		TRANSLATE_X_POS(MotionAction.TRANSLATE_X_POS),
		TRANSLATE_Y_POS(MotionAction.TRANSLATE_Y_POS),
		TRANSLATE_Z_POS(MotionAction.TRANSLATE_Z_POS),
		TRANSLATE_X_NEG(MotionAction.TRANSLATE_X_NEG),
		TRANSLATE_Y_NEG(MotionAction.TRANSLATE_Y_NEG),
		TRANSLATE_Z_NEG(MotionAction.TRANSLATE_Z_NEG),
		ROTATE_X_POS(MotionAction.ROTATE_X_POS),
		ROTATE_X_NEG(MotionAction.ROTATE_X_NEG),
		ROTATE_Y_POS(MotionAction.ROTATE_Y_POS),
		ROTATE_Y_NEG(MotionAction.ROTATE_Y_NEG),
		ROTATE_Z_POS(MotionAction.ROTATE_Z_POS),
		ROTATE_Z_NEG(MotionAction.ROTATE_Z_NEG),

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

		KeyboardAction(MotionAction a) {
			act = a;
		}
	}

	// Scene

	/**
	 * Keyboard action sub-group.
	 */
	public enum SceneAction implements Action<GlobalAction> {
		// KEYfRAMES
		ADD_KEYFRAME_TO_PATH_1(GlobalAction.ADD_KEYFRAME_TO_PATH_1),
		PLAY_PATH_1(GlobalAction.PLAY_PATH_1),
		DELETE_PATH_1(GlobalAction.DELETE_PATH_1),
		ADD_KEYFRAME_TO_PATH_2(GlobalAction.ADD_KEYFRAME_TO_PATH_2),
		PLAY_PATH_2(GlobalAction.PLAY_PATH_2),
		DELETE_PATH_2(GlobalAction.DELETE_PATH_2),
		ADD_KEYFRAME_TO_PATH_3(GlobalAction.ADD_KEYFRAME_TO_PATH_3),
		PLAY_PATH_3(GlobalAction.PLAY_PATH_3),
		DELETE_PATH_3(GlobalAction.DELETE_PATH_3),

		INTERPOLATE_TO_FIT(GlobalAction.INTERPOLATE_TO_FIT),

		// GENERAL KEYBOARD ACTIONs
		TOGGLE_AXES_VISUAL_HINT(GlobalAction.TOGGLE_AXES_VISUAL_HINT),
		TOGGLE_GRID_VISUAL_HINT(GlobalAction.TOGGLE_GRID_VISUAL_HINT),
		TOGGLE_CAMERA_TYPE(GlobalAction.TOGGLE_CAMERA_TYPE),
		TOGGLE_ANIMATION(GlobalAction.TOGGLE_ANIMATION),
		RESET_ANCHOR(GlobalAction.RESET_ANCHOR),
		DISPLAY_INFO(GlobalAction.DISPLAY_INFO),
		TOGGLE_PATHS_VISUAL_HINT(GlobalAction.TOGGLE_PATHS_VISUAL_HINT),
		TOGGLE_PICKING_VISUAL_HINT(GlobalAction.TOGGLE_PICKING_VISUAL_HINT),
		SHOW_ALL(GlobalAction.SHOW_ALL),

		CUSTOM(GlobalAction.CUSTOM);

		@Override
		public GlobalAction referenceAction() {
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

		GlobalAction	act;

		SceneAction(GlobalAction a) {
			act = a;
		}
	}
}
