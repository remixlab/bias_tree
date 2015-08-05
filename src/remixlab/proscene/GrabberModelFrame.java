
package remixlab.proscene;

import processing.core.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

public class GrabberModelFrame extends GrabberFrame implements Model {
	// TODO complete hashCode and equals, once the rest is done

	protected PShape	pshape;
	protected int			id;

	public GrabberModelFrame(Scene scn) {
		super(scn);
		((Scene) gScene).addModel(this);
		id = ++Scene.modelCount;
	}

	public GrabberModelFrame(Scene scn, Frame referenceFrame) {
		super(scn, referenceFrame);
		((Scene) gScene).addModel(this);
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
	public boolean checkIfGrabsInput(float x, float y) {
		((Scene) gScene).pickingBuffer().pushStyle();
		((Scene) gScene).pickingBuffer().colorMode(PApplet.RGB, 255);
		int index = (int) y * gScene.width() + (int) x;
		if ((0 <= index) && (index < ((Scene) gScene).pickingBuffer().pixels.length))
			return ((Scene) gScene).pickingBuffer().pixels[index] == getColor();
		((Scene) gScene).pickingBuffer().popStyle();
		return false;
	}

	public void draw() {
		if (shape() == null)
			return;
		PGraphics pg = ((Scene) gScene).pg();
		draw(pg);
	}

	// TODO doc: remember to mention bind(false);
	@Override
	public void draw(PGraphics pg) {
		if (shape() == null)
			return;
		pg.pushStyle();
		if (pg == ((Scene) gScene).pickingBuffer()) {
			shape().disableStyle();
			pg.colorMode(PApplet.RGB, 255);
			pg.fill(getColor());
			pg.stroke(getColor());
		}
		pg.pushMatrix();
		((Scene) gScene).applyWorldTransformation(pg, this);
		pg.shape(shape());
		pg.popMatrix();
		if (pg == ((Scene) gScene).pickingBuffer())
			shape().enableStyle();
		pg.popStyle();
	}

	public void applyTransformation(PGraphics pg) {
		((Scene) gScene).applyTransformation(pg, this);
	}

	public void applyWorldTransformation(PGraphics pg) {
		((Scene) gScene).applyWorldTransformation(pg, this);
	}

	protected int getColor() {
		// see here: http://stackoverflow.com/questions/2262100/rgb-int-to-rgb-python
		return ((Scene) gScene).pickingBuffer().color(id & 255, (id >> 8) & 255, (id >> 16) & 255);
	}
}