package net.kornr.swit.site.quickstart.examples;

import java.awt.Color;
import java.awt.Font;
import net.kornr.swit.button.ButtonResource;
import net.kornr.swit.button.VistafarianButton;
import net.kornr.swit.button.effect.ShadowBorder;
import org.apache.wicket.markup.html.panel.Panel;

public class ButtonExample2 extends Panel 
{
	static private VistafarianButton s_buttonTemplate = new VistafarianButton();
	static {
		s_buttonTemplate.setFont(new Font("Verdana", Font.BOLD, 24));
		s_buttonTemplate.setWidth(300);
		s_buttonTemplate.setHeight(24);
		s_buttonTemplate.setFontColor(new Color(0xFFFFFF));
		s_buttonTemplate.setAutoExtend(Boolean.TRUE);
		s_buttonTemplate.setShadowDisplayed(Boolean.TRUE);
		s_buttonTemplate.setBaseColor(new Color(0xFF99AA));
		s_buttonTemplate.setRoundSize(18.0f);
		s_buttonTemplate.addEffect(new ShadowBorder(6,0,0,Color.black));
	}
	
	public ButtonExample2(String id) 
	{
		super(id);
		this.add(ButtonResource.getImage("button", s_buttonTemplate, "Some Fine Text For My Button"));
	}
}
