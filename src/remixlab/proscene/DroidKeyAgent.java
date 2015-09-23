/**************************************************************************************
 * ProScene (version 2.1.0)
 * Copyright (c) 2010-2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 * 
 * All rights reserved. Library that eases the creation of interactive scenes
 * in Processing, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.proscene;

import remixlab.bias.event.KeyboardEvent;
import remixlab.dandelion.branch.KeyboardAgent;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * Proscene {@link remixlab.dandelion.agent.KeyboardAgent}.
 */
public class DroidKeyAgent extends KeyboardAgent {
	protected Scene scene; 
	protected KeyboardEvent	currentEvent;
	
	public DroidKeyAgent(Scene scn, String n) {
		super(scn, n);
		scene = scn;
		setDefaultBindings();
	}

	/**
	 * Processing keyEvent method to be registered at the PApplet's instance.
	 */
	public void keyEvent(processing.event.KeyEvent e) {
		if (e.getAction() == processing.event.KeyEvent.PRESS) { 
			if (e.getKeyCode() == android.view.KeyEvent.KEYCODE_MENU) { 
				Object context = scene.pApplet(); 
				InputMethodManager imm = (InputMethodManager) ((Context) context).getSystemService(Context.INPUT_METHOD_SERVICE); 
				imm.toggleSoftInput(0, 0); 
			} 
			else{
				currentEvent = new KeyboardEvent(e.getKey());
				updateTrackedGrabber(currentEvent);
				handle(currentEvent);
			}
		}
	}
	
	@Override
	public int keyCode(char key) {
		return key;
	}
}