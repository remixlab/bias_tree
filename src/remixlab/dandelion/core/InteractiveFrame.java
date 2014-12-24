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
import remixlab.bias.grabber.ActionGrabber;
import remixlab.dandelion.geom.*;
import remixlab.fpstiming.TimingTask;
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
public class InteractiveFrame extends Frame implements ActionGrabber<FrameAction>, Copyable, Constants {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				appendSuper(super.hashCode()).
				append(grabsInputThreshold).
				append(adpThreshold).
				append(isInCamPath).
				append(rotSensitivity).
				append(spngRotation).
				append(spngSensitivity).
				append(dampFriction).
				append(sFriction).
				append(transSensitivity).
				append(wheelSensitivity).
				append(flyDisp).
				append(flySpd).
				append(scnUpVec).
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
				.append(sFriction, other.sFriction)
				.append(rotSensitivity, other.rotSensitivity)
				.append(spngRotation, other.spngRotation)
				.append(spngSensitivity, other.spngSensitivity)
				.append(transSensitivity, other.transSensitivity)
				.append(wheelSensitivity, other.wheelSensitivity)
				.append(flyDisp, other.flyDisp)
				.append(flySpd, other.flySpd)
				.append(scnUpVec, other.scnUpVec)
				.isEquals();
	}

	private float								grabsInputThreshold;
	private boolean							adpThreshold;
	private float								rotSensitivity;
	private float								transSensitivity;
	private float								wheelSensitivity;

	// spinning stuff:
	protected float							eventSpeed;
	private float								spngSensitivity;
	private TimingTask					spinningTimerTask;
	private Rotation						spngRotation;
	protected float							dampFriction;							// new
	// toss and spin share the damp var:
	private float								sFriction;									// new

	// Whether the SCREEN_TRANS direction (horizontal or vertical) is fixed or not.
	public boolean							dirIsFixed;
	private boolean							horiz								= true; // Two simultaneous InteractiveFrame require two mice!

	protected boolean						isInCamPath;

	// " D R I V A B L E " S T U F F :
	protected Vec								tDir;
	protected float							flySpd;
	protected TimingTask				flyTimerTask;
	protected Vec								scnUpVec;
	protected Vec								flyDisp;
	protected static final long	FLY_UPDATE_PERDIOD	= 10;

	/**
	 * Default constructor.
	 * <p>
	 * The {@link #translation()} is set to 0, with an identity {@link #rotation()} and no {@link #scaling()} (see Frame
	 * constructor for details). The different sensitivities are set to their default values (see
	 * {@link #rotationSensitivity()} , {@link #translationSensitivity()}, {@link #spinningSensitivity()} and
	 * {@link #wheelSensitivity()}). {@link #dampingFriction()} is set to 0.5.
	 * <p>
	 * <b>Note:</b> the InteractiveFrame is automatically added to the {@link remixlab.bias.core.InputHandler#agents()}
	 * pool.
	 */
	public InteractiveFrame(AbstractScene scn) {
		super(scn);

		scene.inputHandler().addInAllAgentPools(this);
		isInCamPath = false;

		setGrabsInputThreshold(20);
		// TODO future versions should go (except for iFrames in eyePath?):
		// setGrabsInputThreshold(Math.round(scene.radius()/10f), true);
		setRotationSensitivity(1.0f);
		setTranslationSensitivity(1.0f);
		setWheelSensitivity(20.0f);

		setSpinningSensitivity(0.3f);
		setDampingFriction(0.5f);

		spinningTimerTask = new TimingTask() {
			public void execute() {
				spin();
			}
		};
		scene.registerTimingTask(spinningTimerTask);

		scnUpVec = new Vec(0.0f, 1.0f, 0.0f);
		flyDisp = new Vec(0.0f, 0.0f, 0.0f);

		if (!(this instanceof InteractiveEyeFrame))
			setFlySpeed(0.01f * scene.radius());

		flyTimerTask = new TimingTask() {
			public void execute() {
				toss();
			}
		};
		scene.registerTimingTask(flyTimerTask);
	}
	
	/**
	 * Same as {@code this(scn)} and then calls {@link #setReferenceFrame(Frame)} on {@code referenceFrame}.
	 */
	public InteractiveFrame(AbstractScene scn, Frame referenceFrame) {
		this(scn);
		this.setReferenceFrame(referenceFrame);
	}

	protected InteractiveFrame(InteractiveFrame otherFrame) {
		super(otherFrame);

		for (Agent element : this.scene.inputHandler().agents()) {
			if (this.scene.inputHandler().isInAgentPool(otherFrame, element))
				this.scene.inputHandler().addInAgentPool(this, element);
		}

		this.isInCamPath = otherFrame.isInCamPath;

		this.setGrabsInputThreshold(otherFrame.grabsInputThreshold(), otherFrame.adaptiveGrabsInputThreshold());
		this.setRotationSensitivity(otherFrame.rotationSensitivity());
		this.setTranslationSensitivity(otherFrame.translationSensitivity());
		this.setWheelSensitivity(otherFrame.wheelSensitivity());

		this.setSpinningSensitivity(otherFrame.spinningSensitivity());
		this.setDampingFriction(otherFrame.dampingFriction());

		this.spinningTimerTask = new TimingTask() {
			public void execute() {
				spin();
			}
		};
		this.scene.registerTimingTask(spinningTimerTask);

		this.scnUpVec = new Vec();
		this.scnUpVec.set(otherFrame.sceneUpVector());
		this.flyDisp = new Vec();
		this.flyDisp.set(otherFrame.flyDisp);
		this.setFlySpeed(otherFrame.flySpeed());

		this.flyTimerTask = new TimingTask() {
			public void execute() {
				toss();
			}
		};
		this.scene.registerTimingTask(flyTimerTask);
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
	protected InteractiveFrame(AbstractScene scn, InteractiveEyeFrame iFrame) {
		super(scn, iFrame.translation().get(), iFrame.rotation().get(), iFrame.scaling());

		isInCamPath = true;

		setGrabsInputThreshold(20);
		// TODO future versions should go (except for iFrames in eyePath?):
		// setGrabsInputThreshold(Math.round(scene.radius()/10f), true);
		setRotationSensitivity(1.0f);
		setTranslationSensitivity(1.0f);
		setWheelSensitivity(20.0f);

		setSpinningSensitivity(0.3f);
		setDampingFriction(0.5f);

		spinningTimerTask = new TimingTask() {
			public void execute() {
				spin();
			}
		};
		scene.registerTimingTask(spinningTimerTask);

		scnUpVec = new Vec(0.0f, 1.0f, 0.0f);
		flyDisp = new Vec(0.0f, 0.0f, 0.0f);
		setFlySpeed(0.0f);
		flyTimerTask = new TimingTask() {
			public void execute() {
				toss();
			}
		};
		scene.registerTimingTask(flyTimerTask);
	}
	
  //grabber implementation
	
	protected FrameAction globalAction;
	
	@Override
	public FrameAction referenceAction() {
		return globalAction;
	}
	
	@Override
	public void setReferenceAction(Action<FrameAction> a) {
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
	
	//TODO test me
	protected Enum<?> action(BogusEvent event) {
		if (event instanceof ClickEvent)
			return FrameClickAction.valueOf(referenceAction().toString());
		if (event instanceof DOF1Event)
			return FrameDOF1Action.valueOf(referenceAction().toString());
		if (event instanceof DOF2Event)
			return FrameDOF2Action.valueOf(referenceAction().toString());
		if (event instanceof DOF3Event)
			return FrameDOF3Action.valueOf(referenceAction().toString());
		//if (event instanceof DOF6Event)
		else
			return FrameDOF6Action.valueOf(referenceAction().toString());
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
		//switch(referenceAction()) {
		switch(FrameClickAction.valueOf(referenceAction().toString())) {
		//switch(action(event)) {//not working
		case CENTER_FRAME:
			projectOnLine(scene.eye().position(), scene.eye().viewDirection());
			break;
		case ALIGN_FRAME:
			alignWithFrame(scene.eye().frame());
			break;
		case CUSTOM_CLICK_ACTION:
			performCustomAction(event);
			break;
			/*
		default:
			break;
			*/
		}
	}

	public void performInteraction(DOF1Event event) {
		if(scene.is2D())
			execAction2D(event, true);
		else
			execAction3D(event, true);
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
	
	/*
	protected void execAction2D(ClickEvent event) {
		
	}
	*/
	
	protected void execAction2D(DOF1Event event) {
		execAction2D(event, false);
	}

	protected void execAction2D(DOF1Event event, boolean wheel) {
		switch(FrameDOF1Action.valueOf(referenceAction().toString())) {
		case CUSTOM_DOF1_ACTION:
			performCustomAction(event);
			break;
		case ROTATE_Z:
			rot = new Rot(scene.isRightHanded() ? computeAngle(event, wheel) : -computeAngle(event, wheel));
			rotate(rot);
			setSpinningRotation(rot);
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
		default:
			//TODO
			//AbstractScene.showOnlyEyeWarning(a);
			break;
		}
	}

	protected void execAction2D(DOF2Event event) {
		Vec trans;
		float deltaX, deltaY;
		switch(FrameDOF2Action.valueOf(referenceAction().toString())) {
		case CUSTOM_DOF2_ACTION:
			performCustomAction(event);
			break;
		case MOVE_BACKWARD:
			rotate(computeRot(event, scene.window().projectedCoordinatesOf(position())));
			flyDisp.set(-flySpeed(), 0.0f, 0.0f);
			trans = localInverseTransformOf(flyDisp);
			translate(trans);
			setTossingDirection(trans);
			startTossing(event);
			break;
		case MOVE_FORWARD:
			rotate(computeRot(event, scene.window().projectedCoordinatesOf(position())));
			flyDisp.set(flySpeed(), 0.0f, 0.0f);
			trans = localInverseTransformOf(flyDisp);
			setTossingDirection(trans);
			startTossing(event);
			break;
		case ROTATE:
		case SCREEN_ROTATE:
			rot = computeRot(event, scene.window().projectedCoordinatesOf(position()));
			if (event.isRelative()) {
				setSpinningRotation(rot);
				if (Util.nonZero(dampingFriction()))
					startSpinning(event);
				else
					spin();
			} else
				// absolute needs testing
				rotate(rot);
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
		default:
		  //TODO
			//AbstractScene.showOnlyEyeWarning(a);
			break;
		}
	}

	protected void execAction2D(DOF3Event event) {
		if( FrameDOF3Action.valueOf(referenceAction().toString()) ==  FrameDOF3Action.CUSTOM_DOF3_ACTION )
			performCustomAction(event);
		else
			execAction2D(event.dof2Event());
	}

	protected void execAction2D(DOF6Event event) {
		if( FrameDOF6Action.valueOf(referenceAction().toString()) ==  FrameDOF6Action.CUSTOM_DOF6_ACTION )
			performCustomAction(event);
		else
			execAction2D(event.dof3Event());
	}
	
	// 3D
	
	/*
  protected void execAction3D(ClickEvent event) {
	}
	*/
	
	protected void execAction3D(DOF1Event event) {
		execAction3D(event, false);
	}

	protected void execAction3D(DOF1Event event, boolean wheel) {
		Vec trans;
		float delta;
		switch(FrameDOF1Action.valueOf(referenceAction().toString())) {
		case CUSTOM_DOF1_ACTION:
			performCustomAction(event);
			break;
		case ROTATE_X:
			if(scene.is3D())
				rotateAroundEyeAxes(computeAngle(event, wheel), 0, 0);
			break;
		case ROTATE_Y:
			if(scene.is3D())
				rotateAroundEyeAxes(0, -computeAngle(event, wheel), 0);
			break;
		case ROTATE_Z:
			if(scene.is3D())
				rotateAroundEyeAxes(0, 0, -computeAngle(event, wheel));
			break;
		case SCALE:
			delta = delta1(event, wheel);
			float s = 1 + Math.abs(delta) / (float) scene.height();
			scale(delta >= 0 ? s : 1 / s);
			break;
		case TRANSLATE_X:
			trans = new Vec(delta1(event, wheel), 0.0f, 0.0f);
			scale2Fit(trans);
			translateFromEye(trans, wheel ? 1 : translationSensitivity());
			break;
		case TRANSLATE_Y:
			trans = new Vec(0.0f, scene.isRightHanded() ? -delta1(event, wheel) : delta1(event, wheel), 0.0f);
			scale2Fit(trans);
			translateFromEye(trans, wheel ? 1 : translationSensitivity());
			break;
		case TRANSLATE_Z:
			trans = new Vec(0.0f, 0.0f, delta1(event, wheel));
			scale2Fit(trans);
			translateFromEye(trans, wheel ? 1 : translationSensitivity());
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
			/*
		default:
			break;
			*/
		}
	}

	protected void execAction3D(DOF2Event event) {
		Quat rot;
		Vec trans;
		float angle;
		switch(FrameDOF2Action.valueOf(referenceAction().toString())) {
		case CUSTOM_DOF2_ACTION:
			performCustomAction(event);
			break;
		case DRIVE:
			rotate(turnQuaternion(event.dof1Event(), scene.camera()));
			flyDisp.set(0.0f, 0.0f, flySpeed());
			trans = rotation().rotate(flyDisp);
			setTossingDirection(trans);
			startTossing(event);
			break;
		case LOOK_AROUND:
			rotate(rollPitchQuaternion(event, scene.camera()));
			break;
		case MOVE_BACKWARD:
			rotate(rollPitchQuaternion(event, scene.camera()));
			flyDisp.set(0.0f, 0.0f, flySpeed());
			trans = rotation().rotate(flyDisp);
			setTossingDirection(trans);
			startTossing(event);
			break;
		case MOVE_FORWARD:
			rotate(rollPitchQuaternion(event, scene.camera()));
			flyDisp.set(0.0f, 0.0f, -flySpeed());
			trans = rotation().rotate(flyDisp);
			setTossingDirection(trans);
			startTossing(event);
			break;
		case ROTATE:
			if (event.isAbsolute()) {
				//TODO restore
				//AbstractScene.showEventVariationWarning(a);
				break;
			}
			trans = scene.camera().projectedCoordinatesOf(position());
			rot = deformedBallQuaternion(event, trans.x(), trans.y(), scene.camera());
			trans = rot.axis();
			trans = scene.camera().frame().orientation().rotate(trans);
			trans = transformOf(trans);
			rot = new Quat(trans, -rot.angle());
			setSpinningRotation(rot);
			if (Util.nonZero(dampingFriction()))
				startSpinning(event);
			else
				spin();
			break;
		case SCREEN_ROTATE:
			if (event.isAbsolute()) {
				//TODO
				//AbstractScene.showEventVariationWarning(a);
				break;
			}
			trans = scene.camera().projectedCoordinatesOf(position());
			float prev_angle = (float) Math.atan2(event.prevY() - trans.vec[1], event.prevX() - trans.vec[0]);
			angle = (float) Math.atan2(event.y() - trans.vec[1], event.x() - trans.vec[0]);
			Vec axis = transformOf(scene.camera().frame().orientation().rotate(new Vec(0.0f, 0.0f, -1.0f)));
			if (scene.isRightHanded())
				rot = new Quat(axis, angle - prev_angle);
			else
				rot = new Quat(axis, prev_angle - angle);
			setSpinningRotation(rot);
			if (Util.nonZero(dampingFriction()))
				startSpinning(event);
			else
				spin();
			break;
		case SCREEN_TRANSLATE:
			int dir = originalDirection(event);
			trans = new Vec();
			if (dir == 1)
				if (event.isAbsolute())
					trans.set(event.x(), 0.0f, 0.0f);
				else
					trans.set(event.dx(), 0.0f, 0.0f);
			else if (dir == -1)
				if (event.isAbsolute())
					trans.set(0.0f, scene.isRightHanded() ? -event.y() : event.y(), 0.0f);
				else
					trans.set(0.0f, scene.isRightHanded() ? -event.dy() : event.dy(), 0.0f);
			scale2Fit(trans);
			translateFromEye(trans);
			break;
		case TRANSLATE:
			if (event.isRelative())
				trans = new Vec(event.dx(), scene.isRightHanded() ? -event.dy() : event.dy(), 0.0f);
			else
				trans = new Vec(event.x(), scene.isRightHanded() ? -event.y() : event.y(), 0.0f);
			scale2Fit(trans);
			translateFromEye(trans);
			break;		
		case ROTATE_Y:
		case ROTATE_Z:
		case TRANSLATE_X:
			execAction3D(event.dof1Event(true), false);
			break;
		default:
			execAction3D(event.dof1Event(false), false);
			break;	
		}
	}

	protected void execAction3D(DOF3Event event) {
		switch(FrameDOF3Action.valueOf(referenceAction().toString())) {
		case CUSTOM_DOF3_ACTION:
			performCustomAction(event);
			break;
		case ROTATE_XYZ:
			if (event.isAbsolute())
				rotateAroundEyeAxes(event.x(), -event.y(), -event.z());
			else
				rotateAroundEyeAxes(event.dx(), -event.dy(), -event.dz());
			break;
		case TRANSLATE_XYZ:
			if (event.isRelative())
				trans = new Vec(event.dx(), scene.isRightHanded() ? -event.dy() : event.dy(), event.dz());
			else
				trans = new Vec(event.x(), scene.isRightHanded() ? -event.y() : event.y(), event.z());
			scale2Fit(trans);
			translateFromEye(trans);
			break;
		default:
			execAction3D(event.dof2Event());
			break;
		}
	}

	protected void execAction3D(DOF6Event event) {
		switch(FrameDOF6Action.valueOf(referenceAction().toString())) {
		case CUSTOM_DOF6_ACTION:
			performCustomAction(event);
			break;
		case TRANSLATE_XYZ_ROTATE_XYZ:
		  // A. Translate the iFrame
			if (event.isRelative())
				trans = new Vec(event.dx(), scene.isRightHanded() ? -event.dy() : event.dy(), event.dz());
			else
				trans = new Vec(event.x(), scene.isRightHanded() ? -event.y() : event.y(), event.z());
			scale2Fit(trans);
			translateFromEye(trans);
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

	/**
	 * Returns {@code agent.isInPool(this)}.
	 * 
	 * @see remixlab.bias.core.Agent#isInPool(Grabber)
	 */
	public boolean isInAgentPool(Agent agent) {
		return agent.isInPool(this);
	}

	/**
	 * Convenience wrapper function that simply calls {agent.addInPool(this)}.
	 * 
	 * @see remixlab.bias.core.Agent#addInPool(Grabber)
	 */
	public void addInAgentPool(Agent agent) {
		agent.addInPool(this);
	}

	/**
	 * Convenience wrapper function that simply calls {@code agent.removeFromPool(this)}.
	 * 
	 * @see remixlab.bias.core.Agent#removeFromPool(Grabber)
	 */
	public void removeFromAgentPool(Agent agent) {
		agent.removeFromPool(this);
	}

	/**
	 * Defines the {@link #rotationSensitivity()}.
	 */
	public final void setRotationSensitivity(float sensitivity) {
		rotSensitivity = sensitivity;
	}

	/**
	 * Defines the {@link #translationSensitivity()}.
	 */
	public final void setTranslationSensitivity(float sensitivity) {
		transSensitivity = sensitivity;
	}

	/**
	 * Defines the {@link #spinningSensitivity()}.
	 */
	public final void setSpinningSensitivity(float sensitivity) {
		spngSensitivity = sensitivity;
	}

	/**
	 * Defines the {@link #wheelSensitivity()}.
	 */
	public final void setWheelSensitivity(float sensitivity) {
		wheelSensitivity = sensitivity;
	}

	/**
	 * Returns the influence of a gesture displacement on the InteractiveFrame rotation.
	 * <p>
	 * Default value is 1.0 (which matches an identical mouse displacement), a higher value will generate a larger
	 * rotation (and inversely for lower values). A 0.0 value will forbid rotation (see also {@link #constraint()}).
	 * 
	 * @see #setRotationSensitivity(float)
	 * @see #translationSensitivity()
	 * @see #spinningSensitivity()
	 * @see #wheelSensitivity()
	 */
	public final float rotationSensitivity() {
		return rotSensitivity;
	}

	/**
	 * Returns the influence of a gesture displacement on the InteractiveFrame translation.
	 * <p>
	 * Default value is 1.0 which in the case of a mouse interaction makes the InteractiveFrame precisely stays under the
	 * mouse cursor.
	 * <p>
	 * With an identical gesture displacement, a higher value will generate a larger translation (and inversely for lower
	 * values). A 0.0 value will forbid translation (see also {@link #constraint()}).
	 * 
	 * @see #setTranslationSensitivity(float)
	 * @see #rotationSensitivity()
	 * @see #spinningSensitivity()
	 * @see #wheelSensitivity()
	 */
	public final float translationSensitivity() {
		return transSensitivity;
	}

	/**
	 * Returns the minimum gesture speed required to make the InteractiveFrame {@link #spin()}. Spinning requires to set
	 * to {@link #dampingFriction()} to 0.
	 * <p>
	 * See {@link #spin()}, {@link #spinningRotation()} and {@link #startSpinning(MotionEvent)} for details.
	 * <p>
	 * Gesture speed is expressed in pixels per milliseconds. Default value is 0.3 (300 pixels per second). Use
	 * {@link #setSpinningSensitivity(float)} to tune this value. A higher value will make spinning more difficult (a
	 * value of 100.0 forbids spinning in practice).
	 * 
	 * @see #setSpinningSensitivity(float)
	 * @see #translationSensitivity()
	 * @see #rotationSensitivity()
	 * @see #wheelSensitivity()
	 * @see #setDampingFriction(float)
	 */
	public final float spinningSensitivity() {
		return spngSensitivity;
	}

	/**
	 * Returns the wheel sensitivity.
	 * <p>
	 * Default value is 20.0. A higher value will make the wheel action more efficient (usually meaning a faster zoom).
	 * Use a negative value to invert the zoom in and out directions.
	 * 
	 * @see #setWheelSensitivity(float)
	 * @see #translationSensitivity()
	 * @see #rotationSensitivity()
	 * @see #spinningSensitivity()
	 */
	public float wheelSensitivity() {
		return wheelSensitivity;
	}

	/**
	 * Returns {@code true} when the InteractiveFrame is spinning.
	 * <p>
	 * During spinning, {@link #spin()} rotates the InteractiveFrame by its {@link #spinningRotation()} at a frequency
	 * defined when the InteractiveFrame {@link #startSpinning(MotionEvent)}.
	 * <p>
	 * Use {@link #startSpinning(MotionEvent)} and {@link #stopSpinning()} to change this state. Default value is
	 * {@code false}.
	 * 
	 * @see #isTossing()
	 */
	public final boolean isSpinning() {
		return spinningTimerTask.isActive();
	}

	/**
	 * Returns {@code true} when the InteractiveFrame is tossing.
	 * <p>
	 * During tossing, {@link #toss()} translates the InteractiveFrame by its {@link #tossingDirection()} at a frequency
	 * defined when the InteractiveFrame {@link #startTossing(MotionEvent)}.
	 * <p>
	 * Use {@link #startTossing(MotionEvent)} and {@link #stopTossing()} to change this state. Default value is
	 * {@code false}.
	 * 
	 * {@link #isSpinning()}
	 */
	public final boolean isTossing() {
		return flyTimerTask.isActive();
	}

	/**
	 * Returns the incremental rotation that is applied by {@link #spin()} to the InteractiveFrame orientation when it
	 * {@link #isSpinning()}.
	 * <p>
	 * Default value is a {@code null} rotation. Use {@link #setSpinningRotation(Rotation)} to change this value.
	 * <p>
	 * The {@link #spinningRotation()} axis is defined in the InteractiveFrame coordinate system. You can use
	 * {@link remixlab.dandelion.core.Frame#transformOfFrom(Vec, Frame)} to convert this axis from another Frame
	 * coordinate system.
	 * <p>
	 * <b>Attention: </b>Spinning may be decelerated according to {@link #dampingFriction()} till it stops completely.
	 * 
	 * @see #tossingDirection()
	 */
	public final Rotation spinningRotation() {
		return spngRotation;
	}

	/**
	 * Returns the incremental translation that is applied by {@link #toss()} to the InteractiveFrame position when it
	 * {@link #isTossing()}.
	 * <p>
	 * Default value is no translation. Use {@link #setTossingDirection(Vec)} to change this value.
	 * <p>
	 * <b>Attention: </b>Tossing may be decelerated according to {@link #dampingFriction()} till it stops completely.
	 * 
	 * @see #spinningRotation()
	 */
	public final Vec tossingDirection() {
		return tDir;
	}

	/**
	 * Defines the {@link #spinningRotation()}. Its axis is defined in the InteractiveFrame coordinate system.
	 * 
	 * @see #setTossingDirection(Vec)
	 */
	public final void setSpinningRotation(Rotation spinningRotation) {
		spngRotation = spinningRotation;
	}

	/**
	 * Defines the {@link #tossingDirection()} in the InteractiveFrame coordinate system.
	 * 
	 * @see #setSpinningRotation(Rotation)
	 */
	public final void setTossingDirection(Vec dir) {
		tDir = dir;
	}

	/**
	 * Returns {@code true} when the InteractiveFrame is being manipulated with an agent.
	 */
	public boolean isInInteraction() {
		return globalAction != null;
	}

	/**
	 * Stops the spinning motion started using {@link #startSpinning(MotionEvent)}. {@link #isSpinning()} will return
	 * {@code false} after this call.
	 * <p>
	 * <b>Attention: </b>This method may be called by {@link #spin()}, since spinning may be decelerated according to
	 * {@link #dampingFriction()} till it stops completely.
	 * 
	 * @see #dampingFriction()
	 * @see #toss()
	 */
	public final void stopSpinning() {
		spinningTimerTask.stop();
	}

	/**
	 * Stops the tossing motion started using {@link #startTossing(MotionEvent)}. {@link #isTossing()} will return
	 * {@code false} after this call.
	 * <p>
	 * <b>Attention: </b>This method may be called by {@link #toss()}, since tossing may be decelerated according to
	 * {@link #dampingFriction()} till it stops completely.
	 * 
	 * @see #dampingFriction()
	 * @see #spin()
	 */
	public final void stopTossing() {
		flyTimerTask.stop();
	}

	/**
	 * Starts the spinning of the InteractiveFrame.
	 * <p>
	 * This method starts a timer that will call {@link #spin()} every {@code updateInterval} milliseconds. The
	 * InteractiveFrame {@link #isSpinning()} until you call {@link #stopSpinning()}.
	 * <p>
	 * <b>Attention: </b>Spinning may be decelerated according to {@link #dampingFriction()} till it stops completely.
	 * 
	 * @see #dampingFriction()
	 * @see #toss()
	 */
	public void startSpinning(MotionEvent e) {
		eventSpeed = e.speed();
		int updateInterval = (int) e.delay();
		if (updateInterval > 0)
			spinningTimerTask.run(updateInterval);
	}

	/**
	 * Starts the tossing of the InteractiveFrame.
	 * <p>
	 * This method starts a timer that will call {@link #toss()} every FLY_UPDATE_PERDIOD milliseconds. The
	 * InteractiveFrame {@link #isTossing()} until you call {@link #stopTossing()}.
	 * <p>
	 * <b>Attention: </b>Tossing may be decelerated according to {@link #dampingFriction()} till it stops completely.
	 * 
	 * @see #dampingFriction()
	 * @see #spin()
	 */
	public void startTossing(MotionEvent e) {
		eventSpeed = e.speed();
		flyTimerTask.run(FLY_UPDATE_PERDIOD);
	}

	/**
	 * Rotates the InteractiveFrame by its {@link #spinningRotation()}. Called by a timer when the InteractiveFrame
	 * {@link #isSpinning()}.
	 * <p>
	 * <b>Attention: </b>Spinning may be decelerated according to {@link #dampingFriction()} till it stops completely.
	 * 
	 * @see #dampingFriction()
	 * @see #toss()
	 */
	public void spin() {
		if (Util.nonZero(dampingFriction())) {
			if (eventSpeed == 0) {
				stopSpinning();
				return;
			}
			rotate(spinningRotation());
			recomputeSpinningRotation();
		}
		else
			rotate(spinningRotation());
	}

	/**
	 * Translates the InteractiveFrame by its {@link #tossingDirection()}. Invoked by a timer when the InteractiveFrame is
	 * performing the DRIVE, MOVE_BACKWARD or MOVE_FORWARD dandelion actions.
	 * <p>
	 * <b>Attention: </b>Tossing may be decelerated according to {@link #dampingFriction()} till it stops completely.
	 * 
	 * @see #spin()
	 */
	public void toss() {
		if (Util.nonZero(dampingFriction())) {
			if (eventSpeed == 0) {
				stopTossing();
				return;
			}
			translate(tossingDirection());
			recomputeTossingDirection();
		}
		else
			translate(tossingDirection());
	}

	/**
	 * Defines the {@link #dampingFriction()}. Values must be in the range [0..1].
	 */
	public void setDampingFriction(float f) {
		if (f < 0 || f > 1)
			return;
		dampFriction = f;
		setDampingFrictionFx(dampFriction);
	}

	/**
	 * Defines the spinning deceleration.
	 * <p>
	 * Default value is 0.5. Use {@link #setDampingFriction(float)} to tune this value. A higher value will make damping
	 * more difficult (a value of 1.0 forbids damping).
	 */
	public float dampingFriction() {
		return dampFriction;
	}

	/**
	 * Internal use.
	 * <p>
	 * Computes and caches the value of the spinning friction used in {@link #recomputeSpinningRotation()}.
	 */
	protected void setDampingFrictionFx(float spinningFriction) {
		sFriction = spinningFriction * spinningFriction * spinningFriction;
	}

	/**
	 * Internal use.
	 * <p>
	 * Returns the cached value of the spinning friction used in {@link #recomputeSpinningRotation()}.
	 */
	protected float dampingFrictionFx() {
		return sFriction;
	}

	/**
	 * Internal method. Recomputes the {@link #spinningRotation()} according to {@link #dampingFriction()}.
	 * 
	 * @see #recomputeTossingDirection()
	 */
	protected void recomputeSpinningRotation() {
		float prevSpeed = eventSpeed;
		float damping = 1.0f - dampingFrictionFx();
		eventSpeed *= damping;
		if (Math.abs(eventSpeed) < .001f)
			eventSpeed = 0;
		// float currSpeed = eventSpeed;
		if (scene.is3D())
			((Quat) spinningRotation()).fromAxisAngle(((Quat) spinningRotation()).axis(), spinningRotation().angle()
					* (eventSpeed / prevSpeed));
		else
			this.setSpinningRotation(new Rot(spinningRotation().angle() * (eventSpeed / prevSpeed)));
	}

	/**
	 * Internal method. Recomputes the {@link #tossingDirection()} according to {@link #dampingFriction()}.
	 * 
	 * @see #recomputeSpinningRotation()
	 */
	protected void recomputeTossingDirection() {
		float prevSpeed = eventSpeed;
		float damping = 1.0f - dampingFrictionFx();
		eventSpeed *= damping;
		if (Math.abs(eventSpeed) < .001f)
			eventSpeed = 0;

		flyDisp.setZ(flyDisp.z() * (eventSpeed / prevSpeed));

		if (scene.is2D())
			setTossingDirection(localInverseTransformOf(flyDisp));
		else
			setTossingDirection(rotation().rotate(flyDisp));
	}

	/**
	 * Returns the fly speed, expressed in virtual scene units.
	 * <p>
	 * It corresponds to the incremental displacement that is periodically applied to the InteractiveFrame position when a
	 * MOVE_FORWARD or MOVE_BACKWARD action is proceeded.
	 * <p>
	 * <b>Attention:</b> When the InteractiveFrame is set as the {@link remixlab.dandelion.core.Eye#frame()} or when it is
	 * set as the {@link remixlab.dandelion.core.AbstractScene#avatar()} (which indeed is an instance of the
	 * InteractiveAvatarFrame class), this value is set according to the
	 * {@link remixlab.dandelion.core.AbstractScene#radius()} by
	 * {@link remixlab.dandelion.core.AbstractScene#setRadius(float)}.
	 */
	public float flySpeed() {
		return flySpd;
	}

	/**
	 * Sets the {@link #flySpeed()}, defined in virtual scene units.
	 * <p>
	 * Default value is 0.0, but it is modified according to the {@link remixlab.dandelion.core.AbstractScene#radius()}
	 * when the InteractiveFrame is set as the {@link remixlab.dandelion.core.Eye#frame()} (which indeed is an instance of
	 * the InteractiveEyeFrame class) or when the InteractiveFrame is set as the
	 * {@link remixlab.dandelion.core.AbstractScene#avatar()} (which indeed is an instance of the InteractiveAvatarFrame
	 * class).
	 */
	public void setFlySpeed(float speed) {
		flySpd = speed;
	}

	/**
	 * Returns the up vector used in fly mode, expressed in the world coordinate system.
	 * <p>
	 * Fly mode corresponds to the MOVE_FORWARD and MOVE_BACKWARD action bindings. In these modes, horizontal
	 * displacements of the mouse rotate the InteractiveFrame around this vector. Vertical displacements rotate always
	 * around the frame {@code X} axis.
	 * <p>
	 * This value is also used within the CAD_ROTATE action to define the up vector (and incidentally the 'horizon' plane)
	 * around which the camera will rotate.
	 * <p>
	 * Default value is (0,1,0), but it is updated by the Eye when set as its {@link remixlab.dandelion.core.Eye#frame()}.
	 * {@link remixlab.dandelion.core.Eye#setOrientation(Rotation)} and
	 * {@link remixlab.dandelion.core.Eye#setUpVector(Vec)} modify this value and should be used instead.
	 */
	public Vec sceneUpVector() {
		return scnUpVec;
	}

	/**
	 * Sets the {@link #sceneUpVector()}, defined in the world coordinate system.
	 * <p>
	 * Default value is (0,1,0), but it is updated by the Eye when set as its {@link remixlab.dandelion.core.Eye#frame()}.
	 * Use {@link remixlab.dandelion.core.Eye#setUpVector(Vec)} instead in that case.
	 */
	public void setSceneUpVector(Vec up) {
		scnUpVec = up;
	}

	/**
	 * This method will be called by the Eye when its orientation is changed, so that the {@link #sceneUpVector()} is
	 * changed accordingly. You should not need to call this method.
	 */
	public final void updateSceneUpVector() {
		scnUpVec = orientation().rotate(new Vec(0.0f, 1.0f, 0.0f));
	}

	/**
	 * <a href="http://en.wikipedia.org/wiki/Euler_angles#Extrinsic_rotations">Extrinsic rotation</a> about the
	 * {@link remixlab.dandelion.core.AbstractScene#eye()} {@link remixlab.dandelion.core.InteractiveEyeFrame} axes.
	 * 
	 * @param roll
	 *          Rotation angle in radians around the Eye x-Axis
	 * @param pitch
	 *          Rotation angle in radians around the Eye y-Axis
	 * @param yaw
	 *          Rotation angle in radians around the Eye z-Axis
	 * 
	 * @see remixlab.dandelion.geom.Quat#fromEulerAngles(float, float, float)
	 */
	public void rotateAroundEyeAxes(float roll, float pitch, float yaw) {
		if (scene.is2D()) {
			AbstractScene.showDepthWarning("rotateAroundEyeAxes");
			return;
		}
		Vec trans = new Vec();
		Quat q = new Quat(scene.isLeftHanded() ? roll : -roll, -pitch, scene.isLeftHanded() ? yaw : -yaw);
		// trans = scene.camera().projectedCoordinatesOf(position());
		trans.set(-q.x(), -q.y(), -q.z());
		trans = scene.camera().frame().orientation().rotate(trans);
		trans = transformOf(trans);
		q.setX(trans.x());
		q.setY(trans.y());
		q.setZ(trans.z());
		rotate(q);
	}
	
	// micro-actions procedures

	protected void scale2Fit(Vec trans) {
		// Scale to fit the screen relative event displacement
		switch (scene.camera().type()) {
		case PERSPECTIVE:
			trans.multiply(2.0f
					* (float) Math.tan(scene.camera().fieldOfView() / 2.0f)
					* Math.abs((scene.camera().frame().coordinatesOf(position())).vec[2] * scene.camera().frame().magnitude())
					/ scene.camera().screenHeight());
			break;
		case ORTHOGRAPHIC:
			float[] wh = scene.camera().getBoundaryWidthHeight();
			trans.vec[0] *= 2.0 * wh[0] / scene.camera().screenWidth();
			trans.vec[1] *= 2.0 * wh[1] / scene.camera().screenHeight();
			break;
		}
	}

	protected float delta1(DOF1Event e1, boolean wheel) {
		float delta;
		if (wheel) // its a wheel wheel :P
			delta = e1.x() * wheelSensitivity();
		else if (e1.isAbsolute())
			delta = e1.x();
		else
			delta = e1.dx();
		return delta;
	}

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
		if (scene.isRightHanded())
			rot.negate();
		return rot;
	}

	protected float computeAngle(DOF1Event e1, boolean wheel) {
		float angle;
		if (wheel) // its a wheel wheel :P
			angle = (float) Math.PI * e1.x() * wheelSensitivity() / scene.eye().screenWidth();
		else if (e1.isAbsolute())
			angle = (float) Math.PI * e1.x() / scene.eye().screenWidth();
		else
			angle = (float) Math.PI * e1.dx() / scene.eye().screenWidth();
		return angle;
	}

	protected void translateFromEye(Vec trans) {
		translateFromEye(trans, translationSensitivity());
	}

	protected void translateFromEye(Vec trans, float sens) {
		// Transform from eye to world coordinate system.
		trans = scene.is2D() ? scene.window().frame().inverseTransformOf(Vec.multiply(trans, sens))
				: scene.camera().frame().orientation().rotate(Vec.multiply(trans, sens));

		// And then down to frame
		if (referenceFrame() != null)
			trans = referenceFrame().transformOf(trans);
		translate(trans);
	}

	/**
	 * Returns a Quaternion computed according to the mouse motion. Mouse positions are projected on a deformed ball,
	 * centered on ({@code cx}, {@code cy}).
	 */
	protected Quat deformedBallQuaternion(DOF2Event event, float cx, float cy, Camera camera) {
		// TODO absolute events!?
		float x = event.x();
		float y = event.y();
		float prevX = event.prevX();
		float prevY = event.prevY();
		// Points on the deformed ball
		float px = rotationSensitivity() * ((int) prevX - cx) / camera.screenWidth();
		float py = rotationSensitivity() * (scene.isLeftHanded() ? ((int) prevY - cy) : (cy - (int) prevY))
				/ camera.screenHeight();
		float dx = rotationSensitivity() * (x - cx) / camera.screenWidth();
		float dy = rotationSensitivity() * (scene.isLeftHanded() ? (y - cy) : (cy - y)) / camera.screenHeight();

		Vec p1 = new Vec(px, py, projectOnBall(px, py));
		Vec p2 = new Vec(dx, dy, projectOnBall(dx, dy));
		// Approximation of rotation angle Should be divided by the projectOnBall size, but it is 1.0
		Vec axis = p2.cross(p1);
		float angle = 2.0f * (float) Math.asin((float) Math.sqrt(axis.squaredNorm() / p1.squaredNorm() / p2.squaredNorm()));
		return new Quat(axis, angle);
	}

	/**
	 * Returns "pseudo-distance" from (x,y) to ball of radius size. For a point inside the ball, it is proportional to the
	 * euclidean distance to the ball. For a point outside the ball, it is proportional to the inverse of this distance
	 * (tends to zero) on the ball, the function is continuous.
	 */
	protected static float projectOnBall(float x, float y) {
		// If you change the size value, change angle computation in deformedBallQuaternion().
		float size = 1.0f;
		float size2 = size * size;
		float size_limit = size2 * 0.5f;

		float d = x * x + y * y;
		return d < size_limit ? (float) Math.sqrt(size2 - d) : size_limit / (float) Math.sqrt(d);
	}

	/**
	 * Returns a Quaternion that is a rotation around current camera Y, proportional to the horizontal mouse position.
	 */
	protected final Quat turnQuaternion(DOF1Event event, Camera camera) {
		float deltaX = event.isAbsolute() ? event.x() : event.dx();
		return new Quat(new Vec(0.0f, 1.0f, 0.0f), rotationSensitivity() * (-deltaX) / camera.screenWidth());
	}

	/**
	 * Returns a Quaternion that is the composition of two rotations, inferred from the mouse roll (X axis) and pitch (
	 * {@link #sceneUpVector()} axis).
	 */
	protected final Quat rollPitchQuaternion(DOF2Event event, Camera camera) {
		float deltaX = event.isAbsolute() ? event.x() : event.dx();
		float deltaY = event.isAbsolute() ? event.y() : event.dy();

		if (scene.isRightHanded())
			deltaY = -deltaY;

		Quat rotX = new Quat(new Vec(1.0f, 0.0f, 0.0f), rotationSensitivity() * deltaY / camera.screenHeight());
		Quat rotY = new Quat(transformOf(sceneUpVector()), rotationSensitivity() * (-deltaX) / camera.screenWidth());
		return Quat.multiply(rotY, rotX);
	}

	/**
	 * Return 1 if mouse motion was started horizontally and -1 if it was more vertical. Returns 0 if this could not be
	 * determined yet (perfect diagonal motion, rare).
	 */
	protected int originalDirection(DOF2Event event) {
		if (!dirIsFixed) {
			Point delta;
			if (event.isAbsolute())
				delta = new Point(event.x(), event.y());
			else
				delta = new Point(event.dx(), event.dy());
			dirIsFixed = Math.abs(delta.x()) != Math.abs(delta.y());
			horiz = Math.abs(delta.x()) > Math.abs(delta.y());
		}

		if (dirIsFixed)
			if (horiz)
				return 1;
			else
				return -1;
		else
			return 0;
	}
}