package remixlab.proscene;

import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.bias.ext.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.Frame;
import remixlab.util.*;

class GenericP5Frame extends InteractiveFrame implements Constants {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				appendSuper(super.hashCode()).
				append(profile).
				toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;

		GenericP5Frame other = (GenericP5Frame) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(profile, other.profile)
				.isEquals();
	}
	
	public Profile profile;
	
	public GenericP5Frame(Scene scn) {
		super(scn);
		profile = new Profile(this);
		setDefaultMouseBindings();
		setDefaultKeyBindings();
	}
	
	public GenericP5Frame(Scene scn, Frame referenceFrame) {
		super(scn, referenceFrame);
		profile = new Profile(this);
		setDefaultMouseBindings();
		setDefaultKeyBindings();
	}
	
	public GenericP5Frame(Eye eye) {
		super(eye);
		profile = new Profile(this);
		setDefaultMouseBindings();
		setDefaultKeyBindings();
	}
	
	protected GenericP5Frame(GenericP5Frame otherFrame) {
		super(otherFrame);
		this.profile = new Profile(this);
		this.profile.from(otherFrame.profile);
	}
	
	@Override
	public GenericP5Frame get() {
		return new GenericP5Frame(this);
	}
	
	@Override
	public void performInteraction(BogusEvent event) {
		if( profile.handle(event) )
			return;
	}
	
	//mouse move
	
	public void removeBindings() {
		profile.removeBindings();
	}

	// Motion
	
	/*
	public void setMotionBinding(String methodName) {
		profile.setMotionBinding(new MotionShortcut(), methodName);
	}
	
	public void setMotionBinding(Object object, String methodName) {
		profile.setMotionBinding(object, new MotionShortcut(), methodName);
	}
	*/
	
	public boolean hasMotionBinding() {
		return profile.hasBinding(new MotionShortcut());
	}
	
	public void removeMotionBinding() {
		profile.removeBinding(new MotionShortcut());
	}
	
	public void setMotionBinding(int id, String methodName) {
		profile.setMotionBinding(new MotionShortcut(id), methodName);
	}
	
	public void setMotionBinding(Object object, int id, String methodName) {
		profile.setMotionBinding(object, new MotionShortcut(id), methodName);
	}
	
	public boolean hasMotionBinding(int id) {
		return profile.hasBinding(new MotionShortcut(id));
	}
	
	public void removeMotionBinding(int id) {
		profile.removeBinding(new MotionShortcut(id));
	}
	
	public void removeMotionBindings() {
		profile.removeMotionBindings();
	}
	
	// Key
	
	public void setKeyBinding(int vkey, String methodName) {
		profile.setKeyboardBinding(new KeyboardShortcut(vkey), methodName);
	}
	
	public void setKeyBinding(char key, String methodName) {
		profile.setKeyboardBinding(new KeyboardShortcut(KeyAgent.keyCode(key)), methodName);
	}
	
	public void setKeyBinding(Object object, int vkey, String methodName) {
		profile.setKeyboardBinding(object, new KeyboardShortcut(vkey), methodName);
	}
	
	public void setKeyBinding(Object object, char key, String methodName) {
		profile.setKeyboardBinding(object, new KeyboardShortcut(KeyAgent.keyCode(key)), methodName);
	}
	
	public boolean hasKeyBinding(int vkey) {
		return profile.hasBinding(new KeyboardShortcut(vkey));
	}
	
	public boolean hasKeyBinding(char key) {
		return profile.hasBinding(new KeyboardShortcut(KeyAgent.keyCode(key)));
	}
	
	public void removeKeyBinding(int vkey) {
		profile.removeBinding(new KeyboardShortcut(vkey));
	}
	
	public void removeKeyBinding(char key) {
		profile.removeBinding(new KeyboardShortcut(KeyAgent.keyCode(key)));
	}
	
	public void setKeyBinding(int mask, char key, String methodName) {
		profile.setKeyboardBinding(new KeyboardShortcut(mask, KeyAgent.keyCode(key)), methodName);
	}
	
	public void setKeyBinding(Object object, int mask, char key, String methodName) {
		profile.setKeyboardBinding(object, new KeyboardShortcut(mask, KeyAgent.keyCode(key)), methodName);
	}
	
	public boolean hasKeyBinding(int mask, char key) {
		return profile.hasBinding(new KeyboardShortcut(mask, KeyAgent.keyCode(key)));
	}
	
	public void removeKeyBinding(int mask, char key) {
		profile.removeBinding(new KeyboardShortcut(mask, KeyAgent.keyCode(key)));
	}
	
	public void removeKeyBindings() {
		profile.removeKeyboardBindings();
	}
	
	// click
	
	public void setClickBinding(int id, int count, String methodName) {
		if(count == 1 || count == 2)
			profile.setClickBinding(new ClickShortcut(id, count), methodName);
	}
	
	public void setClickBinding(Object object, int id, int count, String methodName) {
		if(count == 1 || count == 2)
			profile.setClickBinding(object, new ClickShortcut(id, count), methodName);
	}
	
	public boolean hasClickBinding(int id, int count) {
		return profile.hasBinding(new ClickShortcut(id, count));
	}
	
	public void removeClickBinding(int id, int count) {
		profile.removeBinding(new ClickShortcut(id, count));
	}
	
	public void removeClickBindings() {
		profile.removeClickBindings();
	}
	
	public void removeMouseBindings() {
		removeMotionBinding(LEFT_ID);
		removeMotionBinding(CENTER_ID);
		removeMotionBinding(RIGHT_ID);
		removeMotionBinding(WHEEL_ID);
		removeMotionBinding(NO_BUTTON);
		removeClickBinding(LEFT_ID, 1);
		removeClickBinding(CENTER_ID, 1);
		removeClickBinding(RIGHT_ID, 1);
		removeClickBinding(LEFT_ID, 2);
		removeClickBinding(CENTER_ID, 2);
		removeClickBinding(RIGHT_ID, 2);
	}
	
	public void setDefaultMouseBindings() {
		removeMouseBindings();
		
		setMotionBinding(LEFT_ID, "gestureArcball");
		//TODO pending
		//setMotionBinding(CENTER_ID, "gestureScreenTranslate");
		setMotionBinding(RIGHT_ID, "gestureTranslateXY");
		setMotionBinding(WHEEL_ID, scene().is3D() ? isEyeFrame() ? "gestureTranslateZ" : "gestureScale" : "gestureScale");
		
		//removeClickBindings();
    	setClickBinding(LEFT_ID, 2, "gestureAlign");
		setClickBinding(RIGHT_ID, 2, "gestureCenter");
	}
	
	public void setDefaultKeyBindings() {
		removeKeyBindings();
		setKeyBinding('n', "gestureAlign");
		setKeyBinding('c', "gestureCenter");
		setKeyBinding(LEFT_KEY, "gestureTranslateXNeg");
		setKeyBinding(RIGHT_KEY, "gestureTranslateXPos");
		setKeyBinding(DOWN_KEY, "gestureTranslateYNeg");
		setKeyBinding(UP_KEY, "gestureTranslateYPos");
		setKeyBinding('z', "gestureRotateZNeg");
		setKeyBinding(BogusEvent.SHIFT, 'z', "gestureRotateZPos");
	}
	
	public Profile profile() {
		return profile;
	}
}
