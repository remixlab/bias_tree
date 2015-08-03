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

import java.util.Iterator;
import java.util.Map.Entry;

import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

/**
 * A {@code MotionAgent<DOF2Action>}, such as most mice, supporting
 * {@link remixlab.dandelion.core.Constants.ClickAction}s, and up to
 * {@link remixlab.dandelion.core.Constants.DOF2Action}s actions.
 * <p>
 * @see remixlab.dandelion.core.Constants.ClickAction
 * @see remixlab.dandelion.core.Constants.DOF1Action
 * @see remixlab.dandelion.core.Constants.DOF2Action
 */
public abstract class WheeledMouseAgent extends MotionAgent<DOF2Action> {
	protected float		xSens		= 1f;
	protected float		ySens		= 1f;

	/**
	 * Creates a mouse agent and appends the {@link #eyeBranch()} and {@link #frameBranch()} to it.
	 * The mouse agent is added to the {@link remixlab.dandelion.core.AbstractScene#inputHandler()}.
	 */
	public WheeledMouseAgent(AbstractScene scn, String n) {
		super(scn, n);
	}

	@Override
	public float[] sensitivities(MotionEvent event) {
		if (event instanceof DOF2Event)
			return new float[] { xSens, ySens, 1f, 1f, 1f, 1f };
		else
			return super.sensitivities(event);
	}

	/**
	 * Defines the {@link #xSensitivity()}.
	 */
	public void setXSensitivity(float sensitivity) {
		xSens = sensitivity;
	}

	/**
	 * Returns the x sensitivity.
	 * <p>
	 * Default value is 1. A higher value will make the event more efficient (usually meaning a faster motion). Use a
	 * negative value to invert the along x-Axis motion direction.
	 * 
	 * @see #setWheelSensitivity(float)
	 */
	public float xSensitivity() {
		return xSens;
	}

	/**
	 * Defines the {@link #ySensitivity()}.
	 */
	public void setYSensitivity(float sensitivity) {
		ySens = sensitivity;
	}

	/**
	 * Returns the y sensitivity.
	 * <p>
	 * Default value is 1. A higher value will make the event more efficient (usually meaning a faster motion). Use a
	 * negative value to invert the along y-Axis motion direction.
	 * 
	 * @see #setWheelSensitivity(float)
	 */
	public float ySensitivity() {
		return ySens;
	}

	// high-level API

	/**
	 * Set mouse bindings as 'arcball'. Bindings are as follows:
	 * <p>
	 * 1. <b>InteractiveFrame bindings</b><br>
	 * Left button : ROTATE<br>
	 * Center button : SCALE<br>
	 * Right button : TRANSLATE<br>
	 * Shift + Center button : SCREEN_TRANSLATE<br>
	 * Shift + Right button : SCREEN_ROTATE<br>
	 * <p>
	 * 2. <b>InteractiveFrame bindings</b><br>
	 * Left button : ROTATE<br>
	 * Center button : ZOOM<br>
	 * Right button : TRANSLATE<br>
	 * Shift + Left button : ZOOM_ON_REGION<br>
	 * Shift + Center button : SCREEN_TRANSLATE<br>
	 * Shift + Right button : SCREEN_ROTATE.
	 * <p>
	 * Also set the following (common) bindings are:
	 * <p>
	 * 2 left clicks : ALIGN_FRAME<br>
	 * 2right clicks : CENTER_FRAME<br>
	 * Wheel in 2D : SCALE both, InteractiveFrame and InteractiveFrame<br>
	 * Wheel in 3D : SCALE InteractiveFrame, and ZOOM InteractiveFrame<br>
	 * 
	 * @see #dragToFirstPerson()
	 * @see #dragToThirdPerson()
	 */
	public void dragToArcball() {
		removeFrameBindings();
		removeEyeBindings();

		setButtonBinding(Target.EYE, LEFT_ID, DOF2Action.ROTATE);
		setButtonBinding(Target.EYE, CENTER_ID, scene.is3D() ? DOF2Action.TRANSLATE_Z : DOF2Action.SCALE);
		setButtonBinding(Target.EYE, RIGHT_ID, DOF2Action.TRANSLATE);
		setButtonBinding(Target.EYE, MotionEvent.SHIFT, LEFT_ID, DOF2Action.ZOOM_ON_REGION);
		setButtonBinding(Target.EYE, MotionEvent.SHIFT, CENTER_ID, DOF2Action.SCREEN_TRANSLATE);
		setButtonBinding(Target.EYE, MotionEvent.SHIFT, RIGHT_ID, DOF2Action.SCREEN_ROTATE);

		setButtonBinding(Target.FRAME, LEFT_ID, DOF2Action.ROTATE);
		setButtonBinding(Target.FRAME, CENTER_ID, DOF2Action.SCALE);
		setButtonBinding(Target.FRAME, RIGHT_ID, DOF2Action.TRANSLATE);
		setButtonBinding(Target.FRAME, MotionEvent.SHIFT, CENTER_ID, DOF2Action.SCREEN_TRANSLATE);
		setButtonBinding(Target.FRAME, MotionEvent.SHIFT, RIGHT_ID, DOF2Action.SCREEN_ROTATE);

		setCommonBindings();
	}

	/**
	 * Set mouse bindings as 'arcball'. Bindings are as follows:
	 * <p>
	 * 1. <b>InteractiveFrame bindings</b><br>
	 * No-button : ROTATE<br>
	 * Shift + No-button : SCALE<br>
	 * Ctrl + No-button : TRANSLATE<br>
	 * Center button : SCREEN_TRANSLATE<br>
	 * Right button : SCREEN_ROTATE<br>
	 * <p>
	 * 2. <b>InteractiveFrame bindings</b><br>
	 * No-button : ROTATE<br>
	 * Shift + No-button : ZOOM<br>
	 * Ctrl + No-button : TRANSLATE<br>
	 * Ctrl + Shift + No-button : ZOOM_ON_REGION<br>
	 * Center button : SCREEN_TRANSLATE<br>
	 * Right button : SCREEN_ROTATE.
	 * <p>
	 * Also set the following (common) bindings are:
	 * <p>
	 * 2 left clicks : ALIGN_FRAME<br>
	 * 2right clicks : CENTER_FRAME<br>
	 * Wheel in 2D : SCALE both, InteractiveFrame and InteractiveFrame<br>
	 * Wheel in 3D : SCALE InteractiveFrame, and ZOOM InteractiveFrame<br>
	 * <p>
	 * Note that Alt + No-button is bound to the null action.
	 * 
	 * @see #dragToFirstPerson()
	 * @see #dragToThirdPerson()
	 */
	public void moveToArcball() {
		removeFrameBindings();
		removeEyeBindings();

		setGestureBinding(Target.EYE, DOF2Action.ROTATE);
		setGestureBinding(Target.EYE, MotionEvent.SHIFT, scene.is3D() ? DOF2Action.TRANSLATE_Z : DOF2Action.SCALE);
		setGestureBinding(Target.EYE, MotionEvent.CTRL, DOF2Action.TRANSLATE);
		setGestureBinding(Target.EYE, (MotionEvent.CTRL | MotionEvent.SHIFT), DOF2Action.ZOOM_ON_REGION);
		setButtonBinding(Target.EYE, CENTER_ID, DOF2Action.SCREEN_TRANSLATE);
		setButtonBinding(Target.EYE, RIGHT_ID, DOF2Action.SCREEN_ROTATE);
		// TODO really needs testing: seem to be trying to flush()
		// am i talking to myself lonely lately? :p lets make it!
		// I think it currently is being (not enqueued) semaphored by agent.handle
		// study relaxing this constraint, anyway here it goes:
		setGestureBinding(Target.EYE, MotionEvent.ALT, null);

		setGestureBinding(Target.FRAME, DOF2Action.ROTATE);
		setGestureBinding(Target.FRAME, MotionEvent.SHIFT, DOF2Action.SCALE);
		setGestureBinding(Target.FRAME, MotionEvent.CTRL, DOF2Action.TRANSLATE);
		setButtonBinding(Target.FRAME, CENTER_ID, DOF2Action.SCREEN_TRANSLATE);
		setButtonBinding(Target.FRAME, RIGHT_ID, DOF2Action.SCREEN_ROTATE);
		// TODO idem here
		setGestureBinding(Target.FRAME, MotionEvent.ALT, null);

		setCommonBindings();
	}

	/**
	 * Set mouse bindings as 'first-person'. Bindings are as follows:
	 * <p>
	 * 1. <b>InteractiveFrame bindings</b><br>
	 * Left button : ROTATE<br>
	 * Center button : SCALE<br>
	 * Right button : TRANSLATE<br>
	 * Shift + Center button : SCREEN_TRANSLATE<br>
	 * Shift + Right button : SCREEN_ROTATE<br>
	 * <p>
	 * 2. <b>InteractiveFrame bindings</b><br>
	 * Left button : MOVE_FORWARD<br>
	 * Center button : LOOK_AROUND<br>
	 * Right button : MOVE_BACKWARD<br>
	 * Shift + Left button : ROTATE_Z<br>
	 * Shift + Center button : DRIVE<br>
	 * Ctrl + Wheel : ROLL<br>
	 * Shift + Wheel : DRIVE<br>
	 * <p>
	 * Also set the following (common) bindings are:
	 * <p>
	 * 2 left clicks : ALIGN_FRAME<br>
	 * 2right clicks : CENTER_FRAME<br>
	 * Wheel in 2D : SCALE both, InteractiveFrame and InteractiveFrame<br>
	 * Wheel in 3D : SCALE InteractiveFrame, and ZOOM InteractiveFrame<br>
	 * 
	 * @see #dragToArcball()
	 * @see #dragToThirdPerson()
	 */
	public void dragToFirstPerson() {
		removeFrameBindings();
		removeEyeBindings();

		setButtonBinding(Target.EYE, LEFT_ID, DOF2Action.MOVE_FORWARD);
		setButtonBinding(Target.EYE, RIGHT_ID, DOF2Action.MOVE_BACKWARD);
		setButtonBinding(Target.EYE, MotionEvent.SHIFT, LEFT_ID, DOF2Action.ROTATE_Z);

		setWheelBinding(Target.EYE, MotionEvent.CTRL, DOF1Action.ROTATE_Z);
		if (scene.is3D()) {
			setButtonBinding(Target.EYE, CENTER_ID, DOF2Action.LOOK_AROUND);
			setButtonBinding(Target.EYE, MotionEvent.SHIFT, CENTER_ID, DOF2Action.DRIVE);
		}
		setButtonBinding(Target.FRAME, LEFT_ID, DOF2Action.ROTATE);
		setButtonBinding(Target.FRAME, CENTER_ID, DOF2Action.SCALE);
		setButtonBinding(Target.FRAME, RIGHT_ID, DOF2Action.TRANSLATE);
		setButtonBinding(Target.FRAME, MotionEvent.SHIFT, CENTER_ID, DOF2Action.SCREEN_TRANSLATE);
		setButtonBinding(Target.FRAME, MotionEvent.SHIFT, RIGHT_ID, DOF2Action.SCREEN_ROTATE);

		setCommonBindings();
	}

	/**
	 * Set mouse bindings as 'first-person'. Bindings are as follows:
	 * <p>
	 * 1. <b>InteractiveFrame bindings</b><br>
	 * No-button : ROTATE<br>
	 * Shift + No-button : SCALE<br>
	 * Ctrl + No-button : TRANSLATE<br>
	 * Center button : SCREEN_TRANSLATE<br>
	 * Right button : SCREEN_ROTATE<br>
	 * <p>
	 * 2. <b>InteractiveFrame bindings</b><br>
	 * Ctrl + No-button : MOVE_FORWARD<br>
	 * No-button : LOOK_AROUND<br>
	 * Shift + No-button : MOVE_BACKWARD<br>
	 * Right button : ROTATE_Z<br>
	 * Ctrl + Shift + No-button : DRIVE<br>
	 * Ctrl + Shift + Wheel : ROTATE_Z<br>
	 * Shift + Wheel : DRIVE<br>
	 * <p>
	 * Also set the following (common) bindings are:
	 * <p>
	 * 2 left clicks : ALIGN_FRAME<br>
	 * 2right clicks : CENTER_FRAME<br>
	 * Wheel in 2D : SCALE both, InteractiveFrame and InteractiveFrame<br>
	 * Wheel in 3D : SCALE InteractiveFrame, and ZOOM InteractiveFrame<br>
	 * <p>
	 * Note that Alt + No-button is bound to the null action.
	 * <p>
	 * Also set the following (common) bindings are:
	 * <p>
	 * 2 left clicks : ALIGN_FRAME<br>
	 * 2right clicks : CENTER_FRAME<br>
	 * Wheel in 2D : SCALE both, InteractiveFrame and InteractiveFrame<br>
	 * Wheel in 3D : SCALE InteractiveFrame, and ZOOM InteractiveFrame<br>
	 * 
	 * @see #dragToArcball()
	 * @see #dragToThirdPerson()
	 */
	public void moveToFirstPerson() {
		removeFrameBindings();
		removeEyeBindings();

		setGestureBinding(Target.EYE, MotionEvent.CTRL, DOF2Action.MOVE_FORWARD);
		setGestureBinding(Target.EYE, MotionEvent.SHIFT, DOF2Action.MOVE_BACKWARD);
		// TODO idem
		setGestureBinding(Target.EYE, MotionEvent.ALT, null);
		setButtonBinding(Target.EYE, RIGHT_ID, DOF2Action.ROTATE_Z);
		setWheelBinding(Target.EYE, (MotionEvent.CTRL | MotionEvent.SHIFT), DOF1Action.ROTATE_Z);

		if (scene.is3D()) {
			setGestureBinding(Target.EYE, DOF2Action.LOOK_AROUND);
			setGestureBinding(Target.EYE, (MotionEvent.CTRL | MotionEvent.SHIFT), DOF2Action.DRIVE);
		}
		setGestureBinding(Target.FRAME, DOF2Action.ROTATE);
		setGestureBinding(Target.FRAME, MotionEvent.SHIFT, DOF2Action.SCALE);
		setGestureBinding(Target.FRAME, MotionEvent.CTRL, DOF2Action.TRANSLATE);
		// TODO idem
		setGestureBinding(Target.FRAME, MotionEvent.ALT, null);
		setButtonBinding(Target.FRAME, CENTER_ID, DOF2Action.SCREEN_TRANSLATE);
		setButtonBinding(Target.FRAME, RIGHT_ID, DOF2Action.SCREEN_ROTATE);

		setCommonBindings();
	}

	/**
	 * Set mouse bindings as 'third-person'. Bindings are as follows: *
	 * <p>
	 * Left button : MOVE_FORWARD<br>
	 * Center button : LOOK_AROUND<br>
	 * Right button : MOVE_BACKWARD<br>
	 * Shift + Left button : ROLL<br>
	 * Shift + Center button : DRIVE<br>
	 * <p>
	 * Also set the following (common) bindings are:
	 * <p>
	 * 2 left clicks : ALIGN_FRAME<br>
	 * 2right clicks : CENTER_FRAME<br>
	 * Wheel in 2D : SCALE both, InteractiveFrame and InteractiveFrame<br>
	 * Wheel in 3D : SCALE InteractiveFrame, and ZOOM InteractiveFrame<br>
	 * 
	 * @see #dragToArcball()
	 * @see #dragToFirstPerson()
	 */
	public void dragToThirdPerson() {
		removeFrameBindings();
		removeEyeBindings();

		setButtonBinding(Target.FRAME, LEFT_ID, DOF2Action.MOVE_FORWARD);
		setButtonBinding(Target.FRAME, RIGHT_ID, DOF2Action.MOVE_BACKWARD);
		setButtonBinding(Target.FRAME, MotionEvent.SHIFT, LEFT_ID, DOF2Action.ROTATE_Z);

		if (scene.is3D()) {
			setButtonBinding(Target.FRAME, CENTER_ID, DOF2Action.LOOK_AROUND);
			setButtonBinding(Target.FRAME, MotionEvent.SHIFT, CENTER_ID, DOF2Action.DRIVE);
		}

		setCommonBindings();
	}

	/**
	 * Set mouse bindings as 'third-person'. Bindings are as follows:
	 * <p>
	 * Ctrl + No-button : MOVE_FORWARD<br>
	 * No-button : LOOK_AROUND<br>
	 * Shift + No-button : MOVE_BACKWARD<br>
	 * Ctrl + Shift + Wheel : ROTATE_Z<br>
	 * Ctrl + Shift + No-button : DRIVE<br>
	 * <p>
	 * Also set the following (common) bindings are:
	 * <p>
	 * 2 left clicks : ALIGN_FRAME<br>
	 * 2right clicks : CENTER_FRAME<br>
	 * Wheel in 2D : SCALE both, InteractiveFrame and InteractiveFrame<br>
	 * Wheel in 3D : SCALE InteractiveFrame, and ZOOM InteractiveFrame<br>
	 * <p>
	 * Note that Alt + No-button is bound to the null action.
	 * <p>
	 * Also set the following (common) bindings are:
	 * <p>
	 * 2 left clicks : ALIGN_FRAME<br>
	 * 2right clicks : CENTER_FRAME<br>
	 * Wheel in 2D : SCALE both, InteractiveFrame and InteractiveFrame<br>
	 * Wheel in 3D : SCALE InteractiveFrame, and ZOOM InteractiveFrame<br>
	 * 
	 * @see #dragToArcball()
	 * @see #dragToFirstPerson()
	 */
	public void moveToThirdPerson() {
		removeFrameBindings();
		removeEyeBindings();

		setGestureBinding(Target.FRAME, MotionEvent.CTRL, DOF2Action.MOVE_FORWARD);
		setGestureBinding(Target.FRAME, MotionEvent.SHIFT, DOF2Action.MOVE_BACKWARD);

		setWheelBinding(Target.FRAME, (MotionEvent.CTRL | MotionEvent.SHIFT), DOF1Action.ROTATE_Z);
		// TODO idem
		setGestureBinding(Target.FRAME, MotionEvent.ALT, null);
		if (scene.is3D()) {
			setGestureBinding(Target.FRAME, DOF2Action.LOOK_AROUND);
			setGestureBinding(Target.FRAME, (MotionEvent.CTRL | MotionEvent.SHIFT), DOF2Action.DRIVE);
		}

		setCommonBindings();
	}

	/**
	 * Calls {@code removeMotionBindings(Target.EYE)}, {@code removeClickBindings(Target.EYE)} and
	 * {@code removeWheelBindings(Target.EYE)}
	 * 
	 * @see #removeMotionBindings(Target)
	 * @see #removeClickBindings(Target)
	 * @see #removeWheelBindings(Target)
	 */
	public void removeEyeBindings() {
		removeMotionBindings(Target.EYE);
		removeClickBindings(Target.EYE);
		removeWheelBindings(Target.EYE);
	}

	/**
	 * Calls {@code removeMotionBindings(Target.FRAME)}, {@code removeClickBindings(Target.FRAME)} and
	 * {@code removeWheelBindings(Target.FRAME)}
	 * 
	 * @see #removeMotionBindings(Target)
	 * @see #removeClickBindings(Target)
	 * @see #removeWheelBindings(Target)
	 */
	public void removeFrameBindings() {
		removeMotionBindings(Target.FRAME);
		removeClickBindings(Target.FRAME);
		removeWheelBindings(Target.FRAME);
	}

	/**
	 * Set the following (common) bindings:
	 * <p>
	 * 2 left clicks : ALIGN_FRAME<br>
	 * 2right clicks : CENTER_FRAME<br>
	 * Wheel in 2D : SCALE both, InteractiveFrame and InteractiveFrame<br>
	 * Wheel in 3D : SCALE InteractiveFrame, and ZOOM InteractiveFrame<br>
	 * <p>
	 * which are used in {@link #dragToArcball()}, {@link #dragToFirstPerson()} and {@link #dragToThirdPerson()}
	 */
	protected void setCommonBindings() {
		setClickBinding(Target.EYE, LEFT_ID, 2, ClickAction.ALIGN_FRAME);
		setClickBinding(Target.EYE, RIGHT_ID, 2, ClickAction.CENTER_FRAME);

		setClickBinding(Target.FRAME, LEFT_ID, 2, ClickAction.ALIGN_FRAME);
		setClickBinding(Target.FRAME, RIGHT_ID, 2, ClickAction.CENTER_FRAME);

		setWheelBinding(Target.EYE, scene.is3D() ? DOF1Action.TRANSLATE_Z : DOF1Action.SCALE);
		setWheelBinding(Target.FRAME, DOF1Action.SCALE);
	}

	// WRAPPERS

	// Gestures : mouse move

	/**
	 * Same as {@code setBinding(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, BogusEvent.NO_ID), action)}.
	 *
	 * @see remixlab.dandelion.agent.MotionAgent#setBinding(Target, MotionShortcut, Action)
	 */
	public void setGestureBinding(Target target, DOF2Action action) {
		setBinding(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, BogusEvent.NO_ID), action);
	}

	/**
	 * Same as {@code setBinding(target, new MotionShortcut(mask, BogusEvent.NO_ID), action)}.
	 * 
	 * @see remixlab.dandelion.agent.MotionAgent#setBinding(Target, MotionShortcut, Action)
	 */
	public void setGestureBinding(Target target, int mask, DOF2Action action) {
		setBinding(target, new MotionShortcut(mask, BogusEvent.NO_ID), action);
	}

	/**
	 * Same as {@code removeBinding(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, BogusEvent.NO_ID))}.
	 * 
	 * @see #removeBinding(Target, MotionShortcut)
	 */
	public void removeGestureBinding(Target target) {
		removeBinding(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, BogusEvent.NO_ID));
	}

	/**
	 * Same as {@code removeBinding(target, new MotionShortcut(mask, BogusEvent.NO_ID))}.
	 * 
	 * @see #removeBinding(Target, MotionShortcut)
	 */
	public void removeGestureBinding(Target target, int mask) {
		removeBinding(target, new MotionShortcut(mask, BogusEvent.NO_ID));
	}

	/**
	 * Same as {@code return hasBinding(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, BogusEvent.NO_ID))}.
	 * 
	 * @see #hasBinding(Target, MotionShortcut)
	 */
	public boolean hasGestureBinding(Target target) {
		return hasBinding(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, BogusEvent.NO_ID));
	}

	/**
	 * Same as {@code return hasBinding(target, new MotionShortcut(mask, BogusEvent.NO_ID))}.
	 * 
	 * @see #hasBinding(Target, MotionShortcut)
	 */
	public boolean hasGestureBinding(Target target, int mask) {
		return hasBinding(target, new MotionShortcut(mask, BogusEvent.NO_ID));
	}

	/**
	 * Same as {@code return action(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, BogusEvent.NO_ID))}.
	 * 
	 * @see #action(Target, MotionShortcut)
	 */
	public DOF2Action gestureAction(Target target) {
		return action(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, BogusEvent.NO_ID));
	}

	/**
	 * Same as {@code return action(target, new MotionShortcut(mask, BogusEvent.NO_ID))}.
	 * 
	 * @see #action(Target, MotionShortcut)
	 */
	public DOF2Action gestureAction(Target target, int mask) {
		return action(target, new MotionShortcut(mask, BogusEvent.NO_ID));
	}

	// Button : button + drag

	/**
	 * Same as {@code setBinding(target, new MotionShortcut(mask, button), action)}.
	 * 
	 * @see remixlab.dandelion.agent.MotionAgent#setBinding(Target, MotionShortcut, Action)
	 */
	public void setButtonBinding(Target target, int mask, int button, DOF2Action action) {
		setBinding(target, new MotionShortcut(mask, button), action);
	}

	/**
	 * Same as {@code setBinding(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, button), action)}.
	 * 
	 * @see remixlab.dandelion.agent.MotionAgent#setBinding(Target, MotionShortcut, Action)
	 */
	public void setButtonBinding(Target target, int button, DOF2Action action) {
		setBinding(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, button), action);
	}

	/**
	 * Same as {@code removeBinding(target, new MotionShortcut(mask, button))}.
	 * 
	 * @see #removeBinding(Target, MotionShortcut)
	 */
	public void removeButtonBinding(Target target, int mask, int button) {
		removeBinding(target, new MotionShortcut(mask, button));
	}

	/**
	 * Same as {@code removeBinding(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, button))}.
	 * 
	 * @see #removeBinding(Target, MotionShortcut)
	 */
	public void removeButtonBinding(Target target, int button) {
		removeBinding(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, button));
	}

	/**
	 * Same as {@code return hasBinding(target, new MotionShortcut(mask, button))}.
	 * 
	 * @see #hasBinding(Target, MotionShortcut)
	 */
	public boolean hasButtonBinding(Target target, int mask, int button) {
		return hasBinding(target, new MotionShortcut(mask, button));
	}

	/**
	 * Same as {@code return hasBinding(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, button))}.
	 * 
	 * @see #hasBinding(Target, MotionShortcut)
	 */
	public boolean hasButtonBinding(Target target, int button) {
		return hasBinding(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, button));
	}

	/**
	 * Same as {@code return action(target, new MotionShortcut(mask, button))}.
	 * 
	 * @see #action(Target, MotionShortcut)
	 */
	public DOF2Action buttonAction(Target target, int mask, int button) {
		return action(target, new MotionShortcut(mask, button));
	}

	/**
	 * Same as {@code return action(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, button))}.
	 * 
	 * @see #action(Target, MotionShortcut)
	 */
	public DOF2Action buttonAction(Target target, int button) {
		return action(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, button));
	}

	// wheel here

	/**
	 * Same as {@code setBinding(target, new MotionShortcut(mask, WHEEL_ID), action.dof2Action())}.
	 * 
	 * @see remixlab.dandelion.agent.MotionAgent#setBinding(Target, MotionShortcut, Action)
	 */
	public void setWheelBinding(Target target, int mask, DOF1Action action) {
		setBinding(target, new MotionShortcut(mask, WHEEL_ID), action.dof2Action());
	}

	/**
	 * Same as {@code setBinding(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, WHEEL_ID), action.dof2Action())}.
	 * 
	 * @see remixlab.dandelion.agent.MotionAgent#setBinding(Target, MotionShortcut, Action)
	 */
	public void setWheelBinding(Target target, DOF1Action action) {
		setBinding(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, WHEEL_ID), action.dof2Action());
	}

	/**
	 * Same as {@code removeBinding(target, new MotionShortcut(mask, WHEEL_ID))}.
	 * 
	 * @see #removeBinding(Target, MotionShortcut)
	 */
	public void removeWheelBinding(Target target, int mask) {
		removeBinding(target, new MotionShortcut(mask, WHEEL_ID));
	}

	/**
	 * Same as {@code removeBinding(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, WHEEL_ID))}.
	 * 
	 * @see #removeBinding(Target, MotionShortcut)
	 */
	public void removeWheelBinding(Target target) {
		removeBinding(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, WHEEL_ID));
	}

	/**
	 * Remove all wheel (DOF1) action bindings.
	 */
	public void removeWheelBindings(Target target) {
		// bit of a hack ;)
		Profile<MotionAction, MotionShortcut, DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		Iterator<Entry<MotionShortcut, DOF2Action>> it = profile.map().entrySet().iterator();
		while (it.hasNext()) {
			Entry<MotionShortcut, DOF2Action> entry = it.next();
			if (entry.getKey().id() == WHEEL_ID && entry.getValue().dof1Action() != null)
				it.remove();
		}
	}

	/**
	 * Same as {@code return hasBinding(target, new MotionShortcut(mask, WHEEL_ID))}.
	 * 
	 * @see #hasBinding(Target, MotionShortcut)
	 */
	public boolean hasWheelBinding(Target target, int mask) {
		return hasBinding(target, new MotionShortcut(mask, WHEEL_ID));
	}

	/**
	 * Same as {@code return hasBinding(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, WHEEL_ID))}.
	 * 
	 * @see #hasBinding(Target, MotionShortcut)
	 */
	public boolean hasWheelBinding(Target target) {
		return hasBinding(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, WHEEL_ID));
	}

	/**
	 * Same as {@code return isActionBound(target, action.dof2Action())}.
	 * 
	 * @see remixlab.dandelion.agent.MotionAgent#isActionBound(Target, Action)
	 */
	public boolean isWheelActionBound(Target target, DOF1Action action) {
		return isActionBound(target, action.dof2Action());
	}

	/**
	 * Same as {@code return action(target, new MotionShortcut(mask, WHEEL_ID)).dof1Action()}.
	 * 
	 * @see #action(Target, MotionShortcut)
	 */
	public DOF1Action wheelAction(Target target, int mask) {
		return action(target, new MotionShortcut(mask, WHEEL_ID)).dof1Action();
	}

	/**
	 * Same as {@code return action(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, WHEEL_ID)).dof1Action()}.
	 * 
	 * @see #action(Target, MotionShortcut)
	 */
	public DOF1Action wheelAction(Target target) {
		return action(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, WHEEL_ID)).dof1Action();
	}

	// click

	/**
	 * Same as {@code setBinding(target, new ClickShortcut(mask, button, ncs), action)}.
	 * 
	 * @see #setBinding(Target, ClickShortcut, ClickAction)
	 */
	public void setClickBinding(Target target, int mask, int button, int ncs, ClickAction action) {
		setBinding(target, new ClickShortcut(mask, button, ncs), action);
	}

	/**
	 * Same as {@code setBinding(target, new ClickShortcut(button, ncs), action)}.
	 * 
	 * @see #setBinding(Target, ClickShortcut, ClickAction)
	 */
	public void setClickBinding(Target target, int button, int ncs, ClickAction action) {
		setBinding(target, new ClickShortcut(button, ncs), action);
	}

	/**
	 * Same as {@code setBinding(target, new ClickShortcut(button, 1), action)}.
	 * 
	 * @see #setBinding(Target, ClickShortcut, ClickAction)
	 */
	public void setClickBinding(Target target, int button, ClickAction action) {
		setBinding(target, new ClickShortcut(button, 1), action);
	}

	/**
	 * Same as {@code removeBinding(target, new ClickShortcut(mask, button, ncs))}.
	 * 
	 * @see #removeBinding(Target, ClickShortcut)
	 */
	public void removeClickBinding(Target target, int mask, int button, int ncs) {
		removeBinding(target, new ClickShortcut(mask, button, ncs));
	}

	/**
	 * Same as {@code removeBinding(target, new ClickShortcut(button, ncs))}.
	 * 
	 * @see #removeBinding(Target, ClickShortcut)
	 */
	public void removeClickBinding(Target target, int button, int ncs) {
		removeBinding(target, new ClickShortcut(button, ncs));
	}

	/**
	 * Same as {@code removeBinding(target, new ClickShortcut(button, 1))}.
	 * 
	 * @see #removeBinding(Target, ClickShortcut)
	 */
	public void removeClickBinding(Target target, int button) {
		removeBinding(target, new ClickShortcut(button, 1));
	}

	/**
	 * Same as {@code return hasBinding(target, new ClickShortcut(mask, button, ncs))}.
	 * 
	 * @see #hasBinding(Target, ClickShortcut)
	 */
	public boolean hasClickBinding(Target target, int mask, int button, int ncs) {
		return hasBinding(target, new ClickShortcut(mask, button, ncs));
	}

	/**
	 * Same as {@code return hasBinding(target, new ClickShortcut(button, ncs))}.
	 * 
	 * @see #hasBinding(Target, ClickShortcut)
	 */
	public boolean hasClickBinding(Target target, int button, int ncs) {
		return hasBinding(target, new ClickShortcut(button, ncs));
	}

	/**
	 * Same as {@code return hasBinding(target, new ClickShortcut(button, 1))}.
	 * 
	 * @see #hasBinding(Target, ClickShortcut)
	 */
	public boolean hasClickBinding(Target target, int button) {
		return hasBinding(target, new ClickShortcut(button, 1));
	}

	/**
	 * Same as {@code return action(target, new ClickShortcut(mask, button, ncs))}.
	 * 
	 * @see #action(Target, ClickShortcut)
	 */
	public ClickAction clickAction(Target target, int mask, int button, int ncs) {
		return action(target, new ClickShortcut(mask, button, ncs));
	}

	/**
	 * Same as {@code return action(target, new ClickShortcut(button, ncs))}.
	 * 
	 * @see #action(Target, ClickShortcut)
	 */
	public ClickAction clickAction(Target target, int button, int ncs) {
		return action(target, new ClickShortcut(button, ncs));
	}

	/**
	 * Same as {@code return action(target, new ClickShortcut(button, 1))}.
	 * 
	 * @see #action(Target, ClickShortcut)
	 */
	public ClickAction clickAction(Target target, int button) {
		return action(target, new ClickShortcut(button, 1));
	}
}