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
 * A Branch handles a list of {@link remixlab.bias.core.InteractiveGrabber} objects (see {@link #grabbers()})
 * implementing the @param <E> action set which is bound to an input entity such as the mouse or a touch screen.
 * <p>
 * 
 * 
 * A branch holds some {@link remixlab.bias.core.Profile}s relating the same reference action set (defined by
 * the enum parameter type). The branch uses the {@link remixlab.bias.core.Shortcut} :
 * {@link remixlab.bias.core.Action} mappings defined by each of its Profiles to parse the
 * {@link remixlab.bias.core.BogusEvent} into an user-defined {@link remixlab.bias.core.Action}.
 * <p>
 * The default implementation here holds only a single {@link remixlab.bias.core.Profile} (see
 * {@link #profile()}) attribute (note that we use the type of the Profile to parameterize the Branch). Different
 * profile groups are provided by the {@link remixlab.bias.agent.MotionBranch} and the
 * {@link remixlab.bias.agent.KeyboardBranch} specializations, which roughly represent an HIDevice (like a kinect) and
 * a generic keyboard, respectively.
 * <p>
 * Third-parties implementations should "simply":
 * <ul>
 * <li>Derive from the Branch above that best fits their needs and add it into an agent (
 * {@link remixlab.bias.core.Agent#appendBranch(Branch)}).</li>
 * <li>Configure its profiles.
 * <li>Add some grabbers into the branch ({@link remixlab.bias.core.Agent#addGrabber(InteractiveGrabber, Branch)}).</li>
 * </ul>.
 *
 * @param <E> Reference action.
 * @param <A> Action subgroup.
 * @param <S> Shortcut used to bind the action subgroup.
 */
public class Branch<E extends Enum<E>, A extends Action<E>, S extends Shortcut> implements Copyable {
	protected Profile<E, S, A>	profile;
	protected List<InteractiveGrabber<E>> grabbers;
	protected Agent					agent;
	protected String				name;
	protected InteractiveGrabber<E> trackedGrabber, defaultGrabber;

	public Branch(Agent pnt, String n) {
		name = n;
		agent = pnt;
		profile = new Profile<E, S, A>();
		grabbers = new ArrayList<InteractiveGrabber<E>>();
		agent.appendBranch(this);
	}

	protected Branch(Branch<E, A, S> other) {
		name = other.name() + "_deep-copy";
		agent = other.agent();
		profile = other.profile().get();
		grabbers = new ArrayList<InteractiveGrabber<E>>();
		agent.appendBranch(this);
	}

	@Override
	public Branch<E, A, S> get() {
		return new Branch<E, A, S>(this);
	}

	public String name() {
		return name;
	}

	public Agent agent() {
		return agent;
	}

	/**
	 * @return the agents {@link remixlab.bias.core.Profile} instance.
	 */
	public Profile<E, S, A> profile() {
		return profile;
	}

	public String info() {
		String description = new String();
		description += name();
		description += "\n";
		if (profile().description().length() != 0) {
			description += "Shortcuts\n";
			description += profile().description();
		}
		return description;
	}

	/**
	 * The {@link #profile()} is used to parse the event into an user-defined action which is then set into the grabber
	 * (see {@link remixlab.bias.core.InteractiveGrabber#setAction(Action)}) and returned.
	 */
	protected boolean handleTrackedGrabber(BogusEvent event) {
		if(trackedGrabber != agent.trackedGrabber())
			throw new RuntimeException("faulty tracked-grabber in branch!");
		return handle(trackedGrabber, event);
	}
	
	protected boolean handleDefaultGrabber(BogusEvent event) {
		if(defaultGrabber != agent.defaultGrabber())
			throw new RuntimeException("faulty default-grabber in branch!");
		return handle(defaultGrabber, event);
	}
	
	protected boolean handle(InteractiveGrabber<E> grabber, BogusEvent event) {
		if (grabber == null)
			throw new RuntimeException("iGrabber should never be null. Check your agent implementation!");
		if (event == null)
			return false;
		Action<E> action = profile().handle(event);
		if (action == null)
			return false;
		return agent.inputHandler().enqueueEventTuple(new InteractiveEventGrabberTuple<E>(event, grabber, action));
	}
	
	public boolean addGrabber(InteractiveGrabber<E> grabber) {
		if (grabber == null || this.hasGrabber(grabber))
			return false;
		//if grabber is in agent, re-branch it:
		if(agent.hasGrabber(grabber))
			agent.removeGrabber(grabber);
		return grabbers.add(grabber);
	}
	
	public boolean removeGrabber(Grabber grabber) {
		return grabbers.remove(grabber);
	}
	
	public List<InteractiveGrabber<E>> grabbers() {
		return grabbers;
	}
	
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
	
	public void reset() {
		grabbers.clear();
	}
	
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