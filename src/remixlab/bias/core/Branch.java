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
import java.util.List;

import remixlab.util.Copyable;

/**
 * A Branch handles a list of {@link remixlab.bias.core.InteractiveGrabber} objects ({@link #grabbers()},
 * implementing the same reference action set which is used to parameterize the branch.
 * A branch, in turn, is handled by the {@link remixlab.bias.core.Agent} to which it's appended (e.g.,
 * {@link remixlab.bias.agent.InteractiveMotionAgent#appendBranch(String)} or
 * {@link remixlab.bias.agent.InteractiveKeyboardAgent#appendBranch(String)}).
 * <p>
 * To add/remove an {@link remixlab.bias.core.InteractiveGrabber} object to/from a Branch, use 
 * {@link #addGrabber(InteractiveGrabber)} and {@link #removeGrabber(Grabber)}), respectively. The same
 * operations may be performed directly from the agent to which the branch is appended (see
 * {@link remixlab.bias.core.Agent#addGrabber(InteractiveGrabber, Branch)}
 * and {@link remixlab.bias.core.Agent#removeGrabber(Grabber)}. 
 * <p>
 * <b>Note</b> that the {@link remixlab.bias.agent.MotionBranch} and the
 * {@link remixlab.bias.agent.KeyboardBranch} branch specializations cover all
 * {@link remixlab.bias.core.BogusEvent} / {@link remixlab.bias.core.Shortcut} types provided in bias.
 * While a {@link remixlab.bias.agent.InteractiveMotionAgent} handles the former, a
 * {@link remixlab.bias.agent.InteractiveKeyboardAgent} handles the latter.
 *
 * @param <E> 'Reference' enum action set.
 */
public class Branch<E extends Enum<E>> implements Copyable {
	// TODO: describe in the api above how to implement custom branches (BogusEvent-> Shortcut-> CustomBranch,
	//see KeyboardBranch)
	
	protected List<InteractiveGrabber<E>> grabbers;
	protected List<Profile<E, ?, ?>> profiles;
	protected Agent					agent;
	protected String				name;
	protected InteractiveGrabber<E> trackedGrabber, defaultGrabber;

	protected Branch(Agent pnt, String n) {
		name = n;
		agent = pnt;
		grabbers = new ArrayList<InteractiveGrabber<E>>();
		profiles = new ArrayList<Profile<E, ?, ?>>();
		agent.appendBranch(this);
	}

	protected Branch(Branch<E> other) {
		name = other.name() + "_deep-copy";
		agent = other.agent();
		grabbers = new ArrayList<InteractiveGrabber<E>>();
		profiles = new ArrayList<Profile<E, ?, ?>>();
		for(Profile<E, ?, ?>  profile : other.profiles)
			this.profiles.add(profile.get());
		agent.appendBranch(this);
	}

	@Override
	public Branch<E> get() {
		return new Branch<E>(this);
	}

	/**
	 * @return branch name
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns the {@link remixlab.bias.core.Agent} to which this branch is appended.
	 */
	public Agent agent() {
		return agent;
	}

	protected List<Profile<E, ?, ?>> profiles() {
		return profiles;
	}

	/**
	 * Returns a description of the branch bindings.
	 */
	public String info() {
		String description = new String();
		description += name();
		description += "\n";
		for(Profile<E, ?, ?>  profile : profiles())
			if (profile.description().length() != 0)
				description += profile.description();
		return description;
	}

	/**
	 * Internal use.
	 * <p>
	 * Same as {@code return handle(trackedGrabber, event)}.
	 */	
	protected boolean handleTrackedGrabber(BogusEvent event) {
		if(trackedGrabber != agent.trackedGrabber())
			throw new RuntimeException("faulty tracked-grabber in branch!");
		return handle(trackedGrabber, event);
	}
	
	/**
	 * Internal use.
	 * <p>
	 * Same as {@code return handle(defaultGrabber, event)}.
	 */
	protected boolean handleDefaultGrabber(BogusEvent event) {
		if(defaultGrabber != agent.defaultGrabber())
			throw new RuntimeException("faulty default-grabber in branch!");
		return handle(defaultGrabber, event);
	}
	
	/**
	 * Parses the {@link remixlab.bias.core.BogusEvent} (i.e., see if it carries an {@link remixlab.bias.core.Action})
	 * using the branch {@link #profiles()}.
	 * <p>
	 * Returns true if a non-null action is enqueued for later execution (see {@link remixlab.bias.core.InputHandler#handle()}). 
	 */
	protected boolean handle(InteractiveGrabber<E> grabber, BogusEvent event) {
		if (grabber == null)
			throw new RuntimeException("iGrabber should never be null. Check your agent implementation!");
		if (event == null)
			return false;
		
		Action<E> action = null;
		for(Profile<E, ?, ?>  profile : profiles()) {		
			action = profile.handle(event);
			if (action != null)
				return agent.inputHandler().enqueueEventTuple(new InteractiveEventGrabberTuple<E>(event, grabber, action));
		}
		
		return false;
	}
	
	/**
	 * Adds grabber to the branch. If the {@link #agent()} to which the branch is appended  , the grabber
	 * is first remove, i.e., the grabber is re-branched.
	 */
	public boolean addGrabber(InteractiveGrabber<E> grabber) {
		if (grabber == null || this.hasGrabber(grabber))
			return false;
		//if grabber is in agent, re-branch it:
		if(agent.hasGrabber(grabber))
			agent.removeGrabber(grabber);
		return grabbers.add(grabber);
	}
	
	/**
	 * Removes the grabber to the branch.
	 */
	public boolean removeGrabber(Grabber grabber) {
		return grabbers.remove(grabber);
	}
	
	/**
	 * Returns the list of {@link remixlab.bias.core.InteractiveGrabber} objects handled by this branch.
	 */
	public List<InteractiveGrabber<E>> grabbers() {
		return grabbers;
	}
	
	/**
	 * Returns true if the given grabber is in the {@link #grabbers()} list and false otherwise.
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
	 * Remove all  {@link remixlab.bias.core.InteractiveGrabber} objects from this branch.
	 */
	public void reset() {
		grabbers.clear();
	}
	
	/**
	 * Internal use, issued by the agent.
	 */
	protected InteractiveGrabber<E> updateTrackedGrabber(BogusEvent event) {
		//trackedGrabber = null;
		for (InteractiveGrabber<E> g : grabbers())
			if(g.checkIfGrabsInput(event)) {
				trackedGrabber = g;
				return trackedGrabber;
			}
		//return trackedGrabber;
		return null;
	}
	
	/**
	 * Internal use, issued by the agent.
	 */
	protected boolean setDefaultGrabber(Grabber grabber) {
		//defaultGrabber = null;
		for (InteractiveGrabber<E> g : grabbers())
			if(g == grabber) {
				defaultGrabber = g;
				return true;
			}
		return false;
	}
}