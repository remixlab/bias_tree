/**
 * Mouse and Keyboard Customization.
 * by Jean Pierre Charalambos.
 * 
 * This example shows proscene mouse and keyboard customization.
 *
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;
import remixlab.dandelion.agent.*;

Scene scene;
InteractiveFrame iFrame;
boolean exotic = true;

//Choose one of P3D for a 3D scene, or P2D or JAVA2D for a 2D scene
String renderer = P3D;

void setup() {
  size(640, 360, renderer);
  scene = new Scene(this);
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
  // Draw an axis using the Scene static function
  scene.drawAxes(20);
  // Draw a second box attached to the interactive frame
  if (iFrame.grabsInput(scene.motionAgent())) {
    fill(255, 0, 0);
    scene.drawTorusSolenoid();
  }
  else {
    fill(0, 0, 255, 150);
    scene.drawTorusSolenoid();
  }  
  popMatrix();
}

public void setExoticCustomization() {
  //eye
  scene.setMouseButtonBinding(Target.EYE, CENTER, DOF2Action.ZOOM_ON_ANCHOR);
  scene.setMouseButtonBinding(Target.EYE, LEFT, DOF2Action.TRANSLATE);
  scene.setMouseButtonBinding(Target.EYE, RIGHT, DOF2Action.ROTATE_CAD);
  scene.setMouseClickBinding(Target.EYE, Event.SHIFT, CENTER, 2, ClickAction.TOGGLE_AXES_VISUAL_HINT);
  scene.setMouseClickBinding(Target.EYE, Event.SHIFT, LEFT, 2, ClickAction.TOGGLE_PICKING_VISUAL_HINT);
  //frame
  scene.setMouseButtonBinding(Target.FRAME, LEFT, DOF2Action.TRANSLATE);
  scene.setMouseButtonBinding(Target.FRAME, CENTER, DOF2Action.SCALE);
  scene.setMouseWheelBinding(Target.FRAME, DOF1Action.ZOOM);
  scene.setMouseButtonBinding(Target.FRAME, RIGHT, DOF2Action.ROTATE_X);
  //keyboard
  scene.setKeyboardShortcut('g',KeyboardAction.TOGGLE_AXES_VISUAL_HINT);
  scene.setKeyboardShortcut(Event.CTRL,java.awt.event.KeyEvent.VK_G,KeyboardAction.TOGGLE_GRID_VISUAL_HINT);
}

public void keyPressed() {
  if ( key != ' ')
    return;
  if(exotic) {
    scene.setMouseAsArcball();
    scene.setDefaultKeyboardShortcuts();
    exotic = false;
  }
  else {
    setExoticCustomization();
    exotic = true;
  }
}