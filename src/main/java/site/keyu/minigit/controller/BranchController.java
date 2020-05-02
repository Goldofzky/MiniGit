package site.keyu.minigit.controller;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevWalk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import site.keyu.minigit.pojo.BranchInfo;
import site.keyu.minigit.pojo.ErrorNotice;
import site.keyu.minigit.pojo.SuccessInfo;
import site.keyu.minigit.service.BranchService;
import site.keyu.minigit.service.RepositoryService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BranchController {

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    private BranchService branchService;


    /**
     * 获取指定仓库分支的基本信息
     * @param group
     * @param repo
     * @return
     * @throws IOException
     */
    @GetMapping(path = "/api/{group}/{repo}/branch")
    @ResponseBody
    public Object branch(@PathVariable("group") String group,
                         @PathVariable("repo") String repo){

        Repository repository = this.repositoryService.buildRepo(group,repo);


        List<Object> data = new ArrayList<>();
        RevWalk walk = new RevWalk(repository);
        //获取分支信息
        List<BranchInfo> branchInfos = this.branchService.getBranches(repository);
        for (BranchInfo branchInfo:branchInfos
        ) {
            Map<String,String> map = new HashMap<>();
            map.put("ref",branchInfo.getRef().getName());
            map.put("commitId",branchInfo.getHeadCommit().getName());
            map.put("commitMessage",branchInfo.getHeadCommit().getShortMessage());
            data.add(map);
        }
        return new SuccessInfo(data);
    }

    @PostMapping(path = "/api/{group}/{repo}/branch")
    @ResponseBody
    public Object addBranch(@PathVariable("group") String group,
                            @PathVariable("repo") String repo,
                            @RequestParam("name") String name){
        Repository repository = this.repositoryService.buildRepo(group,repo);
        Ref ref = this.branchService.addBranch(repository,name);
        if (ref == null){
            return  new ErrorNotice("failed to add branch:"+name);
        }
        return new SuccessInfo(ref.getName());
    }


    @DeleteMapping(path = "/api/{group}/{repo}/branch")
    @ResponseBody
    public Object removeBranch(@PathVariable("group") String group,
                            @PathVariable("repo") String repo,
                            @RequestParam("name") String name){
        Repository repository = this.repositoryService.buildRepo(group,repo);
        List<String> strs = this.branchService.removeBranch(repository,name);
        if (strs == null){
            return  new ErrorNotice("failed to remove branch:"+name);
        }
        return new SuccessInfo(strs.get(0));
    }

}
