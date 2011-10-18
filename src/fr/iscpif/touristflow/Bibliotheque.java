/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.iscpif.touristflow;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.*; 

/**

 * 
 * 
 * 
 * @author jbilcke
 */
public class Bibliotheque {

   static void loadGraph () {
      Application.session.setTableauGephi(0, new Gephi()); 
      Application.session.getTableauGephi()[0].loadGraph("/home/guest/Bureau/quentin/gexf_julie_20080818/roaming_2009_03_31-custom-0-4.gexf");
      Application.session.setTableauGephi(1, new Gephi());
      Application.session.getTableauGephi()[1].loadGraph("/home/guest/Bureau/quentin/gexf_julie_20080818/roaming_2009_03_31-custom-4-8.gexf");
      Application.session.setTableauGephi(2, new Gephi());
      Application.session.getTableauGephi()[2].loadGraph("/home/guest/Bureau/quentin/gexf_julie_20080818/roaming_2009_03_31-custom-8-12.gexf");
      Application.session.setTableauGephi(3, new Gephi()); 
      Application.session.getTableauGephi()[3].loadGraph("/home/guest/Bureau/quentin/gexf_julie_20080818/roaming_2009_03_31-custom-12-16.gexf");
      Application.session.setTableauGephi(4, new Gephi()); 
      Application.session.getTableauGephi()[4].loadGraph("/home/guest/Bureau/quentin/gexf_julie_20080818/roaming_2009_03_31-custom-16-20.gexf");
      Application.session.setTableauGephi(5, new Gephi());
      Application.session.getTableauGephi()[5].loadGraph("/home/guest/Bureau/quentin/gexf_julie_20080818/roaming_2009_03_31-custom-20-24.gexf");
      Application.session.setIndex(0);
    }

    static void maxEdgeTot(int i){ 
      if (i > Application.session.getMaxEdgeTotal())
        Application.session.setMaxEdgeTotal(i);
    }

    static void maxNodeTot(int i){
      if (i > Application.session.getMaxNodeTotal())
        Application.session.setMaxNodeTotal(i);
    }
   
   // Calcul du poids min et max des edges visibles à l'écran
   static void MinMax() {
      int compt = 0;
      
      int width = Application.session.getPApplet().width;
      int height = Application.session.getPApplet().height;
      for (int i = 0; i < Application.session.getTableauGephi()[Application.session.getIndex()].edgeCount; i++) {
        Location l1 = new Location(Application.session.getMatEdge(0, i),Application.session.getMatEdge(1, i));
        Location l2 = new Location(Application.session.getMatEdge(2, i),Application.session.getMatEdge(3, i));
        float xy1[] = Application.session.getMap().getScreenPositionFromLocation(l1);
        float xy2[] = Application.session.getMap().getScreenPositionFromLocation(l2);
        if ( ( ( (xy1[1] > 0 ) && (xy1[0] > 0 ) && (xy1[0] < width ) && ( xy1[1] < height ) ) || ( (  xy2[0] > 0 ) && (  xy2[1] > 0 ) && (  xy2[0] < width ) && (  xy2[1] < height ) ) ) && ( compt < 2000 ) ) { // on filtre en affichant uniquement les 2000 liens les plus forts situés dans la zone de viz
            float value = Application.session.getMatEdge(4, i);
            if (value > Application.session.getEdgeMaxdyn()) {
                Application.session.setEdgeMaxdyn(value);
            }
            if (value < Application.session.getEdgeMindyn()) {
                Application.session.setEdgeMindyn(value);
            }
            compt ++;
          }
      }
    }
   
   // calcule la distance entre deux points dont on ne connait que la lat/long
   static float distFrom(float lat1, float lng1, float lat2, float lng2) { 
        float earthRadius = (float) 3958.75;
        float dLat = PApplet.radians(lat2-lat1);
        float dLng = PApplet.radians(lng2-lng1);
        float a = PApplet.sin(dLat/2) * PApplet.sin(dLat/2) 
                + PApplet.cos(PApplet.radians(lat1)) * PApplet.cos(PApplet.radians(lat2)) * PApplet.sin(dLng/2) *PApplet.sin(dLng/2);
        float c = 2 * PApplet.atan2(PApplet.sqrt(a), PApplet.sqrt(1-a));
        float dist = earthRadius * c;
        int meterConversion = 1609;
        float d = (float)dist * (float)meterConversion;
        return d;
    }

    // calcule une valeur étalon permettant de passer de la distance en pixel à la distance en mêtre
    static void meter2Pixel() {
        Location l1 = new Location((float)48.895, (float)2.283);
        Location l2 = new Location((float)48.878, (float)2.436);
        float xy1[] = Application.session.getMap().getScreenPositionFromLocation(l1);
        float xy2[] = Application.session.getMap().getScreenPositionFromLocation(l2);
        float distReel = distFrom(l1.getLat(), l1.getLon(), l2.getLat(), l2.getLon());
        float distScreen = PApplet.dist( xy1[0], xy1[1], xy2[0], xy2[1] );
        Application.session.setDmaxOnScreen( Application.session.getDmax()*distScreen/distReel); 
    }

    // détermine l'edge de plus grande et plus courte taille
    static void distMinMax(){ 
       for (int i = 0; i < Application.session.getTableauGephi()[Application.session.getIndex()].edgeCount; i++) { 
          Location l1 = new Location(Application.session.getMatEdge(0,i),Application.session.getMatEdge(1,i));
          Location l2 = new Location(Application.session.getMatEdge(2,i),Application.session.getMatEdge(3,i));
          float distance = distFrom( l1.getLat(), l1.getLon(), l2.getLat(), l2.getLon() );
        if (distance > Application.session.getDistMax())
          Application.session.setDistMax(distance);
        if (distance < Application.session.getDistMin())
          Application.session.setDistMin(distance);
       }
    }
    
    // à chaque changement d'interval, les données sont stockées de l'objet Gephi vers ces 2 matrices, les actions de filtrages et traitements se feront sur ces matrices
    static void remplirTableauImage(int index){  
      for ( int i = 0; i < Application.session.getTableauGephi()[Application.session.getIndex()].edgeCount; i++) {
        Application.session.setMatEdge(0, i, (float)Application.session.getTableauGephi()[Application.session.getIndex()].edge[i][1]);
        Application.session.setMatEdge(1, i, (float)Application.session.getTableauGephi()[Application.session.getIndex()].edge[i][0]);
        Application.session.setMatEdge(2, i, (float)Application.session.getTableauGephi()[Application.session.getIndex()].edge[i][3]);
        Application.session.setMatEdge(3, i, (float)Application.session.getTableauGephi()[Application.session.getIndex()].edge[i][2]);
        Application.session.setMatEdge(4, i, (float)Application.session.getTableauGephi()[Application.session.getIndex()].edge[i][4]);
      }
      TriRapide.trirapide( Application.session.getMatEdge(), (int)Application.session.getTableauGephi()[Application.session.getIndex()].edgeCount, 5 );
       
      for ( int i = 0; i < Application.session.getTableauGephiCount(Application.session.getIndex(), 0); i++ ) {
        Application.session.setMatNode(0, i, (float)Application.session.getTableauGephiNode(Application.session.getIndex(), 0, i) );
        Application.session.setMatNode(1, i, (float)Application.session.getTableauGephiNode(Application.session.getIndex(), 1, i) );
        Application.session.setMatNode(2, i, (float)Application.session.getTableauGephiNode(Application.session.getIndex(), 2, i) );
      }
      TriRapide.trirapide( Application.session.getMatNode(), (int)Application.session.getTableauGephi()[Application.session.getIndex()].nodeCount, 3 );
      
      Application.session.setIndexBis(index);
    }

    static void miseAJourMatriceDistance(int index) {
      Application.session.setTabEdgeDist(new float[(int)Application.session.getMaxEdgeTotal()]);
        for ( int j = 0; j < Application.session.getTableauGephi()[Application.session.getIndex()].edgeCount; j++ ) {
          Application.session.setTabEdgeDist(j, distFrom(Application.session.getMatEdge( 0, j), Application.session.getMatEdge( 1, j), Application.session.getMatEdge( 2, j), Application.session.getMatEdge( 3, j)));
        }

      TriRapide.trirapide2( Application.session.getTabEdgeDist(), (int)Application.session.getTableauGephi()[Application.session.getIndex()].edgeCount); 
    }

    static void maxNbRepartitionEdge(int comp){
      if ( comp > Application.session.getCompMaxEdge() ) {
        Application.session.setCompMaxEdge(comp);
      }
    }
    
}