/*
 * Copyright 2004-2014 SmartBear Software
 *
 * Licensed under the EUPL, Version 1.1 or - as soon as they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the Licence for the specific language governing permissions and limitations
 * under the Licence.
*/
package com.eviware.soapui.utils;

import com.eviware.soapui.support.UISupport;
import com.eviware.x.dialogs.XDialogs;
import com.eviware.x.dialogs.XFileDialogs;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public class StubbedDialogsTestBase {
    private File savedFile;

    protected StubbedDialogs stubbedDialogs = new StubbedDialogs();

    @Mock
    protected XFileDialogs mockedFileDialogs;
    private XDialogs originalDialogs;

    private XFileDialogs originalFileDialogs;


    @Before
    public void setupStubbedDialogs() throws IOException {
        MockitoAnnotations.initMocks(this);

        addSaveAsBehaviour(mockedFileDialogs);
        setMockedDialogsTemporary();
    }

    @After
    public void teardownStubbedDialogs() {
        restoreOriginalDialogs();
    }


    private void addSaveAsBehaviour(XFileDialogs mockedFileDialogs) throws IOException {
        savedFile = File.createTempFile("saved-project-file", ".xml");
        when(mockedFileDialogs.saveAs(anyObject(), anyString(), anyString(), anyString(), isA(File.class))).thenReturn(savedFile);
    }

    private void setMockedDialogsTemporary() {
        originalDialogs = UISupport.getDialogs();
        originalFileDialogs = UISupport.getFileDialogs();
        UISupport.setDialogs(stubbedDialogs);
        UISupport.setFileDialogs(mockedFileDialogs);
    }

    private void restoreOriginalDialogs() {
        UISupport.setDialogs(originalDialogs);
        UISupport.setFileDialogs(originalFileDialogs);
    }
}
