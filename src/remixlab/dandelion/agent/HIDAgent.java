package remixlab.dandelion.agent;

import remixlab.bias.agent.*;
import remixlab.bias.agent.profile.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

///*
public class HIDAgent extends WheeledMotionAgent<EyeDOF6Action, FrameDOF6Action> {
	public HIDAgent(AbstractScene scn, String n) {
		super(scn, n);
	}
	
	@Override
	public DOF6Event feed() {
		return null;
	}
	
	public void setZTranslationSensitivity(float s) {
		eyeAgent.sensitivities()[2] = s;
		frameAgent.sensitivities()[2] = s;
	}

	public void setXRotationSensitivity(float s) {
		eyeAgent.sensitivities()[3] = s;
		frameAgent.sensitivities()[3] = s;
	}

	public void setYRotationSensitivity(float s) {
		eyeAgent.sensitivities()[4] = s;
		frameAgent.sensitivities()[4] = s;
	}

	public void setZRotationSensitivity(float s) {
		eyeAgent.sensitivities()[5] = s;
		frameAgent.sensitivities()[5] = s;
	}
}
//*/

/*
public class HIDAgent extends Agent {
	class FrameAgent extends ActionWheeledMotionAgent<MotionProfile<FrameDOF1Action>, MotionProfile<FrameDOF6Action>, ClickProfile<FrameClickAction>> {
		public FrameAgent(Agent parent, String n) {
			super(new MotionProfile<FrameDOF1Action>(),
					new MotionProfile<FrameDOF6Action>(),
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

		public void setXRotationSensitivity(float s) {
			sens[3] = s;
		}

		public void setYRotationSensitivity(float s) {
			sens[4] = s;
		}

		public void setZRotationSensitivity(float s) {
			sens[5] = s;
		}
	}
	
	class EyeAgent extends ActionWheeledMotionAgent<MotionProfile<EyeDOF1Action>, MotionProfile<EyeDOF6Action>, ClickProfile<EyeClickAction>> {
		public EyeAgent(Agent parent, String n) {
			super(new MotionProfile<EyeDOF1Action>(),
					new MotionProfile<EyeDOF6Action>(),
					new ClickProfile<EyeClickAction>(), parent, n);
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

		public void setXRotationSensitivity(float s) {
			sens[3] = s;
		}

		public void setYRotationSensitivity(float s) {
			sens[4] = s;
		}

		public void setZRotationSensitivity(float s) {
			sens[5] = s;
		}
	}
	
	protected AbstractScene	scene;
	FrameAgent frameAgent;
	EyeAgent eyeAgent;

	public HIDAgent(AbstractScene scn, String n) {
		super(scn.inputHandler(), n);
		scene = scn;
		eyeAgent = new EyeAgent(this, "eye_hidagent_agent");
		eyeAgent.setDefaultGrabber(scene.eye().frame());
		frameAgent = new FrameAgent(this, "frame_hidagent_agent");
	}
	
	@Override
	public DOF6Event feed() {
		return null;
	}
}
//*/