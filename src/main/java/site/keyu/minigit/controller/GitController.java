package site.keyu.minigit.controller;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import site.keyu.minigit.config.GitEnv;
import site.keyu.minigit.service.GitService;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Git命令行操作
 */
@Controller
public class GitController {

    @Autowired
    private GitEnv gitEnv;

    @Autowired
    private GitService gitService;

    private Logger logger = LoggerFactory.getLogger(GitController.class);

    @GetMapping(path = "/hello")
    @ResponseBody
    public String index(){
        return "hello world";
    }


    @GetMapping("/git")
    @ResponseBody
    public Object basepath(){

        return this.gitEnv.basepath;
    }



}
