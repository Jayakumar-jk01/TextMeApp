package com.jayakumar.controller;


import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.jayakumar.Repository.EmailListItemRepository;
import com.jayakumar.Repository.EmailRepository;
import com.jayakumar.Repository.FolderRepository;
import com.jayakumar.Repository.UnreadEmailRepository;
import com.jayakumar.email.Email;
import com.jayakumar.emailList.EmailListItem;
import com.jayakumar.emailList.EmailListItemKey;
import com.jayakumar.folders.Folder;
import com.jayakumar.folders.UnreadEmailStats;
import com.jayakumar.service.FolderService;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller

public class EmailViewController {
    @Autowired
    private FolderService folderService;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private EmailRepository emailRepository;

//    @Autowired
//    private EmailListItemKey emailListItemKey;

    @Autowired
    private EmailListItemRepository emailListItemRepository;

    @Autowired
    private UnreadEmailRepository unreadEmailRepository;



    @GetMapping(value = "/emails/{id}")
    public String emailView(
            @RequestParam String folder,
            @AuthenticationPrincipal OAuth2User principal, Model model, @PathVariable UUID id)
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



         Optional<Email> optionalEmail=emailRepository.findById(id);
        model.addAttribute("userName", principal.getAttribute("login"));

         if(optionalEmail.isEmpty())
         {
             return  "inbox-page";
         }

         Email email=optionalEmail.get();
         String toIds=String.join(",",email.getTo());

         //check if user is allowed to see email

        if(! userId.equals(email.getFrom()) && !email.getTo().contains(userId))
        {
            return "redirect:/";
        }

         model.addAttribute("email",email);
         model.addAttribute("toIds",toIds);

        EmailListItemKey key=new EmailListItemKey();
        key.setId(userId);
        key.setLabel(folder);
        key.setTimeUUID(email.getId());

      Optional<EmailListItem> optionalEmailListItem= emailListItemRepository.findById(key);


      if(optionalEmail.isPresent())
      {
          EmailListItem emailListItem=optionalEmailListItem.get();

          if(emailListItem.isUnread())
          {
              emailListItem.setUnread(false);
              emailListItemRepository.save(emailListItem);

              unreadEmailRepository.decrementunreadcount(userId,folder);
          }
      }

        model.addAttribute("stats",folderService.mapCountToLabels(userId));





        return "email-page";

    }
}
