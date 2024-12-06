package com.jayakumar.service;


import com.jayakumar.Repository.UnreadEmailRepository;
import com.jayakumar.folders.Folder;
import com.jayakumar.folders.UnreadEmailStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FolderService {



    @Autowired
    private UnreadEmailRepository unreadEmailRepository;

    public List<Folder> fetchDefaultFolders(String userId)
    {
        return Arrays.asList(
                new Folder(userId,"Inbox","bule"),
                new Folder(userId,"Sent","green"),
                new Folder(userId,"Important","red")

        );
    }

    public Map<String,Integer> mapCountToLabels(String userId){

        List<UnreadEmailStats> stats=unreadEmailRepository.findAllById(userId);
        return stats.stream().collect(Collectors.toMap(UnreadEmailStats::getLabel,UnreadEmailStats::getUnreadCount));
    }
}
