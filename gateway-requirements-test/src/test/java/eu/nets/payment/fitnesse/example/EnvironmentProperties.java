package eu.nets.payment.fitnesse.example;

public enum EnvironmentProperties {
    DatasourceUrl("datasource.url", System.getProperty("datasource.url", "jdbc:oracle:thin:@vm-udb-7:1521:u7utv")),
    DatasourceUsername("datasource.username", System.getProperty("datasource.username", System.getProperty("user.name", "tard").toLowerCase())),
    DatasourcePassword("datasource.password", System.getProperty("datasource.password", System.getProperty("user.name", "tard").toLowerCase())),;

    private String key;
    private String value;

    private EnvironmentProperties(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static void load() {

        System.out.println("Environment properties:");
        for (EnvironmentProperties prop : values()) {
            System.setProperty(prop.key, prop.value);

            if (prop.key.contains("password")) {
                System.out.println("  " + prop.key + ": [*****]");
            } else {
                System.out.println("  " + prop.key + ": [" + prop.value + "]");
            }
        }
    }

    public String getValue() {
        return value;
    }
}

