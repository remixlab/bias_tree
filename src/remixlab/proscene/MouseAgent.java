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
}