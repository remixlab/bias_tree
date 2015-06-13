/**
 * Basic Branching.
 * by Jean Pierre Charalambos.
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
    
  iMotionBranch = new MotionBranch<MotionAction, DOF2Action, ClickAction>(scene.mouseAgent(), "my_motion_branch");
  iMotionBranch.setMotionBinding(LEFT, DOF2Action.TRANSLATE);
  iMotionBranch.setMotionBinding(RIGHT, DOF2Action.ROTATE);
  //scene.mouseAgent().removeGrabber(toruses[3].iFrame);
  //scene.mouseAgent().appendBranch(iMotionBranch);
  // following line calls previous two:
  iMotionBranch.addGrabber(toruses[3].iFrame);
  //scene.mouseAgent().addGrabber(toruses[3].iFrame, iMotionBranch);//same as prev line
  
  iKeyBranch = scene.keyboardAgent().frameBranch().get();
  iKeyBranch.setBinding(UP, KeyboardAction.TRANSLATE_Y_POS);
  iKeyBranch.setBinding(DOWN, KeyboardAction.TRANSLATE_Y_NEG);
  iKeyBranch.setBinding(RIGHT, KeyboardAction.TRANSLATE_X_POS);
  iKeyBranch.setBinding(LEFT, KeyboardAction.TRANSLATE_X_NEG);
  //scene.keyboardAgent().removeGrabber(toruses[3].iFrame);
  //scene.keyboardAgent().appendBranch(iKeyBranch);
  // following line calls previous two:
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
  if ( key == 't' )
    scene.keyboardAgent().setDefaultGrabber(toruses[3].iFrame);
  if ( key == 'u' )
    scene.keyboardAgent().setDefaultGrabber(toruses[2].iFrame);
  if ( key == 'v' )
    scene.keyboardAgent().resetDefaultGrabber();
}