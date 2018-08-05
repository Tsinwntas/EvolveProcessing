ArrayList<Cell> cells = new ArrayList<Cell>();
ArrayList<Cell> newCells = new ArrayList<Cell>();
boolean isStart = true;
int startingCells = 50;
void setup() {
  surface.setSize(displayWidth-100, displayHeight-100);
  background(#FFFFFF);
}
void draw() {
  //delay(100); //<>//
  update();
  background(#FFFFFF);
  for (int i = 0; i < cells.size(); i++) {
    drawCell(cells.get(i));
  }
}
void drawCell(Cell c) {
  fill((int)(255-(2.55*c.strength)));
  ellipse((float)c.x, (float)c.y, (float)c.size, (float)c.size);
}
void update() {
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
void updateCell(Cell c) {
  moveCell(c);
  if (c.health > 1 && random(1)<c.breedability)
    breedCell(c);
  if (c.health > 1)
    c.update();
}
void moveCell(Cell c) {
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
void breedCell(Cell c) {
  newCells.add(c.breed());
}
void updateCellHealth(Cell c) {
}
void checkCollision(Cell c) {
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
void combat(Cell a, Cell b) {
  //println(a.health*a.size*a.strength+" - "+b.health*b.size*b.strength);
  if (a.health*a.size*a.strength - b.health*b.size*b.strength > 100000) {
    a.health += b.health; 
    b.health =0;
  } else if (b.health*b.size*b.strength - a.health*a.size*a.strength > 100000) { 
    b.health+=a.health; 
    a.health=0;
  }
}
