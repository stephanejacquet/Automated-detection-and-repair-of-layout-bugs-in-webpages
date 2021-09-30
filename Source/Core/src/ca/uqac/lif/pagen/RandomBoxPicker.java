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

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.synthia.Picker;
import ca.uqac.lif.synthia.util.Constant;

public class RandomBoxPicker implements Picker<Box>
{
	protected Picker<Integer> m_numChildren;

	protected Picker<Integer> m_depth;

	protected Picker<Float> m_width;

	protected Picker<Float> m_height;

	protected Picker<LayoutManager> m_layout;

	public RandomBoxPicker(Picker<Integer> children, Picker<Integer> depth, Picker<LayoutManager> layout, Picker<Float> width, Picker<Float> height)
	{
		super();
		m_numChildren = children;
		m_depth = depth;
		m_width = width;
		m_height = height;
		m_layout = layout;
	}

	@Override
	public Picker<Box> duplicate(boolean with_state)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Box pick()
	{
		int num_children = m_numChildren.pick();
		List<Box> children = new ArrayList<Box>(num_children);
		for (int i = 0; i < num_children; i++)
		{
			Box b = null;
			int depth = m_depth.pick();
			if (depth == 0)
			{
				b = new Box(0f, 0f, m_width.pick(), m_height.pick());
				b.setPadding(2);
			}
			else
			{
				RandomBoxPicker bp = new RandomBoxPicker(m_numChildren, new Constant<Integer>(depth - 1), m_layout, m_width, m_height);
				b = bp.pick();
			}
			children.add(b);
		}
		Box parent = new Box(0, 0, 0, 0);
		parent.setPadding(2);
		LayoutManager layout = m_layout.pick();
		layout.arrange(parent, children);
		return parent;
	}

	@Override
	public void reset() 
	{
		m_numChildren.reset();
		m_depth.reset();
	}
}
