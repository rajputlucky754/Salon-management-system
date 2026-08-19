package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.Customer;
import com.cjcc.yakalabs.sakurasaki.model.RedeemedReward;
import com.cjcc.yakalabs.sakurasaki.repository.CustomerRepository;
import com.cjcc.yakalabs.sakurasaki.repository.RedeemedRewardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepo;
    private final RedeemedRewardRepository rewardRepo;

    public CustomerService(CustomerRepository customerRepo, RedeemedRewardRepository rewardRepo) {
        this.customerRepo = customerRepo;
        this.rewardRepo = rewardRepo;
    }

    public Customer createCustomer(String firstName, String lastName, String email, String phone) {
        if (customerRepo.existsByEmail(email)) {
            throw new RuntimeException("A customer with this email already exists.");
        }
        Customer customer = new Customer(firstName, lastName, email, phone);
        return customerRepo.save(customer);
    }

    public List<Customer> findAll() {
        return customerRepo.findAll();
    }

    public Optional<Customer> findById(Long id) {
        return customerRepo.findById(id);
    }

    public Optional<Customer> findByEmail(String email) {
        return customerRepo.findByEmail(email);
    }

    public List<Customer> searchByName(String keyword) {
        return customerRepo.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword);
    }

    public Customer updateCustomer(Long id, String firstName, String lastName, String email, String phone) {
        Customer c = customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        if (firstName != null && !firstName.isBlank()) c.setFirstName(firstName);
        if (lastName != null && !lastName.isBlank()) c.setLastName(lastName);
        if (email != null && !email.isBlank()) c.setEmail(email);
        if (phone != null) c.setPhone(phone);
        return customerRepo.save(c);
    }

    public void addLoyaltyPoints(Customer customer, int points) {
        if (customer.getLoyaltyPoints() == null) {
            customer.setLoyaltyPoints(0);
        }
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + points);
        evaluateTier(customer);
        customerRepo.save(customer);
    }

    public void redeemPoints(Customer customer, int pointsToDeduct, String rewardName) {
        if (customer.getLoyaltyPoints() == null || customer.getLoyaltyPoints() < pointsToDeduct) {
            throw new RuntimeException("Insufficient loyalty points for redemption.");
        }
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() - pointsToDeduct);
        evaluateTier(customer);
        customerRepo.save(customer);
        
        RedeemedReward reward = new RedeemedReward(customer, rewardName, pointsToDeduct);
        rewardRepo.save(reward);
    }

    public void markRewardAsUsed(Long rewardId) {
        RedeemedReward reward = rewardRepo.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Voucher not found."));
        reward.setUsed(true);
        rewardRepo.save(reward);
    }

    private void evaluateTier(Customer customer) {
        int points = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
        String newTier = "Bronze";
        if (points >= 3000) {
            newTier = "Platinum";
        } else if (points >= 1500) {
            newTier = "Gold";
        } else if (points >= 500) {
            newTier = "Silver";
        }
        customer.setMembershipTier(newTier);
    }

    public void deleteCustomer(Long id) {
        customerRepo.deleteById(id);
    }
}
