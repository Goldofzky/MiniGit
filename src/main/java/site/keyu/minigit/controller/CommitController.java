package site.keyu.minigit.controller;


import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
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
import site.keyu.minigit.service.RepositoryService;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CommitController {
    @Autowired
    private CommitService commitService;

    @Autowired
    private RepositoryService repositoryService;

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
