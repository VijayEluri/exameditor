package de.elatexam.editor.pages.taskdef;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.databinder.hib.Databinder;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.event.IEventSource;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.hibernate.Transaction;

import com.visural.wicket.component.confirmer.ConfirmerLink;

import de.elatexam.editor.TaskEditorSession;
import de.elatexam.editor.components.GermanConfirmerLink;
import de.elatexam.editor.components.panels.tree.ComplexTaskDefTree;
import de.elatexam.editor.components.panels.tree.ComplexTaskHierarchyFacade;
import de.elatexam.editor.preview.PreviewComplexLink;
import de.elatexam.editor.user.BasicUser;
import de.elatexam.editor.util.Stuff;
import de.elatexam.model.ComplexTaskDef;
import de.elatexam.model.ComplexTaskDef.Revisions;
import de.elatexam.model.ComplexTaskDef.Revisions.Revision;
import de.elatexam.model.Indexed;
import de.elatexam.model.ObjectFactory;
import de.elatexam.model.SubTaskDef;
import de.elatexam.model.manual.HomogeneousTaskBlock;

/**
 * Actions operating on ComplexTaskDefs
 *
 *
 */
public class TaskDefActions extends Panel{
	private final Link<File> downloadLink;
	private final Link<?> previewLink;
	private final ConfirmerLink deleteLink;
	private final AddElementLink addLink;
	private final ComplexTaskDefTree<Indexed> tree;
	
	public TaskDefActions(String id, ComplexTaskDefTree<Indexed> t) {
		super(id);
		this.tree = t;
		this.downloadLink = createDownloadLink();

		setOutputMarkupId(true);
		ModalWindow taskblockselectormodal = new TaskBlockSelectorModalWindow("taskblockmodal") {
			@Override
			void onSelect(Class<? extends HomogeneousTaskBlock> taskblockclass) {
				addLink.createTaskblock(taskblockclass);
			}
		};
		add(taskblockselectormodal);
		TaskSelectorModalWindow taskselectormodal = new TaskSelectorModalWindow("taskmodal") {
			@Override
			void onSelect(AjaxRequestTarget target, SubTaskDef... subtaskdefs) {
				addLink.addTasks(subtaskdefs);
				target.addComponent(tree);
			}
		};
		add(taskselectormodal);

		this.addLink = new AddElementLink("add", taskblockselectormodal,taskselectormodal, tree);

		deleteLink = new GermanConfirmerLink("delete") {

			@Override
			public void onClick() {
				// which domain object do we need to delete?
				final Object toDelete = new ComplexTaskHierarchyFacade(tree.getProvider()).removeFromParent(tree.getSelected().getObject());
				// do not delete subtaskdefs, only remove them from the current
				// complextaskdef
				if (!(toDelete instanceof SubTaskDef)) {
					final org.hibernate.Session session = Databinder
							.getHibernateSession();
					session.delete(toDelete);
					session.getTransaction().commit();
				} else {
					Stuff.saveAll(tree.getSelected().getObject());
				}
			}
		};
		deleteLink.setEnabled(false);
		deleteLink.setMessageContentHTML("Sind Sie sicher, dass das selektierte Element gel&ouml;scht werden soll?");
		
		previewLink = new PreviewComplexLink("preview",
				new AbstractReadOnlyModel<ComplexTaskDef>() {
					@Override
					public ComplexTaskDef getObject() {
						return tree.getCurrentTaskdef().getObject();
					}
				});
		previewLink.setEnabled(false);

		add(previewLink);
		add(downloadLink);
		add(addLink);
		add(deleteLink);
	}


	/**
	 * Create a link that allows to download a serialized xml file for the
	 * currently selected taskdef.
	 *
	 * @return
	 */
	private DownloadLink createDownloadLink() {
		DownloadLink downloadLink = new DownloadLink("export",
				new AbstractReadOnlyModel<File>() {

					@Override
					public File getObject() {
						File tempFile = null;
						try {
							tempFile = File.createTempFile("taskdef", "export");
							// marshal to xml
							
							final Marshaller marshaller = Stuff.getContext().createMarshaller();
							Writer w = new OutputStreamWriter(new FileOutputStream(tempFile),Charset.forName("UTF8"));
							final ComplexTaskDef ctd = tree.getCurrentTaskdef().getObject();
							addRevisionTo(ctd);
							Stuff.makeIDsUnique(ctd);
							marshaller.marshal(ctd, w);
							w.close();
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
						Revisions revisions = ctd.getRevisions();
						if(revisions==null){
						  revisions=new ObjectFactory().createComplexTaskDefRevisions();
						  ctd.setRevisions(revisions);
						}
						final List<Revision> revList = ctd.getRevisions().getRevision();
						rev.setSerialNumber(revList.size() + 1);
						revList.add(rev);
					}
				}, "pruefung.xml");

		downloadLink.setDeleteAfterDownload(true);
		downloadLink.setEnabled(false);

		return downloadLink;
	}


	/* (non-Javadoc)
	 * @see org.apache.wicket.Component#onEvent(org.apache.wicket.event.IEvent)
	 */
	@Override
	public void onEvent(IEvent<?> event) {
	  super.onEvent(event);
	  IEventSource source = event.getSource();
		if (source instanceof ComplexTaskDefTree) {
			Object selected = ((ComplexTaskDefTree) source).getSelected().getObject();
			boolean enabled = !(selected instanceof BasicUser);

			if (selected instanceof ComplexTaskDef) {
				this.previewLink.setEnabled(true);
				this.downloadLink.setEnabled(true);
			} else {
				this.previewLink.setEnabled(false);
				this.downloadLink.setEnabled(false);
			}
			if (selected instanceof SubTaskDef) {
				this.addLink.setEnabled(false);
			} else {
				this.addLink.setEnabled(true);
			}
			this.deleteLink.setEnabled(enabled);
			// no admin user should be able to delete himself....
			if (selected instanceof BasicUser) {
				this.deleteLink.setEnabled(
					false == ((BasicUser) selected).getUsername()
							.equals( TaskEditorSession.get().getUser().getUsername()) );
			}
			((AjaxRequestTarget)event.getPayload()).add(this);
		}
	}

}