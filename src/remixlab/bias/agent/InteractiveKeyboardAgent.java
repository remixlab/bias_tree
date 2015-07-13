package remixlab.bias.agent;

import remixlab.bias.core.*;

public class InteractiveKeyboardAgent extends Agent {
	public InteractiveKeyboardAgent(InputHandler inputHandler, String name) {
		super(inputHandler, name);
	}
	
	@Override
	protected boolean appendBranch(Branch<?> branch) {
		if (branch instanceof KeyboardBranch)
			return super.appendBranch(branch);
		else {
			System.out.println("Branch should be instanceof KeyboardBranch to be appended");
			return false;
		}
	}
		
	public <E extends Enum<E>, A extends Action<E>> KeyboardBranch<E, A> appendBranch(String name) {
		return new KeyboardBranch<E, A>(this, name);
	}
	
	public int keyCode(char key) {
		System.err.println("keyCode(char) should be implemented by your Agent derived class");
		return BogusEvent.NO_ID;
	}
}