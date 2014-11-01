/**************************************************************************************
 * ProScene (version 2.1.0)
 * Copyright (c) 2010-2014 National University of Colombia, https://github.com/remixlab
 * @author Sebastian Chaparro, url-PENDING
 * @author William Rodriguez, url-PENDING
 * @author Jean Pierre Charalambos, http://otrolado.info/
 * 
 * All rights reserved. Library that eases the creation of interactive scenes
 * in Processing, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.proscene;

import java.lang.reflect.Method;

import processing.core.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.core.*;
import remixlab.util.*;

/**
 * A model is an InteractiveFrame implementing the modelable interface: It provides default 2D/3D high-level picking &
 * interaction to modalables (PShape or graphics procedures defined upon a PGraphics).
 */
public class Model extends InteractiveFrame implements Modelable {
	// TODO complete hashCode and equals, once the rest is done
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				appendSuper(super.hashCode()).
				append(id).
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

		Model other = (Model) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(id, other.id)
				.isEquals();
	}

	// 1. Draw
	protected Object		drawPGraphicsObject;
	// The method in drawHandlerObject to execute
	protected Method		drawPGraphicsMethod;
	// the name of the method to handle the event
	protected String		drawPGraphicsMethodName;

	// TODO implement me
	// 2. Animation
	// The object to handle the animation
	protected Object		animateHandlerObject;
	// The method in animateHandlerObject to execute
	protected Method		animateHandlerMethod;
	// the name of the method to handle the animation
	protected String		animateHandlerMethodName;

	private static int	idCount;

	PShape							pshape;
	int									id;
	boolean							drawA;
	float								axesL	= 1;

	public Model(Scene scn) {
		super(scn);
		id = idCount++;
	}

	public Model(Scene scn, Frame referenceFrame) {
		super(scn, referenceFrame);
		id = idCount++;
	}

	protected Model(Scene scn, InteractiveEyeFrame iFrame) {
		super(scn, iFrame);
		id = idCount++;
	}

	public Model(Scene scn, PShape ps) {
		super(scn);
		pshape = ps;
		id = idCount++;
	}

	public Model(Scene scn, PShape ps, Frame referenceFrame) {
		super(scn, referenceFrame);
		pshape = ps;
		id = idCount++;
	}

	protected Model(Scene scn, PShape ps, InteractiveEyeFrame iFrame) {
		super(scn, iFrame);
		pshape = ps;
		id = idCount++;
	}

	// TODO fix when implementation is complete
	protected Model(Model otherFrame) {
		super(otherFrame);
		this.pshape = otherFrame.pshape;
		this.id = otherFrame.id;
	}

	@Override
	public Model get() {
		return new Model(this);
	}

	public PShape shape() {
		return pshape;
	}

	public void setShape(PShape ps) {
		pshape = ps;
	}

	@Override
	public boolean checkIfGrabsInput(BogusEvent event) {
		DOF2Event event2 = null;

		if (((event instanceof KeyboardEvent)) || (event instanceof DOF1Event))
			throw new RuntimeException("Grabbing an interactive frame is not possible with a "
					+ ((event instanceof KeyboardEvent) ? "Keyboard" : "DOF1") + "Event");

		if (event instanceof DOF2Event)
			event2 = ((DOF2Event) event).get();
		else if (event instanceof DOF3Event)
			event2 = ((DOF3Event) event).dof2Event();
		else if (event instanceof DOF6Event)
			event2 = ((DOF6Event) event).dof3Event().dof2Event();
		else if (event instanceof ClickEvent)
			event2 = new DOF2Event(((ClickEvent) event).x(), ((ClickEvent) event).y());

		int pick = ((Scene) scene).pickingBuffer().get((int) event2.x(), (int) event2.y());
		return getID(pick) == id;
	}

	@Override
	public void drawShape() {
		if (pshape == null)
			return;
		PGraphics pg = ((Scene) scene).pg();
		pg.pushStyle();
		pg.pushMatrix();
		applyWorldTransformation();
		if (drawA)
			((Scene) scene).drawAxes(axesLength());
		pg.shape(pshape);
		pg.popMatrix();
		pg.popStyle();
	}

	@Override
	public void drawShape(PGraphics pg) {
		if (pshape == null)
			return;
		pg.pushStyle();
		pshape.setFill(getColor(id));
		pshape.setStroke(getColor(id));
		pg.pushMatrix();
		Scene.applyWorldTransformation(pg, this);
		pg.shape(pshape);
		pg.popMatrix();
		pg.popStyle();
	}

	/**
	 * Invokes an external drawing method (if registered). Called by
	 * {@link remixlab.dandelion.core.AbstractScene#postDraw()}.
	 * <p>
	 * Requires reflection.
	 */
	@Override
	public boolean invokeGraphicsHandler() {
		// 3. Draw external registered method
		if (drawPGraphicsObject != null) {
			try {
				PGraphics pg = ((Scene) scene).pg();
				pg.pushStyle();
				pg.pushMatrix();
				applyWorldTransformation();
				if (drawA)
					((Scene) scene).drawAxes(axesLength());
				drawPGraphicsMethod.invoke(drawPGraphicsObject, new Object[] { pg });
				pg.popMatrix();
				pg.popStyle();
				return true;
			} catch (Exception e) {
				PApplet.println("Something went wrong when invoking your " + drawPGraphicsMethodName + " method");
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean invokeGraphicsHandler(PGraphics pickingBuffer) {
		// 3. Draw external registered method
		if (drawPGraphicsObject != null) {
			try {
				// PGraphics pickingBuffer = ((Scene) scene).pickingBuffer();
				pickingBuffer.pushStyle();
				pickingBuffer.fill(getColor(id));
				pickingBuffer.stroke(getColor(id));
				pickingBuffer.pushMatrix();
				Scene.applyWorldTransformation(pickingBuffer, this);
				drawPGraphicsMethod.invoke(drawPGraphicsObject, new Object[] { pickingBuffer });
				pickingBuffer.popMatrix();
				pickingBuffer.popStyle();
				return true;
			} catch (Exception e) {
				PApplet.println("Something went wrong when invoking your " + drawPGraphicsMethodName
						+ " method into picking buffer");
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	/**
	 * Attempt to add a 'draw' handler method to the Scene. The default event handler is a method that returns void and
	 * has one single Scene parameter.
	 * 
	 * @param obj
	 *          the object to handle the event
	 * @param methodName
	 *          the method to execute in the object handler class
	 * 
	 * @see #removeGraphicsHandler()
	 * @see #invokeGraphicsHandler()
	 */
	@Override
	public void addGraphicsHandler(Object obj, String methodName) {
		try {
			drawPGraphicsMethod = obj.getClass().getMethod(methodName, new Class<?>[] { PGraphics.class });
			drawPGraphicsObject = obj;
			drawPGraphicsMethodName = methodName;
		} catch (Exception e) {
			PApplet.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}

	/**
	 * Unregisters the 'draw' handler method (if any has previously been added to the Scene).
	 * 
	 * @see #addGraphicsHandler(Object, String)
	 * @see #invokeGraphicsHandler()
	 */
	@Override
	public void removeGraphicsHandler() {
		drawPGraphicsMethod = null;
		drawPGraphicsObject = null;
		drawPGraphicsMethodName = null;
	}

	/**
	 * Returns {@code true} if the user has registered a 'draw' handler method to the Scene and {@code false} otherwise.
	 * 
	 * @see #addGraphicsHandler(Object, String)
	 * @see #invokeGraphicsHandler()
	 */
	@Override
	public boolean hasGraphicsHandler() {
		if (drawPGraphicsMethodName == null)
			return false;
		return true;
	}

	// TODO no estoy completamente seguro de los ejes...
	public void toggleDrawAxes() {
		drawA = !drawA;
	}

	public void setAxesLength(float l) {
		axesL = l;
	}

	public float axesLength() {
		return axesL;
	}

	// TODO: improve next two methods

	private int getColor(int id) {
		return ((Scene) scene).pApplet().color(10 + id, 20 + id, 30 + id);
	}

	private int getID(int c) {
		int r = (int) ((Scene) scene).pApplet().red(c);
		return r - 10;
	}
}
