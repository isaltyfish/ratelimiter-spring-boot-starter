package net.verytools.tools;

public class RateLimitRule {

    private int replenishRate;

    private int burstCapacity;

    private int requestedTokens;

    public int getReplenishRate() {
        return replenishRate;
    }

    public void setReplenishRate(int replenishRate) {
        this.replenishRate = replenishRate;
    }

    public int getBurstCapacity() {
        return burstCapacity;
    }

    public void setBurstCapacity(int burstCapacity) {
        this.burstCapacity = burstCapacity;
    }

    public int getRequestedTokens() {
        return requestedTokens;
    }

    public void setRequestedTokens(int requestedTokens) {
        this.requestedTokens = requestedTokens;
    }
}
