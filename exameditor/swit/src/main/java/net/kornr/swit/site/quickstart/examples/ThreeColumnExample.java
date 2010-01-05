package net.kornr.swit.site.quickstart.examples;

import net.kornr.swit.wicket.layout.ColumnPanel;
import net.kornr.swit.wicket.layout.LayoutInfo;
import net.kornr.swit.wicket.layout.ThreeColumnsLayoutManager;
import net.kornr.swit.wicket.layout.threecol.ThreeColumnsLayoutResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class ThreeColumnExample extends Panel 
{
	static private LayoutInfo s_layout = new LayoutInfo(LayoutInfo.UNIT_PIXEL, 100, 100);
	static {
		ThreeColumnsLayoutResource.register(s_layout);
	}
	
	public ThreeColumnExample(String id) {
		super(id);
		
		ThreeColumnsLayoutManager layoutmanager = new ThreeColumnsLayoutManager("layout", s_layout);
        this.add(layoutmanager);
        
        // ColumnPanel for the right and left columns are provided by the layout manager
        ColumnPanel rightcolumn = layoutmanager.getRightColumn();
        rightcolumn.addContent(new Label(rightcolumn.getContentId(), "Some Random Panel on the right"));
        
        ColumnPanel leftcolumn = layoutmanager.getLeftColumn();
        leftcolumn.addContent(new Label(leftcolumn.getContentId(), "Any component can be added on the sides, not just panels"));
	}

}
