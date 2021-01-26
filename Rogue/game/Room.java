package game;

import java.util.ArrayList;
import java.util.List;

public class Room extends Structure{
    private int id;
    private List<Creature> creatures = null;

    public Room(String _s){
        id = Integer.parseInt(_s);
    }
    
    public void setId(int room){
        id = room;
    }

    public void addCreature(Creature creature){
        // add creature
        if(creatures == null){
            creatures = new ArrayList<Creature>();
        }
        creatures.add(creature);
    }
}