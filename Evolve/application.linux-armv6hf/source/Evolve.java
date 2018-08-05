import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Evolve extends PApplet {

ArrayList<Cell> cells = new ArrayList<Cell>();
ArrayList<Cell> newCells = new ArrayList<Cell>();
boolean isStart = true;
int startingCells = 50;
public void setup() {
  surface.setSize(displayWidth-100, displayHeight-100);
  background(0xffFFFFFF);
}
public void draw() {
  //delay(100); //<>//
  update();
  background(0xffFFFFFF);
  for (int i = 0; i < cells.size(); i++) {
    drawCell(cells.get(i));
  }
}
public void drawCell(Cell c) {
  fill((int)(255-(2.55f*c.strength)));
  ellipse((float)c.x, (float)c.y, (float)c.size, (float)c.size);
}
public void update() {
  surface.setTitle("Population: "+cells.size());
  if (cells.size()==0 && isStart) {
    isStart = false;
    for (int i =0; i< startingCells; i++)
      cells.add(new Cell((int)random(width), (int)random(height)));
  } else if (cells.size() > 0) {
    for (int i =0; i<cells.size(); i++) {
      updateCell(cells.get(i));
    }    
    for (int i =0; i<newCells.size(); i++) {
      cells.add(newCells.get(i));
    }
    newCells.clear();
    int i =0;
    while (i < cells.size()) {
      if (cells.get(i).health <=1 )
        cells.remove(i);
      else
        i++;
    }
  } else {
    surface.setTitle("EXTINCTION");
    noLoop();
  }
}
public void updateCell(Cell c) {
  moveCell(c);
  if (c.health > 1 && random(1)<c.breedability)
    breedCell(c);
  if (c.health > 1)
    c.update();
}
public void moveCell(Cell c) {
  if (c.direction == 0 || c.direction == 1 || c.direction == 7) {
    c.x+=random((float)(c.speed));
    if (c.x > width) {
      c.x= width;
      switch(c.direction) {
      case 0: 
        c.direction = 4;
        break;
      case 1: 
        c.direction = 3;
        break;
      case 7: 
        c.direction = 5;
        break;
      }
    }
  }
  if (c.direction >= 3 && c.direction <= 5) {
    c.x-=random((float)(c.speed));
    if (c.x < 0) {
      c.x = 0;
      switch(c.direction) {
      case 3: 
        c.direction = 1;
        break;
      case 4: 
        c.direction = 0;
        break;
      case 5: 
        c.direction = 7;
        break;
      }
    }
  }
  if (c.direction >=5 && c.direction <=7) {
    c.y-=random((float)(c.speed));
    if (c.y < 0) {
      c.y = 0;    
      switch(c.direction) {
      case 5: 
        c.direction = 7;
        break;
      case 6: 
        c.direction = 2;
        break;
      case 7: 
        c.direction = 5;
        break;
      }
    }
  }
  if (c.direction >=1 && c.direction <=3) {
    c.y+=random((float)(c.speed));
    if (c.y > height) {
      c.y =height;
      switch(c.direction) {
      case 1: 
        c.direction = 3;
        break;
      case 2: 
        c.direction = 6;
        break;
      case 3: 
        c.direction = 1;
        break;
      }
    }
  }
  checkCollision(c);
}
public void breedCell(Cell c) {
  newCells.add(c.breed());
}
public void updateCellHealth(Cell c) {
}
public void checkCollision(Cell c) {
  for (int i =0; i<cells.size(); i++) {
    if (cells.get(i)!=c) {
      Cell current = cells.get(i);
      if (current.health > 0 && sqrt((float)(Math.pow(c.x-current.x, 2)+Math.pow(c.y-current.y, 2))) <= (current.size + c.size)) {
        combat(c, current);
        if (c.health <= 0 ) return;
      }
    }
  }
}
public void combat(Cell a, Cell b) {
  //println(a.health*a.size*a.strength+" - "+b.health*b.size*b.strength);
  if (a.health*a.size*a.strength - b.health*b.size*b.strength > 100000) {
    a.health += b.health; 
    b.health =0;
  } else if (b.health*b.size*b.strength - a.health*a.size*a.strength > 100000) { 
    b.health+=a.health; 
    a.health=0;
  }
}
class Cell {
  double x;
  double y;
  double size;
  double strength;
  double breedability;
  double health=100;
  double speed;
  int direction;
  int speedCap = 10;
  Cell(double x, double y) {
    this(x, y, (double)random(50), (double)random(50), (double)random(0,0.01f));
  }
  Cell(double x, double y, double size, double strength, double breedability) {
    this.x = x;
    this.y = y;
    this.size = size;
    this.strength = strength;
    this.breedability = breedability;
    this.direction = (int)random(8);
    update();
  }
  public void update() {
    health *= (1.0f - ((strength*size)/100000.0f));
    speed = (strength*size*health) / 20000.0f;
    if(speed > speedCap) speed = speedCap;
    double toMove = random(1);
    direction += (toMove < 0.333f ? -1 : toMove < 0.666f ? 1 : 0);
    if (direction == -1) direction = 7;
    else if (direction == 8) direction = 0;
  }
  public Cell breed() {
    double childSize = this.size+random(-10, 10);
    if (childSize < 0) childSize = 0;
    else if (childSize > 100) childSize = 100;
    double childStrength = this.strength+random(-10, 10);
    if (childStrength < 0) childStrength = 0;
    else if (childStrength > 100) childStrength = 100;
    double childBreedability = this.breedability+random(-0.005f,0.005f);
    if(childBreedability < 0) childBreedability = 0;
    else if(childBreedability > 0.01f) childBreedability = 0.01f;
    double childX=this.x;
    double childY=this.y;
    switch(direction){
     case 0: childX = this.x - (childSize + this.size)/2;break;
     case 1: childX = this.x - (childSize + this.size)/2;childY = this.y - (childSize + this.size)/2;break;
     case 2: childY = this.y - (childSize + this.size)/2;break;
     case 3: childX = this.x + (childSize + this.size)/2;childY = this.y - (childSize + this.size)/2;break;
     case 4: childX = this.x + (childSize + this.size)/2;break;
     case 5: childX = this.x + (childSize + this.size)/2;childY = this.y + (childSize + this.size)/2;break;
     case 6: childY = this.y + (childSize + this.size)/2;break;
     case 7: childX = this.x - (childSize + this.size)/2;childY = this.y + (childSize + this.size)/2;break;
    }
    return new Cell(childX, childY, childSize, childStrength, childBreedability);
    
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Evolve" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
