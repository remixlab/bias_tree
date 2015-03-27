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
 * The InteractiveEyeFrame class represents an InteractiveFrame with Eye specific gesture bindings.
 * <p>
 * An InteractiveEyeFrame is a specialization of an InteractiveFrame that is designed to be set as the
 * {@link Eye#frame()}. Some user gestures (those reduced as DOF2Events) are interpreted in a negated way (respect to
 * those defined for the InteractiveFrame). For instance, with a move-to-the-right user gesture the InteractiveEyeFrame
 * has to go to the <i>left</i>, so that the <i>scene</i> seems to move to the right.
 * <p>
 * Depending on the Dandelion action an InteractiveEyeFrame rotates either around the
 * {@link remixlab.dandelion.core.Eye#anchor()} (e.g., ROTATE, HINGE), or its {@link #sceneUpVector()} (e.g.,
 * ROTATE_CAD). In the latter case the {@link #sceneUpVector()} defines a 'vertical' direction around which the camera
 * rotates. The camera can rotate left or right, around this axis. It can also be moved up or down to show the 'top' and
 * 'bottom' views of the scene. As a result, the {@link #sceneUpVector()} will always appear vertical in the scene, and
 * the horizon is preserved and stays projected along the camera's horizontal axis. Use
 * {@link remixlab.dandelion.core.Camera#setUpVector(Vec)} to define the {@link #sceneUpVector()} and align the camera
 * before starting a ROTATE_CAD action to ensure these invariants are preserved.
 * <p>
 * The possible actions that can interactively be performed by the InteractiveEyeFrame are
 * {@link remixlab.dandelion.core.Constants.ClickAction}, {@link remixlab.dandelion.core.Constants.DOF1Action},
 * {@link remixlab.dandelion.core.Constants.DOF2Action}, {@link remixlab.dandelion.core.Constants.DOF3Action} and
 * {@link remixlab.dandelion.core.Constants.DOF6Action}. The {@link remixlab.dandelion.core.AbstractScene#motionAgent()}
 * provides high-level methods to handle some these actions, e.g., a {@link remixlab.dandelion.agent.WheeledMouseAgent}
 * can handle up to {@link remixlab.dandelion.core.Constants.DOF2Action}s.
 * <p>
 * <b>Observation: </b> The InteractiveEyeFrame is not added to the
 * {@link remixlab.dandelion.core.AbstractScene#inputHandler()} {@link remixlab.bias.core.InputHandler#agents()} pool
 * upon creation.
 */
public class InteractiveEyeFrame extends SceneFrame implements ActionGrabber<MotionAction>, Copyable,
		Constants {
	protected Eye	eye;

	/**
	 * Default constructor.
	 * <p>
	 * {@link #flySpeed()} is set to 0.0 and {@link #sceneUpVector()} is set to the Y-axis. The
	 * {@link remixlab.dandelion.core.Eye#anchor()} is set to 0.
	 * <p>
	 * <b>Attention:</b> Created object is removed from the {@link remixlab.dandelion.core.AbstractScene#inputHandler()}
	 * {@link remixlab.bias.core.InputHandler#agents()} pool.
	 */
	public InteractiveEyeFrame(Eye theEye) {
		super(theEye.scene);
		eye = theEye;
	}

	protected InteractiveEyeFrame(InteractiveEyeFrame otherFrame) {
		super(otherFrame);
		this.eye = otherFrame.eye;

		// TODO don't think so: frame is added to the pool when setting the eye
		// if( scene.motionAgent().eyeBranch().isInPool(otherFrame) )
		// scene.motionAgent().eyeBranch().addInPool(this);
	}

	@Override
	public InteractiveEyeFrame get() {
		return new InteractiveEyeFrame(this);
	}

	public Eye eye() {
		return eye;
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
	 * Check if this object is the {@link remixlab.bias.core.Agent#inputGrabber()}. Returns {@code true} if this object
	 * grabs the agent and {@code false} otherwise.
	 */
	public boolean grabsInput(Agent agent) {
		return agent.inputGrabber() == this;
	}

	@Override
	public boolean checkIfGrabsInput(BogusEvent event) {
		return false;
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
		case CUSTOM:
			performCustomAction(event);
			break;
		case ALIGN_FRAME:
			alignWithFrame(null, true);
			break;
		case ANCHOR_FROM_PIXEL:
			if (eye().setAnchorFromPixel(new Point(event.x(), event.y()))) {
				eye().anchorFlag = true;
				eye().timerFx.runOnce(1000);
			}
			break;
		case CENTER_FRAME:
			eye().centerScene();
			break;
		case ZOOM_ON_PIXEL:
			if (scene.is2D()) {
				eye().interpolateToZoomOnPixel(new Point(event.x(), event.y()));
				eye().pupVec = eye().unprojectedCoordinatesOf(new Vec(event.x(), event.y(), 0.5f));
				eye().pupFlag = true;
				eye().timerFx.runOnce(1000);
			}
			else {
				Vec pup = ((Camera) eye()).pointUnderPixel(new Point(event.x(), event.y()));
				if (pup != null) {
					((Camera) eye()).interpolateToZoomOnTarget(pup);
					eye().pupVec = pup;
					eye().pupFlag = true;
					eye().timerFx.runOnce(1000);
				}
			}
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
			//TODO needs testing
			Rot rt = new Rot((wheel ? wheelSensitivity() : rotationSensitivity()) * (scene.isRightHanded() ? computeAngle(event) : -computeAngle(event)));
			rotate(rt);
			setSpinningRotation(rt);
			break;
		case SCALE:
			//TODO needs testing
			float delta = delta1(event) * (wheel ? wheelSensitivity() : translationSensitivity());
			float s = 1 + Math.abs(delta) / (float) -scene.height();
			scale(delta >= 0 ? s : 1 / s);
			break;
		case TRANSLATE_X:
			//TODO needs testing
			translateFromGesture(new Vec(delta1(event), 0), wheel ? wheelSensitivity() : translationSensitivity());
			break;
		case TRANSLATE_Y:
			//TODO needs testing			
			translateFromGesture(new Vec(0, scene.isRightHanded() ? -delta1(event) : delta1(event)), wheel ? wheelSensitivity()
					: translationSensitivity());
			break;
		default:
			// TODO
			// AbstractScene.showOnlyEyeWarning(a);
			break;
		}
	}

	protected void execAction2D(DOF2Event event) {
		float deltaX, deltaY;
		Rotation rt;
		switch (referenceAction()) {
		case CUSTOM:
			performCustomAction(event);
			break;
		case MOVE_BACKWARD:
			rotate(deformedBallRotation(event, eye().projectedCoordinatesOf(position())));
			flyDisp.set(flySpeed(), 0.0f, 0.0f);
			translate(flyDisp);
			setTossingDirection(flyDisp);
			startTossing(event);
			break;
		case MOVE_FORWARD:
			rotate(deformedBallRotation(event, eye().projectedCoordinatesOf(position())));
			flyDisp.set(-flySpeed(), 0.0f, 0.0f);
			translate(flyDisp);
			setTossingDirection(flyDisp);
			startTossing(event);
			break;
		case ROTATE:
		case SCREEN_ROTATE:
			rt = deformedBallRotation(event, eye().projectedCoordinatesOf(eye().anchor()));
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
			break;
		case TRANSLATE:
			deltaX = (event.isRelative()) ? event.dx() : event.x();
			if (event.isRelative())
				deltaY = scene.isRightHanded() ? -event.dy() : event.dy();
			else
				deltaY = scene.isRightHanded() ? -event.y() : event.y();
			translateFromGesture(new Vec(-deltaX, -deltaY, 0.0f));
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
		float wheelSensitivityCoef = 8E-4f;
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
			//TODO testing
			float delta = delta1(event) * (wheel ? wheelSensitivity() : translationSensitivity());
			float s = 1 + Math.abs(delta) / (float) -scene.height();
			scale(delta >= 0 ? s : 1 / s);
			break;
		case TRANSLATE_X:
			trns = new Vec(delta1(event), 0.0f, 0.0f);
		  //TODO experimenting
			//scale2Fit(trns);
			translateFromGesture(trns, wheel ? wheelSensitivity() : translationSensitivity());
			break;
		case TRANSLATE_Y:
			trns = new Vec(0.0f, scene.isRightHanded() ? -delta1(event) : delta1(event), 0.0f);
		  //TODO experimenting
			//scale2Fit(trns);
			translateFromGesture(trns, wheel ? wheelSensitivity() : translationSensitivity());
			break;
		case TRANSLATE_Z:
			trns = new Vec(0.0f, 0.0f, delta1(event));
			//TODO experimenting
			//scale2Fit(trns);
			translateFromGesture(trns, wheel ? wheelSensitivity() : translationSensitivity());
			break;
		case ZOOM:
		  //TODO leave it as TRANSLATE_Z
			///*
			float coef = Math.max(Math.abs((coordinatesOf(eye().anchor())).vec[2] * magnitude()),
					0.2f * eye().sceneRadius());
			if (wheel)
				delta = coef * event.x() * -wheelSensitivity() * wheelSensitivityCoef;
			// TODO should absolute be divided by camera.screenHeight()?
			else if (event.isAbsolute())
				delta = -coef * event.x() / eye().screenHeight();
			else
				delta = -coef * event.dx() / eye().screenHeight();
			trns = new Vec(0.0f, 0.0f, delta);
			translate(orientation().rotate(trns));
			//*/
			/*
			//TODO experimenting
			trns = new Vec(0.0f, 0.0f, delta1(event));
			translateFromEye(trns, wheel ? wheelSensitivity() : translationSensitivity());
			//*/
			break;
		case ZOOM_ON_ANCHOR:
		  /*
			trns = Vec.subtract(eye().anchor(), position());
			trns = eye().eyeCoordinatesOf(trns);
			trns.normalize();
			trns.multiply(delta1(event));
			Vec ineye = trns.get();
			float mag = gesture2Eye(trns);
			
			translateFromEye(trns, wheel ? wheelSensitivity() : translationSensitivity());
			//*/
			///*
			trns = Vec.subtract(eye().anchor(), position());
			trns = eye().eyeCoordinatesOf(trns);
			trns.normalize();
			trns.multiply(delta1(event));
			//translateFromGesture(trns, wheel ? wheelSensitivity() : translationSensitivity());
			translateFromEye(trns, wheel ? wheelSensitivity() : translationSensitivity());
			//*/			
			
			/*
			//TODO experimenting
			scale2Fit(trns);
			translateFromWorld(trns, wheel ? wheelSensitivity() : translationSensitivity());
			//*/

			//TODO only missing case
			/*
			if (wheel)
				delta = event.x() * -wheelSensitivity() * wheelSensitivityCoef;
			// TODO should absolute be divided by camera.screenHeight()?
			else if (event.isAbsolute())
				delta = -event.x() / eye().screenHeight();
			else
				delta = -event.dx() / eye().screenHeight();
			trns = Vec.subtract(position(), scene.camera().anchor());
			if (trns.magnitude() > 0.02f * scene.radius() || delta > 0.0f)
				translate(Vec.multiply(trns, delta));
			//*/
			break;
		default:
			break;
		}
	}

	protected void execAction3D(DOF2Event event) {
		Vec trns = new Vec();
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
			trns = eye().projectedCoordinatesOf(eye().anchor());
			setSpinningRotation(deformedBallRotation(event, trns));
			if (Util.nonZero(dampingFriction()))
				startSpinning(event);
			else
				spin();
			break;
		case ROTATE_CAD:
			if (event.isAbsolute()) {
				// TODO restore
				// AbstractScene.showEventVariationWarning(a);
				break;
			}
			// Multiply by 2.0 to get on average about the same speed as with the deformed ball
			float dx = -2.0f * rotationSensitivity() * event.dx() / scene.camera().screenWidth();
			float dy = 2.0f * rotationSensitivity() * event.dy() / scene.camera().screenHeight();
			if (((Camera) eye()).cadRotationIsReversed)
				dx = -dx;
			if (scene.isRightHanded())
				dy = -dy;
			Vec verticalAxis = transformOf(sceneUpVector());
			setSpinningRotation(Quat.multiply(new Quat(verticalAxis, dx), new Quat(new Vec(1.0f, 0.0f, 0.0f), dy)));
			if (Util.nonZero(dampingFriction()))
				startSpinning(event);
			else
				spin();
			break;
		case SCREEN_ROTATE:
			if (event.isAbsolute()) {
				// TODO restore
				// AbstractScene.showEventVariationWarning(a);
				break;
			}
			trns = eye().projectedCoordinatesOf(eye().anchor());
			float angle = (float) Math.atan2(event.y() - trns.vec[1], event.x() - trns.vec[0])
					- (float) Math.atan2(event.prevY() - trns.vec[1], event.prevX() - trns.vec[0]);
			if (scene.isLeftHanded())
				angle = -angle;
			Rotation rt = new Quat(new Vec(0.0f, 0.0f, 1.0f), angle);
			setSpinningRotation(rt);
			if (Util.nonZero(dampingFriction()))
				startSpinning(event);
			else
				spin();
			updateSceneUpVector();
			break;
		case SCREEN_TRANSLATE:
			int dir = originalDirection(event);
			if (dir == 1)
				if (event.isAbsolute())
					trns.set(-event.x(), 0.0f, 0.0f);
				else
					trns.set(-event.dx(), 0.0f, 0.0f);
			else if (dir == -1)
				if (event.isAbsolute())
					trns.set(0.0f, scene.isRightHanded() ? event.y() : -event.y(), 0.0f);
				else
					trns.set(0.0f, scene.isRightHanded() ? event.dy() : -event.dy(), 0.0f);
			//TODO experimenting
			//scale2Fit(trns);
			//trns = Vec.multiply(trns, translationSensitivity());
			//translate(orientation().rotate(trns));
			translateFromGesture(trns);
			break;
		case TRANSLATE:
			if (event.isRelative())
				trns = new Vec(-event.dx(), scene.isRightHanded() ? event.dy() : -event.dy(), 0.0f);
			else
				trns = new Vec(-event.x(), scene.isRightHanded() ? event.y() : -event.y(), 0.0f);
		  //TODO experimenting
			//scale2Fit(trns);
			translateFromGesture(trns);
			//translate(orientation().rotate(Vec.multiply(trns, translationSensitivity())));
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
			Vec trns;
			if (event.isRelative())
				trns = new Vec(event.dx(), scene.isRightHanded() ? -event.dy() : event.dy(), event.dz());
			else
				trns = new Vec(event.x(), scene.isRightHanded() ? -event.y() : event.y(), event.z());
		  //TODO experimenting
			//scale2Fit(trns);
			translateFromGesture(trns);
			break;
		default:
			execAction3D(event.dof2Event());
			break;
		}
	}

	protected void execAction3D(DOF6Event event) {
		Vec trns = new Vec();
		switch (referenceAction()) {
		case CUSTOM:
			performCustomAction(event);
			break;
		case HINGE: // aka google earth navigation
			// 1. Relate the eye reference frame:
			Vec pos = position();
			Quat o = (Quat) orientation();
			Frame oldRef = referenceFrame();
			SceneFrame rFrame = new SceneFrame(scene);
			rFrame.setPosition(eye().anchor());
			rFrame.setZAxis(Vec.subtract(pos, eye().anchor()));
			rFrame.setXAxis(xAxis());
			setReferenceFrame(rFrame);
			setPosition(pos);
			setOrientation(o);
			// 2. Translate the refFrame along its Z-axis:
			float deltaZ = event.isRelative() ? event.dz() : event.z();
			trns = new Vec(0, scene.isRightHanded() ? -deltaZ : deltaZ, 0);
			screen2Eye(trns);
			float pmag = trns.magnitude();
			translate(0, 0, (deltaZ > 0) ? pmag : -pmag);
			// 3. Rotate the refFrame around its X-axis -> translate forward-backward the frame on the sphere surface
			float deltaY = event.isRelative() ? event.dy() : event.y();
			rFrame.rotate(new Quat(new Vec(1, 0, 0), scene.isRightHanded() ? deltaY : -deltaY));
			// 4. Rotate the refFrame around its Y-axis -> translate left-right the frame on the sphere surface
			float deltaX = event.isRelative() ? event.dx() : event.x();
			rFrame.rotate(new Quat(new Vec(0, 1, 0), deltaX));
			// 5. Rotate the refFrame around its Z-axis -> look around
			float rZ = event.isRelative() ? event.drz() : event.rz();
			rFrame.rotate(new Quat(new Vec(0, 0, 1), scene.isRightHanded() ? -rZ : rZ));
			// 6. Rotate the frame around x-axis -> move head up and down :P
			float rX = event.isRelative() ? event.drx() : event.rx();
			Quat q = new Quat(new Vec(1, 0, 0), scene.isRightHanded() ? rX : -rX);
			rotate(q);
			// 7. Unrelate the frame and restore state:
			pos = position();
			o = (Quat) orientation();
			setReferenceFrame(oldRef);
			setPosition(pos);
			setOrientation(o);
			break;
		case TRANSLATE_XYZ_ROTATE_XYZ:
			// A. Translate the iFrame
			if (event.isRelative())
				trns = new Vec(event.dx(), scene.isRightHanded() ? -event.dy() : event.dy(), event.dz());
			else
				trns = new Vec(event.x(), scene.isRightHanded() ? -event.y() : event.y(), event.z());
		  //TODO experimenting
			//scale2Fit(trns);
			translateFromGesture(trns);
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
	
	/**
	 * This methods gives the same results as the super method. It's only provided to simplify computation.
	 */
	@Override
	public void rotateAroundEyeAxes(float roll, float pitch, float yaw) {
		if (scene.is2D()) {
			AbstractScene.showDepthWarning("rollPitchYaw");
			return;
		}
		rotate(new Quat(scene.isLeftHanded() ? -roll : roll, pitch, scene.isLeftHanded() ? -yaw : yaw));
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
