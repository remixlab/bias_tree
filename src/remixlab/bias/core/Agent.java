
package remixlab.bias.core;

import java.util.ArrayList;
import java.util.List;

import remixlab.bias.agent.ActionAgent;
import remixlab.bias.grabber.ActionGrabber;

//public class InputAgent<ActionAgent<?,?> extends ActionAgent<?,?>> extends Agent {

//public class ActionInputAgent<ActionAgent<?,?> extends ActionAgent<?,?>> extends InputAgent {
//TODO testing type safety
//public abstract class InputAgent<ActionAgent<?,?> extends ActionAgent<?,?>> extends Agent {
public class Agent extends AbstractAgent {
	protected ActionAgent<?,?>				trackedAgent, targetAgent;
	protected InputHandler					handler;
	protected List<ActionAgent<?,?>>	brnchs;

	public Agent(InputHandler inputHandler, String name) {
		super(name);
		handler = inputHandler;
		brnchs = new ArrayList<ActionAgent<?,?>>();
	}

	public List<ActionAgent<?,?>> branches() {
		return brnchs;
	}
	
	public void addBranch(ActionAgent<?,?> actionAgent) {
		System.out.println(this.name() + " add branch: " + actionAgent.name());
  	if (!brnchs.contains(actionAgent)) {
			this.brnchs.add(0, actionAgent);
		}
	}
	
	public void removeBranch(ActionAgent<?,?> a) {
		if (brnchs.contains(a)) {
			if (trackedGrabber() == a.trackedGrabber())
				trackedGrabber = null;
			this.brnchs.remove(a);
		}
	}
	
	public ActionAgent<?,?> branch(String name) {
		for (ActionAgent<?,?> branch : branches())
			if( branch.name().equals(name) )
				return branch;					
		return null;
	}
	
	/*
	public void addBranch(ActionAgent<?,?> a) {
		if (!brnchs.contains(a)) {
			this.brnchs.add(0, a);
		}
	}

	public void removeBranch(ActionAgent<?,?> a) {
		if (brnchs.contains(a)) {
			if (trackedGrabber() == a.trackedGrabber())
				trackedGrabber = null;
			this.brnchs.remove(a);
		}
	}
	*/

	/**
	 * Returns a detailed description of this Agent as a String.
	 */
	public String info() {
		String description = new String();
		description += name();
		description += "\n";
		description += "ActionAgents' info\n";
		int index = 1;
		for (ActionAgent<?,?> branch : branches()) {
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

	// Alien grabber and action-agent branches

	/**
	 * Tells whether or not the {@link #inputGrabber()} is an object implementing the user-defined
	 * {@link remixlab.bias.core.Action} group the third party application is meant to support. Hence, third-parties
	 * should override this method defining that condition.
	 * <p>
	 * Returns {@code false} by default.
	 */
	/*
	 * protected boolean isInputGrabberAlien() { //System.out.println("alienGrabber() invoked"); //TODO testing return
	 * isInPool(inputGrabber()); //return false;//prev worked }
	 */

	// @Override
	public boolean handle(BogusEvent event) {
		if (event == null || !handler.isAgentRegistered(this))
			return false;

		if (inputGrabber() != null) {
			if (targetAgent == null) {
				inputHandler().enqueueEventTuple(new EventGrabberTuple(event, inputGrabber()));
				return true;
			}
			else {
				Action<?> action = targetAgent.handle(event);
				if (action != null) {
					((ActionGrabber) inputGrabber()).setAction(action);
					inputHandler().enqueueEventTuple(new EventGrabberTuple(event, inputGrabber()));
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Grabber updateTrackedGrabber(BogusEvent event) {
		targetAgent = null;
		Grabber g = super.updateTrackedGrabber(event);
		if (g != null)
			return g;
		for (ActionAgent<?,?> branch : branches()) {
			g = branch.updateTrackedGrabber(event);
			if (g != null) {
				trackedAgent = branch;
				return g;
			}
		}
		return g;
	}

	/**
	 * If {@link #trackedGrabber()} is non null, returns it. Otherwise returns the {@link #defaultGrabber()}.
	 * 
	 * @see #trackedGrabber()
	 */
	public Grabber inputGrabber() {
		// /*
		targetAgent = null;
		if (trackedGrabber() != null)
			return trackedGrabber();
		else if (trackedAgent != null)
			if (trackedAgent.trackedGrabber() != null) {
				targetAgent = trackedAgent;
				return trackedAgent.trackedGrabber();
			}

		if (defaultGrabber() != null)
			return defaultGrabber();
		else
			for (ActionAgent<?,?> branch : branches())
				if (branch.defaultGrabber() != null) {
					targetAgent = branch;
					return branch.defaultGrabber();
				}
		return null;
		// */

		/*
		 * if(defaultGrabber() != null) return defaultGrabber(); else for (ActionAgent<?,?> branch : branches()) if(
		 * branch.defaultGrabber() != null ) { targetAgent = branch; return branch.defaultGrabber(); } return null; //
		 */
	}

	/*
	 * public boolean isInputGrabber(Grabber g) { return g.grabsInput(this); }
	 */
}
