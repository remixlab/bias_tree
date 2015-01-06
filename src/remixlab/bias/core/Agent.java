
package remixlab.bias.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import remixlab.bias.branch.*;
import remixlab.bias.branch.profile.*;

public class Agent {
	/*
	 * { public boolean isInPool(ActionGrabber<E> grabber) { if (grabber == null) return false; return
	 * pool().contains(grabber); }
	 * 
	 * public List<ActionGrabber<E>> pool() { return grabbers; }
	 * 
	 * public boolean addInPool(ActionGrabber<E> grabber) { if (grabber == null) return false; if
	 * (!grabbers.contains(grabber)) { grabbers.add(grabber); return true; } return false; } }
	 */

	public class Tuple {
		Grabber				g;
		Branch<?, ?>	b;

		public <E extends Enum<E>> Tuple(ActionGrabber<E> _g, Branch<E, ?> _a) {
			g = _g;
			b = _a;
		}

		public Tuple(Grabber _g) {
			g = _g;
			b = null;
		}

		public Tuple() {
			g = null;
			b = null;
		}
		
		/*
		public <E extends Enum<E>> Branch<E, ?> branch() {
			if(g instanceof ActionGrabber<?>)
				return (Branch<E, ?>) b;
			return null;
		}
		
		public <E extends Enum<E>> ActionGrabber<E> grabber() {
			if(g instanceof ActionGrabber)
				return (ActionGrabber<E>) g;
			return null;
		}
		*/
	}
	
	/*
	public class BranchTuple<E extends Enum<E>> extends Tuple {
		public BranchTuple(ActionGrabber<E> _g, Branch<E, ?> _a) {
			g = _g;
			b = _a;
		}

		public BranchTuple(Grabber _g) {
			g = _g;
			b = null;
		}

		public BranchTuple() {
			g = null;
			b = null;
		}
		
		public Branch<E, ?> branch(ActionGrabber<E> grabber) {
			return (Branch<E, ?>) b;
		}
	}
	*/

	/*
	 * public class ActionTuple<E extends Enum<E>> extends Tuple { //Grabber g; //ActionAgent<?,?> a; public <E extends
	 * Enum<E>> ActionTuple(ActionGrabber<E> _g, ActionAgent<?,? extends Action<E>> _a) { super(_g); g = _g; a = _a; }
	 * 
	 * public ActionTuple(Grabber _g) { g = _g; a = null; } }
	 */

	protected String							nm;

	protected List<Branch<?, ?>>	brnchs;
	protected List<Tuple>					tuples;

	protected Tuple								trackedGrabber;
	protected Tuple								defaultGrabber;
	protected boolean							agentTrckn;

	protected InputHandler				handler;

	/**
	 * Constructs an Agent with the given name and registers is at the given inputHandler.
	 */
	public Agent(InputHandler inputHandler, String name) {
		nm = name;
		tuples = new ArrayList<Tuple>();
		trackedGrabber = new Tuple();
		defaultGrabber = new Tuple();
		setTracking(true);
		handler = inputHandler;
		brnchs = new ArrayList<Branch<?, ?>>();
	}

	/**
	 * Removes the grabber from the {@link #pool()}.
	 * <p>
	 * See {@link #addInPool(Grabber)} for details. Removing a grabber that is not in {@link #pool()} has no effect.
	 */
	public boolean removeFromPool(Grabber grabber) {
		for (Iterator<Tuple> it = tuples.iterator(); it.hasNext();) {
			Tuple t = it.next();
			if (t.g == grabber) {
				it.remove();
				return true;
			}
		}
		return false;
	}

	/**
	 * Clears the {@link #pool()}.
	 * <p>
	 * Use this method only if it is faster to clear the {@link #pool()} and then to add back a few grabbers than to
	 * remove each one independently.
	 */
	public void clearPool() {
		tuples.clear();
	}
	
	public <E extends Enum<E>> void clearPool(Branch<E, ?> branch) {
		for (Iterator<Tuple> it = tuples.iterator(); it.hasNext();) {
			Tuple t = it.next();
			if (t.b == branch)
				it.remove();
		}
	}
	
	public <E extends Enum<E>> List<ActionGrabber<E>> pool(Branch<E, ?> branch) {
		List<ActionGrabber<E>> list = new ArrayList<ActionGrabber<E>>();
		for (Tuple t : tuples)
			if (t.b == branch)
				list.add((ActionGrabber<E>) t.g);
		return list;
	}

	public List<Grabber> pool() {
		List<Grabber> grabbers = new ArrayList<Grabber>();
		for (Tuple t : tuples)
			grabbers.add(t.g);
		return grabbers;
	}

	/**
	 * Returns true if the grabber is currently in the agents {@link #pool()} list.
	 * <p>
	 * When set to false using {@link #removeFromPool(Grabber)}, the handler no longer
	 * {@link remixlab.bias.core.Grabber#checkIfGrabsInput(BogusEvent)} on this grabber. Use {@link #addInPool(Grabber)}
	 * to insert it * back.
	 */
	public boolean isInPool(Grabber grabber) {
		if (grabber == null)
			return false;
		return pool().contains(grabber);
	}

	public List<Branch<?, ?>> branches() {
		return brnchs;
	}

	/*
	 * public <E extends Enum<E>, A extends Action<E>> ActionAgent<E, MotionProfile<A>> addBranch(A action, Agent parent,
	 * String name) { MotionProfile<A> p = new MotionProfile<A>(); ActionAgent<E, MotionProfile<A>> a = new ActionAgent<E,
	 * MotionProfile<A>>(p, parent, name); addBranch(a); return a; }
	 */

	// TODO discard
	public <E extends Enum<E>, M extends Action<E>, C extends Action<E>> MotionBranch<E, MotionProfile<M>, ClickProfile<C>> addBranch(
			M motionAction, C clickAction, String name) {
		return addBranch(new MotionProfile<M>(), new ClickProfile<C>(), name);
	}

	// keep!
	public <E extends Enum<E>, M extends Action<E>, C extends Action<E>> MotionBranch<E, MotionProfile<M>, ClickProfile<C>> addBranch(
			MotionProfile<M> m, ClickProfile<C> c, String name) {
		return new MotionBranch<E, MotionProfile<M>, ClickProfile<C>>(m, c, this, name);
	}

	public <E extends Enum<E>, K extends Branch<E, ?/* extends Action<E> */>, G extends ActionGrabber<E>> boolean
			addInPool(G grabber, K actionAgent) {
		// Overkill but feels safer ;)
		if (grabber == null || this.isInPool(grabber))
			return false;
		tuples.add(new Tuple(grabber, actionAgent));
		return true;
	}

	/**
	 * Adds the grabber in the {@link #pool()}.
	 * <p>
	 * Use {@link #removeFromPool(Grabber)} to remove the grabber from the pool, so that it is no longer tested with
	 * {@link remixlab.bias.core.Grabber#checkIfGrabsInput(BogusEvent)} by the handler, and hence can no longer grab the
	 * agent focus. Use {@link #isInPool(Grabber)} to know the current state of the grabber.
	 */
	public boolean addInPool(Grabber grabber) {
		if (grabber == null)
			return false;
		if (grabber instanceof ActionGrabber) {
			System.out.println("use addInPool(G grabber, K actionAgent) instead");
			return false;
		}
		if (isInPool(grabber))
			return false;
		tuples.add(new Tuple(grabber));
		return true;
	}

	//TODO shoould NOT go if hierarchy is implemented
	// /*
	public boolean addBranch(Branch<?, ?> actionAgent) {
		// System.out.println(this.name() + " add branch: " + actionAgent.name());
		if (!brnchs.contains(actionAgent)) {
			// TODO: priority seems not needed
			this.brnchs.add(0, actionAgent);
			return true;
		}
		return false;
	}
	// */

	public boolean removeBranch(Branch<?, ?> actionAgent) {
		if (brnchs.contains(actionAgent)) {
		  this.clearPool(actionAgent);
			this.brnchs.remove(actionAgent);
			return true;
		}
		return false;
	}

	public <E extends Enum<E>> Branch<E, ?> branch(ActionGrabber<E> actionGrabber) {
		if (actionGrabber == null)
			return null;
		for (Tuple t : tuples)
			if (t.g == actionGrabber)
				return (Branch<E, ?>) t.b;
				//return t.branch();//TODO SEEMS to work :p
		return null;
	}

	public Branch<?, ?> branch(String name) {
		for (Branch<?, ?> branch : branches())
			if (branch.name().equals(name))
				return branch;
		return null;
	}

	/*
	 * public void addBranch(ActionAgent<?,?> a) { if (!brnchs.contains(a)) { this.brnchs.add(0, a); } }
	 * 
	 * public void removeBranch(ActionAgent<?,?> a) { if (brnchs.contains(a)) { if (trackedGrabber() ==
	 * a.trackedGrabber()) trackedGrabber = null; this.brnchs.remove(a); } }
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
		for (Branch<?, ?> branch : branches()) {
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
	 * object in the {@link #pool()} to check if the {@link remixlab.bias.core.Grabber#checkIfGrabsInput(BogusEvent)})
	 * condition is met. The first object meeting the condition will be set as the {@link #inputGrabber()} and returned.
	 * Note that a null grabber means that no object in the {@link #pool()} met the condition. A {@link #inputGrabber()}
	 * may also be enforced simply with {@link #setDefaultGrabber(Grabber)}.
	 * <p>
	 * <b>Note</b> you don't have to call this method since the {@link #inputHandler()} handler does it automatically
	 * every frame.
	 * 
	 * @param event
	 *          to query the {@link #pool()}
	 * @return the new grabber which may be null.
	 * 
	 * @see #setDefaultGrabber(Grabber)
	 * @see #isTracking()
	 */
	public Grabber updateTrackedGrabber(BogusEvent event) {
		if (event == null || !inputHandler().isAgentRegistered(this) || !isTracking())
			return trackedGrabber();

		Grabber g = trackedGrabber();

		// We first check if tracked grabber remains the same
		if (g != null)
			if (g.checkIfGrabsInput(event))
				return trackedGrabber();

		trackedGrabber = null;
		for (Tuple t : tuples) {
			// take whatever. Here the first one
			if (t.g.checkIfGrabsInput(event)) {
				trackedGrabber = t;
				return trackedGrabber();
			}
		}
		return trackedGrabber();
	}

	public boolean handle(BogusEvent event) {
		if (event == null || !handler.isAgentRegistered(this) || inputHandler() == null)
			return false;
		if (trackedGrabber() != null) {
			if (inputGrabber() instanceof ActionGrabber<?>)
				if (trackedGrabber.b.handle((ActionGrabber) inputGrabber(), event) == null)
					return false;
			inputHandler().enqueueEventTuple(new EventGrabberTuple(event, inputGrabber()));
		}
		else if (defaultGrabber() != null) {
			if (inputGrabber() instanceof ActionGrabber<?>)
				if (defaultGrabber.b.handle((ActionGrabber) inputGrabber(), event) == null)
					return false;
			inputHandler().enqueueEventTuple(new EventGrabberTuple(event, inputGrabber()));
		}
		return false;
	}

	/**
	 * If {@link #trackedGrabber()} is non null, returns it. Otherwise returns the {@link #defaultGrabber()}.
	 * 
	 * @see #trackedGrabber()
	 */
	public Grabber inputGrabber() {
		if (trackedGrabber() != null)
			return trackedGrabber();
		else
			return defaultGrabber();
	}

	public boolean isInputGrabber(Grabber g) {
		//TODO discard grabsInput from Grabber
		//return g.grabsInput(this);
		return inputGrabber() == g;
	}

	/**
	 * @return Agents name
	 */
	public String name() {
		return nm;
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
		return trackedGrabber == null ? null : trackedGrabber.g;
	}

	/**
	 * Default {@link #inputGrabber()} returned when {@link #trackedGrabber()} is null and set with
	 * {@link #setDefaultGrabber(Grabber)}.
	 * 
	 * @see #inputGrabber()
	 * @see #trackedGrabber()
	 */
	public Grabber defaultGrabber() {
		return defaultGrabber == null ? null : defaultGrabber.g;
	}

	/**
	 * Sets the {@link #defaultGrabber()}
	 * 
	 * {@link #inputGrabber()}
	 */
	public boolean setDefaultGrabber(Grabber grabber) {
		for (Tuple t : tuples)
			if (t.g == grabber) {
				this.defaultGrabber = t;
				return true;
			}
		return false;
	}

	/**
	 * Resets the {@link #defaultGrabber()}. Convinience function that simply calls: {@code setDefaultGrabber(null)}.
	 * 
	 * @see #setDefaultGrabber(Grabber)
	 */
	public void resetDefaultGrabber() {
		setDefaultGrabber(null);
	}
}
