package game;
public class Char extends Displayable {

    public static final String CLASSID = "Char";
    private String message = "";

    public Char() {
        super();
    }
    
    public void setMessage(String _message){
        message = _message;
    }
}
