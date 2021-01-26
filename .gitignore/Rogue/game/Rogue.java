package game;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class Rogue implements Runnable {

    private static final int DEBUG = 0;
    private boolean isRunning;
    public static final int FRAMESPERSECOND = 60;
    public static final int TIMEPERLOOP = 1000000000 / FRAMESPERSECOND;
    private static ObjectDisplayGrid displayGrid = null;
    private Thread keyStrokePrinter;

    private static List<Displayable> creatures = null;
    private static List<Item> items = null;
    private static List<Passage> passages = null;
    private static List<Displayable> rooms = null;
    static Dungeon dungeon = null;

    public Rogue(int width, int height) {
        displayGrid = new ObjectDisplayGrid(width, height); // SPM
    }

    @Override
    public void run() {
        // SPM displayGrid.fireUp();
        

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }

        displayGrid.buildRooms(rooms, passages, dungeon);

        if (creatures != null) {
            for (Displayable displayable : creatures) {
                displayGrid.addObjectToDisplay(displayable, displayable.getX_Pos(), displayable.getY_Pos());
            }
        }

        if (items != null) {
            for (Displayable displayable : items) {
                displayGrid.addObjectToDisplay(displayable, displayable.getX_Pos(), displayable.getY_Pos());
            }
        }

    }

    public static void main(String[] args) throws Exception {

        /* import parsing code from step1 */
        String fileName = null;
        switch (args.length) {
            case 1:
                // note that the relative file path may depend on what IDE you are
                // using. This worked for NetBeans.
                fileName = "xmlFiles/" + args[0];
                break;
            default:
                System.out.println("java Test <xmlfilename>");
                return;
        }

        // Create a saxParserFactory, that will allow use to create a parser
        // Use this line unchanged
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

        // We haven't covered exceptions, so just copy the try { } catch {...}
        // exactly, // filling in what needs to be changed between the open and
        // closed braces.
        DisplayableXMLHandler handler = null;

        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            handler = new DisplayableXMLHandler();
            saxParser.parse(fileName, handler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace(System.out);
        }

        dungeon = handler.getDungeon();
        creatures = dungeon.getCreatures();
        items = dungeon.getItems();

        rooms = dungeon.getRooms();
        passages = dungeon.getPassages();
        
        // SPM displayGrid = new ObjectDisplayGrid(dungeon.get_gameWidth( ), dungeon.get_gameHeight( )); // SPM
        Rogue rogue = new Rogue(dungeon.get_gameWidth(), dungeon.get_gameHeight()+dungeon.getTopHeight()+dungeon.getBottomHeight());
        displayGrid.setDungeon(dungeon);
        Thread testThread = new Thread(rogue);
        testThread.start();

        rogue.keyStrokePrinter = new Thread(new KeyStrokePrinter(displayGrid));
        rogue.keyStrokePrinter.start();

        testThread.join();
        rogue.keyStrokePrinter.join();
    }
}
