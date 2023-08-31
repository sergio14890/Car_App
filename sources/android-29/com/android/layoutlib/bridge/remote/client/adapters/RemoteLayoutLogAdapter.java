/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.layoutlib.bridge.remote.client.adapters;

import com.android.ide.common.rendering.api.LayoutLog;
import com.android.layout.remote.api.RemoteLayoutLog;
import com.android.tools.layoutlib.annotations.NotNull;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteLayoutLogAdapter implements RemoteLayoutLog {
    private final LayoutLog mLog;

    private RemoteLayoutLogAdapter(@NotNull LayoutLog log) {
        mLog = log;
    }

    public static RemoteLayoutLog create(@NotNull LayoutLog log) throws RemoteException {
        return (RemoteLayoutLog) UnicastRemoteObject.exportObject(new RemoteLayoutLogAdapter(log),
                0);
    }

    @Override
    public void warning(String tag, String message, Serializable data) {
        mLog.warning(tag, message, null);
    }

    @Override
    public void fidelityWarning(String tag, String message, Throwable throwable, Object viewCookie,
            Object data) {
        mLog.fidelityWarning(tag, message, throwable, viewCookie, data);
    }

    @Override
    public void error(String tag, String message, Serializable data) {
        mLog.error(tag, message, null);
    }

    @Override
    public void error(String tag, String message, Throwable throwable, Serializable data) {
        mLog.error(tag, message, throwable, null);
    }
}
