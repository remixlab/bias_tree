package remixlab.bias.inputagent;

import remixlab.bias.agent.*;
import remixlab.bias.core.*;

public class ActionInputMotionAgent extends Agent {
	public ActionInputMotionAgent(InputHandler inputHandler, String name) {
		super(inputHandler, name);
	}
	
	@Override
	public void addBranch(ActionAgent<?> actionAgent) {
		if( !(actionAgent instanceof ActionMotionAgent)) {
			System.out.println("Nothing added in " + this.name());
			return;
		}
		super.addBranch(actionAgent);
	}
}
