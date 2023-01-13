package networking;

public class FWTP {
    public ActionType type;
    public Object obj;

    public FWTP(ActionType type, Object obj) {
        this.type = type;
        this.obj = obj;
    }

    public String toString() {
        if(obj instanceof String[]) {
            return type.toString() + "|" + String.join("|", (String[]) obj);
        } else {
            return type.toString() + "|" + obj.toString();
        }
    }
}
