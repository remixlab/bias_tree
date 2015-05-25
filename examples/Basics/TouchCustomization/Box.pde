/**
 * Box. 
 * by Jean Pierre Charalambos.
 * 
 */

public class Box {
  Scene scene;
  public InteractiveFrame iFrame;
  float w, h, d;
  int c;

  public Box(Scene scn, InteractiveFrame iF) {
    scene = scn;
    iFrame = iF;
    setSize();
    setColor();
  }

  public Box(Scene scn) {
    scene = scn;
    iFrame = new InteractiveFrame(scn);
    setSize();
    setColor();    
    setPosition();
  }

  public void draw() {
    draw(false);
  }

  public void draw(boolean drawAxes) {
    pushMatrix();
    iFrame.applyWorldTransformation();
    if (drawAxes)
      scene.drawAxes(max(w, h, d)*1.3f);
    noStroke();
    if (scene.motionAgent().isInputGrabber(iFrame))
      fill(255, 0, 0);
    else
      fill(getColor());
    box(w, h, d);
    popMatrix();
  }

  public void setSize() {
    w = 50;
    h = 50;
    d = 50;
    iFrame.setGrabsInputThreshold(50, true);
  }

  public void setSize(float myW, float myH, float myD) {
    w=myW; 
    h=myH; 
    d=myD;
    iFrame.setGrabsInputThreshold(max(w,h,d), true);
  }  

  public int getColor() {
    return c;
  }

  public void setColor() {
    c = color(random(0, 255), random(0, 255), random(0, 255));
  }

  public void setColor(int myC) {
    c = myC;
  }

  public Vec getPosition() {
    return iFrame.position();
  }  

  public void setPosition() {
    iFrame.setPosition(new Vec(0, 0, 0));
  }

  public void setPosition(Vec pos) {
    iFrame.setPosition(pos);
  }

  public Quat getOrientation() {
    return (Quat)iFrame.orientation();
  }

  public void setOrientation(Vec v) {
    Vec to = Vec.subtract(v, iFrame.position()); 
    iFrame.setOrientation(new Quat(new Vec(0, 1, 0), to));
  }
}