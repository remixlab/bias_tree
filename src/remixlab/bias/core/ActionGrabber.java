package remixlab.bias.core;

public interface ActionGrabber<E extends Enum<E>>
  ///*
  extends Grabber
  // */ 
{
	E referenceAction();
	void setReferenceAction(Action<E> a);
}
