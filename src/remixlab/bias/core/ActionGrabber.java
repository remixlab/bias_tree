
package remixlab.bias.core;

public interface ActionGrabber<E extends Enum<E>> extends Grabber {
	// E referenceAction();
	// void setReferenceAction(Action<E> a);
	// void setReferenceAction(Action<?> a);

	// option 2... testing
	public void setAction(Action<E> action);

	public Action<E> action();
}
