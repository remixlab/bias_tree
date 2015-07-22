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

import remixlab.bias.core.*;

/**
 * An {@link remixlab.bias.core.Agent} that handles {@link remixlab.bias.agent.MotionBranch}es (see
 * {@link #appendBranch(String)}).
 * <p>
 * The motion agent defines two picking modes (for selecting {@link remixlab.bias.core.Grabber}
 * objects, see {@link #setPickingMode(PickingMode)} and {@link #pickingMode()}):
 * <ol>
 * <li>{@link PickingMode#MOVE}: object selection happens during a drag gesture.</li>
 * <li>{@link PickingMode#CLICK}: object selection happens from a click gesture.</li>
 * </ol>
 */
public abstract class AbstractMotionAgent extends Agent {
	protected PickingMode pMode;

	public enum PickingMode {
		MOVE, CLICK
	};
	
	/**
	 * Constructs a motion agent, registers it at the {@link #inputHandler()} and sets its {@link #pickingMode()}
	 * to {@link PickingMode#MOVE}.
	 */
	public AbstractMotionAgent(InputHandler inputHandler, String name) {
		super(inputHandler, name);
		setPickingMode(PickingMode.MOVE);
	}
	
	/**
	 * Sets the agent {@link #pickingMode()}. Either {@link PickingMode#MOVE} or {@link PickingMode#CLICK}.
	 * 
	 * @see #pickingMode()
	 */
	public void setPickingMode(PickingMode mode) {
		pMode = mode;
	}

	/**
	 * Returns the agent {@link #pickingMode()}. Either {@link PickingMode#MOVE} or {@link PickingMode#CLICK}.
	 * 
	 * @see #setPickingMode(PickingMode)
	 */
	public PickingMode pickingMode() {
		return pMode;
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