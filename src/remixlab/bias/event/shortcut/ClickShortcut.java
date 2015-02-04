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
import remixlab.util.Copyable;
import remixlab.util.EqualsBuilder;
import remixlab.util.HashCodeBuilder;

/**
 * This class represents click shortcuts.
 * <p>
 * Click shortcuts are defined with a specific number of clicks and can be of one out of two forms: 1. A button; and, 2.
 * A button plus a key-modifier (such as the CTRL key).
 * <p>
 * Note that click shortcuts should have at least one click.
 */
public class ClickShortcut extends Shortcut implements Copyable {
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				appendSuper(super.hashCode()).
				append(numberOfClicks).
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

		ClickShortcut other = (ClickShortcut) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(numberOfClicks, other.numberOfClicks)
				.append(id, other.id)
				.isEquals();
	}

	protected final Integer	numberOfClicks;
	protected final Integer	id;

	/**
	 * Defines a single click shortcut from the given button.
	 * 
	 * @param id
	 *          id
	 */
	public ClickShortcut(Integer id) {
		this(BogusEvent.NO_MODIFIER_MASK, id, 1);
	}

	/**
	 * Defines a click shortcut from the given button and number of clicks.
	 * 
	 * @param id
	 *          id
	 * @param c
	 *          number of clicks
	 */
	public ClickShortcut(Integer id, Integer c) {
		this(BogusEvent.NO_MODIFIER_MASK, id, c);
	}

	/**
	 * Defines a click shortcut from the given button, modifier mask, and number of clicks.
	 * 
	 * @param m
	 *          modifier mask
	 * @param id
	 *          id
	 * @param c
	 *          bumber of clicks
	 */
	public ClickShortcut(Integer m, Integer id, Integer c) {
		super(m);
		this.id = id;
		if (c <= 0)
			this.numberOfClicks = 1;
		else
			this.numberOfClicks = c;
	}

	protected ClickShortcut(ClickShortcut other) {
		super(other);
		this.numberOfClicks = new Integer(other.numberOfClicks);
		this.id = new Integer(other.id);
	}

	@Override
	public ClickShortcut get() {
		return new ClickShortcut(this);
	}

	public int id() {
		return id;
	}

	/**
	 * Returns a textual description of this click shortcut.
	 * 
	 * @return description
	 */
	public String description() {
		String r = new String();
		if (mask != 0)
			r += BogusEvent.modifiersText(mask) + "+" + id.toString() + "_ID";
		if (numberOfClicks == 1)
			r += (r.length() > 0) ? "+" + numberOfClicks.toString() + "_click" : numberOfClicks.toString() + "_click";
		else
			r += (r.length() > 0) ? "+" + numberOfClicks.toString() + "_clicks" : numberOfClicks.toString() + "_clicks";
		return r;
	}
}
