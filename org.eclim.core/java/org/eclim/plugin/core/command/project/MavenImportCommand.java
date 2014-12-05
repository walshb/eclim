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

import java.io.File;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collections;

import org.eclim.Services;

import org.eclim.annotation.Command;

import org.eclim.command.CommandLine;
import org.eclim.command.Options;

import org.eclim.plugin.core.command.AbstractCommand;

import org.eclim.plugin.core.util.ProjectUtils;

import org.eclipse.core.resources.IProject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardRegistry;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.IImportWizard;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * Command to import a project from a folder.
 *
 * @author Eric Van Dewoestine
 */
@Command(name = "maven_import", options = "REQUIRED f pom ARG")
public class MavenImportCommand
  extends AbstractCommand
{
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
    IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

    wizard.init(PlatformUI.getWorkbench(), new StructuredSelection(new Object[]{file}));

    // hacky, but I want to re-use the eclipse logic as much as possible.
    //MavenImportWizard wizard = new MavenImportWizard(new ProjectImportConfiguration(), Collections.singletonList(dotproject.getAbsolutePath()));

    wizard.performFinish();

    return Services.getMessage("project.imported", "something");
  }
}
