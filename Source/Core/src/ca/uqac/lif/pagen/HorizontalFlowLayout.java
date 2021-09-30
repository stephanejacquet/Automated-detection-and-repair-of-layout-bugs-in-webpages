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

public class HorizontalFlowLayout extends FlowLayout
{
	/**
	 * Creates a new horizontal flow layout manager
	 * @param max_elements The maximum number of elements to 
	 * flow before moving to the next line. Set to 0 to flow all elements
	 * on the same line
	 */
	public HorizontalFlowLayout(Picker<Integer> max_elements)
	{
		super(max_elements);
	}
	
	/**
	 * Creates a new horizontal flow layout manager
	 */
	public HorizontalFlowLayout()
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
		float x = p_padding, y = p_padding, max_y = 0;
		float bounding_w = 0, bounding_h = 0;
		int n = 0;
		LayoutConstraint.VerticallyAligned const_align = new LayoutConstraint.VerticallyAligned();
		for (int i = 0; i < children.size(); i++)
		{
			Box b = children.get(i);
			parent.addChild(b);
			boolean alignment_toss = m_injectAlignementFault.pick();
			float y_shift = 0;
			boolean altered = false;
			if (alignment_toss)
			{
				m_misalignmentCount++;
				y_shift = m_shiftPicker.pick();
				b.alter();
				altered = true;
			}
			b.shiftX(x);
			b.shiftY(y + y_shift);
			bounding_w = Math.max(bounding_w, x + b.getWidth());
			x += b.getWidth() + m_spacing;
			bounding_h = Math.max(bounding_h, y + b.getHeight() + y_shift);
			n++;
			const_align.add(b);
			if (i < children.size() - 1)
			{
				boolean overlap_toss = m_injectOverlapFault.pick();
				float y_expand = 0;
				if (!altered && overlap_toss)
				{
					m_overlapCount++;
					y_expand = m_overlapPicker.pick();
					b.setWidth(b.getWidth() + y_expand + m_spacing);
					b.alter();
					altered = true;
				}
			}
			if (max_elements > 0 && n == max_elements)
			{
				n = 0;
				x = p_padding;
				y += max_y + m_spacing;
				max_y = 0;
				if (const_align.isValid())
				{
					m_constraints.add(const_align);
				}
				const_align = new LayoutConstraint.VerticallyAligned();
			}
		}
		if (const_align.isValid())
		{
			m_constraints.add(const_align);
		}
		parent.setWidth(bounding_w + p_padding);
		parent.setHeight(bounding_h + p_padding);
		if (!children.isEmpty())
		{
			Box first = children.get(0);
			if (!first.isAltered() && m_injectOverflowFault.pick())
			{
				m_overflowCount++;
				float amount = m_overflowPicker.pick();
				first.shiftX(-amount);
				first.alter();
			}
		}
	}
}
