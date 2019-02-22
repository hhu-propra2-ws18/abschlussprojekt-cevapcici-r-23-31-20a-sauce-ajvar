package de.hhu.sharing.RepositoryTests;

import de.hhu.sharing.data.ConflictRepository;
import de.hhu.sharing.data.ItemRepository;
import de.hhu.sharing.data.UserRepository;
import de.hhu.sharing.model.Address;
import de.hhu.sharing.model.Conflict;
import de.hhu.sharing.model.Item;
import de.hhu.sharing.model.User;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ConflictRepositoryTest {

    @Autowired
    ConflictRepository conflictRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    ItemRepository itemRepo;



    @Test
    public void testFindAll(){
        LocalDate date = LocalDate.of(2000,1,1);
        Address address = new Address("unistrase","duesseldorf", 40233);
        User user1 = new User("user1","password", "role", "lastnmae", "forname", "email",date,address);
        User user2 = new User("user2","password", "role", "lastnmae", "forname", "email",date,address);

        userRepo.save(user1);
        userRepo.save(user2);

        User userEntity1 = userRepo.findByUsername("user1").get();
        User userEntity2 = userRepo.findByUsername("user2").get();

        Item item = new Item("apfel", "lecker",1,1 ,user1 );
        itemRepo.save(item);
        Item itemEntity = itemRepo.findById(item.getId()).get();
        Conflict conflict1 = new Conflict("problem", itemEntity, userEntity1,userEntity2);
        Conflict conflict2 = new Conflict("problem", itemEntity, userEntity2,userEntity1);
        conflictRepo.save(conflict1);
        conflictRepo.save(conflict2);
        Assertions.assertThat(conflictRepo.findAll().size()).isEqualTo(2);
    }
}
