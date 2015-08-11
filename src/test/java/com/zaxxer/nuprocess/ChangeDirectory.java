/*
 * Copyright (C) 2013 Brett Wooldridge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zaxxer.nuprocess;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

/**
 * @author Ben Hamilton
 */
@RunWith(value=RunOnlyOnMac.class)
public class ChangeDirectory
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testNoChangeDirectory() throws InterruptedException, IOException
    {
        PwdHandler processListener = new PwdHandler();
        NuProcessBuilder pb = new NuProcessBuilder(processListener, "pwd");
        NuProcess nuProcess = pb.start();
        nuProcess.waitFor(0, TimeUnit.SECONDS);
        Path currentPwd = Paths.get(System.getProperty("user.dir"));
        Assert.assertEquals(currentPwd.toRealPath(), processListener.result.toRealPath());
    }

    @Test
    public void testChangeDirectory() throws InterruptedException, IOException
    {
        PwdHandler processListener = new PwdHandler();
        NuProcessBuilder pb = new NuProcessBuilder(processListener, "pwd");
        pb.setDirectory(folder.getRoot().toPath());
        NuProcess nuProcess = pb.start();
        nuProcess.waitFor(0, TimeUnit.SECONDS);
        Path currentPwd = folder.getRoot().toPath();
        Assert.assertEquals(currentPwd.toRealPath(), processListener.result.toRealPath());
    }

    private static class PwdHandler extends NuAbstractProcessHandler
    {
        private NuProcess nuProcess;
        Path result;

        @Override
        public void onStdout(ByteBuffer buffer)
        {
            if (buffer == null)
            {
                return;
            }

            byte[] chars = new byte[buffer.remaining()];
            buffer.get(chars);
            result = Paths.get(new String(chars).trim());
            System.out.println("Read: " + result);
        }
    }
}
