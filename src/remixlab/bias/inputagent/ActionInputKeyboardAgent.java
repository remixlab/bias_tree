package remixlab.bias.inputagent;

import remixlab.bias.agent.*;
import remixlab.bias.core.*;

public class ActionInputKeyboardAgent<A extends ActionKeyboardAgent<?>> extends Agent {
	public ActionInputKeyboardAgent(InputHandler inputHandler, String name) {
		super(inputHandler, name);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void addBranch(ActionAgent<?> actionAgent) {
		if( !(actionAgent instanceof ActionKeyboardAgent)) {
			System.out.println("Nothing added in " + this.name());
			return;
		}
		super.addBranch(actionAgent);
	}
}
