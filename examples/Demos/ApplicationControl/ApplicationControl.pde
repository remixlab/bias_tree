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

import remixlab.bias.event.*;
import remixlab.proscene.*;

int                w       = 200;
int                h       = 120;
int                oX      = 640 - w;
int                oY      = 360 - h;
PGraphics          ctrlCanvas;
Scene              ctrlScene;
public PShape      eShape;
InteractiveFrame   e;
PGraphics          canvas;
Scene              scene;
boolean            showAid  = true;

float  radiusX  = 30, radiusY = 30;
int    colour  = color(255, 0, 0);

public void setup() {
  size(640, 360, P2D);

  canvas = createGraphics(640, 360, P3D);
  scene = new Scene(this, canvas);

  ctrlCanvas = createGraphics(w, h, P2D);
  ctrlScene = new Scene(this, ctrlCanvas, oX, oY);

  e = new InteractiveFrame(ctrlScene);
  updateEllipse(e);
  e.setDOF1Binding(this, MouseAgent.WHEEL_ID, "changeShape");
  e.setDOF2Binding(this, LEFT, "changeShape");
  e.setClickBinding(this, LEFT, 1, "changeColor");
  e.setKeyBinding(this, 'x', "colorBlue");
  e.setKeyBinding(this, 'y', "colorRed");
  ctrlScene.keyAgent().setDefaultGrabber(e);
}

public void changeShape(InteractiveFrame frame, DOF1Event event) {
  radiusX += event.dx()*5;
  updateEllipse(frame);
}

public void changeShape(InteractiveFrame frame, DOF2Event event) {
  radiusX += event.dx();
  radiusY += event.dy();
  updateEllipse(frame);
}

public void changeColor(InteractiveFrame frame) {
  colour = color(color(random(0, 255), random(0, 255), random(0, 255), 125));
  updateEllipse(frame);
}

public void colorBlue(InteractiveFrame frame) {
  colour = color(0, 0, 255);
  updateEllipse(frame);
}

public void colorRed(InteractiveFrame frame) {
  colour = color(255, 0, 0);
  updateEllipse(frame);
}

public void updateEllipse(InteractiveFrame frame) {
  frame.setShape(createShape(ELLIPSE, 0, 0, 2 * radiusX, 2 * radiusY));
  frame.shape().setFill(color(colour));
}

public void draw() {
  handleAgents();
  canvas.beginDraw();
  scene.beginDraw();
  canvas.background(255);
  canvas.fill(colour);
  scene.drawTorusSolenoid((int) map(PI * radiusX * radiusY, 20, w * h, 2, 50), 100, radiusY, radiusX);
  scene.endDraw();
  canvas.endDraw();
  image(canvas, scene.originCorner().x(), scene.originCorner().y());
  if (showAid) {
    ctrlCanvas.beginDraw();
    ctrlScene.beginDraw();
    ctrlCanvas.background(125, 125, 125, 125);
    ctrlScene.drawFrames();
    ctrlScene.endDraw();
    ctrlCanvas.endDraw();
    image(ctrlCanvas, ctrlScene.originCorner().x(), ctrlScene.originCorner().y());
  }
}

void handleAgents() {
  scene.enableMotionAgent();
  ctrlScene.disableMotionAgent();
  scene.enableKeyboardAgent();
  ctrlScene.disableKeyboardAgent();
  if ((oX < mouseX) && (oY < mouseY) && showAid) {
    scene.disableMotionAgent();
    ctrlScene.enableMotionAgent();
    scene.disableKeyboardAgent();
    ctrlScene.enableKeyboardAgent();
  }
}

public void keyPressed() {
  if (key == ' ')
    showAid = !showAid;
}