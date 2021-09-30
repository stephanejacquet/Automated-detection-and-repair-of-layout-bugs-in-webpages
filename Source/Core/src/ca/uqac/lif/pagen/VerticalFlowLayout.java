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

import ca.uqac.lif.synthia.Picker;
import ca.uqac.lif.synthia.util.Constant;

public class VerticalFlowLayout extends FlowLayout
{
	/**
	 * Creates a new horizontal flow layout manager
	 * @param max_elements The maximum number of elements to 
	 * flow before moving to the next line. Set to 0 to flow all elements
	 * on the same line
	 */
	public VerticalFlowLayout(Picker<Integer> max_elements)
	{
		super(max_elements);
	}
	
	/**
	 * Creates a new horizontal flow layout manager
	 */
	public VerticalFlowLayout()
	{
		this(new Constant<Integer>(0));
	}
	
	@Override
	public void arrange(Box parent, List<Box> children)
	{
		if (children.isEmpty())
		{
			return;
		}
		int max_elements = m_maxElements.pick();
		float p_padding = parent.getPadding();
		float x = p_padding, y = p_padding, max_x = 0;
		float bounding_w = 0, bounding_h = 0;
		int n = 0;
		LayoutConstraint.HorizontallyAligned const_align = new LayoutConstraint.HorizontallyAligned();
		for (int i = 0; i < children.size(); i++)
		{
			Box b = children.get(i);
			parent.addChild(b);
			boolean toss = m_injectAlignementFault.pick();
			float x_shift = 0;
			if (toss)
			{
				m_misalignmentCount++;
				x_shift = m_shiftPicker.pick();
				b.alter();
			}
			b.shiftX(x + x_shift);
			b.shiftY(y);
			bounding_h = Math.max(bounding_h, y + b.getHeight());
			y += b.getHeight() + m_spacing;
			bounding_w = Math.max(bounding_w, x + b.getWidth() + x_shift);
			max_x = Math.max(max_x, b.getWidth());
			n++;
			const_align.add(b);
			if (i < children.size() - 1)
			{
				boolean overlap_toss = m_injectOverlapFault.pick();
				float y_expand = 0;
				if (overlap_toss)
				{
					m_overlapCount++;
					y_expand = m_overlapPicker.pick();
					b.setHeight(b.getHeight() + y_expand + m_spacing);
					b.alter();
				}
			}
			if (max_elements > 0 && n == max_elements)
			{
				n = 0;
				y = p_padding;
				x += max_x + m_spacing;
				max_x = 0;
				if (const_align.isValid())
				{
					m_constraints.add(const_align);
				}
				const_align = new LayoutConstraint.HorizontallyAligned();
			}
		}
		if (const_align.isValid())
		{
			m_constraints.add(const_align);
		}
		if (!children.isEmpty())
		{
			Box first = children.get(0);
			if (!first.isAltered() && m_injectOverflowFault.pick())
			{
				m_overflowCount++;
				float amount = m_overflowPicker.pick();
				first.shiftY(-amount);
				first.alter();
			}
		}
		parent.setWidth(bounding_w + p_padding);
		parent.setHeight(bounding_h + p_padding);
	}
}
