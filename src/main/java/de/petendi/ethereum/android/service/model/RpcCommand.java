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
package de.petendi.ethereum.android.service.model;

public enum RpcCommand {
    eth_blockNumber,
    eth_call,
    eth_compileSolidity,
    eth_gasPrice,
    eth_getBalance,
    eth_getBlockByHash,
    eth_getBlockByNumber,
    eth_getBlockTransactionCountByHash,
    eth_getBlockTransactionCountByNumber,
    eth_getCode,
    eth_getCompilers,
    eth_getStorageAt,
    eth_getFilterChanges,
    eth_getFilterLogs,
    eth_getLogs,
    eth_getTransactionByBlockHashAndIndex,
    eth_getTransactionByBlockNumberAndIndex,
    eth_getTransactionByHash,
    eth_getTransactionCount,
    eth_getTransactionReceipt,
    eth_getUncleByBlockHashAndIndex,
    eth_getUncleByBlockNumberAndIndex,
    eth_getUncleCountByBlockHash,
    eth_getUncleCountByBlockNumber,
    eth_getWork,
    eth_estimateGas,
    eth_mining,
    eth_newBlockFilter,
    eth_newPendingTransactionFilter,
    eth_newFilter,
    eth_protocolVersion,
    eth_sendRawTransaction,
    eth_syncing,
    eth_uninstallFilter,
    net_version,
    trace_block,
    trace_filter,
    trace_get,
    trace_transaction,
    web3_clientVersion
}
