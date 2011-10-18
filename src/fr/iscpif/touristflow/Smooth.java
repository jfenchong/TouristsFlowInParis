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
public class Smooth {
    
    public static int coefZoom () { // en fonction du zoom la grille d'observation est plus ou moins fine 
      int k = 0;
      if ( Application.session.getMap().getZoom() >= 4096 ) {
        k = 3;
      } else {
        k = 4;
      }
      return k;
    }


    public static void lissage() {
      PApplet p = Application.session.getPApplet();
      int k = coefZoom();
      for (int i = 0; i < p.width; i = i + k) {//l'écran est découpé en petits carrés
        for(int j = 0; j < p.height; j = j + k) { 
          float poids;
          if ( Application.session.isBiweight() ) {
            poids = Biweight(i, j);//Utilisation de la méthode de Biweight
          } else {
            poids = Shepard(i, j);//Utilisation de la méthode de Biweight
          }
          p.noStroke();
          float percent = 0;
          percent = PApplet.norm(  poids, 1, Application.session.getNodeMax() );//On attribut une color à nos petits carrés en fonction du résultat de la méthode
          if ( percent > 0.16 ){
            int c = p.color(189,73,50);
            p.fill(c, 100);
            p.rect( i, j, k, k);
          } 
          else if ( percent > 0.12){
            int c = p.color(219,158,54);
            p.fill(c, 100);
            p.rect( i, j, k, k);
          }
          else if ( percent > 0.8 ){
            int c = p.color(255,211,78);
            p.fill(c, 100);
            p.rect( i, j, k, k);
          }
          else if ( percent > 0.2 ){
            int c = p.color(255,250,213);
            p.fill(c, 100);
            p.rect( i, j, k, k);
          }
          else {
            int c = p.color(16,91,99);
            p.fill(c, 100);
            p.rect( i, j, k, k);
          }
        }
      }
    }

    public static void miseAJourCarteLissee(){
      PApplet p = Application.session.getPApplet();
      Application.session.setNodePourLissageCount(0);
      Application.session.setNodePourLissageHold(Application.session.getNodePourLissage());
      Application.session.setNodePourLissage(new float[3][Application.session.getTableauGephi()[Application.session.getIndex()].nodeCount]);
      for ( int i = 0; i < Application.session.getTableauGephi()[Application.session.getIndex()].nodeCount; i++) {
        Location l = new Location(Application.session.getMatNode(0,i),Application.session.getMatNode(1,i));
        float xy[] = Application.session.getMap().getScreenPositionFromLocation(l);
        if ( Application.session.getMap().getZoom() > 16384 ) {
          if (( xy[0] < p.width + p.width ) && ( xy[1] < p.height + p.height ) && ( xy[0] > 0 - p.width ) && ( xy[1] > 0 - p.height ) ){ // nous travaillons sur un tableau regroupant les BTS visible à l'écran et non tout ceux visibles sur la carte
            Application.session.setNodePourLissage(0,Application.session.getNodePourLissageCount(),xy[0]);
            Application.session.setNodePourLissage(1,Application.session.getNodePourLissageCount(),xy[1]);
            Application.session.setNodePourLissage(2,Application.session.getNodePourLissageCount(),Application.session.getMatNode(2,i));
            Application.session.setNodePourLissageCount(Application.session.getNodePourLissageCount() + 1);
          }
        } else if ( Application.session.getMap().getZoom() > 8192 ) {
          if (( xy[0] < p.width + p.width/2 ) && ( xy[1] < p.height + p.height/2 ) && ( xy[0] > 0 - p.width/2 ) && ( xy[1] > 0 - p.height/2 ) ){ // nous travaillons sur un tableau regroupant les BTS visible à l'écran et non tout ceux visibles sur la carte
            Application.session.setNodePourLissage(0,Application.session.getNodePourLissageCount(),xy[0]);
            Application.session.setNodePourLissage(1,Application.session.getNodePourLissageCount(),xy[1]);
            Application.session.setNodePourLissage(2,Application.session.getNodePourLissageCount(),Application.session.getMatNode(2,i));
            Application.session.setNodePourLissageCount(Application.session.getNodePourLissageCount() + 1);
          }
        } else {
          if (( xy[0] < p.width + p.width/3 ) && ( xy[1] < p.height + p.height/3) && ( xy[0] > 0 - p.width/3 ) && ( xy[1] > 0 - p.height/3 ) ){ // nous travaillons sur un tableau regroupant les BTS visible à l'écran et non tout ceux visibles sur la carte
            Application.session.setNodePourLissage(0,Application.session.getNodePourLissageCount(),xy[0]);
            Application.session.setNodePourLissage(1,Application.session.getNodePourLissageCount(),xy[1]);
            Application.session.setNodePourLissage(2,Application.session.getNodePourLissageCount(),Application.session.getMatNode(2,i));
            Application.session.setNodePourLissageCount(Application.session.getNodePourLissageCount() + 1);
          }
        }
      }
      Bibliotheque.meter2Pixel();
    }

    public static float Biweight (int i, int j) {
      PApplet p = Application.session.getPApplet();
      float sum1 = 1;
      float sum2 = 1;
      for(int k = 0; k < Application.session.getNodePourLissageCount() - 1; k++) {
        if ( Application.session.getMap().getZoom() <= 4096 ) {
          if ( Application.session.getNodePourLissage(2,k) > 10 ) {
            float d = PApplet.dist(Application.session.getNodePourLissage(0,k), Application.session.getNodePourLissage(1,k), i, j);

            if ( d < Application.session.getDmaxOnScreen() ) {
              float tmp1 = PApplet.sq( 1 - PApplet.sq( d / Application.session.getDmaxOnScreen() ));
              sum1 = sum1 + tmp1*Application.session.getNodePourLissage(2,k);
              sum2 = sum2 + tmp1;
            }
          }
        } else {
          float d = PApplet.dist(Application.session.getNodePourLissage(0,k), Application.session.getNodePourLissage(1,k), i, j);
            if ( d < Application.session.getDmaxOnScreen() ) {
              float tmp1 = PApplet.sq( 1 - PApplet.sq( d / Application.session.getDmaxOnScreen() ));
              sum1 = sum1 + tmp1*Application.session.getNodePourLissage(2,k);
              sum2 = sum2 + tmp1;
            }
        }
      }
          p.noStroke();
          float poids = sum1 / sum2;
          return poids;
    }

    public static float Shepard( int i, int j) { 
      PApplet p = Application.session.getPApplet();
      float sum1 = 1;
      float sum2 = 1;
      for(int k = 0; k < Application.session.getNodePourLissageCount() - 1; k++) {
            float d = PApplet.dist(Application.session.getNodePourLissage(0,k), Application.session.getNodePourLissage(1,k), i, j);
            if ( d < Application.session.getDmaxOnScreen() ) {
              float tmp1 = 1/PApplet.pow( d, Application.session.getP());
              sum1 = sum1 + tmp1*Application.session.getNodePourLissage(2,k);
              sum2 = sum2 + tmp1;
            }
          }
          p.noStroke();
          float poids = sum1 / sum2;
          return poids;

    }
    
}