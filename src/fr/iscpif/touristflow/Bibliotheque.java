/*

 Copyright : UMR Géographie Cités - Quentin Lobbé (2012)

 Authors : 
 Quentin Lobbé <quentin.lobbe@gmail.com>
 Julie Fen-Chong <julie.fenchong@gmail.com>
 Julian Bilcke <julian.bilcke@iscpif.fr>

 This file is a part of TouristsFlowInParis Project

 Build with Processing ( Ben Fry, Casey Reas ) ( GNU GPL )
 Build with Unfolding ( Till Nagel, Felix Lange ) ( BSD )


 This software is a computer program whose purpose is to manipulate, 
 explore and visualize large graphs of flow through time and space .
 This software is governed by the CeCILL license under French law and 
 abiding by the rules of distribution of free software. You can use, 
 modify and/ or redistribute the software under the terms of the CeCILL 
 license as circulated by CEA, CNRS and INRIA at the following URL 
 "http://www.cecill.info".


 As a counterpart to the access to the source code and rights to copy, 
 modify and redistribute granted by the license, users are provided only with a 
 limited warranty and the software's author, the holder of the economic rights, 
 and the successive licensors have only limited liability.


 In this respect, the user's attention is drawn to the risks associated with loading, 
 using, modifying and/or developing or reproducing the software by the user in light 
 of its specific status of free software, that may mean that it is complicated to manipulate, 
 and that also therefore means that it is reserved for developers and experienced professionals 
 having in-depth computer knowledge. Users are therefore encouraged to load and test 
 the software's suitability as regards their requirements in conditions enabling the security 
 of their systems and/or data to be ensured and, more generally, to use and operate it in 
 the same conditions as regards security.


 The fact that you are presently reading this means that you have had knowledge of the CeCILL 
 license and that you accept its terms.
 
 */
package fr.iscpif.touristflow;

import de.fhpotsdam.unfolding.geo.Location;
import processing.core.*;
import static java.lang.System.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 *
 *
 *
 *
 * @author Quentin Lobbé
 */
public class Bibliotheque {

    static void loadGraph() {
        String prefix = App.cf.inputGraphPrefix;
        for (int i = 0; i < 24; i++) {
            App.db.setTableauGephi(i, new Gephi());
            String from = String.format("%02d", (i < 24) ? i : 0);
            String to = String.format("%02d", (i < 23) ? (i + 1) : 0);

            String filePath = prefix + from + "-" + to + ".gexf";
            System.out.println("filePath: " + filePath);
            App.db.getTableauGephi()[i].loadG(filePath);
        }
        App.db.setIndex(0);

    }

    static void maxEdgeTot(int i) {
        if (i > App.db.getMaxEdgeTotal()) {
            App.db.setMaxEdgeTotal(i);
        }
    }

    static void maxNodeTot(int i) {
        if (i > App.db.getMaxNodeTotal()) {
            App.db.setMaxNodeTotal(i);
        }
    }

    // calcule la distance entre deux points dont on ne connait que la lat/long
    static float distFrom(float lat1, float lng1, float lat2, float lng2) {
        float earthRadius = (float) 3958.75;
        float dLat = PApplet.radians(lat2 - lat1);
        float dLng = PApplet.radians(lng2 - lng1);
        float a = PApplet.sin(dLat / 2) * PApplet.sin(dLat / 2)
                + PApplet.cos(PApplet.radians(lat1)) * PApplet.cos(PApplet.radians(lat2)) * PApplet.sin(dLng / 2) * PApplet.sin(dLng / 2);
        float c = 2 * PApplet.atan2(PApplet.sqrt(a), PApplet.sqrt(1 - a));
        float dist = earthRadius * c;
        int meterConversion = 1609;
        float d = (float) dist * (float) meterConversion;
        return d;
    }

    // calcule une valeur étalon permettant de passer de la distance en pixel à la distance en mêtre
    static float meter2Pixel(float dReel) {
        Location l1 = new Location((float) 48.895, (float) 2.283);
        Location l2 = new Location((float) 48.878, (float) 2.436);
        float xy1[] = App.db.getMap().getScreenPositionFromLocation(l1);
        float xy2[] = App.db.getMap().getScreenPositionFromLocation(l2);
        float distReel = distFrom(l1.getLat(), l1.getLon(), l2.getLat(), l2.getLon());
        float distScreen = PApplet.dist(xy1[0], xy1[1], xy2[0], xy2[1]);
        float dScreen = dReel * distScreen / distReel;
        return dScreen;
    }

    // détermine l'edge de plus grande et plus courte taille
    static void distMinMax() {
        for (int k = 0; k < Temps.getHourCount(); k++) {
            for (int i = 0; i < App.db.getTableauGephi()[k].edgeCount; i++) {
                Location l1 = new Location(App.db.getMatEdge(0, i), App.db.getMatEdge(1, i));
                Location l2 = new Location(App.db.getMatEdge(2, i), App.db.getMatEdge(3, i));
                float distance = distFrom(l1.getLat(), l1.getLon(), l2.getLat(), l2.getLon());
                if (distance > App.db.getDistMax()) {
                    App.db.setDistMax(distance);
                }
                if (distance < App.db.getDistMin()) {
                    App.db.setDistMin(distance);
                }
            }
        }
    }

    // à chaque changement d'interval, les données sont stockées de l'objet Gephi vers ces 2 matrices, les actions de filtrages et traitements se feront sur ces matrices
    static void remplirTableauImage(int index) {

        int cpt = 0;

        for (int i = 0; i < App.db.getNodePoids().length; i++) {
            App.db.setNodePoids(i, 0);
        }

        for (int k = 0; k < App.db.getEdgePoids().length; k++) {
            App.db.setEdgePoids(k, 0);
        }

        for (int i = 0; i < App.db.getTableauGephi()[App.db.getIndex()].edgeCount; i++) {
            App.db.setMatEdge(0, i, (float) App.db.getTableauGephi()[App.db.getIndex()].edge[i][0]);
            App.db.setMatEdge(1, i, (float) App.db.getTableauGephi()[App.db.getIndex()].edge[i][1]);
            App.db.setMatEdge(2, i, (float) App.db.getTableauGephi()[App.db.getIndex()].edge[i][2]);
            App.db.setMatEdge(3, i, (float) App.db.getTableauGephi()[App.db.getIndex()].edge[i][3]);
            App.db.setMatEdge(4, i, (float) App.db.getTableauGephi()[App.db.getIndex()].edge[i][4]);
            cpt++;
            App.db.setEdgePoids(i, (float) App.db.getTableauGephi()[App.db.getIndex()].edge[i][4]);
        }





        for (int i = 0; i < App.db.getTableauGephiCount(App.db.getIndex(), 0); i++) {
            App.db.setMatNode(0, i, (float) App.db.getTableauGephiNode(App.db.getIndex(), 1, i));
            App.db.setMatNode(1, i, (float) App.db.getTableauGephiNode(App.db.getIndex(), 0, i));
            App.db.setMatNode(2, i, (float) App.db.getTableauGephiNode(App.db.getIndex(), 2, i));
            App.db.setNodePoids(i, (float) App.db.getTableauGephiNode(App.db.getIndex(), 2, i));


        }


        App.db.setNodePoids(PApplet.sort(App.db.getNodePoids()));
        App.db.setEdgePoids(PApplet.sort(App.db.getEdgePoids()));

        App.db.setIndexBis(index);

    }

    // efface tous les oursins de la liste courante 
    static void effacerOursins() {
        while (!App.db.getUrchins().isEmpty()) {
            App.db.getUrchins().remove(0);
        }
    }

    // créer tous les oursins dans la zone définie par l'écran  
    static void creerOursinsVue() {
        PApplet p = App.db.getPApplet();
        int i = 1;
        for (int j = 0; j < App.db.getTableauGephi()[App.db.getIndex()].nodeCount; j++) {
            Location l1 = new Location(App.db.getMatNode(0, j), App.db.getMatNode(1, j));
            float xy1[] = App.db.getMap().getScreenPositionFromLocation(l1);
            //if ((xy1[1] > 0) && (xy1[0] > 0) && (xy1[0] < p.width) && (xy1[1] < p.height) && (App.db.getMatNode(2, j) > 50)) {
                //out.println("Création Oursin " + i);
                Affichage.selectionOursins(xy1[0], xy1[1], App.db.getMatNode(0, j), App.db.getMatNode(1, j));
                i++;
            //}
        }
        out.println("Oursins calculés");
    }

    // affiche les oursins 
    static void showOursins() {
        for (int i = 0; i < App.db.getUrchins().size(); i++) {
            Urchin oursin = (Urchin) App.db.urchins.get(i);
            oursin.setStatus("normal");
        }
    }

    // cacher les oursins 
    static void hideOursins() {
        for (int i = 0; i < App.db.getUrchins().size(); i++) {
            Urchin oursin = App.db.urchins.get(i);
            oursin.setStatus("selected");
        }
    }

    // mettre à jour les oursins en cas de changement d'intervalle de temps
    static void miseAJourOursins() {
        float[][] temp = new float[2][1000];
        int i = 0;
        while (!App.db.getUrchins().isEmpty()) {
            Urchin oursin = App.db.urchins.get(0);
            temp[0][i] = oursin.getXN();
            temp[1][i] = oursin.getYN();
            App.db.getUrchins().remove(0);
            i++;
        }
        for (int j = 0; j < i + 1; j++) {
            Location l = new Location(temp[0][j], temp[1][j]);
            float xy[] = App.db.getMap().getScreenPositionFromLocation(l);

            Affichage.selectionOursins(xy[0], xy[1], temp[0][j], temp[1][j]);
        }

    }

    // matrice regroupant les distances de tous les edges de la plus grande à la plus petite
    static void miseAJourMatriceDistance(int index) {
        App.db.setTabEdgeDist(new float[(int) App.db.getMaxEdgeTotal()]);
        for (int j = 0; j < App.db.getTableauGephi()[App.db.getIndex()].edgeCount; j++) {
            App.db.setTabEdgeDist(j, distFrom(App.db.getMatEdge(0, j), App.db.getMatEdge(1, j), App.db.getMatEdge(2, j), App.db.getMatEdge(3, j)));
        }

        //TriRapide.trirapide2(Application.session.getTabEdgeDist(), (int) Application.session.getTableauGephi()[Application.session.getIndex()].edgeCount);

    }

    static void maxNbRepartitionEdge(int comp) {
        if (comp > App.db.getCompMaxEdge()) {
            App.db.setCompMaxEdge(comp);
        }
    }

    static float[] remplissagePointsCardinaux(float[] pointsCardinaux, int cpt, int sens) {

        // on va créer 16 tableaux qui correspondront à une division de l'espace autour d'un noeud suivant les points cardinaux

        int[] sud = {};

        int[] sudsudEst = {};

        int[] sudEst = {};

        int[] sudEstEst = {};

        int[] Est = {};

        int[] nordEstEst = {};

        int[] nordEst = {};

        int[] nordnordEst = {};

        int[] nord = {};

        int[] nordnordOuest = {};

        int[] nordOuest = {};

        int[] nordOuestOuest = {};

        int[] Ouest = {};

        int[] sudOuestOuest = {};

        int[] sudOuest = {};

        int[] sudsudOuest = {};

        // les arcs sortants sont rangés en fonction de leur orientation dans le tableau qui va bien

        float angle = 0;

        for (int j = 0; j < cpt; j++) {

            if (sens == 1) {
                angle = App.db.getEntrant(5, j);
            } else if (sens == 0) {
                angle = App.db.getSortant(5, j);
            }

            if ((-PConstants.PI / 16 <= angle) && (angle < PConstants.PI / 16)) {
                sud = PApplet.append(sud, j);

            } else if ((PConstants.PI / 16 <= angle) && (angle < 3 * PConstants.PI / 16)) {
                sudsudEst = PApplet.append(sudsudEst, j);

            } else if ((3 * PConstants.PI / 16 <= angle) && (angle < 5 * PConstants.PI / 16)) {
                sudEst = PApplet.append(sudEst, j);

            } else if ((5 * PConstants.PI / 16 <= angle) && (angle < 7 * PConstants.PI / 16)) {
                sudEstEst = PApplet.append(sudEstEst, j);

            } else if ((7 * PConstants.PI / 16 <= angle) && (angle < 9 * PConstants.PI / 16)) {
                Est = PApplet.append(Est, j);

            } else if ((9 * PConstants.PI / 16 <= angle) && (angle < 11 * PConstants.PI / 16)) {
                nordEstEst = PApplet.append(nordEstEst, j);

            } else if ((11 * PConstants.PI / 16 <= angle) && (angle < 13 * PConstants.PI / 16)) {
                nordEst = PApplet.append(nordEst, j);

            } else if ((13 * PConstants.PI / 16 <= angle) && (angle < 15 * PConstants.PI / 16)) {
                nordnordEst = PApplet.append(nordnordEst, j);

            } else if (((15 * PConstants.PI / 16 <= angle) && (angle <= PConstants.PI)) || ((- 15 * PConstants.PI / 16 > angle) && (angle >= -PConstants.PI))) {
                nord = PApplet.append(nord, j);

            } else if ((- 13 * PConstants.PI / 16 > angle) && (angle >= - 15 * PConstants.PI / 16)) {
                nordnordOuest = PApplet.append(nordnordOuest, j);

            } else if ((- 11 * PConstants.PI / 16 > angle) && (angle >= - 13 * PConstants.PI / 16)) {
                nordOuest = PApplet.append(nordOuest, j);

            } else if ((- 9 * PConstants.PI / 16 > angle) && (angle >= - 11 * PConstants.PI / 16)) {
                nordOuestOuest = PApplet.append(nordOuestOuest, j);

            } else if ((- 7 * PConstants.PI / 16 > angle) && (angle >= - 9 * PConstants.PI / 16)) {
                Ouest = PApplet.append(Ouest, j);

            } else if ((- 5 * PConstants.PI / 16 > angle) && (angle >= - 7 * PConstants.PI / 16)) {
                sudOuestOuest = PApplet.append(sudOuestOuest, j);

            } else if ((- 3 * PConstants.PI / 16 > angle) && (angle >= - 5 * PConstants.PI / 16)) {
                sudOuest = PApplet.append(sudOuest, j);

            } else if ((-PConstants.PI / 16 > angle) && (angle >= - 3 * PConstants.PI / 16)) {
                sudsudOuest = PApplet.append(sudsudOuest, j);
            }
        }

        pointsCardinaux = traitementPointsCardinaux(Est, pointsCardinaux, 0, sens);
        pointsCardinaux = traitementPointsCardinaux(sudEstEst, pointsCardinaux, 2, sens);
        pointsCardinaux = traitementPointsCardinaux(sudEst, pointsCardinaux, 4, sens);
        pointsCardinaux = traitementPointsCardinaux(sudsudEst, pointsCardinaux, 6, sens);
        pointsCardinaux = traitementPointsCardinaux(sud, pointsCardinaux, 8, sens);
        pointsCardinaux = traitementPointsCardinaux(sudsudOuest, pointsCardinaux, 10, sens);
        pointsCardinaux = traitementPointsCardinaux(sudOuest, pointsCardinaux, 12, sens);
        pointsCardinaux = traitementPointsCardinaux(sudOuestOuest, pointsCardinaux, 14, sens);
        pointsCardinaux = traitementPointsCardinaux(Ouest, pointsCardinaux, 16, sens);
        pointsCardinaux = traitementPointsCardinaux(nordOuestOuest, pointsCardinaux, 18, sens);
        pointsCardinaux = traitementPointsCardinaux(nordOuest, pointsCardinaux, 20, sens);
        pointsCardinaux = traitementPointsCardinaux(nordnordOuest, pointsCardinaux, 22, sens);
        pointsCardinaux = traitementPointsCardinaux(nord, pointsCardinaux, 24, sens);
        pointsCardinaux = traitementPointsCardinaux(nordnordEst, pointsCardinaux, 26, sens);
        pointsCardinaux = traitementPointsCardinaux(nordEst, pointsCardinaux, 28, sens);
        pointsCardinaux = traitementPointsCardinaux(nordEstEst, pointsCardinaux, 30, sens);

        return pointsCardinaux;
    }

    // Traitement des points cardinaux des Oursins
    static float[] traitementPointsCardinaux(int[] pointCardinal, float[] pointsCardinaux, int rang, int sens) {

        float sum = 0;
        float moy = 0;

        // si la zone n'a pas d'arc normalement le tableau correspondant vaut -1
        if (pointCardinal.length >= 1) {
            for (int i = 0; i < pointCardinal.length; i++) {
                if (pointCardinal[i] > 0) {
                    if (sens == 0) {
                        sum = sum + App.db.getSortant(4, pointCardinal[i]);
                        Location location1 = App.db.getMap().getLocationFromScreenPosition(App.db.getSortant(0, pointCardinal[i]), App.db.getSortant(1, pointCardinal[i]));
                        Location location2 = App.db.getMap().getLocationFromScreenPosition(App.db.getSortant(2, pointCardinal[i]), App.db.getSortant(3, pointCardinal[i]));
                        moy = moy + distFrom(location1.getLat(), location1.getLon(), location2.getLat(), location2.getLon());
                    } else if (sens == 1) {
                        sum = sum + App.db.getSortant(4, pointCardinal[i]);
                        Location location3 = App.db.getMap().getLocationFromScreenPosition(App.db.getEntrant(0, pointCardinal[i]), App.db.getEntrant(1, pointCardinal[i]));
                        Location location4 = App.db.getMap().getLocationFromScreenPosition(App.db.getEntrant(2, pointCardinal[i]), App.db.getEntrant(3, pointCardinal[i]));
                        moy = moy + distFrom(location3.getLat(), location3.getLon(), location4.getLat(), location4.getLon());
                    }
                }
            }
            // sinon on calcul la moyenne des distances et la somme des poids 
            moy = moy / pointCardinal.length;
            moy = PApplet.map(CoxBoxLambda(moy, (float) 0.4), 0, CoxBoxLambda(App.db.getDistMax(), (float) 0.4), 0, 10000);
            sum = PApplet.map(PApplet.log(sum), 0, PApplet.log(App.db.getEdgeMax()), (float) 0.5, 15);

        }
        pointsCardinaux[rang] = sum;
        pointsCardinaux[rang + 1] = moy;

        return pointsCardinaux;
    }

    // fonction de calcule des effectifs max pour les distributions
    public static void effectif(String cas) {

        float count = 0;
        float cpt = 0;
        float max = PConstants.MIN_FLOAT;
        if ("node".equals(cas)) {
            for (int k = 0; k < Temps.getHourCount(); k++) {
                count = App.db.getTableauGephi()[k].nodeCount;
                for (int i = 1; i < count; i++) {
                    App.db.setNodePoids(i, (float) App.db.getTableauGephi()[k].btsDegree[i]);
                }
                App.db.setNodePoids(PApplet.sort(App.db.getNodePoids()));
                cpt = 0;
                float temp = 0;
                for (int i = 1; i < count; i++) {
                    if (App.db.getNodePoids(i) > 0) {
                        if (cpt == 0) {
                            temp = App.db.getNodePoids(i);
                            cpt = 1;
                        } else {
                            if (App.db.getNodePoids(i) == temp) {
                                cpt++;
                            } else {
                                max = PApplet.max(cpt, max);
                                cpt = 1;
                                temp = App.db.getNodePoids(i);
                            }
                        }
                    }
                }
            }
            App.db.setNodeEffMax(max);
        } else if ("edge".equals(cas)) {
            for (int k = 0; k < Temps.getHourCount(); k++) {

                count = App.db.getTableauGephi()[k].edgeCount;

                for (int i = 1; i < count; i++) {
                    App.db.setEdgePoids(i, (float) App.db.getTableauGephi()[k].edge[i][4]);
                }
                App.db.setEdgePoids(PApplet.sort(App.db.getEdgePoids()));
                cpt = 0;
                float temp = 0;
                for (int i = 1; i < count; i++) {
                    if (App.db.getEdgePoids(i) > 0) {
                        if (cpt == 0) {
                            temp = App.db.getEdgePoids(i);
                            cpt = 1;
                        } else {
                            if (App.db.getEdgePoids(i) == temp) {
                                cpt++;
                            } else {
                                max = PApplet.max(cpt, max);
                                cpt = 1;
                                temp = App.db.getEdgePoids(i);
                            }
                        }
                    }
                }
            }
            App.db.setEdgeEffMax(max);
        } else if ("lissage".equals(cas)) {
            count = App.db.getNBRoamBTSMoy(0).length;
            float tab[] = new float[(int) count];
            for (int z = 3; z < 27; z++) {
                for (int k = 0; k < count; k++) {
                    tab[k] = App.db.getNBRoamBTSMoy(z, k);
                }
                tab = PApplet.sort(tab);
                cpt = 0;
                float temp = 0;
                for (int i = 1; i < count; i++) {

                    if (cpt == 0) {
                        temp = tab[i];
                        cpt = 1;
                    } else {
                        if (tab[i] == temp) {
                            cpt++;
                        } else {
                            max = PApplet.max(cpt, max);
                            cpt = 1;
                            temp = tab[i];
                        }
                    }
                }

                //out.println("max effect :" + max);
                //out.println("max poids :" + tab[(int)(count - 1)]);
            }
        }
    }

    /*
     * s'il on veut créer les flèches depuis les oursins
     * sinon on travail sur des fichiers csv préparés
     */

    public static void creerArrow() {
        PApplet p = App.db.getPApplet();

        if (App.cf.createArrowsFromUrchins) {
            creerOursinsVue();
            ArrayList<Urchin> urchins = App.db.getUrchins();
            long size = urchins.size();
            for (int z = 0; z < size; z++) {
                Urchin urchin = urchins.get(z);
                urchin.draw();
            }
            effacerOursins();
        } else {

            String[] lines = p.loadStrings(App.db.getReferencesArrows(App.db.getIndex()));
            for (int i = 1; i < lines.length; i++) {

                String[] mots = PApplet.split(lines[i], ';');

                Arrow a = new Arrow(Float.parseFloat(mots[0]), Float.parseFloat(mots[1]), Float.parseFloat(mots[2]), Float.parseFloat(mots[3]), Float.parseFloat(mots[4]), Float.parseFloat(mots[5]), Float.parseFloat(mots[6]), Float.parseFloat(mots[7]), Boolean.parseBoolean(mots[8]));
                out.println("flèche " + i + " créée");

                if (Boolean.parseBoolean(mots[8])) {
                    App.db.arrowsIN.add(a);
                } else {
                    App.db.arrowsOUT.add(a);
                }
            }
        }
    }

    public static void supprimerArrow() {
        while (App.db.arrowsIN.size() > 0) {
            App.db.arrowsIN.remove(0);
        }
        while (App.db.arrowsOUT.size() > 0) {
            App.db.arrowsOUT.remove(0);
        }
    }

    // extraire les données du csv nb_roam_bts_moy
    public static float[][] readData() {
        PApplet p = App.db.getPApplet();
        float[][] mat;
        try {
            String[] lines = p.loadStrings(App.cf.nbRoamingBTSmoy);
            int count = lines.length;
            mat = new float[27][count];
            for (int i = 1; i < lines.length; i++) {
                String[] mots = PApplet.split(lines[i], App.cf.roamingBTScsvSeparator);
                for (int j = 0; j < mots.length; j++) {
                    mat[j][i - 1] = Float.parseFloat(mots[j].replaceAll("\"", ""));
                }
            }
        } catch (Exception erreur1) {
            System.err.append("erreur lors du chargement du csv de roaming bts moy: " + erreur1);
            mat = new float[27][0];
        }
        return mat;
    }

    // stocker les données des Arrows dans un csv 
    public static void writeArrowData() {
        PApplet p = App.db.getPApplet();
        String[] lines = {};
        lines = PApplet.append(lines, "xSource ; ySource ; angle ; xTarget ; yTarget ; xi ; yi ; poids ; sens");
        for (int i = 0; i < App.db.arrowsIN.size(); i++) {
            Arrow a = (Arrow) App.db.arrowsIN.get(i);

            String words = Float.toString(a.getX()) + ';' + Float.toString(a.getY()) + ';' + Float.toString(a.getAngle()) + ';' + Float.toString(a.get_X()) + ';' + Float.toString(a.get_Y()) + ';' + Float.toString(a.getXi()) + ';' + Float.toString(a.getYi()) + ';' + Float.toString(a.getPoids()) + ';' + Boolean.toString(a.getSens());
            lines = PApplet.append(lines, words);
        }
        for (int i = 0; i < App.db.arrowsOUT.size(); i++) {
            Arrow a = (Arrow) App.db.arrowsOUT.get(i);

            String words = Float.toString(a.getX()) + ';' + Float.toString(a.getY()) + ';' + Float.toString(a.getAngle()) + ';' + Float.toString(a.get_X()) + ';' + Float.toString(a.get_Y()) + ';' + Float.toString(a.getXi()) + ';' + Float.toString(a.getYi()) + ';' + Float.toString(a.getPoids()) + ';' + Boolean.toString(a.getSens());
            lines = PApplet.append(lines, words);

        }

        out.println("Csv crée");

        p.saveStrings(App.cf.writeArrowsPrefix + Temps.getDateText() + ".csv", lines);
    }

    /*
     * définir une grille de lissage sur l'écran 
     * le paramêtre "n" correspond à la taille en pixel sur l'écran celle ci est retranscrite m sur la carte
     * donc plus on zoom plus la grille est précise en mêtre mais l'éccartement à l'écran reste le même
     */
    public static float[][] getGrille(float n) {
        PApplet p = App.db.getPApplet();
        n = Bibliotheque.meter2Pixel(n);
        Location l1 = App.db.getMap().getLocationFromScreenPosition(0, 0);
        Location l2 = App.db.getMap().getLocationFromScreenPosition(n, n);
        Location l3 = App.db.getMap().getLocationFromScreenPosition(0, n);
        float pas_X = l1.getLat() - l2.getLat();
        float pas_Y = l2.getLon() - l1.getLon();
        out.println(pas_X);
        out.println(pas_Y);
        out.println(distFrom(l1.getLat(), l1.getLon(), l3.getLat(), l3.getLon()));
        int cpt = 0;
        float[][] mat = new float[2][100000];
        for (float i = (float) 49.1729; i > 48.1437; i = (float) (i - pas_X)) {
            for (float j = (float) 3.4472; j > 1.48335; j = (float) (j - pas_Y)) {
                mat[0][cpt] = i;
                mat[1][cpt] = j;
                cpt++;
            }
        }


        out.println("done");
        out.println(cpt);
        return mat;
    }

    // calcule la fonction coxbox
    public static float CoxBox(float y, char i) {
        // T(Y) = ((Y^lambda)-1)/lambda si lambda != 0
        float lambda = 1;
        if (i == 'e') {
            lambda = App.db.getLambdaE();
        } else if (i == 'n') {
            lambda = App.db.getLambdaN();
        }
        float ret = 0;
        if (lambda == 0) {
            ret = PApplet.log(y);
        } else {
            ret = (PApplet.pow(y, lambda) - 1) / lambda;
        }
        return ret;
    }

    // calcule de la fonction coxbox en rentrant un lambda donné 
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
}
