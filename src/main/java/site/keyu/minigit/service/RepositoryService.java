package site.keyu.minigit.service;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.keyu.minigit.pojo.BranchInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RepositoryService {

    @Autowired
    GitService gitService;

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

}

