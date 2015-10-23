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

public interface Constants {
	// 1. Processing
	// 1.a. Mouse
	public static int 	LEFT_ID	= 37,		
						CENTER_ID = 3,
						RIGHT_ID = 39,
						WHEEL_ID = 8,
						NO_BUTTON = BogusEvent.NO_ID,
	// 1.b. Keys
						LEFT_KEY	= 37,
						RIGHT_KEY = 39, 
						UP_KEY = 38,
						DOWN_KEY = 40;
	
	int [] motionIDs = {LEFT_ID,CENTER_ID,RIGHT_ID,WHEEL_ID,NO_BUTTON};
}
