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

import remixlab.bias.event.*;

public abstract class InteractiveGrabberObject<E extends Enum<E>> implements InteractiveGrabber<E>
/*
 * , Grabber //
 */
{
	Action<E>	action;

	/**
	 * Empty constructor.
	 */
	public InteractiveGrabberObject() {
	}

	/**
	 * Constructs and adds this grabber to the agent pool.
	 * 
	 * @see remixlab.bias.core.Agent#grabbers()
	 */
	public InteractiveGrabberObject(Agent agent, Branch<E, ? extends Action<E>, ?> actionAgent) {
		agent.addGrabber(this, actionAgent);
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

	/**
	 * Check if this object is the {@link remixlab.bias.core.Agent#inputGrabber()}. Returns {@code true} if this object
	 * grabs the agent and {@code false} otherwise.
	 */
	public boolean grabsInput(Agent agent) {
		return agent.inputGrabber() == this;
	}

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

	// TODO : deal with warnings
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

	protected boolean checkIfGrabsInput(KeyboardEvent event) {
		// AbstractScene.showMissingImplementationWarning("checkIfGrabsInput(KeyboardEvent event)",
		// this.getClass().getName());
		return false;
	}

	protected boolean checkIfGrabsInput(ClickEvent event) {
		// AbstractScene.showMissingImplementationWarning("checkIfGrabsInput(ClickEvent event)", this.getClass().getName());
		return false;
	}

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

	protected boolean checkIfGrabsInput(DOF1Event event) {
		return false;
	}

	protected boolean checkIfGrabsInput(DOF2Event event) {
		return false;
	}

	protected boolean checkIfGrabsInput(DOF3Event event) {
		return false;
	}

	protected boolean checkIfGrabsInput(DOF6Event event) {
		return false;
	}

	Action<E>	initAction;

	/**
	 * TODO: fix docs as the following is only partially true Should always return true after calling
	 * {@link #flushAction(BogusEvent)}. Otherwise the null action may be enqueued to
	 * {@link #performInteraction(BogusEvent)} which will then causes the infamous null pointer exception.
	 */
	public final boolean processEvent(BogusEvent event) {
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
					// System.out.println("case 1 in frame: action() != null && initAction != null (action changes abruptely, calls flush)");
					flushAction(event);
					return initAction(event);// start action
				}
			}
			else {// action() == null
				// System.out.println("case 2 in frame: action() == null && initAction != null (ends action, calls flush)");
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
		if (event instanceof MotionEvent)
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
		if (event instanceof MotionEvent)
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
	 * {@link #processEvent(BogusEvent)} should always return true after calling this one.
	 */
	protected void flushAction(BogusEvent event) {
		if (event instanceof KeyboardEvent)
			flushAction((KeyboardEvent) event);
		if (event instanceof ClickEvent)
			flushAction((ClickEvent) event);
		if (event instanceof MotionEvent)
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