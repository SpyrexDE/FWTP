package networking;

public class FWError extends FWTP{
    public String message;
    public FWErrorType type;

    public FWError(FWErrorType type, String message) {
        super(ActionType.FEHLER, new String[] {String.valueOf(type.ordinal()), message});
        this.message = message;
        this.type = type;
    }

    public FWError(Object obj) {
        super(ActionType.FEHLER, obj);
        String[] o = (String[]) obj;
        int errorCode = Integer.parseInt((String) o[0]);

        this.message = o[1];
        this.type = FWErrorType.values()[errorCode];
    }
}
