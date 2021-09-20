package com.clane.app.wallet.transaction;

public enum TxnActivity {
    WALLET_TRANSFER("Wallet Transfer"), TOP_UP("Top Up");

    private final String description;

    TxnActivity(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name();
    }
}
