package site.keyu.minigit.controller;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import site.keyu.minigit.service.CommitService;
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
    private RepositoryService repositoryService;

    @GetMapping(path = "/{group}/{repo}/commit/{id}")
    @ResponseBody
    public Object commit(@PathVariable("group") String group,
                         @PathVariable("repo") String repo,
                         @PathVariable("id") String id
                        ) throws Exception {

        Repository repository = this.repositoryService.buildRepo(group,repo);

        List<DiffEntry> diffs = this.commitService.getDiffByCommitId(repository,id);
        List<Object> data = new ArrayList<>();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DiffFormatter df = new DiffFormatter(out);
        df.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
        df.setRepository(repository);

        for (DiffEntry diff:diffs){

            Map<String,Object> map = new HashMap<>();
            df.format(diff);
            String diffText = out.toString("UTF-8");
            out.reset();
            map.put("file",diff.getNewPath());
            map.put("content",diffText);

            data.add(map);
        }

        return data;
    }

    }
