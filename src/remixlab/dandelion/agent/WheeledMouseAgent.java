package remixlab.dandelion.agent;

import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.core.*;

public class WheeledMouseAgent extends Agent {	
	protected AbstractScene	scene;
	FrameMouseAgent frameAgent;
	EyeMouseAgent eyeAgent;

	public WheeledMouseAgent(AbstractScene scn, String n) {
		super(scn.inputHandler(), n);
		scene = scn;
		eyeAgent = new EyeMouseAgent(this, "eye_mouse_agent");
		eyeAgent.setDefaultGrabber(scene.eye().frame());
		frameAgent = new FrameMouseAgent(this, "frame_mouse_agent");
	}
	
	@Override
	public DOF2Event feed() {
		return null;
	}
}
