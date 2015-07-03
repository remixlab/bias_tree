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
import remixlab.bias.core.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.util.*;

/**
 * An InteractiveModelFrame is an InteractiveFrame implementing the model interface: It provides default 2D/3D
 * high-level precise picking &amp; interaction to pshapes.
 */
public class InteractiveModelFrame extends InteractiveFrame implements Model {
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

	public InteractiveModelFrame(Scene scn) {
		super(scn);
		((Scene) scene).addModel(this);
		id = ++Scene.modelCount;
	}

	public InteractiveModelFrame(Scene scn, Frame referenceFrame) {
		super(scn, referenceFrame);
		((Scene) scene).addModel(this);
		id = ++Scene.modelCount;
	}

	/*
	 * protected InteractiveModelFrame(Scene scn, InteractiveFrame iFrame) { super(scn, iFrame); ((Scene)
	 * scene).addModel(this); id = ++Scene.modelCount; }
	 */

	public InteractiveModelFrame(Scene scn, PShape ps) {
		super(scn);
		((Scene) scene).addModel(this);
		id = ++Scene.modelCount;
		pshape = ps;
	}

	public InteractiveModelFrame(Scene scn, PShape ps, Frame referenceFrame) {
		super(scn, referenceFrame);
		((Scene) scene).addModel(this);
		id = ++Scene.modelCount;
		pshape = ps;
	}

	// TODO: is needed?
	/*
	 * protected InteractiveModelFrame(Scene scn, PShape ps, InteractiveFrame iFrame) { super(scn, iFrame); ((Scene)
	 * scene).addModel(this); id = ++Scene.modelCount; pshape = ps; }
	 */

	// TODO fix when implementation is complete
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

	//TODO decide whether to leave set shape or burn it at construction time (which seems more reasonable if texture is to be included)
	public void setShape(PShape ps) {
		pshape = ps;
	}

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

	public void draw() {
		if (shape() == null)
			return;
		PGraphics pg = ((Scene) scene).pg();
		draw(pg);
	}

	// TODO doc: remember to mention bind(false);
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

	public void applyTransformation(PGraphics pg) {
		((Scene) scene).applyTransformation(pg, this);
	}

	public void applyWorldTransformation(PGraphics pg) {
		((Scene) scene).applyWorldTransformation(pg, this);
	}

	protected int getColor() {
		// see here: http://stackoverflow.com/questions/2262100/rgb-int-to-rgb-python
		return ((Scene) scene).pickingBuffer().color(id & 255, (id >> 8) & 255, (id >> 16) & 255);
	}

	public boolean grabsInput(Agent agent) {
		return agent.inputGrabber() == this;
	}
}
