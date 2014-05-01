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

import remixlab.dandelion.geom.*;
import remixlab.util.Copyable;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;

/**
 * The InteractiveAvatarFrame class represents an InteractiveFrame that can be tracked by a Camera, i.e., it implements
 * the Trackable interface.
 * <p>
 * The {@link #eyePosition()} of the camera that is to be tracking the frame (see the documentation of the Trackable
 * interface) is defined in spherical coordinates ({@link #azimuth()}, {@link #inclination()} and
 * {@link #trackingDistance()}) respect to the {@link #position()} (which defines its {@link #target()}) of the
 * InteractiveAvatarFrame.
 */
public class InteractiveAvatarFrame extends InteractiveFrame implements Constants, Trackable, Copyable {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				appendSuper(super.hashCode()).
				append(q).
				append(trackingDist).
				append(camRelPos).
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

		InteractiveAvatarFrame other = (InteractiveAvatarFrame) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(q, other.q)
				.append(trackingDist, other.trackingDist)
				.append(camRelPos, other.camRelPos)
				.isEquals();
	}

	private Rotation	q;
	private float	trackingDist;
	private Vec		camRelPos;

	/**
	 * Constructs an InteractiveAvatarFrame and sets its {@link #trackingDistance()} to
	 * {@link remixlab.dandelion.core.AbstractScene#radius()}/5, {@link #azimuth()} to 0, and {@link #inclination()} to 0.
	 * 
	 * @see remixlab.dandelion.core.AbstractScene#setAvatar(Trackable)
	 */
	public InteractiveAvatarFrame(AbstractScene scn) {
		super(scn);
		if( scene.is3D() ) {
			q = new Quat();
			((Quat) q).fromTaitBryan(QUARTER_PI, 0, 0);
		} else {
			q = new Rot(QUARTER_PI);
		}
		camRelPos = new Vec();
		setTrackingDistance(scene.radius() / 5);
		// scene.setAvatar(this);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param otherFrame
	 *          the other interactive avatar frame
	 */
	protected InteractiveAvatarFrame(InteractiveAvatarFrame otherFrame) {
		super(otherFrame);
		this.q = otherFrame.q.get();
		this.camRelPos = new Vec();
		this.camRelPos.set(otherFrame.camRelPos);
		this.setTrackingDistance(otherFrame.trackingDistance());
	}

	/**
	 * Calls {@link #InteractiveAvatarFrame(InteractiveAvatarFrame)} (which is protected) and returns a copy of
	 * {@code this} object.
	 * 
	 * @see #InteractiveAvatarFrame(InteractiveAvatarFrame)
	 */
	@Override
	public InteractiveAvatarFrame get() {
		return new InteractiveAvatarFrame(this);
	}

	/**
	 * Returns the distance between the frame and the tracking camera.
	 */
	public float trackingDistance() {
		return trackingDist;
	}

	/**
	 * Sets the distance between the frame and the tracking camera.
	 */
	public void setTrackingDistance(float d) {
		trackingDist = d;
		computeEyePosition();
	}

	/**
	 * Returns the azimuth of the tracking camera measured respect to the frame's {@link #zAxis()}.
	 */
	public float azimuth() {
		// azimuth <-> pitch
		if( scene.is3D() )
			return ((Quat) q).taitBryanAngles().vec[1];
		else {
			AbstractScene.showDepthWarning("azimuth");
			return 0;
		}
	}

	/**
	 * Sets the {@link #azimuth()} of the tracking camera.
	 */
	public void setAzimuth(float a) {
		if (scene.is3D()) {
			float roll = ((Quat) q).taitBryanAngles().vec[0];
			((Quat) q).fromTaitBryan(roll, a, 0);
			computeEyePosition();
		}
		else
			AbstractScene.showDepthWarning("setAzimuth");
	}

	/**
	 * Returns the inclination of the tracking camera measured respect to the frame's {@link #yAxis()}.
	 */
	public float inclination() {
		// inclination <-> roll
		if(scene.is3D())
			return ((Quat) q).taitBryanAngles().vec[0];
		else
			return q.angle();
	}

	/**
	 * Sets the {@link #inclination()} of the tracking camera.
	 */
	public void setInclination(float i) {
		if( scene.is3D() ) {
			float pitch = ((Quat) q).taitBryanAngles().vec[1];
			((Quat) q).fromTaitBryan(i, pitch, 0);
		}
		else
			q = new Rot(i);
		computeEyePosition();
	}

	// Interface implementation

	/**
	 * Overloading of {@link remixlab.dandelion.core.Trackable#eyePosition()}. Returns the world coordinates of the camera
	 * position computed in {@link #computeEyePosition()}.
	 */
	@Override
	public Vec eyePosition() {
		return inverseCoordinatesOf(camRelPos);
	}

	/**
	 * Overloading of {@link remixlab.dandelion.core.Trackable#upVector()}. Simply returns the frame {@link #yAxis()}.
	 */
	@Override
	public Vec upVector() {
		return yAxis();
	}

	/**
	 * Overloading of {@link remixlab.dandelion.core.Trackable#target()}. Simply returns the frame {@link #position()}.
	 */
	@Override
	public Vec target() {
		return position();
	}

	/**
	 * Overloading of {@link remixlab.dandelion.core.Trackable#computeEyePosition()}.
	 * <p>
	 * The {@link #eyePosition()} of the camera that is to be tracking the frame (see the documentation of the Trackable
	 * interface) is defined in spherical coordinates by means of the {@link #azimuth()}, the {@link #inclination()} and
	 * {@link #trackingDistance()}) respect to the {@link #position()}.
	 */
	@Override
	public void computeEyePosition() {
		if( scene.is3D() )
			camRelPos = q.rotate(new Vec(0, 0, 1));
		else
			camRelPos = q.rotate(new Vec(0, 1));
		camRelPos.multiply(trackingDistance());
	}
}
