/**
 * Picking 3DShapes.
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
InteractiveModel[] models;

void setup() {
  size(640, 360, P2D);
  //Scene instantiation
  scene = new Scene(this);
  models = new InteractiveModel[10];

  for (int i = 0; i < models.length; i++) {
    models[i] = new InteractiveModel(scene, createShape(RECT, 0, 0, 20, 20));
    models[i].translate(10*i, 10*i, 10*i);
  }
  smooth();
}

void draw() {
  background(0);
  updatePickedModelColor();
  scene.drawModels();
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