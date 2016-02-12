/**************************************************************************************
 * ProScene (version 3.0.0)
 * Copyright (c) 2014-2016 National University of Colombia, https://github.com/remixlab
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
import remixlab.dandelion.geom.*;
import remixlab.util.*;

/**
 * A Processing {@link remixlab.dandelion.core.GenericFrame} with a {@link #profile()}
 * instance which allows {@link remixlab.bias.core.Shortcut} to
 * {@link java.lang.reflect.Method} bindings high-level customization. (see all the
 * <b>*Binding*()</b> methods).
 * <p>
 * Visual representations (PShapes or arbitrary graphics procedures) may be related to an
 * interactive-frame in two different ways:
 * <ol>
 * <li>Applying the frame transformation just before the graphics code happens in
 * <b>papplet.draw()</b> (refer to the {@link remixlab.dandelion.core.GenericFrame} API
 * class documentation).
 * <li>Setting a visual representation directly to the frame, either by calling
 * {@link #setShape(PShape)} or {@link #addGraphicsHandler(Object, String)} in
 * <b>papplet.setup()</b>, and then calling {@link remixlab.proscene.Scene#drawFrames()}
 * in <b>papplet.draw()</b>.
 * </ol>
 * Note that in the latter case the interactive-frame will automatically be picked using
 * the {@link remixlab.proscene.Scene#pickingBuffer()}.
 * 
 * @see remixlab.dandelion.core.GenericFrame
 */
public class InteractiveFrame extends GenericP5Frame {
  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(id).toHashCode();
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
    return new EqualsBuilder().appendSuper(super.equals(obj)).append(id, other.id).isEquals();
  }

  // shape
  protected PShape pshape;
  protected int id;
  protected Vec shift;

  // Draw
  protected Object drawHandlerObject;
  protected Method drawHandlerMethod;
  protected boolean highlight = true;

  /**
   * Constructs a interactive-frame and adds to the
   * {@link remixlab.proscene.Scene#frames()} collection. Calls {@code super(scn}.
   * 
   * @see remixlab.dandelion.core.GenericFrame#GenericFrame(AbstractScene)
   * @see remixlab.proscene.Scene#addFrame(InteractiveFrame)
   * @see #shape()
   * @see #setShape(PShape)
   * @see #addGraphicsHandler(Object, String)
   */
  public InteractiveFrame(Scene scn) {
    super(scn);
    ((Scene) gScene).addFrame(this);
    id = ++Scene.frameCount;
    shift = new Vec();
  }

  /**
   * Constructs an interactive-frame as a child of reference frame, and adds it to the
   * {@link remixlab.proscene.Scene#frames()} collection. Calls
   * {@code super(scn, referenceFrame}.
   * 
   * @see remixlab.dandelion.core.GenericFrame#GenericFrame(AbstractScene, Frame)
   * @see remixlab.proscene.Scene#addFrame(InteractiveFrame)
   * @see #shape()
   * @see #setShape(PShape)
   * @see #addGraphicsHandler(Object, String)
   */
  public InteractiveFrame(Scene scn, Frame referenceFrame) {
    super(scn, referenceFrame);
    ((Scene) gScene).addFrame(this);
    id = ++Scene.frameCount;
    shift = new Vec();
  }

  /**
   * Wraps the pshape into this interactive-frame which is then added to the
   * {@link remixlab.proscene.Scene#frames()} collection. Calls {@code super(scn)}.
   * 
   * @see remixlab.dandelion.core.GenericFrame#GenericFrame(AbstractScene)
   * @see remixlab.proscene.Scene#addFrame(InteractiveFrame)
   */
  public InteractiveFrame(Scene scn, PShape ps) {
    super(scn);
    ((Scene) gScene).addFrame(this);
    id = ++Scene.frameCount;
    shift = new Vec();
    setShape(ps);
  }

  /**
   * Wraps the pshape into this interactive-frame which is created as a child of reference
   * frame and then added to the {@link remixlab.proscene.Scene#frames()} collection.
   * Calls {@code super(scn, referenceFrame)}.
   * 
   * @see remixlab.dandelion.core.GenericFrame#GenericFrame(AbstractScene, Frame)
   * @see remixlab.proscene.Scene#addFrame(InteractiveFrame)
   */
  public InteractiveFrame(Scene scn, Frame referenceFrame, PShape ps) {
    super(scn, referenceFrame);
    ((Scene) gScene).addFrame(this);
    id = ++Scene.frameCount;
    shift = new Vec();
    setShape(ps);
  }

  /**
   * Wraps the function object procedure into this interactive-frame which is then added
   * it to the {@link remixlab.proscene.Scene#frames()} collection. Calls
   * {@code super(scn}.
   * 
   * @see remixlab.dandelion.core.GenericFrame#GenericFrame(AbstractScene)
   * @see remixlab.proscene.Scene#addFrame(InteractiveFrame)
   * @see #addGraphicsHandler(Object, String)
   */
  public InteractiveFrame(Scene scn, Object obj, String methodName) {
    super(scn);
    ((Scene) gScene).addFrame(this);
    id = ++Scene.frameCount;
    shift = new Vec();
    addGraphicsHandler(obj, methodName);
  }

  /**
   * Wraps the the function object procedure into this interactive-frame which is is
   * created as a child of reference frame and then added to the
   * {@link remixlab.proscene.Scene#frames()} collection. Calls
   * {@code super(scn, referenceFrame}.
   * 
   * @see remixlab.dandelion.core.GenericFrame#GenericFrame(AbstractScene, Frame)
   * @see remixlab.proscene.Scene#addFrame(InteractiveFrame)
   * @see #addGraphicsHandler(Object, String)
   */
  public InteractiveFrame(Scene scn, Frame referenceFrame, Object obj, String methodName) {
    super(scn, referenceFrame);
    ((Scene) gScene).addFrame(this);
    id = ++Scene.frameCount;
    shift = new Vec();
    addGraphicsHandler(obj, methodName);
  }

  protected InteractiveFrame(InteractiveFrame otherFrame) {
    super(otherFrame);
    this.pshape = otherFrame.pshape;
    this.id = otherFrame.id;
    this.shift = otherFrame.shift.get();
    this.drawHandlerObject = otherFrame.drawHandlerObject;
    this.drawHandlerMethod = otherFrame.drawHandlerMethod;
  }

  @Override
  public InteractiveFrame get() {
    return new InteractiveFrame(this);
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
  
  public void enableHighlighting() {
    highlight = true;
  }
  
  public void disableHighlighting() {
    highlight = false;
  }
  
  public void toggleHighlighting() {
    highlight = !highlight;
  }
  
  public boolean isHighlightingEnabled() {
    return highlight;
  }
  
  protected int highlight(int color) {
    int c = 0;
    float hue, saturation, brightness;
    ((Scene) gScene).pApplet().pushStyle();
    ((Scene) gScene).pApplet().colorMode(PApplet.HSB, 255);
    
    hue = ((Scene) gScene).pApplet().hue(color);
    saturation = ((Scene) gScene).pApplet().saturation(color);
    brightness = ((Scene) gScene).pApplet().brightness(color);
    brightness *= (brightness > 150 ? 10f / 17f : 17f / 10f );
    c = ((Scene) gScene).pApplet().color(hue, saturation, brightness);
    
    ((Scene) gScene).pApplet().popStyle();
    return c;
  }
  
  protected void highlight(PGraphics pg) {
    pg.scale(1.1f);
    //TODO shapes pending, requires PShape style, stroke* and fill* to be readable
    if( pg.stroke )
      pg.stroke(highlight(pg.strokeColor));
    if( pg.fill )
      pg.fill(highlight(pg.fillColor));
  }

  /**
   * Internal use. Frame graphics color to use in the
   * {@link remixlab.proscene.Scene#pickingBuffer()}.
   */
  protected int id() {
    // see here:
    // http://stackoverflow.com/questions/2262100/rgb-int-to-rgb-python
    return ((Scene) gScene).pickingBuffer().color(id & 255, (id >> 8) & 255, (id >> 16) & 255);
  }

  /**
   * Shifts the {@link #shape()} respect to the frame {@link #position()}. Default value
   * is zero.
   * 
   * @see #graphicsShift()
   */
  public void shiftGraphics(Vec shift) {
    this.shift = shift;
  }

  /**
   * Returns the {@link #shape()} shift.
   * 
   * @see #shiftGraphics(Vec)
   */
  public Vec graphicsShift() {
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
    Scene.GRAPHICS = update();
  }

  /**
   * Internal cache optimization method.
   */
  protected boolean update() {
    if (pshape != null || this.hasGraphicsHandler()) {
      setPickingPrecision(PickingPrecision.EXACT);
      return true;
    } else {
      if (pickingPrecision() == PickingPrecision.EXACT)
        setPickingPrecision(PickingPrecision.ADAPTIVE);
      for (InteractiveFrame m : ((Scene) gScene).frames())
        if (m.pickingPrecision() == PickingPrecision.EXACT)
          return true;
    }
    return false;
  }
  
  @Override
  public void setPickingPrecision(PickingPrecision precision) {
    if(precision == PickingPrecision.EXACT)
      if(pshape == null && !this.hasGraphicsHandler()) {
        System.out.println("Warning: nothing done. EXACT picking precision needs shape or graphics handler");
        return;
      }
    pkgnPrecision = precision;
  }

  /**
   * Unsets the shape which is wrapped by this interactive-frame.
   */
  public PShape unsetShape() {
    PShape prev = pshape;
    pshape = null;
    Scene.GRAPHICS = update();
    return prev;
  }

  /**
   * An interactive-frame is selected using
   * <a href="http://schabby.de/picking-opengl-ray-tracing/">'ray-picking'</a> with a
   * color buffer (see {@link remixlab.proscene.Scene#pickingBuffer()}). This method
   * compares the color of the {@link remixlab.proscene.Scene#pickingBuffer()} at
   * {@code (x,y)} with {@link #id()}. Returns true if both colors are the same, and
   * false otherwise.
   */
  @Override
  public final boolean checkIfGrabsInput(float x, float y) {
    if (pickingPrecision() != PickingPrecision.EXACT || !((Scene) gScene).isPickingBufferEnabled())
      return super.checkIfGrabsInput(x, y);
    ((Scene) gScene).pickingBuffer().pushStyle();
    ((Scene) gScene).pickingBuffer().colorMode(PApplet.RGB, 255);
    int index = (int) y * gScene.width() + (int) x;
    if ((0 <= index) && (index < ((Scene) gScene).pickingBuffer().pixels.length))
      return ((Scene) gScene).pickingBuffer().pixels[index] == id();
    ((Scene) gScene).pickingBuffer().popStyle();
    return false;
  }

  /**
   * Same as {@code draw(scene.pg())}.
   * 
   * @see remixlab.proscene.Scene#drawFrames(PGraphics)
   */
  public void draw() {
    if (shape() == null && !this.hasGraphicsHandler())
      return;
    PGraphics pg = ((Scene) gScene).pg();
    draw(pg);
  }

  /**
   * Draw the visual representation of the frame into the given PGraphics using the
   * current point of view (see
   * {@link remixlab.proscene.Scene#applyWorldTransformation(PGraphics, Frame)} ).
   * <p>
   * This method is internally called by the scene to
   * {@link remixlab.proscene.Scene#drawFrames(PGraphics)} into the
   * {@link remixlab.proscene.Scene#disablePickingBuffer()} and by {@link #draw()} to draw
   * the frame into the scene main {@link remixlab.proscene.Scene#pg()}.
   */
  public boolean draw(PGraphics pg) {
    if (shape() == null && !this.hasGraphicsHandler())
      return false;
    pg.pushStyle();
    if (pg == ((Scene) gScene).pickingBuffer()) {
      if (shape() != null)
        shape().disableStyle();
      pg.colorMode(PApplet.RGB, 255);
      pg.fill(id());
      pg.stroke(id());
    }
    pg.pushMatrix();
    ((Scene) gScene).applyWorldTransformation(pg, this);
    //drawNode(pg);
    // ->
    pg.translate(shift.x(), shift.y(), shift.z());
    //TODO shapes pending, requires PShape style, stroke* and fill* to be readable
    if(isHighlightingEnabled() && this.grabsInput() && pg != ((Scene) gScene).pickingBuffer())
      highlight(pg);
    if (shape() != null)
      pg.shape(shape());
    if (this.hasGraphicsHandler())
      this.invokeGraphicsHandler(pg);
    // <-
    pg.popMatrix();
    if (pg == ((Scene) gScene).pickingBuffer()) {
      if (shape() != null)
        shape().enableStyle();
    }
    pg.popStyle();
    return true;
  }
  
  /*
  protected void drawNode(PGraphics pg) {
    pg.translate(shift.x(), shift.y(), shift.z());
    //TODO shapes pending, requires PShape style, stroke* and fill* to be readable
    if(isHighlightingEnabled() && this.grabsInput() && pg != ((Scene) gScene).pickingBuffer())
      highlight(pg);
    if (shape() != null)
      pg.shape(shape());
    if (this.hasGraphicsHandler())
      this.invokeGraphicsHandler(pg);
  }
  */

  // DRAW METHOD REG

  /**
   * Internal use. Invokes an external drawing method (if registered). Called by
   * {@link #draw(PGraphics)}.
   */
  protected boolean invokeGraphicsHandler(PGraphics pg) {
    if (drawHandlerObject != null) {
      try {
        drawHandlerMethod.invoke(drawHandlerObject, new Object[] { pg });
        return true;
      } catch (Exception e) {
        PApplet.println("Something went wrong when invoking your " + drawHandlerMethod.getName() + " method");
        e.printStackTrace();
        return false;
      }
    }
    return false;
  }

  /**
   * Attempt to add a graphics handler method to the frame. The default event handler is a
   * method that returns void and has one single PGraphics parameter. Note that the method
   * should only deal with geometry and that not coloring procedure may be specified
   * within it.
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
      drawHandlerObject = obj;
      Scene.GRAPHICS = update();
    } catch (Exception e) {
      PApplet.println("Something went wrong when registering your " + methodName + " method");
      e.printStackTrace();
    }
  }

  /**
   * Unregisters the graphics handler method (if any has previously been added to the
   * Scene).
   * 
   * @see #addGraphicsHandler(Object, String)
   * @see #invokeGraphicsHandler(PGraphics)
   */
  public void removeGraphicsHandler() {
    drawHandlerMethod = null;
    drawHandlerObject = null;
    Scene.GRAPHICS = update();
  }

  /**
   * Returns {@code true} if the user has registered a graphics handler method to the
   * Scene and {@code false} otherwise.
   * 
   * @see #addGraphicsHandler(Object, String)
   * @see #invokeGraphicsHandler(PGraphics)
   */
  public boolean hasGraphicsHandler() {
    if (drawHandlerMethod == null)
      return false;
    return true;
  }
}