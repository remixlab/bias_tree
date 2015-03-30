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

import remixlab.bias.branch.profile.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.bias.event.shortcut.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

public class WheeledMouseAgent extends MotionAgent<DOF2Action> {
	protected DOF2Event				pressEvent;
	protected DOF2Event				lastEvent;
	protected DOF2Event				prevEvent;
	public static int					LEFT_ID	= 1, CENTER_ID = 2, RIGHT_ID = 3, WHEEL_ID = 4;

	boolean										bypassNullEvent, need4Spin, drive, rotateMode;
	float											dFriction;
	SceneFrame			  iFrame, eyeFrame;

	protected boolean					needHandle;
	/* protected */DOF2Event	spEvent;

	protected float						xSens		= 1f;
	protected float						ySens		= 1f;

	public WheeledMouseAgent(AbstractScene scn, String n) {
		super(scn, n);
		// todo pending
		// this.setWheelSensitivity(20.0f);
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

	@Override
	public DOF2Event feed() {
		return null;
	}

	protected ActionGrabber<MotionAction> actionGrabber() {
		if (inputGrabber() instanceof InteractiveFrame)
			return (InteractiveFrame) inputGrabber();		
		return null;
	}

	// low-level

	/**
	 * Return the last event processed by the agent. Internal use, needed by scene visual hints.
	 */
	public DOF2Event lastDOF2Event() {
		return lastEvent;
	}

	/**
	 * Return the last press event processed by the agent. Internal use, needed by scene visual hints.
	 */
	// TODO visibility and name
	public DOF2Event pressEvent() {
		return pressEvent;
	}

	/**
	 * Call {@link #updateTrackedGrabber(BogusEvent)} on the given event.
	 */
	protected void move(DOF2Event e) {
		lastEvent = e;
		if (pickingMode() == PickingMode.MOVE)
			updateTrackedGrabber(lastEvent);
		
		if (inputGrabber() instanceof InteractiveFrame) {
			if( ((InteractiveFrame)inputGrabber()).isEyeFrame() )
				moveEye(lastEvent);
			else
				moveFrame(lastEvent);
		}
		
		handle(lastEvent);
	}

	protected void moveFrame(DOF2Event e) {
		DOF2Action a = motionProfile().handle(lastEvent);
		if (a != null) {
			if (a == DOF2Action.ZOOM_ON_REGION)
				return;
			if (a == DOF2Action.SCREEN_ROTATE)
				scene.setRotateVisualHint(true);
			else
				scene.setRotateVisualHint(false);
			scene.setZoomVisualHint(false);
		}
	}

	protected void moveEye(DOF2Event e) {
		DOF2Action a = motionProfile().handle(lastEvent);
		if (a != null) {
			if (a == DOF2Action.SCREEN_ROTATE)
				scene.setRotateVisualHint(true);
			else
				scene.setRotateVisualHint(false);

			if (a == DOF2Action.ZOOM_ON_REGION) {
				scene.setZoomVisualHint(true);
				spEvent = e.get();
				spEvent.setPreviousEvent(pressEvent);
				needHandle = true;
				return;
			}
			else {
				scene.setZoomVisualHint(false);
				pressEvent = e.get();
				if (needHandle) {
					inputHandler()
							.enqueueEventTuple(new EventGrabberTuple(spEvent, actionGrabber(), DOF2Action.ZOOM_ON_REGION));
					needHandle = false;
					return;
				}
			}
		}
	}

	/**
	 * Begin interaction and call {@link #handle(BogusEvent)} on the given event. Keeps track of the {@link #pressEvent()}
	 * .
	 */
	protected void press(DOF2Event e) {
		lastEvent = e;
		pressEvent = lastEvent.get();		
		if (inputGrabber() instanceof InteractiveFrame) {
			if( ((InteractiveFrame)inputGrabber()).isEyeFrame() )
				pressEye(lastEvent);
			else
				pressFrame(lastEvent);
			return;
		}
		else
			handle(lastEvent);
	}

	protected void pressFrame(DOF2Event e) {
		if (need4Spin)
			((InteractiveFrame) inputGrabber()).stopSpinning();
		iFrame = (InteractiveFrame) inputGrabber();
		DOF2Action a = motionProfile().handle(lastEvent);
		// Action<?> a = (inputGrabber() instanceof InteractiveFrame) ? eyeProfile().handle(lastEvent) :
		// frameProfile().handle(lastEvent);
		if (a == null)
			return;
		MotionAction dA = (MotionAction) a.referenceAction();
		if (dA == MotionAction.SCREEN_TRANSLATE)
			((InteractiveFrame) inputGrabber()).dirIsFixed = false;
		rotateMode = ((dA == MotionAction.ROTATE) || (dA == MotionAction.ROTATE_XYZ)
				|| (dA == MotionAction.ROTATE_CAD)
				|| (dA == MotionAction.SCREEN_ROTATE) || (dA == MotionAction.TRANSLATE_XYZ_ROTATE_XYZ));
		if (rotateMode && scene.is3D())
			scene.camera().cadRotationIsReversed = scene.camera().frame()
					.transformOf(scene.camera().frame().sceneUpVector()).y() < 0.0f;
		need4Spin = (rotateMode && (((InteractiveFrame) inputGrabber()).dampingFriction() == 0));
		drive = (dA == MotionAction.DRIVE);
		bypassNullEvent = (dA == MotionAction.MOVE_FORWARD) || (dA == MotionAction.MOVE_BACKWARD)
				|| (drive) && scene.inputHandler().isAgentRegistered(this);
		scene.setRotateVisualHint(dA == MotionAction.SCREEN_ROTATE && inputGrabber() instanceof InteractiveFrame
				&& scene.inputHandler().isAgentRegistered(this));
		if (bypassNullEvent || scene.zoomVisualHint() || scene.rotateVisualHint()) {
			if (bypassNullEvent) {
				// This is needed for first person:
				((InteractiveFrame) inputGrabber()).updateSceneUpVector();
				dFriction = ((InteractiveFrame) inputGrabber()).dampingFriction();
				((InteractiveFrame) inputGrabber()).setDampingFriction(0);
				handler.eventTupleQueue().add(new EventGrabberTuple(lastEvent, actionGrabber(), a));
			}
		}
		else
			handle(lastEvent);
	}

	protected void pressEye(DOF2Event e) {
		if (need4Spin)
			((InteractiveFrame) inputGrabber()).stopSpinning();
		eyeFrame = (InteractiveFrame) inputGrabber();
		DOF2Action a = motionProfile().handle(lastEvent);
		// Action<?> a = (inputGrabber() instanceof InteractiveFrame) ? eyeProfile().handle(lastEvent) :
		// frameProfile().handle(lastEvent);
		if (a == null)
			return;
		MotionAction dA = (MotionAction) a.referenceAction();
		if (dA == MotionAction.SCREEN_TRANSLATE)
			((InteractiveFrame) inputGrabber()).dirIsFixed = false;
		rotateMode = ((dA == MotionAction.ROTATE) || (dA == MotionAction.ROTATE_XYZ)
				|| (dA == MotionAction.ROTATE_CAD)
				|| (dA == MotionAction.SCREEN_ROTATE) || (dA == MotionAction.TRANSLATE_XYZ_ROTATE_XYZ));
		if (rotateMode && scene.is3D())
			scene.camera().cadRotationIsReversed = scene.camera().frame()
					.transformOf(scene.camera().frame().sceneUpVector()).y() < 0.0f;
		need4Spin = (rotateMode && (((InteractiveFrame) inputGrabber()).dampingFriction() == 0));
		drive = (dA == MotionAction.DRIVE);
		bypassNullEvent = (dA == MotionAction.MOVE_FORWARD) || (dA == MotionAction.MOVE_BACKWARD)
				|| (drive) && scene.inputHandler().isAgentRegistered(this);
		scene.setZoomVisualHint(dA == MotionAction.ZOOM_ON_REGION && scene.inputHandler().isAgentRegistered(this));
		scene.setRotateVisualHint(dA == MotionAction.SCREEN_ROTATE && scene.inputHandler().isAgentRegistered(this));
		if (bypassNullEvent || scene.zoomVisualHint() || scene.rotateVisualHint()) {
			if (bypassNullEvent) {
				// This is needed for first person:
				((InteractiveFrame) inputGrabber()).updateSceneUpVector();
				dFriction = ((InteractiveFrame) inputGrabber()).dampingFriction();
				((InteractiveFrame) inputGrabber()).setDampingFriction(0);
				handler.eventTupleQueue().add(new EventGrabberTuple(lastEvent, actionGrabber(), a));
			}
		}
		else
			handle(lastEvent);
	}

	/**
	 * Call {@link #handle(BogusEvent)} on the given event.
	 */
	protected void drag(DOF2Event e) {
		lastEvent = e;
		if (inputGrabber() instanceof InteractiveFrame) {
			if( ((InteractiveFrame)inputGrabber()).isEyeFrame() )
				dragEye(lastEvent);
			else
				dragFrame(lastEvent);
			return;
		}
		else
			handle(lastEvent);
	}

	protected void dragFrame(DOF2Event e) {
		if (!scene.zoomVisualHint()) { // bypass zoom_on_region, may be different when using a touch device :P
			if (drive)
				((InteractiveFrame) inputGrabber()).setFlySpeed(0.01f * scene.radius() * 0.01f
						* (lastEvent.y() - pressEvent.y()));
			// never handle ZOOM_ON_REGION on a drag. Could happen if user presses a modifier during drag triggering it
			// Action<?> a = (inputGrabber() instanceof InteractiveFrame) ? eyeProfile().handle((BogusEvent) lastEvent)
			// : frameProfile().handle((BogusEvent) lastEvent);
			DOF2Action a = frameProfile().handle((BogusEvent) lastEvent);
			if (a == null)
				return;
			MotionAction dA = (MotionAction) a.referenceAction();
			if (dA != MotionAction.ZOOM_ON_REGION)
				handle(lastEvent);
		}
	}

	protected void dragEye(DOF2Event e) {
		if (!scene.zoomVisualHint()) { // bypass zoom_on_region, may be different when using a touch device :P
			if (drive)
				((InteractiveFrame) inputGrabber()).setFlySpeed(0.01f * scene.radius() * 0.01f
						* (lastEvent.y() - pressEvent.y()));
			// never handle ZOOM_ON_REGION on a drag. Could happen if user presses a modifier during drag triggering it
			// Action<?> a = (inputGrabber() instanceof InteractiveFrame) ? eyeProfile().handle((BogusEvent) lastEvent)
			// : frameProfile().handle((BogusEvent) lastEvent);
			DOF2Action a = eyeProfile().handle((BogusEvent) lastEvent);
			if (a == null)
				return;
			MotionAction dA = (MotionAction) a.referenceAction();
			if (dA != MotionAction.ZOOM_ON_REGION)
				handle(lastEvent);
		}
	}

	/**
	 * Ends interaction and calls {@link #updateTrackedGrabber(BogusEvent)} on the given event.
	 */
	protected void release(DOF2Event e) {
		prevEvent = lastDOF2Event().get();
		lastEvent = e;
		if (inputGrabber() instanceof InteractiveFrame) {
			if( ((InteractiveFrame)inputGrabber()).isEyeFrame() )
				releaseEye(lastEvent);
			else
				releaseFrame(lastEvent);
			return;
		}
		else
			updateTrackedGrabber(lastEvent);
	}

	protected void releaseFrame(DOF2Event e) {
		// note that the following two lines fail on event when need4Spin
		if (need4Spin && (prevEvent.speed() >= ((InteractiveFrame) inputGrabber()).spinningSensitivity()))
			((InteractiveFrame) inputGrabber()).startSpinning(prevEvent);
		if (scene.zoomVisualHint()) {
			// at first glance this should work
			// handle(event);
			// but the problem is that depending on the order the button and the modifiers are released,
			// different actions maybe triggered, so we go for sure ;) :
			lastEvent.setPreviousEvent(pressEvent);
			inputHandler().enqueueEventTuple(new EventGrabberTuple(lastEvent, actionGrabber(), DOF2Action.ZOOM_ON_REGION));
			scene.setZoomVisualHint(false);
		}
		if (scene.rotateVisualHint())
			scene.setRotateVisualHint(false);
		if (pickingMode() == PickingMode.MOVE)
			updateTrackedGrabber(lastEvent);
		if (bypassNullEvent) {
			iFrame.setDampingFriction(dFriction);
			bypassNullEvent = !bypassNullEvent;
		}
		// restore speed after drive action terminates:
		if (drive)
			((InteractiveFrame) inputGrabber()).setFlySpeed(0.01f * scene.radius());
	}

	protected void releaseEye(DOF2Event e) {
		// note that the following two lines fail on event when need4Spin
		if (need4Spin && (prevEvent.speed() >= ((InteractiveFrame) inputGrabber()).spinningSensitivity()))
			((InteractiveFrame) inputGrabber()).startSpinning(prevEvent);
		if (scene.zoomVisualHint()) {
			// at first glance this should work
			// handle(event);
			// but the problem is that depending on the order the button and the modifiers are released,
			// different actions maybe triggered, so we go for sure ;) :
			lastEvent.setPreviousEvent(pressEvent);
			inputHandler().enqueueEventTuple(new EventGrabberTuple(lastEvent, actionGrabber(), DOF2Action.ZOOM_ON_REGION));
			scene.setZoomVisualHint(false);
		}
		if (scene.rotateVisualHint())
			scene.setRotateVisualHint(false);
		if (pickingMode() == PickingMode.MOVE)
			updateTrackedGrabber(lastEvent);
		if (bypassNullEvent) {
			eyeFrame.setDampingFriction(dFriction);
			bypassNullEvent = !bypassNullEvent;
		}
		// restore speed after drive action terminates:
		if (drive)
			((InteractiveFrame) inputGrabber()).setFlySpeed(0.01f * scene.radius());
	}

	protected void wheel(DOF1Event wEvent) {
		handle(wEvent);
	}

	protected void click(ClickEvent cEvent) {
		if (pickingMode() == PickingMode.CLICK)
			updateTrackedGrabber(cEvent);
		handle(cEvent);
	}

	// high-level API

	/**
	 * Set mouse bindings as 'arcball'. Bindings are as follows:
	 * <p>
	 * 1. <b>InteractiveFrame bindings</b><br>
	 * Left button -> ROTATE<br>
	 * Center button -> SCALE<br>
	 * Right button -> TRANSLATE<br>
	 * Shift + Center button -> SCREEN_TRANSLATE<br>
	 * Shift + Right button -> SCREEN_ROTATE<br>
	 * <p>
	 * 2. <b>InteractiveFrame bindings</b><br>
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
	 * Wheel in 2D -> SCALE both, InteractiveFrame and InteractiveFrame<br>
	 * Wheel in 3D -> SCALE InteractiveFrame, and ZOOM InteractiveFrame<br>
	 * 
	 * @see #dragToFirstPerson()
	 * @see #dragToThirdPerson()
	 */
	public void dragToArcball() {
		removeFrameBindings();
		removeEyeBindings();
		eyeProfile().setBinding(buttonModifiersFix(LEFT_ID), LEFT_ID, DOF2Action.ROTATE);
		eyeProfile()
				.setBinding(buttonModifiersFix(CENTER_ID), CENTER_ID, scene.is3D() ? DOF2Action.TRANSLATE_Z : DOF2Action.SCALE);
		eyeProfile().setBinding(buttonModifiersFix(RIGHT_ID), RIGHT_ID, DOF2Action.TRANSLATE);
		eyeProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, LEFT_ID), LEFT_ID, DOF2Action.ZOOM_ON_REGION);
		eyeProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, CENTER_ID), CENTER_ID, DOF2Action.SCREEN_TRANSLATE);
		eyeProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, RIGHT_ID), RIGHT_ID, DOF2Action.SCREEN_ROTATE);
		frameProfile().setBinding(buttonModifiersFix(LEFT_ID), LEFT_ID, DOF2Action.ROTATE);
		frameProfile().setBinding(buttonModifiersFix(CENTER_ID), CENTER_ID, DOF2Action.SCALE);
		frameProfile().setBinding(buttonModifiersFix(RIGHT_ID), RIGHT_ID, DOF2Action.TRANSLATE);
		frameProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, CENTER_ID), CENTER_ID, DOF2Action.SCREEN_TRANSLATE);
		frameProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, RIGHT_ID), RIGHT_ID, DOF2Action.SCREEN_ROTATE);
		setCommonBindings();
	}

	/**
	 * Set mouse bindings as 'arcball'. Bindings are as follows:
	 * <p>
	 * 1. <b>InteractiveFrame bindings</b><br>
	 * No-button -> ROTATE<br>
	 * Shift + No-button -> SCALE<br>
	 * Ctrl + No-button -> TRANSLATE<br>
	 * Center button -> SCREEN_TRANSLATE<br>
	 * Right button -> SCREEN_ROTATE<br>
	 * <p>
	 * 2. <b>InteractiveFrame bindings</b><br>
	 * No-button -> ROTATE<br>
	 * Shift + No-button -> ZOOM<br>
	 * Ctrl + No-button -> TRANSLATE<br>
	 * Ctrl + Shift + No-button -> ZOOM_ON_REGION<br>
	 * Center button -> SCREEN_TRANSLATE<br>
	 * Right button -> SCREEN_ROTATE.
	 * <p>
	 * Also set the following (common) bindings are:
	 * <p>
	 * 2 left clicks -> ALIGN_FRAME<br>
	 * 2right clicks -> CENTER_FRAME<br>
	 * Wheel in 2D -> SCALE both, InteractiveFrame and InteractiveFrame<br>
	 * Wheel in 3D -> SCALE InteractiveFrame, and ZOOM InteractiveFrame<br>
	 * <p>
	 * Note that Alt + No-button is bound to the null action.
	 * 
	 * @see #dragToFirstPerson()
	 * @see #dragToThirdPerson()
	 */
	public void moveToArcball() {
		removeFrameBindings();
		removeEyeBindings();
		eyeProfile().setBinding(DOF2Action.ROTATE);
		eyeProfile().setBinding(MotionEvent.SHIFT, MotionEvent.NO_ID,
				scene.is3D() ? DOF2Action.TRANSLATE_Z : DOF2Action.SCALE);
		eyeProfile().setBinding(MotionEvent.CTRL, MotionEvent.NO_ID, DOF2Action.TRANSLATE);
		eyeProfile().setBinding((MotionEvent.CTRL | MotionEvent.SHIFT), MotionEvent.NO_ID, DOF2Action.ZOOM_ON_REGION);
		setButtonBinding(Target.EYE, CENTER_ID, DOF2Action.SCREEN_TRANSLATE);
		setButtonBinding(Target.EYE, RIGHT_ID, DOF2Action.SCREEN_ROTATE);
		eyeProfile().setBinding(MotionEvent.ALT, MotionEvent.NO_ID, null);
		frameProfile().setBinding(DOF2Action.ROTATE);
		frameProfile().setBinding(MotionEvent.SHIFT, MotionEvent.NO_ID, DOF2Action.SCALE);
		frameProfile().setBinding(MotionEvent.CTRL, MotionEvent.NO_ID, DOF2Action.TRANSLATE);
		setButtonBinding(Target.FRAME, CENTER_ID, DOF2Action.SCREEN_TRANSLATE);
		setButtonBinding(Target.FRAME, RIGHT_ID, DOF2Action.SCREEN_ROTATE);
		frameProfile().setBinding(MotionEvent.ALT, MotionEvent.NO_ID, null);
		setCommonBindings();
	}

	/**
	 * Set mouse bindings as 'first-person'. Bindings are as follows:
	 * <p>
	 * 1. <b>InteractiveFrame bindings</b><br>
	 * Left button -> ROTATE<br>
	 * Center button -> SCALE<br>
	 * Right button -> TRANSLATE<br>
	 * Shift + Center button -> SCREEN_TRANSLATE<br>
	 * Shift + Right button -> SCREEN_ROTATE<br>
	 * <p>
	 * 2. <b>InteractiveFrame bindings</b><br>
	 * Left button -> MOVE_FORWARD<br>
	 * Center button -> LOOK_AROUND<br>
	 * Right button -> MOVE_BACKWARD<br>
	 * Shift + Left button -> ROTATE_Z<br>
	 * Shift + Center button -> DRIVE<br>
	 * Ctrl + Wheel -> ROLL<br>
	 * Shift + Wheel -> DRIVE<br>
	 * <p>
	 * Also set the following (common) bindings are:
	 * <p>
	 * 2 left clicks -> ALIGN_FRAME<br>
	 * 2right clicks -> CENTER_FRAME<br>
	 * Wheel in 2D -> SCALE both, InteractiveFrame and InteractiveFrame<br>
	 * Wheel in 3D -> SCALE InteractiveFrame, and ZOOM InteractiveFrame<br>
	 * 
	 * @see #dragToArcball()
	 * @see #dragToThirdPerson()
	 */
	public void dragToFirstPerson() {
		removeFrameBindings();
		removeEyeBindings();
		eyeProfile().setBinding(buttonModifiersFix(LEFT_ID), LEFT_ID, DOF2Action.MOVE_FORWARD);
		eyeProfile().setBinding(buttonModifiersFix(RIGHT_ID), RIGHT_ID, DOF2Action.MOVE_BACKWARD);
		eyeProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, LEFT_ID), LEFT_ID, DOF2Action.ROTATE_Z);
		setWheelBinding(Target.EYE, MotionEvent.CTRL, DOF1Action.ROTATE_Z);
		if (scene.is3D()) {
			eyeProfile().setBinding(buttonModifiersFix(CENTER_ID), CENTER_ID, DOF2Action.LOOK_AROUND);
			eyeProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, CENTER_ID), CENTER_ID, DOF2Action.DRIVE);
		}
		frameProfile().setBinding(buttonModifiersFix(LEFT_ID), LEFT_ID, DOF2Action.ROTATE);
		frameProfile().setBinding(buttonModifiersFix(CENTER_ID), CENTER_ID, DOF2Action.SCALE);
		frameProfile().setBinding(buttonModifiersFix(RIGHT_ID), RIGHT_ID, DOF2Action.TRANSLATE);
		frameProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, CENTER_ID), CENTER_ID, DOF2Action.SCREEN_TRANSLATE);
		frameProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, RIGHT_ID), RIGHT_ID, DOF2Action.SCREEN_ROTATE);
		setCommonBindings();
	}

	/**
	 * Set mouse bindings as 'first-person'. Bindings are as follows:
	 * <p>
	 * 1. <b>InteractiveFrame bindings</b><br>
	 * No-button -> ROTATE<br>
	 * Shift + No-button -> SCALE<br>
	 * Ctrl + No-button -> TRANSLATE<br>
	 * Center button -> SCREEN_TRANSLATE<br>
	 * Right button -> SCREEN_ROTATE<br>
	 * <p>
	 * 2. <b>InteractiveFrame bindings</b><br>
	 * Ctrl + No-button -> MOVE_FORWARD<br>
	 * No-button -> LOOK_AROUND<br>
	 * Shift + No-button -> MOVE_BACKWARD<br>
	 * Right button -> ROTATE_Z<br>
	 * Ctrl + Shift + No-button -> DRIVE<br>
	 * Ctrl + Shift + Wheel -> ROTATE_Z<br>
	 * Shift + Wheel -> DRIVE<br>
	 * <p>
	 * Also set the following (common) bindings are:
	 * <p>
	 * 2 left clicks -> ALIGN_FRAME<br>
	 * 2right clicks -> CENTER_FRAME<br>
	 * Wheel in 2D -> SCALE both, InteractiveFrame and InteractiveFrame<br>
	 * Wheel in 3D -> SCALE InteractiveFrame, and ZOOM InteractiveFrame<br>
	 * <p>
	 * Note that Alt + No-button is bound to the null action.
	 * <p>
	 * Also set the following (common) bindings are:
	 * <p>
	 * 2 left clicks -> ALIGN_FRAME<br>
	 * 2right clicks -> CENTER_FRAME<br>
	 * Wheel in 2D -> SCALE both, InteractiveFrame and InteractiveFrame<br>
	 * Wheel in 3D -> SCALE InteractiveFrame, and ZOOM InteractiveFrame<br>
	 * 
	 * @see #dragToArcball()
	 * @see #dragToThirdPerson()
	 */
	public void moveToFirstPerson() {
		removeFrameBindings();
		removeEyeBindings();
		eyeProfile().setBinding(MotionEvent.CTRL, MotionEvent.NO_ID, DOF2Action.MOVE_FORWARD);
		eyeProfile().setBinding(MotionEvent.SHIFT, MotionEvent.NO_ID, DOF2Action.MOVE_BACKWARD);
		eyeProfile().setBinding(MotionEvent.ALT, MotionEvent.NO_ID, null);
		setButtonBinding(Target.EYE, RIGHT_ID, DOF2Action.ROTATE_Z);
		this.setWheelBinding(Target.EYE, (MotionEvent.CTRL | MotionEvent.SHIFT), DOF1Action.ROTATE_Z);
		if (scene.is3D()) {
			eyeProfile().setBinding(DOF2Action.LOOK_AROUND);
			eyeProfile().setBinding((MotionEvent.CTRL | MotionEvent.SHIFT), MotionEvent.NO_ID, DOF2Action.DRIVE);
		}
		frameProfile().setBinding(DOF2Action.ROTATE);
		frameProfile().setBinding(MotionEvent.SHIFT, MotionEvent.NO_ID, DOF2Action.SCALE);
		frameProfile().setBinding(MotionEvent.CTRL, MotionEvent.NO_ID, DOF2Action.TRANSLATE);
		frameProfile().setBinding(MotionEvent.ALT, MotionEvent.NO_ID, null);
		setButtonBinding(Target.FRAME, CENTER_ID, DOF2Action.SCREEN_TRANSLATE);
		setButtonBinding(Target.FRAME, RIGHT_ID, DOF2Action.SCREEN_ROTATE);
		setCommonBindings();
	}

	/**
	 * Set mouse bindings as 'third-person'. Bindings are as follows: *
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
	 * Wheel in 2D -> SCALE both, InteractiveFrame and InteractiveFrame<br>
	 * Wheel in 3D -> SCALE InteractiveFrame, and ZOOM InteractiveFrame<br>
	 * 
	 * @see #dragToArcball()
	 * @see #dragToFirstPerson()
	 */
	public void dragToThirdPerson() {
		removeFrameBindings();
		removeEyeBindings();
		frameProfile().setBinding(buttonModifiersFix(LEFT_ID), LEFT_ID, DOF2Action.MOVE_FORWARD);
		frameProfile().setBinding(buttonModifiersFix(RIGHT_ID), RIGHT_ID, DOF2Action.MOVE_BACKWARD);
		frameProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, LEFT_ID), LEFT_ID, DOF2Action.ROTATE_Z);
		if (scene.is3D()) {
			frameProfile().setBinding(buttonModifiersFix(CENTER_ID), CENTER_ID, DOF2Action.LOOK_AROUND);
			frameProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, CENTER_ID), CENTER_ID, DOF2Action.DRIVE);
		}
		setCommonBindings();
	}

	/**
	 * Set mouse bindings as 'third-person'. Bindings are as follows:
	 * <p>
	 * Ctrl + No-button -> MOVE_FORWARD<br>
	 * No-button -> LOOK_AROUND<br>
	 * Shift + No-button -> MOVE_BACKWARD<br>
	 * Ctrl + Shift + Wheel -> ROTATE_Z<br>
	 * Ctrl + Shift + No-button -> DRIVE<br>
	 * <p>
	 * Also set the following (common) bindings are:
	 * <p>
	 * 2 left clicks -> ALIGN_FRAME<br>
	 * 2right clicks -> CENTER_FRAME<br>
	 * Wheel in 2D -> SCALE both, InteractiveFrame and InteractiveFrame<br>
	 * Wheel in 3D -> SCALE InteractiveFrame, and ZOOM InteractiveFrame<br>
	 * <p>
	 * Note that Alt + No-button is bound to the null action.
	 * <p>
	 * Also set the following (common) bindings are:
	 * <p>
	 * 2 left clicks -> ALIGN_FRAME<br>
	 * 2right clicks -> CENTER_FRAME<br>
	 * Wheel in 2D -> SCALE both, InteractiveFrame and InteractiveFrame<br>
	 * Wheel in 3D -> SCALE InteractiveFrame, and ZOOM InteractiveFrame<br>
	 * 
	 * @see #dragToArcball()
	 * @see #dragToFirstPerson()
	 */
	public void moveToPerson() {
		removeFrameBindings();
		removeEyeBindings();
		frameProfile().setBinding(MotionEvent.CTRL, MotionEvent.NO_ID, DOF2Action.MOVE_FORWARD);
		frameProfile().setBinding(MotionEvent.SHIFT, MotionEvent.NO_ID, DOF2Action.MOVE_BACKWARD);
		setWheelBinding(Target.FRAME, (MotionEvent.CTRL | MotionEvent.SHIFT), DOF1Action.ROTATE_Z);
		frameProfile().setBinding(MotionEvent.ALT, MotionEvent.NO_ID, null);
		if (scene.is3D()) {
			frameProfile().setBinding(DOF2Action.LOOK_AROUND);
			frameProfile().setBinding((MotionEvent.CTRL | MotionEvent.SHIFT), MotionEvent.NO_ID, DOF2Action.DRIVE);
		}
		setCommonBindings();
	}

	public void removeEyeBindings() {
		eyeBranch().clickProfile().removeBindings();
		eyeBranch().motionProfile().removeBindings();
	}

	public void removeFrameBindings() {
		frameBranch().clickProfile().removeBindings();
		frameBranch().motionProfile().removeBindings();
	}

	/**
	 * Set the following (common) bindings:
	 * <p>
	 * 2 left clicks -> ALIGN_FRAME<br>
	 * 2right clicks -> CENTER_FRAME<br>
	 * Wheel in 2D -> SCALE both, InteractiveFrame and InteractiveFrame<br>
	 * Wheel in 3D -> SCALE InteractiveFrame, and ZOOM InteractiveFrame<br>
	 * <p>
	 * which are used in {@link #dragToArcball()}, {@link #dragToFirstPerson()} and {@link #dragToThirdPerson()}
	 */
	protected void setCommonBindings() {
		eyeClickProfile().setBinding(buttonModifiersFix(LEFT_ID), LEFT_ID, 2, ClickAction.ALIGN_FRAME);
		eyeClickProfile().setBinding(buttonModifiersFix(RIGHT_ID), RIGHT_ID, 2, ClickAction.CENTER_FRAME);
		frameClickProfile().setBinding(buttonModifiersFix(LEFT_ID), LEFT_ID, 2, ClickAction.ALIGN_FRAME);
		frameClickProfile().setBinding(buttonModifiersFix(RIGHT_ID), RIGHT_ID, 2, ClickAction.CENTER_FRAME);
		this.setWheelBinding(Target.EYE, MotionEvent.NO_MODIFIER_MASK, scene.is3D() ? DOF1Action.TRANSLATE_Z : DOF1Action.SCALE);
		this.setWheelBinding(Target.FRAME, MotionEvent.NO_MODIFIER_MASK, DOF1Action.SCALE);
	}

	// WRAPPERS

	// Gestures -> mouse move

	/**
	 * Binds the mouse shortcut to the (DOF2) dandelion action to be performed by the given {@code target} (EYE or FRAME).
	 */
	public void setGestureBinding(Target target, DOF2Action action) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		profile.setBinding(MotionEvent.NO_MODIFIER_MASK, MotionEvent.NO_ID, action);
	}

	/**
	 * Binds the mouse shortcut to the (DOF2) dandelion action to be performed by the given {@code target} (EYE or FRAME).
	 */
	public void setGestureBinding(Target target, int mask, DOF2Action action) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		profile.setBinding(mask, MotionEvent.NO_ID, action);
	}

	/**
	 * Removes the mouse shortcut binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeGestureBinding(Target target) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		profile.removeBinding(MotionEvent.NO_MODIFIER_MASK, MotionEvent.NO_ID);
	}

	/**
	 * Removes the mouse shortcut binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeGestureBinding(Target target, int mask) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		profile.removeBinding(mask, MotionEvent.NO_ID);
	}

	/**
	 * Removes all mouse shortcuts from the given {@code target} (EYE or FRAME).
	 */
	public void removeGestureBindings(Target target) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		profile.removeBindings();
	}

	/**
	 * Returns {@code true} if the mouse shortcut is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean hasGestureBinding(Target target) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		return profile.hasBinding(MotionEvent.NO_MODIFIER_MASK, MotionEvent.NO_ID);
	}

	/**
	 * Returns {@code true} if the mouse shortcut is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean hasGestureBinding(Target target, int mask) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		return profile.hasBinding(mask, MotionEvent.NO_ID);
	}

	/**
	 * Returns the (DOF2) dandelion action to be performed by the given {@code target} (EYE or FRAME) that is bound to the
	 * given mouse shortcut. Returns {@code null} if no action is bound to the given shortcut.
	 */
	public DOF2Action gestureAction(Target target) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		return (DOF2Action) profile.action(MotionEvent.NO_MODIFIER_MASK, MotionEvent.NO_ID);
	}

	/**
	 * Returns the (DOF2) dandelion action to be performed by the given {@code target} (EYE or FRAME) that is bound to the
	 * given mouse shortcut. Returns {@code null} if no action is bound to the given shortcut.
	 */
	public DOF2Action gestureAction(Target target, int mask) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		return (DOF2Action) profile.action(mask, MotionEvent.NO_ID);
	}

	// Button -> button + drag

	/**
	 * Binds the mask-button mouse shortcut to the (DOF2) dandelion action to be performed by the given {@code target}
	 * (EYE or FRAME).
	 */
	public void setButtonBinding(Target target, int mask, int button, DOF2Action action) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		profile.setBinding(buttonModifiersFix(mask, button), button, action);
	}

	/**
	 * Binds the button mouse shortcut to the (DOF2) dandelion action to be performed by the given {@code target} (EYE or
	 * FRAME).
	 */
	public void setButtonBinding(Target target, int button, DOF2Action action) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		profile.setBinding(buttonModifiersFix(button), button, action);
	}

	/**
	 * Removes the mask-button mouse shortcut binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeButtonBinding(Target target, int mask, int button) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		profile.removeBinding(buttonModifiersFix(mask, button), button);
	}

	/**
	 * Removes the button mouse shortcut binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeButtonBinding(Target target, int button) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		profile.removeBinding(buttonModifiersFix(button), button);
	}

	/**
	 * Removes all button bindings from the given {@code target} (EYE or FRAME).
	 */
	public void removeButtonBindings(Target target) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		profile.removeBindings();
	}

	/**
	 * Returns {@code true} if the mask-button mouse shortcut is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean hasButtonBinding(Target target, int mask, int button) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		return profile.hasBinding(buttonModifiersFix(mask, button), button);
	}

	/**
	 * Returns {@code true} if the button mouse shortcut is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean hasButtonBinding(Target target, int button) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		return profile.hasBinding(buttonModifiersFix(button), button);
	}

	/**
	 * Returns {@code true} if the mouse action is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean isButtonActionBound(Target target, DOF2Action action) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		return profile.isActionBound(action);
	}

	/**
	 * Returns the (DOF2) dandelion action to be performed by the given {@code target} (EYE or FRAME) that is bound to the
	 * given mask-button mouse shortcut. Returns {@code null} if no action is bound to the given shortcut.
	 */
	public DOF2Action buttonAction(Target target, int mask, int button) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		return (DOF2Action) profile.action(buttonModifiersFix(mask, button), button);
	}

	/**
	 * Returns the (DOF2) dandelion action to be performed by the given {@code target} (EYE or FRAME) that is bound to the
	 * given button mouse shortcut. Returns {@code null} if no action is bound to the given shortcut.
	 */
	public DOF2Action buttonAction(Target target, int button) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		return (DOF2Action) profile.action(buttonModifiersFix(button), button);
	}

	// wheel here

	/**
	 * Binds the mask-wheel shortcut to the (DOF1) dandelion action to be performed by the given {@code target} (EYE or
	 * FRAME).
	 */
	public void setWheelBinding(Target target, int mask, DOF1Action action) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		profile.setBinding(mask, WHEEL_ID, action.dof2Action());
	}

	/**
	 * Binds the wheel to the (DOF1) dandelion action to be performed by the given {@code target} (EYE or FRAME).
	 */
	public void setWheelBinding(Target target, DOF1Action action) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		profile.setBinding(WHEEL_ID, action.dof2Action());
	}

	/**
	 * Removes the mask-wheel shortcut binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeWheelBinding(Target target, int mask) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		profile.removeBinding(mask, WHEEL_ID);
	}

	/**
	 * Removes the wheel binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeWheelBinding(Target target) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		profile.removeBinding(WHEEL_ID);
	}

	/**
	 * Removes all wheel bindings from the given {@code target} (EYE or FRAME).
	 */
	public void removeWheelBindings(Target target) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		Iterator<Entry<MotionShortcut, DOF2Action>> it = profile.map().entrySet().iterator();
		while (it.hasNext()) {
			Entry<MotionShortcut, DOF2Action> entry = it.next();
			if (entry.getKey().id() == WHEEL_ID && entry.getValue().dof1Action() != null)
				it.remove();
		}
	}

	/**
	 * Returns {@code true} if the mask-wheel shortcut is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean hasWheelBinding(Target target, int mask) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		return profile.hasBinding(mask, WHEEL_ID);
	}

	/**
	 * Returns {@code true} if the wheel is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean hasWheelBinding(Target target) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		return profile.hasBinding(WHEEL_ID);
	}

	/**
	 * Returns {@code true} if the mouse wheel action is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean isWheelActionBound(Target target, DOF1Action action) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		return profile.isActionBound(action.dof2Action());
	}

	/**
	 * Returns the (DOF1) dandelion action to be performed by the given {@code target} (EYE or FRAME) that is bound to the
	 * given mask-wheel shortcut. Returns {@code null} if no action is bound to the given shortcut.
	 */
	public DOF1Action wheelAction(Target target, int mask, DOF1Action action) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		return ((DOF2Action) profile.action(mask, WHEEL_ID)).dof1Action();
	}

	/**
	 * Returns the (DOF1) dandelion action to be performed by the given {@code target} (EYE or FRAME) that is bound to the
	 * given wheel shortcut. Returns {@code null} if no action is bound to the given shortcut.
	 */
	public DOF1Action wheelAction(Target target, DOF1Action action) {
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		return ((DOF2Action) profile.action(WHEEL_ID)).dof1Action();
	}
}
