package game;

import java.util.ArrayList;
import java.util.List;

public class Creature extends Displayable {
    private int Hp;
    private int HpMoves;
    private String name;
    private int id;
    public boolean alive = true;
    private List<CreatureAction> death_actions;
    private List<CreatureAction> hit_actions;
    
    public Creature(){
    }

    public void setHpMoves(int hpm){
        HpMoves = hpm;
    }

    public List<CreatureAction> getHitActions(){
        return hit_actions;
    }

    public void addAction(String type, CreatureAction a){
        String s1 = "death";
        String s2 = "hit";
        if(type.equals(s1)){
            
            if(death_actions == null){
                death_actions = new ArrayList<CreatureAction>();
            }

            death_actions.add(a);

        }else if(type.equals(s2)){
            
            if(hit_actions == null){
                hit_actions = new ArrayList<CreatureAction>();
            }
            
        hit_actions.add(a);
        
        }
    }

    public void setName(String str){
        name = str;
    }
    
    public void setId(int room, int serial){
        String s1 = Integer.toString(room); 
        String s2 = Integer.toString(serial); 
        // Concatenate both strings 
        String s = s1 + s2; 
        id = Integer.parseInt(s);
    }
    // Step 5 mods begin here
    public List<CreatureAction> getDeathActions(){
        System.out.println(""+death_actions);
        return death_actions;
    }
}