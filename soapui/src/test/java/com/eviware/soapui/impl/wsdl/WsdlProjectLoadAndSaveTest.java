package com.eviware.soapui.impl.wsdl;

import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.model.project.SaveStatus;
import com.eviware.soapui.support.SoapUIException;
import com.eviware.soapui.utils.StubbedDialogsTestBase;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.apache.xmlbeans.XmlException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class WsdlProjectLoadAndSaveTest extends StubbedDialogsTestBase {

    private static final String SAMPLE_PROJECT_PATH = "/sample-soapui-project.xml";
    private final InputStream sampleProjectInputSteam = getClass().getResourceAsStream("/sample-soapui-project.xml");

    private static final File TEMPORARY_FOLDER = Files.createTempDir();

    private static final String PROJECT_NAME = "Project";

    @Before
    public void setup() throws IOException {
        resetSampleProjectToWritable();
        resetStubbedDialogs();
    }

    @After
    public void cleanUpTemporaryFiles() throws IOException {
        FileUtils.deleteDirectory(TEMPORARY_FOLDER);
    }

    /*@Test
    public void userIsPromptedForSaveLocationWhenSavingProjectLoadedFromInputStream() throws IOException {
        Project project = new WsdlProject(sampleProjectInputSteam, null);

        // The file exists do you want to overwrite? - Yes
        stubbedDialogs.mockConfirmWithReturnValue(true);
        project.save();
        verify(mockedFileDialogs).saveAs(anyObject(), anyString(), anyString(), anyString(), isA(File.class));
    }*/

    @Test
    public void projectLoadedFromInputStreamCanBeSaved() throws IOException {
        Project project = new WsdlProject(sampleProjectInputSteam, null);

        // The file exists do you want to overwrite? - Yes
        stubbedDialogs.mockConfirmWithReturnValue(true);
        SaveStatus status = project.save();
        assertThat(status, is(SaveStatus.SUCCESS));
    }

 /*   @Test
    public void projectLoadedFromFileCanBeSaved() throws IOException {
        String projectFilePath = getClass().getResource(SAMPLE_PROJECT_PATH).getPath();
        Project project = new WsdlProject(projectFilePath, (WorkspaceImpl) null);

        SaveStatus status = project.save();
        assertThat(status, is(SaveStatus.SUCCESS));
    }
*/
    @Test
    public void newlyCreatedProjectCanBeSaved() throws XmlException, IOException, SoapUIException {
        WsdlProject project = createTemporaryProject();

        SaveStatus status = project.saveIn(createTemporaryProjectFile());
        assertThat(status, is(SaveStatus.SUCCESS));
    }

 /*   @Test
    public void newlyCreatedProjectIsNotSavedIfUserOptsNotToSave() throws XmlException, IOException, SoapUIException {
        Project project = createTemporaryProject();

        // Do you want to save? - No
        stubbedDialogs.mockConfirmWithReturnValue(false);

        when(mockedFileDialogs.saveAs(anyObject(), anyString(), anyString(), anyString(), Matchers.isA(File.class))).thenReturn(null);
        SaveStatus status = project.save();
        assertThat(status, is(not(SaveStatus.SUCCESS)));
    }

    @Test
    public void newlyCreatedProjectIsNotSavedIfSaveAsProjectFileIsNotWritable() throws IOException {
        String projectFilePath = getClass().getResource(SAMPLE_PROJECT_PATH).getPath();
        Project project = new WsdlProject(projectFilePath, (WorkspaceImpl) null);
        boolean couldSetWritable = new File(projectFilePath).setWritable(false);
        if (!couldSetWritable) {
            throw new IOException("Can't set sample project file to writable");
        } else {
            // Could not save file. Do you want to write to a new file? - No
            stubbedDialogs.mockConfirmWithReturnValue(false);
            SaveStatus status = project.save();
            assertThat(status, is(SaveStatus.DONT_SAVE));
        }
    }*/


    private void resetSampleProjectToWritable() throws IOException {
        String projectFilePath = getClass().getResource(SAMPLE_PROJECT_PATH).getPath();
        boolean couldSetWritable = new File(projectFilePath).setWritable(true);
        if (!couldSetWritable) {
            throw new IOException("Can't set sample project file to writable");
        }
    }

    private void resetStubbedDialogs() {
        stubbedDialogs.mockConfirmWithReturnValue(true);
    }


    private File createTemporaryProjectFile() {
        return new File(TEMPORARY_FOLDER + File.separator + UUID.randomUUID() + "-soapui-project.xml");
    }

    private WsdlProject createTemporaryProject() throws XmlException, IOException, SoapUIException {
        WsdlProject project = new WsdlProject();
        project.setName(PROJECT_NAME);
        return project;
    }
}