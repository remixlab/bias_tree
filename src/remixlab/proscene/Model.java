/**************************************************************************************
 * ProScene (version 3.0.0)
 * Copyright (c) 2010-2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 * 
 * All rights reserved. Library that eases the creation of interactive scenes
 * in Processing, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.proscene;

import processing.core.*;
import remixlab.bias.core.*;

/**
 * A model is a PSshape wrapper {@link remixlab.bias.core.Grabber}. It may thus be manipulated by any
 * {@link remixlab.bias.core.Agent}, such as the {@link remixlab.dandelion.core.AbstractScene#motionAgent()} or
 * the {@link remixlab.dandelion.core.AbstractScene#keyboardAgent()}.
 * <p>
 * A model is selected using <a href="http://schabby.de/picking-opengl-ray-tracing/">'ray-picking'</a>
 * with a color buffer (see {@link remixlab.proscene.Scene#pickingBuffer()}). For an implementation, see
 * {@link remixlab.proscene.ModelObject#checkIfGrabsInput(float, float)} or
 * {@link remixlab.proscene.InteractiveModelFrame#checkIfGrabsInput(float, float)}. Note that the PShape
 * drawing could thus take place at least into the {@link remixlab.proscene.Scene#pg()} and the
 * {@link remixlab.proscene.Scene#pickingBuffer()}, see {@link #draw(PGraphics)}. 
 * <p>
 * Third-parties should implement the {@link remixlab.bias.core.Grabber} behavior. A
 * {@link remixlab.proscene.InteractiveModelFrame} provides default 2D/3D high-level interactivity.
 * <p>
 * One of the main motivations behind Models is an attempt to cover needs such those discussed
 * <a href="http://forum.processing.org/two/discussion/2631/how-to-pick-an-object-in-3d">here</a>.
 */
public interface Model extends Grabber /* , Animator */{
	/**
	 * @return the PShape instance wrapped by this Model.
	 */
	PShape shape();

	/**
	 * While displaying a model requires the PShape to be rendered into {@link remixlab.proscene.Scene#pg()},
	 * picking requires it to be rendered into {@link remixlab.proscene.Scene#pickingBuffer()} (see 
	 * {@link remixlab.proscene.Scene#drawModels()} and {@link remixlab.proscene.Scene#drawModels(PGraphics)}, resp.).
	 * <p>
	 * For an implementation refer to the {@link remixlab.proscene.ModelObject} and
	 * {@link remixlab.proscene.InteractiveModelFrame} classes.
	 * 
	 * @param pg PGraphics buffer used to draw the PShape instance. 
	 * @see remixlab.proscene.Scene#drawModels()
	 * @see remixlab.proscene.Scene#drawModels(PGraphics)
	 */
	void draw(PGraphics pg);
}
