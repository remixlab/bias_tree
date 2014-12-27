package remixlab.dandelion.agent;

import remixlab.bias.agent.*;
import remixlab.bias.agent.profile.*;
import remixlab.bias.core.*;
import remixlab.dandelion.core.AbstractScene;
import remixlab.dandelion.core.Constants.*;

public class WheeledMotionAgent<A extends Action<?>, B extends Action<?>> extends Agent {
	protected AbstractScene scene;
	protected ActionWheeledMotionAgent<MotionProfile<EyeDOF1Action>, MotionProfile<A>, ClickProfile<EyeClickAction>> eyeAgent;
	protected ActionWheeledMotionAgent<MotionProfile<FrameDOF1Action>, MotionProfile<B>, ClickProfile<FrameClickAction>> frameAgent;
	
	public WheeledMotionAgent(AbstractScene scn, String n) {
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
	
	public ActionWheeledMotionAgent<MotionProfile<EyeDOF1Action>, MotionProfile<A>, ClickProfile<EyeClickAction>> eyeAgent() {
		return eyeAgent;
	}
	
	public ActionWheeledMotionAgent<MotionProfile<FrameDOF1Action>, MotionProfile<B>, ClickProfile<FrameClickAction>> frameAgent() {
		return frameAgent;
	}
}