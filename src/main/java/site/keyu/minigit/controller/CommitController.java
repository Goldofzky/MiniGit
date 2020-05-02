package site.keyu.minigit.controller;


import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import site.keyu.minigit.pojo.ErrorNotice;
import site.keyu.minigit.pojo.SuccessInfo;
import site.keyu.minigit.service.CommitService;
import site.keyu.minigit.service.GitService;
import site.keyu.minigit.service.RepositoryService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CommitController {
    @Autowired
    private CommitService commitService;

    @Autowired
    private GitService gitService;

    @Autowired
    private RepositoryService repositoryService;


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
    @GetMapping(path = "/api/{group}/{repo}/commits")
    @ResponseBody
    public Object getCommits(@PathVariable("group") String group,
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

    @GetMapping(path = "/api/{group}/{repo}/commit/info/{id}")
    @ResponseBody
    public Object commit(@PathVariable("group") String group,
                         @PathVariable("repo") String repo,
                         @PathVariable("id") String id
    ) throws Exception {

        Repository repository = this.repositoryService.buildRepo(group, repo);
        if (repository == null) {
            return new ErrorNotice("repo not found");
        }
        RevCommit revCommit = this.commitService.getRevCommitByCommitId(repository, id);
        if (revCommit == null) {
            return new ErrorNotice("commit not found");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("message", revCommit.getShortMessage());
        data.put("time", revCommit.getCommitTime());
        data.put("authorName", revCommit.getCommitterIdent().getName());
        data.put("authorEmail", revCommit.getCommitterIdent().getEmailAddress());
        data.put("tree", revCommit.getTree().getName());
        data.put("id", revCommit.getId().getName());

        //commit所做的修改
        List<DiffEntry> diffs = this.commitService.getDiffByCommitId(repository, id);
        if (diffs == null) {
            return new SuccessInfo(data);
            //return new ErrorNotice("git diff error");
        }
        List<Object> diffData = new ArrayList<>();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DiffFormatter df = new DiffFormatter(out);
        df.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
        df.setRepository(repository);
        for (DiffEntry diff : diffs) {

            Map<String, Object> map = new HashMap<>();
            df.format(diff);
            String diffText = out.toString("UTF-8");
            out.reset();
            map.put("newFile", diff.getNewPath());
            map.put("oldFile", diff.getOldPath());
            map.put("content", diffText);
            diffData.add(map);
        }
        data.put("diff", diffData);

        return new SuccessInfo(data);
    }

    @GetMapping(path = "/api/{group}/{repo}/commit/diff")
    @ResponseBody
    public Object diff(@PathVariable("group") String group,
                       @PathVariable("repo") String repo,
                       @RequestParam("newCommitId") String newCommitId,
                       @RequestParam("oldCommitId") String oldCommitId) throws Exception{
        Repository repository = this.repositoryService.buildRepo(group, repo);
        if (repository == null) {
            return new ErrorNotice("repo not found");
        }
        List<DiffEntry> diffs = this.commitService.getDiff(repository,oldCommitId,newCommitId);
        if (diffs == null) {
            return new ErrorNotice("git diff error");
        }
        List<Object> diffData = new ArrayList<>();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DiffFormatter df = new DiffFormatter(out);
        df.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
        df.setRepository(repository);
        for (DiffEntry diff : diffs) {
            Map<String, Object> map = new HashMap<>();
            df.format(diff);
            String diffText = out.toString("UTF-8");
            out.reset();
            map.put("newFile", diff.getNewPath());
            map.put("oldFile", diff.getOldPath());
            map.put("content", diffText);
            diffData.add(map);
        }
        return new SuccessInfo(diffData);
    }

}
