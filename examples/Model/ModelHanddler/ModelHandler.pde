package picking;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

public class HandlerPickingShapes extends PApplet {	
	Scene scene;
	Model[] models;
	int num_vertex = 50;
	int color;

	@Override
	public void setup() {
	  size(640, 360, P3D);
	  //Scene instantiation
	  scene = new Scene(this);
	  models = new Model[10];
	  for(int i = 0; i < models.length; i++) {
	    models[i] = new Model(scene);
	    models[i].addGraphicsHandler(this, "drawBox");
	    models[i].translate(10*i,10*i,10*i);
	  }
	  smooth();
	}
	
	@Override
	public void draw() {
		background(0);
  }
	
	public void drawBox(PGraphics pg) {
		fill(255,0,255);
	  pg.box(20, 15, 30);
	}	
}
