package org.ncr.nlidb.repository;

import org.ncr.nlidb.model.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionDetailsRepository extends JpaRepository<TransactionDetails,Long>,TransactionDetailsRepositoryCustom{

}
