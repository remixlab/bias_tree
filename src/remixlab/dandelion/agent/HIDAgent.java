/*********************************************************************************
 * dandelion_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.dandelion.agent;

import remixlab.bias.core.BogusEvent;
import remixlab.bias.event.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

public class HIDAgent extends MotionAgent<DOF6Action> {
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
	public DOF6Event feed() {
		return null;
	}

	@Override
	public float[] sensitivities(MotionEvent event) {
		if (event instanceof DOF6Event)
			return new float[] { xSens, ySens, zSens, xrSens, yrSens, zrSens };
		else
			return super.sensitivities(event);
	}

	public void setSensitivities(float x, float y, float z, float rx, float ry, float rz) {
		xSens = x;
		ySens = y;
		zSens = z;
		xrSens = rx;
		yrSens = ry;
		zrSens = rz;
	}

	public void setXTranslationSensitivity(float s) {
		xSens = s;
	}

	public float xTranslationSensitivity() {
		return xSens;
	}

	public void setYTranslationSensitivity(float s) {
		ySens = s;
	}

	public float yTranslationSensitivity() {
		return ySens;
	}

	public void setZTranslationSensitivity(float s) {
		zSens = s;
	}

	public float zTranslationSensitivity() {
		return zSens;
	}

	public void setXRotationSensitivity(float s) {
		xrSens = s;
	}

	public float xRotationSensitivity() {
		return xrSens;
	}

	public void setYRotationSensitivity(float s) {
		yrSens = s;
	}

	public float yRotationSensitivity() {
		return yrSens;
	}

	public void setZRotationSensitivity(float s) {
		zrSens = s;
	}

	public float zRotationSensitivity() {
		return zrSens;
	}
}