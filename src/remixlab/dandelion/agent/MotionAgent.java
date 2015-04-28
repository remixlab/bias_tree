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

import remixlab.bias.branch.*;
import remixlab.bias.branch.profile.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

public class MotionAgent<A extends Action<MotionAction>> extends Agent {
	protected AbstractScene																														scene;
	protected MotionBranch<MotionAction, MotionProfile<A>, ClickProfile<ClickAction>>	eyeBranch;
	protected MotionBranch<MotionAction, MotionProfile<A>, ClickProfile<ClickAction>>	frameBranch;
	protected PickingMode																															pMode;

	public enum PickingMode {
		MOVE, CLICK
	}; // TODO this actually affect all grabbers!

	protected float	wSens	= 1f;

	public MotionAgent(AbstractScene scn, String n) {
		super(scn.inputHandler(), n);
		scene = scn;
		eyeBranch = new MotionBranch<MotionAction, MotionProfile<A>, ClickProfile<ClickAction>>(
				new MotionProfile<A>(),
				new ClickProfile<ClickAction>(), this, (n + "_eye_mouse_branch"));
		frameBranch = new MotionBranch<MotionAction, MotionProfile<A>, ClickProfile<ClickAction>>(
				new MotionProfile<A>(),
				new ClickProfile<ClickAction>(), this, (n + "_frame_mouse_branch"));
		setPickingMode(PickingMode.MOVE);
	}

	/**
	 * Returns the scene this object belongs to
	 */
	public AbstractScene scene() {
		return scene;
	}

	@Override
	public float[] sensitivities(MotionEvent event) {
		if (event instanceof DOF1Event)
			return new float[] { wSens, 1f, 1f, 1f, 1f, 1f };
		else
			return new float[] { 1f, 1f, 1f, 1f, 1f, 1f };
	}

	/**
	 * Defines the {@link #wheelSensitivity()}.
	 */
	public void setWheelSensitivity(float sensitivity) {
		wSens = sensitivity;
	}

	/**
	 * Returns the wheel sensitivity.
	 * <p>
	 * Default value is 20.0. A higher value will make the wheel action more efficient (usually meaning a faster zoom).
	 * Use a negative value to invert the zoom in and out directions.
	 * 
	 * @see #setWheelSensitivity(float)
	 */
	public float wheelSensitivity() {
		return wSens;
	}

	@Override
	public boolean appendBranch(Branch<?, ?> branch) {
		if (branch instanceof MotionBranch)
			return super.appendBranch(branch);
		else {
			System.out.println("Branch should be instanceof MotionBranch to be appended");
			return false;
		}
	}

	/*
	 * @Override public boolean addGrabber(Grabber frame) { if (frame instanceof InteractiveFrame) return
	 * addGrabber((InteractiveFrame) frame, ((InteractiveFrame) frame).isEyeFrame() ? eyeBranch : frameBranch); if
	 * (!(frame instanceof InteractiveGrabber)) return super.addGrabber(frame); return false; }
	 */

	// TODO debug
	@Override
	public boolean addGrabber(Grabber frame) {
		if (frame instanceof InteractiveFrame) {
			if (((InteractiveFrame) frame).isEyeFrame())
				System.out.println("adding EYE frame in motion");
			else
				System.out.println("adding FRAME frame in motion");
			return addGrabber((InteractiveFrame) frame, ((InteractiveFrame) frame).isEyeFrame() ? eyeBranch : frameBranch);
		}
		if (!(frame instanceof InteractiveGrabber))
			return super.addGrabber(frame);
		return false;
	}

	@Override
	public void resetDefaultGrabber() {
		addGrabber(scene.eye().frame());
		setDefaultGrabber(scene.eye().frame());
	}

	public MotionBranch<MotionAction, MotionProfile<A>, ClickProfile<ClickAction>> eyeBranch() {
		return eyeBranch;
	}

	public MotionBranch<MotionAction, MotionProfile<A>, ClickProfile<ClickAction>> frameBranch() {
		return frameBranch;
	}

	protected MotionProfile<A> motionProfile() {
		if (inputGrabber() instanceof InteractiveFrame)
			if (((InteractiveFrame) inputGrabber()).isEyeFrame())
				return eyeProfile();
			else
				return frameProfile();
		return null;
	}

	protected ClickProfile<ClickAction> clickProfile() {
		if (inputGrabber() instanceof InteractiveFrame)
			if (((InteractiveFrame) inputGrabber()).isEyeFrame())
				return eyeClickProfile();
			else
				return frameClickProfile();
		return null;
	}
	
	// two tempi actions workflow divert from 'normal' (single tempi) actions which just require
	// either updateTrackeGrabber(event) or handle(event).
	
	private MotionAction twotempi;
	private A a;
	private boolean need4Spin, drive, bypassNullEvent;
	//private DOF2Event lastEvent;
	private BogusEvent initEvent;
	//private float dFriction;
  //private GrabberFrame							iFrame;
	
	protected InteractiveGrabber<MotionAction> actionGrabber() {
		if (inputGrabber() instanceof InteractiveFrame)
			return (InteractiveFrame) inputGrabber();
		return null;
	}
	
	protected boolean initAction(BogusEvent event) {
		initEvent = event;
		//if (inputGrabber() instanceof GrabberFrame)	((GrabberFrame) inputGrabber()).stopSpinning();
		if(motionProfile() != null) { // means: inputGrabber() instanceof GrabberFrame
			GrabberFrame gFrame = (GrabberFrame) inputGrabber();
			boolean isEye = gFrame.isEyeFrame();
			gFrame.stopSpinning();
			a = motionProfile().handle(event);
			twotempi = a.referenceAction();
			if (twotempi == MotionAction.SCREEN_TRANSLATE)
				((InteractiveFrame) inputGrabber()).dirIsFixed = false;
			boolean rotateMode = ((twotempi == MotionAction.ROTATE) || (twotempi == MotionAction.ROTATE_XYZ)
					|| (twotempi == MotionAction.ROTATE_CAD)
					|| (twotempi == MotionAction.SCREEN_ROTATE) || (twotempi == MotionAction.TRANSLATE_XYZ_ROTATE_XYZ));
			if (rotateMode && scene.is3D())
				scene.camera().cadRotationIsReversed = scene.camera().frame()
						.transformOf(scene.camera().frame().sceneUpVector()).y() < 0.0f;
			need4Spin = (rotateMode && (((InteractiveFrame) inputGrabber()).dampingFriction() == 0));
			drive = (twotempi == MotionAction.DRIVE);
			bypassNullEvent = (twotempi == MotionAction.MOVE_FORWARD) || (twotempi == MotionAction.MOVE_BACKWARD)
					|| (drive) && scene.inputHandler().isAgentRegistered(this);
			scene.setRotateVisualHint(twotempi == MotionAction.SCREEN_ROTATE && inputGrabber() instanceof InteractiveFrame
					&& scene.inputHandler().isAgentRegistered(this));
			if(isEye)
				scene.setZoomVisualHint(twotempi == MotionAction.ZOOM_ON_REGION && scene.inputHandler().isAgentRegistered(this));
			if (bypassNullEvent || scene.zoomVisualHint() || scene.rotateVisualHint()) {
				if (bypassNullEvent) {
					// This is needed for first person:
					((InteractiveFrame) inputGrabber()).updateSceneUpVector();
					//dFriction = ((InteractiveFrame) inputGrabber()).dampingFriction();
					//((InteractiveFrame) inputGrabber()).setDampingFriction(0);
					handler.eventTupleQueue().add(new EventGrabberTuple(event, actionGrabber(), a));
					return false;
				}
				return true;// i.e., only when zoom_on_region and rotate!?
			}
		}
		return true;
	}
	
	protected void endAction(BogusEvent event) {
		twotempi = null;
		MotionEvent mEvent = null;
		if(event instanceof MotionEvent)
		mEvent = (MotionEvent) event;
		if(mEvent != null) {
			if (need4Spin) {
				System.out.println("need$Spin called at end-action " + mEvent.speed() + " " + mEvent.delay());
				((InteractiveFrame) inputGrabber()).startSpinning(mEvent.speed(), mEvent.delay());
			}
			if (scene.zoomVisualHint()) {
				// at first glance this should work
				// handle(event);
				// but the problem is that depending on the order the button and the modifiers are released,
				// different actions maybe triggered, so we go for sure ;) :
				//TODO hack! fix me
				((DOF2Event)mEvent).setPreviousEvent((DOF2Event)initEvent);
				inputHandler().enqueueEventTuple(new EventGrabberTuple(event, actionGrabber(), DOF2Action.ZOOM_ON_REGION));
				scene.setZoomVisualHint(false);
			}
			if (scene.rotateVisualHint())
				scene.setRotateVisualHint(false);
			if (pickingMode() == PickingMode.MOVE)
				updateTrackedGrabber(event);
			if (bypassNullEvent) {
				//iFrame.setDampingFriction(dFriction);
				bypassNullEvent = !bypassNullEvent;
			}
			// restore speed after drive action terminates:
			if (drive)
				((InteractiveFrame) inputGrabber()).setFlySpeed(0.01f * scene.radius());
		}
	}

	// TODO test all protected down here in stable before going on

	/**
	 * Profile defining InteractiveFrame action bindings from {@link remixlab.bias.event.shortcut.MotionShortcut}s.
	 */
	public MotionProfile<A> eyeProfile() {
		return eyeBranch().profile();
	}

	/**
	 * Profile defining InteractiveFrame action bindings from {@link remixlab.bias.event.shortcut.MotionShortcut}s.
	 */
	public MotionProfile<A> frameProfile() {
		return frameBranch().profile();
	}

	/**
	 * Profile defining InteractiveFrame action bindings from {@link remixlab.bias.event.shortcut.ClickShortcut}s.
	 */
	public ClickProfile<ClickAction> eyeClickProfile() {
		return eyeBranch().clickProfile();
	}

	/**
	 * Profile defining InteractiveFrame action bindings from {@link remixlab.bias.event.shortcut.ClickShortcut}s.
	 */
	public ClickProfile<ClickAction> frameClickProfile() {
		return frameBranch().clickProfile();
	}

	// common api

	public void setPickingMode(PickingMode mode) {
		pMode = mode;
	}

	public PickingMode pickingMode() {
		return pMode;
	}

	/**
	 * Same as {@code return buttonModifiersFix(BogusEvent.NOMODIFIER_MASK, button)}.
	 * 
	 * @see #buttonModifiersFix(int, int)
	 */
	public int buttonModifiersFix(int button) {
		return buttonModifiersFix(BogusEvent.NO_MODIFIER_MASK, button);
	}

	/**
	 * Hack to deal with some platforms not reporting correctly the mouse event mask, such as with Processing:
	 * https://github.com/processing/processing/issues/1693
	 * <p>
	 * Default implementation simple returns the same mask.
	 */
	public int buttonModifiersFix(int mask, int button) {
		return mask;
	}

	// click

	/**
	 * Binds the mask-button-ncs (number-of-clicks) click-shortcut to the (click) dandelion action to be performed by the
	 * given {@code target} (EYE or FRAME).
	 */
	public void setClickBinding(Target target, int mask, int button, int ncs, ClickAction action) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		profile.setBinding(buttonModifiersFix(mask, button), button, ncs, action);
	}

	/**
	 * Binds the button-ncs (number-of-clicks) click-shortcut to the (click) dandelion action to be performed by the given
	 * {@code target} (EYE or FRAME).
	 */
	public void setClickBinding(Target target, int button, int ncs, ClickAction action) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		profile.setBinding(buttonModifiersFix(button), button, ncs, action);
	}

	/**
	 * Binds the single-clicked button shortcut to the (click) dandelion action to be performed by the given
	 * {@code target} (EYE or FRAME).
	 */
	public void setClickBinding(Target target, int button, ClickAction action) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		profile.setBinding(buttonModifiersFix(button), button, 1, action);
	}

	/**
	 * Removes the mask-button-ncs (number-of-clicks) click-shortcut binding from the
	 * {@link remixlab.dandelion.core.InteractiveFrame} (if {@code eye} is {@code true}) or from the
	 * {@link remixlab.dandelion.core.InteractiveFrame} (if {@code eye} is {@code false}).
	 */
	public void removeClickBinding(Target target, int mask, int button, int ncs) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		profile.removeBinding(buttonModifiersFix(mask, button), button, ncs);
	}

	/**
	 * Removes the button-ncs (number-of-clicks) click-shortcut binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeClickBinding(Target target, int button, int ncs) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		profile.removeBinding(buttonModifiersFix(button), button, ncs);
	}

	/**
	 * Removes the single-clicked button shortcut binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeClickBinding(Target target, int button) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		profile.removeBinding(buttonModifiersFix(button), button, 1);
	}

	/**
	 * Removes all click bindings from the given {@code target} (EYE or FRAME).
	 */
	public void removeClickBindings(Target target) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		profile.removeBindings();
	}

	/**
	 * Returns {@code true} if the mask-button-ncs (number-of-clicks) click-shortcut is bound to the given {@code target}
	 * (EYE or FRAME).
	 */
	public boolean hasClickBinding(Target target, int mask, int button, int ncs) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		return profile.hasBinding(buttonModifiersFix(mask, button), button, ncs);
	}

	/**
	 * Returns {@code true} if the button-ncs (number-of-clicks) click-shortcut is bound to the given {@code target} (EYE
	 * or FRAME).
	 */
	public boolean hasClickBinding(Target target, int button, int ncs) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		return profile.hasBinding(buttonModifiersFix(button), button, ncs);
	}

	/**
	 * Returns {@code true} if the single-clicked button shortcut is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean hasClickBinding(Target target, int button) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		return profile.hasBinding(buttonModifiersFix(button), button, 1);
	}

	/**
	 * Returns {@code true} if the mouse click action is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean isClickActionBound(Target target, ClickAction action) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		return profile.isActionBound(action);
	}

	/**
	 * Returns the (click) dandelion action to be performed by the given {@code target} (EYE or FRAME) that is bound to
	 * the given mask-button-ncs (number-of-clicks) click-shortcut. Returns {@code null} if no action is bound to the
	 * given shortcut.
	 */
	public ClickAction clickAction(Target target, int mask, int button, int ncs) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		return (ClickAction) profile.action(buttonModifiersFix(mask, button), button, ncs);
	}

	/**
	 * Returns the (click) dandelion action to be performed by the given {@code target} (EYE or FRAME) that is bound to
	 * the given button-ncs (number-of-clicks) click-shortcut. Returns {@code null} if no action is bound to the given
	 * shortcut.
	 */
	public ClickAction clickAction(Target target, int button, int ncs) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		return (ClickAction) profile.action(buttonModifiersFix(button), button, ncs);
	}

	/**
	 * Returns the (click) dandelion action to be performed by the given {@code target} (EYE or FRAME) that is bound to
	 * the given single-clicked button shortcut. Returns {@code null} if no action is bound to the given shortcut.
	 */
	public ClickAction clickAction(Target target, int button) {
		ClickProfile<ClickAction> profile = target == Target.EYE ? eyeBranch.clickProfile() : frameBranch.clickProfile();
		return (ClickAction) profile.action(buttonModifiersFix(button), button, 1);
	}
}