package com.jayakumar.Repository;

import com.jayakumar.folders.Folder;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FolderRepository extends CassandraRepository<Folder,String> {

    List<Folder> findAllById(String id);
}
