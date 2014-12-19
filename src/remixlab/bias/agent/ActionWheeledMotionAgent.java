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

import remixlab.bias.agent.profile.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.bias.grabber.ActionGrabber;

/**
 * A {@link remixlab.bias.agent.ActionMotionAgent} with an extra {@link remixlab.bias.agent.profile.MotionProfile}
 * defining {@link remixlab.bias.event.shortcut.ButtonShortcut} -> {@link remixlab.bias.core.Action} mappings.
 * <p>
 * The Agent thus is defined by three profiles: the {@link #motionProfile()} (alias for {@link #profile()} provided for
 * convenience), the {@link #clickProfile()} and the extra {@link #wheelProfile()}.
 * 
 * @param <W>
 *          {@link remixlab.bias.agent.profile.MotionProfile} to parameterize the Agent with.
 * @param <M>
 *          {@link remixlab.bias.agent.profile.MotionProfile} to parameterize the Agent with.
 * @param <C>
 *          {@link remixlab.bias.agent.profile.ClickProfile} to parameterize the Agent with.
 */
public class ActionWheeledMotionAgent<W extends MotionProfile<?>, M extends MotionProfile<?>, C extends ClickProfile<?>>
		extends ActionMotionAgent<M, C> {

	protected W	wheelProfile;

	/**
	 * @param w
	 *          {@link remixlab.bias.agent.profile.MotionProfile} instance
	 * @param p
	 *          {@link remixlab.bias.agent.profile.MotionProfile} second instance
	 * @param c
	 *          {@link remixlab.bias.agent.profile.ClickProfile} instance
	 * @param tHandler
	 *          {@link remixlab.bias.core.InputHandler} to register this Agent to
	 * @param n
	 *          Agent name
	 */
	public ActionWheeledMotionAgent(W w, M p, C c, InputHandler tHandler, String n) {
		super(p, c, tHandler, n);
		wheelProfile = w;
	}

	public ActionWheeledMotionAgent(W w, M p, C c, ActionMotionAgent<?,?> parent, String n) {
		this(w, p, c, parent.inputHandler(), n);
		parent.addBranch(this);
	}

	/**
	 * @return the agents second {@link remixlab.bias.agent.profile.MotionProfile} instance.
	 */
	public W wheelProfile() {
		return wheelProfile;
	}

	/**
	 * Sets the {@link remixlab.bias.agent.profile.MotionProfile} second instance.
	 */
	public void setWheelProfile(W profile) {
		wheelProfile = profile;
	}

	@Override
	public String info() {
		String description = new String();
		description += name();
		description += "\n";
		if (clickProfile().description().length() != 0) {
			description += "Click shortcuts\n";
			description += clickProfile().description();
		}
		if (motionProfile().description().length() != 0) {
			description += "Motion shortcuts\n";
			description += motionProfile().description();
		}
		if (wheelProfile().description().length() != 0) {
			description += "Wheel shortcuts\n";
			description += wheelProfile().description();
		}
		return description;
	}

	@Override
	public boolean handle(BogusEvent event) {
		// overkill but feels safer ;)
		if (event == null || !handler.isAgentRegistered(this) || inputGrabber() == null)
			return false;
		return validateGrabberTupple(event, inputGrabber());
	}
	
	/*
	// TODO old. remove me.
	@Override
	protected boolean validateGrabberTupple(BogusEvent event, Grabber g) {
		if (event instanceof ClickEvent)
			// if (alienGrabber())
			// enqueueEventTuple(new EventGrabberTuple(event, inputGrabber()), false);
			// begin new
			if (alienGrabber())
				if (branches().isEmpty())
					enqueueEventTuple(new EventGrabberTuple(event, inputGrabber()), false);
				else {
					for (Agent branch : branches())
						if (branch.handle(event))
							return true;
					return false;
				}
			// end
			else
				enqueueEventTuple(new EventGrabberTuple(event, clickProfile().handle(event), inputGrabber()));
		else if (event instanceof MotionEvent) {
			((MotionEvent) event).modulate(sens);
			// if (alienGrabber())
			// enqueueEventTuple(new EventGrabberTuple(event, inputGrabber()), false);
			// begin new
			if (alienGrabber())
				if (branches().isEmpty())
					enqueueEventTuple(new EventGrabberTuple(event, inputGrabber()), false);
				else {
					for (Agent branch : branches())
						if (branch.handle(event))
							return true;
					return false;
				}
			// end
			else if (event instanceof DOF1Event)
				enqueueEventTuple(new EventGrabberTuple(event, wheelProfile().handle(event), inputGrabber()));
			else
				enqueueEventTuple(new EventGrabberTuple(event, motionProfile().handle(event), inputGrabber()));
		}
		return true;
	}
	// */
	
	// /*
	//new
	@Override
	protected boolean validateGrabberTupple(BogusEvent event, Grabber g) {
		if (event instanceof ClickEvent) {
			// if (alienGrabber())
			// enqueueEventTuple(new EventGrabberTuple(event, inputGrabber()), false);
			// begin new
			if (alienGrabber())
			  //TODO remove this case
				if (branches().isEmpty()) {
					enqueueEventTuple(new EventGrabberTuple(event, g), false);
					return true;
				}
				else {
					for (Agent branch : branches())
						if (branch.handle(event))
							return true;
					return false;
				}
			// end
			else {
				if( (g instanceof ActionGrabber) ) {
					Action<?> grabberAction = clickProfile().handle(event);
					if( grabberAction == null )	return false;
					if ( ((ActionGrabber<?>)g).referenceAction().getClass() == grabberAction.referenceAction().getClass() ) {
						enqueueEventTuple(new EventGrabberTuple(event, grabberAction, g));
						return true;
					}
					else {
					  //re-accommodate really sucks
						//TODO debug: may simply throw an exception
						System.out.println("ActionGrabber cannot be HANDLE in this agent: " + this.name());
						return false;
					}
				}
				else {
				//re-accommodate really sucks
					//TODO debug: may simply throw an exception
					System.out.println("Grabber cannot be HANDLE in this agent: " + this.name());
					return false;
				}
			}
		}
		else if (event instanceof MotionEvent) {
			((MotionEvent) event).modulate(sens);
			// if (alienGrabber())
			// enqueueEventTuple(new EventGrabberTuple(event, inputGrabber()), false);
			// begin new
			if (alienGrabber())
			  //TODO remove this case
				if (branches().isEmpty()) {
					enqueueEventTuple(new EventGrabberTuple(event, g), false);
					return true;
				}
				else {
					for (Agent branch : branches())
						if (branch.handle(event))
							return true;
					return false;
				}
			// end
			else if (event instanceof DOF1Event) {				
				if( (g instanceof ActionGrabber) ) {			
					Action<?> grabberAction = wheelProfile().handle(event);
					if( grabberAction == null )	return false;
					
					//TODO problem found here!
					((ActionGrabber)g).setReferenceAction(grabberAction);
					if ( ((ActionGrabber<?>)g).referenceAction() == null) return false;
					//end
					
					if ( ((ActionGrabber<?>)g).referenceAction().getClass() == grabberAction.referenceAction().getClass() ) {
						System.out.println("wheeled arrived here1!"); //TODO debug
						enqueueEventTuple(new EventGrabberTuple(event, grabberAction, g));
						System.out.println("wheeled arrived here2!"); //TODO debug
						return true;
					}
					else {
					  //re-accommodate really sucks
						//TODO debug: may simply throw an exception
						System.out.println("ActionGrabber cannot be HANDLE in this agent: " + this.name());
						return false;
					}
				}
				else {
				//re-accommodate really sucks
					//TODO debug: may simply throw an exception
					System.out.println(g.getClass().getName() + " Grabber cannot be HANDLE in this agent: " + this.name());
					return false;
				}
				//enqueueEventTuple(new EventGrabberTuple(event, wheelProfile().handle(event), g));
			}
			else {
				if( (g instanceof ActionGrabber) ) {			
					Action<?> grabberAction = motionProfile().handle(event);
					if( grabberAction == null )	return false;
					if ( ((ActionGrabber<?>)g).referenceAction().getClass() == grabberAction.referenceAction().getClass() ) {						
						enqueueEventTuple(new EventGrabberTuple(event, grabberAction, g));
						return true;
					}
					else {
					  //re-accommodate really sucks
						//TODO debug: may simply throw an exception
						System.out.println("ActionGrabber cannot be HANDLE in this agent: " + this.name());
						return false;
					}
				}
				else {
				//re-accommodate really sucks
					//TODO debug: may simply throw an exception
					System.out.println(g.getClass().getName() + " Grabber cannot be HANDLE in this agent: " + this.name());
					return false;
				}
				//enqueueEventTuple(new EventGrabberTuple(event, motionProfile().handle(event), g));
			}
		}
		return true;
	}
	// */
}
