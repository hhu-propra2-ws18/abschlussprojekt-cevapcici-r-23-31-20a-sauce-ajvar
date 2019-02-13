package de.hhu.sharing.data;

import de.hhu.sharing.model.Address;
import de.hhu.sharing.model.Item;
import de.hhu.sharing.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Arrays;
import java.util.Date;

@Component
public class
DatabaseInitializer implements ServletContextInitializer {

    @Autowired
    private UserRepository users;

    @Autowired
    private ItemRepository items;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.out.println("Populating the database");

        User hans = mkUser("Hansi", "$2a$07$MIpz7Ns1kzEq66wpMQPAIuySvXaQrkkFgwkyetdJiKpFZUDy3I0hC","ROLE_USER", "Hans", "Hans", "test1@test.de",
                new Date(1990, 12, 12),
                mkAddress("Kaiser-Wilhelm-Allee 3","Leverkusen", 51373));
        Item mixer = mkItem("Mixer", "mixt Sachen", 5, 20,
                new Date(2019, 1, 1),
                new Date(2019, 2, 12),
                hans);

        User peter = mkUser("Nashorn",  "$2a$07$MIpz7Ns1kzEq66wpMQPAIuySvXaQrkkFgwkyetdJiKpFZUDy3I0hC","ROLE_USER", "Peti", "Peter","test2@test.de",
                new Date(1991, 12, 1),
                mkAddress("Berliner Ring 3","Wolfsburg",  38440));
        Item fahrrad = mkItem("Fahrrad", "Räder die fahren", 20, 200,
                new Date(2019, 1, 1),
                new Date(2019, 2, 12),
                peter);
        Item motorrad = mkItem("Motorrad", "Moter mit Rädern", 50, 2000,
                new Date(2019, 1, 1),
                new Date(2019, 2, 12),
                peter);

        User guenther = mkUser("Guenther", "$2a$07$MIpz7Ns1kzEq66wpMQPAIuySvXaQrkkFgwkyetdJiKpFZUDy3I0hC", "ROLE_ADMIN","Gurke","Guenther", "test3@test.de",
                new Date(1995, 8, 21),
                mkAddress("Dorfstrasse 50","Feusisberg, Schweiz",  8834));


        users.saveAll(Arrays.asList(hans,peter,guenther));
        items.saveAll(Arrays.asList(mixer,fahrrad,motorrad));
        peter.addToBorrowedItem(mixer);
        users.saveAll(Arrays.asList(hans,peter,guenther));

    }

    private Address mkAddress(String street, String city, int pc) {
        Address address = new Address();
        address.setStreet(street);
        address.setPostcode(pc);
        address.setCity(city);
        return address;
    }

    private User mkUser(String username, String password, String role, String lastname, String forename, String mail, Date birthdate, Address address) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        user.setLastname(lastname);
        user.setForename(forename);
        user.setMail(mail);
        user.setBirthdate(birthdate);
        user.setAddress(address);
        return user;
    }

    private Item mkItem(String name, String description, int rental, int deposit, Date startdate, Date enddate, User lender){
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setRental(rental);
        item.setDeposit(deposit);
        item.setStartdate(startdate);
        item.setEnddate(enddate);
        item.setLender(lender);
        return item;
    }

}
