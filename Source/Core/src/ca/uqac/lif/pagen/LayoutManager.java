/*
    A random DOM tree generator
    Copyright (C) 2020 Sylvain Hallé
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.pagen;

import java.util.List;
import java.util.Set;

public interface LayoutManager
{
	/**
	 * Positions a list of boxes inside a parent box
	 * @param parent The parent box
	 * @param children The children boxes
	 */
	public void arrange(Box parent, List<Box> children);
	
	/**
	 * Gets the set of constraints accumulated by the operation of this
	 * layout manager
	 * @return The set of layout constraints
	 */
	public Set<LayoutConstraint> getConstraints();
}
