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

/**
 * The InteractiveFrame class represents an InteractiveFrame with Eye specific gesture bindings.
 * <p>
 * An InteractiveFrame is a specialization of an InteractiveFrame that is designed to be set as the {@link Eye#frame()}.
 * Some user gestures (those reduced as DOF2Events) are interpreted in a negated way (respect to those defined for the
 * InteractiveFrame). For instance, with a move-to-the-right user gesture the InteractiveFrame has to go to the
 * <i>left</i>, so that the <i>scene</i> seems to move to the right.
 * <p>
 * Depending on the Dandelion action an InteractiveFrame rotates either around the
 * {@link remixlab.dandelion.core.Eye#anchor()} (e.g., ROTATE, HINGE), or its {@link #sceneUpVector()} (e.g.,
 * ROTATE_CAD). In the latter case the {@link #sceneUpVector()} defines a 'vertical' direction around which the camera
 * rotates. The camera can rotate left or right, around this axis. It can also be moved up or down to show the 'top' and
 * 'bottom' views of the scene. As a result, the {@link #sceneUpVector()} will always appear vertical in the scene, and
 * the horizon is preserved and stays projected along the camera's horizontal axis. Use
 * {@link remixlab.dandelion.core.Camera#setUpVector(Vec)} to define the {@link #sceneUpVector()} and align the camera
 * before starting a ROTATE_CAD action to ensure these invariants are preserved.
 * <p>
 * The possible actions that can interactively be performed by the InteractiveFrame are
 * {@link remixlab.dandelion.core.Constants.ClickAction}, {@link remixlab.dandelion.core.Constants.DOF1Action},
 * {@link remixlab.dandelion.core.Constants.DOF2Action}, {@link remixlab.dandelion.core.Constants.DOF3Action} and
 * {@link remixlab.dandelion.core.Constants.DOF6Action}. The {@link remixlab.dandelion.core.AbstractScene#motionAgent()}
 * provides high-level methods to handle some these actions, e.g., a {@link remixlab.dandelion.agent.WheeledMouseAgent}
 * can handle up to {@link remixlab.dandelion.core.Constants.DOF2Action}s.
 * <p>
 * <b>Observation: </b> The InteractiveFrame is not added to the
 * {@link remixlab.dandelion.core.AbstractScene#inputHandler()} {@link remixlab.bias.core.InputHandler#agents()} pool
 * upon creation.
 */
public class InteractiveFrame extends GrabberFrame implements InteractiveGrabber<MotionAction>, Copyable,
		Constants {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				appendSuper(super.hashCode()).
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
				.append(action, other.action)
				.isEquals();
	}

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
		// TODO merge with eye?
		super(scn, referenceFrame, p, r, s);
		scene.motionAgent().addGrabber(this);
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
	protected InteractiveFrame(AbstractScene scn, GrabberFrame iFrame) {
		super(scn, iFrame);
	}

	// --

	/**
	 * Default constructor.
	 * <p>
	 * {@link #flySpeed()} is set to 0.0 and {@link #sceneUpVector()} is set to the Y-axis. The
	 * {@link remixlab.dandelion.core.Eye#anchor()} is set to 0.
	 * <p>
	 * <b>Attention:</b> Created object is removed from the {@link remixlab.dandelion.core.AbstractScene#inputHandler()}
	 * {@link remixlab.bias.core.InputHandler#agents()} pool.
	 */
	public InteractiveFrame(Eye theEye) {
		super(theEye);

		// TODO merge with frame? Really depends on the next line
		// scene.motionAgent().addGrabber(this);
	}

	protected InteractiveFrame(InteractiveFrame otherFrame) {
		super(otherFrame);

		// this.scnUpVec.set(otherFrame.sceneUpVector().get());
		// this.flyDisp.set(otherFrame.flyDisp.get());
		// this.setFlySpeed(otherFrame.flySpeed());

		if (!isEyeFrame()) {
			if (scene.motionAgent().hasGrabber(otherFrame))
				scene.motionAgent().addGrabber(this);

			this.setAction(otherFrame.action());
		}
		// TODO don't think so: frame is added to the pool when setting the eye
		// else {
		// if( scene.motionAgent().eyeBranch().isInPool(otherFrame) )
		// scene.motionAgent().eyeBranch().addInPool(this);
		// }
	}

	/*
	 * protected InteractiveFrame(InteractiveFrame otherFrame) { super(otherFrame.theeye);
	 * 
	 * 
	 * }
	 */

	@Override
	public InteractiveFrame get() {
		return new InteractiveFrame(this);
	}

	// grabber implementation

	protected Action<MotionAction>	action;

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
	 * Returns {@code true} when the InteractiveFrame is being manipulated with an agent.
	 */
	public boolean isInInteraction() {
		return action != null;
	}

	/*
	 * @Override public void performInteraction(BogusEvent event) { if (event instanceof ClickEvent)
	 * performInteraction((ClickEvent) event); if (event instanceof MotionEvent) performInteraction((MotionEvent) event);
	 * }
	 */

	// TODO replace with above
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
		case CUSTOM:
			performCustomAction(event);
			break;
		case ALIGN_FRAME:
			align();
			break;
		case ANCHOR_FROM_PIXEL:
			if (!isEyeFrame()) {
				AbstractScene.showOnlyEyeWarning(referenceAction());
				break;
			}
			eye().setAnchorFromPixel(new Point(event.x(), event.y()));
			break;
		case CENTER_FRAME:
			center();
			break;
		case ZOOM_ON_PIXEL:
			if (!isEyeFrame()) {
				AbstractScene.showOnlyEyeWarning(referenceAction());
				break;
			}
			eye().interpolateToZoomOnPixel(new Point(event.x(), event.y()));
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

	protected void execAction2D(DOF1Event event) {
		execAction2D(event, false);
	}

	protected void execAction2D(DOF1Event event, boolean wheel) {
		switch (referenceAction()) {
		case CUSTOM:
			performCustomAction(event);
			break;
		case ROTATE_Z:
			gestureRotateZ(event, wheel ? wheelSensitivity() : translationSensitivity());
			break;
		case SCALE:
			scale(event, wheel ? wheelSensitivity() : translationSensitivity());
			break;
		case TRANSLATE_X:
			gestureTranslateX(event, wheel ? wheelSensitivity() : translationSensitivity());
			break;
		case TRANSLATE_Y:
			gestureTranslateY(event, wheel ? wheelSensitivity() : translationSensitivity());
			break;
		default:
			// TODO
			// AbstractScene.showOnlyEyeWarning(a);
			break;
		}
	}

	protected void execAction2D(DOF2Event event) {
		switch (referenceAction()) {
		case CUSTOM:
			performCustomAction(event);
			break;
		case MOVE_BACKWARD:
			moveBackward(event);
			break;
		case MOVE_FORWARD:
			moveForward(event);
			break;
		case ROTATE:
		case SCREEN_ROTATE:
			arcball(event);
			break;
		case SCREEN_TRANSLATE:
			break;
		case TRANSLATE:
			gestureTranslateXY(event);
			break;
		case ZOOM_ON_REGION:
			if (event.isAbsolute()) {
				// TODO restore
				// AbstractScene.showEventVariationWarning(a);
				break;
			}
			int w = (int) Math.abs(event.dx());
			int tlX = (int) event.prevX() < (int) event.x() ? (int) event.prevX() : (int) event.x();
			int h = (int) Math.abs(event.dy());
			int tlY = (int) event.prevY() < (int) event.y() ? (int) event.prevY() : (int) event.y();
			// viewWindow.fitScreenRegion( new Rectangle (tlX, tlY, w, h) );
			eye().interpolateToZoomOnRegion(new Rect(tlX, tlY, w, h));
			break;
		case ROTATE_Z:
		case TRANSLATE_X:
			execAction2D(event.dof1Event(true));
			break;
		case SCALE:
		case TRANSLATE_Y:
			execAction2D(event.dof1Event(false));
			break;
		default:
			// TODO
			// AbstractScene.showOnlyEyeWarning(a);
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

	protected void execAction3D(DOF1Event event) {
		execAction3D(event, false);
	}

	protected void execAction3D(DOF1Event event, boolean wheel) {
		Vec trns;
		switch (referenceAction()) {
		case CUSTOM:
			performCustomAction(event);
			break;
		case ROTATE_X:
			rotateAroundEyeAxes((wheel ? wheelSensitivity() : rotationSensitivity()) * computeAngle(event), 0, 0);
			break;
		case ROTATE_Y:
			rotateAroundEyeAxes(0, (wheel ? wheelSensitivity() : rotationSensitivity()) * -computeAngle(event), 0);
			break;
		case ROTATE_Z:
			rotateAroundEyeAxes(0, 0, (wheel ? wheelSensitivity() : rotationSensitivity()) * -computeAngle(event));
			break;
		case SCALE:
			scale(event, wheel ? wheelSensitivity() : translationSensitivity());
			break;
		case TRANSLATE_X:
			gestureTranslateX(event, wheel ? wheelSensitivity() : translationSensitivity());
			break;
		case TRANSLATE_Y:
			gestureTranslateY(event, wheel ? wheelSensitivity() : translationSensitivity());
			break;
		case TRANSLATE_Z:
			gestureTranslateZ(event, wheel ? wheelSensitivity() : translationSensitivity());
			break;
		case ZOOM_ON_ANCHOR:
			/*
			 * trns = Vec.subtract(eye().anchor(), position()); trns = eye().eyeCoordinatesOf(trns); trns.normalize();
			 * trns.multiply(delta1(event)); Vec ineye = trns.get(); float mag = gesture2Eye(trns);
			 * 
			 * translateFromEye(trns, wheel ? wheelSensitivity() : translationSensitivity()); //
			 */
			// /*
			trns = Vec.subtract(eye().anchor(), position());
			trns = eye().eyeCoordinatesOf(trns);
			trns.normalize();
			trns.multiply(delta1(event));
			// translateFromGesture(trns, wheel ? wheelSensitivity() : translationSensitivity());
			// TODO: broken once again!
			// eyeTranslate(trns, wheel ? wheelSensitivity() : translationSensitivity());
			// */

			/*
			 * //TODO experimenting scale2Fit(trns); translateFromWorld(trns, wheel ? wheelSensitivity() :
			 * translationSensitivity()); //
			 */

			// TODO only missing case
			/*
			 * if (wheel) delta = event.x() * -wheelSensitivity() * wheelSensitivityCoef; // TODO should absolute be divided
			 * by camera.screenHeight()? else if (event.isAbsolute()) delta = -event.x() / eye().screenHeight(); else delta =
			 * -event.dx() / eye().screenHeight(); trns = Vec.subtract(position(), scene.camera().anchor()); if
			 * (trns.magnitude() > 0.02f * scene.radius() || delta > 0.0f) translate(Vec.multiply(trns, delta)); //
			 */
			break;
		default:
			break;
		}
	}

	protected void execAction3D(DOF2Event event) {
		switch (referenceAction()) {
		case CUSTOM:
			performCustomAction(event);
			break;
		case DRIVE:
			drive(event);
			break;
		case LOOK_AROUND:
			rotate(rollPitchQuaternion(event, scene.camera()));
			break;
		case MOVE_BACKWARD:
			moveBackward(event);
			break;
		case MOVE_FORWARD:
			moveForward(event);
			break;
		case ROTATE:
			arcball(event);
			break;
		case ROTATE_CAD:
			rotateCAD(event);
			break;
		case SCREEN_ROTATE:
			screenRotate(event);
			break;
		case SCREEN_TRANSLATE:
			int dir = originalDirection(event);
			if (dir == 1)
				gestureTranslateX(event.dof1Event(true), translationSensitivity());
			else if (dir == -1)
				gestureTranslateY(event.dof1Event(false), translationSensitivity());
			break;
		case TRANSLATE:
			gestureTranslateXY(event);
			break;
		case ZOOM_ON_REGION:
			if (event.isAbsolute()) {
				// TODO restore
				// AbstractScene.showEventVariationWarning(a);
				break;
			}
			int w = (int) Math.abs(event.dx());
			int tlX = (int) event.prevX() < (int) event.x() ? (int) event.prevX() : (int) event.x();
			int h = (int) Math.abs(event.dy());
			int tlY = (int) event.prevY() < (int) event.y() ? (int) event.prevY() : (int) event.y();
			// camera.fitScreenRegion( new Rectangle (tlX, tlY, w, h) );
			eye().interpolateToZoomOnRegion(new Rect(tlX, tlY, w, h));
			break;
		case ROTATE_Y:
		case ROTATE_Z:
		case TRANSLATE_X:
		case ZOOM_ON_ANCHOR:
			execAction3D(event.dof1Event(true), false);
			break;
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
			gestureTranslateXYZ(event);
			break;
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
		case HINGE:
			hinge(event);
			break;
		case TRANSLATE_XYZ_ROTATE_XYZ:
			// A. Translate the iFrame
			gestureTranslateXYZ(event);
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
}
