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
		if (processEvent(event))
			return;
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
	
	String initAction;
	
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
	
	/**
	 * Internal use. Algorithm to split a gesture flow into a 'three-tempi' {@link remixlab.bias.branch.Action} sequence.
	 * It's called like this (see {@link #performInteraction(BogusEvent)}):
	 * <pre>
     * {@code
	 * public void performInteraction(BogusEvent event) {
	 *	if (processEvent(event))
	 *		return;
	 *	if (event instanceof KeyboardEvent)
	 *		performInteraction((KeyboardEvent) event);
	 *	if (event instanceof ClickEvent)
	 *		performInteraction((ClickEvent) event);
	 *	if (event instanceof MotionEvent)
	 *		performInteraction((MotionEvent) event);
	 * }
     * }
     * </pre>
	 * <p>
	 * The algorithm parses the bogus-event in {@link #performInteraction(BogusEvent)} and then decide what to call:
	 * <ol>
     * <li>{@link #initAction(BogusEvent)} (1st tempi): sets the initAction, called when initAction == null.</li>
     * <li>{@link #execAction(BogusEvent)} (2nd tempi): continues action execution, called when initAction == action()
     * (current action)</li>
     * <li>{@link #flushAction(BogusEvent)} (3rd): ends action, called when {@link remixlab.bias.core.BogusEvent#flushed()}
     * is true or when initAction != action()</li>
     * </ol>
     * <p>
     * Useful to parse multiple-tempi gestures, such as a mouse press/move/drag/release flow.
     * <p>
     * The following motion-actions have been implemented using the aforementioned technique:
	 * {@link remixlab.dandelion.branch.Constants.DOF2Action#SCREEN_ROTATE},
	 * {@link remixlab.dandelion.branch.Constants.DOF2Action#ZOOM_ON_REGION},
	 * {@link remixlab.dandelion.branch.Constants.DOF2Action#MOVE_BACKWARD}, and
	 * {@link remixlab.dandelion.branch.Constants.DOF2Action#MOVE_FORWARD}.
	 * <p>
     * Current implementation only supports {@link remixlab.bias.event.MotionEvent}s.
	 */
	protected final boolean processEvent(BogusEvent event) {
		if (initAction == null) {
			if (!event.flushed()) {
				return initAction(event);// start action
			}
		}
		else { // initAction != null
			if (!event.flushed()) {
				if (initAction == profile.methodName(event.shortcut()))
					return execAction(event);// continue action
				else { // initAction != action() -> action changes abruptly
					flushAction(event);
					return initAction(event);// start action
				}
			}
			else {// action() == null
				flushAction(event);// stopAction
				initAction = null;
				//setAction(null); // experimental, but sounds logical since: initAction != null && action() == null
				return true;
			}
		}
		return true;// i.e., if initAction == action() == null -> ignore :)
	}

	// init domain

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected boolean initAction(BogusEvent event) {
		initAction = profile.methodName(event.shortcut());
		if (event instanceof KeyboardEvent)
			return initAction((KeyboardEvent) event);
		if (event instanceof ClickEvent)
			return initAction((ClickEvent) event);
		if (event instanceof MotionEvent)
			return initAction((MotionEvent) event);
		return false;
	}

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected boolean initAction(ClickEvent event) {
		return false;
	}
	
	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected boolean initAction(KeyboardEvent event) {
		return false;
	}

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected boolean initAction(MotionEvent event) {
		if (event instanceof DOF1Event)
			return false;
		return initAction(MotionEvent.dof2Event(event));
	}

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	//TODO generalize me (taking a MotionEvent)
	protected boolean initAction(DOF2Event event) {
		initMotionEvent = event.get();
		currMotionEvent = event;
		stopSpinning();
		String twotempi = profile.methodName(event.shortcut());
		if (twotempi == "gestureScreenTranslate")
			dirIsFixed = false;
		boolean rotateMode = ((twotempi == "gestureArcball") || (twotempi == "gestureRotateXYZ")
				|| (twotempi == "gestureRotateCAD")
				|| (twotempi == "gestureScreenRotate") || (twotempi == "gestureTranslateRotateXYZ"));
		if (rotateMode && gScene.is3D())
			gScene.camera().cadRotationIsReversed = gScene.camera().frame()
					.transformOf(gScene.camera().frame().sceneUpVector()).y() < 0.0f;
		need4Spin = (rotateMode && (damping() == 0));
		drive = (twotempi == "gestureDrive");
		if (drive)
			flySpeedCache = flySpeed();
		need4Tossing = (twotempi == "gestureMoveForward") || (twotempi == "gestureMoveBackward")
				|| (drive);
		if (need4Tossing)
			updateSceneUpVector();
		rotateHint = twotempi == "gestureScreenRotate";
		if (rotateHint)
			gScene.setRotateVisualHint(true);
		if (isEyeFrame() && twotempi == "gestureZoomOnRegion") {
			gScene.setZoomVisualHint(true);
			zor = event.get();
			return true;
		}
		return false;
	}

	// exec domain

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected boolean execAction(BogusEvent event) {
		if (event instanceof KeyboardEvent)
			return execAction((KeyboardEvent) event);
		if (event instanceof ClickEvent)
			return execAction((ClickEvent) event);
		if (event instanceof MotionEvent)
			return execAction((MotionEvent) event);
		return false;
	}

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected boolean execAction(MotionEvent event) {
		if (event instanceof DOF1Event)
			return false;
		return execAction(MotionEvent.dof2Event(event));
	}

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	//TODO generalize me (taking a MotionEvent)
	protected boolean execAction(DOF2Event event) {
		currMotionEvent = event;
		if (zor != null) {
			zor = event.get();
			zor.setPreviousEvent(initMotionEvent.get());
			return true;// bypass
		}
		// never handle ZOOM_ON_REGION on a drag. Could happen if user presses a modifier during drag triggering it
		if (profile.methodName(event.shortcut()) == "gestureZoomOnRegion") {
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
	 * @see #processEvent(BogusEvent)
	 */
	protected boolean execAction(ClickEvent event) {
		return false;
	}
	
	protected boolean execAction(KeyboardEvent event) {
		return false;
	}

	// flushDomain

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected void flushAction(BogusEvent event) {
		if (event instanceof KeyboardEvent)
			flushAction((KeyboardEvent) event);
		if (event instanceof ClickEvent)
			flushAction((ClickEvent) event);
		if (event instanceof MotionEvent)
			flushAction((MotionEvent) event);
	}

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected void flushAction(MotionEvent event) {
		if (!(event instanceof DOF1Event))
			flushAction(MotionEvent.dof2Event(event));
	}
	
	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected void flushAction(KeyboardEvent event) {		
	}

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected void flushAction(ClickEvent event) {
	}

	/**
	 * Internal use.
	 * 
	 * @see #processEvent(BogusEvent)
	 */
	protected void flushAction(DOF2Event event) {
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
			gestureZoomOnRegion(zor);// now action need to be executed on event
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
