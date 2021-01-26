package game;

import java.util.ArrayList;
import java.util.List;

public class Item extends Displayable {
    private Creature owner;
    private int room = 0;
    private int serial = 0;
    private int value = 0;
    protected String name;
    private List<ItemAction> actions = null;

    public Item(String _name) {
        super();
        name = _name;
    }

    public void setOwner(Creature _owner) {
        owner = _owner;
    }

    protected void setId(int _room, int _serial) {
        room = _room;
        serial = _serial;
    }

    public void setValue(int _val) {
        value = _val;
    }

    public int getValue() {
        return value;
    }

    public void updateValue(int change) {
        value += change;
    }

    public void addAction(String name, ItemAction a){
        if(actions == null){
            actions = new ArrayList<ItemAction>();
        }
        actions.add(a);
    }

    public List<ItemAction> getActions(){
        return actions;
    }

    public String getName(){
        return name;
    }
    public void setName(String _name){
        name = _name;
    }
}