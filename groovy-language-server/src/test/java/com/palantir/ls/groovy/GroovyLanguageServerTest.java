/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.palantir.ls.groovy;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import io.typefox.lsapi.InitializeParams;
import io.typefox.lsapi.InitializeResult;
import io.typefox.lsapi.TextDocumentSyncKind;
import io.typefox.lsapi.builders.InitializeParamsBuilder;
import io.typefox.lsapi.impl.ClientCapabilitiesImpl;
import io.typefox.lsapi.services.TextDocumentService;
import io.typefox.lsapi.services.WindowService;
import io.typefox.lsapi.services.WorkspaceService;
import java.util.concurrent.ExecutionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

public final class GroovyLanguageServerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testInitialize() throws InterruptedException, ExecutionException {
        GroovyLanguageServer server =
                new GroovyLanguageServer(new CompilerWrapperProvider() {
                    @Override
                    public void set(CompilerWrapper compilerWrapper) {}

                    @Override
                    public CompilerWrapper get() {
                        return Mockito.mock(CompilerWrapper.class);
                    }
                }, Mockito.mock(LanguageServerConfig.class), Mockito.mock(TextDocumentService.class),
                        Mockito.mock(WorkspaceService.class), Mockito.mock(WindowService.class));
        InitializeParams params =
                new InitializeParamsBuilder().capabilities(new ClientCapabilitiesImpl()).processId(1)
                        .rootPath(folder.getRoot().toPath().toAbsolutePath().toString()).build();
        InitializeResult result = server.initialize(params).get();
        assertThat(server.getWorkspaceRoot(), is(folder.getRoot().toPath().toAbsolutePath().normalize()));
        assertThat(result.getCapabilities().getTextDocumentSync(), is(TextDocumentSyncKind.Incremental));
        assertTrue(result.getCapabilities().isDocumentSymbolProvider());
        assertTrue(result.getCapabilities().isWorkspaceSymbolProvider());
    }

}