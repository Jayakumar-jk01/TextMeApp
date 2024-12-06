package com.jayakumar.controller;

import com.jayakumar.Repository.EmailRepository;
import com.jayakumar.Repository.FolderRepository;
import com.jayakumar.folders.Folder;
import com.jayakumar.service.EmailService;
import com.jayakumar.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ComposeController {
    @Autowired
    private FolderService folderService;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping(value = "/compose")
    public String composePage(@RequestParam(required = false) String to, @AuthenticationPrincipal OAuth2User principal, Model model)
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
        model.addAttribute("userName", principal.getAttribute("login"));



            List<String> uniqueToIds = getIds(to);

            model.addAttribute("toIds", String.join(",", uniqueToIds));
        model.addAttribute("stats",folderService.mapCountToLabels(userId));






        return "compose-page";

    }

    private static List<String> getIds(String to) {

        if(!StringUtils.hasText(to))
        {
            return new ArrayList<>();
        }
        String[] splitIds = to.split(",");

        List<String> uniqueToIds = Arrays.asList(splitIds).stream().map(id -> StringUtils.trimAllWhitespace(id))
                .filter(id -> StringUtils.hasText(id))
                .distinct()
                .collect(Collectors.toList());
        return uniqueToIds;
    }




    @PostMapping("/sendEmail")
    public ModelAndView sendEmail(
            @RequestBody MultiValueMap<String,String> formData,
            @AuthenticationPrincipal OAuth2User principal
            )
    {
        if(principal==null|| principal.getAttribute("login").toString().isEmpty()) {


            return new ModelAndView("redirect:/");
        }

        String from=principal.getAttribute("login").toString();
        String subject= formData.getFirst("subject");
        List<String> toIds= getIds(formData.getFirst("toIds"));
        String body= formData.getFirst("body");

        emailService.sendMail(from,toIds,subject,body);

        return new ModelAndView("redirect:/");
    }
}
