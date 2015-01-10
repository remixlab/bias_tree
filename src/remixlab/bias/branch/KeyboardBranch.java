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

import remixlab.bias.branch.profile.KeyboardProfile;
import remixlab.bias.core.*;

/**
 * This class is provided purely for symmetry and style reasons against the events and shortcuts API. Only needed if you
 * plan to implement your own KeyboardAgent.
 * 
 * @param <K>
 *          The {@link remixlab.bias.branch.profile.KeyboardProfile} to parameterize this Agent with.
 */
public class KeyboardBranch<E extends Enum<E>, K extends KeyboardProfile<? extends Action<E>>> extends Branch<E, K> {

	public KeyboardBranch(K k, Agent pnt, String n) {
		super(k, pnt, n);
	}

	/**
	 * @return The {@link remixlab.bias.branch.profile.KeyboardProfile}
	 */
	public K keyboardProfile() {
		return profile();
	}

	/**
	 * Sets the The {@link remixlab.bias.branch.profile.KeyboardProfile}.
	 */
	public void setKeyboardProfile(K kprofile) {
		setProfile(profile);
	}
}
