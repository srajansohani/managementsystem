package com.Bank.managementSystem.repository;
import java.util.List;

import com.Bank.managementSystem.Exception.DuplicateAccountException;
import org.springframework.stereotype.Repository;
import com.Bank.managementSystem.entity.BankUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class bankUserManager {
    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public BankUser create(String name, String accountType,Long phone) {
        BankUser user = findUserByNameAndCredentials(name,phone);
        if (user != null) {
            // User exists, add the new account
            if (accountType.equalsIgnoreCase(BankUser.CURRENT_ACCOUNT) && user.hasCurrentAccount()) {
                throw new DuplicateAccountException("A current account already exists. You can only have one current account.");
            } else if (accountType.equalsIgnoreCase(BankUser.SAVINGS_ACCOUNT) && user.hasSavingsAccount()) {
                throw new DuplicateAccountException("A savings account already exists. You can only have one savings account.");
            }
            user.addAccount(accountType);
            entityManager.merge(user);
        } else {
            // User does not exist, create a new user
            user = new BankUser(name, accountType);
            entityManager.persist(user);
        }

        return user;
    }

    private BankUser findUserByNameAndCredentials(String name, Long mobileNumber) {
        List<BankUser> users = entityManager.createNamedQuery("BankUser.findAll", BankUser.class).getResultList();
        for (BankUser user : users) {
            if (user.getName().equals(name) && user.getMobileNumber().equals(mobileNumber)) {
                return user;
            }
        }
        return null;
    }

    public BankUser findById(int id){
        return entityManager.find(BankUser.class, id);
    }

    public BankUser update(BankUser BankUser)  {return entityManager.merge(BankUser);}

    public void delete(int id){
        BankUser BankUser = findById(id);
        entityManager.remove(BankUser);
    }

    //This is done using JPQL as the findall method is not available in JPA that is why we will use this
    public List<BankUser> findAll(){
       TypedQuery<BankUser> namedQuery = entityManager.createNamedQuery("BankUser.findAll", BankUser.class);
       return namedQuery.getResultList();
    }

    @Transactional
    public void clearTable() {
        entityManager.createQuery("DELETE FROM Account").executeUpdate();
        entityManager.createQuery("DELETE FROM BankUser").executeUpdate();
    }
}

