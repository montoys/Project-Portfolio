package game;

import java.util.ArrayList;
import java.util.List;

public class Passage extends Structure {
    private String name;
    private int Id;
    private List<Integer> x_cord = null;
    private List<Integer> y_cord = null;

    public Passage(){
    }

    public void add_x(int x){
        if(x_cord == null){
            x_cord = new ArrayList<Integer>();
        }
        x_cord.add(x);  
    }

    public void add_y(int y){
        if(y_cord == null){
            y_cord = new ArrayList<Integer>();
        }
        y_cord.add(y);  
    }
    public List<Integer> getXcord(){
        return x_cord;
    }
    public List<Integer> getYcord(){
        return y_cord;
    }

}