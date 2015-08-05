/*********************************************************************************
 * dandelion_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.dandelion.addon;

import remixlab.bias.addon.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.addon.Constants.*;
import remixlab.dandelion.core.*;

/**
 * The root of all dandelion motion agents (which may have a wheel), such as a mouse or a
 * touch-screen. This agent specializes in handling
 * {@link remixlab.dandelion.addon.InteractiveFrame} objects, but it can also handle third-party
 * {@link remixlab.bias.core.Grabber} or {@link remixlab.bias.addon.InteractiveGrabber} object
 * instances. In the latter case, third-parties should implement their own
 * {@link remixlab.bias.addon.MotionBranch}es.
 * <p>
 * The agent has two branches of the type {@code MotionBranch<MotionAction, A, ClickAction>} to handle
 * {@link remixlab.dandelion.addon.InteractiveFrame} object instances: {@link #eyeBranch()}, for
 * {@link remixlab.dandelion.addon.Constants.Target#EYE} (which typically has one one single
 * instance, that of the {@link remixlab.dandelion.core.GrabberScene#eyeFrame()}); and {@link #frameBranch()}, for
 * {@link remixlab.dandelion.addon.Constants.Target#FRAME} (which may have several instances, see
 * {@link #addGrabber(Grabber)}). Note that through the aforementioned branches the following
 * agent {@link remixlab.bias.addon.Profile}s are available:
 * {@link #eyeProfile()}, {@link #frameProfile()}, {@link #eyeClickProfile()}
 * and {@link #frameClickProfile()}. Refer to the {@link remixlab.bias.addon.Branch} and
 * {@link remixlab.bias.addon.Profile} for details.
 * <p>
 * 'Interaction customization', i.e., binding motion and click event shortcuts (see
 * {@link remixlab.bias.event.MotionShortcut}) to dandelion click and motion actions, may
 * be achieved through those profiles or through various of the high-level methods conveniently
 * provided by this agent, such as
 * {@link #setBinding(Target, ClickShortcut, ClickAction)},
 * {@link #setBinding(Target, ClickShortcut, ClickAction)}, and so on. Note that all those
 * methods take a {@link remixlab.dandelion.addon.Constants.Target} parameter type which actually
 * denotes the profile the agent should target.
 * <p>
 * The agent's {@link #defaultGrabber()} is the {@link remixlab.dandelion.core.GrabberScene#eye()}
 * frame (see {@link remixlab.dandelion.core.Eye#frame()}) (note that {@link #resetDefaultGrabber()}
 * will thus defaults to the eye frame too).
 * 
 * @param <A> Motion Action used the parameterize the agent. It defines the maximum degrees-of-freedom
 * (DOFs) that the agent supports. See {@link remixlab.dandelion.addon.TrackballAgent},
 * {@link remixlab.dandelion.addon.WheeledMouseAgent}, {@link remixlab.dandelion.addon.JoystickAgent}
 * and {@link remixlab.dandelion.addon.HIDAgent}.
 * 
 * @see remixlab.dandelion.addon.Constants.MotionAction
 * @see remixlab.dandelion.addon.Constants.ClickAction
 */
public abstract class MotionAgent<A extends remixlab.bias.addon.Action<MotionAction>> extends MotionBranchAgent {
	protected InteractiveScene																scene;
	protected MotionBranch<MotionAction, A, ClickAction>	eyeBranch;
	protected MotionBranch<MotionAction, A, ClickAction>	frameBranch;

	protected float	wSens	= 1f;

	/**
	 * Creates a motion agent and appends the {@link #eyeBranch()} and {@link #frameBranch()} to it.
	 * The motion agent is added to to the {@link remixlab.dandelion.core.GrabberScene#inputHandler()}.
	 */
	public MotionAgent(InteractiveScene scn, String n) {
		super(scn.inputHandler(), n);
		scene = scn;
		eyeBranch = appendBranch(n + "_eye_mouse_branch");
		frameBranch = appendBranch(n + "_frame_mouse_branch");
	}

	/**
	 * Returns the scene this object belongs to
	 */
	public GrabberScene scene() {
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
		if (!(frame instanceof remixlab.bias.addon.InteractiveGrabber))
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
	 * <p>
	 * {@code A} is the ({@code Action<MotionAction>}) used to parameterize the agent. See
	 * {@link remixlab.dandelion.addon.TrackballAgent}, {@link remixlab.dandelion.addon.WheeledMouseAgent},
	 * {@link remixlab.dandelion.addon.JoystickAgent} and {@link remixlab.dandelion.addon.HIDAgent}.
	 */
	public MotionBranch<MotionAction, A, ClickAction> eyeBranch() {
		return eyeBranch;
	}

	/**
	 * Returns the frame {@code MotionBranch<MotionAction, A, ClickAction>}.
	 * <p>
	 * {@code A} is the ({@code Action<MotionAction>}) used to parameterize the agent. See
	 * {@link remixlab.dandelion.addon.TrackballAgent}, {@link remixlab.dandelion.addon.WheeledMouseAgent},
	 * {@link remixlab.dandelion.addon.JoystickAgent} and {@link remixlab.dandelion.addon.HIDAgent}.
	 */
	public MotionBranch<MotionAction, A, ClickAction> frameBranch() {
		return frameBranch;
	}

	/**
	 * Same as {@code return target == Target.EYE ? eyeProfile() : frameProfile()}.
	 * <p>
	 * The profile defines customizable {@link remixlab.bias.core.KeyboardShortcut} to
	 * to (action) {@code A} mappings (bindings) either the {@link remixlab.dandelion.core.GrabberScene#eyeFrame()}
	 * (for {@link remixlab.dandelion.addon.Constants.Target#EYE}); or, for other interactive-frame instances
	 * (for {@link remixlab.dandelion.addon.Constants.Target#FRAME}).
	 * <p>
	 * {@code A} is the ({@code Action<MotionAction>}) used to parameterize the agent. See
	 * {@link remixlab.dandelion.agent.TrackballMouseAgent}, {@link remixlab.dandelion.addon.WheeledMouseAgent},
	 * {@link remixlab.dandelion.agent.JoystickMouseAgent} and {@link remixlab.dandelion.agent.HIDMouseAgent}.
	 * 
	 * @see #eyeProfile()
	 * @see #frameProfile()
	 */
	protected remixlab.bias.addon.Profile<MotionAction, MotionShortcut, A> motionProfile(Target target) {
		return target == Target.EYE ? eyeProfile() : frameProfile();
	}
	
	/**
	 * Same as {@code return eyeBranch().motionProfile()}.
	 * <p>
	 * The profile defines customizable {@link remixlab.bias.core.MotionShortcut} to (action) {@code A} mappings (bindings)
	 * for the {@link remixlab.dandelion.core.GrabberScene#eyeFrame()} instance.
	 * <p>
	 * {@code A} is the ({@code Action<MotionAction>}) used to parameterize the agent. See
	 * {@link remixlab.dandelion.agent.TrackballMouseAgent}, {@link remixlab.dandelion.addon.WheeledMouseAgent},
	 * {@link remixlab.dandelion.agent.JoystickMouseAgent} and {@link remixlab.dandelion.agent.HIDMouseAgent}.
	 * 
	 * @see #eyeBranch()
	 */
	protected remixlab.bias.addon.Profile<MotionAction, MotionShortcut, A> eyeProfile() {
		return eyeBranch().motionProfile();
	}

	/**
	 * Same as {@code return frameBranch().motionProfile()}.
	 * <p>
	 * The profile defines customizable {@link remixlab.bias.core.MotionShortcut} to (action) {@code A} mappings (bindings)
	 * for interactive-frame instances different than the {@link remixlab.dandelion.core.GrabberScene#eyeFrame()}.
	 * <p>
	 * {@code A} is the ({@code Action<MotionAction>}) used to parameterize the agent. See
	 * {@link remixlab.dandelion.agent.TrackballMouseAgent}, {@link remixlab.dandelion.addon.WheeledMouseAgent},
	 * {@link remixlab.dandelion.agent.JoystickMouseAgent} and {@link remixlab.dandelion.agent.HIDMouseAgent}.
	 * 
	 * @see #frameBranch()
	 */
	protected remixlab.bias.addon.Profile<MotionAction, MotionShortcut, A> frameProfile() {
		return frameBranch().motionProfile();
	}

	/**
	 * Same as {@code return target == Target.EYE ? eyeClickProfile() : frameClickProfile()}.
	 * <p>
	 * The profile defines customizable {@link remixlab.bias.core.ClickShortcut} to
	 * to {@link remixlab.dandelion.addon.Constants.ClickAction} mappings (bindings) either the
	 * {@link remixlab.dandelion.core.GrabberScene#eyeFrame()} (for {@link remixlab.dandelion.addon.Constants.Target#EYE});
	 * or, for other interactive-frame instances (for {@link remixlab.dandelion.addon.Constants.Target#FRAME}).
	 */
	protected remixlab.bias.addon.Profile<MotionAction, ClickShortcut, ClickAction> clickProfile(Target target) {
		return target == Target.EYE ? eyeClickProfile() : frameClickProfile();
	}
	
	/**
	 * Same as {@code return eyeBranch().clickProfile()}.
	 * <p>
	 * The profile defines customizable {@link remixlab.bias.core.ClickShortcut} to
	 * {@link remixlab.dandelion.addon.Constants.ClickAction} mappings (bindings) for the
	 * {@link remixlab.dandelion.core.GrabberScene#eyeFrame()} instance.
	 * 
	 * @see #eyeBranch()
	 */
	protected remixlab.bias.addon.Profile<MotionAction, ClickShortcut, ClickAction> eyeClickProfile() {
		return eyeBranch().clickProfile();
	}

	/**
	 * Same as {@code return frameBranch().clickProfile()}.
	 * <p>
	 * The profile defines customizable {@link remixlab.bias.core.ClickShortcut} to
	 * {@link remixlab.dandelion.addon.Constants.ClickAction} mappings (bindings) for interactive-frame instances
	 * different that the {@link remixlab.dandelion.core.GrabberScene#eyeFrame()}.
	 * 
	 * @see #frameBranch()
	 */
	protected remixlab.bias.addon.Profile<MotionAction, ClickShortcut, ClickAction> frameClickProfile() {
		return frameBranch().clickProfile();
	}

	// common api

	// high level (new) with plain shortcuts

	/** 
	 * Defines a motion-shortcut binding for the target interactive-frame. Same as
	 * {@code motionProfile(target).setBinding(shortcut, action)}.
	 * <p>
	 * The {@code target} may be either ({@link remixlab.dandelion.addon.Constants.Target#EYE} to point out the
	 * {@link remixlab.dandelion.core.GrabberScene#eyeFrame()}, or {@link remixlab.dandelion.addon.Constants.Target#FRAME} to specify
	 * the remaining interactive-frames.
	 * 
	 * @see #motionProfile(Target)
	 */
	public void setBinding(Target target, MotionShortcut shortcut, A action) {
		motionProfile(target).setBinding(shortcut, action);
	}

	/**
	 * Defines a click-shortcut binding for the target interactive-frame. Same as
	 * {@code clickProfile(target).setBinding(shortcut, action)}.
	 * <p>
	 * The {@code target} may be either ({@link remixlab.dandelion.addon.Constants.Target#EYE} to point out the
	 * {@link remixlab.dandelion.core.GrabberScene#eyeFrame()}, or {@link remixlab.dandelion.addon.Constants.Target#FRAME} to specify
	 * the remaining interactive-frames.
	 * 
	 * @see #clickProfile(Target)
	 */
	public void setBinding(Target target, ClickShortcut shortcut, ClickAction action) {
		clickProfile(target).setBinding(shortcut, action);
	}

	/**
	 * Removes the target interactive-frame motion-shortcut binding. Same as
	 * {@code motionProfile(target).removeBinding(shortcut)}.
	 * <p>
	 * The {@code target} may be either ({@link remixlab.dandelion.addon.Constants.Target#EYE} to point out the
	 * {@link remixlab.dandelion.core.GrabberScene#eyeFrame()}, or {@link remixlab.dandelion.addon.Constants.Target#FRAME} to specify
	 * the remaining interactive-frames.
	 * 
	 * @see #motionProfile(Target)
	 */
	public void removeBinding(Target target, MotionShortcut shortcut) {
		motionProfile(target).removeBinding(shortcut);
	}

	/**
	 * Removes the target interactive-frame click-shortcut binding. Same as {@code clickProfile(target).removeBinding(shortcut)}.
	 * <p>
	 * The {@code target} may be either ({@link remixlab.dandelion.addon.Constants.Target#EYE} to point out the
	 * {@link remixlab.dandelion.core.GrabberScene#eyeFrame()}, or {@link remixlab.dandelion.addon.Constants.Target#FRAME} to specify
	 * the remaining interactive-frames.
	 * 
	 * @see #clickProfile(Target)
	 */
	public void removeBinding(Target target, ClickShortcut shortcut) {
		clickProfile(target).removeBinding(shortcut);
	}

	/**
	 * Checks if the target interactive-frame or other frames motion-shortcut binding exists (true/false).
	 * Same as {@code return motionProfile(target).hasBinding(shortcut)}.
	 * <p>
	 * The {@code target} may be either ({@link remixlab.dandelion.addon.Constants.Target#EYE} to point out the
	 * {@link remixlab.dandelion.core.GrabberScene#eyeFrame()}, or {@link remixlab.dandelion.addon.Constants.Target#FRAME} to specify
	 * the remaining interactive-frames.
	 * 
	 * @see #motionProfile(Target)
	 */
	public boolean hasBinding(Target target, MotionShortcut shortcut) {
		return motionProfile(target).hasBinding(shortcut);
	}

	/**
	 * Checks if the target interactive-frame or other frames click-shortcut binding exists (true/false).
	 * Same as {@code return clickProfile(target).hasBinding(shortcut)}.
	 * <p>
	 * The {@code target} may be either ({@link remixlab.dandelion.addon.Constants.Target#EYE} to point out the
	 * {@link remixlab.dandelion.core.GrabberScene#eyeFrame()}, or {@link remixlab.dandelion.addon.Constants.Target#FRAME} to specify
	 * the remaining interactive-frames.
	 * 
	 * @see #clickProfile(Target)
	 */
	public boolean hasBinding(Target target, ClickShortcut shortcut) {
		return clickProfile(target).hasBinding(shortcut);
	}

	/**
	 * Returns the target interactive-frame action that is bound to the motion-shortcut (may be null).
	 * Same as {@code return motionProfile(target).action(shortcut)}.
	 * <p>
	 * The {@code target} may be either ({@link remixlab.dandelion.addon.Constants.Target#EYE} to point out the
	 * {@link remixlab.dandelion.core.GrabberScene#eyeFrame()}, or {@link remixlab.dandelion.addon.Constants.Target#FRAME} to specify
	 * the remaining interactive-frames.
	 * 
	 * @see #motionProfile(Target)
	 */
	public A action(Target target, MotionShortcut shortcut) {
		return motionProfile(target).action(shortcut);
	}

	/**
	 * Returns the target interactive-frame action that is bound to the click-shortcut (may be null).
	 * Same as {@code return clickProfile(target).action(shortcut)}.
	 * <p>
	 * The {@code target} may be either ({@link remixlab.dandelion.addon.Constants.Target#EYE} to point out the
	 * {@link remixlab.dandelion.core.GrabberScene#eyeFrame()}, or {@link remixlab.dandelion.addon.Constants.Target#FRAME} to specify
	 * the remaining interactive-frames.
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