/**
 * Custom Interactive Eye Frame.
 * by Jean Pierre Charalambos.
 * 
 * This example shows how to customize the camera eye frame with custom
 * user defined actions which requires to extend from the GrabberFrame
 * and implement: 1. Some of the performInteraction() methods on the
 * event types the new frame needs to support (see the CustomEyeFrame
 * example); and, 2. The InteractiveGrabber interface. //<>//
 *
 * Press ' ' to switch between the custom eye frame and the default one.
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.bias.addon.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.addon.*;
import remixlab.dandelion.geom.*;
import remixlab.proscene.*;

// 1. Action declaration
// We first declare the global action set the frame will support
public enum GlobalAction {
  ROTATE, 
  TRANSLATE, 
  ZOOM,
  CENTER
}


public enum MyMotionAction implements Action<GlobalAction> {
  ROTATE(GlobalAction.ROTATE), 
  TRANSLATE(GlobalAction.TRANSLATE);

  @Override
  public GlobalAction referenceAction() {
    return act;
  }

  @Override
  public String description() {
    return "A simple motion action";
  }

  GlobalAction  act;

  MyMotionAction(GlobalAction a) {
    act = a;
  }
}

public enum MyClickAction implements Action<GlobalAction> {
  ZOOM(GlobalAction.ZOOM),
  CENTER(GlobalAction.CENTER);

  @Override
  public GlobalAction referenceAction() {
    return act;
  }

  @Override
  public String description() {
    return "A simple click action";
  }

  GlobalAction  act;

  MyClickAction(GlobalAction a) {
    act = a;
  }
}

public class CustumEyeFrame extends GrabberFrame implements InteractiveGrabber<GlobalAction> {
  public CustumEyeFrame(Eye _eye) {
    super(_eye);
  }

  protected CustumEyeFrame(CustumEyeFrame otherFrame) {
    super(otherFrame);
    this.setAction(otherFrame.action());
  }

  @Override
  public CustumEyeFrame get() {
    return new CustumEyeFrame(this);
  }

  protected Action<GlobalAction> action;

  public GlobalAction referenceAction() {
    return action != null ? action.referenceAction() : null;
  }

  @Override
  public void setAction(Action<GlobalAction> a) {
    action = a;
  }

  @Override
  public Action<GlobalAction> action() {
    return action;
  }

  @Override
  public void performInteraction(DOF2Event event) {
    switch(referenceAction()) {
    case ROTATE:
      gestureArcball(event);
      break;
    case TRANSLATE:
      gestureTranslateXY(event);
      break;
    }
  }

  @Override
  public void performInteraction(ClickEvent event) {
    switch (referenceAction()) {
    case ZOOM:
      eye().interpolateToZoomOnPixel(event.x(), event.y());
      break;
    case CENTER:
      center();
      break;
    }
  }
}

Scene              scene;
CustumEyeFrame  eyeFrame;
GrabberFrame orig;
public void setup() {
  size(640, 360, P3D); 
  scene = new Scene(this);    

  orig = scene.eyeFrame();
  MotionBranch<GlobalAction, MyMotionAction, MyClickAction> mouseBranch = scene.mouseAgent().appendBranch("mouseBranch");
  mouseBranch.setClickBinding(LEFT, 1, MyClickAction.ZOOM);
  mouseBranch.setMotionBinding(LEFT, MyMotionAction.TRANSLATE);
  mouseBranch.setMotionBinding(RIGHT, MyMotionAction.ROTATE);
  eyeFrame = new CustumEyeFrame(scene.eye());
  scene.mouseAgent().addGrabber(eyeFrame, mouseBranch);
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