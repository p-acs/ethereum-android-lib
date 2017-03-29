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


import java.math.BigInteger;

public final class Utils {

    private static final char[] HEXCHARS = "0123456789ABCDEF".toCharArray();

    private static String splitHexString(String hexString) {
        return hexString.replace("0x", "");
    }

    public static BigInteger fromHexString(String hexString) {
        return new BigInteger(splitHexString(hexString), 16);
    }

    public static String toHexString(BigInteger value) {
        return value.toString(16);
    }

    public static final String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEXCHARS[v >>> 4];
            hexChars[j * 2 + 1] = HEXCHARS[v & 0x0F];
        }
        return new String(hexChars);
    }

    private Utils() {
        //hide
    }
}
