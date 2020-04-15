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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RepositoryService {

    @Autowired
    GitService gitService;

    @Autowired
    private GitEnv gitEnv;

    private Logger logger = LoggerFactory.getLogger(RepositoryService.class);

    /**
     * 获取仓库的分支基本信息
     * @param repo
     * @return
     */
    public List<BranchInfo> getBranches(Repository repo){
        List<Ref> branchRefs =  gitService.branch(repo);//分支的引用
        RevWalk revWalk = new RevWalk(repo);
        List<BranchInfo> branchesInfos = new ArrayList<>();
        for (Ref ref:branchRefs) {
            try {
                RevCommit revCommit = revWalk.parseCommit(ref.getObjectId());
                BranchInfo branchInfo = new BranchInfo();
                branchInfo.setHeadCommit(revCommit);
                branchInfo.setRef(ref);
                branchesInfos.add(branchInfo);
            }catch (Exception e){
                logger.error(e.getMessage());
            }
        }
        return branchesInfos;
    }

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
            this.logger.info(e.getMessage());
        }
        return null;
    }

}

