package eu.nets.factory.gateway.model;

public class HeaderRuleModel {
    public Long id;
    public String name;
    public String prefixMatch;

    public HeaderRuleModel(String name, String prefixMatch) {
        this.name = name;
        this.prefixMatch = prefixMatch;
    }
    public HeaderRuleModel() {

    }
    public HeaderRuleModel(HeaderRule headerRule) {
        this.name=headerRule.getName();
        this.prefixMatch=headerRule.getPrefixMatch();
        this.id=headerRule.getId();
    }
    public static HeaderRuleModel summary(HeaderRule headerRule) {
        return new HeaderRuleModel(headerRule);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefixMatch() {
        return prefixMatch;
    }

    public void setPrefixMatch(String prefixMatch) {
        this.prefixMatch = prefixMatch;
    }
}
