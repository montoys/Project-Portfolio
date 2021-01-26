package game;

import asciiPanel.AsciiPanel;

import javax.swing.*;


import java.awt.event.*;
import java.io.CharArrayReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Queue;
import java.util.Random;

@SuppressWarnings("unchecked")
public class ObjectDisplayGrid extends JFrame implements Runnable, KeyListener, InputSubject {

    private static final int DEBUG = 0;
    private static final String CLASSID = ".ObjectDisplayGrid";

    private static AsciiPanel terminal;

    private List<InputObserver> inputObservers = null;
    private Stack<Displayable>[][] objectGrid = null;

    private static int height;
    private static int width;
    private Dungeon dungeon;
    private Class<?> cls;
    private boolean hallucination = false;
    private int hallMoves = 0;


    
    public ObjectDisplayGrid(int _width, int _height) {
        width = _width;
        height = _height;

        
        objectGrid = (Stack<Displayable>[][]) new Stack[width][height];

        terminal = new AsciiPanel(width, height);

        initializeDisplay();
        super.add(terminal);
        super.setSize(width * 9, height * 16);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // super.repaint();
        // terminal.repaint( );
        super.setVisible(true);
        terminal.setVisible(true);
        super.addKeyListener(this);
        inputObservers = new ArrayList<>();
        super.repaint();
    }

    @Override
    public void registerInputObserver(InputObserver observer) {
        if (DEBUG > 0) {
            System.out.println(CLASSID + ".registerInputObserver " + observer.toString());
        }
        inputObservers.add(observer);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (DEBUG > 0) {
            System.out.println(CLASSID + ".keyTyped entered" + e.toString());
        }
        KeyEvent keypress = (KeyEvent) e;
        notifyInputObservers(keypress.getKeyChar());
    }

    private void notifyInputObservers(char ch) {
        for (InputObserver observer : inputObservers) {
            observer.observerUpdate(ch);
            if (DEBUG > 0) {
                System.out.println(CLASSID + ".notifyInputObserver " + ch);
            }
        }
    }

    public void setHallMoves(int _hallMoves) {
        hallMoves = _hallMoves;
    }
    // we have to override, but we don't use this
    @Override
    public void keyPressed(KeyEvent even) {
    }

    // we have to override, but we don't use this
    @Override
    public void keyReleased(KeyEvent e) {
    }


    public final void initializeDisplay() {
        System.out.println(height);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                terminal.write('.', i, j);
            }
        }
        terminal.repaint();
    }

    public void fireUp() {
        if (terminal.requestFocusInWindow()) {
            System.out.println(CLASSID + ".ObjectDisplayGrid(...) requestFocusInWindow Succeeded");
        } else {
            System.out.println(CLASSID + ".ObjectDisplayGrid(...) requestFocusInWindow FAILED");
        }
    }

    public void addObjectToDisplay(Displayable displayable, int x, int y) {
        if ((0 <= x) && (x < objectGrid.length)) {
            if ((0 <= y) && (y < objectGrid[0].length+1)) {
                if(objectGrid[x][y] == null)
                {
                    objectGrid[x][y] = new Stack<Displayable>();
                }
                objectGrid[x][y].push(displayable);
                writeToTerminal(x, y);
            }
        }
    }
    public void removeObjectFromDisplay(int x, int y) {
		if ((0 <= x) && (x < objectGrid.length)) {
            if ((0 <= y) && (y < objectGrid[0].length+1)) {
                if(!objectGrid[x][y].empty())
                {
                    objectGrid[x][y].pop();
                }

                if(objectGrid[x][y].empty()){
                    objectGrid[x][y].add(new Displayable('.'));
                }
                writeToTerminal(x, y);
            }
        }
    }

    private void writeToTerminal(int x, int y) {
        
        char ch = objectGrid[x][y].peek().get_dispChar();
        terminal.write(ch, x, y);
        terminal.repaint();
    }

    public void writeToTerminal(String string, int x, int y){
        terminal.write(string, x, y);
        terminal.repaint();
    }

    public void Hallucinate(){
        hallucination = true;
    }

    public void MovePlayer(int x, int y, Player mainPlayer){

        System.out.println(""+mainPlayer);
        System.out.println("moving to "+x+","+y);
        int pos_X = mainPlayer.getX_Pos();
        int pos_Y = mainPlayer.getY_Pos();
    
        if ((objectGrid[x][y] != null) && (objectGrid[x][y].peek().get_dispChar() != 'X')){
            if ((objectGrid[x][y].peek().get_dispChar() == 'H') || (objectGrid[x][y].peek().get_dispChar() == 'S') || (objectGrid[x][y].peek().get_dispChar() == 'T')){
                
                int damage = mainPlayer.getDamage();
                if (mainPlayer.getWeapon() != null){
                    damage += mainPlayer.getWeapon().getValue();
                }
        
                terminal.write("Player Damage: "+damage,0,dungeon.getTopHeight()+dungeon.get_gameHeight());
                objectGrid[x][y].peek().setHP(objectGrid[x][y].peek().getHP()-damage);
                List<CreatureAction> actions2 = ((Creature)objectGrid[x][y].peek()).getHitActions();
                    if(actions2 !=null){
                        actions2.get(0).Teleport((Creature)objectGrid[x][y].peek(), this, objectGrid);
                       
                    }
                
                if((objectGrid[x][y] != null) && (!objectGrid[x][y].isEmpty())  && (objectGrid[x][y].peek().getHP() < 1)){
                    System.out.println("Monster was killed");
                    List <CreatureAction> actions = ((Creature) objectGrid[x][y].peek()).getDeathActions();
                    System.out.println("Death Actions:"+actions);
                    executeActions(actions, (Creature)objectGrid[x][y].peek());
                }else{
                    damage = objectGrid[x][y].peek().getDamage();
                    if(mainPlayer.getArmor() != null){
                        damage -= mainPlayer.getArmor().getValue();
                    }
                    terminal.write("Monster Damage: "+damage,0,dungeon.getTopHeight()+dungeon.get_gameHeight()+1);
                    mainPlayer.setHP(mainPlayer.getHP()-damage);
                    List<CreatureAction> actions = mainPlayer.getHitActions();
                    if(actions!=null){
                        mainPlayer.DropPack();
                    }
                }
        
                if(mainPlayer.getHP() < 1){
                    System.out.println("player has died");
                    List <CreatureAction> actions = ((Player) mainPlayer).getDeathActions();
                    System.out.println("Death Actions:"+actions);
                    executeActions(actions, mainPlayer);
                    terminal.write("Player died ----- Game has ended",0,dungeon.getTopHeight()+dungeon.get_gameHeight()+2);
                    //mainPlayer.EndGame();
                    terminal.repaint();
                    
                }
        
            }else{
                if(hallucination){
                    mainPlayer.addHallucinateMoves();
                    if(mainPlayer.getHallucinateMoves() <= hallMoves){
                        //boardRooms
                        changeDisplay();

                    }
                    if(mainPlayer.getHallucinateMoves() == hallMoves + 1){
                        mainPlayer.resetHallucinateMoves();
                        hallucination = false;
                        //NormalizeDisplayables
                        for(int i = 0; i < dungeon.get_gameWidth(); i++){
                            for(int j = 0; j < dungeon.get_gameHeight()+dungeon.getTopHeight(); j++){
                                if(objectGrid[i][j] != null && objectGrid[i][j].peek()!=null){
                                    char tempChar = objectGrid[i][j].peek().get_dispChar();
                                    if(tempChar != '.'){
                                        terminal.write(tempChar,i,j);
                                    }
                                }
                            }
                        }
                        terminal.repaint();
                        
                    }

                }
                mainPlayer.addMoves();
                if(mainPlayer.getMoves() == mainPlayer.getHpMoves()){
                    mainPlayer.addHP();
                    mainPlayer.resetMoves();
                }
                removeObjectFromDisplay(mainPlayer.getX_Pos(), mainPlayer.getY_Pos());
                addObjectToDisplay(mainPlayer, x, y);
                mainPlayer.setPosX(x);
                mainPlayer.setPosY(y);
            }
            terminal.repaint();
        }

    }
    
    public void changeDisplay(){
        char tempChar;
        Random rand = new Random();
        for(int i = 0; i < dungeon.get_gameWidth(); i++){
            for(int j = 0; j < dungeon.get_gameHeight()+dungeon.getTopHeight(); j++){
                if(objectGrid[i][j] != null && objectGrid[i][j].peek()!=null){
                    tempChar = objectGrid[i][j].peek().get_dispChar();
                    if(tempChar != '.'){
                        tempChar = (char) ((char) rand.nextInt(58) + 64);
                        terminal.write(tempChar,i,j);
                    }
                }
            }
        }
        terminal.repaint();
    }

    public boolean updateGrid(char ch, Player mainPlayer, Queue<Character> inputQueue){
        int pos_X = mainPlayer.getX_Pos();
        int pos_Y = mainPlayer.getY_Pos();
        int pack_idx = 0;
        char tempCh = '0';
        Item tempItem = null;

        for (int i = 0; i < dungeon.get_gameWidth(); i++){
            for(int j = 0; j < 3; j++){
                terminal.write(".",i,dungeon.get_gameHeight()+dungeon.getTopHeight()+j);
            } 
        }
        /*for(int k = 0; k < dungeon.get_gameWidth(); k++){
            terminal.write(".",k,1);
        }*/
        terminal.repaint();
        
        if(!mainPlayer.getStatus()){
            return false;
        }


        if(ch == 'l'){
            //move right
            MovePlayer(pos_X+1, pos_Y, mainPlayer);
            
        }
        else if(ch == 'j'){
            //move down
            MovePlayer(pos_X, pos_Y+1, mainPlayer);
            
            
        }
        else if (ch == 'k'){
            //Move up
            MovePlayer(pos_X, pos_Y-1, mainPlayer);
        
        }
        else if (ch == 'h'){
            //Move left
            MovePlayer(pos_X-1, pos_Y, mainPlayer);

        }
        else if (ch == 'p'){

            objectGrid[pos_X][pos_Y].pop();
            Displayable temp = objectGrid[mainPlayer.getX_Pos()][pos_Y].peek();
            
            if(temp.get_dispChar() == '?'){
                mainPlayer.addItem((Item)temp);
                removeObjectFromDisplay(pos_X, mainPlayer.getY_Pos());
            } 
            else if (temp.get_dispChar() == ')') {
                
                mainPlayer.addItem((Item)temp);
                removeObjectFromDisplay(pos_X, mainPlayer.getY_Pos());

            } 
            else if (temp.get_dispChar() == ']') {
                mainPlayer.addItem((Item)temp);
                removeObjectFromDisplay(pos_X, mainPlayer.getY_Pos());
                
            }
            
            addObjectToDisplay(mainPlayer, pos_X, mainPlayer.getY_Pos());
            
        }
        else if (ch == 'd'){

            objectGrid[pos_X][pos_Y].pop();
            while(inputQueue.peek() == null){
                System.out.println("");//do nothing
            }

            tempCh = inputQueue.peek();
            if(Character.isDigit(tempCh)){
                pack_idx = Integer.parseInt(String.valueOf(tempCh));
                tempItem = mainPlayer.getItem(pack_idx-1);
            }else{
                tempItem = null;
            }

            
            if(tempItem != null){
                String name = tempItem.getName();
                name = name.replace("(a)", "");
                name = name.replace("(w)","");
                tempItem.setName(name);
                mainPlayer.dropItem(pack_idx-1);
                addObjectToDisplay(tempItem, pos_X, mainPlayer.getY_Pos());
                terminal.repaint();
            }
            
            addObjectToDisplay(mainPlayer, pos_X, mainPlayer.getY_Pos());
        }
        else if (ch == 'i'){

            int idx = 6;
            int idx_2 = 1; //index of pack
            terminal.write("Pack: ",0,dungeon.getTopHeight()+dungeon.get_gameHeight());
            if(mainPlayer.getItems() != null){
                for (Item item : mainPlayer.getItems()){
                    terminal.write(""+idx_2+"-"+item.getName(),idx,dungeon.getTopHeight()+dungeon.get_gameHeight());
                    terminal.repaint();
                    idx += (3+item.getName().length());
                    idx_2++;
                }
                for (int i = idx; i < dungeon.get_gameWidth(); i++){
                    terminal.write(".",i,dungeon.get_gameHeight()+dungeon.getTopHeight()+2);
                }
                
            }
            terminal.repaint();
        }
        else if (ch == 'w'){
            
            while(inputQueue.peek() == null){
                System.out.println("");//do nothing
            }

            tempCh = inputQueue.peek();
            boolean check = false;

            if(Character.isDigit(tempCh)){
                pack_idx = Integer.parseInt(String.valueOf(tempCh));
                tempItem = mainPlayer.getItem(pack_idx-1);
                if(tempItem != null){check = tempItem.getClass() == Armor.class;}
            }else{
                tempItem = null;
            }

            if((tempItem != null) && (check) && (mainPlayer.getArmor() == null)){

                mainPlayer.SetArmor((Armor)tempItem);
                
            }
        }
        else if (ch == 'T'){

            while(inputQueue.peek() == null){
                System.out.println("");//do nothing
            }

            tempCh = inputQueue.peek();
            boolean check = false;
            
            if(Character.isDigit(tempCh)){
                pack_idx = Integer.parseInt(String.valueOf(tempCh));
                tempItem = mainPlayer.getItem(pack_idx-1);
                if(tempItem != null){check = tempItem.getClass() == Sword.class;}
            }else{
                tempItem = null;
            }

            if((tempItem != null) && (check) && (mainPlayer.getWeapon() == null)){

                mainPlayer.SetWeapon((Sword)tempItem);
            }
        }
        else if (ch == 'r'){

            while(inputQueue.peek() == null){
                System.out.println("");//do nothing
            }

            tempCh = inputQueue.peek();
            boolean check = false;
            
            if(Character.isDigit(tempCh)){
                pack_idx = Integer.parseInt(String.valueOf(tempCh));
                tempItem = mainPlayer.getItem(pack_idx-1);
                if(tempItem != null){check = (tempItem.getClass() == Scroll.class);}
            }else{
                tempItem = null;
            }

            if((tempItem != null) && (check)){

                List<ItemAction> actions = tempItem.getActions();
                if(actions != null){executeItemActions(actions, tempItem, this, mainPlayer);}
                mainPlayer.dropItem(pack_idx-1);
            }
        }
        else if(ch == '?'){

            terminal.write("Commands: (h)-(j)-(k)-(l)-(i)-(d)-(p)-(T)-(w)-(r)-(?)",0,dungeon.getTopHeight()+dungeon.get_gameHeight());
            terminal.repaint();

        }
        else if(ch == 'c'){

            Armor temp = mainPlayer.getArmor();
            if(temp != null){
                String name = temp.getName();
                name = name.replace("(a)", "");
                temp.setName(name);
                mainPlayer.SetArmor(null);
            }
        }

        
        terminal.write("Player HP: "+mainPlayer.getHP()+".......",0,0);
        terminal.repaint();

        if(!mainPlayer.getStatus()){
            return false;
        }

        
        return true;
    }

    public void executeActions(List<CreatureAction> actions, Creature creature){
        System.out.println("executeActions with Creature is called");
        
        if(actions != null){
        
            for(CreatureAction act: actions){
                String name = act.getName();
                name = name.replace(" ","");
                if(name.equals(new String("Remove"))){
                    act.Remove(creature, this);
                }else if(name.equals(new String("YouWin"))){
                    act.YouWin(creature);
                }else if(name.equals(new String("ChangeDisplayedType"))){
                    act.ChangeDisplayType(creature,this);
                }else if(name.equals(new String("UpdateDisplay"))){
                    act.UpdateDisplay(creature);
                }else if(name.equals(new String("EndGame"))){
                    act.EndGame(creature);
                }
            }
        }
    }

    public void executeItemActions(List<ItemAction> actions, Item item, ObjectDisplayGrid grid, Player mainPlayer){
        
        for(ItemAction act: actions){
            String name = act.getName();
            name = name.replace(" ","");
            System.out.println("name: "+name);

            if(name.equals(new String("Hallucinate"))){
                act.Hallucinate(item, this, act);
            }else if(name.equals(new String("BlessArmor"))){
                act.BlessArmor(item,mainPlayer, this, act);
            }
        }
    }
    


    public void setDungeon(Dungeon dung){
        dungeon = dung;
    }
    public Dungeon getDungeon(){
        return dungeon;
    }


    public void buildRooms(List<Displayable> rooms, List<Passage> passages, Dungeon dungeon){
        for(Displayable displayable : rooms){

            int width = displayable.get_Width();
            int height = displayable.get_Height();
            int x_pos = displayable.getX_Pos();
            int y_pos = displayable.getY_Pos();
            int i = 0;
            int j = 0;
            int topHeight = dungeon.getTopHeight();

            for(i = x_pos; i < width + x_pos; i++){
                for(j = y_pos; j < height + y_pos; j++){
                    if ((i == x_pos) || (i == x_pos + width - 1)){
                        addObjectToDisplay(new Displayable('X'), i, j);
                    }
                    else if ((j == y_pos) || (j == y_pos + height -1)){
                        addObjectToDisplay(new Displayable('X'), i, j);
                    }
                    else{
                        addObjectToDisplay(new Displayable('.'), i, j);
                    }
                } 
            }
            
            if (passages != null){
                for (Passage disp : passages){
                    List<Integer> x_cords = disp.getXcord();
                    List<Integer> y_cords = disp.getYcord();
                    
                    for(int idx=1; idx<x_cords.size(); idx++){
                        int x_0 = x_cords.get(idx-1);
                        int x_1 = x_cords.get(idx);
                        int y_0 = y_cords.get(idx-1);
                        int y_1 = y_cords.get(idx);
                        if (x_1 >= x_0){
                            for(; x_0 <= x_1; x_0++){
                                addObjectToDisplay(new Displayable('#'), x_0, y_cords.get(idx-1));
                            }
                        }
                        else{
                            for(; x_0 >= x_1; x_0--){
                                addObjectToDisplay(new Displayable('#'), x_0, y_cords.get(idx-1));
                            }
                        }
                        if (y_1 >= y_0){
                            for(; y_0 <= y_1; y_0++){
                                addObjectToDisplay(new Displayable('#'), x_cords.get(idx-1), y_0);
                            }
                        }
                        else{
                            for(; y_0 >= y_1; y_0--){
                               addObjectToDisplay(new Displayable('#'), x_cords.get(idx-1), y_0);
                            }
                        }
                    }
                } 
            }
            terminal.repaint();
        }
    }

    @Override
    public void run( ) {
       try {
          Thread.sleep(20);
       } catch(Exception e) {System.out.println(e);}
   }
}