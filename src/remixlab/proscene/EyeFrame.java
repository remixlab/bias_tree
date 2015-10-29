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
}