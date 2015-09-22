
package remixlab.proscene;

import java.lang.reflect.Method;

import processing.core.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;

public class GrabberModelFrame extends GrabberFrame implements Model {
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

		GrabberModelFrame other = (GrabberModelFrame) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(id, other.id)
				.isEquals();
	}

	protected PShape	pshape;
	protected int			id;
	protected PVector shift;
	
	// Draw	
	protected Object						drawHandlerObject;
	protected Method						drawHandlerMethod;
	protected String						drawHandlerMethodName;

	public GrabberModelFrame(Scene scn) {
		super(scn);
		((Scene) gScene).addModel(this);
		id = ++Scene.modelCount;
		shift = new PVector();
	}

	public GrabberModelFrame(Scene scn, Frame referenceFrame) {
		super(scn, referenceFrame);
		((Scene) gScene).addModel(this);
		id = ++Scene.modelCount;
		shift = new PVector();
	}
	
	protected GrabberModelFrame(GrabberModelFrame otherFrame) {
		super(otherFrame);
		this.pshape = otherFrame.pshape;
		this.id = otherFrame.id;
		this.shift = otherFrame.shift.copy();
		this.drawHandlerObject = otherFrame.drawHandlerObject;
		this.drawHandlerMethod = otherFrame.drawHandlerMethod;
		this.drawHandlerMethodName = otherFrame.drawHandlerMethodName;
	}

	@Override
	public GrabberModelFrame get() {
		return new GrabberModelFrame(this);
	}
	
	public void applyTransformation(PGraphics pg) {
		((Scene) gScene).applyTransformation(pg, this);
	}

	public void applyWorldTransformation(PGraphics pg) {
		((Scene) gScene).applyWorldTransformation(pg, this);
	}

	protected int getColor() {
		// see here: http://stackoverflow.com/questions/2262100/rgb-int-to-rgb-python
		return ((Scene) gScene).pickingBuffer().color(id & 255, (id >> 8) & 255, (id >> 16) & 255);
	}

	@Override
	public PShape shape() {
		return pshape;
	}

	public void setShape(PShape ps) {
		pshape = ps;
	}
	
	public PShape unsetShape() {
		PShape prev = pshape;
		pshape = null;
		return prev;
	}
	
	/**
	 * Shifts the {@link #shape()} respect to the frame {@link #position()}. Default value is zero.
	 * 
	 * @see #shift()
	 */
	public void shiftShape(PVector shift) {
		this.shift = shift;
	}
	
	/**
	 * Returns the {@link #shape()} shift.
	 * 
	 * @see #shiftShape(PVector)
	 */
	public PVector shift() {
		return shift;
	}

	@Override
	public boolean checkIfGrabsInput(float x, float y) {
		if (shape() == null  && !this.hasGraphicsHandler())
			return super.checkIfGrabsInput(x, y);
		((Scene) gScene).pickingBuffer().pushStyle();
		((Scene) gScene).pickingBuffer().colorMode(PApplet.RGB, 255);
		int index = (int) y * gScene.width() + (int) x;
		if ((0 <= index) && (index < ((Scene) gScene).pickingBuffer().pixels.length))
			return ((Scene) gScene).pickingBuffer().pixels[index] == getColor();
		((Scene) gScene).pickingBuffer().popStyle();
		return false;
	}

	public void draw() {
		if (shape() == null)
			return;
		PGraphics pg = ((Scene) gScene).pg();
		draw(pg);
	}

	/*
	// TODO doc: remember to mention bind(false);
	@Override
	public void draw(PGraphics pg) {
		if (shape() == null)
			return;
		pg.pushStyle();
		if (pg == ((Scene) gScene).pickingBuffer()) {
			shape().disableStyle();
			pg.colorMode(PApplet.RGB, 255);
			pg.fill(getColor());
			pg.stroke(getColor());
		}
		pg.pushMatrix();
		((Scene) gScene).applyWorldTransformation(pg, this);
		pg.shape(shape());
		pg.popMatrix();
		if (pg == ((Scene) gScene).pickingBuffer())
			shape().enableStyle();
		pg.popStyle();
	}
	*/
	
	@Override
	public void draw(PGraphics pg) {
		if (shape() == null && !this.hasGraphicsHandler())
			return;
		pg.pushStyle();
		if (pg == ((Scene) gScene).pickingBuffer()) {
			if(shape()!=null) {
				shape().disableStyle();
			}
			pg.colorMode(PApplet.RGB, 255);
			pg.fill(getColor());
			pg.stroke(getColor());
		}
		pg.pushMatrix();
		((Scene) gScene).applyWorldTransformation(pg, this);
		pg.translate(shift.x, shift.y, shift.z);
		if(shape()!=null)
			pg.shape(shape());
		if( this.hasGraphicsHandler() )
			this.invokeGraphicsHandler(pg);
		pg.popMatrix();
		if (pg == ((Scene) gScene).pickingBuffer()) {
			if(shape()!=null) {
				shape().enableStyle();
			}
		}
		pg.popStyle();
	}
	
	// DRAW METHOD REG
	
	protected boolean invokeGraphicsHandler(PGraphics pg) {
		// 3. Draw external registered method
		if (drawHandlerObject != null) {
			try {
				drawHandlerMethod.invoke(drawHandlerObject, new Object[] { pg });
				return true;
			} catch (Exception e) {
				PApplet.println("Something went wrong when invoking your " + drawHandlerMethodName + " method");
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Attempt to add a 'draw' handler method to the InteractiveFrame. The default event handler is a method that returns void and
	 * has one single PGraphics parameter.
	 * 
	 * @param obj
	 *          the object to handle the event
	 * @param methodName
	 *          the method to execute in the object handler class
	 * 
	 * @see #removeGraphicsHandler()
	 * @see #invokeGraphicsHandler(PGraphics)
	 */
	public void addGraphicsHandler(Object obj, String methodName) {
		try {
			drawHandlerMethod = obj.getClass().getMethod(methodName, new Class<?>[] { PGraphics.class });
			//drawHandlerMethod = obj.getClass().getMethod(methodName, new Class<?>[] { });
			drawHandlerObject = obj;
			drawHandlerMethodName = methodName;
		} catch (Exception e) {
			PApplet.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}

	/**
	 * Unregisters the 'draw' handler method (if any has previously been added to the Scene).
	 * 
	 * @see #addGraphicsHandler(Object, String)
	 * @see #invokeGraphicsHandler(PGraphics)
	 */
	public void removeGraphicsHandler() {
		drawHandlerMethod = null;
		drawHandlerObject = null;
		drawHandlerMethodName = null;
	}

	/**
	 * Returns {@code true} if the user has registered a 'draw' handler method to the Scene and {@code false} otherwise.
	 * 
	 * @see #addGraphicsHandler(Object, String)
	 * @see #invokeGraphicsHandler(PGraphics)
	 */
	public boolean hasGraphicsHandler() {
		if (drawHandlerMethodName == null)
			return false;
		return true;
	}
}