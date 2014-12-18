package remixlab.bias.grabber;

import remixlab.bias.core.Action;
import remixlab.bias.core.Grabber;

public interface ActionGrabber<E extends Enum<E>>  extends Grabber {
	E referenceAction();
	void setReferenceAction(Action<E> a);
}
