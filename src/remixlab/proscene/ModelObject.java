
package remixlab.proscene;

import processing.core.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;

public abstract class ModelObject implements Model {
	protected Scene		scene;
	protected int			id;
	protected PShape	pshape;

	public ModelObject(Scene scn, PShape ps) {
		scene = scn;
		pshape = ps;
		id = scene.models().size();
		scene.addModel(this);
	}

	public ModelObject(Scene scn) {
		scene = scn;
		id = scene.models().size();
		scene.addModel(this);
	}

	public void setShape(PShape ps) {
		pshape = ps;
	}

	@Override
	public PShape shape() {
		return pshape;
	}

	public void draw() {
		if (shape() == null)
			return;
		PGraphics pg = scene.pg();
		draw(pg);
	}

	// TODO doc: remember to mention bind(false);
	@Override
	public void draw(PGraphics pg) {
		if (shape() == null)
			return;
		pg.pushStyle();
		if (pg == scene.pickingBuffer()) {
			shape().disableStyle();
			pg.colorMode(PApplet.RGB, 255);
			pg.fill(getColor(id));
			pg.stroke(getColor(id));
		}
		pg.pushMatrix();
		// ((Scene) scene).applyWorldTransformation(pg, this);//needs testing! needs bind
		pg.shape(shape());
		pg.popMatrix();
		if (pg == scene.pickingBuffer())
			shape().enableStyle();
		pg.popStyle();
	}

	@Override
	public boolean checkIfGrabsInput(BogusEvent event) {
		DOF2Event event2 = null;

		if (((event instanceof KeyboardEvent)) || (event instanceof DOF1Event))
			throw new RuntimeException("Grabbing a ModelObject is not possible with a "
					+ ((event instanceof KeyboardEvent) ? "Keyboard" : "DOF1") + "Event");

		if (event instanceof DOF2Event)
			event2 = ((DOF2Event) event).get();
		else if (event instanceof DOF3Event)
			event2 = ((DOF3Event) event).dof2Event();
		else if (event instanceof DOF6Event)
			event2 = ((DOF6Event) event).dof3Event().dof2Event();
		else if (event instanceof ClickEvent)
			event2 = new DOF2Event(((ClickEvent) event).x(), ((ClickEvent) event).y());

		scene.pickingBuffer().pushStyle();
		scene.pickingBuffer().colorMode(PApplet.RGB, 255);
		int index = (int) event2.y() * scene.width() + (int) event2.x();
		if ((0 <= index) && (index < scene.pickingBuffer().pixels.length)) {
			int pick = scene.pickingBuffer().pixels[index];
			return getID(pick) == id;
		}
		scene.pickingBuffer().popStyle();
		return false;
	}

	@Override
	public boolean grabsInput(Agent agent) {
		return agent.inputGrabber() == this;
	}

	protected int getColor(int id) {
		return scene.pApplet().color(10 + id, 20 + id, 30 + id);
	}

	protected int getID(int c) {
		int r = (int) scene.pApplet().red(c);
		return r - 10;
	}
}
