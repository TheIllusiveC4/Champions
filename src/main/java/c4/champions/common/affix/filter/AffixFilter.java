package c4.champions.common.affix.filter;

public class AffixFilter {

    private final String identifier;
    private final boolean enabled;
    private final String[] entityBlacklist;
    private final String[] alwaysOnEntity;
    private final int tier;

    public AffixFilter(String identifier, boolean enabled, String[] entityBlacklist, String[] alwaysOnEntity, int
            tier) {
        this.identifier = identifier;
        this.enabled = enabled;
        this.entityBlacklist = entityBlacklist;
        this.alwaysOnEntity = alwaysOnEntity;
        this.tier = tier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String[] getEntityBlacklist() {
        return entityBlacklist;
    }

    public String[] getAlwaysOnEntity() {
        return alwaysOnEntity;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getTier() {
        return tier;
    }
}
