package remixlab.bias.core;

/**
 * A [{@link remixlab.bias.core.BogusEvent},{@link remixlab.bias.core.InteractiveGrabber},
 * {@link remixlab.bias.core.Action}] tuple. An enqueued tuple
 * fires a {@link remixlab.bias.core.InteractiveGrabber#performInteraction(BogusEvent)}
 * call from the event in the tuple. For details refer to the
 * {@link remixlab.bias.core.InteractiveGrabber} documentation.
 * <p>
 * Tuples are typically enqueued by an agent (through their branches, see
 * {@link remixlab.bias.core.Agent#handle(BogusEvent)}), but may be enqueued
 * manually, see {@link remixlab.bias.core.InputHandler#enqueueEventTuple(EventGrabberTuple)}.
 */
public class InteractiveEventGrabberTuple<E extends Enum<E>> extends EventGrabberTuple {
	InteractiveGrabber<E> iGrabber;
	Action<E> iAction;
	
	/**
	 * @param e
	 *          {@link remixlab.bias.core.BogusEvent}
	 * @param g
	 *          {@link remixlab.bias.core.InteractiveGrabber}
	 * @param a
	 *          {@link remixlab.bias.core.Action}
	 */
	public InteractiveEventGrabberTuple(BogusEvent e, InteractiveGrabber<E> g, Action<E> a) {
		super(e, g);
		iGrabber = g;
		iAction = a;
	}
	
	@Override
	public boolean perform() {
		if(iGrabber == null || iAction == null || event == null)
			return false;
		iGrabber.setAction(iAction);
		iGrabber.performInteraction(event);
		return true;
	}
	
	/**
	 * Returns the object Grabber in the tuple.
	 */
	@Override
	public InteractiveGrabber<E> grabber() {
		return iGrabber;
	}

	public Action<E> action() {
		return iAction;
	}
}