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

import processing.core.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.core.*;
import remixlab.util.*;

/**
 * A model is an InteractiveFrame implementing the model interface: It provides default 2D/3D high-level precise picking
 * & interaction to pshapes.
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
	protected int			id;

	public InteractiveModelFrame(Scene scn) {
		super(scn);
		id = ((Scene) scene).models().size();
		((Scene) scene).addModel(this);
	}

	public InteractiveModelFrame(Scene scn, Frame referenceFrame) {
		super(scn, referenceFrame);
		id = ((Scene) scene).models().size();
		((Scene) scene).addModel(this);
	}

	protected InteractiveModelFrame(Scene scn, InteractiveEyeFrame iFrame) {
		super(scn, iFrame);
		id = ((Scene) scene).models().size();
		((Scene) scene).addModel(this);
	}

	public InteractiveModelFrame(Scene scn, PShape ps) {
		super(scn);
		id = ((Scene) scene).models().size();
		((Scene) scene).addModel(this);
		pshape = ps;
	}

	public InteractiveModelFrame(Scene scn, PShape ps, Frame referenceFrame) {
		super(scn, referenceFrame);
		id = ((Scene) scene).models().size();
		((Scene) scene).addModel(this);
		pshape = ps;
	}

	// TODO: is needed?
	protected InteractiveModelFrame(Scene scn, PShape ps, InteractiveEyeFrame iFrame) {
		super(scn, iFrame);
		id = ((Scene) scene).models().size();
		((Scene) scene).addModel(this);
	}

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

		((Scene) scene).pickingBuffer().pushStyle();
		((Scene) scene).pickingBuffer().colorMode(PApplet.RGB, 255);
		int index = (int) event2.y() * scene.width() + (int) event2.x();
		if ((0 <= index) && (index < ((Scene) scene).pickingBuffer().pixels.length)) {
			int pick = ((Scene) scene).pickingBuffer().pixels[index];
			return getID(pick) == id;
		}
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
			pg.colorMode(PApplet.RGB, 255);
			pg.fill(getColor(id));
			pg.stroke(getColor(id));
		}
		pg.pushMatrix();
		((Scene) scene).applyWorldTransformation(pg, this);
		pg.shape(shape());
		pg.popMatrix();
		if (pg == ((Scene) scene).pickingBuffer())
			shape().enableStyle();
		pg.popStyle();
	}

	public void applyTransformation(PGraphics pg) {
		((Scene) scene).applyTransformation(pg, this);
	}

	public void applyWorldTransformation(PGraphics pg) {
		((Scene) scene).applyWorldTransformation(pg, this);
	}

	// TODO: improve next two methods

	protected int getColor(int id) {
		return ((Scene) scene).pApplet().color(10 + id, 20 + id, 30 + id);
	}

	protected int getID(int c) {
		int r = (int) ((Scene) scene).pApplet().red(c);
		return r - 10;
	}
}
