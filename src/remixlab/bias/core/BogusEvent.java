/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.core;

import remixlab.util.Copyable;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;

/**
 * The root event class of all events that are to be handled by an {@link remixlab.bias.core.Agent}. Every BogusEvent
 * encapsulates a {@link remixlab.bias.core.Shortcut} which may be bound to an user-defined
 * {@link remixlab.bias.core.Action} (see {@link #shortcut()}).
 * <p>
 * The following are the main class specializations: {@link remixlab.bias.event.MotionEvent},
 * {@link remixlab.bias.event.ClickEvent}, and {@link remixlab.bias.event.KeyboardEvent}. Please refer to their
 * documentation for details.
 * <p>
 * <b>Note</b> BogusEvent detection/reduction could happened in several different ways. For instance, in the context of
 * Java-based application, it typically takes place when implementing a mouse listener interface. In Processing, it does
 * it when registering at the PApplet the so called mouseEvent and KeyEvent methods. Moreover, the
 * {@link remixlab.bias.core.Agent#feed()} provides a callback alternative when none of these mechanisms are available
 * (as it often happens when dealing with specialized, non-default input hardware).
 */
public class BogusEvent implements Copyable {
	// modifier keys
	public static final int	NO_MODIFIER_MASK	= 0;
	public static final int	NO_ID							= 0;
	public static final int	SHIFT							= 1 << 0;
	public static final int	CTRL							= 1 << 1;
	public static final int	META							= 1 << 2;
	public static final int	ALT								= 1 << 3;
	public static final int	ALT_GRAPH					= 1 << 4;
	
	private boolean flush;

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				append(modifiers).
				append(id).
				append(timestamp).
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

		BogusEvent other = (BogusEvent) obj;
		return new EqualsBuilder()
				.append(modifiers, other.modifiers)
				.append(id, other.id)
				.append(timestamp, other.timestamp)
				.isEquals();
	}

	protected final int	modifiers;
	protected long			timestamp;
	protected int				id;

	/**
	 * Constructs an event with an "empty" {@link remixlab.bias.core.Shortcut}.
	 */
	public BogusEvent() {
		this.modifiers = NO_MODIFIER_MASK;
		this.id = NO_ID;
		timestamp = System.currentTimeMillis();
	}

	/**
	 * Constructs an event taking the given {@code modifiers} as a {@link remixlab.bias.core.Shortcut}.
	 */
	public BogusEvent(int modifiers, int id) {
		this.modifiers = modifiers;
		this.id = id;
		timestamp = System.currentTimeMillis();
	}

	protected BogusEvent(BogusEvent other) {
		this.modifiers = other.modifiers;
		this.id = other.id;
		this.timestamp = other.timestamp;
	}

	@Override
	public BogusEvent get() {
		return new BogusEvent(this);
	}
	
	public BogusEvent flush() {
		BogusEvent message = this.get();
		message.flush = true;
		return message;
	}
	
	public boolean flushed() {
		return flush;
	}

	/**
	 * @return the shortcut encapsulated by this event.
	 */
	public Shortcut shortcut() {
		return new Shortcut(modifiers(), id());
	}

	/**
	 * @return the modifiers defining the event {@link remixlab.bias.core.Shortcut}.
	 */
	public int modifiers() {
		return modifiers;
	}

	/**
	 * Returns the id defining the event's {@link remixlab.bias.core.Shortcut}.
	 */
	public int id() {
		return id;
	}

	/**
	 * @return the time at which the event occurs
	 */
	public long timestamp() {
		return timestamp;
	}

	/**
	 * Useful when reducing a motion bogus event with higher to lesser dof's.
	 * 
	 * @see remixlab.bias.event.DOF2Event#dof1Event()
	 * @see remixlab.bias.event.DOF3Event#dof2Event()
	 * @see remixlab.bias.event.DOF6Event#dof3Event()
	 */
	public void modifiedTimestamp(long newtimestamp) {
		timestamp = newtimestamp;
	}

	/**
	 * Only {@link remixlab.bias.event.MotionEvent}s may be null.
	 */
	public boolean isNull() {
		return false;
	}

	/**
	 * @return true if Shift was down when the event occurs
	 */
	public boolean isShiftDown() {
		return (modifiers & SHIFT) != 0;
	}

	/**
	 * @return true if Ctrl was down when the event occurs
	 */
	public boolean isControlDown() {
		return (modifiers & CTRL) != 0;
	}

	/**
	 * @return true if Meta was down when the event occurs
	 */
	public boolean isMetaDown() {
		return (modifiers & META) != 0;
	}

	/**
	 * @return true if Alt was down when the event occurs
	 */
	public boolean isAltDown() {
		return (modifiers & ALT) != 0;
	}

	/**
	 * @return true if AltGraph was down when the event occurs
	 */
	public boolean isAltGraph() {
		return (modifiers & ALT_GRAPH) != 0;
	}

	/**
	 * @param mask
	 *          of modifiers
	 * @return a String listing the event modifiers
	 */
	public static String modifiersText(int mask) {
		String r = new String();
		if ((ALT & mask) == ALT)
			r += "ALT";
		if ((SHIFT & mask) == SHIFT)
			r += (r.length() > 0) ? "+SHIFT" : "SHIFT";
		if ((CTRL & mask) == CTRL)
			r += (r.length() > 0) ? "+CTRL" : "CTRL";
		if ((META & mask) == META)
			r += (r.length() > 0) ? "+META" : "META";
		if ((ALT_GRAPH & mask) == ALT_GRAPH)
			r += (r.length() > 0) ? "+ALT_GRAPH" : "ALT_GRAPH";
		return r;
	}
}
