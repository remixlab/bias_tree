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
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.bias.event.shortcut.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

public class MotionAgent<A extends Action<MotionAction>> extends Agent {
	protected AbstractScene																scene;
	protected MotionBranch<MotionAction, A, ClickAction>	eyeBranch;
	protected MotionBranch<MotionAction, A, ClickAction>	frameBranch;
	protected PickingMode																	pMode;

	public enum PickingMode {
		MOVE, CLICK
	}; // this actually affect all grabbers!

	protected float	wSens	= 1f;

	public MotionAgent(AbstractScene scn, String n) {
		super(scn.inputHandler(), n);
		scene = scn;
		eyeBranch = new MotionBranch<MotionAction, A, ClickAction>(this, (n + "_eye_mouse_branch"));
		frameBranch = new MotionBranch<MotionAction, A, ClickAction>(this, (n + "_frame_mouse_branch"));
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
	
	//TODO discard me, use only next method
	@Override
	protected boolean appendBranch(Branch<?, ?, ?> branch) {
		if (branch instanceof MotionBranch)
			return super.appendBranch(branch);
		else {
			System.out.println("Branch should be instanceof MotionBranch to be appended");
			return false;
		}
	}
	
	//TODO what would be the method signature in Agent class this method overrides? (might allow to make all branch constructors protected)
	public <E extends Enum<E>, B extends Action<E>, C extends Action<E>> MotionBranch<E, B, C> appendBranch() {
		return new MotionBranch<E, B, C>(this, "my_motion_branch");
	}
	
	@Override
	public boolean addGrabber(Grabber frame) {
		if (frame instanceof InteractiveFrame)
			return addGrabber((InteractiveFrame) frame, ((InteractiveFrame) frame).isEyeFrame() ? eyeBranch : frameBranch);
		if (!(frame instanceof InteractiveGrabber))
			return super.addGrabber(frame);
		System.err.println("use addGrabber(G grabber, K MotionBranch) instead");
		return false;
	}
	
	@Override
	public boolean resetDefaultGrabber() {
		addGrabber(scene.eye().frame());
		return setDefaultGrabber(scene.eye().frame());
	}
	
	public MotionBranch<MotionAction, A, ClickAction> eyeBranch() {
		return eyeBranch;
	}

	public MotionBranch<MotionAction, A, ClickAction> frameBranch() {
		return frameBranch;
	}

	// TODO test all protected down here in stable before going on

	protected Profile<MotionShortcut, A> motionProfile(Target target) {
		return target == Target.EYE ? eyeProfile() : frameProfile();
	}

	/**
	 * Profile defining InteractiveFrame action bindings from {@link remixlab.bias.event.shortcut.MotionShortcut}s.
	 */
	public Profile<MotionShortcut, A> eyeProfile() {
		return eyeBranch().profile();
	}

	/**
	 * Profile defining InteractiveFrame action bindings from {@link remixlab.bias.event.shortcut.MotionShortcut}s.
	 */
	public Profile<MotionShortcut, A> frameProfile() {
		return frameBranch().profile();
	}

	protected Profile<ClickShortcut, ClickAction> clickProfile(Target target) {
		return target == Target.EYE ? eyeClickProfile() : frameClickProfile();
	}

	/**
	 * Profile defining InteractiveFrame action bindings from {@link remixlab.bias.event.shortcut.ClickShortcut}s.
	 */
	public Profile<ClickShortcut, ClickAction> eyeClickProfile() {
		return eyeBranch().clickProfile();
	}

	/**
	 * Profile defining InteractiveFrame action bindings from {@link remixlab.bias.event.shortcut.ClickShortcut}s.
	 */
	public Profile<ClickShortcut, ClickAction> frameClickProfile() {
		return frameBranch().clickProfile();
	}

	// common api

	public void setPickingMode(PickingMode mode) {
		pMode = mode;
	}

	public PickingMode pickingMode() {
		return pMode;
	}

	// high level (new) with plain shortcuts

	public void setBinding(Target target, MotionShortcut shortcut, A action) {
		motionProfile(target).setBinding(shortcut, action);
	}

	public void setBinding(Target target, ClickShortcut shortcut, ClickAction action) {
		clickProfile(target).setBinding(shortcut, action);
	}

	public void removeBinding(Target target, MotionShortcut shortcut) {
		motionProfile(target).removeBinding(shortcut);
	}

	public void removeBinding(Target target, ClickShortcut shortcut) {
		clickProfile(target).removeBinding(shortcut);
	}

	public boolean hasBinding(Target target, MotionShortcut shortcut) {
		return motionProfile(target).hasBinding(shortcut);
	}

	public boolean hasBinding(Target target, ClickShortcut shortcut) {
		return clickProfile(target).hasBinding(shortcut);
	}

	public A action(Target target, MotionShortcut shortcut) {
		return motionProfile(target).action(shortcut);
	}

	public ClickAction action(Target target, ClickShortcut shortcut) {
		return clickProfile(target).action(shortcut);
	}

	// don't override from here

	public void removeMotionBindings(Target target) {
		motionProfile(target).removeBindings();
	}

	public void removeClickBindings(Target target) {
		clickProfile(target).removeBindings();
	}

	public boolean isActionBound(Target target, A action) {
		return motionProfile(target).isActionBound(action);
	}

	/**
	 * Returns {@code true} if the mouse click action is bound to the given {@code target} (EYE or FRAME).
	 */
	public boolean isActionBound(Target target, ClickAction action) {
		return clickProfile(target).isActionBound(action);
	}
}