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

import android.content.Context;
import android.content.Intent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import de.petendi.ethereum.android.service.model.AccountRequest;
import de.petendi.ethereum.android.service.model.Request;
import de.petendi.ethereum.android.service.model.Response;
import de.petendi.ethereum.android.service.model.ServiceError;

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

    public EthereumAndroid(Context context, EthereumAndroidCallback callback) {
        this.context = context;
        this.callback = callback;
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        EthereumAndroidService.responseHandler = new CallbackHandler();
        packageName = context.getApplicationInfo().packageName;
    }

    public int getAccount(String accountAddress) {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAddress(accountAddress);
        return send(accountRequest);
    }

    public int send(Request request) {
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

    private void handleResponse(Intent reponse) {
        int id = reponse.getIntExtra(ID, 0);
        String error = reponse.getStringExtra(EXTRA_ERROR);
        String response = reponse.getStringExtra(EXTRA_DATA);
        if (error != null) {
            try {
                ServiceError errorObj = objectMapper.readValue(error, ServiceError.class);
                callback.handleError(id, errorObj);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        } else if (response != null) {
            try {
                Response responseObj = objectMapper.readValue(response, Response.class);
                callback.handleResponse(id, responseObj);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }


    }
}
