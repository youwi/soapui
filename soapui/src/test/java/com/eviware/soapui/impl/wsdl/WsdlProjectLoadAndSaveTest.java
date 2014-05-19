package com.eviware.soapui.impl.wsdl;

import com.eviware.soapui.impl.WorkspaceImpl;
import com.eviware.soapui.model.project.SaveStatus;
import com.eviware.soapui.support.SoapUIException;
import com.eviware.soapui.utils.StubbedDialogsTestBase;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.apache.xmlbeans.XmlException;
import org.junit.After;
import org.junit.Test;
import org.mockito.Matchers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WsdlProjectLoadAndSaveTest extends StubbedDialogsTestBase {

    public static final String SAMPLE_PROJECT_FILE_NAME = "/sample-soapui-project.xml";
    private final InputStream testFileInputStream = getClass().getResourceAsStream("/sample-soapui-project.xml");
    private final File tempDirectory = Files.createTempDir();

    @After
    public void cleanUpTemporaryFiles() throws IOException {
        FileUtils.deleteDirectory(tempDirectory);
    }

    @Test
    public void userIsPromptedForSaveLocationWhenSavingProjectLoadedFromInputStream() throws IOException {
        WsdlProject project = new WsdlProject(testFileInputStream, null);

        stubbedDialogs.mockConfirmWithReturnValue(true);
        project.save();
        verify(mockedFileDialogs).saveAs(anyObject(), anyString(), anyString(), anyString(), isA(File.class));
    }

    @Test
    public void projectLoadedFromInputStreamCanBeSaved() throws IOException {
        WsdlProject project = new WsdlProject(testFileInputStream, null);

        stubbedDialogs.mockConfirmWithReturnValue(true);
        SaveStatus status = project.save();
        assertThat(status, is(SaveStatus.SUCCESS));
    }

    @Test
    public void projectLoadedFromFileCanBeSaved() throws IOException {
        String projectFilePath = getClass().getResource(SAMPLE_PROJECT_FILE_NAME).getPath();
        WsdlProject project = new WsdlProject(projectFilePath, (WorkspaceImpl) null);

        SaveStatus status = project.save();
        assertThat(status, is(SaveStatus.SUCCESS));
    }

    @Test
    public void newlyCreatedProjectCanBeSaved() throws XmlException, IOException, SoapUIException {
        WsdlProject project = createTemporaryProject();

        SaveStatus status = project.saveIn(createTemporaryProjectFile());
        assertThat(status, is(SaveStatus.SUCCESS));
    }

    @Test
    public void newlyCreatedProjectIsNotSavedIfUserOptsNotToSave() throws XmlException, IOException, SoapUIException {
        WsdlProject project = createTemporaryProject();

        //Mocka att man svarar nej på att spara
        stubbedDialogs.mockConfirmWithReturnValue(false);

        when(mockedFileDialogs.saveAs(anyObject(), anyString(), anyString(), anyString(), Matchers.isA(File.class))).thenReturn(null);
        SaveStatus status = project.save();
        assertThat(status, is(not((SaveStatus.SUCCESS)));
    }

    @Test
    public void projectCannotBeSavedIfDirectoryIsSelected() {

    }

    private WsdlProject createTemporaryProject() throws XmlException, IOException, SoapUIException {
        WsdlProject project = new WsdlProject();
        project.setName("Temporary project");

        return project;
    }

    private File createTemporaryProjectFile() {
        File projectFile = new File(tempDirectory + File.separator + UUID.randomUUID() + "-soapui-project.xml");

        return projectFile;
    }
}