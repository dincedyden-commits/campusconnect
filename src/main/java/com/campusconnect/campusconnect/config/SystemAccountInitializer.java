package com.campusconnect.campusconnect.config;

import com.campusconnect.campusconnect.entity.User;
import com.campusconnect.campusconnect.repository.UserRepository;
import com.campusconnect.campusconnect.repository.CommunityRepository;
import com.campusconnect.campusconnect.repository.CommunityMemberRepository;
import com.campusconnect.campusconnect.entity.Community;
import com.campusconnect.campusconnect.entity.CommunityMember;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SystemAccountInitializer {
    @Bean
    ApplicationRunner seedSystemAccounts(
            UserRepository users, CommunityRepository communities, CommunityMemberRepository members,
            PasswordEncoder encoder,
            @Value("${system.accounts.enabled:true}") boolean enabled,
            @Value("${system.official.password:}") String officialPassword,
            @Value("${system.admin.password:}") String adminPassword) {
        return args -> {
            if(!enabled) return;
            ensure(users,encoder,"campusconnect","Campus Connect Official","campusconnect@campusconnect.app","OFFICIAL",
                    officialPassword);
            ensure(users,encoder,"campusconnectadmin","Campus Connect Admin","admin@campusconnect.app","ADMIN",
                    adminPassword);
            User official=users.findByUsernameIgnoreCase("campusconnect").orElseThrow();
            seedOfficialCommunity(communities,members,official,"Campus Connect","GROUP","Official CampusConnect community. Managed by CampusConnect Official/Admin.");
            seedOfficialCommunity(communities,members,official,"Campus Connect Official","CHANNEL","Official CampusConnect announcements and verified updates.");
        };
    }

    private void seedOfficialCommunity(CommunityRepository communities, CommunityMemberRepository members, User official, String name, String type, String description){
        Community c=communities.findAllByOrderByCreatedAtDesc().stream().filter(x->name.equalsIgnoreCase(x.getName())&&type.equalsIgnoreCase(x.getType())).findFirst().orElse(null);
        if(c==null){c=new Community();c.setName(name);c.setType(type);c.setDescription(description);c.setOwner(official);c=communities.save(c);}
        if(!members.existsByCommunityIdAndUserId(c.getId(),official.getId())){CommunityMember cm=new CommunityMember();cm.setCommunity(c);cm.setUser(official);cm.setRole("OWNER");members.save(cm);}
    }

    private void ensure(UserRepository users, PasswordEncoder encoder, String username, String fullName,
                        String email, String role, String password) {
        if(users.findByUsernameIgnoreCase(username).isPresent()) return;
        User u=new User(); u.setUsername(username); u.setFullName(fullName); u.setEmail(email);
        u.setRole(role); u.setUniversity("CampusConnect");
        u.setBio(role.equals("OFFICIAL") ? "Official CampusConnect support and announcements." : "CampusConnect administration.");
        u.setPassword(encoder.encode(password==null||password.isBlank()?java.util.UUID.randomUUID().toString():password));
        users.save(u);
    }
}
