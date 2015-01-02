package remixlab.bias.inputagent;

import remixlab.bias.agent.*;
import remixlab.bias.core.*;

// /*
public class ActionInputWheeledMotionAgent<A extends ActionWheeledMotionAgent<?,?,?>> extends InputAgent<A> {
	public ActionInputWheeledMotionAgent(InputHandler inputHandler, String name) {
		super(inputHandler, name);
	}
	
	///*
	@Override
	public void addBranch(A actionAgent) {
		System.out.println("ActionInputWheeledMotionAgent add branch: " + actionAgent.name());
  	if (!brnchs.contains(actionAgent)) {
			this.brnchs.add(0, actionAgent);
		}
	}
	//*/
}
//*/

/*
public class ActionInputWheeledMotionAgent<A extends ActionWheeledMotionAgent<?,?,?>> extends ActionInputMotionAgent<?,?> {

	public ActionInputWheeledMotionAgent(InputHandler inputHandler, String name) {
		super(inputHandler, name);
		// TODO Auto-generated constructor stub
	}
	
}
*/
