import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.core.Constants.*;
import remixlab.dandelion.agent.MultiTouchAgent.Gestures;
import remixlab.dandelion.agent.*;

Scene scene;
DroidTouchAgent touch;
float x,y,z;
Box box;

void setup() {
  scene = new Scene(this);
  touch = scene.touchAgent();
  box = new Box(scene);
  frameRate(100);
}

public String sketchRenderer() {
  return P3D; 
}

void draw() {
  background(0);
  lights();
  
  scene.beginScreenDrawing();  
  text(frameRate, 5, 17);
  scene.endScreenDrawing();    
  box.draw();
}

public void keyPressed() {
  touch.removeAllGestureBinding(Target.FRAME);
  touch.removeAllGestureBinding(Target.EYE);
  if(key == ' ')  //Default
    touch.setDefaultBinding();
  if(key == 't') {  //Turn&Roll
    touch.setGestureBinding(Target.FRAME, Gestures.DRAG_TWO_ID, DOF2Action.TRANSLATE);
    touch.setGestureBinding(Target.FRAME, Gestures.PINCH_TWO_ID,  DOF1Action.TRANSLATE_Z);
    touch.setGestureBinding(Target.FRAME, Gestures.DRAG_ONE_ID, DOF2Action.ROTATE);
    touch.setGestureBinding(Target.FRAME, Gestures.TURN_TWO_ID, DOF1Action.ROTATE_Z);
  }
  if(key == 's') {  //STICKY TOOLS
    touch.setGestureBinding(Target.FRAME, Gestures.DRAG_ONE_ID, DOF2Action.TRANSLATE);
    touch.setGestureBinding(Target.FRAME, Gestures.PINCH_TWO_ID,  DOF1Action.TRANSLATE_Z);
    touch.setGestureBinding(Target.FRAME, Gestures.OPPOSABLE_THREE_ID, DOF2Action.ROTATE);
    touch.setGestureBinding(Target.FRAME, Gestures.TURN_TWO_ID, DOF1Action.ROTATE_Z);
  }
  if ( key == 'd'){
    touch.setGestureBinding(Target.EYE, Gestures.DRAG_TWO_ID, DOF2Action.TRANSLATE);
    touch.setGestureBinding(Target.EYE, Gestures.PINCH_TWO_ID,  DOF1Action.TRANSLATE_Z);
    touch.setGestureBinding(Target.EYE, Gestures.DRAG_ONE_ID, DOF2Action.ROTATE);
  }
}

public boolean dispatchTouchEvent(android.view.MotionEvent event) {
  //Call the method to control the agent
  ((DroidTouchAgent)scene.motionAgent()).touchEvent(event);
  return super.dispatchTouchEvent(event);        // pass data along when done!
}