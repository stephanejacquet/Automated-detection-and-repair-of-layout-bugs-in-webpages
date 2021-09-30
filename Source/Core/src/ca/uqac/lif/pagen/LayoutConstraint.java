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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class LayoutConstraint 
{
	
	public LayoutConstraint()
	{
		super();
	}
	
	/**
	 * Gets the name of the constraint
	 * @return The name of the constraint
	 */
	protected abstract String getName();
	
	/**
	 * Determines if a constraint is valid
	 * @return <tt>true</tt> if the constraint is valid, <tt>false</tt> otherwise
	 */
	protected boolean isValid()
	{
		return true;
	}
	
	public abstract static class BinaryLayoutConstraint extends LayoutConstraint
	{
		protected Box m_box1;
		
		protected Box m_box2;
		
		public BinaryLayoutConstraint(Box b1, Box b2)
		{
			super();
			m_box1 = b1;
			m_box2 = b2;
		}
		
		@Override
		protected boolean isValid()
		{
			return m_box1 != null && m_box2 != null && m_box1.getId() != m_box2.getId();
		}
	}
	
	public abstract static class MultiLayoutConstraint extends LayoutConstraint
	{
		protected Set<Box> m_boxes;
		
		public MultiLayoutConstraint()
		{
			super();
			m_boxes = new HashSet<Box>();
		}
		
		/**
		 * Adds a box subjected to a constraint
		 * @param b The box
		 * @return This constraint
		 */
		public LayoutConstraint add(Box b)
		{
			m_boxes.add(b);
			return this;
		}
		
		@Override
		public String toString()
		{
			StringBuilder out = new StringBuilder();
			out.append(getName());
			boolean first = true;
			for (Box b : m_boxes)
			{
				if (first)
				{
					first = false;
				}
				else
				{
					out.append(",");
				}
				out.append(b.getId());
			}
			return out.toString();
		}
	}
	
	public static class HorizontallyAligned extends MultiLayoutConstraint
	{
		@Override
		protected String getName()
		{
			return "Horizontally aligned: ";
		}
		
		@Override
		protected boolean isValid()
		{
			return m_boxes.size() >= 2;
		}
	}
	
	public static class VerticallyAligned extends MultiLayoutConstraint
	{
		@Override
		protected String getName()
		{
			return "Vertically aligned: ";
		}
		
		@Override
		protected boolean isValid()
		{
			return m_boxes.size() >= 2;
		}
	}
	
	public static class Disjoint extends BinaryLayoutConstraint
	{
		public Disjoint(Box b1, Box b2) 
		{
			super(b1, b2);
		}
		
		@Override
		protected String getName()
		{
			return "Disjoint: ";
		}
		
		/**
		 * Parses a box tree and generates all the disjointness constraints between
		 * all the children of a parent box
		 * @param b The parent box
		 * @return The set of constraints
		 */
		public static Set<LayoutConstraint> addContainmentConstraints(Box b)
		{
			Set<LayoutConstraint> set = new HashSet<LayoutConstraint>();
			addDisjointnessConstraints(b, set);
			return set;
		}
		
		/**
		 * Recursively arses a box tree and generates all the disjointness
		 * constraints between all the children of a parent box
		 * @param b The parent box
		 * @param set The set of constraints. Constraints will be added into
		 * that set
		 */
		protected static void addDisjointnessConstraints(Box b, Set<LayoutConstraint> set)
		{
			List<Box> children = b.getChildren();
			for (int i = 0; i < children.size(); i++)
			{
				for (int j = i + 1; j < children.size(); j++)
				{
					set.add(new Disjoint(children.get(i), children.get(j)));
				}
				addDisjointnessConstraints(children.get(i), set);
			}
		}
	}
	
	public static class Contained extends BinaryLayoutConstraint
	{
		public Contained(Box parent, Box child) 
		{
			super(parent, child);
		}

		@Override
		protected String getName()
		{
			return "Contained: ";
		}
		
		@Override
		public String toString()
		{
			return m_box2.getId() + " within " + m_box1.getId();
		}
		
		/**
		 * Parses a box tree and generates all the containment constraints between
		 * a parent box and its children
		 * @param b The parent box
		 * @return The set of constraints
		 */
		public static Set<LayoutConstraint> addContainmentConstraints(Box b)
		{
			Set<LayoutConstraint> set = new HashSet<LayoutConstraint>();
			addContainmentConstraints(b, set);
			return set;
		}
		
		/**
		 * Recursively arses a box tree and generates all the containment
		 * constraints between a parent box and its children
		 * @param b The parent box
		 * @param set The set of constraints. Constraints will be added into
		 * that set
		 */
		protected static void addContainmentConstraints(Box b, Set<LayoutConstraint> set)
		{
			for (Box child : b.getChildren())
			{
				set.add(new Contained(b, child));
				addContainmentConstraints(child, set);
			}
		}
	}
}