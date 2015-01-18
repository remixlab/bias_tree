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

/**
 * A {@link remixlab.bias.branch.MotionBranch} with an extra {@link remixlab.bias.branch.profile.MotionProfile} defining
 * {@link remixlab.bias.event.shortcut.MotionShortcut} -> {@link remixlab.bias.core.Action} mappings.
 * <p>
 * The Agent thus is defined by three profiles: the {@link #motionProfile()} (alias for {@link #profile()} provided for
 * convenience), the {@link #clickProfile()} and the extra {@link #wheelProfile()}.
 * 
 * @param <W>
 *          {@link remixlab.bias.branch.profile.MotionProfile} to parameterize the Agent with.
 * @param <M>
 *          {@link remixlab.bias.branch.profile.MotionProfile} to parameterize the Agent with.
 * @param <C>
 *          {@link remixlab.bias.branch.profile.ClickProfile} to parameterize the Agent with.
 */
public class WheeledMotionBranch<E extends Enum<E>, W extends MotionProfile<? extends Action<E>>, M extends MotionProfile<? extends Action<E>>, C extends ClickProfile<? extends Action<E>>>
		extends MotionBranch<E, M, C> {

	protected W	wheelProfile;

	/**
	 * @param w
	 *          {@link remixlab.bias.branch.profile.MotionProfile} instance
	 * @param p
	 *          {@link remixlab.bias.branch.profile.MotionProfile} second instance
	 * @param c
	 *          {@link remixlab.bias.branch.profile.ClickProfile} instance
	 * @param a
	 *          {@link remixlab.bias.core.Agent} instance
	 * @param n
	 *          Agent name
	 */
	public WheeledMotionBranch(W w, M p, C c, Agent a, String n) {
		super(p, c, a, n);
		wheelProfile = w;
	}

	/**
	 * @return the agents second {@link remixlab.bias.branch.profile.MotionProfile} instance.
	 */
	public W wheelProfile() {
		return wheelProfile;
	}

	/**
	 * Sets the {@link remixlab.bias.branch.profile.MotionProfile} second instance.
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
	public Action<E> handle(ActionGrabber<E> grabber, BogusEvent event) {
		if (grabber == null || event == null)
			return null;
		Action<E> action = null;
		if (event instanceof DOF1Event)
			action = wheelProfile().handle(event);
		else if (event instanceof MotionEvent)
			action = profile().handle(event);
		else if (event instanceof ClickEvent)
			action = clickProfile.handle(event);
		//if (action != null)	grabber.setAction(action);
		return action;
	}
}
