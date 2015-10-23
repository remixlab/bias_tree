/**************************************************************************************
 * ProScene (version 3.0.0)
 * Copyright (c) 2010-2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 * 
 * All rights reserved. Library that eases the creation of interactive scenes
 * in Processing, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.proscene;

import remixlab.bias.event.KeyboardEvent;
import remixlab.bias.event.KeyboardShortcut;
import remixlab.dandelion.core.AbstractScene;
import remixlab.bias.core.*;

/**
 * Proscene key-agent. A {@link remixlab.dandelion.branch.KeyboardAgent} specialization
 * which handles Processing key-events.
 * 
 * @see remixlab.dandelion.branch.KeyboardAgent
 * @see remixlab.proscene.MouseAgent
 * @see remixlab.proscene.DroidKeyAgent
 * @see remixlab.proscene.DroidTouchAgent
 */
public class KeyAgent extends Agent {
	public final int 	LEFT_KEY	= 37,
			 			RIGHT_KEY = 39, 
			 			UP_KEY = 38,
			 			DOWN_KEY = 40;
	//public static int LEFT_KEY	= PApplet.LEFT, RIGHT_KEY = PApplet.RIGHT, UP_KEY = PApplet.UP, DOWN_KEY = PApplet.DOWN;
	protected Scene scene;
	protected boolean				press, release, type;
	protected KeyboardEvent	currentEvent;

	/**
	 * Calls super on (scn,n) and sets default keyboard shortcuts.
	 * 
	 * @see #setDefaultBindings()
	 */
	public KeyAgent(Scene scn, String n) {
		super(scn.inputHandler(), n);
		scene = scn;
		addGrabber(scene);
		//TODO pending
		//setDefaultBindings();
	}
	
	@Override
	public boolean setDefaultGrabber(Grabber g) {
		if( g instanceof AbstractScene ) {
			System.err.println("No default keyboard agent grabber set. A scene cannot be set as a default keyboard agent input grabber.");
			return false;
		}
		return super.setDefaultGrabber(g);
	}
	
	@Override
	public boolean resetDefaultGrabber() {
		addGrabber(scene.eye().frame());
		return setDefaultGrabber(scene.eye().frame());
	}
	
	/**
	 * Returns the scene this object belongs to.
	 */
	public Scene scene() {
		return scene;
	}

	/**
	 * Processing keyEvent method to be registered at the PApplet's instance.
	 */
	public void keyEvent(processing.event.KeyEvent e) {
		press = e.getAction() == processing.event.KeyEvent.PRESS;
		release = e.getAction() == processing.event.KeyEvent.RELEASE;
		type = e.getAction() == processing.event.KeyEvent.TYPE;
		currentEvent = new KeyboardEvent(e.getKey(), e.getModifiers(), e.getKeyCode());
		if (press) {
			updateTrackedGrabber(currentEvent);
			handle(currentEvent);
		}
	}
	
	public static int keyCode(char key) {
		return java.awt.event.KeyEvent.getExtendedKeyCodeForChar(key);
	}
	
	public void removeBindings(GenericP5Frame frame) {
		frame.profile.removeKeyboardBindings();
	}
	
	public void setDefaultBindings(GenericP5Frame frame) {
		removeBindings(frame);
		frame.setKeyBinding('n', "align");
		frame.setKeyBinding('c', "center");
		frame.setKeyBinding(LEFT_KEY, "translateXNeg");
		frame.setKeyBinding(RIGHT_KEY, "translateXPos");
		frame.setKeyBinding(DOWN_KEY, "translateYNeg");
		frame.setKeyBinding(UP_KEY, "translateYPos");
		frame.profile.setKeyboardBinding(new KeyboardShortcut(BogusEvent.SHIFT, LEFT_KEY), "rotateXNeg");
		frame.profile.setKeyboardBinding(new KeyboardShortcut(BogusEvent.SHIFT, RIGHT_KEY), "rotateXPos");
		frame.profile.setKeyboardBinding(new KeyboardShortcut(BogusEvent.SHIFT, DOWN_KEY), "rotateYNeg");
		frame.profile.setKeyboardBinding(new KeyboardShortcut(BogusEvent.SHIFT, UP_KEY), "rotateYPos");	
		frame.setKeyBinding('z', "rotateZNeg");
		frame.setKeyBinding(BogusEvent.SHIFT, 'z', "rotateZPos");
	}
	
	/*
	public void removeBindings() {
		for( Grabber grabber : this.grabbers() ) 
			if(grabber instanceof GenericP5Frame)
				removeBindings((GenericP5Frame)grabber);
	}
	
	public void setDefaultBindings() {
		for( Grabber grabber : this.grabbers() ) 
			if(grabber instanceof GenericP5Frame)
				setDefaultBindings((GenericP5Frame)grabber);
	}
	*/
	
	/*
	public void removeBindings(GenericP5Frame frame) {
		frame.removeKeyBindings();
	}
	*/
	
	public void setBinding(GenericP5Frame frame, int vkey, String methodName) {
		frame.profile.setKeyboardBinding(new KeyboardShortcut(vkey), methodName);
	}
	
	public void setBinding(GenericP5Frame frame, char key, String methodName) {
		frame.profile.setKeyboardBinding(new KeyboardShortcut(KeyAgent.keyCode(key)), methodName);
	}
	
	public void setBinding(GenericP5Frame frame, Object object, int vkey, String methodName) {
		frame.profile.setKeyboardBinding(object, new KeyboardShortcut(vkey), methodName);
	}
	
	public void setBinding(Object object, GenericP5Frame frame, char key, String methodName) {
		frame.profile.setKeyboardBinding(object, new KeyboardShortcut(KeyAgent.keyCode(key)), methodName);
	}
	
	public boolean hasBinding(GenericP5Frame frame, int vkey) {
		return frame.profile.hasBinding(new KeyboardShortcut(vkey));
	}
	
	public boolean hasBinding(GenericP5Frame frame, char key) {
		return frame.profile.hasBinding(new KeyboardShortcut(KeyAgent.keyCode(key)));
	}
	
	public void removeBinding(GenericP5Frame frame, int vkey) {
		frame.profile.removeBinding(new KeyboardShortcut(vkey));
	}
	
	public void removeBinding(GenericP5Frame frame, char key) {
		frame.profile.removeBinding(new KeyboardShortcut(KeyAgent.keyCode(key)));
	}
	
	public void setBinding(GenericP5Frame frame, int mask, char key, String methodName) {
		frame.profile.setKeyboardBinding(new KeyboardShortcut(mask, KeyAgent.keyCode(key)), methodName);
	}
	
	public void setBinding(Object object, GenericP5Frame frame, int mask, char key, String methodName) {
		frame.profile.setKeyboardBinding(object, new KeyboardShortcut(mask, KeyAgent.keyCode(key)), methodName);
	}
	
	public boolean hasBinding(GenericP5Frame frame, int mask, char key) {
		return frame.profile.hasBinding(new KeyboardShortcut(mask, KeyAgent.keyCode(key)));
	}
	
	public void removeBinding(GenericP5Frame frame, int mask, char key) {
		frame.profile.removeBinding(new KeyboardShortcut(mask, KeyAgent.keyCode(key)));
	}
}