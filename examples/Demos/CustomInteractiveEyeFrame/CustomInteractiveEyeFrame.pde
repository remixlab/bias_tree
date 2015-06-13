import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.bias.branch.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.proscene.*;

public enum GlobalAction {
  ROTATE, 
  TRANSLATE, 
  ZOOM
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
  ZOOM(GlobalAction.ZOOM);

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

public class CustomMouseBranch extends MotionBranch<GlobalAction, MyMotionAction, MyClickAction> {
  public CustomMouseBranch(MouseAgent parent, String n) {
    super(parent, n);
    setClickBinding(LEFT, 1, MyClickAction.ZOOM);
    setMotionBinding(LEFT, MyMotionAction.TRANSLATE);
    setMotionBinding(RIGHT, MyMotionAction.ROTATE);
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
      println("to be implemented");
      break;
    }
  }
}

Scene              scene;
CustomMouseBranch  mouseBranch;
CustumEyeFrame  eyeFrame;
GrabberFrame orig;
public void setup() {
  size(640, 360, P3D); 
  scene = new Scene(this);    

  orig = scene.eye().frame();
  mouseBranch = new CustomMouseBranch(scene.mouseAgent(), "my_mouse");
  eyeFrame = new CustumEyeFrame(scene.eye());
  scene.mouseAgent().addGrabber(eyeFrame, mouseBranch);
  scene.camera().setFieldOfView((float) Math.PI / 3.0f);

  if (scene.motionAgent().branch(eyeFrame) == mouseBranch)
    println("validating agent.branch(1)");

  if (scene.mouseAgent().branch(scene.eye().frame()) == scene.mouseAgent().eyeBranch())
    println("validating agent.branch(2)");

  if (scene.mouseAgent().branch(scene.eye().frame()) != scene.mouseAgent().frameBranch()) {
    println("validating agent.branch(3)");
  }      

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