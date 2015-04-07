
package remixlab.proscene;

import processing.core.*;
import remixlab.bias.core.Agent;
import remixlab.bias.event.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

public class GrabberModelFrame extends GrabberFrame implements Model {
	// TODO complete hashCode and equals, once the rest is done

	protected PShape	pshape;
	protected int			id;

	public GrabberModelFrame(Scene scn) {
		super(scn);
		((Scene) scene).addModel(this);
		id = ++Scene.modelCount;
	}

	public GrabberModelFrame(Scene scn, Frame referenceFrame) {
		super(scn, referenceFrame);
		((Scene) scene).addModel(this);
		id = ++Scene.modelCount;
	}

	@Override
	public PShape shape() {
		return pshape;
	}

	public void setShape(PShape ps) {
		pshape = ps;
	}

	@Override
	public boolean checkIfGrabsInput(DOF2Event event) {
		((Scene) scene).pickingBuffer().pushStyle();
		((Scene) scene).pickingBuffer().colorMode(PApplet.RGB, 255);
		int index = (int) event.y() * scene.width() + (int) event.x();
		if ((0 <= index) && (index < ((Scene) scene).pickingBuffer().pixels.length))
			return ((Scene) scene).pickingBuffer().pixels[index] == getColor();
		((Scene) scene).pickingBuffer().popStyle();
		return false;
	}

	public void draw() {
		if (shape() == null)
			return;
		PGraphics pg = ((Scene) scene).pg();
		draw(pg);
	}

	// TODO doc: remember to mention bind(false);
	@Override
	public void draw(PGraphics pg) {
		if (shape() == null)
			return;
		pg.pushStyle();
		if (pg == ((Scene) scene).pickingBuffer()) {
			shape().disableStyle();
			pg.colorMode(PApplet.RGB, 255);
			pg.fill(getColor());
			pg.stroke(getColor());
		}
		pg.pushMatrix();
		((Scene) scene).applyWorldTransformation(pg, this);
		pg.shape(shape());
		pg.popMatrix();
		if (pg == ((Scene) scene).pickingBuffer())
			shape().enableStyle();
		pg.popStyle();
	}

	public void applyTransformation(PGraphics pg) {
		((Scene) scene).applyTransformation(pg, this);
	}

	public void applyWorldTransformation(PGraphics pg) {
		((Scene) scene).applyWorldTransformation(pg, this);
	}

	protected int getColor() {
		// see here: http://stackoverflow.com/questions/2262100/rgb-int-to-rgb-python
		return ((Scene) scene).pickingBuffer().color(id & 255, (id >> 8) & 255, (id >> 16) & 255);
	}
	
	public boolean grabsInput(Agent agent) {
		return agent.inputGrabber() == this;
	}
}
