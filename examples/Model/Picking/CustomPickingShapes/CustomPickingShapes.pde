/**
 * Custom Picking Shapes.
 * by Sebastian Chaparro, William Rodriguez and Jean Pierre Charalambos.
 * 
 * TODO: doc me
 * 
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

public class PolygonModel extends InteractiveModelFrame {
  float radiusX = 30, radiusY = 30;
  color colour = color(255, 255, 0);
  public PolygonModel(Scene scn) {
    super(scn);
    setShape(create());
  }
    
  @Override
  public void performInteraction(ClickEvent event) {
    colour = color(color(random(0, 255), random(0, 255), random(0, 255), 125));
    scene.motionAgent().setDefaultGrabber( (scene.motionAgent().trackedGrabber()==this) ? this : scene.eye().frame() );
    setShape(create());
  }
  
  public PShape create() {
    PShape sh = createShape(); 
    sh.beginShape(QUAD_STRIP);
    for (int i = 0; i < 50; i++) {
      sh.vertex(random(0, 15), random(0, 15), random(0, 15));
    }
    sh.endShape(CLOSE);
    sh.setFill(color(colour));
    return sh;
  }
}

Scene scene;
PolygonModel[] models;

void setup() {
  size(640, 360, P3D);
  //Scene instantiation
  scene = new Scene(this);
  models = new PolygonModel[10];	  
  for (int i = 0; i < models.length; i++) {
    models[i] = new PolygonModel(scene);
    models[i].translate(10*i, 10*i, 10*i);
  }
  scene.mouseAgent().setClickBinding(Target.FRAME,LEFT,ClickAction.CUSTOM);
  smooth();
}

void draw() {
  background(0);
  scene.drawModels();
}