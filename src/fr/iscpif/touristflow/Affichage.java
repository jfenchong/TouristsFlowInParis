/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.iscpif.touristflow;

import processing.core.*;
import de.fhpotsdam.unfolding.geo.Location;

/**
 *
 * @author Quentin Lobbé
 */
public class Affichage {

    public static void selection(float x, float y, float radius, int i) { // fonction d'affichage en mode selection 
        PApplet p = Application.session.getPApplet();
        p.fill(190, 201, 186, 100);
        p.rect(0, 0, p.width, p.height);
        p.textAlign(PConstants.LEFT, PConstants.TOP);
        p.stroke(153);



        p.fill(16, 91, 136);
        p.text("Arc Entrant", p.width / 56, (float) (p.height / 1.958));


        p.fill(182, 92, 96);
        p.stroke(153);
        p.text("Arc Sortant", p.width / 56, (float) (p.height / 2.06));
        p.textAlign(PConstants.CENTER);
        
        for (int k = 0; k < Application.session.getTableauGephi()[Application.session.getIndex()].edgeCount; k++) {
            Location l1 = new Location(Application.session.getMatEdge(0, k), Application.session.getMatEdge(1, k));
            Location l2 = new Location(Application.session.getMatEdge(2, k), Application.session.getMatEdge(3, k));
            float xy1[] = Application.session.getMap().getScreenPositionFromLocation(l1);
            float xy2[] = Application.session.getMap().getScreenPositionFromLocation(l2);
            if (Application.session.getMatEdge(4, k) > 1) {
                float poids = PApplet.map(PApplet.log(Application.session.getMatEdge(4, k)), 0, PApplet.log(Application.session.getEdgeMax()), 1, 15);
                p.strokeWeight(poids);
                if ((x == xy1[0]) && (y == xy1[1])) {

                    p.stroke(182, 92, 96, 255);
                    p.line(xy1[0], xy1[1], xy2[0], xy2[1]);

                } else if ((x == xy2[0]) && (y == xy2[1])) {

                    // on dessine un arc bleu pour un lien sortant
                    p.stroke(16, 91, 136, 255);
                    p.line(xy1[0], xy1[1], xy2[0], xy2[1]);
                }
            }
        }
        p.noStroke();
        p.noFill();
        p.fill(1, 160, 20, 200);
        p.ellipse(x, y, radius, radius);

    }

    public static void selectionOursins(float x, float y, float xN, float yN) { // fonction d'affichage en mode selection 
        PApplet p = Application.session.getPApplet();
        p.textAlign(PConstants.LEFT, PConstants.TOP);
        p.stroke(153);
        int sortantcpt = 0;
        int entrantcpt = 0;
        Location l3 = new Location(x, y);
        float xy3[] = Application.session.getMap().getScreenPositionFromLocation(l3);
        for (int k = 0; k < Application.session.getTableauGephi()[Application.session.getIndex()].edgeCount; k++) {
            Location l1 = new Location(Application.session.getMatEdge(0, k), Application.session.getMatEdge(1, k));
            Location l2 = new Location(Application.session.getMatEdge(2, k), Application.session.getMatEdge(3, k));
            float xy1[] = Application.session.getMap().getScreenPositionFromLocation(l1);
            float xy2[] = Application.session.getMap().getScreenPositionFromLocation(l2);
            if (Application.session.getMatEdge(4, k) > 1) {

                if ((x == xy1[0]) && (y == xy1[1])) {


                    Application.session.setSortant(0, sortantcpt, xy1[0]);
                    Application.session.setSortant(1, sortantcpt, xy1[1]);
                    Application.session.setSortant(2, sortantcpt, xy2[0]);
                    Application.session.setSortant(3, sortantcpt, xy2[1]);
                    Application.session.setSortant(4, sortantcpt, (float)Application.session.getMatEdge(4, k));
                    Application.session.setSortant(5, sortantcpt, PApplet.atan2(xy2[0] - x, xy2[1] - y)); // angle par rapport à un axe vertical passant par l'horigine des points 
                    sortantcpt++;


                } else if ((x == xy2[0]) && (y == xy2[1])) {

                    Application.session.setEntrant(0, entrantcpt, xy1[0]);
                    Application.session.setEntrant(1, entrantcpt, xy1[1]);
                    Application.session.setEntrant(2, entrantcpt, xy2[0]);
                    Application.session.setEntrant(3, entrantcpt, xy2[1]);
                    Application.session.setEntrant(4, entrantcpt, (float)Application.session.getMatEdge(4, k));
                    Application.session.setEntrant(5, entrantcpt, PApplet.atan2(xy1[0] - x, xy1[1] - y)); // angle par rapport à un axe vertical passant par l'horigine des points 
                    entrantcpt++;

                }
            }
        }



        // nous créons un nouveau tableau qui contiendra les inforamtions agrégées de tous les points cardinaux
        float[] pointsCardinauxEntrant = new float[32];
        float[] pointsCardinauxSortant = new float[32];
        pointsCardinauxEntrant = Bibliotheque.remplissagePointsCardinaux(pointsCardinauxEntrant, entrantcpt, 1);
        pointsCardinauxSortant = Bibliotheque.remplissagePointsCardinaux(pointsCardinauxSortant, sortantcpt, 0);



        p.noStroke();
        p.noFill();

        boolean deja = false;
        for (int z = 0; z < Application.session.getOursins().size(); z++) {
            Oursin oursin = (Oursin) Application.session.Oursins.get(z);
            if ((oursin.getXN() == xN) && (oursin.getYN() == yN)) {
                deja = true;
                oursin.pressed();
            }
            //if ("selected".equals(oursin.getStatus())) {
                //Application.session.getOursins().remove(z);
                //z = z - 1;
            //}
        }
        if (!deja) {
            Application.session.getOursins().add(new Oursin(pointsCardinauxEntrant, pointsCardinauxSortant, x, y, xN, yN));
        }

    }

    public static void afficheEchelle() {
        PApplet p = Application.session.getPApplet();
        p.fill(0);
        p.stroke(0);
        p.strokeWeight(2);
        p.line((p.width + (float) (p.width / 1.4)) / 2 - 40, p.height - 45, (p.width + (float) (p.width / 1.4)) / 2 + 40, p.height - 45); // ligne de référence sur l'écran : 80 PConstants.PIxels
        p.line((p.width + (float) (p.width / 1.4)) / 2 - 42, p.height - 40, (p.width + (float) (p.width / 1.4)) / 2 - 42, p.height - 50);
        p.line((p.width + (float) (p.width / 1.4)) / 2 + 42, p.height - 40, (p.width + (float) (p.width / 1.4)) / 2 + 42, p.height - 50);
        Location location1 = Application.session.getMap().getLocationFromScreenPosition(p.width / 2, p.height / 2); // transformation des extrémités en coordonnées Lat/Lon 
        Location location2 = Application.session.getMap().getLocationFromScreenPosition(p.width / 2 + 80, p.height / 2);
        Application.session.setD(Bibliotheque.distFrom(location1.getLat(), location1.getLon(), location2.getLat(), location2.getLon())); // appel de la fonction de calcul de distance entre deux points
        p.textAlign(PConstants.CENTER);
        p.text((int) Application.session.getD() + " m", (p.width + (float) (p.width / 1.4)) / 2, p.height - 50);
    }

    public static void afficheLegendeLissee() {
        PApplet p = Application.session.getPApplet();
        float x = p.width / 56;
        float y = p.height - 150;
        float h = 100;
        float l = 320;

        if (Application.session.isBiweight() || Application.session.isShepard()) {
            Smooth.miseAJourCarteLissee();
            int i = 0;
            while (i != 500) {
                i++;
            }
            Smooth.lissage();
            p.textAlign(PConstants.LEFT, PConstants.TOP);
            p.strokeWeight(2);
            p.stroke(10, 150);
            p.fill(190, 201, 186, 100);
            p.rect(x, y, l, h);
            drawDegrade(x + 25, y + 75, l - 200, 20);
            p.strokeWeight(2);
            p.stroke(10, 150);
            p.rect(x + 25, y + 75, l - 200, 20);
            p.fill(0);
            p.text('-', x + 5, y + 76);
            p.text('+', x + 155, y + 76);
            if (!Application.session.isBiweight()) {
                p.text("facteur de puissance", x + 7, y + 12);
                p.line(x + 165, y + 23, x + l - 10, y + 23);
            }
            p.text("rayon de lissage (m)", x + 7, y + 47);
            p.line(x + 165, y + 58, x + l - 10, y + 58);
            Application.session.getCurseur().draw();
            Application.session.setDmax(Application.session.getCurseur().getCurs());
            if (!Application.session.isBiweight()) {
                Application.session.getCurseur2().draw();
                Application.session.setP(Application.session.getCurseur2().getCurs());
            }
            Smooth.miseAJourCarteLissee();
            PFont font2 = p.createFont("DejaVuSans-ExtraLight-", 15);
            p.textFont(font2);
            p.textAlign(PConstants.CENTER);
            if (Application.session.isBiweight()) {
                p.fill(255);
                p.text("DENSITE D'OCCUPATION DES BTS ", p.width / 2, (float) (p.height / 16.317));
                p.text("Méthode de BIWEIGHT", p.width / 2, (float) (p.height / 12.238));
            } else if (Application.session.isShepard()) {
                p.fill(255);
                p.text("DENSITE D'OCCUPATION DES BTS ", p.width / 2, (float) (p.height / 16.317));
                p.text("Méthode de SHEPARD", p.width / 2, (float) (p.height / 12.238));
            }
        } else {
            int c = p.color(16, 91, 99);
            p.fill(c, 100);
            p.rect(0, 0, p.width, p.height);
        }
    }

    public static void drawDegrade(float x, float y, float l, float h) {
        PApplet p = Application.session.getPApplet();
        int c1 = p.color(16, 91, 99);
        int c2 = p.color(194, 190, 201);
        int c4 = p.color(219, 158, 54);
        int c5 = p.color(189, 73, 50);
        int c = p.color(0);
        for (int i = 0; i < l; i++) {
            float d = PApplet.dist(i + x, 0, l + x, 0);
            d = l - d;

            if (i > l * 2 / 3) {
                float percent1 = PApplet.norm(d, 2 * l / 3, l);
                c = p.lerpColor(c4, c5, percent1);
                p.stroke(c);

            } else if (i > l / 3) {
                float percent2 = PApplet.norm(d, 1 * l / 3, 2 * l / 3);
                c = p.lerpColor(c2, c4, percent2);
                p.stroke(c);

            } else {
                float percent3 = PApplet.norm(d, 0, l / 3);
                p.stroke(c);
                c = p.lerpColor(c1, c2, percent3);
            }

            p.stroke(c);
            p.line(i + x, y, i + x, y + h);
            p.noStroke();
        }
    }

    public static void afficheLegendeNodeEdge() {
        PApplet p = Application.session.getPApplet();
        float x = p.width / 70;
        float y = p.height - 320;
        float h = 100;
        float l = 175;
        p.strokeWeight(2);
        p.stroke(10, 150);
        p.fill(190, 201, 186, 100);
        p.rect(x, y, l, h); // rectangle de base
        p.fill(182, 92, 96);
        p.rect(x, y, (float) (15), (float) (15));
        p.fill(16, 91, 136);
        p.rect((float) (x + l - 15), (float) (y + h - 15), (float) (15), (float) (15));
        p.fill(190, 201, 186, 100);
        p.line(x + l / 2, y + 10, x + l / 2, y + 90);
        p.stroke(255);
        p.ellipseMode(PConstants.RADIUS);
        p.fill(182, 92, 96);
        p.ellipse(x + 35, y + 25, 15, 15);
        p.ellipse(x + 35, y + 70, 1, 1);
        p.fill(16, 91, 136);
        p.rect(x + 115, y + 20, 40, 10);
        p.rect(x + 115, y + 65, 40, 1);
        p.fill(0);
        p.textAlign(PConstants.CENTER);
        PFont font2 = p.createFont("DejaVuSans-ExtraLight-", 20);
        p.textFont(font2);
        if ((!Application.session.isLog()) && (Application.session.isEdge() || Application.session.isNode()) && (!Application.session.isPetit()) && (!Application.session.isChaud())) {
            p.text("Distribution Brute ( par plage de 4h )", p.width / 2, (float) (p.height / 16.317));
        } else if (Application.session.isLog() && Application.session.isEdge() && (!Application.session.isPetit()) && (!Application.session.isChaud())) {
            p.text("Distribution Logarithmique ( par plage de 4h )", p.width / 2, (float) (p.height / 16.317));
        } else if (Application.session.isEdge() && (!Application.session.isLog()) && Application.session.isPetit() && (!Application.session.isChaud())) {
            p.text("Distribution en exp(1/x) ( par plage de 4h )", p.width / 2, (float) (p.height / 16.317));
        }
        PFont font3 = p.createFont("DejaVuSans-ExtraLight-", 15);
        p.textFont(font3);
        if (Application.session.isEdge() && (!Application.session.isPetit()) && (!Application.session.isChaud()) && (!Application.session.isBoxCox())) {
            p.text("Mise en avant des Arcs de poids fort ( variation sur l'épaisseur )", p.width / 2, (float) (p.height / 12.238));
        }
        if (Application.session.isEdge() && (!Application.session.isPetit()) && (!Application.session.isChaud()) && Application.session.isBoxCox()) {
            p.text("Transformation du poids par la méthode de box cox", p.width / 2, (float) (p.height / 12.238));
        } else if (Application.session.isEdge() && (!Application.session.isLog()) && Application.session.isPetit() && (!Application.session.isChaud())) {
            p.text("Mise en avant des Arcs de courte longueur ( variation sur l'oppacité, on conserve par ailleur la variation d'épaisseur )", p.width / 2, (float) (p.height / 12.238));
        }
        p.fill(0);
        PFont font1 = p.createFont("DejaVuSans-ExtraLight-", 15);
        p.textFont(font1);
        if ((!Application.session.isBoxCox()) && (!Application.session.isLog())) {
            p.text((int) Application.session.getEdgeMax(), x + 135, y + 50);
            p.text((int) Application.session.getEdgeMin(), x + 135, y + 85);
        } else if (Application.session.isBoxCox() && (!Application.session.isLog())) {
            if (CoxBox(Application.session.getEdgeMax(), 'e') >= 1000) {
                p.text(PApplet.nf((int) CoxBox(Application.session.getEdgeMax(), 'e'), 4), x + 135, y + 50);
            } else if (CoxBox(Application.session.getEdgeMax(), 'e') >= 100) {
                p.text(PApplet.nf(CoxBox(Application.session.getEdgeMax(), 'e'), 3, 1), x + 135, y + 50);
            } else if (CoxBox(Application.session.getEdgeMax(), 'e') >= 10) {
                p.text(PApplet.nf(CoxBox(Application.session.getEdgeMax(), 'e'), 2, 2), x + 135, y + 50);
            } else {
                p.text(PApplet.nf(CoxBox(Application.session.getEdgeMax(), 'e'), 1, 3), x + 135, y + 50);
            }
            p.text(0, x + 135, y + 85);
        }
        if ((!Application.session.isBoxCox()) && (Application.session.isLog())) {
            p.text((int) PApplet.log(Application.session.getEdgeMax()), x + 135, y + 50);
            p.text(0, x + 135, y + 85);
        }
        
        
        if ((!Application.session.isBoxCoxNode())) {
            p.text((int) Application.session.getNodeMax(), x + 35, y + 60);
            p.text((int) Application.session.getNodeMin(), x + 35, y + 90);
        } else if (Application.session.isBoxCoxNode()) {
            if (CoxBox(Application.session.getNodeMax(), 'n') >= 1000) {
                p.text(PApplet.nf((int) CoxBox(Application.session.getNodeMax(), 'n'), 4), x + 35, y + 60);
            } else if (CoxBox(Application.session.getNodeMax(), 'n') >= 100) {
                p.text(PApplet.nf(CoxBox(Application.session.getNodeMax(), 'n'), 3, 1), x + 35, y + 60);
            } else if (CoxBox(Application.session.getNodeMax(), 'n') >= 10) {
                p.text(PApplet.nf(CoxBox(Application.session.getNodeMax(), 'n'), 2, 2), x + 35, y + 60);
            } else {
                p.text(PApplet.nf(CoxBox(Application.session.getNodeMax(), 'n'), 1, 3), x + 35, y + 60);
            } 
            p.text(0, x + 35, y + 90);
        }


        if (Application.session.isNodeDistri()) {
            afficheDistributionNode(x, y, l, h);
        }
        if (Application.session.isEdgeDistri()) {
            afficheDistributionEdge(x, y + 100 + 175, l, h);
        }
        if (Application.session.isBoxCox() || Application.session.isBoxCoxNode()) {
            afficheBoxCox(x + 175, y + 100 + 175, l, h + 75);
        }
        PFont font5 = p.createFont("DejaVuSans-ExtraLight-", 12);
        p.textFont(font5);
    }

    public static void afficheLegendeHeatMap() {
        PApplet p = Application.session.getPApplet();
        float x = p.width / 70;
        float y = p.height - 320;
        float h = 100;
        float l = 175;
        p.strokeWeight(2);
        p.stroke(10, 150);
        p.fill(190, 201, 186, 100);
        p.rect(x, y, l, h); // rectangle de base
        p.noFill();

        int from = p.color(189, 73, 50);
        int to = p.color(255);
        for (int i = (int) x + 38; i < x + 38 + 100; i++) {
            float d = PApplet.dist(i, y, x + 38 + 100, y);
            d = 100 - d;
            float percent = PApplet.norm(d, 0, 100);
            int c = p.lerpColor(from, to, percent);
            p.stroke(c);
            p.line(i, y + 60, i, y + 80);
        }
        p.stroke(10, 150);
        p.rect(x + 38, y + 60, 100, 20);

        PFont font1 = p.createFont("DejaVuSans-ExtraLight-", 15);
        p.textFont(font1);
        p.fill(0);
        p.textAlign(PConstants.CENTER);
        p.text("-", x + 19, y + 75);
        p.text("+", x - 19 + l, y + 75);
        p.text("Degré des Noeuds", x + l / 2, y + 40);
        PFont font2 = p.createFont("DejaVuSans-ExtraLight-", 17);
        p.textFont(font2);
        if (Application.session.isNode() && Application.session.isChaud()) {
            p.text("Représentation en HeatMap du degré des Noeuds ( par plage de 4h )", p.width / 2, (float) (p.height / 16.317));
        }
        p.textFont(font1);
    }

    public static void afficheDistributionNode(float x, float y, float l, float h) {
        PApplet p = Application.session.getPApplet();
        p.strokeWeight(2);
        p.stroke(10, 150);
        p.fill(224);
        // rectangle gris de base
        p.rect(x, y - 175, l, h + 75);
        p.fill(182, 92, 96);
        // rectangle rouge clicable
        p.rect((float) x, (float) (y - l / 11.6), (float) (l / 11.6), (float) (l / 11.6));
        p.fill(224);
        p.noStroke();
        p.fill(255);
        // rectangle blanc 
        p.rect(x + 30, y - 155, l - 35, h + 25);

        // on se place dans le coin inférieur gauche du rectangle blanc 
        float x1 = x + 30;
        float y1 = y - 155 + h + 25;
        float l1 = l - 35;
        float h1 = h + 25;


        p.strokeWeight(1);
        p.stroke(0);
        PFont font2 = p.createFont("DejaVuSans-ExtraLight-", 8);
        p.textFont(font2);
        p.fill(0);

        // dessiner les graduations de l'axe des ordonnées
        //  436 correspond au nombre max de répartition pour un poids, on peut le retrouver grâce à la fonction effectif(String cas) de la bibliotheque
        for (int j = 0; j < 5; j++) {
            p.fill(10);
            float yLabel = PApplet.map(j * h1 / 4, 0, h1, 0, 436);
            p.text((int) yLabel, x1 - 17, y1 - j * h1 / 4 + 2);
            p.line(x1 - 3, y1 - j * h1 / 4, x1 + 2, y1 - j * h1 / 4);
        }
        // desssiner les graduations de l'axe des absisses
        for (int k = 0; k < 5; k++) {
            float xLabel = PApplet.map(k * l1 / 4, 0, l1, 0, Application.session.getNodeMax());
            p.line(x1 + k * l1 / 4, y1 - 3, x1 + k * l1 / 4, y1 + 3);
            p.text((int) xLabel, x1 + k * l1 / 4, y - 18);
        }

        p.strokeWeight(2);
        PFont font1 = p.createFont("DejaVuSans-ExtraLight-", 12);
        p.textFont(font1);
        p.text(" effectif ", x + 50, y - 160);
        p.text(" poids ", x + 82, y - 5);

        float[] temp = new float[(int) Application.session.getMaxNodeTotal()];
        int comp = 1;
        temp[0] = Application.session.getNodePoids(0);
        temp[1] = comp;
        p.fill(0);

        for (int i = 1; i < Application.session.getNodePoids().length; i++) {
            if (Application.session.getNodePoids(i) != temp[0]) {
                drawStick(temp, x1, y1, l1, h1);
                temp[0] = Application.session.getNodePoids(i);
                comp = 1;
                temp[1] = comp;
            } else {
                comp++;
                temp[1] = comp;
            }
        }

    }

    public static void afficheDistributionEdge(float x, float y, float l, float h) {
        // x et y représentent les coordo du coin inférieur gauche du rectangle gris
        PApplet p = Application.session.getPApplet();
        p.strokeWeight(2);
        p.stroke(10, 150);
        p.fill(224);
        // rectangle gris
        p.rect(x, y - 175, l, h + 75);

        p.fill(16, 91, 136);
        // rectangle clicable bleu ( en haut à droite du gris )
        p.rect((float) (x + l - 15), (float) (y - h - 75), (float) (15), (float) (15));
        p.noStroke();
        p.fill(255);
        // rectangle blanc
        p.rect(x + 30, y - 155, l - 35, h + 25);

        p.strokeWeight(1);
        p.stroke(0);
        PFont font2 = p.createFont("DejaVuSans-ExtraLight-", 8);
        p.textFont(font2);
        p.fill(0);

        // on se place maintenant dans le coin inférieur gauche du rectangle blanc
        float x1 = x + 30;
        float y1 = y - 155 + h + 25;
        float l1 = l - 35;
        float h1 = h + 25;

        // dessiner les graduations de l'axe des ordonnées
        for (int j = 0; j < 5; j++) {
            // 50631 correspond au nombre max de répartition pour un poids, on peut le retrouver grâce à la fonction effectif(String cas) de la bibliotheque 
            float yLabel = PApplet.map(j * h1 / 4, 0, h1, 0, 50631);
            p.fill(10);
            if (Application.session.isLog()) {
                yLabel = PApplet.map(j * h1 / 4, 0, h1, 0, PApplet.log(50631));
                p.text(yLabel, x1 - 17, y1 - j * h1 / 4 + 2);
            } else if (Application.session.isPetit()) {
                yLabel = 112374 / 4 * j;
                p.text((int) yLabel, x1 - 17, y1 - j * h1 / 4 + 2);
            } else {
                p.text((int) yLabel, x1 - 17, y1 - j * h1 / 4 + 2);
            }
            p.line(x1 - 3, y1 - j * h1 / 4, x1 + 2, y1 - j * h1 / 4);
        }

        // dessiner les graduations de l'axe des absisses ( représentation logarithmique )
        for (int k = 0; k < 4; k++) {
            float xLabel = 0;
            xLabel = PApplet.map(PApplet.log(1 * PApplet.pow(10, k)), 0, PApplet.log(Application.session.getEdgeMax()), 0, 130);
            if (Application.session.isLog()) {
                p.text(PApplet.log(5250 * k + 1), x + 25 + 4 + xLabel, y - 18);
            } else if (!Application.session.isPetit()) {
                float bob = 1 * PApplet.pow(10, k);
                p.text((int) bob, x + 25 + 4 + xLabel, y - 18);
            }
            if (!Application.session.isPetit()) {
                p.line(x + 25 + 4 + xLabel, y - 31, x + 25 + 4 + xLabel, y - 28);
            }
        }
        p.strokeWeight(2);

        p.fill(0);
        PFont font1 = p.createFont("DejaVuSans-ExtraLight-", 12);
        p.textFont(font1);
        if ((!Application.session.isLog()) && Application.session.isEdge() && (!Application.session.isPetit())) {
            p.text(" effectif ", x + 50, y - 160);
            p.text(" poids ", x + 82, y - 5);
        } else if (Application.session.isLog() && Application.session.isEdge() && (!Application.session.isPetit())) {
            p.text(" log(effectif) ", x + 60, y - 160);
            p.text(" log(poids) ", x + 82, y - 5);
        } else if (Application.session.isEdge() && (!Application.session.isLog()) && Application.session.isPetit()) {
            p.text(" distance (m) ", x + 60, y - 160);
            p.text(" Arcs triés ", x + 82, y - 5);
        }

        // pour le poids
        if (!Application.session.isPetit()) {
            float[] temp = new float[(int) Application.session.getMaxEdgeTotal()];
            int comp = 1;
            temp[0] = Application.session.getEdgePoids(0);
            temp[1] = comp;
            for (int i = 1; i < Application.session.getEdgePoids().length; i++) {
                if (Application.session.getEdgePoids(i) != temp[0]) {
                    drawStickBis(temp, x1, y1, l1, h1);
                    temp[0] = Application.session.getEdgePoids(i);
                    comp = 1;
                    temp[1] = comp;
                } else {
                    comp++;
                    temp[1] = comp;
                }
            }
        }

        // pour les distances
        if (Application.session.isPetit()) {
            for (int i = 1; i < Application.session.getTableauGephi()[Application.session.getIndex()].edgeCount; i++) {
                drawStickDistBis(Application.session.getTabEdgeDist(), x1, y1, i, l1, h1);
            }
        }

    }

    public static void drawStick(float[] temp, float X, float Y, float l, float h) {
        PApplet p = Application.session.getPApplet();
        p.stroke(0);
        if (temp[0] != 0) {
            float x = PApplet.map(temp[0], 0, Application.session.getNodeMax(), 0, l);
            float y = PApplet.map(temp[1], 0, 436, 0, h);
            p.line(X + x, Y, X + x, Y - y);
        }
        p.noStroke();
    }

    public static void drawStickBis(float[] temp, float X, float Y, float l, float h) {
        PApplet p = Application.session.getPApplet();
        p.stroke(0);
        p.strokeWeight(2);
        if (temp[0] != 0) {
            float y = 0;
            float x = PApplet.map(PApplet.log(temp[0]), 0, PApplet.log(Application.session.getEdgeMax()), 0, l);
            if (!Application.session.isLog()) {
                y = PApplet.map(temp[1], 0, 50631, 0, h);
            } else {
                y = PApplet.map(PApplet.log(temp[1]), 0, PApplet.log(50631), 0, h);
            }
            p.line(X + x, Y, X + x, Y - y);
        }
        p.noStroke();
    }

    public static void drawStickDistBis(float[] tabEdgeDist, float X, float Y, int i, float l, float h) {
        PApplet p = Application.session.getPApplet();
        p.stroke(0);
        float y = 0;
        float x = PApplet.map(i, 0, Application.session.getTableauGephi()[Application.session.getIndex()].edgeCount, 0, l);
        y = PApplet.map(tabEdgeDist[i], 0, 123242, 0, h);
        p.line(X + x, Y, X + x, Y - y);
        p.noStroke();
    }

    public static void afficheBoxCox(float x, float y, float l, float h) {
        PApplet p = Application.session.getPApplet();
        // box cox pour les edges
        if (Application.session.isBoxCox()) {
            p.strokeWeight(2);
            p.stroke(10, 150);
            p.fill(224);
            //rectangle  du curseur box cox edge
            p.rect(x, y - h - 50, l, 50);
            p.fill(10);
            PFont font2 = p.createFont("DejaVuSans-ExtraLight-", (float) 12);
            p.textFont(font2);
            p.text("lambda :", x + 30, y - h - 50 + 14);
            p.fill(16, 91, 136);
            p.rect(x + l - 15, y - h - 15, 15, 15);
            Application.session.getCurseur3().draw();
            Application.session.setLambdaE(Application.session.getCurseur3().getCurs());
            p.stroke(10, 150);
            if (Application.session.isEdgeBoxCoxDistri()) {
                p.fill(224);
                p.rect(x, y - h, l, h);
                p.fill(16, 91, 136);
                p.rect(x + l - 15, y - h, 15, 15);
                p.noStroke();
                p.fill(255);
                p.rect(x + 30, y - 155, l - 35, h - 50);
                distributionCoxBox(x + 30, y - 155 + h - 50, l - 35, h - 50);
            }
        }
        // box cox pour les nodes    
        if (Application.session.isBoxCoxNode()) {
            p.strokeWeight(2);
            p.stroke(10, 150);
            p.fill(224);
            //rectangle  du curseur box cox edge
            p.rect(x, p.height - 320, l, 50);
            p.fill(10);
            PFont font2 = p.createFont("DejaVuSans-ExtraLight-", (float) 12);
            p.textFont(font2);
            p.text("lambda :", x + 30, p.height - 320 + 14);
            p.fill(182, 92, 96);
            p.rect(x + l - 15, p.height - 320, 15, 15);
            Application.session.getCurseur4().draw();
            Application.session.setLambdaN(Application.session.getCurseur4().getCurs());
            p.stroke(10, 150);
            if (Application.session.isNodeBoxCoxDistri()) {
                p.fill(224);
                p.rect(x, p.height - 320 - l, l, h);
                p.fill(182, 92, 96);
                p.rect(x + l - 15, p.height - 320 - 15, 15, 15);
                p.noStroke();
                p.fill(255);
                p.rect(x + 30, p.height - 320 - 155, l - 35, h - 50);
                distributionCoxBoxNode(x + 30, p.height - 320 - 155 + h - 50, l - 35, h - 50);
            }
        }
    }

    public static void distributionCoxBoxNode(float x, float y, float l, float h) {
        PApplet p = Application.session.getPApplet();
        p.strokeWeight(1);
        p.stroke(10);
        PFont font2 = p.createFont("DejaVuSans-ExtraLight-", (float) 7.6);
        p.textFont(font2);
        for (int i = 0; i < 5; i++) {

            float yLabel = PApplet.map(i * h / 4, 0, h, 0, 436);
            p.line(x - 3, y - i * h / 4, x + 2, y - i * h / 4);
            yLabel = CoxBox(yLabel, 'n');
            p.fill(10);
            if (yLabel >= 1000) {
                p.text(PApplet.nf((int) yLabel, 4), x - 17, y - i * h / 4);
            } else if (yLabel >= 100) {
                p.text(PApplet.nf(yLabel, 3, 1), x - 17, y - i * h / 4);
            } else if (yLabel >= 10) {
                p.text(PApplet.nf(yLabel, 2, 2), x - 17, y - i * h / 4);
            } else {
                p.text(PApplet.nf(yLabel, 1, 3), x - 17, y - i * h / 4);
            }

            float xLabel = PApplet.map(i * l / 4, 0, l, 0, Application.session.getNodeMax());
            p.line(x + i * l / 4, y - 3, x + i * l / 4, y + 3);
            xLabel = CoxBox(xLabel, 'n');
            if (xLabel >= 1000) {
                p.text(PApplet.nf((int) xLabel, 4) + "     ", x + i * l / 4, y + 12);
            } else if (xLabel >= 100) {
                p.text(PApplet.nf(xLabel, 3, 1) + "     ", x + i * l / 4, y + 12);
            } else if (xLabel >= 10) {
                p.text(PApplet.nf(xLabel, 2, 2) + "     ", x + i * l / 4, y + 12);
            } else {
                p.text(PApplet.nf(xLabel, 1, 3) + "     ", x + i * l / 4, y + 12);
            }
            //http://lstat.kuleuven.be/java/version2.0/Applet015.html
        }
        p.textMode(PConstants.CENTER);
        PFont font = p.createFont("DejaVuSans-ExtraLight-", 12);
        p.textFont(font);
        p.text("effectif modifié", x + l / 4, y - h - 7);
        p.text("poids modifié", x + l / 2, y + 25);

        float[] temp = new float[(int) Application.session.getMaxNodeTotal()];
        int comp = 1;
        temp[0] = Application.session.getNodePoids(0);
        temp[1] = comp;
        for (int i = 1; i < Application.session.getNodePoids().length; i++) {
            if (Application.session.getNodePoids(i) != temp[0]) {
                drawStickCoxBoxNode(temp, x, y, l, h);
                temp[0] = Application.session.getNodePoids(i);
                comp = 1;
                temp[1] = comp;
            } else {
                comp++;
                temp[1] = comp;
            }
        }

    }

    public static void distributionCoxBox(float x, float y, float l, float h) {
        PApplet p = Application.session.getPApplet();
        p.strokeWeight(1);
        p.stroke(10);
        PFont font2 = p.createFont("DejaVuSans-ExtraLight-", (float) 7.6);
        p.textFont(font2);
        for (int i = 0; i < 5; i++) {

            float yLabel = PApplet.map(i * h / 4, 0, h, 0, 50631);
            p.line(x - 3, y - i * h / 4, x + 2, y - i * h / 4);
            yLabel = CoxBox(yLabel, 'e');
            p.fill(10);
            if (yLabel >= 1000) {
                p.text(PApplet.nf((int) yLabel, 4), x - 17, y - i * h / 4);
            } else if (yLabel >= 100) {
                p.text(PApplet.nf(yLabel, 3, 1), x - 17, y - i * h / 4);
            } else if (yLabel >= 10) {
                p.text(PApplet.nf(yLabel, 2, 2), x - 17, y - i * h / 4);
            } else {
                p.text(PApplet.nf(yLabel, 1, 3), x - 17, y - i * h / 4);
            }

            float xLabel = PApplet.map(i * l / 4, 0, l, 0, Application.session.getEdgeMax());
            p.line(x + i * l / 4, y - 3, x + i * l / 4, y + 3);
            xLabel = CoxBox(xLabel, 'e');
            if (xLabel >= 1000) {
                p.text(PApplet.nf((int) xLabel, 4) + "     ", x + i * l / 4, y + 12);
            } else if (xLabel >= 100) {
                p.text(PApplet.nf(xLabel, 3, 1) + "     ", x + i * l / 4, y + 12);
            } else if (xLabel >= 10) {
                p.text(PApplet.nf(xLabel, 2, 2) + "     ", x + i * l / 4, y + 12);
            } else {
                p.text(PApplet.nf(xLabel, 1, 3) + "     ", x + i * l / 4, y + 12);
            }
            //http://lstat.kuleuven.be/java/version2.0/Applet015.html
        }
        p.textMode(PConstants.CENTER);
        PFont font = p.createFont("DejaVuSans-ExtraLight-", 12);
        p.textFont(font);
        p.text("effectif modifié", x + l / 4, y - h - 7);
        p.text("poids modifié", x + l / 2, y + 25);

        float[] temp = new float[(int) Application.session.getMaxEdgeTotal()];
        int comp = 1;
        temp[0] = Application.session.getEdgePoids(0);
        temp[1] = comp;
        for (int i = 1; i < Application.session.getEdgePoids().length; i++) {
            if (Application.session.getEdgePoids(i) != temp[0]) {
                drawStickCoxBox(temp, x, y, l, h);
                temp[0] = Application.session.getEdgePoids(i);
                comp = 1;
                temp[1] = comp;
            } else {
                comp++;
                temp[1] = comp;
            }
        }

    }

    public static void drawStickCoxBox(float[] temp, float X, float Y, float l, float h) {
        PApplet p = Application.session.getPApplet();
        p.stroke(0);
        p.strokeWeight(2);
        if (temp[0] != 0) {
            float x = PApplet.map(CoxBox(temp[0], 'e'), 0, CoxBox(Application.session.getEdgeMax(), 'e'), 0, l);
            float y = PApplet.map(CoxBox(temp[1], 'e'), 0, CoxBox(50631, 'e'), 0, h);
            p.line(X + x, Y, X + x, Y - y);
        }
        p.noStroke();
    }

    public static void drawStickCoxBoxNode(float[] temp, float X, float Y, float l, float h) {
        PApplet p = Application.session.getPApplet();
        p.stroke(0);
        p.strokeWeight(2);
        if (temp[0] != 0) {
            float x = PApplet.map(CoxBox(temp[0], 'n'), 0, CoxBox(Application.session.getNodeMax(), 'n'), 0, l);
            float y = PApplet.map(CoxBox(temp[1], 'n'), 0, CoxBox(436, 'n'), 0, h);
            p.line(X + x, Y, X + x, Y - y);
        }
        p.noStroke();
    }

    public static float CoxBox(float y, char i) {
        // T(Y) = ((Y^lambda)-1)/lambda si lambda != 0
        float lambda = 1;
        if (i == 'e') {
            lambda = Application.session.getLambdaE();
        } else if (i == 'n') {
            lambda = Application.session.getLambdaN();
        }
        float ret = 0;
        if (lambda == 0) {
            ret = PApplet.log(y);
        } else {
            ret = (PApplet.pow(y, lambda) - 1) / lambda;
        }
        return ret;
    }
    
    public static float CoxBoxLambda(float y, float lambda) {
        // T(Y) = ((Y^lambda)-1)/lambda si lambda != 0
        
        float ret = 0;
        if (lambda == 0) {
            ret = PApplet.log(y);
        } else {
            ret = (PApplet.pow(y, lambda) - 1) / lambda;
        }
        return ret;
    }
    
    // affichage de la légende du clustering 
    public static void afficheCluster() {
        PApplet p = Application.session.getPApplet();
        
        // coordonnées du rectangle
        float x = p.width - 250;
        float y = p.height / 18;
        float l = 220;
        float h = 50;
        
        p.stroke(10, 150);
        p.fill(190, 201, 186, 100);
        
        p.strokeWeight(1);
        // show
        p.rect(x + 20, y - 15, l/2 - 20, 15);
        // hide
        p.rect(x + l/2, y - 15, l/2 - 20, 15);
        // create
        p.rect(x + 20, y - 30, l/2 - 20, 15);
        // delete
        p.rect(x + l/2, y - 30, l/2 - 20, 15);
        
        
        p.strokeWeight(2);
        // rectangle de base 
        p.rect(x, y, l, h);
        
        
        
        
        // boutons selections
        p.fill(138,184,51);
        p.triangle(x + 95, y + 20, x + 95, y + 44, x + 120, y + 32);
        
        p.fill(16, 91, 136);
        p.ellipse(x + 150, y + 30, 12, 12);
        
        p.fill(182, 92, 96);
        p.rectMode(PConstants.CENTER);
        p.rect(x + 195, y + 30, 24, 24);
        p.rectMode(PConstants.CORNER);
        
        // textes
        p.fill(0); 
        p.text("clusters :", x + 35, y + 15);
        p.textMode(PConstants.CENTER);
        p.text("run", x + 90 + 25/2, y + 15);
        p.text("show", x + 150, y + 15);
        p.text("clean", x + 195, y + 15);
        p.text("show", x + 20 + l/4 - 10, y - 3 );
        p.text("hide", x + l/2 + l/4 - 10, y - 3 );
        p.text("create", x + 20 + l/4 - 10, y - 17 );
        p.text("delete", x + l/2 + l/4 - 10, y - 17 );
        
        // curseur
        Application.session.getCurseur5().drawStep();
        KMeans.setN((int)Application.session.getCurseur5().getCurs());
        PFont font1 = p.createFont("DejaVuSans-ExtraLight-", 12);
        p.textFont(font1);
    }
    
}
