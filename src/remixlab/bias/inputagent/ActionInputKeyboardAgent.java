package remixlab.bias.inputagent;

import remixlab.bias.agent.*;
import remixlab.bias.core.*;

public class ActionInputKeyboardAgent<A extends ActionKeyboardAgent<?>> extends InputAgent<A> {
	public ActionInputKeyboardAgent(InputHandler inputHandler, String name) {
		super(inputHandler, name);
		// TODO Auto-generated constructor stub
	}
	
	///*
	@Override
	public void addBranch(A actionAgent) {
		System.out.println("ActionInputKeyboardAgent add branch: " + actionAgent.name());
  	if (!brnchs.contains(actionAgent)) {
			this.brnchs.add(0, actionAgent);
		}
	}
	//*/
}
