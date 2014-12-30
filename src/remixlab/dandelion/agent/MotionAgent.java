package remixlab.dandelion.agent;

import remixlab.bias.agent.*;
import remixlab.bias.agent.profile.*;
import remixlab.bias.core.*;
import remixlab.dandelion.core.AbstractScene;
import remixlab.dandelion.core.Constants.*;

//TODO probably not really needed
public class MotionAgent<A extends Action<?>> extends Agent {
	protected AbstractScene scene;
	protected ActionMotionAgent<MotionProfile<A>, ClickProfile<ClickAction>> eyeAgent;
	protected ActionMotionAgent<MotionProfile<A>, ClickProfile<ClickAction>> frameAgent;
	
	public MotionAgent(AbstractScene scn, String n) {
		super(scn.inputHandler(), n);
		scene = scn;
		eyeAgent = new ActionWheeledMotionAgent<MotionProfile<DOF1Action>, MotionProfile<A>, ClickProfile<ClickAction>>(new MotionProfile<DOF1Action>(),
				new MotionProfile<A>(),
				new ClickProfile<ClickAction>(), this, (n + "_eye_mouse_agent"));
		eyeAgent.setDefaultGrabber(scene.eye().frame());
		frameAgent = new ActionWheeledMotionAgent<MotionProfile<DOF1Action>, MotionProfile<A>, ClickProfile<ClickAction>>(new MotionProfile<DOF1Action>(),
				new MotionProfile<A>(),
				new ClickProfile<ClickAction>(), this, (n + "_frame_mouse_agent"));
	}
	
	public void setXTranslationSensitivity(float s) {
		eyeAgent.sensitivities()[0] = s;
		frameAgent.sensitivities()[0] = s;
	}

	public void setYTranslationSensitivity(float s) {
		eyeAgent.sensitivities()[1] = s;
		frameAgent.sensitivities()[1] = s;
	}
	
	public ActionMotionAgent<MotionProfile<A>, ClickProfile<ClickAction>> eyeAgent() {
		return eyeAgent;
	}
	
	public ActionMotionAgent<MotionProfile<A>, ClickProfile<ClickAction>> frameAgent() {
		return frameAgent;
	}
}