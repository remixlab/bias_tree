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
	public <K extends Branch<E, ?/* extends Action<E> */, ?>> InteractiveModelObject(Scene scn, Agent a, K actionAgent,
			PShape ps) {
		scene = scn;
		pshape = ps;
		if (scene.addModel(this))
			a.addGrabber(this, actionAgent);
		id = ++Scene.modelCount;
	}

	public <K extends Branch<E, ?/* extends Action<E> */, ?>> InteractiveModelObject(Scene scn, Agent a, K actionAgent) {
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
		return checkIfGrabsInput(event.x(), event.y());
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
		if (event.isAbsolute()) {
			System.out.println("Grabbing a gFrame is only possible from a relative MotionEvent or from a ClickEvent");
			return false;
		}
		return checkIfGrabsInput(event.x(), event.y());
	}

	public final boolean checkIfGrabsInput(float x, float y) {
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

	protected int getColor() {
		// see here: http://stackoverflow.com/questions/2262100/rgb-int-to-rgb-python
		return scene.pickingBuffer().color(id & 255, (id >> 8) & 255, (id >> 16) & 255);
	}
	
	// new is good
	
	@Override
	public void performInteraction(BogusEvent event) {
		if (processAction(event))
			return;
		if (event instanceof KeyboardEvent)
			performInteraction((KeyboardEvent) event);
		if (event instanceof ClickEvent)
			performInteraction((ClickEvent) event);
		if(event instanceof MotionEvent)
			performInteraction((MotionEvent) event);
	}
	
  //TODO : deal with warnings
	protected void performInteraction(KeyboardEvent event) {
		// AbstractScene.showMissingImplementationWarning("performInteraction(KeyboardEvent event)",
		// this.getClass().getName());
	}

	protected void performInteraction(ClickEvent event) {
		// AbstractScene.showMissingImplementationWarning("performInteraction(ClickEvent event)",
		// this.getClass().getName());
	}
	
	protected void performInteraction(MotionEvent event) {
		if (event instanceof DOF1Event)
			performInteraction((DOF1Event) event);
		if (event instanceof DOF2Event)
			performInteraction((DOF2Event) event);
		if (event instanceof DOF3Event)
			performInteraction((DOF3Event) event);
		if (event instanceof DOF6Event)
			performInteraction((DOF6Event) event);
	}

	protected void performInteraction(DOF1Event event) {
		// AbstractScene.showMissingImplementationWarning("performInteraction(DOF1Event event)", this.getClass().getName());
	}

	protected void performInteraction(DOF2Event event) {
		// AbstractScene.showMissingImplementationWarning("performInteraction(DOF2Event event)", this.getClass().getName());
	}

	protected void performInteraction(DOF3Event event) {
		// AbstractScene.showMissingImplementationWarning("performInteraction(DOF3Event event)", this.getClass().getName());
	}

	protected void performInteraction(DOF6Event event) {
		// AbstractScene.showMissingImplementationWarning("performInteraction(DOF6Event event)", this.getClass().getName());
	}
	
Action<E>	initAction;
	
	/**
	 * TODO: fix docs as the following is only partially true
	 * Should always return true after calling {@link #flushAction(BogusEvent)}. Otherwise the null action may be enqueued
	 * to {@link #performInteraction(BogusEvent)} which will then causes the infamous null pointer exception.
	 */
	@Override
	public final boolean processAction(BogusEvent event) {
		if (initAction == null) {
			if (action() != null) {
				return initAction(event);// start action
			}
		}
		else { // initAction != null
			if (action() != null) {
				if (initAction == action())
					return execAction(event);// continue action
				else { // initAction != action() -> action changes abruptly, i.e.,
					//System.out.println("case 1");
					flushAction(event);
					return initAction(event);// start action
				}
			}
			else {// action() == null
				//System.out.println("case 2");
				flushAction(event);// stopAction
				initAction = null;
				setAction(null); // experimental, but sounds logical since: initAction != null && action() == null
				return true;
			}
		}
		return true;// i.e., if initAction == action() == null -> ignore :)
	}
	
	protected boolean initAction(BogusEvent event) {
		initAction = action();
		if (event instanceof KeyboardEvent)
			return initAction((KeyboardEvent) event);
		if (event instanceof ClickEvent)		
			return initAction((ClickEvent) event);
		if(event instanceof MotionEvent)
			return initAction((MotionEvent) event);		
		return false;
	}
	
	protected boolean initAction(KeyboardEvent event) {
		// AbstractScene.showMissingImplementationWarning("initAction(KeyboardEvent event)",
		// this.getClass().getName());
		return false;
	}

	protected boolean initAction(ClickEvent event) {
		// AbstractScene.showMissingImplementationWarning("initAction(ClickEvent event)", this.getClass().getName());
		return false;
	}
	
	public boolean initAction(MotionEvent event) {
		if (event instanceof DOF1Event)
			return initAction((DOF1Event) event);
		if (event instanceof DOF2Event)
			return initAction((DOF2Event) event);
		if (event instanceof DOF3Event)
			return initAction((DOF3Event) event);
		if (event instanceof DOF6Event)
			return initAction((DOF6Event) event);
		return false;
	}

	protected boolean initAction(DOF1Event event) {
		return false;
	}

	protected boolean initAction(DOF2Event event) {
		return false;
	}

	protected boolean initAction(DOF3Event event) {
		return false;
	}

	protected boolean initAction(DOF6Event event) {
		return false;
	}	
	
	protected boolean execAction(BogusEvent event) {
		if (event instanceof KeyboardEvent)
			return execAction((KeyboardEvent) event);
		if (event instanceof ClickEvent)		
			return execAction((ClickEvent) event);
		if(event instanceof MotionEvent)
			return execAction((MotionEvent) event);
		return false;
	}
	
	protected boolean execAction(KeyboardEvent event) {
		// AbstractScene.showMissingImplementationWarning("execAction(KeyboardEvent event)",
		// this.getClass().getName());
		return false;
	}

	protected boolean execAction(ClickEvent event) {
		// AbstractScene.showMissingImplementationWarning("execAction(ClickEvent event)", this.getClass().getName());
		return false;
	}
	
	public boolean execAction(MotionEvent event) {
		if (event instanceof DOF1Event)
			return execAction((DOF1Event) event);
		if (event instanceof DOF2Event)
			return execAction((DOF2Event) event);
		if (event instanceof DOF3Event)
			return execAction((DOF3Event) event);
		if (event instanceof DOF6Event)
			return execAction((DOF6Event) event);
		return false;
	}

	protected boolean execAction(DOF1Event event) {
		return false;
	}

	protected boolean execAction(DOF2Event event) {
		return false;
	}

	protected boolean execAction(DOF3Event event) {
		return false;
	}

	protected boolean execAction(DOF6Event event) {
		return false;
	}
	
	/**
	 * {@link #processAction(BogusEvent)} should always return true after calling this one.
	 */
	protected void flushAction(BogusEvent event) {
		if (event instanceof KeyboardEvent)
			flushAction((KeyboardEvent) event);
		if (event instanceof ClickEvent)		
			flushAction((ClickEvent) event);
		if(event instanceof MotionEvent)
			flushAction((MotionEvent) event);
	}
	
	protected void flushAction(KeyboardEvent event) {
	}

	protected void flushAction(ClickEvent event) {
	}
	
	public void flushAction(MotionEvent event) {
		if (event instanceof DOF1Event)
			flushAction((DOF1Event) event);
		if (event instanceof DOF2Event)
			flushAction((DOF2Event) event);
		if (event instanceof DOF3Event)
			flushAction((DOF3Event) event);
		if (event instanceof DOF6Event)
			flushAction((DOF6Event) event);
	}

	protected void flushAction(DOF1Event event) {
		
	}

	protected void flushAction(DOF2Event event) {
		
	}

	protected void flushAction(DOF3Event event) {
		
	}

	protected void flushAction(DOF6Event event) {
		
	}
}
