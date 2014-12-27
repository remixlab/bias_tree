package remixlab.dandelion.agent;

import remixlab.bias.agent.*;
import remixlab.bias.agent.profile.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

public class JoystickAgent extends Agent {
	class FrameAgent extends ActionWheeledMotionAgent<MotionProfile<FrameDOF1Action>, MotionProfile<FrameDOF3Action>, ClickProfile<FrameClickAction>> {
		public FrameAgent(Agent parent, String n) {
			super(new MotionProfile<FrameDOF1Action>(),
					new MotionProfile<FrameDOF3Action>(),
					new ClickProfile<FrameClickAction>(), parent, n);
		}

		@Override
		public DOF6Event feed() {
			return null;
		}

		public void setXTranslationSensitivity(float s) {
			sens[0] = s;
		}

		public void setYTranslationSensitivity(float s) {
			sens[1] = s;
		}
		
		public void setZTranslationSensitivity(float s) {
			sens[2] = s;
		}
	}
	
	class EyeAgent extends ActionWheeledMotionAgent<MotionProfile<EyeDOF1Action>, MotionProfile<EyeDOF3Action>, ClickProfile<EyeClickAction>> {
		public EyeAgent(Agent parent, String n) {
			super(new MotionProfile<EyeDOF1Action>(),
					new MotionProfile<EyeDOF3Action>(),
					new ClickProfile<EyeClickAction>(), parent, n);
		}

		@Override
		public DOF3Event feed() {
			return null;
		}

		public void setXTranslationSensitivity(float s) {
			sens[0] = s;
		}

		public void setYTranslationSensitivity(float s) {
			sens[1] = s;
		}
		
		public void setZTranslationSensitivity(float s) {
			sens[2] = s;
		}
	}
	
	protected AbstractScene	scene;
	FrameAgent frameAgent;
	EyeAgent eyeAgent;

	public JoystickAgent(AbstractScene scn, String n) {
		super(scn.inputHandler(), n);
		scene = scn;
		eyeAgent = new EyeAgent(this, "eye_mouse_agent");
		eyeAgent.setDefaultGrabber(scene.eye().frame());
		frameAgent = new FrameAgent(this, "frame_mouse_agent");
	}
	
	@Override
	public DOF3Event feed() {
		return null;
	}
}