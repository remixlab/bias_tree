
package remixlab.dandelion.agent;

import remixlab.bias.branch.*;
import remixlab.bias.branch.profile.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

//public class WheeledMotionAgent<A extends Action<?>> extends Agent {
public class WheeledMotionAgent<A extends Action<MotionAction>> extends Agent {
	protected AbstractScene																																															scene;
	protected WheeledMotionBranch<MotionAction, MotionProfile<DOF1Action>, MotionProfile<A>, ClickProfile<ClickAction>>	eyeBranch;
	protected WheeledMotionBranch<MotionAction, MotionProfile<DOF1Action>, MotionProfile<A>, ClickProfile<ClickAction>>	frameBranch;
	protected PickingMode																																																pMode;

	public enum PickingMode {
		MOVE, CLICK
	};

	protected float	wSens	= 1f;

	public WheeledMotionAgent(AbstractScene scn, String n) {
		super(scn.inputHandler(), n);
		scene = scn;
		eyeBranch = new WheeledMotionBranch<MotionAction, MotionProfile<DOF1Action>, MotionProfile<A>, ClickProfile<ClickAction>>(
				new MotionProfile<DOF1Action>(),
				new MotionProfile<A>(),
				new ClickProfile<ClickAction>(), this, (n + "_eye_mouse_branch"));
		frameBranch = new WheeledMotionBranch<MotionAction, MotionProfile<DOF1Action>, MotionProfile<A>, ClickProfile<ClickAction>>(
				new MotionProfile<DOF1Action>(),
				new MotionProfile<A>(),
				new ClickProfile<ClickAction>(), this, (n + "_frame_mouse_branch"));
		setPickingMode(PickingMode.MOVE);
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

	public boolean addGrabber(InteractiveFrame frame) {
		return addGrabber(frame, frameBranch);
	}

	public boolean addGrabber(InteractiveEyeFrame frame) {
		// this.resetBranch(eyeBranch);
		return addGrabber(frame, eyeBranch);
	}

	@Override
	public void resetDefaultGrabber() {
		addGrabber(scene.eye().frame());
		this.setDefaultGrabber(scene.eye().frame());
	}

	public WheeledMotionBranch<MotionAction, MotionProfile<DOF1Action>, MotionProfile<A>, ClickProfile<ClickAction>> eyeBranch() {
		return eyeBranch;
	}

	public WheeledMotionBranch<MotionAction, MotionProfile<DOF1Action>, MotionProfile<A>, ClickProfile<ClickAction>> frameBranch() {
		return frameBranch;
	}

	protected MotionProfile<A> motionProfile() {
		if (inputGrabber() instanceof InteractiveEyeFrame)
			return eyeBranch.profile();
		if (inputGrabber() instanceof InteractiveFrame)
			return frameBranch().profile();
		return null;
	}

	protected ClickProfile<ClickAction> clickProfile() {
		if (inputGrabber() instanceof InteractiveEyeFrame)
			return eyeBranch.clickProfile();
		if (inputGrabber() instanceof InteractiveFrame)
			return frameBranch.clickProfile();
		return null;
	}

	protected MotionProfile<DOF1Action> wheelProfile() {
		if (inputGrabber() instanceof InteractiveEyeFrame)
			return eyeBranch.wheelProfile();
		if (inputGrabber() instanceof InteractiveFrame)
			return frameBranch().wheelProfile();
		return null;
	}

	// TODO test all protected down here in stable before going on

	/**
	 * Profile defining InteractiveEyeFrame action bindings from {@link remixlab.bias.event.shortcut.MotionShortcut}s.
	 */
	public MotionProfile<A> eyeProfile() {
		return eyeBranch.profile();
	}

	/**
	 * Profile defining InteractiveFrame action bindings from {@link remixlab.bias.event.shortcut.MotionShortcut}s.
	 */
	public MotionProfile<A> frameProfile() {
		return frameBranch.profile();
	}

	/**
	 * Profile defining InteractiveEyeFrame action bindings from {@link remixlab.bias.event.shortcut.ClickShortcut}s.
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

	/**
	 * Profile defining InteractiveEyeFrame action bindings from (wheel)
	 * {@link remixlab.bias.event.shortcut.MotionShortcut}s.
	 */
	public MotionProfile<DOF1Action> eyeWheelProfile() {
		return eyeBranch.wheelProfile();
	}

	/**
	 * Profile defining InteractiveFrame action bindings from (wheel) {@link remixlab.bias.event.shortcut.MotionShortcut}
	 * s.
	 */
	public MotionProfile<DOF1Action> frameWheelProfile() {
		return frameBranch.wheelProfile();
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

	// wheel here

	/**
	 * Binds the mask-wheel shortcut to the (DOF1) dandelion action to be performed by the given {@code target} (EYE or
	 * FRAME).
	 */
	public void setWheelBinding(Target target, int mask, DOF1Action action) {
		MotionProfile<DOF1Action> profile = target == Target.EYE ? eyeBranch.wheelProfile() : frameBranch.wheelProfile();
		profile.setBinding(mask, MotionEvent.NO_ID, action);
	}

	/**
	 * Binds the wheel to the (DOF1) dandelion action to be performed by the given {@code target} (EYE or FRAME).
	 */
	public void setWheelBinding(Target target, DOF1Action action) {
		MotionProfile<DOF1Action> profile = target == Target.EYE ? eyeBranch.wheelProfile() : frameBranch.wheelProfile();
		profile.setBinding(action);
	}

	/**
	 * Removes the mask-wheel shortcut binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeWheelBinding(Target target, int mask) {
		MotionProfile<DOF1Action> profile = target == Target.EYE ? eyeBranch.wheelProfile() : frameBranch.wheelProfile();
		profile.removeBinding(mask, MotionEvent.NO_ID);
	}

	/**
	 * Removes the wheel binding from the given {@code target} (EYE or FRAME).
	 */
	public void removeWheelBinding(Target target) {
		MotionProfile<DOF1Action> profile = target == Target.EYE ? eyeBranch.wheelProfile() : frameBranch.wheelProfile();
		profile.removeBinding();
	}

	/**
	 * Removes all wheel bindings from the given {@code target} (EYE or FRAME).
	 */
	public void removeWheelBindings(Target target) {
		MotionProfile<DOF1Action> profile = target == Target.EYE ? eyeBranch.wheelProfile() : frameBranch.wheelProfile();
		profile.removeBindings();
	}

	/**
	 * Returns {@code true} if the mask-wheel shortcut is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean hasWheelBinding(Target target, int mask) {
		MotionProfile<DOF1Action> profile = target == Target.EYE ? eyeBranch.wheelProfile() : frameBranch.wheelProfile();
		return profile.hasBinding(mask, MotionEvent.NO_ID);
	}

	/**
	 * Returns {@code true} if the wheel is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean hasWheelBinding(Target target) {
		MotionProfile<DOF1Action> profile = target == Target.EYE ? eyeBranch.wheelProfile() : frameBranch.wheelProfile();
		return profile.hasBinding();
	}

	/**
	 * Returns {@code true} if the mouse wheel action is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean isWheelActionBound(Target target, DOF1Action action) {
		MotionProfile<DOF1Action> profile = target == Target.EYE ? eyeBranch.wheelProfile() : frameBranch.wheelProfile();
		return profile.isActionBound(action);
	}

	/**
	 * Returns the (DOF1) dandelion action to be performed by the given {@code target} (EYE or FRAME) that is bound to the
	 * given mask-wheel shortcut. Returns {@code null} if no action is bound to the given shortcut.
	 */
	public DOF1Action wheelAction(Target target, int mask, DOF1Action action) {
		MotionProfile<DOF1Action> profile = target == Target.EYE ? eyeBranch.wheelProfile() : frameBranch.wheelProfile();
		return (DOF1Action) profile.action(mask, MotionEvent.NO_ID);
	}

	/**
	 * Returns the (DOF1) dandelion action to be performed by the given {@code target} (EYE or FRAME) that is bound to the
	 * given wheel shortcut. Returns {@code null} if no action is bound to the given shortcut.
	 */
	public DOF1Action wheelAction(Target target, DOF1Action action) {
		MotionProfile<DOF1Action> profile = target == Target.EYE ? eyeBranch.wheelProfile() : frameBranch.wheelProfile();
		return (DOF1Action) profile.action(MotionEvent.NO_ID);
	}

	// mouse click

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
	 * {@link remixlab.dandelion.core.InteractiveEyeFrame} (if {@code eye} is {@code true}) or from the
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