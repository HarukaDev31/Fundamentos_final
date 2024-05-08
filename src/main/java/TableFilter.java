public class TableFilter {
    private String key;
    private String value;
    TableFilter(String key, String value) {
        this.key = key;
        this.value = value;
    }
    public String getKey() {
        return key;
    }
    public String getValue() {
        return value;
    }
    public String toString() {
        System.out.println("Key: " + key + " Value: " + value);
        return null;
    }

}
