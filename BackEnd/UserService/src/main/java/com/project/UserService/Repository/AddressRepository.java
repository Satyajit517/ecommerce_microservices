package com.project.UserService.Repository;

import com.project.UserService.Entity.Address;
import com.project.UserService.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    
    List<Address> findByUser(User user);
    

    Optional<Address> findByIdAndUser(UUID id, User user);

    boolean existsByIdAndUser(UUID id, User user);

    boolean existsByUserAndIsDefaultTrue(User user);

    Optional<Address> findByIdAndUserId(UUID addressId, UUID userId);
}
