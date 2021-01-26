package game;

public class Action{
    private String message;
    private int value;
    private char c;
    private Displayable owner;
    private String type;
    private String name;

    public Action(Displayable _owner)
    {
        owner = _owner;
        //System.out.println("creating Action object\n");
    }

    public void SetIntVaule(int v){
        value = v;
    }

    public void SetMessage(String msg){
        message = msg;
    }

    public void SetCharValue(char _c){
        c = _c;
    }

    public void SetType(String _s){
        type = _s;
    }

    public void SetName(String _s){
        name = _s;
    }
    //Begin here
    public String getName(){
        return name;
    }
    public char getCharValue(){
        return c;
    }

    public String getMessage(){
        return message;
    }

    public int getIntValue(){
        return value;
    }
    

}