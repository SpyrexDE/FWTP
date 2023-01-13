package networking;

public class FWTP {
    public ActionType type;
    public Object body;

    public FWTP(ActionType type, Object obj) {
        this.type = type;
        this.body = obj;
    }

    public String toString() {
        if(body instanceof String[]) {
            return type.toString() + "|" + String.join("|", (String[]) body);
        } else {
            return type.toString() + "|" + body.toString();
        }
    }
}
