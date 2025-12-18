package jakartamission.udbl.jakartamission.business;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakartamission.udbl.jakartamission.entities.Lieu;
import java.util.List;

/**
 * EJB Stateless pour gérer la logique métier des lieux
 */
@Stateless
@LocalBean
public class LieuEntrepriseBean {

    @PersistenceContext
    private EntityManager em;

    public void ajouterLieuEntreprise(String nom, String description, double latitude, double longitude) {
        Lieu lieu = new Lieu(nom, description, longitude, latitude);
        em.persist(lieu);
        em.flush(); // Force l'exécution immédiate
    }

    public List<Lieu> listerTousLesLieux() {
        return em.createQuery("SELECT L FROM Lieu L", Lieu.class).getResultList();
    }

    public void supprimerLieu(int id) {
        Lieu lieu = em.find(Lieu.class, id);
        if (lieu != null) {
            em.remove(lieu);
            em.flush(); // Force l'exécution immédiate
        }
    }

    public void modifierLieu(int id, String nom, String description, double latitude, double longitude) {
        Lieu lieu = em.find(Lieu.class, id);
        if (lieu != null) {
            lieu.setNom(nom);
            lieu.setDescription(description);
            lieu.setLatitude(latitude);
            lieu.setLongitude(longitude);
            em.merge(lieu);
            em.flush(); // Force l'exécution immédiate
        }
    }

    public Lieu trouverLieuParId(int id) {
        return em.find(Lieu.class, id);
    }
}
