/*********************************************************************************
 * dandelion_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.dandelion.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import remixlab.bias.core.*;
import remixlab.dandelion.core.Constants.*;
import remixlab.bias.event.*;
import remixlab.dandelion.agent.*;
import remixlab.dandelion.constraint.*;
import remixlab.dandelion.geom.*;
import remixlab.fpstiming.*;

/**
 * A 2D or 3D interactive abstract Scene. //TODO: see iFrame intro api docs
 * 
 * Main package class representing an interface between Dandelion and the outside
 * world. For an introduction to DANDELION please refer to <a
 * href="http://nakednous.github.io/projects/dandelion">this</a>.
 * <p>
 * Each AbstractScene provides the following main object instances:
 * <ol>
 * <li>An {@link #eye()} which represents the 2D ({@link remixlab.dandelion.core.Window}) or 3D (
 * {@link remixlab.dandelion.core.Camera}) controlling object. For details please refer to the
 * {@link remixlab.dandelion.core.Eye} class.</li>
 * <li>A {@link #timingHandler()} which control (single-threaded) timing operations. For details please refer to the
 * {@link remixlab.fpstiming.TimingHandler} class.</li>
 * <li>An {@link #inputHandler()} which handles all user input through {@link remixlab.bias.core.Agent}s (for details
 * please refer to the {@link remixlab.bias.core.InputHandler} class). The {@link #inputHandler()} holds a (default)
 * {@link #motionAgent()} and a (default) {@link #keyboardAgent()} which should be instantiated by derived classes at
 * construction time.</li>
 * <li>A {@link #matrixHelper()} which handles matrix operations either through the
 * {@link remixlab.dandelion.core.MatrixStackHelper} or through a third party matrix stack (like it's done with
 * Processing). For details please refer to the {@link remixlab.dandelion.core.MatrixHelper} interface.</li>
 * </ol>
 */
public abstract class AbstractScene extends AnimatorObject implements InteractiveGrabber<GlobalAction>, Constants {
	protected boolean					dottedGrid;

	// O B J E C T S
	protected MatrixHelper		matrixHelper;
	protected Eye							eye;
	protected Trackable				trck;

	// E X C E P T I O N H A N D L I N G
	protected int							startCoordCalls;

	// NUMBER OF FRAMES SINCE THE FIRST SCENE WAS INSTANTIATED
	static public long				frameCount;

	// InputHandler
	protected InputHandler		iHandler;

	// D I S P L A Y F L A G S
	protected int							visualHintMask;

	// LEFT vs RIGHT_HAND
	protected boolean					rightHanded;

	// S I Z E
	protected int							width, height;

	// offscreen
	// TODO should be protected
	public Point							upperLeftCorner;
	protected boolean					offscreen;
	protected long						lastEqUpdate;

	// FRAME SYNC requires this:
	protected final long			deltaCount;

	protected MotionAgent<?>	defMotionAgent;
	protected KeyboardAgent		defKeyboardAgent;

	/**
	 * Visual hints as "the last shall be first"
	 */
	public final static int		AXES		= 1 << 0;
	public final static int		GRID		= 1 << 1;
	public final static int		PICKING	= 1 << 2;
	public final static int		PATHS		= 1 << 3;
	public final static int		ZOOM		= 1 << 4; // prosceneMouse.zoomOnRegion
	public final static int		ROTATE	= 1 << 5; // prosceneMouse.screenRotate

	protected Platform				platform;

	public enum Platform {
		PROCESSING_DESKTOP, PROCESSING_ANDROID, PROCESSING_JS
	}

	// public final static int PUP = 1 << 6;
	// public final static int ARP = 1 << 7;

	/**
	 * Default constructor which defines a right-handed OpenGL compatible Scene with its own
	 * {@link remixlab.dandelion.core.MatrixStackHelper}. The constructor also instantiates the {@link #inputHandler()}
	 * and the {@link #timingHandler()}, and sets the AXES and GRID visual hint flags.
	 * <p>
	 * Third party (concrete) Scenes should additionally:
	 * <ol>
	 * <li>(Optionally) Define a custom {@link #matrixHelper()}. Only if the target platform (such as Processing) provides
	 * its own matrix handling.</li>
	 * <li>Call {@link #setEye(Eye)} to set the {@link #eye()}, once it's known if the Scene {@link #is2D()} or
	 * {@link #is3D()}.</li>
	 * <li>Instantiate the {@link #motionAgent()} and the {@link #keyboardAgent()} and enable them (register them at the
	 * {@link #inputHandler()}) and possibly some other {@link remixlab.bias.core.Agent}s as well and .</li>
	 * <li>Define whether or not the Scene {@link #isOffscreen()}.</li>
	 * <li>Call {@link #init()} at the end of the constructor.</li>
	 * </ol>
	 * 
	 * @see #timingHandler()
	 * @see #inputHandler()
	 * @see #setMatrixHelper(MatrixHelper)
	 * @see #setRightHanded()
	 * @see #setVisualHints(int)
	 * @see #setEye(Eye)
	 */
	public AbstractScene() {
		setPlatform();
		setTimingHandler(new TimingHandler(this));
		deltaCount = frameCount;
		iHandler = new InputHandler();
		setMatrixHelper(new MatrixStackHelper(this));
		setRightHanded();
		setVisualHints(AXES | GRID);
		upperLeftCorner = new Point(0, 0);
	}

	// grabber implementation

	protected Action<GlobalAction>	action;

	public GlobalAction referenceAction() {
		return action.referenceAction();
	}

	@Override
	public void setAction(Action<GlobalAction> a) {
		action = a;
	}

	@Override
	public Action<GlobalAction> action() {
		return action;
	}

	public boolean grabsInput(Agent agent) {
		return agent.inputGrabber() == this;
	}

	@Override
	public void performInteraction(BogusEvent event) {
		//if (processAction(event)) // may call performInteraction(KeyboardEvent event) by setting the action() :o
			//return;
		if (event instanceof KeyboardEvent)
			performInteraction((KeyboardEvent) event);
	}

	protected void performInteraction(KeyboardEvent event) {
		switch (referenceAction()) {
		case ADD_KEYFRAME_TO_PATH_1:
			eye().addKeyFrameToPath(1);
			break;
		case ADD_KEYFRAME_TO_PATH_2:
			eye().addKeyFrameToPath(2);
			break;
		case ADD_KEYFRAME_TO_PATH_3:
			eye().addKeyFrameToPath(3);
			break;
		case CUSTOM:
			performCustomAction(event);
			break;
		case DELETE_PATH_1:
			eye().deletePath(1);
			break;
		case DELETE_PATH_2:
			eye().deletePath(2);
			break;
		case DELETE_PATH_3:
			eye().deletePath(3);
			break;
		case DISPLAY_INFO:
			displayInfo();
			break;
		case INTERPOLATE_TO_FIT:
			eye().interpolateToFitScene();
			break;
		case PLAY_PATH_1:
			eye().playPath(1);
			break;
		case PLAY_PATH_2:
			eye().playPath(2);
			break;
		case PLAY_PATH_3:
			eye().playPath(3);
			break;
		case RESET_ANCHOR:
			eye().setAnchor(new Vec(0, 0, 0));
			// looks horrible, but works ;)
			eye().anchorFlag = true;
			eye().timerFx.runOnce(1000);
			break;
		case SHOW_ALL:
			showAll();
			break;
		case TOGGLE_ANIMATION:
			toggleAnimation();
			break;
		case TOGGLE_AXES_VISUAL_HINT:
			toggleAxesVisualHint();
			break;
		case TOGGLE_CAMERA_TYPE:
			toggleCameraType();
			break;
		case TOGGLE_GRID_VISUAL_HINT:
			toggleGridVisualHint();
			break;
		case TOGGLE_PATHS_VISUAL_HINT:
			togglePathsVisualHint();
			break;
		case TOGGLE_PICKING_VISUAL_HINT:
			togglePickingVisualhint();
			break;
		default:
			break;
		}
	}

	protected void performCustomAction(KeyboardEvent event) {
		AbstractScene.showMissingImplementationWarning("performCustomAction(KeyboardEvent event)", this.getClass()
				.getName());
	}

	@Override
	public boolean checkIfGrabsInput(BogusEvent event) {
		if (event instanceof KeyboardEvent)
			return checkIfGrabsInput((KeyboardEvent) event);
		return false;
	}

	protected boolean checkIfGrabsInput(KeyboardEvent event) {
		return keyboardAgent().hasBinding(event.shortcut());
	}

	/**
	 * Returns the upper left corner of the Scene window. It's always (0,0) for on-screen scenes, but off-screen scenes
	 * may be defined elsewhere on a canvas.
	 */
	public Point originCorner() {
		return upperLeftCorner;
	}

	/**
	 * Determines under which platform dandelion is running. Either DESKTOP, ANDROID or JS.
	 */
	protected abstract void setPlatform();

	/**
	 * Returns the platform where dandelion is running. Either DESKTOP, ANDROID or JS.
	 */
	public Platform platform() {
		return platform;
	}

	// AGENTs

	// Keyboard

	/**
	 * Returns the default {@link remixlab.dandelion.agent.KeyboardAgent} keyboard agent.
	 * 
	 * @see #motionAgent()
	 */
	public KeyboardAgent keyboardAgent() {
		return defKeyboardAgent;
	}

	/**
	 * Returns {@code true} if the {@link #keyboardAgent()} is enabled and {@code false} otherwise.
	 * 
	 * @see #enableKeyboardAgent()
	 * @see #disableKeyboardAgent()
	 * @see #isMotionAgentEnabled()
	 */
	public boolean isKeyboardAgentEnabled() {
		return inputHandler().isAgentRegistered(defKeyboardAgent);
	}

	/**
	 * Enables keyboard handling through the {@link #keyboardAgent()}.
	 * 
	 * @see #isKeyboardAgentEnabled()
	 * @see #disableKeyboardAgent()
	 * @see #enableMotionAgent()
	 */
	public void enableKeyboardAgent() {
		if (!inputHandler().isAgentRegistered(keyboardAgent())) {
			inputHandler().registerAgent(keyboardAgent());
		}
	}

	/**
	 * Disables the default {@link remixlab.dandelion.agent.KeyboardAgent} and returns it.
	 * 
	 * @see #isKeyboardAgentEnabled()
	 * @see #enableKeyboardAgent()
	 * @see #disableMotionAgent()
	 */
	public KeyboardAgent disableKeyboardAgent() {
		if (inputHandler().isAgentRegistered(keyboardAgent())) {
			return (KeyboardAgent) inputHandler().unregisterAgent(keyboardAgent());
		}
		return keyboardAgent();
	}

	// TODO decide whether to include this wrappers or not

	/**
	 * Restores the default keyboard shortcuts:
	 * <p>
	 * {@code 'a' -> KeyboardAction.TOGGLE_AXES_VISUAL_HINT}<br>
	 * {@code 'f' -> KeyboardAction.TOGGLE_FRAME_VISUAL_HINT}<br>
	 * {@code 'g' -> KeyboardAction.TOGGLE_GRID_VISUAL_HINT}<br>
	 * {@code 'm' -> KeyboardAction.TOGGLE_ANIMATION}<br>
	 * {@code 'e' -> KeyboardAction.TOGGLE_CAMERA_TYPE}<br>
	 * {@code 'h' -> KeyboardAction.DISPLAY_INFO}<br>
	 * {@code 'r' -> KeyboardAction.TOGGLE_PATHS_VISUAL_HINT}<br>
	 * {@code 's' -> KeyboardAction.INTERPOLATE_TO_FIT}<br>
	 * {@code 'S' -> KeyboardAction.SHOW_ALL}<br>
	 * {@code left_arrow -> KeyboardAction.MOVE_LEFT}<br>
	 * {@code right_arrow -> KeyboardAction.MOVE_RIGHT}<br>
	 * {@code up_arrow -> KeyboardAction.MOVE_UP}<br>
	 * {@code down_arrow -> KeyboardAction.MOVE_DOWN	}<br>
	 * {@code 'CTRL' + '1' -> KeyboardAction.ADD_KEYFRAME_TO_PATH_1}<br>
	 * {@code 'ALT' + '1' -> KeyboardAction.DELETE_PATH_1}<br>
	 * {@code '1' -> KeyboardAction.PLAY_PATH_1}<br>
	 * {@code 'CTRL' + '2' -> KeyboardAction.ADD_KEYFRAME_TO_PATH_2}<br>
	 * {@code 'ALT' + '2' -> KeyboardAction.DELETE_PATH_2}<br>
	 * {@code '2' -> KeyboardAction.PLAY_PATH_2}<br>
	 * {@code 'CTRL' + '3' -> KeyboardAction.ADD_KEYFRAME_TO_PATH_3}<br>
	 * {@code 'ALT' + '3' -> KeyboardAction.DELETE_PATH_3}<br>
	 * {@code '3' -> KeyboardAction.PLAY_PATH_3}<br>
	 * 
	 * @see remixlab.dandelion.agent.KeyboardAgent#setDefaultBindings()
	 */
	/*
	 * public void setDefaultKeyboardBindings() { keyboardAgent().setDefaultBindings(); }
	 */

	/**
	 * Set the virtual-key to play path. Defaults are java.awt.event.KeyEvent.VK_1, java.awt.event.KeyEvent.VK_2 and
	 * java.awt.event.KeyEvent.VK_3 which will play paths 1, 2, 3, resp.
	 */
	/*
	 * public void setKeyCodeToPlayPath(int code, int path) { keyboardAgent().setKeyCodeToPlayPath(code, path); }
	 */

	/**
	 * Binds the key shortcut to the (Keyboard) dandelion action.
	 */
	/*
	 * public void setKeyboardBinding(Character key, SceneAction action) { keyboardAgent().setBinding(key, action); }
	 */

	/**
	 * Binds the mask-vKey (virtual key) shortcut to the (Keyboard) dandelion action.
	 */
	/*
	 * public void setKeyboardBinding(int mask, int vKey, SceneAction action) { keyboardAgent().setBinding(mask, vKey,
	 * action); }
	 */

	/**
	 * Removes key shortcut binding (if present).
	 */
	/*
	 * public void removeKeyboardBinding(Character key) { keyboardAgent().removeBinding(key); }
	 */

	/**
	 * Removes mask-vKey (virtual key) shortcut binding (if present).
	 */
	/*
	 * public void removeKeyboarBinding(int mask, int vKey) { keyboardAgent().removeBinding(mask, vKey); }
	 */

	/**
	 * Removes all shortcut bindings.
	 */
	/*
	 * public void removeKeyboardBindings() { keyboardAgent().removeBindings(); }
	 */

	/**
	 * Returns {@code true} if the key shortcut is bound to a (Keyboard) dandelion action.
	 */
	/*
	 * public boolean hasKeyboardBinding(Character key) { return keyboardAgent().hasBinding(key); }
	 */

	/**
	 * Returns {@code true} if the mask-vKey (virtual key) shortcut is bound to a (Keyboard) dandelion action.
	 */
	/*
	 * public boolean hasKeyboardBinding(int mask, int vKey) { return keyboardAgent().hasBinding(mask, vKey); }
	 */

	/**
	 * Returns {@code true} if the keyboard action is bound.
	 */
	/*
	 * public boolean isKeyboardActionBound(SceneAction action) { return keyboardAgent().isActionBound(action); }
	 */

	/**
	 * Returns the (Keyboard) dandelion action that is bound to the given key shortcut. Returns {@code null} if no action
	 * is bound to the given shortcut.
	 */
	/*
	 * public SceneAction keyboardAction(Character key) { return keyboardAgent().action(key); }
	 */

	/**
	 * Returns the (Keyboard) dandelion action that is bound to the given mask-vKey (virtual key) shortcut. Returns
	 * {@code null} if no action is bound to the given shortcut.
	 */
	/*
	 * public SceneAction keyboardAction(int mask, int vKey) { return keyboardAgent().action(mask, vKey); }
	 */

	// Motion agent

	/**
	 * Returns the default motion agent.
	 * 
	 * @see #keyboardAgent()
	 */
	public MotionAgent<?> motionAgent() {
		return defMotionAgent;
	}

	/**
	 * Returns {@code true} if the {@link #motionAgent()} is enabled and {@code false} otherwise.
	 * 
	 * @see #enableMotionAgent()
	 * @see #disableMotionAgent()
	 * @see #isKeyboardAgentEnabled()
	 */
	public boolean isMotionAgentEnabled() {
		return inputHandler().isAgentRegistered(defMotionAgent);
	}

	/**
	 * Enables motion handling through the {@link #motionAgent()}.
	 * 
	 * @see #isMotionAgentEnabled()
	 * @see #disableMotionAgent()
	 * @see #enableKeyboardAgent()
	 */
	public void enableMotionAgent() {
		if (!inputHandler().isAgentRegistered(motionAgent())) {
			inputHandler().registerAgent(motionAgent());
		}
	}

	/**
	 * Disables the default motion agent and returns it.
	 * 
	 * @see #isMotionAgentEnabled()
	 * @see #enableMotionAgent()
	 * @see #enableKeyboardAgent()
	 */
	public Agent disableMotionAgent() {
		if (inputHandler().isAgentRegistered(motionAgent())) {
			return inputHandler().unregisterAgent(motionAgent());
		}
		return motionAgent();
	}

	// FPSTiming STUFF

	/**
	 * Returns the number of frames displayed since the scene was instantiated.
	 * <p>
	 * Use {@code AbstractScene.frameCount} to retrieve the number of frames displayed since the first scene was
	 * instantiated.
	 */
	public long frameCount() {
		return timingHandler().frameCount();
	}

	/**
	 * Convenience wrapper function that simply calls {@code timingHandler().registerTask(task)}.
	 * 
	 * @see remixlab.fpstiming.TimingHandler#registerTask(TimingTask)
	 */
	public void registerTimingTask(TimingTask task) {
		timingHandler().registerTask(task);
	}

	/**
	 * Convenience wrapper function that simply calls {@code timingHandler().unregisterTask(task)}.
	 */
	public void unregisterTimingTask(TimingTask task) {
		timingHandler().unregisterTask(task);
	}

	/**
	 * Convenience wrapper function that simply returns {@code timingHandler().isTaskRegistered(task)}.
	 */
	public boolean isTimingTaskRegistered(TimingTask task) {
		return timingHandler().isTaskRegistered(task);
	}

	/**
	 * Convenience wrapper function that simply calls {@code timingHandler().registerAnimator(object)}.
	 */
	public void registerAnimator(Animator object) {
		timingHandler().registerAnimator(object);
	}

	/**
	 * Convenience wrapper function that simply calls {@code timingHandler().unregisterAnimator(object)}.
	 * 
	 * @see remixlab.fpstiming.TimingHandler#unregisterAnimator(Animator)
	 */
	public void unregisterAnimator(Animator object) {
		timingHandler().unregisterAnimator(object);
	}

	/**
	 * Convenience wrapper function that simply returns {@code timingHandler().isAnimatorRegistered(object)}.
	 * 
	 * @see remixlab.fpstiming.TimingHandler#isAnimatorRegistered(Animator)
	 */
	public boolean isAnimatorRegistered(Animator object) {
		return timingHandler().isAnimatorRegistered(object);
	}

	// E V E N T H A N D L I N G

	/**
	 * Returns the scene {@link remixlab.bias.core.InputHandler}.
	 */
	public InputHandler inputHandler() {
		return iHandler;
	}

	/**
	 * Convenience function that simply returns {@code inputHandler().info()}.
	 * 
	 * @see #displayInfo(boolean)
	 */
	public String info() {
		return inputHandler().info();
	}

	/**
	 * Convenience function that simply calls {@code displayInfo(true)}.
	 */
	public void displayInfo() {
		displayInfo(true);
	}

	/**
	 * Displays the {@link #info()} bindings.
	 * 
	 * @param onConsole
	 *          if this flag is true displays the help on console. Otherwise displays it on the applet
	 * 
	 * @see #info()
	 */
	public void displayInfo(boolean onConsole) {
		if (onConsole)
			System.out.println(info());
		else
			AbstractScene.showMissingImplementationWarning("displayInfo", getClass().getName());
	}

	// 1. Scene overloaded

	// MATRIX and TRANSFORMATION STUFF

	/**
	 * Sets the {@link remixlab.dandelion.core.MatrixHelper} defining how dandelion matrices are to be handled.
	 * 
	 * @see #matrixHelper()
	 */
	public void setMatrixHelper(MatrixHelper r) {
		matrixHelper = r;
	}

	/**
	 * Returns the {@link remixlab.dandelion.core.MatrixHelper}.
	 * 
	 * @see #setMatrixHelper(MatrixHelper)
	 */
	public MatrixHelper matrixHelper() {
		return matrixHelper;
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#beginScreenDrawing()}. Adds exception when no properly
	 * closing the screen drawing with a call to {@link #endScreenDrawing()}.
	 * 
	 * @see remixlab.dandelion.core.MatrixHelper#beginScreenDrawing()
	 */
	public void beginScreenDrawing() {
		if (startCoordCalls != 0)
			throw new RuntimeException("There should be exactly one beginScreenDrawing() call followed by a "
					+ "endScreenDrawing() and they cannot be nested. Check your implementation!");

		startCoordCalls++;

		disableDepthTest();
		matrixHelper.beginScreenDrawing();
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#endScreenDrawing()}. Adds exception if
	 * {@link #beginScreenDrawing()} wasn't properly called before
	 * 
	 * @see remixlab.dandelion.core.MatrixHelper#endScreenDrawing()
	 */
	public void endScreenDrawing() {
		startCoordCalls--;
		if (startCoordCalls != 0)
			throw new RuntimeException("There should be exactly one beginScreenDrawing() call followed by a "
					+ "endScreenDrawing() and they cannot be nested. Check your implementation!");

		matrixHelper.endScreenDrawing();
		enableDepthTest();
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#bind()}
	 */
	protected void bindMatrices() {
		matrixHelper.bind();
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#pushModelView()}
	 */
	public void pushModelView() {
		matrixHelper.pushModelView();
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#popModelView()}
	 */
	public void popModelView() {
		matrixHelper.popModelView();
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#pushProjection()}
	 */
	public void pushProjection() {
		matrixHelper.pushProjection();
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#popProjection()}
	 */
	public void popProjection() {
		matrixHelper.popProjection();
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#translate(float, float)}
	 */
	public void translate(float tx, float ty) {
		matrixHelper.translate(tx, ty);
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#translate(float, float, float)}
	 */
	public void translate(float tx, float ty, float tz) {
		matrixHelper.translate(tx, ty, tz);
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#rotate(float)}
	 */
	public void rotate(float angle) {
		matrixHelper.rotate(angle);
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#rotateX(float)}
	 */
	public void rotateX(float angle) {
		matrixHelper.rotateX(angle);
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#rotateY(float)}
	 */
	public void rotateY(float angle) {
		matrixHelper.rotateY(angle);
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#rotateZ(float)}
	 */
	public void rotateZ(float angle) {
		matrixHelper.rotateZ(angle);
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#rotate(float, float, float, float)}
	 */
	public void rotate(float angle, float vx, float vy, float vz) {
		matrixHelper.rotate(angle, vx, vy, vz);
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#scale(float)}
	 */
	public void scale(float s) {
		matrixHelper.scale(s);
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#scale(float, float)}
	 */
	public void scale(float sx, float sy) {
		matrixHelper.scale(sx, sy);
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#scale(float, float, float)}
	 */
	public void scale(float x, float y, float z) {
		matrixHelper.scale(x, y, z);
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#resetModelView()}
	 */
	public void resetModelView() {
		matrixHelper.resetModelView();
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#resetProjection()}
	 */
	public void resetProjection() {
		matrixHelper.resetProjection();
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#applyModelView(Mat)}
	 */
	public void applyModelView(Mat source) {
		matrixHelper.applyModelView(source);
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#applyProjection(Mat)}
	 */
	public void applyProjection(Mat source) {
		matrixHelper.applyProjection(source);
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#modelView()}
	 */
	public Mat modelView() {
		return matrixHelper.modelView();
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#projection()}
	 */
	public Mat projection() {
		return matrixHelper.projection();
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#getModelView(Mat)}
	 */
	public Mat getModelView(Mat target) {
		return matrixHelper.getModelView(target);
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#getProjection(Mat)}
	 */
	public Mat getProjection(Mat target) {
		return matrixHelper.getProjection(target);
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#setModelView(Mat)}
	 */
	public void setModelView(Mat source) {
		matrixHelper.setModelView(source);
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#setProjection(Mat)}
	 */
	public void setProjection(Mat source) {
		matrixHelper.setProjection(source);
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#printModelView()}
	 */
	public void printModelView() {
		matrixHelper.printModelView();
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#printProjection()}
	 */
	public void printProjection() {
		matrixHelper.printProjection();
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#isProjectionViewInverseCached()}.
	 * <p>
	 * Use it only when continuously calling {@link #unprojectedCoordinatesOf(Vec)}.
	 * 
	 * @see #optimizeUnprojectedCoordinatesOf(boolean)
	 * @see #unprojectedCoordinatesOf(Vec)
	 */
	public boolean isUnprojectedCoordinatesOfOptimized() {
		return matrixHelper.isProjectionViewInverseCached();
	}

	/**
	 * Wrapper for {@link remixlab.dandelion.core.MatrixHelper#cacheProjectionViewInverse(boolean)}.
	 * <p>
	 * Use it only when continuously calling {@link #unprojectedCoordinatesOf(Vec)}.
	 * 
	 * @see #isUnprojectedCoordinatesOfOptimized()
	 * @see #unprojectedCoordinatesOf(Vec)
	 */
	public void optimizeUnprojectedCoordinatesOf(boolean optimise) {
		matrixHelper.cacheProjectionViewInverse(optimise);
	}

	// DRAWING STUFF

	/**
	 * Returns the visual hints flag.
	 */
	public int visualHints() {
		return this.visualHintMask;
	}

	/**
	 * Low level setting of visual flags. You'd prefer {@link #setAxesVisualHint(boolean)},
	 * {@link #setGridVisualHint(boolean)}, {@link #setPathsVisualHint(boolean)} and
	 * {@link #setPickingVisualHint(boolean)}, unless you want to set them all at once, e.g.,
	 * {@code setVisualHints(Constants.AXES | Constants.GRID | Constants.PATHS | Constants.PICKING)}.
	 */
	public void setVisualHints(int flag) {
		visualHintMask = flag;
	}

	/**
	 * Toggles the state of {@link #axesVisualHint()}.
	 * 
	 * @see #axesVisualHint()
	 * @see #setAxesVisualHint(boolean)
	 */
	public void toggleAxesVisualHint() {
		setAxesVisualHint(!axesVisualHint());
	}

	/**
	 * Toggles the state of {@link #gridVisualHint()}.
	 * 
	 * @see #setGridVisualHint(boolean)
	 */
	public void toggleGridVisualHint() {
		setGridVisualHint(!gridVisualHint());
	}

	/**
	 * Toggles the state of {@link #pickingVisualHint()}.
	 * 
	 * @see #setPickingVisualHint(boolean)
	 */
	public void togglePickingVisualhint() {
		setPickingVisualHint(!pickingVisualHint());
	}

	/**
	 * Toggles the state of {@link #pathsVisualHint()}.
	 * 
	 * @see #setPathsVisualHint(boolean)
	 */
	public void togglePathsVisualHint() {
		setPathsVisualHint(!pathsVisualHint());
	}

	/**
	 * Internal :p
	 */
	protected void toggleZoomVisualHint() {
		setZoomVisualHint(!zoomVisualHint());
	}

	/**
	 * Internal :p
	 */
	protected void toggleRotateVisualHint() {
		setRotateVisualHint(!rotateVisualHint());
	}

	/**
	 * Returns {@code true} if axes are currently being drawn and {@code false} otherwise.
	 */
	public boolean axesVisualHint() {
		return ((visualHintMask & AXES) != 0);
	}

	/**
	 * Returns {@code true} if grid is currently being drawn and {@code false} otherwise.
	 */
	public boolean gridVisualHint() {
		return ((visualHintMask & GRID) != 0);
	}

	/**
	 * Returns {@code true} if the picking selection visual hint is currently being drawn and {@code false} otherwise.
	 */
	public boolean pickingVisualHint() {
		return ((visualHintMask & PICKING) != 0);
	}

	/**
	 * Returns {@code true} if the eye paths visual hints are currently being drawn and {@code false} otherwise.
	 */
	public boolean pathsVisualHint() {
		return ((visualHintMask & PATHS) != 0);
	}

	/**
	 * Internal. Third parties should not call this.
	 */
	public boolean zoomVisualHint() {
		return ((visualHintMask & ZOOM) != 0);
	}

	/**
	 * Internal. Third parties should not call this.
	 */
	public boolean rotateVisualHint() {
		return ((visualHintMask & ROTATE) != 0);
	}

	/**
	 * Sets the display of the axes according to {@code draw}
	 */
	public void setAxesVisualHint(boolean draw) {
		if (draw)
			visualHintMask |= AXES;
		else
			visualHintMask &= ~AXES;
	}

	/**
	 * Sets the display of the grid according to {@code draw}
	 */
	public void setGridVisualHint(boolean draw) {
		if (draw)
			visualHintMask |= GRID;
		else
			visualHintMask &= ~GRID;
	}

	/**
	 * Sets the display of the interactive frames' selection hints according to {@code draw}
	 */
	public void setPickingVisualHint(boolean draw) {
		if (draw)
			visualHintMask |= PICKING;
		else
			visualHintMask &= ~PICKING;
	}

	/**
	 * Sets the display of the camera key frame paths according to {@code draw}
	 */
	public void setPathsVisualHint(boolean draw) {
		if (draw) {
			if(eye()!=null) {
				visualHintMask |= PATHS;
				eye().attachPaths();
			}
			else
				System.err.println("Warning: null eye, no path attached!");
		}
		else {
			if(eye()!=null) {
				visualHintMask &= ~PATHS;
				eye().detachPaths();
			}
			else
				System.err.println("Warning: null eye, no path dettached!");
		}
	}

	/**
	 * Internal. Third parties should not call this.
	 */
	public void setZoomVisualHint(boolean draw) {
		if (draw)
			visualHintMask |= ZOOM;
		else
			visualHintMask &= ~ZOOM;
	}

	/**
	 * Internal. Third parties should not call this.
	 */
	public void setRotateVisualHint(boolean draw) {
		if (draw)
			visualHintMask |= ROTATE;
		else
			visualHintMask &= ~ROTATE;
	}

	/**
	 * Called before your main drawing, e.g., P5.pre().
	 * <p>
	 * Handles the {@link #avatar()}, then calls {@link #bindMatrices()} and finally
	 * {@link remixlab.dandelion.core.Eye#updateBoundaryEquations()} if {@link #areBoundaryEquationsEnabled()}.
	 */
	public void preDraw() {
		if (avatar() != null && (!eye().anyInterpolationStarted())) {
			eye().frame().setPosition(avatar().eyeFrame().position());
			eye().frame().setOrientation(avatar().eyeFrame().orientation());
			eye().frame().setScaling(avatar().eyeFrame().scaling());
		}

		bindMatrices();
		if (areBoundaryEquationsEnabled() && (eye().lastUpdate() > lastEqUpdate || lastEqUpdate == 0)) {
			eye().updateBoundaryEquations();
			lastEqUpdate = timingHandler().frameCount();
		}
	}

	/**
	 * Called after your main drawing, e.g., P5.draw().
	 * <p>
	 * Calls:
	 * <ol>
	 * <li>{@link remixlab.fpstiming.TimingHandler#handle()}</li>
	 * <li>{@link remixlab.bias.core.InputHandler#handle()}</li>
	 * <li>{@link #proscenium()}</li>
	 * <li> {@link #invokeGraphicsHandler()}</li>
	 * <li>{@link #displayVisualHints()}.</li>
	 * </ol>
	 * 
	 * @see #proscenium()
	 * @see #invokeGraphicsHandler()
	 * @see #gridVisualHint()
	 * @see #visualHints()
	 */
	public void postDraw() {
		// 1. timers
		timingHandler().handle();
		if (frameCount < frameCount())
			frameCount = frameCount();
		if (frameCount < frameCount() + deltaCount)
			frameCount = frameCount() + deltaCount;
		// 2. Agents
		inputHandler().handle();
		// 3. Alternative use only
		proscenium();
		// 4. Draw external registered method (only in java sub-classes)
		invokeGraphicsHandler(); // abstract
		// 5. Display visual hints
		displayVisualHints(); // abstract
	}

	/**
	 * Invokes an external drawing method (if registered). Called by {@link #postDraw()}.
	 * <p>
	 * Requires reflection and thus default implementation is empty. See proscene.Scene for an implementation.
	 */
	protected boolean invokeGraphicsHandler() {
		return false;
	}

	/**
	 * Internal use. Display various on-screen visual hints to be called from {@link #postDraw()}.
	 */
	protected void displayVisualHints() {
		if (gridVisualHint())
			drawGridHint();
		if (axesVisualHint())
			drawAxesHint();
		if (pickingVisualHint())
			drawPickingHint();
		if (pathsVisualHint())
			drawPathsHint();
		if (zoomVisualHint())
			drawZoomWindowHint();
		if (rotateVisualHint())
			drawScreenRotateHint();
		if (eye().anchorFlag)
			drawAnchorHint();
		if (eye().pupFlag)
			drawPointUnderPixelHint();
	}

	/**
	 * Internal use.
	 */
	protected void drawPickingHint() {
		drawPickingTargets();
	}
	
	protected void drawPickingTargets() {
		List<GrabberFrame> gList = new ArrayList<GrabberFrame>();
		for (Grabber mg : motionAgent().grabbers())
			if(mg instanceof GrabberFrame)
				if( !((GrabberFrame)mg).isEyeFrame() )
					gList.add((GrabberFrame)mg);
		gList.removeAll(eye.keyFrames());
		for(GrabberFrame g : gList)
			this.drawPickingTarget(g);
	}

	/**
	 * Internal use.
	 */
	protected void drawAxesHint() {
		drawAxes(eye().sceneRadius());
	}

	/**
	 * Internal use.
	 */
	protected void drawGridHint() {
		if (gridIsDotted())
			drawDottedGrid(eye().sceneRadius());
		else
			drawGrid(eye().sceneRadius());
	}

	/**
	 * Internal use.
	 */
	protected void drawPathsHint() {
		drawPaths();
	}
	
	protected void drawPaths() {
		/*
		Iterator<Integer> itrtr = eye.kfi.keySet().iterator();
		while (itrtr.hasNext()) {
			Integer key = itrtr.next();
			drawPath(eye.keyFrameInterpolatorMap().get(key), 3, is3D() ? 5 : 2, radius());
		}
		*/
		// alternative:
		// /*
		KeyFrameInterpolator[] k = eye.keyFrameInterpolatorArray();
		for(int i=0; i< k.length; i++)
			drawPath(k[i], 3, 5, radius());
	    // */ 
		
		for(GrabberFrame gFrame : eye.keyFrames())
			drawPickingTarget(gFrame);
	}

	/**
	 * Convenience function that simply calls {@code drawPath(kfi, 1, 6, 100)}.
	 * 
	 * @see #drawPath(KeyFrameInterpolator, int, int, float)
	 */
	public void drawPath(KeyFrameInterpolator kfi) {
		drawPath(kfi, 1, 6, 100);
	}

	/**
	 * Convenience function that simply calls {@code drawPath(kfi, 1, 6, scale)}
	 * 
	 * @see #drawPath(KeyFrameInterpolator, int, int, float)
	 */
	public void drawPath(KeyFrameInterpolator kfi, float scale) {
		drawPath(kfi, 1, 6, scale);
	}

	/**
	 * Convenience function that simply calls {@code drawPath(kfi, mask, nbFrames, * 100)}
	 * 
	 * @see #drawPath(KeyFrameInterpolator, int, int, float)
	 */
	public void drawPath(KeyFrameInterpolator kfi, int mask, int nbFrames) {
		drawPath(kfi, mask, nbFrames, 100);
	}

	/**
	 * Convenience function that simply calls {@code drawAxis(100)}.
	 */
	public void drawAxes() {
		drawAxes(100);
	}

	/**
	 * Convenience function that simplt calls {@code drawDottedGrid(100, 10)}.
	 */
	public void drawDottedGrid() {
		drawDottedGrid(100, 10);
	}

	/**
	 * Convenience function that simply calls {@code drawGrid(100, 10)}
	 * 
	 * @see #drawGrid(float, int)
	 */
	public void drawGrid() {
		drawGrid(100, 10);
	}

	/**
	 * Convenience function that simplt calls {@code drawDottedGrid(size, 10)}.
	 */
	public void drawDottedGrid(float size) {
		drawDottedGrid(size, 10);
	}

	/**
	 * Convenience function that simply calls {@code drawGrid(size, 10)}
	 * 
	 * @see #drawGrid(float, int)
	 */
	public void drawGrid(float size) {
		drawGrid(size, 10);
	}

	/**
	 * Convenience function that simplt calls {@code drawDottedGrid(100, nbSubdivisions)}.
	 */
	public void drawDottedGrid(int nbSubdivisions) {
		drawDottedGrid(100, nbSubdivisions);
	}

	/**
	 * Convenience function that simply calls {@code drawGrid(100, nbSubdivisions)}
	 * 
	 * @see #drawGrid(float, int)
	 */
	public void drawGrid(int nbSubdivisions) {
		drawGrid(100, nbSubdivisions);
	}

	/**
	 * Convenience function that simply calls {@code drawTorusSolenoid(6)}.
	 * 
	 * @see #drawTorusSolenoid(int, int, float, float)
	 */
	public void drawTorusSolenoid() {
		drawTorusSolenoid(6);
	}

	/**
	 * Convenience function that simply calls {@code drawTorusSolenoid(faces, 0.07f * radius())}.
	 * 
	 * @see #drawTorusSolenoid(int, int, float, float)
	 */
	public void drawTorusSolenoid(int faces) {
		drawTorusSolenoid(faces, 0.07f * radius());
	}

	/**
	 * Convenience function that simply calls {@code drawTorusSolenoid(6, insideRadius)}.
	 * 
	 * @see #drawTorusSolenoid(int, int, float, float)
	 */
	public void drawTorusSolenoid(float insideRadius) {
		drawTorusSolenoid(6, insideRadius);
	}

	/**
	 * Convenience function that simply calls {@code drawTorusSolenoid(faces, 100, insideRadius, insideRadius * 1.3f)}.
	 * 
	 * @see #drawTorusSolenoid(int, int, float, float)
	 */
	public void drawTorusSolenoid(int faces, float insideRadius) {
		drawTorusSolenoid(faces, 100, insideRadius, insideRadius * 1.3f);
	}

	/**
	 * Draws a torus solenoid. Dandelion logo.
	 * 
	 * @param faces
	 * @param detail
	 * @param insideRadius
	 * @param outsideRadius
	 */
	public abstract void drawTorusSolenoid(int faces, int detail, float insideRadius, float outsideRadius);

	/**
	 * Same as {@code cone(det, 0, 0, r, h);}
	 * 
	 * @see #drawCone(int, float, float, float, float)
	 */
	public void drawCone(int det, float r, float h) {
		drawCone(det, 0, 0, r, h);
	}

	/**
	 * Same as {@code cone(12, 0, 0, r, h);}
	 * 
	 * @see #drawCone(int, float, float, float, float)
	 */
	public void drawCone(float r, float h) {
		drawCone(12, 0, 0, r, h);
	}

	/**
	 * Same as {@code cone(det, 0, 0, r1, r2, h);}
	 * 
	 * @see #drawCone(int, float, float, float, float, float)
	 */
	public void drawCone(int det, float r1, float r2, float h) {
		drawCone(det, 0, 0, r1, r2, h);
	}

	/**
	 * Same as {@code cone(18, 0, 0, r1, r2, h);}
	 * 
	 * @see #drawCone(int, float, float, float, float, float)
	 */
	public void drawCone(float r1, float r2, float h) {
		drawCone(18, 0, 0, r1, r2, h);
	}

	/**
	 * Simply calls {@code drawArrow(length, 0.05f * length)}
	 * 
	 * @see #drawArrow(float, float)
	 */
	public void drawArrow(float length) {
		drawArrow(length, 0.05f * length);
	}

	/**
	 * Draws a 3D arrow along the positive Z axis.
	 * <p>
	 * {@code length} and {@code radius} define its geometry.
	 * <p>
	 * Use {@link #drawArrow(Vec, Vec, float)} to place the arrow in 3D.
	 */
	public void drawArrow(float length, float radius) {
		float head = 2.5f * (radius / length) + 0.1f;
		float coneRadiusCoef = 4.0f - 5.0f * head;

		drawCylinder(radius, length * (1.0f - head / coneRadiusCoef));
		translate(0.0f, 0.0f, length * (1.0f - head));
		drawCone(coneRadiusCoef * radius, head * length);
		translate(0.0f, 0.0f, -length * (1.0f - head));
	}

	/**
	 * Draws a 3D arrow between the 3D point {@code from} and the 3D point {@code to}, both defined in the current world
	 * coordinate system.
	 * 
	 * @see #drawArrow(float, float)
	 */
	public void drawArrow(Vec from, Vec to, float radius) {
		pushModelView();
		translate(from.x(), from.y(), from.z());
		applyModelView(new Quat(new Vec(0, 0, 1), Vec.subtract(to, from)).matrix());
		drawArrow(Vec.subtract(to, from).magnitude(), radius);
		popModelView();
	}

	/**
	 * Convenience function that simply calls {@code drawEye(eye, 1)}.
	 */
	public void drawEye(Eye eye) {
		drawEye(eye, 1);
	}

	/**
	 * Convenience function that simply calls {@code drawCross(pg3d.color(255, 255, 255), px, py, 15, 3)}.
	 */
	public void drawCross(float px, float py) {
		drawCross(px, py, 30);
	}

	/**
	 * Convenience function that simply calls {@code drawFilledCircle(40, center, radius)}.
	 * 
	 * @see #drawFilledCircle(int, Vec, float)
	 */
	public void drawFilledCircle(Vec center, float radius) {
		drawFilledCircle(40, center, radius);
	}

	// abstract drawing methods

	/**
	 * Draws a cylinder of width {@code w} and height {@code h}, along the positive {@code z} axis.
	 */
	public abstract void drawCylinder(float w, float h);

	/**
	 * Draws a cylinder whose bases are formed by two cutting planes ({@code m} and {@code n}), along the Camera positive
	 * {@code z} axis.
	 * 
	 * @param detail
	 * @param w
	 *          radius of the cylinder and h is its height
	 * @param h
	 *          height of the cylinder
	 * @param m
	 *          normal of the plane that intersects the cylinder at z=0
	 * @param n
	 *          normal of the plane that intersects the cylinder at z=h
	 * 
	 * @see #drawCylinder(float, float)
	 */
	public abstract void drawHollowCylinder(int detail, float w, float h, Vec m, Vec n);

	/**
	 * Draws a cone along the positive {@code z} axis, with its base centered at {@code (x,y)}, height {@code h}, and
	 * radius {@code r}.
	 * 
	 * @see #drawCone(int, float, float, float, float, float)
	 */
	public abstract void drawCone(int detail, float x, float y, float r, float h);

	/**
	 * Draws a truncated cone along the positive {@code z} axis, with its base centered at {@code (x,y)}, height {@code h}
	 * , and radii {@code r1} and {@code r2} (basis and height respectively).
	 * 
	 * @see #drawCone(int, float, float, float, float)
	 */
	public abstract void drawCone(int detail, float x, float y, float r1, float r2, float h);

	/**
	 * Draws axes of length {@code length} which origin correspond to the world coordinate system origin.
	 * 
	 * @see #drawGrid(float, int)
	 */
	public abstract void drawAxes(float length);

	/**
	 * Draws a grid in the XY plane, centered on (0,0,0) (defined in the current coordinate system).
	 * <p>
	 * {@code size} and {@code nbSubdivisions} define its geometry.
	 * 
	 * @see #drawAxes(float)
	 */
	public abstract void drawGrid(float size, int nbSubdivisions);

	/**
	 * Draws a dotted-grid in the XY plane, centered on (0,0,0) (defined in the current coordinate system).
	 * <p>
	 * {@code size} and {@code nbSubdivisions} define its geometry.
	 * 
	 * @see #drawAxes(float)
	 */
	public abstract void drawDottedGrid(float size, int nbSubdivisions);

	/**
	 * Draws the path used to interpolate the {@link remixlab.dandelion.core.KeyFrameInterpolator#frame()}
	 * <p>
	 * {@code mask} controls what is drawn: If ( (mask &amp; 1) != 0 ), the position path is drawn. If ( (mask &amp; 2) != 0 ), a
	 * camera representation is regularly drawn and if ( (mask &amp; 4) != 0 ), oriented axes are regularly drawn. Examples:
	 * <p>
	 * {@code drawPath(); // Simply draws the interpolation path} <br>
	 * {@code drawPath(3); // Draws path and cameras} <br>
	 * {@code drawPath(5); // Draws path and axes} <br>
	 * <p>
	 * In the case where camera or axes are drawn, {@code nbFrames} controls the number of objects (axes or camera) drawn
	 * between two successive keyFrames. When {@code nbFrames = 1}, only the path KeyFrames are drawn.
	 * {@code nbFrames = 2} also draws the intermediate orientation, etc. The maximum value is 30. {@code nbFrames} should
	 * divide 30 so that an object is drawn for each KeyFrame. Default value is 6.
	 * <p>
	 * {@code scale} controls the scaling of the camera and axes drawing. A value of {@link #radius()} should give good
	 * results.
	 */
	public abstract void drawPath(KeyFrameInterpolator kfi, int mask, int nbFrames, float scale);

	/**
	 * Draws a representation of the {@code camera} in the 3D virtual world.
	 * <p>
	 * The near and far planes are drawn as quads, the frustum is drawn using lines and the camera up vector is
	 * represented by an arrow to disambiguate the drawing.
	 * <p>
	 * When {@code drawFarPlane} is {@code false}, only the near plane is drawn. {@code scale} can be used to scale the
	 * drawing: a value of 1.0 (default) will draw the Camera's frustum at its actual size.
	 * <p>
	 * <b>Note:</b> The drawing of a Scene's own Scene.camera() should not be visible, but may create artifacts due to
	 * numerical imprecisions.
	 */
	public abstract void drawEye(Eye eye, float scale);

	/**
	 * Internal use.
	 */
	protected abstract void drawKFIEye(float scale);

	/**
	 * Draws a rectangle on the screen showing the region where a zoom operation is taking place.
	 */
	protected abstract void drawZoomWindowHint();

	/**
	 * Draws visual hint (a line on the screen) when a screen rotation is taking place.
	 */
	protected abstract void drawScreenRotateHint();

	/**
	 * Draws visual hint (a cross on the screen) when the {@link remixlab.dandelion.core.Eye#anchor()} is being set.
	 * <p>
	 * Simply calls {@link #drawCross(float, float, float)} on
	 * {@link remixlab.dandelion.core.Eye#projectedCoordinatesOf()} from {@link remixlab.dandelion.core.Eye#anchor()}.
	 * 
	 * @see #drawCross(float, float, float)
	 */
	protected abstract void drawAnchorHint();

	/**
	 * Internal use.
	 */
	protected abstract void drawPointUnderPixelHint();

	/**
	 * Draws a cross on the screen centered under pixel {@code (px, py)}, and edge of size {@code size}.
	 * 
	 * @see #drawAnchorHint()
	 */
	public abstract void drawCross(float px, float py, float size);

	/**
	 * Draws a filled circle using screen coordinates.
	 * 
	 * @param subdivisions
	 *          Number of triangles approximating the circle.
	 * @param center
	 *          Circle screen center.
	 * @param radius
	 *          Circle screen radius.
	 */
	public abstract void drawFilledCircle(int subdivisions, Vec center, float radius);

	/**
	 * Draws a filled square using screen coordinates.
	 * 
	 * @param center
	 *          Square screen center.
	 * @param edge
	 *          Square edge length.
	 */
	public abstract void drawFilledSquare(Vec center, float edge);

	/**
	 * Draws the classical shooter target on the screen.
	 * 
	 * @param center
	 *          Center of the target on the screen
	 * @param length
	 *          Length of the target in pixels
	 */
	public abstract void drawShooterTarget(Vec center, float length);

	/**
	 * Draws all InteractiveFrames' picking targets: a shooter target visual hint of
	 * {@link remixlab.dandelion.core.InteractiveFrame#grabsInputThreshold()} pixels size.
	 * 
	 * <b>Attention:</b> the target is drawn either if the iFrame is part of camera path and keyFrame is {@code true}, or
	 * if the iFrame is not part of camera path and keyFrame is {@code false}.
	 */
	public abstract void drawPickingTarget(GrabberFrame gFrame);

	// end wrapper

	// 0. Optimization stuff

	// public abstract long frameCount();

	// 1. Associated objects

	// AVATAR STUFF

	/**
	 * Returns the avatar object to be tracked by the Camera when it is in Third Person mode.
	 * <p>
	 * Simply returns {@code null} if no avatar has been set.
	 */
	public Trackable avatar() {
		return trck;
	}

	/**
	 * Sets the avatar object to be tracked by the Camera when it is in Third Person mode.
	 * 
	 * @see #unsetAvatar()
	 */
	public void setAvatar(Trackable t) {
		trck = t;
		if (avatar() == null)
			return;

		eye().frame().stopSpinning();
		if (avatar() instanceof GrabberFrame)
			((GrabberFrame) (avatar())).stopSpinning();

		// perform small animation ;)
		if (eye().anyInterpolationStarted())
			eye().stopInterpolations();
		// eye().interpolateTo(avatar().eyeFrame());//works only when eyeFrame scaling = magnitude
		GrabberFrame eyeFrameCopy = avatar().eyeFrame().get();
		eyeFrameCopy.setMagnitude(avatar().eyeFrame().scaling());
		eye().interpolateTo(eyeFrameCopy);
		
		if (avatar() instanceof GrabberFrame) {
			GrabberFrame avatarGrabber = (GrabberFrame)avatar();
			for(Agent agent : inputHandler().agents()) {
				if( !(avatarGrabber instanceof InteractiveGrabber) || avatarGrabber instanceof InteractiveFrame) {
					agent.addGrabber(avatarGrabber);
					agent.setDefaultGrabber(avatarGrabber);
				}
			}
		}
	}

	/**
	 * If there's an avatar unset it. Returns previous avatar.
	 * 
	 * @see #setAvatar(Trackable)
	 */
	public Trackable unsetAvatar() {
		Trackable prev = trck;
		if(prev != null) {
			for(Agent agent : inputHandler().agents()) {
				agent.resetTrackedGrabber();
				agent.resetDefaultGrabber();
			}
			eye().interpolateToFitScene();
		}
		trck = null;
		return prev;
	}

	// 3. EYE STUFF

	/**
	 * Returns the associated Eye, never {@code null}. This is the high level version of {@link #window()} and
	 * {@link #camera()} which holds that which is common of the two.
	 * <p>
	 * 2D applications should simply use {@link #window()} and 3D applications should simply use {@link #camera()}. If you
	 * plan to implement two versions of the same application one in 2D and the other in 3D, use this method.
	 * <p>
	 * <b>Note</b> that not all methods defined in the Camera class are available in the Eye class and that all methods
	 * defined in the Window class are.
	 */
	public Eye eye() {
		return eye;
	}
	
	public GrabberFrame eyeFrame() {
		return eye.frame();
	} 
	
	/**
	 * Replaces the current {@link #eye()} with {@code vp}.
	 * <p>
	 * The {@link #inputHandler()} will attempt to the {@link #eyeFrame()} to all
	 * its {@link remixlab.bias.core.InputHandler#agents()}, such as the {@link #motionAgent()}
	 * and {@link #keyboardAgent()}.
	 */
	public void setEye(Eye vp) {
		if (vp == null)
			return;
		if(!replaceEye(vp)) {
			eye = vp;			
			for(Agent agent : inputHandler().agents()) {
				if( !(eye().frame() instanceof InteractiveGrabber) || eye().frame() instanceof InteractiveFrame) {
					agent.addGrabber(eye().frame());
					agent.setDefaultGrabber(eye().frame());
				}
			}
		}		
		eye().setSceneRadius(radius());
		eye().setSceneCenter(center());
		eye().setScreenWidthAndHeight(width(), height());
		showAll();
	}
	
	protected boolean replaceEye(Eye vp) {
		if (vp == null || vp == eye())
			return false;
		if (eye() != null) {
			List<Agent> agents = new ArrayList<Agent>();
			for(Agent agent : inputHandler().agents())
				if(agent.defaultGrabber() == eye().frame() && agent.defaultGrabber() != null) {
					agents.add(agent);
				agent.removeGrabber(eye().frame());
			}
			eye = vp;
			for(Agent agent : inputHandler().agents())
				if( !(eye().frame() instanceof InteractiveGrabber) || eye().frame() instanceof InteractiveFrame)
				  agent.addGrabber(eye().frame());
			for(Agent agent : agents)
				agent.setDefaultGrabber(eye().frame());
			
			return true;
		}
		return false;
	}

	/**
	 * If {@link #isLeftHanded()} calls {@link #setRightHanded()}, otherwise calls {@link #setLeftHanded()}.
	 */
	public void flip() {
		if (isLeftHanded())
			setRightHanded();
		else
			setLeftHanded();
	}

	/**
	 * If {@link #is3D()} returns the associated Camera, never {@code null}. If {@link #is2D()} throws an exception.
	 * 
	 * @see #eye()
	 */
	public Camera camera() {
		if (this.is3D())
			return (Camera) eye;
		else
			throw new RuntimeException("Camera type is only available in 3D");
	}

	/**
	 * If {@link #is3D()} sets the Camera. If {@link #is2D()} throws an exception.
	 * 
	 * @see #setEye(Eye)
	 */
	public void setCamera(Camera cam) {
		if (this.is2D()) {
			System.out.println("Warning: Camera Type is only available in 3D");
		}
		else
			setEye(cam);
	}

	/**
	 * If {@link #is2D()} returns the associated Window, never {@code null}. If {@link #is3D()} throws an exception.
	 * 
	 * @see #eye()
	 */
	public Window window() {
		if (this.is2D())
			return (Window) eye;
		else
			throw new RuntimeException("Window type is only available in 2D");
	}

	/**
	 * If {@link #is2D()} sets the Window. If {@link #is3D()} throws an exception.
	 * 
	 * @see #setEye(Eye)
	 */
	public void setWindow(Window win) {
		if (this.is3D()) {
			System.out.println("Warning: Window Type is only available in 2D");
		}
		else
			setEye(win);
	}

	/**
	 * Same as {@code eye().frame().setConstraint(constraint)}.
	 * 
	 * @see remixlab.dandelion.core.InteractiveFrame#setConstraint(Constraint)
	 */
	public void setEyeConstraint(Constraint constraint) {
		eye().frame().setConstraint(constraint);
	}

	/**
	 * Same as {@code return eye().pointIsVisible(point)}.
	 * 
	 * @see remixlab.dandelion.core.Eye#isPointVisible(Vec)
	 */
	public boolean isPointVisible(Vec point) {
		return eye().isPointVisible(point);
	}

	/**
	 * Same as {@code return eye().ballIsVisible(center, radius)}.
	 * 
	 * @see remixlab.dandelion.core.Eye#ballVisibility(Vec, float)
	 */
	public Eye.Visibility ballVisibility(Vec center, float radius) {
		return eye().ballVisibility(center, radius);
	}

	/**
	 * Same as {@code return eye().boxIsVisible(p1, p2)}.
	 * 
	 * @see remixlab.dandelion.core.Eye#boxVisibility(Vec, Vec)
	 */
	public Eye.Visibility boxVisibility(Vec p1, Vec p2) {
		return eye().boxVisibility(p1, p2);
	}

	/**
	 * Returns {@code true} if automatic update of the camera frustum plane equations is enabled and {@code false}
	 * otherwise. Computation of the equations is expensive and hence is disabled by default.
	 * 
	 * @see #toggleBoundaryEquations()
	 * @see #disableBoundaryEquations()
	 * @see #enableBoundaryEquations()
	 * @see #enableBoundaryEquations(boolean)
	 * @see remixlab.dandelion.core.Camera#updateBoundaryEquations()
	 */
	public boolean areBoundaryEquationsEnabled() {
		return eye().areBoundaryEquationsEnabled();
	}

	/**
	 * Toggles automatic update of the camera frustum plane equations every frame. Computation of the equations is
	 * expensive and hence is disabled by default.
	 * 
	 * @see #areBoundaryEquationsEnabled()
	 * @see #disableBoundaryEquations()
	 * @see #enableBoundaryEquations()
	 * @see #enableBoundaryEquations(boolean)
	 * @see remixlab.dandelion.core.Camera#updateBoundaryEquations()
	 */
	public void toggleBoundaryEquations() {
		if (areBoundaryEquationsEnabled())
			disableBoundaryEquations();
		else
			enableBoundaryEquations();
	}

	/**
	 * Disables automatic update of the camera frustum plane equations every frame. Computation of the equations is
	 * expensive and hence is disabled by default.
	 * 
	 * @see #areBoundaryEquationsEnabled()
	 * @see #toggleBoundaryEquations()
	 * @see #enableBoundaryEquations()
	 * @see #enableBoundaryEquations(boolean)
	 * @see remixlab.dandelion.core.Camera#updateBoundaryEquations()
	 */
	public void disableBoundaryEquations() {
		enableBoundaryEquations(false);
	}

	/**
	 * Enables automatic update of the camera frustum plane equations every frame. Computation of the equations is
	 * expensive and hence is disabled by default.
	 * 
	 * @see #areBoundaryEquationsEnabled()
	 * @see #toggleBoundaryEquations()
	 * @see #disableBoundaryEquations()
	 * @see #enableBoundaryEquations(boolean)
	 * @see remixlab.dandelion.core.Camera#updateBoundaryEquations()
	 */
	public void enableBoundaryEquations() {
		enableBoundaryEquations(true);
	}

	/**
	 * Enables or disables automatic update of the camera frustum plane equations every frame according to {@code flag}.
	 * Computation of the equations is expensive and hence is disabled by default.
	 * 
	 * @see #areBoundaryEquationsEnabled()
	 * @see #toggleBoundaryEquations()
	 * @see #disableBoundaryEquations()
	 * @see #enableBoundaryEquations()
	 * @see remixlab.dandelion.core.Camera#updateBoundaryEquations()
	 */
	public void enableBoundaryEquations(boolean flag) {
		eye().enableBoundaryEquations(flag);
	}

	/**
	 * Toggles the {@link #eye()} type between PERSPECTIVE and ORTHOGRAPHIC.
	 */
	public void toggleCameraType() {
		if (this.is2D()) {
			AbstractScene.showDepthWarning("toggleCameraType");
			return;
		}
		else {
			if (((Camera) eye()).type() == Camera.Type.PERSPECTIVE)
				setCameraType(Camera.Type.ORTHOGRAPHIC);
			else
				setCameraType(Camera.Type.PERSPECTIVE);
		}
	}

	/**
	 * Same as {@code return camera().isFaceBackFacing(a, b, c)}.
	 * <p>
	 * This method is only available in 3D.
	 * 
	 * @see remixlab.dandelion.core.Camera#isFaceBackFacing(Vec, Vec, Vec)
	 */
	public boolean isFaceBackFacing(Vec a, Vec b, Vec c) {
		if (this.is2D()) {
			AbstractScene.showDepthWarning("isFaceBackFacing");
			return false;
		}
		return camera().isFaceBackFacing(a, b, c);
	}

	/**
	 * Same as {@code return camera().isConeBackFacing(vertex, normals)}.
	 * <p>
	 * This method is only available in 3D.
	 * 
	 * @see remixlab.dandelion.core.Camera#isConeBackFacing(Vec, Vec[])
	 */
	public boolean isConeBackFacing(Vec vertex, Vec[] normals) {
		if (this.is2D()) {
			AbstractScene.showDepthWarning("isConeBackFacing");
			return false;
		}
		return camera().isConeBackFacing(vertex, normals);
	}

	/**
	 * Same as {@code return camera().isConeBackFacing(vertex, axis, angle)}.
	 * <p>
	 * This method is only available in 3D.
	 * 
	 * @see remixlab.dandelion.core.Camera#isConeBackFacing(Vec, Vec, float)
	 */
	public boolean isConeBackFacing(Vec vertex, Vec axis, float angle) {
		if (this.is2D()) {
			AbstractScene.showDepthWarning("isConeBackFacing");
			return false;
		}
		return camera().isConeBackFacing(vertex, axis, angle);
	}

	/**
	 * Returns the world coordinates of the 3D point located at {@code pixel} (x,y) on screen. May be null if no pixel is
	 * under pixel.
	 */
	public Vec pointUnderPixel(Point pixel) {
		float depth = pixelDepth(pixel);
		Vec point = unprojectedCoordinatesOf(new Vec(pixel.x(), pixel.y(), depth));
		return (depth < 1.0f) ? point : null;
	}
	
	public Vec pointUnderPixel(float x, float y) {
		return pointUnderPixel(new Point(x, y));
	}

	/**
	 * Returns the depth (z-value) of the object under the {@code pixel}.
	 * <p>
	 * The z-value ranges in [0..1] (near and far plane respectively). In 3D Note that this value is not a linear
	 * interpolation between {@link remixlab.dandelion.core.Camera#zNear()} and
	 * {@link remixlab.dandelion.core.Camera#zFar()}; {@code z = zFar() / (zFar() - zNear()) * (1.0f - zNear() / z');}
	 * where {@code z'} is the distance from the point you project to the camera, along the
	 * {@link remixlab.dandelion.core.Camera#viewDirection()}. See the {@code gluUnProject} man page for details.
	 */
	public abstract float pixelDepth(Point pixel);
	
	public float pixelDepth(float x, float y) {
		return pixelDepth(new Point(x, y));
	}

	/**
	 * Same as {@link remixlab.dandelion.core.Eye#projectedCoordinatesOf(Mat, Vec)}.
	 */
	public Vec projectedCoordinatesOf(Vec src) {
		return eye().projectedCoordinatesOf(this.matrixHelper().projectionView(), src);
	}

	/**
	 * If {@link remixlab.dandelion.core.MatrixHelper#isProjectionViewInverseCached()} (cache version) returns
	 * {@link remixlab.dandelion.core.Eye#unprojectedCoordinatesOf(Mat, Vec)} (Mat is
	 * {@link remixlab.dandelion.core.MatrixHelper#projectionViewInverse()}). Otherwise (non-cache version) returns
	 * {@link remixlab.dandelion.core.Eye#unprojectedCoordinatesOf(Vec)}.
	 */
	public Vec unprojectedCoordinatesOf(Vec src) {
		if (isUnprojectedCoordinatesOfOptimized())
			return eye().unprojectedCoordinatesOf(this.matrixHelper().projectionViewInverse(), src);
		else
			return eye().unprojectedCoordinatesOf(src);
	}

	/**
	 * Returns the scene radius.
	 * <p>
	 * Convenience wrapper function that simply calls {@code camera().sceneRadius()}
	 * 
	 * @see #setRadius(float)
	 * @see #center()
	 */
	public float radius() {
		return eye().sceneRadius();
	}

	/**
	 * Returns the scene center.
	 * <p>
	 * Convenience wrapper function that simply returns {@code camera().sceneCenter()}
	 * 
	 * @see #setCenter(Vec) {@link #radius()}
	 */
	public Vec center() {
		return eye().sceneCenter();
	}

	/**
	 * Returns the {@link remixlab.dandelion.core.Eye#anchor()}.
	 * <p>
	 * Convenience wrapper function that simply returns {@code eye().anchor()}
	 * 
	 * @see #setCenter(Vec) {@link #radius()}
	 */
	public Vec anchor() {
		return eye().anchor();
	}

	/**
	 * Same as {@link remixlab.dandelion.core.Eye#setAnchor(Vec)}.
	 */
	public void setAnchor(Vec anchor) {
		eye().setAnchor(anchor);
	}

	/**
	 * Sets the {@link #radius()} of the Scene.
	 * <p>
	 * Convenience wrapper function that simply returns {@code camera().setSceneRadius(radius)}
	 * 
	 * @see #setCenter(Vec)
	 */
	public void setRadius(float radius) {
		eye().setSceneRadius(radius);
	}

	/**
	 * Sets the {@link #center()} of the Scene.
	 * <p>
	 * Convenience wrapper function that simply calls {@code }
	 * 
	 * @see #setRadius(float)
	 */
	public void setCenter(Vec center) {
		eye().setSceneCenter(center);
	}

	/**
	 * Sets the {@link #center()} and {@link #radius()} of the Scene from the {@code min} and {@code max} vectors.
	 * <p>
	 * Convenience wrapper function that simply calls {@code camera().setSceneBoundingBox(min,max)}
	 * 
	 * @see #setRadius(float)
	 * @see #setCenter(Vec)
	 */
	public void setBoundingBox(Vec min, Vec max) {
		if (this.is2D())
			System.out.println("setBoundingBox is available only in 3D. Use setBoundingRect instead");
		else
			((Camera) eye()).setSceneBoundingBox(min, max);
	}

	public void setBoundingRect(Vec min, Vec max) {
		if (this.is3D())
			System.out.println("setBoundingRect is available only in 2D. Use setBoundingBox instead");
		else
			((Window) eye()).setSceneBoundingBox(min, max);
	}

	/**
	 * Convenience wrapper function that simply calls {@code camera().showEntireScene()}
	 * 
	 * @see remixlab.dandelion.core.Camera#showEntireScene()
	 */
	public void showAll() {
		eye().showEntireScene();
	}

	/**
	 * Convenience wrapper function that simply returns {@code eye().setAnchorFromPixel(pixel)}.
	 * <p>
	 * Current implementation set no {@link remixlab.dandelion.core.Eye#anchor()}. Override
	 * {@link remixlab.dandelion.core.Camera#pointUnderPixel(Point)} in your openGL based camera for this to work.
	 * 
	 * @see remixlab.dandelion.core.Eye#setAnchorFromPixel(Point)
	 * @see remixlab.dandelion.core.Camera#pointUnderPixel(Point)
	 */
	public boolean setAnchorFromPixel(Point pixel) {
		return eye().setAnchorFromPixel(pixel);
	}
	
	public boolean setAnchorFromPixel(float x, float y) {
		return setAnchorFromPixel(new Point(x, y));
	}

	/**
	 * Convenience wrapper function that simply returns {@code camera().setSceneCenterFromPixel(pixel)}
	 * <p>
	 * Current implementation set no {@link remixlab.dandelion.core.Camera#sceneCenter()}. Override
	 * {@link remixlab.dandelion.core.Camera#pointUnderPixel(Point)} in your openGL based camera for this to work.
	 * 
	 * @see remixlab.dandelion.core.Camera#setSceneCenterFromPixel(Point)
	 * @see remixlab.dandelion.core.Camera#pointUnderPixel(Point)
	 */
	public boolean setCenterFromPixel(Point pixel) {
		return eye().setSceneCenterFromPixel(pixel);
	}
	
	public boolean setCenterFromPixel(float x, float y) {
		return setCenterFromPixel(new Point(x, y));
	}

	/**
	 * Returns the current {@link #eye()} type.
	 */
	public final Camera.Type cameraType() {
		if (this.is2D()) {
			System.out.println("Warning: Camera Type is only available in 3D");
			return null;
		}
		else
			return ((Camera) eye()).type();
	}

	/**
	 * Sets the {@link #eye()} type.
	 */
	public void setCameraType(Camera.Type type) {
		if (this.is2D()) {
			System.out.println("Warning: Camera Type is only available in 3D");
		}
		else if (type != ((Camera) eye()).type())
			((Camera) eye()).setType(type);
	}

	// WARNINGS and EXCEPTIONS STUFF

	static protected HashMap<String, Object>	warnings;

	/**
	 * Show warning, and keep track of it so that it's only shown once.
	 * 
	 * @param msg
	 *          the error message (which will be stored for later comparison)
	 */
	static public void showWarning(String msg) { // ignore
		if (warnings == null) {
			warnings = new HashMap<String, Object>();
		}
		if (!warnings.containsKey(msg)) {
			System.err.println(msg);
			warnings.put(msg, new Object());
		}
	}

	/**
	 * Display a warning that the specified method is only available in 3D.
	 * 
	 * @param method
	 *          The method name (no parentheses)
	 */
	static public void showDepthWarning(String method) {
		showWarning(method + "() is not available in 2d");
	}

	/**
	 * Display a warning that the specified Action is only available with 3D.
	 * 
	 * @param action
	 *          the action name (no parentheses)
	 */
	static public void showDepthWarning(MotionAction action) {
		showWarning(action.name() + " is not available in 2D.");
	}

	/**
	 * Display a warning that the specified method lacks implementation.
	 */
	static public void showMissingImplementationWarning(String method, String theclass) {
		showWarning(method + "(), should be implemented by your " + theclass + " derived class.");
	}

	/**
	 * Display a warning that the specified Action lacks implementation.
	 */
	static public void showMissingImplementationWarning(MotionAction action, String theclass) {
		showWarning(action.name() + " should be implemented by your " + theclass + " derived class.");
	}

	/**
	 * Display a warning that the specified Action can only be implemented from a relative bogus event.
	 */
	static public void showEventVariationWarning(MotionAction action) {
		showWarning(action.name() + " can only be performed using a relative event.");
	}

	/**
	 * Display a warning that the specified method can only be implemented from a relative bogus event.
	 */
	static public void showEventVariationWarning(String method) {
		showWarning(method + " can only be performed using a relative event.");
	}

	static public void showOnlyEyeWarning(MotionAction action) {
		showOnlyEyeWarning(action, true);
	}

	/**
	 * Display a warning that the specified Action is only available for the Eye frame.
	 */
	static public void showOnlyEyeWarning(MotionAction action, boolean eye) {
		if (eye)
			showWarning(action.name() + " can only be performed when frame is attached to an eye.");
		else
			showWarning(action.name() + " can only be performed when frame is detached from an eye.");
	}

	static public void showOnlyEyeWarning(String method) {
		showOnlyEyeWarning(method, true);
	}

	/**
	 * Display a warning that the specified method is only available for a frame (but not an eye-frame).
	 */
	static public void showOnlyEyeWarning(String method, boolean eye) {
		if (eye)
			showWarning(method + "() can only be performed when frame is attached to an eye.");
		else
			showWarning(method + "() can only be performed when frame detached from an eye.");
	}

	/**
	 * Display a warning that the specified method is not available under the specified platform.
	 */
	static public void showPlatformVariationWarning(String themethod, Platform platform) {
		showWarning(themethod + " is not available under the " + platform + " platform.");
	}

	static public void showClickWarning(MotionAction action) {
		showWarning(action.name() + " cannot be performed from a ClickEvent.");
	}

	static public void showMotionWarning(MotionAction action) {
		showWarning(action.name() + " cannot be performed from a MotionEvent.");
	}

	static public void showKeyboardWarning(MotionAction action) {
		showWarning(action.name() + " cannot only be performed from a KeyboardEvent.");
	}

	static public void showMinDOFsWarning(String themethod, int dofs) {
		showWarning(themethod + "() requires at least a " + dofs + " dofs.");
	}

	// NICE STUFF

	/**
	 * Apply the local transformation defined by {@code frame}, i.e., respect to the frame
	 * {@link remixlab.dandelion.geom.Frame#referenceFrame()}. The Frame is first translated and then rotated around the
	 * new translated origin.
	 * <p>
	 * This method may be used to modify the modelview matrix from a Frame hierarchy. For example, with this Frame
	 * hierarchy:
	 * <p>
	 * {@code Frame body = new Frame();} <br>
	 * {@code Frame leftArm = new Frame();} <br>
	 * {@code Frame rightArm = new Frame();} <br>
	 * {@code leftArm.setReferenceFrame(body);} <br>
	 * {@code rightArm.setReferenceFrame(body);} <br>
	 * <p>
	 * The associated drawing code should look like:
	 * <p>
	 * {@code pushModelView();} <br>
	 * {@code applyTransformation(body);} <br>
	 * {@code drawBody();} <br>
	 * {@code pushModelView();} <br>
	 * {@code applyTransformation(leftArm);} <br>
	 * {@code drawArm();} <br>
	 * {@code popMatrix();} <br>
	 * {@code pushMatrix();} <br>
	 * {@code applyTransformation(rightArm);} <br>
	 * {@code drawArm();} <br>
	 * {@code popModelView();} <br>
	 * {@code popModelView();} <br>
	 * <p>
	 * Note the use of nested {@link #pushModelView()} and {@link #popModelView()} blocks to represent the frame
	 * hierarchy: {@code leftArm} and {@code rightArm} are both correctly drawn with respect to the {@code body}
	 * coordinate system.
	 * <p>
	 * <b>Attention:</b> When drawing a frame hierarchy as above, this method should be used whenever possible.
	 * 
	 * @see #applyWorldTransformation(Frame)
	 */
	public void applyTransformation(Frame frame) {
		if (is2D()) {
			translate(frame.translation().x(), frame.translation().y());
			rotate(frame.rotation().angle());
			scale(frame.scaling(), frame.scaling());
		}
		else {
			translate(frame.translation().vec[0], frame.translation().vec[1], frame.translation().vec[2]);
			rotate(frame.rotation().angle(), ((Quat) frame.rotation()).axis().vec[0],
					((Quat) frame.rotation()).axis().vec[1], ((Quat) frame.rotation()).axis().vec[2]);
			scale(frame.scaling(), frame.scaling(), frame.scaling());
		}
	}

	/**
	 * Same as {@link #applyTransformation(Frame)} but applies the global transformation defined by the frame.
	 */
	public void applyWorldTransformation(Frame frame) {
		// TODO check for beta2 doing these with frames position(), orientation() and magnitude()
		Frame refFrame = frame.referenceFrame();
		if (refFrame != null) {
			applyWorldTransformation(refFrame);
			applyTransformation(frame);
		}
		else {
			applyTransformation(frame);
		}
	}

	/**
	 * This method is called before the first drawing happen and should be overloaded to initialize stuff. The default
	 * implementation is empty.
	 * <p>
	 * Typical usage include {@link #eye()} initialization ({@link #showAll()}) and Scene state setup (
	 * {@link #setAxesVisualHint(boolean)} and {@link #setGridVisualHint(boolean)}.
	 */
	public void init() {
	}

	/**
	 * The method that actually defines the scene.
	 * <p>
	 * If you build a class that inherits from Scene, this is the method you should overload, but no if you instantiate
	 * your own Scene object (for instance, in Processing you should just overload {@code PApplet.draw()} to define your
	 * scene).
	 * <p>
	 * The eye matrices set in {@link #bindMatrices()} converts from the world to the camera coordinate systems. Thus
	 * vertices given here can then be considered as being given in the world coordinate system. The eye is moved in this
	 * world using the mouse. This representation is much more intuitive than a camera-centric system (which for instance
	 * is the standard in OpenGL).
	 */
	public void proscenium() {
	}

	// GENERAL STUFF

	/**
	 * Returns true if scene is left handed. Note that the scene is right handed by default. However in proscene we set it
	 * as right handed (same as with P5).
	 * 
	 * @see #setLeftHanded()
	 */
	public boolean isLeftHanded() {
		return !rightHanded;
	}

	/**
	 * Returns true if scene is right handed. Note that the scene is right handed by default. However in proscene we set
	 * it as right handed (same as with P5).
	 * 
	 * @see #setRightHanded()
	 */
	public boolean isRightHanded() {
		return rightHanded;
	}

	/**
	 * Set the scene as right handed.
	 * 
	 * @see #isRightHanded()
	 */
	public void setRightHanded() {
		rightHanded = true;
	}

	/**
	 * Set the scene as left handed.
	 * 
	 * @see #isLeftHanded()
	 */
	public void setLeftHanded() {
		rightHanded = false;
	}

	/**
	 * Returns {@code true} if this Scene is associated to an off-screen renderer and {@code false} otherwise.
	 */
	public boolean isOffscreen() {
		return offscreen;
	}

	/**
	 * @return true if the scene is 2D.
	 */
	public boolean is2D() {
		return !is3D();
	}

	/**
	 * @return true if the scene is 3D.
	 */
	public abstract boolean is3D();

	// dimensions

	/**
	 * Returns the {@link #width()} to {@link #height()} aspect ratio of the display window.
	 */
	public float aspectRatio() {
		return (float) width() / (float) height();
	}

	/**
	 * Returns true grid is dotted.
	 */
	public boolean gridIsDotted() {
		return dottedGrid;
	}

	/**
	 * Sets the drawing of the grid visual hint as dotted or not.
	 */
	public void setDottedGrid(boolean dotted) {
		dottedGrid = dotted;
	}

	// ABSTRACT STUFF

	/**
	 * @return width of the screen window.
	 */
	public abstract int width();

	/**
	 * @return height of the screen window.
	 */
	public abstract int height();

	/**
	 * Disables z-buffer.
	 */
	public abstract void disableDepthTest();

	/**
	 * Enables z-buffer.
	 */
	public abstract void enableDepthTest();
}
