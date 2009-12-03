package de.elateportal.editor.components.panels.tasks.mapping;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import de.elateportal.editor.components.listeditor.ListEditor;
import de.elateportal.editor.components.listeditor.ListItem;
import de.elateportal.editor.components.listeditor.RemoveButton;
import de.elateportal.editor.components.panels.tasks.SubtaskSpecificsInputPanel;
import de.elateportal.model.MappingSubTaskDef;
import de.elateportal.model.MappingSubTaskDef.Assignment;
import de.elateportal.model.MappingSubTaskDef.Concept;

/**
 * @author Steffen Dienst
 * 
 */
public class MappingSubtaskDefInputPanel extends SubtaskSpecificsInputPanel<MappingSubTaskDef> {
    private final WebMarkupContainer assignementContainer, conceptContainer;

    public MappingSubtaskDefInputPanel(final String id, final IModel<MappingSubTaskDef> model) {
        super(id, model);
        // assignments
        assignementContainer = new WebMarkupContainer("assignementContainer");
        final ListEditor<Assignment> assignmentsList = new ListEditor<Assignment>("assignments", new PropertyModel<List<Assignment>>(model, "assignment")) {

            @Override
            protected void onPopulateItem(final ListItem<Assignment> item) {
                item.add(new TextField("id", new PropertyModel(item.getModel().getObject(), "id")));
                item.add(new TextField("name", new PropertyModel(item.getModel().getObject(), "name")));
                item.add(new RemoveButton("delete"));
            }

        };
        final AjaxButton addAssignement = new AjaxButton("addassignment") {
            @Override
            protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                assignmentsList.addItem(new Assignment());
                if (target != null) {
                    target.addComponent(assignementContainer);
                }
            }
        };
        add(addAssignement);
        addAssignement.setDefaultFormProcessing(false);
        assignementContainer.setOutputMarkupId(true);
        assignementContainer.add(assignmentsList);
        add(assignementContainer);

        conceptContainer = new WebMarkupContainer("conceptContainer");
        final ListEditor<Concept> conceptList = new ListEditor<Concept>("concepts", new PropertyModel<List<Concept>>(model, "concept")) {

            @Override
            protected void onPopulateItem(final ListItem<Concept> item) {
                item.add(new TextField("name", new PropertyModel(item.getModel().getObject(), "name")).setEscapeModelStrings(false));
                // TODO use getCorrectAssignmentIDItems instead of correctAssignmentID, the former
                // TODO is jpa specific, the later jaxb.... they don't get synchronized #$%##%!!
                item.add(new TextField("correctAssignmentID", new PropertyModel(item.getModel().getObject(), "correctAssignmentIDItems")) {
                    @Override
                    public IConverter getConverter(final Class<?> type) {
                        if (List.class.isAssignableFrom(type)) {
                            return new CorrectAssignementConverter();
                        } else {
                            return super.getConverter(type);
                        }
                    }

                    @Override
                    public void updateModel() {
                        // TODO Auto-generated method stub
                        super.updateModel();
                    }
                });
                item.add(new RemoveButton("delete"));
            }

        };
        add(conceptList);
        final AjaxButton addConcept = new AjaxButton("addconcept") {
            @Override
            protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                conceptList.addItem(new Concept());
                if (target != null) {
                    target.addComponent(conceptContainer);
                }
            }
        };

        addConcept.setDefaultFormProcessing(false);
        add(addConcept);
        conceptContainer.setOutputMarkupId(true);
        conceptContainer.add(conceptList);
        add(conceptContainer);
    }
}