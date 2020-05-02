package site.keyu.minigit.service;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.keyu.minigit.config.GitEnv;
import site.keyu.minigit.pojo.BranchInfo;
import site.keyu.minigit.util.ZipUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class RepositoryService {

    @Autowired
    GitService gitService;

    @Autowired
    private GitEnv gitEnv;

    @Autowired
    private ZipUtil zipUtil;

    private Logger logger = LoggerFactory.getLogger(RepositoryService.class);


    /**
     * 获取绑定的Repository对象
     * @param group 组织名
     * @param repoName repo名
     * @return
     */
    public Repository buildRepo(String group,String repoName){
        String targetPath = "/" + group + "/" + repoName;
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        repositoryBuilder.setMustExist(true);
        repositoryBuilder.setGitDir(new File( this.gitEnv.basepath + targetPath + "/.git"));
        try{
            Repository repository = repositoryBuilder.build();
            return  repository;
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return null;
    }

    public String zipRepo(String group,String repoName){
        String targetPath = this.gitEnv.tempPath + "/" + group + "-" + repoName + "-" + System.currentTimeMillis() + ".zip";
        String sourcePath = this.gitEnv.basepath + "/" + group + "/" + repoName;
        try {
            OutputStream outputStream = new FileOutputStream(targetPath);
            zipUtil.toZip(sourcePath,outputStream);
            return targetPath;
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return null;
    }


}

