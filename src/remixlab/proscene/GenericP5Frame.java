package remixlab.proscene;

import java.lang.reflect.Method;

import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.bias.fx.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.AbstractScene.Platform;
import remixlab.dandelion.geom.Frame;
import remixlab.util.*;

public class GenericP5Frame extends GenericFrame {	
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
	
	@Override
	public Scene scene() {
		return (Scene)gScene;
	}
	
	public Profile profile;
	
	public GenericP5Frame(Scene scn) {
		super(scn);
		profile = new Profile(this);
		setDefaultBindings();
		addActionHandlers();
	}
	
	public GenericP5Frame(Scene scn, Frame referenceFrame) {
		super(scn, referenceFrame);
		profile = new Profile(this);
		if(referenceFrame instanceof GenericP5Frame)
			this.profile.from(((GenericP5Frame)referenceFrame).profile);
		else
			setDefaultBindings();
		//TODO copy pending
		addActionHandlers();
	}
	
	public GenericP5Frame(Eye eye) {
		super(eye);
		profile = new Profile(this);
		setDefaultBindings();
		addActionHandlers();
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
		profile.handle(event);
	}
	
	@Override
	protected boolean checkIfGrabsInput(KeyboardEvent event) {
		return profile.hasBinding(event.shortcut());
	}
	
	//mouse move
	
	public void removeBindings() {
		profile.removeBindings();
	}
	
	public Method action(Shortcut key) {
		return profile.action(key);
	}
	
	public boolean isActionBound(String method) {
		return profile.isActionBound(method);
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
	
	/*
	public boolean hasMotionBinding() {
		return profile.hasBinding(new MotionShortcut());
	}
	
	public void removeMotionBinding() {
		profile.removeBinding(new MotionShortcut());
	}
	*/
	
    //TODO decide these three:
	
	public void setDefaultBindings() {
		setDefaultMotionBindings();
		setDefaultKeyBindings();
	}
	
	protected void addActionHandlers() {
		profile.addActionHandler("initKeyboard");
		profile.addActionHandler("flushKeyboard");
		
		profile.addActionHandler("initMotion");
		profile.addActionHandler("execMotion");
		profile.addActionHandler("flushMotion");
		
		profile.addActionHandler("initDOF2");
		profile.addActionHandler("execDOF2");
		profile.addActionHandler("flushDOF2");
		
		profile.addActionHandler("initDOF3");
		profile.addActionHandler("execDOF3");
		profile.addActionHandler("flushDOF3");
		
		profile.addActionHandler("initDOF6");
		profile.addActionHandler("execDOF6");
		profile.addActionHandler("flushDOF6");
	}
	
	public void setDefaultMotionBindings() {
		if(Scene.platform() == Platform.PROCESSING_DESKTOP)
			scene().mouseAgent().setDefaultBindings(this);
	}
	
	public void setDefaultKeyBindings() {
		removeKeyBindings();
		setKeyBinding('n', "align");
		setKeyBinding('c', "center");
		setKeyBinding(KeyAgent.LEFT_KEY, "translateXNeg");
		setKeyBinding(KeyAgent.RIGHT_KEY, "translateXPos");
		setKeyBinding(KeyAgent.DOWN_KEY, "translateYNeg");
		setKeyBinding(KeyAgent.UP_KEY, "translateYPos");
		setKeyBinding(BogusEvent.SHIFT, KeyAgent.LEFT_KEY, "rotateXNeg");
		setKeyBinding(BogusEvent.SHIFT, KeyAgent.RIGHT_KEY, "rotateXPos");
		setKeyBinding(BogusEvent.SHIFT, KeyAgent.DOWN_KEY, "rotateYNeg");
		setKeyBinding(BogusEvent.SHIFT, KeyAgent.UP_KEY, "rotateYPos");	
		setKeyBinding('z', "rotateZNeg");
		setKeyBinding('Z', "rotateZPos");
	}
	
	//
	
	public void setMotionBinding(int id, String methodName) {
		profile.setMotionBinding(new MotionShortcut(id), methodName);
	}
	
	public void setMotionBinding(Object object, int id, String methodName) {
		profile.setMotionBinding(object, new MotionShortcut(id), methodName);
	}
	
	public void removeMotionBindings() {
		profile.removeMotionBindings();
	}
	
	// good for all dofs :P
	
	public boolean hasMotionBinding(int id) {
		return profile.hasBinding(new MotionShortcut(id));
	}
	
	public void removeMotionBinding(int id) {
		profile.removeBinding(new MotionShortcut(id));
	}
	
	public void removeMotionBindings(int [] ids) {
		profile.removeMotionBindings(ids);
	}
	
	// DOF1
	
	public void setDOF1Binding(int id, String methodName) {
		profile.setDOF1Binding(new MotionShortcut(id), methodName);
	}
	
	public void setDOF1Binding(Object object, int id, String methodName) {
		profile.setDOF1Binding(object, new MotionShortcut(id), methodName);
	}
	
	// DOF2
	
	public void setDOF2Binding(int id, String methodName) {
		profile.setDOF2Binding(new MotionShortcut(id), methodName);
	}
		
	public void setDOF2Binding(Object object, int id, String methodName) {
		profile.setDOF2Binding(object, new MotionShortcut(id), methodName);
	}
	
	// DOF3
	
	public void setDOF3Binding(int id, String methodName) {
		profile.setDOF3Binding(new MotionShortcut(id), methodName);
	}
		
	public void setDOF3Binding(Object object, int id, String methodName) {
		profile.setDOF3Binding(object, new MotionShortcut(id), methodName);
	}
	
	// DOF6
	
	public void setDOF6Binding(int id, String methodName) {
		profile.setDOF6Binding(new MotionShortcut(id), methodName);
	}
		
	public void setDOF6Binding(Object object, int id, String methodName) {
		profile.setDOF6Binding(object, new MotionShortcut(id), methodName);
	}
	//*/
	
	// Key
	
	public void setKeyBinding(int vkey, String methodName) {
		profile.setKeyboardBinding(new KeyboardShortcut(vkey), methodName);
	}
	
	public void setKeyBinding(char key, String methodName) {
		profile.setKeyboardBinding(new KeyboardShortcut(key), methodName);
	}
	
	public void setKeyBinding(Object object, int vkey, String methodName) {
		profile.setKeyboardBinding(object, new KeyboardShortcut(vkey), methodName);
	}
	
	public void setKeyBinding(Object object, char key, String methodName) {
		profile.setKeyboardBinding(object, new KeyboardShortcut(key), methodName);
	}
	
	public boolean hasKeyBinding(int vkey) {
		return profile.hasBinding(new KeyboardShortcut(vkey));
	}
	
	public boolean hasKeyBinding(char key) {
		return profile.hasBinding(new KeyboardShortcut(key));
	}
	
	public void removeKeyBinding(int vkey) {
		profile.removeBinding(new KeyboardShortcut(vkey));
	}
	
	public void removeKeyBinding(char key) {
		profile.removeBinding(new KeyboardShortcut(key));
	}
	
	public void setKeyBinding(int mask, int vkey, String methodName) {
		profile.setKeyboardBinding(new KeyboardShortcut(mask, vkey), methodName);
	}
	
	public void setKeyBinding(Object object, int mask, int vkey, String methodName) {
		profile.setKeyboardBinding(object, new KeyboardShortcut(mask, vkey), methodName);
	}
	
	public boolean hasKeyBinding(int mask, int vkey) {
		return profile.hasBinding(new KeyboardShortcut(mask, vkey));
	}
	
	public void removeKeyBinding(int mask, int vkey) {
		profile.removeBinding(new KeyboardShortcut(mask, vkey));
	}
	
	public void setKeyBinding(int mask, char key, String methodName) {
		setKeyBinding(mask, KeyAgent.keyCode(key), methodName);
	}
	
	public void setKeyBinding(Object object, int mask, char key, String methodName) {
		setKeyBinding(object, mask, KeyAgent.keyCode(key), methodName);
	}
	
	public boolean hasKeyBinding(int mask, char key) {
		return hasKeyBinding(mask, KeyAgent.keyCode(key));
	}
	
	public void removeKeyBinding(int mask, char key) {
		removeKeyBinding(mask, KeyAgent.keyCode(key));
	}
	
	public void removeKeyBindings() {
		profile.removeKeyboardBindings();
	}
	
	// click
	
	public void setClickBinding(int id, int count, String methodName) {
		profile.setClickBinding(new ClickShortcut(id, count), methodName);
	}
	
	public void setClickBinding(Object object, int id, int count, String methodName) {
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
	
	public void removeClickBindings(int [] ids) {
		profile.removeClickBindings(ids);
	}
	
	/*
	 
	// mouse
	
	protected void removeMouseClickBindings() {
		profile.removeClickBindings(new int[]{MouseAgent.LEFT_ID,MouseAgent.CENTER_ID,MouseAgent.RIGHT_ID});
	}
	
	protected void removeMouseMotionBindings() {
		profile.removeMotionBindings(new int[]{MouseAgent.LEFT_ID,MouseAgent.CENTER_ID,MouseAgent.RIGHT_ID,MouseAgent.WHEEL_ID,MouseAgent.NO_BUTTON});
	}
	
	public void removeMouseBindings() {
		removeMouseMotionBindings();
		removeMouseClickBindings();
	}
	
	public void setDefaultMouseBindings() {
		removeMouseBindings();
		
		setMotionBinding(MouseAgent.LEFT_ID, "rotate");
		//setMotionBinding(MouseAgent.CENTER_ID, "screenRotate");//
		setMotionBinding(MouseAgent.CENTER_ID, "zoomOnRegion");
		setMotionBinding(MouseAgent.RIGHT_ID, "translate");
		setMotionBinding(MouseAgent.WHEEL_ID, scene().is3D() ? isEyeFrame() ? "translateZ" : "scale" : "scale");
		
		//removeClickBindings();
    	setClickBinding(MouseAgent.LEFT_ID, 2, "align");
		setClickBinding(MouseAgent.RIGHT_ID, 2, "center");
	}	
	*/
	
	//
	
	public Profile profile() {
		return profile;
	}
	
	public void setBindings(GenericP5Frame otherFrame) {
		profile.from(otherFrame.profile());
	}
	
	public String info() {
		String result = new String();
		String info = profile().keyboardBindingsInfo();
		if(!info.isEmpty()) {
			result = "Key bindings:\n";
			result += Scene.parseKeyInfo(info);
		}
		info = profile().motionBindingsInfo();
		if(!info.isEmpty()) {
			result += "Motion bindings:\n";
			result += Scene.parseInfo(info);
		}
		info = profile().clickBindingsInfo();
		if(!info.isEmpty()) {
			result += "Click bindings:\n";
			result += Scene.parseInfo(info);
		}
		return result;
	}
	
	//
	String vkeyAction;
	
	// private A a;//TODO study make me an attribute to com between init and end
	protected boolean			need4Spin;
	protected boolean			need4Tossing;
	protected boolean			drive;
	protected boolean			rotateHint;
	protected MotionEvent	currMotionEvent;
	public MotionEvent		initMotionEvent;
	public DOF2Event			zor;
	protected float				flySpeedCache;
	
	protected MotionEvent initMotionEvent() {
		return initMotionEvent;
	}

	protected MotionEvent currentMotionEvent() {
		return currMotionEvent;
	}
	
	// lets see
	
	public void addActionHandler(String methodName) {
		profile().addActionHandler(methodName);
	}
	
	public void addActionHandler(Object object, String methodName) {
		profile().addActionHandler(object, methodName);
	}
	
	public boolean hasActionHandler(String methodName) {
		return profile().hasActionHandler(methodName);
	}
	
	public void removeActionHandler(String methodName) {
		profile().removeActionHandler(methodName);
	}
	
	public boolean initKeyboard(KeyboardEvent event) {
		if(event.id() == 0)//TYPE event
			return vkeyAction == null ? false : true;
		else {
			vkeyAction = profile.actionName(event.shortcut());
		    return false;
		}
	}
	
	public void flushKeyboard(KeyboardEvent event) {
		if( event.flushed() && vkeyAction != null )
			vkeyAction = null;
	}

	/**
	 * Internal use.
	 * 
	 * @see #processAction(BogusEvent)
	 */
	public boolean initMotion(MotionEvent e) {
		return initDOF2(MotionEvent.dof2Event(e));
	}
	
	public boolean initDOF3(DOF3Event e) {
		return initDOF2(MotionEvent.dof2Event(e));
	}
	
	public boolean initDOF6(DOF6Event e) {
		return initDOF2(MotionEvent.dof2Event(e));
	}
	
	public boolean initDOF2(DOF2Event event) {
		if(event == null)
			return false;
		initMotionEvent = event.get();
		currMotionEvent = event;
		stopSpinning();
		String twotempi = profile.actionName(event.shortcut());
		if (twotempi == "screenTranslate")
			dirIsFixed = false;
		boolean rotateMode = ((twotempi == "rotate") || (twotempi == "rotateXYZ")
				|| (twotempi == "rotateCAD")
				|| (twotempi == "screenRotate") || (twotempi == "translateRotateXYZ"));
		if (rotateMode && gScene.is3D())
			gScene.camera().cadRotationIsReversed = gScene.camera().frame()
					.transformOf(gScene.camera().frame().sceneUpVector()).y() < 0.0f;
		need4Spin = (rotateMode && (damping() == 0));
		drive = (twotempi == "drive");
		if (drive)
			flySpeedCache = flySpeed();
		need4Tossing = (twotempi == "moveForward") || (twotempi == "moveBackward")
				|| (drive);
		if (need4Tossing)
			updateSceneUpVector();
		rotateHint = twotempi == "screenRotate";
		if (rotateHint)
			gScene.setRotateVisualHint(true);
		if (isEyeFrame() && twotempi == "zoomOnRegion") {
			gScene.setZoomVisualHint(true);
			zor = event.get();
			return true;
		}
		return false;
	}
	
	/**
	 * Internal use.
	 * 
	 * @see #processAction(BogusEvent)
	 */
	public boolean execMotion(MotionEvent e) {
		return execDOF2(MotionEvent.dof2Event(e));
	}
	
	public boolean execDOF3(DOF3Event e) {
		return execDOF2(MotionEvent.dof2Event(e));
	}
	
	public boolean execDOF6(DOF6Event e) {
		return execDOF2(MotionEvent.dof2Event(e));
	}
	
	public boolean execDOF2(DOF2Event event) {
		if(event == null)
			return false;
		currMotionEvent = event;
		if (zor != null) {
			zor = event.get();
			zor.setPreviousEvent(initMotionEvent.get());
			return true;// bypass
		}
		// never handle ZOOM_ON_REGION on a drag. Could happen if user presses a modifier during drag triggering it
		if (profile.actionName(event.shortcut()) == "zoomOnRegion") {
			return true;
		}
		if (drive) {
			setFlySpeed(0.01f * gScene.radius() * 0.01f * (event.y() - event.y()));
			return false;
		}
		return false;
	}

	/**
	 * Internal use.
	 * 
	 * @see #processAction(BogusEvent)
	 */
	public void flushMotion(MotionEvent e) {
		flushMotion(MotionEvent.dof2Event(e));
	}
	
	public void flushDOF3(DOF3Event e) {
		flushMotion(MotionEvent.dof2Event(e));
	}
	
	public void flushDOF6(DOF6Event e) {
		flushMotion(MotionEvent.dof2Event(e));
	}
	
	public void flushDOF2(DOF2Event event) {
		if(event == null)
			return;
		if (rotateHint) {
			gScene.setRotateVisualHint(false);
			rotateHint = false;
		}
		if (currentMotionEvent() != null) {
			if (need4Spin) {
				startSpinning(spinningRotation(), currentMotionEvent().speed(), currentMotionEvent().delay());
			}
		}
		if (zor != null) {
			// the problem is that depending on the order the button and the modifiers are released,
			// different actions maybe triggered, so we go for sure ;) :
			gScene.setZoomVisualHint(false);
			zoomOnRegion(zor);// now action need to be executed on event
			zor = null;
		}
		if (need4Tossing) {
			// restore speed after drive action terminates:
			if (drive)
				setFlySpeed(flySpeedCache);
			stopFlying();
		}
	}
}
