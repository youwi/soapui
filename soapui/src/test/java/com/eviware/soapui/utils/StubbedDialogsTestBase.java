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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: manne
 * Date: 2/17/14
 * Time: 10:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class StubbedDialogsTestBase {
    protected File savedFile;

    protected StubbedDialogs stubbedDialogs;
    private XDialogs originalDialogs;

    @Mock
    protected XFileDialogs mockedFileDialogs;
    private XFileDialogs originalFileDialogs;


    @Before
    public void resetDialogs() throws IOException {
        MockitoAnnotations.initMocks(this);

        savedFile = File.createTempFile("saved-project-file", "xml");

        originalDialogs = UISupport.getDialogs();
        originalFileDialogs = UISupport.getFileDialogs();

        stubbedDialogs = new StubbedDialogs();

        when(mockedFileDialogs.saveAs(anyObject(), anyString(), anyString(), anyString(), isA(File.class))).thenReturn(savedFile);

        UISupport.setDialogs(stubbedDialogs);
        UISupport.setFileDialogs(mockedFileDialogs);
    }

    @After
    public void restoreDialogs() {
        UISupport.setDialogs(originalDialogs);
        UISupport.setFileDialogs(originalFileDialogs);
    }
}
