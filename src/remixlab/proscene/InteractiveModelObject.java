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
import remixlab.bias.event.*;
import remixlab.dandelion.core.AbstractScene;

/**
 * {@link remixlab.proscene.InteractiveModel} object which eases third-party implementation of the
 * {@link remixlab.proscene.InteractiveModel} interface.
 * <p>
 * Based on the concrete event type, this model object splits the {@link #performInteraction(BogusEvent)} method
 * into more specific versions of it, e.g., {@link #performInteraction(DOF6Event)},
 * {@link #performInteraction(KeyboardEvent)} and so on. Thus allowing implementations of this abstract
 * InteractiveModelObject to override only those method signatures that might be of their interest.
 * <p>
 * This interactive model object implementation also provided an algorithm to parse an
 * {@link remixlab.bias.core.Action} sequence from an init action variable, see {@link #processEvent(BogusEvent)}.
 *
 * @param <E> Reference action used to parameterize the {@link remixlab.proscene.InteractiveModel}
 */
public abstract class InteractiveModelObject<E extends Enum<E>> implements InteractiveModel<E> {
	Action<E>					action;
	protected Scene		scene;
	// protected Agent agent;
	protected int			id;
	protected PShape	pshape;
	
	// TODO new experimenting with textures
	protected PImage    tex;
		
	/**
	 * Constructs a interactive-model-object with a null {@link #shape()} and adds it to the
	 * {@link remixlab.proscene.Scene#models()} collection. Don't forget to call {@link #setShape(PShape)}.
	 * Third-parties should also add the interactive-model-object into some agents, see
	 * {@link remixlab.bias.core.Agent#addGrabber(InteractiveGrabber, Branch)}.
	 * 
	 * @see remixlab.proscene.Scene#addModel(Model)
	 * @see #shape()
	 * @see #setShape(PShape)
	 */
	public InteractiveModelObject(Scene scn) {
		scene = scn;
		if (scene.addModel(this))
			id = ++Scene.modelCount;
	}
	
	/**
	 * Wraps the pshape into this interactive-model-object and adds it to the
	 * {@link remixlab.proscene.Scene#models()} collection. Third-parties should add the interactive-model-object
	 * into some agents, see {@link remixlab.bias.core.Agent#addGrabber(InteractiveGrabber, Branch)}.
	 * 
	 * @see remixlab.proscene.Scene#addModel(Model)
	 */
	public InteractiveModelObject(Scene scn, PShape ps) {
		scene = scn;
		pshape = ps;
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

	/**
	 * Replaces previous {@link #shape()} with {@code ps}.
	 */
	public void setShape(PShape ps) {
		pshape = ps;
	}

	@Override
	public PShape shape() {
		return pshape;
	}

	/**
	 * Same as {@code draw(scene.pg())}.
	 * 
	 * @see remixlab.proscene.Scene#drawModels(PGraphics)
	 */
	public void draw() {
		if (shape() == null)
			return;
		PGraphics pg = scene.pg();
		draw(pg);
	}

	@Override
	public void draw(PGraphics pg) {
		if (shape() == null)
			return;
		pg.pushStyle();
		if (pg == scene.pickingBuffer()) {
			shape().disableStyle();
			if(tex!=null) shape().noTexture();
			pg.colorMode(PApplet.RGB, 255);
			pg.fill(getColor());
			pg.stroke(getColor());
		}
		pg.pushMatrix();
		pg.shape(shape());
		pg.popMatrix();
		if (pg == scene.pickingBuffer()) {
			if(tex!=null) shape().texture(tex);
			shape().enableStyle();
		}
		pg.popStyle();
	}
	
	@Override
	public boolean checkIfGrabsInput(BogusEvent event) {
		if (event instanceof KeyboardEvent)
			return checkIfGrabsInput((KeyboardEvent) event);
		if (event instanceof ClickEvent)
			return checkIfGrabsInput((ClickEvent) event);
		if (event instanceof MotionEvent)
			return checkIfGrabsInput((MotionEvent) event);
		return false;
	}
	
	/**
	 * Calls checkIfGrabsInput() on the proper motion event: {@link remixlab.bias.event.DOF1Event},
	 * {@link remixlab.bias.event.DOF2Event}, {@link remixlab.bias.event.DOF3Event} or {@link remixlab.bias.event.DOF6Event}.
	 */
	public boolean checkIfGrabsInput(MotionEvent event) {
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

	/**
	 * Override this method when you want the object to be picked from a {@link remixlab.bias.event.ClickEvent}. 
	 */
	protected boolean checkIfGrabsInput(ClickEvent event) {
		return checkIfGrabsInput(event.x(), event.y());
	}

	/**
	 * Override this method when you want the object to be picked from a {@link remixlab.bias.event.KeyboardEvent}. 
	 */
	protected boolean checkIfGrabsInput(KeyboardEvent event) {
		AbstractScene.showMissingImplementationWarning("checkIfGrabsInput(KeyboardEvent event)", this.getClass().getName());
		return false;
	}

	/**
	 * Override this method when you want the object to be picked from a {@link remixlab.bias.event.DOF1Event}. 
	 */
	protected boolean checkIfGrabsInput(DOF1Event event) {
		AbstractScene.showMissingImplementationWarning("checkIfGrabsInput(DOF1Event event)", this.getClass().getName());
		return false;
	}

	/**
	 * Override this method when you want the object to be picked from a {@link remixlab.bias.event.DOF2Event}. 
	 */
	protected boolean checkIfGrabsInput(DOF2Event event) {
		if (event.isAbsolute()) {
			System.out.println("Grabbing a gFrame is only possible from a relative MotionEvent or from a ClickEvent");
			return false;
		}
		return checkIfGrabsInput(event.x(), event.y());
	}


	/**
	 * An interactive-model-object is selected using <a href="http://schabby.de/picking-opengl-ray-tracing/">'ray-picking'</a>
     * with a color buffer (see {@link remixlab.proscene.Scene#pickingBuffer()}). This method compares the color of 
     * the {@link remixlab.proscene.Scene#pickingBuffer()} at at {@code (x,y)} with {@link #getColor()}.
     * Returns true if both colors are the same, and false otherwise.
	 */
	public final boolean checkIfGrabsInput(float x, float y) {
		scene.pickingBuffer().pushStyle();
		scene.pickingBuffer().colorMode(PApplet.RGB, 255);
		int index = (int) y * scene.width() + (int) x;
		if ((0 <= index) && (index < scene.pickingBuffer().pixels.length))
			return scene.pickingBuffer().pixels[index] == getColor();
		scene.pickingBuffer().popStyle();
		return false;
	}

	/**
	 * Override this method when you want the object to be picked from a {@link remixlab.bias.event.DOF3Event}. 
	 */
	protected boolean checkIfGrabsInput(DOF3Event event) {
		return checkIfGrabsInput(event.dof2Event());
	}

	/**
	 * Override this method when you want the object to be picked from a {@link remixlab.bias.event.DOF6Event}. 
	 */
	protected boolean checkIfGrabsInput(DOF6Event event) {
		return checkIfGrabsInput(event.dof3Event().dof2Event());
	}

	/**
	 * Check if this object is the {@link remixlab.bias.core.Agent#inputGrabber()}. Returns {@code true} if this object
	 * grabs the agent and {@code false} otherwise.
	 */
	public boolean grabsInput(Agent agent) {
		return agent.inputGrabber() == this;
	}
	
	/**
	 * Checks if the frame grabs input from any agent registered at the scene input handler.
	 */
	public boolean grabsInput() {
		for(Agent agent : scene.inputHandler().agents()) {
			if(agent.inputGrabber() == this)
				return true;
		}
		return false;
	}

	protected int getColor() {
		// see here: http://stackoverflow.com/questions/2262100/rgb-int-to-rgb-python
		return scene.pickingBuffer().color(id & 255, (id >> 8) & 255, (id >> 16) & 255);
	}

	// new is good

	@Override
	public void performInteraction(BogusEvent event) {
		if (processEvent(event))
			return;
		if (event instanceof KeyboardEvent)
			performInteraction((KeyboardEvent) event);
		if (event instanceof ClickEvent)
			performInteraction((ClickEvent) event);
		if (event instanceof MotionEvent)
			performInteraction((MotionEvent) event);
	}

	/**
	 * Override this method when you want the object to perform an interaction from a
	 * {@link remixlab.bias.event.KeyboardEvent}. 
	 */
	protected void performInteraction(KeyboardEvent event) {
		// AbstractScene.showMissingImplementationWarning("performInteraction(KeyboardEvent event)",
		// this.getClass().getName());
	}

	/**
	 * Override this method when you want the object to perform an interaction from a
	 * {@link remixlab.bias.event.ClickEvent}. 
	 */
	protected void performInteraction(ClickEvent event) {
		// AbstractScene.showMissingImplementationWarning("performInteraction(ClickEvent event)",
		// this.getClass().getName());
	}

	/**
	 * Calls performInteraction() on the proper motion event: {@link remixlab.bias.event.DOF1Event},
	 * {@link remixlab.bias.event.DOF2Event}, {@link remixlab.bias.event.DOF3Event} or {@link remixlab.bias.event.DOF6Event}.
	 * <p>
	 * Override this method when you want the object to perform an interaction from a
	 * {@link remixlab.bias.event.MotionEvent}. 
	 */
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

	/**
	 * Override this method when you want the object to perform an interaction from a
	 * {@link remixlab.bias.event.DOF1Event}. 
	 */
	protected void performInteraction(DOF1Event event) {
		// AbstractScene.showMissingImplementationWarning("performInteraction(DOF1Event event)", this.getClass().getName());
	}

	/**
	 * Override this method when you want the object to perform an interaction from a
	 * {@link remixlab.bias.event.DOF2Event}. 
	 */
	protected void performInteraction(DOF2Event event) {
		// AbstractScene.showMissingImplementationWarning("performInteraction(DOF2Event event)", this.getClass().getName());
	}

	/**
	 * Override this method when you want the object to perform an interaction from a
	 * {@link remixlab.bias.event.DOF3Event}. 
	 */
	protected void performInteraction(DOF3Event event) {
		// AbstractScene.showMissingImplementationWarning("performInteraction(DOF3Event event)", this.getClass().getName());
	}

	/**
	 * Override this method when you want the object to perform an interaction from a
	 * {@link remixlab.bias.event.DOF6Event}. 
	 */
	protected void performInteraction(DOF6Event event) {
		// AbstractScene.showMissingImplementationWarning("performInteraction(DOF6Event event)", this.getClass().getName());
	}

	Action<E>	initAction;
	
	/**
	 * Internal use. Algorithm to split a gesture flow into a 'three-tempi' {@link remixlab.bias.core.Action} sequence.
	 * Call it like this (see {@link #performInteraction(BogusEvent)}):
	 * <pre>
     * {@code
	 * public void performInteraction(BogusEvent event) {
	 *	if (processEvent(event))
	 *		return;
	 *	if (event instanceof KeyboardEvent)
	 *		performInteraction((KeyboardEvent) event);
	 *	if (event instanceof ClickEvent)
	 *		performInteraction((ClickEvent) event);
	 *	if (event instanceof MotionEvent)
	 *		performInteraction((MotionEvent) event);
	 * }
     * }
     * </pre>
	 * <p>
	 * The algorithm parses the bogus-event in {@link #performInteraction(BogusEvent)} and then decide what to call:
	 * <ol>
     * <li>{@link #initAction(BogusEvent)} (1st tempi): sets the initAction, called when initAction == null.</li>
     * <li>{@link #execAction(BogusEvent)} (2nd tempi): continues action execution, called when initAction == action()
     * (current action)</li>
     * <li>{@link #flushAction(BogusEvent)} (3rd): ends action, called when {@link remixlab.bias.core.BogusEvent#flushed()}
     * is true or when initAction != action()</li>
     * </ol>
     * <p>
     * Useful to parse multiple-tempi gestures, such as a mouse press/move/drag/release flow.
	 */
	protected final boolean processEvent(BogusEvent event) {
		if (initAction == null) {
			if (!event.flushed()) {
				return initAction(event);// start action
			}
		}
		else { // initAction != null
			if (!event.flushed()) {
				if (initAction == action())
					return execAction(event);// continue action
				else { // initAction != action() -> action changes abruptly, i.e.,
					// System.out.println("case 1");
					flushAction(event);
					return initAction(event);// start action
				}
			}
			else {// action() == null
				// System.out.println("case 2");
				flushAction(event);// stopAction
				initAction = null;
				//setAction(null); // experimental, but sounds logical since: initAction != null && action() == null
				return true;
			}
		}
		return true;// i.e., if initAction == action() == null -> ignore :)
	}

	/**
	 * Calls initAction() on the proper event type. Returns true when succeeded and false otherwise.
	 */
	protected boolean initAction(BogusEvent event) {
		initAction = action();
		if (event instanceof KeyboardEvent)
			return initAction((KeyboardEvent) event);
		if (event instanceof ClickEvent)
			return initAction((ClickEvent) event);
		if (event instanceof MotionEvent)
			return initAction((MotionEvent) event);
		return false;
	}

	/**
	 * Override this method when you want the object to init an action from a
	 * {@link remixlab.bias.event.KeyboardEvent}. 
	 */
	protected boolean initAction(KeyboardEvent event) {
		// AbstractScene.showMissingImplementationWarning("initAction(KeyboardEvent event)",
		// this.getClass().getName());
		return false;
	}

	/**
	 * Override this method when you want the object to init an action from a
	 * {@link remixlab.bias.event.ClickEvent}. 
	 */
	protected boolean initAction(ClickEvent event) {
		// AbstractScene.showMissingImplementationWarning("initAction(ClickEvent event)", this.getClass().getName());
		return false;
	}

	/**
	 * Calls initAction() on the proper motion event: {@link remixlab.bias.event.DOF1Event},
	 * {@link remixlab.bias.event.DOF2Event}, {@link remixlab.bias.event.DOF3Event} or {@link remixlab.bias.event.DOF6Event}. 
	 */
	protected boolean initAction(MotionEvent event) {
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

	/**
	 * Override this method when you want the object to init an action from a
	 * {@link remixlab.bias.event.DOF1Event}. 
	 */
	protected boolean initAction(DOF1Event event) {
		return false;
	}

	/**
	 * Override this method when you want the object to init an action from a
	 * {@link remixlab.bias.event.DOF2Event}. 
	 */
	protected boolean initAction(DOF2Event event) {
		return false;
	}

	/**
	 * Override this method when you want the object to init an action from a
	 * {@link remixlab.bias.event.DOF3Event}. 
	 */
	protected boolean initAction(DOF3Event event) {
		return false;
	}

	/**
	 * Override this method when you want the object to init an action from a
	 * {@link remixlab.bias.event.DOF6Event}. 
	 */
	protected boolean initAction(DOF6Event event) {
		return false;
	}

	protected boolean execAction(BogusEvent event) {
		if (event instanceof KeyboardEvent)
			return execAction((KeyboardEvent) event);
		if (event instanceof ClickEvent)
			return execAction((ClickEvent) event);
		if (event instanceof MotionEvent)
			return execAction((MotionEvent) event);
		return false;
	}

	/**
	 * Calls execAction() on the proper event type. Returns true when succeeded and false otherwise.
	 */
	protected boolean execAction(KeyboardEvent event) {
		// AbstractScene.showMissingImplementationWarning("execAction(KeyboardEvent event)",
		// this.getClass().getName());
		return false;
	}

	/**
	 * Override this method when you want the object to execute an action from a
	 * {@link remixlab.bias.event.ClickEvent}. 
	 */
	protected boolean execAction(ClickEvent event) {
		// AbstractScene.showMissingImplementationWarning("execAction(ClickEvent event)", this.getClass().getName());
		return false;
	}

	/**
	 * Calls execAction() on the proper motion event: {@link remixlab.bias.event.DOF1Event},
	 * {@link remixlab.bias.event.DOF2Event}, {@link remixlab.bias.event.DOF3Event} or {@link remixlab.bias.event.DOF6Event}.
	 */
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

	/**
	 * Override this method when you want the object to execute an action from a
	 * {@link remixlab.bias.event.DOF1Event}. 
	 */
	protected boolean execAction(DOF1Event event) {
		return false;
	}

	/**
	 * Override this method when you want the object to execute an action from a
	 * {@link remixlab.bias.event.DOF2Event}. 
	 */
	protected boolean execAction(DOF2Event event) {
		return false;
	}

	/**
	 * Override this method when you want the object to execute an action from a
	 * {@link remixlab.bias.event.DOF3Event}. 
	 */
	protected boolean execAction(DOF3Event event) {
		return false;
	}

	/**
	 * Override this method when you want the object to execute an action from a
	 * {@link remixlab.bias.event.DOF6Event}. 
	 */
	protected boolean execAction(DOF6Event event) {
		return false;
	}

	/** 
	 * Calls flushAction() on the proper event type. For consistency {@link #processEvent(BogusEvent)} should
	 * always return true after calling this one.
	 */
	protected void flushAction(BogusEvent event) {
		if (event instanceof KeyboardEvent)
			flushAction((KeyboardEvent) event);
		if (event instanceof ClickEvent)
			flushAction((ClickEvent) event);
		if (event instanceof MotionEvent)
			flushAction((MotionEvent) event);
	}

	/**
	 * Override this method when you want the object to flush an action from a
	 * {@link remixlab.bias.event.KeyboardEvent}. 
	 */
	protected void flushAction(KeyboardEvent event) {
	}

	/**
	 * Override this method when you want the object to flush an action from a
	 * {@link remixlab.bias.event.ClickEvent}. 
	 */
	protected void flushAction(ClickEvent event) {
	}

	/**
	 * Calls flushAction() on the proper motion event: {@link remixlab.bias.event.DOF1Event},
	 * {@link remixlab.bias.event.DOF2Event}, {@link remixlab.bias.event.DOF3Event} or {@link remixlab.bias.event.DOF6Event}.
	 */
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

	/**
	 * Override this method when you want the object to flush an action from a
	 * {@link remixlab.bias.event.DOF1Event}. 
	 */
	protected void flushAction(DOF1Event event) {

	}


	/**
	 * Override this method when you want the object to flush an action from a
	 * {@link remixlab.bias.event.DOF2Event}. 
	 */
	protected void flushAction(DOF2Event event) {

	}

	/**
	 * Override this method when you want the object to flush an action from a
	 * {@link remixlab.bias.event.DOF3Event}. 
	 */
	protected void flushAction(DOF3Event event) {

	}

	/**
	 * Override this method when you want the object to flush an action from a
	 * {@link remixlab.bias.event.DOF6Event}. 
	 */
	protected void flushAction(DOF6Event event) {

	}
}
