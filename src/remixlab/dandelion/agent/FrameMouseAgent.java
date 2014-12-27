package remixlab.dandelion.agent;

import remixlab.bias.agent.*;
import remixlab.bias.agent.profile.*;
import remixlab.bias.core.Agent;
import remixlab.bias.event.*;
import remixlab.dandelion.core.Constants.*;

/**
 * An abstract {@link remixlab.dandelion.agent.ActionWheeledBiMotionAgent} representing a pointing device supporting 2
 * Degrees-Of-Freedom (e.g., two translations or two rotations), such as most mice.
 */
//TODO add into WheeledMouseAgent when fisnished
class FrameMouseAgent extends ActionWheeledMotionAgent<MotionProfile<FrameDOF1Action>, MotionProfile<FrameDOF2Action>, ClickProfile<FrameClickAction>> {
	/**
	 * Constructs a PointingAgent. Nothing fancy.
	 * 
	 * @param scn
	 *          AbstractScene
	 * @param n
	 *          Agents name
	 */
	public FrameMouseAgent(Agent parent, String n) {
		super(new MotionProfile<FrameDOF1Action>(),
				new MotionProfile<FrameDOF2Action>(),
				new ClickProfile<FrameClickAction>(), parent, n);
	}

	@Override
	public DOF2Event feed() {
		return null;
	}

	/**
	 * Sets the mouse translation sensitivity along X.
	 */
	public void setXTranslationSensitivity(float s) {
		sens[0] = s;
	}

	/**
	 * Sets the mouse translation sensitivity along Y.
	 */
	public void setYTranslationSensitivity(float s) {
		sens[1] = s;
	}
}