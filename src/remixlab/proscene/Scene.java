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

import processing.core.*;
import processing.opengl.*;
import remixlab.bias.core.*;
import remixlab.bias.event.DOF2Event;
import remixlab.dandelion.agent.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.fpstiming.*;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

// begin: GWT-incompatible
///*
import java.lang.reflect.Method;
import java.nio.FloatBuffer;
// end: GWT-incompatible
//*/

/**
 * A 2D or 3D interactive Processing Scene. The Scene is a specialization of the
 * {@link remixlab.dandelion.core.AbstractScene}, providing an interface between Dandelion and Processing.
 * <p>
 * <h3>Usage</h3>
 * To use a Scene you have three choices:
 * <ol>
 * <li><b>Direct instantiation</b>. In this case you should instantiate your own Scene object at the
 * {@code PApplet.setup()} function. See the example <i>BasicUse</i>.
 * <li><b>Inheritance</b>. In this case, once you declare a Scene derived class, you should implement
 * {@link #proscenium()} which defines the objects in your scene. Just make sure to define the {@code PApplet.draw()}
 * method, even if it's empty. See the example <i>AlternativeUse</i>.
 * <li><b>External draw handler registration</b>. In addition (not being part of Dandelion), you can even declare an
 * external drawing method and then register it at the Scene with {@link #addGraphicsHandler(Object, String)}. That
 * method should return {@code void} and have one single {@code Scene} parameter. This strategy may be useful when there
 * are multiple viewers sharing the same drawing code. See the example <i>StandardCamera</i>.
 * </ol>
 * <h3>Interactivity mechanisms</h3>
 * 
 * ProScene provides powerful interactivity mechanisms allowing a wide range of scene setups ranging from very simple to
 * complex ones. For convenience, two interaction mechanisms are provided by default: {@link #keyboardAgent()}, and
 * {@link #motionAgent()} (which in the desktop version of proscene defaults to a {@link #mouseAgent()}):
 * <ol>
 * <li><b>The default keyboard agent</b> provides shortcuts to Dandelion keyboard actions such as {@link #drawGrid()} or
 * {@link #drawAxes()}. See {@link #setKeyboardBinding(Character, remixlab.dandelion.core.Constants.SceneAction)} and
 * {@link #setKeyboardBinding(int, int, remixlab.dandelion.core.Constants.SceneAction)}.
 * <li><b>The default mouse agent</b> provides high-level methods to manage Eye and Frame motion actions. Please refer
 * to the different {@code setMouseButtonBinding()}, {@code setMouseClickBinding()}, {@code setMouseWheelBinding()}
 * methods.
 * </ol>
 * <h3>Animation mechanisms</h3>
 * ProScene provides three animation mechanisms to define how your scene evolves over time:
 * <ol>
 * <li><b>Overriding the Dandelion {@link #animate()} method.</b> In this case, once you declare a Scene derived class,
 * you should implement {@link #animate()} which defines how your scene objects evolve over time. See the example
 * <i>Animation</i>.
 * <li><b>By checking if the Dandelion AbstractScene's {@link #timer()} was triggered within the frame.</b> See the
 * example <i>Flock</i>.
 * <li><b>External animation handler registration.</b> In addition (not being part of Dandelion), you can also declare
 * an external animation method and then register it at the Scene with {@link #addAnimationHandler(Object, String)}.
 * That method should return {@code void} and have one single {@code Scene} parameter. See the example
 * <i>AnimationHandler</i>.
 */
public class Scene extends AbstractScene implements PConstants {
	// begin: GWT-incompatible
	// /*
	// Reflection
	// 1. Draw
	protected Object						drawHandlerObject;
	// The method in drawHandlerObject to execute
	protected Method						drawHandlerMethod;
	// the name of the method to handle the event
	protected String						drawHandlerMethodName;
	// 2. Animation
	// The object to handle the animation
	protected Object						animateHandlerObject;
	// The method in animateHandlerObject to execute
	protected Method						animateHandlerMethod;
	// the name of the method to handle the animation
	protected String						animateHandlerMethodName;

	// Timing
	protected boolean						javaTiming;
	// end: GWT-incompatible
	// */

	public static final String	prettyVersion	= "3.0.0";

	public static final String	version				= "23";

	// P R O C E S S I N G A P P L E T A N D O B J E C T S
	protected PApplet						parent;
	protected PGraphics					pgraphics;

	// Models
	protected static int				modelCount;
	protected PGraphics					pickingBuffer;
	protected List<Model>				models;

	// E X C E P T I O N H A N D L I N G
	protected int								beginOffScreenDrawingCalls;

	// CONSTRUCTORS

	/**
	 * Constructor that defines an on-screen Processing Scene. Same as {@code this(p, p.g}.
	 * 
	 * @see #Scene(PApplet, PGraphics)
	 * @see #Scene(PApplet, PGraphics, int, int)
	 */
	public Scene(PApplet p) {
		this(p, p.g);
	}

	/**
	 * Same as {@code this(p, renderer, 0, 0)}.
	 * 
	 * @see #Scene(PApplet)
	 * @see #Scene(PApplet, PGraphics, int, int)
	 */
	public Scene(PApplet p, PGraphics renderer) {
		this(p, renderer, 0, 0);
	}

	/**
	 * Main constructor defining a left-handed Processing compatible Scene. Calls {@link #setMatrixHelper(MatrixHelper)}
	 * using a customized {@link remixlab.dandelion.core.MatrixHelper} depending on the {@code pg} type (see
	 * {@link remixlab.proscene.Java2DMatrixHelper} and {@link remixlab.proscene.GLMatrixHelper}). The constructor
	 * instantiates the {@link #inputHandler()} and the {@link #timingHandler()}, sets the AXIS and GRID visual hint
	 * flags, instantiates the {@link #eye()} (a {@link remixlab.dandelion.core.Camera} if the Scene {@link #is3D()} or a
	 * {@link remixlab.dandelion.core.Window} if the Scene {@link #is2D()}). It also instantiates the
	 * {@link #keyboardAgent()} and the {@link #mouseAgent()}, and finally calls {@link #init()}.
	 * <p>
	 * An off-screen Processing Scene is defined if {@code pg != p.g}. In this case the {@code x} and {@code y} parameters
	 * define the position of the upper-left corner where the off-screen Scene is expected to be displayed, e.g., for
	 * instance with a call to Processing the {@code image(img, x, y)} function. If {@code pg == p.g}) (which defines an
	 * on-screen Scene, see also {@link #isOffscreen()}), the values of x and y are meaningless (both are set to 0 to be
	 * taken as dummy values).
	 * 
	 * @see remixlab.dandelion.core.AbstractScene#AbstractScene()
	 * @see #Scene(PApplet)
	 * @see #Scene(PApplet, PGraphics)
	 */
	public Scene(PApplet p, PGraphics pg, int x, int y) {
		// 1. P5 objects
		parent = p;
		pgraphics = pg;
		offscreen = pg != p.g;
		upperLeftCorner = offscreen ? new Point(x, y) : new Point(0, 0);

		// 2. Matrix helper
		setMatrixHelper(matrixHelper(pg));

		// 3. Models & picking buffer
		models = new ArrayList<Model>();
		pickingBuffer = (pg() instanceof processing.opengl.PGraphicsOpenGL) ? pApplet().createGraphics(pg().width,
				pg().height, pg() instanceof PGraphics3D ? P3D : P2D) : pApplet().createGraphics(pg().width, pg().height,
				JAVA2D);

		// 4. (TODO prev 6.) Create agents and register P5 methods
		if (platform() == Platform.PROCESSING_ANDROID) {
			defMotionAgent = new DroidTouchAgent(this, "proscene_touch");
			defKeyboardAgent = new DroidKeyAgent(this, "proscene_keyboard");
		} else {
			defMotionAgent = new MouseAgent(this, "proscene_mouse");
			defKeyboardAgent = new KeyAgent(this, "proscene_keyboard");
			parent.registerMethod("mouseEvent", motionAgent());
		}
		parent.registerMethod("keyEvent", keyboardAgent());
		pApplet().registerMethod("pre", this);
		pApplet().registerMethod("draw", this);

		// Android: remove the following 2 lines if needed to compile the project
		if (platform() == Platform.PROCESSING_DESKTOP)
			pApplet().registerMethod("post", this);// -> handle picking buffer

		// 5. (TODO prev 4.) Eye
		setLeftHanded();
		width = pg.width;
		height = pg.height;
		eye = is3D() ? new Camera(this) : new Window(this);
		setEye(eye());// calls showAll();

		// 6. Misc stuff:
		setDottedGrid(!(platform() == Platform.PROCESSING_ANDROID || is2D()));
		if (platform() == Platform.PROCESSING_DESKTOP || platform() == Platform.PROCESSING_ANDROID)
			this.setNonSeqTimers();
		// pApplet().frameRate(100);

		// 7. Init should be called only once
		init();
	}

	// P5 STUFF

	/**
	 * Returns the PApplet instance this Scene is related to.
	 */
	public PApplet pApplet() {
		return parent;
	}

	/**
	 * Returns the PGraphics instance this Scene is related to. It may be the PApplets one, if the Scene is on-screen or
	 * an user-defined if the Scene {@link #isOffscreen()}.
	 */
	public PGraphics pg() {
		return pgraphics;
	}

	public PGraphics pickingBuffer() {
		return pickingBuffer;
	}

	@Override
	public int width() {
		return pg().width;
	}

	@Override
	public int height() {
		return pg().height;
	}

	// DIM

	@Override
	public boolean is3D() {
		return (pgraphics instanceof PGraphics3D);
	}

	// CHOOSE PLATFORM

	@Override
	protected void setPlatform() {
		Properties p = System.getProperties();
		Enumeration<?> keys = p.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = (String) p.get(key);
			if (key.contains("java.vm.vendor")) {
				if (Pattern.compile(Pattern.quote("Android"), Pattern.CASE_INSENSITIVE).matcher(value).find())
					platform = Platform.PROCESSING_ANDROID;
				else
					platform = Platform.PROCESSING_DESKTOP;
				break;
			}
		}
	}

	// P5-WRAPPERS

	/**
	 * Wrapper for PGraphics.vertex(x,y,z)
	 */
	public void vertex(float x, float y, float z) {
		if (this.is2D())
			pg().vertex(x, y);
		else
			pg().vertex(x, y, z);
	}

	/**
	 * Wrapper for PGraphics.vertex(x,y)
	 */
	public void vertex(float x, float y) {
		pg().vertex(x, y);
	}

	/**
	 * Wrapper for PGraphics.line(x1, y1, z1, x2, y2, z2)
	 */
	public void line(float x1, float y1, float z1, float x2, float y2, float z2) {
		if (this.is2D())
			pg().line(x1, y1, x2, y2);
		else
			pg().line(x1, y1, z1, x2, y2, z2);
	}

	/**
	 * Wrapper for PGraphics.line(x1, y1, x2, y2)
	 */
	public void line(float x1, float y1, float x2, float y2) {
		pg().line(x1, y1, x2, y2);
	}

	/**
	 * Converts a {@link remixlab.dandelion.geom.Vec} to a PVec.
	 */
	public static PVector toPVector(Vec v) {
		return new PVector(v.x(), v.y(), v.z());
	}

	/**
	 * Converts a PVec to a {@link remixlab.dandelion.geom.Vec}.
	 */
	public static Vec toVec(PVector v) {
		return new Vec(v.x, v.y, v.z);
	}

	/**
	 * Converts a {@link remixlab.dandelion.geom.Mat} to a PMatrix3D.
	 */
	public static PMatrix3D toPMatrix(Mat m) {
		float[] a = m.getTransposed(new float[16]);
		return new PMatrix3D(a[0], a[1], a[2], a[3],
				a[4], a[5], a[6], a[7],
				a[8], a[9], a[10], a[11],
				a[12], a[13], a[14], a[15]);
	}

	/**
	 * Converts a PMatrix3D to a {@link remixlab.dandelion.geom.Mat}.
	 */
	public static Mat toMat(PMatrix3D m) {
		return new Mat(m.get(new float[16]), true);
	}

	/**
	 * Converts a PMatrix2D to a {@link remixlab.dandelion.geom.Mat}.
	 */
	public static Mat toMat(PMatrix2D m) {
		return toMat(new PMatrix3D(m));
	}

	/**
	 * Converts a {@link remixlab.dandelion.geom.Mat} to a PMatrix2D.
	 */
	public static PMatrix2D toPMatrix2D(Mat m) {
		float[] a = m.getTransposed(new float[16]);
		return new PMatrix2D(a[0], a[1], a[3],
				a[4], a[5], a[7]);
	}

	// firstly, of course, dirty things that I used to love :P

	// DEFAULT MOTION-AGENT

	/**
	 * Enables Proscene mouse handling through the {@link #mouseAgent()}.
	 * 
	 * @see #isMotionAgentEnabled()
	 * @see #disableMotionAgent()
	 * @see #enableKeyboardAgent()
	 */
	@Override
	public void enableMotionAgent() {
		if (!isMotionAgentEnabled()) {
			inputHandler().registerAgent(motionAgent());
			parent.registerMethod("mouseEvent", motionAgent());
		}
	}

	/**
	 * Disables the default mouse agent and returns it.
	 * 
	 * @see #isMotionAgentEnabled()
	 * @see #enableMotionAgent()
	 * @see #enableKeyboardAgent()
	 */
	@Override
	public Agent disableMotionAgent() {
		if (isMotionAgentEnabled()) {
			parent.unregisterMethod("mouseEvent", motionAgent());
			return inputHandler().unregisterAgent(motionAgent());
		}
		return motionAgent();
	}

	/**
	 * Returns the default mouse agent handling Processing mouse events. If you plan to customize your mouse use this
	 * method.
	 * 
	 * @see #keyboardAgent()
	 */
	public MouseAgent mouseAgent() {
		if (platform() == Platform.PROCESSING_ANDROID) {
			throw new RuntimeException("Proscene mouseAgent() is not available in Android mode");
		}
		return (MouseAgent) defMotionAgent;
	}

	/**
	 * Returns the default touch agent handling touch events. If you plan to customize your touch use this method.
	 * 
	 * @see #keyboardAgent()
	 */
	public DroidTouchAgent touchAgent() {
		if (platform() == Platform.PROCESSING_DESKTOP) {
			throw new RuntimeException("Proscene touchAgent() is not available in Desktop mode");
		}
		return (DroidTouchAgent) defMotionAgent;
	}

	// TODO doc me and re-add me
	/*
	 * public DroidTouchAgent droidTouchAgent() { if (platform() != Platform.PROCESSING_ANDROID) { throw new
	 * RuntimeException("Proscene droidTouchAgent() is not available in Desktop mode"); } return (DroidTouchAgent)
	 * motionAgent(); }
	 */

	// KEYBOARD

	/**
	 * Enables Proscene keyboard handling through the {@link #keyboardAgent()}.
	 * 
	 * @see #isKeyboardAgentEnabled()
	 * @see #disableKeyboardAgent()
	 * @see #enableMotionAgent()
	 */
	@Override
	public void enableKeyboardAgent() {
		if (!isKeyboardAgentEnabled()) {
			inputHandler().registerAgent(keyboardAgent());
			parent.registerMethod("keyEvent", keyboardAgent());
		}
	}

	/**
	 * Disables the default keyboard agent and returns it.
	 * 
	 * @see #isKeyboardAgentEnabled()
	 * @see #enableKeyboardAgent()
	 * @see #disableMotionAgent()
	 */
	@Override
	public KeyboardAgent disableKeyboardAgent() {
		if (inputHandler().isAgentRegistered(keyboardAgent())) {
			parent.unregisterMethod("keyEvent", keyboardAgent());
			return (KeyboardAgent) inputHandler().unregisterAgent(keyboardAgent());
		}
		return keyboardAgent();
	}

	// INFO

	@Override
	public String info() {
		String info = super.info();

		// info PARSING

		// 1. We first parse the mouse agent info (the one contained in super.info())

		String l = "ID_" + String.valueOf(MouseAgent.LEFT_ID);
		String r = "ID_" + String.valueOf(MouseAgent.RIGHT_ID);
		String c = "ID_" + String.valueOf(MouseAgent.CENTER_ID);
		String w = "ID_" + String.valueOf(WheeledMouseAgent.WHEEL_ID);
		String n = "ID_0";

		// ... and replace it with proper descriptions:

		info = info.replace(l, "LEFT_BUTTON").replace(r, "RIGHT_BUTTON").replace(c, "CENTER_BUTTON").replace(w, "WHEEL")
				.replace(n, "NO_BUTTON");
		String keyboardtitle = keyboardAgent().name()
				+ " (key-codes are defined here: http://docs.oracle.com/javase/7/docs/api/constant-values.html)";
		info = info.replace(keyboardAgent().name(), keyboardtitle);

		// 2. keyboard parsing is split in two steps:

		// 2a. Parse the "1", "2", "3" and left-right-up-down keys:

		String vk_1 = "VKEY_" + String.valueOf(49);
		String vk_2 = "VKEY_" + String.valueOf(50);
		String vk_3 = "VKEY_" + String.valueOf(51);
		String vk_l = "VKEY_" + String.valueOf(37);
		String vk_u = "VKEY_" + String.valueOf(38);
		String vk_r = "VKEY_" + String.valueOf(39);
		String vk_d = "VKEY_" + String.valueOf(40);

		// ... and replace it with proper descriptions:

		info = info.replace(vk_1, "'1'").replace(vk_2, "'2'").replace(vk_3, "'3'")
				.replace(vk_l, "LEFT_vkey").replace(vk_u, "UP_vkey").replace(vk_r, "RIGHT_vkey").replace(vk_d, "DOWN_vkey");

		// 2b. Parse the remaining virtual key codes:

		/*
		 * //TODO (far fancier than the mouse agent)
		 * 
		 * Search for the following pattern in info string: "VKEY_id " (note the final white space)
		 * 
		 * where id is the virtual key code (as defined in the above url)
		 * 
		 * and replace it with: "char"
		 * 
		 * where char is the key bound to that code, i.e., the one obtained with the following code: char char=(char)id_int
		 * 
		 * where id_int is the integer representation of the id char (see:
		 * http://stackoverflow.com/questions/15991822/java-converting-keycode-to-string-or-char);
		 * 
		 * Note that id_int should be obtained from "_id " before the actual replacement.
		 * 
		 * See: http://www.vogella.com/tutorials/JavaRegularExpressions/article.html
		 */

		return info;
	}

	@Override
	public void displayInfo(boolean onConsole) {
		if (onConsole)
			System.out.println(info());
		else { // on applet
			pg().textFont(parent.createFont("Arial", 12));
			beginScreenDrawing();
			pg().fill(0, 255, 0);
			pg().textLeading(20);
			pg().text(info(), 10, 10, (pg().width - 20), (pg().height - 20));
			endScreenDrawing();
		}
	}

	// begin: GWT-incompatible
	// /*

	// TIMING

	@Override
	public void registerTimingTask(TimingTask task) {
		if (areTimersSeq())
			timingHandler().registerTask(task);
		else
			timingHandler().registerTask(task, new NonSeqTimer(this, task));
	}

	/**
	 * Sets all {@link #timingHandler()} timers as (single-threaded) {@link remixlab.fpstiming.SeqTimer}(s).
	 * 
	 * @see #setNonSeqTimers()
	 * @see #switchTimers()
	 * @see #areTimersSeq()
	 */
	public void setSeqTimers() {
		if (areTimersSeq())
			return;

		javaTiming = false;
		timingHandler().restoreTimers();
	}

	/**
	 * Sets all {@link #timingHandler()} timers as (multi-threaded) java.util.Timer(s).
	 * 
	 * @see #setSeqTimers()
	 * @see #switchTimers()
	 * @see #areTimersSeq()
	 */
	public void setNonSeqTimers() {
		if (!areTimersSeq())
			return;

		boolean isActive;

		for (TimingTask task : timingHandler().timerPool()) {
			long period = 0;
			boolean rOnce = false;
			isActive = task.isActive();
			if (isActive) {
				period = task.period();
				rOnce = task.timer().isSingleShot();
			}
			task.stop();
			task.setTimer(new NonSeqTimer(this, task));
			if (isActive) {
				if (rOnce)
					task.runOnce(period);
				else
					task.run(period);
			}
		}

		javaTiming = true;
		PApplet.println("java util timers set");
	}

	/**
	 * @return true, if timing is handling sequentially (i.e., all {@link #timingHandler()} timers are (single-threaded)
	 *         {@link remixlab.fpstiming.SeqTimer}(s)).
	 * 
	 * @see #setSeqTimers()
	 * @see #setNonSeqTimers()
	 * @see #switchTimers()
	 */
	public boolean areTimersSeq() {
		return !javaTiming;
	}

	/**
	 * If {@link #areTimersSeq()} calls {@link #setNonSeqTimers()}, otherwise call {@link #setSeqTimers()}.
	 */
	public void switchTimers() {
		if (areTimersSeq())
			setNonSeqTimers();
		else
			setSeqTimers();
	}

	// DRAW METHOD REG

	@Override
	protected boolean invokeGraphicsHandler() {
		// 3. Draw external registered method
		if (drawHandlerObject != null) {
			try {
				drawHandlerMethod.invoke(drawHandlerObject, new Object[] { this });
				return true;
			} catch (Exception e) {
				PApplet.println("Something went wrong when invoking your " + drawHandlerMethodName + " method");
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	/**
	 * Attempt to add a 'draw' handler method to the Scene. The default event handler is a method that returns void and
	 * has one single Scene parameter.
	 * 
	 * @param obj
	 *          the object to handle the event
	 * @param methodName
	 *          the method to execute in the object handler class
	 * 
	 * @see #removeGraphicsHandler()
	 * @see #invokeGraphicsHandler()
	 */
	public void addGraphicsHandler(Object obj, String methodName) {
		try {
			drawHandlerMethod = obj.getClass().getMethod(methodName, new Class<?>[] { Scene.class });
			drawHandlerObject = obj;
			drawHandlerMethodName = methodName;
		} catch (Exception e) {
			PApplet.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}

	/**
	 * Unregisters the 'draw' handler method (if any has previously been added to the Scene).
	 * 
	 * @see #addGraphicsHandler(Object, String)
	 * @see #invokeGraphicsHandler()
	 */
	public void removeGraphicsHandler() {
		drawHandlerMethod = null;
		drawHandlerObject = null;
		drawHandlerMethodName = null;
	}

	/**
	 * Returns {@code true} if the user has registered a 'draw' handler method to the Scene and {@code false} otherwise.
	 * 
	 * @see #addGraphicsHandler(Object, String)
	 * @see #invokeGraphicsHandler()
	 */
	public boolean hasGraphicsHandler() {
		if (drawHandlerMethodName == null)
			return false;
		return true;
	}

	// ANIMATION METHOD REG

	@Override
	public boolean invokeAnimationHandler() {
		if (animateHandlerObject != null) {
			try {
				animateHandlerMethod.invoke(animateHandlerObject, new Object[] { this });
				return true;
			} catch (Exception e) {
				PApplet.println("Something went wrong when invoking your " + animateHandlerMethodName + " method");
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	/**
	 * Attempt to add an 'animation' handler method to the Scene. The default event handler is a method that returns void
	 * and has one single Scene parameter.
	 * 
	 * @param obj
	 *          the object to handle the event
	 * @param methodName
	 *          the method to execute in the object handler class
	 * 
	 * @see #animate()
	 * @see #removeAnimationHandler()
	 */
	public void addAnimationHandler(Object obj, String methodName) {
		try {
			animateHandlerMethod = obj.getClass().getMethod(methodName, new Class<?>[] { Scene.class });
			animateHandlerObject = obj;
			animateHandlerMethodName = methodName;
		} catch (Exception e) {
			PApplet.println("Something went wrong when registering your " + methodName + " method");
			e.printStackTrace();
		}
	}

	/**
	 * Unregisters the 'animation' handler method (if any has previously been added to the Scene).
	 * 
	 * @see #addAnimationHandler(Object, String)
	 */
	public void removeAnimationHandler() {
		animateHandlerMethod = null;
		animateHandlerObject = null;
		animateHandlerMethodName = null;
	}

	/**
	 * Returns {@code true} if the user has registered an 'animation' handler method to the Scene and {@code false}
	 * otherwise.
	 * 
	 * @see #addAnimationHandler(Object, String)
	 * @see #removeAnimationHandler()
	 */
	public boolean hasAnimationHandler() {
		if (animateHandlerMethodName == null)
			return false;
		return true;
	}

	// OPENGL

	@Override
	public float pixelDepth(Point pixel) {
		PGraphicsOpenGL pggl;
		if (pg() instanceof PGraphicsOpenGL)
			pggl = (PGraphicsOpenGL) pg();
		else
			throw new RuntimeException("pg() is not instance of PGraphicsOpenGL");
		float[] depth = new float[1];
		PGL pgl = pggl.beginPGL();
		pgl.readPixels(pixel.x(), (camera().screenHeight() - pixel.y()), 1, 1, PGL.DEPTH_COMPONENT, PGL.FLOAT,
				FloatBuffer.wrap(depth));
		pggl.endPGL();
		return depth[0];
	}

	@Override
	public void disableDepthTest() {
		pg().hint(PApplet.DISABLE_DEPTH_TEST);
	}

	@Override
	public void enableDepthTest() {
		pg().hint(PApplet.ENABLE_DEPTH_TEST);
	}

	// end: GWT-incompatible
	// */

	// 3. Drawing methods

	/**
	 * Paint method which is called just before your {@code PApplet.draw()} method. Simply calls {@link #preDraw()}. This
	 * method is registered at the PApplet and hence you don't need to call it.
	 * <p>
	 * If {@link #isOffscreen()} does nothing.
	 * <p>
	 * If {@link #pg()} is resized then (re)sets the scene {@link #width()} and {@link #height()}, and calls
	 * {@link remixlab.dandelion.core.Eye#setScreenWidthAndHeight(int, int)}.
	 * 
	 * @see #draw()
	 * @see #preDraw()
	 * @see #postDraw()
	 * @see #beginDraw()
	 * @see #endDraw()
	 * @see #isOffscreen()
	 */
	public void pre() {
		if (isOffscreen())
			return;

		if ((width != pg().width) || (height != pg().height)) {
			width = pg().width;
			height = pg().height;
			eye().setScreenWidthAndHeight(width, height);
		}

		preDraw();
	}

	/**
	 * Paint method which is called just after your {@code PApplet.draw()} method. Simply calls {@link #postDraw()}. This
	 * method is registered at the PApplet and hence you don't need to call it.
	 * <p>
	 * If {@link #isOffscreen()} does nothing.
	 * 
	 * @see #pre()
	 * @see #preDraw()
	 * @see #postDraw()
	 * @see #beginDraw()
	 * @see #endDraw()
	 * @see #isOffscreen()
	 */
	public void draw() {
		if (isOffscreen())
			return;
		postDraw();
	}

	// Android: remove this method if needed to compile the project
	public void post() {
		// draw into picking buffer
		pickingBuffer().beginDraw();
		pickingBuffer().pushStyle();
		pickingBuffer().background(0);
		if (models().size() > 0)
			drawModels(pickingBuffer());
		pickingBuffer().popStyle();
		pickingBuffer().endDraw();
		if (models().size() > 0)
			pickingBuffer().loadPixels();
	}

	/**
	 * Only if the Scene {@link #isOffscreen()}. This method should be called just after the {@link #pg()} beginDraw()
	 * method. Simply calls {@link #preDraw()}.
	 * <p>
	 * If {@link #pg()} is resized then (re)sets the scene {@link #width()} and {@link #height()}, and calls
	 * {@link remixlab.dandelion.core.Eye#setScreenWidthAndHeight(int, int)}.
	 * 
	 * @see #draw()
	 * @see #preDraw()
	 * @see #postDraw()
	 * @see #pre()
	 * @see #endDraw()
	 * @see #isOffscreen()
	 */
	public void beginDraw() {
		if (!isOffscreen())
			throw new RuntimeException(
					"begin(/end)Draw() should be used only within offscreen scenes. Check your implementation!");

		if (beginOffScreenDrawingCalls != 0)
			throw new RuntimeException("There should be exactly one beginDraw() call followed by a "
					+ "endDraw() and they cannot be nested. Check your implementation!");

		beginOffScreenDrawingCalls++;

		if ((width != pg().width) || (height != pg().height)) {
			width = pg().width;
			height = pg().height;
			eye().setScreenWidthAndHeight(width, height);
		}

		preDraw();
	}

	/**
	 * Only if the Scene {@link #isOffscreen()}. This method should be called just before {@link #pg()} endDraw() method.
	 * Simply calls {@link #postDraw()}.
	 * 
	 * @see #draw()
	 * @see #preDraw()
	 * @see #postDraw()
	 * @see #beginDraw()
	 * @see #pre()
	 * @see #isOffscreen()
	 */
	public void endDraw() {
		if (!isOffscreen())
			throw new RuntimeException(
					"(begin/)endDraw() should be used only within offscreen scenes. Check your implementation!");

		beginOffScreenDrawingCalls--;

		if (beginOffScreenDrawingCalls != 0)
			throw new RuntimeException(
					"There should be exactly one beginDraw() call followed by a "
							+ "endDraw() and they cannot be nested. Check your implementation!");

		postDraw();
	}

	/**
	 * Returns all the models handled by the scene.
	 * 
	 * @see #drawModels()
	 * @see #drawModels(PGraphics)
	 * @see #addModel(Model)
	 * @see #removeModel(Model)
	 */
	public List<Model> models() {
		return models;
	}

	/**
	 * Add the {@code model} into the scene. Does nothing if the current models belongs to the scene.
	 * 
	 * @see #models()
	 * @see #drawModels()
	 * @see #drawModels(PGraphics)
	 * @see #removeModel(Model)
	 */
	public boolean addModel(Model model) {
		if (model == null)
			return false;
		if (models().contains(model))
			return false;
		if (models().size() == 0)
			pickingBuffer().loadPixels();
		boolean result = models().add(model);
		if (model instanceof ModelObject)
			for (Agent agent : inputHandler().agents())
				agent.addGrabber(model);
		return result;
	}

	/**
	 * Returns true if scene has {@code model} and false otherwise.
	 */
	public boolean hasModel(Model model) {
		return models().contains(model);
	}

	/**
	 * Remove the {@code model} from the scene.
	 * 
	 * @see #models()
	 * @see #drawModels()
	 * @see #drawModels(PGraphics)
	 * @see #addModel(Model)
	 */
	public boolean removeModel(Model model) {
		return models().remove(model);
	}

	public void removeModels() {
		models().clear();
	}

	/**
	 * Draw all scene {@link #models()}. Shader chaining may be accomplished by {@link #drawModels(PGraphics)}.
	 * 
	 * @see #models()
	 * @see #drawModels(PGraphics)
	 * @see #addModel(Model)
	 * @see #removeModel(Model)
	 */
	public void drawModels() {
		for (Model model : models())
			model.draw(pg());
	}

	/**
	 * Draw all {@link #models()} into the given pgraphics without calling {@code pgraphics.beginDraw()/endDraw()} (which
	 * should be called manually).
	 * <p>
	 * This method allows shader chaining.
	 * 
	 * @param pgraphics
	 * 
	 * @see #models()
	 * @see #drawModels()
	 * @see #addModel(Model)
	 * @see #removeModel(Model)
	 */
	public void drawModels(PGraphics pgraphics) {
		// 1. Set pgraphics matrices using a custom MatrixHelper
		bindMatrices(pgraphics);

		// 2. Draw all models into pgraphics
		for (Model model : models())
			model.draw(pgraphics);
	}

	/**
	 * Returns a new matrix helper for the given {@code pgraphics}. Rarely needed.
	 * <p>
	 * Note that the current scene matrix helper may be retrieved by {@link #matrixHelper()}.
	 * 
	 * @see #matrixHelper()
	 * @see #setMatrixHelper(MatrixHelper)
	 * @see #drawModels()
	 * @see #drawModels(PGraphics)
	 * @see #applyWorldTransformation(PGraphics, Frame)
	 */
	public MatrixHelper matrixHelper(PGraphics pgraphics) {
		return (pgraphics instanceof processing.opengl.PGraphicsOpenGL) ? new GLMatrixHelper(this,
				(PGraphicsOpenGL) pgraphics) : new Java2DMatrixHelper(this, pgraphics);
	}

	/**
	 * Same as {@code matrixHelper(pgraphics).bind(false)}. Set the {@code pgraphics} matrices by calling
	 * {@link remixlab.dandelion.core.MatrixHelper#loadProjection(boolean)} and
	 * {@link remixlab.dandelion.core.MatrixHelper#loadModelView(boolean)} (only makes sense when {@link #pg()} is
	 * different than {@code pgraphics}).
	 * <p>
	 * This method doesn't perform any computation, but simple retrieve the current matrices whose actual computation has
	 * been updated in {@link #preDraw()}.
	 */
	public void bindMatrices(PGraphics pgraphics) {
		if (this.pg() == pgraphics)
			return;
		matrixHelper(pgraphics).bind(false);
	}

	/**
	 * Apply the local transformation defined by the given {@code frame} on the given {@code pgraphics}. This method
	 * doesn't call {@link #bindMatrices(PGraphics)} which should be called manually (only makes sense when {@link #pg()}
	 * is different than {@code pgraphics}). Needed by {@link #applyWorldTransformation(PGraphics, Frame)}.
	 * 
	 * @see #applyWorldTransformation(PGraphics, Frame)
	 * @see #bindMatrices(PGraphics)
	 */
	public void applyTransformation(PGraphics pgraphics, Frame frame) {
		if (pgraphics instanceof PGraphics3D) {
			pgraphics.translate(frame.translation().vec[0], frame.translation().vec[1], frame.translation().vec[2]);
			pgraphics.rotate(frame.rotation().angle(), ((Quat) frame.rotation()).axis().vec[0],
					((Quat) frame.rotation()).axis().vec[1], ((Quat) frame.rotation()).axis().vec[2]);
			pgraphics.scale(frame.scaling(), frame.scaling(), frame.scaling());
		}
		else {
			pgraphics.translate(frame.translation().x(), frame.translation().y());
			pgraphics.rotate(frame.rotation().angle());
			pgraphics.scale(frame.scaling(), frame.scaling());
		}
	}

	/**
	 * Apply the global transformation defined by the given {@code frame} on the given {@code pgraphics}. This method
	 * doesn't call {@link #bindMatrices(PGraphics)} which should be called manually (only makes sense when {@link #pg()}
	 * is different than {@code pgraphics}). Needed by {@link remixlab.proscene.Model#draw(PGraphics)}
	 * 
	 * @see remixlab.proscene.Model#draw(PGraphics)
	 * @see #applyTransformation(PGraphics, Frame)
	 * @see #bindMatrices(PGraphics)
	 */
	public void applyWorldTransformation(PGraphics pgraphics, Frame frame) {
		Frame refFrame = frame.referenceFrame();
		if (refFrame != null) {
			applyWorldTransformation(pgraphics, refFrame);
			applyTransformation(pgraphics, frame);
		}
		else {
			applyTransformation(pgraphics, frame);
		}
	}

	// SCREENDRAWING

	/**
	 * Need to override it because of this issue: https://github.com/remixlab/proscene/issues/1
	 */
	@Override
	public void beginScreenDrawing() {
		if (startCoordCalls != 0)
			throw new RuntimeException("There should be exactly one beginScreenDrawing() call followed by a "
					+ "endScreenDrawing() and they cannot be nested. Check your implementation!");

		startCoordCalls++;

		pg().hint(PApplet.DISABLE_OPTIMIZED_STROKE);// -> new line not present in AbstractScene.bS
		disableDepthTest();
		matrixHelper.beginScreenDrawing();
	}

	/**
	 * Need to override it because of this issue: https://github.com/remixlab/proscene/issues/1
	 */
	@Override
	public void endScreenDrawing() {
		startCoordCalls--;
		if (startCoordCalls != 0)
			throw new RuntimeException("There should be exactly one beginScreenDrawing() call followed by a "
					+ "endScreenDrawing() and they cannot be nested. Check your implementation!");

		matrixHelper.endScreenDrawing();
		enableDepthTest();
		pg().hint(PApplet.ENABLE_OPTIMIZED_STROKE);// -> new line not present in AbstractScene.bS
	}

	// DRAWING

	@Override
	public void drawCylinder(float w, float h) {
		if (is2D()) {
			AbstractScene.showDepthWarning("drawCylinder");
			return;
		}

		pg().pushStyle();
		float px, py;

		pg().beginShape(PApplet.QUAD_STRIP);
		for (float i = 0; i < 13; i++) {
			px = (float) Math.cos(PApplet.radians(i * 30)) * w;
			py = (float) Math.sin(PApplet.radians(i * 30)) * w;
			vertex(px, py, 0);
			vertex(px, py, h);
		}
		pg().endShape();

		pg().beginShape(PApplet.TRIANGLE_FAN);
		vertex(0, 0, 0);
		for (float i = 12; i > -1; i--) {
			px = (float) Math.cos(PApplet.radians(i * 30)) * w;
			py = (float) Math.sin(PApplet.radians(i * 30)) * w;
			vertex(px, py, 0);
		}
		pg().endShape();

		pg().beginShape(PApplet.TRIANGLE_FAN);
		vertex(0, 0, h);
		for (float i = 0; i < 13; i++) {
			px = (float) Math.cos(PApplet.radians(i * 30)) * w;
			py = (float) Math.sin(PApplet.radians(i * 30)) * w;
			vertex(px, py, h);
		}
		pg().endShape();
		pg().popStyle();
	}

	@Override
	public void drawHollowCylinder(int detail, float w, float h, Vec m, Vec n) {
		if (is2D()) {
			AbstractScene.showDepthWarning("drawHollowCylinder");
			return;
		}

		pg().pushStyle();
		// eqs taken from: http://en.wikipedia.org/wiki/Line-plane_intersection
		Vec pm0 = new Vec(0, 0, 0);
		Vec pn0 = new Vec(0, 0, h);
		Vec l0 = new Vec();
		Vec l = new Vec(0, 0, 1);
		Vec p = new Vec();
		float x, y, d;

		pg().noStroke();
		pg().beginShape(PApplet.QUAD_STRIP);

		for (float t = 0; t <= detail; t++) {
			x = w * PApplet.cos(t * PApplet.TWO_PI / detail);
			y = w * PApplet.sin(t * PApplet.TWO_PI / detail);
			l0.set(x, y, 0);

			d = (m.dot(Vec.subtract(pm0, l0))) / (l.dot(m));
			p = Vec.add(Vec.multiply(l, d), l0);
			vertex(p.x(), p.y(), p.z());

			l0.setZ(h);
			d = (n.dot(Vec.subtract(pn0, l0))) / (l.dot(n));
			p = Vec.add(Vec.multiply(l, d), l0);
			vertex(p.x(), p.y(), p.z());
		}
		pg().endShape();
		pg().popStyle();
	}

	@Override
	public void drawCone(int detail, float x, float y, float r, float h) {
		if (is2D()) {
			AbstractScene.showDepthWarning("drawCone");
			return;
		}
		pg().pushStyle();
		float unitConeX[] = new float[detail + 1];
		float unitConeY[] = new float[detail + 1];

		for (int i = 0; i <= detail; i++) {
			float a1 = PApplet.TWO_PI * i / detail;
			unitConeX[i] = r * (float) Math.cos(a1);
			unitConeY[i] = r * (float) Math.sin(a1);
		}

		pushModelView();
		translate(x, y);
		pg().beginShape(PApplet.TRIANGLE_FAN);
		vertex(0, 0, h);
		for (int i = 0; i <= detail; i++) {
			vertex(unitConeX[i], unitConeY[i], 0.0f);
		}
		pg().endShape();
		popModelView();
		pg().popStyle();
	}

	@Override
	public void drawCone(int detail, float x, float y, float r1, float r2, float h) {
		if (is2D()) {
			AbstractScene.showDepthWarning("drawCone");
			return;
		}
		pg().pushStyle();
		float firstCircleX[] = new float[detail + 1];
		float firstCircleY[] = new float[detail + 1];
		float secondCircleX[] = new float[detail + 1];
		float secondCircleY[] = new float[detail + 1];

		for (int i = 0; i <= detail; i++) {
			float a1 = PApplet.TWO_PI * i / detail;
			firstCircleX[i] = r1 * (float) Math.cos(a1);
			firstCircleY[i] = r1 * (float) Math.sin(a1);
			secondCircleX[i] = r2 * (float) Math.cos(a1);
			secondCircleY[i] = r2 * (float) Math.sin(a1);
		}

		pushModelView();
		translate(x, y);
		pg().beginShape(PApplet.QUAD_STRIP);
		for (int i = 0; i <= detail; i++) {
			vertex(firstCircleX[i], firstCircleY[i], 0);
			vertex(secondCircleX[i], secondCircleY[i], h);
		}
		pg().endShape();
		popModelView();
		pg().popStyle();
	}

	@Override
	public void drawAxes(float length) {
		pg().pushStyle();
		pg().colorMode(PApplet.RGB, 255);
		float charWidth = length / 40.0f;
		float charHeight = length / 30.0f;
		float charShift = 1.04f * length;

		pg().pushStyle();
		pg().beginShape(PApplet.LINES);
		pg().strokeWeight(2);
		if (is2D()) {
			// The X
			pg().stroke(200, 0, 0);
			vertex(charShift + charWidth, -charHeight);
			vertex(charShift - charWidth, charHeight);
			vertex(charShift - charWidth, -charHeight);
			vertex(charShift + charWidth, charHeight);

			// The Y
			charShift *= 1.02;
			pg().stroke(0, 200, 0);
			vertex(charWidth, charShift + (isRightHanded() ? charHeight : -charHeight));
			vertex(0.0f, charShift + 0.0f);
			vertex(-charWidth, charShift + (isRightHanded() ? charHeight : -charHeight));
			vertex(0.0f, charShift + 0.0f);
			vertex(0.0f, charShift + 0.0f);
			vertex(0.0f, charShift + -(isRightHanded() ? charHeight : -charHeight));
		}
		else {
			// The X
			pg().stroke(200, 0, 0);
			vertex(charShift, charWidth, -charHeight);
			vertex(charShift, -charWidth, charHeight);
			vertex(charShift, -charWidth, -charHeight);
			vertex(charShift, charWidth, charHeight);
			// The Y
			pg().stroke(0, 200, 0);
			vertex(charWidth, charShift, (isLeftHanded() ? charHeight : -charHeight));
			vertex(0.0f, charShift, 0.0f);
			vertex(-charWidth, charShift, (isLeftHanded() ? charHeight : -charHeight));
			vertex(0.0f, charShift, 0.0f);
			vertex(0.0f, charShift, 0.0f);
			vertex(0.0f, charShift, -(isLeftHanded() ? charHeight : -charHeight));
			// The Z
			pg().stroke(0, 100, 200);
			vertex(-charWidth, isRightHanded() ? charHeight : -charHeight, charShift);
			vertex(charWidth, isRightHanded() ? charHeight : -charHeight, charShift);
			vertex(charWidth, isRightHanded() ? charHeight : -charHeight, charShift);
			vertex(-charWidth, isRightHanded() ? -charHeight : charHeight, charShift);
			vertex(-charWidth, isRightHanded() ? -charHeight : charHeight, charShift);
			vertex(charWidth, isRightHanded() ? -charHeight : charHeight, charShift);
		}
		pg().endShape();
		pg().popStyle();

		// X Axis
		pg().stroke(200, 0, 0);
		line(0, 0, 0, length, 0, 0);
		// Y Axis
		pg().stroke(0, 200, 0);
		line(0, 0, 0, 0, length, 0);

		// Z Axis
		if (is3D()) {
			pg().stroke(0, 100, 200);
			line(0, 0, 0, 0, 0, length);
		}
		pg().popStyle();
	}

	@Override
	public void drawGrid(float size, int nbSubdivisions) {
		pg().pushStyle();
		pg().beginShape(LINES);
		for (int i = 0; i <= nbSubdivisions; ++i) {
			final float pos = size * (2.0f * i / nbSubdivisions - 1.0f);
			vertex(pos, -size);
			vertex(pos, +size);
			vertex(-size, pos);
			vertex(size, pos);
		}
		pg().endShape();
		pg().popStyle();
	}

	@Override
	public void drawDottedGrid(float size, int nbSubdivisions) {
		pg().pushStyle();
		float posi, posj;
		pg().beginShape(POINTS);
		for (int i = 0; i <= nbSubdivisions; ++i) {
			posi = size * (2.0f * i / nbSubdivisions - 1.0f);
			for (int j = 0; j <= nbSubdivisions; ++j) {
				posj = size * (2.0f * j / nbSubdivisions - 1.0f);
				vertex(posi, posj);
			}
		}
		pg().endShape();
		int internalSub = 5;
		int subSubdivisions = nbSubdivisions * internalSub;
		float currentWeight = pg().strokeWeight;
		pg().colorMode(HSB, 255);
		float hue = pg().hue(pg().strokeColor);
		float saturation = pg().saturation(pg().strokeColor);
		float brightness = pg().brightness(pg().strokeColor);
		pg().stroke(hue, saturation, brightness * 10f / 17f);
		pg().strokeWeight(currentWeight / 2);
		pg().beginShape(POINTS);
		for (int i = 0; i <= subSubdivisions; ++i) {
			posi = size * (2.0f * i / subSubdivisions - 1.0f);
			for (int j = 0; j <= subSubdivisions; ++j) {
				posj = size * (2.0f * j / subSubdivisions - 1.0f);
				if (((i % internalSub) != 0) || ((j % internalSub) != 0))
					vertex(posi, posj);
			}
		}
		pg().endShape();
		pg().popStyle();
	}

	@Override
	public void drawEye(Eye eye, float scale) {
		pg().pushStyle();
		// boolean drawFarPlane = true;
		// int farIndex = drawFarPlane ? 1 : 0;
		int farIndex = is3D() ? 1 : 0;
		boolean ortho = false;
		if (is3D())
			if (((Camera) eye).type() == Camera.Type.ORTHOGRAPHIC)
				ortho = true;
		pushModelView();
		// applyMatrix(camera.frame().worldMatrix());
		// same as the previous line, but maybe more efficient

		// Frame tmpFrame = new Frame(is3D());
		// tmpFrame.fromMatrix(eye.frame().worldMatrix());
		// applyTransformation(tmpFrame);
		// same as above but easier
		// scene().applyTransformation(camera.frame());

		// fails due to scaling!

		// take into account the whole hierarchy:
		if (is2D()) {
			// applyWorldTransformation(eye.frame());
			translate(eye.frame().position().vec[0], eye.frame().position().vec[1]);
			rotate(eye.frame().orientation().angle());
		} else {
			translate(eye.frame().position().vec[0], eye.frame().position().vec[1], eye.frame().position().vec[2]);
			rotate(eye.frame().orientation().angle(), ((Quat) eye.frame().orientation()).axis().vec[0], ((Quat) eye.frame()
					.orientation()).axis().vec[1], ((Quat) eye.frame().orientation()).axis().vec[2]);
		}

		// 0 is the upper left coordinates of the near corner, 1 for the far one
		Vec[] points = new Vec[2];
		points[0] = new Vec();
		points[1] = new Vec();

		if (is2D() || ortho) {
			float[] wh = eye.getBoundaryWidthHeight();
			points[0].setX(scale * wh[0]);
			points[1].setX(scale * wh[0]);
			points[0].setY(scale * wh[1]);
			points[1].setY(scale * wh[1]);
		}

		if (is3D()) {
			points[0].setZ(scale * ((Camera) eye).zNear());
			points[1].setZ(scale * ((Camera) eye).zFar());

			if (((Camera) eye).type() == Camera.Type.PERSPECTIVE) {
				points[0].setY(points[0].z() * PApplet.tan(((Camera) eye).fieldOfView() / 2.0f));
				points[0].setX(points[0].y() * ((Camera) eye).aspectRatio());
				float ratio = points[1].z() / points[0].z();
				points[1].setY(ratio * points[0].y());
				points[1].setX(ratio * points[0].x());
			}

			// Frustum lines
			switch (((Camera) eye).type()) {
			case PERSPECTIVE: {
				pg().beginShape(PApplet.LINES);
				vertex(0.0f, 0.0f, 0.0f);
				vertex(points[farIndex].x(), points[farIndex].y(), -points[farIndex].z());
				vertex(0.0f, 0.0f, 0.0f);
				vertex(-points[farIndex].x(), points[farIndex].y(), -points[farIndex].z());
				vertex(0.0f, 0.0f, 0.0f);
				vertex(-points[farIndex].x(), -points[farIndex].y(), -points[farIndex].z());
				vertex(0.0f, 0.0f, 0.0f);
				vertex(points[farIndex].x(), -points[farIndex].y(), -points[farIndex].z());
				pg().endShape();
				break;
			}
			case ORTHOGRAPHIC: {
				// if (drawFarPlane) {
				pg().beginShape(PApplet.LINES);
				vertex(points[0].x(), points[0].y(), -points[0].z());
				vertex(points[1].x(), points[1].y(), -points[1].z());
				vertex(-points[0].x(), points[0].y(), -points[0].z());
				vertex(-points[1].x(), points[1].y(), -points[1].z());
				vertex(-points[0].x(), -points[0].y(), -points[0].z());
				vertex(-points[1].x(), -points[1].y(), -points[1].z());
				vertex(points[0].x(), -points[0].y(), -points[0].z());
				vertex(points[1].x(), -points[1].y(), -points[1].z());
				pg().endShape();
				// }
				break;
			}
			}
		}

		// Near and (optionally) far plane(s)
		pg().noStroke();
		pg().beginShape(PApplet.QUADS);
		for (int i = farIndex; i >= 0; --i) {
			pg().normal(0.0f, 0.0f, (i == 0) ? 1.0f : -1.0f);
			vertex(points[i].x(), points[i].y(), -points[i].z());
			vertex(-points[i].x(), points[i].y(), -points[i].z());
			vertex(-points[i].x(), -points[i].y(), -points[i].z());
			vertex(points[i].x(), -points[i].y(), -points[i].z());
		}
		pg().endShape();

		// Up arrow
		float arrowHeight = 1.5f * points[0].y();
		float baseHeight = 1.2f * points[0].y();
		float arrowHalfWidth = 0.5f * points[0].x();
		float baseHalfWidth = 0.3f * points[0].x();

		// pg3d().noStroke();
		// Arrow base
		pg().beginShape(PApplet.QUADS);
		if (isLeftHanded()) {
			vertex(-baseHalfWidth, -points[0].y(), -points[0].z());
			vertex(baseHalfWidth, -points[0].y(), -points[0].z());
			vertex(baseHalfWidth, -baseHeight, -points[0].z());
			vertex(-baseHalfWidth, -baseHeight, -points[0].z());
		} else {
			vertex(-baseHalfWidth, points[0].y(), -points[0].z());
			vertex(baseHalfWidth, points[0].y(), -points[0].z());
			vertex(baseHalfWidth, baseHeight, -points[0].z());
			vertex(-baseHalfWidth, baseHeight, -points[0].z());
		}
		pg().endShape();

		// Arrow
		pg().beginShape(PApplet.TRIANGLES);
		if (isLeftHanded()) {
			vertex(0.0f, -arrowHeight, -points[0].z());
			vertex(-arrowHalfWidth, -baseHeight, -points[0].z());
			vertex(arrowHalfWidth, -baseHeight, -points[0].z());
		} else {
			vertex(0.0f, arrowHeight, -points[0].z());
			vertex(-arrowHalfWidth, baseHeight, -points[0].z());
			vertex(arrowHalfWidth, baseHeight, -points[0].z());
		}
		pg().endShape();
		popModelView();
		pg().popStyle();
	}

	@Override
	public void drawPath(KeyFrameInterpolator kfi, int mask, int nbFrames, float scale) {
		pg().pushStyle();
		if (mask != 0) {
			int nbSteps = 30;
			pg().strokeWeight(2 * pg().strokeWeight);
			pg().noFill();

			List<Frame> path = kfi.path();
			if (((mask & 1) != 0) && path.size() > 1) {
				pg().beginShape();
				for (Frame myFr : path)
					vertex(myFr.position().x(), myFr.position().y(), myFr.position().z());
				pg().endShape();
			}
			if ((mask & 6) != 0) {
				int count = 0;
				if (nbFrames > nbSteps)
					nbFrames = nbSteps;
				float goal = 0.0f;

				for (Frame myFr : path)
					if ((count++) >= goal) {
						goal += nbSteps / (float) nbFrames;
						pushModelView();

						applyTransformation(myFr);

						if ((mask & 2) != 0)
							drawKFIEye(scale);
						if ((mask & 4) != 0)
							drawAxes(scale / 10.0f);

						popModelView();
					}
			}
			kfi.addPathToMotionAgent();
			pg().strokeWeight(pg().strokeWeight / 2f);
			drawPickingTargets(true);
		}
		pg().popStyle();
	}

	@Override
	protected void drawKFIEye(float scale) {
		pg().pushStyle();
		float halfHeight = scale * (is2D() ? 1.2f : 0.07f);
		float halfWidth = halfHeight * 1.3f;
		float dist = halfHeight / (float) Math.tan(PApplet.PI / 8.0f);

		float arrowHeight = 1.5f * halfHeight;
		float baseHeight = 1.2f * halfHeight;
		float arrowHalfWidth = 0.5f * halfWidth;
		float baseHalfWidth = 0.3f * halfWidth;

		// Frustum outline
		pg().noFill();
		pg().beginShape();
		vertex(-halfWidth, halfHeight, -dist);
		vertex(-halfWidth, -halfHeight, -dist);
		vertex(0.0f, 0.0f, 0.0f);
		vertex(halfWidth, -halfHeight, -dist);
		vertex(-halfWidth, -halfHeight, -dist);
		pg().endShape();
		pg().noFill();
		pg().beginShape();
		vertex(halfWidth, -halfHeight, -dist);
		vertex(halfWidth, halfHeight, -dist);
		vertex(0.0f, 0.0f, 0.0f);
		vertex(-halfWidth, halfHeight, -dist);
		vertex(halfWidth, halfHeight, -dist);
		pg().endShape();

		// Up arrow
		pg().noStroke();
		pg().fill(pg().strokeColor);
		// Base
		pg().beginShape(PApplet.QUADS);

		if (isLeftHanded()) {
			vertex(baseHalfWidth, -halfHeight, -dist);
			vertex(-baseHalfWidth, -halfHeight, -dist);
			vertex(-baseHalfWidth, -baseHeight, -dist);
			vertex(baseHalfWidth, -baseHeight, -dist);
		}
		else {
			vertex(-baseHalfWidth, halfHeight, -dist);
			vertex(baseHalfWidth, halfHeight, -dist);
			vertex(baseHalfWidth, baseHeight, -dist);
			vertex(-baseHalfWidth, baseHeight, -dist);
		}

		pg().endShape();
		// Arrow
		pg().beginShape(PApplet.TRIANGLES);

		if (isLeftHanded()) {
			vertex(0.0f, -arrowHeight, -dist);
			vertex(arrowHalfWidth, -baseHeight, -dist);
			vertex(-arrowHalfWidth, -baseHeight, -dist);
		}
		else {
			vertex(0.0f, arrowHeight, -dist);
			vertex(-arrowHalfWidth, baseHeight, -dist);
			vertex(arrowHalfWidth, baseHeight, -dist);
		}
		pg().endShape();
		pg().popStyle();
	}

	@Override
	public void drawCross(float px, float py, float size) {
		float half_size = size / 2f;
		pg().pushStyle();
		beginScreenDrawing();
		pg().noFill();
		pg().beginShape(LINES);
		vertex(px - half_size, py);
		vertex(px + half_size, py);
		vertex(px, py - half_size);
		vertex(px, py + half_size);
		pg().endShape();
		endScreenDrawing();
		pg().popStyle();
	}

	@Override
	public void drawFilledCircle(int subdivisions, Vec center, float radius) {
		pg().pushStyle();
		float precision = PApplet.TWO_PI / subdivisions;
		float x = center.x();
		float y = center.y();
		float angle, x2, y2;
		beginScreenDrawing();
		pg().noStroke();
		pg().beginShape(TRIANGLE_FAN);
		vertex(x, y);
		for (angle = 0.0f; angle <= PApplet.TWO_PI + 1.1 * precision; angle += precision) {
			x2 = x + PApplet.sin(angle) * radius;
			y2 = y + PApplet.cos(angle) * radius;
			vertex(x2, y2);
		}
		pg().endShape();
		endScreenDrawing();
		pg().popStyle();
	}

	@Override
	public void drawFilledSquare(Vec center, float edge) {
		float half_edge = edge / 2f;
		pg().pushStyle();
		float x = center.x();
		float y = center.y();
		beginScreenDrawing();
		pg().noStroke();
		pg().beginShape(QUADS);
		vertex(x - half_edge, y + half_edge);
		vertex(x + half_edge, y + half_edge);
		vertex(x + half_edge, y - half_edge);
		vertex(x - half_edge, y - half_edge);
		pg().endShape();
		endScreenDrawing();
		pg().popStyle();
	}

	@Override
	public void drawShooterTarget(Vec center, float length) {
		float half_length = length / 2f;
		pg().pushStyle();
		float x = center.x();
		float y = center.y();
		beginScreenDrawing();
		pg().noFill();

		pg().beginShape();
		vertex((x - half_length), (y - half_length) + (0.6f * half_length));
		vertex((x - half_length), (y - half_length));
		vertex((x - half_length) + (0.6f * half_length), (y - half_length));
		pg().endShape();

		pg().beginShape();
		vertex((x + half_length) - (0.6f * half_length), (y - half_length));
		vertex((x + half_length), (y - half_length));
		vertex((x + half_length), ((y - half_length) + (0.6f * half_length)));
		pg().endShape();

		pg().beginShape();
		vertex((x + half_length), ((y + half_length) - (0.6f * half_length)));
		vertex((x + half_length), (y + half_length));
		vertex(((x + half_length) - (0.6f * half_length)), (y + half_length));
		pg().endShape();

		pg().beginShape();
		vertex((x - half_length) + (0.6f * half_length), (y + half_length));
		vertex((x - half_length), (y + half_length));
		vertex((x - half_length), ((y + half_length) - (0.6f * half_length)));
		pg().endShape();
		endScreenDrawing();
		drawCross(center.x(), center.y(), 0.6f * length);
		pg().popStyle();
	}

	@Override
	public void drawPickingTargets(boolean keyFrame) {
		pg().pushStyle();
		for (Grabber mg : motionAgent().grabbers()) {
			if (mg instanceof GrabberFrame) {
				GrabberFrame iF = (GrabberFrame) mg;// downcast needed
				// frames
				if (!(iF.isInEyePath() ^ keyFrame) && (!iF.isEyeFrame())) {
					Vec center = projectedCoordinatesOf(iF.position());
					// if (iF.grabsInput(motionAgent())) {
					if (motionAgent().isInputGrabber(mg)) {
						pg().pushStyle();
						pg().strokeWeight(2 * pg().strokeWeight);
						pg().colorMode(HSB, 255);
						float hue = pg().hue(pg().strokeColor);
						float saturation = pg().saturation(pg().strokeColor);
						float brightness = pg().brightness(pg().strokeColor);
						pg().stroke(hue, saturation * 1.4f, brightness * 1.4f);
						drawShooterTarget(center, (iF.grabsInputThreshold() + 1));
						pg().popStyle();
					}
					else {
						pg().pushStyle();
						pg().colorMode(HSB, 255);
						float hue = pg().hue(pg().strokeColor);
						float saturation = pg().saturation(pg().strokeColor);
						float brightness = pg().brightness(pg().strokeColor);
						pg().stroke(hue, saturation * 1.4f, brightness);
						drawShooterTarget(center, iF.grabsInputThreshold());
						pg().popStyle();
					}
				}
			}
		}
		pg().popStyle();
	}

	/**
	 * Code contributed by Jacques Maire (http://www.alcys.com/) See also:
	 * http://www.mathcurve.com/courbes3d/solenoidtoric/solenoidtoric.shtml
	 * http://crazybiocomputing.blogspot.fr/2011/12/3d-curves-toric-solenoids.html
	 */
	@Override
	public void drawTorusSolenoid(int faces, int detail, float insideRadius, float outsideRadius) {
		pg().pushStyle();
		pg().noStroke();
		Vec v1, v2;
		int b, ii, jj, a;
		float eps = PApplet.TWO_PI / detail;
		for (a = 0; a < faces; a += 2) {
			pg().beginShape(PApplet.TRIANGLE_STRIP);
			b = (a <= (faces - 1)) ? a + 1 : 0;
			for (int i = 0; i < (detail + 1); i++) {
				ii = (i < detail) ? i : 0;
				jj = ii + 1;
				float ai = eps * jj;
				float alpha = a * PApplet.TWO_PI / faces + ai;
				v1 = new Vec((outsideRadius + insideRadius * PApplet.cos(alpha)) * PApplet.cos(ai),
						(outsideRadius + insideRadius * PApplet.cos(alpha)) * PApplet.sin(ai), insideRadius
								* PApplet.sin(alpha));
				alpha = b * PApplet.TWO_PI / faces + ai;
				v2 = new Vec((outsideRadius + insideRadius * PApplet.cos(alpha)) * PApplet.cos(ai),
						(outsideRadius + insideRadius * PApplet.cos(alpha)) * PApplet.sin(ai), insideRadius
								* PApplet.sin(alpha));
				vertex(v1.x(), v1.y(), v1.z());
				vertex(v2.x(), v2.y(), v2.z());
			}
			pg().endShape();
		}
		pg().popStyle();
	}

	/*
	 * Copy paste from AbstractScene but we add the style (color, stroke, etc) here.
	 */
	@Override
	protected void drawAxesHint() {
		pg().pushStyle();
		pg().strokeWeight(2);
		drawAxes(eye().sceneRadius());
		pg().popStyle();
	}

	/*
	 * Copy paste from AbstractScene but we add the style (color, stroke, etc) here.
	 */
	@Override
	protected void drawGridHint() {
		pg().pushStyle();
		pg().stroke(170);
		if (gridIsDotted()) {
			pg().strokeWeight(2);
			drawDottedGrid(eye().sceneRadius());
		}
		else {
			pg().strokeWeight(1);
			drawGrid(eye().sceneRadius());
		}
		pg().popStyle();
	}

	/*
	 * Copy paste from AbstractScene but we add the style (color, stroke, etc) here.
	 */
	@Override
	protected void drawPathsHint() {
		pg().pushStyle();
		pg().colorMode(PApplet.RGB, 255);
		pg().strokeWeight(1);
		pg().stroke(0, 220, 220);
		drawEyePaths();
		pg().popStyle();
	}

	@Override
	/*
	 * Copy paste from AbstractScene but we add the style (color, stroke, etc) here.
	 */
	protected void drawPickingHint() {
		pg().pushStyle();
		pg().colorMode(PApplet.RGB, 255);
		pg().strokeWeight(1);
		pg().stroke(220, 220, 220);
		drawPickingTargets();
		pg().popStyle();
	}

	@Override
	protected void drawAnchorHint() {
		pg().pushStyle();
		Vec p = eye().projectedCoordinatesOf(anchor());
		pg().stroke(255);
		pg().strokeWeight(3);
		drawCross(p.vec[0], p.vec[1]);
		pg().popStyle();
	}

	@Override
	protected void drawPointUnderPixelHint() {
		pg().pushStyle();
		Vec v = eye().projectedCoordinatesOf(eye().pupVec);
		pg().stroke(255);
		pg().strokeWeight(3);
		drawCross(v.vec[0], v.vec[1], 30);
		pg().popStyle();
	}

	// TODO check these comments:
	@Override
	protected void drawScreenRotateHint() {
		if (!(motionAgent() instanceof WheeledMouseAgent))
			return;
		if (!(motionAgent().inputGrabber() instanceof InteractiveFrame))
			return;

		pg().pushStyle();
		float p1x = mouseAgent().currentEvent.x() /*- originCorner().x()*/;
		float p1y = mouseAgent().currentEvent.y() /*- originCorner().y()*/;

		Vec p2 = new Vec();
		if (motionAgent().inputGrabber() instanceof GrabberFrame) {
			if (((GrabberFrame) motionAgent().inputGrabber()).isEyeFrame())
				p2 = eye().projectedCoordinatesOf(anchor());
			else
				p2 = eye().projectedCoordinatesOf(((GrabberFrame) mouseAgent().inputGrabber()).position());
		}
		beginScreenDrawing();
		pg().stroke(255, 255, 255);
		pg().strokeWeight(2);
		pg().noFill();
		line(p2.x(), p2.y(), p1x, p1y);
		endScreenDrawing();
		pg().popStyle();
	}

	// TODO check these comments:
	@Override
	protected void drawZoomWindowHint() {
		if (!(motionAgent() instanceof WheeledMouseAgent))
			return;
		if (!(motionAgent().inputGrabber() instanceof InteractiveFrame))
			return;
		InteractiveFrame iFrame = (InteractiveFrame) motionAgent().inputGrabber();
		if (!(iFrame.initMotionEvent instanceof DOF2Event))
			return;

		pg().pushStyle();
		DOF2Event init = (DOF2Event) iFrame.initMotionEvent;
		float p1x = init.x() /*- originCorner().x()*/;
		float p1y = init.y() /*- originCorner().y()*/;
		float p2x = mouseAgent().currentEvent.x() /*- originCorner().x()*/;
		float p2y = mouseAgent().currentEvent.y() /*- originCorner().y()*/;
		beginScreenDrawing();
		pg().stroke(255, 255, 255);
		pg().strokeWeight(2);
		pg().noFill();
		pg().beginShape();
		vertex(p1x, p1y);
		vertex(p2x, p1y);
		vertex(p2x, p2y);
		vertex(p1x, p2y);
		pg().endShape(CLOSE);
		endScreenDrawing();
		pg().popStyle();
	}

	// decide whether or not to include these in the 3.0 release:

	// PVector <-> toVec
	
//	public void drawArrow(PVector from, PVector to, float radius) {
//		drawArrow(Scene.toVec(from), Scene.toVec(to), radius);
//	}
//
//	public void drawFilledCircle(PVector center, float radius) {
//		drawFilledCircle(Scene.toVec(center), radius);
//	}
//
//	public void drawHollowCylinder(int detail, float w, float h, PVector m, PVector n) {
//		drawHollowCylinder(detail, w, h, Scene.toVec(m), Scene.toVec(n));
//	}
//
//	public void drawFilledSquare(PVector center, float edge) {
//		drawFilledSquare(Scene.toVec(center), edge);
//	}
//
//	public void drawShooterTarget(PVector center, float length) {
//		drawShooterTarget(Scene.toVec(center), length);
//	}
//
//	public boolean isPointVisible(PVector point) {
//		return isPointVisible(Scene.toVec(point));
//	}
//
//	public Eye.Visibility ballVisibility(PVector center, float radius) {
//		return ballVisibility(Scene.toVec(center), radius);
//	}
//
//	public Eye.Visibility boxVisibility(PVector p1, PVector p2) {
//		return boxVisibility(Scene.toVec(p1), Scene.toVec(p2));
//	}
//
//	public boolean isFaceBackFacing(PVector a, PVector b, PVector c) {
//		return isFaceBackFacing(Scene.toVec(a), Scene.toVec(b), Scene.toVec(c));
//	}
//
//	public PVector pointUnderPixel(float x, float y) {
//		return Scene.toPVector(pointUnderPixel(new Point(x,y)));
//	}
//
//	public PVector projectedCoordinatesOf(PVector src) {
//		return Scene.toPVector(projectedCoordinatesOf(Scene.toVec(src)));
//	}
//
//	public PVector unprojectedCoordinatesOf(PVector src) {
//		return Scene.toPVector(unprojectedCoordinatesOf(Scene.toVec(src)));
//	}
//
//	public void setCenter(PVector center) {
//		setCenter(Scene.toVec(center));
//	}
//
//	public void setAnchor(PVector anchor) {
//		setAnchor(Scene.toVec(anchor));
//	}
//
//	public void setBoundingBox(PVector min, PVector max) {
//		setBoundingBox(Scene.toVec(min), Scene.toVec(max));
//	}
//
//	public void setBoundingRect(PVector min, PVector max) {
//		setBoundingRect(Scene.toVec(min), Scene.toVec(max));
//	}

	// PMatrix <-> toMat

//	public void applyModelViewMatrix(PMatrix2D source) {
//		applyModelView(Scene.toMat(source));
//	}
//
//	public void applyModelViewMatrix(PMatrix3D source) {
//		applyModelView(Scene.toMat(source));
//	}
//
//	public void applyProjectionMatrix(PMatrix3D source) {
//		applyProjection(Scene.toMat(source));
//	}
//
//	public PMatrix2D modelViewMatrix2D() {
//		return Scene.toPMatrix2D(modelView());
//	}
//
//	public PMatrix3D modelViewMatrix() {
//		return Scene.toPMatrix(modelView());
//	}
//
//	public PMatrix3D projectionMatrix() {
//		return Scene.toPMatrix(projection());
//	}
//
//	public void setModelViewMatrix(PMatrix2D source) {
//		setModelView(Scene.toMat(source));
//	}
//
//	public void setModelViewMatrix(PMatrix3D source) {
//		setModelView(Scene.toMat(source));
//	}
//
//	public void setProjectionMatrix(PMatrix3D source) {
//		setProjection(Scene.toMat(source));
//	}
}