/**************************************************************************************
 * ProScene (version 3.0.0)
 * Copyright (c) 2010-2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 * 
 * All rights reserved. Library that eases the creation of interactive scenes
 * in Processing, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

// Thanks to Sebastian Chaparro, url-PENDING and William Rodriguez, url-PENDING
// for providing an initial picking example and searching the documentation for it:
// http://n.clavaud.free.fr/processing/picking/pickcode.htm
// http://content.gpwiki.org/index.php/OpenGL_Selection_Using_Unique_Color_IDs

package remixlab.proscene;

import java.lang.reflect.Method;

import processing.core.*;
import remixlab.bias.core.Agent;
import remixlab.dandelion.branch.*;
import remixlab.dandelion.geom.*;
import remixlab.util.*;

/**
 * A {@link remixlab.proscene.Model} {@link remixlab.dandelion.branch.GenericFrame}.
 * 
 * @see remixlab.proscene.Model
 * @see remixlab.dandelion.branch.GenericFrame
 */
public class InteractiveFrame extends GenericFrame implements Model, Constants {
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

		InteractiveFrame other = (InteractiveFrame) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(id, other.id)
				.isEquals();
	}
	
	// shape
	protected PShape	pshape;
	protected int		id;
	protected PVector shift;
	
	// Draw	
	protected Object						drawHandlerObject;
	protected Method						drawHandlerMethod;
	protected String						drawHandlerMethodName;
		
	// TODO new experimenting with textures	
	protected PImage    tex;	
		
	public InteractiveFrame(Scene scn, PShape ps, PImage texture) {
		super(scn);
		((Scene) gScene).addModel(this);
		id = ++Scene.modelCount;
		pshape = ps;
		tex = texture;
		shift = new PVector();
	}
	
	//--
	
	/**
	 * Constructs a interactive-frame and adds to the {@link remixlab.proscene.Scene#models()} collection.
	 * Calls {@code super(scn}.
	 * 
	 * @see remixlab.dandelion.branch.GenericFrame#GenericFrame(GenericScene)
	 * @see remixlab.proscene.Scene#addModel(Model)
	 * @see #shape()
	 * @see #setShape(PShape)
	 * @see #addGraphicsHandler(Object, String)
	 */
	public InteractiveFrame(Scene scn) {
		super(scn);
		((Scene) gScene).addModel(this);
		id = ++Scene.modelCount;
		shift = new PVector();
	}
	
	/**
	 * Constructs an interactive-frame as a child of reference frame, and adds it to the
	 * {@link remixlab.proscene.Scene#models()} collection. Calls {@code super(scn, referenceFrame}.
	 * 
	 * @see remixlab.dandelion.branch.GenericFrame#GenericFrame(GenericScene, Frame)
	 * @see remixlab.proscene.Scene#addModel(Model)
	 * @see #shape()
	 * @see #setShape(PShape)
	 * @see #addGraphicsHandler(Object, String)
	 */
	public InteractiveFrame(Scene scn, Frame referenceFrame) {
		super(scn, referenceFrame);
		((Scene) gScene).addModel(this);
		id = ++Scene.modelCount;
		shift = new PVector();
	}
	
	/**
	 * 
	 * Wraps the pshape into this interactive-frame which is then added to the
	 * {@link remixlab.proscene.Scene#models()} collection. Calls {@code super(scn)}.
	 * 
	 * @see remixlab.dandelion.branch.GenericFrame#GenericFrame(GenericScene)
	 * @see remixlab.proscene.Scene#addModel(Model)
	 */
	public InteractiveFrame(Scene scn, PShape ps) {
		super(scn);
		((Scene) gScene).addModel(this);
		id = ++Scene.modelCount;
		shift = new PVector();
		setShape(ps);
	}

	/**
	 * Wraps the pshape into this interactive-frame which is created as a child of reference frame and then added to the
	 * {@link remixlab.proscene.Scene#models()} collection. Calls {@code super(scn, referenceFrame)}.
	 * 
	 * @see remixlab.dandelion.branch.GenericFrame#GenericFrame(GenericScene, Frame)
	 * @see remixlab.proscene.Scene#addModel(Model)
	 */
	public InteractiveFrame(Scene scn, Frame referenceFrame, PShape ps) {
		super(scn, referenceFrame);
		((Scene) gScene).addModel(this);
		id = ++Scene.modelCount;
		shift = new PVector();
		setShape(ps);
	}

	/**
	 * Wraps the function object procedure into this interactive-frame which is then added it to the
	 * {@link remixlab.proscene.Scene#models()} collection. Calls {@code super(scn}.
	 * 
	 * @see remixlab.dandelion.branch.GenericFrame#GenericFrame(GenericScene)
	 * @see remixlab.proscene.Scene#addModel(Model)
	 * @see #addGraphicsHandler(Object, String)
	 */
	public InteractiveFrame(Scene scn, Object obj, String methodName) {
		super(scn);
		((Scene) gScene).addModel(this);
		id = ++Scene.modelCount;
		shift = new PVector();
		addGraphicsHandler(obj, methodName);
	}

	/**
	 * Wraps the the function object procedure into this interactive-frame which is is created as a child of reference frame
	 * and then added to the {@link remixlab.proscene.Scene#models()} collection. Calls {@code super(scn, referenceFrame}.
	 * 
	 * @see remixlab.dandelion.branch.GenericFrame#GenericFrame(GenericScene, Frame)
	 * @see remixlab.proscene.Scene#addModel(Model)
	 * @see #addGraphicsHandler(Object, String)
	 */
	public InteractiveFrame(Scene scn, Frame referenceFrame, Object obj, String methodName) {
		super(scn, referenceFrame);
		((Scene) gScene).addModel(this);
		id = ++Scene.modelCount;
		shift = new PVector();
		addGraphicsHandler(obj, methodName);
	}

	protected InteractiveFrame(InteractiveFrame otherFrame) {
		super(otherFrame);
		this.pshape = otherFrame.pshape;
		this.id = otherFrame.id;
		this.shift = otherFrame.shift.copy();
		this.drawHandlerObject = otherFrame.drawHandlerObject;
		this.drawHandlerMethod = otherFrame.drawHandlerMethod;
		this.drawHandlerMethodName = otherFrame.drawHandlerMethodName;
	}

	@Override
	public InteractiveFrame get() {
		return new InteractiveFrame(this);
	}
	
	@Override
	public InteractiveFrame detach() {
		InteractiveFrame frame = new InteractiveFrame((Scene)gScene);
		for(Agent agent : gScene.inputHandler().agents())
			agent.removeGrabber(frame);
		frame.fromFrame(this);
		return frame;
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
	
	// shape

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
	 * An interactive-frame is selected using <a href="http://schabby.de/picking-opengl-ray-tracing/">'ray-picking'</a>
     * with a color buffer (see {@link remixlab.proscene.Scene#pickingBuffer()}). This method compares the color of 
     * the {@link remixlab.proscene.Scene#pickingBuffer()} at {@code (x,y)} with {@link #getColor()}.
     * Returns true if both colors are the same, and false otherwise.
	 */
	@Override
	public final boolean checkIfGrabsInput(float x, float y) {
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

	/**
	 * Same as {@code draw(scene.pg())}.
	 * 
	 * @see remixlab.proscene.Scene#drawModels(PGraphics)
	 */
	public void draw() {
		if (shape() == null  && !this.hasGraphicsHandler())
			return;
		PGraphics pg = ((Scene) gScene).pg();
		draw(pg);
	}

	//TODO important once debugged draw should be re-implemented at all Model* classes
	@Override
	public void draw(PGraphics pg) {
		if (shape() == null && !this.hasGraphicsHandler())
			return;
		pg.pushStyle();
		if (pg == ((Scene) gScene).pickingBuffer()) {
			if(shape()!=null) {
				shape().disableStyle();
				if(tex!=null) shape().noTexture();
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
				if(tex!=null)
					shape().texture(tex);
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
