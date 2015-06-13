import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.proscene.*;

public class CustumEyeFrame extends GrabberFrame {
  public CustumEyeFrame(Eye _eye) {
    super(_eye);
  }

  protected CustumEyeFrame(CustumEyeFrame otherFrame) {
    super(otherFrame);
  }

  @Override
  public CustumEyeFrame get() {
    return new CustumEyeFrame(this);
  }

  @Override
  public void performInteraction(DOF2Event event) {
    if(event.id() == LEFT)
      gestureArcball(event);
    if(event.id() == RIGHT)
      gestureTranslateXY(event);
  }

  @Override
  public void performInteraction(ClickEvent event) {
    println("to be implemented");
  }
}

Scene              scene;
CustumEyeFrame  eyeFrame;
GrabberFrame orig;
public void setup() {
  size(640, 360, P3D); 
  scene = new Scene(this);
  orig = scene.eye().frame();
  eyeFrame = new CustumEyeFrame(scene.eye());
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
    if ( scene.eye().frame() == orig ) {
      println("setting custom eye");
      scene.eye().setFrame(eyeFrame);
    } else {
      println("resetting to orig eye frame");
      scene.eye().setFrame(orig);
    }
  }
  if ( key == 'p' ) {
    print("pos: ");
    scene.eye().frame().position().print();
    println("frame magnitude: " + scene.eye().frame().magnitude());
  }
  if ( key == 'q' ) {
    print("anchor: ");
    scene.eye().anchor().print();
    print("anchor projectedCoordinatesOf: ");
    scene.eye().projectedCoordinatesOf(scene.eye().anchor()).print();
  }
}