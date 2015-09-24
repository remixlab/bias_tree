/**
 * Lamp by Jean Pierre Charalambos.
 * 
 * This class is part of the Luxo example.
 *
 * Any object that needs to be "pickable" (such as the Caja), should be
 * attached to its own InteractiveFrame. That's all there is to it.
 *
 * The built-in picking proscene mechanism actually works as follows. At
 * instantiation time all InteractiveFrame objects are added to a mouse
 * grabber pool. Scene parses this pool every frame to check if the mouse
 * grabs a InteractiveFrame by projecting its origin onto the screen.
 * If the mouse position is close enough to that projection (default
 * implementation defines a 10x10 pixel square centered at it), the object
 * will be picked. 
 *
 * Override InteractiveFrame.checkIfGrabsMouse if you need a more
 * sophisticated picking mechanism.
 *
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

public class Lamp {
  Scene scene;
  InteractiveFrame [] frameArray;

  Camera cam;

  Lamp(Scene s) {
    scene =  s;
    frameArray = new InteractiveFrame[4];
    
    for (int i = 0; i < 4; ++i)
      frameArray[i] = new InteractiveFrame(scene, i>0 ? frameArray[i-1] : null);

    // Initialize frames
    frame(1).setTranslation(0, 0, 8); // Base height
    frame(2).setTranslation(0, 0, 50);  // Arm length
    frame(3).setTranslation(0, 0, 50);  // Arm length

    frame(1).setRotation(new Quat(new Vec(1.0f, 0.0f, 0.0f), 0.6f));
    frame(2).setRotation(new Quat(new Vec(1.0f, 0.0f, 0.0f), -2.0f));
    frame(3).setRotation(new Quat(new Vec(1.0f, -0.3f, 0.0f), -1.7f));
    
    //graphics handers
    frame(0).addGraphicsHandler(this, "drawBase");
    frame(1).addGraphicsHandler(this, "drawPivotArm");
    frame(2).addGraphicsHandler(this, "drawPivotArm");
    frame(3).addGraphicsHandler(this, "drawHead");

    // Set frame constraints
    WorldConstraint baseConstraint = new WorldConstraint();
    baseConstraint.setTranslationConstraint(AxisPlaneConstraint.Type.PLANE, new Vec(0.0f, 0.0f, 1.0f));
    baseConstraint.setRotationConstraint(AxisPlaneConstraint.Type.AXIS, new Vec(0.0f, 0.0f, 1.0f));
    frame(0).setConstraint(baseConstraint);

    LocalConstraint XAxis = new LocalConstraint();
    XAxis.setTranslationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new Vec(0.0f, 0.0f, 0.0f));
    XAxis.setRotationConstraint   (AxisPlaneConstraint.Type.AXIS, new Vec(1.0f, 0.0f, 0.0f));
    frame(1).setConstraint(XAxis);
    frame(2).setConstraint(XAxis);

    LocalConstraint headConstraint = new LocalConstraint();
    headConstraint.setTranslationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new Vec(0.0f, 0.0f, 0.0f));
    frame(3).setConstraint(headConstraint);
  }

  public void drawBase(PGraphics pg) {
    drawCone(pg, 0, 3, 15, 15, 30);
    drawCone(pg, 3, 5, 15, 13, 30);
    drawCone(pg, 5, 7, 13, 1, 30);
    drawCone(pg, 7, 9, 1, 1, 10);
  }

  public void drawArm(PGraphics pg) {
    pg.translate(2, 0, 0);
    drawCone(pg, 0, 50, 1, 1, 10);
    pg.translate(-4, 0, 0);  
    drawCone(pg, 0, 50, 1, 1, 10);    
    pg.translate(2, 0, 0);
  }

  public void drawHead(PGraphics pg) {
    drawCone(pg, -2, 6, 4, 4, 30);
    drawCone(pg, 6, 15, 4, 17, 30);
    drawCone(pg, 15, 17, 17, 17, 30);
    pg.spotLight(155, 255, 255, 0, 0, 0, 0, 0, 1, THIRD_PI, 1);
  }

  public void drawCylinder(PGraphics pg) {
    pg.pushMatrix();
    pg.rotate(HALF_PI, 0, 1, 0);
    drawCone(pg, -5, 5, 2, 2, 20);
    pg.popMatrix();
  }
  
  public void drawPivotArm(PGraphics pg) {
    drawCylinder(pg);
    drawArm(pg);
  }

  public void drawCone(PGraphics pg, float zMin, float zMax, float r1, float r2, int nbSub) {
    pg.translate(0.0f, 0.0f, zMin);
    drawCone(pg, nbSub, 0, 0, r1, r2, zMax-zMin);
    pg.translate(0.0f, 0.0f, -zMin);
  }
  
  public void drawCone(PGraphics pg, int detail, float x, float y, float r1, float r2, float h) {
    pg.pushStyle();
    float firstCircleX[] = new float[detail + 1];
    float firstCircleY[] = new float[detail + 1];
    float secondCircleX[] = new float[detail + 1];
    float secondCircleY[] = new float[detail + 1];

    for (int i = 0; i <= detail; i++) {
      float a1 = PApplet.TWO_PI * i / detail;
      firstCircleX[i] = r1 * cos(a1);
      firstCircleY[i] = r1 * sin(a1);
      secondCircleX[i] = r2 * cos(a1);
      secondCircleY[i] = r2 * sin(a1);
    }

    pg.pushMatrix();
    pg.translate(x, y);
    pg.beginShape(PApplet.QUAD_STRIP);
    for (int i = 0; i <= detail; i++) {
      pg.vertex(firstCircleX[i], firstCircleY[i], 0);
      pg.vertex(secondCircleX[i], secondCircleY[i], h);
    }
    pg.endShape();
    pg.popMatrix();
    pg.popStyle();
  }

  public void setColor(boolean selected) {
    if (selected)
      fill(200, 200, 0);    
    else
      fill(200, 200, 200);
  }

  public InteractiveFrame frame(int i) {
    return frameArray[i];
  }
}