package site.keyu.minigit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
public class GitEnv {


    @Value("${site.keyu.git.basepath}")
    public String basepath;


}
