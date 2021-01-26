package game;

import java.util.List;
import java.util.Random;
import java.util.Stack;


public class CreatureAction extends Action{

    public CreatureAction(Creature owner){
        super(owner);
    }

    public void Remove(Creature owner, ObjectDisplayGrid objectGrid){
        owner.setInvisible();
        objectGrid.removeObjectFromDisplay(owner.getX_Pos(), owner.getY_Pos());
        System.out.println("Remove");

    }

    public void YouWin(Creature owner){
        String msg = getMessage();
        System.out.println(msg);
        System.out.println("YouWin");
    };
    public void UpdateDisplay(Creature owner){
        System.out.println("UpdateDisplay call");
    };

    public void Teleport(Creature owner, ObjectDisplayGrid grid, Stack<Displayable>[][] objectGrid){
        int x = owner.getX_Pos();
        int y = owner.getY_Pos();
        grid.removeObjectFromDisplay(x, y);
        Random rand = new Random();
        List<Displayable> rooms = grid.getDungeon().getRooms();
        Room rndRoom = (Room) rooms.get(rand.nextInt(rooms.size() - 1));
        x = rand.nextInt(rndRoom.get_Width() - 1) + rndRoom.getX_Pos();
        y = rand.nextInt(rndRoom.get_Height() - 1) + rndRoom.getY_Pos();
        while(objectGrid[x][y].peek().get_dispChar() != '.' ){
            x = rand.nextInt(rndRoom.get_Width() - 1) + rndRoom.getX_Pos();
            y = rand.nextInt(rndRoom.get_Height() - 1) + rndRoom.getY_Pos();
        }
        //Generate random numbers until the coordinates are valid
        owner.setPosX(x);
        owner.setPosY(y);
        grid.addObjectToDisplay(owner, x, y);
        System.out.println("Teleported!");

    };  
    public void ChangeDisplayType(Creature owner, ObjectDisplayGrid grid){
        List <CreatureAction> actions = owner.getDeathActions();
        for (CreatureAction act: actions){
            if(act.getName().equals(new String("ChangeDisplayedType"))){
                owner.set_dispChar(act.getCharValue());
                grid.removeObjectFromDisplay(owner.getX_Pos(), owner.getY_Pos());
                grid.addObjectToDisplay(owner, owner.getX_Pos(), owner.getY_Pos());
                break;
            } 
        }
        System.out.println("Changed displayed type");
    };
    
    
    public void EndGame(Creature owner){
        System.out.println("Player end game call");
        owner.alive = false;
    }

}