package com.jayakumar.controller;


import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.jayakumar.Repository.EmailListItemRepository;
import com.jayakumar.Repository.FolderRepository;
import com.jayakumar.Repository.UnreadEmailRepository;
import com.jayakumar.emailList.EmailListItem;
import com.jayakumar.folders.Folder;
import com.jayakumar.folders.UnreadEmailStats;
import com.jayakumar.service.FolderService;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class InboxController {

    @Autowired
    private FolderService folderService;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private EmailListItemRepository emailListItemRepository;




    @Autowired
    private UnreadEmailRepository unreadEmailRepository;


    @GetMapping(value = "/")
    public String homepage(@AuthenticationPrincipal OAuth2User principal, Model model, @RequestParam(required = false) String folder )
    {

        if(principal==null|| principal.getAttribute("login").toString().isEmpty()) {


            return "index";
        }



        String userId=principal.getAttribute("login").toString();

        //fetch folders

        List<Folder> userFolders=folderRepository.findAllById(userId);

        model.addAttribute("userFolders",userFolders);

        List<Folder> defaultFolders=folderService.fetchDefaultFolders(userId);

        model.addAttribute("defaultFolders",defaultFolders);

        model.addAttribute("stats",folderService.mapCountToLabels(userId));

        model.addAttribute("userName", principal.getAttribute("login"));


        //fetch messages
                String folderLabel;

        if(folder==null)
        {
            folderLabel="Inbox";
        }
        else{
            folderLabel=folder.toString();
        }



        List<EmailListItem> emailList=emailListItemRepository.
                findAllByKey_IdAndKey_Label(userId,folderLabel);

        PrettyTime p=new PrettyTime();

        emailList.stream().forEach(email->{
            UUID uuid=email.getKey().getTimeUUID();
            Date emailDateTime=new Date(Uuids.unixTimestamp(uuid));
            email.setAgoTime(p.format(emailDateTime));
        });

        model.addAttribute("emailList",emailList);

        model.addAttribute("folderName",folderLabel);





        return "inbox-page";

    }
}
