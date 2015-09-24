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

import java.lang.reflect.Method;

import processing.core.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.core.GrabberScene;

/**
 * {@link remixlab.proscene.Model} object which eases third-party implementation of the
 * {@link remixlab.proscene.Model} interface.
 * <p>
 * Based on the concrete event type, this model object splits the {@link #performInteraction(BogusEvent)} method
 * into more specific versions of it, e.g., {@link #performInteraction(DOF6Event)},
 * {@link #performInteraction(KeyboardEvent)} and so on. Thus allowing implementations of this abstract
 * ModelObject to override only those method signatures that might be of their interest.
 * 
 * @see remixlab.bias.core.Grabber#performInteraction(BogusEvent)
 */
public abstract class ModelObject implements Model {
	protected Scene		scene;
	protected int			id;
	protected PShape	pshape;
	
	// Draw	
	protected Object						drawHandlerObject;
	protected Method						drawHandlerMethod;
	protected String						drawHandlerMethodName;
	
	// TODO new experimenting with textures
	protected PImage    tex;
	
	public ModelObject(Scene scn, PShape ps, PImage texture) {
		scene = scn;
		setShape(ps);
		tex = texture;
		scene.addModel(this);
		id = ++Scene.modelCount;
	}
	
	/**
	 * Constructs a ModelObject with a null {@link #shape()} and adds it to the
	 * {@link remixlab.proscene.Scene#models()} collection.
	 * 
	 * @see remixlab.proscene.Scene#addModel(Model)
	 * @see #shape()
	 * @see #setShape(PShape)
	 */
	public ModelObject(Scene scn) {
		scene = scn;
		scene.addModel(this);
		id = ++Scene.modelCount;
	}

	/**
	 * Wraps the pshape into this model-object which is then added to the {@link remixlab.proscene.Scene#models()} collection. 
	 * 
	 * @see remixlab.proscene.Scene#addModel(Model)
	 * @see #addGraphicsHandler(Object, String)
	 */
	public ModelObject(Scene scn, PShape ps) {
		scene = scn;
		scene.addModel(this);
		id = ++Scene.modelCount;
		setShape(ps);
	}

	/**
	 * Wraps the pshape into this model-object which is then added to the {@link remixlab.proscene.Scene#models()} collection. 
	 * 
	 * @see remixlab.proscene.Scene#addModel(Model)
	 * @see #setShape(PShape)
	 */
	public ModelObject(Scene scn, Object obj, String methodName) {
		scene = scn;
		scene.addModel(this);
		id = ++Scene.modelCount;
		addGraphicsHandler(obj, methodName);
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
	 * Same as {@code draw(scene.pg())}.
	 * 
	 * @see remixlab.proscene.Scene#drawModels(PGraphics)
	 */
	public void draw() {
		if (shape() == null)
			return;
		PGraphics pg = scene.pg();
		draw(pg);
	}

	/*
	@Override
	public void draw(PGraphics pg) {
		if (shape() == null)
			return;
		pg.pushStyle();
		if (pg == scene.pickingBuffer()) {
			shape().disableStyle();
			if(tex!=null) shape().noTexture();
			pg.colorMode(PApplet.RGB, 255);
			pg.fill(getColor());
			pg.stroke(getColor());
		}
		pg.pushMatrix();
		pg.shape(shape());
		pg.popMatrix();
		if (pg == scene.pickingBuffer()) {
			if(tex!=null) shape().texture(tex);
			shape().enableStyle();
		}
		pg.popStyle();
	}
	*/
	
	//TODO experimental
	@Override
	public void draw(PGraphics pg) {
		if (shape() == null && !this.hasGraphicsHandler())
			return;
		pg.pushStyle();
		if (pg == scene.pickingBuffer()) {
			if(shape()!=null) {
				shape().disableStyle();
				if(tex!=null) shape().noTexture();
			}
			pg.colorMode(PApplet.RGB, 255);
			pg.fill(getColor());
			pg.stroke(getColor());
		}
		pg.pushMatrix();
		if(shape()!=null)
			pg.shape(shape());
		if( this.hasGraphicsHandler() )
			this.invokeGraphicsHandler(pg);
		pg.popMatrix();
		if (pg == scene.pickingBuffer()) {
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
	
	@Override
	public boolean checkIfGrabsInput(BogusEvent event) {
		if (event instanceof KeyboardEvent)
			return checkIfGrabsInput((KeyboardEvent) event);
		if (event instanceof ClickEvent)
			return checkIfGrabsInput((ClickEvent) event);
		if (event instanceof MotionEvent)
			return checkIfGrabsInput((MotionEvent) event);
		return false;
	}
	
	/**
	 * Calls checkIfGrabsInput() on the proper motion event: {@link remixlab.bias.event.DOF1Event},
	 * {@link remixlab.bias.event.DOF2Event}, {@link remixlab.bias.event.DOF3Event} or {@link remixlab.bias.event.DOF6Event}.
	 */
	public boolean checkIfGrabsInput(MotionEvent event) {
		if (event instanceof DOF1Event)
			return checkIfGrabsInput((DOF1Event) event);
		if (event instanceof DOF2Event)
			return checkIfGrabsInput((DOF2Event) event);
		if (event instanceof DOF3Event)
			return checkIfGrabsInput((DOF3Event) event);
		if (event instanceof DOF6Event)
			return checkIfGrabsInput((DOF6Event) event);
		return false;
	}

	/**
	 * Same as {@code return checkIfGrabsInput(event.x(), event.y())}.
	 * 
	 * @see #checkIfGrabsInput(float, float)
	 */
	protected boolean checkIfGrabsInput(ClickEvent event) {
		return checkIfGrabsInput(event.x(), event.y());
	}

	/**
	 * Selection with a picking buffer requires a MotionEvernt with at least two degrees-of-freedom.
	 */
	protected boolean checkIfGrabsInput(KeyboardEvent event) {
		GrabberScene.showMissingImplementationWarning("checkIfGrabsInput(KeyboardEvent event)", this.getClass().getName());
		return false;
	}

	/**
	 * Selection with a picking buffer requires at least two degrees-of-freedom.
	 * 
	 * @see #checkIfGrabsInput(float, float)
	 */
	protected boolean checkIfGrabsInput(DOF1Event event) {
		GrabberScene.showMissingImplementationWarning("checkIfGrabsInput(DOF1Event event)", this.getClass().getName());
		return false;
	}

	/**
	 * Same as return {@code checkIfGrabsInput(event.x(), event.y())}.
	 * 
	 * @see #checkIfGrabsInput(float, float)
	 */
	protected boolean checkIfGrabsInput(DOF2Event event) {
		if (event.isAbsolute()) {
			System.out.println("Grabbing a modelObject is only possible from a relative MotionEvent or from a ClickEvent");
			return false;
		}
		return checkIfGrabsInput(event.x(), event.y());
	}
	
	/**
	 * Override this method when you want the object to be picked from a {@link remixlab.bias.event.DOF3Event}. 
	 */
	protected boolean checkIfGrabsInput(DOF3Event event) {
		return false;
	}

	/**
	 * Override this method when you want the object to be picked from a {@link remixlab.bias.event.DOF6Event}. 
	 */
	protected boolean checkIfGrabsInput(DOF6Event event) {
		return false;
	}

	/**
	 * A model object is selected using <a href="http://schabby.de/picking-opengl-ray-tracing/">'ray-picking'</a>
     * with a color buffer (see {@link remixlab.proscene.Scene#pickingBuffer()}). This method compares the color of 
     * the {@link remixlab.proscene.Scene#pickingBuffer()} at {@code (x,y)} with {@link #getColor()}.
     * Returns true if both colors are the same, and false otherwise.
	 */
	public final boolean checkIfGrabsInput(float x, float y) {
		if (shape() == null && !this.hasGraphicsHandler())
			return false;
		scene.pickingBuffer().pushStyle();
		scene.pickingBuffer().colorMode(PApplet.RGB, 255);
		int index = (int) y * scene.width() + (int) x;
		if ((0 <= index) && (index < scene.pickingBuffer().pixels.length))
			return scene.pickingBuffer().pixels[index] == getColor();
		scene.pickingBuffer().popStyle();
		return false;
	}

	/**
	 * Check if this object is the {@link remixlab.bias.core.Agent#inputGrabber()}. Returns {@code true} if this object
	 * grabs the agent and {@code false} otherwise.
	 */
	public boolean grabsInput(Agent agent) {
		return agent.inputGrabber() == this;
	}
	
	/**
	 * Checks if the frame grabs input from any agent registered at the scene input handler.
	 */
	public boolean grabsInput() {
		for(Agent agent : scene.inputHandler().agents()) {
			if(agent.inputGrabber() == this)
				return true;
		}
		return false;
	}
	
	@Override
	public void performInteraction(BogusEvent event) {
		if (event instanceof KeyboardEvent)
			performInteraction((KeyboardEvent) event);
		if (event instanceof ClickEvent)
			performInteraction((ClickEvent) event);
		if (event instanceof MotionEvent)
			performInteraction((MotionEvent) event);
	}
	
	/**
	 * Calls performInteraction() on the proper motion event: {@link remixlab.bias.event.DOF1Event},
	 * {@link remixlab.bias.event.DOF2Event}, {@link remixlab.bias.event.DOF3Event} or {@link remixlab.bias.event.DOF6Event}.
	 * <p>
	 * Override this method when you want the object to perform an interaction from a
	 * {@link remixlab.bias.event.MotionEvent}. 
	 */
	protected void performInteraction(MotionEvent event) {
		if (event instanceof DOF1Event)
			performInteraction((DOF1Event) event);
		if (event instanceof DOF2Event)
			performInteraction((DOF2Event) event);
		if (event instanceof DOF3Event)
			performInteraction((DOF3Event) event);
		if (event instanceof DOF6Event)
			performInteraction((DOF6Event) event);
	}
	
	/**
	 * Override this method when you want the object to perform an interaction from a
	 * {@link remixlab.bias.event.KeyboardEvent}. 
	 */
	protected void performInteraction(KeyboardEvent event) {
		GrabberScene
				.showMissingImplementationWarning("performInteraction(KeyboardEvent event)", this.getClass().getName());
	}

	/**
	 * Override this method when you want the object to perform an interaction from a
	 * {@link remixlab.bias.event.ClickEvent}. 
	 */
	protected void performInteraction(ClickEvent event) {
		GrabberScene.showMissingImplementationWarning("performInteraction(ClickEvent event)", this.getClass().getName());
	}

	/**
	 * Override this method when you want the object to perform an interaction from a
	 * {@link remixlab.bias.event.DOF1Event}. 
	 */
	protected void performInteraction(DOF1Event event) {
		GrabberScene.showMissingImplementationWarning("performInteraction(DOF1Event event)", this.getClass().getName());
	}

	/**
	 * Override this method when you want the object to perform an interaction from a
	 * {@link remixlab.bias.event.DOF2Event}. 
	 */
	protected void performInteraction(DOF2Event event) {
		GrabberScene.showMissingImplementationWarning("performInteraction(DOF2Event event)", this.getClass().getName());
	}

	/**
	 * Override this method when you want the object to perform an interaction from a
	 * {@link remixlab.bias.event.DOF3Event}. 
	 */
	protected void performInteraction(DOF3Event event) {
		GrabberScene.showMissingImplementationWarning("performInteraction(DOF3Event event)", this.getClass().getName());
	}

	/**
	 * Override this method when you want the object to perform an interaction from a
	 * {@link remixlab.bias.event.DOF6Event}. 
	 */
	protected void performInteraction(DOF6Event event) {
		GrabberScene.showMissingImplementationWarning("performInteraction(DOF6Event event)", this.getClass().getName());
	}

	/**
	 * Internal use. Model color to use in the {@link remixlab.proscene.Scene#pickingBuffer()}.
	 */
	protected int getColor() {
		// see here: http://stackoverflow.com/questions/2262100/rgb-int-to-rgb-python
		return scene.pickingBuffer().color(id & 255, (id >> 8) & 255, (id >> 16) & 255);
	}
}
