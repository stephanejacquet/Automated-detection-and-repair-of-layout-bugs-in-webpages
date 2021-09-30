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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import ca.uqac.lif.pagen.CliParser.Argument;
import ca.uqac.lif.pagen.CliParser.ArgumentMap;
import ca.uqac.lif.pagen.LayoutConstraint.Contained;
import ca.uqac.lif.pagen.LayoutConstraint.Disjoint;
import ca.uqac.lif.synthia.Picker;
import ca.uqac.lif.synthia.random.RandomIntervalFloat;
import ca.uqac.lif.synthia.util.ElementPicker;
import ca.uqac.lif.synthia.random.PoissonInteger;
import ca.uqac.lif.synthia.random.RandomBoolean;
import ca.uqac.lif.synthia.random.RandomFloat;
import ca.uqac.lif.synthia.random.RandomInteger;

public class Main 
{
	public static void main(String[] args) throws FileNotFoundException 
	{
		// Program defaults
		int seed = 1;
		int min_depth = 3;
		int max_depth = 4;
		float p_degree = 2.2f;
		float p_misalignment = 0.1f, p_overlap = 0.5f, p_overflow = 0.5f;
		String type = "html";
		boolean quiet = false;
		PrintStream out = System.out;

		// Override by CLI parameters
		CliParser parser = setupParser();
		ArgumentMap arg_map = parser.parse(args);
		if (arg_map == null || arg_map.containsKey("help"))
		{
			parser.printHelp("Random DOM tree generator\n(C) 2020 Laboratoire d'informatique formelle\nUniversité du Québec à Chicoutimi, Canada\nhttps://liflab.ca\n\nUsage java -jar pagen.jar [options]", System.out);
			System.exit(1);
		}
		if (arg_map.hasOption("min-depth"))
		{
			min_depth = Integer.parseInt(arg_map.get("min_depth").trim());
		}
		if (arg_map.hasOption("max-depth"))
		{
			max_depth = Integer.parseInt(arg_map.get("max_depth").trim());
		}
		if (arg_map.hasOption("degree"))
		{
			p_degree = Float.parseFloat(arg_map.get("degree").trim());
		}
		if (arg_map.hasOption("seed"))
		{
			seed = Integer.parseInt(arg_map.get("seed").trim());
		}
		if (arg_map.hasOption("misalign"))
		{
			p_misalignment = Float.parseFloat(arg_map.get("misalign").trim());
		}
		if (arg_map.hasOption("overlap"))
		{
			p_overlap = Float.parseFloat(arg_map.get("overlap").trim());
		}
		if (arg_map.hasOption("overflow"))
		{
			p_overflow = Float.parseFloat(arg_map.get("overflow").trim());
		}
		if (arg_map.hasOption("type"))
		{
			type = arg_map.get("type");
		}
		if (arg_map.hasOption("output"))
		{
			String filename = arg_map.get("output");
			out = new PrintStream(new FileOutputStream(new File(filename)));
		}

		// Initialize RNGs and seed
		RandomInteger depth = new RandomInteger(min_depth, max_depth); // 6-22
		PoissonInteger degree = new PoissonInteger(p_degree);
		RandomIntervalFloat width = new RandomIntervalFloat(5, 20);
		RandomIntervalFloat height = new RandomIntervalFloat(5, 10);
		RandomFloat float_source = new RandomFloat();
		RandomInteger row_size = new RandomInteger(0, 10);
		RandomInteger column_size = new RandomInteger(0, 10);
		RandomBoolean misalignment = new RandomBoolean(p_misalignment);
		RandomInteger misalignment_shift = new RandomInteger(2, 10);
		RandomBoolean overlap = new RandomBoolean(p_overlap);
		RandomInteger overlap_shift = new RandomInteger(2, 10);
		RandomBoolean overflow = new RandomBoolean(p_overflow);
		RandomInteger overflow_shift = new RandomInteger(2, 10);
		ElementPicker<LayoutManager> layout = new ElementPicker<LayoutManager>(float_source);
		RandomInteger rand_color = new RandomInteger(128,255);
		Picker<String> color = new ColorPicker(rand_color);
		if (seed >= 0)
		{
			depth.setSeed(seed);
			degree.setSeed(seed + 1);
			float_source.setSeed(seed + 2);
			width.setSeed(seed + 3);
			height.setSeed(seed + 4);
			row_size.setSeed(seed + 5);
			column_size.setSeed(seed + 6);
			misalignment_shift.setSeed(seed + 7);
			misalignment.setSeed(seed + 8);
			overlap_shift.setSeed(seed + 9);
			overlap.setSeed(seed + 10);
			overflow_shift.setSeed(seed + 11);
			overflow.setSeed(seed + 12);
			rand_color.setSeed(seed + 13);
		}

		// Setup box picker
		HorizontalFlowLayout hfl_1 = new HorizontalFlowLayout();
		hfl_1.setAlignmentFault(misalignment, misalignment_shift);
		hfl_1.setOverlapFault(overlap, overlap_shift);
		hfl_1.setOverflowFault(overflow, overflow_shift);
		HorizontalFlowLayout hfl_2 = new HorizontalFlowLayout(row_size);
		hfl_2.setAlignmentFault(misalignment, misalignment_shift);
		hfl_2.setOverlapFault(overlap, overlap_shift);
		hfl_2.setOverflowFault(overflow, overflow_shift);
		VerticalFlowLayout vfl_1 = new VerticalFlowLayout(column_size);
		vfl_1.setAlignmentFault(misalignment, misalignment_shift);
		vfl_1.setOverlapFault(overlap, overlap_shift);
		vfl_1.setOverflowFault(overflow, overflow_shift);
		layout.add(hfl_1, 0.2);
		layout.add(hfl_2, 0.4);
		layout.add(vfl_1, 0.4);
		RandomBoxPicker box_picker = new RandomBoxPicker(degree, depth, layout, width, height);
		Box b = box_picker.pick();
		
		// Render
		BoxRenderer renderer = null;
		if (type.compareToIgnoreCase("html") == 0)
		{
			renderer = new HtmlRenderer(color);
		}
		else if (type.compareToIgnoreCase("opl") == 0)
		{
			renderer = new OplRenderer(hfl_1.getConstraints(), hfl_2.getConstraints(), vfl_1.getConstraints(), Contained.addContainmentConstraints(b), Disjoint.addContainmentConstraints(b));
		}
		else if (type.compareToIgnoreCase("dot") == 0)
		{
			renderer = new DotRenderer();
		}
		renderer.render(out, b);
		if (!quiet)
		{
			System.err.println("Tree size:                " + b.getSize());
			System.err.println("Tree depth:               " + b.getDepth());
			System.err.println("Horizontal misalignments: " + (hfl_1.getMisalignmentCount() + hfl_2.getMisalignmentCount()));
			System.err.println("Vertical misalignments:   " + (vfl_1.getMisalignmentCount()));
			System.err.println("Overlappings:             " + (vfl_1.getOverlapCount()));
			System.err.println("Overflows:                " + (vfl_1.getOverflowCount()));
		}
		out.close();
	}

	public static void renderToFile(Box b, BoxRenderer r, String filename) throws FileNotFoundException
	{
		PrintStream ps = new PrintStream(new FileOutputStream(new File(filename)));
		r.render(ps, b);
		ps.close();
	}

	protected static CliParser setupParser()
	{
		CliParser parser = new CliParser();
		parser.addArgument(new Argument().withLongName("type").withShortName("t").withArgument("x").withDescription("\tOutput file of type x (html, dot, opl)"));
		parser.addArgument(new Argument().withLongName("seed").withShortName("s").withArgument("x").withDescription("\tInitialize RNG with seed s"));
		parser.addArgument(new Argument().withLongName("misalign").withShortName("m").withArgument("x").withDescription("\tSet misalignment probability to p (in [0,1])"));
		parser.addArgument(new Argument().withLongName("overlap").withShortName("l").withArgument("x").withDescription("\tSet overlap probability to p (in [0,1])"));
		parser.addArgument(new Argument().withLongName("overflow").withShortName("w").withArgument("x").withDescription("\tSet overflow probability to p (in [0,1])"));
		parser.addArgument(new Argument().withLongName("min-depth").withShortName("d").withArgument("x").withDescription("Set minimum document depth to x"));
		parser.addArgument(new Argument().withLongName("max-depth").withShortName("D").withArgument("x").withDescription("Set maximum document depth to x"));
		parser.addArgument(new Argument().withLongName("degree").withShortName("g").withArgument("x").withDescription("\tSet degree to Poisson distribution with parameter x"));
		parser.addArgument(new Argument().withLongName("quiet").withShortName("q").withDescription("\tDon't print generation stats to stderr"));
		parser.addArgument(new Argument().withLongName("help").withShortName("?").withDescription("\tShow command line usage"));
		parser.addArgument(new Argument().withLongName("output").withShortName("o").withArgument("file").withDescription("Output to file"));
		return parser;
	}

}
