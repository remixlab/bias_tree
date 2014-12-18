package remixlab.proscene;

import remixlab.bias.grabber.ActionGrabber;

public interface ActionModel <E extends Enum<E>> extends Model, ActionGrabber<E> {}
