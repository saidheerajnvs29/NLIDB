package org.ncr.nlidb.repository;

import org.springframework.stereotype.Repository;
import org.ncr.nlidb.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface BankDetailsRepository extends JpaRepository<BankDetails,Long>
{
	
}
