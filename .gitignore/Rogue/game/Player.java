package game;

import java.util.List;
import java.util.ArrayList;

public class Player extends Creature{
    private Sword weapon = null;
    private Armor armor = null;
    private ArrayList<Item> items = null;
    private int moves = 0;
    private int hpMoves = 0;
    private int hallucinateMoves = 0;

    public Player(){
        super();
    }

    public void addHallucinateMoves(){
        hallucinateMoves++;
    }

    public void resetHallucinateMoves(){
        hallucinateMoves = 0;
    }

    public int getHallucinateMoves(){
        return hallucinateMoves;
    }



    public int getMoves(){
        return moves;
    }

    public void addMoves(){
        moves++;
    }

    public void resetMoves(){
        moves = 0;
    }

    public void SetWeapon(Sword _weapon){
        weapon = _weapon;
        
        if(weapon != null){
            String tempName = _weapon.getName();
            tempName += "(w)";
            weapon.setName(tempName);
        }
    }

    public void SetArmor(Armor _armor){
        armor = _armor;

        if(armor != null){
            String tempName = _armor.getName();
            tempName += "(a)";
            armor.setName(tempName);
        }
        
    }

    public void addItem(Item _item){
        if (items == null){
            items = new ArrayList<Item>();
        }
        items.add(_item);
    }

    public void dropItem(int idx){
        
        if (items!= null){
            items.remove(idx);
        }
        
    }

    public Sword getWeapon (){
        return weapon;
    }

    public Armor getArmor (){
        return armor;
    }

    public Item getItem(int idx){
        if(items != null){
            if((idx >= 0) && (idx < items.size())){
                return items.get(idx);
            }else{
                return null;
            }
        }else{
            return null;
        }
        

    }

    public List<Item> getItems(){
        if(items != null){
            return items;
        } else {
            return null;
        }

    }

    public void setHpMoves(int _hpMoves){
        hpMoves = _hpMoves;
    }

    public int getHpMoves(){
        return hpMoves;
    }

    public void DropPack(){
        if(items!= null){
            dropItem(0) ;
        }
        System.out.println("DropPack executed");
    };

    public void EmptyPack(){
        if (items!=null){
            while((items!=null) && (!items.isEmpty())){
                dropItem(0);
            }
        }
    }
    

    public boolean getStatus(){
        return alive;
    }
}