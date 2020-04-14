package site.keyu.minigit.controller;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import site.keyu.minigit.config.GitEnv;
import site.keyu.minigit.pojo.BranchInfo;
import site.keyu.minigit.pojo.ErrorNotice;
import site.keyu.minigit.service.GitService;
import site.keyu.minigit.service.RepositoryService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仓库基本信息
 */
@Controller
public class RepositoryController {

    @Autowired
    private GitEnv gitEnv;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private GitService gitService;

    /**
     * 获取指定仓库分支的基本信息
     * @param group
     * @param repo
     * @return
     * @throws IOException
     */
    @GetMapping(path = "/{group}/{repo}/branch")
    @ResponseBody
    public Object branch(@PathVariable("group") String group,
                         @PathVariable("repo") String repo) throws IOException {
        String targetPath = "/" + group + "/" + repo;
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        repositoryBuilder.setMustExist(true);
        repositoryBuilder.setGitDir(new File( this.gitEnv.basepath + targetPath + "/.git"));//绑定某一仓库
        Repository repository = repositoryBuilder.findGitDir().build();


        List<Object> data = new ArrayList<>();
        RevWalk walk = new RevWalk(repository);
        //获取分支信息
        List<BranchInfo> branchInfos = this.repositoryService.getBranches(repository);
        for (BranchInfo branchInfo:branchInfos
             ) {
            Map<String,String> map = new HashMap<>();
            map.put("ref",branchInfo.getRef().getName());
            map.put("commitId",branchInfo.getHeadCommit().getName());
            map.put("commitMessage",branchInfo.getHeadCommit().getShortMessage());
            data.add(map);
        }
        return  data;
    }

    @GetMapping(path = "/{group}/{repo}/commit")
    @ResponseBody
    public Object commit(@PathVariable("group") String group,
                         @PathVariable("repo") String repo,
                         @RequestParam(name = "branch",required = false) String branch) throws IOException, NoHeadException, GitAPIException {

        String targetPath = "/" + group + "/" + repo;
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        repositoryBuilder.setMustExist(true);
        repositoryBuilder.setGitDir(new File( this.gitEnv.basepath + targetPath + "/.git"));
        Repository repository = repositoryBuilder.build();

        Map<String,Object> map = new HashMap<>();
        List<Object> commitsInfo = new ArrayList<>();
        Iterable<RevCommit> log;
        if (branch != null){
            Ref ref = repository.getRef("/refs/heads/"+branch);
            if(ref == null){
                return new ErrorNotice("没有该分支:"+branch);
            }
            log = this.gitService.log(repository,ref.getObjectId());
        }else{
            log = this.gitService.log(repository);
        }

        log.forEach((RevCommit commit) -> {
            Map<String,Object> temp = new HashMap<>();
            temp.put("message",commit.getShortMessage());
            temp.put("time",commit.getCommitTime());
            temp.put("authorName",commit.getCommitterIdent().getName());
            temp.put("authorEmail",commit.getCommitterIdent().getEmailAddress());
            temp.put("tree",commit.getTree().getName());
            temp.put("id",commit.getId().getName());
            commitsInfo.add(temp);
        });

        map.put("commits",commitsInfo);
        return map;
    }




}
