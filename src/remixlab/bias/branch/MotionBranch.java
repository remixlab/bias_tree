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
import remixlab.bias.event.*;
import remixlab.bias.event.shortcut.*;

/**
 * An {@link remixlab.bias.core.Branch} with an extra {@link remixlab.bias.branch.profile.ClickProfile} defining
 * {@link remixlab.bias.event.shortcut.ClickShortcut} -> {@link remixlab.bias.core.Action} mappings.
 * <p>
 * The Agent thus is defined by two profiles: the {@link #motionProfile()} (alias for {@link #profile()} provided for
 * convenience) and the (extra) {@link #clickProfile()}.
 * 
 * @param <M>
 *          {@link remixlab.bias.branch.profile.MotionProfile} to parameterize the Agent with.
 * @param <C>
 *          {@link remixlab.bias.branch.profile.ClickProfile} to parameterize the Agent with.
 */
public class MotionBranch<E extends Enum<E>, A extends Action<E>, C extends Action<E>> extends
		Branch<E, A, MotionShortcut> {
	protected Profile<ClickShortcut, C>	clickProfile;

	/**
	 * 
	 * @param p
	 *          {@link remixlab.bias.branch.profile.MotionProfile} instance
	 * @param c
	 *          {@link remixlab.bias.branch.profile.ClickProfile} instance
	 * @param a
	 *          {@link remixlab.bias.core.Agent} instance
	 * @param n
	 *          the branch name
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
	 * Sets the {@link remixlab.bias.branch.profile.MotionProfile}
	 */
	public void setMotionProfile(Profile<MotionShortcut, A> profile) {
		setProfile(profile);
	}

	/**
	 * Returns the {@link remixlab.bias.branch.profile.ClickProfile} instance.
	 */
	public Profile<ClickShortcut, C> clickProfile() {
		return clickProfile;
	}

	/**
	 * Sets the {@link remixlab.bias.branch.profile.ClickProfile}
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
	protected boolean handle(BogusEvent event) {
		//TODO testing
		if (inputGrabber == null) {
			System.out.println("MotionBranch weird message throw by handle() that should never happen!");
			return false;
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
		return agent.inputHandler().enqueueEventTuple(new EventGrabberTuple(event, inputGrabber, action));
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
		motionProfile().setBinding(new MotionShortcut(mask, id), action);
	}

	public void setMotionBinding(int id, A action) {
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
		clickProfile().setBinding(new ClickShortcut(mask, button, ncs), action);
	}

	public void setClickBinding(int button, int ncs, C action) {
		clickProfile().setBinding(new ClickShortcut(button, ncs), action);
	}

	public void setClickBinding(int button, C action) {
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
