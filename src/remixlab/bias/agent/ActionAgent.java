/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.agent;

import remixlab.bias.agent.profile.Profile;
import remixlab.bias.core.*;
import remixlab.bias.grabber.*;

/**
 * An ActionAgent is just an {@link remixlab.bias.core.AbstractAgent} holding some {@link remixlab.bias.agent.profile.Profile}
 * s. The Agent uses the {@link remixlab.bias.event.shortcut.Shortcut} -> {@link remixlab.bias.core.Action} mappings
 * defined by each of its Profiles to parse the {@link remixlab.bias.core.BogusEvent} into an user-defined
 * {@link remixlab.bias.core.Action} (see {@link #handle(BogusEvent)}).
 * <p>
 * The default implementation here holds only a single {@link remixlab.bias.agent.profile.Profile} (see
 * {@link #profile()}) attribute (note that we use the type of the Profile to parameterize the ActionAgent). Different
 * profile groups are provided by the {@link remixlab.bias.agent.ActionMotionAgent}, the
 * {@link remixlab.bias.agent.ActionWheeledMotionAgent} and the {@link remixlab.bias.agent.ActionKeyboardAgent}
 * specializations, which roughly represent an HIDevice (like a kinect), a wheeled HIDevice (like a mouse) and a generic
 * keyboard, respectively.
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
 *          {@link remixlab.bias.agent.profile.Profile} to parameterize the Agent with.
 */
//public class ActionAgent<E extends Enum<E>, P extends Profile<?, /*? extends*/ Action<E>>> extends AbstractAgent {
public class ActionAgent<E extends Enum<E>, P extends Profile<?, ?>> extends AbstractAgent {
	protected P						profile;
	protected Agent	parent;

	public ActionAgent(P p, Agent pnt, String n) {
		super(n);
		profile = p;
		parent = pnt;
		parent.addBranch(this);
	}

	public Agent parentAgent() {
		return parent;
	}

	/**
	 * @return the agents {@link remixlab.bias.agent.profile.Profile} instance.
	 */
	public P profile() {
		return profile;
	}

	/**
	 * Sets the {@link remixlab.bias.agent.profile.Profile}
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
	
	@Override
	public boolean addInPool(Grabber grabber) {
		if(grabber instanceof ActionGrabber)
			addInPool((ActionGrabber) grabber);
		return false;
	}
	
	public boolean addInPool(ActionGrabber<E> grabber) {
		if (grabber == null)
			return false;
		if (!isInPool(grabber)) {
		  System.out.println(this.name() + ".addInPool(ActionGrabber<E> grabber) called on " +  grabber.toString());
			pool().add(grabber);
			return true;
		}
		return false;
	}

	/**
	 * Overriding of the {@link remixlab.bias.core.AbstractAgent} main method. The {@link #profile()} is used to parse the event
	 * into an user-defined action which is then enqueued as an event-grabber tuple (
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
	/*
	 * @Override public boolean handle(BogusEvent event) { // overkill but feels safer ;) if (event == null inputGrabber()
	 * == null) return false; inputHandler().enqueueEventTuple(new EventGrabberTuple(event, profile().handle(event),
	 * inputGrabber())); }
	 */

	public Action<?> handle(BogusEvent event) {
		return profile().handle(event);
	}

	/*
	 * public EventGrabberTuple tuple(BogusEvent event) { if (event == null) return null; return new
	 * EventGrabberTuple(event, profile().handle(event), inputGrabber()); }
	 */

	/*
	 * // TODO old. remove me. protected boolean validateGrabberTupple(BogusEvent e, Grabber g) { if (alienGrabber()) if
	 * (branches().isEmpty()) enqueueEventTuple(new EventGrabberTuple(e, inputGrabber()), false); else { for (Agent branch
	 * : branches()) if (branch.handle(e)) return true; return false; } else enqueueEventTuple(new EventGrabberTuple(e,
	 * profile().handle(e), inputGrabber())); return true; } //
	 */

	/*
	 * //new protected boolean validateGrabberTupple(BogusEvent e, Grabber g) { if (alienGrabber()) { //TODO remove this
	 * case if (branches().isEmpty()) { enqueueEventTuple(new EventGrabberTuple(e, g)); return true; } else { for (Agent
	 * branch : branches()) if (branch.handle(e)) return true; return false; } } else { return proc(e, g, profile()); } }
	 * //
	 */

	// TODO pending
	/*
	 * protected boolean validateGrabberTuple(BogusEvent e, ActionGrabber<?> g, Profile<?,?> p) { Action<?> grabberAction
	 * = p.handle(e); if( grabberAction == null ) return false; boolean result = inputHandler().enqueueEventTuple(new
	 * EventGrabberTuple(e, grabberAction, g)); if(!result) { //re-accommodate really sucks //TODO debug: may simply throw
	 * an exception System.out.println("ActionGrabber cannot be HANDLE in this agent: " + this.name()); } return result; }
	 */

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