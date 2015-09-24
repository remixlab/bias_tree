/**
 * Custom Eye Frame.
 * by Jean Pierre Charalambos.
 * 
 * Customizing the camera eye frame only requires to extend from the
 * GrabberFrame and implementing some of the performInteraction() methods on
 * the event types the new frame needs to support.
 *
 * Press ' ' to switch between the custom eye frame and the default one.
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.bias.core.*;
import remixlab.dandelion.core.*;
import remixlab.bias.event.*;

public class CustomFrame extends GrabberFrame {
  public CustomFrame(Scene _scn) {
    super(_scn);
  }
  
  public CustomFrame(Eye _eye) {
    super(_eye);
  }

  protected CustomFrame(CustomFrame otherFrame) {
    super(otherFrame);
  }

  @Override
  public CustomFrame get() {
    return new CustomFrame(this);
  }

  @Override
  public CustomFrame detach() {
    CustomFrame frame = new CustomFrame(scene);
    for (Agent agent : scene.inputHandler().agents())
      agent.removeGrabber(frame);
    frame.fromFrame(this);
    return frame;
  }

  @Override
  public void performInteraction(DOF2Event event) {
    if (event.id() == LEFT)
      gestureArcball(event);
    if (event.id() == RIGHT)
      gestureTranslateXY(event);
  }

  @Override
  public void performInteraction(ClickEvent event) {
    println("to be implemented");
  }
}

Scene scene;
CustomFrame  eyeFrame;
GrabberFrame orig;
public void setup() {
  size(640, 360, P3D); 
  scene = new Scene(this);
  orig = scene.eyeFrame();
  eyeFrame = new CustomFrame(scene.eye());
  scene.mouseAgent().addGrabber(eyeFrame);
  scene.camera().setFieldOfView((float) Math.PI / 3.0f);
  scene.eye().setFrame(eyeFrame);
  scene.showAll();
}

public void draw() {
  background(0);
  fill(204, 102, 0);
  box(20, 30, 40);
}

public void keyPressed() {
  if (key == ' ') {
    if ( scene.eyeFrame() == orig ) {
      println("setting custom eye");
      scene.eye().setFrame(eyeFrame);
    } else {
      println("resetting to orig eye frame");
      scene.eye().setFrame(orig);
    }
  }
}