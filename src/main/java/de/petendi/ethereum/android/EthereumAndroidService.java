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

import android.app.IntentService;
import android.content.Intent;

public class EthereumAndroidService extends IntentService {

    private static final String ACTION_RESPONSE = "de.petendi.ethereum.android.action.RESPONSE";
    static ResponseHandler responseHandler = null;

    public EthereumAndroidService() {
        super("EthereumAndroidService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_RESPONSE.equals(action)) {

                if (responseHandler != null) {
                    responseHandler.onHandleResponseIntent(intent);
                }
            }
        }
    }

    interface ResponseHandler {
        void onHandleResponseIntent(Intent intent);
    }

}
