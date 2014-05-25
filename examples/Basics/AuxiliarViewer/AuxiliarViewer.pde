/**
 * Auxiliar Viewer
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates how to use proscene off-screen rendering to build
 * an second view on the main Scene. It also shows Frame syncing among views. 
 *
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

Scene scene, auxScene;
PGraphics canvas, auxCanvas;  
InteractiveFrame frame1, auxFrame1, frame2, auxFrame2, frame3, auxFrame3;  
boolean drawHints = false;

//Choose one of P3D for a 3D scene, or P2D or JAVA2D for a 2D scene
String renderer = P2D;

public void setup() {
  size(640, 720, renderer);
  canvas = createGraphics(640, 360, renderer);
  canvas.smooth();
  scene = new Scene(this, canvas);
  scene.addDrawHandler(this, "mainDrawing");
  frame1 = new InteractiveFrame(scene);
  frame1.translate(new Vec(30, 30));
  frame2 = new InteractiveFrame(scene);
  frame2.setReferenceFrame(frame1);
  frame2.translate(new Vec(40, 0, 0));
  frame3 = new InteractiveFrame(scene);
  frame3.setReferenceFrame(frame2);
  frame3.translate(new Vec(40, 0, 0));

  auxCanvas = createGraphics(640, 360, renderer);
  auxCanvas.smooth();
  // Note that we pass the upper left corner coordinates where the scene
  // is to be drawn (see drawing code below) to its constructor.
  auxScene = new Scene(this, auxCanvas, 0, 360);
  auxScene.addDrawHandler(this, "auxDrawing");
  auxScene.setRadius(200);
  auxScene.showAll();

  auxFrame1 = new InteractiveFrame(auxScene);
  auxFrame1.translate(new Vec(30, 30));
  auxFrame2 = new InteractiveFrame(auxScene);
  auxFrame2.setReferenceFrame(auxFrame1);
  auxFrame2.translate(new Vec(40, 0, 0));
  auxFrame3 = new InteractiveFrame(auxScene);
  auxFrame3.setReferenceFrame(auxFrame2);
  auxFrame3.translate(new Vec(40, 0, 0));

  handleMouse();
  smooth();
}

public void draw() {
  handleMouse();
  Frame.sync(frame1, auxFrame1);
  Frame.sync(frame2, auxFrame2);
  Frame.sync(frame3, auxFrame3);
  canvas.beginDraw();
  scene.beginDraw();
  canvas.background(0);
  scene.endDraw();
  canvas.endDraw();
  image(canvas, 0, 0);

  auxCanvas.beginDraw();
  auxScene.beginDraw();
  auxCanvas.background(0);    
  auxScene.endDraw();
  auxCanvas.endDraw();

  // We retrieve the scene upper left coordinates defined above.
  image(auxCanvas, auxScene.upperLeftCorner.x(), auxScene.upperLeftCorner.y());
}

public void mainDrawing(Scene s) {        
  s.pg().pushStyle();
  s.pushModelView();
  s.applyTransformation(frame1);  
  if (drawHints)
    s.drawAxes(40);
  if (drawHints && ((s.motionAgent().trackedGrabber() == frame1) || (s.motionAgent().trackedGrabber() == auxFrame1)))
    s.pg().fill(255, 0, 0);
  else 
    s.pg().fill(0, 0, 255);
  s.pg().rect(0, 0, 40, 10, 5);

  s.pushModelView();
  s.applyTransformation(frame2);  
  if (drawHints)
    s.drawAxes(40);
  if (drawHints && ((s.motionAgent().trackedGrabber() == frame2) || (s.motionAgent().trackedGrabber() == auxFrame2)))
    s.pg().fill(255, 0, 0);
  else
    s.pg().fill(255, 0, 255);
  s.pg().rect(0, 0, 40, 10, 5);

  s.pushModelView();
  s.applyTransformation(frame3);
  if (drawHints)
    s.drawAxes(40);
  if (drawHints && ((s.motionAgent().trackedGrabber() == frame3) || (s.motionAgent().trackedGrabber() == auxFrame3)))
    s.pg().fill(255, 0, 0);
  else 
    s.pg().fill(0, 255, 255);
  s.pg().rect(0, 0, 40, 10, 5);
  
  s.popModelView();
  s.popModelView();
  s.popModelView();
  s.pg().popStyle();
}

public void auxDrawing(Scene s) {
  mainDrawing(s);
  s.pg().pushStyle();
  s.pg().stroke(255, 255, 0);
  s.pg().fill(255, 255, 0, 160);
  s.drawEye(scene.eye());
  s.pg().popStyle();
}

public void handleMouse() {
  if (mouseY < 360) {
    scene.enableMouseAgent();
    scene.enableKeyboardAgent();
    auxScene.disableMouseAgent();
    auxScene.disableKeyboardAgent();
  } 
  else {
    scene.disableMouseAgent();
    scene.disableKeyboardAgent();
    auxScene.enableMouseAgent();
    auxScene.enableKeyboardAgent();
  }
}

public void printFrame(Frame frame) {
  println("Translation: " + frame.translation());
  println("Angle: " + frame.rotation().angle());
  println("Scaling: " + frame.scaling());
}

public void keyPressed() {
  if (key == 'u' || key == 'U') {
    drawHints = !drawHints;
  }
  if (key == 'v' || key == 'V') {
    scene.flip();
  }
}