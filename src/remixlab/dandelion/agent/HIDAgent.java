
package remixlab.dandelion.agent;

import remixlab.bias.event.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

public class HIDAgent extends WheeledMotionAgent<DOF6Action> {
	public HIDAgent(AbstractScene scn, String n) {
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

	public void setXRotationSensitivity(float s) {
		eyeBranch.sensitivities()[3] = s;
		frameBranch.sensitivities()[3] = s;
	}

	public void setYRotationSensitivity(float s) {
		eyeBranch.sensitivities()[4] = s;
		frameBranch.sensitivities()[4] = s;
	}

	public void setZRotationSensitivity(float s) {
		eyeBranch.sensitivities()[5] = s;
		frameBranch.sensitivities()[5] = s;
	}
}