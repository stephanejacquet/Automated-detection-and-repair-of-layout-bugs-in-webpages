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

public class DotRenderer extends BoxRenderer
{
	public DotRenderer()
	{
		super();
	}
	
	@Override
	public void render(PrintStream ps, Box b)
	{
		ps.println("digraph G {");
		ps.println("node [shape=\"circle\",fillstyle=\"solid\"]");
		render(ps, b, -1);
		ps.println("}");
	}
	
	protected void render(PrintStream ps, Box b, int parent)
	{
		int id = b.getId();
		if (parent >= 0)
		{
			ps.println(parent + " -> " + id + ";");
		}
		for (Box b_c : b.getChildren())
		{
			render(ps, b_c, id);
		}
	}
}
