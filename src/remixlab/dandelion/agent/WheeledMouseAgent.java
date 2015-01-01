package remixlab.dandelion.agent;

import remixlab.bias.agent.profile.*;
import remixlab.bias.core.*;
import remixlab.bias.grabber.*;
import remixlab.bias.event.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

public class WheeledMouseAgent extends WheeledMotionAgent<DOF2Action> {
	protected DOF2Event				pressEvent;
	protected DOF2Event				lastEvent;
	protected DOF2Event				prevEvent;
	protected int							left	= 1, center = 2, right = 3;

	boolean										bypassNullEvent, need4Spin, drive, rotateMode;
	float											dFriction;
	InteractiveFrame					iFrame;
	InteractiveEyeFrame				eyeFrame;

	protected boolean					needHandle;
	/* protected */DOF2Event	spEvent;				
	
	public WheeledMouseAgent(AbstractScene scn, String n) {
		super(scn, n);
	}	
	
	@Override
	public DOF2Event feed() {
		return null;
	}
	
	protected ActionGrabber<MotionAction> actionGrabber() {
		if(inputGrabber() instanceof InteractiveEyeFrame)
			return (InteractiveEyeFrame) inputGrabber();
		if( inputGrabber() instanceof InteractiveFrame )
			return (InteractiveFrame)inputGrabber();
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

		if (inputGrabber() instanceof InteractiveEyeFrame) {
			moveEye(lastEvent);
		}
		else if (inputGrabber() instanceof InteractiveFrame) {
			moveFrame(lastEvent);
		}
		
		handle(lastEvent);
	}
	
	protected void moveFrame(DOF2Event e) {
		if (inputGrabber() instanceof InteractiveFrame ) {
			//Action<?> a = motionProfile().handle(lastEvent);
			//TODO test in stable before going on 
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
	}
	
  protected void moveEye(DOF2Event e) {
  	if (inputGrabber() instanceof InteractiveFrame || inputGrabber() instanceof InteractiveEyeFrame ) {
			//Action<?> a = motionProfile().handle(lastEvent);
			//TODO test in stable before going on 
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
						//TODO new
						actionGrabber().setAction(DOF2Action.ZOOM_ON_REGION);
						inputHandler().enqueueEventTuple(new EventGrabberTuple(spEvent, actionGrabber()));
						needHandle = false;
						return;
					}
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
		// if( inputGrabber() == null )
		// return;
		if (inputGrabber() instanceof InteractiveEyeFrame) {
			pressEye(lastEvent);
			return;
		}
		else if (inputGrabber() instanceof InteractiveFrame) {
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
		//Action<?> a = motionProfile().handle(lastEvent);
	  //TODO test in stable before going on 
		DOF2Action a = motionProfile().handle(lastEvent);
		// Action<?> a = (inputGrabber() instanceof InteractiveEyeFrame) ? eyeProfile().handle(lastEvent) :
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
			scene.camera().frame().cadRotationIsReversed = scene.camera().frame()
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
				//TODO new
				actionGrabber().setAction(a);
				handler.eventTupleQueue().add(new EventGrabberTuple(lastEvent, actionGrabber()));
			}
		}
		else
			handle(lastEvent);
	}
	
  protected void pressEye(DOF2Event e) {
  	if (need4Spin)
			((InteractiveEyeFrame) inputGrabber()).stopSpinning();
		eyeFrame = (InteractiveEyeFrame) inputGrabber();
		//Action<?> a = motionProfile().handle(lastEvent);
	  //TODO test in stable before going on 
		DOF2Action a = motionProfile().handle(lastEvent);
		// Action<?> a = (inputGrabber() instanceof InteractiveEyeFrame) ? eyeProfile().handle(lastEvent) :
		// frameProfile().handle(lastEvent);
		if (a == null)
			return;
		MotionAction dA = (MotionAction) a.referenceAction();
		if (dA == MotionAction.SCREEN_TRANSLATE)
			((InteractiveEyeFrame) inputGrabber()).dirIsFixed = false;
		rotateMode = ((dA == MotionAction.ROTATE) || (dA == MotionAction.ROTATE_XYZ)
				|| (dA == MotionAction.ROTATE_CAD)
				|| (dA == MotionAction.SCREEN_ROTATE) || (dA == MotionAction.TRANSLATE_XYZ_ROTATE_XYZ));
		if (rotateMode && scene.is3D())
			scene.camera().frame().cadRotationIsReversed = scene.camera().frame()
					.transformOf(scene.camera().frame().sceneUpVector()).y() < 0.0f;
		need4Spin = (rotateMode && (((InteractiveEyeFrame) inputGrabber()).dampingFriction() == 0));
		drive = (dA == MotionAction.DRIVE);
		bypassNullEvent = (dA == MotionAction.MOVE_FORWARD) || (dA == MotionAction.MOVE_BACKWARD)
				|| (drive) && scene.inputHandler().isAgentRegistered(this);
		scene.setZoomVisualHint(dA == MotionAction.ZOOM_ON_REGION && scene.inputHandler().isAgentRegistered(this));
		scene.setRotateVisualHint(dA == MotionAction.SCREEN_ROTATE && scene.inputHandler().isAgentRegistered(this));
		if (bypassNullEvent || scene.zoomVisualHint() || scene.rotateVisualHint()) {
			if (bypassNullEvent) {
				// This is needed for first person:
				((InteractiveEyeFrame) inputGrabber()).updateSceneUpVector();
				dFriction = ((InteractiveEyeFrame) inputGrabber()).dampingFriction();
				((InteractiveEyeFrame) inputGrabber()).setDampingFriction(0);
				//TODO new
				actionGrabber().setAction(a);
				handler.eventTupleQueue().add(new EventGrabberTuple(lastEvent, actionGrabber()));
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
		if (inputGrabber() instanceof InteractiveEyeFrame) {
			dragEye(lastEvent);
			return;
		}
		else if (inputGrabber() instanceof InteractiveFrame) {
			dragFrame(lastEvent);
			return;
		}
		// TODO test
		// else
		// handle(lastEvent);
	}
	
	protected void dragFrame(DOF2Event e) {
		if (!scene.zoomVisualHint()) { // bypass zoom_on_region, may be different when using a touch device :P
			if (drive)
				((InteractiveFrame) inputGrabber()).setFlySpeed(0.01f * scene.radius() * 0.01f
						* (lastEvent.y() - pressEvent.y()));
			// never handle ZOOM_ON_REGION on a drag. Could happen if user presses a modifier during drag triggering it
			//Action<?> a = (inputGrabber() instanceof InteractiveEyeFrame) ? eyeProfile().handle((BogusEvent) lastEvent)
					//: frameProfile().handle((BogusEvent) lastEvent);
		  //TODO test in stable before going on
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
				((InteractiveEyeFrame) inputGrabber()).setFlySpeed(0.01f * scene.radius() * 0.01f
						* (lastEvent.y() - pressEvent.y()));
			// never handle ZOOM_ON_REGION on a drag. Could happen if user presses a modifier during drag triggering it
			//Action<?> a = (inputGrabber() instanceof InteractiveEyeFrame) ? eyeProfile().handle((BogusEvent) lastEvent)
					//: frameProfile().handle((BogusEvent) lastEvent);
		  //TODO test in stable before going on
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
		if (inputGrabber() instanceof InteractiveEyeFrame) {
			releaseEye(lastEvent);
			return;
		}
		else if (inputGrabber() instanceof InteractiveFrame) {
			releaseFrame(lastEvent);
			return;
		}
		// TODO test
		// else
		// updateTrackedGrabber(lastEvent);
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
			//TODO check
			actionGrabber().setAction(DOF2Action.ZOOM_ON_REGION);//new			
			inputHandler().enqueueEventTuple(new EventGrabberTuple(lastEvent, actionGrabber()));
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
		if (need4Spin && (prevEvent.speed() >= ((InteractiveEyeFrame) inputGrabber()).spinningSensitivity()))
				((InteractiveEyeFrame) inputGrabber()).startSpinning(prevEvent);
		if (scene.zoomVisualHint()) {
			// at first glance this should work
			// handle(event);
			// but the problem is that depending on the order the button and the modifiers are released,
			// different actions maybe triggered, so we go for sure ;) :
			lastEvent.setPreviousEvent(pressEvent);
			//TODO check
			actionGrabber().setAction(DOF2Action.ZOOM_ON_REGION);//new			
			inputHandler().enqueueEventTuple(new EventGrabberTuple(lastEvent, actionGrabber()));
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
			((InteractiveEyeFrame) inputGrabber()).setFlySpeed(0.01f * scene.radius());
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
	public void setAsArcball() {
		frameBranch().resetAllProfiles();
		eyeBranch().resetAllProfiles();
		eyeProfile().setBinding(buttonModifiersFix(left), left, DOF2Action.ROTATE);
		eyeProfile().setBinding(buttonModifiersFix(center), center, scene.is3D() ? DOF2Action.ZOOM : DOF2Action.SCALE);
		eyeProfile().setBinding(buttonModifiersFix(right), right, DOF2Action.TRANSLATE);
		eyeProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, left), left, DOF2Action.ZOOM_ON_REGION);
		eyeProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, center), center, DOF2Action.SCREEN_TRANSLATE);
		eyeProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, right), right, DOF2Action.SCREEN_ROTATE);
		frameProfile().setBinding(buttonModifiersFix(left), left, DOF2Action.ROTATE);
		frameProfile().setBinding(buttonModifiersFix(center), center, DOF2Action.SCALE);
		frameProfile().setBinding(buttonModifiersFix(right), right, DOF2Action.TRANSLATE);
		frameProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, center), center, DOF2Action.SCREEN_TRANSLATE);
		frameProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, right), right, DOF2Action.SCREEN_ROTATE);
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
	 * 2. <b>InteractiveEyeFrame bindings</b><br>
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
	 * Wheel in 2D -> SCALE both, InteractiveFrame and InteractiveEyeFrame<br>
	 * Wheel in 3D -> SCALE InteractiveFrame, and ZOOM InteractiveEyeFrame<br>
	 * <p>
	 * Note that Alt + No-button is bound to the null action.
	 * 
	 * @see #setAsFirstPerson()
	 * @see #setAsThirdPerson()
	 */
	public void setAsArcballTrackpad() {
		frameBranch().resetAllProfiles();
		eyeBranch().resetAllProfiles();
		eyeProfile().setBinding(DOF2Action.ROTATE);
		eyeProfile().setBinding(MotionEvent.SHIFT, MotionEvent.NOBUTTON,
				scene.is3D() ? DOF2Action.ZOOM : DOF2Action.SCALE);
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
	 * Set mouse bindings as 'first-person'. Bindings are as follows:
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
	 * Shift + Left button -> ROTATE_Z<br>
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
	public void setAsFirstPerson() {
		frameBranch().resetAllProfiles();
		eyeBranch().resetAllProfiles();
		eyeProfile().setBinding(buttonModifiersFix(left), left, DOF2Action.MOVE_FORWARD);
		eyeProfile().setBinding(buttonModifiersFix(right), right, DOF2Action.MOVE_BACKWARD);
		eyeProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, left), left, DOF2Action.ROTATE_Z);
		eyeWheelProfile().setBinding(MotionEvent.CTRL, MotionEvent.NOBUTTON, DOF1Action.ROTATE_Z);
		if (scene.is3D()) {
			eyeProfile().setBinding(buttonModifiersFix(center), center, DOF2Action.LOOK_AROUND);
			eyeProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, center), center, DOF2Action.DRIVE);
		}
		frameProfile().setBinding(buttonModifiersFix(left), left, DOF2Action.ROTATE);
		frameProfile().setBinding(buttonModifiersFix(center), center, DOF2Action.SCALE);
		frameProfile().setBinding(buttonModifiersFix(right), right, DOF2Action.TRANSLATE);
		frameProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, center), center, DOF2Action.SCREEN_TRANSLATE);
		frameProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, right), right, DOF2Action.SCREEN_ROTATE);
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
	 * 2. <b>InteractiveEyeFrame bindings</b><br>
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
	 * Wheel in 2D -> SCALE both, InteractiveFrame and InteractiveEyeFrame<br>
	 * Wheel in 3D -> SCALE InteractiveFrame, and ZOOM InteractiveEyeFrame<br>
	 * <p>
	 * Note that Alt + No-button is bound to the null action.
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
	public void setAsFirstPersonTrackpad() {
		frameBranch().resetAllProfiles();
		eyeBranch().resetAllProfiles();
		eyeProfile().setBinding(MotionEvent.CTRL, MotionEvent.NOBUTTON, DOF2Action.MOVE_FORWARD);
		eyeProfile().setBinding(MotionEvent.SHIFT, MotionEvent.NOBUTTON, DOF2Action.MOVE_BACKWARD);
		eyeProfile().setBinding(MotionEvent.ALT, MotionEvent.NOBUTTON, null);
		setButtonBinding(Target.EYE, right, DOF2Action.ROTATE_Z);
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
	 * Wheel in 2D -> SCALE both, InteractiveFrame and InteractiveEyeFrame<br>
	 * Wheel in 3D -> SCALE InteractiveFrame, and ZOOM InteractiveEyeFrame<br>
	 * 
	 * @see #setAsArcball()
	 * @see #setAsFirstPerson()
	 */
	public void setAsThirdPerson() {
		frameBranch().resetAllProfiles();
		eyeBranch().resetAllProfiles();
		frameProfile().setBinding(buttonModifiersFix(left), left, DOF2Action.MOVE_FORWARD);
		frameProfile().setBinding(buttonModifiersFix(right), right, DOF2Action.MOVE_BACKWARD);
		frameProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, left), left, DOF2Action.ROTATE_Z);
		if (scene.is3D()) {
			frameProfile().setBinding(buttonModifiersFix(center), center, DOF2Action.LOOK_AROUND);
			frameProfile().setBinding(buttonModifiersFix(MotionEvent.SHIFT, center), center, DOF2Action.DRIVE);
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
	 * Wheel in 2D -> SCALE both, InteractiveFrame and InteractiveEyeFrame<br>
	 * Wheel in 3D -> SCALE InteractiveFrame, and ZOOM InteractiveEyeFrame<br>
	 * <p>
	 * Note that Alt + No-button is bound to the null action.
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
	public void setAsThirdPersonTrackpad() {
		frameBranch().resetAllProfiles();
		eyeBranch().resetAllProfiles();
		frameProfile().setBinding(MotionEvent.CTRL, MotionEvent.NOBUTTON, DOF2Action.MOVE_FORWARD);
		frameProfile().setBinding(MotionEvent.SHIFT, MotionEvent.NOBUTTON, DOF2Action.MOVE_BACKWARD);
		frameWheelProfile().setBinding((MotionEvent.CTRL | MotionEvent.SHIFT), DOF1Action.ROTATE_Z);
		frameProfile().setBinding(MotionEvent.ALT, MotionEvent.NOBUTTON, null);
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
		MotionProfile<DOF2Action> profile = target == Target.EYE ? eyeProfile() : frameProfile();
		profile.setBinding(MotionEvent.NOMODIFIER_MASK, MotionEvent.NOBUTTON, action);
	}

	/**
	 * Binds the mouse shortcut to the (DOF2) dandelion action to be performed by the given {@code target} (EYE or FRAME).
	 */
	public void setBinding(Target target, int mask, DOF2Action action) {
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
}
