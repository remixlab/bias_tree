
package remixlab.proscene;

import processing.core.*;
import remixlab.bias.core.*;

/**
 * A modelable is a PShape or graphics procedure that can be picked and manipulated by any user means, being it a
 * hardware such as a joystick, or a software entity like a user coded intelligent-agent.
 * <p>
 * Third-parties should implement the {@link remixlab.bias.core.Grabber} behavior. A {@link remixlab.proscene.Model} 
 * provides default 2D/3D high-level interactivity.
 * <p>
 * TODO: doc about: animation handler, how precise picking was implemented, are missed
 */
public interface Modelable extends Grabber {
	// PShape
	PShape shape();

	void setShape(PShape ps);

	void drawShape();

	void drawShape(PGraphics pg);

	// PGraphics draw handler
	boolean hasGraphicsHandler();

	boolean invokeGraphicsHandler();

	boolean invokeGraphicsHandler(PGraphics pg);

	void addGraphicsHandler(Object obj, String methodName);

	void removeGraphicsHandler();

	/**
	 * TODO I think color and id should be left out of here. Rational: Interface should expose only methods that may
	 * attract third-parties interest and even though drawShape(pg) and invokeGraphicsHandler(pg) may appear to contradict
	 * that, maybe someone would like to draw the picking buffer contents by herself.
	 * 
	 * Maybe animation handler should be included. That's why I declared at the Model implementation some variables to
	 * handle it.
	 * 
	 * What about including: + Axes + Picking visual hint
	 * 
	 * What else?
	 */
}
