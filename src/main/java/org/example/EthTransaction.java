package org.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EthTransaction {
    public String hash;
    public String from;
    public String to;
    public String value;
    public String timeStamp;
    public String isError;

    public String type; // ← ДОБАВИЛИ
}
