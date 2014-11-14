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

public class CustomMouseAgent extends ActionMotionAgent<MotionProfile<MotionAction>, ClickProfile<ClickAction>> {
  DOF2Event event, prevEvent;
  public CustomMouseAgent(Scene scn, String n) {
    super(new MotionProfile<MotionAction>(), new ClickProfile<ClickAction>(), scn.inputHandler(), n);
    clickProfile().setBinding(LEFT, 1, ClickAction.CHANGE_COLOR);
    profile().setBinding(LEFT, MotionAction.CHANGE_SHAPE);
  }

  public void mouseEvent(processing.event.MouseEvent e) {
    if ( e.getAction() == processing.event.MouseEvent.CLICK ) {
      handle(new ClickEvent(e.getX() - ctrlScene.originCorner().x(), e.getY() - ctrlScene.originCorner().y(), e.getModifiers(), e.getButton(), e.getCount()));
      return;
    }
    event = new DOF2Event(prevEvent, e.getX() - ctrlScene.originCorner().x(), e.getY() - ctrlScene.originCorner().y(), e.getModifiers(), e.getButton());
    if ( e.getAction() == processing.event.MouseEvent.MOVE )
      updateTrackedGrabber(event);
    if ( e.getAction() == processing.event.MouseEvent.DRAG )
      handle(event);
    prevEvent = event.get();
  }
}

public class ModelEllipse extends ModelObject {
  public ModelEllipse(Scene scn) {
    super(scn);
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
CustomMouseAgent agent;
PGraphics ctrlCanvas;
Scene ctrlScene;

public int colour;
public float radiusX, radiusY;
public PVector center;
public PShape eShape;
ModelEllipse e;
PGraphics canvas;
Scene scene;
boolean showAid = true;

public void setup() {
  size(640, 360, P3D);

  canvas = createGraphics(640, 360, P3D); 
  scene = new Scene(this, canvas);

  ctrlCanvas = createGraphics(w, h, P2D);
  ctrlScene = new Scene(this, ctrlCanvas, oX, oY);
  ctrlScene.disableMotionAgent();
  agent = new CustomMouseAgent(ctrlScene, "my_mouse");
  colour = color(255, 0, 0);
  center = new PVector();
  radiusX = 30;
  radiusY = 30;
  e = new ModelEllipse(ctrlScene);
  agent.addInPool(e);
}

public void updateShape() {
  PShape shape = ctrlScene.pg().createShape(ELLIPSE, center.x-radiusX, center.y-radiusY, 2*radiusX, 2*radiusY);
  shape.setFill(color(colour));
  e.setShape(shape);
}

public void draw() {
  handleMouse();
  canvas.beginDraw();
  scene.beginDraw();
  canvas.background(255);
  canvas.fill(colour);
  scene.drawTorusSolenoid((int)map(PI*radiusX*radiusY, 20, w*h, 2, 50), 100, radiusY, radiusX);
  scene.endDraw();
  canvas.endDraw();
  image(canvas, scene.originCorner().x(), scene.originCorner().y());

  if (showAid) {
    ctrlCanvas.beginDraw();
    ctrlScene.beginDraw();
    ctrlCanvas.background(125, 125, 125, 125);
    updateShape();
    ctrlScene.drawModels();
    ctrlScene.endDraw();
    ctrlCanvas.endDraw();
    image(ctrlCanvas, ctrlScene.originCorner().x(), ctrlScene.originCorner().y());
  }
}

void handleMouse() {
  scene.enableMotionAgent();
  disableCustomAgent();
  if ((oX < mouseX) && (oY < mouseY) && showAid) {
    scene.disableMotionAgent();
    enableCustomAgent();
  }
}

void enableCustomAgent() {
  if (!scene.inputHandler().isAgentRegistered(agent)) {
    ctrlScene.inputHandler().registerAgent(agent);
    registerMethod("mouseEvent", agent);
  }
}

void disableCustomAgent() {
  ctrlScene.inputHandler().unregisterAgent(agent);
  unregisterMethod("mouseEvent", agent);
}

public void keyPressed() {
  if (key == ' ')
    showAid = !showAid;
}