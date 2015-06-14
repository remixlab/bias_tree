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

import processing.core.PApplet;
import remixlab.dandelion.agent.*;
import remixlab.bias.core.BogusEvent;
import remixlab.bias.event.*;

/**
 * Proscene {@link remixlab.dandelion.agent.WheeledMouseAgent}.
 */
public class MouseAgent extends WheeledMouseAgent {
	protected DOF2Event	currentEvent, prevEvent;
	protected boolean		move, press, drag, release;

	public MouseAgent(Scene scn, String n) {
		super(scn, n);
		LEFT_ID = PApplet.LEFT;
		CENTER_ID = PApplet.CENTER;
		RIGHT_ID = PApplet.RIGHT;
		dragToArcball();
		// registration requires a call to PApplet.registerMethod("mouseEvent", motionAgent());
		// which is done in Scene.enableMotionAgent(), which also register the agent at the inputHandler
		//inputHandler().unregisterAgent(this);
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
					e.getModifiers(), move ? BogusEvent.NO_ID : e.getButton());
			if (move && (pickingMode() == PickingMode.MOVE))
				updateTrackedGrabber(currentEvent);
			if (move || press || drag)
				handle(currentEvent);
			if (release)
				flush(currentEvent);
			prevEvent = currentEvent.get();
			return;
		}
		if (e.getAction() == processing.event.MouseEvent.WHEEL) {// e.getAction() = MouseEvent.WHEEL = 8
			handle(new DOF1Event(e.getCount(), e.getModifiers(), WHEEL_ID));
			return;
		}
		if (e.getAction() == processing.event.MouseEvent.CLICK) {
			ClickEvent bogusClickEvent = new ClickEvent(e.getX() - scene.originCorner().x(), e.getY()
					- scene.originCorner().y(),
					e.getModifiers(), e.getButton(), e.getCount());
			if (pickingMode() == PickingMode.CLICK)
				updateTrackedGrabber(bogusClickEvent);
			handle(bogusClickEvent);
			return;
		}
	}
}