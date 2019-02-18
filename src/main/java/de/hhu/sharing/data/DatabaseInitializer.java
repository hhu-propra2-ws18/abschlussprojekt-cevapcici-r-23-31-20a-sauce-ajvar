package de.hhu.sharing.data;

import com.github.javafaker.Faker;
import de.hhu.sharing.model.Address;
import de.hhu.sharing.model.Item;
import de.hhu.sharing.model.Request;
import de.hhu.sharing.model.User;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Component
public class DatabaseInitializer implements ServletContextInitializer {

    @Autowired
    private UserRepository users;

    @Autowired
    private ItemRepository items;

    @Autowired
    private RequestRepository requests;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        final Faker faker = new Faker(Locale.GERMAN);

        for(int i = 1; i < 21; i++){
            Address address = new Address(
                    faker.address().streetAddress(),
                    faker.gameOfThrones().city(),
                    Integer.parseInt(faker.address().zipCode()));
            User user = new User("user" + i, encoder.encode("password" + i), "ROLE_USER",
                    faker.gameOfThrones().house(),
                    faker.gameOfThrones().character(),
                    faker.internet().emailAddress(),
                    faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    address);
            users.save(user);
            for(int j = 0; j < faker.number().numberBetween(1,5); j++){
                Item item = new Item(faker.gameOfThrones().dragon(),
                        String.join("\n", faker.lorem().paragraphs(5)),
                        faker.number().numberBetween(1,1000),
                        faker.number().numberBetween(1,1000),
                        user);
                items.save(item);
            }
        }

        for(User user : users.findAll()){
            List<Item> itemList = items.findFirst2ByLenderNot(user);
            Request request1 = new Request(
                    faker.date().past(10,TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    faker.date().future(10,TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    user);
            Request request2 = new Request(
                    faker.date().past(10,TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    faker.date().future(10,TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    user);
            requests.save(request1);
            itemList.get(0).addToRequests(request1);
            requests.save(request2);
            itemList.get(1).addToRequests(request2);
            items.saveAll(itemList);

        }
    }
}
