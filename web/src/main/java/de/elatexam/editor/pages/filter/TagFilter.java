/*

Copyright (C) 2011 Steffen Dienst

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
package de.elatexam.editor.pages.filter;

import org.apache.poi.hssf.record.formula.functions.T;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.AbstractFilter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.wicketstuff.tagit.TagItTextField;

/**
 * @author Steffen Dienst
 *
 */
public class TagFilter  extends AbstractFilter{
  private TextField<T> filter;

  public TagFilter(final String id, final FilterForm<?> form)
  {
    super(id, form);
    filter = new TagItTextField("filter"){
      @Override
      protected Iterable getChoices(String input) {
        // TODO Auto-generated method stub
        return null;
      }};
    //enableFocusTracking(filter);
    add(filter);
  }

  /**
   * @return underlying {@link TextField} form component that represents this filter
   */
  public final TextField<T> getFilter()
  {
    return filter;
  }
}
