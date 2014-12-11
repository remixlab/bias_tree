package remixlab.proscene;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import remixlab.bias.core.Action;
import remixlab.bias.core.ActionGrabber;
import remixlab.bias.core.Agent;
import remixlab.bias.core.BogusEvent;
import remixlab.bias.event.ClickEvent;
import remixlab.bias.event.DOF1Event;
import remixlab.bias.event.DOF2Event;
import remixlab.bias.event.DOF3Event;
import remixlab.bias.event.DOF6Event;
import remixlab.bias.event.KeyboardEvent;
import remixlab.dandelion.core.AbstractScene;


public abstract class ActionModelObject<E extends Enum<E>> implements 
/*
Model
// */
// /*
ActionModel<E>
// */
{
	
	E globalAction;	
	protected Scene		scene;
	protected int			id;
	protected PShape	pshape;

	public ActionModelObject(Scene scn, PShape ps) {
		scene = scn;
		pshape = ps;
		scene.addModel(this);
		id = ++Scene.modelCount;
	}

	public ActionModelObject(Scene scn) {
		scene = scn;
		scene.addModel(this);
		id = ++Scene.modelCount;
	}
	
	public E referenceAction() {
		return globalAction;
	}
	
	public void setReferenceAction(Action<E> a) {
		globalAction = a.referenceAction();
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
			pg.fill(getColor());
			pg.stroke(getColor());
		}
		pg.pushMatrix();
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
		if ((0 <= index) && (index < scene.pickingBuffer().pixels.length))
			return scene.pickingBuffer().pixels[index] == getColor();
		scene.pickingBuffer().popStyle();
		return false;
	}

	@Override
	public boolean grabsInput(Agent agent) {
		return agent.inputGrabber() == this;
	}

	@Override
	public void performInteraction(BogusEvent event) {
		if (event instanceof KeyboardEvent)
			performInteraction((KeyboardEvent) event);
		if (event instanceof ClickEvent)
			performInteraction((ClickEvent) event);
		if (event instanceof DOF1Event)
			performInteraction((DOF1Event) event);
		if (event instanceof DOF2Event)
			performInteraction((DOF2Event) event);
		if (event instanceof DOF3Event)
			performInteraction((DOF3Event) event);
		if (event instanceof DOF6Event)
			performInteraction((DOF6Event) event);
	}

	public void performInteraction(KeyboardEvent event) {
		AbstractScene
				.showMissingImplementationWarning("performInteraction(KeyboardEvent event)", this.getClass().getName());
	}

	public void performInteraction(ClickEvent event) {
		AbstractScene.showMissingImplementationWarning("performInteraction(ClickEvent event)", this.getClass().getName());
	}

	public void performInteraction(DOF1Event event) {
		AbstractScene.showMissingImplementationWarning("performInteraction(DOF1Event event)", this.getClass().getName());
	}

	public void performInteraction(DOF2Event event) {
		AbstractScene.showMissingImplementationWarning("performInteraction(DOF2Event event)", this.getClass().getName());
	}

	public void performInteraction(DOF3Event event) {
		AbstractScene.showMissingImplementationWarning("performInteraction(DOF3Event event)", this.getClass().getName());
	}

	public void performInteraction(DOF6Event event) {
		AbstractScene.showMissingImplementationWarning("performInteraction(DOF6Event event)", this.getClass().getName());
	}

	protected int getColor() {
		// see here: http://stackoverflow.com/questions/2262100/rgb-int-to-rgb-python
		return scene.pickingBuffer().color(id & 255, (id >> 8) & 255, (id >> 16) & 255);
	}
}
