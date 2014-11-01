/**
 * Picking 2DShapes.
 * by Sebastian Chaparro, William Rodriguez and Jean Pierre Charalambos.
 * 
 * TODO: doc me
 * 
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;

Scene scene;
Model[] models;
String renderer = JAVA2D;
//String renderer = P2D;

void setup() {
  size(640, 360, renderer);
  scene = new Scene(this);
  models = new Model[10];

  for (int i = 0; i < models.length; i++) {
    models[i] = new Model(scene);
    models[i].addGraphicsHandler(this, "drawRect");
    models[i].translate(10*i, 10*i, 10*i);
  }
  smooth();
}

void draw() {
  background(0);
}

void drawRect(PGraphics pg) {
  fill(255, 0, 255);// :P
  pg.rect(20, 15, 30, 20);
}
