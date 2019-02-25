package de.hhu.sharing.services;

import de.hhu.sharing.data.ItemRepository;
import de.hhu.sharing.model.lendableItem;
import de.hhu.sharing.model.Period;
import de.hhu.sharing.model.User;
import de.hhu.sharing.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ItemService{

    @Autowired
    private ItemRepository items;

    @Autowired
    private ConflictService conflictService;

    @Autowired
    private StorageService storageService;


    public void create(String name, String description, Integer rental, Integer deposit, User user, MultipartFile file) {
        lendableItem lendableItem = new lendableItem(name, description, rental, deposit, user);
        items.save(lendableItem);
        if(file!=null) {
        	storageService.storeItem(file, lendableItem);
        }else {
        	System.out.println("No picture");
       }
    }

    public void edit(Long id, String name, String description, Integer rental, Integer deposit, User user) {
        lendableItem lendableItem = this.get(id);
        lendableItem.setName(name);
        lendableItem.setDescription(description);
        lendableItem.setRental(rental);
        lendableItem.setDeposit(deposit);
        lendableItem.setOwner(user);
        items.save(lendableItem);
    }

    public void delete(Long id) {
        lendableItem lendableItem = this.get(id);
        items.delete(lendableItem);
    }

    public lendableItem get(Long id) {
        return this.items.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("lendableItem not found!"));
    }

    public List<lendableItem> getAll() {
        return this.items.findAll();
    }

    public lendableItem getFromRequestId(Long requestId) {
        return this.items.findByRequests_id(requestId)
                .orElseThrow(
                        () -> new RuntimeException("lendableItem not found!"));
    }

    public List<lendableItem> getAllIPosted(User user) {
        return this.items.findAllByLender(user);
    }


    public List<lendableItem> getAllIRequested(User user) {
        return this.items.findAllByRequests_requester(user);

    }

    public List<lendableItem> searchFor(String query) {
        return this.items.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query,query);
    }

    public boolean isChangeable(Long id) {
        lendableItem lendableItem = this.get(id);
        return lendableItem.noPeriodsAndRequests() && conflictService.noConflictWith(lendableItem);
    }

    public boolean isAvailableAt(lendableItem lendableItem, LocalDate startdate, LocalDate enddate) {
        return lendableItem.isAvailableAt(new Period(startdate, enddate));
    }

    public boolean isOwner(Long id, User user) {
        lendableItem lendableItem = this.get(id);
        return lendableItem.getOwner() == user;
    }


    public List allDatesInbetween(lendableItem lendableItem){

        List <Period> allPeriods = lendableItem.getPeriods();
        List <LocalDate> allDates = new ArrayList<>();
        for(Period period : allPeriods){
            LocalDate current = period.getStartdate();
            List <LocalDate> periodDates = new ArrayList<>();
            while(!current.isAfter(period.getEnddate())) {
                periodDates.add(current);
                current = current.plusDays(1);
            }
            allDates.addAll(periodDates);


        }


        return allDates;
    }
}
