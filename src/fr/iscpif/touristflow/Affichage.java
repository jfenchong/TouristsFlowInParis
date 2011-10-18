/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.iscpif.touristflow;
import processing.core.*; 
import de.fhpotsdam.unfolding.geo.Location;
/**
 *
 * @author guest
 */
public class Affichage {
    
    public static void selection( float x, float y, float radius, int i) { // fonction d'affichage en mode selection 
      PApplet p = Application.session.getPApplet();  
      p.fill(190, 201, 186, 100);
      p.rect(0, 0, 1400, 950); 
      p.textAlign(PConstants.LEFT, PConstants.TOP);
      p.stroke(153);
      p.fill(16, 91, 136);
      p.text("Arc Entrant", 25, 500);
      p.fill(182, 92, 96);
      p.stroke(153);
      p.text("Arc Sortant", 25, 475);

      for (int k = 0; k < Application.session.getTableauGephi()[Application.session.getIndex()].edgeCount; k++){
         Location l1 = new Location(Application.session.getMatEdge(0, k),Application.session.getMatEdge(1, k));
         Location l2 = new Location(Application.session.getMatEdge(2, k),Application.session.getMatEdge(3, k));
         float xy1[] = Application.session.getMap().getScreenPositionFromLocation(l1);
         float xy2[] = Application.session.getMap().getScreenPositionFromLocation(l2);
        if ( Application.session.getMatEdge(4, k) > 10 ) {
          float poids = PApplet.map( PApplet.log(Application.session.getMatEdge(4, k)), PApplet.log(10), PApplet.log(Application.session.getEdgeMax()), 1, 15 );
          p.strokeWeight(poids);
          if ((x == xy1[0])&&(y == xy1[1])){
            p.stroke(182, 92, 96, 255);
            p.line(xy1[0], xy1[1], xy2[0], xy2[1] );
          }
          else if ((x == xy2[0])&&(y == xy2[1])){
            p.stroke(16, 91, 136, 255);
            p.line(xy1[0], xy1[1], xy2[0], xy2[1] );  
          }
        }
      }

      p.fill(1, 160, 20, 200);
      p.ellipse(x,y,radius,radius);
      p.noStroke();
      p.noFill();
    }


     public static void afficheEchelle() {
      PApplet p = Application.session.getPApplet();
      p.fill( 255 );
      p.strokeWeight( 2 ); 
      p.line( (p.width+1000)/2 - 40, 800, (p.width+1000)/2 + 40, 800 ); // ligne de référence sur l'écran : 80 pixels
      p.strokeWeight( 2 );
      p.line( (p.width+1000)/2 - 42, 805, (p.width+1000)/2 - 42, 795 );
      p.line( (p.width+1000)/2 + 42, 805, (p.width+1000)/2 + 42, 795 );
      Location location1 = Application.session.getMap().getLocationFromScreenPosition(598, 700); // transformation des extrémités en coordonnées Lat/Lon 
      Location location2 = Application.session.getMap().getLocationFromScreenPosition(682, 700);
      Application.session.setD(Bibliotheque.distFrom(location1.getLat(), location1.getLon(), location2.getLat(), location2.getLon())); // appel de la fonction de calcul de distance entre deux points
      p.textAlign(PConstants.CENTER);
      p.text( (int)Application.session.getD() + " m", (p.width+1000)/2, 795);
    }

    public static void afficheLegendeLissee(){
      PApplet p = Application.session.getPApplet();
      float x = p.width/56;
      float y = (float) (p.height/1.4);
      float h = (float) (p.height/9.79);
      float l = (float) (p.width/4.375);
      if ( Application.session.isBiweight() || Application.session.isShepard() ) { 
          Smooth.miseAJourCarteLissee(); 
          Smooth.lissage();
          p.textAlign(PConstants.LEFT,PConstants.TOP);
          p.strokeWeight(2);
          p.stroke(10, 150);
          p.fill(190, 201, 186, 100);
          p.rect(x, y, l, h);
          p.rect(x + 25, y + 75, l - 200, 20);
          p.fill(16,91,99, 200);
          p.rect(x + 25, y + 75, (l - 200)/3, 20);
          p.fill(219,158,54,200);
          p.rect(x + 25 + 120/3, y + 75, (l - 200)/3, 20);
          p.fill(189,73,50, 200);
          p.rect(x + 25 + 240/3, y + 75, (l - 200)/3, 20);
          p.fill( 0 );
          p.text( '-', x + 5, y + 76);
          p.text( '+', x + 155, y + 76);
          if ( ! Application.session.isBiweight() ) {
            p.text( "facteur de puissance", x + 7, y + 12);
            p.line(x + 165, y + 23, x + l - 10, y + 23);
          }
          p.text( "rayon de lissage (m)", x + 7 , y + 47);
          p.line(x + 165, y + 58, x + l - 10, y + 58);
          Application.session.getCurseur().draw();
          Application.session.setDmax(Application.session.getCurseur().getCurs());
          if ( ! Application.session.isBiweight() ) {
            Application.session.getCurseur2().draw();
            Application.session.setP(Application.session.getCurseur2().getCurs());
          }
          Smooth.miseAJourCarteLissee();
          PFont font2 = p.createFont("DejaVuSans-ExtraLight-", 15);
          p.textFont(font2);
          p.textAlign(PConstants.CENTER);
          if ( Application.session.isBiweight() ) {
            p.fill( 255 );
            p.text( "DENSITE D'OCCUPATION DES BTS ", p.width/2, 60);
            p.text( "Méthode de BIWEIGHT", p.width/2, 80 );
          } else if ( Application.session.isShepard() ){
            p.fill( 255 );
            p.text( "DENSITE D'OCCUPATION DES BTS ", p.width/2, 60);
            p.text( "Méthode de SHEPARD", p.width/2, 80 );
          }
        } else {
          int c = p.color(16,91,99);
          p.fill(c, 100);
          p.rect(0, 0, p.width, p.height);
        }
    }


    public static void afficheLegendeNodeEdge(){
      PApplet p = Application.session.getPApplet();
      float x = p.width/70;
      float y = (float) (p.height/1.8);
      float h = (float) (p.height/9.79);
      float l = p.width/8;  
      p.strokeWeight(2);
      p.stroke(10, 150);
      p.fill(190, 201, 186, 100);
      p.rect( x, y, l, h ); // rectangle de base
      p.fill(101, 157, 255);
      p.rect( x, y, (float)(l/11.6), (float)(l/11.6) );
      if ( ! Application.session.isPetit() ){
        p.fill(255, 227, 0);
      } else {
        p.fill(140,29, 20);
      }
      p.rect( (float)(x + l - l/11.6), (float)(y + h - l/11.6), (float)(l/11.6), (float)(l/11.6) ); 
      p.fill(190, 201, 186, 100);
      p.line( x + l/2, y + 10,  x + l/2, y + 90);
      p.stroke( 255 );
      p.ellipseMode(PConstants.RADIUS);
      p.fill(101, 157, 255, 100);
      p.ellipse(x + 35, y + 25,15,15);
      p.ellipse(x + 35, y + 70,1,1);
      if ( ! Application.session.isPetit() ){
        p.fill(255, 227, 0);
      } else {
        p.fill(140,29, 20);
      }
      p.rect( x + 115, y + 20, 40, 10);
      p.rect( x + 115, y + 65, 40, 1); 
      p.fill(255);
      p.textAlign(PConstants.CENTER);
      PFont font2 = p.createFont("DejaVuSans-ExtraLight-", 20);
      p.textFont(font2);
      if (( ! Application.session.isLog() ) && ( Application.session.isEdge() || Application.session.isNode() ) && ( ! Application.session.isPetit() ) && ( ! Application.session.isChaud() )){
        p.text( "Distribution Brute ( par plage de 4h )", p.width/2, 60);
      } else if ( Application.session.isLog() && Application.session.isEdge() && ( ! Application.session.isPetit() ) && ( ! Application.session.isChaud() )){
        p.text( "Distribution Logarithmique ( par plage de 4h )", p.width/2, 60);
      } else if ( Application.session.isEdge() && ( ! Application.session.isLog() ) && Application.session.isPetit() && ( ! Application.session.isChaud() )){
        p.text( "Distribution en exp(1/x) ( par plage de 4h )", p.width/2, 60);
      } else if ( Application.session.isNode() && Application.session.isChaud() ) {
        p.text( "Représentation en HeatMap ( par plage de 4h )", p.width/2, 60);
      }
      PFont font3 = p.createFont("DejaVuSans-ExtraLight-", 15);
      p.textFont(font3);
      if ( Application.session.isEdge() && (! Application.session.isPetit()) && ( ! Application.session.isChaud() ) && ( ! Application.session.isDyn() ) ) {
       p.text( "Mise en avant des Arcs de poids fort ( variation sur l'épaisseur )", p.width/2, 80 );
      } if ( Application.session.isEdge() && (! Application.session.isPetit()) && ( ! Application.session.isChaud() ) &&  Application.session.isDyn() ) {
       p.text( "Mise en avant des Arcs de poids fort VISIBLES à l'écran ( variation sur l'épaisseur )", p.width/2, 80 );  
      } else if ( Application.session.isEdge() && ( ! Application.session.isLog() ) && Application.session.isPetit() && ( ! Application.session.isChaud() ) ) {
       p.text( "Mise en avant des Arcs de courte longueur ( variation sur l'oppacité, on conserve par ailleur la variation d'épaisseur )", p.width/2, 80 );
      } else if ( Application.session.isNode() && Application.session.isChaud() ) {
       p.text( "Mise en avant des points de forte densité ( gradiant sur le poids des noeuds )", p.width/2, 80 );
      }
      p.fill(0);
      PFont font1 = p.createFont("DejaVuSans-ExtraLight-", 15);
      p.textFont(font1); 
      if (( ! Application.session.isDyn() ) && ( ! Application.session.isLog() )){
        p.text((int)Application.session.getEdgeMax(),x + 135, y + 50);
        p.text((int)Application.session.getEdgeMin(),x + 135, y + 85);
      } else if ( Application.session.isDyn() && ( ! Application.session.isLog() )) {
        p.text((int)Application.session.getEdgeMaxdyn(), x + 135, y + 50);
        p.text((int)Application.session.getEdgeMindyn(),x + 135, y + 85);
      }
      if (( ! Application.session.isDyn() ) && ( Application.session.isLog() )){
        p.text((int)PApplet.log(Application.session.getEdgeMax()),x + 135, y + 50);
        p.text(0,x + 135, y + 85);
      } else if ( Application.session.isDyn() && ( Application.session.isLog() )) {
        p.text((int)PApplet.log(Application.session.getEdgeMaxdyn()), x + 135, y + 50);
        p.text(0,x + 135, y + 85);
      }
      p.text((int)Application.session.getNodeMax(),x + 35,y + 60);
      p.text((int)Application.session.getNodeMin(),x + 35,y + 90);

      if ( Application.session.isNodeDistri() )
        afficheDistributionNode(x,y,l,h);
      if ( Application.session.isEdgeDistri() )
        afficheDistributionEdge(x,y + 100 + 175,l,h);
    }


    public static void afficheDistributionNode(float x, float y, float l, float h){
      PApplet p = Application.session.getPApplet();
      p.strokeWeight(2);
      p.stroke(10, 150);
      p.fill(224);
      p.rect( x, y - 175, l, h + 75 );
      p.fill(101, 157, 255);
      p.rect( (float)x, (float)(y - l/11.6), (float)(l/11.6), (float)(l/11.6) );
      p.fill(224);
      p.noStroke();
      p.fill(255);
      p.rect( x + 25, y - 155, l - 35, h + 25);
      p.strokeWeight(1);
      p.stroke(0);
      PFont font2 = p.createFont("DejaVuSans-ExtraLight-", 8);
      p.textFont(font2); 
      p.fill(0);
      for ( int j = 0; j < 4; j ++ ) { 
        p.line( x + 23, y - 155 + 15 + j*( l - 35 )/4, x + 26, y - 155 + 15 + j*( l - 35 )/4); 
        int yLabel = 336 - j*112;
        p.text ( yLabel, x + 10, y - 155 + 15 + j*( l - 35 )/4 );
      }
      for ( int k = 0; k < 4; k ++ ) {
        float xLabel = PApplet.map(PApplet.log(1*PApplet.pow(10,k)), 0, PApplet.log(Application.session.getNodeMax()), 0, 130 );
        p.line( x + 25 + 4 + xLabel, y - 31,x + 25 + 4 + xLabel, y - 28 );
        p.text ( (int)PApplet.pow(10,k), x + 25 + 4 + xLabel, y - 18 );
      }
      p.strokeWeight(2);  
      float[] temp = new float[(int)Application.session.getMaxNodeTotal()];
      int comp = 1;
      temp[0] = Application.session.getMatNode(2,0);
      temp[1] = comp;
      p.fill(0);
      PFont font1 = p.createFont("DejaVuSans-ExtraLight-", 12);
      p.textFont(font1); 
      p.text( " effectif ", x + 30, y - 160 );
      p.text( " poids ", x + 82, y - 5 );
      for ( int i = 1; i < Application.session.getTableauGephi()[Application.session.getIndex()].nodeCount; i++ ) {
       if ( Application.session.getMatNode(2,i) != temp[0] ){ 
         drawStick(temp, x, y);
         temp[0] = Application.session.getMatNode(2,i);
         comp = 1;
         temp[1] = comp;
       } else {
         comp ++;
         temp[1] = comp;
       }
     }
    }

    public static void afficheDistributionEdge(float x, float y, float l, float h){
      PApplet p = Application.session.getPApplet();
      p.strokeWeight(2);
      p.stroke(10, 150);
      p.fill(224);
      p.rect( x, y - 175, l, h + 75 );
      if( ! Application.session.isPetit() ) { 
        p.fill(255, 227, 0);
      } else { 
        p.fill(140,29, 20);
      } 
      p.rect( (float)(x + l - l/11.6), (float)(y - h - 75), (float)(l/11.6), (float)(l/11.6) );
      p.noStroke();
      p.fill(255);
      p.rect( x + 25, y - 155, l - 35, h + 25);
      p.strokeWeight(1);
      p.stroke(0);
      PFont font2 = p.createFont("DejaVuSans-ExtraLight-", 8);
      p.textFont(font2); 
      p.fill(0);
      for ( int j = 0; j < 4; j ++ ) {  
        float yLabel = 0;
        if ( Application.session.isLog() ) {
          yLabel = PApplet.map(j*( l - 35 )/4, 0, (l - 35), 0, PApplet.log(80) );
          p.text ( yLabel, x + 10, y - 155 + 15 + (3 - j)*( l - 35 )/4 );

        } else if ( Application.session.isPetit() ) {
          yLabel = 112374/4*j;
          p.text ( (int)yLabel, x + 10,  y - 155 + 15 + (3 - j)*( l - 35 )/4 );
        } else {
          yLabel = 80 - 20*(j + 1);
          p.text ( (int)yLabel, x + 10, y - 155 + 15 + j*( l - 35 )/4 );
        }
        p.line( x + 23, y - 155 + 15 + j*( l - 35 )/4, x + 26, y - 155 + 15 + j*( l - 35 )/4); 
      }
      for ( int k = 0; k < 4; k ++ ) {
        float xLabel = 0;
        xLabel = PApplet.map(PApplet.log(1*PApplet.pow(10,k)), 0, PApplet.log(Application.session.getEdgeMax()), 0, 130 );
        if ( Application.session.isLog() ){
          p.text ( PApplet.log(5250*k + 1), x + 25 + 4 + xLabel, y - 18 );
        } else if ( ! Application.session.isPetit() ) {
          float bob = 1*PApplet.pow(10,k);
          p.text ( (int)bob, x + 25 + 4 + xLabel, y - 18 );
        }
        if ( ! Application.session.isPetit() )
          p.line( x + 25 + 4 + xLabel, y - 31,x + 25 + 4 + xLabel, y - 28 );

      }
      p.strokeWeight(2);  

      float[] temp = new float[(int)Application.session.getMaxEdgeTotal()]; // répartition poids 
      int comp = 1;
      temp[0] = Application.session.getMatEdge(4,0);
      temp[1] = comp;

      p.fill(0);
      PFont font1 = p.createFont("DejaVuSans-ExtraLight-", 12);
      p.textFont(font1); 
      if (( ! Application.session.isLog() ) && Application.session.isEdge() && ( ! Application.session.isPetit() ) ) {
        p.text( " effectif ", x + 30, y - 160 );
        p.text( " poids ", x + 82, y - 5 );
      } else if ( Application.session.isLog() && Application.session.isEdge() && ( ! Application.session.isPetit() ) ){
        p.text( " log(effectif) ", x + 40, y - 160 );
        p.text( " log(poids) ", x + 82, y - 5 );
      } else if ( Application.session.isEdge() && ( ! Application.session.isLog() ) && Application.session.isPetit() ){
        p.text( " distance (m) ", x + 40, y - 160 );
        p.text( " Arcs triés ", x + 82, y - 5 );
      }

      // pour le poids
      if ( ! Application.session.isPetit() ) {
        for ( int i = 1; i < Application.session.getTableauGephi()[Application.session.getIndex()].edgeCount; i++ ) {
         if ( Application.session.getMatEdge(4,i) != temp[0] ){ 
           drawStickBis(temp, x, y);
           temp[0] = Application.session.getMatEdge(4,i);
           comp = 1;
           temp[1] = comp;
         } else {
           comp ++;
           temp[1] = comp;
         }
        }
      }

      // pour les distances
      if ( Application.session.isPetit() ) {
        for ( int i = 1; i < Application.session.getTableauGephi()[Application.session.getIndex()].edgeCount; i++ ) {
          drawStickDistBis( Application.session.getTabEdgeDist(), x, y, i );
        }
      }

    }


    public static void drawStick(float [] temp, float X, float Y){
        PApplet p = Application.session.getPApplet();
        p.stroke(0);
        float x = PApplet.map( PApplet.log(temp[0]), 0, PApplet.log(Application.session.getNodeMax()), 0, 130 );
        float y = PApplet.map( temp[1], 0, 450, 0, 150 );
        p.line ( X + 20 + x, Y - 35, X + 20 + x, Y - 35 - y );
        p.noStroke();
      } 

    public static void drawStickBis(float [] temp, float X, float Y){  
        PApplet p = Application.session.getPApplet();
        p.stroke(0);
        float y = 0;
        float x = PApplet.map( PApplet.log(temp[0]+1), 0, PApplet.log(Application.session.getEdgeMax()), 0, 130 );
        if ( ! Application.session.isLog() ) {
          y = PApplet.map( temp[1], 0, 80, 0, 130 );
        } else {
          y = PApplet.map( PApplet.log(temp[1]), 0, PApplet.log (80), 0, 125 );
        } 
        p.line ( X + 20 + x, Y - 35, X + 20 + x, Y - 35 - y );
        p.noStroke();
    }

    public static void drawStickDistBis(float[] tabEdgeDist, float X, float Y, int i){
        PApplet p = Application.session.getPApplet();
        p.stroke(0);
        float y = 0;
        float x = PApplet.map( i, 0, Application.session.getTableauGephi()[Application.session.getIndex()].edgeCount, 0, 130 );
        y = PApplet.map( tabEdgeDist[i], 0, Application.session.getDistMax(), 0, 120 );
        p.line ( X + 30 + x, Y - 35, X + 30 + x, Y - 35 - y );
        p.noStroke();
    }

}