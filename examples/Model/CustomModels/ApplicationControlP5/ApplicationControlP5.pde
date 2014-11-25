import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.proscene.*;

public class RectModel extends ModelObject {
  float edge = 30;
  color colour = color(255, 0, 0);
  public RectModel(Scene scn) {
    super(scn);
    update();
  }
  
  void update() {
    setShape(createShape(RECT, -edge/2, -edge/2, edge, edge));
    shape().setFill(color(colour));
  }
}

//same as iAppCtrl, but with a ModelObject
public class ModelEllipse extends ModelObject {
  float radiusX = 30, radiusY = 30;
  color colour = color(255, 0, 0);
  public ModelEllipse(Scene scn) {
    super(scn);
    update();
  }
  
  void update() {
    setShape(createShape(ELLIPSE, -radiusX, -radiusY, 2*radiusX, 2*radiusY));
    shape().setFill(color(colour));
  }
}

int w = 200;
int h = 120;
int oX = 640-w;
int oY = 360-h;
PGraphics ctrlCanvas;
Scene ctrlScene;
public PShape eShape;
ModelEllipse e;
RectModel r;
PGraphics canvas;
Scene scene;
boolean showAid = true;

void setup() {
  size(640, 360, P2D);

  canvas = createGraphics(640, 360, P3D); 
  scene = new Scene(this, canvas);

  ctrlCanvas = createGraphics(w, h, P2D);
  ctrlScene = new Scene(this, ctrlCanvas, oX, oY);
  ctrlScene.setAxesVisualHint(false);
  ctrlScene.setGridVisualHint(false);
  e = new ModelEllipse(ctrlScene);
  r = new RectModel(ctrlScene);
  ctrlScene.removeModel(r);//re-add me when implementing me
  ctrlScene.motionAgent().addInPool(e);
  ctrlScene.motionAgent().addInPool(r);
}

void mouseClicked() {
  if (ctrlScene.mouseAgent().trackedGrabber()==e) {
    e.colour = color(color(random(0, 255), random(0, 255), random(0, 255), 125));
    e.update();
  }
  if (ctrlScene.mouseAgent().trackedGrabber()==r) {
    r.colour = color(color(random(0, 255), random(0, 255), random(0, 255), 125));
    r.update();
  }
}

void mouseDragged() {
  if (ctrlScene.mouseAgent().trackedGrabber()==e) {
    e.radiusX += mouseX -pmouseX;
    e.radiusY += mouseY -pmouseY;
    e.update();
  }
  if (ctrlScene.mouseAgent().trackedGrabber()==r) {
    r.edge += mouseX -pmouseX;
    r.update();
  }
}

void draw() {
  handleMouse();
  canvas.beginDraw();
  scene.beginDraw();
  canvas.background(255);
  canvas.fill(e.colour);
  scene.drawTorusSolenoid((int)map(PI*e.radiusX*e.radiusY, 20, w*h, 2, 50), 100, e.radiusY, e.radiusX);
  scene.endDraw();
  canvas.endDraw();
  image(canvas, scene.originCorner().x(), scene.originCorner().y());

  if (showAid) {
    ctrlCanvas.beginDraw();
    ctrlScene.beginDraw();
    ctrlCanvas.background(125, 125, 125, 125);
    ctrlScene.drawModels();
    ctrlScene.endDraw();
    ctrlCanvas.endDraw();
    image(ctrlCanvas, ctrlScene.originCorner().x(), ctrlScene.originCorner().y());
  }
}

void handleMouse() {
  scene.enableMotionAgent();
  ctrlScene.disableMotionAgent();
  if ((oX < mouseX) && (oY < mouseY) && showAid) {
    scene.disableMotionAgent();
    ctrlScene.enableMotionAgent();
  }
}

void keyPressed() {
  if (key == ' ')
    showAid = !showAid;
}