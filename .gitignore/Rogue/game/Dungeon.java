package game;
import java.util.ArrayList;
import java.util.List;



public class Dungeon{
    private String name;
    private int width;
    private int gameHeight;
    private int bottomHeight;
    private int topHeight;
    private List<Displayable> rooms;
    private List<Displayable> creatures;
    private List<Item> items;
    private List<Passage> passages;
    private Player mainPlayer;

    public Dungeon(String _name, int _width, int _topHeight, int _gameHeight, int _bottomHeight){
        name = _name;
        width = _width;
        gameHeight = _gameHeight;
        topHeight = _topHeight;
        bottomHeight = _bottomHeight;
        
    }

    public int get_gameHeight(){
        return gameHeight;
    }
    
    public int get_gameWidth(){
        return width;
    }

    public void addRoom(Displayable room) {
        // append room to list of rooms
        if(rooms == null){
            rooms = new ArrayList<Displayable>();
        }
        rooms.add(room);
    }
    public void setMainPlayer(Player mainP){
        mainPlayer = mainP;
    }
    public void addCreature(Displayable creature){
        // add creature
        if(creatures == null){
            creatures = new ArrayList<Displayable>();
        }
        creatures.add(creature);
    }

    public void addPassage(Passage passage) {
        // add passage to list of passages
        if(passages == null){
            passages = new ArrayList<Passage>();
        }
        passages.add(passage);
    }
    
    public void addItem(Item item){
        if(items == null){
            items = new ArrayList<Item>();
        }
        items.add(item);
    }

    public List<Displayable> getCreatures() {
        return creatures;
    }

    public List<Item> getItems() {
        return items;
    }
    public Player getMainPlayer(){
        return mainPlayer;
    }

    public List<Displayable> getRooms() {
        return rooms;
    }

    public List<Passage> getPassages() {
        return passages;
    }

    public int getTopHeight(){
        return topHeight;
    }

    public int getBottomHeight(){
        return bottomHeight;
    }
}