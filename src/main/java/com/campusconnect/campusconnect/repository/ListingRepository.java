package com.campusconnect.campusconnect.repository;
import com.campusconnect.campusconnect.entity.Listing; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ListingRepository extends JpaRepository<Listing,Long>{
 List<Listing> findAllByOrderByCreatedAtDesc(); List<Listing> findByCategoryOrderByCreatedAtDesc(String category);
}