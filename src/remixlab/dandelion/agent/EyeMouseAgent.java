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
class EyeMouseAgent extends ActionWheeledMotionAgent<MotionProfile<EyeDOF1Action>, MotionProfile<EyeDOF2Action>, ClickProfile<EyeClickAction>> {
	/**
	 * Constructs a PointingAgent. Nothing fancy.
	 * 
	 * @param scn
	 *          AbstractScene
	 * @param n
	 *          Agents name
	 */
	public EyeMouseAgent(Agent parent, String n) {
		super(new MotionProfile<EyeDOF1Action>(),
				new MotionProfile<EyeDOF2Action>(),
				new ClickProfile<EyeClickAction>(), parent, n);
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
