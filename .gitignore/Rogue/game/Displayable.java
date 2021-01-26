package game;

import java.util.Random;
public class Displayable{
    private int Hp = 10;
    private int maxHit = 0;
    private char type;
    private int val;
    private int x_pos;
    private int y_pos;
    private int width;
    private int height;
    private int visibility; //1 - visible, 0 - invisible
    private char disp_char;

    public Displayable(){
    }

    public Displayable(char ch){
        disp_char = ch;
    }
    public void setVisible(){
        visibility = 1;
    }
    public void setInvisible(){
        visibility = 0;
    }

    public void setMaxHit(int _maxHit){
        maxHit = _maxHit;
        
    }

    public void setType(char t){
        type = t;
    }
    public void setIntValue(int v){
        val = v;
    }
    public void setPosX(int x){
        x_pos = x;
        
    }
    public void setPosY(int y){
        y_pos = y;
    }
    public void setWidth(int x){
        width = x;
    }
    public void setHeight(int y){
        height = y;
    }

    public int getX_Pos(){
        return x_pos;
    }
    public int getY_Pos(){
        return y_pos;
    }

    public char get_dispChar(){
        return disp_char;
    }

    public void set_dispChar(char display_character){
        disp_char = display_character;
    }

    public int get_Width(){
        return width;
    }

    public int get_Height(){
        return height;
    }
	public void add_x(int parseInt) {
    }
    
    public void add_y(int parseInt){

    }

    public int getMaxHit(){
        return this.maxHit;
    }

    public int getDamage(){
        Random rand = new Random();
        int n = rand.nextInt(this.getMaxHit()+1);
        return n;
    }

    public int getHP(){
        return Hp;
    }

    public void setHP(int _hp){
        Hp = _hp;
    }
    
    public void addHP(){
        Hp++;
    }
    public int getVisibility(){
        return visibility;
    }

}