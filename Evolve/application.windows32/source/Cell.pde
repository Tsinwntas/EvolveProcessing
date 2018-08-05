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
    this(x, y, (double)random(50), (double)random(50), (double)random(0,0.01));
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
  void update() {
    health *= (1.0 - ((strength*size)/100000.0));
    speed = (strength*size*health) / 20000.0;
    if(speed > speedCap) speed = speedCap;
    double toMove = random(1);
    direction += (toMove < 0.333 ? -1 : toMove < 0.666 ? 1 : 0);
    if (direction == -1) direction = 7;
    else if (direction == 8) direction = 0;
  }
  Cell breed() {
    double childSize = this.size+random(-10, 10);
    if (childSize < 0) childSize = 0;
    else if (childSize > 100) childSize = 100;
    double childStrength = this.strength+random(-10, 10);
    if (childStrength < 0) childStrength = 0;
    else if (childStrength > 100) childStrength = 100;
    double childBreedability = this.breedability+random(-0.005,0.005);
    if(childBreedability < 0) childBreedability = 0;
    else if(childBreedability > 0.01) childBreedability = 0.01;
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
