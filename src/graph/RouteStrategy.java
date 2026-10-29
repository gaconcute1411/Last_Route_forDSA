package graph;

public enum RouteStrategy {
    SAFE,   // chỉ đường an toàn
    DIRECT, // đi SAFE + DANGEROUS
    EMERGENCY // đi mọi đường trừ BLOCKED
}
