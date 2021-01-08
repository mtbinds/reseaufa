import java.util.ArrayList;

public class Serveur {

    private int id;
    private double q;//Les probabilites d entree au serveur
    private double p;//Les probabilites de sortie de serveur
    private double temps;
    private ArrayList<Requete> serveur;
    private int comprequ;

    public Serveur(int id,double q,double p,double temps,ArrayList<Requete> serveur,int comprequ){

        this.id=id;
        this.q=q;
        this.p=p;
        this.temps=temps;
        this.serveur=serveur;
        this.comprequ=comprequ;

    }

    //Les getteurs

    public double getQ(){
        return this.q;
    }
    public double getP(){
        return this.p;
    }

    public double getTemps(){
        return this.temps;
    }

    public ArrayList<Requete> getServeur(){
        return serveur;
    }

    //Les setteurs
    public void setQ(double q){
        this.q=q;
    }

    public void addRequete(Requete requete){
        this.serveur.add(requete);
    }

}
