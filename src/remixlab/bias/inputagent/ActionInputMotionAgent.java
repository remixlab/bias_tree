package remixlab.bias.inputagent;

import remixlab.bias.agent.*;
import remixlab.bias.core.*;

public class ActionInputMotionAgent<A extends ActionMotionAgent<?,?>> extends Agent<A> {
	public ActionInputMotionAgent(InputHandler inputHandler, String name) {
		super(inputHandler, name);
	}
	
	/*
	@Override
	public void addBranch(A actionAgent) {
		System.out.println("ActionInputMotionAgent add branch: " + actionAgent.name());
  	if (!brnchs.contains(actionAgent)) {
			this.brnchs.add(0, actionAgent);
		}
	}
	//*/
}
