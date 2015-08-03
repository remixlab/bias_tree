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

import processing.core.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.util.*;

/**
 * A {@link remixlab.proscene.Model} {@link remixlab.dandelion.core.InteractiveFrame}.
 * 
 * @see remixlab.proscene.Model
 * @see remixlab.dandelion.core.InteractiveFrame
 */
public class InteractiveModelFrame extends InteractiveFrame implements Model {
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

		InteractiveModelFrame other = (InteractiveModelFrame) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(id, other.id)
				.isEquals();
	}

	protected PShape	pshape;
	protected int		id;
	
	// TODO new experimenting with textures
	protected PImage    tex;
	
	public InteractiveModelFrame(Scene scn, PShape ps, PImage texture) {
		super(scn);
		((Scene) scene).addModel(this);
		id = ++Scene.modelCount;
		pshape = ps;
		tex = texture;
	}
	
	//--
	
	/**
	 * Constructs a interactive-model-frame with a null {@link #shape()} and adds it to the
	 * {@link remixlab.proscene.Scene#models()} collection. Don't forget to call {@link #setShape(PShape)}.
	 * Calls {@code super(scn}.
	 * 
	 * @see remixlab.dandelion.core.InteractiveFrame#InteractiveFrame(AbstractScene)
	 * @see remixlab.proscene.Scene#addModel(Model)
	 * @see #shape()
	 * @see #setShape(PShape)
	 */
	public InteractiveModelFrame(Scene scn) {
		super(scn);
		((Scene) scene).addModel(this);
		id = ++Scene.modelCount;
	}
	
	/**
	 * Constructs a interactive-model-frame with a null {@link #shape()} and adds it to the
	 * {@link remixlab.proscene.Scene#models()} collection. Don't forget to call {@link #setShape(PShape)}.
	 * Calls {@code super(scn, referenceFrame}.
	 * 
	 * @see remixlab.dandelion.core.InteractiveFrame#InteractiveFrame(AbstractScene, Frame)
	 * @see remixlab.proscene.Scene#addModel(Model)
	 * @see #shape()
	 * @see #setShape(PShape)
	 */
	public InteractiveModelFrame(Scene scn, Frame referenceFrame) {
		super(scn, referenceFrame);
		((Scene) scene).addModel(this);
		id = ++Scene.modelCount;
	}

	/**
	 * Wraps the pshape into this interactive-model-frame and adds it to the
	 * {@link remixlab.proscene.Scene#models()} collection. Calls {@code super(scn}.
	 * 
	 * @see remixlab.dandelion.core.InteractiveFrame#InteractiveFrame(AbstractScene)
	 * @see remixlab.proscene.Scene#addModel(Model)
	 */
	public InteractiveModelFrame(Scene scn, PShape ps) {
		super(scn);
		((Scene) scene).addModel(this);
		id = ++Scene.modelCount;
		pshape = ps;
	}

	/**
	 * Wraps the pshape into this interactive-model-frame and adds it to the
	 * {@link remixlab.proscene.Scene#models()} collection. Calls {@code super(scn, referenceFrame}.
	 * 
	 * @see remixlab.dandelion.core.InteractiveFrame#InteractiveFrame(AbstractScene, Frame)
	 * @see remixlab.proscene.Scene#addModel(Model)
	 */
	public InteractiveModelFrame(Scene scn, PShape ps, Frame referenceFrame) {
		super(scn, referenceFrame);
		((Scene) scene).addModel(this);
		id = ++Scene.modelCount;
		pshape = ps;
	}

	protected InteractiveModelFrame(InteractiveModelFrame otherFrame) {
		super(otherFrame);
		this.pshape = otherFrame.pshape;
		this.id = otherFrame.id;
	}

	@Override
	public InteractiveModelFrame get() {
		return new InteractiveModelFrame(this);
	}

	@Override
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
	 * Same as {@code for (int i = 0; i < shape().getVertexCount(); i++)
	 * shape().setVertex(i,PVector.add(shape().getVertex(i), shift));} which shifts all {@link #shape()} vertices.
	 * <p>
	 * Works only when PShape has been created with the {@code beginShape()}/ {@code endShapa()} command.
	 */
	public void shiftShape(PVector shift) {
		if(shape() != null)
			for (int i = 0; i < shape().getVertexCount(); i++)
				shape().setVertex(i,PVector.add(shape().getVertex(i), shift));
	}
	
	/**
	 * An interactive-model-frame is selected using <a href="http://schabby.de/picking-opengl-ray-tracing/">'ray-picking'</a>
     * with a color buffer (see {@link remixlab.proscene.Scene#pickingBuffer()}). This method compares the color of 
     * the {@link remixlab.proscene.Scene#pickingBuffer()} at {@code (x,y)} with {@link #getColor()}.
     * Returns true if both colors are the same, and false otherwise.
	 */
	@Override
	public final boolean checkIfGrabsInput(float x, float y) {
		((Scene) scene).pickingBuffer().pushStyle();
		((Scene) scene).pickingBuffer().colorMode(PApplet.RGB, 255);
		int index = (int) y * scene.width() + (int) x;
		if ((0 <= index) && (index < ((Scene) scene).pickingBuffer().pixels.length))
			return ((Scene) scene).pickingBuffer().pixels[index] == getColor();
		((Scene) scene).pickingBuffer().popStyle();
		return false;
	}

	/**
	 * Same as {@code draw(scene.pg())}.
	 * 
	 * @see remixlab.proscene.Scene#drawModels(PGraphics)
	 */
	public void draw() {
		if (shape() == null)
			return;
		PGraphics pg = ((Scene) scene).pg();
		draw(pg);
	}

	@Override
	public void draw(PGraphics pg) {
		if (shape() == null)
			return;
		pg.pushStyle();
		if (pg == ((Scene) scene).pickingBuffer()) {
			shape().disableStyle();
			if(tex!=null) shape().noTexture();
			pg.colorMode(PApplet.RGB, 255);
			pg.fill(getColor());
			pg.stroke(getColor());
		}
		pg.pushMatrix();
		((Scene) scene).applyWorldTransformation(pg, this);
		pg.shape(shape());
		pg.popMatrix();
		if (pg == ((Scene) scene).pickingBuffer()) {
			if(tex!=null) shape().texture(tex);
			shape().enableStyle();
		}
		pg.popStyle();
	}

	/**
	 * Same as {@code ((Scene) scene).applyTransformation(pg, this)}.
	 * 
	 * @see remixlab.proscene.Scene#applyTransformation(PGraphics, Frame)
	 */
	public void applyTransformation(PGraphics pg) {
		((Scene) scene).applyTransformation(pg, this);
	}

	/**
	 * Same as {@code ((Scene) scene).applyWorldTransformation(pg, this)}.
	 * 
	 * @see remixlab.proscene.Scene#applyWorldTransformation(PGraphics, Frame)
	 */
	public void applyWorldTransformation(PGraphics pg) {
		((Scene) scene).applyWorldTransformation(pg, this);
	}

	/**
	 * Internal use. Model color to use in the {@link remixlab.proscene.Scene#pickingBuffer()}.
	 */
	protected int getColor() {
		// see here: http://stackoverflow.com/questions/2262100/rgb-int-to-rgb-python
		return ((Scene) scene).pickingBuffer().color(id & 255, (id >> 8) & 255, (id >> 16) & 255);
	}
}
