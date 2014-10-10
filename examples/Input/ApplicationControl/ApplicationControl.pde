/**
 * Application Control.
 * by Jean Pierre Charalambos.
 * 
 * This demo controls the shape and color of the scene torus using and a custom mouse agent.
 * 
 * Click and drag the ellipse with the left mouse to control the torus color and shape.
 * Press ' ' (the spacebar) to toggle the application canvas aid.
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.bias.core.*;
import remixlab.bias.agent.*;
import remixlab.bias.agent.profile.*;
import remixlab.bias.event.*;
import remixlab.proscene.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.*;

public class MouseAgent extends ActionMotionAgent<MotionProfile<MotionAction>, ClickProfile<ClickAction>> {
  DOF2Event event, prevEvent;
  public MouseAgent(InputHandler scn, String n) {
    super(new MotionProfile<MotionAction>(), 
    new ClickProfile<ClickAction>(), scn, n);
    clickProfile().setBinding(LEFT, 1, ClickAction.CHANGE_COLOR);
    profile().setBinding(LEFT, MotionAction.CHANGE_SHAPE);
  }

  public void mouseEvent(processing.event.MouseEvent e) {
    if ( e.getAction() == processing.event.MouseEvent.CLICK ) {
      handle(new ClickEvent(e.getX() - oX, e.getY() - oY, e.getModifiers(), e.getButton(), e.getCount()));
      return;
    }
    event = new DOF2Event(prevEvent, e.getX() - oX, e.getY() - oY, e.getModifiers(), e.getButton());
    if ( e.getAction() == processing.event.MouseEvent.MOVE )
      updateTrackedGrabber(event);
    if ( e.getAction() == processing.event.MouseEvent.DRAG )
      handle(event);
    prevEvent = event.get();
  }
}

public class Ellipse extends GrabberObject {
  PGraphics pg;
  public float radiusX, radiusY;
  public PVector center;
  public color colour;

  public Ellipse(PGraphics p) {
    pg = p;
    setColor();
    setPosition();
  }

  public Ellipse(PGraphics p, PVector c, float r) {
    pg = p;
    radiusX = r;
    radiusY = r;
    center = c;    
    setColor();
  }

  public void setColor() {
    setColor(color(random(0, 255), random(0, 255), random(0, 255), 125));
  }

  public void setColor(color myC) {
    colour = myC;
  }

  public void setPosition(float x, float y) {
    setPositionAndRadii(new PVector(x, y), radiusX, radiusY);
  }

  public void setPositionAndRadii(PVector p, float rx, float ry) {
    center = p;
    radiusX = rx;
    radiusY = ry;
  }

  public void setPosition() {
    float maxRadius = 50;
    float low = maxRadius;
    float highX = w - maxRadius;
    float highY = h - maxRadius;
    float r = random(20, maxRadius);
    setPositionAndRadii(new PVector(random(low, highX), random(low, highY)), r, r);
  }

  public void draw() {
    draw(colour);
  }

  public void draw(int c) {
    pg.pushStyle();
    pg.stroke(c);
    pg.fill(c);
    pg.ellipse(center.x, center.y, 2*radiusX, 2*radiusY);
    pg.popStyle();
  }

  @Override
  public boolean checkIfGrabsInput(BogusEvent event) {
    if (event instanceof DOF2Event) {
      float x = ((DOF2Event)event).x();
      float y = ((DOF2Event)event).y();
      return(pow((x - center.x), 2)/pow(radiusX, 2) + pow((y - center.y), 2)/pow(radiusY, 2) <= 1);
    }      
    return false;
  }

  @Override
  public void performInteraction(BogusEvent event) {
    if (((BogusEvent)event).action() != null) {
      switch ((GlobalAction) ((BogusEvent)event).action().referenceAction()) {
      case CHANGE_COLOR:
        colour = color(color(random(0, 255), random(0, 255), random(0, 255), 125));
        break;
      case CHANGE_SHAPE:
        radiusX += ((DOF2Event)event).dx();
        radiusY += ((DOF2Event)event).dy();
        break;
      }
    }
  }
}

int w = 200;
int h = 120;
int oX = 640-w;
int oY = 360-h;
MouseAgent agent;
Ellipse e;
PGraphics canvas;
Scene scene;
PGraphics ctrlCanvas;
boolean showAid = true;

color c;

void setup() {
  size(640, 360, P3D);

  canvas = createGraphics(640, 360, P3D); 
  scene = new Scene(this, canvas);
  scene.addDrawHandler(this, "drawing");

  ctrlCanvas = createGraphics(w, h);  
  agent = new MouseAgent(scene.inputHandler(), "my_mouse");
  scene.inputHandler().unregisterAgent(agent);

  e = new Ellipse(ctrlCanvas, new PVector(w/2, h/2), 30);
  agent.addInPool(e);
}

void draw() {
  handleMouse();
  canvas.beginDraw();
  scene.beginDraw();
  canvas.background(255);    
  scene.endDraw();
  canvas.endDraw();
  image(canvas, scene.originCorner().x(), scene.originCorner().y());
 
  if(showAid) {
    ctrlCanvas.beginDraw();  
    ctrlCanvas.background(125, 125, 125, 125);    
    ctrlDrawing(ctrlCanvas);
    ctrlCanvas.endDraw();
    image(ctrlCanvas, oX, oY);
  }
}

void drawing(Scene s) {
  s.pg().pushStyle();
  s.pg().fill(e.colour);
  s.drawTorusSolenoid(int(map(PI*e.radiusX*e.radiusY, 20, w*h, 2, 50)), 100, e.radiusY, e.radiusX);
  s.pg().popStyle();
}

void ctrlDrawing(PGraphics pg) {
  pg.pushStyle();
  pg.stroke(255, 255, 0);
  pg.fill(100);
  if ( e.grabsInput(agent) )
    e.draw(color(red(e.colour), green(e.colour), blue(e.colour)));
  else
    e.draw();
  pg.popStyle();
}

void handleMouse() {
  scene.enableMotionAgent();
  disableCustomAgent();
  if((oX < mouseX) && (oY < mouseY) && showAid) {
    scene.disableMotionAgent();
    enableCustomAgent();
  }
}

void enableCustomAgent() {
  if (!scene.inputHandler().isAgentRegistered(agent)) {
    scene.inputHandler().registerAgent(agent);
    registerMethod("mouseEvent", agent);
  }
}

void disableCustomAgent(){
  scene.inputHandler().unregisterAgent(agent);
  unregisterMethod("mouseEvent", agent);
}

void keyPressed() {
  if(key == ' ')
    showAid = !showAid;
}