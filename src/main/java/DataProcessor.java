public interface DataProcessor {
    void load() throws Exception;
    void export() throws Exception;
}