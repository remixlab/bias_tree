/*********************************************************************************
 * bias_tree
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 *********************************************************************************/

package remixlab.bias.event.shortcut;

import remixlab.bias.core.BogusEvent;
import remixlab.bias.event.MotionEvent;
import remixlab.util.Copyable;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;

/**
 * This class represents button shortcuts.
 * <p>
 * Button shortcuts can be of one of two forms: 1. Buttons (e.g., 'LEFT' , or even 'B_NOBUTTON'); 2. Button + modifier
 * key combinations (e.g., 'RIGHT' + 'CTRL').
 * <p>
 * Note that the shortcut may be empty: the no-button (B_NOBUTTON) and no-modifier-mask (B_NOMODIFIER_MASK) combo may
 * also defined a shortcut. Empty shortcuts may bind button-less motion interactions (e.g., mouse move without any
 * button pressed).
 */
public final class MotionShortcut extends Shortcut implements Copyable {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				appendSuper(super.hashCode()).
				append(id).
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

		MotionShortcut other = (MotionShortcut) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(id, other.id)
				.isEquals();
	}

	protected final Integer	id;

	/**
	 * Constructs an "empty" shortcut by conveniently calling {@code this(B_NOMODIFIER_MASK, B_NOBUTTON);}
	 */
	public MotionShortcut() {
		this(MotionEvent.NOMODIFIER_MASK, MotionEvent.NOID);
	}

	/**
	 * Defines a shortcut from the given button.
	 * 
	 * @param b
	 *          button
	 */
	public MotionShortcut(Integer b) {
		this(MotionEvent.NOMODIFIER_MASK, b);
	}

	/**
	 * Defines a shortcut from the given modifier mask and button combination.
	 * 
	 * @param m
	 *          the mask
	 * @param b
	 *          button
	 */
	public MotionShortcut(Integer m, Integer b) {
		super(m);
		this.id = b;
	}

	protected MotionShortcut(MotionShortcut other) {
		super(other);
		this.id = new Integer(other.id);
	}

	@Override
	public MotionShortcut get() {
		return new MotionShortcut(this);
	}

	/**
	 * Returns a textual description of this shortcut.
	 * 
	 * @return description
	 */
	public String description() {
		return description(id);
	}

	/**
	 * Internal. Low-level description() function.
	 */
	protected String description(Integer b) {
		String r = BogusEvent.modifiersText(mask);
		String bn = (b == MotionEvent.NOID) ? "NO_BUTTON" : b.toString() + "_BUTTON";
		r += (r.length() > 0) ? "+" + bn : bn;
		return r;
	}
}
