package remixlab.dandelion.agent;

import remixlab.bias.agent.*;
import remixlab.bias.agent.profile.*;
import remixlab.bias.core.*;
import remixlab.dandelion.core.AbstractScene;
import remixlab.dandelion.core.Constants.*;

//TODO probably not really needed
public class MotionAgent<A extends Action<?>, B extends Action<?>> extends Agent {
	protected AbstractScene scene;
	protected ActionMotionAgent<MotionProfile<A>, ClickProfile<EyeClickAction>> eyeAgent;
	protected ActionMotionAgent<MotionProfile<B>, ClickProfile<FrameClickAction>> frameAgent;
	
	public MotionAgent(AbstractScene scn, String n) {
		super(scn.inputHandler(), n);
		scene = scn;
		eyeAgent = new ActionWheeledMotionAgent<MotionProfile<EyeDOF1Action>, MotionProfile<A>, ClickProfile<EyeClickAction>>(new MotionProfile<EyeDOF1Action>(),
				new MotionProfile<A>(),
				new ClickProfile<EyeClickAction>(), this, (n + "_eye_mouse_agent"));
		eyeAgent.setDefaultGrabber(scene.eye().frame());
		frameAgent = new ActionWheeledMotionAgent<MotionProfile<FrameDOF1Action>, MotionProfile<B>, ClickProfile<FrameClickAction>>(new MotionProfile<FrameDOF1Action>(),
				new MotionProfile<B>(),
				new ClickProfile<FrameClickAction>(), this, (n + "_frame_mouse_agent"));
	}
	
	public void setXTranslationSensitivity(float s) {
		eyeAgent.sensitivities()[0] = s;
		frameAgent.sensitivities()[0] = s;
	}

	public void setYTranslationSensitivity(float s) {
		eyeAgent.sensitivities()[1] = s;
		frameAgent.sensitivities()[1] = s;
	}
	
	public ActionMotionAgent<MotionProfile<A>, ClickProfile<EyeClickAction>> eyeAgent() {
		return eyeAgent;
	}
	
	public ActionMotionAgent<MotionProfile<B>, ClickProfile<FrameClickAction>> frameAgent() {
		return frameAgent;
	}
}