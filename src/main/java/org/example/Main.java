package org.example;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.*;
import java.util.*;


import com.fasterxml.jackson.databind.*;

public class Main {



    public static void main(String[] args) throws Exception {
        Database.init(); // Create database





        String apiKey = "ETHERSCAN_API_KEY";
        String address = "ETHEREUM_WALLET_ADDRESS";

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        // ===== 1️⃣ ETH TX =====
        String ethUrl = "https://api.etherscan.io/v2/api"
                + "?chainid=1&module=account&action=txlist"
                + "&address=" + address
                + "&sort=desc"
                + "&apikey=" + apiKey;

        JsonNode ethRoot = call(client, mapper, ethUrl);

        // ===== 2️⃣ ERC20 TX =====
        String erc20Url = "https://api.etherscan.io/v2/api"
                + "?chainid=1&module=account&action=tokentx"
                + "&address=" + address
                + "&apikey=" + apiKey;

        JsonNode erc20Root = call(client, mapper, erc20Url);

        // ===== 3️⃣ NFT TX =====
        String nftUrl = "https://api.etherscan.io/v2/api"
                + "?chainid=1&module=account&action=tokennfttx"
                + "&address=" + address
                + "&apikey=" + apiKey;

        JsonNode nftRoot = call(client, mapper, nftUrl);

        // ===== ANALYSIS =====

        // NFT hashes
        Set<String> nftHashes = new HashSet<>();
        for (JsonNode n : nftRoot.get("result")) {
            nftHashes.add(n.get("hash").asText());
        }

        // ERC20 count per txHash
        Map<String, Integer> erc20Counter = new HashMap<>();
        for (JsonNode e : erc20Root.get("result")) {
            String hash = e.get("hash").asText();
            erc20Counter.put(hash, erc20Counter.getOrDefault(hash, 0) + 1);
        }

        // ===== OUTPUT =====
        JsonNode resultNode = ethRoot.get("result");

        for (JsonNode txNode : resultNode) {
            // Time interpreter
            String timeStamp = txNode.get("timeStamp").asText();
            long ts = Long.parseLong(timeStamp);
            String formattedTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault())
                    .format(Instant.ofEpochSecond(ts));



            // Value interpreter from wei to eth
            String rawValue = txNode.get("value").asText(); // из JSON
            BigDecimal valueEth = new BigDecimal(new BigInteger(rawValue))
                    .divide(BigDecimal.TEN.pow(18));
            String hash = txNode.get("hash").asText();
            String from = txNode.get("from").asText();
            String to = txNode.get("to").asText();
            String value = txNode.get("value").asText();
            String timestamp = txNode.get("timeStamp").asText();
            boolean exists = Database.existsByHash(hash);

            if (!exists) {
                Database.saveTransaction(hash, from, to, value, timestamp);

                String message =
                        " New transaction\n\n" +
                                "From: " + from + "\n" +
                                "To: " + to + "\n" +
                                "Value: " + valueEth + " ETH\n" +
                                "Time: " + formattedTime;

                TelegramService.sendMessage(message);
            }


            EthTransaction tx = mapper.treeToValue(txNode, EthTransaction.class);


            if (nftHashes.contains(tx.hash)) {
                tx.type = "NFT";
            } else if (erc20Counter.getOrDefault(tx.hash, 0) >= 2) {
                tx.type = "SWAP";
            } else {
                tx.type = "TRANSFER";
            }
            if(tx.type.equals("TRANSFER")){
            // === OUTPUT ===
            System.out.println("Type: " + tx.type);
            System.out.println("Hash: " + tx.hash);
            System.out.println("From: " + tx.from);
            System.out.println("To: " + tx.to);
            System.out.println("Value (ETH): " + valueEth);
            System.out.println("Timestamp: " + formattedTime);
            System.out.println("IsError: " + tx.isError);
            System.out.println("------");
            }
            else{}
        }
    }

    static JsonNode call(HttpClient client, ObjectMapper mapper, String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        return mapper.readTree(res.body());
    }
}
