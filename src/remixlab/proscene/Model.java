/**************************************************************************************
 * ProScene (version 2.1.0)
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

//import remixlab.fpstiming.*;

/**
 * A model is a pshape that can be picked and manipulated by any user means, being it a hardware such as a joystick, or
 * a software entity like a user coded intelligent-agent.
 * <p>
 * Third-parties should implement the {@link remixlab.bias.core.Grabber} behavior. A
 * {@link remixlab.proscene.InteractiveModel} provides default 2D/3D high-level interactivity.
 */
public interface Model extends Grabber /* , Animator */{
	PShape shape();

	void draw(PGraphics pg);
}
