package com.jayakumar;

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
import com.jayakumar.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.Arrays;

@SpringBootApplication
@RestController
public class TextMeApp {

	@Autowired

	private FolderRepository folderRepository;

	@Autowired
	private EmailListItemRepository emailListItemRepository;

	@Autowired
	private EmailRepository emailRepository;

	@Autowired
	private UnreadEmailRepository unreadEmailRepository;

	@Autowired
	private EmailService emailService;

	public static void main(String[] args) {
		SpringApplication.run(TextMeApp.class, args);
	}

	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}

	@PostConstruct
	public void init()
	{
		folderRepository.save(new Folder("Jayakumar-jk01", "Work","blue"));
		folderRepository.save(new Folder("Jayakumar-jk01", "Personal","green"));
		folderRepository.save(new Folder("Jayakumar-jk01", "Home","yellow"));



		for(int i=0;i<10;i++)
		{

			emailService.sendMail("Jayakumar-jk01",Arrays.asList("Jayakumar-jk01"),"hello "+i,"body"+i);



		}
	}

}
