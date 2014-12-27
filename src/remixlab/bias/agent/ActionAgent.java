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
 * An ActionAgent is just an {@link remixlab.bias.core.Agent} holding some {@link remixlab.bias.agent.profile.Profile}
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
public class ActionAgent<P extends Profile<?, ?>> extends Agent {
	protected P											profile;

	/**
	 * @param p
	 *          {@link remixlab.bias.agent.profile.Profile}
	 * @param tHandler
	 *          {@link remixlab.bias.core.InputHandler} to register this Agent to
	 * @param n
	 *          Agent name
	 */
	public ActionAgent(P p, InputHandler tHandler, String n) {
		super(tHandler, n);
		profile = p;
	}

	public ActionAgent(P p, Agent parent, String n) {
		this(p, parent.inputHandler(), n);
		parent.addBranch(this);
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

	@Override
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
		if (grabber == null)
			return false;
		if (!isInPool(grabber)) {
			if( (grabber instanceof ActionGrabber<?>) ) {				
				pool().add(grabber);
				return true;
			}
		}
		return false;
	}

	///*
	@Override
	public Grabber updateTrackedGrabber(BogusEvent event) {
		Grabber g = super.updateTrackedGrabber(event);
		//TODO check condition
		if (g != null)
			if(!this.alienGrabber())
				return g;
			else if(branches().isEmpty())
				return g;
		if (!branches().isEmpty())
			for (Agent branch : branches()) {				
				g = branch.updateTrackedGrabber(event);
				if (g != null) {
					trackedGrabber = g;// the alien grabber!					
					return g;
				}
			}
		return g;
	}
	//*/

	/**
	 * Overriding of the {@link remixlab.bias.core.Agent} main method. The {@link #profile()} is used to parse the event
	 * into an user-defined action which is then enqueued as an event-grabber tuple (
	 * {@link #enqueueEventTuple(EventGrabberTuple)}), used to instruct the {@link #inputGrabber()} the user-defined
	 * action to perform.
	 * <p>
	 * <b>Note 1:</b> {@link #alienGrabber()}s always make the tuple to be enqueued even if the action is null (see
	 * {@link #enqueueEventTuple(EventGrabberTuple, boolean)}).
	 * <p>
	 * <b>Note 2:</b> This method should be overridden only in the (rare) case the ActionAgent should deal with custom
	 * BogusEvents defined by the third-party, i.e., bogus events different than those declared in the
	 * {@code remixlab.bias.event} package.
	 */
	@Override
	public boolean handle(BogusEvent event) {
		// overkill but feels safer ;)
		if (event == null || !handler.isAgentRegistered(this) || inputGrabber() == null)
			return false;
		//TODO testing
		//System.out.println("Invoking alienGrabber()");
		if (alienGrabber()) {
			//TODO remove this case
			if (branches().isEmpty()) {
				inputHandler().enqueueEventTuple(new EventGrabberTuple(event, inputGrabber()));
				return true;
			}
			else {
				for (Agent branch : branches())
					if (branch.handle(event))
						return true;
				return false;
			}
		}
		else {
			if(inputGrabber() instanceof ActionGrabber<?>)
			  return validateGrabberTuple(event, (ActionGrabber<?>)inputGrabber(), profile());
			else {
			  //re-accommodate really sucks
				//TODO debug: may simply throw an exception
				System.out.println("Grabber cannot be HANDLE in this agent: " + this.name());
				return false;
			}
		}
		//return validateGrabberTupple(event, inputGrabber());
	}
	
	/*
	// TODO old. remove me.
	protected boolean validateGrabberTupple(BogusEvent e, Grabber g) {
		if (alienGrabber())
			if (branches().isEmpty())
				enqueueEventTuple(new EventGrabberTuple(e, inputGrabber()), false);
			else {
				for (Agent branch : branches())
					if (branch.handle(e))
						return true;
				return false;
			}
		else
			enqueueEventTuple(new EventGrabberTuple(e, profile().handle(e), inputGrabber()));
		return true;
	}
  //*/
	
	/*
	//new
	protected boolean validateGrabberTupple(BogusEvent e, Grabber g) {
		if (alienGrabber()) {
			//TODO remove this case
			if (branches().isEmpty()) {
				enqueueEventTuple(new EventGrabberTuple(e, g));
				return true;
			}
			else {
				for (Agent branch : branches())
					if (branch.handle(e))
						return true;
				return false;
			}
		}
		else {
			return proc(e, g, profile());
		}
	}
	// */
	
	//TODO pending
	protected boolean validateGrabberTuple(BogusEvent e, ActionGrabber<?> g, Profile<?,?> p) {
		Action<?> grabberAction = p.handle(e);
		if( grabberAction == null )	return false;
		boolean result = inputHandler().enqueueEventTuple(new EventGrabberTuple(e, grabberAction, g));
		if(!result) {
		  //re-accommodate really sucks
			//TODO debug: may simply throw an exception
			System.out.println("ActionGrabber cannot be HANDLE in this agent: " + this.name());			
		}
		return result;
	}
}