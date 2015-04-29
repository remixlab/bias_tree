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
import remixlab.bias.branch.Branch;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.core.AbstractScene;

public abstract class InteractiveModelObject<E extends Enum<E>> implements InteractiveModel<E> {
	Action<E>					action;
	protected Scene		scene;
	// protected Agent agent;
	protected int			id;
	protected PShape	pshape;

	// public ActionModelObject(Scene scn, Agent a, ActionAgent<E, ? extends Action<E>> actionAgent, PShape ps) {
	public <K extends Branch<E, ?/* extends Action<E> */>> InteractiveModelObject(Scene scn, Agent a, K actionAgent,
			PShape ps) {
		scene = scn;
		pshape = ps;
		if (scene.addModel(this))
			a.addGrabber(this, actionAgent);
		id = ++Scene.modelCount;
	}

	public <K extends Branch<E, ?/* extends Action<E> */>> InteractiveModelObject(Scene scn, Agent a, K actionAgent) {
		// public ActionModelObject(Scene scn, Agent a, ActionAgent<E, ? extends Action<E>> actionAgent) {
		scene = scn;
		if (scene.addModel(this))
			a.addGrabber(this, actionAgent);
		id = ++Scene.modelCount;
	}

	public InteractiveModelObject(Scene scn) {
		scene = scn;
		if (scene.addModel(this))
			id = ++Scene.modelCount;
	}

	public E referenceAction() {
		return action != null ? action.referenceAction() : null;
	}

	@Override
	public void setAction(Action<E> a) {
		action = a;
	}

	@Override
	public Action<E> action() {
		return action;
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
		if (event instanceof KeyboardEvent)
			return checkIfGrabsInput((KeyboardEvent) event);
		if (event instanceof ClickEvent)
			return checkIfGrabsInput((ClickEvent) event);
		if (event instanceof DOF1Event)
			return checkIfGrabsInput((DOF1Event) event);
		if (event instanceof DOF2Event)
			return checkIfGrabsInput((DOF2Event) event);
		if (event instanceof DOF3Event)
			return checkIfGrabsInput((DOF3Event) event);
		if (event instanceof DOF6Event)
			return checkIfGrabsInput((DOF6Event) event);
		return false;
	}

	protected boolean checkIfGrabsInput(ClickEvent event) {
		return checkIfGrabsInput(new DOF2Event(event.x(), event.y()));
	}

	protected boolean checkIfGrabsInput(KeyboardEvent event) {
		AbstractScene.showMissingImplementationWarning("checkIfGrabsInput(KeyboardEvent event)", this.getClass().getName());
		return false;
	}

	protected boolean checkIfGrabsInput(DOF1Event event) {
		AbstractScene.showMissingImplementationWarning("checkIfGrabsInput(DOF1Event event)", this.getClass().getName());
		return false;
	}
	
	protected boolean checkIfGrabsInput(DOF2Event event) {
		if(event.isAbsolute()) {
			System.out.println("Grabbing a gFrame is only possible from a relative MotionEvent or from a ClickEvent");
			return false;
		}
		return checkIfGrabsInput(event.x(), event.y());
	}
	
	protected final boolean checkIfGrabsInput(float x, float y) {
		scene.pickingBuffer().pushStyle();
		scene.pickingBuffer().colorMode(PApplet.RGB, 255);
		int index = (int) y * scene.width() + (int) x;
		if ((0 <= index) && (index < scene.pickingBuffer().pixels.length))
			return scene.pickingBuffer().pixels[index] == getColor();
		scene.pickingBuffer().popStyle();
		return false;
	}

	protected boolean checkIfGrabsInput(DOF3Event event) {
		return checkIfGrabsInput(event.dof2Event());
	}

	protected boolean checkIfGrabsInput(DOF6Event event) {
		return checkIfGrabsInput(event.dof3Event().dof2Event());
	}

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

	protected void performInteraction(KeyboardEvent event) {
		AbstractScene
				.showMissingImplementationWarning("performInteraction(KeyboardEvent event)", this.getClass().getName());
	}

	protected void performInteraction(ClickEvent event) {
		AbstractScene.showMissingImplementationWarning("performInteraction(ClickEvent event)", this.getClass().getName());
	}

	protected void performInteraction(DOF1Event event) {
		AbstractScene.showMissingImplementationWarning("performInteraction(DOF1Event event)", this.getClass().getName());
	}

	protected void performInteraction(DOF2Event event) {
		AbstractScene.showMissingImplementationWarning("performInteraction(DOF2Event event)", this.getClass().getName());
	}

	protected void performInteraction(DOF3Event event) {
		AbstractScene.showMissingImplementationWarning("performInteraction(DOF3Event event)", this.getClass().getName());
	}

	protected void performInteraction(DOF6Event event) {
		AbstractScene.showMissingImplementationWarning("performInteraction(DOF6Event event)", this.getClass().getName());
	}

	protected int getColor() {
		// see here: http://stackoverflow.com/questions/2262100/rgb-int-to-rgb-python
		return scene.pickingBuffer().color(id & 255, (id >> 8) & 255, (id >> 16) & 255);
	}
}
