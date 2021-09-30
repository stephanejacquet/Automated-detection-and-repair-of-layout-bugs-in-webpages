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

import ca.uqac.lif.synthia.Picker;

public class ColorPicker implements Picker<String>
{
	/**
	 * A picker for the red component of the color
	 */
	protected Picker<Integer> m_r;
	
	/**
	 * A picker for the green component of the color
	 */
	protected Picker<Integer> m_g;
	
	/**
	 * A picker for the blue component of the color
	 */
	protected Picker<Integer> m_b;
	
	public ColorPicker(Picker<Integer> p)
	{
		this(p, p, p);
	}
	
	public ColorPicker(Picker<Integer> r, Picker<Integer> g, Picker<Integer> b)
	{
		super();
		m_r = r;
		m_g = g;
		m_b = b;
	}
	
	
	@Override
	public Picker<String> duplicate(boolean arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String pick()
	{
		return String.format("#%02x%02x%02x", m_r.pick(), m_g.pick(), m_b.pick());
	}

	@Override
	public void reset()
	{
		m_r.reset();
		m_g.reset();
		m_b.reset();
	}

}
