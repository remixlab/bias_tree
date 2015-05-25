/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.core;

public interface InteractiveGrabber<E extends Enum<E>> extends Grabber {
	// E referenceAction();
	// void setReferenceAction(Action<E> a);
	// void setReferenceAction(Action<?> a);

	// option 2... testing
	public void setAction(Action<E> action);

	public Action<E> action();
	
	public boolean processAction(BogusEvent event);
}
