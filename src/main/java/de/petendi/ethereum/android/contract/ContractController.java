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
package de.petendi.ethereum.android.contract;

import java.math.BigInteger;

public class ContractController {
    public byte[] encode(String abi,String functionName, Object... args) {
        throw new IllegalStateException("not implemented yet");
    }

    public byte[] generateContractInteraction(String address, BigInteger value, byte[] encoded) {
        throw new IllegalStateException("not implemented yet");
    }

    public Object[] decodeResult(String abi, String functionName, String arg) {
        throw new IllegalStateException("not implemented yet");
    }

    public String call(String address, BigInteger wei, byte[] encoded) {
       throw new IllegalStateException("not implemented yet");
    }
}
