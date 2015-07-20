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
 * An {@link remixlab.bias.core.Agent} to which {@link remixlab.bias.agent.KeyboardBranch}es may be appended
 * (see {@link #appendBranch(String)}).
 */
public abstract class AbstractKeyboardAgent extends Agent {
	public AbstractKeyboardAgent(InputHandler inputHandler, String name) {
		super(inputHandler, name);
	}
	
	@Override
	protected boolean appendBranch(Branch<?> branch) {
		if (branch instanceof KeyboardBranch)
			return super.appendBranch(branch);
		else
			throw new RuntimeException("Branch should be instanceof KeyboardBranch to be appended");
	}
	
	/**
	 * @param name of the {@link remixlab.bias.agent.KeyboardBranch} to be appended.
	 * 
	 * @return the appended {@link remixlab.bias.agent.KeyboardBranch}.
	 */
	public <E extends Enum<E>, A extends Action<E>> KeyboardBranch<E, A> appendBranch(String name) {
		return new KeyboardBranch<E, A>(this, name);
	}
	
	/**
	 * Return the key code for the given key. Should be implemented by derived class (platform specific).
	 * 
	 * @param key
	 * @return key code for the given key
	 */
	public abstract int keyCode(char key);
}