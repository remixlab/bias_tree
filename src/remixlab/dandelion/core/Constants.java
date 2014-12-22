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

public interface Constants {
	public enum EyeAction {
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

		EyeAction(String description, boolean td, int ds) {
			this.description = description;
			this.twoD = td;
			this.dofs = ds;
		}

		EyeAction(String description, int ds) {
			this.description = description;
			this.twoD = true;
			this.dofs = ds;
		}

		EyeAction(String description, boolean td) {
			this.description = description;
			this.twoD = td;
			this.dofs = 2;
		}

		EyeAction(String description) {
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

	public enum FrameAction {
		/**
		 * Center frame
		 */
		CENTER_FRAME("Center frame", true, 0),
		/**
		 * Align frame with world
		 */
		ALIGN_FRAME("Align frame with world", true, 0),

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

		FrameAction(String description, boolean td, int ds) {
			this.description = description;
			this.twoD = td;
			this.dofs = ds;
		}

		FrameAction(String description, int ds) {
			this.description = description;
			this.twoD = true;
			this.dofs = ds;
		}

		FrameAction(String description, boolean td) {
			this.description = description;
			this.twoD = td;
			this.dofs = 2;
		}

		FrameAction(String description) {
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
	public enum EyeClickAction implements Action<EyeAction> {
		CENTER_FRAME(EyeAction.CENTER_FRAME),
		ALIGN_FRAME(EyeAction.ALIGN_FRAME),

		// Click actions require cursor pos:
		ZOOM_ON_PIXEL(EyeAction.ZOOM_ON_PIXEL),
		ANCHOR_FROM_PIXEL(EyeAction.ANCHOR_FROM_PIXEL),

		CUSTOM(EyeAction.CUSTOM_CLICK_ACTION);

		@Override
		public EyeAction referenceAction() {
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

		EyeAction	act;

		EyeClickAction(EyeAction a) {
			act = a;
		}
	}

	/**
	 * DOF1 action sub-group.
	 */
	public enum EyeDOF1Action implements Action<EyeAction> {
		// DOF_1
		SCALE(EyeAction.SCALE),
		ZOOM(EyeAction.ZOOM),
		ZOOM_ON_ANCHOR(EyeAction.ZOOM_ON_ANCHOR),
		TRANSLATE_X(EyeAction.TRANSLATE_X),
		TRANSLATE_Y(EyeAction.TRANSLATE_Y),
		TRANSLATE_Z(EyeAction.TRANSLATE_Z),
		ROTATE_X(EyeAction.ROTATE_X),
		ROTATE_Y(EyeAction.ROTATE_Y),
		ROTATE_Z(EyeAction.ROTATE_Z),

		CUSTOM(EyeAction.CUSTOM_DOF1_ACTION);

		@Override
		public EyeAction referenceAction() {
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

		EyeAction	act;

		EyeDOF1Action(EyeAction a) {
			act = a;
		}
	}

	/**
	 * DOF2 action sub-group.
	 */
	public enum EyeDOF2Action implements Action<EyeAction> {
		// DOF_1
		SCALE(EyeAction.SCALE),
		ZOOM(EyeAction.ZOOM),
		ZOOM_ON_ANCHOR(EyeAction.ZOOM_ON_ANCHOR),
		TRANSLATE_X(EyeAction.TRANSLATE_X),
		TRANSLATE_Y(EyeAction.TRANSLATE_Y),
		TRANSLATE_Z(EyeAction.TRANSLATE_Z),
		ROTATE_X(EyeAction.ROTATE_X),
		ROTATE_Y(EyeAction.ROTATE_Y),
		ROTATE_Z(EyeAction.ROTATE_Z),

		// DOF_2
		DRIVE(EyeAction.DRIVE),
		ROTATE(EyeAction.ROTATE),
		ROTATE_CAD(EyeAction.ROTATE_CAD),
		TRANSLATE(EyeAction.TRANSLATE),
		MOVE_FORWARD(EyeAction.MOVE_FORWARD),
		MOVE_BACKWARD(EyeAction.MOVE_BACKWARD),
		LOOK_AROUND(EyeAction.LOOK_AROUND),
		SCREEN_ROTATE(EyeAction.SCREEN_ROTATE),
		SCREEN_TRANSLATE(EyeAction.SCREEN_TRANSLATE),
		ZOOM_ON_REGION(EyeAction.ZOOM_ON_REGION),

		CUSTOM(EyeAction.CUSTOM_DOF2_ACTION);

		@Override
		public EyeAction referenceAction() {
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

		EyeAction	act;

		EyeDOF2Action(EyeAction a) {
			act = a;
		}
	}

	/**
	 * DOF3 action sub-group.
	 */
	public enum EyeDOF3Action implements Action<EyeAction> {
		// DOF_1
		SCALE(EyeAction.SCALE),
		ZOOM(EyeAction.ZOOM),
		ZOOM_ON_ANCHOR(EyeAction.ZOOM_ON_ANCHOR),
		TRANSLATE_X(EyeAction.TRANSLATE_X),
		TRANSLATE_Y(EyeAction.TRANSLATE_Y),
		TRANSLATE_Z(EyeAction.TRANSLATE_Z),
		ROTATE_X(EyeAction.ROTATE_X),
		ROTATE_Y(EyeAction.ROTATE_Y),
		ROTATE_Z(EyeAction.ROTATE_Z),

		// DOF_2
		DRIVE(EyeAction.DRIVE),
		ROTATE(EyeAction.ROTATE),
		ROTATE_CAD(EyeAction.ROTATE_CAD),
		TRANSLATE(EyeAction.TRANSLATE),
		MOVE_FORWARD(EyeAction.MOVE_FORWARD),
		MOVE_BACKWARD(EyeAction.MOVE_BACKWARD),
		LOOK_AROUND(EyeAction.LOOK_AROUND),
		SCREEN_ROTATE(EyeAction.SCREEN_ROTATE),
		SCREEN_TRANSLATE(EyeAction.SCREEN_TRANSLATE),
		ZOOM_ON_REGION(EyeAction.ZOOM_ON_REGION),

		// DOF_3
		TRANSLATE_XYZ(EyeAction.TRANSLATE_XYZ),
		ROTATE_XYZ(EyeAction.ROTATE_XYZ),

		CUSTOM(EyeAction.CUSTOM_DOF3_ACTION);

		@Override
		public EyeAction referenceAction() {
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

		EyeAction	act;

		EyeDOF3Action(EyeAction a) {
			act = a;
		}
	}

	/**
	 * DOF6 action sub-group.
	 */
	public enum EyeDOF6Action implements Action<EyeAction> {
		// DOF_1
		SCALE(EyeAction.SCALE),
		ZOOM(EyeAction.ZOOM),
		ZOOM_ON_ANCHOR(EyeAction.ZOOM_ON_ANCHOR),
		TRANSLATE_X(EyeAction.TRANSLATE_X),
		TRANSLATE_Y(EyeAction.TRANSLATE_Y),
		TRANSLATE_Z(EyeAction.TRANSLATE_Z),
		ROTATE_X(EyeAction.ROTATE_X),
		ROTATE_Y(EyeAction.ROTATE_Y),
		ROTATE_Z(EyeAction.ROTATE_Z),

		// DOF_2
		ROTATE(EyeAction.ROTATE),
		DRIVE(EyeAction.DRIVE),
		ROTATE_CAD(EyeAction.ROTATE_CAD),
		TRANSLATE(EyeAction.TRANSLATE),
		MOVE_FORWARD(EyeAction.MOVE_FORWARD),
		MOVE_BACKWARD(EyeAction.MOVE_BACKWARD),
		LOOK_AROUND(EyeAction.LOOK_AROUND),
		SCREEN_ROTATE(EyeAction.SCREEN_ROTATE),
		SCREEN_TRANSLATE(EyeAction.SCREEN_TRANSLATE),
		ZOOM_ON_REGION(EyeAction.ZOOM_ON_REGION),

		// DOF_3
		TRANSLATE_XYZ(EyeAction.TRANSLATE_XYZ),
		ROTATE_XYZ(EyeAction.ROTATE_XYZ),

		// DOF_4
		HINGE(EyeAction.HINGE),

		// DOF_6
		TRANSLATE_XYZ_ROTATE_XYZ(EyeAction.TRANSLATE_XYZ_ROTATE_XYZ),

		CUSTOM(EyeAction.CUSTOM_DOF6_ACTION);

		@Override
		public EyeAction referenceAction() {
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

		EyeAction	act;

		EyeDOF6Action(EyeAction a) {
			act = a;
		}
	}

	// Frame

	/**
	 * Click action sub-group.
	 */
	public enum FrameClickAction implements Action<FrameAction> {
		CENTER_FRAME(FrameAction.CENTER_FRAME),
		ALIGN_FRAME(FrameAction.ALIGN_FRAME),

		CUSTOM(FrameAction.CUSTOM_CLICK_ACTION);

		@Override
		public FrameAction referenceAction() {
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

		FrameAction	act;

		FrameClickAction(FrameAction a) {
			act = a;
		}
	}

	/**
	 * DOF1 action sub-group.
	 */
	public enum FrameDOF1Action implements Action<FrameAction> {
		// DOF_1
		SCALE(FrameAction.SCALE),
		ZOOM(FrameAction.ZOOM),
		ZOOM_ON_ANCHOR(FrameAction.ZOOM_ON_ANCHOR),
		TRANSLATE_X(FrameAction.TRANSLATE_X),
		TRANSLATE_Y(FrameAction.TRANSLATE_Y),
		TRANSLATE_Z(FrameAction.TRANSLATE_Z),
		ROTATE_X(FrameAction.ROTATE_X),
		ROTATE_Y(FrameAction.ROTATE_Y),
		ROTATE_Z(FrameAction.ROTATE_Z),

		CUSTOM(FrameAction.CUSTOM_DOF1_ACTION);

		@Override
		public FrameAction referenceAction() {
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

		FrameAction	act;

		FrameDOF1Action(FrameAction a) {
			act = a;
		}
	}

	/**
	 * DOF2 action sub-group.
	 */
	public enum FrameDOF2Action implements Action<FrameAction> {
		// DOF_1
		SCALE(FrameAction.SCALE),
		ZOOM(FrameAction.ZOOM),
		ZOOM_ON_ANCHOR(FrameAction.ZOOM_ON_ANCHOR),
		TRANSLATE_X(FrameAction.TRANSLATE_X),
		TRANSLATE_Y(FrameAction.TRANSLATE_Y),
		TRANSLATE_Z(FrameAction.TRANSLATE_Z),
		ROTATE_X(FrameAction.ROTATE_X),
		ROTATE_Y(FrameAction.ROTATE_Y),
		ROTATE_Z(FrameAction.ROTATE_Z),

		// DOF_2
		DRIVE(FrameAction.DRIVE),
		ROTATE(FrameAction.ROTATE),
		TRANSLATE(FrameAction.TRANSLATE),
		MOVE_FORWARD(FrameAction.MOVE_FORWARD),
		MOVE_BACKWARD(FrameAction.MOVE_BACKWARD),
		LOOK_AROUND(FrameAction.LOOK_AROUND),
		SCREEN_ROTATE(FrameAction.SCREEN_ROTATE),
		SCREEN_TRANSLATE(FrameAction.SCREEN_TRANSLATE),
		ZOOM_ON_REGION(FrameAction.ZOOM_ON_REGION),

		CUSTOM(FrameAction.CUSTOM_DOF2_ACTION);

		@Override
		public FrameAction referenceAction() {
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

		FrameAction	act;

		FrameDOF2Action(FrameAction a) {
			act = a;
		}
	}

	/**
	 * DOF3 action sub-group.
	 */
	public enum FrameDOF3Action implements Action<FrameAction> {
		// DOF_1
		SCALE(FrameAction.SCALE),
		ZOOM(FrameAction.ZOOM),
		ZOOM_ON_ANCHOR(FrameAction.ZOOM_ON_ANCHOR),
		TRANSLATE_X(FrameAction.TRANSLATE_X),
		TRANSLATE_Y(FrameAction.TRANSLATE_Y),
		TRANSLATE_Z(FrameAction.TRANSLATE_Z),
		ROTATE_X(FrameAction.ROTATE_X),
		ROTATE_Y(FrameAction.ROTATE_Y),
		ROTATE_Z(FrameAction.ROTATE_Z),

		// DOF_2
		DRIVE(FrameAction.DRIVE),
		ROTATE(FrameAction.ROTATE),
		TRANSLATE(FrameAction.TRANSLATE),
		MOVE_FORWARD(FrameAction.MOVE_FORWARD),
		MOVE_BACKWARD(FrameAction.MOVE_BACKWARD),
		LOOK_AROUND(FrameAction.LOOK_AROUND),
		SCREEN_ROTATE(FrameAction.SCREEN_ROTATE),
		SCREEN_TRANSLATE(FrameAction.SCREEN_TRANSLATE),
		ZOOM_ON_REGION(FrameAction.ZOOM_ON_REGION),

		// DOF_3
		TRANSLATE_XYZ(FrameAction.TRANSLATE_XYZ),
		ROTATE_XYZ(FrameAction.ROTATE_XYZ),

		CUSTOM(FrameAction.CUSTOM_DOF3_ACTION);

		@Override
		public FrameAction referenceAction() {
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

		FrameAction	act;

		FrameDOF3Action(FrameAction a) {
			act = a;
		}
	}

	/**
	 * DOF6 action sub-group.
	 */
	public enum FrameDOF6Action implements Action<FrameAction> {
		// DOF_1
		SCALE(FrameAction.SCALE),
		ZOOM(FrameAction.ZOOM),
		ZOOM_ON_ANCHOR(FrameAction.ZOOM_ON_ANCHOR),
		TRANSLATE_X(FrameAction.TRANSLATE_X),
		TRANSLATE_Y(FrameAction.TRANSLATE_Y),
		TRANSLATE_Z(FrameAction.TRANSLATE_Z),
		ROTATE_X(FrameAction.ROTATE_X),
		ROTATE_Y(FrameAction.ROTATE_Y),
		ROTATE_Z(FrameAction.ROTATE_Z),

		// DOF_2
		ROTATE(FrameAction.ROTATE),
		DRIVE(FrameAction.DRIVE),
		TRANSLATE(FrameAction.TRANSLATE),
		MOVE_FORWARD(FrameAction.MOVE_FORWARD),
		MOVE_BACKWARD(FrameAction.MOVE_BACKWARD),
		LOOK_AROUND(FrameAction.LOOK_AROUND),
		SCREEN_ROTATE(FrameAction.SCREEN_ROTATE),
		SCREEN_TRANSLATE(FrameAction.SCREEN_TRANSLATE),
		ZOOM_ON_REGION(FrameAction.ZOOM_ON_REGION),

		// DOF_3
		TRANSLATE_XYZ(FrameAction.TRANSLATE_XYZ),
		ROTATE_XYZ(FrameAction.ROTATE_XYZ),

		// DOF_6
		TRANSLATE_XYZ_ROTATE_XYZ(FrameAction.TRANSLATE_XYZ_ROTATE_XYZ),

		CUSTOM(FrameAction.CUSTOM_DOF6_ACTION);

		@Override
		public FrameAction referenceAction() {
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

		FrameAction	act;

		FrameDOF6Action(FrameAction a) {
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
