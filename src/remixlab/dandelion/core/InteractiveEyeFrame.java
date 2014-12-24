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
import remixlab.bias.grabber.*;
import remixlab.dandelion.geom.*;
import remixlab.fpstiming.TimingTask;
import remixlab.util.*;

/**
 * The InteractiveEyeFrame class represents an InteractiveFrame with Eye specific gesture bindings.
 * <p>
 * An InteractiveEyeFrame is a specialization of an InteractiveFrame that is designed to be set as the
 * {@link Eye#frame()}. Some user gestures (those reduced as DOF2Events) are interpreted in a negated way (respect to
 * those defined for the InteractiveFrame). For instance, with a move-to-the-right user gesture the InteractiveEyeFrame
 * has to go to the <i>left</i>, so that the <i>scene</i> seems to move to the right.
 * <p>
 * Depending on the Dandelion action an InteractiveEyeFrame rotates either around its {@link #anchor()} (e.g., ROTATE,
 * HINGE) which is a wrapper to {@link Eye#anchor()}), or its {@link #sceneUpVector()} (e.g., ROTATE_CAD). In the latter
 * case the {@link #sceneUpVector()} defines a 'vertical' direction around which the camera rotates. The camera can
 * rotate left or right, around this axis. It can also be moved up or down to show the 'top' and 'bottom' views of the
 * scene. As a result, the {@link #sceneUpVector()} will always appear vertical in the scene, and the horizon is
 * preserved and stays projected along the camera's horizontal axis. Use
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
public class InteractiveEyeFrame extends Frame implements ActionGrabber<EyeAction>, Copyable, Constants {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				appendSuper(super.hashCode()).
				append(anchorPnt).
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

		InteractiveEyeFrame other = (InteractiveEyeFrame) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(anchorPnt, other.anchorPnt)
				.isEquals();
	}

	protected Eye					eye;
	protected Vec					anchorPnt;

	// L O C A L T I M E R
	public boolean				anchorFlag;
	public boolean				pupFlag;
	public Vec						pupVec;
	protected TimingTask	timerFx;

	// Inverse the direction of an horizontal mouse motion. Depends on the projected
	// screen orientation of the vertical axis when the mouse button is pressed.
	public boolean				cadRotationIsReversed;

	/**
	 * Default constructor.
	 * <p>
	 * {@link #flySpeed()} is set to 0.0 and {@link #sceneUpVector()} is set to the Y-axis. The {@link #anchor()} is set
	 * to 0.
	 * <p>
	 * <b>Attention:</b> Created object is removed from the {@link remixlab.dandelion.core.AbstractScene#inputHandler()}
	 * {@link remixlab.bias.core.InputHandler#agents()} pool.
	 */
	public InteractiveEyeFrame(Eye theEye) {
		super(theEye.scene);
		eye = theEye;
		scene.inputHandler().removeFromAllAgentPools(this);
		anchorPnt = new Vec(0.0f, 0.0f, 0.0f);

		timerFx = new TimingTask() {
			public void execute() {
				unSetTimerFlag();
			}
		};
		scene.registerTimingTask(timerFx);
	}

	protected InteractiveEyeFrame(InteractiveEyeFrame otherFrame) {
		super(otherFrame);
		this.eye = otherFrame.eye;
		this.anchorPnt = new Vec();
		this.anchorPnt.set(otherFrame.anchorPnt);
		this.scene.inputHandler().removeFromAllAgentPools(this);
		this.timerFx = new TimingTask() {
			public void execute() {
				unSetTimerFlag();
			}
		};
		this.scene.registerTimingTask(timerFx);
	}

	@Override
	public InteractiveEyeFrame get() {
		return new InteractiveEyeFrame(this);
	}

	public Eye eye() {
		return eye;
	}
	
  //grabber implementation
	
	protected EyeAction globalAction;
	
	@Override
	public EyeAction referenceAction() {
		return globalAction;
	}
	
	@Override
	public void setReferenceAction(Action<EyeAction> a) {
		globalAction = a.referenceAction();
	}
	
	@Override
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
	
	//TODO improve?
	public boolean checkIfGrabsInput(ClickEvent event) {
		return true;
	}

	public boolean checkIfGrabsInput(DOF2Event event) {
		return true;
	}

	public boolean checkIfGrabsInput(DOF3Event event) {
		return true;
	}

	public boolean checkIfGrabsInput(DOF6Event event) {
		return true;
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

	public void performInteraction(DOF1Event event) {
		if(scene.is2D())
			execAction2D(event);
		else
			execAction3D(event);
	}

	public void performInteraction(DOF2Event event) {
		if(scene.is2D())
			execAction2D(event);
		else
			execAction3D(event);
	}

	public void performInteraction(DOF3Event event) {
		if(scene.is2D())
			execAction2D(event);
		else
			execAction3D(event);
	}

	public void performInteraction(DOF6Event event) {
		if(scene.is2D())
			execAction2D(event);
		else
			execAction3D(event);
	}
	
	// 2D
	
	protected void execAction2D(ClickEvent event) {
		
	}

	protected void execAction2D(DOF1Event event) {
		
	}

	protected void execAction2D(DOF2Event event) {
		
	}

	protected void execAction2D(DOF3Event event) {
		
	}

	protected void execAction2D(DOF6Event event) {
		
	}
	
	// 3D
	
  protected void execAction3D(ClickEvent event) {
		
	}

	protected void execAction3D(DOF1Event event) {
		
	}

	protected void execAction3D(DOF2Event event) {
		
	}

	protected void execAction3D(DOF3Event event) {
		
	}

	protected void execAction3D(DOF6Event event) {
		
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

	// 2. Local timer

	/**
	 * Internal use. Called from the timer to stop displaying the point under pixel and anchor visual hints.
	 */
	protected void unSetTimerFlag() {
		anchorFlag = false;
		pupFlag = false;
	}

	/**
	 * Overloading of {@link remixlab.dandelion.core.InteractiveFrame#spin()}.
	 * <p>
	 * Rotates the InteractiveEyeFrame around its {@link #anchor()} instead of its origin.
	 */
	@Override
	public void spin() {
		if (dampFriction > 0) {
			if (eventSpeed == 0) {
				stopSpinning();
				return;
			}
			rotateAroundPoint(spinningRotation(), anchor());
			recomputeSpinningRotation();
		}
		else
			rotateAroundPoint(spinningRotation(), anchor());
	}

	/**
	 * Returns the point the InteractiveEyeFrame revolves around when rotated.
	 * <p>
	 * It is defined in the world coordinate system. Default value is 0.
	 * <p>
	 * When the InteractiveEyeFrame is associated to an Eye, {@link remixlab.dandelion.core.Eye#anchor()} also returns
	 * this value.
	 */
	public Vec anchor() {
		return anchorPnt;
	}

	/**
	 * Sets the {@link #anchor()}, defined in the world coordinate system.
	 */
	public void setAnchor(Vec refP) {
		anchorPnt = refP;
		if (scene.is2D())
			anchorPnt.setZ(0);
	}

	// This methods gives the same results as the super method. It's only provided to simplify computation
	@Override
	public void rotateAroundEyeAxes(float roll, float pitch, float yaw) {
		if (scene.is2D()) {
			AbstractScene.showDepthWarning("rollPitchYaw");
			return;
		}
		rotate(new Quat(scene.isLeftHanded() ? -roll : roll, pitch, scene.isLeftHanded() ? -yaw : yaw));
	}

	@Override
	protected void scale2Fit(Vec trans) {
		// Scale to fit the screen mouse displacement
		switch (scene.camera().type()) {
		case PERSPECTIVE:
			trans.multiply(2.0f * (float) Math.tan(scene.camera().fieldOfView() / 2.0f)
					* Math.abs(coordinatesOf(anchor()).vec[2] * magnitude())
					/ scene.camera().screenHeight());
			break;
		case ORTHOGRAPHIC:
			float[] wh = scene.camera().getBoundaryWidthHeight();
			trans.vec[0] *= 2.0f * wh[0] / scene.camera().screenWidth();
			trans.vec[1] *= 2.0f * wh[1] / scene.camera().screenHeight();
			break;
		}
	}

	@Override
	protected Rot computeRot(DOF2Event e2, Vec trans) {
		Rot rot;
		if (e2.isRelative()) {
			Point prevPos = new Point(e2.prevX(), e2.prevY());
			Point curPos = new Point(e2.x(), e2.y());
			rot = new Rot(new Point(trans.x(), trans.y()), prevPos, curPos);
			rot = new Rot(rot.angle() * rotationSensitivity());
		}
		else
			rot = new Rot(e2.x() * rotationSensitivity());
		if (scene.isLeftHanded())
			rot.negate();
		return rot;
	}
}
