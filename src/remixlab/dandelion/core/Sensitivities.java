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

public interface Sensitivities {
	public float dampingFriction();

	public void setDampingFriction(float f);

	public void setRotationSensitivity(float sensitivity);

	public void setTranslationSensitivity(float sensitivity);

	public void setSpinningSensitivity(float sensitivity);

	public void setWheelSensitivity(float sensitivity);

	public float rotationSensitivity();

	public float translationSensitivity();

	public float spinningSensitivity();

	public float wheelSensitivity();
}
