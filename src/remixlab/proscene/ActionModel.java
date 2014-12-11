package remixlab.proscene;

import remixlab.bias.core.*;

public interface ActionModel <E extends Enum<E>>
///*
extends Model
// */ 
{
E referenceAction();
void setReferenceAction(Action<E> a);
}
