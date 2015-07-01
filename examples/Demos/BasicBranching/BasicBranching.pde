/**
 * Basic Branching.
 * by Jean Pierre Charalambos.
 * 
 * Creating agent branches to control a specific object instance
 * (in this case the torus with its axes drawn) differently than it's
 * done with the others.
 *
 * To set the input grabber object (the object targeted for interaction)
 * for both the mouse and the keyboard press: 
 *
 * Press 'u' to shift among two single toruses
 * Press 'v' to reset the default grabber (the scene.eyeFrame)
 *
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.bias.core.*;
import remixlab.bias.branch.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

Scene scene;
InteractiveTorus [] toruses;
MotionBranch<MotionAction, DOF2Action, ClickAction> iMotionBranch;
KeyboardBranch<MotionAction, KeyboardAction> iKeyBranch;

//Choose one of P3D for a 3D scene, or P2D or JAVA2D for a 2D scene
String renderer = P3D;
	
void setup() {
  size(640, 360, renderer);  
  scene = new Scene(this);
  scene.setRadius(150);
  scene.showAll();
  
  toruses = new InteractiveTorus[5];
  for (int i = 0; i < toruses.length; i++)
    toruses[i] = new InteractiveTorus(scene);
    
  iMotionBranch = scene.motionAgent().appendBranch();
  //iMotionBranch = scene.mouseAgent().frameBranch().get();//deep copy may be possible
  iMotionBranch.setMotionBinding(LEFT, DOF2Action.TRANSLATE);
  iMotionBranch.setMotionBinding(RIGHT, DOF2Action.ROTATE);
  iMotionBranch.addGrabber(toruses[3].iFrame);
  //scene.mouseAgent().addGrabber(toruses[3].iFrame, iMotionBranch);//same as prev line
  
  // 2. Creating a (keyboard) branch
  //iKeyBranch = new KeyboardBranch<MotionAction, KeyboardAction>(scene.keyboardAgent(), "my_key_branch");
  iKeyBranch = scene.keyboardAgent().appendBranch();
  iKeyBranch.setBinding('y', KeyboardAction.TRANSLATE_Y_POS);
  iKeyBranch.setBinding(Event.SHIFT, 'y', KeyboardAction.TRANSLATE_Y_NEG);
  iKeyBranch.setBinding('x', KeyboardAction.TRANSLATE_X_POS);
  iKeyBranch.setBinding(Event.SHIFT, 'x', KeyboardAction.TRANSLATE_X_NEG);
  //iKeyBranch.addGrabber(toruses[3].iFrame);
  scene.keyboardAgent().addGrabber(toruses[3].iFrame, iKeyBranch);//same as prev line
}

void draw() {
  background(0);
  // A. 3D drawing
  for (int i = 0; i < toruses.length; i++)
    toruses[i].draw(i==3);
}

void keyPressed() {
  if ( key == 'u' ) {
    scene.keyboardAgent().shiftDefaultGrabber(toruses[2].iFrame, toruses[3].iFrame);
    scene.motionAgent().shiftDefaultGrabber(toruses[2].iFrame, toruses[3].iFrame);
  }
  if ( key == 'v' ) {
    scene.keyboardAgent().resetDefaultGrabber();
    scene.motionAgent().resetDefaultGrabber();
  }
}