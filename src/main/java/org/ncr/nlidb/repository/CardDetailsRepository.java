package org.ncr.nlidb.repository;

import org.ncr.nlidb.model.CardDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardDetailsRepository extends JpaRepository<CardDetails,Long>,CardDetailsRepositoryCustom
{

}
