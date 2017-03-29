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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

import java.security.MessageDigest;
import java.util.List;

public class EthereumAndroidFactory {

    final static String PACKAGENAME = "de.petendi.ethereum.android";
    final static String FINGERPRINT = "2F91D2BEC39D37D4409855EFB9D856674D81A070";
    final static String SERVICE_ACTION = PACKAGENAME + ".action.REQUEST";
    static boolean DEV = false;

    private static final String TAG = EthereumAndroid.class.getSimpleName();


    private final Context context;

    public EthereumAndroidFactory(Context context) {
        this.context = context;
    }

    public boolean isInstalled() {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(SERVICE_ACTION);
        intent.setPackage(PACKAGENAME);
        List<ResolveInfo> services = pm.queryIntentServices(intent, 0);
        if (services == null) {
            return false;
        } else if (services.size() == 1) {
            if (DEV) {
                return true;
            } else {
                ResolveInfo resolveInfo = services.get(0);
                try {
                    PackageInfo packageInfo = pm.getPackageInfo(resolveInfo.serviceInfo.packageName, PackageManager.GET_SIGNATURES);
                    final byte[] rawCert = packageInfo.signatures[0].toByteArray();
                    if (sha1Hash(rawCert).equals(FINGERPRINT)) {
                        return true;
                    } else {
                        Log.w(TAG, "found package is wrongly signed: " + packageInfo);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    throw new IllegalStateException(e);
                }
            }
        } else if (services.size() > 0) {
            throw new IllegalStateException("more than one suitable service found");
        }
        return false;

    }

    public boolean showInstallationDialog() {
        Intent playIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=de.petendi.ethereum.android"));
        final PackageManager pm = context.getPackageManager();
        if (pm.resolveActivity(playIntent, 0) != null) {
            context.startActivity(playIntent);
            return true;
        } else {
            return false;
        }
    }

    public EthereumAndroid create() throws EthereumNotInstalledException{
        if(isInstalled()) {
            return new EthereumAndroid(context);
        } else {
            throw new EthereumNotInstalledException();
        }
    }

    public EthereumAndroid create(EthereumAndroidCallback callback) throws EthereumNotInstalledException{
        if(isInstalled()) {
            return new EthereumAndroid(context,callback);
        } else {
            throw new EthereumNotInstalledException();
        }
    }

    private final static String sha1Hash(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();
            return Utils.bytesToHex(bytes);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
