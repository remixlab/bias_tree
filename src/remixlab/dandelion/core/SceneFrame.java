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

import remixlab.bias.event.DOF1Event;
import remixlab.bias.event.DOF2Event;
import remixlab.bias.event.MotionEvent;
import remixlab.dandelion.geom.*;
import remixlab.fpstiming.TimingTask;
import remixlab.util.*;

/**
 * The frame is loosely-coupled with the scene object used to instantiate it, i.e., the transformation it represents may
 * be applied to a different scene. See {@link #applyTransformation()} and {@link #applyTransformation(AbstractScene)}.
 * <p>
 * Two frames can be synced together ({@link #sync(SceneFrame, SceneFrame)}), meaning that they will share their global
 * parameters (position, orientation and magnitude) taken the one that has been most recently updated. Syncing can be
 * useful to share frames among different off-screen scenes (see ProScene's CameraCrane and the AuxiliarViewer
 * examples).
 */
public class SceneFrame extends Frame {
	// Sens
	private float								rotSensitivity;
	private float								transSensitivity;
	private float								wheelSensitivity;

	// spinning stuff:
	private float								spngSensitivity;
	private TimingTask					spinningTimerTask;
	private Rotation						spngRotation;
	protected float							dampFriction;							// new
	// toss and spin share the damp var:
	private float								sFriction;									// new

	// Whether the SCREEN_TRANS direction (horizontal or vertical) is fixed or not.
	public boolean							dirIsFixed;
	private boolean							horiz								= true; // Two simultaneous InteractiveFrame require two mice!

	// TODO decide whether to include this:
	protected float							eventSpeed;								// spnning and tossing
	protected Vec								tDir;
	protected float							flySpd;
	protected TimingTask				flyTimerTask;
	protected Vec								scnUpVec;
	protected Vec								flyDisp;
	protected static final long	FLY_UPDATE_PERDIOD	= 10;

	protected long							lastUpdate;
	protected AbstractScene			scene;

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				appendSuper(super.hashCode()).
				// append(grabsInputThreshold).
				// append(adpThreshold).
				// append(isInCamPath).
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

		SceneFrame other = (SceneFrame) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				// .append(grabsInputThreshold, other.grabsInputThreshold)
				// .append(adpThreshold, other.adpThreshold)
				// .append(isInCamPath, other.isInCamPath)
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
				.append(lastUpdate, other.lastUpdate)
				.isEquals();
	}

	/**
	 * Same as {@code this(scn, null, new Vec(), scn.is3D() ? new Quat() : new Rot(), 1)}.
	 * 
	 * @see #SceneFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public SceneFrame(AbstractScene scn) {
		this(scn, null, new Vec(), scn.is3D() ? new Quat() : new Rot(), 1);
	}

	/**
	 * Same as {@code this(scn, null, p, scn.is3D() ? new Quat() : new Rot(), 1)}.
	 * 
	 * @see #SceneFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public SceneFrame(AbstractScene scn, Vec p) {
		this(scn, null, p, scn.is3D() ? new Quat() : new Rot(), 1);
	}

	/**
	 * Same as {@code this(scn, null, new Vec(), r, 1)}.
	 * 
	 * @see #SceneFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public SceneFrame(AbstractScene scn, Rotation r) {
		this(scn, null, new Vec(), r, 1);
	}

	/**
	 * Same as {@code this(scn, null, new Vec(), scn.is3D() ? new Quat() : new Rot(), s)}.
	 * 
	 * @see #SceneFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public SceneFrame(AbstractScene scn, float s) {
		this(scn, null, new Vec(), scn.is3D() ? new Quat() : new Rot(), s);
	}

	/**
	 * Same as {@code this(scn, null, p, scn.is3D() ? new Quat() : new Rot(), s)}.
	 * 
	 * @see #SceneFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public SceneFrame(AbstractScene scn, Vec p, float s) {
		this(scn, null, p, scn.is3D() ? new Quat() : new Rot(), s);
	}

	/**
	 * Same as {@code this(scn, null, p, r, 1)}.
	 * 
	 * @see #SceneFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public SceneFrame(AbstractScene scn, Vec p, Rotation r) {
		this(scn, null, p, r, 1);
	}

	/**
	 * Same as {@code this(scn, null, new Vec(), r, s)}.
	 * 
	 * @see #SceneFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public SceneFrame(AbstractScene scn, Rotation r, float s) {
		this(scn, null, new Vec(), r, s);
	}

	/**
	 * Same as {@code this(scn, null, p, r, s)}.
	 * 
	 * @see #SceneFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public SceneFrame(AbstractScene scn, Vec p, Rotation r, float s) {
		this(scn, null, p, r, s);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, new Vec(), scn.is3D() ? new Quat() : new Rot(), 1)}.
	 * 
	 * @see #SceneFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public SceneFrame(AbstractScene scn, Frame referenceFrame) {
		this(scn, referenceFrame, new Vec(), scn.is3D() ? new Quat() : new Rot(), 1);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, p, scn.is3D() ? new Quat() : new Rot(), 1)}.
	 * 
	 * @see #SceneFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public SceneFrame(AbstractScene scn, Frame referenceFrame, Vec p) {
		this(scn, referenceFrame, p, scn.is3D() ? new Quat() : new Rot(), 1);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, new Vec(), r, 1)}.
	 * 
	 * @see #SceneFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public SceneFrame(AbstractScene scn, Frame referenceFrame, Rotation r) {
		this(scn, referenceFrame, new Vec(), r, 1);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, new Vec(), scn.is3D() ? new Quat() : new Rot(), s)}.
	 * 
	 * @see #SceneFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public SceneFrame(AbstractScene scn, Frame referenceFrame, float s) {
		this(scn, referenceFrame, new Vec(), scn.is3D() ? new Quat() : new Rot(), s);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, p, scn.is3D() ? new Quat() : new Rot(), s)}.
	 * 
	 * @see #SceneFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public SceneFrame(AbstractScene scn, Frame referenceFrame, Vec p, float s) {
		this(scn, referenceFrame, p, scn.is3D() ? new Quat() : new Rot(), s);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, p, r, 1)}.
	 * 
	 * @see #SceneFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public SceneFrame(AbstractScene scn, Frame referenceFrame, Vec p, Rotation r) {
		this(scn, referenceFrame, p, r, 1);
	}

	/**
	 * Same as {@code this(scn, referenceFrame, new Vec(), r, s)}.
	 * 
	 * @see #SceneFrame(AbstractScene, Frame, Vec, Rotation, float)
	 */
	public SceneFrame(AbstractScene scn, Frame referenceFrame, Rotation r, float s) {
		this(scn, referenceFrame, new Vec(), r, s);
	}

	/**
	 * Creates a Frame bound to {@code scn} with {@code referenceFrame} as {@link #referenceFrame()}, and {@code p},
	 * {@code r} and {@code s} as the frame {@link #translation()}, {@link #rotation()} and {@link #scaling()},
	 * respectively.
	 */
	public SceneFrame(AbstractScene scn, Frame referenceFrame, Vec p, Rotation r, float s) {
		super(referenceFrame, p, r, s);
		scene = scn;

		setRotationSensitivity(1.0f);
		setTranslationSensitivity(1.0f);
		setWheelSensitivity(5.0f);
		setSpinningSensitivity(0.3f);
		setDampingFriction(0.5f);

		spinningTimerTask = new TimingTask() {
			public void execute() {
				spin();
			}
		};
		scene.registerTimingTask(spinningTimerTask);

		// TODO decide whether to include this:
		scnUpVec = new Vec(0.0f, 1.0f, 0.0f);
		flyDisp = new Vec(0.0f, 0.0f, 0.0f);
		flyTimerTask = new TimingTask() {
			public void execute() {
				toss();
			}
		};
		scene.registerTimingTask(flyTimerTask);
		// end
	}

	protected SceneFrame(SceneFrame otherFrame) {
		super(otherFrame);
		this.scene = otherFrame.scene;

		this.spinningTimerTask = new TimingTask() {
			public void execute() {
				spin();
			}
		};

		this.scene.registerTimingTask(spinningTimerTask);

		// TODO decide whether to include this:
		this.scnUpVec = new Vec();
		this.scnUpVec.set(otherFrame.sceneUpVector());
		this.flyDisp = new Vec();
		this.flyDisp.set(otherFrame.flyDisp.get());
		this.flyTimerTask = new TimingTask() {
			public void execute() {
				toss();
			}
		};
		this.scene.registerTimingTask(flyTimerTask);
		lastUpdate = otherFrame.lastUpdate();

		// end

		// this.isInCamPath = otherFrame.isInCamPath;
		//
		// this.setGrabsInputThreshold(otherFrame.grabsInputThreshold(), otherFrame.adaptiveGrabsInputThreshold());
		// this.setRotationSensitivity(otherFrame.rotationSensitivity());
		// this.setTranslationSensitivity(otherFrame.translationSensitivity());
		// this.setWheelSensitivity(otherFrame.wheelSensitivity());
		//
		// this.setSpinningSensitivity(otherFrame.spinningSensitivity());
		// this.setDampingFriction(otherFrame.dampingFriction());
		//
		// this.setAction(otherFrame.action());
		//
		// this.spinningTimerTask = new TimingTask() {
		// public void execute() {
		// spin();
		// }
		// };
		// this.scene.registerTimingTask(spinningTimerTask);
		//
		// this.scnUpVec = new Vec();
		// this.scnUpVec.set(otherFrame.sceneUpVector());
		// this.flyDisp = new Vec();
		// this.flyDisp.set(otherFrame.flyDisp);
		// this.setFlySpeed(otherFrame.flySpeed());
		//
		// this.flyTimerTask = new TimingTask() {
		// public void execute() {
		// toss();
		// }
		// };
		// this.scene.registerTimingTask(flyTimerTask);
	}

	@Override
	public SceneFrame get() {
		return new SceneFrame(this);
	}

	/**
	 * Returns the scene this object belongs to
	 */
	public AbstractScene scene() {
		return scene;
	}

	// APPLY TRANSFORMATION

	/**
	 * Convenience function that simply calls {@code applyTransformation(scene)}. It applies the transformation defined by
	 * the frame to the scene used to instantiated.
	 * 
	 * @see #applyTransformation(AbstractScene)
	 * @see #matrix()
	 */
	public void applyTransformation() {
		applyTransformation(scene);
	}

	/**
	 * Convenience function that simply calls {@code applyWorldTransformation(scene)}. It applies the world transformation
	 * defined by the frame to the scene used to instantiated.
	 * 
	 * @see #applyWorldTransformation(AbstractScene)
	 * @see #worldMatrix()
	 */
	public void applyWorldTransformation() {
		applyWorldTransformation(scene);
	}

	/**
	 * Convenience function that simply calls {@code scn.applyTransformation(this)}. You may apply the transformation
	 * represented by this frame to any scene you want using this method.
	 * <p>
	 * Very efficient prefer always this than
	 * 
	 * @see #applyTransformation()
	 * @see #matrix()
	 * @see remixlab.dandelion.core.AbstractScene#applyTransformation(Frame)
	 */
	public void applyTransformation(AbstractScene scn) {
		scn.applyTransformation(this);
	}

	/**
	 * Convenience function that simply calls {@code scn.applyWorldTransformation(this)}. You may apply the world
	 * transformation represented by this frame to any scene you want using this method.
	 * 
	 * @see #applyWorldTransformation()
	 * @see #worldMatrix()
	 * @see remixlab.dandelion.core.AbstractScene#applyWorldTransformation(Frame)
	 */
	public void applyWorldTransformation(AbstractScene scn) {
		scn.applyWorldTransformation(this);
	}

	// MODIFIED

	/**
	 * Internal use. Automatically call by all methods which change the Frame state.
	 */
	protected void modified() {
		if (scene != null)
			lastUpdate = scene.frameCount();
		super.modified();
	}

	/**
	 * Internal use. Needed by {@link #sync(Frame, Frame, boolean)}.
	 */
	// TODO decide whether to include this one since it now seems overkill to me -jp
	protected long lastGlobalUpdate() {
		return lastUpdate() + scene.deltaCount;
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
	 * @see #sync(SceneFrame, SceneFrame)
	 */
	public void sync(SceneFrame otherFrame) {
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
	public static void sync(SceneFrame f1, SceneFrame f2) {
		if (f1.lastGlobalUpdate() == f2.lastGlobalUpdate())
			return;
		SceneFrame source = (f1.lastGlobalUpdate() > f2.lastGlobalUpdate()) ? f1 : f2;
		SceneFrame target = (f1.lastGlobalUpdate() > f2.lastGlobalUpdate()) ? f2 : f1;
		target.fromFrame(source);
	}

	// Fx

	/**
	 * Internal use.
	 * <p>
	 * Returns the cached value of the spinning friction used in {@link #recomputeSpinningRotation()}.
	 */
	protected float dampingFrictionFx() {
		return sFriction;
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
	 * Defines the {@link #dampingFriction()}. Values must be in the range [0..1].
	 */
	public void setDampingFriction(float f) {
		if (f < 0 || f > 1)
			return;
		dampFriction = f;
		setDampingFrictionFx(dampFriction);
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
	 * Default value is 5.0. A higher value will make the wheel action more efficient (usually meaning faster motion).
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
	 * Returns the incremental rotation that is applied by {@link #spin()} to the InteractiveFrame orientation when it
	 * {@link #isSpinning()}.
	 * <p>
	 * Default value is a {@code null} rotation. Use {@link #setSpinningRotation(Rotation)} to change this value.
	 * <p>
	 * The {@link #spinningRotation()} axis is defined in the InteractiveFrame coordinate system. You can use
	 * {@link remixlab.dandelion.geom.Frame#transformOfFrom(Vec, Frame)} to convert this axis from another Frame
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
	 * Defines the {@link #spinningRotation()}. Its axis is defined in the InteractiveFrame coordinate system.
	 * 
	 * @see #setTossingDirection(Vec)
	 */
	public final void setSpinningRotation(Rotation spinningRotation) {
		spngRotation = spinningRotation;
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
	 * Rotates the scene-frame by its {@link #spinningRotation()} or around the
	 * {@link remixlab.dandelion.core.Eye#anchor()} when this scene-frame is the
	 * {@link remixlab.dandelion.core.AbstractScene#eye()}. Called by a timer when the InteractiveFrame
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
			if (this != scene.eye().frame())
				rotate(spinningRotation());
			else
				rotateAroundPoint(spinningRotation(), scene.eye().anchor());
			recomputeSpinningRotation();
		}
		else if (this != scene.eye().frame())
			rotate(spinningRotation());
		else
			rotateAroundPoint(spinningRotation(), scene.eye().anchor());
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

	/**
	 * Returns a Rotation computed according to the mouse motion. Mouse positions are projected on a deformed ball,
	 * centered on ({@code trns.x()}, {@code trns.y()}).
	 */
	public Rotation deformedBallRotation(DOF2Event event, Vec trns) {
		if(scene.is2D()) {
			Rot rt;
			if (event.isRelative()) {
				Point prevPos = new Point(event.prevX(), event.prevY());
				Point curPos = new Point(event.x(), event.y());
				rt = new Rot(new Point(trns.x(), trns.y()), prevPos, curPos);
				rt = new Rot(rt.angle() * rotationSensitivity());
			}
			else
				rt = new Rot(event.x() * rotationSensitivity());
			if ((scene.isRightHanded() && this != scene.eye().frame()) || (scene.isLeftHanded() && this == scene.eye().frame()))
				rt.negate();
			return rt;
		}
		else{
		  // TODO absolute events!?
			float cx = trns.x();
			float cy = trns.y();
			float x = event.x();
			float y = event.y();
			float prevX = event.prevX();
			float prevY = event.prevY();
			// Points on the deformed ball
			float px = rotationSensitivity() * ((int) prevX - cx) / scene.camera().screenWidth();
			float py = rotationSensitivity() * (scene.isLeftHanded() ? ((int) prevY - cy) : (cy - (int) prevY))
					/ scene.camera().screenHeight();
			float dx = rotationSensitivity() * (x - cx) / scene.camera().screenWidth();
			float dy = rotationSensitivity() * (scene.isLeftHanded() ? (y - cy) : (cy - y)) / scene.camera().screenHeight();

			Vec p1 = new Vec(px, py, projectOnBall(px, py));
			Vec p2 = new Vec(dx, dy, projectOnBall(dx, dy));
			// Approximation of rotation angle Should be divided by the projectOnBall size, but it is 1.0
			Vec axis = p2.cross(p1);
			float angle = 2.0f * (float) Math.asin((float) Math.sqrt(axis.squaredNorm() / p1.squaredNorm() / p2.squaredNorm()));
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
		Vec trns = new Vec();
		Quat q = new Quat(scene.isLeftHanded() ? roll : -roll, -pitch, scene.isLeftHanded() ? yaw : -yaw);
		trns.set(-q.x(), -q.y(), -q.z());
		trns = scene.camera().frame().orientation().rotate(trns);
		trns = transformOf(trns);
		q.setX(trns.x());
		q.setY(trns.y());
		q.setZ(trns.z());
		rotate(q);
	}

	// micro-actions procedures
	
	public Vec screen2Eye(Vec trns) {
		Vec eyeVec = trns.get();
	  // Scale to fit the screen relative event displacement
		if (scene.is2D())
			// Quite excited to see how simple it's in 2d:
			return eyeVec;
		// and amazed of how dirty it's in 3d:
		switch (scene.camera().type()) {
		case PERSPECTIVE:
			float k = (float) Math.tan(scene.camera().fieldOfView() / 2.0f)
					* Math.abs(scene.camera().frame().coordinatesOf(this == scene.eye().frame() ? scene.eye().anchor() : position()).vec[2]	* scene.camera().frame().magnitude());
					//* Math.abs(scene.camera().frame().coordinatesOf(this == scene.eye().frame() ? scene.eye().anchor() : position()).vec[2]);
			eyeVec.vec[0] *= 2.0 * k / scene.camera().screenHeight();
			eyeVec.vec[1] *= 2.0 * k / scene.camera().screenHeight();
			break;
		case ORTHOGRAPHIC:
			float[] wh = scene.camera().getBoundaryWidthHeight();
			//float[] wh = scene.camera().getOrthoWidthHeight();
			eyeVec.vec[0] *= 2.0 * wh[0] / scene.camera().screenWidth();			
			eyeVec.vec[1] *= 2.0 * wh[1] / scene.camera().screenHeight();
			break;
		}
		float coef;
		if( this == scene.eye().frame() ) {
			//float coef = 8E-4f;
			coef = Math.max(Math.abs((coordinatesOf(scene.camera().anchor())).vec[2] * magnitude()), 0.2f * scene.camera().sceneRadius());
			eyeVec.vec[2] *= coef / scene.camera().screenHeight();
			//TODO eye wheel seems different
			//trns.vec[2] *= coef * 8E-4f;
		}
		else {
			coef = Vec.subtract(scene.camera().position(), position()).magnitude();
			eyeVec.vec[2] *= coef / scene.camera().screenHeight();
		}
		//if( this == scene.eye().frame() )
		eyeVec.divide(scene.eye().frame().magnitude());
	  return eyeVec;
	}
	
	protected float computeAngle(DOF1Event e1) {
		return delta1(e1) * (float) Math.PI / scene.eye().screenWidth();
	}
	
	protected float delta1(DOF1Event e1) {
		return e1.isAbsolute() ? e1.x() : e1.dx();
	}

	public void screenTranslate(Vec trns) {
		screenTranslate(trns, translationSensitivity());
	}

	public void screenTranslate(Vec trns, float sens) {
	  //if( scene.is3D() )	gesture2Eye(trns);
	  eyeTranslate(screen2Eye(trns), sens);
	}
	
	public void eyeTranslate(Vec trns, float sens) {
	  // Transform from eye to world coordinate system.
		trns = scene.eye().frame().inverseTransformOf(Vec.multiply(trns, sens));
		
		if(this != scene.eye().frame()) {
			if (referenceFrame() != null)
				trns = referenceFrame().transformOf(trns);
			translate(trns);
		}
		else
			translate(trns);	
	}

	// TODO decide whether to include this:

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
	 * Defines the {@link #tossingDirection()} in the InteractiveFrame coordinate system.
	 * 
	 * @see #setSpinningRotation(Rotation)
	 */
	public final void setTossingDirection(Vec dir) {
		tDir = dir;
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
	 * Returns a Quaternion that is the composition of two rotations, inferred from the mouse roll (X axis) and pitch (
	 * {@link #sceneUpVector()} axis).
	 */
	public final Quat rollPitchQuaternion(DOF2Event event, Camera camera) {
		float deltaX = event.isAbsolute() ? event.x() : event.dx();
		float deltaY = event.isAbsolute() ? event.y() : event.dy();

		if (scene.isRightHanded())
			deltaY = -deltaY;

		Quat rotX = new Quat(new Vec(1.0f, 0.0f, 0.0f), rotationSensitivity() * deltaY / camera.screenHeight());
		Quat rotY = new Quat(transformOf(sceneUpVector()), rotationSensitivity() * (-deltaX) / camera.screenWidth());
		return Quat.multiply(rotY, rotX);
	}

	// drive:

	/**
	 * Returns a Quaternion that is a rotation around current camera Y, proportional to the horizontal mouse position.
	 */
	public Quat turnQuaternion(DOF1Event event, Camera camera) {
		float deltaX = event.isAbsolute() ? event.x() : event.dx();
		return new Quat(new Vec(0.0f, 1.0f, 0.0f), rotationSensitivity() * (-deltaX) / camera.screenWidth());
	}

	// end decide
}