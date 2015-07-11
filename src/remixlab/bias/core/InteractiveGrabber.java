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

/**
 * The root of {@link remixlab.bias.core.Grabber} objects that may implement user-defined actions from
 * different sorts of input data (see {@link #performInteraction(BogusEvent)}).
 * <p>
 * User-defined actions to be performed by an InteractiveGrabber object should be defined by a third-party
 * from the Enum that parameterizes this object. Note that the enum value may be queried
 * with {@link remixlab.bias.core.Action#referenceAction()} (see {@link #action()}).
 * <p>
 * An InteractiveGrabber can then implement each action by overriding the
 * {@link remixlab.bias.core.Grabber#performInteraction(BogusEvent)} method, simply switching among the Enum
 * values, e.g.,
 * 
 * <pre>
 * {@code
 * public enum GlobalAction {
 *   CHANGE_COLOR,
 *   CHANGE_STROKE_WEIGHT,
 *   CHANGE_POSITION,
 *   CHANGE_SHAPE
 * }
 * }
 * </pre>
 * 
 * <pre>
 * {@code
 * public class MyInteractiveObject extends InteractiveGrabber<GlobalAction> {
 *   @Override
 *   public void performInteraction(BogusEvent event) {
 *     switch (action().referenceAction()) {
 *       case CHANGE_COLOR:
 *         // action 1 implementation
 *         break;
 *       // other cases
 *       case CHANGE_SHAPE:
 *         // action n implementation
 *         break;
 *      }
 *   }
 * }
 * }
 * </pre>
 *
 * @param <E> 'Reference' enum action set.
 */
public interface InteractiveGrabber<E extends Enum<E>> extends Grabber {
	/**
	 * Sets the current {@link remixlab.bias.core.Action} value. Called by
	 * {@link remixlab.bias.core.InteractiveEventGrabberTuple#perform()}. Should be implemented simply
	 * as (see {@link remixlab.bias.core.InteractiveGrabberObject#setAction(Action)}):
	 * 
	 * <pre>
     * {@code
	 * @Override
	 * public void setAction(Action<E> a) {
	 *	 action = a;
	 * }
	 * }
     * </pre>
	 */
	public void setAction(Action<E> action);

	/**
	 * Returns the current {@link remixlab.bias.core.Action} value. Should be implemented simply as
	 * ({see @link remixlab.bias.core.InteractiveGrabberObject#action()}):
	 * 
	 * <pre>
     * {@code
	 * @Override
	 * public Action<E> action() {
	 *   return action;
	 * }
     * }
     * </pre> 
     * 
     * <b>Note</b> that the action mapped 'reference' value may be retrieved by {@link remixlab.bias.core.Action#referenceAction()}.
	 */
	public Action<E> action();
}
