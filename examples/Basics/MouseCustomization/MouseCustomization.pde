/**
 * Mouse Customization.
 * by Jean Pierre Charalambos.
 * 
 * This example shows pinking modes and mouse customization in proscene.
 *
 * There are two picking modes in proscene: MOVE and CLICK. In the MOVE mode objects are
 * picked with a mouse move gesture. In CLICK mode objetcs are picked with a mouse click
 * gesture. In addition, mouse actions may be bound to a mouse move gesture
 * (mouse.setGestureBinding()) or to a mouse  press-drag-release gesture
 * (mouse.setButtonBinding). The mouse methods moveToArcBall() and dragToArcball() provide
 * convenient default mouse move and button bindings, resp.
 *
 * Press 'u' to select MOVE picking mode and activate dragToArcball bindings.
 * Press 'v' to select CLICK picking mode and activate moveToArcball bindings.
 * Press 'i' to switch the interaction between the camera frame and the interactive frame.
 * Press ' ' (the space bar) to randomly change the mouse bindings and keyboard shortcuts.
 * Press 'q' to display customization details.
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;
import remixlab.dandelion.agent.*;

Scene scene;
MouseAgent mouse;
InteractiveFrame iFrame;

//Choose one of P3D for a 3D scene, or P2D or JAVA2D for a 2D scene
String renderer = P3D;

void setup() {
  size(640, 360, renderer);
  scene = new Scene(this);
  mouse = scene.mouseAgent();
  iFrame = new InteractiveFrame(scene);
  iFrame.translate(new Vec(30, 30));
  setExoticCustomization();
}

void draw() {
  background(0);
  fill(204, 102, 0, 150);
  scene.drawTorusSolenoid();
  // Save the current model view matrix
  pushMatrix();
  // Multiply matrix to get in the frame coordinate system.
  scene.applyModelView(iFrame.matrix());  //Option 1. or,
  //iFrame.applyTransformation(); //Option 2.
  // Draw axes using the Scene static function
  scene.drawAxes(20);
  // Draw a second torus attached to the interactive frame
  if (scene.motionAgent().defaultGrabber() == iFrame) {
    fill(0, 255, 255);
    scene.drawTorusSolenoid();
  }
  else if (iFrame.grabsInput(scene.motionAgent())) {
    fill(255, 0, 0);
    scene.drawTorusSolenoid();
  }
  else {
    fill(0, 0, 255, 150);
    scene.drawTorusSolenoid();
  }
  popMatrix();
}

// http://stackoverflow.com/questions/1972392/java-pick-a-random-value-from-an-enum/14257525#14257525
public <T extends Enum<?>> T randomAction(Class<T> actionClass) {
  int x = int(random(actionClass.getEnumConstants().length));
  return actionClass.getEnumConstants()[x];
}

public void setExoticCustomization() {
  // 1. Randomless:
  mouse.removeButtonBinding(Target.EYE, CENTER);
  mouse.setButtonBinding(Target.EYE, Event.SHIFT, LEFT, DOF2Action.TRANSLATE); 
  mouse.setButtonBinding(Target.FRAME, RIGHT, DOF2Action.TRANSLATE);
  mouse.setClickBinding(Target.FRAME, Event.SHIFT, RIGHT, 2, ClickAction.ALIGN_FRAME);  
  mouse.setWheelBinding(Target.FRAME, Event.CTRL, DOF1Action.ZOOM_ON_ANCHOR);  
 
  // 2. Random
  mouse.setButtonBinding(Target.FRAME, Event.CTRL, LEFT, randomAction(DOF2Action.class));
  mouse.setButtonBinding(Target.EYE, RIGHT, randomAction(DOF2Action.class));
  mouse.setClickBinding(Target.EYE, LEFT, randomAction(ClickAction.class));
  mouse.setWheelBinding(Target.EYE, randomAction(DOF1Action.class));
}

public void keyPressed() {
  if(key == ' ')
    setExoticCustomization();
  if(key == 'u') {
    mouse.setPickingMode(MouseAgent.PickingMode.MOVE);
    mouse.dragToArcball();
  }
  if(key == 'v') {
    mouse.setPickingMode(MouseAgent.PickingMode.CLICK);
    mouse.moveToArcball();
  }
  if(key == 'q') {
    String info;
    info = "RIGHT mouse button + 2 clicks, ";
    info += mouse.hasClickBinding(Target.EYE, Event.SHIFT, RIGHT, 2) ? "define an EYE binding\n" : "isn't a binding\n";
    info += "ROTATE_X action ";
    info += mouse.isActionBound(Target.FRAME, DOF2Action.ROTATE_X) ? "bound to the frame\n" : "not bound\n";
    info += "CTRL + LEFT button -> " + mouse.buttonAction(Target.FRAME, Event.CTRL, LEFT) + " frame\n";
    println(info);
  }
  if ( key == 'i')
    scene.motionAgent().setDefaultGrabber(scene.motionAgent().defaultGrabber() == iFrame ? scene.eye().frame() : iFrame);
}