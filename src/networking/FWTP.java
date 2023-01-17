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
            return type.ordinal() + "|" + String.join("|", (String[]) body);
        } else {
            System.out.println("FWTP: " + type.ordinal() + "|" + body.toString() + "");
            return type.ordinal() + "|" + body.toString();
        }
    }
}
