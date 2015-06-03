/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.branch;

import remixlab.bias.branch.profile.*;
import remixlab.bias.core.*;
import remixlab.bias.event.shortcut.Shortcut;
import remixlab.util.Copyable;

/**
 * A branch holds some {@link remixlab.bias.branch.profile.Profile}s relating the same reference action set (defined by
 * the enum parameter type). The branch uses the {@link remixlab.bias.event.shortcut.Shortcut} ->
 * {@link remixlab.bias.core.Action} mappings defined by each of its Profiles to parse the
 * {@link remixlab.bias.core.BogusEvent} into an user-defined {@link remixlab.bias.core.Action}.
 * <p>
 * The default implementation here holds only a single {@link remixlab.bias.branch.profile.Profile} (see
 * {@link #profile()}) attribute (note that we use the type of the Profile to parameterize the Branch). Different
 * profile groups are provided by the {@link remixlab.bias.branch.MotionBranch} and the
 * {@link remixlab.bias.branch.KeyboardBranch} specializations, which roughly represent an HIDevice (like a kinect) and
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
 * @param <E>
 *          Reference action set used to parameterized the Agent.
 * 
 * @param <P>
 *          {@link remixlab.bias.branch.profile.Profile} to parameterize the Agent with.
 */
public class Branch<E extends Enum<E>, A extends Action<E>, S extends Shortcut> implements Copyable {
	protected Profile<S, A>	profile;
	protected Agent					parent;
	protected String				name;

	public Branch(Agent pnt, String n) {
		name = n;
		profile = new Profile<S, A>();
		parent = pnt;
		parent.appendBranch(this);
	}

	protected Branch(Branch<E, A, S> other) {
		name = other.name() + "_deep-copy";
		profile = other.profile().get();
		parent = other.agent();
		parent.appendBranch(this);
	}

	@Override
	public Branch<E, A, S> get() {
		return new Branch<E, A, S>(this);
	}

	public String name() {
		return name;
	}

	public Agent agent() {
		return parent;
	}

	/**
	 * @return the agents {@link remixlab.bias.branch.profile.Profile} instance.
	 */
	public Profile<S, A> profile() {
		return profile;
	}

	/**
	 * Sets the {@link remixlab.bias.branch.profile.Profile}
	 * 
	 * @param p
	 */
	public void setProfile(Profile<S, A> p) {
		if (p == null)
			return;
		profile = p;
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
	public Action<E> handle(BogusEvent event) {
		if (event == null)
			return null;
		Action<E> action = profile().handle(event);
		// if (action != null) grabber.setAction(action);
		return action;
	}
}