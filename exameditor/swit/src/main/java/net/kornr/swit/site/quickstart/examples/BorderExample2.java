package net.kornr.swit.site.quickstart.examples;

import java.awt.Color;
import net.kornr.swit.wicket.border.TableImageBorder;
import net.kornr.swit.wicket.border.graphics.GenericShadowBorder;
import net.kornr.swit.wicket.border.graphics.RoundedBorderMaker;
import org.apache.wicket.markup.html.panel.Panel;

public class BorderExample2 extends Panel 
{
	static private Long s_border = RoundedBorderMaker.register(12, 6f, Color.red, Color.white);
	static private Long s_shadowed = GenericShadowBorder.register(s_border, 0, 0, 6, null);
	
	public BorderExample2(String id) {
		super(id);
		this.add(new TableImageBorder("border", s_shadowed, Color.white));
	}

}
