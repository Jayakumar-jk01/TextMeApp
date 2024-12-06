package com.jayakumar.Repository;

import com.jayakumar.folders.Folder;
import com.jayakumar.folders.UnreadEmailStats;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UnreadEmailRepository extends CassandraRepository<UnreadEmailStats,String> {

    List<UnreadEmailStats> findAllById(String id);


    @Query("update unread_email_stats set unreadcount = unreadcount +1 where user_id=?0 and label=?1")
    public void incrementunreadcount(String user_id,String label);

    @Query("update unread_email_stats set unreadcount = unreadcount -1 where user_id=?0 and label=?1")
    public void decrementunreadcount(String user_id,String label);
}
