package remixlab.proscene;

import remixlab.dandelion.core.*;

public class EyeFrame extends GenericP5Frame {
	public EyeFrame(Eye eye) {
		super(eye);
	}
	
	protected EyeFrame(EyeFrame otherFrame) {
		super(otherFrame);
	}

	@Override
	public EyeFrame get() {
		return new EyeFrame(this);
	}
	
	//TODO remove me
	public void mouseAsFirstPerson() {
		removeMouseBindings();
		setMotionBinding(NO_BUTTON, "lookAround");
		setMotionBinding(LEFT_ID, "moveForward");		
		setMotionBinding(RIGHT_ID, "moveBackward");
		setClickBinding(LEFT_ID, 2, "align");
		setClickBinding(RIGHT_ID, 2, "center");
	}
}