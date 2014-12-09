/**
 * Copyright (C) 2005 - 2012  Eric Van Dewoestine
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.eclim.plugin.core.command.project;

import org.eclim.Services;
import org.eclim.annotation.Command;
import org.eclim.command.CommandLine;
import org.eclim.command.Options;
import org.eclim.eclipse.EclimPlugin;
import org.eclim.logging.Logger;
import org.eclim.plugin.core.command.AbstractCommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.m2e.core.ui.internal.wizards.MavenImportWizardPage;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.IWizardRegistry;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.project.AbstractProjectScanner;
import org.eclipse.m2e.core.project.LocalProjectScanner;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.ui.internal.wizards.ImportMavenProjectsJob;
*/

/**
 * Command to import a project from a folder.
 *
 * @author Eric Van Dewoestine
 */
@Command(name = "maven_import", options = "REQUIRED f pom ARG")
public class MavenImportCommand
  extends AbstractCommand
{
  private static final Logger logger = Logger.getLogger(MavenImportCommand.class);

  /*
  private static class PomFile extends org.eclipse.core.internal.resources.File {
      public PomFile(IPath path, Workspace workspace) {
      super(path, workspace);
    }

    public IPath getLocation() {
        return getLocalManager().locationFor(this);
    }
  }
  */

  /**
   * {@inheritDoc}
   */
  public Object execute(CommandLine commandLine)
    throws Exception
  {
    String folder = commandLine.getValue(Options.FOLDER_OPTION);
    if(folder.endsWith("/") || folder.endsWith("\\")){
      folder = folder.substring(0, folder.length() - 1);
    }

    if (!new File(folder).exists()){
      return Services.getMessage("project.directory.missing", folder);
    }

    File dotproject = new File(folder);
    if (!dotproject.exists()){
      return Services.getMessage("project.dotproject.missing", folder);
    }

    IWizardRegistry wizardRegistry = PlatformUI.getWorkbench().getImportWizardRegistry();

    final String wizardId = "org.eclipse.m2e.core.wizards.Maven2ImportWizard";

    IWizardDescriptor wizardDescriptor = wizardRegistry.findWizard(wizardId);
    IImportWizard wizard = (IImportWizard)wizardDescriptor.createWizard();

    IPath path = Path.fromOSString(folder);
    //IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
    //IFile file = new PomFile(path, ResourcesPlugin.getWorkspace());
    //IStructuredSelection selection = new StructuredSelection(new Object[]{file});

    //wizard.init(PlatformUI.getWorkbench(), selection);
    List<String> locations = new ArrayList<String>();
    locations.add(folder);

    Field field = wizard.getClass().getDeclaredField("locations");
    field.setAccessible(true);
    field.set(wizard, locations);

    Shell shell = EclimPlugin.getShell();
    WizardDialog wizardDialog = new WizardDialog(shell, wizard);
    wizardDialog.create();

    //wizard.addPages();

    //Thread.sleep(5000);

    MavenImportWizardPage page = (MavenImportWizardPage)wizard.getPages()[0];

    /*
    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
    File root = workspaceRoot.getLocation().toFile();
    List<String> locations = new ArrayList<String>();
    locations.add(folder);
    boolean basedirRenameRequired = false;
    MavenModelManager modelManager = MavenPlugin.getMavenModelManager();
    AbstractProjectScanner<MavenProjectInfo> projectScanner = new LocalProjectScanner(root, locations, basedirRenameRequired, modelManager);
    IProgressMonitor monitor = new NullProgressMonitor();
    projectScanner.run(monitor);
    List<MavenProjectInfo> projects = projectScanner.getProjects();

    ProjectImportConfiguration importConfiguration = new ProjectImportConfiguration();
    List<IWorkingSet> workingSets = new ArrayList<IWorkingSet>();
    ImportMavenProjectsJob job = new ImportMavenProjectsJob(projects, workingSets, importConfiguration);
    job.setRule(MavenPlugin.getProjectConfigurationManager().getRule());
    job.schedule();
    */

    /*
    page.setShowLocation(false);
    page.createControl(wizard.getContainer());
    page.scanProjects();
    page.setPageComplete(true);
    */

    wizard.performFinish();

    return Services.getMessage("project.imported", "something");
  }
}
