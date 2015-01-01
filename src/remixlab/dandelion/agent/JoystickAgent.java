
package remixlab.dandelion.agent;

import remixlab.bias.event.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

public class JoystickAgent extends WheeledMotionAgent<DOF3Action> {
	public JoystickAgent(AbstractScene scn, String n) {
		super(scn, n);
	}

	@Override
	public DOF6Event feed() {
		return null;
	}

	public void setZTranslationSensitivity(float s) {
		eyeBranch.sensitivities()[2] = s;
		frameBranch.sensitivities()[2] = s;
	}
}