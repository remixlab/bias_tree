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

import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.bias.event.shortcut.*;

/**
 * An {@link remixlab.bias.core.Branch} with an extra {@link remixlab.bias.core.Profile} defining
 * {@link remixlab.bias.event.shortcut.ClickShortcut} : {@link remixlab.bias.core.Action} mappings.
 * <p>
 * The Agent thus is defined by two profiles: the {@link #motionProfile()} (alias for {@link #profile()} provided for
 * convenience) and the (extra) {@link #clickProfile()}.
 *
 * @param <E> Reference action enum.
 * @param <A> Motion action enum sub-group.
 * @param <C> Click/tap action enum sub-group.
 */
public class MotionBranch<E extends Enum<E>, A extends Action<E>, C extends Action<E>> extends
		Branch<E, A, MotionShortcut> {
	protected Profile<ClickShortcut, C>	clickProfile;
	
	/**
	 * @param a {@link remixlab.bias.core.Agent} instance
	 * @param n the branch name
	 */
	public MotionBranch(Agent a, String n) {
		super(a, n);
		clickProfile = new Profile<ClickShortcut, C>();
	}

	protected MotionBranch(MotionBranch<E, A, C> other) {
		super(other);
		clickProfile = other.clickProfile().get();
	}

	@Override
	public MotionBranch<E, A, C> get() {
		return new MotionBranch<E, A, C>(this);
	}

	/**
	 * Alias for {@link #profile()}.
	 */
	public Profile<MotionShortcut, A> motionProfile() {
		return profile();
	}

	/**
	 * Sets the motion {@link remixlab.bias.core.Profile}
	 */
	public void setMotionProfile(Profile<MotionShortcut, A> profile) {
		setProfile(profile);
	}

	/**
	 * Returns the click {@link remixlab.bias.core.Profile} instance.
	 */
	public Profile<ClickShortcut, C> clickProfile() {
		return clickProfile;
	}

	/**
	 * Sets the click {@link remixlab.bias.core.Profile}
	 */
	public void setClickProfile(Profile<ClickShortcut, C> profile) {
		clickProfile = profile;
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
		return description;
	}
	
	@Override
	protected boolean handle(InteractiveGrabber<E> grabber, BogusEvent event) {
		//TODO testing
		if (grabber == null) {
			throw new RuntimeException("InteractiveGrabber should never be null. Check your agent implementation!");
		}
		if (event == null)
			return false;
		
        Action<E> action = null;
		
		///*
		if (event instanceof MotionEvent)
			action = profile().handle(event);
		if (event instanceof ClickEvent)
			action = clickProfile.handle(event);
		//*/
		
		/*
		action = profile().handle(event);
		if (action == null)
			action = clickProfile.handle(event);
		//*/
		
		if (action == null)
			return false;
		return agent.inputHandler().enqueueEventTuple(new InteractiveEventGrabberTuple<E>(event, grabber, action));
	}

	// high-level api (wrappers around the profile): from here nor really needed
	
	/*
	 
	public void setMotionBinding(A action) {
		motionProfile().setBinding(new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, BogusEvent.NO_ID), action);
	}
	
	public void removeMotionBinding() {
		motionProfile().removeBinding(new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, BogusEvent.NO_ID));
	}
	
	public boolean hasMotionBinding() {
		return motionProfile().hasBinding(new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, BogusEvent.NO_ID));
	}
	
	public A gestureAction() {
		return motionProfile().action(new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, BogusEvent.NO_ID));
	}
	
	*/
	
	//
	
	public void setMotionBinding(int mask, int id, A action) {
		/*
		for(Branch<?, ?, ?> branch : agent.branches())
			if(branch instanceof MotionBranch)
				if(branch != this)
					if(((MotionBranch<?, ?, ?>)branch).hasMotionBinding(mask, id))
						System.out.println("Warning: MotionShortcut already bound to " + ((MotionBranch<?, ?, ?>)branch).motionAction(mask, id) + " in " + branch.name());
						//*/
		motionProfile().setBinding(new MotionShortcut(mask, id), action);
	}

	public void setMotionBinding(int id, A action) {
		/*
		for(Branch<?, ?, ?> branch : agent.branches())
			if(branch instanceof MotionBranch)
				if(branch != this)
					if(((MotionBranch<?, ?, ?>)branch).hasMotionBinding(id))
						System.out.println("Warning: MotionShortcut already bound to " + ((MotionBranch<?, ?, ?>)branch).motionAction(id) + " in " + branch.name());
		//*/
		motionProfile().setBinding(new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, id), action);
	}

	public void removeMotionBinding(int mask, int id) {
		motionProfile().removeBinding(new MotionShortcut(mask, id));
	}

	public void removeMotionBinding(int id) {
		motionProfile().removeBinding(new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, id));
	}

	public boolean hasMotionBinding(int mask, int id) {
		return motionProfile().hasBinding(new MotionShortcut(mask, id));
	}

	public boolean hasMotionBinding(int id) {
		return motionProfile().hasBinding(new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, id));
	}

	public A motionAction(int mask, int id) {
		return motionProfile().action(new MotionShortcut(mask, id));
	}

	public A motionAction(int id) {
		return motionProfile().action(new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, id));
	}
	
	// click
	
	public void setClickBinding(int mask, int button, int ncs, C action) {
		/*
		for(Branch<?, ?, ?> branch : agent.branches())
			if(branch instanceof MotionBranch)
				if(branch != this)
					if(((MotionBranch<?, ?, ?>)branch).hasClickBinding(mask, button, ncs))
						System.out.println("Warning: ClickShortcut already bound to " + ((MotionBranch<?, ?, ?>)branch).clickAction(mask, button, ncs) + " in " + branch.name());
	    //*/
		clickProfile().setBinding(new ClickShortcut(mask, button, ncs), action);
	}

	public void setClickBinding(int button, int ncs, C action) {
		/*
		for(Branch<?, ?, ?> branch : agent.branches())
			if(branch instanceof MotionBranch)
				if(branch != this)
					if(((MotionBranch<?, ?, ?>)branch).hasClickBinding(button, ncs))
						System.out.println("Warning: ClickShortcut already bound to " + ((MotionBranch<?, ?, ?>)branch).clickAction(button, ncs) + " in " + branch.name());
		//*/
		clickProfile().setBinding(new ClickShortcut(button, ncs), action);
	}

	public void setClickBinding(int button, C action) {
		/*
		for(Branch<?, ?, ?> branch : agent.branches())
			if(branch instanceof MotionBranch)
				if(branch != this)
					if(((MotionBranch<?, ?, ?>)branch).hasClickBinding(button))
						System.out.println("Warning: ClickShortcut already bound to " + ((MotionBranch<?, ?, ?>)branch).clickAction(button) + " in " + branch.name());
		//*/
		clickProfile().setBinding(new ClickShortcut(button, 1), action);
	}

	public void removeClickBinding(int mask, int button, int ncs) {
		clickProfile().removeBinding(new ClickShortcut(mask, button, ncs));
	}

	public void removeClickBinding(int button, int ncs) {
		clickProfile().removeBinding(new ClickShortcut(button, ncs));
	}

	public void removeClickBinding(int button) {
		clickProfile().removeBinding(new ClickShortcut(button, 1));
	}

	public boolean hasClickBinding(int mask, int button, int ncs) {
		return clickProfile().hasBinding(new ClickShortcut(mask, button, ncs));
	}

	public boolean hasClickBinding(int button, int ncs) {
		return clickProfile().hasBinding(new ClickShortcut(button, ncs));
	}

	public boolean hasClickBinding(int button) {
		return clickProfile().hasBinding(new ClickShortcut(button, 1));
	}

	public C clickAction(int mask, int button, int ncs) {
		return clickProfile().action(new ClickShortcut(mask, button, ncs));
	}

	public C clickAction(int button, int ncs) {
		return clickProfile().action(new ClickShortcut(button, ncs));
	}

	public C clickAction(int button) {
		return clickProfile().action(new ClickShortcut(button, 1));
	}
}