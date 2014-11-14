
package remixlab.proscene;

import processing.core.*;
import remixlab.bias.core.*;

//import remixlab.fpstiming.*;

/**
 * A model is an object that can be picked and manipulated by any user means, being it a hardware such as a joystick, or
 * a software entity like a user coded intelligent-agent.
 * <p>
 * Third-parties should implement the {@link remixlab.bias.core.Grabber} behavior. A
 * {@link remixlab.proscene.InteractiveModel} provides default 2D/3D high-level interactivity.
 */
public interface Model extends Grabber /* , Animator */{
	PShape shape();

	void draw(PGraphics pg);
}
