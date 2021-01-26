package game;
public class Structure extends Displayable{
    private int id;
    public Structure(){
        super();
    }

    public void setId(int room1, int room2){
        String s1 = Integer.toString(room1); 
        String s2 = Integer.toString(room2); 
        // Concatenate both strings 
        String s = s1 + s2; 
        id = Integer.parseInt(s);
    }
}