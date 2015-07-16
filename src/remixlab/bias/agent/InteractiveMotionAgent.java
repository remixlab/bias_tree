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

import remixlab.bias.core.Action;
import remixlab.bias.core.Agent;
import remixlab.bias.core.Branch;
import remixlab.bias.core.InputHandler;

/**
 * An {@link remixlab.bias.core.Agent} that handles {@link remixlab.bias.agent.MotionBranch}es (see
 * {@link #appendBranch(String)}).
 */
public class InteractiveMotionAgent extends Agent {
	public InteractiveMotionAgent(InputHandler inputHandler, String name) {
		super(inputHandler, name);
	}
	
	@Override
	protected boolean appendBranch(Branch<?> branch) {
		if (branch instanceof MotionBranch)
			return super.appendBranch(branch);
		else
			throw new RuntimeException("Branch should be instanceof MotionBranch to be appended");
	}

	/**
	 * @param name of the {@link remixlab.bias.agent.MotionBranch} to be appended.
	 * 
	 * @return the appended {@link remixlab.bias.agent.MotionBranch}.
	 */
	public <E extends Enum<E>, B extends Action<E>, C extends Action<E>> MotionBranch<E, B, C> appendBranch(String name) {
		return new MotionBranch<E, B, C>(this, name);
	}
}