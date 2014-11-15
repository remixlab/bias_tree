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

CustomMouseAgent customMouseAgent;
Scene scene;
InteractiveModelFrame[] models;

void setup() {
  size(640, 360, P3D);
  //Scene instantiation
  scene = new Scene(this);
  customMouseAgent = new CustomMouseAgent(scene, "MyMouseAgent");
  models = new InteractiveModelFrame[10];	  
  for (int i = 0; i < models.length; i++) {
    models[i] = new InteractiveModelFrame(scene, polygon(50));
    models[i].translate(10*i, 10*i, 10*i);
    customMouseAgent.addInPool(models[i]);
  }
  switchAgents();
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

PShape polygon(int num_vertex) {
  PShape sh = createShape(); 
  sh.beginShape(QUAD_STRIP);
  for (int i = 0; i < num_vertex; i++) {
    sh.vertex(random(0, 15), random(0, 15), random(0, 15));
  }
  sh.endShape(CLOSE);
  return sh;
}

void switchAgents() {
  if ( scene.isMotionAgentEnabled() ) {
    scene.disableMotionAgent();
    scene.inputHandler().registerAgent(customMouseAgent);
    registerMethod("mouseEvent", customMouseAgent);
  } else {
    scene.inputHandler().unregisterAgent(customMouseAgent);
    unregisterMethod("mouseEvent", customMouseAgent);
    scene.enableMotionAgent();
  }
}

void keyPressed() {
  // We switch between the default mouse agent and the one we created:
  if ( key == ' ')
    switchAgents();
}

public class CustomMouseAgent extends MouseAgent {
  Grabber tracked;
  public CustomMouseAgent(Scene scn, String n) {
    super(scn, n);
  }

  public void mouseEvent(processing.event.MouseEvent e) {
    if (e.getAction() == processing.event.MouseEvent.MOVE || e.getAction() == processing.event.MouseEvent.CLICK) {
      if (e.getAction() == processing.event.MouseEvent.MOVE)
        event = new DOF2Event(lastEvent(), e.getX() - scene.originCorner().x(), e.getY() - scene.originCorner().y());
      else {
        boolean tracking = this.isTracking();
        if (!tracking)
          this.enableTracking();
        if ( this.isTracking() )
          updateTrackedGrabber(new ClickEvent(e.getX() - scene.originCorner().x(), e.getY() - scene.originCorner().y(), 	e.getModifiers(), e.getButton(), e.getCount()));
        tracked = this.trackedGrabber();
        if (!tracking)
          this.disableTracking();
      }
    } else {
      if ( tracked != null ) {
        setDefaultGrabber(tracked);
        disableTracking();
      } else {
        setDefaultGrabber(scene.eye().frame());
        enableTracking();
      }
      super.mouseEvent(e);
    }
  }
}