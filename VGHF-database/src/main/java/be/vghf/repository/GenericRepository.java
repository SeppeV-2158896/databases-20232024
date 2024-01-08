package be.vghf.repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class GenericRepository {
    public GenericRepository(){
    }

    public static <T> void save(T object){
        EntityManager em = EntityManagerSingleton.getInstance();

        em.getTransaction().begin();
        em.persist(object);
        em.getTransaction().commit();
    }

    public static <T> List<T> query(CriteriaQuery<T> query){
        return EntityManagerSingleton.getInstance().createQuery(query).getResultList();
    }

    public static <T> void delete(T object){
        EntityManager em = EntityManagerSingleton.getInstance();

        em.getTransaction().begin();
        em.remove(object);
        em.getTransaction().commit();
    }

    public static <T> void update(T object){
        EntityManager em = EntityManagerSingleton.getInstance();

        em.getTransaction().begin();
        em.merge(object);
        em.getTransaction().commit();
    }
}
