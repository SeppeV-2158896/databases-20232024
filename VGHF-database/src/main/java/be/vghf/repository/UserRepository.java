package be.vghf.repository;

import be.vghf.domain.Game;
import be.vghf.domain.User;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserRepository implements Repository{
    public UserRepository(){}

    public static List<User> getUserByName(String name) {

        var criteriaBuilder = EntityManagerSingleton.getInstance().getCriteriaBuilder();
        var query = criteriaBuilder.createQuery(User.class);
        var root = query.from(User.class);

        query.where(criteriaBuilder.equal(root.get("username"), name));

        return GenericRepository.query(query);
    }

    public List<User> getAll() {
        var query = EntityManagerSingleton.getInstance().getCriteriaBuilder().createQuery(User.class);
        var root = query.from(User.class);

        query.select(root);

        return GenericRepository.query(query);
    }

    public List<User> getUserByAddress(String[] address){
        Set<User> users = new HashSet<>();

        for(String str : address){
            var criteriaBuilder = EntityManagerSingleton.getInstance().getCriteriaBuilder();
            var query = criteriaBuilder.createQuery(User.class);
            var root = query.from(User.class);

            //dit gaat nog niet werken
            query.where(criteriaBuilder.like(root.get("address"), "%" + str + "%"));

            users.addAll(GenericRepository.query(query));
        }
        return new ArrayList<>(users);
    }

    public static String hashPassword(String input){
        //source: https://howtodoinjava.com/java/java-security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Add password bytes to digest
            md.update(input.getBytes());

            // Get the hash's bytes
            byte[] bytes = md.digest();

            // This bytes[] has bytes in decimal format. Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            // Get complete hashed password in hex format
            return sb.toString();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
