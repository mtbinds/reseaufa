

public class Requete{
    private int id;
    private double dateEntreeInitiale;
    private double dateEntreeCourante;
    private double dateSortie;

    public Requete(int id,double dateEntreeInitiale,double dateEntreeCourante,double dateSortie) {
        this.id = id;
        this.dateEntreeInitiale = dateEntreeInitiale;
        this.dateEntreeCourante = dateEntreeCourante;
        this.dateSortie = dateSortie;
    }

    //Les getteurs
    public int getId() {
        return this.id;
    }

    public double getDateEntreeInitiale() {
        return this.dateEntreeInitiale;
    }

    public double getDateEntreeCourante(){
        return this.dateEntreeCourante;
    }

    public double getDateSortie() {
        return this.dateSortie;
    }



    //Les setteurs
    public void setId(int id){
        this.id=id;
    }

    public void setDateEntreeInitiale(double datei){
        this.dateEntreeInitiale=datei;
    }
    public void setDateEntreeCourante(double datec){
        this.dateEntreeCourante=datec;
    }
    public void setDateSortie(double dates){
        this.dateSortie=dates;
    }








}
