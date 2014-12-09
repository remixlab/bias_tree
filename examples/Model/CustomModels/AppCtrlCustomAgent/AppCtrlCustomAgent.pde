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
import remixlab.bias.event.*;
import remixlab.bias.agent.*;
import remixlab.bias.agent.profile.*;
import remixlab.proscene.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.*;

public class CustomMouseAgent extends ActionMotionAgent<MotionProfile<MotionAction>, ClickProfile<ClickAction>> {
  public CustomMouseAgent(MouseAgent parent, String n) {
    super(new MotionProfile<MotionAction>(), 
    new ClickProfile<ClickAction>(), parent, n);
    clickProfile().setBinding(LEFT, 1, ClickAction.CHANGE_COLOR);
    profile().setBinding(LEFT, MotionAction.CHANGE_SHAPE);
  }
}

public class ModelEllipse extends ModelObject {
  float radiusX = 30, radiusY = 30;
  color colour = color(255, 0, 0);
  public ModelEllipse(Scene scn) {
    super(scn);
    update();
  }
  
  @Override
  public void performInteraction(DOF2Event event) {
    if (event.action() != null) {
      switch ((MotionAction) event.action()) {
      case CHANGE_SHAPE:
        radiusX += event.dx();
        radiusY += event.dy();
        update();
        break;
      }
    }
  }
  
  @Override
  public void performInteraction(ClickEvent event) {
    if (event.action() != null) {
      //switch ((GlobalAction) event.action().referenceAction()) {
      switch ((ClickAction) event.action()) {
      case CHANGE_COLOR:
        colour = color(color(random(0, 255), random(0, 255), random(0, 255), 125));
        update();
        break;
      }
    }
  }
  
  void update() {
    setShape(createShape(ELLIPSE, -radiusX, -radiusY, 2*radiusX, 2*radiusY));
    shape().setFill(color(colour));
  }
}

int w = 200;
int h = 120;
int oX = 640-w;
int oY = 360-h;
PGraphics ctrlCanvas;
Scene ctrlScene;
CustomMouseAgent agent;
public PShape eShape;
ModelEllipse e;
PGraphics canvas;
Scene scene;
boolean showAid = true;

void setup() {
  size(640, 360, P2D);

  canvas = createGraphics(640, 360, P3D); 
  scene = new Scene(this, canvas);

  ctrlCanvas = createGraphics(w, h, P2D);
  ctrlScene = new Scene(this, ctrlCanvas, oX, oY);
  agent = new CustomMouseAgent(ctrlScene.mouseAgent(), "my_mouse");
  ctrlScene.setAxesVisualHint(false);
  ctrlScene.setGridVisualHint(false);
  e = new ModelEllipse(ctrlScene);
  //agent.addInPool(e);
}

void draw() {
  handleMouse();
  canvas.beginDraw();
  scene.beginDraw();
  canvas.background(255);
  canvas.fill(e.colour);
  scene.drawTorusSolenoid((int)map(PI*e.radiusX*e.radiusY, 20, w*h, 2, 50), 100, e.radiusY, e.radiusX);
  scene.endDraw();
  canvas.endDraw();
  image(canvas, scene.originCorner().x(), scene.originCorner().y());

  if (showAid) {
    ctrlCanvas.beginDraw();
    ctrlScene.beginDraw();
    ctrlCanvas.background(125, 125, 125, 125);
    ctrlScene.drawModels();
    ctrlScene.endDraw();
    ctrlCanvas.endDraw();
    image(ctrlCanvas, ctrlScene.originCorner().x(), ctrlScene.originCorner().y());
  }
}

void handleMouse() {
  scene.enableMotionAgent();
  ctrlScene.disableMotionAgent();
  if ((oX < mouseX) && (oY < mouseY) && showAid) {
    scene.disableMotionAgent();
    ctrlScene.enableMotionAgent();
  }
}

void keyPressed() {
  if (key == ' ')
    showAid = !showAid;
}