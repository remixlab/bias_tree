/**
 * Keyboard Customization.
 * by Jean Pierre Charalambos.
 * 
 * This example shows keyboard customization in proscene.
 *
 * Here we illustrate scene, eye and frame control behavior. That of your custom objects is
 * covered in (yet to come) another example.
 *
 * Press 'i' to switch the interaction between the camera frame and the interactive frame.
 * Press ' ' (the space bar) to randomly change the keyboard scene bindings.
 * Press 'u' to restore the default keyboard bindings
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.addon.*;
import remixlab.dandelion.addon.Constants.*;

Scene scene;
KeyboardAgent keyboard;
InteractiveFrame iFrame;

//Choose one of P3D for a 3D scene, or P2D or JAVA2D for a 2D scene
String renderer = P3D;

void setup() {
  size(640, 360, renderer);
  scene = new Scene(this);
  keyboard = scene.keyboardAgent();
  iFrame = new InteractiveFrame(scene);
  iFrame.translate(30, 30);
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
  else if (iFrame.grabsInput()) {
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
  // 1. Scene
  // 1a. Randomless:
  keyboard.setBinding(Event.CTRL, 'a', SceneAction.TOGGLE_GRID_VISUAL_HINT);
  // 1b. Random
  keyboard.setBinding('a', randomAction(SceneAction.class)); //<>//
  // 2. Frame:
  keyboard.setBinding(Target.FRAME, 'x', KeyboardAction.ROTATE_X_POS);
  keyboard.setBinding(Target.FRAME, Event.SHIFT, 'x', KeyboardAction.ROTATE_X_NEG);
  keyboard.setBinding(Target.FRAME, 'y', KeyboardAction.ROTATE_Y_POS);
  keyboard.setBinding(Target.FRAME, Event.SHIFT, 'y', KeyboardAction.ROTATE_Y_NEG);
  keyboard.setBinding(Target.FRAME, 'z', KeyboardAction.ROTATE_Z_POS);
  keyboard.setBinding(Target.FRAME, Event.SHIFT, 'z', KeyboardAction.ROTATE_Z_NEG);
  // 3. Eye
  keyboard.setBinding(Target.EYE, 'x', KeyboardAction.ROTATE_X_POS);
  keyboard.setBinding(Target.EYE, Event.SHIFT, 'x', KeyboardAction.ROTATE_X_NEG);
  keyboard.setBinding(Target.EYE, 'y', KeyboardAction.ROTATE_Y_POS);
  keyboard.setBinding(Target.EYE, Event.SHIFT, 'y', KeyboardAction.ROTATE_Y_NEG);
  keyboard.setBinding(Target.EYE, 'z', KeyboardAction.ROTATE_Z_POS);
  keyboard.setBinding(Target.EYE, Event.SHIFT, 'z', KeyboardAction.ROTATE_Z_NEG);    
}

public void keyPressed() {
  if(key == ' ')
    setExoticCustomization();
  if(key == 'u')
    keyboard.setDefaultBindings();
  if ( key == 'i')
    keyboard.shiftDefaultGrabber(scene.eyeFrame(), iFrame);
}