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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import remixlab.bias.branch.*;
import remixlab.bias.event.*;

public class Agent {
	protected String										nm;
	protected List<Branch<?, ?, ?>>			brnchs;
	protected List<Grabber>	grabberList;
	protected Branch<?, ?, ?>				trackedGrabberBranch, defaultGrabberBranch;
	protected Grabber trackedGrabber, defaultGrabber;
	protected boolean							agentTrckn;
	protected InputHandler							handler;

	/**
	 * Constructs an Agent with the given name and registers is at the given inputHandler.
	 */
	public Agent(InputHandler inputHandler, String name) {
		nm = name;
		grabberList = new ArrayList<Grabber>();
		brnchs = new ArrayList<Branch<?, ?, ?>>();
		setTracking(true);
		handler = inputHandler;
		handler.registerAgent(this);
	}

	/**
	 * @return Agents name
	 */
	public String name() {
		return nm;
	}

	/**
	 * Removes the grabber from the {@link #grabbers()} list.
	 * <p>
	 * See {@link #addGrabber(Grabber)} for details. Removing a grabber that is not in {@link #grabbers()} has no effect.
	 */	
	public boolean removeGrabber(Grabber grabber) {
		if(defaultGrabber() == grabber)
			setDefaultGrabber(null);
		if(trackedGrabber() == grabber)
			trackedGrabber = null;
		if(grabberList.remove(grabber))
			return true;
		for (int i = 0; i < brnchs.size(); i++) {
			if(brnchs.get(i).removeGrabber(grabber)) {
				//if(grabber == this.trackedGrabber())
					//tGrabberBranch = null;
				return true;
			}
		}
		return false;
	}

	/**
	 * Clears the {@link #grabbers()} list.
	 */	
	public void removeGrabbers() {
		setDefaultGrabber(null);
		trackedGrabber = null;
		//tGrabberBranch = null;		
		grabberList.clear();		
		for (Iterator<Branch<?, ?, ?>> it = brnchs.iterator(); it.hasNext();)
			it.next().reset();
	}
	
	public List<Grabber> grabbers() {
		List<Grabber> pool = new ArrayList<Grabber>();		
		pool.removeAll(grabberList);
		pool.addAll(grabberList);		
		for (int i = 0; i < brnchs.size(); i++) {
			pool.removeAll(brnchs.get(i).grabbers());
			pool.addAll(brnchs.get(i).grabbers());
		}		
		return pool;
	}

	/**
	 * Returns true if the grabber is currently in the agents {@link #grabbers()} list.
	 * <p>
	 * When set to false using {@link #removeGrabber(Grabber)}, the handler no longer
	 * {@link remixlab.bias.core.Grabber#checkIfGrabsInput(BogusEvent)} on this grabber. Use {@link #addGrabber(Grabber)}
	 * to insert it back.
	 */
	public boolean hasGrabber(Grabber grabber) {
		if (grabber == null)
			return false;
		// return grabbers().contains(grabber);
		// /*
		for (Grabber g : grabbers())
			if (g == grabber)
				return true;
		return false;
		// */
	}

	/**
	 * Adds the grabber in the {@link #grabbers()}.
	 * <p>
	 * Use {@link #removeGrabber(Grabber)} to remove the grabber from the pool, so that it is no longer tested with
	 * {@link remixlab.bias.core.Grabber#checkIfGrabsInput(BogusEvent)} by the handler, and hence can no longer grab the
	 * agent focus. Use {@link #hasGrabber(Grabber)} to know the current state of the grabber.
	 */
	public boolean addGrabber(Grabber grabber) {
		if (grabber == null)
			return false;
		if (hasGrabber(grabber))
			return false;
		if (grabber instanceof InteractiveGrabber) {
			System.err.println("use addGrabber(G grabber, K actionAgent) instead");
			return false;
		}
		return grabberList.add(grabber);
	}
	
	public <E extends Enum<E>> List<InteractiveGrabber<E>> grabbers(Branch<E, ?, ?> branch) {
		return branch.grabbers();
	}
	
	public <E extends Enum<E>, K extends Branch<E, ?, ?>, G extends InteractiveGrabber<E>> boolean addGrabber(G grabber, K branch) {
		if(branch == null)
			return false;
		if (!hasBranch(branch))
			if (!this.appendBranch(branch))
				return false;
		return branch.addGrabber(grabber);
	}

	public Branch<?, ?, ?> branch(Grabber g) {
		for (Branch<?, ?, ?> b : branches())
			if (b.hasGrabber(g))
					return b;
		return null;
	}

	public List<Branch<?, ?, ?>> branches() {
		return brnchs;
	}

	public boolean appendBranch(Branch<?, ?, ?> branch) {
		if (branch == null)
			return false;
		if (!brnchs.contains(branch)) {
			this.brnchs.add(branch);
			return true;
		}
		return false;
	}

	public boolean hasBranch(Branch<?, ?, ?> branch) {
		return brnchs.contains(branch);
	}
	
	public void resetBranch(Branch<?, ?, ?> branch) {
		branch.reset();
		if(branch == this.defaultGrabberBranch)
			setDefaultGrabber(null);
		if(branch == this.trackedGrabberBranch)
			trackedGrabber = null;
			//tGrabberBranch = null;
	}

	public void resetBranches() {
		for (Branch<?, ?, ?> branch : branches())
			resetBranch(branch);
	}

	public boolean pruneBranch(Branch<?, ?, ?> branch) {
		if (brnchs.contains(branch)) {
			this.resetBranch(branch);
			this.brnchs.remove(branch);
			return true;
		}
		return false;
	}

	public void pruneBranches() {
		resetBranches();
		branches().clear();
	}

	/**
	 * Returns a detailed description of this Agent as a String.
	 */
	public String info() {
		String description = new String();
		description += name();
		description += "\n";
		description += "ActionAgents' info\n";
		int index = 1;
		for (Branch<?, ?, ?> branch : branches()) {
			description += index;
			description += ". ";
			description += branch.info();
			index++;
		}
		return description;
	}

	/**
	 * Callback (user-space) event reduction routine. Obtains data from the outside world and returns a BogusEvent i.e.,
	 * reduces external data into a BogusEvent. Automatically call by the main event loop (
	 * {@link remixlab.bias.core.InputHandler#handle()}). See ProScene's Space-Navigator example.
	 * 
	 * @see remixlab.bias.core.InputHandler#handle()
	 */
	public BogusEvent feed() {
		return null;
	}

	/**
	 * Returns the {@link remixlab.bias.core.InputHandler} this agent is registered to.
	 */
	public InputHandler inputHandler() {
		return handler;
	}	

	/**
	 * If {@link #isTracking()} is enabled and the agent is registered at the {@link #inputHandler()} then queries each
	 * object in the {@link #grabbers()} to check if the {@link remixlab.bias.core.Grabber#checkIfGrabsInput(BogusEvent)})
	 * condition is met. The first object meeting the condition will be set as the {@link #inputGrabber()} and returned.
	 * Note that a null grabber means that no object in the {@link #grabbers()} met the condition. A
	 * {@link #inputGrabber()} may also be enforced simply with {@link #setDefaultGrabber(Grabber)}.
	 * 
	 * @param event
	 *          to query the {@link #grabbers()}
	 * @return the new grabber which may be null.
	 * 
	 * @see #setDefaultGrabber(Grabber)
	 * @see #isTracking()
	 */
	protected Grabber updateTrackedGrabber(BogusEvent event) {
		if (event == null || !inputHandler().isAgentRegistered(this) || !isTracking())
			return trackedGrabber();
		Grabber g = trackedGrabber();
		// We first check if tracked grabber remains the same
		if (g != null)
			if (g.checkIfGrabsInput(event))
				return trackedGrabber();
		// pick the first otherwise
		trackedGrabber = null;
		for (Grabber grabber : grabberList) {
			if(grabber.checkIfGrabsInput(event)) {
				trackedGrabber = grabber;
				return trackedGrabber();
			}
		}
		for (Branch<?, ?, ?> branch : branches()) {
			for (InteractiveGrabber<?> grabber : branch.grabbers()) {
				if(grabber.checkIfGrabsInput(event)) {
					trackedGrabber = grabber;
					this.trackedGrabberBranch = branch;
					return trackedGrabber();
				}
			}
		}
		return trackedGrabber();
	}

	public float[] sensitivities(MotionEvent event) {
		return new float[] { 1f, 1f, 1f, 1f, 1f, 1f };
	}

	/**
	 * Main agent method. Parses the {@link #inputGrabber()} using the proper branch to determine the user-defined action
	 * the {@link #inputGrabber()} should perform. Calls
	 * {@code inputHandler().enqueueEventTuple(new EventGrabberTuple(event, grabber()))}.
	 * <p>
	 * <b>Note</b> that the agent must be registered at the {@link #inputHandler()} for this method to take effect.
	 * 
	 * @see #inputGrabber()
	 */
	@SuppressWarnings({ "unchecked" })
	protected <E extends Enum<E>> boolean handle(BogusEvent event) {
		if (event == null || !handler.isAgentRegistered(this) || inputHandler() == null)
			return false;
		if (event instanceof MotionEvent)
			if (((MotionEvent) event).isAbsolute())
				if (event.isNull())
					return false;
		if (event instanceof MotionEvent)
			((MotionEvent) event).modulate(sensitivities((MotionEvent) event));
		Grabber inputGrabber = inputGrabber();
		if (inputGrabber != null) {
			if (inputGrabber instanceof InteractiveGrabber<?>) {
				Branch<?,?,?> t = trackedGrabber() != null ? trackedGrabberBranch : defaultGrabberBranch;				
				Action<E> action = (Action<E>) t.handle(event);
				return action != null ? inputHandler().enqueueEventTuple(
						new EventGrabberTuple(event, (InteractiveGrabber<E>) inputGrabber, action)) : false;
			}
			return inputHandler().enqueueEventTuple(new EventGrabberTuple(event, inputGrabber));
		}
		return false;
	}

	/**
	 * Force a null action on the interactive grabber.
	 */
	@SuppressWarnings("unchecked")
	protected <E extends Enum<E>> boolean flush(BogusEvent event) {
		if (event == null || !handler.isAgentRegistered(this) || inputHandler() == null)
			return false;
		/*
		if (event instanceof MotionEvent)
			if (((MotionEvent) event).isAbsolute())
				if (event.isNull())
					return false;
		*/
		if (event instanceof MotionEvent)
			((MotionEvent) event).modulate(sensitivities((MotionEvent) event));
		Grabber inputGrabber = inputGrabber();
		if (inputGrabber != null) {
			BogusEvent flushedEvent = event.flush();
			if (inputGrabber instanceof InteractiveGrabber<?>) {
				//option 1: previous action
				//Action<E> action = ((InteractiveGrabber<E>) inputGrabber).action();
				//option 2: parse action
				Branch<?,?,?> t = trackedGrabber() != null ? trackedGrabberBranch : defaultGrabberBranch;				
				Action<E> action = (Action<E>) t.handle(event);
				return action != null ? inputHandler().enqueueEventTuple(new EventGrabberTuple(flushedEvent, (InteractiveGrabber<E>) inputGrabber, action)) : false;
			}
			return inputHandler().enqueueEventTuple(new EventGrabberTuple(flushedEvent, inputGrabber));
		}
		return false;
	}

	/**
	 * If {@link #trackedGrabber()} is non null, returns it. Otherwise returns the {@link #defaultGrabber()}.
	 * 
	 * @see #trackedGrabber()
	 */
	public Grabber inputGrabber() {
		return trackedGrabber() != null ? trackedGrabber() : defaultGrabber();
	}

	/**
	 * Returns true if {@code g} is the agent's {@link #inputGrabber()} and false otherwise.
	 */
	public boolean isInputGrabber(Grabber g) {
		return inputGrabber() == g;
	}

	/**
	 * Returns {@code true} if this agent is tracking its grabbers.
	 * <p>
	 * You may need to {@link #enableTracking()} first.
	 */
	public boolean isTracking() {
		return agentTrckn;
	}

	/**
	 * Enables tracking so that the {@link #inputGrabber()} may be updated when calling
	 * {@link #updateTrackedGrabber(BogusEvent)}.
	 * 
	 * @see #disableTracking()
	 */
	public void enableTracking() {
		setTracking(true);
	}

	/**
	 * Disables tracking.
	 * 
	 * @see #enableTracking()
	 */
	public void disableTracking() {
		setTracking(false);
	}

	/**
	 * Sets the {@link #isTracking()} value.
	 */
	public void setTracking(boolean enable) {
		agentTrckn = enable;
		if (!isTracking())
			trackedGrabber = null;
	}

	/**
	 * Calls {@link #setTracking(boolean)} to toggle the {@link #isTracking()} value.
	 */
	public void toggleTracking() {
		setTracking(!isTracking());
	}

	/**
	 * Returns the grabber set after {@link #updateTrackedGrabber(BogusEvent)} is called. It may be null.
	 */
	public Grabber trackedGrabber() {
		return trackedGrabber;
	}

	/**
	 * Default {@link #inputGrabber()} returned when {@link #trackedGrabber()} is null and set with
	 * {@link #setDefaultGrabber(Grabber)}.
	 * 
	 * @see #inputGrabber()
	 * @see #trackedGrabber()
	 */
	public Grabber defaultGrabber() {
		return defaultGrabber;
	}

	/**
	 * Sets the {@link #defaultGrabber()}
	 * 
	 * {@link #inputGrabber()}
	 */
	public boolean setDefaultGrabber(Grabber grabber) {
		if (grabber == null) {
			this.defaultGrabber = null;
			//this.dGrabberBranch = null;
			return true;
		}
		if(grabberList.contains(grabber)) {
			this.defaultGrabber = grabber;
			//this.dGrabberBranch = null;
			return true;
		}			
		for (Branch<?,?,?> b : branches())
			if (b.hasGrabber(grabber)) {
				this.defaultGrabber = grabber;
				this.defaultGrabberBranch = b;
				return true;
			}
		return false;
	}

	/**
	 * Resets the {@link #defaultGrabber()}. Convenience function that simply calls: {@code setDefaultGrabber(null)}.
	 * 
	 * @see #setDefaultGrabber(Grabber)
	 */
	public void resetDefaultGrabber() {
		setDefaultGrabber(null);
	}
	
	// Char hack from here

	public int keyCode(char key) {
		System.err.println("keyCode(char) should be implemented by your Agent derived class");
		return BogusEvent.NO_ID;
	}
}