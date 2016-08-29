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
import java.util.ArrayList;

import de.petendi.ethereum.android.EthereumAndroid;
import de.petendi.ethereum.android.contract.model.ContractCommand;
import de.petendi.ethereum.android.contract.model.ResponseNotOKException;
import de.petendi.ethereum.android.service.model.WrappedRequest;
import de.petendi.ethereum.android.service.model.WrappedResponse;

public class ContractController {

    private final EthereumAndroid ethereumAndroid;

    public ContractController(EthereumAndroid ethereumAndroid) {
        this.ethereumAndroid = ethereumAndroid;
    }

    public String encode(String abi, String functionName, Object... args) {
        WrappedRequest wrappedRequest = new WrappedRequest();
        wrappedRequest.setCommand(ContractCommand.contract_encode.toString());
        Object[] parameters;
        if (args == null) {
            parameters = new Object[2];
        } else {
            parameters = new Object[2 + args.length];
        }
        parameters[0] = abi;
        parameters[1] = functionName;
        if (args != null) {
            System.arraycopy(args, 0, parameters, 2, args.length);
        }
        wrappedRequest.setParameters(parameters);
        WrappedResponse response = ethereumAndroid.send(wrappedRequest);
        if (response.isSuccess()) {
            return (String) response.getResponse();
        } else {
            throw new ResponseNotOKException(response.getErrorMessage());
        }
    }

    public String generateContractInteraction(String address, BigInteger value, String encoded) {
        WrappedRequest wrappedRequest = new WrappedRequest();
        wrappedRequest.setCommand(ContractCommand.contract_interaction.toString());
        Object[] parameters = new Object[3];
        parameters[0] = address;
        parameters[1] = value;
        parameters[2] = encoded;
        wrappedRequest.setParameters(parameters);
        WrappedResponse response = ethereumAndroid.send(wrappedRequest);
        if (response.isSuccess()) {
            return (String) response.getResponse();
        } else {
            throw new ResponseNotOKException(response.getErrorMessage());
        }
    }

    public Object[] decodeResult(String abi, String functionName, String arg) {
        WrappedRequest wrappedRequest = new WrappedRequest();
        wrappedRequest.setCommand(ContractCommand.contract_decode.toString());
        Object[] parameters = new Object[3];
        parameters[0] = abi;
        parameters[1] = functionName;
        parameters[2] = arg;
        wrappedRequest.setParameters(parameters);
        WrappedResponse response = ethereumAndroid.send(wrappedRequest);
        if (response.isSuccess()) {
            return ((ArrayList) response.getResponse()).toArray();
        } else {
            throw new ResponseNotOKException(response.getErrorMessage());
        }
    }

    public String call(String address, BigInteger wei, String encoded) {
        WrappedRequest wrappedRequest = new WrappedRequest();
        wrappedRequest.setCommand(ContractCommand.contract_call.toString());
        Object[] parameters = new Object[3];
        parameters[0] = address;
        parameters[1] = wei;
        parameters[2] = encoded;
        wrappedRequest.setParameters(parameters);
        WrappedResponse response = ethereumAndroid.send(wrappedRequest);
        if (response.isSuccess()) {
            return (String) response.getResponse();
        } else {
            throw new ResponseNotOKException(response.getErrorMessage());
        }
    }


    public String generateContractCreation(String contractBytecode, String contractAbi, Object... constructorParams) {
        WrappedRequest wrappedRequest = new WrappedRequest();
        wrappedRequest.setCommand(ContractCommand.contract_creation.toString());
        Object[] parameters;
        if (constructorParams == null) {
            parameters = new Object[2];
        } else {
            parameters = new Object[2 + constructorParams.length];
        }
        parameters[0] = contractBytecode;
        parameters[1] = contractAbi;
        if (constructorParams != null) {
            System.arraycopy(constructorParams, 0, parameters, 2, constructorParams.length);
        }
        wrappedRequest.setParameters(parameters);
        WrappedResponse response = ethereumAndroid.send(wrappedRequest);
        if (response.isSuccess()) {
            return (String) response.getResponse();
        } else {
            throw new ResponseNotOKException(response.getErrorMessage());
        }
    }
}
