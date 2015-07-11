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
 * Generic interface defining user action (sub)groups.
 * <p>
 * (User-defined) actions in bias should be defined by a third-party simply using an Enum. This interface allows
 * grouping items of that 'reference' action Enum ({@link #referenceAction()}) together, thus possibly forming
 * action sub-groups. Action subgroups are typically bound to different data input kinds
 * ({@link remixlab.bias.core.BogusEvent}), such as that gathered from tap and drag gestures.
 * <p>
 * Since the {@link #referenceAction()} is used to parameterize an {@link remixlab.bias.core.InteractiveGrabber}
 * object, each value in the action sub-group should be mapped to a value in the (see {@link #referenceAction()}),
 * otherwise the {@link remixlab.bias.core.InteractiveGrabber} wouldn't be able to discriminate among different
 * data input kinds (e.g., a tap from a drag gesture).
 * <p>
 * <b>Note:</b> all data input related to a single entity or device (such as the mouse) that binds an
 * {@link remixlab.bias.core.InteractiveGrabber} is handled by agent {@link remixlab.bias.core.Branch}es.
 * <p>
 * <b>Observation</b> Enums provide an easy (typical) implementation of this Interface. For example, given the
 * following global Action set:
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
 * An implementation of an Action group defined with the CHANGE_POSITION and CHANGE_SHAPE items, would then look
 * like this:
 * 
 * <pre>
 * {@code
 * public enum MotionAction implements Action<GlobalAction> {
 *   CHANGE_POSITION(GlobalAction.CHANGE_POSITION), 
 *   CHANGE_SHAPE(GlobalAction.CHANGE_SHAPE);
 *   
 *   public GlobalAction referenceAction() {
 *     return act;
 *   }
 * 
 *   public String description() {
 *     return "A simple motion action";
 *   }
 * 
 *   GlobalAction act;
 * 
 *   MotionAction(GlobalAction a) {
 *     act = a;
 *   }
 * }
 * }
 * </pre>
 * 
 * @param <E> 'Reference' enum action set.
 */
public interface Action<E extends Enum<E>> {
	/**
	 * Returns group to reference-action mappings.
	 */
	E referenceAction();

	/**
	 * Returns a description of the action.
	 */
	String description();
}
