/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.core;

import remixlab.bias.event.ClickEvent;
import remixlab.bias.event.DOF1Event;
import remixlab.bias.event.DOF2Event;
import remixlab.bias.event.DOF3Event;
import remixlab.bias.event.DOF6Event;
import remixlab.bias.event.KeyboardEvent;
import remixlab.dandelion.core.AbstractScene;

/**
 * Default implementation of the Grabber interface which eases implementation by simply overriding
 * {@link #grabsInput(AbstractAgent)}.
 */
public abstract class GrabberObject implements Grabber {
	/**
	 * Empty constructor.
	 */
	public GrabberObject() {
	}

	/**
	 * Constructs and adds this grabber to the agent pool.
	 * 
	 * @see remixlab.bias.core.AbstractAgent#pool()
	 */
	public GrabberObject(AbstractAgent agent) {
		agent.addInPool(this);
	}

	/**
	 * Constructs and adds this grabber to all agents belonging to the input handler.
	 * 
	 * @see remixlab.bias.core.InputHandler#agents()
	 */
	public GrabberObject(InputHandler inputHandler) {
		for (AbstractAgent agent : inputHandler.agents())
			agent.addInPool(this);
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

	public boolean checkIfGrabsInput(KeyboardEvent event) {
		AbstractScene.showMissingImplementationWarning("checkIfGrabsInput(KeyboardEvent event)", this.getClass().getName());
		return false;
	}

	public boolean checkIfGrabsInput(ClickEvent event) {
		AbstractScene.showMissingImplementationWarning("checkIfGrabsInput(ClickEvent event)", this.getClass().getName());
		return false;
	}

	public boolean checkIfGrabsInput(DOF1Event event) {
		AbstractScene.showMissingImplementationWarning("checkIfGrabsInput(DOF1Event event)", this.getClass().getName());
		return false;
	}

	public boolean checkIfGrabsInput(DOF2Event event) {
		AbstractScene.showMissingImplementationWarning("checkIfGrabsInput(DOF2Event event)", this.getClass().getName());
		return false;
	}

	public boolean checkIfGrabsInput(DOF3Event event) {
		AbstractScene.showMissingImplementationWarning("checkIfGrabsInput(DOF3Event event)", this.getClass().getName());
		return false;
	}

	public boolean checkIfGrabsInput(DOF6Event event) {
		AbstractScene.showMissingImplementationWarning("checkIfGrabsInput(DOF6Event event)", this.getClass().getName());
		return false;
	}
}
