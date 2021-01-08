
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


//La classe réseau de files d'attentes

public class ReseauFilesAttentes {
    private int duree;//100000
    private double tempsService;//Le temps de service de coordinateur
    private  ArrayList<Requete> coordinateur;

    //Liste des serveurs de notre reseau de files d attentes
    private  ArrayList<Serveur> serveur;
    //Liste des requetes de sortie des serveurs
    private  ArrayList<Requete> sortie;
    //Le nombre de requetes envoyees a chaque serveur
    private int[] comp_requ;
    //Le nombre de requetes auquelles chaque serveur a pu repondre
    private int[] comp_requ_sort;



    //Le constructeur de la classe ReseauFilesAttente
    public ReseauFilesAttentes(int duree,double tempsService,ArrayList<Serveur> serveur){
        this.duree = duree;
        this.tempsService=tempsService;
        this.coordinateur = new ArrayList<>();
        this.serveur=serveur;
        this.sortie = new ArrayList<>();
    }

    //Les getteurs
    public int getDuree() {
        return duree;
    }

    public ArrayList<Requete> getCoordinateur(){
        return coordinateur;
    }

    public ArrayList<Requete> getSortie(){
        return sortie;
    }

    //La fonction qui alimente le coordinateur en requetes
    public double equation4(double lambda) {
        double r = Math.random();
        return -Math.log(1-r)/lambda;
    }

    //Ajout de requetes au coordinateur
    public void addRequeteCoordinateur(Requete r){
        this.getCoordinateur().add(r);
    }

    //Les donnees de coordinateur
    public void lesDonneesCoordinateur(double lambda){
        double x=0;
        int y=0;
        double dateEntreeInitiale = 0;
        double dateEntreeCourante = 0;
        double dateSortie = 0;
        while (x < duree)
        {
            x=x+equation4(lambda);
            if(x>duree) break;
            dateEntreeInitiale = x;
            dateEntreeCourante = x;

            if(y==0){
                dateSortie = dateEntreeInitiale+tempsService;

            }else{
                if(dateSortie<dateEntreeInitiale)
                    dateSortie = dateEntreeInitiale+tempsService;
                else
                    dateSortie = dateSortie+tempsService;
            }
            addRequeteCoordinateur(new Requete(y,dateEntreeInitiale,dateEntreeCourante,dateSortie));
            y++;//y pour compter les requetes
        }
    }

    //trier les requetes du tableau coordinateur avec dateEntreeCourante
    public void trierCoordinateur(){
        double dateEntreeCourante;
        int id1;
        double dateEntreeInitiale1;
        double dateEntreeCourante1;
        double dateSortie1;
        for (int j = 0; j < this.getCoordinateur().size(); j++)
            for (int i = 0; i < this.getCoordinateur().size()-1; i++) {
                dateEntreeCourante = this.getCoordinateur().get(i).getDateEntreeCourante();
                id1 = this.getCoordinateur().get(i+1).getId();
                dateEntreeInitiale1  = this.getCoordinateur().get(i+1).getDateEntreeInitiale();
                dateEntreeCourante1 = this.getCoordinateur().get(i+1).getDateEntreeCourante();
                dateSortie1 = this.getCoordinateur().get(i+1).getDateSortie();
                // Si this.getSortie()[j] < this.getSortie()[i]
                // Ensuite, échangez les positions les uns avec les autres.
                if (dateEntreeCourante1 < dateEntreeCourante) {
                    this.getCoordinateur().remove(i+1);
                    this.getCoordinateur().add(i,new Requete(id1,dateEntreeInitiale1,dateEntreeCourante1,dateSortie1));
                }
            }
    }

    //Trier le tableau avec id de la requete, cette méthode est utilisée dans la méthode leTempsDeTraitement

    public void trierSortie(){
        int id;
        int id1;
        double dateEntreeInitiale1;
        double dateEntreeCourante1;
        double dateSortie1;
        for (int j = 0; j < this.getSortie().size(); j++)
            for (int i = 0; i < this.getSortie().size()-1; i++) {
                id = this.getSortie().get(i).getId();
                id1 = this.getSortie().get(i+1).getId();
                dateEntreeInitiale1  = this.getSortie().get(i+1).getDateEntreeInitiale();
                dateEntreeCourante1 = this.getSortie().get(i+1).getDateEntreeCourante();
                dateSortie1 = this.getSortie().get(i+1).getDateSortie();
                // Si this.getSortie()[j] < this.getSortie()[i]
                // Ensuite, échangez les positions les uns avec les autres.
                if (id1 < id) {
                    this.getSortie().remove(i+1);
                    this.getSortie().add(i,new Requete(id1,dateEntreeInitiale1,dateEntreeCourante1,dateSortie1));
                }
            }
    }



    //Le temps de traiteent de chaque requête (tableau)
    public String[][] tempsTraitementRequete(ArrayList<Requete> req){
        String[][] tab=new String[req.size()][2];
        for(int i=0;i< req.size();i++){
            tab[i][0]= String.valueOf(i+1);
            tab[i][1]= String.valueOf(req.get(i).getDateSortie()-req.get(i).getDateEntreeInitiale());
        }
        return tab;
    }


    public double leTempsMoyen(){
        double moyen = 0;
        for(int i=0;i<this.getSortie().size();i++){
            moyen += this.getSortie().get(i).getDateSortie() - this.getSortie().get(i).getDateEntreeInitiale();
        }
        return moyen/this.getSortie().size();
    }


    //après appelant la méthode processus,on obtient le tableau sortie qui contient peut-etre des requetes(dateSortie > duree),il faut appeler la methode sortieALaFin() pour supprimer les requetes(dateSortie > duree)

    public void processus(){//Processus avec n serveurs
        double r;//pour choisir le serveur
        double r1;//pour choisir si la requete sort ou pas

        //initialisations(nombre de requetes entrees ou sortie de chaque serveur)
        comp_requ=new int[this.serveur.size()];
        comp_requ_sort=new int[this.serveur.size()];

        //initialisation de la requete
        Requete requete=new Requete(0,0,0,0);

        int nb = 0;//Le nombre de requetes entrees dans le système

        //Initialisation de tableau de nombre de requetes envoyees a chaque serveur
        for(int i=0;i<serveur.size();i++){
            this.comp_requ[i]=0;
        }
        //Initialisation de tableau de nombre de requetes auxquelles chaque serveur a pu repondre
        for(int i=0;i<serveur.size();i++){
            this.comp_requ_sort[i]=0;
        }


        //traitement des probabilités (ordonnee par ordre croissant)

        for(int i=0;i<this.serveur.size();i++){
            for(int j=1;j<=i;j++){
                if(this.serveur.get(j).getQ()>this.serveur.get(i).getQ()){
                    Serveur serv=this.serveur.get(i);
                    this.serveur.set(i,this.serveur.get(j));
                    this.serveur.set(j,serv);
                }
            }
        }

        while((this.getCoordinateur().size()!=0)&&(this.getCoordinateur().get(0).getDateEntreeCourante()<this.getDuree())){

            nb++;
            r = Math.random();

            double valueQ=this.serveur.get(0).getQ();//On initialise la valeur de Q pour le traitement

            for(int i=0;i<this.serveur.size();i++){

            if(r<=valueQ){
                this.serveur.get(i).addRequete(this.getCoordinateur().get(0));
                this.comp_requ[i]++;
                nb++;
                if(requete.getDateEntreeCourante()==0){ //la condition n'a pas de requete précedente
                    requete.setId(this.getCoordinateur().get(0).getId());
                    requete.setDateEntreeInitiale(this.getCoordinateur().get(0).getDateEntreeInitiale());
                    requete.setDateEntreeCourante(this.serveur.get(i).getServeur().get(0).getDateSortie());
                    requete.setDateSortie(requete.getDateEntreeCourante() + this.serveur.get(i).getTemps());

                }else{//la condition a la requete précedente
                    if (this.serveur.get(i).getServeur().get(0).getDateSortie() < requete.getDateSortie()){
                        requete.setId(this.getCoordinateur().get(0).getId());
                        requete.setDateEntreeInitiale(this.getCoordinateur().get(0).getDateEntreeInitiale());
                        requete.setDateEntreeCourante(requete.getDateSortie());
                        requete.setDateSortie( requete.getDateEntreeCourante()+ this.serveur.get(i).getTemps());
                    }else{
                        requete.setId(this.getCoordinateur().get(0).getId());
                        requete.setDateEntreeInitiale(this.getCoordinateur().get(0).getDateEntreeInitiale());
                        requete.setDateEntreeCourante(this.serveur.get(i).getServeur().get(0).getDateSortie());
                        requete.setDateSortie(requete.getDateEntreeCourante() +this.serveur.get(i).getTemps());
                    }
                }
                this.serveur.get(i).getServeur().get(0).setDateEntreeCourante(requete.getDateEntreeCourante());
                this.serveur.get(i).getServeur().get(0).setDateSortie(requete.getDateSortie());
                this.getCoordinateur().remove(0);

                //la condition de sortir
                r1 = Math.random();

                if(this.serveur.get(i).getP()==0){
                    sortie.add(requete);
                    if(requete.getDateSortie()<=this.getDuree()){
                    this.comp_requ_sort[i]++;
                    }
                }else{
                    if(r1<=this.serveur.get(i).getP()){
                        this.getCoordinateur().add(requete);
                        this.trierCoordinateur();
                    }else{
                        sortie.add(requete);
                        if(requete.getDateSortie()<=this.getDuree()){
                            this.comp_requ_sort[i]++;
                        }
                    }
                }

                this.serveur.get(i).getServeur().remove(0);


             }


            }

        }
        System.out.println("Le nombre de requêtes entrées dans le système durant la période de simulation est : "+nb);
        //System.out.println("Pour chaque serveur1, le nombre de requêtes traitées est : "+nb1);
        //System.out.println("Pour chaque serveur2, le nombre de requêtes traitées est : "+nb2);
    }

    //supprimer les requetes qui ont dateSortie > duree
    public void sortieALaFin(){
        for(int i=0;i<this.getSortie().size();i++){
            if(this.getSortie().get(i).getDateSortie()>this.getDuree())
                this.getSortie().remove(i);
        }
    }

    //le parametre id est pour extraire la requete du tableau sortie,s'il n'existe pas dans le tableau,renvoie null
    public Requete extraireRequete(int id){
        for(int i=0;i<this.getSortie().size();i++) {
            if(this.getSortie().get(i).getId() == id) return this.getSortie().get(i);
        }
        return null;
    }

    //calculer le nombre des requetes dans le systeme à chaque instant
    public String nbRequetesMilliseconde(ArrayList<Requete> r) {
        int nb;
        String contenu = "";
        Requete requete;
        for (int i = 1; i <= this.getDuree(); i++) {
            nb = 0;
            for (int j = 0; j < r.size(); j++) {//ici j(id de la requete)
                requete = this.extraireRequete(j);
                if(requete != null){
                    if (requete.getDateEntreeInitiale() < i&&requete.getDateSortie() > i) nb++;
                }else{
                    if (r.get(j).getDateEntreeInitiale() < i) nb++;
                }
            }
            contenu += i + " " + nb + "\n";
        }
        return contenu;
    }




    public void enregistrerLesDonnees(String filename, String partieNom, String contenu) {
        try {
            String filepath = System.getProperty("user.dir") + File.separator + "Data/" + filename
                    + String.valueOf(partieNom) + ".dat";
            System.out.println("Ecriture du fichier : " + filepath);
            FileWriter fw = new FileWriter(filepath);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(contenu);
            bw.close();
        } catch ( IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        //Construction d une liste de serveurs avec paramètres
        ArrayList<Serveur> serveur=new ArrayList<>(2);
        Serveur server1=new Serveur(0,0.5,0.5,100,new ArrayList<>(1),0);
        Serveur server2=new Serveur(1,0.5,0.5,200,new ArrayList<>(1),0);
        serveur.add(server1);
        serveur.add(server2);


        ReseauFilesAttentes rfa = new ReseauFilesAttentes(100000,10,serveur);
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
        System.out.println();
        rfa.lesDonneesCoordinateur(0.006);
        System.out.println();
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
        System.out.println();
        System.out.println("Le nombre de requetes presentes dans le système : "+rfa.getCoordinateur().size());
        System.out.println();
        ArrayList<Requete> c =  new ArrayList<>();//copie tout les donnees du table coordinateur dans le table c
        for(int i=0;i<rfa.getCoordinateur().size();i++){
            c.add(new Requete(rfa.getCoordinateur().get(i).getId(),rfa.getCoordinateur().get(i).getDateEntreeInitiale(),rfa.getCoordinateur().get(i).getDateEntreeCourante(),rfa.getCoordinateur().get(i).getDateSortie()));
        }

        rfa.processus();
        rfa.sortieALaFin();

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        //Enregistrement des donnes

        //Le nombre de requetes envoyees par le coordinateur vers chaque serveur

        for(int i=0;i<serveur.size();i++){
            rfa.enregistrerLesDonnees("Le nobre de requetes envoyées au serveur numero "+(i+1)+") ",serveur.get(i).getP()+" lambda=0.006 ",String.valueOf(rfa.comp_requ[i]));
        }

        //Le temps de traitement de chaque requete
        String[][] t=rfa.tempsTraitementRequete(rfa.sortie);
        String contenu="";
        for(int i=0;i<t.length;i++){
            contenu+=t[i][0]+" "+t[i][1]+"\n";
        }

        rfa.enregistrerLesDonnees("leTempsDeTraitementDeChaqueRequete"," lambda=0.006 ",contenu);
        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
        System.out.println();

        //Le temps dans le système de chaque requ



//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
        System.out.println();
        System.out.println("Le nombre de requêtes sorties dans le systeme durant la periode de simulation est : "+rfa.getSortie().size());
        System.out.println();
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
        System.out.println();
        //Le nombre de requetes envoyees a chaque serveur
        for(int i=0;i<serveur.size();i++){
            System.out.println("Le nombre de requetes envoyees au serveur numero "+(i+1)+" est "+rfa.comp_requ[i]);
        }
        System.out.println();
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
        System.out.println();

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
        System.out.println();
        //Le temps de traitement de chaque requete
        String [][] tableau=rfa.tempsTraitementRequete(rfa.sortie);

        for(int i=0;i<tableau.length;i++){

            System.out.println("Le temps de traitement de la requete numero "+tableau[i][0]+" est "+tableau[i][1]);

        }

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        System.out.println();
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
        System.out.println();

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        //Le nombre de requetes que chaque serveur a traité et a pu repondre
        for(int i=0;i<serveur.size();i++){
          System.out.println("Le nombre de requetes traitées (sorties) de serveur numero "+(i+1)+" est "+rfa.comp_requ_sort[i]);
        }
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        System.out.println();
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
        System.out.println();


        System.out.println("Le temps  moyen de traitement des requetes est: "+rfa.leTempsMoyen());

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        System.out.println();
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
        System.out.println();


//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




    }
}


