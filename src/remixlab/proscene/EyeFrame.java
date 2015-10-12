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
		setMotionBinding(NO_BUTTON, "gestureLookAround");
		setMotionBinding(LEFT_ID, "gestureMoveForward");		
		setMotionBinding(RIGHT_ID, "gestureMoveBackward");
		setClickBinding(LEFT_ID, 2, "gestureAlign");
		setClickBinding(RIGHT_ID, 2, "gestureCenter");
	}
}