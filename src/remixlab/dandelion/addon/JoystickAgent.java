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

import remixlab.bias.event.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

/**
 * A {@code MotionAgent<DOF3Action>}, such most joystick, supporting
 * {@link remixlab.dandelion.core.Constants.ClickAction}s, and up to
 * {@link remixlab.dandelion.core.Constants.DOF3Action}s actions.
 * <p>
 * @see remixlab.dandelion.core.Constants.ClickAction
 * @see remixlab.dandelion.core.Constants.DOF1Action
 * @see remixlab.dandelion.core.Constants.DOF2Action
 * @see remixlab.dandelion.core.Constants.DOF3Action
 */
public abstract class JoystickAgent extends MotionAgent<DOF3Action> {
	protected float	xSens	= 1f;
	protected float	ySens	= 1f;
	protected float	zSens	= 1f;

	public JoystickAgent(AbstractScene scn, String n) {
		super(scn, n);
	}

	@Override
	public float[] sensitivities(MotionEvent event) {
		if (event instanceof DOF3Event)
			return new float[] { xSens, ySens, zSens, 1f, 1f, 1f };
		else
			return super.sensitivities(event);
	}

	/**
	 * Defines the {@link #xSensitivity()}.
	 */
	public void setXSensitivity(float sensitivity) {
		xSens = sensitivity;
	}

	/**
	 * Returns the x sensitivity.
	 * <p>
	 * Default value is 1. A higher value will make the event more efficient (usually meaning a faster motion). Use a
	 * negative value to invert the along x-Axis motion direction.
	 * 
	 * @see #setWheelSensitivity(float)
	 */
	public float xSensitivity() {
		return xSens;
	}

	/**
	 * Defines the {@link #ySensitivity()}.
	 */
	public void setYSensitivity(float sensitivity) {
		ySens = sensitivity;
	}

	/**
	 * Returns the y sensitivity.
	 * <p>
	 * Default value is 1. A higher value will make the event more efficient (usually meaning a faster motion). Use a
	 * negative value to invert the along y-Axis motion direction.
	 * 
	 * @see #setWheelSensitivity(float)
	 */
	public float ySensitivity() {
		return ySens;
	}

	/**
	 * Defines the {@link #ySensitivity()}.
	 */
	public void setZSensitivity(float sensitivity) {
		zSens = sensitivity;
	}

	/**
	 * Returns the y sensitivity.
	 * <p>
	 * Default value is 1. A higher value will make the event more efficient (usually meaning a faster motion). Use a
	 * negative value to invert the along z-Axis motion direction.
	 * 
	 * @see #setWheelSensitivity(float)
	 */
	public float zSensitivity() {
		return zSens;
	}
}