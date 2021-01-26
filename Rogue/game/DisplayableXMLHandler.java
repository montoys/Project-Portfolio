package game;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;  
import javax.xml.parsers.SAXParserFactory;  


public class DisplayableXMLHandler extends DefaultHandler {

    // the two lines that follow declare a DEBUG flag to control
    // debug print statements and to allow the class to be easily
    // printed out.  These are not necessary for the parser.
    private static final int DEBUG = 1;
    private static final String CLASSID = "DispXMLHandler";

    // data can be called anything, but it is the variables that
    // contains information found while parsing the xml file
    private StringBuilder data;

    // When the parser parses the file it will add references to
    // Student objects to this array so that it has a list of 
    // all specified students.  Had we covered containers at the
    // time I put this file on the web page I would have made this
    // an ArrayList of Students (ArrayList<Student>) and not needed
    // to keep tract of the length and maxStudents.  You should use
    // an ArrayList in your project.
    private List <Displayable> displayables;

    // The XML file contains a list of Students, and within each 
    // Student a list of activities (clubs and classes) that the
    // student participates in.  When the XML file initially
    // defines a student, many of the fields of the object have
    // not been filled in.  Additional lines in the XML file 
    // give the values of the fields.  Having access to the 
    // current Student and Activity allows setters on those 
    // objects to be called to initialize those fields.
    private Action actionBeingParsed = null;
    private Creature creatureBeingParsed = null;
    private Displayable displayableBeingParsed = null;
    private Item itemBeingParsed = null;
    private Room roomBeingParsed = null;
    private Displayable passageBeingParsed = null;
    private Player playerBeingParsed = null;
    private Dungeon dungeon = null;
    

    // The bX fields here indicate that at corresponding field is
    // having a value defined in the XML file.  In particular, a
    // line in the xml file might be:
    // <instructor>Brook Parke</instructor> 
    // The startElement method (below) is called when <instructor>
    // is seen, and there we would set bInstructor.  The endElement
    // method (below) is called when </instructor> is found, and
    // in that code we check if bInstructor is set.  If it is,
    // we can extract a string representing the instructor name 
    // from the data variable above.
    boolean bvisible = false;
    boolean bposX = false;
    boolean bposY = false;
    boolean bwidth = false;
    boolean bheight = false;
    boolean bactionMessage = false;
    boolean bactionIntVal = false;
    boolean bactionCharVal = false;
    boolean bhp = false;
    boolean bhpmoves = false;
    boolean bmaxhit = false;
    boolean btype = false;
    boolean bitemIntValue = false;
    boolean bpassage = false;
    boolean bplayer = false;


    // Used by code outside the class to get the list of Student objects
    // that have been constructed.
    public List<Displayable> getDisplayables() {
        return displayables;
    }

    public Dungeon getDungeon() {
        return dungeon;

    }
    // A constructor for this class.  It makes an implicit call to the
    // DefaultHandler zero arg constructor, which does the real work
    // DefaultHandler is defined in org.xml.sax.helpers.DefaultHandler;
    // imported above, and we don't need to write it.  We get its 
    // functionality by deriving from it!
    public DisplayableXMLHandler() {
    }

    // startElement is called when a <some element> is called as part of 
    // <some element> ... </some element> start and end tags.
    // Rather than explain everything, look at the xml file in one screen
    // and the code below in another, and see how the different xml elements
    // are handled.
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        int topHeight = 0;
        if(qName.equalsIgnoreCase("Dungeon")){
            String dungeonName = attributes.getValue("name");
            int dungeonWidth = Integer.parseInt(attributes.getValue("width"));
            topHeight = Integer.parseInt(attributes.getValue("topHeight"));
            int gameHeight = Integer.parseInt(attributes.getValue("gameHeight"));
            int bottomHegiht = Integer.parseInt(attributes.getValue("bottomHeight"));
            dungeon = new Dungeon(dungeonName, dungeonWidth, topHeight, gameHeight, bottomHegiht);
            displayables = new ArrayList<Displayable>(); 
        }
        
        //STRUCTURES

        else if (qName.equalsIgnoreCase("Room")) {
            Structure room = new Room(attributes.getValue("room"));
            displayables.add(room);
            dungeon.addRoom(room);
            displayableBeingParsed = room;
            roomBeingParsed = (Room) room;
        }
        else if(qName.equalsIgnoreCase("Passage")){
            Passage passage = new Passage();
            passage.setId(Integer.parseInt(attributes.getValue("room1")), Integer.parseInt(attributes.getValue("room2")));
            displayables.add(passage);
            dungeon.addPassage(passage);
            displayableBeingParsed = passage;
            bpassage = true;
            passageBeingParsed = passage;
        }

        //SETTING DISPLAYABLE VALUES
        else if(qName.equalsIgnoreCase("visible")) {
            bvisible = true;
        }
        else if(qName.equalsIgnoreCase("posX")) {
            bposX = true;
        }
        else if(qName.equalsIgnoreCase("posY")) {
            bposY = true;
        }
        else if(qName.equalsIgnoreCase("width")) {
            bwidth = true;
        }
        else if(qName.equalsIgnoreCase("height")) {
            bheight = true;
        }
        else if(qName.equalsIgnoreCase("type")) {
            btype = true;
        }
        else if(qName.equalsIgnoreCase("hp")) {
            bhp = true;
        }
        else if(qName.equalsIgnoreCase("maxhit")) {
            bmaxhit = true;
        }
        else if(qName.equalsIgnoreCase("hpMoves")) {
            bhpmoves = true;
        }
        else if(qName.equalsIgnoreCase("actionMessage")){
            bactionMessage = true;
        }
        else if(qName.equalsIgnoreCase("actionIntValue")){
            bactionIntVal = true;
        }
        else if(qName.equalsIgnoreCase("actionCharValue")){
            bactionCharVal = true;
        }
        else if(qName.equalsIgnoreCase("ItemIntValue")) {
            bitemIntValue = true;
        }
        
        //CREATURES
        else if(qName.equalsIgnoreCase("Monster")) {
            Monster monster = new Monster();
            String name = attributes.getValue("name");
            monster.setId(Integer.parseInt(attributes.getValue("room")), Integer.parseInt(attributes.getValue("serial")));
            
            if(name.equals("Troll")){
                monster.set_dispChar('T');
            } else if(name.equals("Snake")){
                monster.set_dispChar('S');
            } else if(name.equals("Hobgoblin")){
                monster.set_dispChar('H');
            }
            
            displayables.add(monster);
            dungeon.addCreature(monster);
            roomBeingParsed.addCreature(monster);
            creatureBeingParsed = monster;
            displayableBeingParsed = monster;
        }

        else if(qName.equalsIgnoreCase("Player")) {
            Player player = new Player();
            player.setName(attributes.getValue("name"));
            player.setId(Integer.parseInt(attributes.getValue("room")), Integer.parseInt(attributes.getValue("serial")));
            player.set_dispChar('@'); 
            displayables.add(player);
            dungeon.addCreature(player);
            dungeon.setMainPlayer(player);
            creatureBeingParsed = player;
            displayableBeingParsed = player;
            playerBeingParsed = player;
            bplayer = true;
        }

        //ITEMS
        else if(qName.equalsIgnoreCase("Scroll")){
            Scroll scroll = new Scroll(attributes.getValue("name"));
            scroll.setId(Integer.parseInt(attributes.getValue("room")), Integer.parseInt(attributes.getValue("serial")));
            scroll.set_dispChar('?');
            displayables.add(scroll);
            dungeon.addItem(scroll);
            itemBeingParsed = scroll;
            displayableBeingParsed = scroll;
            if(playerBeingParsed != null && bplayer == true){
                playerBeingParsed.addItem(scroll);
                playerBeingParsed = null;
            }
        }
        else if(qName.equalsIgnoreCase("Armor")){
            Armor armor = new Armor(attributes.getValue("name"));
            armor.setId(Integer.parseInt(attributes.getValue("room")), Integer.parseInt(attributes.getValue("serial")));
            armor.set_dispChar(']');
            displayables.add(armor);
            if(!bplayer){dungeon.addItem(armor);}
            itemBeingParsed = armor;
            displayableBeingParsed = armor;
            if(playerBeingParsed != null && bplayer == true){
                playerBeingParsed.addItem(armor);
                playerBeingParsed = null;
            }
        }
        else if(qName.equalsIgnoreCase("Sword")){
            Sword sword = new Sword(attributes.getValue("name"));
            sword.setId(Integer.parseInt(attributes.getValue("room")), Integer.parseInt(attributes.getValue("serial")));
            sword.set_dispChar(')');
            displayables.add(sword);
            dungeon.addItem(sword);
            itemBeingParsed = sword;
            displayableBeingParsed = sword;
            if(playerBeingParsed != null && bplayer == true){
                playerBeingParsed.addItem(sword);
                playerBeingParsed = null;
            }
        }

        //ACTIONS
        else if(qName.equalsIgnoreCase("CreatureAction")){
            CreatureAction action = new CreatureAction(creatureBeingParsed);
            String name = attributes.getValue("name");
            String type = attributes.getValue("type");
            action.SetName(name);
            action.SetType(type);
            creatureBeingParsed.addAction(type, action);        
            actionBeingParsed = action;
        }
        else if(qName.equalsIgnoreCase("ItemAction")){
            ItemAction action = new ItemAction(itemBeingParsed);
            String name = attributes.getValue("name");
            String type = attributes.getValue("type");
            action.SetName(name);
            action.SetType(type);
            itemBeingParsed.addAction(name, (ItemAction)action);
            actionBeingParsed = action;
        }

        data = new StringBuilder();
    }



    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("Player")){
            bplayer = false;
        }
        if(!bpassage){
            if (bvisible) {
                displayableBeingParsed.setVisible();
                bvisible = false;
            } else if (bposX) {
                displayableBeingParsed.setPosX(Integer.parseInt(data.toString()));
                bposX = false;
            } else if (bposY) {
                displayableBeingParsed.setPosY(Integer.parseInt(data.toString())+dungeon.getTopHeight());
                bposY = false;
            } else if (bwidth) {
                displayableBeingParsed.setWidth(Integer.parseInt(data.toString()));
                bwidth = false;
            } else if (bheight) {
                displayableBeingParsed.setHeight(Integer.parseInt(data.toString()));
                bheight = false;
            } else if (btype) {
                displayableBeingParsed.setType(data.toString().charAt(0));
                btype = false;
            } else if (bhp) {
                creatureBeingParsed.setHP(Integer.parseInt(data.toString()));
                bhp = false;
            } else if (bmaxhit) {
                displayableBeingParsed.setMaxHit(Integer.parseInt(data.toString()));
                bmaxhit = false;
            } else if (bhpmoves) {
                playerBeingParsed.setHpMoves(Integer.parseInt(data.toString()));
                bhpmoves = false;
            } else if (bactionMessage) {
                actionBeingParsed.SetMessage(data.toString());
                bactionMessage = false;
            } else if (bactionIntVal) {
                actionBeingParsed.SetIntVaule(Integer.parseInt(data.toString()));
                bactionIntVal = false;
            } else if (bactionCharVal) {
                actionBeingParsed.SetCharValue(data.toString().charAt(0));
                bactionCharVal = false;
            } else if (bitemIntValue) {
                itemBeingParsed.setIntValue(Integer.parseInt(data.toString()));
                bitemIntValue = false;
            }
        } else {
            if(bposX){
                passageBeingParsed.add_x(Integer.parseInt(data.toString()));
                bposX = false;
            }
            if(bposY){
                passageBeingParsed.add_y(Integer.parseInt(data.toString())+dungeon.getTopHeight());
                bposY = false;
            }

        }
    }




    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        data.append(new String(ch, start, length));
        if (DEBUG > 1) {
            System.out.println(CLASSID + ".characters: " + new String(ch, start, length));
            System.out.flush();
        }
    }
}
