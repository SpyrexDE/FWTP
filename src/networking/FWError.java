package networking;

public class FWError {
    public String message;
    public FWErrorType type;

    public FWError(String message, FWErrorType type) {
        this.message = message;
        this.type = type;
    }
}
