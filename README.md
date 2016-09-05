# Ethereum Android Library

This library enables you to securely interact with every Ethereum based blockchain from within your Android application.

Ethereum Android makes sure that that the whole communication to a node is end to end encrypted.

Check https://github.com/p-acs/ethereum-secure-proxy for the steps to secure your own node.


## Initialization

    EthereumAndroidFactory ethereumAndroidFactory = new EthereumAndroidFactory(context);
    EthereumAndroid ethereumAndroid;
    try {
        ethereumAndroid = ethereumAndroidFactory.create();
    } catch (EthereumNotInstalledException e) {
        //let the user install Ethereum
        ethereumAndroidFactory.showInstallationDialog();
    }

This checks if Ethereum Android is installed and that the installed application is correctly signed.
If the check failed, you should give the user the possibility to install Ethereum Android.



## RPC API

Ethereum Android supports a subset of the Ethereum RPC API (see https://github.com/ethereum/wiki/wiki/JSON-RPC)
to let you interact with a connected Ethereum node.

### Create the request


    WrappedRequest request = new WrappedRequest();

Have a look at the Ethereum RPC API. To check the Balance of an Ethereum Address for example,
use https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_getbalance


    request.setCommand(RpcCommand.eth_getBalance.toString());

And set the appropriate parameters

    request.setParameters(new String[]{accountAddress, "latest"});

You can choose if you want to do the call synchronously or asynchronously.

### Synchronous call

This does a synchronous call, which you should execute from within a background thread.

    WrappedResponse response = ethereumAndroid.send(request);

Check if ```response.isSuccess()``` and use ```response.getErrorMessage()``` if not.

### Asynchronous call

Make sure first that you have set a callback

    ethereumAndroid.setCallback(callback);

Send the request and remember the messageId

    int messageId = ethereumAndroid.sendAsync(request);

Wait for the response to arrive

    callback.handleResponse(messageId, response);

If there was an error the callback object will receive an error instead

    callback.handleError(messageId, error);

### Handle the response

    Object responseObj = response.getResponse();

The type of the responseObj depends on the sent RPC command.
For ```RpcCommand.eth_getBalance``` the response is a hex encoded quantity, so the response type is a ```String```.

In case the response is a structured Object, the type is a ```Map```.

Hint: if you want to use your own data object you can use the Jackson Objectmapper

      MyResponseData myResponseData = new ObjectMapper().convertValue(response.getResponse(), MyResponseData.class);

## Contract API

Ethereum Android offers an API which lets you interact with a smart contract via a plain Java interface.
The complexity (RLP encoding, nonce handling) is hidden.

    Contracts contracts = ethereumAndroid.contracts();

### Reading

Assume a smart contract is deployed which offers a constant function called ```get``` returning a stored String value.

This would be the ABI definition

    [{"constant":true,"inputs":[],"name":"get","outputs":[{"name":"","type":"string"}],"type":"function"}]

And this would be the corresponding Java interface

    interface SimpleStorage {
        String get();
     }

With ABI and the interface you can read the value

    SimpleStorage simpleStorage = contracts().bind(contractAddress, CONTRACT_ABI, SimpleStorage.class);
    String storedValue = simpleStorage.get();

### Writing

Assume a smart contract is deployed which offers a function ```set``` taking one String as input.

This would be the ABI definition

    [{"constant":false,"inputs":[{"name":"d","type":"string"}],"name":"set","outputs":[],"type":"function"}]

And this would be the corresponding Java interface

    interface SimpleStorage {
        PendingTransaction<Void> set(String data);
    }

This is a write operation, so an object of type ```PendingTransaction``` is returned.
Because the function has no outputs, its return value is defined as ```Void```

    SimpleStorage simpleStorage = contracts().bind(contractAddress, CONTRACT_ABI, SimpleStorage.class);
    PendingTransaction<Void> pendingWrite = simpleStorage.set("a new value");

Every write operation needs to be signed by the user, because its costs the users Ether.

    ethereumAndroid.submitTransaction(parentActivity, requestCode, pendingWrite.getUnsignedTransaction());

Check for the result

    parentActivity.onActivityResult(int requestCode, int resultCode, Intent data);

In case ```resultCode == RESULT_OK``` the result Intent will contain the transaction hash

     String transaction = data.getStringExtra("transaction");

It the result was not OK the Intent will contain the error message instead

     String error = data.getStringExtra("error");

### Errorhandling

The Contract API bundles several RPC API calls at once and involves network connection, so every call can fail.

You should therefore surround every interaction in a try-catch block and check for ```ResponseNotOKException```.

If an exception occurs, do the following:

* check if the device has internet access
* check if your app is connected to Ethereum Android with ```ethereumAndroid.hasServiceConnection```

In case the service connection was lost, create a new instance of ```ethereumAndroid```

## Release instance

Once you are done using the API, you should close it

    ethereumAndroid.release();


## Samples

Check our sample application https://github.com/p-acs/ethereum-android-sample

## Questions/Feedback

Contact us via our Support Portal https://ethereum-android.com



























