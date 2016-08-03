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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigInteger;
import java.util.Arrays;


public class ContractFactory {

    private final ContractController contractController;

    public ContractFactory(ContractController contractController) {
        this.contractController = contractController;
    }

    public <T> T create(String contractAddress, String abi, Class<T> clazz) {
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, new MyInvocationHandler(abi, contractAddress));
    }


    private class MyInvocationHandler implements InvocationHandler {
        private final String abi;
        private final String address;

        private MyInvocationHandler(String abi, String address) {
            this.abi = abi;
            this.address = address;
        }


        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            BigInteger value = null;
            if (args != null && args.length > 0) {
                Annotation[][] annotations = method.getParameterAnnotations();
                int position = args.length - 1;
                Annotation[] annotationsList = annotations[position];
                for (Annotation annotation : annotationsList) {
                    if (annotation instanceof de.petendi.ethereum.android.contract.Value) {
                        Object valueArg = args[position];
                        if (valueArg instanceof BigInteger) {
                            BigInteger arg = (BigInteger) valueArg;
                            value = arg;
                            args = Arrays.copyOfRange(args, 0, position);
                        }
                    }
                }
            }

            if (value == null) {
                value = BigInteger.ZERO;
            }

            String functionName = method.getName();
            byte[] encoded = contractController.encode(abi, method.getName(), args);

            if (method.getReturnType().equals(PendingTransaction.class)) {

                byte[] rawTransaction = contractController.generateContractInteraction(address, value, encoded);
                return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{PendingTransaction.class}, new PendingTransactionInvocationHandler(abi, functionName, rawTransaction));

            } else {
                String response = contractController.call(address, BigInteger.ZERO, encoded);
                Object[] decoded = contractController.decodeResult(abi, functionName, response);
                return decoded[0];
            }
        }
    }

    private class PendingTransactionInvocationHandler implements InvocationHandler {

        private final String abi;
        private final String functionName;
        private final byte[] rawTransaction;


        public PendingTransactionInvocationHandler(String abi, String functionName, byte[] rawTransaction) {
            this.abi = abi;
            this.functionName = functionName;
            this.rawTransaction = rawTransaction;

        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().startsWith("getUnsignedTransaction")) {
                return rawTransaction;
            } else if (method.getName().startsWith("decodeResult")) {

                Object[] decoded = contractController.decodeResult(abi, functionName, (String) args[0]);
                if (method.getReturnType().isArray()) {
                    return decoded;
                } else {
                    return decoded[0];
                }
            } else if (method.getName().startsWith("toString")) {
                return "proxy";
            } else {
                throw new IllegalAccessException("wrong method called: " + method.getName());
            }
        }
    }
}
