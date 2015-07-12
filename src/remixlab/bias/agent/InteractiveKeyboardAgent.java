package remixlab.bias.agent;

import remixlab.bias.core.*;

public class InteractiveKeyboardAgent extends Agent {
	public InteractiveKeyboardAgent(InputHandler inputHandler, String name) {
		super(inputHandler, name);
	}
	
	//TODO discard me, use only next method
	@Override
	protected boolean appendBranch(Branch<?, ?, ?> branch) {
		if (branch instanceof KeyboardBranch)
			return super.appendBranch(branch);
		else {
			System.out.println("Branch should be instanceof KeyboardBranch to be appended");
			return false;
		}
	}
		
	//TODO what would be the method signature in Agent class this method overrides? (might allow to make all branch constructors protected)
	public <E extends Enum<E>, A extends Action<E>> KeyboardBranch<E, A> appendBranch() {
		return new KeyboardBranch<E, A>(this, "my_key_branch");
	}
	
	// Char hack from here
	public int keyCode(char key) {
		System.err.println("keyCode(char) should be implemented by your Agent derived class");
		return BogusEvent.NO_ID;
	}
}
