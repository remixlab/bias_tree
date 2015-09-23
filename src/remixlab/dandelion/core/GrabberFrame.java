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

import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.geom.*;
import remixlab.fpstiming.TimingTask;
import remixlab.util.*;

/**
 * A {@link remixlab.dandelion.geom.Frame} implementing the {@link remixlab.bias.core.Grabber} interface, which
 * converts user gestures into translation, rotation and scaling {@link remixlab.dandelion.geom.Frame}
 * updates (see {@link #translationSensitivity()}, {@link #rotationSensitivity()} and {@link #scalingSensitivity()}).
 * A grabber-frame may thus be attached to some of your scene objects to control their motion using an
 * {@link remixlab.bias.core.Agent}, such as the {@link remixlab.dandelion.core.GrabberScene#motionAgent()} and
 * the {@link remixlab.dandelion.core.GrabberScene#keyboardAgent()} (see {@link #GrabberFrame(GrabberScene)} and all
 * the constructors that take an scene parameter). To attach a grabber-frame to {@code MyObject} use code like this:
 * <pre>
 * {@code
 * public class MyObject {
 *   public GrabberFrame gFrame;
 *   public void draw() {
 *     gFrame.scene().pushModelView();
 *     gFrame.applyWorldTransformation();
 *     drawMyObject();
 *     gFrame.scene().popModelView();
 *   }
 * }
 * }
 * </pre>
 * See {@link #applyTransformation()}, {@link #applyTransformation()}, {@link #scene()},
 * {@link remixlab.dandelion.core.GrabberScene#pushModelView()} and
 * {@link remixlab.dandelion.core.GrabberScene#popModelView()}
 * <p>
 * A grabber-frame may also be attached to an {@link remixlab.dandelion.core.Eye}, such as the
 * {@link remixlab.dandelion.core.GrabberScene#eyeFrame()} which is attached to the
 * {@link remixlab.dandelion.core.GrabberScene#eye()} (see {@link #isEyeFrame()}). Some user gestures are then 
 * interpreted in a negated way, respect to eye detached frames. For instance, with a move-to-the-right user gesture the
 * {@link remixlab.dandelion.core.GrabberScene#eyeFrame()} has to go to the <i>left</i>, so that the <i>scene</i> seems
 * to move to the right. A grabber-frame can be attached to an eye only at construction times (see {@link #GrabberFrame(Eye)} and
 * all the constructors that take an eye parameter). An eye may have more than one grabber-frame attached to it. To set
 * one of them as the {@link remixlab.dandelion.core.Eye#frame()}, call
 * {@link remixlab.dandelion.core.Eye#setFrame(GrabberFrame)}. Note that a grabber-frame may be detached from the eye at any time,
 * see {@link #detach()}.
 * <p>
 * This class provides several gesture-to-motion converting methods, such as: {@link #gestureArcball(MotionEvent)},
 * {@link #gestureMoveForward(DOF2Event, boolean)}, {@link #gestureTranslateX(KeyboardEvent, boolean)}, etc. To use them,
 * derive from this class and override the version of {@code performInteraction} with the (bogus-event) parameter type you want
 * to customize (see {@link #performInteraction(MotionEvent)}, {@link #performInteraction(KeyboardEvent)}, etc.). For example,
 * with the following code:
 * <pre>
 * {@code
 * protected void performInteraction(DOF2Event event) {
 *   if(event.id() == LEFT)
 *     gestureArcball(event);
 *   if(event.id() == RIGHT)
 *     gestureTranslateXY(event);
 * }
 * }
 * </pre>
 * your custom grabber-frame will then accordingly react to the LEFT and RIGHT mouse buttons, provided it's added to
 * the mouse-agent first (see {@link remixlab.bias.core.Agent#addGrabber(Grabber)}.
 * <p>
 * Picking a grabber-frame is simply done by checking if the pointer is within a circled area around the frame
 * {@link #center()} screen projection (see {@link #checkIfGrabsInput(float, float)},
 * {@link #setGrabsInputThreshold(float, boolean)} and {@link #adaptiveGrabsInputThreshold()}).
 * <p>
 * A grabber-frame is loosely-coupled with the scene object used to instantiate it, i.e., the transformation it represents may
 * be applied to a different scene. See {@link #applyTransformation()} and {@link #applyTransformation(GrabberScene)}.
 * <p>
 * Two grabber-frames can be synced together ({@link #sync(GrabberFrame, GrabberFrame)}), meaning that they will share
 * their global parameters (position, orientation and magnitude) taken the one that has been most recently updated. Syncing
 * can be useful to share frames among different off-screen scenes (see ProScene's CameraCrane and the AuxiliarViewer
 * examples).
 * <p>
 * Finally, a grabber-frame can be followed by an {@link remixlab.dandelion.core.Eye}, defining a 'third-person' eye
 * mode, see {@link remixlab.dandelion.core.Trackable} documentation. See also {@link #setTrackingEyeDistance(float)},
 * {@link #setTrackingEyeAzimuth(float)} and {@link #setTrackingEyeInclination(float)}.
 */
public class GrabberFrame extends Frame implements Grabber, Trackable {
	// according to space-nav fine tuning it turned out that the space-nav is right handed
	// we thus define our gesture physical space as right-handed as follows:
	// hid.sens should be non-negative for the space-nav to behave as expected from the physical interface
	// TODO: really need to check the second part above. For a fact it's known
	// 1. from the space-bav pov LH vs RH works the same way
	// 2. all space-nav sens are positive
	// Sens
	private float								rotSensitivity;
	private float								transSensitivity;
	private float								sclSensitivity;
	private float								wheelSensitivity;
	private float								keySensitivity;

	// spinning stuff:
	private float								spngSensitivity;
	private TimingTask					spinningTimerTask;
	private Rotation						spngRotation;
	protected float							dampFriction;							// new
	// toss and spin share the damp var:
	private float								sFriction;									// new

	// Whether the SCREEN_TRANS direction (horizontal or vertical) is fixed or not.
	public boolean							dirIsFixed;
	private boolean							horiz								= true; // Two simultaneous frames require two mice!

	protected float							eventSpeed;								// spnning and tossing
	protected Vec								fDir;
	protected float							flySpd;
	protected TimingTask				flyTimerTask;
	protected Vec								scnUpVec;
	protected Vec								flyDisp;
	protected static final long	FLY_UPDATE_PERDIOD	= 20;

	protected long							lastUpdate;
	protected GrabberScene			gScene;
	protected Eye								theeye;										// TODO add me in hashCode and equals?

	private float								grabsInputThreshold;
	private boolean							adpThreshold;

	// TODO decide this mode vs constraint! seems overkill
	// protected boolean rspct2Frame;
	// protected Frame gFrame;

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				appendSuper(super.hashCode()).
				append(grabsInputThreshold).
				append(adpThreshold).
				append(rotSensitivity).
				append(transSensitivity).
				append(sclSensitivity).
				append(spngRotation).
				append(spngSensitivity).
				append(dampFriction).
				append(sFriction).
				append(wheelSensitivity).
				append(keySensitivity).
				append(flyDisp).
				append(flySpd).
				append(scnUpVec).
				append(lastUpdate).
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

		GrabberFrame other = (GrabberFrame) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(grabsInputThreshold, other.grabsInputThreshold)
				.append(adpThreshold, other.adpThreshold)
				.append(dampFriction, other.dampFriction)
				.append(sFriction, other.sFriction)
				.append(rotSensitivity, other.rotSensitivity)
				.append(sclSensitivity, other.sclSensitivity)
				.append(spngRotation, other.spngRotation)
				.append(spngSensitivity, other.spngSensitivity)
				.append(wheelSensitivity, other.wheelSensitivity)
				.append(keySensitivity, other.keySensitivity)
				.append(flyDisp, other.flyDisp)
				.append(flySpd, other.flySpd)
				.append(scnUpVec, other.scnUpVec)
				.append(lastUpdate, other.lastUpdate)
				.isEquals();
	}

	/**
	 * Same as {@code this(scn, null, new Vec(), scn.is3D() ? new Quat() : new Rot(), 1)}.
	 * 
	 * @see #GrabberFrame(GrabberScene, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(GrabberScene scn) {
		this(scn, null, new Vec(), scn.is3D() ? new Quat() : new Rot(), 1);
	}

	/**
	 * Same as {@code this(eye, null, new Vec(), eye.scene().is3D() ? new Quat() : new Rot(), 1)}.
	 * 
	 * @see #GrabberFrame(Eye, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(Eye eye) {
		this(eye, null, new Vec(), eye.scene().is3D() ? new Quat() : new Rot(), 1);
	}

	/**
	 * Same as {@code this(scn, null, p, scn.is3D() ? new Quat() : new Rot(), 1)}.
	 * 
	 * @see #GrabberFrame(GrabberScene, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(GrabberScene scn, Vec p) {
		this(scn, null, p, scn.is3D() ? new Quat() : new Rot(), 1);
	}

	/**
	 * Same as {@code this(eye, null, p, eye.scene().is3D() ? new Quat() : new Rot(), 1)}.
	 * 
	 * @see #GrabberFrame(Eye, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(Eye eye, Vec p) {
		this(eye, null, p, eye.scene().is3D() ? new Quat() : new Rot(), 1);
	}

	/**
	 * Same as {@code this(scn, null, new Vec(), r, 1)}.
	 * 
	 * @see #GrabberFrame(GrabberScene, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(GrabberScene scn, Rotation r) {
		this(scn, null, new Vec(), r, 1);
	}

	/**
	 * Same as {@code this(eye, null, new Vec(), r, 1)}.
	 * 
	 * @see #GrabberFrame(Eye, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(Eye eye, Rotation r) {
		this(eye, null, new Vec(), r, 1);
	}

	/**
	 * Same as {@code this(scn, null, new Vec(), scn.is3D() ? new Quat() : new Rot(), s)}.
	 * 
	 * @see #GrabberFrame(GrabberScene, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(GrabberScene scn, float s) {
		this(scn, null, new Vec(), scn.is3D() ? new Quat() : new Rot(), s);
	}

	/**
	 * Same as {@code this(eye, null, new Vec(), eye.scene().is3D() ? new Quat() : new Rot(), s)}.
	 * 
	 * @see #GrabberFrame(Eye, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(Eye eye, float s) {
		this(eye, null, new Vec(), eye.scene().is3D() ? new Quat() : new Rot(), s);
	}

	/**
	 * Same as {@code this(scn, null, p, scn.is3D() ? new Quat() : new Rot(), s)}.
	 * 
	 * @see #GrabberFrame(GrabberScene, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(GrabberScene scn, Vec p, float s) {
		this(scn, null, p, scn.is3D() ? new Quat() : new Rot(), s);
	}

	/**
	 * Same as {@code this(eye, null, p, eye.scene().is3D() ? new Quat() : new Rot(), s)}.
	 * 
	 * @see #GrabberFrame(Eye, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(Eye eye, Vec p, float s) {
		this(eye, null, p, eye.scene().is3D() ? new Quat() : new Rot(), s);
	}

	/**
	 * Same as {@code this(scn, null, p, r, 1)}.
	 * 
	 * @see #GrabberFrame(GrabberScene, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(GrabberScene scn, Vec p, Rotation r) {
		this(scn, null, p, r, 1);
	}

	/**
	 * Same as {@code this(eye, null, p, r, 1)}.
	 * 
	 * @see #GrabberFrame(Eye, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(Eye eye, Vec p, Rotation r) {
		this(eye, null, p, r, 1);
	}

	/**
	 * Same as {@code this(scn, null, new Vec(), r, s)}.
	 * 
	 * @see #GrabberFrame(GrabberScene, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(GrabberScene scn, Rotation r, float s) {
		this(scn, null, new Vec(), r, s);
	}

	/**
	 * Same as {@code this(eye, null, new Vec(), r, s)}.
	 * 
	 * @see #GrabberFrame(Eye, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(Eye eye, Rotation r, float s) {
		this(eye, null, new Vec(), r, s);
	}

	/**
	 * Same as {@code this(scn, null, p, r, s)}.
	 * 
	 * @see #GrabberFrame(GrabberScene, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(GrabberScene scn, Vec p, Rotation r, float s) {
		this(scn, null, p, r, s);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, new Vec(), scn.is3D() ? new Quat() : new Rot(), 1)}.
	 * 
	 * @see #GrabberFrame(GrabberScene, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(GrabberScene scn, Frame referenceFrame) {
		this(scn, referenceFrame, new Vec(), scn.is3D() ? new Quat() : new Rot(), 1);
	}

	public GrabberFrame(Eye eye, Frame referenceFrame) {
		this(eye, referenceFrame, new Vec(), eye.scene().is3D() ? new Quat() : new Rot(), 1);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, p, scn.is3D() ? new Quat() : new Rot(), 1)}.
	 * 
	 * @see #GrabberFrame(GrabberScene, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(GrabberScene scn, Frame referenceFrame, Vec p) {
		this(scn, referenceFrame, p, scn.is3D() ? new Quat() : new Rot(), 1);
	}

	/**
	 * Same as {@code this(eye, referenceFrame, p, eye.scene().is3D() ? new Quat() : new Rot(), 1)}.
	 * 
	 * @see #GrabberFrame(Eye, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(Eye eye, Frame referenceFrame, Vec p) {
		this(eye, referenceFrame, p, eye.scene().is3D() ? new Quat() : new Rot(), 1);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, new Vec(), r, 1)}.
	 * 
	 * @see #GrabberFrame(GrabberScene, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(GrabberScene scn, Frame referenceFrame, Rotation r) {
		this(scn, referenceFrame, new Vec(), r, 1);
	}

	/**
	 * Same as {@code this(eye, referenceFrame, new Vec(), r, 1)}.
	 * 
	 * @see #GrabberFrame(Eye, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(Eye eye, Frame referenceFrame, Rotation r) {
		this(eye, referenceFrame, new Vec(), r, 1);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, new Vec(), scn.is3D() ? new Quat() : new Rot(), s)}.
	 * 
	 * @see #GrabberFrame(GrabberScene, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(GrabberScene scn, Frame referenceFrame, float s) {
		this(scn, referenceFrame, new Vec(), scn.is3D() ? new Quat() : new Rot(), s);
	}

	/**
	 * Same as {@code this(eye, referenceFrame, new Vec(), eye.scene().is3D() ? new Quat() : new Rot(), s)}.
	 * 
	 * @see #GrabberFrame(Eye, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(Eye eye, Frame referenceFrame, float s) {
		this(eye, referenceFrame, new Vec(), eye.scene().is3D() ? new Quat() : new Rot(), s);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, p, scn.is3D() ? new Quat() : new Rot(), s)}.
	 * 
	 * @see #GrabberFrame(GrabberScene, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(GrabberScene scn, Frame referenceFrame, Vec p, float s) {
		this(scn, referenceFrame, p, scn.is3D() ? new Quat() : new Rot(), s);
	}

	/**
	 * Same as {@code this(eye, referenceFrame, p, eye.scene().is3D() ? new Quat() : new Rot(), s)}.
	 * 
	 * @see #GrabberFrame(Eye, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(Eye eye, Frame referenceFrame, Vec p, float s) {
		this(eye, referenceFrame, p, eye.scene().is3D() ? new Quat() : new Rot(), s);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, p, r, 1)}.
	 * 
	 * @see #GrabberFrame(GrabberScene, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(GrabberScene scn, Frame referenceFrame, Vec p, Rotation r) {
		this(scn, referenceFrame, p, r, 1);
	}

	/**
	 * Same as {@code this(eye, referenceFrame, p, r, 1)}.
	 * 
	 * @see #GrabberFrame(Eye, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(Eye eye, Frame referenceFrame, Vec p, Rotation r) {
		this(eye, referenceFrame, p, r, 1);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, new Vec(), r, s)}.
	 * 
	 * @see #GrabberFrame(GrabberScene, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(GrabberScene scn, Frame referenceFrame, Rotation r, float s) {
		this(scn, referenceFrame, new Vec(), r, s);
	}

	/**
	 * Same as {@code this(eye, referenceFrame, new Vec(), r, s)}.
	 * 
	 * @see #GrabberFrame(Eye, Frame, Vec, Rotation, float)
	 */
	public GrabberFrame(Eye eye, Frame referenceFrame, Rotation r, float s) {
		this(eye, referenceFrame, new Vec(), r, s);
	}

	/**
	 * Creates a scene grabber-frame with {@code referenceFrame} as {@link #referenceFrame()}, and {@code p},
	 * {@code r} and {@code s} as the frame {@link #translation()}, {@link #rotation()} and {@link #scaling()},
	 * respectively.
	 * <p>
	 * The {@link remixlab.dandelion.core.GrabberScene#inputHandler()} will attempt to add the
	 * grabber-frame to all its {@link remixlab.bias.core.InputHandler#agents()}, such as the
	 * {@link remixlab.dandelion.core.GrabberScene#motionAgent()} and the
	 * {@link remixlab.dandelion.core.GrabberScene#keyboardAgent()}.
	 * <p>
	 * The grabber-frame sensitivities are set to their default values, see {@link #spinningSensitivity()},
	 * {@link #wheelSensitivity()}, {@link #keyboardSensitivity()}, {@link #rotationSensitivity()},
	 * {@link #translationSensitivity()} and {@link #scalingSensitivity()}.
	 * <p>
	 * After object creation a call to {@link #isEyeFrame()} will return {@code false}.
	 */
	public GrabberFrame(GrabberScene scn, Frame referenceFrame, Vec p, Rotation r, float s) {
		super(referenceFrame, p, r, s);
		init(scn);
		setGrabsInputThreshold(20);
		setFlySpeed(0.01f * scene().eye().sceneRadius());
		for(Agent agent : scene().inputHandler().agents())
			agent.addGrabber(this);
	}

	/**
	 * Creates an eye grabber-frame with {@code referenceFrame} as {@link #referenceFrame()}, and {@code p},
	 * {@code r} and {@code s} as the frame {@link #translation()}, {@link #rotation()} and {@link #scaling()},
	 * respectively.
	 * <p>
	 * The grabber-frame isn't added to any of the {@link remixlab.dandelion.core.GrabberScene#inputHandler()}
	 * {@link remixlab.bias.core.InputHandler#agents()}. A call to
	 * {@link remixlab.dandelion.core.GrabberScene#setEye(Eye)} will do it.
	 * <p>
	 * The grabber-frame sensitivities are set to their default values, see {@link #spinningSensitivity()},
	 * {@link #wheelSensitivity()}, {@link #keyboardSensitivity()}, {@link #rotationSensitivity()},
	 * {@link #translationSensitivity()} and {@link #scalingSensitivity()}.
	 * <p>
	 * After object creation a call to {@link #isEyeFrame()} will return {@code true}.
	 */
	public GrabberFrame(Eye eye, Frame referenceFrame, Vec p, Rotation r, float s) {
		super(referenceFrame, p, r, s);
		theeye = eye;
		init(theeye.scene());
		setFlySpeed(0.01f * eye().sceneRadius());
		//fov =  Math.PI / 3.0f
		if(scene().is3D())
			setMagnitude((float) Math.tan(((float) Math.PI / 3.0f) / 2.0f));
	}
	
	protected void init(GrabberScene scn) {
		gScene = scn;
		setRotationSensitivity(1.0f);
		setScalingSensitivity(1.0f);
		setTranslationSensitivity(1.0f);
		// TODO normalize
		setWheelSensitivity(15f);
		setKeyboardSensitivity(1.0f);
		setSpinningSensitivity(0.3f);
		setDamping(0.5f);

		spinningTimerTask = new TimingTask() {
			public void execute() {
				spinExecution();
			}
		};
		scene().registerTimingTask(spinningTimerTask);

		scnUpVec = new Vec(0.0f, 1.0f, 0.0f);
		flyDisp = new Vec(0.0f, 0.0f, 0.0f);
		flyTimerTask = new TimingTask() {
			public void execute() {
				fly();
			}
		};
		scene().registerTimingTask(flyTimerTask);
		// end

		// new
		// TODO future versions should go (except for iFrames in eyePath?):
		// setGrabsInputThreshold(Math.round(scene.radius()/10f), true);
		
		//TODO experimental
		q = scene().is3D() ? new Quat((float) Math.PI / 4, 0, 0) : new Rot((float) Math.PI / 4);
		//updateTrackingEyeFrame();
	}
	
	protected GrabberFrame(GrabberFrame otherFrame) {
		super(otherFrame);
		this.gScene = otherFrame.gScene;		
		this.theeye = otherFrame.theeye;
		
		this.spinningTimerTask = new TimingTask() {
			public void execute() {
				spinExecution();
			}
		};

		this.gScene.registerTimingTask(spinningTimerTask);

		this.scnUpVec = new Vec();
		this.scnUpVec.set(otherFrame.sceneUpVector());
		this.flyDisp = new Vec();
		this.flyDisp.set(otherFrame.flyDisp.get());
		this.flyTimerTask = new TimingTask() {
			public void execute() {
				fly();
			}
		};
		this.gScene.registerTimingTask(flyTimerTask);
		lastUpdate = otherFrame.lastUpdate();
		// end
		// this.isInCamPath = otherFrame.isInCamPath;
		//
		// this.setGrabsInputThreshold(otherFrame.grabsInputThreshold(), otherFrame.adaptiveGrabsInputThreshold());
		this.adpThreshold = otherFrame.adpThreshold;
		this.grabsInputThreshold = otherFrame.grabsInputThreshold;

		this.setRotationSensitivity(otherFrame.rotationSensitivity());
		this.setScalingSensitivity(otherFrame.scalingSensitivity());
		this.setTranslationSensitivity(otherFrame.translationSensitivity());
		this.setWheelSensitivity(otherFrame.wheelSensitivity());
		this.setKeyboardSensitivity(otherFrame.keyboardSensitivity());
		//
		this.setSpinningSensitivity(otherFrame.spinningSensitivity());
		this.setDamping(otherFrame.damping());
		//
		this.setFlySpeed(otherFrame.flySpeed());
		
		if(!this.isEyeFrame())
			for(Agent agent : gScene.inputHandler().agents())
				if(agent.hasGrabber(otherFrame))
					agent.addGrabber(this);
	}

	@Override
	public GrabberFrame get() {
		return new GrabberFrame(this);
	}
	
	@Override
	public GrabberFrame detach() {
		GrabberFrame frame = new GrabberFrame(gScene);
		for(Agent agent : gScene.inputHandler().agents())
			agent.removeGrabber(frame);
		frame.fromFrame(this);
		return frame;
	}

	/**
	 * Returns the scene this object belongs to.
	 * <p>
	 * Note that if this {@link #isEyeFrame()} then returns {@code eye().scene()}.
	 * 
	 * @see #eye()
	 * @see remixlab.dandelion.core.Eye#scene()
	 */
	public GrabberScene scene() {
		return gScene;
	}

	/**
	 * Returns the eye object this grabber-frame is attached to. May be null if the grabber-frame is not attach to an eye.
	 * 
	 * @see #isEyeFrame()
	 */
	public Eye eye() {
		return theeye;
	}

	/**
	 * Returns true if the grabber-frame is attached to an eye, and false otherwise. Grabber-frames can only be attached
	 * to an eye at construction times. Refer to the grabber-frame constructors that take an eye parameter.
	 * 
	 * @see #eye()
	 */
	public boolean isEyeFrame() {
		return theeye != null;
	}
	
	@Override
	public boolean checkIfGrabsInput(BogusEvent event) {
		if (event instanceof KeyboardEvent)
			return checkIfGrabsInput((KeyboardEvent) event);
		if (event instanceof ClickEvent)
			return checkIfGrabsInput((ClickEvent) event);
		if (event instanceof MotionEvent)
			return checkIfGrabsInput((MotionEvent) event);
		return false;
	}

	public boolean checkIfGrabsInput(MotionEvent event) {
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

	protected boolean checkIfGrabsInput(ClickEvent event) {
		return checkIfGrabsInput(event.x(), event.y());
	}

	protected boolean checkIfGrabsInput(KeyboardEvent event) {
		GrabberScene.showMissingImplementationWarning("checkIfGrabsInput(KeyboardEvent event)", this.getClass().getName());
		return false;
	}

	protected boolean checkIfGrabsInput(DOF1Event event) {
		GrabberScene.showMissingImplementationWarning("checkIfGrabsInput(DOF1Event event)", this.getClass().getName());
		return false;
	}

	protected boolean checkIfGrabsInput(DOF2Event event) {
		if (event.isAbsolute()) {
			GrabberScene.showEventVariationWarning("checkIfGrabsInput");
			return false;
		}
		return checkIfGrabsInput(event.x(), event.y());
	}

	/**
	 * Picks the grabber-frame 
	 */
	public boolean checkIfGrabsInput(float x, float y) {
		Vec proj = gScene.eye().projectedCoordinatesOf(position());
		float halfThreshold = grabsInputThreshold() / 2;
		return ((Math.abs(x - proj.vec[0]) < halfThreshold) && (Math.abs(y - proj.vec[1]) < halfThreshold));
	}

	protected boolean checkIfGrabsInput(DOF3Event event) {
		return checkIfGrabsInput(event.dof2Event());
	}

	protected boolean checkIfGrabsInput(DOF6Event event) {
		return checkIfGrabsInput(event.dof3Event().dof2Event());
	}

	@Override
	public void performInteraction(BogusEvent event) {
		if (event instanceof ClickEvent)
			performInteraction((ClickEvent) event);
		if (event instanceof MotionEvent)
			performInteraction((MotionEvent) event);
		if (event instanceof KeyboardEvent)
			performInteraction((KeyboardEvent) event);
	}

	/**
	 * Calls performInteraction() on the proper motion event: {@link remixlab.bias.event.DOF1Event},
	 * {@link remixlab.bias.event.DOF2Event}, {@link remixlab.bias.event.DOF3Event} or {@link remixlab.bias.event.DOF6Event}.
	 * <p>
	 * Override this method when you want the object to perform an interaction from a
	 * {@link remixlab.bias.event.MotionEvent}. 
	 */
	protected void performInteraction(MotionEvent event) {
		if (event instanceof DOF1Event)
			performInteraction((DOF1Event) event);
		if (event instanceof DOF2Event)
			performInteraction((DOF2Event) event);
		if (event instanceof DOF3Event)
			performInteraction((DOF3Event) event);
		if (event instanceof DOF6Event)
			performInteraction((DOF6Event) event);
	}

	/**
	 * Override this method when you want the object to perform an interaction from a
	 * {@link remixlab.bias.event.DOF1Event}. 
	 */
	protected void performInteraction(DOF1Event event) {
	}

	/**
	 * Override this method when you want the object to perform an interaction from a
	 * {@link remixlab.bias.event.DOF2Event}.
	 */
	protected void performInteraction(DOF2Event event) {
	}

	/**
	 * Override this method when you want the object to perform an interaction from a
	 * {@link remixlab.bias.event.DOF3Event}. 
	 */
	protected void performInteraction(DOF3Event event) {
	}

	/**
	 * Override this method when you want the object to perform an interaction from a
	 * {@link remixlab.bias.event.DOF6Event}. 
	 */
	protected void performInteraction(DOF6Event event) {
	}

	/**
	 * Override this method when you want the object to perform an interaction from a
	 * {@link remixlab.bias.event.ClickEvent}. 
	 */
	protected void performInteraction(ClickEvent event) {
	}

	/**
	 * Override this method when you want the object to perform an interaction from a
	 * {@link remixlab.bias.event.KeyboardEvent}. 
	 */
	protected void performInteraction(KeyboardEvent event) {
	}

	// APPLY TRANSFORMATION

	/**
	 * Convenience function that simply calls {@code applyTransformation(scene)}. It applies the transformation defined by
	 * the frame to the scene used to instantiated.
	 * 
	 * @see #applyTransformation(GrabberScene)
	 * @see #matrix()
	 */
	public void applyTransformation() {
		applyTransformation(gScene);
	}

	/**
	 * Convenience function that simply calls {@code applyWorldTransformation(scene)}. It applies the world transformation
	 * defined by the frame to the scene used to instantiated.
	 * 
	 * @see #applyWorldTransformation(GrabberScene)
	 * @see #worldMatrix()
	 */
	public void applyWorldTransformation() {
		applyWorldTransformation(gScene);
	}

	/**
	 * Convenience function that simply calls {@code scn.applyTransformation(this)}. You may apply the transformation
	 * represented by this frame to any scene you want using this method.
	 * <p>
	 * Very efficient prefer always this than
	 * 
	 * @see #applyTransformation()
	 * @see #matrix()
	 * @see remixlab.dandelion.core.GrabberScene#applyTransformation(Frame)
	 */
	public void applyTransformation(GrabberScene scn) {
		scn.applyTransformation(this);
	}

	/**
	 * Convenience function that simply calls {@code scn.applyWorldTransformation(this)}. You may apply the world
	 * transformation represented by this frame to any scene you want using this method.
	 * 
	 * @see #applyWorldTransformation()
	 * @see #worldMatrix()
	 * @see remixlab.dandelion.core.GrabberScene#applyWorldTransformation(Frame)
	 */
	public void applyWorldTransformation(GrabberScene scn) {
		scn.applyWorldTransformation(this);
	}

	// MODIFIED

	/**
	 * Internal use. Automatically call by all methods which change the Frame state.
	 */
	protected void modified() {
		if (gScene != null)
			lastUpdate = gScene.frameCount();
		super.modified();
	}

	/**
	 * Internal use. Needed by {@link #sync(Frame, Frame, boolean)}.
	 */
	// TODO decide whether to include this one since it now seems overkill to me -jp
	protected long lastGlobalUpdate() {
		return lastUpdate() + gScene.deltaCount;
	}

	/**
	 * @return the last frame the Frame was updated.
	 */
	public long lastUpdate() {
		return lastUpdate;
	}

	// SYNC

	/**
	 * Same as {@code sync(this, otherFrame)}.
	 * 
	 * @see #sync(GrabberFrame, GrabberFrame)
	 */
	public void sync(GrabberFrame otherFrame) {
		sync(this, otherFrame);
	}

	/**
	 * If {@code f1} has been more recently updated than {@code f2}, calls {@code f2.fromFrame(f1)}, otherwise calls
	 * {@code f1.fromFrame(f2)}.
	 * <p>
	 * This method syncs only the global parameters ({@link #position()}, {@link #orientation()} and {@link #magnitude()})
	 * among the two frames. The {@link #referenceFrame()} and {@link #constraint()} (if any) of each frame are kept
	 * separately.
	 * 
	 * @see #fromFrame(Frame)
	 */
	public static void sync(GrabberFrame f1, GrabberFrame f2) {
		if(f1 == null || f2 == null)
			return;
		if (f1.lastGlobalUpdate() == f2.lastGlobalUpdate())
			return;
		GrabberFrame source = (f1.lastGlobalUpdate() > f2.lastGlobalUpdate()) ? f1 : f2;
		GrabberFrame target = (f1.lastGlobalUpdate() > f2.lastGlobalUpdate()) ? f2 : f1;
		target.fromFrame(source);
	}

	// Fx

	/**
	 * Internal use.
	 * <p>
	 * Returns the cached value of the spinning friction used in {@link #recomputeSpinningRotation()}.
	 */
	protected float dampingFx() {
		return sFriction;
	}

	/**
	 * Defines the spinning deceleration.
	 * <p>
	 * Default value is 0.5. Use {@link #setDamping(float)} to tune this value. A higher value will make damping more
	 * difficult (a value of 1.0 forbids damping).
	 */
	public float damping() {
		return dampFriction;
	}

	/**
	 * Defines the {@link #damping()}. Values must be in the range [0..1].
	 */
	public void setDamping(float f) {
		if (f < 0 || f > 1)
			return;
		dampFriction = f;
		setDampingFx(dampFriction);
	}

	/**
	 * Internal use.
	 * <p>
	 * Computes and caches the value of the spinning friction used in {@link #recomputeSpinningRotation()}.
	 */
	protected void setDampingFx(float spinningFriction) {
		sFriction = spinningFriction * spinningFriction * spinningFriction;
	}

	/**
	 * Defines the {@link #rotationSensitivity()}.
	 */
	public final void setRotationSensitivity(float sensitivity) {
		rotSensitivity = sensitivity;
	}

	/**
	 * Defines the {@link #scalingSensitivity()}.
	 */
	public final void setScalingSensitivity(float sensitivity) {
		sclSensitivity = sensitivity;
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
	 * Defines the {@link #keyboardSensitivity()}.
	 */
	public final void setKeyboardSensitivity(float sensitivity) {
		keySensitivity = sensitivity;
	}

	/**
	 * Returns the influence of a gesture displacement on the grabber-frame rotation.
	 * <p>
	 * Default value is 1.0 (which matches an identical mouse displacement), a higher value will generate a larger
	 * rotation (and inversely for lower values). A 0.0 value will forbid rotation (see also {@link #constraint()}).
	 * 
	 * @see #setRotationSensitivity(float)
	 * @see #translationSensitivity()
	 * @see #scalingSensitivity()
	 * @see #keyboardSensitivity()
	 * @see #spinningSensitivity()
	 * @see #wheelSensitivity()
	 */
	public final float rotationSensitivity() {
		return rotSensitivity;
	}

	/**
	 * Returns the influence of a gesture displacement on the grabber-frame scaling.
	 * <p>
	 * Default value is 1.0, a higher value will generate a larger scaling (and inversely for lower values). A 0.0 value
	 * will forbid scaling (see also {@link #constraint()}).
	 * 
	 * @see #setScalingSensitivity(float)
	 * @see #setRotationSensitivity(float)
	 * @see #translationSensitivity()
	 * @see #keyboardSensitivity()
	 * @see #spinningSensitivity()
	 * @see #wheelSensitivity()
	 */
	public final float scalingSensitivity() {
		return sclSensitivity;
	}

	/**
	 * Returns the influence of a gesture displacement on the grabber-frame translation.
	 * <p>
	 * Default value is 1.0 which in the case of a mouse interaction makes the grabber-frame precisely stays under the
	 * mouse cursor.
	 * <p>
	 * With an identical gesture displacement, a higher value will generate a larger translation (and inversely for lower
	 * values). A 0.0 value will forbid translation (see also {@link #constraint()}).
	 * 
	 * @see #setTranslationSensitivity(float)
	 * @see #rotationSensitivity()
	 * @see #scalingSensitivity()
	 * @see #keyboardSensitivity()
	 * @see #spinningSensitivity()
	 * @see #wheelSensitivity()
	 */
	public final float translationSensitivity() {
		return transSensitivity;
	}

	/**
	 * Returns the minimum gesture speed required to make the grabber-frame {@link #spin()}. Spinning requires to set
	 * to {@link #damping()} to 0.
	 * <p>
	 * See  {@link #spin()}, {@link #spinningRotation()} and {@link #startSpinning(MotionEvent, Rotation)} for details.
	 * <p>
	 * Gesture speed is expressed in pixels per milliseconds. Default value is 0.3 (300 pixels per second). Use
	 * {@link #setSpinningSensitivity(float)} to tune this value. A higher value will make spinning more difficult (a
	 * value of 100.0 forbids spinning in practice).
	 * 
	 * @see #setSpinningSensitivity(float)
	 * @see #translationSensitivity()
	 * @see #rotationSensitivity()
	 * @see #scalingSensitivity()
	 * @see #keyboardSensitivity()
	 * @see #wheelSensitivity()
	 * @see #setDamping(float)
	 */
	public final float spinningSensitivity() {
		return spngSensitivity;
	}

	/**
	 * Returns the wheel sensitivity.
	 * <p>
	 * Default value is 5.0. A higher value will make the wheel action more efficient (usually meaning faster motion). Use
	 * a negative value to invert the operation direction.
	 * 
	 * @see #setWheelSensitivity(float)
	 * @see #translationSensitivity()
	 * @see #rotationSensitivity()
	 * @see #scalingSensitivity()
	 * @see #keyboardSensitivity()
	 * @see #spinningSensitivity()
	 */
	public float wheelSensitivity() {
		return wheelSensitivity;
	}

	/**
	 * Returns the keyboard sensitivity.
	 * <p>
	 * Default value is 1.0. A higher value will make the keyboard more efficient (usually meaning faster motion).
	 * 
	 * @see #setKeyboardSensitivity(float)
	 * @see #translationSensitivity()
	 * @see #rotationSensitivity()
	 * @see #scalingSensitivity()
	 * @see #wheelSensitivity()
	 * @see #setDamping(float)
	 */
	public float keyboardSensitivity() {
		return keySensitivity;
	}

	/**
	 * Returns {@code true} when the grabber-frame is spinning.
	 * <p>
	 * During spinning, {@link #spin()} rotates the grabber-frame by its {@link #spinningRotation()} at a frequency
	 * defined when the grabber-frame {@link #startSpinning(MotionEvent, Rotation)}.
	 * <p>
	 * Use {@link #startSpinning(MotionEvent, Rotation)} and {@link #stopSpinning()} to change this state. Default value is
	 * {@code false}.
	 * 
	 * @see #isFlying()
	 */
	public final boolean isSpinning() {
		return spinningTimerTask.isActive();
	}

	/**
	 * Returns the incremental rotation that is applied by {@link #spin()} to the grabber-frame orientation when it
	 * {@link #isSpinning()}.
	 * <p>
	 * Default value is a {@code null} rotation. Use {@link #setSpinningRotation(Rotation)} to change this value.
	 * <p>
	 * The {@link #spinningRotation()} axis is defined in the grabber-frame coordinate system. You can use
	 * {@link remixlab.dandelion.geom.Frame#transformOfFrom(Vec, Frame)} to convert this axis from another Frame
	 * coordinate system.
	 * <p>
	 * <b>Attention: </b>Spinning may be decelerated according to {@link #damping()} till it stops completely.
	 * 
	 * @see #flyDirection()
	 */
	public final Rotation spinningRotation() {
		return spngRotation;
	}

	/**
	 * Defines the {@link #spinningRotation()}. Its axis is defined in the grabber-frame coordinate system.
	 * 
	 * @see #setFlyDirection(Vec)
	 */
	public final void setSpinningRotation(Rotation spinningRotation) {
		spngRotation = spinningRotation;
	}

	/**
	 * Stops the spinning motion started using {@link #startSpinning(MotionEvent, Rotation)}. {@link #isSpinning()} will return
	 * {@code false} after this call.
	 * <p>
	 * <b>Attention: </b>This method may be called by {@link #spin()}, since spinning may be decelerated according to
	 * {@link #damping()} till it stops completely.
	 * 
	 * @see #damping()
	 */
	public final void stopSpinning() {
		spinningTimerTask.stop();
	}

	/**
	 * Internal use. Same as {@code startSpinning(rt, event.speed(), event.delay())}.
	 * 
	 * @see #startFlying(MotionEvent, Vec)
	 * @see #startSpinning(Rotation, float, long)
	 */
	protected void startSpinning(MotionEvent event, Rotation rt) {
		startSpinning(rt, event.speed(), event.delay());
	}

	/**
	 * Starts the spinning of the grabber-frame.
	 * <p>
	 * This method starts a timer that will call {@link #spin()} every {@code updateInterval} milliseconds. The
	 * grabber-frame {@link #isSpinning()} until you call {@link #stopSpinning()}.
	 * <p>
	 * <b>Attention: </b>Spinning may be decelerated according to {@link #damping()} till it stops completely.
	 * 
	 * @see #damping()
	 * @see #startFlying(Vec, float)
	 */
	public void startSpinning(Rotation rt, float speed, long delay) {
		setSpinningRotation(rt);
		eventSpeed = speed;
		if (Util.zero(damping()) && eventSpeed < spinningSensitivity())
			return;
		int updateInterval = (int) delay;
		if (updateInterval > 0)
			spinningTimerTask.run(updateInterval);
	}

	protected void spinExecution() {
		if (Util.zero(damping()))
			spin();
		else {
			if (eventSpeed == 0) {
				stopSpinning();
				return;
			}
			spin();
			recomputeSpinningRotation();
		}
	}

	protected void spin(Rotation rt, float speed, long delay) {
		if (Util.zero(damping()))
			spin(rt);
		else
			startSpinning(rt, speed, delay);
	}

	protected void spin(Rotation rt) {
		setSpinningRotation(rt);
		spin();
	}

	/**
	 * Rotates the scene-frame by its {@link #spinningRotation()} or around the
	 * {@link remixlab.dandelion.core.Eye#anchor()} when this scene-frame is the
	 * {@link remixlab.dandelion.core.GrabberScene#eye()}. Called by a timer when the grabber-frame
	 * {@link #isSpinning()}.
	 * <p>
	 * <b>Attention: </b>Spinning may be decelerated according to {@link #damping()} till it stops completely.
	 * 
	 * @see #damping()
	 */
	protected void spin() {
		if (isEyeFrame())
			rotateAroundPoint(spinningRotation(), eye().anchor());
		else
			rotate(spinningRotation());
	}

	/**
	 * Internal method. Recomputes the {@link #spinningRotation()} according to {@link #damping()}.
	 * 
	 * @see #recomputeFlyDirection()
	 */
	protected void recomputeSpinningRotation() {
		float prevSpeed = eventSpeed;
		float damping = 1.0f - dampingFx();
		eventSpeed *= damping;
		if (Math.abs(eventSpeed) < .001f)
			eventSpeed = 0;
		// float currSpeed = eventSpeed;
		if (gScene.is3D())
			((Quat) spinningRotation()).fromAxisAngle(((Quat) spinningRotation()).axis(), spinningRotation().angle()
					* (eventSpeed / prevSpeed));
		else
			this.setSpinningRotation(new Rot(spinningRotation().angle() * (eventSpeed / prevSpeed)));
	}

	protected int originalDirection(MotionEvent event) {
		return originalDirection(event, true);
	}

	protected int originalDirection(MotionEvent event, boolean fromX) {
		DOF2Event dof2Event = MotionEvent.dof2Event(event, fromX);
		if (dof2Event != null)
			return originalDirection(dof2Event);
		else {
			GrabberScene.showMinDOFsWarning("originalDirection", 2);
			return 0;
		}
	}

	/**
	 * Return 1 if mouse motion was started horizontally and -1 if it was more vertical. Returns 0 if this could not be
	 * determined yet (perfect diagonal motion, rare).
	 */
	protected int originalDirection(DOF2Event event) {
		if (!dirIsFixed) {
			Point delta = new Point(event.dx(), event.dy());
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

	/**
	 * Returns a Rotation computed according to the mouse motion. Mouse positions are projected on a deformed ball,
	 * centered on ({@code center.x()}, {@code center.y()}).
	 */
	public Rotation deformedBallRotation(DOF2Event event, Vec center) {
		if (event.isAbsolute()) {
			GrabberScene.showEventVariationWarning("deformedBallRotation");
			return null;
		}
		if (gScene.is2D()) {
			Rot rt;
			Point prevPos = new Point(event.prevX(), event.prevY());
			Point curPos = new Point(event.x(), event.y());
			rt = new Rot(new Point(center.x(), center.y()), prevPos, curPos);
			rt = new Rot(rt.angle() * rotationSensitivity());
			if ((gScene.isRightHanded() && !isEyeFrame()) || (gScene.isLeftHanded() && isEyeFrame()))
				rt.negate();
			return rt;
		}
		else {
			float cx = center.x();
			float cy = center.y();
			float x = event.x();
			float y = event.y();
			float prevX = event.prevX();
			float prevY = event.prevY();
			// Points on the deformed ball
			float px = rotationSensitivity() * ((int) prevX - cx) / gScene.camera().screenWidth();
			float py = rotationSensitivity() * (gScene.isLeftHanded() ? ((int) prevY - cy) : (cy - (int) prevY))
					/ gScene.camera().screenHeight();
			float dx = rotationSensitivity() * (x - cx) / gScene.camera().screenWidth();
			float dy = rotationSensitivity() * (gScene.isLeftHanded() ? (y - cy) : (cy - y)) / gScene.camera().screenHeight();

			Vec p1 = new Vec(px, py, projectOnBall(px, py));
			Vec p2 = new Vec(dx, dy, projectOnBall(dx, dy));
			// Approximation of rotation angle Should be divided by the projectOnBall size, but it is 1.0
			Vec axis = p2.cross(p1);
			float angle = 2.0f * (float) Math
					.asin((float) Math.sqrt(axis.squaredNorm() / p1.squaredNorm() / p2.squaredNorm()));
			return new Quat(axis, angle);
		}
	}

	/**
	 * Returns "pseudo-distance" from (x,y) to ball of radius size. For a point inside the ball, it is proportional to the
	 * euclidean distance to the ball. For a point outside the ball, it is proportional to the inverse of this distance
	 * (tends to zero) on the ball, the function is continuous.
	 */
	protected float projectOnBall(float x, float y) {
		// If you change the size value, change angle computation in deformedBallQuaternion().
		float size = 1.0f;
		float size2 = size * size;
		float size_limit = size2 * 0.5f;

		float d = x * x + y * y;
		return d < size_limit ? (float) Math.sqrt(size2 - d) : size_limit / (float) Math.sqrt(d);
	}

	// macro's

	protected float computeAngle(DOF1Event e1) {
		return computeAngle(e1.dx());
	}

	protected float computeAngle(KeyboardEvent e1) {
		return computeAngle(1);
	}

	protected float computeAngle(float dx) {
		return dx * (float) Math.PI / gScene.eye().screenWidth();
	}

	protected boolean wheel(MotionEvent event) {
		return event instanceof DOF1Event;
	}

	//

	/**
	 * Wrapper method for {@link #alignWithFrame(Frame, boolean, float)} that discriminates between eye and
	 * non-eye frames.
	 */
	public void align() {
		if (isEyeFrame())
			alignWithFrame(null, true);
		else
			alignWithFrame(gScene.eye().frame());
	}

	/**
	 * Centers the grabber-frame into the scene.
	 */
	public void center() {
		if (isEyeFrame())
			eye().centerScene();
		else
			projectOnLine(gScene.eye().position(), gScene.eye().viewDirection());
	}

	//

	/**
	 * User gesture into x-translation conversion routine.
	 */
	protected void gestureTranslateX(MotionEvent event) {
		gestureTranslateX(event, true);
	}

	/**
	 * User gesture into x-translation conversion routine.
	 */
	protected void gestureTranslateX(MotionEvent event, boolean fromX) {
		DOF1Event dof1Event = MotionEvent.dof1Event(event, fromX);
		if (dof1Event != null)
			gestureTranslateX(dof1Event, wheel(event) ? this.wheelSensitivity() : this.translationSensitivity());
	}

	/**
	 * User gesture into x-translation conversion routine.
	 */
	protected void gestureTranslateX(DOF1Event event, float sens) {
		translate(screenToVec(Vec.multiply(new Vec(isEyeFrame() ? -event.dx() : event.dx(), 0, 0), sens)));
	}

	/**
	 * User gesture into x-translation conversion routine.
	 */
	protected void gestureTranslateX(KeyboardEvent event, boolean right) {
		translate(screenToVec(Vec.multiply(new Vec(1, 0), (right ^ this.isEyeFrame()) ? keyboardSensitivity()
				: -keyboardSensitivity())));
	}

	/**
	 * User gesture into x-translation conversion routine.
	 */
	protected void gestureTranslateY(MotionEvent event) {
		gestureTranslateY(event, false);
	}

	/**
	 * User gesture into y-translation conversion routine.
	 */
	protected void gestureTranslateY(MotionEvent event, boolean fromX) {
		DOF1Event dof1Event = MotionEvent.dof1Event(event, fromX);
		if (dof1Event != null)
			gestureTranslateY(dof1Event, wheel(event) ? this.wheelSensitivity() : this.translationSensitivity());
	}

	/**
	 * User gesture into y-translation conversion routine.
	 */
	protected void gestureTranslateY(DOF1Event event, float sens) {
		translate(screenToVec(Vec.multiply(new Vec(0, isEyeFrame() ^ gScene.isRightHanded() ? -event.dx() : event.dx()),
				sens)));
	}

	/**
	 * User gesture into y-translation conversion routine.
	 */
	protected void gestureTranslateY(KeyboardEvent event, boolean up) {
		translate(screenToVec(Vec.multiply(new Vec(0, (up ^ this.isEyeFrame() ^ gScene.isLeftHanded()) ? 1 : -1),
				this.keyboardSensitivity())));
	}

	/**
	 * User gesture into z-translation conversion routine.
	 */
	protected void gestureTranslateZ(MotionEvent event) {
		gestureTranslateZ(event, true);
	}

	/**
	 * User gesture into z-translation conversion routine.
	 */
	protected void gestureTranslateZ(MotionEvent event, boolean fromX) {
		if (gScene.is2D()) {
			GrabberScene.showDepthWarning("gestureTranslateZ");
			return;
		}
		DOF1Event dof1Event = MotionEvent.dof1Event(event, fromX);
		if (dof1Event != null)
			gestureTranslateZ(dof1Event, wheel(event) ? this.wheelSensitivity() : this.translationSensitivity());
	}

	/**
	 * User gesture into z-translation conversion routine.
	 */
	protected void gestureTranslateZ(DOF1Event event, float sens) {
		translate(screenToVec(Vec.multiply(new Vec(0.0f, 0.0f, isEyeFrame() ? -event.dx() : event.dx()), sens)));
	}

	/**
	 * User gesture into z-translation conversion routine.
	 */
	protected void gestureTranslateZ(KeyboardEvent event, boolean up) {
		if (gScene.is2D()) {
			GrabberScene.showDepthWarning("gestureTranslateZ");
			return;
		}
		translate(screenToVec(Vec.multiply(new Vec(0.0f, 0.0f, 1), (up ^ this.isEyeFrame()) ? -keyboardSensitivity()
				: keyboardSensitivity())));
	}

	/**
	 * User gesture into xy-translation conversion routine.
	 */
	protected void gestureTranslateXY(MotionEvent event) {
		gestureTranslateXY(event, true);
	}

	/**
	 * User gesture into xy-translation conversion routine.
	 */
	protected void gestureTranslateXY(MotionEvent event, boolean fromX) {
		DOF2Event dof2Event = MotionEvent.dof2Event(event, fromX);
		if (dof2Event != null)
			gestureTranslateXY(dof2Event);
		else
			GrabberScene.showMinDOFsWarning("gestureTranslateXY", 2);
	}

	/**
	 * User gesture into xy-translation conversion routine.
	 */
	protected void gestureTranslateXY(DOF2Event event) {
		translate(screenToVec(Vec.multiply(new Vec(isEyeFrame() ? -event.dx() : event.dx(),
				(gScene.isRightHanded() ^ isEyeFrame()) ? -event.dy() : event.dy(), 0.0f), this.translationSensitivity())));
	}

	/**
	 * User gesture into xyz-translation conversion routine.
	 */
	protected void gestureTranslateXYZ(MotionEvent event) {
		if (gScene.is2D()) {
			GrabberScene.showDepthWarning("gestureTranslateXYZ");
			return;
		}
		DOF3Event dof3Event = MotionEvent.dof3Event(event, true);
		if (dof3Event != null)
			gestureTranslateXYZ(dof3Event);
		else
			GrabberScene.showMinDOFsWarning("gestureTranslateXYZ", 3);
	}

	/**
	 * User gesture into xyz-translation conversion routine.
	 */
	protected void gestureTranslateXYZ(DOF3Event event) {
		translate(screenToVec(Vec.multiply(
				new Vec(event.dx(), gScene.isRightHanded() ? -event.dy() : event.dy(), -event.dz()),
				this.translationSensitivity())));
	}

	/**
	 * User gesture into zoom-on-anchor conversion routine.
	 * 
	 * @see remixlab.dandelion.core.Eye#anchor()
	 */
	protected void gestureZoomOnAnchor(MotionEvent event) {
		gestureZoomOnAnchor(event, true);
	}

	/**
	 * User gesture into zoom-on-anchor conversion routine.
	 * 
	 * @see remixlab.dandelion.core.Eye#anchor()
	 */
	protected void gestureZoomOnAnchor(MotionEvent event, boolean fromX) {
		DOF1Event dof1Event = MotionEvent.dof1Event(event, fromX);
		if (dof1Event != null)
			gestureZoomOnAnchor(dof1Event, wheel(event) ? this.wheelSensitivity() : this.translationSensitivity());
	}

	/**
	 * User gesture into zoom-on-anchor conversion routine.
	 * 
	 * @see remixlab.dandelion.core.Eye#anchor()
	 */
	protected void gestureZoomOnAnchor(DOF1Event event, float sens) {
		Vec direction = Vec.subtract(gScene.eye().anchor(), position());
		if (referenceFrame() != null)
			direction = referenceFrame().transformOf(direction);
		float delta = event.dx() * sens / gScene.eye().screenHeight();
		if (direction.magnitude() > 0.02f * gScene.radius() || delta > 0.0f)
			translate(Vec.multiply(direction, delta));
	}

	/**
	 * User gesture into zoom-on-anchor conversion routine.
	 * 
	 * @see remixlab.dandelion.core.Eye#anchor()
	 */
	protected void gestureZoomOnAnchor(KeyboardEvent event, boolean in) {
		Vec direction = Vec.subtract(gScene.eye().anchor(), position());
		if (referenceFrame() != null)
			direction = referenceFrame().transformOf(direction);
		float delta = (in ? keyboardSensitivity() : -keyboardSensitivity()) / gScene.eye().screenHeight();
		if (direction.magnitude() > 0.02f * gScene.radius() || delta > 0.0f)
			translate(Vec.multiply(direction, delta));
	}

	/**
	 * User gesture into zoom-on-region conversion routine.
	 */
	protected void gestureZoomOnRegion(MotionEvent event) {
		if (!isEyeFrame()) {
			GrabberScene.showOnlyEyeWarning("gestureZoomOnRegion");
		}
		if (event.isAbsolute()) {
			GrabberScene.showEventVariationWarning("gestureZoomOnRegion");
			return;
		}
		DOF2Event dof2 = MotionEvent.dof2Event(event);
		if (dof2 == null) {
			GrabberScene.showMinDOFsWarning("gestureZoomOnRegion", 2);
			return;
		}
		gestureZoomOnRegion(dof2);
	}

	/**
	 * User gesture into zoom-on-region conversion routine.
	 */
	protected void gestureZoomOnRegion(DOF2Event event) {
		int w = (int) Math.abs(event.dx());
		int tlX = (int) event.prevX() < (int) event.x() ? (int) event.prevX() : (int) event.x();
		int h = (int) Math.abs(event.dy());
		int tlY = (int) event.prevY() < (int) event.y() ? (int) event.prevY() : (int) event.y();
		eye().interpolateToZoomOnRegion(new Rect(tlX, tlY, w, h));
	}

	/**
	 * User gesture into x-rotation conversion routine.
	 */
	protected void gestureRotateX(MotionEvent event) {
		gestureRotateX(event, false);
	}

	/**
	 * User gesture into x-rotation conversion routine.
	 */
	protected void gestureRotateX(MotionEvent event, boolean fromX) {
		if (gScene.is2D()) {
			GrabberScene.showDepthWarning("gestureRotateX");
			return;
		}
		DOF1Event dof1Event = MotionEvent.dof1Event(event, fromX);
		if (dof1Event != null)
			gestureRotateX(dof1Event, wheel(event) ? this.wheelSensitivity() : this.rotationSensitivity());
	}

	/**
	 * User gesture into x-rotation conversion routine.
	 */
	protected void gestureRotateX(DOF1Event event, float sens) {
		if (gScene.is2D()) {
			GrabberScene.showDepthWarning("gestureRotateX");
			return;
		}
		spin(screenToQuat(computeAngle(event) * (isEyeFrame() ? -sens : sens), 0, 0), event.speed(), event.delay());
	}

	/**
	 * User gesture into x-rotation conversion routine.
	 */
	protected void gestureRotateX(KeyboardEvent event, boolean up) {
		if (gScene.is2D()) {
			GrabberScene.showDepthWarning("gestureRotateX");
			return;
		}
		rotate(screenToQuat(computeAngle(event) * (up ? keyboardSensitivity() : -keyboardSensitivity()), 0, 0));
	}

	/**
	 * User gesture into y-rotation conversion routine.
	 */
	protected void gestureRotateY(MotionEvent event) {
		gestureRotateY(event, true);
	}

	/**
	 * User gesture into y-rotation conversion routine.
	 */
	protected void gestureRotateY(MotionEvent event, boolean fromX) {
		if (gScene.is2D()) {
			GrabberScene.showDepthWarning("gestureRotateY");
			return;
		}
		DOF1Event dof1Event = MotionEvent.dof1Event(event, fromX);
		if (dof1Event != null)
			gestureRotateY(dof1Event, wheel(event) ? this.wheelSensitivity() : this.rotationSensitivity());
	}

	/**
	 * User gesture into y-rotation conversion routine.
	 */
	protected void gestureRotateY(DOF1Event event, float sens) {
		if (gScene.is2D()) {
			GrabberScene.showDepthWarning("gestureRotateY");
			return;
		}
		spin(screenToQuat(0, computeAngle(event) * (isEyeFrame() ? -sens : sens), 0), event.speed(), event.delay());
	}

	/**
	 * User gesture into y-rotation conversion routine.
	 */
	protected void gestureRotateY(KeyboardEvent event, boolean up) {
		if (gScene.is2D()) {
			GrabberScene.showDepthWarning("gestureRotateY");
			return;
		}
		Rotation rt = screenToQuat(0, computeAngle(event) * (up ? keyboardSensitivity() : -keyboardSensitivity()), 0);
		rotate(rt);
	}

	/**
	 * User gesture into z-rotation conversion routine.
	 */
	protected void gestureRotateZ(MotionEvent event) {
		gestureRotateZ(event, false);
	}

	/**
	 * User gesture into z-rotation conversion routine.
	 */
	protected void gestureRotateZ(MotionEvent event, boolean fromX) {
		DOF1Event dof1Event = MotionEvent.dof1Event(event);
		if (dof1Event != null)
			gestureRotateZ(dof1Event, wheel(event) ? this.wheelSensitivity() : this.rotationSensitivity());
	}

	/**
	 * User gesture into z-rotation conversion routine.
	 */
	protected void gestureRotateZ(DOF1Event event, float sens) {
		Rotation rt;
		if (isEyeFrame())
			if (is2D())
				rt = new Rot(sens * (gScene.isRightHanded() ? computeAngle(event) : -computeAngle(event)));
			else
				rt = screenToQuat(0, 0, sens * -computeAngle(event));
		else if (is2D())
			rt = new Rot(sens * (gScene.isRightHanded() ? -computeAngle(event) : computeAngle(event)));
		else
			rt = screenToQuat(0, 0, sens * computeAngle(event));
		spin(rt, event.speed(), event.delay());
	}

	/**
	 * User gesture into z-rotation conversion routine.
	 */
	protected void gestureRotateZ(KeyboardEvent event, boolean up) {
		Rotation rt;
		if (is2D())
			rt = new Rot(computeAngle(event) * (up ? keyboardSensitivity() : -keyboardSensitivity()));
		else
			rt = screenToQuat(0, 0, computeAngle(event) * (up ? keyboardSensitivity() : -keyboardSensitivity()));
		rotate(rt);
	}

	/**
	 * User gesture into xyz-rotation conversion routine.
	 */
	protected void gestureRotateXYZ(MotionEvent event) {
		if (gScene.is2D()) {
			GrabberScene.showDepthWarning("gestureRotateXYZ");
			return;
		}
		DOF3Event dof3Event = MotionEvent.dof3Event(event, false);
		if (dof3Event != null)
			gestureRotateXYZ(dof3Event);
		else
			GrabberScene.showMinDOFsWarning("gestureRotateXYZ", 2);
	}

	/**
	 * User gesture into xyz-rotation conversion routine.
	 */
	protected void gestureRotateXYZ(DOF3Event event) {
		rotate(screenToQuat(Vec.multiply(
				new Vec(computeAngle(event.dx()), computeAngle(-event.dy()), computeAngle(-event.dz())), rotationSensitivity())));
	}

	/**
	 * User gesture into arcball-rotation conversion routine.
	 */
	protected void gestureArcball(MotionEvent event) {
		DOF2Event dof2Event = MotionEvent.dof2Event(event);
		if (dof2Event != null)
			gestureArcball(dof2Event);
		else
			GrabberScene.showMinDOFsWarning("arcball", 2);
	}

	/**
	 * User gesture into arcball-rotation conversion routine.
	 */
	protected void gestureArcball(DOF2Event event) {
		if (event.isAbsolute()) {
			GrabberScene.showEventVariationWarning("deformedBallRotation");
			return;
		}
		Rotation rt;
		Vec trns;
		if (isEyeFrame())
			rt = deformedBallRotation(event, eye().projectedCoordinatesOf(eye().anchor()));
		else {
			if (is2D())
				rt = deformedBallRotation(event, gScene.window().projectedCoordinatesOf(position()));
			else {
				trns = gScene.camera().projectedCoordinatesOf(position());
				rt = deformedBallRotation(event, trns);
				trns = ((Quat) rt).axis();
				trns = gScene.camera().frame().orientation().rotate(trns);
				trns = transformOf(trns);
				rt = new Quat(trns, -rt.angle());
			}
		}
		spin(rt, event.speed(), event.delay());
	}

	/**
	 * User gesture into scaling conversion routine.
	 */
	protected void gestureScale(MotionEvent event) {
		DOF1Event dof1Event = MotionEvent.dof1Event(event);
		if (dof1Event != null)
			gestureScale(dof1Event, wheel(event) ? wheelSensitivity() : scalingSensitivity());
	}

	/**
	 * User gesture into scaling conversion routine.
	 */
	protected void gestureScale(DOF1Event event, float sens) {
		if (isEyeFrame()) {
			float delta = event.dx() * sens;
			float s = 1 + Math.abs(delta) / (float) -gScene.height();
			scale(delta >= 0 ? s : 1 / s);
		}
		else {
			float delta = event.dx() * sens;
			float s = 1 + Math.abs(delta) / (float) gScene.height();
			scale(delta >= 0 ? s : 1 / s);
		}
	}

	/**
	 * User gesture into scaling conversion routine.
	 */
	protected void gestureScale(KeyboardEvent event, boolean up) {
		float s = 1 + Math.abs(keyboardSensitivity()) / (isEyeFrame() ? (float) -gScene.height() : (float) gScene.height());
		scale(up ? s : 1 / s);
	}

	/**
	 * User gesture into move-forward conversion routine.
	 */
	protected void gestureMoveForward(MotionEvent event) {
		gestureMoveForward(event, true);
	}

	/**
	 * User gesture into move-forward conversion routine.
	 */
	protected void gestureMoveForward(MotionEvent event, boolean forward) {
		DOF2Event dof2Event = MotionEvent.dof2Event(event);
		if (dof2Event != null)
			gestureMoveForward(dof2Event, forward);
		else
			GrabberScene.showMinDOFsWarning("moveForward", 2);
	}

	/**
	 * User gesture into move-forward conversion routine.
	 */
	protected void gestureMoveForward(DOF2Event event, boolean forward) {
		Vec trns;
		float fSpeed = forward ? -flySpeed() : flySpeed();
		if (is2D()) {
			rotate(deformedBallRotation(event, gScene.window().projectedCoordinatesOf(position())));
			flyDisp.set(-fSpeed, 0.0f, 0.0f);
			trns = localInverseTransformOf(flyDisp);
			startFlying(event, trns);
		}
		else {
			rotate(rollPitchQuaternion(event, gScene.camera()));
			flyDisp.set(0.0f, 0.0f, fSpeed);
			trns = rotation().rotate(flyDisp);
			startFlying(event, trns);
		}
	}

	/**
	 * User gesture into drive conversion routine.
	 */
	protected void gestureDrive(MotionEvent event) {
		if (gScene.is2D()) {
			GrabberScene.showDepthWarning("drive");
			return;
		}
		DOF2Event dof2Event = MotionEvent.dof2Event(event);
		if (dof2Event != null)
			gestureDrive(dof2Event);
		else
			GrabberScene.showMinDOFsWarning("drive", 2);
	}

	/**
	 * User gesture into drive conversion routine.
	 */
	protected void gestureDrive(DOF2Event event) {
		Vec trns;
		rotate(turnQuaternion(event.dof1Event(), gScene.camera()));
		flyDisp.set(0.0f, 0.0f, flySpeed());
		trns = rotation().rotate(flyDisp);
		startFlying(event, trns);
	}

	/**
	 * User gesture into CAD-rotation conversion routine.
	 */
	protected void gestureRotateCAD(MotionEvent event) {
		if (gScene.is2D()) {
			GrabberScene.showDepthWarning("gestureRotateCAD");
			return;
		}
		DOF2Event dof2Event = MotionEvent.dof2Event(event);
		if (dof2Event != null)
			gestureRotateCAD(dof2Event);
		else
			GrabberScene.showMinDOFsWarning("gestureRotateCAD", 2);
	}

	/**
	 * User gesture into CAD-rotation conversion routine.
	 */
	protected void gestureRotateCAD(DOF2Event event) {
		if (event.isAbsolute()) {
			GrabberScene.showEventVariationWarning("gestureRotateCAD");
			return;
		}
		// Multiply by 2.0 to get on average about the same speed as with the deformed ball
		float dx = -2.0f * rotationSensitivity() * event.dx() / gScene.camera().screenWidth();
		float dy = 2.0f * rotationSensitivity() * event.dy() / gScene.camera().screenHeight();
		if (((Camera) eye()).cadRotationIsReversed)
			dx = -dx;
		if (gScene.isRightHanded())
			dy = -dy;
		Vec verticalAxis = transformOf(sceneUpVector());
		spin(Quat.multiply(new Quat(verticalAxis, dx), new Quat(new Vec(1.0f, 0.0f, 0.0f), dy)), event.speed(),
				event.delay());
	}

	/**
	 * User gesture into hinge conversion routine.
	 */
	protected void gestureHinge(MotionEvent event) {
		if (gScene.is2D()) {
			GrabberScene.showDepthWarning("hinge");
			return;
		}
		if (!isEyeFrame()) {
			GrabberScene.showOnlyEyeWarning("hinge");
			return;
		}
		DOF6Event dof6Event = MotionEvent.dof6Event(event);
		if (dof6Event != null)
			gestureHinge(dof6Event);
		else
			GrabberScene.showMinDOFsWarning("hinge", 6);
	}

	/**
	 * User gesture into hinge conversion routine.
	 */
	protected void gestureHinge(DOF6Event event) {
		// aka google earth navigation
		// 1. Relate the eye reference frame:
		Vec trns = new Vec();
		Vec pos = position();
		Quat o = (Quat) orientation();
		Frame oldRef = referenceFrame();
		GrabberFrame rFrame = new GrabberFrame(gScene);
		rFrame.setPosition(eye().anchor());
		rFrame.setZAxis(Vec.subtract(pos, eye().anchor()));
		rFrame.setXAxis(xAxis());
		setReferenceFrame(rFrame);
		setPosition(pos);
		setOrientation(o);
		// 2. Translate the refFrame along its Z-axis:
		float deltaZ = event.dz();
		trns = new Vec(0, deltaZ, 0);
		screenToEye(trns);
		float pmag = trns.magnitude();
		translate(0, 0, (deltaZ > 0) ? -pmag : pmag);
		// 3. Rotate the refFrame around its X-axis -> translate forward-backward the frame on the sphere surface
		float deltaY = computeAngle(event.dy());
		rFrame.rotate(new Quat(new Vec(1, 0, 0), gScene.isRightHanded() ? deltaY : -deltaY));
		// 4. Rotate the refFrame around its Y-axis -> translate left-right the frame on the sphere surface
		float deltaX = computeAngle(event.dx());
		rFrame.rotate(new Quat(new Vec(0, 1, 0), deltaX));
		// 5. Rotate the refFrame around its Z-axis -> look around
		float rZ = computeAngle(event.drz());
		rFrame.rotate(new Quat(new Vec(0, 0, 1), gScene.isRightHanded() ? -rZ : rZ));
		// 6. Rotate the frame around x-axis -> move head up and down :P
		float rX = computeAngle(event.drx());
		Quat q = new Quat(new Vec(1, 0, 0), gScene.isRightHanded() ? rX : -rX);
		rotate(q);
		// 7. Unrelate the frame and restore state:
		pos = position();
		o = (Quat) orientation();
		setReferenceFrame(oldRef);
		setPosition(pos);
		setOrientation(o);
	}

	/**
	 * User gesture screen-rotation conversion routine.
	 */
	protected void gestureScreenRotate(MotionEvent event) {
		DOF2Event dof2Event = MotionEvent.dof2Event(event);
		if (dof2Event != null)
			gestureScreenRotate(dof2Event);
		else
			GrabberScene.showMinDOFsWarning("screenRotate", 2);
	}

	/**
	 * User gesture screen-rotation conversion routine.
	 */
	protected void gestureScreenRotate(DOF2Event event) {
		if (event.isAbsolute()) {
			GrabberScene.showEventVariationWarning("gestureScreenRotate");
			return;
		}
		if (this.is2D()) {
			gestureArcball(event);
			return;
		}
		Quat rt;
		Vec trns;
		float angle;
		if (isEyeFrame()) {
			trns = eye().projectedCoordinatesOf(eye().anchor());
			angle = (float) Math.atan2(event.y() - trns.vec[1], event.x() - trns.vec[0])
					- (float) Math.atan2(event.prevY() - trns.vec[1], event.prevX() - trns.vec[0]);
			if (gScene.isLeftHanded())
				angle = -angle;
			rt = new Quat(new Vec(0.0f, 0.0f, 1.0f), angle);
			spin(rt, event.speed(), event.delay());
			// updateSceneUpVector();
		}
		else {
			trns = gScene.camera().projectedCoordinatesOf(position());
			float prev_angle = (float) Math.atan2(event.prevY() - trns.vec[1], event.prevX() - trns.vec[0]);
			angle = (float) Math.atan2(event.y() - trns.vec[1], event.x() - trns.vec[0]);
			Vec axis = transformOf(gScene.camera().frame().orientation().rotate(new Vec(0.0f, 0.0f, -1.0f)));
			if (gScene.isRightHanded())
				rt = new Quat(axis, angle - prev_angle);
			else
				rt = new Quat(axis, prev_angle - angle);
			spin(rt, event.speed(), event.delay());
		}
	}

	// Quite nice

	/**
	 * Same as {@code return screenToVec(new Vec(x, y, z))}.
	 * 
	 * @see #screenToVec(Vec)
	 */
	public Vec screenToVec(float x, float y, float z) {
		return screenToVec(new Vec(x, y, z));
	}

	/**
	 * Same as {@code return eyeToReferenceFrame(screenToEye(trns))}. Transforms the vector from screen
	 * (device) coordinates to {@link #referenceFrame()} coordinates. 
	 * 
	 * @see #screenToEye(Vec)
	 * @see #eyeToReferenceFrame(Vec)
	 */
	public Vec screenToVec(Vec trns) {
		return eyeToReferenceFrame(screenToEye(trns));
	}

	/**
	 * Same as {@code return eyeToReferenceFrame(new Vec(x, y, z))}.
	 * 
	 * @see #eyeToReferenceFrame(Vec)
	 */
	public Vec eyeToReferenceFrame(float x, float y, float z) {
		return eyeToReferenceFrame(new Vec(x, y, z));
	}

	/**
	 * Converts the vector from eye coordinates to {@link #referenceFrame()} coordinates.
	 * <p>
	 * It's worth noting that all gesture to grabber-frame motion converting methods, are implemented from just
	 * {@link #screenToEye(Vec)}, {@link #eyeToReferenceFrame(Vec)} and {@link #screenToQuat(float, float, float)}. 
	 * 
	 * @see #screenToEye(Vec)
	 * @see #screenToQuat(float, float, float)
	 */
	public Vec eyeToReferenceFrame(Vec trns) {
		GrabberFrame gFrame = isEyeFrame() ? this : /* respectToEye() ? */gScene.eye().frame() /* : this */;
		Vec t = gFrame.inverseTransformOf(trns);
		if (referenceFrame() != null)
			t = referenceFrame().transformOf(t);
		return t;
	}

	/**
	 * Same as {@code return screenToEye(new Vec(x, y, z))}.
	 * 
	 * @see #screenToEye(Vec)
	 */
	public Vec screenToEye(float x, float y, float z) {
		return screenToEye(new Vec(x, y, z));
	}

	/**
	 * Converts the vector from screen (device) coordinates into eye coordinates.
	 * <p>
	 * It's worth noting that all gesture to grabber-frame motion converting methods, are implemented from just
	 * {@link #screenToEye(Vec)}, {@link #eyeToReferenceFrame(Vec)} and {@link #screenToQuat(float, float, float)}.
	 * 
	 * @see #eyeToReferenceFrame(Vec)
	 * @see #screenToQuat(float, float, float)
	 */
	public Vec screenToEye(Vec trns) {
		Vec eyeVec = trns.get();
		// Scale to fit the screen relative event displacement
		if (gScene.is2D())
			// Quite excited to see how simple it's in 2d:
			return eyeVec;
		// ... and amazed as to how dirty it's in 3d:
		switch (gScene.camera().type()) {
		case PERSPECTIVE:
			float k = (float) Math.tan(gScene.camera().fieldOfView() / 2.0f)
					* Math.abs(gScene.camera().frame().coordinatesOf(isEyeFrame() ? eye().anchor() : position()).vec[2]
							* gScene.eye().frame().magnitude());
			// * Math.abs(scene.camera().frame().coordinatesOf(isEyeFrame() ? scene.eye().anchor() : position()).vec[2]);
			eyeVec.vec[0] *= 2.0 * k / gScene.eye().screenHeight();
			eyeVec.vec[1] *= 2.0 * k / gScene.eye().screenHeight();
			break;
		case ORTHOGRAPHIC:
			float[] wh = gScene.eye().getBoundaryWidthHeight();
			// float[] wh = scene.camera().getOrthoWidthHeight();
			eyeVec.vec[0] *= 2.0 * wh[0] / gScene.eye().screenWidth();
			eyeVec.vec[1] *= 2.0 * wh[1] / gScene.eye().screenHeight();
			break;
		}
		float coef;
		if (isEyeFrame()) {
			// float coef = 8E-4f;
			coef = Math.max(Math.abs((coordinatesOf(eye().anchor())).vec[2] * magnitude()), 0.2f * eye().sceneRadius());
			eyeVec.vec[2] *= coef / eye().screenHeight();
			// TODO eye wheel seems different
			// trns.vec[2] *= coef * 8E-4f;
			eyeVec.divide(eye().frame().magnitude());
		}
		else {
			coef = Vec.subtract(gScene.camera().position(), position()).magnitude();
			eyeVec.vec[2] *= coef / gScene.camera().screenHeight();
			eyeVec.divide(gScene.eye().frame().magnitude());
		}
		// if( isEyeFrame() )
		return eyeVec;
	}

	/**
	 * Same as {@code return screenToQuat(angles.vec[0], angles.vec[1], angles.vec[2])}.
	 * 
	 * @see #screenToQuat(float, float, float)
	 */
	public Quat screenToQuat(Vec angles) {
		return screenToQuat(angles.vec[0], angles.vec[1], angles.vec[2]);
	}

	/**
	 * Reduces the screen (device)
	 * <a href="http://en.wikipedia.org/wiki/Euler_angles#Extrinsic_rotations">Extrinsic rotation</a>
	 * into a {@link remixlab.dandelion.geom.Quat}.
	 * <p>
	 * It's worth noting that all gesture to grabber-frame motion converting methods, are implemented from just
	 * {@link #screenToEye(Vec)}, {@link #eyeToReferenceFrame(Vec)} and {@link #screenToQuat(float, float, float)}.
	 * 
	 * @param roll
	 *          Rotation angle in radians around the screen x-Axis
	 * @param pitch
	 *          Rotation angle in radians around the screen y-Axis
	 * @param yaw
	 *          Rotation angle in radians around the screen z-Axis
	 * 
	 * @see remixlab.dandelion.geom.Quat#fromEulerAngles(float, float, float)
	 */
	public Quat screenToQuat(float roll, float pitch, float yaw) {
		if (gScene.is2D()) {
			GrabberScene.showDepthWarning("gestureToQuat");
			return null;
		}

		// don't really need to differentiate among the two cases, but eyeFrame can be speeded up
		if (isEyeFrame() /* || (!isEyeFrame() && !this.respectToEye()) */) {
			return new Quat(gScene.isLeftHanded() ? -roll : roll, pitch, gScene.isLeftHanded() ? -yaw : yaw);
		}
		else {
			Vec trns = new Vec();
			Quat q = new Quat(gScene.isLeftHanded() ? roll : -roll, -pitch, gScene.isLeftHanded() ? yaw : -yaw);
			trns.set(-q.x(), -q.y(), -q.z());
			trns = gScene.camera().frame().orientation().rotate(trns);
			trns = transformOf(trns);
			q.setX(trns.x());
			q.setY(trns.y());
			q.setZ(trns.z());
			return q;
		}
	}

	@Override
	public void rotateAroundFrame(float roll, float pitch, float yaw, Frame frame) {
		if (frame != null) {
			Frame ref = frame.get();
			if (ref instanceof Grabber) {
				gScene.motionAgent().removeGrabber((Grabber) ref);
				gScene.keyboardAgent().removeGrabber((Grabber) ref);
			}
			GrabberFrame copy = get();
			gScene.motionAgent().removeGrabber((Grabber) copy);
			gScene.keyboardAgent().removeGrabber((Grabber) copy);
			copy.setReferenceFrame(ref);
			copy.fromFrame(this);
			ref.rotate(new Quat(gScene.isLeftHanded() ? -roll : roll, pitch, gScene.isLeftHanded() ? -yaw : yaw));
			fromFrame(copy);
			return;
		}
	}

	/**
	 * Returns the up vector used in {@link #gestureMoveForward(MotionEvent)} in which horizontal displacements of the
	 * motion device (e.g., mouse) rotate grabber-frame around this vector. Vertical displacements rotate always
	 * around the grabber-frame {@code X} axis.
	 * <p>
	 * This value is also used within {@link #gestureRotateCAD(MotionEvent)} to define the up vector (and incidentally
	 * the 'horizon' plane) around which the grabber-frame will rotate.
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
	 * Returns {@code true} when the grabber-frame is tossing.
	 * <p>
	 * During tossing, {@link #damping()} translates the grabber-frame by its {@link #flyDirection()} at a
	 * frequency defined when the grabber-frame {@link #startFlying(MotionEvent, Vec)}.
	 * <p>
	 * Use {@link #startFlying(MotionEvent, Vec)} and {@link #stopFlying()} to change this state. Default value is
	 * {@code false}.
	 * 
	 * {@link #isSpinning()}
	 */
	public final boolean isFlying() {
		return flyTimerTask.isActive();
	}

	/**
	 * Stops the tossing motion started using {@link #startFlying(MotionEvent, Vec)}. {@link #isFlying()} will return
	 * {@code false} after this call.
	 * <p>
	 * <b>Attention: </b>This method may be called by {@link #damping()}, since tossing may be decelerated according to
	 * {@link #damping()} till it stops completely.
	 * 
	 * @see #damping()
	 * @see #spin()
	 */
	public final void stopFlying() {
		flyTimerTask.stop();
	}

	/**
	 * Returns the incremental translation that is applied by {@link #damping()} to the grabber-frame position when
	 * it {@link #isFlying()}.
	 * <p>
	 * Default value is no translation. Use {@link #setFlyDirection(Vec)} to change this value.
	 * <p>
	 * <b>Attention: </b>Tossing may be decelerated according to {@link #damping()} till it stops completely.
	 * 
	 * @see #spinningRotation()
	 */
	public final Vec flyDirection() {
		return fDir;
	}

	/**
	 * Defines the {@link #flyDirection()} in the reference frame coordinate system.
	 * 
	 * @see #setSpinningRotation(Rotation)
	 */
	public final void setFlyDirection(Vec dir) {
		fDir = dir;
	}

	/**
	 * Internal use. Same as {@code startFlying(direction, event.speed())}.
	 * 
	 * @see #startFlying(Vec, float)
	 * @see #startSpinning(MotionEvent, Rotation)
	 */
	protected void startFlying(MotionEvent event, Vec direction) {
		startFlying(direction, event.speed());
	}

	/**
	 * Starts the tossing of the grabber-frame.
	 * <p>
	 * This method starts a timer that will call {@link #damping()} every FLY_UPDATE_PERDIOD milliseconds. The
	 * grabber-frame {@link #isFlying()} until you call {@link #stopFlying()}.
	 * <p>
	 * <b>Attention: </b>Tossing may be decelerated according to {@link #damping()} till it stops completely.
	 * 
	 * @see #damping()
	 * @see #spin()
	 * @see #startFlying(MotionEvent, Vec)
	 * @see #startSpinning(Rotation, float, long)
	 */
	public void startFlying(Vec direction, float speed) {
		eventSpeed = speed;
		setFlyDirection(direction);
		flyTimerTask.run(FLY_UPDATE_PERDIOD);
	}

	/**
	 * Translates the grabber-frame by its {@link #flyDirection()}. Invoked by 
	 * {@link #gestureMoveForward(MotionEvent, boolean)} and {@link #gestureDrive(MotionEvent)}.
	 * <p>
	 * <b>Attention: </b>Tossing may be decelerated according to {@link #damping()} till it stops completely.
	 * 
	 * @see #spin()
	 */
	protected void fly() {
		translate(flyDirection());
	}

	/**
	 * Returns the fly speed, expressed in virtual scene units.
	 * <p>
	 * It corresponds to the incremental displacement that is periodically applied to the grabber-frame by
	 * {@link #gestureMoveForward(MotionEvent, boolean)}.
	 * <p>
	 * <b>Attention:</b> When the grabber-frame is set as the {@link remixlab.dandelion.core.Eye#frame()} or when it is
	 * set as the {@link remixlab.dandelion.core.GrabberScene#avatar()} (which indeed is an instance of the
	 * InteractiveAvatarFrame class), this value is set according to the
	 * {@link remixlab.dandelion.core.GrabberScene#radius()} by
	 * {@link remixlab.dandelion.core.GrabberScene#setRadius(float)}.
	 */
	public float flySpeed() {
		return flySpd;
	}

	/**
	 * Sets the {@link #flySpeed()}, defined in virtual scene units.
	 * <p>
	 * Default value is 0.0, but it is modified according to the {@link remixlab.dandelion.core.GrabberScene#radius()}
	 * when the grabber-frame is set as the {@link remixlab.dandelion.core.Eye#frame()} (which indeed is an instance of
	 * the grabber-frame class) or when the grabber-frame is set as the
	 * {@link remixlab.dandelion.core.GrabberScene#avatar()} (which indeed is an instance of the InteractiveAvatarFrame
	 * class).
	 */
	public void setFlySpeed(float speed) {
		flySpd = speed;
	}

	// --

	// TODO tossing pending, but really seems overkill

	// public final boolean isTossing() {
	// return tossTimerTask.isActive();
	// }
	//
	// public final void stopTossing() {
	// tossTimerTask.stop();
	// }
	//
	// public final Vec tossDirection() {
	// return tDir;
	// }
	//
	// public final void setTossDirection(Vec dir) {
	// tDir = dir;
	// }
	//
	// public void startTossing(MotionEvent event, Vec direction) {
	// startTossing(direction, event.speed());
	// }
	//
	// public void startTossing(Vec direction, float speed) {
	// eventSpeed = speed;
	// setTossDirection(direction);
	// tossTimerTask.run(FLY_UPDATE_PERDIOD);
	// }
	//
	// protected void recomputeTossDirection() {
	// float prevSpeed = eventSpeed;
	// float damping = 1.0f - tossDampingFx();
	// eventSpeed *= damping;
	// if (Math.abs(eventSpeed) < .001f)
	// eventSpeed = 0;
	// setTossDirection(Vec.multiply(tossDirection(), (eventSpeed / prevSpeed)));
	// }
	//
	// protected void tossExecution() {
	// if(Util.zero(tossDamping()))
	// toss();
	// else {
	// if (eventSpeed == 0) {
	// stopTossing();
	// return;
	// }
	// toss();
	// recomputeTossDirection();
	// }
	// }
	//
	// protected void toss() {
	// translate(tossDirection());
	// }

	// --

	protected Quat rollPitchQuaternion(MotionEvent event, Camera camera) {
		if (gScene.is2D()) {
			GrabberScene.showDepthWarning("rollPitchQuaternion");
			return null;
		}
		DOF2Event dof2Event = MotionEvent.dof2Event(event);
		if (dof2Event != null)
			return rollPitchQuaternion(dof2Event, camera);
		else {
			GrabberScene.showMinDOFsWarning("rollPitchQuaternion", 2);
			return null;
		}
	}

	/**
	 * Returns a Quaternion that is the composition of two rotations, inferred from the mouse roll (X axis) and pitch (
	 * {@link #sceneUpVector()} axis).
	 */
	protected Quat rollPitchQuaternion(DOF2Event event, Camera camera) {
		float deltaX = event.dx();
		float deltaY = event.dy();

		if (gScene.isRightHanded())
			deltaY = -deltaY;

		Quat rotX = new Quat(new Vec(1.0f, 0.0f, 0.0f), rotationSensitivity() * deltaY / camera.screenHeight());
		Quat rotY = new Quat(transformOf(sceneUpVector()), rotationSensitivity() * (-deltaX) / camera.screenWidth());
		return Quat.multiply(rotY, rotX);
	}

	// drive:

	/**
	 * Returns a Quaternion that is a rotation around Y-axis, proportional to the horizontal event X-displacement.
	 */
	protected Quat turnQuaternion(DOF1Event event, Camera camera) {
		float deltaX = event.dx();
		return new Quat(new Vec(0.0f, 1.0f, 0.0f), rotationSensitivity() * (-deltaX) / camera.screenWidth());
	}

	// end decide

	/**
	 * Returns the grabs input threshold which is used by the interactive frame to {@link #checkIfGrabsInput(BogusEvent)}.
	 * 
	 * @see #setGrabsInputThreshold(float)
	 */
	public float grabsInputThreshold() {
		if (isEyeFrame()) {
			GrabberScene.showOnlyEyeWarning("grabsInputThreshold", false);
			return 0;
		}
		if (adaptiveGrabsInputThreshold())
			return grabsInputThreshold * scaling() * gScene.eye().pixelToSceneRatio(position());
		return grabsInputThreshold;
	}

	/**
	 * Returns {@code true} if the {@link #checkIfGrabsInput(BogusEvent)} test is adaptive and {@code false} otherwise.
	 * 
	 * @see #setGrabsInputThreshold(float, boolean)
	 */
	public boolean adaptiveGrabsInputThreshold() {
		if (isEyeFrame()) {
			GrabberScene.showOnlyEyeWarning("adaptiveGrabsInputThreshold", false);
			return false;
		}
		return adpThreshold;
	}

	/**
	 * Convenience function that simply calls {@code setGrabsInputThreshold(threshold, false)}.
	 * 
	 * @see #setGrabsInputThreshold(float, boolean)
	 */
	public void setGrabsInputThreshold(float threshold) {
		if (isEyeFrame()) {
			GrabberScene.showOnlyEyeWarning("setGrabsInputThreshold", false);
			return;
		}
		setGrabsInputThreshold(threshold, false);
	}

	/**
	 * Sets the length of the hint that defined the {@link #checkIfGrabsInput(BogusEvent)} condition used for frame
	 * picking.
	 * <p>
	 * If {@code adaptive} is {@code false}, the {@code threshold} is expressed in pixels and directly defines the fixed
	 * length of the {@link remixlab.dandelion.core.GrabberScene#drawShooterTarget(Vec, float)}, centered at the
	 * projection of the frame origin onto the screen.
	 * <p>
	 * If {@code adaptive} is {@code true}, the {@code threshold} is expressed in object space (world units) and defines
	 * the edge length of a squared bounding box that leads to an adaptive length of the
	 * {@link remixlab.dandelion.core.GrabberScene#drawShooterTarget(Vec, float)}, centered at the projection of the
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
		if (isEyeFrame()) {
			GrabberScene.showOnlyEyeWarning("setGrabsInputThreshold", false);
			return;
		}
		if (threshold >= 0) {
			adpThreshold = adaptive;
			grabsInputThreshold = threshold;
		}
	}

	/**
	 * Check if this object is the {@link remixlab.bias.core.Agent#inputGrabber()}. Returns {@code true} if this object
	 * grabs the agent and {@code false} otherwise.
	 */
	public boolean grabsInput(Agent agent) {
		return agent.inputGrabber() == this;
	}
	
	/**
	 * Checks if the frame grabs input from any agent registered at the scene input handler.
	 */
	public boolean grabsInput() {
		for(Agent agent : gScene.inputHandler().agents()) {
			if(agent.inputGrabber() == this)
				return true;
		}
		return false;
	}
	
	// Trackable Interface implementation
	
	protected GrabberFrame eFrame;
	protected Rotation				q;
	protected float					trackingDist;
	
	@Override
	public void scale(float s) {
		super.scale(s);
		updateTrackingEyeFrame();
	}
	
	/**
	 * Returns the distance between the frame and the tracking camera. Only meaningful when this frame
	 * has been set as the scene avatar.
	 * 
	 * @see remixlab.dandelion.core.GrabberScene#setAvatar(Trackable)
	 */
	public float trackingEyeDistance() {
		return trackingDist;
	}

	/**
	 * Sets the distance between the frame and the tracking camera. Only meaningful when this frame
	 * has been set as the scene avatar.
	 * 
	 * @see remixlab.dandelion.core.GrabberScene#setAvatar(Trackable)
	 */
	public void setTrackingEyeDistance(float d) {
		trackingDist = d;
		updateTrackingEyeFrame();
	}

	/**
	 * Returns the azimuth of the tracking camera measured respect to the frame's {@link #zAxis()}.
	 * Only meaningful when this frame has been set as the scene avatar.
	 * 
	 * @see remixlab.dandelion.core.GrabberScene#setAvatar(Trackable)
	 */
	public float trackingEyeAzimuth() {
		// azimuth <-> pitch
		if (scene().is3D())
			return ((Quat) q).taitBryanAngles().vec[1];
		else {
			GrabberScene.showDepthWarning("azimuth");
			return 0;
		}
	}

	/**
	 * Sets the {@link #trackingEyeAzimuth()} of the tracking camera. Only meaningful when this frame
	 * has been set as the scene avatar.
	 * 
	 * @see remixlab.dandelion.core.GrabberScene#setAvatar(Trackable)
	 */
	public void setTrackingEyeAzimuth(float a) {
		if (scene().is3D()) {
			//TODO experimental
			trackingDist = scene().radius() / 5;
			float roll = ((Quat) q).taitBryanAngles().vec[0];
			((Quat) q).fromTaitBryan(roll, a, 0);
			updateTrackingEyeFrame();
		}
		else
			GrabberScene.showDepthWarning("setAzimuth");
	}

	/**
	 * Returns the inclination of the tracking camera measured respect to the frame's {@link #yAxis()}.
	 * Only meaningful when this frame
	 * has been set as the scene avatar.
	 * 
	 * @see remixlab.dandelion.core.GrabberScene#setAvatar(Trackable)
	 */
	public float trackingEyeInclination() {
		// inclination <-> roll
		if (scene().is3D())
			return ((Quat) q).taitBryanAngles().vec[0];
		else
			return q.angle();
	}

	/**
	 * Sets the {@link #trackingEyeInclination()} of the tracking camera. Only meaningful when this frame
	 * has been set as the scene avatar.
	 * 
	 * @see remixlab.dandelion.core.GrabberScene#setAvatar(Trackable)
	 */
	public void setTrackingEyeInclination(float i) {
		if (scene().is3D()) {
			//TODO experimental
			trackingDist = scene().radius() / 5;
			float pitch = ((Quat) q).taitBryanAngles().vec[1];
			((Quat) q).fromTaitBryan(i, pitch, 0);
		}
		else
			q = new Rot(i);
		updateTrackingEyeFrame();
	}

	/**
	 * The {@link #trackingEyeFrame()} of the Eye that is to be tracking the frame (see the documentation of the Trackable
	 * interface) is defined in spherical coordinates by means of the {@link #trackingEyeAzimuth()}, the {@link #trackingEyeInclination()} and
	 * {@link #trackingEyeDistance()}) respect to the Frame {@link #position()}. Only meaningful when this frame
	 * has been set as the scene avatar.
	 * 
	 * @see remixlab.dandelion.core.GrabberScene#setAvatar(Trackable)
	 */
	protected void updateTrackingEyeFrame() {
		System.out.println("updateTrackingEyeFrame called!");
		if(eFrame == null) {
			eFrame = new GrabberFrame(scene(), this);
			scene().motionAgent().removeGrabber(eFrame);
			scene().keyboardAgent().removeGrabber(eFrame);
		}
		if (scene().is3D()) {
			Vec p = q.rotate(new Vec(0, 0, 1));
			p.multiply(trackingEyeDistance() / magnitude());
			eFrame.setTranslation(p);
			eFrame.setYAxis(yAxis());
			eFrame.setZAxis(inverseTransformOf(p));
			eFrame.setScaling(scene().eye().frame().scaling());
		}
		else {
			Vec p = q.rotate(new Vec(0, 1));
			p.multiply(trackingEyeDistance() / magnitude());
			eFrame.setTranslation(p);
			eFrame.setYAxis(yAxis());
			float size = Math.min(scene().width(), scene().height());
			eFrame.setScaling((2.5f * trackingEyeDistance() / size)); // window.fitBall which sets the scaling
		}
	}

	// Interface implementation

	/**
	 * Overloading of {@link remixlab.dandelion.core.Trackable#trackingEyeFrame()}. Returns the world coordinates of the camera
	 * position computed in {@link #updateTrackingEyeFrame()}. Only meaningful when this frame
	 * has been set as the scene avatar.
	 * 
	 * @see remixlab.dandelion.core.GrabberScene#setAvatar(Trackable)
	 */
	@Override
	public GrabberFrame trackingEyeFrame() {
		return eFrame;
	}
}