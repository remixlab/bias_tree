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

import remixlab.bias.core.BogusEvent;
import remixlab.bias.event.KeyboardEvent;
import remixlab.dandelion.agent.*;
import remixlab.dandelion.core.AbstractScene;
import remixlab.dandelion.core.Constants.SceneAction;

public class KeyAgent extends KeyboardAgent {
	public KeyAgent(AbstractScene scn, String n) {
		super(scn, n);
		// registration requires a call to PApplet.registerMethod("keyEvent", keyboardAgent());
		// which is done in Scene.enableKeyboardAgent(), which also register the agent at the inputHandler
		inputHandler().unregisterAgent(this);
	}

	/**
	 * Processing keyEvent method to be registered at the PApplet's instance.
	 */
	public void keyEvent(processing.event.KeyEvent e) {
		if (e.getAction() == processing.event.KeyEvent.TYPE) {
			updateTrackedGrabber(new KeyboardEvent(e.getKey()));// TODO needs testing
			handle(new KeyboardEvent(e.getKey()));
		}
		else if (e.getAction() == processing.event.KeyEvent.RELEASE) {
			updateTrackedGrabber(new KeyboardEvent(e.getModifiers(), e.getKeyCode()));// TODO needs testing
			handle(new KeyboardEvent(e.getModifiers(), e.getKeyCode()));
		}
	}

	/**
	 * Calls {@link remixlab.dandelion.agent.KeyboardAgent#setDefaultBindings()} and then adds the following:
	 * <p>
	 * {@code left_arrow -> KeyboardAction.MOVE_LEFT}<br>
	 * {@code right_arrow -> KeyboardAction.MOVE_RIGHT}<br>
	 * {@code up_arrow -> KeyboardAction.MOVE_UP}<br>
	 * {@code down_arrow -> KeyboardAction.MOVE_DOWN	}<br>
	 * {@code CTRL + java.awt.event.KeyEvent.VK_1 -> KeyboardAction.ADD_KEYFRAME_TO_PATH_1}<br>
	 * {@code ALT + java.awt.event.KeyEvent.VK_1 -> KeyboardAction.DELETE_PATH_1}<br>
	 * {@code CTRL + java.awt.event.KeyEvent.VK_2 -> KeyboardAction.ADD_KEYFRAME_TO_PATH_2}<br>
	 * {@code ALT + java.awt.event.KeyEvent.VK_2 -> KeyboardAction.DELETE_PATH_2}<br>
	 * {@code CTRL + java.awt.event.KeyEvent.VK_3 -> KeyboardAction.ADD_KEYFRAME_TO_PATH_3}<br>
	 * {@code ALT + java.awt.event.KeyEvent.VK_3 -> KeyboardAction.DELETE_PATH_3}<br>
	 * <p>
	 * Finally, it calls: {@code setKeyCodeToPlayPath(java.awt.event.KeyEvent.VK_1, 1)},
	 * {@code setKeyCodeToPlayPath(java.awt.event.KeyEvent.VK_2, 2)} and
	 * {@code setKeyCodeToPlayPath(java.awt.event.KeyEvent.VK_3, 3)} to play the paths.
	 * 
	 * @see remixlab.dandelion.agent.KeyboardAgent#setDefaultBindings()
	 * @see remixlab.dandelion.agent.KeyboardAgent#setKeyCodeToPlayPath(int, int)
	 */
	@Override
	public void setDefaultBindings() {
		// VK values here: http://docs.oracle.com/javase/7/docs/api/constant-values.html
		super.setDefaultBindings();
		// VK_LEFT : 37
		sceneProfile().setBinding(BogusEvent.NO_MODIFIER_MASK, 37, SceneAction.MOVE_LEFT);
		// VK_UP : 38
		sceneProfile().setBinding(BogusEvent.NO_MODIFIER_MASK, 38, SceneAction.MOVE_UP);
		// VK_RIGHT : 39
		sceneProfile().setBinding(BogusEvent.NO_MODIFIER_MASK, 39, SceneAction.MOVE_RIGHT);
		// VK_DOWN : 40
		sceneProfile().setBinding(BogusEvent.NO_MODIFIER_MASK, 40, SceneAction.MOVE_DOWN);

		// VK_1 : 49
		sceneProfile().setBinding(BogusEvent.CTRL, 49, SceneAction.ADD_KEYFRAME_TO_PATH_1);
		sceneProfile().setBinding(BogusEvent.ALT, 49, SceneAction.DELETE_PATH_1);
		setKeyCodeToPlayPath(49, 1);
		// VK_2 : 50
		sceneProfile().setBinding(BogusEvent.CTRL, 50, SceneAction.ADD_KEYFRAME_TO_PATH_2);
		sceneProfile().setBinding(BogusEvent.ALT, 50, SceneAction.DELETE_PATH_2);
		setKeyCodeToPlayPath(50, 2);
		// VK_3 : 51
		sceneProfile().setBinding(BogusEvent.CTRL, 51, SceneAction.ADD_KEYFRAME_TO_PATH_3);
		sceneProfile().setBinding(BogusEvent.ALT, 51, SceneAction.DELETE_PATH_3);
		setKeyCodeToPlayPath(51, 3);
	}
}
