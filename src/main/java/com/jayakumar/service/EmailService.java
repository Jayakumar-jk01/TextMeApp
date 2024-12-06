package com.jayakumar.service;


import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.jayakumar.Repository.EmailListItemRepository;
import com.jayakumar.Repository.EmailRepository;
import com.jayakumar.Repository.UnreadEmailRepository;
import com.jayakumar.email.Email;
import com.jayakumar.emailList.EmailListItem;
import com.jayakumar.emailList.EmailListItemKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EmailListItemRepository emailListItemRepository;

    @Autowired
    private UnreadEmailRepository unreadEmailRepository;


    public void sendMail(String from, List<String> to, String subject, String body)
    {
        Email email=new Email();

        email.setFrom(from);
        email.setTo(to);
        email.setSubject(subject);
        email.setBody(body);
        email.setId(Uuids.timeBased());

        emailRepository.save(email);

        to.forEach(toId->{
            EmailListItem item = createEmailListItem(to, subject, toId, email,"Inbox");

            emailListItemRepository.save(item);

            unreadEmailRepository.incrementunreadcount(toId,"Inbox");
        });

       EmailListItem sentEmailListItem= createEmailListItem(to,subject,from,email,"Sent");

       sentEmailListItem.setUnread(false);

       emailListItemRepository.save(sentEmailListItem);

    }

    private EmailListItem createEmailListItem(List<String> to, String subject, String ownerId, Email email,String folderName) {
        EmailListItemKey key=new EmailListItemKey();
        key.setId(ownerId);
        key.setLabel(folderName);
        key.setTimeUUID(email.getId());

        EmailListItem item=new EmailListItem();
        item.setKey(key);
        item.setTo(to);
        item.setSubject(subject);
        item.setUnread(true);
        return item;
    }
}
