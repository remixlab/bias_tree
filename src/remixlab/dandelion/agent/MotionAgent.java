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

import remixlab.bias.agent.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

/**
 * The root of all dandelion motion agents representing an HID (which may have a wheel), such as
 * a mouse or a touch-screen. This agent specializes in handling
 * {@link remixlab.dandelion.core.InteractiveFrame} objects, but it can also handle third-party
 * {@link remixlab.bias.core.Grabber} or {@link remixlab.bias.core.InteractiveGrabber} object
 * instances. In the latter case, third-parties should implement their own
 * {@link remixlab.bias.agent.MotionBranch}es.
 * <p>
 * The agent has two branches of the type {@code MotionBranch<MotionAction, A, ClickAction>} to handle
 * {@link remixlab.dandelion.core.InteractiveFrame} object instances: {@link #eyeBranch()}, for
 * {@link remixlab.dandelion.core.Constants.Target#EYE} (which typically has one one single
 * instance, that of the {@link remixlab.dandelion.core.AbstractScene#eyeFrame()}); and {@link #frameBranch()}, for
 * {@link remixlab.dandelion.core.Constants.Target#FRAME} (which may have several instances, see
 * {@link #addGrabber(Grabber)}). Note that through the aforementioned branches the following
 * agent {@link remixlab.bias.core.Profile}s are available:
 * {@link #eyeProfile()}, {@link #frameProfile()}, {@link #eyeClickProfile()}
 * and {@link #frameClickProfile()}. Refer to the {@link remixlab.bias.core.Branch} and
 * {@link remixlab.bias.core.Profile} for details.
 * <p>
 * 'Interaction customization', i.e., binding motion and click event shortcuts (see
 * {@link remixlab.bias.event.MotionShortcut}) to dandelion click and motion actions, may
 * be achieved through those profiles or through various of the high-level methods conveniently
 * provided by this agent, such as
 * {@link #setBinding(Target, ClickShortcut, ClickAction)},
 * {@link #setBinding(Target, ClickShortcut, ClickAction)}, and so on. Note that all those
 * methods take a {@link remixlab.dandelion.core.Constants.Target} parameter type which actually
 * denotes the profile the agent should target.
 * <p>
 * The agent's {@link #defaultGrabber()} is the {@link remixlab.dandelion.core.AbstractScene#eye()}
 * frame (see {@link remixlab.dandelion.core.Eye#frame()}) (note that {@link #resetDefaultGrabber()}
 * will thus defaults to the eye frame too).
 * <p>
 * The motion agent defines two picking modes (for selecting {@link remixlab.bias.core.Grabber}
 * objects, see {@link #setPickingMode(PickingMode)} and {@link #pickingMode()}):
 * <ol>
 * <li>{@link remixlab.dandelion.agent.MotionAgent.PickingMode#MOVE}: object selection happens
 * during a drag gesture.</li>
 * <li>{@link remixlab.dandelion.agent.MotionAgent.PickingMode#CLICK}: object selection happens
 * from a click gesture.</li>
 * </ol>
 * 
 * @param <A> Motion Action used the parameterize the agent. It defined the agent DOFs.
 * 
 * @see remixlab.dandelion.core.Constants.MotionAction
 * @see remixlab.dandelion.core.Constants.ClickAction
 */
public abstract class MotionAgent<A extends Action<MotionAction>> extends AbstractMotionAgent {
	protected AbstractScene																scene;
	protected MotionBranch<MotionAction, A, ClickAction>	eyeBranch;
	protected MotionBranch<MotionAction, A, ClickAction>	frameBranch;
	protected PickingMode																	pMode;

	public enum PickingMode {
		MOVE, CLICK
	}; // this actually affect all grabbers!

	protected float	wSens	= 1f;

	/**
	 * Creates a motion agent and appends the {@link #eyeBranch()} and {@link #frameBranch()} to it.
	 * The motion agent is added to to the {@link remixlab.dandelion.core.AbstractScene#inputHandler()}.
	 */
	public MotionAgent(AbstractScene scn, String n) {
		super(scn.inputHandler(), n);
		scene = scn;
		eyeBranch = appendBranch(n + "_eye_mouse_branch");
		frameBranch = appendBranch(n + "_frame_mouse_branch");
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
	 * Returns the wheel sensitivity. Default value is 1.
	 * 
	 * @see #setWheelSensitivity(float)
	 */
	public float wheelSensitivity() {
		return wSens;
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
	
	/**
	 * Returns the eye {@code MotionBranch<MotionAction, A, ClickAction>}.
	 */
	public MotionBranch<MotionAction, A, ClickAction> eyeBranch() {
		return eyeBranch;
	}

	/**
	 * Returns the frame {@code MotionBranch<MotionAction, A, ClickAction>}.
	 */
	public MotionBranch<MotionAction, A, ClickAction> frameBranch() {
		return frameBranch;
	}

	/**
	 * Same as {@code return target == Target.EYE ? eyeProfile() : frameProfile()}
	 */
	protected Profile<MotionAction, MotionShortcut, A> motionProfile(Target target) {
		return target == Target.EYE ? eyeProfile() : frameProfile();
	}
	
	/**
	 * Same as {@code return eyeBranch().motionProfile()}
	 * 
	 * @see #eyeBranch()
	 */
	public Profile<MotionAction, MotionShortcut, A> eyeProfile() {
		return eyeBranch().motionProfile();
	}

	/**
	 * Same as {@code return frameBranch().motionProfile()}
	 * 
	 * @see #frameBranch()
	 */
	public Profile<MotionAction, MotionShortcut, A> frameProfile() {
		return frameBranch().motionProfile();
	}

	/**
	 * Same as {@code return target == Target.EYE ? eyeClickProfile() : frameClickProfile()}
	 */
	protected Profile<MotionAction, ClickShortcut, ClickAction> clickProfile(Target target) {
		return target == Target.EYE ? eyeClickProfile() : frameClickProfile();
	}
	
	/**
	 * Same as {@code return eyeBranch().clickProfile()}
	 * 
	 * @see #eyeBranch()
	 */
	public Profile<MotionAction, ClickShortcut, ClickAction> eyeClickProfile() {
		return eyeBranch().clickProfile();
	}

	/**
	 * Same as {@code return frameBranch().clickProfile()}
	 * 
	 * @see #frameBranch()
	 */
	public Profile<MotionAction, ClickShortcut, ClickAction> frameClickProfile() {
		return frameBranch().clickProfile();
	}

	// common api
	
	/**
	 * Sets the agent {@link #pickingMode()}. Either {@link PickingMode#MOVE} or {@link PickingMode#CLICK}.
	 * 
	 * @see #pickingMode()
	 */
	public void setPickingMode(PickingMode mode) {
		pMode = mode;
	}

	/**
	 * Returns the agent {@link #pickingMode()}. Either {@link PickingMode#MOVE} or {@link PickingMode#CLICK}.
	 * 
	 * @see #setPickingMode(PickingMode)
	 */
	public PickingMode pickingMode() {
		return pMode;
	}

	// high level (new) with plain shortcuts

	/**
	 * Same as {@code motionProfile(target).setBinding(shortcut, action)}.
	 * 
	 * @see #motionProfile(Target)
	 */
	public void setBinding(Target target, MotionShortcut shortcut, A action) {
		motionProfile(target).setBinding(shortcut, action);
	}

	/**
	 * Same as {@code clickProfile(target).setBinding(shortcut, action)}.
	 * 
	 * @see #clickProfile(Target)
	 */
	public void setBinding(Target target, ClickShortcut shortcut, ClickAction action) {
		clickProfile(target).setBinding(shortcut, action);
	}

	/**
	 * Same as {@code motionProfile(target).removeBinding(shortcut)}.
	 * 
	 * @see #motionProfile(Target)
	 */
	public void removeBinding(Target target, MotionShortcut shortcut) {
		motionProfile(target).removeBinding(shortcut);
	}

	/**
	 * Same as {@code clickProfile(target).removeBinding(shortcut)}.
	 * 
	 * @see #clickProfile(Target)
	 */
	public void removeBinding(Target target, ClickShortcut shortcut) {
		clickProfile(target).removeBinding(shortcut);
	}

	/**
	 * Same as {@code return motionProfile(target).hasBinding(shortcut)}.
	 * 
	 * @see #motionProfile(Target)
	 */
	public boolean hasBinding(Target target, MotionShortcut shortcut) {
		return motionProfile(target).hasBinding(shortcut);
	}

	/**
	 * Same as {@code return clickProfile(target).hasBinding(shortcut)}.
	 * 
	 * @see #clickProfile(Target)
	 */
	public boolean hasBinding(Target target, ClickShortcut shortcut) {
		return clickProfile(target).hasBinding(shortcut);
	}

	/**
	 * Same as {@code return motionProfile(target).action(shortcut)}.
	 * 
	 * @see #motionProfile(Target)
	 */
	public A action(Target target, MotionShortcut shortcut) {
		return motionProfile(target).action(shortcut);
	}

	/**
	 * Same as {@code return clickProfile(target).action(shortcut)}.
	 * 
	 * @see #clickProfile(Target)
	 */
	public ClickAction action(Target target, ClickShortcut shortcut) {
		return clickProfile(target).action(shortcut);
	}

	// don't override from here

	/**
	 * Same as {@code motionProfile(target).removeBindings()}.
	 * 
	 * @see #motionProfile(Target)
	 */
	public void removeMotionBindings(Target target) {
		motionProfile(target).removeBindings();
	}

	/**
	 * Same as {@code clickProfile(target).removeBindings()}.
	 * 
	 * @see #clickProfile(Target)
	 */
	public void removeClickBindings(Target target) {
		clickProfile(target).removeBindings();
	}

	/**
	 * Same as {@code return motionProfile(target).isActionBound(action)}.
	 * 
	 * @see #motionProfile(Target)
	 */
	public boolean isActionBound(Target target, A action) {
		return motionProfile(target).isActionBound(action);
	}

	/**
	 * Same as {@code return clickProfile(target).isActionBound(action)}.
	 * 
	 * @see #clickProfile(Target)
	 */
	public boolean isActionBound(Target target, ClickAction action) {
		return clickProfile(target).isActionBound(action);
	}
}