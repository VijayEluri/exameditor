package de.elatexam.editor.pages.taskdef;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.databinder.hib.Databinder;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.hibernate.Transaction;

import de.elatexam.editor.TaskEditorSession;
import de.elatexam.editor.components.panels.tree.ComplexTaskHierarchyPruner;
import de.elatexam.editor.pages.TaskDefPage;
import de.elatexam.editor.preview.PreviewLink;
import de.elatexam.editor.user.BasicUser;
import de.elatexam.model.ComplexTaskDef;
import de.elatexam.model.ComplexTaskDef.Revisions.Revision;
import de.elatexam.model.ObjectFactory;
import de.elatexam.model.SubTaskDef;

/**
 * Actions operating on ComplexTaskDefs
 *
 *
 */
public class TaskDefActions extends Panel {

    /**
     *
     */
    private final TaskDefPage taskDefPage;
    private final Link deleteLink;
    private final DownloadLink downloadLink;
    private final PreviewLink previewLink;
    private final AddElementLink addLink;
    private final TaskBlockSelectorModalWindow taskblockselectormodal;

    public TaskDefActions(final TaskDefPage taskDefPage, final String id) {
        super(id);
        this.taskDefPage = taskDefPage;

        this.downloadLink = createDownloadLink();


        add(taskblockselectormodal = new TaskBlockSelectorModalWindow("taskblockmodal"){

            @Override
            void onSelect(Class taskblockclass) {
                addLink.createTaskblock(taskblockclass);
            }

        });

        this.addLink = new AddElementLink("add", taskblockselectormodal, taskDefPage);

        deleteLink = new Link("delete") {

            @Override
            public void onClick() {
                // which domain object do we need to delete?
                final Object toDelete = new ComplexTaskHierarchyPruner(taskDefPage.getTree().getProvider()).removeFromParent(taskDefPage.getTreeSelection().getObject());
                // do not delete subtaskdefs, only remove them from the current complextaskdef
                if (!(toDelete instanceof SubTaskDef)) {
                    final org.hibernate.classic.Session session = Databinder.getHibernateSession();
                    final Transaction transaction = session.beginTransaction();
                    session.delete(toDelete);
                    transaction.commit();
                }

            }

        };
        deleteLink.add(new AttributeModifier("onclick", true, Model.of("return confirm('Sind Sie sicher, dass das selektierte Element gel&ouml;scht werden soll?');")));
        deleteLink.setEnabled(false);
        previewLink = new PreviewLink("preview", new AbstractReadOnlyModel<ComplexTaskDef>() {
            @Override
            public ComplexTaskDef getObject() {
                return taskDefPage.getTree().getCurrentTaskdef().getObject();
            }
        });
        previewLink.setEnabled(false);

        add(previewLink);
        add(downloadLink);
        add(addLink);
        add(deleteLink);
        // add(new NullPlug("delete"));
    }

    /**
     * Create a link that allows to download a serialized xml file for the currently selected taskdef.
     *
     * @return
     */
    private DownloadLink createDownloadLink() {
        DownloadLink downloadLink = new DownloadLink("export", new AbstractReadOnlyModel<File>() {

            @Override
            public File getObject() {
                File tempFile = null;
                try {
                    tempFile = File.createTempFile("taskdef", "export");
                    // marshal to xml
                    final JAXBContext context = JAXBContext.newInstance(ComplexTaskDef.class);
                    final Marshaller marshaller = context.createMarshaller();
                    final BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
                    final ComplexTaskDef ctd = taskDefPage.getTree().getCurrentTaskdef().getObject();
                    addRevisionTo(ctd);
                    marshaller.marshal(ctd, bw);
                    bw.close();
                } catch (final IOException e) {
                    error("Konnte leider keine Datei schreiben, Infos siehe Logfile.");
                    e.printStackTrace();
                } catch (final JAXBException e) {
                    error("Konnte leider kein XML erstellen, Infos siehe Logfile.");
                    e.printStackTrace();
                }
                return tempFile;
            }

            /**
             * Add current timestamp+author name as new revision.
             *
             * @param ctd
             */
            private void addRevisionTo(final ComplexTaskDef ctd) {
                final Revision rev = new ObjectFactory().createComplexTaskDefRevisionsRevision();
                rev.setAuthor(TaskEditorSession.get().getUser().getUsername());
                rev.setDate(System.currentTimeMillis());
                final List<Revision> revisions = ctd.getRevisions().getRevision();
                rev.setSerialNumber(revisions.size() + 1);
                revisions.add(rev);
            }
        }, "pruefung.xml");

        downloadLink.setDeleteAfterDownload(true);
        downloadLink.setEnabled(false);

        return downloadLink;
    }

    public void onSelect(final IModel<?> selectedModel, final AjaxRequestTarget target) {
        Object selected = selectedModel.getObject();
        boolean enabled = !(selected instanceof BasicUser);

        this.downloadLink.setEnabled(enabled);
        this.previewLink.setEnabled(enabled);
        this.deleteLink.setEnabled(enabled);
        this.addLink.setEnabled(true);
        // no admin user should be able to delete himself....
        if (selected instanceof BasicUser) {
            this.deleteLink.setEnabled(false == ((BasicUser) selected).getUsername().equals(TaskEditorSession.get().getUser().getUsername()));
        }
        target.addComponent(this);
    }

}