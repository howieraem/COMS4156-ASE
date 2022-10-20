package com.lgtm.easymoney.repositories;

import com.lgtm.easymoney.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * groups repo schema.
 */
public interface GroupRepository extends JpaRepository<Group, Long> {
}
