package game;
public class ItemAction extends Action{
    
    public ItemAction(Item owner){
        super(owner);
    }

    public void BlessArmor(Item owner, Player mainPlayer, ObjectDisplayGrid grid, ItemAction act){
        System.out.println("Scroll value: " +owner.getValue());
        char ch = this.getCharValue();

        
        if(ch == 'a'){
            if(mainPlayer.getArmor() == null){
                grid.writeToTerminal("scroll does nothing because armor not being used", 0, 1);
            }else{
                Armor armor = mainPlayer.getArmor();
                armor.updateValue(act.getIntValue());
                grid.writeToTerminal("Armor cursed! "+act.getIntValue()+" taken from its effectiveness", 0, 1);
            }
        }else if(ch == 'w'){
            if(mainPlayer.getWeapon() == null){
                grid.writeToTerminal("scroll does nothing because sword not being used", 0, 1);
            }else{
                Sword sword = mainPlayer.getWeapon();
                sword.updateValue(act.getIntValue());
                grid.writeToTerminal("Weapon cursed! "+act.getIntValue()+" taken from its effectiveness", 0, 1);
                //print message
            }
        }
    };
    
    public void Hallucinate(Item owner, ObjectDisplayGrid grid, ItemAction act){
        System.out.println("Hallucinate call");
        grid.Hallucinate();
        grid.setHallMoves(act.getIntValue());
        grid.changeDisplay();
    };
}