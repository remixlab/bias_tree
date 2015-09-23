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

import remixlab.bias.event.*;

/**
 * A {@link remixlab.bias.branch.Branch} with a {@link #motionProfile()}, defining some
 * {@link remixlab.bias.event.MotionShortcut} / (motion) action mappings; and a {@link #clickProfile()},
 * defining some {@link remixlab.bias.event.ClickShortcut} / (click) action mappings. Motion branches
 * may be appended only to an {@link remixlab.bias.branch.GenericMotionAgent}. See
 * {@link remixlab.bias.branch.GenericMotionAgent#appendBranch(String)}.
 * <p>
 * <b>Note</b> that all the methods provided here are simply wrappers to the {@link #motionProfile()} and
 * {@link #clickProfile()}. See {@link remixlab.bias.branch.Profile}.
 *
 * @param <E> Reference action enum.
 * @param <A> Motion action enum sub-group.
 * @param <C> Click/tap action enum sub-group.
 */
public class MotionBranch<E extends Enum<E>, A extends Action<E>, C extends Action<E>> extends Branch<E> {
	GenericMotionAgent motionAgent;
	protected Profile<E, MotionShortcut, A>	motionProfile;
	protected Profile<E, ClickShortcut, C>	clickProfile;
	
	protected MotionBranch(GenericMotionAgent a, String n) {
		super(a, n);
		motionAgent = a;
		motionProfile = new Profile<E, MotionShortcut, A>();
		profiles.add(motionProfile);
		clickProfile = new Profile<E, ClickShortcut, C>();
		profiles.add(clickProfile);
	}

	protected MotionBranch(MotionBranch<E, A, C> other) {
		super(other);
		profiles.clear();
		motionProfile = other.motionProfile().get();
		clickProfile = other.clickProfile().get();
		profiles.add(motionProfile);
		profiles.add(clickProfile);
	}

	@Override
	public MotionBranch<E, A, C> get() {
		return new MotionBranch<E, A, C>(this);
	}
	
	/**
	 * Returns the motion {@link remixlab.bias.branch.Profile} instance.
	 */
	public Profile<E, MotionShortcut, A> motionProfile() {
		return motionProfile;
	}

	/**
	 * Returns the click {@link remixlab.bias.branch.Profile} instance.
	 */
	public Profile<E, ClickShortcut, C> clickProfile() {
		return clickProfile;
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
	
	/**
	 * Binds the mask-id shortcut to the (motion) action.
	 */
	public void setMotionBinding(int mask, int id, A action) {
		/*
		for(Branch<?> branch : motionAgent.branches())
			if(branch instanceof MotionBranch)
				if(branch != this)
					if(((MotionBranch<?, ?, ?>)branch).hasMotionBinding(mask, id))
						System.out.println("Warning: MotionShortcut already bound to " + ((MotionBranch<?, ?, ?>)branch).motionAction(mask, id) + " in " + branch.name());
						//*/
		motionProfile().setBinding(new MotionShortcut(mask, id), action);
	}

	/**
	 * Binds the id shortcut to the (motion) action.
	 */
	public void setMotionBinding(int id, A action) {
		/*
		for(Branch<?> branch : motionAgent.branches())
			if(branch instanceof MotionBranch)
				if(branch != this)
					if(((MotionBranch<?, ?, ?>)branch).hasMotionBinding(id))
						System.out.println("Warning: MotionShortcut already bound to " + ((MotionBranch<?, ?, ?>)branch).motionAction(id) + " in " + branch.name());
		//*/
		motionProfile().setBinding(new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, id), action);
	}

	/**
	 * Removes the mask-id binding (if present).
	 */
	public void removeMotionBinding(int mask, int id) {
		motionProfile().removeBinding(new MotionShortcut(mask, id));
	}

	/**
	 * Removes id shortcut binding (if present).
	 */
	public void removeMotionBinding(int id) {
		motionProfile().removeBinding(new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, id));
	}

	/**
	 * Returns {@code true} if the mask-id binds a (motion) action.
	 */
	public boolean hasMotionBinding(int mask, int id) {
		return motionProfile().hasBinding(new MotionShortcut(mask, id));
	}

	/**
	 * Returns {@code true} if the id binds a (motion) action.
	 */
	public boolean hasMotionBinding(int id) {
		return motionProfile().hasBinding(new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, id));
	}

	/**
	 * Returns the (motion) action that is bound to the given mask-id. Returns
	 * {@code null} if no action is bound.
	 */
	public A motionAction(int mask, int id) {
		return motionProfile().action(new MotionShortcut(mask, id));
	}

	/**
	 * Returns the (motion) action that is bound to the given id. Returns
	 * {@code null} if no action is bound.
	 */
	public A motionAction(int id) {
		return motionProfile().action(new MotionShortcut(MotionEvent.NO_MODIFIER_MASK, id));
	}
	
	// click
	
	/**
	 * Binds the mask/id/click-count shortcut to the (click) action.
	 */
	public void setClickBinding(int mask, int id, int ncs, C action) {
		/*
		for(Branch<?> branch : motionAgent.branches())
			if(branch instanceof MotionBranch)
				if(branch != this)
					if(((MotionBranch<?, ?, ?>)branch).hasClickBinding(mask, id, ncs))
						System.out.println("Warning: ClickShortcut already bound to " + ((MotionBranch<?, ?, ?>)branch).clickAction(mask, id, ncs) + " in " + branch.name());
	    //*/
		clickProfile().setBinding(new ClickShortcut(mask, id, ncs), action);
	}

	/**
	 * Binds the id/click-count shortcut to the (click) action.
	 */
	public void setClickBinding(int id, int ncs, C action) {
		/*
		for(Branch<?> branch : motionAgent.branches())
			if(branch instanceof MotionBranch)
				if(branch != this)
					if(((MotionBranch<?, ?, ?>)branch).hasClickBinding(id, ncs))
						System.out.println("Warning: ClickShortcut already bound to " + ((MotionBranch<?, ?, ?>)branch).clickAction(id, ncs) + " in " + branch.name());
		//*/
		clickProfile().setBinding(new ClickShortcut(id, ncs), action);
	}

	/**
	 * Binds id to the (click) action.
	 */
	public void setClickBinding(int id, C action) {
		/*
		for(Branch<?> branch : motionAgent.branches())
			if(branch instanceof MotionBranch)
				if(branch != this)
					if(((MotionBranch<?, ?, ?>)branch).hasClickBinding(id))
						System.out.println("Warning: ClickShortcut already bound to " + ((MotionBranch<?, ?, ?>)branch).clickAction(id) + " in " + branch.name());
		//*/
		clickProfile().setBinding(new ClickShortcut(id, 1), action);
	}

	/**
	 * Removes the mask/id/click-count binding (if present).
	 */
	public void removeClickBinding(int mask, int id, int ncs) {
		clickProfile().removeBinding(new ClickShortcut(mask, id, ncs));
	}

	/**
	 * Removes the id/click-count binding (if present).
	 */
	public void removeClickBinding(int id, int ncs) {
		clickProfile().removeBinding(new ClickShortcut(id, ncs));
	}

	/**
	 * Removes the id binding (if present).
	 */
	public void removeClickBinding(int id) {
		clickProfile().removeBinding(new ClickShortcut(id, 1));
	}

	/**
	 * Returns {@code true} if the mask/id/click-count binds a (click) action.
	 */
	public boolean hasClickBinding(int mask, int id, int ncs) {
		return clickProfile().hasBinding(new ClickShortcut(mask, id, ncs));
	}

	/**
	 * Returns {@code true} if the id/click-count binds a (click) action.
	 */
	public boolean hasClickBinding(int id, int ncs) {
		return clickProfile().hasBinding(new ClickShortcut(id, ncs));
	}

	/**
	 * Returns {@code true} if id binds a (click) action.
	 */
	public boolean hasClickBinding(int id) {
		return clickProfile().hasBinding(new ClickShortcut(id, 1));
	}

	/**
	 * Returns the (click) action that is bound to the given mask/id/click-count shortcut. Returns
	 * {@code null} if no action is bound.
	 */
	public C clickAction(int mask, int id, int ncs) {
		return clickProfile().action(new ClickShortcut(mask, id, ncs));
	}

	/**
	 * Returns the (click) action that is bound to the given id/click-count shortcut. Returns
	 * {@code null} if no action is bound.
	 */
	public C clickAction(int id, int ncs) {
		return clickProfile().action(new ClickShortcut(id, ncs));
	}

	/**
	 * Returns the (click) action that is bound to the given id. Returns
	 * {@code null} if no action is bound.
	 */
	public C clickAction(int id) {
		return clickProfile().action(new ClickShortcut(id, 1));
	}
}