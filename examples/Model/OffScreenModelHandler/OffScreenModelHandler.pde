/**
 * Off-Screen Model Handler.
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
Model[] models;	
int num_vertex = 50;

@Override
public void setup() {
  size(640, 720, P3D);
  //Scene instantiation
  main_buffer = createGraphics(640, 360, P3D);
  main_buffer.smooth();
  scene = new Scene(this, main_buffer);
  scene.addGraphicsHandler(this, "mainDrawing");
  models = new Model[10];
  for (int i = 0; i < models.length; i++) {
    models[i] = new Model(scene);
    models[i].addGraphicsHandler(this, "drawRandomPolygon");
    models[i].translate(10*i, 10*i, 10*i);
  }
}

void draw() {
  background(0);
  main_buffer.beginDraw();
  scene.beginDraw();
  scene.endDraw();
  main_buffer.endDraw();
  image(main_buffer, 0, 0);
  image(scene.pickingBuffer(), 0, 360);
}

void mainDrawing(Scene s) {
  s.pg().background(0);
}

void drawRandomPolygon(PGraphics pg) {
  pg.box(20, 15, 30);
}
