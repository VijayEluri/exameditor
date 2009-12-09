/*

Copyright (C) 2009 Steffen Dienst

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package de.elateportal.editor.components.panels.tree;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.model.IModel;

import wickettree.NestedTree;
import de.elateportal.editor.pages.TaskDefPage;

/**
 * @author Steffen Dienst
 * 
 */
public class ComplexTaskDefTree extends NestedTree {

	private final TaskDefPage taskDefPage;

	public ComplexTaskDefTree(String id, TaskDefPage taskDefPage, ComplexTaskdefTreeProvider provider) {
		super(id, provider);
		this.taskDefPage = taskDefPage;
		add(CSSPackageResource.getHeaderContribution(new CompressedResourceReference(ComplexTaskDefTree.class, "theme/theme.css")));

	}

	@Override
	protected Component newContentComponent(String id, IModel model) {
		return new TaskTreeElement(id, this, taskDefPage, model);
	}

}
