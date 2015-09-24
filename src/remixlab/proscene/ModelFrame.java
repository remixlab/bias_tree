
package remixlab.proscene;

import java.lang.reflect.Method;

import processing.core.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;

public class ModelFrame extends GrabberFrame implements Model {
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

		ModelFrame other = (ModelFrame) obj;
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

	/**
	 * Constructs a model-frame and adds to the {@link remixlab.proscene.Scene#models()} collection.
	 * Calls {@code super(scn}.
	 * 
	 * @see remixlab.proscene.Scene#addModel(Model)
	 * @see #shape()
	 * @see #setShape(PShape)
	 * @see #addGraphicsHandler(Object, String)
	 */
	public ModelFrame(Scene scn) {
		super(scn);
		((Scene) gScene).addModel(this);
		id = ++Scene.modelCount;
		shift = new PVector();
	}

	/**
	 * Constructs an grabber-frame as a child of reference frame, and adds it to the
	 * {@link remixlab.proscene.Scene#models()} collection. Calls {@code super(scn, referenceFrame}.
	 *
	 * @see remixlab.proscene.Scene#addModel(Model)
	 * @see #shape()
	 * @see #setShape(PShape)
	 * @see #addGraphicsHandler(Object, String)
	 */
	public ModelFrame(Scene scn, Frame referenceFrame) {
		super(scn, referenceFrame);
		((Scene) gScene).addModel(this);
		id = ++Scene.modelCount;
		shift = new PVector();
	}
	
	/**
	 * Wraps the pshape into this model-frame which is then added to the
	 * {@link remixlab.proscene.Scene#models()} collection. Calls {@code super(scn}.
	 * 
	 * @see remixlab.proscene.Scene#addModel(Model)
	 */
	public ModelFrame(Scene scn, PShape ps) {
		super(scn);
		((Scene) gScene).addModel(this);
		id = ++Scene.modelCount;
		shift = new PVector();
		setShape(ps);
	}
	
	/**
	 * Wraps the pshape into this model-frame which is created as a child of reference frame and then added
	 * to the {@link remixlab.proscene.Scene#models()} collection. Calls {@code super(scn, referenceFrame)}.
	 * 
	 * @see remixlab.proscene.Scene#addModel(Model)
	 */
	public ModelFrame(Scene scn, Frame referenceFrame, PShape ps) {
		super(scn, referenceFrame);
		((Scene) gScene).addModel(this);
		id = ++Scene.modelCount;
		shift = new PVector();
		setShape(ps);
	}
	
	/**
	 * Wraps the function object procedure into this model-frame which is then added it to the
	 * {@link remixlab.proscene.Scene#models()} collection. Calls {@code super(scn}.
	 * 
	 * @see remixlab.proscene.Scene#addModel(Model)
	 * @see #addGraphicsHandler(Object, String)
	 */
	public ModelFrame(Scene scn, Object obj, String methodName) {
		super(scn);
		((Scene) gScene).addModel(this);
		id = ++Scene.modelCount;
		shift = new PVector();
		addGraphicsHandler(obj, methodName);
	}
	
	/**
	 * Wraps the the function object procedure into this model-frame which is is created as a child of reference frame
	 * and then added to the {@link remixlab.proscene.Scene#models()} collection. Calls {@code super(scn, referenceFrame}.
	 * 
	 * @see remixlab.proscene.Scene#addModel(Model)
	 * @see #addGraphicsHandler(Object, String)
	 */
	public ModelFrame(Scene scn, Frame referenceFrame, Object obj, String methodName) {
		super(scn, referenceFrame);
		((Scene) gScene).addModel(this);
		id = ++Scene.modelCount;
		shift = new PVector();
		addGraphicsHandler(obj, methodName);
	}
	
	protected ModelFrame(ModelFrame otherFrame) {
		super(otherFrame);
		this.pshape = otherFrame.pshape;
		this.id = otherFrame.id;
		this.shift = otherFrame.shift.copy();
		this.drawHandlerObject = otherFrame.drawHandlerObject;
		this.drawHandlerMethod = otherFrame.drawHandlerMethod;
		this.drawHandlerMethodName = otherFrame.drawHandlerMethodName;
	}

	@Override
	public ModelFrame get() {
		return new ModelFrame(this);
	}
	
	/**
	 * Same as {@code ((Scene) scene).applyTransformation(pg, this)}.
	 * 
	 * @see remixlab.proscene.Scene#applyTransformation(PGraphics, Frame)
	 */
	public void applyTransformation(PGraphics pg) {
		((Scene) gScene).applyTransformation(pg, this);
	}

	/**
	 * Same as {@code ((Scene) scene).applyWorldTransformation(pg, this)}.
	 * 
	 * @see remixlab.proscene.Scene#applyWorldTransformation(PGraphics, Frame)
	 */
	public void applyWorldTransformation(PGraphics pg) {
		((Scene) gScene).applyWorldTransformation(pg, this);
	}

	/**
	 * Internal use. Model color to use in the {@link remixlab.proscene.Scene#pickingBuffer()}.
	 */
	protected int getColor() {
		// see here: http://stackoverflow.com/questions/2262100/rgb-int-to-rgb-python
		return ((Scene) gScene).pickingBuffer().color(id & 255, (id >> 8) & 255, (id >> 16) & 255);
	}

	/**
	 * Returns the shape wrap by this interactive-frame.
	 */
	public PShape shape() {
		return pshape;
	}

	/**
	 * Replaces previous {@link #shape()} with {@code ps}.
	 */
	public void setShape(PShape ps) {
		pshape = ps;
	}
	
	/**
	 * Unsets the shape which is wrapped by this interactive-frame.
	 */
	public PShape unsetShape() {
		PShape prev = pshape;
		pshape = null;
		return prev;
	}
	
	/**
	 * Shifts the {@link #shape()} respect to the frame {@link #position()}. Default value is zero.
	 * 
	 * @see #modelShift()
	 */
	public void shiftModel(PVector shift) {
		this.shift = shift;
	}
	
	/**
	 * Returns the {@link #shape()} shift.
	 * 
	 * @see #shiftModel(PVector)
	 */
	public PVector modelShift() {
		return shift;
	}

	@Override
	public boolean checkIfGrabsInput(float x, float y) {
		if ((shape() == null  && !this.hasGraphicsHandler() ) || !((Scene) gScene).isPickingBufferEnabled() )
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
	 * Attempt to add a graphics handler method to the InteractiveFrame. The default event handler is a method that
	 * returns void and has one single PGraphics parameter. Note that the method should only deal with geometry and
	 * that not coloring procedure may be specified within it.
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
	 * Unregisters the graphics handler method (if any has previously been added to the Scene).
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
	 * Returns {@code true} if the user has registered a graphics handler method to the Scene and {@code false} otherwise.
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