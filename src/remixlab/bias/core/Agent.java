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

import remixlab.bias.event.*;

/**
 * Agents gather data from different sources --mostly from input devices such touch surfaces or simple mice--
 * and reduce them into a rather simple but quite 'useful' set of interface events ({@link remixlab.bias.core.BogusEvent})
 * for third party objects ({@link remixlab.bias.core.Grabber} objects) to consume them ({@link #handle(BogusEvent)}).
 * Agents thus effectively open up a channel between all kinds of input data sources and user-space objects. To add/remove
 * a grabber to/from the #grabbers() collection issue #addGrabber(Grabber) / #removeGrabber(Grabber) calls.
 * Derive from this agent and either call {@link #handle(BogusEvent)} or override {@link #handleFeed()}.
 * <p>
 * The agent may send bogus-events to its #inputGrabber() which may be regarded as the agent's grabber target. The
 * {@link #inputGrabber()} may be set by querying each grabber object in {@link #grabbers()} to check if its
 * {@link remixlab.bias.core.Grabber#checkIfGrabsInput(BogusEvent)}) condition is met
 * (see {@link #updateTrackedGrabber(BogusEvent)}, {@link #updateTrackedGrabberFeed()}). The first grabber meeting
 * the condition, namely the {@link #trackedGrabber()}), will then be set as the {@link #inputGrabber()}. When no
 * grabber meets the condition, the {@link #trackedGrabber()} is then set to null. In this case, a non-null
 * {@link #inputGrabber()} may still be set with {@link #setDefaultGrabber(Grabber)} (see also {@link #defaultGrabber()}).
 * <p>
 * Agents may be extended by appending branches to them, see
 * {@link remixlab.bias.agent.AbstractMotionAgent#appendBranch(String)},
 * {@link remixlab.bias.agent.AbstractKeyboardAgent#appendBranch(String)}. For branch handling refer to methods such
 * as {@link #pruneBranch(Branch)}, {@link #branches()}, {@link #branch(Grabber)} and others. Branches enable the agent
 * to parse bogus-events into {@link remixlab.bias.core.InteractiveGrabber} object {@link remixlab.bias.core.Action}s
 * (see {@link #addGrabber(InteractiveGrabber, Branch)}). Please refer to the {@link remixlab.bias.core.Branch} and the
 * {@link remixlab.bias.core.InteractiveGrabber} documentations for details.
 */
public abstract class Agent {
	protected String										nm;
	protected List<Branch<?>>			brnchs;
	protected List<Grabber>	grabberList;
	protected Branch<?>				trackedGrabberBranch, defaultGrabberBranch;
	protected Grabber trackedGrabber, defaultGrabber;
	protected boolean							agentTrckn;
	protected InputHandler							handler;

	/**
	 * Constructs an Agent with the given name and registers is at the given inputHandler.
	 */
	public Agent(InputHandler inputHandler, String name) {
		nm = name;
		grabberList = new ArrayList<Grabber>();
		brnchs = new ArrayList<Branch<?>>();
		setTracking(true);
		handler = inputHandler;
		handler.registerAgent(this);
	}

	/**
	 * @return agent's name
	 */
	public String name() {
		return nm;
	}
	
	// 1. Grabbers

	/**
	 * Removes the grabber from the {@link #grabbers()} list.
	 * 
	 * @see #removeGrabbers()
	 * @see #addGrabber(Grabber)
	 * @see #addGrabber(InteractiveGrabber, Branch)
	 * @see #hasGrabber(Grabber)
	 * @see #grabbers()
	 * @see #grabbers(Branch)
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
	 * 
	 * @see #removeGrabber(Grabber)
	 * @see #addGrabber(Grabber)
	 * @see #addGrabber(InteractiveGrabber, Branch)
	 * @see #hasGrabber(Grabber)
	 * @see #grabbers()
	 * @see #grabbers(Branch)
	 */	
	public void removeGrabbers() {
		setDefaultGrabber(null);
		trackedGrabber = null;
		//tGrabberBranch = null;		
		grabberList.clear();		
		for (Iterator<Branch<?>> it = brnchs.iterator(); it.hasNext();)
			it.next().reset();
	}
	
	/**
	 * Returns the list of grabber (and interactive-grabber) objects handled by this agent.
	 * 
	 * @see #removeGrabber(Grabber)
	 * @see #addGrabber(Grabber)
	 * @see #addGrabber(InteractiveGrabber, Branch)
	 * @see #hasGrabber(Grabber)
	 * @see #removeGrabbers()
	 * @see #grabbers(Branch)
	 */
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
	 * 
	 * @see #removeGrabber(Grabber)
	 * @see #addGrabber(Grabber)
	 * @see #addGrabber(InteractiveGrabber, Branch)
	 * @see #grabbers()
	 * @see #grabbers(Branch)
	 * @see #removeGrabbers()
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
	 * Adds the grabber in {@link #grabbers()}.
	 * 
	 * @see #removeGrabber(Grabber)
	 * @see #hasGrabber(Grabber)
	 * @see #addGrabber(InteractiveGrabber, Branch)
	 * @see #grabbers()
	 * @see #grabbers(Branch)
	 * @see #removeGrabbers()
	 */
	public boolean addGrabber(Grabber grabber) {
		if (grabber == null)
			return false;
		if (hasGrabber(grabber))
			return false;
		if (grabber instanceof InteractiveGrabber) {
			System.err.println("use addGrabber(G grabber, K Branch) instead");
			return false;
		}
		return grabberList.add(grabber);
	}
	
	/**
	 * Returns the branch list of interactive-grabber objects.
	 * 
	 * @see #addGrabber(InteractiveGrabber, Branch)
	 * @see #removeGrabber(Grabber)
	 * @see #addGrabber(Grabber)
	 * @see #hasGrabber(Grabber)
	 * @see #removeGrabbers()
	 * @see #grabbers()
	 */
	public <E extends Enum<E>> List<InteractiveGrabber<E>> grabbers(Branch<E> branch) {
		return branch.grabbers();
	}
	
	/**
	 * Adds grabber to branch. 
	 *
	 * @see #removeGrabber(Grabber)
	 * @see #addGrabber(Grabber)
	 * @see #hasGrabber(Grabber)
	 * @see #removeGrabbers()
	 * @see #grabbers()
	 * @see #grabbers(Branch)
	 */
	public <E extends Enum<E>, K extends Branch<E>, G extends InteractiveGrabber<E>> boolean addGrabber(G grabber, K branch) {
		if(branch == null)
			return false;
		if (!hasBranch(branch)) {
			if (!this.appendBranch(branch))
				return false;
			return false;
		}
		return branch.addGrabber(grabber);
	}
	
	// 2. Branches

	/**
	 * Returns the Branch to which the grabber belongs. May be null.
	 * 
	 * @see #hasBranch(Branch)
	 * @see #resetBranch(Branch)
	 * @see #pruneBranch(Branch)
	 * @see #branches()
	 * @see #resetBranches()
	 * @see #pruneBranches()
	 */
	public Branch<?> branch(Grabber g) {
		for (Branch<?> b : brnchs)
			if (b.hasGrabber(g))
					return b;
		return null;
	}
	
	/**
	 * Returns the list of appended branches.
	 * 
	 * @see #hasBranch(Branch)
	 * @see #resetBranch(Branch)
	 * @see #pruneBranch(Branch)
	 * @see #branch(Grabber)
	 * @see #resetBranches()
	 * @see #pruneBranches()
	 */
	public List<Branch<?>> branches() {
		return brnchs;
	}

	/**
	 * Internal use. Branches should be appended through derived agents.
	 */
	protected boolean appendBranch(Branch<?> branch) {
		if (branch == null)
			return false;
		if (!brnchs.contains(branch)) {
			this.brnchs.add(branch);
			return true;
		}
		return false;
	}
	
	/*
	// produces a name clash with iAgents
	public <E extends Enum<E>> Branch<E> appendBranch(String name) {
		return new Branch<E>(this, name);
	}
	//*/
	
	/**
	 * Returns true if branch is appended to the agent and false otherwise.
	 * 
	 * @see #branches()
	 * @see #resetBranch(Branch)
	 * @see #pruneBranch(Branch)
	 * @see #branch(Grabber)
	 * @see #resetBranches()
	 * @see #pruneBranches()
	 */
	public boolean hasBranch(Branch<?> branch) {
		return brnchs.contains(branch);
	}

	/**
	 * Removes all interactive grabber objects from branch.
	 * 
	 * @see #hasBranch(Branch)
	 * @see #resetBranches()
	 * @see #pruneBranch(Branch)
	 * @see #branch(Grabber)
	 * @see #hasBranch(Branch)
	 * @see #pruneBranches()
	 */
	public void resetBranch(Branch<?> branch) {
		branch.reset();
		if(branch == this.defaultGrabberBranch)
			setDefaultGrabber(null);
		if(branch == this.trackedGrabberBranch)
			trackedGrabber = null;
	}
	
	/**
	 * Removes all interactive grabber objects from all branches appended to this agent.
	 * 
	 * @see #hasBranch(Branch)
	 * @see #resetBranch(Branch)
	 * @see #pruneBranch(Branch)
	 * @see #branch(Grabber)
	 * @see #hasBranch(Branch)
	 * @see #pruneBranches()
	 */
	public void resetBranches() {
		for (Branch<?> branch : brnchs)
			resetBranch(branch);
	}

	/**
	 * Calls {@link #resetBranch(Branch)} and then removes the branch from the agent.
	 * 
	 * @see #hasBranch(Branch)
	 * @see #resetBranch(Branch)
	 * @see #resetBranches()
	 * @see #branch(Grabber)
	 * @see #hasBranch(Branch)
	 * @see #pruneBranches()
	 */
	public boolean pruneBranch(Branch<?> branch) {
		if (brnchs.contains(branch)) {
			this.resetBranch(branch);
			this.brnchs.remove(branch);
			return true;
		}
		return false;
	}

	/**
	 * Calls {@link #resetBranch(Branch)} on all branches appended to this agent and the removes them.
	 * 
	 * @see #hasBranch(Branch)
	 * @see #resetBranch(Branch)
	 * @see #resetBranches()
	 * @see #branch(Grabber)
	 * @see #hasBranch(Branch)
	 * @see #pruneBranch(Branch)
	 */
	public void pruneBranches() {
		resetBranches();
		brnchs.clear();
	}

	/**
	 * Returns a String with a detailed description of this Agent.
	 */
	public String info() {
		String description = new String();
		description += name();
		description += "\n";
		description += "Branches' info\n";
		int index = 1;
		for (Branch<?> branch : brnchs) {
			description += index;
			description += ". ";
			description += branch.info();
			index++;
		}
		return description;
	}

	/**
	 * Feeds {@link #handle(BogusEvent)} with the returned event. Returns null by default.
	 * <p>
	 * Automatically call by the main event loop ({@link remixlab.bias.core.InputHandler#handle()}).
	 * See ProScene's Space-Navigator example.
	 * 
	 * @see remixlab.bias.core.InputHandler#handle()
	 * @see #updateTrackedGrabberFeed()
	 * @see #handle(BogusEvent)
	 * @see #updateTrackedGrabber(BogusEvent)
	 */
	protected BogusEvent handleFeed() {
		return null;
	}
	
	/**
	 * Feeds {@link #updateTrackedGrabber(BogusEvent)} with the returned event. Returns null by default.
	 * <p>
	 * Automatically call by the main event loop ({@link remixlab.bias.core.InputHandler#handle()}).
	 * 
	 * @see remixlab.bias.core.InputHandler#handle()
	 * @see #handleFeed()
	 * @see #handle(BogusEvent)
	 * @see #updateTrackedGrabber(BogusEvent)
	 */
	protected BogusEvent updateTrackedGrabberFeed() {
		return null;
	}

	/**
	 * Returns the {@link remixlab.bias.core.InputHandler} this agent is registered to.
	 */
	public InputHandler inputHandler() {
		return handler;
	}	

	/**
	 * If {@link #isTracking()} and the agent is registered at the {@link #inputHandler()} then queries each
	 * object in the {@link #grabbers()} to check if the {@link remixlab.bias.core.Grabber#checkIfGrabsInput(BogusEvent)})
	 * condition is met. The first object meeting the condition will be set as the {@link #inputGrabber()} and returned.
	 * Note that a null grabber means that no object in the {@link #grabbers()} met the condition. A
	 * {@link #inputGrabber()} may also be enforced simply with {@link #setDefaultGrabber(Grabber)}.
	 * 
	 * @param event to query the {@link #grabbers()}
	 * @return the new grabber which may be null.
	 * 
	 * @see #setDefaultGrabber(Grabber)
	 * @see #isTracking()
	 * @see #handle(BogusEvent)
	 * @see #trackedGrabber()
	 * @see #defaultGrabber()
	 * @see #inputGrabber()
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
		trackedGrabberBranch = null;//this seems an important line :p 
		for (Branch<?> branch : brnchs) {
			InteractiveGrabber<?> iGrabber = branch.updateTrackedGrabber(event);
			if(iGrabber != null) {
				trackedGrabber = iGrabber;
				this.trackedGrabberBranch = branch;
				return trackedGrabber();
			}
		}
		return trackedGrabber();
	}

	/**
	 * Returns the sensitivities used in {@link #handle(BogusEvent)} to {@link remixlab.bias.event.MotionEvent#modulate(float[])}.
	 */
	public float[] sensitivities(MotionEvent event) {
		return new float[] { 1f, 1f, 1f, 1f, 1f, 1f };
	}

	/**
	 * Enqueues an EventGrabberTuple(event, inputGrabber()) on the {@link remixlab.bias.core.InputHandler#eventTupleQueue()},
	 * thus enabling a call on the {@link #inputGrabber()} {@link remixlab.bias.core.Grabber#performInteraction(BogusEvent)} method
	 * (which is scheduled for execution till the end of this main event loop iteration, see
	 * {@link remixlab.bias.core.InputHandler#enqueueEventTuple(EventGrabberTuple)} for details).
	 * <p>
	 * If {@link #inputGrabber()} is instance of {@link remixlab.bias.core.InteractiveGrabber} an
	 * InteractiveEventGrabberTuple<E>(event, grabber, action) is enqueued instead. Note that the agent
	 * uses its branches to find the action that's is to be enqueued in this case.
	 * 
	 * @see #inputGrabber()
	 * @see #updateTrackedGrabber(BogusEvent)
	 */
	protected boolean handle(BogusEvent event) {
		if (event == null || !handler.isAgentRegistered(this) || inputHandler() == null)
			return false;
		if (event instanceof MotionEvent)
			if (((MotionEvent) event).isAbsolute())
				if (event.isNull() && !event.flushed())
					return false;
		if (event instanceof MotionEvent)
			((MotionEvent) event).modulate(sensitivities((MotionEvent) event));
		Grabber inputGrabber = inputGrabber();
		if (inputGrabber != null) {
			if (inputGrabber instanceof InteractiveGrabber<?>) {
				Branch<?> t = trackedGrabber() != null ? trackedGrabberBranch : defaultGrabberBranch;
				return trackedGrabber() != null ? t.handleTrackedGrabber(event) : t.handleDefaultGrabber(event);
			}
			return inputHandler().enqueueEventTuple(new EventGrabberTuple(event, inputGrabber));
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
	 * Same as {@code defaultGrabber() != g1 ? setDefaultGrabber(g1) ? true : setDefaultGrabber(g2) : setDefaultGrabber(g2)} which
	 * is ubiquitous among the examples.
	 */
    public boolean shiftDefaultGrabber(Grabber g1, Grabber g2) {
    	return defaultGrabber() != g1 ? setDefaultGrabber(g1) ? true : setDefaultGrabber(g2) : setDefaultGrabber(g2); 
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
		if( ! hasGrabber(grabber) ) {
			System.err.println("To set a default grabber the object should be added into agent first. Use one of the agent addGrabber() methods");
			return false;
		}
		if(grabberList.contains(grabber)) {
			this.defaultGrabber = grabber;
			//this.dGrabberBranch = null;
			return true;
		}
		//this.defaultGrabberBranch = null;// not needed since hasGrabber already checked
		for (Branch<?> b : brnchs)
			if( b.setDefaultGrabber(grabber) ) {
				this.defaultGrabber = grabber;
				this.defaultGrabberBranch = b;
				return true;
			}
		return false;
	}
	
	public void resetTrackedGrabber() {
		trackedGrabber = null;
	}

	/**
	 * Resets the {@link #defaultGrabber()}. Convenience function that simply calls: {@code setDefaultGrabber(null)}.
	 * 
	 * @see #setDefaultGrabber(Grabber)
	 */
	public boolean resetDefaultGrabber() {
		return setDefaultGrabber(null);
	}
}