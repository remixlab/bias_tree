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

import java.util.Arrays;

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
	protected Scene scene;	
	protected DOF2Event	currentEvent, prevEvent;
	protected boolean		move, press, drag, release;	
	protected PickingMode pMode;	
	protected int [] motionIDs = {LEFT_ID,CENTER_ID,RIGHT_ID,WHEEL_ID,NO_BUTTON};
	protected int [] dof2IDs = {LEFT_ID,CENTER_ID,RIGHT_ID,NO_BUTTON};
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
	//see here: http://stackoverflow.com/questions/880581/how-to-convert-int-to-integer-in-java
	protected boolean validateDOF2IDs(int id) {
		if(Arrays.asList(Arrays.stream(dof2IDs).boxed().toArray( Integer[]::new )).contains(id))
			return true;
		else {
			System.out.println("Warning: no mouse dof2 binding set. Use a valid dof2 id: LEFT_ID,CENTER_ID,RIGHT_ID,NO_BUTTON");
			return false;
		}
	}
	
	protected boolean validateClickIDs(int id) {
		if(Arrays.asList(Arrays.stream(clickIDs).boxed().toArray( Integer[]::new )).contains(id))
			return true;
		else {
			System.out.println("Warning: no mouse click binding set. Use a valid click id: LEFT_ID,CENTER_ID,RIGHT_ID");
			return false;
		}
	}
	
	// 1. Default
	
	public void setDefaultBindings(GenericP5Frame frame) {	
		removeBindings(frame);
		
		setDOF2Binding(frame, LEFT_ID, "rotate");
		setDOF2Binding(frame, CENTER_ID, "zoomOnRegion");
		setDOF2Binding(frame, RIGHT_ID, "translate");
		setDOF1Binding(frame, scene().is3D() ? frame.isEyeFrame() ? "translateZ" : "scale" : "scale");
		
		removeClickBindings(frame);
		setClickBinding(frame, LEFT_ID, 2, "align");
		setClickBinding(frame, RIGHT_ID, 2, "center");
	}
	
	public void removeBindings(GenericP5Frame frame) {
		//if(validateFrame(frame)) {
			removeDOF2Bindings(frame);
			removeDOF1Binding(frame);
			removeClickBindings(frame);
		//}
	}
	
	/*
	protected boolean validateFrame(GenericP5Frame frame) {
		if(hasGrabber(frame))
			return true;
		else {
			System.out.println("Warning: nothing done. Add frame first: scene.mouseAgent().addGrabber(frame)");
			return false;
		}
	}
	*/
	
	// 2. 2DOF
	
	public void setDOF2Binding(GenericP5Frame frame, int id, String methodName) {
		if(validateDOF2IDs(id) /*&& validateFrame(frame)*/)
			frame.profile.setDOF2Binding(new MotionShortcut(id), methodName);
	}
		
	public void setDOF2Binding(Object object, GenericP5Frame frame, int id, String methodName) {
		if(validateDOF2IDs(id) /*&& validateFrame(frame)*/)
			frame.profile.setDOF2Binding(object, new MotionShortcut(id), methodName);
	}
	
	public boolean hasDOF2Binding(GenericP5Frame frame, int id) {
		if(validateDOF2IDs(id) /*&& validateFrame(frame)*/)
			return frame.profile.hasBinding(new MotionShortcut(id));
		return false;
	}
	
	public void removeDOF2Binding(GenericP5Frame frame, int id) {
		if(validateDOF2IDs(id) /*&& validateFrame(frame)*/)
			frame.profile.removeBinding(new MotionShortcut(id));
	}
	
	public void removeDOF2Bindings(GenericP5Frame frame) {
		//if(validateFrame(frame))
			frame.profile.removeMotionBindings(dof2IDs);
	}
	
	// 3. Wheel
	
	public void setDOF1Binding(GenericP5Frame frame, String methodName) {
		//if(validateFrame(frame))
			frame.profile.setDOF1Binding(new MotionShortcut(WHEEL_ID), methodName);
	}
		
	public void setDOF1Binding(Object object, GenericP5Frame frame, String methodName) {
		//if(validateFrame(frame))
			frame.profile.setDOF1Binding(object, new MotionShortcut(WHEEL_ID), methodName);
	}
	
	public boolean hasDOF1Binding(GenericP5Frame frame) {
		//if(validateFrame(frame))
			return frame.profile.hasBinding(new MotionShortcut(WHEEL_ID));
		//return false;
	}
	
	public void removeDOF1Binding(GenericP5Frame frame) {
		//if(validateFrame(frame))
			frame.profile.removeBinding(new MotionShortcut(WHEEL_ID));
	}
	
	// 4. Click
	
	public void setClickBinding(GenericP5Frame frame, int id, String methodName) {
		setClickBinding(frame, id, 1, methodName);
	}
	
	public void setClickBinding(GenericP5Frame frame, int id, int count, String methodName) {
		if(validateClickIDs(id) /*&& validateFrame(frame)*/)
			frame.profile.setClickBinding(new ClickShortcut(id, count), methodName);
	}
	
	public void setClickBinding(Object object, GenericP5Frame frame, int id, String methodName) {
		setClickBinding(object, frame, id, 1, methodName);
	}
	
	public void setClickBinding(Object object, GenericP5Frame frame, int id, int count, String methodName) {
		if(validateClickIDs(id) /*&& validateFrame(frame)*/)
			frame.profile.setClickBinding(object, new ClickShortcut(id, count), methodName);
	}
	
	public boolean hasClickBinding(GenericP5Frame frame, int id) {
		return hasClickBinding(frame, id, 1);
	}
	
	public boolean hasClickBinding(GenericP5Frame frame, int id, int count) {
		if(validateClickIDs(id) /*&& validateFrame(frame)*/)
			return frame.profile.hasBinding(new ClickShortcut(id, count));
		return false;
	}
	
	public void removeClickBinding(GenericP5Frame frame, int id) {
		removeClickBinding(frame, id, 1);
	}
	
	public void removeClickBinding(GenericP5Frame frame, int id, int count) {
		if(validateClickIDs(id) /*&& validateFrame(frame)*/)
			frame.profile.removeBinding(new ClickShortcut(id, count));
	}
	
	public void removeClickBindings(GenericP5Frame frame) {
		//if(validateFrame(frame))
			frame.profile.removeClickBindings(clickIDs);
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