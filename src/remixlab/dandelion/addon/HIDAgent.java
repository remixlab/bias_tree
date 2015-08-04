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

import remixlab.bias.core.BogusEvent;
import remixlab.bias.event.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

/**
 * A {@code MotionAgent<DOF6Action>}, such most joystick, supporting
 * {@link remixlab.dandelion.core.Constants.ClickAction}s, and up to
 * {@link remixlab.dandelion.core.Constants.DOF6Action}s actions.
 * <p>
 * @see remixlab.dandelion.core.Constants.ClickAction
 * @see remixlab.dandelion.core.Constants.DOF1Action
 * @see remixlab.dandelion.core.Constants.DOF2Action
 * @see remixlab.dandelion.core.Constants.DOF3Action
 * @see remixlab.dandelion.core.Constants.DOF6Action
 */
public abstract class HIDAgent extends MotionAgent<DOF6Action> {
	protected float	xSens		= 1f;
	protected float	ySens		= 1f;
	protected float	zSens		= 1f;
	protected float	xrSens	= 1f;
	protected float	yrSens	= 1f;
	protected float	zrSens	= 1f;

	public HIDAgent(AbstractScene scn, String n) {
		super(scn, n);
		setGestureBinding(Target.EYE, DOF6Action.TRANSLATE_XYZ_ROTATE_XYZ);
		setGestureBinding(Target.FRAME, DOF6Action.TRANSLATE_XYZ_ROTATE_XYZ);
	}

	public void setGestureBinding(Target target, DOF6Action action) {
		setBinding(target, new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, BogusEvent.NO_ID), action);
	}

	@Override
	public float[] sensitivities(MotionEvent event) {
		if (event instanceof DOF6Event)
			return new float[] { xSens, ySens, zSens, xrSens, yrSens, zrSens };
		else
			return super.sensitivities(event);
	}

	/**
	 * Sets sensitivities for each of the 6 DOF's.
	 */
	public void setSensitivities(float x, float y, float z, float rx, float ry, float rz) {
		xSens = x;
		ySens = y;
		zSens = z;
		xrSens = rx;
		yrSens = ry;
		zrSens = rz;
	}

	/**
	 * Sets the {@link #xTranslationSensitivity()}.
	 */
	public void setXTranslationSensitivity(float s) {
		xSens = s;
	}

	/**
	 * Returns the x translation sensitivity.
	 * <p>
	 * Default value is 1. A higher value will make the event more efficient (usually meaning a faster motion). Use a
	 * negative value to invert the along x-Axis motion direction.
	 */
	public float xTranslationSensitivity() {
		return xSens;
	}

	/**
	 * Sets the {@link #yTranslationSensitivity()}.
	 */
	public void setYTranslationSensitivity(float s) {
		ySens = s;
	}

	/**
	 * Returns the y translation sensitivity.
	 * <p>
	 * Default value is 1. A higher value will make the event more efficient (usually meaning a faster motion). Use a
	 * negative value to invert the along y-Axis motion direction.
	 */
	public float yTranslationSensitivity() {
		return ySens;
	}

	/**
	 * Sets the {@link #zTranslationSensitivity()}.
	 */
	public void setZTranslationSensitivity(float s) {
		zSens = s;
	}

	/**
	 * Returns the z translation sensitivity.
	 * <p>
	 * Default value is 1. A higher value will make the event more efficient (usually meaning a faster motion). Use a
	 * negative value to invert the along z-Axis motion direction.
	 */
	public float zTranslationSensitivity() {
		return zSens;
	}

	/**
	 * Sets the {@link #xRotationSensitivity()}.
	 */
	public void setXRotationSensitivity(float s) {
		xrSens = s;
	}

	/**
	 * Returns the x rotation sensitivity.
	 * <p>
	 * Default value is 1. A higher value will make the event more efficient (usually meaning a faster motion). Use a
	 * negative value to invert the x-Axis.
	 */
	public float xRotationSensitivity() {
		return xrSens;
	}

	/**
	 * Sets the {@link #yRotationSensitivity()}.
	 */
	public void setYRotationSensitivity(float s) {
		yrSens = s;
	}

	/**
	 * Returns the y rotation sensitivity.
	 * <p>
	 * Default value is 1. A higher value will make the event more efficient (usually meaning a faster motion). Use a
	 * negative value to invert the y-Axis.
	 */
	public float yRotationSensitivity() {
		return yrSens;
	}

	/**
	 * Sets the {@link #zRotationSensitivity()}.
	 */
	public void setZRotationSensitivity(float s) {
		zrSens = s;
	}

	/**
	 * Returns the z rotation sensitivity.
	 * <p>
	 * Default value is 1. A higher value will make the event more efficient (usually meaning a faster motion). Use a
	 * negative value to invert the z-Axis.
	 */
	public float zRotationSensitivity() {
		return zrSens;
	}
}