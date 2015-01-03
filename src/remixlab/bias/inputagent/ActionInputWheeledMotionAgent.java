package remixlab.bias.inputagent;

import remixlab.bias.agent.*;
import remixlab.bias.core.*;

// /*
public class ActionInputWheeledMotionAgent<A extends ActionWheeledMotionAgent<?,?,?>> extends Agent {
	public ActionInputWheeledMotionAgent(InputHandler inputHandler, String name) {
		super(inputHandler, name);
	}
	
	@Override
	public void addBranch(ActionAgent<?> actionAgent) {
		if( !(actionAgent instanceof ActionWheeledMotionAgent)) {
			System.out.println("Nothing added in " + this.name());
			return;
		}
		super.addBranch(actionAgent);
	}
}
