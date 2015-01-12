
package remixlab.dandelion.agent;

import remixlab.bias.event.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

public class HIDAgent extends WheeledMotionAgent<DOF6Action> {
	protected float	xSens		= 1f;
	protected float	ySens		= 1f;
	protected float	zSens		= 1f;
	protected float	xrSens	= 1f;
	protected float	yrSens	= 1f;
	protected float	zrSens	= 1f;

	public HIDAgent(AbstractScene scn, String n) {
		super(scn, n);
	}

	@Override
	public DOF6Event feed() {
		return null;
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