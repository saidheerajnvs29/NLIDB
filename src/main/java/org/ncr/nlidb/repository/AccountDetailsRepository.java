package org.ncr.nlidb.repository;

import org.ncr.nlidb.model.AccountDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDetailsRepository extends JpaRepository<AccountDetails,Long>,AccountDetailsRepositoryCustom
{

}
