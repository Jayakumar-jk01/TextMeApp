package com.jayakumar.Repository;

import com.jayakumar.emailList.EmailListItemKey;
import com.jayakumar.emailList.EmailListItem;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EmailListItemRepository extends CassandraRepository<EmailListItem, EmailListItemKey> {

//    List<EmailListItem> findAllById(EmailListItemKey id);

     List<EmailListItem> findAllByKey_IdAndKey_Label(String id,String label);
}
