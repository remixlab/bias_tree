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

import remixlab.dandelion.addon.Constants.*;
import remixlab.dandelion.core.*;
import remixlab.bias.addon.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.geom.*;
import remixlab.util.*;

/**
 * An interactive-frame is a {@link remixlab.dandelion.core.GrabberFrame} that implements the
 * {@link remixlab.bias.addon.InteractiveGrabber} interface. An interactive-frame implements the following actions
 * on top of the grabber-frame gesture converting methods (such as {@link #gestureArcball(MotionEvent)},
 * {@link #gestureDrive(MotionEvent)}, etc): {@link remixlab.dandelion.addon.Constants.KeyboardAction},
 * {@link remixlab.dandelion.addon.Constants.ClickAction}, {@link remixlab.dandelion.addon.Constants.DOF1Action},
 * {@link remixlab.dandelion.addon.Constants.DOF2Action}, {@link remixlab.dandelion.addon.Constants.DOF3Action} and
 * {@link remixlab.dandelion.addon.Constants.DOF6Action}. The {@link remixlab.dandelion.core.GrabberScene#motionAgent()}
 * and the {@link remixlab.dandelion.core.GrabberScene#keyboardAgent()} provide high-level methods to define custom
 * action bindings.
 * <p>
 * An interactive-frame rotates either around the {@link remixlab.dandelion.core.Eye#anchor()} (e.g.,
 * {@link remixlab.dandelion.addon.Constants.DOF2Action#ROTATE}, {@link remixlab.dandelion.addon.Constants.DOF6Action#HINGE}),
 * or its {@link #sceneUpVector()} (e.g., {@link remixlab.dandelion.addon.Constants.DOF2Action#ROTATE_CAD}). In the latter
 * case, the {@link #sceneUpVector()} defines a 'vertical' direction around which the camera rotates. The camera can
 * rotate left or right, around this axis. It can also be moved up or down to show the 'top' and 'bottom' views of the
 * scene. As a result, the {@link #sceneUpVector()} will always appear vertical in the scene, and the horizon is preserved
 * and stays projected along the camera's horizontal axis. Use {@link remixlab.dandelion.core.Camera#setUpVector(Vec)} to
 * define the {@link #sceneUpVector()} and align the camera before starting a
 * {@link remixlab.dandelion.addon.Constants.DOF2Action#ROTATE_CAD} action to ensure these invariants are preserved.
 * 
 * @see remixlab.dandelion.addon.MotionAgent
 * @see remixlab.dandelion.addon.KeyboardAgent
 */
public class InteractiveFrame extends GrabberFrame implements InteractiveGrabber<MotionAction>, Copyable,
		Constants {

	// Multiple tempi actions require this:
	remixlab.bias.addon.Action<MotionAction>	initAction;
	// private MotionAction twotempi;
	// private A a;//TODO study make me an attribute to com between init and end
	protected boolean			need4Spin;
	protected boolean			need4Tossing;
	protected boolean			drive;
	protected boolean			rotateHint;
	protected MotionEvent	currMotionEvent;
	public MotionEvent		initMotionEvent;
	public DOF2Event			zor;
	protected float				flySpeedCache;

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				appendSuper(super.hashCode()).
				append(action).
				append(initAction).
				append(need4Spin).
				append(drive).
				append(rotateHint).
				append(currMotionEvent).
				append(initMotionEvent).
				append(zor).
				append(flySpeedCache).
				toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;

		InteractiveFrame other = (InteractiveFrame) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(action, other.action)
				.append(initAction, other.initAction)
				.append(need4Spin, other.need4Spin)
				.append(drive, other.drive)
				.append(rotateHint, other.rotateHint)
				.append(currMotionEvent, other.currMotionEvent)
				.append(initMotionEvent, other.initMotionEvent)
				.append(zor, other.zor)
				.append(flySpeedCache, other.flySpeedCache)
				.isEquals();
	}

	/**
	 * Same as {@code this(scn, null, new Vec(), scn.is3D() ? new Quat() : new Rot(), 1)}.
	 * 
	 * @see #InteractiveFrame(InteractiveScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(InteractiveScene scn) {
		this(scn, null, new Vec(), scn.is3D() ? new Quat() : new Rot(), 1);
	}

	/**
	 * Same as {@code this(scn, null, p, scn.is3D() ? new Quat() : new Rot(), 1)}.
	 * 
	 * @see #InteractiveFrame(InteractiveScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(InteractiveScene scn, Vec p) {
		this(scn, null, p, scn.is3D() ? new Quat() : new Rot(), 1);
	}

	/**
	 * Same as {@code this(scn, null, new Vec(), r, 1)}.
	 * 
	 * @see #InteractiveFrame(InteractiveScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(InteractiveScene scn, Rotation r) {
		this(scn, null, new Vec(), r, 1);
	}

	/**
	 * Same as {@code this(scn, null, new Vec(), scn.is3D() ? new Quat() : new Rot(), s)}.
	 * 
	 * @see #InteractiveFrame(InteractiveScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(InteractiveScene scn, float s) {
		this(scn, null, new Vec(), scn.is3D() ? new Quat() : new Rot(), s);
	}

	/**
	 * Same as {@code this(scn, null, p, scn.is3D() ? new Quat() : new Rot(), s)}.
	 * 
	 * @see #InteractiveFrame(InteractiveScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(InteractiveScene scn, Vec p, float s) {
		this(scn, null, p, scn.is3D() ? new Quat() : new Rot(), s);
	}

	/**
	 * Same as {@code this(scn, null, p, r, 1)}.
	 * 
	 * @see #InteractiveFrame(InteractiveScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(InteractiveScene scn, Vec p, Rotation r) {
		this(scn, null, p, r, 1);
	}

	/**
	 * Same as {@code this(scn, null, new Vec(), r, s)}.
	 * 
	 * @see #InteractiveFrame(InteractiveScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(InteractiveScene scn, Rotation r, float s) {
		this(scn, null, new Vec(), r, s);
	}

	/**
	 * Same as {@code this(scn, null, p, r, s)}.
	 * 
	 * @see #InteractiveFrame(InteractiveScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(InteractiveScene scn, Vec p, Rotation r, float s) {
		this(scn, null, p, r, s);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, new Vec(), scn.is3D() ? new Quat() : new Rot(), 1)}.
	 * 
	 * @see #InteractiveFrame(InteractiveScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(InteractiveScene scn, Frame referenceFrame) {
		this(scn, referenceFrame, new Vec(), scn.is3D() ? new Quat() : new Rot(), 1);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, p, scn.is3D() ? new Quat() : new Rot(), 1)}.
	 * 
	 * @see #InteractiveFrame(InteractiveScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(InteractiveScene scn, Frame referenceFrame, Vec p) {
		this(scn, referenceFrame, p, scn.is3D() ? new Quat() : new Rot(), 1);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, new Vec(), r, 1)}.
	 * 
	 * @see #InteractiveFrame(InteractiveScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(InteractiveScene scn, Frame referenceFrame, Rotation r) {
		this(scn, referenceFrame, new Vec(), r, 1);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, new Vec(), scn.is3D() ? new Quat() : new Rot(), s)}.
	 * 
	 * @see #InteractiveFrame(InteractiveScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(InteractiveScene scn, Frame referenceFrame, float s) {
		this(scn, referenceFrame, new Vec(), scn.is3D() ? new Quat() : new Rot(), s);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, p, scn.is3D() ? new Quat() : new Rot(), s)}.
	 * 
	 * @see #InteractiveFrame(InteractiveScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(InteractiveScene scn, Frame referenceFrame, Vec p, float s) {
		this(scn, referenceFrame, p, scn.is3D() ? new Quat() : new Rot(), s);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, p, r, 1)}.
	 * 
	 * @see #InteractiveFrame(InteractiveScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(InteractiveScene scn, Frame referenceFrame, Vec p, Rotation r) {
		this(scn, referenceFrame, p, r, 1);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, new Vec(), r, s)}.
	 * 
	 * @see #InteractiveFrame(InteractiveScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(InteractiveScene scn, Frame referenceFrame, Rotation r, float s) {
		this(scn, referenceFrame, new Vec(), r, s);
	}

	/**
	 * Same as {@code super(scn, referenceFrame, p, r, s)}.
	 * 
	 * @see remixlab.dandelion.core.GrabberFrame#GrabberFrame(GrabberScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(InteractiveScene scn, Frame referenceFrame, Vec p, Rotation r, float s) {
		super(scn, referenceFrame, p, r, s);
	}

	/**
	 * Default interactive-frame eye constructor (See {@link #isEyeFrame()}). Same as {@code super(theEye)}.
	 * 
	 * @see remixlab.dandelion.core.GrabberFrame#GrabberFrame(Eye, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(Eye theEye) {
		super(theEye);
	}

	protected InteractiveFrame(InteractiveFrame otherFrame) {
		super(otherFrame);
		this.setAction(otherFrame.action());
		this.initAction = otherFrame.initAction;
		if (otherFrame.currMotionEvent != null)
			this.currMotionEvent = otherFrame.currMotionEvent.get();
		if (otherFrame.initMotionEvent != null)
			this.initMotionEvent = otherFrame.initMotionEvent.get();
		if (otherFrame.zor != null)
			this.zor = otherFrame.zor.get();
	}

	@Override
	public InteractiveFrame get() {
		return new InteractiveFrame(this);
	}
	
	@Override
	public InteractiveFrame detach() {
		InteractiveFrame frame = new InteractiveFrame((InteractiveScene)gScene);
		for(Agent agent : gScene.inputHandler().agents())
			agent.removeGrabber(frame);
		frame.fromFrame(this);
		return frame;
	}

	// grabber implementation

	protected remixlab.bias.addon.Action<MotionAction>	action;

	/**
	 * Returns the {@link #action()} reference action. 
	 * 
	 * @see remixlab.bias.addon.Action#referenceAction()
	 */
	public MotionAction referenceAction() {
		return action != null ? action.referenceAction() : null;
	}

	@Override
	public void setAction(remixlab.bias.addon.Action<MotionAction> a) {
		action = a;
	}

	@Override
	public remixlab.bias.addon.Action<MotionAction> action() {
		return action;
	}

	/**
	 * Returns {@code true} when the InteractiveFrame is being manipulated with an agent.
	 */
	public boolean isInInteraction() {
		return action != null;
	}

	@Override
	public void performInteraction(BogusEvent event) {
		if (processEvent(event))
			return;
		if (event instanceof KeyboardEvent)
			performInteraction((KeyboardEvent) event);
		if (event instanceof ClickEvent)
			performInteraction((ClickEvent) event);
		if (event instanceof MotionEvent)
			performInteraction((MotionEvent) event);
	}

	@Override
	protected void performInteraction(ClickEvent event) {
		switch (referenceAction()) {
		case CUSTOM:
			performCustomAction(event);
			break;
		case ALIGN_FRAME:
			align();
			break;
		case ANCHOR_FROM_PIXEL:
			if (!isEyeFrame()) {
				InteractiveScene.showOnlyEyeWarning(referenceAction());
				break;
			}
			eye().setAnchorFromPixel(new Point(event.x(), event.y()));
			break;
		case CENTER_FRAME:
			center();
			break;
		case ZOOM_ON_PIXEL:
			if (!isEyeFrame()) {
				InteractiveScene.showOnlyEyeWarning(referenceAction());
				break;
			}
			eye().interpolateToZoomOnPixel(new Point(event.x(), event.y()));
			break;
		default:
			InteractiveScene.showClickWarning(referenceAction());
			break;
		}
	}

	@Override
	protected void performInteraction(MotionEvent event) {
		switch (referenceAction()) {
		case CUSTOM:
			performCustomAction(event);
			break;
		case DRIVE:
			gestureDrive(event);
			break;
		case HINGE:
			gestureHinge(event);
			break;
		case LOOK_AROUND:
			rotate(rollPitchQuaternion(event, gScene.camera()));
			break;
		case MOVE_BACKWARD:
			gestureMoveForward(event, false);
			break;
		case MOVE_FORWARD:
			gestureMoveForward(event, true);
			break;
		case ROTATE:
			gestureArcball(event);
			break;
		case ROTATE_CAD:
			// TODO study merge with previous
			// gestureArcball + gestureRotateCAD = gestureRotateDOF2?
			gestureRotateCAD(event);
			break;
		case ROTATE_X:
			gestureRotateX(event);
			break;
		case ROTATE_XYZ:
			gestureRotateXYZ(event);
			break;
		case ROTATE_Y:
			gestureRotateY(event);
			break;
		case ROTATE_Z:
			gestureRotateZ(event);
			break;
		case SCALE:
			gestureScale(event);
			break;
		case SCREEN_ROTATE:
			gestureScreenRotate(event);
			break;
		case SCREEN_TRANSLATE:
			int dir = originalDirection(event);
			// TODO try to simplify second argument
			if (dir == 1)
				gestureTranslateX(event, true);
			else if (dir == -1)
				gestureTranslateY(event, true);
			break;
		case TRANSLATE:
			gestureTranslateXY(event, true);
			break;
		case TRANSLATE_X:
			gestureTranslateX(event, true);
			break;
		case TRANSLATE_XYZ:
			gestureTranslateXYZ(event);
			break;
		case TRANSLATE_XYZ_ROTATE_XYZ:
			// A. Translate the iFrame
			gestureTranslateXYZ(event);
			// B. Rotate the iFrame
			gestureRotateXYZ(event);
			break;
		case TRANSLATE_Y:
			gestureTranslateY(event, false);
			break;
		case TRANSLATE_Z:
			gestureTranslateZ(event, true);
			break;
		case ZOOM_ON_ANCHOR:
			gestureZoomOnAnchor(event);
			break;
		case ZOOM_ON_REGION:
			// this get called when processing the action
			// gestureZoomOnRegion(event);
			break;
		default:
			InteractiveScene.showMotionWarning(referenceAction());
			break;
		}
	}

	@Override
	protected void performInteraction(KeyboardEvent event) {
		switch (referenceAction()) {
		case ALIGN_FRAME:
			align();
			break;
		case CENTER_FRAME:
			center();
			break;
		case CUSTOM:
			performCustomAction(event);
			break;
		case SCALE_UP:
			gestureScale(event, true);
			break;
		case SCALE_DOWN:
			gestureScale(event, false);
			break;
		case ZOOM_IN_ON_ANCHOR:
			gestureZoomOnAnchor(event, true);
			break;
		case ZOOM_OUT_ON_ANCHOR:
			gestureZoomOnAnchor(event, false);
			break;
		case ROTATE_X_POS:
			gestureRotateX(event, true);
			break;
		case ROTATE_X_NEG:
			gestureRotateX(event, false);
			break;
		case ROTATE_Y_POS:
			gestureRotateY(event, true);
			break;
		case ROTATE_Y_NEG:
			gestureRotateY(event, false);
			break;
		case ROTATE_Z_POS:
			gestureRotateZ(event, true);
			break;
		case ROTATE_Z_NEG:
			gestureRotateZ(event, false);
			break;
		case TRANSLATE_X_POS:
			gestureTranslateX(event, true);
			break;
		case TRANSLATE_X_NEG:
			gestureTranslateX(event, false);
			break;
		case TRANSLATE_Y_POS:
			gestureTranslateY(event, true);
			break;
		case TRANSLATE_Y_NEG:
			gestureTranslateY(event, false);
			break;
		case TRANSLATE_Z_POS:
			gestureTranslateZ(event, true);
			break;
		case TRANSLATE_Z_NEG:
			gestureTranslateZ(event, false);
			break;
		default:
			InteractiveScene.showKeyboardWarning(referenceAction());
			break;
		}
	}

	// Custom

	/**
	 * Override me when implementing the {@link remixlab.dandelion.addon.Constants.KeyboardAction#CUSTOM} action. 
	 */
	protected void performCustomAction(KeyboardEvent event) {
		GrabberScene.showMissingImplementationWarning("performCustomAction(KeyboardEvent event)", this.getClass()
				.getName());
	}

	/**
	 * Override me when implementing the {@link remixlab.dandelion.addon.Constants.ClickAction#CUSTOM} action. 
	 */
	protected void performCustomAction(ClickEvent event) {
		GrabberScene.showMissingImplementationWarning("performCustomAction(ClickEvent event)", this.getClass().getName());
	}

	/**
	 * Internal use. Bogus event decomposition. 
	 */
	protected void performCustomAction(MotionEvent event) {
		if (event instanceof DOF1Event)
			performCustomAction((DOF1Event) event);
		if (event instanceof DOF2Event)
			performCustomAction((DOF2Event) event);
		if (event instanceof DOF3Event)
			performCustomAction((DOF3Event) event);
		if (event instanceof DOF6Event)
			performCustomAction((DOF6Event) event);
	}
	
	/**
	 * Override me when implementing the {@link remixlab.dandelion.addon.Constants.DOF6Action#CUSTOM} action. 
	 */
	protected void performCustomAction(DOF1Event event) {
		GrabberScene.showMissingImplementationWarning("performCustomAction(DOF1Event event)", this.getClass().getName());
	}

	/**
	 * Override me when implementing the {@link remixlab.dandelion.addon.Constants.DOF6Action#CUSTOM} action. 
	 */
	protected void performCustomAction(DOF2Event event) {
		GrabberScene.showMissingImplementationWarning("performCustomAction(DOF2Event event)", this.getClass().getName());
	}

	/**
	 * Override me when implementing the {@link remixlab.dandelion.addon.Constants.DOF6Action#CUSTOM} action. 
	 */
	protected void performCustomAction(DOF3Event event) {
		GrabberScene.showMissingImplementationWarning("performCustomAction(DOF3Event event)", this.getClass().getName());
	}

	/**
	 * Override me when implementing the {@link remixlab.dandelion.addon.Constants.DOF6Action#CUSTOM} action. 
	 */
	protected void performCustomAction(DOF6Event event) {
		GrabberScene.showMissingImplementationWarning("performCustomAction(DOF6Event event)", this.getClass().getName());
	}

	// two tempi actions workflow divert from 'normal' (single tempi) actions which just require
	// either updateTrackeGrabber(event) or handle(event).

	protected MotionEvent initMotionEvent() {
		return initMotionEvent;
	}

	protected MotionEvent currentMotionEvent() {
		return currMotionEvent;
	}

	/**
	 * Internal use. Algorithm to split a gesture flow into a 'three-tempi' {@link remixlab.bias.addon.Action} sequence.
	 * It's called like this (see {@link #performInteraction(BogusEvent)}):
	 * <pre>
     * {@code
	 * public void performInteraction(BogusEvent event) {
	 *	if (processEvent(event))
	 *		return;
	 *	if (event instanceof KeyboardEvent)
	 *		performInteraction((KeyboardEvent) event);
	 *	if (event instanceof ClickEvent)
	 *		performInteraction((ClickEvent) event);
	 *	if (event instanceof MotionEvent)
	 *		performInteraction((MotionEvent) event);
	 * }
     * }
     * </pre>
	 * <p>
	 * The algorithm parses the bogus-event in {@link #performInteraction(BogusEvent)} and then decide what to call:
	 * <ol>
     * <li>{@link #initAction(BogusEvent)} (1st tempi): sets the initAction, called when initAction == null.</li>
     * <li>{@link #execAction(BogusEvent)} (2nd tempi): continues action execution, called when initAction == action()
     * (current action)</li>
     * <li>{@link #flushAction(BogusEvent)} (3rd): ends action, called when {@link remixlab.bias.core.BogusEvent#flushed()}
     * is true or when initAction != action()</li>
     * </ol>
     * <p>
     * Useful to parse multiple-tempi gestures, such as a mouse press/move/drag/release flow.
     * <p>
     * The following motion-actions have been implemented using the aforementioned technique:
	 * {@link remixlab.dandelion.addon.Constants.DOF2Action#SCREEN_ROTATE},
	 * {@link remixlab.dandelion.addon.Constants.DOF2Action#ZOOM_ON_REGION},
	 * {@link remixlab.dandelion.addon.Constants.DOF2Action#MOVE_BACKWARD}, and
	 * {@link remixlab.dandelion.addon.Constants.DOF2Action#MOVE_FORWARD}.
	 * <p>
     * Current implementation only supports {@link remixlab.bias.event.MotionEvent}s.
	 */
	protected final boolean processEvent(BogusEvent event) {
		if (initAction == null) {
			if (!event.flushed()) {
				return initAction(event);// start action
			}
		}
		else { // initAction != null
			if (!event.flushed()) {
				if (initAction == action())
					return execAction(event);// continue action
				else { // initAction != action() -> action changes abruptly
					flushAction(event);
					return initAction(event);// start action
				}
			}
			else {// action() == null
				flushAction(event);// stopAction
				initAction = null;
				//setAction(null); // experimental, but sounds logical since: initAction != null && action() == null
				return true;
			}
		}
		return true;// i.e., if initAction == action() == null -> ignore :)
	}

	// init domain

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected boolean initAction(BogusEvent event) {
		initAction = action();
		if (event instanceof KeyboardEvent)
			return initAction((KeyboardEvent) event);
		if (event instanceof ClickEvent)
			return initAction((ClickEvent) event);
		if (event instanceof MotionEvent)
			return initAction((MotionEvent) event);
		return false;
	}

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected boolean initAction(ClickEvent event) {
		return false;
	}
	
	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected boolean initAction(KeyboardEvent event) {
		return false;
	}

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected boolean initAction(MotionEvent event) {
		if (event instanceof DOF1Event)
			return false;
		return initAction(MotionEvent.dof2Event(event));
	}

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected boolean initAction(DOF2Event event) {
		initMotionEvent = event.get();
		currMotionEvent = event;
		stopSpinning();
		MotionAction twotempi = action().referenceAction();
		if (twotempi == MotionAction.SCREEN_TRANSLATE)
			dirIsFixed = false;
		boolean rotateMode = ((twotempi == MotionAction.ROTATE) || (twotempi == MotionAction.ROTATE_XYZ)
				|| (twotempi == MotionAction.ROTATE_CAD)
				|| (twotempi == MotionAction.SCREEN_ROTATE) || (twotempi == MotionAction.TRANSLATE_XYZ_ROTATE_XYZ));
		if (rotateMode && gScene.is3D())
			gScene.camera().cadRotationIsReversed = gScene.camera().frame()
					.transformOf(gScene.camera().frame().sceneUpVector()).y() < 0.0f;
		need4Spin = (rotateMode && (damping() == 0));
		drive = (twotempi == MotionAction.DRIVE);
		if (drive)
			flySpeedCache = flySpeed();
		need4Tossing = (twotempi == MotionAction.MOVE_FORWARD) || (twotempi == MotionAction.MOVE_BACKWARD)
				|| (drive);
		if (need4Tossing)
			updateSceneUpVector();
		rotateHint = twotempi == MotionAction.SCREEN_ROTATE;
		if (rotateHint)
			gScene.setRotateVisualHint(true);
		if (isEyeFrame() && twotempi == MotionAction.ZOOM_ON_REGION) {
			gScene.setZoomVisualHint(true);
			zor = event.get();
			return true;
		}
		return false;
	}

	// exec domain

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected boolean execAction(BogusEvent event) {
		if (event instanceof KeyboardEvent)
			return execAction((KeyboardEvent) event);
		if (event instanceof ClickEvent)
			return execAction((ClickEvent) event);
		if (event instanceof MotionEvent)
			return execAction((MotionEvent) event);
		return false;
	}

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected boolean execAction(MotionEvent event) {
		if (event instanceof DOF1Event)
			return false;
		return execAction(MotionEvent.dof2Event(event));
	}

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected boolean execAction(DOF2Event event) {
		currMotionEvent = event;
		if (zor != null) {
			zor = event.get();
			zor.setPreviousEvent(initMotionEvent.get());
			return true;// bypass
		}
		// never handle ZOOM_ON_REGION on a drag. Could happen if user presses a modifier during drag triggering it
		if (action().referenceAction() == MotionAction.ZOOM_ON_REGION) {
			return true;
		}
		if (drive) {
			setFlySpeed(0.01f * gScene.radius() * 0.01f * (event.y() - event.y()));
			return false;
		}
		return false;
	}

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected boolean execAction(ClickEvent event) {
		return false;
	}
	
	protected boolean execAction(KeyboardEvent event) {
		return false;
	}

	// flushDomain

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected void flushAction(BogusEvent event) {
		if (event instanceof KeyboardEvent)
			flushAction((KeyboardEvent) event);
		if (event instanceof ClickEvent)
			flushAction((ClickEvent) event);
		if (event instanceof MotionEvent)
			flushAction((MotionEvent) event);
	}

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected void flushAction(MotionEvent event) {
		if (!(event instanceof DOF1Event))
			flushAction(MotionEvent.dof2Event(event));
	}
	
	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected void flushAction(KeyboardEvent event) {		
	}

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected void flushAction(ClickEvent event) {
	}

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected void flushAction(DOF2Event event) {
		if (rotateHint) {
			gScene.setRotateVisualHint(false);
			rotateHint = false;
		}
		if (currentMotionEvent() != null) {
			if (need4Spin) {
				startSpinning(spinningRotation(), currentMotionEvent().speed(), currentMotionEvent().delay());
			}
		}
		if (zor != null) {
			// the problem is that depending on the order the button and the modifiers are released,
			// different actions maybe triggered, so we go for sure ;) :
			gScene.setZoomVisualHint(false);
			gestureZoomOnRegion(zor);// now action need to be executed on event
			zor = null;
		}
		if (need4Tossing) {
			// restore speed after drive action terminates:
			if (drive)
				setFlySpeed(flySpeedCache);
			stopFlying();
		}
	}
}