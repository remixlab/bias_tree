/*********************************************************************************
 * dandelion_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.dandelion.core;

import remixlab.dandelion.core.Constants.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.geom.*;
import remixlab.util.*;

/**
 * An InteractiveFrame is a Frame that can be rotated, translated and scaled by user interaction means.
 * <p>
 * An InteractiveFrame converts user gestures into translation, rotation and scaling updates. An InteractiveFrame is
 * used to move an object in the scene (and thus it's tightly-coupled with it). Combined with object selection, its
 * Grabber properties and a dynamic update of the scene, the InteractiveFrame introduces a great reactivity to your
 * dandelion-based applications.
 * <p>
 * The possible actions that can interactively be performed by the InteractiveFrame are
 * {@link remixlab.dandelion.core.Constants.ClickAction}, {@link remixlab.dandelion.core.Constants.DOF1Action},
 * {@link remixlab.dandelion.core.Constants.DOF2Action}, {@link remixlab.dandelion.core.Constants.DOF3Action} and
 * {@link remixlab.dandelion.core.Constants.DOF6Action}. The {@link remixlab.dandelion.core.AbstractScene#motionAgent()}
 * provides high-level methods to handle some of these actions, e.g., a
 * {@link remixlab.dandelion.agent.WheeledMouseAgent} can handle up to
 * {@link remixlab.dandelion.core.Constants.DOF2Action}s
 * <p>
 * <b>Note:</b> Once created, the InteractiveFrame is automatically added to the scene
 * {@link remixlab.bias.core.InputHandler#agents()} pool.
 */
public class InteractiveFrame extends SceneFrame implements ActionGrabber<MotionAction>, Copyable, Constants {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				appendSuper(super.hashCode()).
				append(grabsInputThreshold).
				append(adpThreshold).
				append(isInCamPath).
				// append(flyDisp).
				// append(flySpd).
				// append(scnUpVec).
				append(action).
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
				.append(grabsInputThreshold, other.grabsInputThreshold)
				.append(adpThreshold, other.adpThreshold)
				.append(isInCamPath, other.isInCamPath)
				.append(dampFriction, other.dampFriction)
				// .append(flyDisp, other.flyDisp)
				// .append(flySpd, other.flySpd)
				// .append(scnUpVec, other.scnUpVec)
				.append(action, other.action)
				.isEquals();
	}

	private float			grabsInputThreshold;
	private boolean		adpThreshold;

	protected boolean	isInCamPath;

	/**
	 * Same as {@code this(scn, null, new Vec(), scn.is3D() ? new Quat() : new Rot(), 1)}.
	 * 
	 * @see #InteractiveFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(AbstractScene scn) {
		this(scn, null, new Vec(), scn.is3D() ? new Quat() : new Rot(), 1);
	}

	/**
	 * Same as {@code this(scn, null, p, scn.is3D() ? new Quat() : new Rot(), 1)}.
	 * 
	 * @see #InteractiveFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(AbstractScene scn, Vec p) {
		this(scn, null, p, scn.is3D() ? new Quat() : new Rot(), 1);
	}

	/**
	 * Same as {@code this(scn, null, new Vec(), r, 1)}.
	 * 
	 * @see #InteractiveFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(AbstractScene scn, Rotation r) {
		this(scn, null, new Vec(), r, 1);
	}

	/**
	 * Same as {@code this(scn, null, new Vec(), scn.is3D() ? new Quat() : new Rot(), s)}.
	 * 
	 * @see #InteractiveFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(AbstractScene scn, float s) {
		this(scn, null, new Vec(), scn.is3D() ? new Quat() : new Rot(), s);
	}

	/**
	 * Same as {@code this(scn, null, p, scn.is3D() ? new Quat() : new Rot(), s)}.
	 * 
	 * @see #InteractiveFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(AbstractScene scn, Vec p, float s) {
		this(scn, null, p, scn.is3D() ? new Quat() : new Rot(), s);
	}

	/**
	 * Same as {@code this(scn, null, p, r, 1)}.
	 * 
	 * @see #InteractiveFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(AbstractScene scn, Vec p, Rotation r) {
		this(scn, null, p, r, 1);
	}

	/**
	 * Same as {@code this(scn, null, new Vec(), r, s)}.
	 * 
	 * @see #InteractiveFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(AbstractScene scn, Rotation r, float s) {
		this(scn, null, new Vec(), r, s);
	}

	/**
	 * Same as {@code this(scn, null, p, r, s)}.
	 * 
	 * @see #InteractiveFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(AbstractScene scn, Vec p, Rotation r, float s) {
		this(scn, null, p, r, s);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, new Vec(), scn.is3D() ? new Quat() : new Rot(), 1)}.
	 * 
	 * @see #InteractiveFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(AbstractScene scn, Frame referenceFrame) {
		this(scn, referenceFrame, new Vec(), scn.is3D() ? new Quat() : new Rot(), 1);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, p, scn.is3D() ? new Quat() : new Rot(), 1)}.
	 * 
	 * @see #InteractiveFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(AbstractScene scn, Frame referenceFrame, Vec p) {
		this(scn, referenceFrame, p, scn.is3D() ? new Quat() : new Rot(), 1);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, new Vec(), r, 1)}.
	 * 
	 * @see #InteractiveFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(AbstractScene scn, Frame referenceFrame, Rotation r) {
		this(scn, referenceFrame, new Vec(), r, 1);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, new Vec(), scn.is3D() ? new Quat() : new Rot(), s)}.
	 * 
	 * @see #InteractiveFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(AbstractScene scn, Frame referenceFrame, float s) {
		this(scn, referenceFrame, new Vec(), scn.is3D() ? new Quat() : new Rot(), s);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, p, scn.is3D() ? new Quat() : new Rot(), s)}.
	 * 
	 * @see #InteractiveFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(AbstractScene scn, Frame referenceFrame, Vec p, float s) {
		this(scn, referenceFrame, p, scn.is3D() ? new Quat() : new Rot(), s);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, p, r, 1)}.
	 * 
	 * @see #InteractiveFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(AbstractScene scn, Frame referenceFrame, Vec p, Rotation r) {
		this(scn, referenceFrame, p, r, 1);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, new Vec(), r, s)}.
	 * 
	 * @see #InteractiveFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public InteractiveFrame(AbstractScene scn, Frame referenceFrame, Rotation r, float s) {
		this(scn, referenceFrame, new Vec(), r, s);
	}

	/**
	 * The {@link #translation()} is set to 0, with an identity {@link #rotation()} and no {@link #scaling()} (see Frame
	 * constructor for details). The different sensitivities are set to their default values (see
	 * {@link #rotationSensitivity()} , {@link #translationSensitivity()}, {@link #spinningSensitivity()} and
	 * {@link #wheelSensitivity()}). {@link #dampingFriction()} is set to 0.5.
	 * <p>
	 * <b>Note:</b> the InteractiveFrame is automatically added to the {@link remixlab.bias.core.InputHandler#agents()}
	 * pool.
	 */
	public InteractiveFrame(AbstractScene scn, Frame referenceFrame, Vec p, Rotation r, float s) {
		super(scn, referenceFrame, p, r, s);
		scene.motionAgent().addGrabber(this);
		isInCamPath = false;
		setGrabsInputThreshold(20);
		// TODO future versions should go (except for iFrames in eyePath?):
		// setGrabsInputThreshold(Math.round(scene.radius()/10f), true);

		// scnUpVec = new Vec(0.0f, 1.0f, 0.0f);
		// flyDisp = new Vec(0.0f, 0.0f, 0.0f);

		// if (!(this instanceof InteractiveEyeFrame))
		setFlySpeed(0.01f * scene.radius());
	}

	protected InteractiveFrame(InteractiveFrame otherFrame) {
		super(otherFrame);
		if (scene.motionAgent().hasGrabber(otherFrame))
			scene.motionAgent().addGrabber(this);

		this.setAction(otherFrame.action());

		// this.scnUpVec.set(otherFrame.sceneUpVector().get());
		// this.flyDisp.set(otherFrame.flyDisp.get());
		// this.setFlySpeed(otherFrame.flySpeed());
	}

	@Override
	public InteractiveFrame get() {
		return new InteractiveFrame(this);
	}

	/**
	 * Ad-hoc constructor needed to make editable an Eye path defined by a KeyFrameInterpolator.
	 * <p>
	 * Constructs a Frame from the the {@code iFrame} {@link #translation()}, {@link #rotation()} and {@link #scaling()}
	 * and immediately adds it to the scene {@link remixlab.bias.core.InputHandler#agents()} pool.
	 * <p>
	 * A call on {@link #isInEyePath()} on this Frame will return {@code true}.
	 * 
	 * <b>Attention:</b> Internal use. You should not call this constructor in your own applications.
	 * 
	 * @see remixlab.dandelion.core.Eye#addKeyFrameToPath(int)
	 */
	protected InteractiveFrame(AbstractScene scn, SceneFrame iFrame) {
		super(scn, iFrame.translation().get(), iFrame.rotation().get(), iFrame.scaling());

		isInCamPath = true;
		setGrabsInputThreshold(20);
		// TODO future versions should go (except for iFrames in eyePath?):
		// setGrabsInputThreshold(Math.round(scene.radius()/10f), true);

		// scnUpVec = new Vec(0.0f, 1.0f, 0.0f);
	}

	// grabber implementation

	// TODO testing instantiation
	// see here http://stackoverflow.com/questions/23056324/why-does-java-allow-null-value-to-be-assigned-to-an-enum
	protected Action<MotionAction>	action	= DOF1Action.CUSTOM;

	public MotionAction referenceAction() {
		return action != null ? action.referenceAction() : null;
	}

	@Override
	public void setAction(Action<MotionAction> a) {
		action = a;
	}

	@Override
	public Action<MotionAction> action() {
		return action;
	}

	/**
	 * Check if this object is the {@link remixlab.bias.core.Agent#inputGrabber()}. Returns {@code true} if this object
	 * grabs the agent and {@code false} otherwise.
	 */
	public boolean grabsInput(Agent agent) {
		return agent.inputGrabber() == this;
	}

	@Override
	public boolean checkIfGrabsInput(BogusEvent event) {
		if (event instanceof ClickEvent)
			return checkIfGrabsInput((ClickEvent) event);
		if (event instanceof DOF1Event)
			return checkIfGrabsInput((DOF1Event) event);
		if (event instanceof DOF2Event)
			return checkIfGrabsInput((DOF2Event) event);
		if (event instanceof DOF3Event)
			return checkIfGrabsInput((DOF3Event) event);
		if (event instanceof DOF6Event)
			return checkIfGrabsInput((DOF6Event) event);
		return false;
	}

	public boolean checkIfGrabsInput(ClickEvent event) {
		return checkIfGrabsInput(new DOF2Event(event.x(), event.y()));
	}

	public boolean checkIfGrabsInput(DOF2Event event) {
		Vec proj = scene.eye().projectedCoordinatesOf(position());
		float halfThreshold = grabsInputThreshold() / 2;
		return ((Math.abs(event.x() - proj.vec[0]) < halfThreshold) && (Math.abs(event.y() - proj.vec[1]) < halfThreshold));
	}

	public boolean checkIfGrabsInput(DOF3Event event) {
		return checkIfGrabsInput(event.dof2Event());
	}

	public boolean checkIfGrabsInput(DOF6Event event) {
		return checkIfGrabsInput(event.dof3Event().dof2Event());
	}

	@Override
	public void performInteraction(BogusEvent event) {
		if (event instanceof ClickEvent)
			performInteraction((ClickEvent) event);
		if (event instanceof DOF1Event)
			performInteraction((DOF1Event) event);
		if (event instanceof DOF2Event)
			performInteraction((DOF2Event) event);
		if (event instanceof DOF3Event)
			performInteraction((DOF3Event) event);
		if (event instanceof DOF6Event)
			performInteraction((DOF6Event) event);
	}

	public void performInteraction(ClickEvent event) {
		switch (referenceAction()) {
		case CENTER_FRAME:
			projectOnLine(scene.eye().position(), scene.eye().viewDirection());
			break;
		case ALIGN_FRAME:
			alignWithFrame(scene.eye().frame());
			break;
		case CUSTOM:
			performCustomAction(event);
			break;
		case ANCHOR_FROM_PIXEL:
		case ZOOM_ON_PIXEL:
			AbstractScene.showOnlyEyeWarning(referenceAction());
			break;
		default:
			break;
		}
	}

	public void performInteraction(DOF1Event event) {
		if (scene.is2D())
			execAction2D(event, true);
		else
			execAction3D(event, true);
	}

	public void performInteraction(DOF2Event event) {
		if (scene.is2D())
			execAction2D(event);
		else
			execAction3D(event);
	}

	public void performInteraction(DOF3Event event) {
		if (scene.is2D())
			execAction2D(event);
		else
			execAction3D(event);
	}

	public void performInteraction(DOF6Event event) {
		if (scene.is2D())
			execAction2D(event);
		else
			execAction3D(event);
	}

	// 2D

	/*
	 * protected void execAction2D(ClickEvent event) {
	 * 
	 * }
	 */

	protected void execAction2D(DOF1Event event) {
		execAction2D(event, false);
	}

	protected void execAction2D(DOF1Event event, boolean wheel) {
		switch (referenceAction()) {
		case CUSTOM:
			performCustomAction(event);
			break;
		case ROTATE_Z:
			Rot rt = new Rot(scene.isRightHanded() ? computeAngle(event, wheel) : -computeAngle(event, wheel));
			rotate(rt);
			setSpinningRotation(rt);
			break;
		case SCALE:
			float delta = delta1(event, wheel);
			float s = 1 + Math.abs(delta) / (float) scene.height();
			scale(delta >= 0 ? s : 1 / s);
			break;
		case TRANSLATE_X:
			translateFromEye(new Vec(delta1(event, wheel), 0), wheel ? 1 : translationSensitivity());
			break;
		case TRANSLATE_Y:
			translateFromEye(new Vec(0, scene.isRightHanded() ? -delta1(event, wheel) : delta1(event, wheel)), wheel ? 1
					: translationSensitivity());
			break;
		case ROTATE_X:
		case ROTATE_Y:
		case TRANSLATE_Z:
			AbstractScene.showDepthWarning(referenceAction());
			break;
		case ZOOM:
		case ZOOM_ON_ANCHOR:
			AbstractScene.showOnlyEyeWarning(referenceAction());
			break;
		default:
			break;
		}
	}

	protected void execAction2D(DOF2Event event) {
		Vec trns;
		float deltaX, deltaY;
		switch (referenceAction()) {
		case CUSTOM:
			performCustomAction(event);
			break;
		case MOVE_BACKWARD:
			rotate(computeRot(event, scene.window().projectedCoordinatesOf(position())));
			flyDisp.set(-flySpeed(), 0.0f, 0.0f);
			trns = localInverseTransformOf(flyDisp);
			translate(trns);
			setTossingDirection(trns);
			startTossing(event);
			break;
		case MOVE_FORWARD:
			rotate(computeRot(event, scene.window().projectedCoordinatesOf(position())));
			flyDisp.set(flySpeed(), 0.0f, 0.0f);
			trns = localInverseTransformOf(flyDisp);
			setTossingDirection(trns);
			startTossing(event);
			break;
		case ROTATE:
		case SCREEN_ROTATE:
			Rotation rt = computeRot(event, scene.window().projectedCoordinatesOf(position()));
			if (event.isRelative()) {
				setSpinningRotation(rt);
				if (Util.nonZero(dampingFriction()))
					startSpinning(event);
				else
					spin();
			} else
				// absolute needs testing
				rotate(rt);
			break;
		case SCREEN_TRANSLATE:
			deltaX = (event.isRelative()) ? event.dx() : event.x();
			if (event.isRelative())
				deltaY = scene.isRightHanded() ? event.dy() : -event.dy();
			else
				deltaY = scene.isRightHanded() ? event.y() : -event.y();
			int dir = originalDirection(event);
			if (dir == 1)
				translateFromEye(new Vec(deltaX, 0.0f, 0.0f));
			else if (dir == -1)
				translateFromEye(new Vec(0.0f, -deltaY, 0.0f));
			break;
		case TRANSLATE:
			deltaX = (event.isRelative()) ? event.dx() : event.x();
			if (event.isRelative())
				deltaY = scene.isRightHanded() ? event.dy() : -event.dy();
			else
				deltaY = scene.isRightHanded() ? event.y() : -event.y();
			translateFromEye(new Vec(deltaX, -deltaY, 0.0f));
			break;
		case ROTATE_Z:
		case TRANSLATE_X:
			execAction2D(event.dof1Event(true));
			break;
		case SCALE:
		case TRANSLATE_Y:
			execAction2D(event.dof1Event(false));
			break;
		case ZOOM:
		case ZOOM_ON_ANCHOR:
		case ROTATE_CAD:
		case ZOOM_ON_REGION:
			AbstractScene.showOnlyEyeWarning(referenceAction());
			break;
		// ROTATE_X: ROTATE_Y: TRANSLATE_Z: DRIVE: LOOK_AROUND:
		default:
			AbstractScene.showDepthWarning(referenceAction());
			break;
		}
	}

	protected void execAction2D(DOF3Event event) {
		if (referenceAction() == MotionAction.CUSTOM)
			performCustomAction(event);
		else
			execAction2D(event.dof2Event());
	}

	protected void execAction2D(DOF6Event event) {
		if (referenceAction() == MotionAction.CUSTOM)
			performCustomAction(event);
		else
			execAction2D(event.dof3Event());
	}

	// 3D

	/*
	 * protected void execAction3D(ClickEvent event) { }
	 */

	protected void execAction3D(DOF1Event event) {
		execAction3D(event, false);
	}

	protected void execAction3D(DOF1Event event, boolean wheel) {
		Vec trns;
		float delta;
		switch (referenceAction()) {
		case CUSTOM:
			performCustomAction(event);
			break;
		case ROTATE_X:
			if (scene.is3D())
				rotateAroundEyeAxes(computeAngle(event, wheel), 0, 0);
			break;
		case ROTATE_Y:
			if (scene.is3D())
				rotateAroundEyeAxes(0, -computeAngle(event, wheel), 0);
			break;
		case ROTATE_Z:
			if (scene.is3D())
				rotateAroundEyeAxes(0, 0, -computeAngle(event, wheel));
			break;
		case SCALE:
			delta = delta1(event, wheel);
			float s = 1 + Math.abs(delta) / (float) scene.height();
			scale(delta >= 0 ? s : 1 / s);
			break;
		case TRANSLATE_X:
			trns = new Vec(delta1(event, wheel), 0.0f, 0.0f);
			scale2Fit(trns);
			translateFromEye(trns, wheel ? 1 : translationSensitivity());
			break;
		case TRANSLATE_Y:
			trns = new Vec(0.0f, scene.isRightHanded() ? -delta1(event, wheel) : delta1(event, wheel), 0.0f);
			scale2Fit(trns);
			translateFromEye(trns, wheel ? 1 : translationSensitivity());
			break;
		case TRANSLATE_Z:
			trns = new Vec(0.0f, 0.0f, delta1(event, wheel));
			scale2Fit(trns);
			translateFromEye(trns, wheel ? 1 : translationSensitivity());
			break;
		case ZOOM:
			if (wheel) {
				delta = event.x() * wheelSensitivity();
				translateFromEye(new Vec(0.0f, 0.0f, Vec.subtract(scene.camera().position(), position()).magnitude() * delta
						/ scene.camera().screenHeight()), 1);
			}
			else {
				delta = event.isAbsolute() ? event.x() : event.dx();
				translateFromEye(new Vec(0.0f, 0.0f, Vec.subtract(scene.camera().position(), position()).magnitude() * delta
						/ scene.camera().screenHeight()));
			}
		case ZOOM_ON_ANCHOR:
			AbstractScene.showOnlyEyeWarning(referenceAction());
			break;
		default:
			break;
		}
	}

	protected void execAction3D(DOF2Event event) {
		Quat rt;
		Vec trns;
		float angle;
		switch (referenceAction()) {
		case CUSTOM:
			performCustomAction(event);
			break;
		case DRIVE:
			rotate(turnQuaternion(event.dof1Event(), scene.camera()));
			flyDisp.set(0.0f, 0.0f, flySpeed());
			trns = rotation().rotate(flyDisp);
			setTossingDirection(trns);
			startTossing(event);
			break;
		case LOOK_AROUND:
			rotate(rollPitchQuaternion(event, scene.camera()));
			break;
		case MOVE_BACKWARD:
			rotate(rollPitchQuaternion(event, scene.camera()));
			flyDisp.set(0.0f, 0.0f, flySpeed());
			trns = rotation().rotate(flyDisp);
			setTossingDirection(trns);
			startTossing(event);
			break;
		case MOVE_FORWARD:
			rotate(rollPitchQuaternion(event, scene.camera()));
			flyDisp.set(0.0f, 0.0f, -flySpeed());
			trns = rotation().rotate(flyDisp);
			setTossingDirection(trns);
			startTossing(event);
			break;
		case ROTATE:
			if (event.isAbsolute()) {
				// TODO restore
				// AbstractScene.showEventVariationWarning(a);
				break;
			}
			trns = scene.camera().projectedCoordinatesOf(position());
			rt = deformedBallQuaternion(event, trns.x(), trns.y(), scene.camera());
			trns = rt.axis();
			trns = scene.camera().frame().orientation().rotate(trns);
			trns = transformOf(trns);
			rt = new Quat(trns, -rt.angle());
			setSpinningRotation(rt);
			if (Util.nonZero(dampingFriction()))
				startSpinning(event);
			else
				spin();
			break;
		case SCREEN_ROTATE:
			if (event.isAbsolute()) {
				// TODO
				// AbstractScene.showEventVariationWarning(a);
				break;
			}
			trns = scene.camera().projectedCoordinatesOf(position());
			float prev_angle = (float) Math.atan2(event.prevY() - trns.vec[1], event.prevX() - trns.vec[0]);
			angle = (float) Math.atan2(event.y() - trns.vec[1], event.x() - trns.vec[0]);
			Vec axis = transformOf(scene.camera().frame().orientation().rotate(new Vec(0.0f, 0.0f, -1.0f)));
			if (scene.isRightHanded())
				rt = new Quat(axis, angle - prev_angle);
			else
				rt = new Quat(axis, prev_angle - angle);
			setSpinningRotation(rt);
			if (Util.nonZero(dampingFriction()))
				startSpinning(event);
			else
				spin();
			break;
		case SCREEN_TRANSLATE:
			int dir = originalDirection(event);
			trns = new Vec();
			if (dir == 1)
				if (event.isAbsolute())
					trns.set(event.x(), 0.0f, 0.0f);
				else
					trns.set(event.dx(), 0.0f, 0.0f);
			else if (dir == -1)
				if (event.isAbsolute())
					trns.set(0.0f, scene.isRightHanded() ? -event.y() : event.y(), 0.0f);
				else
					trns.set(0.0f, scene.isRightHanded() ? -event.dy() : event.dy(), 0.0f);
			scale2Fit(trns);
			translateFromEye(trns);
			break;
		case TRANSLATE:
			if (event.isRelative())
				trns = new Vec(event.dx(), scene.isRightHanded() ? -event.dy() : event.dy(), 0.0f);
			else
				trns = new Vec(event.x(), scene.isRightHanded() ? -event.y() : event.y(), 0.0f);
			scale2Fit(trns);
			translateFromEye(trns);
			break;
		case ROTATE_Y:
		case ROTATE_Z:
		case TRANSLATE_X:
			execAction3D(event.dof1Event(true), false);
			break;
		// ROTATE_CAD: ROTATE_X: SCALE: TRANSLATE_Y: TRANSLATE_Z: ZOOM: ZOOM_ON_ANCHOR: ZOOM_ON_REGION:
		default:
			execAction3D(event.dof1Event(false), false);
			break;
		}
	}

	protected void execAction3D(DOF3Event event) {
		switch (referenceAction()) {
		case CUSTOM:
			performCustomAction(event);
			break;
		case ROTATE_XYZ:
			if (event.isAbsolute())
				rotateAroundEyeAxes(event.x(), -event.y(), -event.z());
			else
				rotateAroundEyeAxes(event.dx(), -event.dy(), -event.dz());
			break;
		case TRANSLATE_XYZ:
			Vec trns;
			if (event.isRelative())
				trns = new Vec(event.dx(), scene.isRightHanded() ? -event.dy() : event.dy(), event.dz());
			else
				trns = new Vec(event.x(), scene.isRightHanded() ? -event.y() : event.y(), event.z());
			scale2Fit(trns);
			translateFromEye(trns);
			break;
		// ROTATE_CAD: ROTATE_X: SCALE: TRANSLATE_Y: TRANSLATE_Z: ZOOM: ZOOM_ON_ANCHOR: ZOOM_ON_REGION:
		default:
			execAction3D(event.dof2Event());
			break;
		}
	}

	protected void execAction3D(DOF6Event event) {
		switch (referenceAction()) {
		case CUSTOM:
			performCustomAction(event);
			break;
		case TRANSLATE_XYZ_ROTATE_XYZ:
			// A. Translate the iFrame
			Vec trns;
			if (event.isRelative())
				trns = new Vec(event.dx(), scene.isRightHanded() ? -event.dy() : event.dy(), event.dz());
			else
				trns = new Vec(event.x(), scene.isRightHanded() ? -event.y() : event.y(), event.z());
			scale2Fit(trns);
			translateFromEye(trns);
			// B. Rotate the iFrame
			if (event.isAbsolute())
				rotateAroundEyeAxes(event.roll(), -event.pitch(), -event.yaw());
			else
				rotateAroundEyeAxes(event.drx(), -event.dry(), -event.drz());
			break;
		default:
			execAction3D(event.dof3Event());
			break;
		}
	}

	// Custom

	public void performCustomAction(ClickEvent event) {
		AbstractScene.showMissingImplementationWarning("performCustomAction(ClickEvent event)", this.getClass().getName());
	}

	public void performCustomAction(DOF1Event event) {
		AbstractScene.showMissingImplementationWarning("performCustomAction(DOF1Event event)", this.getClass().getName());
	}

	public void performCustomAction(DOF2Event event) {
		AbstractScene.showMissingImplementationWarning("performCustomAction(DOF2Event event)", this.getClass().getName());
	}

	public void performCustomAction(DOF3Event event) {
		AbstractScene.showMissingImplementationWarning("performCustomAction(DOF3Event event)", this.getClass().getName());
	}

	public void performCustomAction(DOF6Event event) {
		AbstractScene.showMissingImplementationWarning("performCustomAction(DOF6Event event)", this.getClass().getName());
	}

	/**
	 * Returns {@code true} when the InteractiveFrame is being manipulated with an agent.
	 */
	public boolean isInInteraction() {
		return action != null;
	}

	// --

	/**
	 * Returns {@code true} if the InteractiveFrame forms part of an Eye path and {@code false} otherwise.
	 * 
	 */
	public boolean isInEyePath() {
		return isInCamPath;
	}

	/**
	 * Returns the grabs input threshold which is used by the interactive frame to {@link #checkIfGrabsInput(BogusEvent)}.
	 * 
	 * @see #setGrabsInputThreshold(float)
	 */
	public float grabsInputThreshold() {
		if (adaptiveGrabsInputThreshold())
			return grabsInputThreshold * scaling() * scene.eye().pixelToSceneRatio(position());
		return grabsInputThreshold;
	}

	/**
	 * Returns {@code true} if the {@link #checkIfGrabsInput(BogusEvent)} test is adaptive and {@code false} otherwise.
	 * 
	 * @see #setGrabsInputThreshold(float, boolean)
	 */
	public boolean adaptiveGrabsInputThreshold() {
		return adpThreshold;
	}

	/**
	 * Convenience function that simply calls {@code setGrabsInputThreshold(threshold, false)}.
	 * 
	 * @see #setGrabsInputThreshold(float, boolean)
	 */
	public void setGrabsInputThreshold(float threshold) {
		setGrabsInputThreshold(threshold, false);
	}

	/**
	 * Sets the length of the hint that defined the {@link #checkIfGrabsInput(BogusEvent)} condition used for frame
	 * picking.
	 * <p>
	 * If {@code adaptive} is {@code false}, the {@code threshold} is expressed in pixels and directly defines the fixed
	 * length of the {@link remixlab.dandelion.core.AbstractScene#drawShooterTarget(Vec, float)}, centered at the
	 * projection of the frame origin onto the screen.
	 * <p>
	 * If {@code adaptive} is {@code true}, the {@code threshold} is expressed in object space (world units) and defines
	 * the edge length of a squared bounding box that leads to an adaptive length of the
	 * {@link remixlab.dandelion.core.AbstractScene#drawShooterTarget(Vec, float)}, centered at the projection of the
	 * frame origin onto the screen. Use this version only if you have a good idea of the bounding box size of the object
	 * you are attaching to the frame.
	 * <p>
	 * Default behavior is to set the {@link #grabsInputThreshold()} to 20 pixels length (in a non-adaptive manner).
	 * <p>
	 * Negative {@code threshold} values are silently ignored.
	 * 
	 * @see #grabsInputThreshold()
	 * @see #checkIfGrabsInput(BogusEvent)
	 */
	public void setGrabsInputThreshold(float threshold, boolean adaptive) {
		if (threshold >= 0) {
			adpThreshold = adaptive;
			grabsInputThreshold = threshold;
		}
	}
}