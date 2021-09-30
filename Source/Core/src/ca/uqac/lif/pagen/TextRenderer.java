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

import java.io.PrintStream;

public class TextRenderer extends BoxRenderer
{
	public TextRenderer()
	{
		super();
	}
	
	@Override
	public void render(PrintStream ps, Box b)
	{
		int id = b.getId();
		ps.print("x_" + id + "=" + b.getX());
		ps.print(",");
		ps.print("y_" + id + "=" + b.getY());
		ps.print(",");
		ps.print("w_" + id + "=" + b.getWidth());
		ps.print(",");
		ps.print("h_" + id + "=" + b.getHeight());
		ps.println();
		for (Box b_c : b.getChildren())
		{
			render(ps, b_c);
		}
	}
}
