/**************************************************************************************
 * ProScene (version 3.0.0)
 * Copyright (c) 2010-2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 * 
 * All rights reserved. Library that eases the creation of interactive scenes
 * in Processing, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.proscene;

import remixlab.bias.core.InteractiveGrabber;

/**
 * {@link remixlab.proscene.Model} and {@link remixlab.bias.core.InteractiveGrabber} linking interface.
 *
 * @param <E> Reference action used to parameterize the {@link remixlab.bias.core.InteractiveGrabber}
 * @see remixlab.proscene.Model
 * @see remixlab.bias.core.InteractiveGrabber
 */
public interface InteractiveModel<E extends Enum<E>> extends Model, InteractiveGrabber<E> {
}
