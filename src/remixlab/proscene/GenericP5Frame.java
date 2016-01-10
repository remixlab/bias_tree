/**************************************************************************************
 * ProScene (version 3.0.0)
 * Copyright (c) 2014-2016 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 * 
 * All rights reserved. Library that eases the creation of interactive scenes
 * in Processing, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.proscene;

import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.bias.ext.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.AbstractScene.Platform;
import remixlab.dandelion.geom.Frame;
import remixlab.util.*;

public class GenericP5Frame extends GenericFrame {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				appendSuper(super.hashCode()).
				append(profile).
				toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;

		GenericP5Frame other = (GenericP5Frame) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(profile, other.profile)
				.isEquals();
	}
	
	@Override
	public Scene scene() {
		return (Scene)gScene;
	}
	
	public Profile profile;
	
	public GenericP5Frame(Scene scn) {
		super(scn);
		setProfile(new Profile(this));
		//TODO
		if(Scene.platform() == Platform.PROCESSING_DESKTOP)
			setDefaultMouseBindings();
		// else
		    // setDefaultTouchBindings();
		setDefaultKeyBindings();
		addStageHandlers();
	}
	
	public GenericP5Frame(Scene scn, Frame referenceFrame) {
		super(scn, referenceFrame);
		setProfile(new Profile(this));
		if(referenceFrame instanceof GenericP5Frame)
			this.profile.from(((GenericP5Frame)referenceFrame).profile);
		else {
			//TODO
			if(Scene.platform() == Platform.PROCESSING_DESKTOP)
				setDefaultMouseBindings();
			// else
			    // setDefaultTouchBindings();
			setDefaultKeyBindings();
			addStageHandlers();
		}
	}
	
	public GenericP5Frame(Eye eye) {
		super(eye);
		setProfile(new Profile(this));
		//TODO
		if(Scene.platform() == Platform.PROCESSING_DESKTOP)
			setDefaultMouseBindings();
		// else
		    // setDefaultTouchBindings();
		setDefaultKeyBindings();
		addStageHandlers();
	}
	
	protected GenericP5Frame(GenericP5Frame otherFrame) {
		super(otherFrame);
		setProfile(new Profile(this));
		this.profile.from(otherFrame.profile);
	}
	
	@Override
	public GenericP5Frame get() {
		return new GenericP5Frame(this);
	}
	
	@Override
	public void performInteraction(BogusEvent event) {
		profile.handle(event);
	}
	
	@Override
	protected boolean checkIfGrabsInput(KeyboardEvent event) {
		return profile.hasBinding(event.shortcut());
	}
	
	public void removeBindings() {
		profile.removeBindings();
	}
	
	public String action(Shortcut key) {
		return profile.action(key);
	}
	
	public boolean isActionBound(String action) {
		return profile.isActionBound(action);
	}
	
	protected void addStageHandlers() {
		addInitHandler(KeyboardEvent.class, "initKeyboard");
		addFlushHandler(KeyboardEvent.class, "flushKeyboard");
		
		/*
		addInitHandler(DOF2Event.class, "initDOF2");
		addExecHandler(DOF2Event.class, "execDOF2");
		addFlushHandler(DOF2Event.class, "flushDOF2");
		
		addInitHandler(DOF3Event.class, "initDOF3");
		addExecHandler(DOF3Event.class, "execDOF3");
		addFlushHandler(DOF3Event.class, "flushDOF3");
		
		addInitHandler(DOF6Event.class, "initDOF6");
		addExecHandler(DOF6Event.class, "execDOF6");
		addFlushHandler(DOF6Event.class, "flushDOF6");
		*/
	}
	
	public void setDefaultMouseBindings() {
		scene().mouseAgent().setDefaultBindings(this);
	}
	
	//TODO restore me
	/*
	public void setDefaultTouchBindings() {
		scene().touchAgent().setDefaultBindings(this);
	}
	*/
	
	public void setDefaultKeyBindings() {
		removeKeyBindings();
		setKeyBinding('n', "align");
		setKeyBinding('c', "center");
		setKeyBinding(KeyAgent.LEFT_KEY, "translateXNeg");
		setKeyBinding(KeyAgent.RIGHT_KEY, "translateXPos");
		setKeyBinding(KeyAgent.DOWN_KEY, "translateYNeg");
		setKeyBinding(KeyAgent.UP_KEY, "translateYPos");
		setKeyBinding(BogusEvent.SHIFT, KeyAgent.LEFT_KEY, "rotateXNeg");
		setKeyBinding(BogusEvent.SHIFT, KeyAgent.RIGHT_KEY, "rotateXPos");
		setKeyBinding(BogusEvent.SHIFT, KeyAgent.DOWN_KEY, "rotateYNeg");
		setKeyBinding(BogusEvent.SHIFT, KeyAgent.UP_KEY, "rotateYPos");	
		setKeyBinding('z', "rotateZNeg");
		setKeyBinding('Z', "rotateZPos");
	}

	// good for all dofs :P
	
	public void setMotionBinding(int id, String action) {
		profile.setBinding(new MotionShortcut(id), action);
	}
	
	public void setMotionBinding(Object object, int id, String action) {
		profile.setBinding(object, new MotionShortcut(id), action);
	}
	
	public void removeMotionBindings() {
		profile.removeBindings(MotionShortcut.class);
	}
	
	public boolean hasMotionBinding(int id) {
		return profile.hasBinding(new MotionShortcut(id));
	}
	
	public void removeMotionBinding(int id) {
		profile.removeBinding(new MotionShortcut(id));
	}
	
	public void removeMotionBindings(int [] ids) {
		for(int i=0; i< ids.length; i++)
			removeMotionBinding(ids[i]);
	}
	
	// Key
	
	public void setKeyBinding(int vkey, String action) {
		profile.setBinding(new KeyboardShortcut(vkey), action);
	}
	
	public void setKeyBinding(char key, String action) {
		profile.setBinding(new KeyboardShortcut(key), action);
	}
	
	public void setKeyBinding(Object object, int vkey, String action) {
		profile.setBinding(object, new KeyboardShortcut(vkey), action);
	}
	
	public void setKeyBinding(Object object, char key, String action) {
		profile.setBinding(object, new KeyboardShortcut(key), action);
	}
	
	public boolean hasKeyBinding(int vkey) {
		return profile.hasBinding(new KeyboardShortcut(vkey));
	}
	
	public boolean hasKeyBinding(char key) {
		return profile.hasBinding(new KeyboardShortcut(key));
	}
	
	public void removeKeyBinding(int vkey) {
		profile.removeBinding(new KeyboardShortcut(vkey));
	}
	
	public void removeKeyBinding(char key) {
		profile.removeBinding(new KeyboardShortcut(key));
	}
	
	public void setKeyBinding(int mask, int vkey, String action) {
		profile.setBinding(new KeyboardShortcut(mask, vkey), action);
	}
	
	public void setKeyBinding(Object object, int mask, int vkey, String action) {
		profile.setBinding(object, new KeyboardShortcut(mask, vkey), action);
	}
	
	public boolean hasKeyBinding(int mask, int vkey) {
		return profile.hasBinding(new KeyboardShortcut(mask, vkey));
	}
	
	public void removeKeyBinding(int mask, int vkey) {
		profile.removeBinding(new KeyboardShortcut(mask, vkey));
	}
	
	public void setKeyBinding(int mask, char key, String action) {
		setKeyBinding(mask, KeyAgent.keyCode(key), action);
	}
	
	public void setKeyBinding(Object object, int mask, char key, String action) {
		setKeyBinding(object, mask, KeyAgent.keyCode(key), action);
	}
	
	public boolean hasKeyBinding(int mask, char key) {
		return hasKeyBinding(mask, KeyAgent.keyCode(key));
	}
	
	public void removeKeyBinding(int mask, char key) {
		removeKeyBinding(mask, KeyAgent.keyCode(key));
	}
	
	public void removeKeyBindings() {
		profile.removeBindings(KeyboardShortcut.class);
	}
	
	// click
	
	public void setClickBinding(int id, int count, String action) {
		if(count > 0 && count < 4)
			profile.setBinding(new ClickShortcut(id, count), action);
		else
			System.out.println("Warning no click binding set! Count should be between 1 and 3");
	}
	
	public void setClickBinding(Object object, int id, int count, String action) {
		if(count > 0 && count < 4)
			profile.setBinding(object, new ClickShortcut(id, count), action);
		else
			System.out.println("Warning no click binding set! Count should be between 1 and 3");
	}
	
	public boolean hasClickBinding(int id, int count) {
		return profile.hasBinding(new ClickShortcut(id, count));
	}
	
	public void removeClickBinding(int id, int count) {
		profile.removeBinding(new ClickShortcut(id, count));
	}
	
	public void removeClickBindings() {
		profile.removeBindings(ClickShortcut.class);
	}
	
	public void removeClickBindings(int [] ids, int count) {
		for(int i=0; i<ids.length; i++)
			removeClickBinding(ids[i], count);
	}
	
	public Profile profile() {
		return profile;
	}
	
	public void setProfile(Profile p) {
		if( p.grabber() == this )
			profile = p;
		else
			System.out.println("Nothing done, profile grabber is different than this grabber");
	}
	
	public void setBindings(GenericP5Frame otherFrame) {
		profile.from(otherFrame.profile());
	}
	
	public String info() {
		String result = new String();
		String info = profile().info(KeyboardShortcut.class);
		if(!info.isEmpty()) {
			result = "Key bindings:\n";
			result += Scene.parseKeyInfo(info);
		}
		info = profile().info(MotionShortcut.class);
		if(!info.isEmpty()) {
			result += "Motion bindings:\n";
			result += Scene.parseInfo(info);
		}
		info = profile().info(ClickShortcut.class);
		if(!info.isEmpty()) {
			result += "Click bindings:\n";
			result += Scene.parseInfo(info);
		}
		return result;
	}
	
	//
	String vkeyAction;
	
	// private A a;//TODO study make me an attribute to com between init and end
	//protected boolean			need4Spin;
	//protected boolean			need4Tossing;
	//protected boolean			drive;
	//protected float				flySpeedCache;
	
	/*
	protected MotionEvent initMotionEvent() {
		return initMotionEvent;
	}

	protected MotionEvent currentMotionEvent() {
		return currentMotionEvent;
	}
	*/
	
	// lets see
	
	public void addInitHandler(Class<?> event, String action) {
		profile().addInitHandler(event, action);
	}
	
	public void addInitHandler(Object object, Class<?> event, String action) {
		profile().addInitHandler(object, event, action);
	}
	
	public boolean hasInitHandler(Class<?> event) {
		return profile().hasInitHandler(event);
	}
	
	public void removeInitHandler(Class<?> event) {
		profile().removeInitHandler(event);
	}
	
	public void addExecHandler(Class<?> event, String action) {
		profile().addExecHandler(event, action);
	}
	
	public void addExecHandler(Object object, Class<?> event, String action) {
		profile().addExecHandler(object, event, action);
	}
	
	public boolean hasExecHandler(Class<?> event) {
		return profile().hasExecHandler(event);
	}
	
	public void removeExecHandler(Class<?> event) {
		profile().removeExecHandler(event);
	}
	
	public void addFlushHandler(Class<?> event, String action) {
		profile().addFlushHandler(event, action);
	}
	
	public void addFlushHandler(Object object, Class<?> event, String action) {
		profile().addFlushHandler(object, event, action);
	}
	
	public boolean hasFlushHandler(Class<?> event) {
		return profile().hasFlushHandler(event);
	}
	
	public void removeFlushHandler(Class<?> event) {
		profile().removeFlushHandler(event);
	}
	
	public boolean initKeyboard(KeyboardEvent event) {
		if(event.id() == 0)//TYPE event
			return vkeyAction == null ? false : true;
		else {
			vkeyAction = profile.action(event.shortcut());
		    return false;
		}
	}
	
	public boolean flushKeyboard(KeyboardEvent event) {
		if( event.flushed() && vkeyAction != null )
			vkeyAction = null;
		return true;
	}

	/**
	 * Internal use.
	 * 
	 * @see remixlab.bias.ext.Profile
	 */	
	public boolean initDOF3(DOF3Event e) {
		return initDOF2(MotionEvent.dof2Event(e));
	}
	
	public boolean initDOF6(DOF6Event e) {
		return initDOF2(MotionEvent.dof2Event(e));
	}
	
	public boolean initDOF2(DOF2Event event) {
		//initMotionEvent = event.get();
		//currentMotionEvent = event;
		//stopSpinning();
		return false;
	}
	
	/**
	 * Internal use.
	 * 
	 * @see remixlab.bias.ext.Profile
	 */	
	public boolean execDOF3(DOF3Event e) {
		return execDOF2(MotionEvent.dof2Event(e));
	}
	
	public boolean execDOF6(DOF6Event e) {
		return execDOF2(MotionEvent.dof2Event(e));
	}
	
	public boolean execDOF2(DOF2Event event) {
		//currentMotionEvent = event;
		return false;
	}

	/**
	 * Internal use.
	 * 
	 * @see remixlab.bias.ext.Profile
	 */	
	public boolean flushDOF3(DOF3Event e) {
		return flushDOF2(MotionEvent.dof2Event(e));
	}
	
	public boolean flushDOF6(DOF6Event e) {
		return flushDOF2(MotionEvent.dof2Event(e));
	}
	
	public boolean flushDOF2(DOF2Event event) {
		return true;
	}
}