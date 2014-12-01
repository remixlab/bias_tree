
package remixlab.proscene;

import processing.core.PApplet;
import processing.event.MouseEvent;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.agent.*;
import remixlab.dandelion.core.*;

public class TrackpadAgent extends WheeledTrackpadAgent {
	protected boolean	needHandle;
	protected DOF2Event spEvent;
	public TrackpadAgent(Scene scn, String n) {
		super(scn, n);
		left = PApplet.LEFT;
		center = PApplet.CENTER;
		right = PApplet.RIGHT;
		setAsArcball();
		// registration requires a call to PApplet.registerMethod("mouseEvent", motionAgent());
		// which is done in Scene.enableMotionAgent(), which also register the agent at the inputHandler
		inputHandler().unregisterAgent(this);
	}

	/**
	 * Hack to deal with this: https://github.com/processing/processing/issues/1693 is to override all the following so
	 * that:
	 * <p>
	 * <ol>
	 * <li>Whenever B_CENTER appears B_ALT should be present.</li>
	 * <li>Whenever B_RIGHT appears B_META should be present.</li>
	 * </ol>
	 */
	@Override
	public int buttonModifiersFix(int m, int button) {
		int mask = m;
		// ALT
		if (button == center)
			mask = (BogusEvent.ALT | m);
		// META
		else if (button == right)
			mask = (BogusEvent.META | m);
		return mask;
	}

	/**
	 * Processing mouseEvent method to be registered at the PApplet's instance.
	 */
	public void mouseEvent(MouseEvent e) {
		if (e.getAction() == MouseEvent.MOVE || e.getAction() == MouseEvent.DRAG || e.getAction() == MouseEvent.RELEASE) {
			DOF2Event dof2Event = new DOF2Event(lastEvent, e.getX() - scene.originCorner().x(), e.getY()
					- scene.originCorner().y(),
					e.getModifiers(), e.getButton());			
			lastEvent = dof2Event;

			///*
			if (inputGrabber() instanceof InteractiveFrame) {
				// InteractiveFrame iFrame = (InteractiveFrame) inputGrabber();
				Action<?> a = (inputGrabber() instanceof InteractiveEyeFrame) ? eyeProfile().handle((BogusEvent) lastEvent)
						: frameProfile().handle((BogusEvent) lastEvent);
				if (a == null)
					return;
				
				if (a == DOF2Action.SCREEN_ROTATE)
					scene.setRotateVisualHint(true);
				else
					scene.setRotateVisualHint(false);
				
				if (a == DOF2Action.ZOOM_ON_REGION) {
					scene.setZoomVisualHint(true);
					spEvent = new DOF2Event(pressEvent, e.getX() - scene.originCorner().x(), e.getY()
							- scene.originCorner().y(), e.getModifiers(), e.getButton());					
					needHandle = true;										
					return;
				}
				else {
					scene.setZoomVisualHint(false);
					pressEvent = lastEvent.get();
					if(needHandle) {				
						enqueueEventTuple(new EventGrabberTuple(spEvent, DOF2Action.ZOOM_ON_REGION, inputGrabber()));
						needHandle = false;
						return;
					}
				}
			}
			//*/
			handle(dof2Event);	
		}
		if (e.getAction() == processing.event.MouseEvent.WHEEL) {
			handle(new DOF1Event(e.getCount(), e.getModifiers(), MotionEvent.NOBUTTON));
		}
		if (e.getAction() == processing.event.MouseEvent.CLICK) {
			updateTrackedGrabber(new ClickEvent(e.getX() - scene.originCorner().x(), e.getY() - scene.originCorner().y(),
					e.getModifiers(), e.getButton(), e.getCount()));
		}
	}
}