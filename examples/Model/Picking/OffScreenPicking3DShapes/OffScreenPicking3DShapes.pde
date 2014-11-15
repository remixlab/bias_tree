/**
 * Off-Screen Picking 3DShapes
 * by Sebastian Chaparro, William Rodriguez and Jean Pierre Charalambos.
 * 
 * TODO: doc me
 * 
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;

PGraphics main_buffer;
Scene scene;
InteractiveModelFrame[] models;

void setup() {
  size(640, 720, P3D);
  //Scene instantiation
  main_buffer = createGraphics(640, 360, P3D);
  main_buffer.smooth();
  scene = new Scene(this, main_buffer);
  models = new InteractiveModelFrame[10];
  for (int i = 0; i < models.length; i++) {
    models[i] = new InteractiveModelFrame(scene, polygon(50));
    models[i].translate(10*i, 10*i, 10*i);
  }
}

void draw() {
  background(0);
  main_buffer.beginDraw();
  main_buffer.background(0);
  scene.beginDraw();
  updatePickedModelColor();
  scene.drawModels();
  scene.endDraw();
  main_buffer.endDraw();
  image(main_buffer, 0, 0);
  image(scene.pickingBuffer(), 0, 360);
}

void updatePickedModelColor() {
  for (int i = 0; i < models.length; i++) {
    if (scene.grabsAnyAgentInput(models[i]))
      models[i].shape().setFill(color(255, 0, 0));
    else
      models[i].shape().setFill(color(0, 0, 255));
    models[i].shape().setStroke(color(255, 0, 0));
  }
}

PShape polygon(int num_vertex) {
  PShape sh = createShape(); 
  sh.beginShape(QUAD_STRIP);
  for (int i = 0; i < num_vertex; i++) {
    sh.vertex(random(0, 15), random(0, 15), random(0, 15));
  }
  sh.endShape(CLOSE);
  return sh;
}