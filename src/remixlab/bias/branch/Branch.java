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

/**
 * An ActionAgent is just an {@link remixlab.bias.core.AbstractAgent} holding some
 * {@link remixlab.bias.branch.profile.Profile} s. The Agent uses the {@link remixlab.bias.event.shortcut.Shortcut} ->
 * {@link remixlab.bias.core.Action} mappings defined by each of its Profiles to parse the
 * {@link remixlab.bias.core.BogusEvent} into an user-defined {@link remixlab.bias.core.Action} (see
 * {@link #handle(BogusEvent)}).
 * <p>
 * The default implementation here holds only a single {@link remixlab.bias.branch.profile.Profile} (see
 * {@link #profile()}) attribute (note that we use the type of the Profile to parameterize the ActionAgent). Different
 * profile groups are provided by the {@link remixlab.bias.branch.MotionBranch}, the
 * {@link remixlab.bias.branch.WheeledMotionBranch} and the {@link remixlab.bias.branch.KeyboardBranch} specializations,
 * which roughly represent an HIDevice (like a kinect), a wheeled HIDevice (like a mouse) and a generic keyboard,
 * respectively.
 * <p>
 * Third-parties implementations should "simply":
 * <ul>
 * <li>Derive from the ActionAgent above that best fits their needs.</li>
 * <li>Supply a routine to reduce application-specific input data into BogusEvents (given them their name).</li>
 * <li>Properly call {@link #updateTrackedGrabber(BogusEvent)} and {@link #handle(BogusEvent)} on them.</li>
 * </ul>
 * The <b>remixlab.proscene.Scene.ProsceneMouse</b> and <b>remixlab.proscene.Scene.ProsceneKeyboard</b> classes provide
 * good example implementations. Note that the ActionAgent methods defined in this package (bias) should rarely be in
 * need to be overridden, not even {@link #handle(BogusEvent)}.
 * 
 * @param <P>
 *          {@link remixlab.bias.branch.profile.Profile} to parameterize the Agent with.
 */
public class Branch<E extends Enum<E>, P extends Profile<?, ? extends Action<E>>> {
	protected P				profile;
	protected Agent		parent;
	protected String	name;

	public Branch(P p, Agent pnt, String n) {
		name = n;
		profile = p;
		parent = pnt;
		parent.addBranch(this);
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
	public P profile() {
		return profile;
	}

	/**
	 * Sets the {@link remixlab.bias.branch.profile.Profile}
	 * 
	 * @param p
	 */
	public void setProfile(P p) {
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
	 * The {@link #profile()} is used to parse the
	 * event into an user-defined action which is then enqueued as an event-grabber tuple (
	 * {@link #enqueueEventTuple(EventGrabberTuple)}), used to instruct the {@link #inputGrabber()} the user-defined
	 * action to perform.
	 * <p>
	 * <b>Note 1:</b> {@link #isInputGrabberAlien()}s always make the tuple to be enqueued even if the action is null (see
	 * {@link #enqueueEventTuple(EventGrabberTuple, boolean)}).
	 * <p>
	 * <b>Note 2:</b> This method should be overridden only in the (rare) case the ActionAgent should deal with custom
	 * BogusEvents defined by the third-party, i.e., bogus events different than those declared in the
	 * {@code remixlab.bias.event} package.
	 */
	public Action<E> handle(ActionGrabber<E> grabber, BogusEvent event) {
		if (grabber == null || event == null)
			return null;
		Action<E> action = profile().handle(event);
		if (action != null) {
			grabber.setAction(action);
		}
		return action;
	}

	/**
	 * Convenience function that simply calls {@code resetProfile()}.
	 * 
	 * @see #resetProfile()
	 */
	public void resetAllProfiles() {
		resetProfile();
	}

	/**
	 * Convenience function that simply calls {@code profile.removeAllBindings()}.
	 */
	public void resetProfile() {
		profile.removeAllBindings();
	}
}