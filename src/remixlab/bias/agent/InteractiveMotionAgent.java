package remixlab.bias.agent;

import remixlab.bias.core.Action;
import remixlab.bias.core.Agent;
import remixlab.bias.core.Branch;
import remixlab.bias.core.InputHandler;

public class InteractiveMotionAgent extends Agent {
	public InteractiveMotionAgent(InputHandler inputHandler, String name) {
		super(inputHandler, name);
	}
	
	//TODO discard me, use only next method
	@Override
	protected boolean appendBranch(Branch<?, ?, ?> branch) {
		if (branch instanceof MotionBranch)
			return super.appendBranch(branch);
		else {
			System.out.println("Branch should be instanceof MotionBranch to be appended");
			return false;
		}
	}
		
	//TODO what would be the method signature in Agent class this method overrides? (might allow to make all branch constructors protected)
	public <E extends Enum<E>, B extends Action<E>, C extends Action<E>> MotionBranch<E, B, C> appendBranch() {
		return new MotionBranch<E, B, C>(this, "my_motion_branch");
	}
}
