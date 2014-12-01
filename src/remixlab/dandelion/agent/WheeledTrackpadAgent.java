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

import remixlab.bias.agent.profile.*;
import remixlab.bias.event.MotionEvent;
import remixlab.dandelion.core.*;

/**
 * An {@link remixlab.dandelion.agent.ActionWheeledBiMotionAgent} representing a Wheeled mouse and thus only holds 2
 * Degrees-Of-Freedom (e.g., two translations or two rotations), such as most mice.
 */
public class WheeledTrackpadAgent extends WheeledPointingAgent {
	/**
	 * Constructs a PointingAgent. Nothing fancy.
	 * 
	 * @param scn
	 *          AbstractScene
	 * @param n
	 *          Agents name
	 */
	public WheeledTrackpadAgent(AbstractScene scn, String n) {
		super(scn, n);
	}

	// HIGH-LEVEL

	/**
	 * Set mouse bindings as 'arcball':
	 * <p>
	 * 1. <b>InteractiveFrame bindings</b><br>
	 * Left button -> ROTATE<br>
	 * Center button -> SCALE<br>
	 * Right button -> TRANSLATE<br>
	 * Shift + Center button -> SCREEN_TRANSLATE<br>
	 * Shift + Right button -> SCREEN_ROTATE<br>
	 * <p>
	 * 2. <b>InteractiveEyeFrame bindings</b><br>
	 * Left button -> ROTATE<br>
	 * Center button -> ZOOM<br>
	 * Right button -> TRANSLATE<br>
	 * Shift + Left button -> ZOOM_ON_REGION<br>
	 * Shift + Center button -> SCREEN_TRANSLATE<br>
	 * Shift + Right button -> SCREEN_ROTATE.
	 * <p>
	 * Also set the following (common) bindings are:
	 * <p>
	 * 2 left clicks -> ALIGN_FRAME<br>
	 * 2right clicks -> CENTER_FRAME<br>
	 * Wheel in 2D -> SCALE both, InteractiveFrame and InteractiveEyeFrame<br>
	 * Wheel in 3D -> SCALE InteractiveFrame, and ZOOM InteractiveEyeFrame<br>
	 * 
	 * @see #setAsFirstPerson()
	 * @see #setAsThirdPerson()
	 */
	@Override
	public void setAsArcball() {
		resetAllProfiles();
		eyeProfile().setBinding(DOF2Action.ROTATE);
		eyeProfile().setBinding(MotionEvent.SHIFT, MotionEvent.NOBUTTON, scene.is3D() ? DOF2Action.ZOOM : DOF2Action.SCALE);
		eyeProfile().setBinding(MotionEvent.CTRL, MotionEvent.NOBUTTON, DOF2Action.TRANSLATE);
		eyeProfile().setBinding((MotionEvent.CTRL | MotionEvent.SHIFT), MotionEvent.NOBUTTON, DOF2Action.ZOOM_ON_REGION);
		setButtonBinding(Target.EYE, center, DOF2Action.SCREEN_TRANSLATE);
		setButtonBinding(Target.EYE, right, DOF2Action.SCREEN_ROTATE);
		eyeProfile().setBinding(MotionEvent.ALT, MotionEvent.NOBUTTON, null);

		frameProfile().setBinding(DOF2Action.ROTATE);
		frameProfile().setBinding(MotionEvent.SHIFT, MotionEvent.NOBUTTON, DOF2Action.SCALE);
		frameProfile().setBinding(MotionEvent.CTRL, MotionEvent.NOBUTTON, DOF2Action.TRANSLATE);
		setButtonBinding(Target.FRAME, center, DOF2Action.SCREEN_TRANSLATE);
		setButtonBinding(Target.FRAME, right, DOF2Action.SCREEN_ROTATE);
		frameProfile().setBinding(MotionEvent.ALT, MotionEvent.NOBUTTON, null);
		setCommonBindings();
	}

	/**
	 * Set mouse bindings as 'first-person':
	 * <p>
	 * 1. <b>InteractiveFrame bindings</b><br>
	 * Left button -> ROTATE<br>
	 * Center button -> SCALE<br>
	 * Right button -> TRANSLATE<br>
	 * Shift + Center button -> SCREEN_TRANSLATE<br>
	 * Shift + Right button -> SCREEN_ROTATE<br>
	 * <p>
	 * 2. <b>InteractiveEyeFrame bindings</b><br>
	 * Left button -> MOVE_FORWARD<br>
	 * Center button -> LOOK_AROUND<br>
	 * Right button -> MOVE_BACKWARD<br>
	 * Shift + Left button -> ROLL<br>
	 * Shift + Center button -> DRIVE<br>
	 * Ctrl + Wheel -> ROLL<br>
	 * Shift + Wheel -> DRIVE<br>
	 * <p>
	 * Also set the following (common) bindings are:
	 * <p>
	 * 2 left clicks -> ALIGN_FRAME<br>
	 * 2right clicks -> CENTER_FRAME<br>
	 * Wheel in 2D -> SCALE both, InteractiveFrame and InteractiveEyeFrame<br>
	 * Wheel in 3D -> SCALE InteractiveFrame, and ZOOM InteractiveEyeFrame<br>
	 * 
	 * @see #setAsArcball()
	 * @see #setAsThirdPerson()
	 */
	@Override
	public void setAsFirstPerson() {
		resetAllProfiles();
		eyeProfile().setBinding(MotionEvent.CTRL, MotionEvent.NOBUTTON, DOF2Action.MOVE_FORWARD);
		eyeProfile().setBinding(MotionEvent.SHIFT, MotionEvent.NOBUTTON, DOF2Action.MOVE_BACKWARD);
		eyeProfile().setBinding(MotionEvent.ALT, MotionEvent.NOBUTTON, null);
		eyeWheelProfile().setBinding((MotionEvent.CTRL | MotionEvent.SHIFT), DOF1Action.ROTATE_Z);
		if (scene.is3D()) {
			eyeProfile().setBinding(DOF2Action.LOOK_AROUND);
			eyeProfile().setBinding((MotionEvent.CTRL | MotionEvent.SHIFT), MotionEvent.NOBUTTON, DOF2Action.DRIVE);
		}
		frameProfile().setBinding(DOF2Action.ROTATE);
		frameProfile().setBinding(MotionEvent.SHIFT, MotionEvent.NOBUTTON, DOF2Action.SCALE);
		frameProfile().setBinding(MotionEvent.CTRL, MotionEvent.NOBUTTON, DOF2Action.TRANSLATE);
		frameProfile().setBinding(MotionEvent.ALT, MotionEvent.NOBUTTON, null);
		setButtonBinding(Target.FRAME, center, DOF2Action.SCREEN_TRANSLATE);
		setButtonBinding(Target.FRAME, right, DOF2Action.SCREEN_ROTATE);
		setCommonBindings();
	}

	/**
	 * Set mouse bindings as third-person:
	 * <p>
	 * Left button -> MOVE_FORWARD<br>
	 * Center button -> LOOK_AROUND<br>
	 * Right button -> MOVE_BACKWARD<br>
	 * Shift + Left button -> ROLL<br>
	 * Shift + Center button -> DRIVE<br>
	 * <p>
	 * Also set the following (common) bindings are:
	 * <p>
	 * 2 left clicks -> ALIGN_FRAME<br>
	 * 2right clicks -> CENTER_FRAME<br>
	 * Wheel in 2D -> SCALE both, InteractiveFrame and InteractiveEyeFrame<br>
	 * Wheel in 3D -> SCALE InteractiveFrame, and ZOOM InteractiveEyeFrame<br>
	 * 
	 * @see #setAsArcball()
	 * @see #setAsFirstPerson()
	 */
	@Override
	public void setAsThirdPerson() {
		resetAllProfiles();
		frameProfile().setBinding(MotionEvent.CTRL, MotionEvent.NOBUTTON, DOF2Action.MOVE_FORWARD);
		frameProfile().setBinding(MotionEvent.SHIFT, MotionEvent.NOBUTTON, DOF2Action.MOVE_BACKWARD);
		frameWheelProfile().setBinding((MotionEvent.CTRL | MotionEvent.SHIFT), DOF1Action.ROTATE_Z);
		frameProfile().setBinding(MotionEvent.CTRL, MotionEvent.NOBUTTON, null);
		if (scene.is3D()) {
			frameProfile().setBinding(DOF2Action.LOOK_AROUND);
			frameProfile().setBinding((MotionEvent.CTRL | MotionEvent.SHIFT), MotionEvent.NOBUTTON, DOF2Action.DRIVE);
		}
		setCommonBindings();
	}

	/**
	 * Set the following (common) bindings:
	 * <p>
	 * 2 left clicks -> ALIGN_FRAME<br>
	 * 2right clicks -> CENTER_FRAME<br>
	 * Wheel in 2D -> SCALE both, InteractiveFrame and InteractiveEyeFrame<br>
	 * Wheel in 3D -> SCALE InteractiveFrame, and ZOOM InteractiveEyeFrame<br>
	 * <p>
	 * which are used in {@link #setAsArcball()}, {@link #setAsFirstPerson()} and {@link #setAsThirdPerson()}
	 */
	protected void setCommonBindings() {
		eyeClickProfile().setBinding(buttonModifiersFix(left), left, 2, ClickAction.ALIGN_FRAME);
		eyeClickProfile().setBinding(buttonModifiersFix(right), right, 2, ClickAction.CENTER_FRAME);
		frameClickProfile().setBinding(buttonModifiersFix(left), left, 2, ClickAction.ALIGN_FRAME);
		frameClickProfile().setBinding(buttonModifiersFix(right), right, 2, ClickAction.CENTER_FRAME);
		eyeWheelProfile().setBinding(MotionEvent.NOMODIFIER_MASK, MotionEvent.NOBUTTON,
				scene.is3D() ? DOF1Action.ZOOM : DOF1Action.SCALE);
		frameWheelProfile().setBinding(MotionEvent.NOMODIFIER_MASK, MotionEvent.NOBUTTON, DOF1Action.SCALE);
	}

	// WRAPPERS

	/**
	 * Binds the mouse shortcut to the (DOF2) dandelion action to be performed by the given {@code target} (EYE or FRAME).
	 */
	public void setBinding(Target target, DOF2Action action) {
		if (action == DOF2Action.ZOOM_ON_REGION) {
			AbstractScene.showMissingImplementationWarning(action.name(), WheeledMouseAgent.class.getName());
			return;
		}
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		profile.setBinding(MotionEvent.NOMODIFIER_MASK, MotionEvent.NOBUTTON, action);
	}

	/**
	 * Binds the mouse shortcut to the (DOF2) dandelion action to be performed by the given {@code target} (EYE or FRAME).
	 */
	public void setBinding(Target target, int mask, DOF2Action action) {
		if (action == DOF2Action.ZOOM_ON_REGION) {
			AbstractScene.showMissingImplementationWarning(action.name(), WheeledMouseAgent.class.getName());
			return;
		}
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		profile.setBinding(mask, MotionEvent.NOBUTTON, action);
	}

	/**
	 * Removes the mouse shortcut binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeBinding(Target target) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		profile.removeBinding(MotionEvent.NOMODIFIER_MASK, MotionEvent.NOBUTTON);
	}

	/**
	 * Removes the mouse shortcut binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeBinding(Target target, int mask) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		profile.removeBinding(mask, MotionEvent.NOBUTTON);
	}

	/**
	 * Returns {@code true} if the mouse shortcut is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean hasBinding(Target target) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		return profile.hasBinding(MotionEvent.NOMODIFIER_MASK, MotionEvent.NOBUTTON);
	}

	/**
	 * Returns {@code true} if the mouse shortcut is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean hasBinding(Target target, int mask) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		return profile.hasBinding(mask, MotionEvent.NOBUTTON);
	}

	/**
	 * Returns the (DOF2) dandelion action to be performed by the given {@code target} (EYE or FRAME) that is bound to the
	 * given mouse shortcut. Returns {@code null} if no action is bound to the given shortcut.
	 */
	public DOF2Action action(Target target) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		return (DOF2Action) profile.action(MotionEvent.NOMODIFIER_MASK, MotionEvent.NOBUTTON);
	}

	/**
	 * Returns the (DOF2) dandelion action to be performed by the given {@code target} (EYE or FRAME) that is bound to the
	 * given mouse shortcut. Returns {@code null} if no action is bound to the given shortcut.
	 */
	public DOF2Action action(Target target, int mask) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		return (DOF2Action) profile.action(mask, MotionEvent.NOBUTTON);
	}
}
