package com.cjcc.yakalabs.sakurasaki.repository;

import com.cjcc.yakalabs.sakurasaki.model.RedeemedReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedeemedRewardRepository extends JpaRepository<RedeemedReward, Long> {
    List<RedeemedReward> findByCustomerIdOrderByRedeemedDateDesc(Long customerId);
}
