
package remixlab.bias.grabber;

import remixlab.bias.agent.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;

public abstract class ActionGrabberObject<E extends Enum<E>> implements ActionGrabber<E>
/*
 * , Grabber //
 */
{
	Action<E>	action;

	/**
	 * Empty constructor.
	 */
	public ActionGrabberObject() {
	}

	/**
	 * Constructs and adds this grabber to the agent pool.
	 * 
	 * @see remixlab.bias.core.AbstractAgent#pool()
	 */
	//TODO improve type safety here!
	public ActionGrabberObject(ActionAgent<?,?> agent) {
		agent.addInPool(this);
	}

	public E referenceAction() {
		return action != null ? action.referenceAction() : null;
	}

	@Override
	public Action<E> action() {
		return action;
	}

	@Override
	public void setAction(Action<E> a) {
		action = a;
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

	//TODO : deal with warnings
	public void performInteraction(KeyboardEvent event) {
		//AbstractScene.showMissingImplementationWarning("performInteraction(KeyboardEvent event)", this.getClass().getName());
	}

	public void performInteraction(ClickEvent event) {
		//AbstractScene.showMissingImplementationWarning("performInteraction(ClickEvent event)", this.getClass().getName());
	}

	public void performInteraction(DOF1Event event) {
		//AbstractScene.showMissingImplementationWarning("performInteraction(DOF1Event event)", this.getClass().getName());
	}

	public void performInteraction(DOF2Event event) {
		//AbstractScene.showMissingImplementationWarning("performInteraction(DOF2Event event)", this.getClass().getName());
	}

	public void performInteraction(DOF3Event event) {
		//AbstractScene.showMissingImplementationWarning("performInteraction(DOF3Event event)", this.getClass().getName());
	}

	public void performInteraction(DOF6Event event) {
		//AbstractScene.showMissingImplementationWarning("performInteraction(DOF6Event event)", this.getClass().getName());
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
		//AbstractScene.showMissingImplementationWarning("checkIfGrabsInput(KeyboardEvent event)", this.getClass().getName());
		return false;
	}

	public boolean checkIfGrabsInput(ClickEvent event) {
		//AbstractScene.showMissingImplementationWarning("checkIfGrabsInput(ClickEvent event)", this.getClass().getName());
		return false;
	}

	public boolean checkIfGrabsInput(DOF1Event event) {
		//AbstractScene.showMissingImplementationWarning("checkIfGrabsInput(DOF1Event event)", this.getClass().getName());
		return false;
	}

	public boolean checkIfGrabsInput(DOF2Event event) {
		//AbstractScene.showMissingImplementationWarning("checkIfGrabsInput(DOF2Event event)", this.getClass().getName());
		return false;
	}

	public boolean checkIfGrabsInput(DOF3Event event) {
		//AbstractScene.showMissingImplementationWarning("checkIfGrabsInput(DOF3Event event)", this.getClass().getName());
		return false;
	}

	public boolean checkIfGrabsInput(DOF6Event event) {
		//AbstractScene.showMissingImplementationWarning("checkIfGrabsInput(DOF6Event event)", this.getClass().getName());
		return false;
	}
}
