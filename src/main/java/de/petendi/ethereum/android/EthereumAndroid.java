/**
 * Copyright 2016  Jan Petendi <jan.petendi@p-acs.com>
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
package de.petendi.ethereum.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import de.petendi.ethereum.android.service.IEthereumService;
import de.petendi.ethereum.android.service.model.ServiceError;
import de.petendi.ethereum.android.service.model.WrappedRequest;
import de.petendi.ethereum.android.service.model.WrappedResponse;

public class EthereumAndroid {

    private class CallbackHandler implements EthereumAndroidService.ResponseHandler {

        @Override
        public void onHandleResponseIntent(Intent intent) {
            handleResponse(intent);
        }
    }

    private final static String ID = "id";
    private final static String EXTRA_DATA = "data";
    private final static String EXTRA_ERROR = "error";
    private final static String EXTRA_PACKAGE = "package";


    private final AtomicInteger messageId = new AtomicInteger(0);
    private final Context context;
    private final EthereumAndroidCallback callback;
    private final ObjectMapper objectMapper;
    private final String packageName;

    private IEthereumService binder;
    private ServiceConnection serviceConnection;


    public EthereumAndroid(Context context, EthereumAndroidCallback callback) {
        this.context = context;
        this.callback = callback;
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        EthereumAndroidService.responseHandler = new CallbackHandler();
        packageName = context.getApplicationInfo().packageName;
        Intent intent = new Intent("de.petendi.ethereum.android.action.BIND_API");
        intent.setPackage(EthereumAndroidFactory.PACKAGENAME);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                binder = IEthereumService.Stub.asInterface(iBinder);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                binder = null;
            }
        };
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public int sendAsync(WrappedRequest request) {
        try {
            Intent intent = new Intent(EthereumAndroidFactory.SERVICE_ACTION);
            intent.setPackage(EthereumAndroidFactory.PACKAGENAME);
            int id = messageId.incrementAndGet();
            intent.putExtra(ID, id);
            intent.putExtra(EXTRA_DATA, objectMapper.writeValueAsString(request));
            intent.putExtra(EXTRA_PACKAGE, packageName);
            context.startService(intent);
            return id;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public WrappedResponse send(WrappedRequest request) {
        if (binder == null) {
            throw new IllegalStateException("not (yet) bound to service");
        }
        try {
            String response = binder.dispatch(objectMapper.writeValueAsString(request));
            return objectMapper.readValue(response, WrappedResponse.class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void handleResponse(Intent reponse) {
        int id = reponse.getIntExtra(ID, 0);
        String error = reponse.getStringExtra(EXTRA_ERROR);
        byte[] response = reponse.getByteArrayExtra(EXTRA_DATA);
        if (error != null) {
            try {
                ServiceError errorObj = objectMapper.readValue(error, ServiceError.class);
                callback.handleError(id, errorObj);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        } else if (response != null) {
            try {
                WrappedResponse responseObj = objectMapper.readValue(response, WrappedResponse.class);
                callback.handleResponse(id, responseObj);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public void release() {
        context.unbindService(serviceConnection);
        binder = null;
    }
}
