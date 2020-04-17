package site.keyu.minigit.controller;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import site.keyu.minigit.config.GitEnv;
import site.keyu.minigit.pojo.BranchInfo;
import site.keyu.minigit.pojo.ErrorNotice;
import site.keyu.minigit.pojo.SuccessInfo;
import site.keyu.minigit.service.GitService;
import site.keyu.minigit.service.RepositoryService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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

    private Logger logger = LoggerFactory.getLogger(RepositoryController.class);

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
                         @PathVariable("repo") String repo){

        Repository repository = this.repositoryService.buildRepo(group,repo);


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
        return new SuccessInfo(data);
    }

    /**
     * 获取指定分支的commit信息
     * @param group
     * @param repo
     * @param branch
     * @return
     * @throws IOException
     * @throws NoHeadException
     * @throws GitAPIException
     */
    @GetMapping(path = "/{group}/{repo}/commits")
    @ResponseBody
    public Object commit(@PathVariable("group") String group,
                         @PathVariable("repo") String repo,
                         @RequestParam(name = "branch",required = false) String branch) throws IOException {

        Repository repository = this.repositoryService.buildRepo(group,repo);

        Map<String,Object> map = new HashMap<>();
        List<Object> commitsInfo = new ArrayList<>();
        Iterable<RevCommit> log;
        if (branch != null){
            Ref ref = repository.getRef("/refs/heads/"+branch);
            if(ref == null){
                return new ErrorNotice("branch not found:"+branch);
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
        return new SuccessInfo(map);
    }

    /**
     * 提供repo的压缩包下载
     * @param group
     * @param repo
     * @return
     */
    @GetMapping(path = "/{group}/{repo}/download")
    public StreamingResponseBody download(@PathVariable("group") String group,
                                          @PathVariable("repo") String repo,
                                          HttpServletResponse response){

        try {
            String downloadPath = this.repositoryService.zipRepo(group,repo);
            response.setHeader("Content-Disposition", "attachment;filename=" + repo + ".zip");
            final InputStream is = new FileInputStream(downloadPath);
            return outputStream -> {
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = is.read(data,0,data.length)) != -1){
                    outputStream.write(data,0,nRead);
                }
                outputStream.flush();
                outputStream.close();
                //删除缓冲区的压缩文件
                //TODO

            };
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return null;
    }

}
