/**************************************************************************************
 * ProScene (version 3.0.0)
 * Copyright (c) 2010-2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 * 
 * All rights reserved. Library that eases the creation of interactive scenes
 * in Processing, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.proscene;

import remixlab.bias.core.*;
import remixlab.bias.event.*;

/**
 * Proscene mouse-agent. A {@link remixlab.dandelion.branch.WheeledMouseAgent} specialization
 * which handles Processing mouse-events. 
 *
 * @see remixlab.dandelion.branch.WheeledMouseAgent
 * @see remixlab.proscene.KeyAgent
 * @see remixlab.proscene.DroidKeyAgent
 * @see remixlab.proscene.DroidTouchAgent
 */
public class MouseAgent extends Agent {
	protected float		xSens		= 1f;
	protected float		ySens		= 1f;
	
	//TODO simplify event reduction (no MOD) according to P5 mouse simplicity behavior
	protected Scene scene;
	//public static int LEFT_ID	= PApplet.LEFT, CENTER_ID = PApplet.CENTER, RIGHT_ID = PApplet.RIGHT, WHEEL_ID = MouseEvent.WHEEL;
	
	protected DOF2Event	currentEvent, prevEvent;
	protected boolean		move, press, drag, release;
	
	protected PickingMode pMode;
	
	protected int [] motionIDs = {LEFT_ID,CENTER_ID,RIGHT_ID,WHEEL_ID,NO_BUTTON};
	protected int [] clickIDs = {LEFT_ID,CENTER_ID,RIGHT_ID};

	public enum PickingMode {
		MOVE, CLICK
	};

	/**
	 * Calls super on (scn,n) and sets {@link #dragToArcball()} bindings.
	 * 
	 * @see #dragToArcball()
	 */	    
	public MouseAgent(Scene scn, String n) {
		super(scn.inputHandler(), n);
		scene = scn;
		setPickingMode(PickingMode.MOVE);
	}
	
	@Override
	public boolean resetDefaultGrabber() {
		addGrabber(scene.eye().frame());
		return setDefaultGrabber(scene.eye().frame());
	}
	
	/**
	 * Returns the scene this object belongs to.
	 */
	public Scene scene() {
		return scene;
	}
	
	/**
	 * Sets the agent {@link #pickingMode()}. Either {@link PickingMode#MOVE} or {@link PickingMode#CLICK}.
	 * 
	 * @see #pickingMode()
	 */
	public void setPickingMode(PickingMode mode) {
		pMode = mode;
	}

	/**
	 * Returns the agent {@link #pickingMode()}. Either {@link PickingMode#MOVE} or {@link PickingMode#CLICK}.
	 * 
	 * @see #setPickingMode(PickingMode)
	 */
	public PickingMode pickingMode() {
		return pMode;
	}
	
	/**
	 * Processing mouseEvent method to be registered at the PApplet's instance.
	 */
	public void mouseEvent(processing.event.MouseEvent e) {		
		move = e.getAction() == processing.event.MouseEvent.MOVE;
		press = e.getAction() == processing.event.MouseEvent.PRESS;
		drag = e.getAction() == processing.event.MouseEvent.DRAG;
		release = e.getAction() == processing.event.MouseEvent.RELEASE;
		if (move || press || drag || release) {
			currentEvent = new DOF2Event(prevEvent, e.getX() - scene.originCorner().x(), e.getY() - scene.originCorner().y(),
					/*e.getModifiers()*/BogusEvent.NO_MODIFIER_MASK, move ? BogusEvent.NO_ID : e.getButton());			
			if (move && (pickingMode() == PickingMode.MOVE))
				updateTrackedGrabber(currentEvent);
			handle(release ? currentEvent.flush() : currentEvent);			
			prevEvent = currentEvent.get();
			return;
		}
		if (e.getAction() == processing.event.MouseEvent.WHEEL) {// e.getAction() = MouseEvent.WHEEL = 8
			handle(new DOF1Event(e.getCount(), /*e.getModifiers()*/BogusEvent.NO_MODIFIER_MASK, WHEEL_ID));
			return;
		}
		if (e.getAction() == processing.event.MouseEvent.CLICK) {
			ClickEvent bogusClickEvent = new ClickEvent(e.getX() - scene.originCorner().x(), e.getY()
					- scene.originCorner().y(),
					/*e.getModifiers()*/BogusEvent.NO_MODIFIER_MASK, e.getButton(), e.getCount());
			if (pickingMode() == PickingMode.CLICK)
				updateTrackedGrabber(bogusClickEvent);
			handle(bogusClickEvent);
			return;
		}
	}
	
	@Override
	public float[] sensitivities(MotionEvent event) {
		if (event instanceof DOF2Event)
			return new float[] { xSens, ySens, 1f, 1f, 1f, 1f };
		else
			return super.sensitivities(event);
	}

	/**
	 * Defines the {@link #xSensitivity()}.
	 */
	public void setXSensitivity(float sensitivity) {
		xSens = sensitivity;
	}

	/**
	 * Returns the x sensitivity.
	 * <p>
	 * Default value is 1. A higher value will make the event more efficient (usually meaning a faster motion). Use a
	 * negative value to invert the along x-Axis motion direction.
	 * 
	 * @see #setWheelSensitivity(float)
	 */
	public float xSensitivity() {
		return xSens;
	}

	/**
	 * Defines the {@link #ySensitivity()}.
	 */
	public void setYSensitivity(float sensitivity) {
		ySens = sensitivity;
	}

	/**
	 * Returns the y sensitivity.
	 * <p>
	 * Default value is 1. A higher value will make the event more efficient (usually meaning a faster motion). Use a
	 * negative value to invert the along y-Axis motion direction.
	 * 
	 * @see #setWheelSensitivity(float)
	 */
	public float ySensitivity() {
		return ySens;
	}
	
	// 1. Frame bindings
	
	// 1.a. Default
	
	public void setDefaultBindings(GenericP5Frame frame) {
		if( ! this.hasGrabber(frame) ) {
			System.out.println("Nothing done frame should be added to the mouse agent first. Call agent.addGrabber(frame) first");
			return;
		}
		
		removeBindings(frame);
		
		setMotionBinding(frame, LEFT_ID, "rotate");
		//setMotionBinding(CENTER_ID, "screenRotate");//
		setMotionBinding(frame, CENTER_ID, "zoomOnRegion");
		setMotionBinding(frame, RIGHT_ID, "translate");
		setMotionBinding(frame, WHEEL_ID, scene().is3D() ? frame.isEyeFrame() ? "translateZ" : "scale" : "scale");
		
		removeClickBindings(frame);
		/*//TODO pending
		setClickBinding(frame, LEFT_ID, 2, "align");
		setClickBinding(frame, RIGHT_ID, 2, "center");
		*/
		setClickBinding(frame, LEFT_ID, "align");
		setClickBinding(frame, RIGHT_ID, "center");
	}
	
	public void removeBindings(GenericP5Frame frame) {
		if( ! this.hasGrabber(frame) ) {
			System.out.println("Nothing done frame should be added to the mouse agent first. Call agent.addGrabber(frame) first");
			return;
		}
		removeMotionBindings(frame);
		removeClickBindings(frame);		
	}
	
	// 1.b. Motion
	
	// 1.b.1 2DOF
	
	public void removeMotionBindings(GenericP5Frame frame) {
		if( ! this.hasGrabber(frame) ) {
			System.out.println("Nothing done frame should be added to the mouse agent first. Call agent.addGrabber(frame) first");
			return;
		}
		frame.profile.removeMotionBindings(motionIDs);
	}
	
	public void setMotionBinding(GenericP5Frame frame, int id, String methodName) {
		frame.setMotionBinding(id, methodName);
	}
		
	public void setMotionBinding(Object object, GenericP5Frame frame, int id, String methodName) {
		frame.setMotionBinding(object, id, methodName);
	}
	
	// 1.b.1 Wheel
	
	// 1.c. (single) Click
	
	public void removeClickBindings(GenericP5Frame frame) {
		if( ! this.hasGrabber(frame) ) {
			System.out.println("Nothing done frame should be added to the mouse agent first. Call agent.addGrabber(frame) first");
			return;
		}
		frame.profile.removeClickBindings(clickIDs);
	}
	
	public void setClickBinding(GenericP5Frame frame, int id, String methodName) {
		frame.profile.setClickBinding(new ClickShortcut(id, 1), methodName);
	}
	
	public void setClickBinding(Object object, GenericP5Frame frame, int id, String methodName) {
		frame.profile.setClickBinding(object, new ClickShortcut(id, 1), methodName);
	}
	
	public boolean hasClickBinding(GenericP5Frame frame, int id) {
		return frame.profile.hasBinding(new ClickShortcut(id, 1));
	}
	
	public void removeClickBinding(GenericP5Frame frame, int id) {
		frame.profile.removeBinding(new ClickShortcut(id, 1));
	}
	
	/*
	public void setFirstPersonBindings(GenericP5Frame frame) {
		removeBindings(scene.eyeFrame());
		frame.setMotionBinding(NO_BUTTON, "lookAround");
		frame.setMotionBinding(LEFT_ID, "moveForward");		
		frame.setMotionBinding(RIGHT_ID, "moveBackward");
		frame.setClickBinding(LEFT_ID, 2, "align");
		frame.setClickBinding(RIGHT_ID, 2, "center");
	}
	*/

	/*
	// global bindings

	public void removeBindings() {
		for( Grabber grabber : this.grabbers() ) 
			if(grabber instanceof GenericP5Frame)
				removeBindings((GenericP5Frame)grabber);
	}
	
	public void setDefaultBindings() {
		for( Grabber grabber : this.grabbers() ) 
			if(grabber instanceof GenericP5Frame)
				setDefaultBindings((GenericP5Frame)grabber);
	}
	*/
}