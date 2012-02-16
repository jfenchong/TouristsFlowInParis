/*

Copyright Quentin Lobbé (2012)
Author : Quentin Lobbé <quentin.lobbe@gmail.com>
Contributor : Julian Bilcke

This file is a part of TouristsFlowInParis Project

Build with Processing ( Ben Fry, Casey Reas ) ( GNU GPL )


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

import processing.core.*;

/**
 *
 * @author Quentin Lobbé
 */
public class BoutonMenu {

    protected float size;
    public float x;
    public float y;
    public String nom;
    public String[] enfants; // tableau regroupant les noms des nodes fils
    public float xPapa = 0; // coordonnées du père
    public float yPapa = 0; // cooordonnées du père
    private String statusNormal = "normal"; // 2 status possibles pour un noeud
    private String statusSelected = "selected";
    public String status = statusNormal;
    public boolean accordParental = false; // un noeud ne peut s'afficher que si son père a été selection avant
    public boolean init = true;

    public BoutonMenu(float size, float x, float y, String nom, String[] enfant) {  // constructeur type  1
        this.size = size;
        this.x = x;
        this.y = y;
        this.nom = nom;
        enfants = new String[Application.session.getNbMaxEnfants()];
        System.arraycopy(enfant, 0, this.enfants, 0, enfant.length);
    }

    public BoutonMenu(float size, float x, float y, String nom) { // constructeur type 2
        this.size = size;
        this.x = x;
        this.y = y;
        this.nom = nom;
        enfants = new String[Application.session.getNbMaxEnfants()];
    }

    public BoutonMenu(float size, float x, float y, String nom, boolean accordParental, String[] enfant) { // constructeur type 3
        this.size = size;
        this.x = x;
        this.y = y;
        this.nom = nom;
        this.accordParental = accordParental;
        enfants = new String[Application.session.getNbMaxEnfants()];
        System.arraycopy(enfant, 0, this.enfants, 0, enfant.length);
    }

    public BoutonMenu(float size, float x, float y, String nom, boolean accordParental) { // constructeur type 4
        this.size = size;
        this.x = x;
        this.y = y;
        this.nom = nom;
        this.accordParental = accordParental;
    }

    public void draw() {
        PApplet p = Application.session.getPApplet();
        p.strokeWeight(2);
        p.stroke(10, 150);
        if (accordParental) { // si le père est selectionné
            if (status.equals(statusNormal)) { // si le noeud n'est pas cliqué  
                drawNormal();
            }
            if (status.equals(statusSelected)) { // si le noeud est cliqué
                drawSelected();
            }
        } else {
            constantesOff(); // sinon mettre à jour les constantes
        }
        propagation(); // fonction permettant de propager le fait qu'un noeud soit cliqué ou non à ces fils 
    }

    public void setAccord(boolean a) {
        this.accordParental = a;
    }

    public void setXpapa(float x) {
        this.xPapa = x;
    }

    public void setYpapa(float y) {
        this.yPapa = y;
    }

    public void setStatus(String z) {
        this.status = z;
    }

    protected void drawNormal() { // draw si le noeud n'est pas cliqué 
        PApplet p = Application.session.getPApplet();
        p.fill(190, 201, 186, 100);
        p.ellipse(x, y, size, size);
        if ((xPapa != 0) && (yPapa != 0)) {
            p.stroke(200, 170);
            p.line(xPapa, yPapa, x, y);
        }
        p.fill(80);
        p.textAlign(PConstants.CENTER);
        p.text(nom, x, y - size + size / 3);
        p.noFill();
        constantesOff(); // mise à jour des constantes
    }

    protected void drawSelected() { // draw si le père est cliqué
        PApplet p = Application.session.getPApplet();
        p.fill(16, 91, 136, 200);

        p.ellipse(x, y, size, size);
        p.stroke(16, 91, 136, 200);
        if ((xPapa != 0) && (yPapa != 0)) {
            p.line(xPapa, yPapa, x, y);
        }
        p.fill(0);
        p.textAlign(PConstants.CENTER);
        p.text(nom, x, y - size + size / 3);
        p.noFill();
        constantesOn(); // mise à jour des constantes 
    }

    protected void propagation() {
        if (this.status.equals(statusNormal)) {
            for (int i = 0; i < enfants.length; i++) {
                for (int j = 0; j < Application.session.getBoutons().size(); j++) {
                    BoutonMenu boutonMenu = (BoutonMenu) Application.session.getBoutons().get(j); // on "etteind" les noeuds dont le père n'est pas cliqué
                    if (boutonMenu.nom.equals(enfants[i])) {
                        boutonMenu.setAccord(false);
                        boutonMenu.setStatus(statusNormal);
                        boutonMenu.setXpapa(x);
                        boutonMenu.setYpapa(y);
                    }
                }
            }
        }
    }

    public void pressed(float tempx, float tempy) {
        if (PApplet.dist(tempx, tempy, x, y) < this.size / 2) {
            incompatib();
            if (status.equals(statusNormal)) { // si le status est normal alors celui devient selected
                status = statusSelected;
                for (int i = 0; i < enfants.length; i++) {
                    String name = enfants[i];
                    for (int j = 0; j < Application.session.getBoutons().size(); j++) {
                        BoutonMenu boutonMenu = (BoutonMenu) Application.session.getBoutons().get(j);
                        if (boutonMenu.nom.equals(name)) {
                            boutonMenu.setAccord(true);
                            boutonMenu.setXpapa(x);
                            boutonMenu.setYpapa(y);
                        }
                    }
                }
            } else if (status.equals(statusSelected)) { // si le status est selected alors celui ci devient normal 
                status = statusNormal;
                for (int i = 0; i < enfants.length; i++) {
                    String name = enfants[i];
                    for (int j = 0; j < Application.session.getBoutons().size(); j++) {
                        BoutonMenu boutonMenu = (BoutonMenu) Application.session.getBoutons().get(j);
                        if (boutonMenu.nom.equals(name)) {
                            boutonMenu.setAccord(false);
                            boutonMenu.setStatus(statusNormal);
                        }
                    }
                }
            }
        }
    }

    protected void incompatib() { // empêche les combinaisons de filtres incompatibles
        if (this.nom.equals("Biweight")) {
            BoutonMenu boutonMenu2 = (BoutonMenu) Application.session.getBoutons().get(11);
            boutonMenu2.setStatus(statusNormal);
        }
        if (this.nom.equals("Shepard")) {
            BoutonMenu boutonMenu3 = (BoutonMenu) Application.session.getBoutons().get(10);
            boutonMenu3.setStatus(statusNormal);
        }
        if (this.nom.equals("Box Cox")) {
            BoutonMenu boutonMenu1 = (BoutonMenu) Application.session.getBoutons().get(8);
            boutonMenu1.setStatus(statusNormal);
        }
        if (this.nom.equals("Log")) {
            BoutonMenu boutonMenu = (BoutonMenu) Application.session.getBoutons().get(6);
            boutonMenu.setStatus(statusNormal);
        }
    }

    protected void constantesOn() { // activation des constantes de filtres
        if (this.nom.equals("Select")) {
            Application.session.setSelect(false);
        }
        if (this.nom.equals("Box Cox Noeud")) {
            Application.session.setBoxCoxNode(true);
        }
        if (this.nom.equals("Lissée")) {
            Application.session.setHeat(true);
            Application.session.setPremierLissage(true);
            Application.session.setBoxCox(false);
        }
        if (this.nom.equals("Box Cox")) {
            Application.session.setBoxCox(true);
            Application.session.setLog(false);
        }
        if (this.nom.equals("Exp(1/x)")) {
            Application.session.setGros(false);
            Application.session.setPetit(true);
        }
        if (this.nom.equals("Arc")) {
            Application.session.setEdge(true);
        }
        if (this.nom.equals("Noeud")) {
            Application.session.setNode(true);
        }
        if (this.nom.equals("Biweight")) {
            Application.session.setBiweight(true);
            Application.session.setShepard(false);
            if (init) {
                Smooth.initBuff1();
                init = false;
            }
        }
        if (this.nom.equals("Shepard")) {
            Application.session.setShepard(true);
            Application.session.setBiweight(false);
            if (init) {
                Smooth.initBuff1();
                init = false;
            }
        }
        if (this.nom.equals("HeatMap")) {
            Application.session.setChaud(true);
        }
        if (this.nom.equals("Log")) {
            Application.session.setLog(true);
        }
        if (this.nom.equals("Oursins")) {
            Application.session.setOursin(true);
        }
        if (this.nom.equals("Arrow")) {
            Application.session.setArrow(true);
            if (init) {
                Smooth.initBuff1();
                init = false;
            }
        }
    }

    protected void constantesOff() { // désactivation des constantes de filtres
        if (this.nom.equals("Select")) {
            Application.session.setSelect(true);
        }
        if (this.nom.equals("Box Cox Noeud")) {
            Application.session.setBoxCoxNode(false);
        }
        if (this.nom.equals("Lissée")) {
            Application.session.setHeat(false);
            Application.session.setPremierLissage(false);
        }
        if (this.nom.equals("Box Cox")) {
            Application.session.setBoxCox(false);
        }
        if (this.nom.equals("Exp(1/x)")) {
            Application.session.setGros(true);
            Application.session.setPetit(false);
        }
        if (this.nom.equals("Arc")) {
            Application.session.setEdge(false);
        }
        if (this.nom.equals("Noeud")) {
            Application.session.setNode(false);
        }
        if (this.nom.equals("Biweight")) {
            Application.session.setBiweight(false);
        }
        if (!init) {
            Smooth.initBuff1();
            init = true;
        }
        if (this.nom.equals("Shepard")) {
            Application.session.setShepard(false);
        }
        if (this.nom.equals("HeatMap")) {
            Application.session.setChaud(false);
        }
        if (this.nom.equals("Log")) {
            Application.session.setLog(false);
        }
        if (this.nom.equals("Oursins")) {
            Application.session.setOursin(false);
            KMeans.KMeansClean();
            Bibliotheque.effacerOursins();
        }
        if (this.nom.equals("Arrow")) {
            Application.session.setArrow(false);
        }
    }
}
